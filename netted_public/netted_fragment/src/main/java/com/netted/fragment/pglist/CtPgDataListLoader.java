package com.netted.fragment.pglist;

import android.content.Context;

import com.netted.ba.ct.NetUtil;
import com.netted.ba.ct.TypeUtil;
import com.netted.ba.ct.UserApp;
import com.netted.ba.ctact.CtActEnvHelper;
import com.netted.ba.ctact.CtUrlDataLoader;
import com.netted.fragment.pglist.CtPgListFragment.OnCtPgListFragmentEvent;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class CtPgDataListLoader extends CtUrlDataLoader {

	public static final String CT_PG_COMMLIST_CACHE_TABLENAME = "ctsys_pg_commlist";

	public int pageSize = 0; // 使用默认值
	public int pageNo = 1;
	public String urlHeader = "/ctweixun.nx?action=xx&userId=${USERID}";
	public String sortBy = "";
	public String listParentNode = null;

	public OnCtPgListFragmentEvent theUrlObtainEvt = null;

	protected boolean hasMore = true;
	protected boolean mayRefresh = true;

	protected List<Map<String, Object>> wxDataList = new ArrayList<Map<String, Object>>(); // 缓存数据
	protected List<Map<String, Object>> curPageList = new ArrayList<Map<String, Object>>(); // 缓存数据

	public CtPgDataListLoader() {
		super();
		this.cacheTableName = CT_PG_COMMLIST_CACHE_TABLENAME;
		this.needVerifyCode = true;
	}

	public void setCacheTableName(String tbn) {
		cacheTableName = tbn;
	}

	@Override
	public void init(Context ctx, int tpId) {
		if (tpId <= 0)
			tpId = 1;
		super.init(ctx, tpId);
	}

	public void loadFirstPage(boolean bRefresh) {
		pageNo = 1;
		hasMore = true;
		loadDataEx(bRefresh);
	}

	public void loadNextPage() {
		if (!hasMore)
			return;
		pageNo++;
		loadDataEx(refreshMode);
	}

	public boolean isHasMore() {
		return hasMore;
	}

	public boolean isMayRefresh() {
		return mayRefresh;
	}

	public boolean isRefreshMode() {
		return refreshMode;
	}
	
	public List<Map<String, Object>> getWxDataList() {
		return wxDataList;
	}

	public List<Map<String, Object>> getCurPageList() {
		return curPageList;
	}

	public void setWxDataList(List<Map<String, Object>> wxDataList) {
		this.wxDataList = wxDataList;
	}

	public void setCurPageList(List<Map<String, Object>> wxDataList) {
		this.wxDataList = wxDataList;
	}
	@Override
	public void parseJsonData(JSONObject arg0)  throws JSONException {
		super.parseJsonData(arg0);
		afterParseJsonData();
	}
	
	protected void afterParseJsonData() {
		List<Map<String, Object>> ds = TypeUtil.convertMapWmListToList_SO(
				dataMap, listParentNode, "itemList", "colNameList");
		curPageList.clear();
		curPageList.addAll(ds);
		if (pageNo == 1)
			wxDataList.clear();
		wxDataList.addAll(ds);

		int sz = TypeUtil.ObjectToInt(dataMap.get("pageSize"));
		if(sz > 0)
			pageSize = sz;
//		if (sz <= 0)
//			sz = 25;
		if (pageSize == 0 || ds.size() < pageSize)
			hasMore = false;
	}

	public void genPgListDataUrl() {
		String url;
		url = UserApp.getBaServerUrl_ne() + urlHeader + "&pageNo=" + pageNo;
		if (pageSize > 1)
			url += "&pageSize=" + pageSize;
		if (sortBy != null && sortBy.length() > 0)
			url += "&sortBy=" + NetUtil.urlEncode(sortBy);
		url = CtActEnvHelper.checkSpecValueEx(theCtx, url, this, this.dataMap,
				true);
		if (theUrlObtainEvt != null)
			url = theUrlObtainEvt.doObtainPgListDataUrl(this, url);
		custDataUrl = url;
	}

	/**
	 * 获取接口地址
	 */
	@Override
	public String getDataUrl() {
		genPgListDataUrl();
		return super.getDataUrl();
	}
	
}
