package com.ztwifi.account;

import java.util.HashMap;
import java.util.Map;

import com.netted.account.R;
import com.netted.ba.ct.TypeUtil;
import com.netted.ba.ct.UserApp;
import com.netted.ba.ctact.CtActEnvHelper;
import com.netted.ba.ctact.CtDataLoader;
import com.netted.ba.ctact.CvDataLoader;
import com.netted.ba.ctact.CtActEnvHelper.OnCtViewUrlExecEvent;
import com.netted.ba.ctact.CtDataLoader.OnCtDataEvent;
import com.netted.ba.util.EncryptUtil;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;

public class ChangePasswordActivity extends Activity {
	private int updateCvId = UserInfoUIHelper.userInfoEditCvId;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.act_change_password);
		initControls();
	}

	public boolean doExecUrlEx(Activity act, View v, String url) {
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
	
	protected void doIt() {
		
		CvDataLoader updateHelper = new CvDataLoader();
		updateHelper.init(this, updateCvId);
		updateHelper.showProgress = true;
		
		String oldpasswordVal = CtActEnvHelper.getCtViewValue(this, "oldpassword");
		String newpasswordVal = CtActEnvHelper.getCtViewValue(this, "password");
		String newpassword2Val = CtActEnvHelper.getCtViewValue(this, "password2");
		Map<String, Object> pars = new HashMap<String, Object>();
		if (oldpasswordVal == null || oldpasswordVal.length() == 0) {
			showHint(UserApp.getResString("ba_account_change_pwd_enter_old_pwd", "请输入原密码"));
			return;
		}
		if (newpasswordVal == null || newpasswordVal.length() == 0) {
			showHint(UserApp.getResString("ba_account_change_pwd_enter_new_pwd", "请输入新密码!"));
			return;
		}
		if (newpassword2Val == null || newpassword2Val.length() == 0) {
			showHint(UserApp.getResString("ba_account_change_pwd_confirm_new_pwd", "请输入确认密码!"));
			return;
		}
		if(!newpasswordVal.equals(newpassword2Val)){
			showHint(UserApp.getResString("ba_account_change_pwd_passwords_not_matched", "两次输入的新密码不一致!"));
			return;
		}


		try {
			if("1".equals(UserApp.curApp().getGParamValue("APP_CONFIG.ENC_ACCOUNT_URL_PWDS"))){
				oldpasswordVal=EncryptUtil.encryptLoginPass(oldpasswordVal);
				newpasswordVal=EncryptUtil.encryptLoginPass(newpasswordVal);
				newpassword2Val=EncryptUtil.encryptLoginPass(newpassword2Val);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		pars.put("u_oldpassword", oldpasswordVal);
		pars.put("u_password1", newpasswordVal);
		pars.put("u_password2", newpassword2Val);

		updateHelper.postParams = pars;

		updateHelper.cacheExpireTm = UserApp.CAHCE_EXPIRE_TIME_IMMEDIA;
		OnCtDataEvent evt = new OnCtDataEvent() {
			@Override
			public void onDataCanceled() {
				showHint(UserApp.getResString("ba_mst_aborted"));
			}

			@Override
			public void onDataError(String msg) {
				showHint(msg);
			}

			@Override
			public void onDataLoaded(CtDataLoader cvth) {
				if (cvth.getCurrentDataMap() != null) {
					doActSuccess(cvth);
				}

			}

			@Override
			public void afterFetchData() {
			}
		};
		updateHelper.setCtDataEvt(evt);
		updateHelper.loadData(0);
	}

	private void doActSuccess(CtDataLoader cvth) {
		Map<String, Object> ds = cvth.getCurrentDataMap();
		if (ds.get("userupdate_res") == null
				|| ds.get("userupdate_res").equals("")) {
			showHint(UserApp.getResString("ba_account_change_password_failed", "密码修改失败"));
		} else {
			String rs=TypeUtil.ObjToStrNotNull(ds.get("userupdate_res"));
			if(rs.contains("成功") || rs.contains("success")) {

				boolean savePwd = "1".equals(UserApp.curApp().getUserPropValue("SAVEPWD"));
				if(savePwd)
				try {
					String pass = CtActEnvHelper.getCtViewValue(this, "password");
					String password = EncryptUtil.getEncPassBody(EncryptUtil.encryptLoginPass(pass));
					UserApp.curApp().getUserProps().put("PASSWORD", password);
					UserApp.curApp().saveUserInfo();
				} catch (Exception e) {
					e.printStackTrace();
					showHint(UserApp.getResString("ba_account_change_pwd_save_pwd_failed", "保存密码出错: 信息加密失败"));
				}

				UserApp.showMessageEx(this, UserApp.getResString("ba_mst_tip"), UserApp.getResString("ba_account_change_pwd_operation_success", "密码修改成功！"), true);
			}
			else
				showHint((String) ds.get("userupdate_res"));
		}
	}
}
