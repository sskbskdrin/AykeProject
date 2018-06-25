package com.ayke.library.abstracts;

import android.content.Context;
import android.support.annotation.IdRes;
import android.support.annotation.LayoutRes;
import android.util.SparseArray;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by keayuan on 2015/6/2.
 */
public abstract class IBaseAdapter<T> extends BaseAdapter implements IText, IImage {
    private static final int TAG_VALUE = 0xff0000ff;
    protected Context mContext;
    protected List<T> mList;

    private int mLayoutId;

    private View mCurrentView;

    public IBaseAdapter(Context context, List<T> list) {
        mContext = context;
        mList = list;
        if (mList == null) {
            mList = new ArrayList<>();
        }
    }

    public IBaseAdapter(Context context, List<T> list, @LayoutRes int layoutId) {
        mContext = context;
        mLayoutId = layoutId;
        mList = list;
        if (mList == null) {
            mList = new ArrayList<>();
        }
    }

    public List<T> getList() {
        if (mList == null) {
            mList = new ArrayList<>();
        }
        return mList;
    }

    public void updateList(List<T> list) {
        mList = list;
        if (mList == null) {
            mList = new ArrayList<>();
        }
        notifyDataSetChanged();
    }

    @Override
    public final int getCount() {
        return mList.size();
    }

    @Override
    public final T getItem(int position) {
        return mList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @SuppressWarnings("unchecked")
    @Override
    public final View getView(int position, View convertView, ViewGroup parent) {
        int type = getItemViewType(position);
        if (convertView == null) {
            int layoutId = getLayoutId(type);
            if (layoutId <= 0) {
                convertView = generateView(position, parent, type);
            } else {
                convertView = View.inflate(mContext, getLayoutId(type), null);
            }
            convertView.setTag(TAG_VALUE, new SparseArray<>(4));
        }
        mCurrentView = convertView;
        convert(convertView, position, getItem(position));
        return convertView;
    }


    @SuppressWarnings("unchecked")
    @Override
    public final <V extends View> V getView(@IdRes int id) {
        SparseArray<View> array = (SparseArray<View>) mCurrentView.getTag(TAG_VALUE);
        if (array == null) {
            array = new SparseArray<>();
            mCurrentView.setTag(TAG_VALUE, array);
        }
        View view = array.get(id);
        if (view == null) {
            view = mCurrentView.findViewById(id);
            array.put(id, view);
        }
        return (V) view;
    }

    @LayoutRes
    protected int getLayoutId(int type) {
        return mLayoutId;
    }

    protected View generateView(int position, ViewGroup parent, int type) {
        return null;
    }

    protected abstract void convert(View view, int position, T t);
}
