package com.netted.fragment;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import android.app.ActivityGroup;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TabHost;
import android.widget.TabHost.OnTabChangeListener;
import android.widget.TabHost.TabSpec;

import com.netted.ba.ctact.AppUrlManager;
import com.netted.ba.ctact.CtActEnvHelper;

@SuppressWarnings("deprecation")
public class CtTabActivity extends ActivityGroup {
	// 浣跨敤鏂规硶锛氱户鎵匡紝缁檛abInfos鍜宼abHostViewName璧嬪�硷紝鐒跺悗璋冪敤initCtTabs

	public static class CtTabInfo {
		public String btnViewName;
		public Class<?> tabActClass;
		public Map<String, Object> params;
	}

	public List<CtTabInfo> tabInfos = new ArrayList<CtTabInfo>();
	public String tabHostViewName = "cttabhost";
	public String curTabName = null;

	public int currentTabIndex = 0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	public void addTabInfo(String vn, Class cls) {
		addTabInfoEx(vn, cls, null);
	}

	public void addTabInfoEx(String vn, Class cls, Map<String, Object> params) {
		CtTabInfo ti = new CtTabInfo();
		ti.btnViewName = vn;
		ti.tabActClass = cls;
		ti.params = params;
		tabInfos.add(ti);
	}

	protected void initCtTabs() {
		if (tabInfos.size() == 0)
			return;
		TabHost tabHost = (TabHost) findCtView(tabHostViewName);
		if (tabHost == null)
			return;
		tabHost.setup(getLocalActivityManager());
		tabHost.setOnTabChangedListener(new OnTabChangeListener() {
			@Override
			public void onTabChanged(String tabId) {
				onTabChangedEx(tabId);
			}
		});
		OnClickListener lst = new OnClickListener() {
			@Override
			public void onClick(View v) {
				String tn = CtActEnvHelper.getTagItemValue(v, "cttab_name");
				selectTabByName(tn);
			}

		};
		for (CtTabInfo ti : tabInfos) {
			Intent intt = new Intent(this, ti.tabActClass);
			if (ti.params != null)
				AppUrlManager.putParamMapToIntent(ti.params, intt);
			TabSpec ts = tabHost.newTabSpec(ti.btnViewName)
					.setIndicator(ti.btnViewName).setContent(intt);
			tabHost.addTab(ts);

			View btn = findCtView(ti.btnViewName);
			CtActEnvHelper.setTagItemValue(btn, "cttab_name", ti.btnViewName);
			btn.setOnClickListener(lst);

			if (curTabName == null)
				curTabName = ti.btnViewName;
		}
		selectTabByName(curTabName);
	}

	protected void onTabChangedEx(String tabId)
	{
		
	}
	
	protected View findCtView(String vn) {
		return CtActEnvHelper.findViewOfCtName(this, vn);
	}

	protected void selectTabByName(String tn) {
		curTabName = tn;
		setTvSelect(tn);
		TabHost tabHost = (TabHost) findCtView(tabHostViewName);
		if (tabHost != null)
			tabHost.setCurrentTabByTag(tn);
	}

	protected void setTvSelect(String vn) {
		View tv = findCtView(vn);
		tv.setSelected(true);
		for (CtTabInfo ti : tabInfos) {
			View btn = findCtView(ti.btnViewName);
			if (btn != tv)
				btn.setSelected(false);
		}
		tv.setSelected(true);
	}

	@Override
	protected void onDestroy() {
		onDetachedFromWindow();
		super.onDestroy();
	}

}
