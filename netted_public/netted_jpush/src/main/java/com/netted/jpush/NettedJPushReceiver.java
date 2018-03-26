package com.netted.jpush;

import java.util.HashMap;
import java.util.Map;

import com.netted.ba.ct.UserApp;
import com.netted.ba.ctact.AppUrlManager;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import cn.jpush.android.api.JPushInterface;

/**
 * 自定义接收器
 * 
 * 如果不定义这个 Receiver，则：
 * 1) 默认用户会打开主界面
 * 2) 接收不到自定义消息
 */
public class NettedJPushReceiver extends BroadcastReceiver {
	public static Class<Activity> jpushMsgActiviyClass = null;
	
	@Override
	public void onReceive(Context context, Intent intent) {
        Bundle bundle = intent.getExtras();
		UserApp.LogD("[NettedJPushReceiver] onReceive - " + intent.getAction());
		
        if (JPushInterface.ACTION_REGISTRATION_ID.equals(intent.getAction())) {
            String regId = bundle.getString(JPushInterface.EXTRA_REGISTRATION_ID);
            UserApp.LogD("[NettedJPushReceiver] 接收Registration Id : " + regId);
        } else if (JPushInterface.ACTION_MESSAGE_RECEIVED.equals(intent.getAction())) {
        	String msg=bundle.getString(JPushInterface.EXTRA_MESSAGE);
        	UserApp.LogD("[NettedJPushReceiver] 接收到推送下来的自定义消息: " + msg);

            Map<String, Object> msgParams=new HashMap<String, Object>();
            AppUrlManager.putIntentToParamMap(intent, msgParams);
            UserApp.curApp().onPushMessageReceived(0, msg, msgParams);
        } else if (JPushInterface.ACTION_NOTIFICATION_RECEIVED.equals(intent.getAction())) {
            int notifactionId = bundle.getInt(JPushInterface.EXTRA_NOTIFICATION_ID);
            UserApp.LogD("[NettedJPushReceiver] 接收到推送下来的通知的ID: " + notifactionId);
            
            Map<String, Object> msgParams=new HashMap<String, Object>();
            AppUrlManager.putIntentToParamMap(intent, msgParams);
            UserApp.curApp().onPushMessageReceived(1, Integer.toString(notifactionId), msgParams);
        	
        } else if (JPushInterface.ACTION_NOTIFICATION_OPENED.equals(intent.getAction())) {
        	if(jpushMsgActiviyClass == null){
        		
        	}else{
        		Map<String, Object> msgParams=new HashMap<String, Object>();
                AppUrlManager.putIntentToParamMap(intent, msgParams);
                UserApp.curApp().onPushMessageReceived(-1, null, msgParams);
        	}
        }
	}

}
