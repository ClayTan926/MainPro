package com.ztwifi.ba.lib_loader;

import com.netted.account.LoginActivity;
import com.netted.account.RegisterActivity;
import com.netted.ba.ct.UserApp;
import com.netted.ba.ctact.AppUrlManager;

/*
 * 安卓库工程自动加载类
 * 所有库工程只要继承此类，并放在com.netted.com.ztwifi.wallet.ba.lib_loader下，实现相关方法，即可自动完成所有初始化工作
 */
public class NettedAccountLibLoader extends AppLibLoader {

	/*
	 * 程序初始化
	 */
	@Override
	public void onAppInit() {

		// URL注册
		// 登录
		AppUrlManager.registerParser(new LoginActivity.LoginUrlParser());
		AppUrlManager.registerParser(new RegisterActivity.UserRegUrlParser());

		AppUrlManager.registerActParser("find_password",
				"com.netted.account.FindPasswordActivity");

		AppUrlManager.registerActParser("change_password",
				"com.netted.account.ChangePasswordActivity");

		// 我的
		AppUrlManager.registerActParser("my",
				"com.netted.account.AccountActivity");

		// 反馈
		AppUrlManager.registerActParser("feedback",
				"com.netted.account.more.FeedBackActivity");
		// 更多
		AppUrlManager.registerActParser("more",
				"com.netted.account.more.CommonMoreActivity");
		// 收藏
		AppUrlManager.registerActParser("favs",
				"com.netted.account.more.CollectActivity");

		// 关于
		AppUrlManager.registerUrlReplacer("act://about/",
				"act://cv/?layout=act_about");

		// 消息
		theApp.setGParamValue("APP_SETTINGS.MY_MESSAGES.CVID", "710143");
		String s=UserApp.getResString("ba_account_my_messages","我的消息");
		AppUrlManager
				.registerUrlReplacer(
						"act://my_messages/",
						"act://cv/?layout=act_my_message&checkLogin=4&checkLoginName="+s+"&redirect=1&cvId=${GPARAM[APP_SETTINGS.MY_MESSAGES.CVID]}&itemId=1&"
								+ "extraParams=[[addparam=WMCV_TITLE:"+s+"&addparam=P_SYSTYPE:Android&addparam=P_CLIENTTYPE:${APPTYPE}&addparam=P_USINFO:${USERID}_${TICK}]]");
	
	   
		// 站点结果
				AppUrlManager.registerActParser("bussationresult",
						"com.netted.bus.busstation.BusStationResultActivity");
	
	}

}
