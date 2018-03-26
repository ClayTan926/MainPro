package com.ztwifi.account;

import com.netted.account.R;
import com.netted.ba.ctact.AppUrlManager;
import com.netted.ba.ctact.AppUrlParserIntf;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.View;

public class RegisterActivity extends VerPwdActivity {

	public static Class<?> registerActClass=RegisterActivity.class;
	public static int registerActLayout=R.layout.act_register;
	
	public static int regCodeCvId = 710085;
	public static int regCvId = 710086;

	@Override
	protected void doSetContentView() {
		setContentView(registerActLayout);
		this.vercodeCvId=regCodeCvId;
		this.actCvId=regCvId;
	}

	@Override
	public boolean doExecUrlEx(Activity act, View v, String url) {
		if(url.startsWith("cmd://finishReg/")){
			doIt();
			return true;
		}
		
		return super.doExecUrlEx(act, v, url);
	}
	
	@Override
	protected void showHint(String hint) {
		if("接受用户协议才能继续操作".equals(hint))
			hint="接受用户协议才能注册";
		if("操作执行成功，请重新登录".equals(hint))
			hint="注册成功，请重新登录";
		super.showHint(hint);
	}

	public static class UserRegUrlParser implements AppUrlParserIntf {

		@Override
		public String getParserName() {
			return "UserRegUrlParser";
		}

		@Override
		public String getDescribe() {
			return "用户注册URL";
		}

		@Override
		public String getSampleUrl() {
			return "app://register/";
		}

		@Override
		public String getUrlProtocol() {
			return "app://register/";
		}

		@Override
		public boolean isMyUrl(String url) {
			if (url.trim().startsWith("app://register/"))
				return true;
			else
				return false;
		}

		@Override
		public boolean gotoUrl(Context ctx, String url) {
			if (!isMyUrl(url))
				return false;
			Intent intent = new Intent(ctx, registerActClass);
			AppUrlManager.putUrlParamToIntent(url, intent);
			((Activity) ctx).startActivity(intent);
			return true;
		}

	}
}
