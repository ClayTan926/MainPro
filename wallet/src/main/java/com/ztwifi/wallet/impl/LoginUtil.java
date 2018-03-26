package com.ztwifi.wallet.impl;


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
	
	//创建 LoginUtil 的一个对象
    private static LoginUtil instance = new LoginUtil();
    //让构造函数为 private，这样该类就不会被实例化
    private LoginUtil(){}
    //获取唯一可用的对象
    public static LoginUtil getInstance(){
        return instance;
    }
    public LoginUtil(Context context) {
		super();
		this.context = context;
	}
    
    //SDK初始化方法
	public void init(String uId, String nick, String iconurl) {
    	dbUserInfo=new DbUserInfo(context);
    	isUser=dbUserInfo.booleanInseartInFo(uId);
    	
    	if (isUser) {
    		isLogin=dbUserInfo.booleanLogined(uId);
    		loginInfo=dbUserInfo.getInFo(uId);
    		token=loginInfo.getToken();

    		if (""==isLogin) {
    				uId=null;
    				Log.d(TAG,"SDK init，未登录，置uId为空");
    		}
    		//更新用户数据
    		dbUserInfo.updateInFo(uId, isLogin, token, "", nick, iconurl,0);
		}else {
			Log.d(TAG, "没有该用户");
			//新增新用户数据
			dbUserInfo.insertInFo(uId, "2", token, timeOut, nick, iconurl,0);
		}	
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

