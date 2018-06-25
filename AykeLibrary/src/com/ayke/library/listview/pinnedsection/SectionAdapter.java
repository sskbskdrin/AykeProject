package com.ayke.library.listview.pinnedsection;

import android.content.Context;
import android.view.View;
import android.widget.SectionIndexer;

import com.ayke.library.abstracts.IBaseAdapter;

import java.util.ArrayList;
import java.util.List;

public abstract class SectionAdapter<T extends SectionItem> extends IBaseAdapter<T> implements
        SectionIndexer {
    private static final int ITEM = 0;
    private static final int SECTION = 1;

    private final List<T> mSectionList = new ArrayList<>();

    protected Context mContext;

    protected SectionAdapter(Context context, List<T> list) {
        super(context, list);
    }

    protected SectionAdapter(Context context, List<T> list, int layoutId) {
        super(context, list, layoutId);
    }

    private void setList() {
        mSectionList.clear();
        for (T item : mList) {
            if (item.isSection()) {
                mSectionList.add(item);
            }
        }
    }

    @Override
    public final void notifyDataSetChanged() {
        setList();
        super.notifyDataSetChanged();
    }

    @Override
    protected final void convert(View view, int position, T t) {
        if (t.isSection()) {
            convertSectionView(view, position, t);
        } else {
            convertItemView(view, position, t);
        }
    }

    public abstract void convertItemView(View view, int position, T t);

    public abstract void convertSectionView(View view, int position, T t);

    @Override
    public int getViewTypeCount() {
        return 2;
    }

    @Override
    public int getItemViewType(int position) {
        return getItem(position).isSection() ? SECTION : ITEM;
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