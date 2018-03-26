package com.ztwifi.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;

public class CtFragmentActivity extends FragmentActivity {

	public CtFragment findCtFragmentById(int layid) {
		Fragment fx = this.getSupportFragmentManager().findFragmentById(layid);
		if (fx instanceof CtFragment)
			return (CtFragment) fx;
		else
			return null;
	}

	public CtFragment findCtFragmentByTag(String tag) {
		Fragment fx = this.getSupportFragmentManager().findFragmentByTag(tag);
		if (fx instanceof CtFragment)
			return (CtFragment) fx;
		else
			return null;
	}

	public CtListFragment findCtListFragmentById(int layid) {
		Fragment fx = this.getSupportFragmentManager().findFragmentById(layid);
		if (fx instanceof CtListFragment)
			return (CtListFragment) fx;
		else
			return null;
	}

	public CtListFragment findCtListFragmentByTag(String tag) {
		Fragment fx = this.getSupportFragmentManager().findFragmentByTag(tag);
		if (fx instanceof CtListFragment)
			return (CtListFragment) fx;
		else
			return null;
	}

	public CtFragment getCtFragment(Bundle bd, String vn) {
		Fragment fx = this.getSupportFragmentManager().getFragment(bd, vn);
		if (fx instanceof CtFragment)
			return (CtFragment) fx;
		else
			return null;
	}
	
	public void addCtFragment(int viewId, CtFragment frg, String tag){
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.add(viewId, frg, tag);
        transaction.commitAllowingStateLoss();
	}
	
	public void replaceCtFragment(int viewId, CtFragment frg, String tag){
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(viewId, frg, tag);
        transaction.commitAllowingStateLoss();
	}

	public void removeCtFragment(CtFragment frg){
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.remove(frg);
        transaction.commitAllowingStateLoss();
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		removeFragmentsSaveInstanceState(outState);
	}

	protected void removeFragmentsSaveInstanceState(Bundle outState) {
		if (outState != null) {
			// 去掉保存的Fragment状态
			outState.remove("android:support:fragments");
			outState.remove("android:fragments");
		}
	}
}
