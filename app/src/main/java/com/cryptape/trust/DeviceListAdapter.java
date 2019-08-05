package com.cryptape.trust;

import android.bluetooth.BluetoothDevice;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.graphics.Color;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.trustconnector.ble.pursesdk.BlePurseSDK;

import java.util.List;

public class DeviceListAdapter extends BaseAdapter {
    private Context context;
    private List<BluetoothDevice> datas;
    private Handler handler = new Handler();
    public boolean isConnect;
    public TextView tvPk;
    private PinCallback pinCallback;

    public DeviceListAdapter(Context context, List<BluetoothDevice> datas) {
        this.context = context;
        this.datas = datas;
    }

    @Override
    public int getCount() {
        return datas.size();
    }

    @Override
    public Object getItem(int position) {
        return datas.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        BluetoothDevice device = (BluetoothDevice) getItem(position);
        View view;
        ViewHolder viewHolder;
        if (convertView == null) {
            view = LayoutInflater.from(context).inflate(R.layout.listview_item_devices, null);
            viewHolder = new ViewHolder();
            viewHolder.tvDeviceName = view.findViewById(R.id.tv_deviceName);
            viewHolder.tvMacAddress = view.findViewById(R.id.tv_macAddress);
            viewHolder.btnConnect = view.findViewById(R.id.btn_connect);
            viewHolder.pbConnect = view.findViewById(R.id.pb_connect);
            viewHolder.vLine = view.findViewById(R.id.tv_line);
            viewHolder.tvPkLabel = view.findViewById(R.id.tv_pk_label);
            viewHolder.tvPk = view.findViewById(R.id.tv_pk);
            viewHolder.ivCopy = view.findViewById(R.id.iv_copy);
            viewHolder.btnResetPin = view.findViewById(R.id.btn_reset_pin);
            viewHolder.btnUnlockPin = view.findViewById(R.id.btn_unlock_pin);
            view.setTag(viewHolder);
        } else {
            view = convertView;
            viewHolder = (ViewHolder) view.getTag();
        }
        viewHolder.tvDeviceName.setText(device.getName());
        viewHolder.tvMacAddress.setText(device.getAddress());
        viewHolder.btnConnect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewHolder.pbConnect.setVisibility(View.VISIBLE);
                viewHolder.btnConnect.setVisibility(View.INVISIBLE);
                new Thread(new Runnable() {
                    @Override
                    public void run() {

                        isConnect = BlePurseSDK.connectPeripheral(context,device);
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                    viewHolder.pbConnect.setVisibility(View.INVISIBLE);
                                    viewHolder.btnConnect.setVisibility(View.VISIBLE);
                                if(isConnect){
                                    viewHolder.btnConnect.setBackgroundResource(R.drawable.btn_connected_bg);
                                    viewHolder.btnConnect.setText("Connected");
                                    viewHolder.btnConnect.setTextColor(Color.parseColor("#275DD3"));
                                    tvPk = viewHolder.tvPk;
                                }else {
                                    viewHolder.btnConnect.setBackgroundResource(R.drawable.btn_connect_bg);
                                    viewHolder.btnConnect.setText("Connect");
                                    viewHolder.btnConnect.setTextColor(Color.parseColor("#E82D1E"));
                                    Toast.makeText(context,"Connection failed，please try again",Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }
                }).start();

            }
        });
        viewHolder.ivCopy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String pubKey = viewHolder.tvPk.getText().toString();
                ClipboardManager cm = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData mClipData = ClipData.newPlainText("trust", pubKey);
                cm.setPrimaryClip(mClipData);
                Toast.makeText(context,"Public key copied",Toast.LENGTH_SHORT).show();
            }
        });
        viewHolder.btnResetPin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pinCallback.reset();
            }
        });
        viewHolder.btnUnlockPin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pinCallback.unlock();
            }
        });
        return view;
    }

    class ViewHolder {
        TextView tvDeviceName;
        TextView tvMacAddress;
        Button btnConnect;
        ProgressBar pbConnect;
        View vLine;
        TextView tvPkLabel;
        TextView tvPk;
        ImageView ivCopy;
        Button btnResetPin;
        Button btnUnlockPin;
    }

    interface PinCallback{
        void reset();
        void unlock();
    }

    public void setPinCallback(PinCallback callback){
        pinCallback = callback;
    }
}
