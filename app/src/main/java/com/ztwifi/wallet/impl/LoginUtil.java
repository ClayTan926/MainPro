package com.ztwifi.wallet.impl;


import com.ztwifi.wallet.bean.LoginInfo;
import com.ztwifi.wallet.dbutil.DbUserInfo;

import android.content.Context;
import android.util.Log;

public class LoginUtil {
	
	private final static String TAG="InitUtil";
	private DbUserInfo dbUserInfo;
	private LoginInfo loginInfo;
	private Boolean isUser;
	private String isLogin;
	private String token="";
	private String timeOut="";
	private Context context;

    public LoginUtil(Context context) {
		super();
		this.context = context;
	}
    
    //SDK初始化方法
	public void init(String uId, String nick, String iconurl) {
    	    dbUserInfo=new DbUserInfo(context);
			isUser=dbUserInfo.booleanInseartInFo(uId);

    		isLogin=dbUserInfo.booleanLogined(uId);
    		loginInfo=dbUserInfo.getInFo(uId);
    		token=loginInfo.getToken();

    		if (""==isLogin) {
    				uId=null;
    				Log.d(TAG,"SDK init，未登录，置uId为空");

    		//更新用户数据
    		dbUserInfo.updateInFo(uId, isLogin, token, "", nick, iconurl,0);
		}else {
			Log.d(TAG, "没有该用户");
			//新增新用户数据
			dbUserInfo.insertInFo(uId, "2", token, timeOut, nick, iconurl,0);
		}	
	}


}

