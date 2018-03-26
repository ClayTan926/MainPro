package com.ztwifi.account;

import com.netted.account.R;
import com.netted.common.helpers.BaseActivityHelper;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;

/**
 * 类<code>UserAgreementActivity</code> 用户协议.
 * 
 * @author hqm
 * @version 2012-05-28
 * @see java.lang.Class
 * @since JDK 1.6
 */
public class UserAgreementActivity extends Activity {
	protected BaseActivityHelper baseHelper = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.act_useragreement);
		baseHelper = new BaseActivityHelper();
		baseHelper.init(this);

		initControls();
	}
	

	/**
	 * 初始化页面控件和添加事件
	 */
	protected void initControls() {
		//
		baseHelper.title_view.setText("用户协议");
		/*
		Button agree_btn = (Button) this.findViewById(R.id.agree_bt);
		agree_btn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});	*/	
		this.findViewById(R.id.left_layout).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});		
	}

	@Override
	protected void onPause() {
		super.onPause();
		BaseActivityHelper.onPause(this);
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		BaseActivityHelper.onResume(this);
	}
}
