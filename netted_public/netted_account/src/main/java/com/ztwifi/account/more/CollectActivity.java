package com.ztwifi.account.more;

import java.util.ArrayList;
import java.util.List;

import com.netted.account.R;
import com.netted.ba.ct.NetUtil;
import com.netted.ba.ct.UserApp;
import com.netted.common.helpers.BaseActivityHelper;
import com.netted.common.helpers.FavoriteHelper;
import com.netted.common.helpers.FavoriteHelper.Favorite;

import android.app.Activity;
import android.app.ProgressDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.DialogInterface.OnKeyListener;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ListView;

public class CollectActivity extends Activity {

	public List<Favorite> collects = new ArrayList<Favorite>();
	public CollectAdapter collectAdapter;
	public ListView collectListView;

	Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 1:
				checkLoadingFav();
				break;
			}
			super.handleMessage(msg);
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.act_collect_index);
		initControls();

		collectAdapter = new CollectAdapter(this, collects);
		collectListView.setAdapter(collectAdapter);
	}

	public ProgressDialog mProgressDlg = null;

	protected void checkLoadingFav() {
		if (FavoriteHelper.isSyncingFavData() && mProgressDlg == null) {
			mProgressDlg = UserApp.createProgressDialog(this);
			mProgressDlg.setTitle("提示");
			mProgressDlg
					.setProgressStyle(android.R.style.Widget_ProgressBar_Large);
			mProgressDlg.setMessage("正在加载，请稍候...");
			mProgressDlg.setCancelable(false);
			OnKeyListener ls = new OnKeyListener() {
				public boolean onKey(DialogInterface dialog, int keyCode,
						KeyEvent event) {
					if (keyCode == KeyEvent.KEYCODE_BACK) {
						UserApp.hideDialog(dialog);
						return true;
					}
					return false;
				}
			};
			mProgressDlg.setOnKeyListener(ls);
			UserApp.showDialog(mProgressDlg);
		}
		if (FavoriteHelper.isSyncingFavData()) {
			Message message = new Message();
			message.what = 1;
			handler.sendMessageDelayed(message, 1000);
		} else if (mProgressDlg != null && mProgressDlg.isShowing()) {
			UserApp.hideDialog(mProgressDlg);
			mProgressDlg = null;
			loadData();
			return;
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
		BaseActivityHelper.onResume(this);
		loadData();
		if (FavoriteHelper.isSyncingFavData()) {
			mProgressDlg = null;
			checkLoadingFav();
		}
	}

	public void loadData() {
		collects.clear();

		getBusFavData("BUSLINE");
		getBusFavData("BUSSTATION");
		getBusFavData("BUSROUTE");

		View v = this.findViewById(R.id.textViewNoFavTip);
		if (collects.size() == 0)
			v.setVisibility(View.VISIBLE);
		else
			v.setVisibility(View.GONE);

		collectAdapter.notifyDataSetChanged();
	}

	protected void getBusFavData(String tp) {
		List<Favorite> res = FavoriteHelper.loadFavData(this, tp, UserApp
				.curApp().getCurCityCode());

		collects.addAll(res);
	}

	private void initControls() {
		findViewById(R.id.left_layout).setOnClickListener(
				new OnClickListener() {

					@Override
					public void onClick(View v) {
						finish();

					}
				});

		collectListView = (ListView) findViewById(R.id.collect_lv);
		collectListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				Favorite fav = collects.get(arg2);

				String tp = fav.getType();
				String val = fav.getValue();
				String[] vals = val.split("\n");
				String url = "";
				if ("BUSROUTE".equals(tp)) {
					url = "act://com.netted.bus.buschange.BusChangeResultActivity/?QueryStart="
							+ NetUtil.urlEncode(vals[1])
							+ "&QueryEnd="
							+ NetUtil.urlEncode(vals[5])
							+ "&QueryStart_X="
							+ NetUtil.urlEncode(vals[2])
							+ "&QueryStart_Y="
							+ NetUtil.urlEncode(vals[3])
							+ "&QueryEnd_X="
							+ NetUtil.urlEncode(vals[6])
							+ "&QueryEnd_Y="
							+ NetUtil.urlEncode(vals[7])
							+ "&QueryCityName="
							+ NetUtil.urlEncode(vals[0])
							+ "&QueryType=BUSROUTE";
				} else if ("BUSLINE".equals(tp)) {
					url = "act://com.netted.bus.busline.BusLineResultActivity/?QueryBusName="
							+ NetUtil.urlEncode(vals[1])
							+ "&QueryCityName="
							+ NetUtil.urlEncode(vals[0]);
					if (vals.length > 4)
						url = url + "&OnclickIndex=" + vals[3]
								+ "&queryStationName" + vals[4];
				} else if ("BUSSTATION".equals(tp)) {
					url = "act://bussationresult/?QueryStationName="
							+ NetUtil.urlEncode(vals[1])
							+ "&QueryCityName="
							+ NetUtil.urlEncode(vals[0]);
				}
				if (url.length() == 0)
					UserApp.showToast("无法识别此收藏项");
				else
					UserApp.callAppURL(CollectActivity.this, arg1, url);
			}
		});

		// 添加长按点击
		collectListView
				.setOnItemLongClickListener(new OnItemLongClickListener() {
					@Override
					public boolean onItemLongClick(AdapterView<?> parent,
							View view, int position, long id) {
						final Favorite fav = collects.get(position);
						Builder builder = UserApp
								.createAlertDlgBuilder(CollectActivity.this);
						builder.setTitle("收藏记录删除");
						builder.setMessage("您确定要删除收藏记录 " + fav.getName()
								+ " 吗？");
						builder.setPositiveButton("删除",
								new DialogInterface.OnClickListener() {
									@Override
									public void onClick(DialogInterface dialog,
											int which) {
										fav.deleteFromCache();
										collects.remove(fav);
										collectAdapter.notifyDataSetChanged();
										UserApp.hideDialog(dialog);
									}
								});
						builder.setNegativeButton("取消",
								new DialogInterface.OnClickListener() {
									@Override
									public void onClick(DialogInterface dialog,
											int which) {
										UserApp.hideDialog(dialog);
									}
								});
						UserApp.showDialog(builder.create());
						return false;
					}
				});
	}
}
