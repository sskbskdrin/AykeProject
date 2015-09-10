package com.ayke.library.listview.expandable;

import android.content.Context;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.widget.ListAdapter;
import android.widget.ListView;

/**
 * Simple subclass of listview which does nothing more than wrap any ListAdapter
 * in a SlideExpandalbeListAdapter
 */
public class ExpandListView extends ListView {
	private ExpandBaseAdapter mAdapter;

	public ExpandListView(Context context) {
		super(context);
	}

	public ExpandListView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public ExpandListView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	/**
	 * Collapses the currently open view.
	 * 
	 * @return true if a view was collapsed, false if there was no open view.
	 */
	public boolean collapse() {
		if (mAdapter != null) {
			return mAdapter.collapseLastOpen();
		}
		return false;
	}

	@Override
	public void setAdapter(ListAdapter adapter) {
		if (!(adapter instanceof ExpandBaseAdapter))
			throw new IllegalArgumentException(
					"Does your adapter extends ExpandBaseAdapter?");
		this.mAdapter = (ExpandBaseAdapter) adapter;
		super.setAdapter(adapter);
	}

	@Override
	public Parcelable onSaveInstanceState() {
		return mAdapter.onSaveInstanceState(super.onSaveInstanceState());
	}

	@Override
	public void onRestoreInstanceState(Parcelable state) {
		if (!(state instanceof ExpandBaseAdapter.SavedState)) {
			super.onRestoreInstanceState(state);
			return;
		}
		ExpandBaseAdapter.SavedState ss = (ExpandBaseAdapter.SavedState) state;
		super.onRestoreInstanceState(ss.getSuperState());
		mAdapter.onRestoreInstanceState(ss);
	}
}