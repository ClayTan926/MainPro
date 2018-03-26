package com.netted.account.more;

import java.util.HashMap;
import java.util.Map;

import com.netted.account.R;
import com.netted.ba.ct.UserApp;
import com.netted.ba.ctact.CtActEnvHelper;
import com.netted.ba.ctact.CtDataLoader;
import com.netted.ba.ctact.CvDataLoader;
import com.netted.ba.ctact.CtActEnvHelper.OnCtViewUrlExecEvent;
import com.netted.ba.ctact.CtDataLoader.OnCtDataEvent;
import com.netted.ba.ctact.TraceLogActivity;

import android.app.Activity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class FeedBackActivity extends Activity {
	public static int feedBackCvid = 710043;
	private int cvId = feedBackCvid;
	private CvDataLoader theHelper;

	private EditText etData; // 反馈信息
	private EditText etContactInfo; // 联系方式
	private Button btnOk; // 提交
	private TextView textnum;
	private String contactInfo;
	public String fb_type;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.act_feedback);
		TextView tv_title = (TextView) findViewById(R.id.middle_title);
		Bundle bd = this.getIntent().getExtras();
		if (bd != null) {
			String title = bd.getString("title");
			fb_type = bd.getString("fb_type");
			if (title != null && title.length() > 0)
				tv_title.setText(title);
			String rightLayoutVisible = bd.getString("rightLayout");
			if (rightLayoutVisible != null && rightLayoutVisible.length() > 0) {
				if (rightLayoutVisible.equals("1")) {
					findViewById(R.id.right_layout).setVisibility(View.VISIBLE);
				}
			}
			String tv1 = bd.getString("tv1");
			if (tv1 != null && tv1.length() > 0) {
				((TextView) findViewById(R.id.tv1)).setText(tv1);
			}
		}
		initControls();

		OnCtViewUrlExecEvent urlEvt = new OnCtViewUrlExecEvent() {
			@Override
			public boolean doExecUrl(Activity act, View v, String url) {
				return false;
			}
		};
		CtActEnvHelper.createCtTagUI(this, null, urlEvt);
	}

	/**
	 * 初始化页面控件和添加事件
	 */
	protected void initControls() {
		// 返回
		findViewById(R.id.left_layout).setOnClickListener(
				new OnClickListener() {
					@Override
					public void onClick(View v) {
						finish();
					}

				});

		etData = (EditText) findViewById(R.id.etdata);
		etContactInfo = (EditText) findViewById(R.id.etrelation);
		btnOk = (Button) findViewById(R.id.btnok);
		textnum = (TextView) findViewById(R.id.textnum);
		// 设置输入字符数监听事件
		etData.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				int len = s.toString().length();
				int i = 200 - len;
				textnum.setText(i + "");
				if (i == 0) {
					Toast.makeText(FeedBackActivity.this, "最多不能超过200字！感谢您的反馈",
							Toast.LENGTH_SHORT).show();
				}
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
			}

			@Override
			public void afterTextChanged(Editable s) {
			}
		});

		btnOk.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				sendFeedBack();
			}
		});
	}

	private void sendFeedBack() {
		String msg = etData.getText().toString();
		contactInfo = etContactInfo.getText().toString();

		if (msg == null || msg.trim().length() == 0) {
			UserApp.showToast("反馈内容为空，请输入您的反馈内容再提交！");
			return;
		}

		String res = UserApp.curApp().callCustCmd(this, "USER_FEEDBACK", msg,
				null);
		if (res == null || res.length() == 0 || res.equals(msg)) {

		} else if ("[CANCEL]".equals(res))
			return;
		else
			msg = res;

		if ("27511".equals(msg.trim())) {
			UserApp.curApp().setSharePrefParamValue(
					"BusInfo.realTimeInfoEnabled", "2");
			Log.d("realTimeInfoEnabled", UserApp.curApp()
					.getSharePrefParamValue("BusInfo.realTimeInfoEnabled", "0"));
			UserApp.curApp().setSharePrefParamValue("DEMO_MODE", "1");
			UserApp.curApp().setGParamValue("DEMO_MODE", "1");
			finish();
			return;
		}
		if ("27510".equals(msg.trim())) {
			UserApp.curApp().setSharePrefParamValue(
					"BusInfo.realTimeInfoEnabled", "0");
			Log.d("realTimeInfoEnabled", UserApp.curApp()
					.getSharePrefParamValue("BusInfo.realTimeInfoEnabled", "0"));
			UserApp.curApp().setSharePrefParamValue("DEMO_MODE", "0");
			UserApp.curApp().setGParamValue("DEMO_MODE", "0");
			finish();
			return;
		}
		if ("2751".equals(msg.trim())) {
			UserApp.temp_debug_mode = true;
			UserApp.startActivity(this, TraceLogActivity.class, false, null);
			finish();
			return;
		}

		update(msg, contactInfo);
	}

	public void update(String msg, String contactInfo) {
		if (theHelper == null) {
			theHelper = new CvDataLoader();
			theHelper.init(this, cvId);
			theHelper.showProgress = true;
		}
		Map<String, Object> pars = new HashMap<String, Object>();
		pars.put("fb_user",
				UserApp.getAppType() + "@" + UserApp.getAppSysVer() + "@"
						+ UserApp.curApp().getUserName() + "@" + "main_form"
						+ "@" + UserApp.curApp().getDeviceInfo() + "@"
						+ UserApp.getAppPkgName() + "@" + UserApp.curApp().getDeviceId());
		pars.put("fb_type", UserApp.getAppType());
		if (fb_type != null && fb_type.length() > 0)
			pars.put("fb_type", fb_type);
		pars.put("fb_msg", msg);
		if (contactInfo == null || contactInfo.trim().length() == 0)
			contactInfo = UserApp.curApp().getUserName();
		pars.put("fb_contactinfo", contactInfo);
		theHelper.postParams = pars;

		theHelper.cacheExpireTm = UserApp.CAHCE_EXPIRE_TIME_HOUR;
		OnCtDataEvent evt = new OnCtDataEvent() {
			@Override
			public void onDataCanceled() {
				UserApp.showToast(FeedBackActivity.this, "操作中止");
			}

			@Override
			public void onDataError(String msg) {
				UserApp.showToast(FeedBackActivity.this, msg);
			}

			@Override
			public void onDataLoaded(CtDataLoader cvth) {
				if (theHelper.getCurrentDataMap() != null) {
					Map<String, Object> ds = theHelper.getCurrentDataMap();
					if (ds.get("feedback_res") == null
							|| "".equals(ds.get("feedback_res").toString())) {
						UserApp.showToast(FeedBackActivity.this, "提交失败");
					} else {
						UserApp.showToast(FeedBackActivity.this,
								ds.get("feedback_res").toString());
						finish();
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
}
