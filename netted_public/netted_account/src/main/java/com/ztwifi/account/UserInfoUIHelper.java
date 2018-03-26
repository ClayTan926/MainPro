package com.ztwifi.account;

import java.util.Map;

import com.netted.account.R;
import com.netted.ba.ct.UserApp;
import com.netted.ba.ctact.CtActEnvHelper;
import com.netted.ba.ctact.CtActEnvHelper.CtEnvViewEvents;
import com.netted.ba.ctact.CtDataLoader;
import com.netted.ba.ctact.AppUrlManager;
import com.netted.ba.ctact.CvDataLoader;
import com.netted.ba.ctact.CtDataLoader.OnCtDataEvent;
import com.netted.ba.login.CtAutoLoginHelper;
import android.app.Activity;
import android.view.View;

public class UserInfoUIHelper {
	public static int userInfoCvId = 0;//710145;
	public static int userInfoEditCvId = 0;//710144;
	public Activity theAct;
	protected CtEnvViewEvents vtEvt;

	public UserInfoUIHelper(Activity act, CtEnvViewEvents evt) {
		theAct = act;
		vtEvt=evt;
		if(vtEvt==null)
			vtEvt=CtActEnvHelper.createCtTagUIEvt(theAct, null, null);
	}

	public void refreshUserInfo() {
		if (!UserApp.curApp().isLoggedIn()) {
			UserApp.callAppURL(theAct, "cmd://showview/?idvalue=not_login_panel:1&idvalue=user_info_panel:0&idvalue=btn_logout:0");
		}
		else {
			UserApp.callAppURL(theAct, "cmd://showview/?idvalue=not_login_panel:0&idvalue=user_info_panel:1&idvalue=btn_logout:1");
			loadData();
		}
	}

	protected void initControls() {
		if (theAct == null)
			return;
	}

	Runnable delayLoadRunner=new Runnable() {
		@Override
		public void run() {
			loadData();
		}
	};
	
	
	private void loadData() {
		if(userInfoCvId<=0){
			refreshUI(null);
			return;
		}
		View v=theAct.findViewById(R.id.not_login_panel);
		if(v==null)
		{
			refreshUI(null);
			return;
		}
		v.removeCallbacks(delayLoadRunner);
		if(CtAutoLoginHelper.isAutoLoginInProgress()){
			v.postDelayed(delayLoadRunner, 1000);
		}else
			loadDataEx();
	}
	
	private void loadDataEx() {
		CvDataLoader theHelper = new CvDataLoader();
		theHelper.init(theAct, userInfoCvId);
		theHelper.showProgress = true;

		theHelper.cacheExpireTm = UserApp.CAHCE_EXPIRE_TIME_HOUR;
		//theHelper.extraParams = "addparam=update:" + update;
		theHelper.loadingMessage = "加载需要时间，请耐心等待";
		theHelper.itemId = 0;

		if (theHelper.tryLoadFromCache()) {
			refreshUI(theHelper.getCurrentDataMap());
			return;
		}

		OnCtDataEvent evt = new OnCtDataEvent() {
			@Override
			public void onDataCanceled() {
				UserApp.showToast(theAct, "操作中止");
			}

			@Override
			public void onDataError(String msg) {
				if (AppUrlManager.showLoginError(theAct, msg)) {
					return;
				} else
					UserApp.showToast(theAct, msg);
			}

			@Override
			public void onDataLoaded(CtDataLoader cth) {
				refreshUI( cth.getCurrentDataMap());
			}

			@Override
			public void afterFetchData() {
			}
		};
		theHelper.setCtDataEvt(evt);
		theHelper.loadData(0);
	}

	protected void refreshUI(Map<String, Object> dmap) {
		View v=theAct.findViewById(R.id.user_info_panel);
		if(v!=null)
			CtActEnvHelper.checkViewTextTags(theAct, v,
					vtEvt, null, dmap);
	}

}
