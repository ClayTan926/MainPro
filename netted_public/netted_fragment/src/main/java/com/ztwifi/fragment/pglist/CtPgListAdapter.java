package com.ztwifi.fragment.pglist;

import android.view.View;
import android.view.ViewGroup;
import android.widget.SectionIndexer;

import com.netted.ba.ctact.CtListViewAdapter;
import com.netted.ba.util.CtRuntimeException;
import com.netted.common.ui.indexlv.IndexableListViewHelper;
import com.netted.common.ui.indexlv.IndexableSectionHelper;
import com.netted.fragment.pglist.CtPgListFragment.OnCtPgListFragmentEvent;

import java.util.List;
import java.util.Map;

public class CtPgListAdapter extends CtListViewAdapter implements
		SectionIndexer {

	public OnCtPgListFragmentEvent theItemViewEvt = null;
	public IndexableSectionHelper idxh = null;
	private IndexableListViewHelper lvHelper;
	

	public void initIndexableItemList(IndexableListViewHelper lvHelper, List<Map<String, Object>> ds, String nameField, String groupHeaderView) {
		if (idxh == null)
			idxh = new IndexableSectionHelper(this.theAct);
		this.lvHelper=lvHelper;
		if (nameField != null && nameField.length() > 0)
			idxh.nameField = nameField;
		if (groupHeaderView != null && groupHeaderView.length() > 0)
			idxh.groupHeaderView = groupHeaderView;
		this.setItemList(ds, false);
	}
	
	@Override
	public void setItemList(List<Map<String, Object>> ds, boolean bAppend) {
		if(idxh!=null){
			if(bAppend)
				throw new CtRuntimeException("字母索引的列表不允许添加更多数据");
			idxh.setItemList(ds);
			if(lvHelper!=null)
				lvHelper.setIndexable(true);
		}
		super.setItemList(ds, bAppend);
		for(int i=0;i<this.itemList.size();i++){
			this.itemList.get(i).put("_CTPGLIST_ITEM_INDEX", i);
		}
	}

	@Override
	public int getPositionForSection(int section) {
		if (idxh == null)
			return 0;
		else
			return idxh.getPositionForSection(section);
	}

	@Override
	public int getSectionForPosition(int position) {
		if (idxh == null)
			return 0;
		else
			return idxh.getSectionForPosition(position);
	}

	@Override
	public Object[] getSections() {
		if (idxh == null)
			return null;
		else
			return idxh.getSections();
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View res = super.getView(position, convertView, parent);
		if (theItemViewEvt != null)
			res = theItemViewEvt.doPgListAdapterGetItemView(this, position, res);
		if (idxh != null)
			idxh.setItemViewHeader(res, position);
		return res;
	}
	
}
