package com.ztwifi.ba.lib_loader;

import com.netted.ba.ct.UserApp;
import com.umeng.analytics.MobclickAgent;

import android.app.Activity;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;

/*
 * 安卓库工程自动加载类
 * 所有库工程只要继承此类，并放在com.netted.com.ztwifi.wallet.ba.lib_loader下，实现相关方法，即可自动完成所有初始化工作
 */
public class NettedStatLibLoader extends AppLibLoader {

	/*
	 * 程序初始化
	 */
	@Override
	public void onAppInit() {
		ApplicationInfo appInfo;
		try {
			appInfo = theApp.getPackageManager().getApplicationInfo(
					theApp.getPackageName(), PackageManager.GET_META_DATA);
			String s = null;
			if (appInfo.metaData != null)
				s = appInfo.metaData.getString("UMENG_CHANNEL");
			if (s != null && s.length() > 0)
				UserApp.theAppMarketName = s;
			MobclickAgent.setLocation(113,21);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	@Override
	public void onActResume(Activity act) {
		MobclickAgent.onResume(act);
		MobclickAgent.onEvent(act, "ViewAct", act.getClass().getName());
	}

	@Override
	public void onActPause(Activity act) {
		MobclickAgent.onPause(act);
	}

	@Override
	public void onAppEvent(Context context, String event_id, String label,
			int acc) {
		MobclickAgent.onEvent(context, event_id, "["+acc+"]"+label);
	}

	@Override
	public void onAppError(String err) {
		MobclickAgent.reportError(theApp, err);
	}

}
