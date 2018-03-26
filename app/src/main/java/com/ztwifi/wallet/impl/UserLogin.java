package com.ztwifi.wallet.impl;

import android.content.Context;

import com.ztwifi.wallet.bean.LoginInfo;
import com.ztwifi.wallet.dbutil.DbUserInfo;

/**
 * Created by Netted on 2018/3/26.
 */

public class UserLogin {
    private final static String TAG="InitUtil";
    private DbUserInfo dbUserInfo;
    private LoginInfo loginInfo;
    private Boolean isUser;
    private String isLogin;
    private String token="";
    private String timeOut="";
    private Context context;

    public UserLogin(Context context) {
        super();
        this.context = context;
    }

    //通知SDK当前登陆用户信息
    public void userLogin(Integer inLogin, String uId, String nick, String iconurl) {

        dbUserInfo=new DbUserInfo(context);

        isLogin=dbUserInfo.booleanLogined(uId);
        loginInfo=dbUserInfo.getInFo(uId);

        if (""==isLogin) {
            isLogin="2";
        }else {
            isLogin="1";
        }

        token=loginInfo.getToken();
        //更新用户数据
        dbUserInfo.updateInFo(uId, isLogin, token, timeOut, nick, iconurl,0);

    }
}
