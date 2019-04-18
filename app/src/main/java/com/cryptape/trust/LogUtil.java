package com.cryptape.trust;

import android.util.Log;

public class LogUtil {
    private static final String TAG = "trust";
    private static final boolean isDebug = false;

    public static void d(String msg){
        if(isDebug){
            Log.d(TAG,msg);
        }
    }
}
