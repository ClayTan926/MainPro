package com.netted.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;

public class CtFragment extends Fragment {

	public CtFragment findCtSubFragmentById(int layid) {
		Fragment fx = this.getFragmentManager().findFragmentById(layid);
		if (fx instanceof CtFragment)
			return (CtFragment) fx;
		else
			return null;
	}

	public CtFragment findCtSubFragmentByTag(String tag) {
		Fragment fx = this.getFragmentManager().findFragmentByTag(tag);
		if (fx instanceof CtFragment)
			return (CtFragment) fx;
		else
			return null;
	}

	public CtListFragment findCtListFragmentById(int layid) {
		Fragment fx = this.getFragmentManager().findFragmentById(layid);
		if (fx instanceof CtListFragment)
			return (CtListFragment) fx;
		else
			return null;
	}

	public CtListFragment findCtListFragmentByTag(String tag) {
		Fragment fx = this.getFragmentManager().findFragmentByTag(tag);
		if (fx instanceof CtListFragment)
			return (CtListFragment) fx;
		else
			return null;
	}

	public CtFragment getCtSubFragment(Bundle bd, String vn) {
		Fragment fx = this.getFragmentManager().getFragment(bd, vn);
		if (fx instanceof CtFragment)
			return (CtFragment) fx;
		else
			return null;
	}

	public void addCtSubFragment(int viewId, CtFragment frg, String tag) {
		FragmentTransaction transaction = this.getFragmentManager()
				.beginTransaction();
		transaction.add(viewId, frg, tag);
		transaction.commitAllowingStateLoss();
	}

	public void replaceCtSubFragment(int viewId, CtFragment frg, String tag) {
		FragmentTransaction transaction = this.getFragmentManager()
				.beginTransaction();
		transaction.replace(viewId, frg, tag);
		transaction.commitAllowingStateLoss();
	}

	public void removeCtSubFragment(CtFragment frg) {
		FragmentTransaction transaction = this.getFragmentManager()
				.beginTransaction();
		transaction.remove(frg);
		transaction.commitAllowingStateLoss();
	}

}
