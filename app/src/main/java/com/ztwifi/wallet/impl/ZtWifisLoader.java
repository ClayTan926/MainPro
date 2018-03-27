package com.ztwifi.wallet.impl;

import android.content.Context;
import android.util.Log;

import com.ztwifi.wallet.bean.LoginInfo;
import com.ztwifi.wallet.dbutil.DbUserInfo;

/**
 * Created by Netted on 2018/3/26.
 */

public class ZtWifisLoader {

    private static final String TAG = "ZtWifisLoader";
    WifiCallback wifiCallback;
    LoginUtil loginUtil;
    private Context context;
    private String uId;
    private String nick;
    private String iconUrl;
    private String token="";
    private String timeOut="";
    private DbUserInfo dbUserInfo;
    private LoginInfo loginInfo;
    private String isLogin;

    public ZtWifisLoader(Context context, String uId, String nick, String iconUrl) {
        this.context = context;
        this.uId = uId;
        this.nick = nick;
        this.iconUrl = iconUrl;
    }
    public void init(WifiCallback wifiCallback){
        Log.e(TAG, "ZtWifisLoader初始化" );
        dbUserInfo=new DbUserInfo(context);

        isLogin=dbUserInfo.booleanLogined(uId);
        loginInfo=dbUserInfo.getInFo(uId);
        token=loginInfo.getToken();

        if ("".equals(isLogin)) {
            uId=null;
            Log.d(TAG,"SDK init，未登录，置uId为空");

            //更新用户数据
            dbUserInfo.updateInFo(uId, isLogin, token, "", nick, iconUrl,0);
        }else {
            Log.d(TAG, "没有该用户");
            //新增新用户数据
            dbUserInfo.insertInFo(uId, "2", token, timeOut, nick, iconUrl,0);
        }



     }
}
