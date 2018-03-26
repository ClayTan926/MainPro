package com.ztwifi.wallet.impl;

import com.ztwifi.wallet.bean.LoginInfo;
import com.ztwifi.wallet.dbutil.DbUserInfo;

import android.content.Context;
import android.util.Log;

public class InfoCallBack {
	
	private final static String TAG="InfoCallBack";
	private DbUserInfo dbUserInfo;
	private LoginInfo loginInfo;
	private UserLogin userLogin;
	private int isLogin;
	private Context context;
 
	public InfoCallBack(Context context) {
		super();
		this.context = context;
	}

	//当前登陆用户信息发生变化时通知游戏端
	public void registerLoginCallback(String uId,LoginUtil loginUtil) {
		
		dbUserInfo=new DbUserInfo(context);
		
		if (dbUserInfo.booleanInseartInFo(uId)) {
			
			if (""==dbUserInfo.booleanLogined(uId)) {
				isLogin=2;
				
			}else {
				isLogin=1;
			}
			
			loginInfo=dbUserInfo.getInFo(uId);
			userLogin=new UserLogin(context);
			Log.d(TAG, "回调方法");
			userLogin.userLogin(isLogin, uId, loginInfo.getNick(), loginInfo.getIconurl());
		}	
			
	}

}
