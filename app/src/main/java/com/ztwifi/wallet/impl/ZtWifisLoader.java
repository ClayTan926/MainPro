package com.ztwifi.wallet.impl;

import android.util.Log;

/**
 * Created by Netted on 2018/3/26.
 */

public class ZtWifisLoader {

    private static final String TAG = "ZtWifisLoader";
    WifiCallback wifiCallback;
    LoginUtil loginUtil;
    private String uId,nick,iconUrl;

    public ZtWifisLoader(WifiCallback wifiCallback) {
        this.wifiCallback = wifiCallback;
    }

    public void init(WifiCallback wifiCallback){
        Log.e(TAG, "ZtWifisLoader初始化" );
        wifiCallback.login();


     }
}
