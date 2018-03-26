package com.ztwifi.fragment.pglist;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.netted.ba.ct.TypeUtil;
import com.netted.ba.ct.UserApp;
import com.netted.ba.ctact.AppUrlManager;
import com.netted.ba.ctact.CtActEnvHelper;
import com.netted.ba.ctact.CtActEnvHelper.CtEnvViewEvents;
import com.netted.ba.ctact.CtActEnvHelper.OnCtViewUrlExecEvent;
import com.netted.ba.ctact.CtDataLoader;
import com.netted.ba.ctact.CtDataLoader.OnCtDataEvent;
import com.netted.common.ui.XListView;
import com.netted.common.ui.XListView.IXListViewListener;
import com.netted.common.ui.indexlv.IndexableXListView;
import com.netted.fragment.CtListFragment;

import java.util.List;
import java.util.Map;

public class CtPgListFragment extends CtListFragment {

	public interface OnCtPgListFragmentEvent {
		public String doObtainPgListDataUrl(CtPgDataListLoader ctPgDataListLoader,
				String url);
		public View doPgListAdapterGetItemView(CtPgListAdapter adt, int position, View convertView);
	}
	
	public CtPgDataListLoader theDataLoader; //分页数据加载
	public OnCtDataEvent theEvt; //数据加载事件
	public CtPgListAdapter theListAdapter; //列表适配


	public CtEnvViewEvents theCtUIEvt; //UI tag事件（列表项用） 
	public OnCtViewUrlExecEvent theUrlEvt; //url执行事件，供外部传入接管
	public OnCtPgListFragmentEvent thePgListFrgEvt=null; //数据url获取事件
	public String frgLayoutName="frg_wxlist_msg";
	public String cacheTableName=null;
	private int frgLayoutId;
	public XListView xListView;
	protected String dataUrlHeader;
	protected boolean ldInited=false;
	public String noDataHint="没有找到任何数据";
	public String onlyOnePageHint=" ";
	public String noMoreDataHint="已经是最后一页了";
	public String indexableNameField="";
	public String indexableGroupHeaderView="";
	public boolean alwaysShowProgress=false;//每次都显示加载进度条
	
	onFinishload dataloadfinish;
	
	public interface onFinishload {
		public void dataFinishLoad(List<Map<String, Object>> list);
	}
	
	public void setOnDataloadfinish(onFinishload dataloadfinish) {
		this.dataloadfinish = dataloadfinish;
	}
	

	protected OnCtViewUrlExecEvent urlEvt = new OnCtViewUrlExecEvent() {
		@Override
		public boolean doExecUrl(Activity act, View v, String url) {
			if (theUrlEvt != null)
				if(theUrlEvt.doExecUrl(act, v, url))
					return true;
			if(url.startsWith("cmd://refreshPgList/")){
				loadFirstPage(true);
				return true;
			}
			return false;
		}
	};

	public CtPgDataListLoader getTheWxListLoader() {
		return theDataLoader;
	}

	public CtPgListAdapter getTheListAdapter() {
		return theListAdapter;
	}
	
	protected void doCreateLoaderAdapter() {
		theDataLoader = new CtPgDataListLoader();
		theListAdapter = new CtPgListAdapter();
	}
	
	public void setDataUrlHeader(String urlHeader){
		this.dataUrlHeader=urlHeader;
		if(theDataLoader!=null)
			theDataLoader.urlHeader=dataUrlHeader;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		String atg = this.getTag();
		String tg=CtActEnvHelper.getTagStringValue(atg, "frg_layout");
		if (tg != null && tg.length() > 0)
			frgLayoutName=tg;
		if(frgLayoutName!=null && frgLayoutName.length()>0)
			frgLayoutId = AppUrlManager
					.getAndroidResourceIdOfURL("res://layout/" + frgLayoutName);
		
		View res = inflater.inflate(frgLayoutId, container, false);
		
		if (theEvt == null) {
			theEvt = new OnCtDataEvent() {

				@Override
				public void onDataLoaded(CtDataLoader wxh) {
					if (getActivity() == null || getActivity().isFinishing())
						return;
					doPgDataLoaded();
				}

				@Override
				public void onDataError(String err) {
					if (getActivity() == null || getActivity().isFinishing())
						return;
					UserApp.showToast(err);
					xListView.onRefreshComplete(false);
				}

				@Override
				public void onDataCanceled() {
					if (getActivity() == null || getActivity().isFinishing())
						return;
					UserApp.showToast("操作已取消");
					xListView.onRefreshComplete(false);
				}

				@Override
				public void afterFetchData() {

				}
			};
		}
		return res;
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		doCreateLoaderAdapter();
		theCtUIEvt = CtActEnvHelper.createCtTagUIEvt(this.getActivity(), null,
				urlEvt);
		theListAdapter.setTheAct(activity);
		theDataLoader.theCtx=activity;
		initParams();
	}

	public void initParams() {
		theListAdapter.theCtUIEvt = this.theCtUIEvt;

		String atg = this.getTag();
		
		String tg = CtActEnvHelper.getTagStringValue(atg, "item_layout");
		if (tg != null && tg.length() > 0)
			theListAdapter.setItemLayoutId(AppUrlManager
					.getAndroidResourceIdOfURL("res://layout/" + tg));
		
		tg=CtActEnvHelper.getTagStringValue(atg, "frg_layout");
		if (tg != null && tg.length() > 0)
			frgLayoutName=tg;
		
		tg=CtActEnvHelper.getTagStringValue(atg, "cacheTableName");
		if (tg != null && tg.length() > 0){
			cacheTableName=tg;
			theDataLoader.setCacheTableName(tg);
		}

		tg=CtActEnvHelper.getTagStringValue(atg, "urlHeader");
		if (tg != null && tg.length() > 0)
			theDataLoader.urlHeader=tg;
		if(dataUrlHeader!=null && dataUrlHeader.length()>0)
			theDataLoader.urlHeader=dataUrlHeader;

		tg=CtActEnvHelper.getTagStringValue(atg, "sortBy");
		if (tg != null && tg.length() > 0)
			theDataLoader.sortBy=tg;

		tg=CtActEnvHelper.getTagStringValue(atg, "listParentNode");
		if (tg != null && tg.length() > 0)
			theDataLoader.listParentNode=tg;

		tg=CtActEnvHelper.getTagStringValue(atg, "pageSize");
		if (tg != null && tg.length() > 0)
			theDataLoader.pageSize=TypeUtil.ObjectToIntDef(tg, theDataLoader.pageSize);

		tg=CtActEnvHelper.getTagStringValue(atg, "loadingMessage");
		if (tg != null && tg.length() > 0)
			theDataLoader.loadingMessage=tg;
		tg=CtActEnvHelper.getTagStringValue(atg, "alwaysShowProgress");
		if (tg != null && tg.length() > 0)
			alwaysShowProgress=TypeUtil.ObjectToBoolean(tg);

		tg=CtActEnvHelper.getTagStringValue(atg, "indexableNameField");
		if (tg != null && tg.length() > 0)
			indexableNameField=tg;
		tg=CtActEnvHelper.getTagStringValue(atg, "indexableGroupHeaderView");
		if (tg != null && tg.length() > 0)
			indexableGroupHeaderView=tg;
		
		tg=CtActEnvHelper.getTagStringValue(atg, "noDataHint");
		if (tg != null && tg.length() > 0)
			noDataHint=tg;

		tg=CtActEnvHelper.getTagStringValue(atg, "onlyOnePageHint");
		if (tg != null && tg.length() > 0)
			onlyOnePageHint=tg;

		tg=CtActEnvHelper.getTagStringValue(atg, "noMoreDataHint");
		if (tg != null && tg.length() > 0)
			noMoreDataHint=tg;
		
		if(thePgListFrgEvt!=null){
			theDataLoader.theUrlObtainEvt = this.thePgListFrgEvt;
			theListAdapter.theItemViewEvt = this.thePgListFrgEvt;
		}
		if(cacheTableName!=null)
			theDataLoader.setCacheTableName(this.cacheTableName);
	}
	
	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		initListView();
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		// initListView();
	}

	protected void initListView() {
		this.setListAdapter(theListAdapter);
		xListView = (XListView) this.getListView();
		initXListViewListener();
	}

	protected void initXListViewListener() {
		IXListViewListener ls = new IXListViewListener() {
			@Override
			public void onRefresh() {
				loadFirstPage(true);
			}

			@Override
			public void onLoadMore() {
				loadNextPage();
			}
		};
		xListView.setXListViewListener(ls);
	}

	public void checkNoMore() {
		if (theDataLoader == null || theListAdapter == null || xListView == null)
			return;
		xListView.setNoMoreData(!theDataLoader.isHasMore());
		if (theDataLoader.pageNo == 1) {
			if (theDataLoader.getCurPageList().size() == 0)
				xListView.setNoMoreDataHint(noDataHint);
			else if (!theDataLoader.hasMore)
				xListView.setNoMoreDataHint(onlyOnePageHint);
			else
				xListView.setNoMoreDataHint(noMoreDataHint);
		}

		String atg = this.getTag();
		String tg = CtActEnvHelper.getTagStringValue(atg, "EnablePullRefresh");
		if (tg != null && "0".equals(tg))
			xListView.setPullRefreshEnable(false);
		else
			xListView.setPullRefreshEnable(theDataLoader.isMayRefresh());
		tg = CtActEnvHelper.getTagStringValue(atg, "EnablePullLoad");
		if (tg != null && "0".equals(tg))
			xListView.setPullLoadEnable(false);
	}

	protected void doPgDataLoaded() {
		if(theDataLoader.pageNo == 1 && theDataLoader.isRefreshMode())
			UserApp.curApp().disableCache(2000);
		
		if (dataloadfinish != null){
			dataloadfinish.dataFinishLoad(theDataLoader.getCurPageList());
		}
		
		if (theDataLoader.pageNo == 1) {
			if (xListView instanceof IndexableXListView) {
				theListAdapter.initIndexableItemList(((IndexableXListView) xListView).getIdxHelper(),
						theDataLoader.getCurPageList(), indexableNameField, indexableGroupHeaderView);
			} else
				theListAdapter.setItemList(theDataLoader.getCurPageList(), false);
		} else
			theListAdapter.setItemList(theDataLoader.getCurPageList(), true);

		checkNoMore();

		xListView.onRefreshComplete(true);
	}

	public void initPgList(String parUrl) {
		ldInited=true;
		if(thePgListFrgEvt!=null){
			theDataLoader.theUrlObtainEvt = this.thePgListFrgEvt;
			theListAdapter.theItemViewEvt = this.thePgListFrgEvt;
		}
		theDataLoader.initLoaderParamUrl(getActivity(), parUrl);
	}

	public void loadFirstPage(boolean bRefresh) {
		if(!ldInited)
			initPgList(null);
		xListView.setNoMoreData(true);
		xListView.setNoMoreDataHint(" ");
		theDataLoader.setCtDataEvt(theEvt);
		theDataLoader.showProgress=alwaysShowProgress || bRefresh;
		theDataLoader.loadFirstPage(bRefresh);
		
	}
	
	public void loadFirstPage(boolean bRefresh,boolean ref) {
		if(!ldInited)
			initPgList(null);
		xListView.setNoMoreData(true);
		xListView.setNoMoreDataHint(" ");
		theDataLoader.setCtDataEvt(theEvt);
		theDataLoader.showProgress=alwaysShowProgress || (bRefresh && ref);
		theDataLoader.loadFirstPage(bRefresh);
		
	}

	public void loadNextPage() {
		theDataLoader.showProgress=alwaysShowProgress;
		theDataLoader.loadNextPage();
	}
	
}
