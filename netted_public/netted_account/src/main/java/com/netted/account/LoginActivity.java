package com.netted.account;


import java.util.List;

import com.netted.account.R;
import com.netted.ba.ct.NetUtil;
import com.netted.ba.ct.UserApp;
import com.netted.ba.ctact.CtActEnvHelper;
import com.netted.ba.ctact.CtActEnvHelper.OnCtViewUrlExecEvent;
import com.netted.ba.ctact.CtDataLoader;
import com.netted.ba.ctact.AppUrlManager;
import com.netted.ba.ctact.AppUrlParserIntf;
import com.netted.ba.ctact.CtDataLoader.OnCtDataEvent;
import com.netted.ba.login.CtLoginHelper;
import com.netted.ba.login.CtLogoutHelper;
import com.netted.ba.login.CtLogoutHelper.AfterLogoutEvent;
import com.netted.ba.util.EncryptUtil;
import com.netted.common.helpers.FavoriteHelper;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

public class LoginActivity extends Activity {

	public static Class<?> loginActClass=LoginActivity.class;
	
	private int checkHackCounter=0;
	protected boolean skipAppInFrontCheck=false;
	protected InnerRecevier homeRecevier;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.act_login);
		initControls();
	}
	
	@Override
	public void finish() {
		skipAppInFrontCheck=true;
		super.finish();
	}

	public boolean doExecUrlEx(Activity act, View v, String url){
		if(url.startsWith("cmd://doLogin/")){
			doLogin();
			return true;
		}
		return false;
	}
	
	/**
	 * 初始化页面控件和添加事件
	 */
	protected void initControls() {
		
		String us=null, pwd=null;
		us=this.getIntent().getStringExtra("username");
		if(us==null || us.length()==0)
			us=UserApp.curApp().getSharePrefParamValue("lastUserName", "");

		pwd=this.getIntent().getStringExtra("password");
		if (UserApp.curApp().isLoggedIn()) {
			us=UserApp.curApp().getUserName();
			if(pwd==null || pwd.length()==0)
			try {
				pwd = UserApp.curApp().getUserPropValue("PASSWORD");
				pwd = EncryptUtil.getUserPassword(pwd);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		this.getIntent().putExtra("account_name", us);
		this.getIntent().putExtra("account_password", pwd);
		
		OnCtViewUrlExecEvent urlEvt=new OnCtViewUrlExecEvent() {
			
			@Override
			public boolean doExecUrl(Activity act, View v, String url) {
				return doExecUrlEx(act, v, url);
			}
		};
		CtActEnvHelper.createCtTagUI(this, null, urlEvt);
	}

	protected void tryHideKeyb(int focusViewId) {
		View v = this.findViewById(focusViewId);
		if (v == null)
			return;
		InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
	}

	protected void doLogin() {
		
		String user = CtActEnvHelper.getCtViewValue(this, "account_name");
		String password = CtActEnvHelper.getCtViewValue(this, "account_password");
		if (user == null || user.length() == 0) {
			UserApp.showToast(this, UserApp.getResString("ba_account_login_name_required",  "请输入用户名或手机号"));
			return;
		}
		if (password == null || password.length() == 0) {
			UserApp.showToast(this, UserApp.getResString("ba_account_login_password_required","请输入密码"));
			return;
		}

		AppUrlManager.gotoURL(this, null, "cmd://hidekb/");
		
		OnCtDataEvent evt = new OnCtDataEvent() {
			@Override
			public void onDataCanceled() {
				doLoginCancel();
			}

			@Override
			public void onDataError(String msg) {
				doLoginError(msg);
			}

			@Override
			public void onDataLoaded(CtDataLoader cvth) {
				finishLogin();
			}

			@Override
			public void afterFetchData() {
			}

		};

		CtLoginHelper.callLogin(this, user, password, "",
				true, evt);
	}

	protected void doLoginCancel() {
		UserApp.showToast(UserApp.getResString("ba_mst_aborted"));
	}

	protected void doLoginError(String msg) {
		UserApp.showMessage(LoginActivity.this, UserApp.getResString("ba_account_login_operation_failed","登录失败"), msg);
	}

	protected void finishLogin() {
		FavoriteHelper.updateUnknownFavToCurUser(this);
		UserApp.curApp().setGParamValue("lastLoginStatMayChanged", "1");
		UserApp.showToast(this, UserApp.getResString("ba_account_login_operation_success","登录成功"));
		String returnurl=this.getIntent().getStringExtra("returnurl");
		if (returnurl != null && returnurl.length() > 0){
			AppUrlManager.gotoURL(this, null, returnurl);
		}
		finish();
	}

	//public void onCtUrlResult(String url, String tpName, Object res, View v) {
	public static class LoginUrlParser implements AppUrlParserIntf {

		@Override
		public String getParserName() {
			return "LoginUrlParser";
		}

		@Override
		public String getDescribe() {
			return "登录URL，可传returnurl用于登录后跳转";
		}

		@Override
		public String getSampleUrl() {
			return "app://login/?returnurl=[[act://cv/?cvId=1234]]";
		}

		@Override
		public String getUrlProtocol() {
			return "app://login/";
		}

		@Override
		public boolean isMyUrl(String url) {
			if (url.startsWith("app://login/") || url.startsWith("app://logout/"))
				return true;
			else
				return false;
		}

		@Override
		public boolean gotoUrl(Context ctx, String url) {
			if (!isMyUrl(url))
				return false;
			if (url.startsWith("app://login/")){
				Intent intent = new Intent(ctx, loginActClass);
				AppUrlManager.putUrlParamToIntent(url, intent);
				ctx.startActivity(intent);
			} else if (url.startsWith("app://logout/")) {
				promptLogout((Activity)ctx, url);
			}
			return true;
		}

		public void promptLogout(final Activity act, final String url) {
			if (!UserApp.curApp().isLoggedIn()) {
				UserApp.showMessage(act, UserApp.getResString("ba_account_logout","退出登录"), UserApp.getResString("ba_account_msg_already_logged_out", "您已经退出登录！"));
				return;
			}
			
			String prm=NetUtil.getUrlParamValue(url, "prompt");
			if("NO".equals(prm) || "NONE".equals(prm)){
				callLogout(act, url);
				return;
			}
			if(prm==null || prm.length()==0)
				prm=UserApp.getResString("ba_account_msg_confirm_logout", "您确定要注销登录吗?");

			String title=NetUtil.getUrlParamValue(url, "title");
			if(title==null || title.length()==0)
				title=UserApp.getResString("ba_msg_tip");

			AlertDialog.Builder builder = UserApp.createAlertDlgBuilder(act);
			builder.setTitle(title);
			builder.setMessage(prm);
			builder.setPositiveButton(UserApp.getResString("ba_btn_ok"), new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					UserApp.hideDialog(dialog);
					callLogout(act, url);
				}
			});
			builder.setNegativeButton(UserApp.getResString("ba_btn_cancel"), new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					UserApp.hideDialog(dialog);
				}
			});
			UserApp.showDialog(builder.create());
		}

		private void callLogout(final Activity act, final String url) {
			CtLogoutHelper.callLogout(act, false, new AfterLogoutEvent() {

				@Override
				public void afterLogout() {
					UserApp.curApp().setGParamValue("MYINFO_UPDATE",
							System.currentTimeMillis());
					UserApp.curApp().initDefUserProps();
					UserApp.curApp().saveUserInfo();
					UserApp.showToast(UserApp.getResString("ba_account_msg_logout_success", "注销成功!"));
					AppUrlManager.returnURLResultData(act, null, url, "app://logout/", "1");
				}

			});
		}
		
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		if (homeRecevier == null)
			homeRecevier = new InnerRecevier();
		registerReceiver(homeRecevier, new IntentFilter(Intent.ACTION_CLOSE_SYSTEM_DIALOGS));
	}

	@Override
	protected void onPause() {
		try {
			unregisterReceiver(homeRecevier);
		} catch (Exception e) {
		}
		super.onPause();
		checkHackCounter=0;
		checkLoginHacker();
	}

	private void checkLoginHacker() {

		UserApp.curApp().getAppHandler().postDelayed(new Runnable() {
			
			@Override
			public void run() {
				if(skipAppInFrontCheck)
					return;
				checkAppInFront();
				if(checkHackCounter<10){
					checkLoginHacker();
				}
			}
		}, 500);
	}

	public static boolean isBackgroundHuz(Context context) {

	    ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
		List<RunningAppProcessInfo> appProcesses = activityManager.getRunningAppProcesses();
		for (RunningAppProcessInfo appProcess : appProcesses) {
			if (appProcess.processName.equals(context.getPackageName())) {
				if (appProcess.importance == RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
					return false;
				} else {
					return true;
				}
			}
		}
	    return true;
	}


	public static boolean isBackground(Context context) {
		boolean isAppRunningForeground = false;
		ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);

		if (Build.VERSION.SDK_INT >= 21/*Build.VERSION_CODES.LOLLIPOP*/) {
			List<ActivityManager.RunningAppProcessInfo> appProcesses = activityManager.getRunningAppProcesses();
			if (appProcesses == null)
				return false;
			for (ActivityManager.RunningAppProcessInfo appProcess : appProcesses) {
				if (appProcess.processName.equals(UserApp.getAppPkgName())) {
					if (appProcess.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
						isAppRunningForeground = true;
						break;
					}
				}
			}
		} else {
			List<ActivityManager.RunningTaskInfo> list = activityManager.getRunningTasks(10);
			if (list == null || list.size() == 0)
				return false;
			ComponentName topActivity = list.get(0).topActivity;
			if (topActivity.getPackageName().equals(UserApp.getAppPkgName())) {
				isAppRunningForeground = true;
			}
		}
		return !isAppRunningForeground;
	}

	
	protected void checkAppInFront() {
		if(skipAppInFrontCheck)
			return;
		checkHackCounter++;
		boolean bHint=true;
		try {
			if(!isBackground(this))
				bHint=false;
		} catch (Exception e) {
			e.printStackTrace();
		}
		if(bHint){
			String showWarnings=UserApp.curApp().getGParamValue("APP_CONFIG.SHOW_LOGIN_HACK_WARNING");
			if("YES".equals(showWarnings)){
				final String s=UserApp.getResStringFmt("ba_account_login_hack_warning","警告：%s的登录界面被其它应用挡住了，可能被劫持，请不要输入%s的用户名密码等信息！！！\n", UserApp.getAppName(), UserApp.getAppName());
				UserApp.showToastLong(s);
	
				UserApp.curApp().getAppHandler().postDelayed(new Runnable() {
					
					@Override
					public void run() {
						UserApp.showToastLong(s);
					}
				}, 2500);
			} else
				UserApp.showToastLong(UserApp.getResStringFmt("ba_account_login_ui_in_background", "%s 已转到后台运行", UserApp.getAppName()));
			checkHackCounter=9999;
		}
	}
	
	class InnerRecevier extends BroadcastReceiver {
		 
        final String SYSTEM_DIALOG_REASON_KEY = "reason";
 
        final String SYSTEM_DIALOG_REASON_GLOBAL_ACTIONS = "globalactions";
 
        final String SYSTEM_DIALOG_REASON_RECENT_APPS = "recentapps";//long press home
 
        final String SYSTEM_DIALOG_REASON_HOME_KEY = "homekey";//press home
 
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (Intent.ACTION_CLOSE_SYSTEM_DIALOGS.equals(action)) {
                String reason = intent.getStringExtra(SYSTEM_DIALOG_REASON_KEY);
                if (reason != null) {
					if (reason.equals(SYSTEM_DIALOG_REASON_HOME_KEY)
							|| reason.equals(SYSTEM_DIALOG_REASON_RECENT_APPS)) {
						if (isFinishing())
							return;
						skipAppInFrontCheck = true;
						UserApp.showToastLong(UserApp.getResStringFmt("ba_account_login_ui_in_background", "%s 已转到后台运行", UserApp.getAppName()));
						try {
							unregisterReceiver(this);
						} catch (Exception e) {
						}
					}
				}
            }
        }
    }
}
