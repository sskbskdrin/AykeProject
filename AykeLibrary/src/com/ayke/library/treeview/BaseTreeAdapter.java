package com.ayke.library.treeview;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

public abstract class BaseTreeAdapter<T extends BaseTreeNode> extends
	BaseAdapter implements OnClickListener {
	private static final int KEY = Integer.MAX_VALUE / 2;

	private Context mContext;
	private List<T> mList;

	public BaseTreeAdapter(Context context, List<T> list) {
		mContext = context;
		mList = list;
		for (int i = mList.size() - 1; i >= 0; i--) {
			T item = mList.get(i);
			if (item.isExpand()) {
				expand(item, i);
			}
		}
	}

	@Override
	public void onClick(View v) {
		int position = (Integer) v.getTag(KEY);
		T node = getItem(position);
		onClickTreeItem(node);
		if (!node.isLeaf()) {
			if (node.isExpand()) {
				collapse(node, position);
			} else {
				expand(node, position);
			}
			node.setExpand(!node.isExpand());
		}
		notifyDataSetChanged();
	}

	private void collapse(T node, int position) {
		for (int i = 0; i < node.getChildList().size(); i++) {
			T item = mList.remove(position + 1);
			if (item.isExpand()) {
				collapse(item, position);
			}
		}
	}

	private int expand(T node, int position) {
		int count = 0;
		for (int i = 0; i < node.getChildList().size(); i++) {
			T item = (T) node.getChildList().get(i);
			mList.add(position + i + 1, item);
			count++;
			if (item.isExpand()) {
				position += expand(item, position + i + 1);
			}
		}
		return count;
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

	@Override
	public int getItemViewType(int position) {
		return getItem(position).isLeaf() ? 1 : 0;
	}

	@Override
	public int getViewTypeCount() {
		return 2;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		T item = getItem(position);
		LayoutInflater factory = LayoutInflater.from(mContext);
		if (getItemViewType(position) == 0) {
			convertView = getLimbView(item, convertView, factory);
		} else {
			convertView = getLeafView(item, convertView, factory);
		}
		convertView.setOnClickListener(this);
		convertView.setTag(KEY, position);
		return convertView;
	}

	public abstract View getLimbView(T node, View convertView, LayoutInflater
		inflate);

	public abstract View getLeafView(T node, View convertView, LayoutInflater
		inflate);

	public abstract void onClickTreeItem(T node);

}
