package com.ayke.demo.widget;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckedTextView;
import android.widget.ListView;
import android.widget.TextView;

import com.ayke.demo.R;
import com.ayke.library.abstracts.IFragment;
import com.ayke.library.treeview.BaseTreeAdapter;
import com.ayke.library.util.ToastUtil;

import java.util.ArrayList;
import java.util.List;

public class TreeFragment extends IFragment {

	@Override
	protected int getLayoutId() {
		return R.layout.simple_list_view;
	}

	@Override
	protected void initView() {
		ListView listView = (ListView) getRootView();
		List<Node> list = new ArrayList<Node>();
		for (int i = 0; i < 10; i++) {
			Node note0 = new Node("node " + i, 0);
			for (int j = 0; j < 50; j++) {
				Node note1 = new Node("node " + i + " " + j, 1);
				for (int k = 0; k < 50; k++) {
					Node note2 = new Node("node " + i + " " + j + " " + k, 2);
					note1.getChildList().add(note2);
				}
				note0.getChildList().add(note1);
			}
			list.add(note0);
		}
		listView.setAdapter(new Adapter(getActivity(), list));
	}

	@Override
	protected void initData() {

	}

	class Adapter extends BaseTreeAdapter<Node> {

		public Adapter(Context context, List<Node> list) {
			super(context, list);
		}

		@Override
		public View getLimbView(Node node, View convertView, LayoutInflater
			inflate) {
			if (convertView == null) {
				convertView = inflate.inflate(android.R.layout
					.simple_list_item_1, null);
			}
			String aa = "+";
			if (node.isExpand()) aa = "-";
			for (int i = 0; i < node.getLevel(); i++)
				aa = " " + aa;
			((TextView) convertView).setText(aa + node.getOrgName());
			return convertView;
		}

		@Override
		public View getLeafView(Node node, View convertView, LayoutInflater
			inflate) {
			if (convertView == null) {
				convertView = inflate.inflate(android.R.layout
					.simple_list_item_multiple_choice, null);
			}
			String aa = " ";
			for (int i = 0; i < node.getLevel(); i++)
				aa += " ";
			((CheckedTextView) convertView).setText(aa + node.getOrgName());
			((CheckedTextView) convertView).setChecked(node.isCheck);
			return convertView;
		}

		@Override
		public void onClickTreeItem(Node node) {
			ToastUtil.show(getActivity(), node.getOrgName() + " level=" + node
				.getLevel());
			node.isCheck = !node.isCheck;
		}

	}

}
