package com.ztwifi.account;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.telephony.SmsMessage;

public class RegSmsReceiver extends BroadcastReceiver {
    private Activity theAct;

	public RegSmsReceiver(Activity act) {
    	this.theAct=act;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
    	if(intent==null || intent.getExtras()==null)
    		return;
       Object[] pdus = (Object[]) intent.getExtras().get("pdus");
       if (pdus != null && pdus.length > 0) {
           SmsMessage[] messages = new SmsMessage[pdus.length];
           for (int i = 0; i < pdus.length; i++) {
               byte[] pdu = (byte[]) pdus[i];
               messages[i] = SmsMessage.createFromPdu(pdu);
           }
           for (SmsMessage message : messages) {
               String content = message.getMessageBody();// 得到短信内容
               if(theAct instanceof RegisterActivity)
            	   ((RegisterActivity)theAct).doSmsReceived(content);
               else if(theAct instanceof FindPasswordActivity)
            	   ((FindPasswordActivity)theAct).doSmsReceived(content);
           }
       }
   }
}
