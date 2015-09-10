package com.ayke.library.abstracts;

import android.content.Context;
import android.widget.BaseAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by keayuan on 2015/6/2.
 */
public abstract class IBaseAdapter<T> extends BaseAdapter {
	protected Context mContext;
	protected List<T> mList;

	protected IBaseAdapter(Context context, List<T> list) {
		mContext = context;
		mList = list;
		if (mList == null) {
			mList = new ArrayList();
		}
	}

	public List<T> getList() {
		return mList;
	}

	public void updateList(List<T> list) {
		mList = list;
		notifyDataSetChanged();
	}

	@Override
	public int getCount() {
		return mList.size();
	}

	@Override
	public T getItem(int position) {
		return mList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}
}
