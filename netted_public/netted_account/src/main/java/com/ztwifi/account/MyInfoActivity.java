package com.ztwifi.account;

import java.util.HashMap;
import java.util.Map;

import com.netted.account.R;
import com.netted.ba.ct.UserApp;
import com.netted.ba.ctact.CtDataLoader;
import com.netted.ba.ctact.CvDataLoader;
import com.netted.ba.ctact.CtDataLoader.OnCtDataEvent;
import com.netted.common.ui.UIUtils;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class MyInfoActivity extends Activity {
	private int loadCvId = UserInfoUIHelper.userInfoCvId;
	private int updateCvId = UserInfoUIHelper.userInfoEditCvId;
	private CvDataLoader theHelper;
	private CvDataLoader updateHelper;
	private String update;

	private RelativeLayout info_name;
	private RelativeLayout info_sex;
	private RelativeLayout info_email;
	private RelativeLayout info_password;
	private Button save;
	private TextView phone;
	private EditText name;
	private TextView sex;
	private EditText email;
	
	private String dataName;
	private String dataSex;
	private String dataEmail;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.act_myinfo);
		initControls();
		loadData();
	}

	public void onResume() {
		super.onResume();
	}

	/**
	 * 初始化页面控件和添加事件
	 */
	protected void initControls() {
		// 返回
		findViewById(R.id.left_layout).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}

		});
		phone = (TextView) findViewById(R.id.phone);
		phone.setText(UserApp.curApp().getUserPhoneNum());
		name = (EditText) findViewById(R.id.name);
		info_name = (RelativeLayout) findViewById(R.id.info_name);
		info_name.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				name.requestFocus();
				name.setSelection(name.getText().length());
				UIUtils.showKeyboard(name);
			}
		});
		sex = (TextView) findViewById(R.id.sex);
		info_sex = (RelativeLayout) findViewById(R.id.info_sex);
		info_sex.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (sex.getText().equals("男")) {
					sex.setText("女");
				} else {
					sex.setText("男");
				}
			}
		});
		email = (EditText) findViewById(R.id.email);
		info_email = (RelativeLayout) findViewById(R.id.info_email);
		info_email.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				email.requestFocus();
				email.setSelection(email.getText().length());
				UIUtils.showKeyboard(email);
			}
		});
		info_password = (RelativeLayout) findViewById(R.id.info_password);
		info_password.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent=new Intent(MyInfoActivity.this,ChangePasswordActivity.class);
			    startActivity(intent);
			}
		});
		
		save = (Button) findViewById(R.id.save);
		save.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if(!name.getText().toString().equals(dataName) || !email.getText().toString().equals(dataEmail) || !sex.getText().toString().equals(dataSex)){
					update();
				}else{
					Toast.makeText(MyInfoActivity.this, "暂无信息修改，无需保存", Toast.LENGTH_SHORT).show();
				}
			}
		});
	}

	private void loadData() {
		if (UserApp.curApp().getGParamValue("MYINFO_UPDATE") == null) {
			UserApp.curApp().setGParamValue("MYINFO_UPDATE",
					System.currentTimeMillis());
		} else {
			if (update != null
					&& update.equals(UserApp.curApp()
							.getGParamValue("MYINFO_UPDATE").toString())) {
				return;
			}
		}
		update = UserApp.curApp().getGParamValue("MYINFO_UPDATE");
		if (theHelper == null) {
			theHelper = new CvDataLoader();
			theHelper.init(this, loadCvId);
			theHelper.showProgress = true;
		}
		theHelper.extraParams = "addparam=update:"+ update;
		theHelper.cacheExpireTm = UserApp.CAHCE_EXPIRE_TIME_HOUR;
		theHelper.showProgress = true;
		theHelper.loadingMessage = "加载需要时间，请耐心等待";
		OnCtDataEvent evt = new OnCtDataEvent() {
			@Override
			public void onDataCanceled() {
				UserApp.showToast(MyInfoActivity.this, "操作中止");
			}

			@Override
			public void onDataError(String msg) {
				UserApp.showToast(MyInfoActivity.this, msg);
			}

			@Override
			public void onDataLoaded(CtDataLoader cvth) {
				if (theHelper.getCurrentDataMap() != null) {
					Map<String, Object> ds = theHelper.getCurrentDataMap();

					if (ds.get("userName") != null) {
						dataName=ds.get("userName").toString();
						name.setText(dataName);
					}
					if (ds.get("userSex") != null) {
						dataSex=ds.get("userSex").toString();
						sex.setText(dataSex);
					}
					if (ds.get("userEmail") != null) {
						dataEmail= ds.get("userEmail").toString();
						email.setText(dataEmail);
					}
				}

			}

			@Override
			public void afterFetchData() {
			}
		};
		theHelper.setCtDataEvt(evt);
		theHelper.loadData(0);
	}

	private void update() {
		if (updateHelper == null) {
			updateHelper = new CvDataLoader();
			updateHelper.init(this, updateCvId);
			updateHelper.showProgress = true;
		}
		String nameVal = name.getText().toString().trim();
		String sexVal = sex.getText().toString().trim();
		String emailVal = email.getText().toString().trim();
		Map<String, Object> pars = new HashMap<String, Object>();
		pars.put("u_username", nameVal);
		pars.put("u_sex", sexVal);
		pars.put("u_email", emailVal);

		updateHelper.postParams = pars;
		updateHelper.loadingMessage="正在保存...";
		updateHelper.cacheExpireTm = UserApp.CAHCE_EXPIRE_TIME_IMMEDIA;
		OnCtDataEvent evt = new OnCtDataEvent() {
			@Override
			public void onDataCanceled() {
				UserApp.showToast(MyInfoActivity.this, "操作中止");
			}

			@Override
			public void onDataError(String msg) {
				UserApp.showToast(MyInfoActivity.this, msg);
			}

			@Override
			public void onDataLoaded(CtDataLoader cvth) {
				if (updateHelper.getCurrentDataMap() != null) {
					Map<String, Object> ds = updateHelper.getCurrentDataMap();
					if (ds.get("userupdate_res") == null
							|| ds.get("userupdate_res").equals("")) {
						UserApp.showToast("更新失败");
					} else {
						UserApp.showToast((String) ds.get("userupdate_res"));
						if ("信息修改成功！".equals(ds.get("userupdate_res")
								.toString())) {
							UserApp.curApp().setGParamValue("MYINFO_UPDATE",
									System.currentTimeMillis());
							finish();
						}
					}
				}

			}

			@Override
			public void afterFetchData() {
			}
		};
		updateHelper.setCtDataEvt(evt);
		updateHelper.loadData(0);
	}
}
