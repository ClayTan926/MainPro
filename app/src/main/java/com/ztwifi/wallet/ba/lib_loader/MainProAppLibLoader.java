package com.ztwifi.wallet.ba.lib_loader;


import android.util.Log;

import com.netted.ba.lib_loader.AppLibLoader;

/*
 * 安卓库工程自动加载类
 * 所有库工程只要继承此类，并放在com.netted.ba.lib_loader下，实现相关方法，即可自动完成所有初始化工作
 */
public class MainProAppLibLoader extends AppLibLoader {
	private static final String TAG = "MainProAppLibLoader";
	/*
	 * 程序初始化
	 */
	@Override
	public void onAppInit() {
		Log.e(TAG, "onAppInit: APP自动加载类" );
		//URL注册
	}
}
