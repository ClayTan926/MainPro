package com.ztwifi.account;

import com.netted.ba.ct.UserApp;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

public class LoginRegActivity extends Activity{
	
	protected int step=0; //1启动RESUME 2弹登录框PAUSE 3回到本界面RESUME

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.ct_loading);
		TextView tv = (TextView) this
				.findViewById(R.id.ct_loading_title);
		if (tv != null)
			tv.setText(UserApp.getResString("ba_account_msg_checking_login","正在检查登录.."));
		overridePendingTransition(0,0);
	}

	@Override
	protected void onPause() {
		if(step==1){
			step=2;
			UserApp.LogD("LoginReg: 正在登录..");
		}
		super.onPause();
	}

	@Override
	protected void onResume() {
		if(step==0){
			step=1;
			UserApp.LogD("LoginReg: 启动登录..");
			UserApp.callAppURL(this, null, "app://login/");
		} else if(step==2){
			step=3;
			Intent data=new Intent();
			int uid=0;
			if(UserApp.curApp().isLoggedIn())
				uid=UserApp.curApp().getUserId();
			data.putExtra("UserId", uid);
			data.putExtra("UserName", UserApp.curApp().getUserName());
			data.putExtra("RealName", UserApp.curApp().getUserRealName());
			this.setResult(0, data);
			UserApp.LogD("LoginReg: 完成登录.."+uid+":"+UserApp.curApp().getUserName());
			this.finish();
		}
		super.onResume();
	}

}
