package com.ayke.library.listview.pinnedsection;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.SectionIndexer;

public abstract class SectionAdapter extends BaseAdapter implements
		SectionIndexer {
	private static final int ITEM = 0;
	private static final int SECTION = 1;

	private List<SectionItem> mList = new ArrayList<SectionItem>();
	private List<SectionItem> mSectionList = new ArrayList<SectionItem>();

	protected Context mContext;

	public SectionAdapter(Context context, List<SectionItem> list) {
		mContext = context;
		setList(list);
	}

	private void setList(List<SectionItem> list) {
		if (list == null)
			throw new NullPointerException("list is null");
		if (list.size() == 0)
			list = new ArrayList<SectionItem>();
		else if (!(list.get(0) instanceof SectionItem))
			throw new IllegalArgumentException(
					"Does your adapter implements SectionItem?");
		mList = list;
		mSectionList.clear();
		for (SectionItem item : mList) {
			if (item.isSection())
				mSectionList.add(item);
		}
	}

	public void updataList(List<SectionItem> list) {
		setList(list);
		this.notifyDataSetChanged();
	}

	@Override
	public int getCount() {
		return mList.size();
	}

	@Override
	public Object getItem(int position) {
		return mList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		Object item = getItem(position);
		if (((SectionItem) item).isSection()) {
			return getSectionView(position, convertView, item);
		}
		return getItemView(position, convertView, item);
	}

	public abstract View getItemView(int position, View convertView, Object obj);

	public abstract View getSectionView(int position, View convertView,
			Object obj);

	@Override
	public int getViewTypeCount() {
		return 2;
	}

	@Override
	public int getItemViewType(int position) {
		return ((SectionItem) getItem(position)).isSection() ? SECTION : ITEM;
	}

	public boolean isItemViewTypePinned(int viewType) {
		return viewType == SECTION;
	}

	@Override
	public int getPositionForSection(int section) {
		if (section >= mSectionList.size()) {
			section = mSectionList.size() - 1;
		}
		return mSectionList.get(section).getlistPosition();
	}

	@Override
	public int getSectionForPosition(int position) {
		if (position >= getCount()) {
			position = getCount() - 1;
		}
		return mList.get(position).getSectionPosition();
	}

	@Override
	public Object[] getSections() {
		return mSectionList.toArray();
	}

}