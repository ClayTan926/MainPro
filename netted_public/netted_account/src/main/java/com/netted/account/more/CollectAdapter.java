package com.netted.account.more;

import java.util.List;

import com.netted.account.R;
import com.netted.ba.ct.UserApp;
import com.netted.common.helpers.FavoriteHelper.Favorite;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class CollectAdapter extends BaseAdapter {

	Context context;
	List<Favorite> collects;
	LayoutInflater inflater;

	public CollectAdapter(Context context, List<Favorite> collects) {
		this.context = context;
		this.collects = collects;
		inflater = LayoutInflater.from(this.context);
	}

	@Override
	public int getCount() {
		return collects.size();
	}

	@Override
	public Object getItem(int position) {
		return position;
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
			convertView = inflater.inflate(R.layout.act_collect_index_item,
					null);
		}
		TextView typeTextView = (TextView) convertView
				.findViewById(R.id.type_tv);
		TextView firstTextView = (TextView) convertView
				.findViewById(R.id.first_tv);
		firstTextView.setText(collects.get(position).getName());
		String tp = collects.get(position).getType();
		String vals[] = collects.get(position).getValue().split("\n");
		if (UserApp.curApp().getCurCityName().equals("无锡") && vals.length > 4) {
			String dir = null;
			if (vals[2].equals("0"))
				dir = "上行";
			else if (vals[2].equals("1"))
				dir = "下行";
			String s = firstTextView.getText().toString() + " " + dir;
			if (vals[4] != null&& !vals[4].equals("null"))
				s = s + " " + vals[4];
			firstTextView.setText(s);
		}
		if ("BUSROUTE".equals(tp)) {
			typeTextView.setText("[换乘]");
		} else if ("BUSLINE".equals(tp)) {
			typeTextView.setText("[线路]");
		} else if ("BUSSTATION".equals(tp)) {
			typeTextView.setText("[站点]");
		} else
			typeTextView.setText("[未知]");

		return convertView;
	}

}
