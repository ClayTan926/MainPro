package com.ztwifi.wallet.main;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.ContextThemeWrapper;

import com.netted.ba.ct.UserApp;
import com.netted.ba.ctact.CtDataLoader;
import com.netted.ba.util.CtRuntimeException;
import com.netted.common.welcome.WelcomeActivityHelper;
import com.ztwifi.wallet.R;

import java.util.Map;

public class MainProApp extends UserApp {

	public static final String SDCARD_ROOT_PATH = android.os.Environment
			.getExternalStorageDirectory().getAbsolutePath();// 路径
	public static final String CACHE_SAVE_FOLDER = ".cache/"; // 缓存保存文件夹
	public static final boolean clear_after_save = false;
	private static final String TAG = "MainProApp";
	public static CtDataLoader.OnCtDataEvent afterAutoLoginEvt=null;
	private String uId;
 	private String nick;
	private String iconurl;

	public void onCreate() {
		super.onCreate();
		Log.e(TAG,"App类初始化");

	}


	@Override
	public String getUserPushId() {
		Log.e(TAG, "getUserPushId: 获取PushId" );
		return UserApp.curApp().getDeviceId()+UserApp.curApp().getUserId(); // 根据需要设定PushId，默认会在有登录时自动生成
}

	@Override
	protected void attachBaseContext(Context base) {
		Log.e(TAG, "attachBaseContext: 依附BaseContext" );
		super.attachBaseContext(base);
	}


	/*@Override
	public void afterUserLogout() {
		super.afterUserLogout();
		UserApp.gotoURL(UserApp.curApp().getBaseContext(),"app://login/");
	}*/

	@Override
	public void initApp() {
		super.initApp();
		Log.e(TAG, "initApp:初始化App及全局变量 " );
		UserApp.userLoginCvId=10000208;
		UserApp.checkNewVersionCvId=10000261;
		//RegisterActivity.registerActClass = WsoaRegisterActivity.class;
		//LoginActivity.loginActClass = WsoaLoginActivity.class;
		UserApp.DEF_CVLOGIN_HEADER="ct/ctlogin.nx?isWM=1&isPost=1";
		setGParamValue("APP_SETTINGS.MAIN_ACT_URL", "act://com.ztwifi.wallet.main.MainActivity/");
		setGParamValue("APP_SETTINGS.MAIN_ACT_URL",
				"act://com.netted.wsoa.main.MainActivity/?checkLogin=6");
		setGParamValue("APP_SETTINGS.MAIN_ACT_LOGIN_URL", "act://com.ztwifi.wallet.main.MainActivity/?checkLogin=5");
		setGParamValue("APP_SETTINGS.MY_MESSAGES.CVID","10105");
		WelcomeActivityHelper.autoLoginEnabled = true;
	}

	// 初始化服务地址
	@Override
	protected void initServerAddr() {
		Log.e(TAG, "initServerAddr: 初始化服务器地址" );
		//serverAddrList = "http://oa.wtdchina.com/panoffice/";
		serverAddrList = "http://192.0.0.16/wsoa2017/";
		//serverAddrList = "http://192.0.0.110/wsoa2017/";
		//serverAddrList = "http://192.0.0.112/wsoa2017/";
		//serverSwitchList = "http://oa.wtdchina.com/panoffice/\n";
		super.initServerAddr();
	}
	//判断用户登陆状态、自动登陆
	@Override
	public void afterUserLogin(boolean isAutoLogin) {
		super.afterUserLogin(isAutoLogin);
		/*CookieUtils.deleteCookiesForDomain(this, CookieUtils.WSOAURL);
		CookieUtils.setCookie(this, CookieUtils.WSOAURL);
		*/
		Log.e(TAG, "afterUserLogin: 登录后保存信息，更改登陆状态" );
		UserApp.curApp().setSharePrefParamValue("REFRESH_TIME_MILLIS", System.currentTimeMillis()+"");
		if(isAutoLogin && afterAutoLoginEvt!=null)
			afterAutoLoginEvt.onDataLoaded(null);
	}
	//初始化全局变量
	@Override
	protected void initGlobalParams() {
		super.initGlobalParams();
		globalParams.clear();
		// 设置URL将PUSHID发到服务端

		SharedPreferences userInfo = getSharedPreferences("CITY_INFO", Context.MODE_PRIVATE);
		globalParams.put("CUR_UID", userInfo.getString("cur_uid", "001"));
		globalParams.put("CUR_ISLOGIN", userInfo.getString("cur_islogin", "2"));
		globalParams.put("CUR_TOKEN", userInfo.getString("cur_token", ""));
		globalParams.put("CUR_TIMEOUT", userInfo.getString("cur_timeout", ""));
		globalParams.put("CUR_NICK", userInfo.getString("cur_nick", "nick"));
		globalParams.put("CUR_ICONURL", userInfo.getString("cur_iconurl", "iconurl"));
		globalParams.put("CUR_DATALEVEL", userInfo.getString("cur_datalevel", "0"));
		globalParams.put("LAST_MAPVIEWINFO", "");
		globalParams.put("DEMO_MODE", getSharePrefParamValue("DEMO_MODE", "0"));
		globalParams.put("APP_VER", getAppSysVer());
		
	}


	public ProgressDialog createProgressBarDialogEx(Context theCtx, String par) {
		if(theCtx != null && (theCtx instanceof Activity)){
			ContextThemeWrapper ctxTw=new ContextThemeWrapper(theCtx, android.R.style.Theme_Holo_Light_Dialog_MinWidth);
			ProgressDialog dlg = new ProgressDialog(ctxTw);
			//ProgressDialog dlg = new ProgressDialog(theCtx,android.R.style.Theme_Holo_Light_Dialog_MinWidth);
			dlg.setMessage("正在加载...");
			if(par!=null && par.contains("[FOR:AUTO_UPDATE_DOWNLOAD]"))
				dlg.setProgressNumberFormat("%1d/%2d kB");
			//dlg.setProgressStyle(android.R.style.Widget_ProgressBar_Large);
			return dlg;
		}
		else
			throw new CtRuntimeException("创建进度条的context必须是一个Activity");
	}


	@Override
	public void initDefUserProps() {
		super.initDefUserProps();
		userProps.put("USERID", 10001);
	}

	@Override
	public void onPushMessageReceived(int arg0, String arg1,
			Map<String, Object> arg2) {
		super.onPushMessageReceived(arg0, arg1, arg2);

	}

	public static String getPathInSD() {
		return UserApp.curApp().getResources().getString(R.string.SAVE_PATH_IN_SDCARD);
	}



}
