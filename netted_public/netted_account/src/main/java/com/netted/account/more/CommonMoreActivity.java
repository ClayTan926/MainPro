package com.netted.account.more;


import com.netted.account.UserInfoUIHelper;
import com.netted.ba.ctact.CtActEnvHelper;
import com.netted.ba.ctact.CtActEnvHelper.CtEnvViewEvents;
import com.netted.ba.ctact.CtActEnvHelper.OnCtViewUrlExecEvent;
import com.netted.ba.ctact.CvActivity;
import com.netted.common.SystemUtils;
import com.netted.common.helpers.BaseActivityHelper;
import com.netted.common.welcome.OperGuideActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class CommonMoreActivity extends CvActivity {

	private UserInfoUIHelper accHelper;

	@Override
	protected void onCreate(Bundle arg0) {
		String lyr=getIntent().getStringExtra("layout");
		if(lyr==null || lyr.length()==0)
			getIntent().putExtra("layout", "act_more");
		
		super.onCreate(arg0);

		OnCtViewUrlExecEvent urlEvt=new OnCtViewUrlExecEvent() {
			
			@Override
			public boolean doExecUrl(Activity arg0, View v, String url) {
				return CommonMoreActivity.this.doExecUrl(v, url);
			}
		};
		CtEnvViewEvents evt=CtActEnvHelper.createCtTagUIEvt(this, null, urlEvt);
		accHelper=new UserInfoUIHelper(this, evt);
	}

	@Override
	protected void onResume() {
		super.onResume();
		if(accHelper!=null)
			accHelper.refreshUserInfo();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		BaseActivityHelper.onActivityResult(this, requestCode, resultCode, data);
	}
	
	public void onCtUrlResult(String url, String tpName, Object res, View v) {
		if(url.startsWith("app://logout/") || url.startsWith("app://login/"))
			if(accHelper!=null)
				accHelper.refreshUserInfo();
	}

	@Override
	public void afterCtViewRefresh() {
		super.afterCtViewRefresh();
		View v=CtActEnvHelper.findViewOfCtName(this, "rel_oper_guide");
		if(OperGuideActivity.welcome_guide_imgs==null && v!=null){
			v.setVisibility(View.GONE);
		}

		if(accHelper!=null)
			accHelper.refreshUserInfo();
	}

	@Override
	public boolean doExecUrl(View v, String url) {
		if(url==null)
			return super.doExecUrl(v, url);
		if(url.startsWith("cmd://rate_in_market/")){
			SystemUtils.toMarket(this, getPackageName());
			return true;
		}
		else
			return super.doExecUrl(v, url);
	}
	

}
