package com.ztwifi.account;

import android.app.Activity;
import android.view.View;

import com.netted.ba.ctact.CtActEnvHelper;
import com.netted.ba.ctact.CvActivity;
import com.netted.ba.ctact.CtActEnvHelper.CtEnvViewEvents;
import com.netted.ba.ctact.CtActEnvHelper.OnCtViewUrlExecEvent;

public class AccountActivity extends CvActivity {

	private UserInfoUIHelper accHelper;
	
	protected void doOnCreate() {
		OnCtViewUrlExecEvent urlEvt=new OnCtViewUrlExecEvent() {
			
			@Override
			public boolean doExecUrl(Activity arg0, View v, String url) {
				return AccountActivity.this.doExecUrl(v, url);
			}
		};
		CtEnvViewEvents evt=CtActEnvHelper.createCtTagUIEvt(this, null, urlEvt);
		accHelper=new UserInfoUIHelper(this, evt);
		
		doCreateWithLayout("act_account");
	}

	@Override
	protected void onResume() {
		super.onResume();
		accHelper.refreshUserInfo();
	}



}
