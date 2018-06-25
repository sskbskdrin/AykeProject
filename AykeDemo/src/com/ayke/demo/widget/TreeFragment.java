package com.ayke.demo.widget;

import android.content.Context;
import android.view.View;
import android.widget.CheckedTextView;
import android.widget.ListView;

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
        List<Node> list = new ArrayList<>();
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
        protected int getLayoutId(int type) {
            return type == TYPE_LIMB ? android.R.layout.simple_list_item_1 : android.R.layout
                    .simple_list_item_multiple_choice;
        }

        @Override
        public void convertLimbView(View view, int position, Node node) {
            String aa = "+";
            if (node.isExpand()) aa = "-";
            for (int i = 0; i < node.getLevel(); i++)
                aa = " " + aa;
            setText(getView(android.R.id.text1), aa + node.getOrgName());
        }

        @Override
        public void convertLeafView(View view, int position, Node node) {
            String aa = " ";
            for (int i = 0; i < node.getLevel(); i++)
                aa += " ";
            CheckedTextView textView = getView(android.R.id.text1);
            setText(textView, aa + node.getOrgName());
            textView.setChecked(node.isCheck);
        }

        @Override
        public void onClickTreeItem(Node node) {
            ToastUtil.show(getActivity(), node.getOrgName() + " level=" + node.getLevel());
            node.isCheck = !node.isCheck;
        }
    }

}
