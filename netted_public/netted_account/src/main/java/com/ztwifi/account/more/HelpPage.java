package com.ztwifi.account.more;

import android.os.Handler;
import android.os.Message;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.PopupWindow;
import android.widget.RelativeLayout.LayoutParams;

import com.netted.ba.ct.UserApp;

public class HelpPage {

	private PopupWindow popupWindow;
	View view;
	private Handler myHandler;
	int delaytime = 5;
	public HelpPage(View downView, View selfview, String setting,int mdelaytime) {

		String frist = ""; // 引导页
		this.delaytime =mdelaytime;
		frist = (String) UserApp.curApp().getCommonCache(setting);
		if (frist == null || frist.length() == 0) {
			UserApp.curApp().putCommonCache(setting, "1");
			View popuWindowView = selfview;
			popupWindow = new PopupWindow(popuWindowView,
					LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT);
			view = downView;
			popupWindow.showAtLocation(view, Gravity.LEFT | Gravity.TOP, 0, 0);
			popuWindowView.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					if(popupWindow!=null && popupWindow.isShowing())
						UserApp.hidePopupWindow(popupWindow);
				}
			});
			new Thread(new Runnable() {
				public void run() {
					int i = 0;
					while (true) {
						i = i + 1;
						if (i == delaytime) {
							myHandler.sendEmptyMessage(0);
							break;
						} else {
							try {
								Thread.sleep(1000);
							} catch (InterruptedException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}
					}
				}
			}).start();

			myHandler = new Handler() {
				@Override
				public void handleMessage(Message msg) {
					// TODO Auto-generated method stub
					if(popupWindow!=null && popupWindow.isShowing())
						UserApp.hidePopupWindow(popupWindow);
				}

			};
		}
	}

}
