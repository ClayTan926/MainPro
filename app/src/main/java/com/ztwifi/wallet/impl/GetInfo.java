package com.ztwifi.wallet.impl;

import com.ztwifi.wallet.bean.LoginInfo;
import com.ztwifi.wallet.dbutil.DbUserInfo;

import android.content.Context;
import android.util.Log;

public class GetInfo {
	
	private final static String TAG="GetInfo";
	private DbUserInfo dbUserInfo;
	private LoginInfo loginInfo;
	private Context context;
	private String timeOut;
	private Boolean isUser;
	
	
    
    public GetInfo(Context context) {
		super();
		this.context = context;
	}
    
    //SDK判断用户是否登陆超时，获取当前登陆用户信息
	public LoginInfo getLoginInfo(String uId) {
		dbUserInfo=new DbUserInfo(context);
		isUser=dbUserInfo.booleanInseartInFo(uId);
		
		if(isUser) {
		timeOut=dbUserInfo.booleanLogined(uId);
			if (""!=timeOut) {
				Log.d(TAG,"登陆未超时");
				loginInfo=dbUserInfo.getInFo(uId);
				return loginInfo;
			}
		}
		
		Log.d(TAG,"登陆超时");
		
		loginInfo=null;
		return loginInfo;
	}

}
