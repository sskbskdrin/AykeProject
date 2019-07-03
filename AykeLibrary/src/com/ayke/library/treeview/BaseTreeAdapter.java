package com.ayke.library.treeview;

import android.content.Context;
import android.view.View;
import android.view.View.OnClickListener;

import com.ayke.library.abstracts.IBaseAdapter;

import java.util.List;

public abstract class BaseTreeAdapter<T extends BaseTreeNode> extends IBaseAdapter<T> implements OnClickListener {
    private static final int KEY = Integer.MAX_VALUE / 2;
    protected static final int TYPE_LIMB = 0;
    protected static final int TYPE_LEAF = 1;

    public BaseTreeAdapter(Context context, List<T> list) {
        super(context, list);
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
        List<T> list = node.getChildList();
        if (list != null) {
            for (int i = 0; i < list.size(); i++) {
                T item = list.get(i);
                mList.add(position + i + 1, item);
                count++;
                if (item.isExpand()) {
                    position += expand(item, position + i + 1);
                }
            }
        }
        return count;
    }

    @Override
    public int getItemViewType(int position) {
        return getItem(position).isLeaf() ? TYPE_LEAF : TYPE_LIMB;
    }

    @Override
    public int getViewTypeCount() {
        return 2;
    }

    protected abstract int getLayoutId(int type);

    @Override
    protected void convert(View view, int position, T t) {
        if (getItemViewType(position) == TYPE_LIMB) {
            convertLimbView(view, position, t);
        } else {
            convertLeafView(view, position, t);
        }
        view.setOnClickListener(this);
        view.setTag(KEY, position);
    }

    public abstract void convertLimbView(View view, int position, T node);

    public abstract void convertLeafView(View view, int position, T node);

    public abstract void onClickTreeItem(T node);

}
