package com.ztwifi.account;

import java.util.Map;

import com.netted.account.R;
import com.netted.ba.ct.NetUtil;
import com.netted.ba.ct.UserApp;
import com.netted.ba.ctact.CtActEnvHelper;
import com.netted.ba.ctact.CtActEnvHelper.OnCtViewUrlExecEvent;
import com.netted.ba.ctact.CtDataLoader;
import com.netted.ba.ctact.CtUrlDataLoader;
import com.netted.ba.ctact.CtDataLoader.OnCtDataEvent;
import com.netted.ba.util.EncryptUtil;

import android.app.Activity;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;

public class VerPwdActivity extends Activity {

	protected static final String ACTION = "android.provider.Telephony.SMS_RECEIVED";
	protected RegSmsReceiver receiver;
	protected int vercodeCvId=1234;
	protected int actCvId=5678;


	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		doSetContentView();
		initControls();
		
		receiver=new RegSmsReceiver(this);
        IntentFilter filter = new IntentFilter(ACTION);
        filter.setPriority(1000);//设置优先级最大
        registerReceiver(receiver, filter);
	}
	
	protected void doSetContentView() {
		//setContentView(R.layout.act_register);
	}

	@Override
	protected void onDestroy() {
		unregisterReceiver(receiver);
		super.onDestroy();
	}
	
	public void doSmsReceived(String sms){
		if(sms==null)
			return;
		int po=sms.indexOf("您的验证码：");
		if(po>=0){
			sms=sms.substring(po+6);
			po=sms.indexOf("。");
			if(po>0){
				sms=sms.substring(0,po);
				CtActEnvHelper.setViewValue(this, "yzm", sms);
			}
		}
	}
	

	public boolean doExecUrlEx(Activity act, View v, String url) {
		if(url.startsWith("cmd://sendCode/")){
			sendCode();
			return true;
		}
		if(url.startsWith("cmd://doIt/")){
			doIt();
			return true;
		}
		
		return false;
	}

	/**
	 * 初始化页面控件和添加事件
	 */
	protected void initControls() {

		OnCtViewUrlExecEvent urlEvt=new OnCtViewUrlExecEvent() {
			
			@Override
			public boolean doExecUrl(Activity act, View v, String url) {
				return doExecUrlEx(act, v, url);
			}
		};
		CtActEnvHelper.createCtTagUI(this, null, urlEvt);

	}
	
	
	protected void showHint(String hint){
		if(hint!=null && hint.length()>0)
			UserApp.showToast(this, hint);
	}
	/**
	 * 发送验证码
	 * 
	 */
	protected void sendCode() {
		CtUrlDataLoader regCodeLoader = new CtUrlDataLoader();
		regCodeLoader.init(this, 1);
		regCodeLoader.showProgress = true;

		if(!checkAccept())
			return;
		
		String phn = CtActEnvHelper.getCtViewValue(this, "phone");
		if (phn == null || phn.trim().length() == 0) {
			showHint(UserApp.getResString("ba_account_ver_please_enter_phone_num", "请输入手机号"));
			return;
		}
		regCodeLoader.custDataUrl=getVerifyCodeReqUrl();
		regCodeLoader.needVerifyCode=true;
		regCodeLoader.cacheExpireTm = UserApp.CAHCE_EXPIRE_TIME_IMMEDIA;
		OnCtDataEvent evt = new OnCtDataEvent() {
			@Override
			public void onDataCanceled() {
				showHint(UserApp.getResString("ba_msg_aborted"));
			}

			@Override
			public void onDataError(String msg) {
				showHint(msg);
			}

			@Override
			public void onDataLoaded(CtDataLoader cth) {
				if (cth.getCurrentDataMap() != null) {
					doVerCodeResult(cth.getCurrentDataMap());
				}

			}

			@Override
			public void afterFetchData() {
			}
		};
		regCodeLoader.setCtDataEvt(evt);
		regCodeLoader.loadData();
	}

	protected String getVerifyCodeReqUrl() {
		String phn = CtActEnvHelper.getCtViewValue(this, "phone");
		String url=UserApp.getBaServerCvUrl() + "&cvId="
				+ Integer.toString(vercodeCvId) + "&itemId=1&addparam=P_PHONENUM:" 
				+ NetUtil.urlEncode(phn)
				+"&addparam=P_APPTYPE:"+NetUtil.urlEncode(UserApp.getAppType());
		return url;
	}

	protected void doVerCodeResult(Map<String, Object> dm) {
		String msg=(String)dm.get("CTMESSAGE_POPUP");
		if (msg != null && msg.length()>0){
			showHint(msg);
			CtActEnvHelper.setViewValue(this, "yzm_msg", msg);
		}
		CtActEnvHelper.ShowOrHideViewById(this, null, "step1", 0);
		CtActEnvHelper.ShowOrHideViewById(this, null, "step2", 1);
	}

	protected boolean checkAccept() {
		CheckBox accept=(CheckBox) CtActEnvHelper.findViewOfCtName(this, "accept");
		if(accept==null)
			return true;
		if (!accept.isChecked()) {
			showHint(UserApp.getResString("ba_account_ver_must_accept_agreement","接受用户协议才能继续操作"));
			return false;
		}
		return true;
	}

	protected String getFinishReqUrl() {
		String phn = CtActEnvHelper.getCtViewValue(this, "phone");
		String vm=CtActEnvHelper.getCtViewValue(this, "yzm");
		String pw1 = CtActEnvHelper.getCtViewValue(this, "password");
		try {
			if("1".equals(UserApp.curApp().getGParamValue("APP_CONFIG.ENC_ACCOUNT_URL_PWDS"))){
				phn=EncryptUtil.encryptLoginPass(phn);
				vm=EncryptUtil.encryptLoginPass(vm);
				pw1=EncryptUtil.encryptLoginPass(pw1);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		String url=UserApp.getBaServerCvUrl() + "&cvId="
				+ Integer.toString(actCvId) + "&itemId=1&addparam=P_PHONENUM:"
				+ NetUtil.urlEncode(phn)
				+ "&addparam=P_VERCODE:"
				+ NetUtil.urlEncode(vm)
				+ "&addparam=P_PWD:"
				+ NetUtil.urlEncode(pw1)
				+ "&addparam=P_NAME:&addparam=P_SEX:&addparam=P_USERCITY:"
				+ "&addparam=P_DEVID:" + NetUtil.urlEncode(UserApp.curApp().getDeviceId())
				+ "&addparam=P_DEVINFO:"+NetUtil.urlEncode(UserApp.curApp().getDeviceInfo())
				+ "&addparam=P_APPTYPE:"+NetUtil.urlEncode(UserApp.getAppType())
				+ "&addparam=P_MARKET:"+NetUtil.urlEncode(UserApp.curApp().getAppMarket())
				+ "&addparam=P_APPID:"+NetUtil.urlEncode(UserApp.getAppPkgName())
				+ "&addparam=P_CURVER:"+NetUtil.urlEncode(UserApp.getAppSysVer())
				+ "&addparam=P_USAGREE:1";
		return url;
	}

	protected void doIt() {
		CtUrlDataLoader actHelper;
		actHelper = new CtUrlDataLoader();
		actHelper.init(this, 1);
		actHelper.showProgress = true;

		if(!checkAccept())
			return;
		if(!checkVerifyCode())
			return;
		
		final String phn = CtActEnvHelper.getCtViewValue(this, "phone");
		final String pw1 = CtActEnvHelper.getCtViewValue(this, "password");
		String pw2 = CtActEnvHelper.getCtViewValue(this, "password2");
		if (pw1 == null || pw1.length() == 0) {
			showHint(UserApp.getResString("ba_account_ver_please_enter_password","请输入密码"));
			return;
		}
		if (pw2 == null || !pw1.equals(pw2)) {
			showHint(UserApp.getResString("ba_account_ver_passwords_not_matched","两次输入的密码不一样，请确认"));
			return;
		}
		actHelper.custDataUrl=getFinishReqUrl();
		actHelper.needVerifyCode=true;
		actHelper.cacheExpireTm = UserApp.CAHCE_EXPIRE_TIME_IMMEDIA;
		OnCtDataEvent evt = new OnCtDataEvent() {
			@Override
			public void onDataCanceled() {
				showHint(UserApp.getResString("ba_msg_aborted"));
			}

			@Override
			public void onDataError(String msg) {
				showHint(msg);
			}

			@Override
			public void onDataLoaded(CtDataLoader cvth) {
				returnToLogin(phn, pw1);
			}

			@Override
			public void afterFetchData() {
			}
		};
		actHelper.setCtDataEvt(evt);
		actHelper.loadData();
	}

	public boolean checkVerifyCode() {
		if(this.findViewById(R.id.yzm)==null)
			return true;
		String vm=CtActEnvHelper.getCtViewValue(this, "yzm");
		if (vm == null
				|| vm.trim().length() == 0) {
			showHint(UserApp.getResString("ba_account_ver_please_enter_verify_code","请输入验证码"));
			return false;
		}
		return true;
	}

	/**
	 * 返回登录界面
	 * 
	 * @param name
	 * @param password
	 */
	protected void returnToLogin(String name, String password) {
		showHint(UserApp.getResString("ba_account_ver_operation_success_turn_to_login","操作执行成功，请重新登录"));
		String login_returnurl=this.getIntent().getStringExtra("returnurl");
		if(login_returnurl==null)
			login_returnurl="";
		UserApp.callAppURL(this,null,"app://login/?username="+NetUtil.urlEncode(name)+"&password="+NetUtil.urlEncode(password)+
				"&returnurl="+NetUtil.urlEncode(login_returnurl));
		finish();
	}
}
