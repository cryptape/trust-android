package com.cryptape.trust;

import android.app.Activity;
import android.app.Application;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.ComponentCallbacks;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.KeyEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.cryptape.trust.R;

import libs.trustconnector.ble.pursesdk.BlePurseSDK;
import com.yanzhenjie.permission.AndPermission;
import com.yanzhenjie.permission.runtime.Permission;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private static final int REQUEST_BLE = 1;
    private Context mContext;
    private ProgressBar pb;
    private ListView listView;
    BluetoothAdapter mAdapter;
    private Handler mHandler = new Handler();
    private List<BluetoothDevice> mListDevices = new ArrayList<>();
    private DeviceListAdapter deviceListAdapter;
    AlertDialog keysetsDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        mContext = this;
        mAdapter = BluetoothAdapter.getDefaultAdapter();

//        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
//        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);

        Toolbar toolbar = findViewById(R.id.tb_toolbar);
        setSupportActionBar(toolbar);
//        getSupportActionBar().setDisplayShowTitleEnabled(false);

        setCustomDensity(this, getApplication());

        pb = findViewById(R.id.pb_search);
        listView = findViewById(R.id.lv_devices);

        deviceListAdapter = new DeviceListAdapter(mContext, mListDevices);
        listView.setAdapter(deviceListAdapter);

        scan();
        BlePurseSDK.setDefaultTime(10 * 1000);
        initPurseSdk();//after scan
        setPinCallback();
        createKeysetsDialog();//after initPurseSdk
    }

    private void setPinCallback(){
        deviceListAdapter.setPinCallback(new DeviceListAdapter.PinCallback() {
            @Override
            public void reset() {
                AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                View reseView = View.inflate(mContext, R.layout.dialog_reset_pin, null);
                builder.setView(reseView);
                AlertDialog dialog = builder.create();
                dialog.setCancelable(false);
                TextView tvOk = reseView.findViewById(R.id.tv_ok);
                TextView tvCancel = reseView.findViewById(R.id.tv_cancel);
                EditText edtCurrentPin = reseView.findViewById(R.id.edt_current_pin);
                EditText edtNewPin = reseView.findViewById(R.id.edt_new_pin);
                EditText edtRepeatPin = reseView.findViewById(R.id.edt_repeat_pin);
                tvOk.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String currentPin = edtCurrentPin.getText().toString();
                        String newPin = edtNewPin.getText().toString();
                        String repeatPin = edtRepeatPin.getText().toString();

                        if(TextUtils.isEmpty(currentPin) || TextUtils.isEmpty(newPin) || TextUtils.isEmpty(repeatPin)){
                            toast("Please enter full information");
                            return;
                        }
                        if(newPin.length()!=8){
                            toast("New PIN should be 8 digits");
                            return;
                        }
                        if(!newPin.equals(repeatPin)){
                            toast("The new PIN entered is not the same，please enter again");
                            return;
                        }
                        int verifyResult = BlePurseSDK.verifyPIN(currentPin.getBytes());
                        if(verifyResult != 0x9000){
                            toast("Please enter the correct current PIN");
                            return;
                        }

                        int result = BlePurseSDK.changePIN(newPin.getBytes());
                        if(result == 0x9000){
                            toast("Reset success");
                        }else {
                            toast("Reset fail");
                        }
                        dialog.dismiss();
                    }
                });
                tvCancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });
                dialog.show();
            }

            @Override
            public void unlock() {
                if(deviceListAdapter.isConnect){
                    AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                    View unlockView = View.inflate(mContext, R.layout.dialog_unlock_pin, null);
                    builder.setView(unlockView);
                    AlertDialog dialog = builder.create();
                    dialog.setCancelable(false);
                    TextView tvOk = unlockView.findViewById(R.id.tv_ok);
                    TextView tvCancel = unlockView.findViewById(R.id.tv_cancel);
                    EditText edtPuk = unlockView.findViewById(R.id.edt_puk);
                    EditText edtNewPin = unlockView.findViewById(R.id.edt_new_pin);
                    EditText edtRepeatPin = unlockView.findViewById(R.id.edt_repeat_pin);
                    tvOk.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            String puk = edtPuk.getText().toString();
                            String newPin = edtNewPin.getText().toString();
                            String repeatPin = edtRepeatPin.getText().toString();
                            if(TextUtils.isEmpty(puk) || TextUtils.isEmpty(newPin) || TextUtils.isEmpty(repeatPin)){
                                toast("Please enter full information");
                                return;
                            }
                            if(newPin.length()!=8){
                                toast("New PIN should be 8 digits");
                                return;
                            }
                            if(!newPin.equals(repeatPin)){
                                toast("The new PIN entered is not the same，please enter again");
                                return;
                            }
                            int result = BlePurseSDK.unblockPIN(Utils.parseHexString(puk),newPin.getBytes());
                            if(result == 0x9000){
                                toast("Unlock success");
                            }else {
                                toast("Unlock fail");
                            }
                            dialog.dismiss();
                        }
                    });
                    tvCancel.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialog.dismiss();
                        }
                    });
                    dialog.show();
                }else {
                    toast("Please connect the device first");
                }
            }
        });
    }

    private void initPurseSdk() {
        SharedPreferences sp = getSharedPreferences("keysets",Context.MODE_PRIVATE);
        String enc = sp.getString("enc",null);
        if(enc == null){
            SharedPreferences.Editor editor = sp.edit();
            String defEnc = "7404BE01D1C52CDD0DEA7BFAD37B5CD8";
            String defMac = "121C29F27546F9DCF25E3AB7C116EA61";
            String defDec = "7377C0D7F2F3A6561FABFD13DFC5E501";
            editor.putString("enc",defEnc);
            editor.putString("mac",defMac);
            editor.putString("dec",defDec);
            editor.commit();
            BlePurseSDK.initKey(defEnc, defMac, defDec);
        }else {
            String mac = sp.getString("mac",null);
            String dec = sp.getString("dec",null);
            BlePurseSDK.initKey(enc, mac, dec);
        }
    }

    private void createKeysetsDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        View keysetsView = View.inflate(mContext, R.layout.dialog_keysets, null);
        builder.setView(keysetsView);
        keysetsDialog = builder.create();
        keysetsDialog.setCancelable(false);

        EditText edtEnc = keysetsView.findViewById(R.id.edt_enc);
        EditText edtMac = keysetsView.findViewById(R.id.edt_mac);
        EditText edtDec = keysetsView.findViewById(R.id.edt_dec);
        SharedPreferences sp = getSharedPreferences("keysets",Context.MODE_PRIVATE);
        String enc = sp.getString("enc",null);
        String mac = sp.getString("mac",null);
        String dec = sp.getString("dec",null);

        edtEnc.setText(enc);
        edtMac.setText(mac);
        edtDec.setText(dec);

        Button btnOk = keysetsView.findViewById(R.id.btn_keysets_ok);
        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String editedEnc = edtEnc.getText().toString();
                String editedMac = edtMac.getText().toString();
                String editedDec = edtDec.getText().toString();

                if (editedEnc.length() != 32){
                    toast("invalide encKey");
                    return;
                }
                if (editedMac.length() != 32){
                    toast("invalide macKey");
                    return;
                }
                if (editedDec.length() != 32){
                    toast("invalide decKey");
                    return;
                }

                SharedPreferences.Editor editor = sp.edit();
                editor.putString("enc",editedEnc);
                editor.putString("mac",editedMac);
                editor.putString("dec",editedDec);
                editor.commit();
                initPurseSdk();//re init
                keysetsDialog.dismiss();
            }
        });
    }


    private static float sRoncompatDennsity;
    private static float sRoncompatScaledDensity;

    private void setCustomDensity(@NonNull Activity activity, final @NonNull Application application) {

        //application
        final DisplayMetrics appDisplayMetrics = application.getResources().getDisplayMetrics();

        if (sRoncompatDennsity == 0) {
            sRoncompatDennsity = appDisplayMetrics.density;
            sRoncompatScaledDensity = appDisplayMetrics.scaledDensity;
            application.registerComponentCallbacks(new ComponentCallbacks() {
                @Override
                public void onConfigurationChanged(Configuration newConfig) {
                    if (newConfig != null && newConfig.fontScale > 0) {
                        sRoncompatScaledDensity = application.getResources().getDisplayMetrics().scaledDensity;
                    }
                }

                @Override
                public void onLowMemory() {

                }
            });
        }

        final float targetDensity = appDisplayMetrics.widthPixels / 360;
        final float targetScaledDensity = targetDensity * (sRoncompatScaledDensity / sRoncompatDennsity);
        final int targetDensityDpi = (int) (targetDensity * 160);

        appDisplayMetrics.density = targetDensity;
        appDisplayMetrics.densityDpi = targetDensityDpi;
        appDisplayMetrics.scaledDensity = targetScaledDensity;

        //activity
        final DisplayMetrics activityDisplayMetrics = activity.getResources().getDisplayMetrics();

        activityDisplayMetrics.density = targetDensity;
        activityDisplayMetrics.densityDpi = targetDensityDpi;
        activityDisplayMetrics.scaledDensity = targetScaledDensity;
    }

    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_key_sets:
                keysetsDialog.show();
                break;
            case R.id.iv_gen_key:
                genKey();
                break;
            case R.id.iv_digital_sign:
                sign();
                break;
            case R.id.iv_reset_key:
                resetKey();
                break;
            case R.id.iv_import_key:
                importKey();
                break;
            case R.id.tv_devices:
            case R.id.pb_search:
                scan();
                break;
        }
    }

    private void importKey(){
        verifyPin(new VerifyPinCallback() {
            @Override
            public void onSuccess() {
                byte[] bytes = BlePurseSDK.getPublicKey();
                if (bytes != null) {
                    toastL("Already have a private key, please reset the private key and try again");
                    return;
                }

                AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                View importView = View.inflate(mContext, R.layout.dialog_import_key, null);
                builder.setView(importView);
                AlertDialog dialog = builder.create();
                dialog.setCancelable(false);
                EditText edtPrvKey = importView.findViewById(R.id.edt_prvkey);
                EditText edtPubKey = importView.findViewById(R.id.edt_pubkey);
                TextView tvOk = importView.findViewById(R.id.tv_ok);
                TextView tvCancel = importView.findViewById(R.id.tv_cancel);
                tvOk.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String prvKey = edtPrvKey.getText().toString();
                        String pubKey = edtPubKey.getText().toString();

                        if(!Utils.isValidHex(prvKey) || !Utils.isValidHex(pubKey)){
                            toast("Please enter the right Private key and the corresponding public key");
                            return;
                        }

                        int result = BlePurseSDK.importKey(Utils.parseHexString(prvKey),Utils.parseHexString(pubKey));
                        if(result == 0x9000){
                            toast("import success");
                            deviceListAdapter.tvPk.setText(pubKey);
                        }else {
                            toast("import fail");
                        }
                        dialog.dismiss();
                    }
                });
                tvCancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });
                dialog.show();
            }
        });
    }

    private void resetKey(){
        verifyPin(new VerifyPinCallback() {
            @Override
            public void onSuccess() {
                AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                View reseView = View.inflate(mContext, R.layout.dialog_reset_key, null);
                builder.setView(reseView);
                AlertDialog dialog = builder.create();
                dialog.setCancelable(false);
                TextView tvOk = reseView.findViewById(R.id.tv_ok);
                TextView tvCancel = reseView.findViewById(R.id.tv_cancel);
                tvOk.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int result = BlePurseSDK.resetKey();
                        if(result == 0x9000){
                            toast("Reset success");
                            deviceListAdapter.tvPk.setText("");
                        }else {
                            toast("Reset fail");
                        }
                        dialog.dismiss();
                    }
                });
                tvCancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });
                dialog.show();
            }
        });
    }

    private void genKey() {
        verifyPin(new VerifyPinCallback() {
            @Override
            public void onSuccess() {
                byte[] bytes = BlePurseSDK.getPublicKey();
                if (bytes != null) {
                    toast("Already have a private key, please reset the private key and try again");
                    String pubkey = Utils.toHexString(bytes);
                    deviceListAdapter.tvPk.setText(pubkey);
                } else {
                    int result = BlePurseSDK.generateKey();
                    if (result == 0x9000) {
                        toast("Key Generated");
                    } else {
                        toast("Generate fail");
                    }

                    byte[] newPk = BlePurseSDK.getPublicKey();
                    if(newPk != null){
                        deviceListAdapter.tvPk.setText(Utils.toHexString(newPk));
                    }
                }
            }
        });
    }

    private void sign() {
        verifyPin(new VerifyPinCallback() {
            @Override
            public void onSuccess() {
                AlertDialog dialog;
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                View verifyPinView = View.inflate(mContext, R.layout.dialog_gen_signature, null);
                builder.setView(verifyPinView);
                dialog = builder.create();
                dialog.setCancelable(false);
                TextView tvOk = verifyPinView.findViewById(R.id.tv_ok);
                TextView tvCancel = verifyPinView.findViewById(R.id.tv_cancel);
                EditText edtData = verifyPinView.findViewById(R.id.edt_data);
                tvOk.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String data = edtData.getText().toString();
                        boolean isHex = Utils.isValidHex(data);
                        if(!isHex){
                            toast("Please enter the right format");
                            return;
                        }
                        byte[] signature = BlePurseSDK.sign(Utils.parseHexString(data));
                        if (signature != null){
                            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                            View sigView = View.inflate(mContext, R.layout.dialog_signature, null);
                            builder.setView(sigView);
                            AlertDialog dialogSig = builder.create();
                            dialogSig.setCancelable(false);
                            TextView tvOk = sigView.findViewById(R.id.tv_ok);
                            TextView tvSig = sigView.findViewById(R.id.tv_sig);
                            tvSig.setText(Utils.toHexString(signature));
                            tvSig.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    String sig = tvSig.getText().toString();
                                    ClipboardManager cm = (ClipboardManager) mContext.getSystemService(Context.CLIPBOARD_SERVICE);
                                    ClipData mClipData = ClipData.newPlainText("trust", sig);
                                    cm.setPrimaryClip(mClipData);
                                    toast("Digital signature copied");
                                }
                            });
                            tvOk.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    dialogSig.dismiss();
                                }
                            });
                            dialog.dismiss();
                            dialogSig.show();
                        }else {
                            toast("Generate signature failed");
                        }
                    }
                });
                tvCancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.dismiss();
                    }
                });
                dialog.show();
            }
        });
    }

    private void verifyPin(VerifyPinCallback callback) {
        if (deviceListAdapter.isConnect) {
            AlertDialog dialog;
            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
            View verifyPinView = View.inflate(mContext, R.layout.dialog_verify_pin, null);
            builder.setView(verifyPinView);
            dialog = builder.create();
            dialog.setCancelable(false);
            TextView tvOk = verifyPinView.findViewById(R.id.tv_ok);
            TextView tvCancel = verifyPinView.findViewById(R.id.tv_cancel);
            EditText edtPIn = verifyPinView.findViewById(R.id.edt_pin);
            tvOk.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String pin = edtPIn.getText().toString();
                    if (pin.length() != 8) {
                        toast("Please enter the correct PIN");
                    }
                    int result = BlePurseSDK.verifyPIN(pin.getBytes());
                    LogUtil.d("verify pin:" + result);
                    if (result == 0x9000) {
                        dialog.dismiss();
                        callback.onSuccess();
                    } else if (result == -1 || result == -2) {
                        toast("Connect Error,Please reconnect");
                        dialog.dismiss();
                    } else {
                        if (result == 0x63C0) {
                            String msg = "The PIN is locked as it has been incorrect for more than 10 times, please unlock the PIN first.";
                            toastL(msg);
                        } else {
                            toast("Please enter the correct PIN");
                        }
                    }
                }
            });
            tvCancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dialog.dismiss();
                }
            });
            dialog.show();
        } else {
            toast("Please connect the device first");
        }
    }

    interface VerifyPinCallback {
        void onSuccess();
    }

    interface RequestBleCallback {
        void onOpen();
    }


    private RequestBleCallback mRequestBleCallback;

    public void requestBle(RequestBleCallback callback) {
        mRequestBleCallback = callback;

        IntentFilter statusFilter = new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED);
        mContext.registerReceiver(mStatusReceive, statusFilter);

        AndPermission.with(mContext)
                .runtime()
                .permission(Permission.ACCESS_COARSE_LOCATION)
                .onGranted(permissions -> {
                    BluetoothAdapter adapter = BluetoothAdapter.getDefaultAdapter();
                    if (adapter != null) {
                        if (adapter.isEnabled()) {
                            callback.onOpen();
                        } else {
                            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                            startActivityForResult(enableBtIntent, REQUEST_BLE);

//                            if(adapter.isEnabled()){
//                                callback.onOpen();
//                            }else {
//                                toast("please open bluetooth");
//                            }
                        }
                    }

                })
                .onDenied(permissions -> {
                    toast("please allow the permission request");
                })
                .start();
    }

    private BroadcastReceiver mStatusReceive = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (BluetoothAdapter.ACTION_STATE_CHANGED.equals(intent.getAction())) {

                int blueState = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, 0);
                switch (blueState) {
//                    case BluetoothAdapter.STATE_TURNING_ON:
//                        break;
                    case BluetoothAdapter.STATE_ON:
                        scan();
                        break;
                    case BluetoothAdapter.STATE_TURNING_OFF:
                        stopScan();
                        break;
//                    case BluetoothAdapter.STATE_OFF:
//                        break;

                }
            }
        }
    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQUEST_BLE:
                if (resultCode == RESULT_OK) {
                    mRequestBleCallback.onOpen();
                } else {
                    toast("please open bluetooth");
                }
                break;
        }
    }


    private void toast(String msg) {
        Toast.makeText(mContext, msg, Toast.LENGTH_SHORT).show();
    }
    private void toastL(String msg){
        Toast.makeText(mContext, msg, Toast.LENGTH_LONG).show();
    }

    private boolean isScanning = false;

    private void startScanning() {
        if (!isScanning) {
            LogUtil.d("start scan...");
            startProgress();
            isScanning = true;
            mAdapter.startLeScan(scanCallback);
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    stopScan();
                }
            }, 1000 * 15);
        } else {
            LogUtil.d("isScanning...");
        }
    }


    private void stopScan() {
        if (isScanning) {
            mAdapter.stopLeScan(scanCallback);
            pb.clearAnimation();
            isScanning = false;
        }
    }

    BluetoothAdapter.LeScanCallback scanCallback = new BluetoothAdapter.LeScanCallback() {
        @Override
        public void onLeScan(BluetoothDevice device, int rssi, byte[] scanRecord) {
            runOnUiThread(() -> {
                String name = device.getName();
                if (name != null && name.contains("NKey")) {
                    if (!mListDevices.contains(device)) {
                        LogUtil.d(name);
                        mListDevices.add(device);
                        deviceListAdapter.notifyDataSetChanged();
                    }
                }
            });
        }
    };

    private void startProgress() {
        pb.setVisibility(View.VISIBLE);
        findViewById(R.id.tv_devices).setVisibility(View.VISIBLE);
        listView.setVisibility(View.VISIBLE);


        RotateAnimation animation;
        int magnify = 10000;
        int toDegrees = 360;
        int duration = 1200;
        toDegrees *= magnify;
        duration *= magnify;
        animation = new RotateAnimation(0, toDegrees,
                Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        animation.setDuration(duration);
        LinearInterpolator lir = new LinearInterpolator();
        animation.setInterpolator(lir);
        animation.setRepeatCount(Animation.INFINITE);
        animation.setRepeatMode(Animation.RESTART);
        pb.startAnimation(animation);
    }

    private void scan() {
        requestBle(new RequestBleCallback() {
            @Override
            public void onOpen() {
                startScanning();
            }
        });
    }

    private long mExitTime;

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if ((System.currentTimeMillis() - mExitTime) > 2000) {
                Toast.makeText(this, "click again to exit the application", Toast.LENGTH_SHORT).show();
                mExitTime = System.currentTimeMillis();
            } else {
                System.exit(0);
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    private boolean isValideKey(String key){
        if(key.length() != 32){
            return false;
        }
        return Utils.isValidHex(key);
    }
}
