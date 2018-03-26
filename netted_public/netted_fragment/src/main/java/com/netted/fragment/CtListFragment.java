package com.netted.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.ListFragment;

public class CtListFragment extends ListFragment {

	public CtFragment findCtSubFragmentById(int layid){
		Fragment fx = this.getFragmentManager().findFragmentById(layid);
		if(fx instanceof CtFragment)
			return (CtFragment) fx;
		else
			return null;
	}

	public CtFragment findCtSubFragmentByTag(String tag){
		Fragment fx = this.getFragmentManager().findFragmentByTag(tag);
		if(fx instanceof CtFragment)
			return (CtFragment) fx;
		else
			return null;
	}

	public CtListFragment findCtListFragmentById(int layid){
		Fragment fx = this.getFragmentManager().findFragmentById(layid);
		if(fx instanceof CtListFragment)
			return (CtListFragment) fx;
		else
			return null;
	}

	public CtListFragment findCtListFragmentByTag(String tag){
		Fragment fx = this.getFragmentManager().findFragmentByTag(tag);
		if(fx instanceof CtListFragment)
			return (CtListFragment) fx;
		else
			return null;
	}
	public CtFragment getCtSubFragment(Bundle bd, String vn){
		Fragment fx = this.getFragmentManager().getFragment(bd, vn);
		if(fx instanceof CtFragment)
			return (CtFragment) fx;
		else
			return null;
	}
}
