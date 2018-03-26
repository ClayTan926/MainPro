package com.ztwifi.ba.lib_loader;

import java.util.regex.Pattern;

import android.app.Activity;
import cn.jpush.android.api.JPushInterface;

import com.netted.ba.ct.NetUtil;
import com.netted.ba.ct.UserApp;
import com.netted.ba.ctact.CtUrlDataLoader;

/*
 * 安卓库工程自动加载类
 * 所有库工程只要继承此类，并放在com.netted.com.ztwifi.wallet.ba.lib_loader下，实现相关方法，即可自动完成所有初始化工作
 *     
 * 将manifest文件中注释的内容拷到主工程
 * 用户APP中可继承以下方法：
 *  @Override
    public String getUserPushId() {
    	return xxx; //根据需要设定PushId，默认会在有登录时自动生成
    }
    
    @Override
    protected void initGlobalParams() {
    	super.initGlobalParams();
    	//设置URL将PUSHID发到服务
		this.setGParamValue("APP_CONFIG.NETTED_JPUSH_REGTK_URL",
				"http://192.0.0.16/asdft/regpush.nx?dste=${USERID}&sdft=${PUSHID}&tk=${TICK}");
		// 设置URL将PUSHID从服务端注销
		this.setGParamValue("APP_CONFIG.NETTED_JPUSH_UNREGTK_URL",
				"http://192.0.0.16/asdft/unregpush.nx?dste=${USERID}&sdft=${PUSHID}&tk=${TICK}");
    }
    
    在APP和LibLoader中可继承以下方法接收消息：
	public void onPushMessageReceived(int msgType, String msgContent, Map<String, Object> msgParams) {
		super.onPushMessageReceived(msgType, msgContent, msgParams);
		//msgType: 0消息 1通知 -1用户点击了消息
	}
	
proguard混淆时要加上以下配置：（必须使用最新版proguard）
-dontwarn cn.jpush.**
-keep class cn.jpush.** { *; }
 */
public class NettedJPushLibLoader extends AppLibLoader {

	public static boolean enableJPushDebug = false;
	public static String DEF_NETTED_JPUSH_REGTK_URL=null;
	public static String DEF_NETTED_JPUSH_UNREGTK_URL=null;
	
	/*
	 * 程序初始化
	 */
	@Override
	public void onAppInit() {
		if (UserApp.isDebugVersion() && enableJPushDebug)
			JPushInterface.setDebugMode(true); // 设置开启日志,发布时请关闭日志
		//checkRegPush();
	}

	protected void checkRegPush() {
		
		String userId = theApp.getUserId()+"";
		if (userId == null || userId.length() == 0){
			JPushInterface.stopPush(theApp);
			return;
		}
		String pushId = theApp.getUserPushId();
		if (pushId == null || pushId.length() == 0){
			JPushInterface.stopPush(theApp);
			return;
		}
		
		// 初始化 JPush
		JPushInterface.init(theApp);
		JPushInterface.setAliasAndTags(theApp, pushId, null);
		JPushInterface.resumePush(theApp);

		String surl=DEF_NETTED_JPUSH_REGTK_URL;
		if(surl==null || surl.length()==0)
			surl=UserApp.curApp().getGParamValue("APP_CONFIG.NETTED_JPUSH_REGTK_URL");
		if(surl==null || surl.length()==0)
			return;
		if(surl.contains("${PUSHID}"))
			surl=surl.replaceAll(Pattern.quote("${PUSHID}"), NetUtil.urlEncode(pushId));
		if(surl.contains("${USERID}"))
			surl=surl.replaceAll(Pattern.quote("${USERID}"), NetUtil.urlEncode(userId));
		CtUrlDataLoader ld=new CtUrlDataLoader();
		ld.custDataUrl=surl;
		ld.init(theApp, 1);
		ld.showProgress=false;
		ld.loadData();
	}

	@Override
	public void afterUserLogin(boolean isAutoLogin) {
		super.afterUserLogin(isAutoLogin);
		checkRegPush();
	}

	protected void checkUnRegPush() {
		String userId = theApp.getUserId()+"";
		if (userId == null || userId.length() == 0){
			JPushInterface.stopPush(theApp);
			return;
		}
		String pushId = theApp.getUserPushId();
		if (pushId == null || pushId.length() == 0){
			JPushInterface.stopPush(theApp);
			return;
		}
		
		String surl=DEF_NETTED_JPUSH_UNREGTK_URL;
		if(surl==null || surl.length()==0)
			surl=UserApp.curApp().getGParamValue("APP_CONFIG.NETTED_JPUSH_UNREGTK_URL");
		if(surl==null || surl.length()==0)
			return;
		if(surl.contains("${PUSHID}"))
			surl=surl.replaceAll(Pattern.quote("${PUSHID}"), NetUtil.urlEncode(pushId));
		if(surl.contains("${USERID}"))
			surl=surl.replaceAll(Pattern.quote("${USERID}"), NetUtil.urlEncode(userId));
		CtUrlDataLoader ld=new CtUrlDataLoader();
		ld.custDataUrl=surl;
		ld.init(theApp, 1);
		ld.showProgress=false;
		ld.loadData();
	}

	@Override
	public void beforeUserLogout() {
		super.beforeUserLogout();
		checkUnRegPush();
	}
	
	@Override
	public void onActPause(Activity act) {
		if(act!=null && act.getParent()==null){
			//UserApp.LogD("jpush_act pause:"+act);
			JPushInterface.onPause(act);
		}
		super.onActPause(act);
	}
	
	@Override
	public void onActResume(Activity act) {
		super.onActResume(act);
		if(act!=null && act.getParent()==null){
			JPushInterface.onResume(act);
			//UserApp.LogD("jpush_act resume:"+act);
		}
	}
}
