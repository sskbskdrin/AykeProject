package com.ayke.demo.common;

import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.ayke.demo.R;
import com.ayke.library.abstracts.IBaseAdapter;
import com.ayke.library.abstracts.IFragment;

import java.util.ArrayList;
import java.util.List;

public class SampleListFragment extends IFragment implements OnItemClickListener {

    public static final String LIST_DATA = "list_data";
    public static final String LIST_ADAPTER = "list_adapter";
    private ListView mListView;
    private OnItemClickListener mOnItemClickListener;

    @Override
    protected int getLayoutId() {
        return R.layout.simple_list_view;
    }

    @Override
    protected void initView() {
        mListView = (ListView) getRootView();
        mListView.setOnItemClickListener(this);
    }

    @Override
    protected void initData() {
        IBaseAdapter<Object> mListAdapter;
        if (mBundle != null) {
            List<Object> list = (List<Object>) mBundle.getSerializable(LIST_DATA);
            if (list != null) {
                mListAdapter = new SimpleAdapter<>(getActivity(), list);
                mListView.setAdapter(mListAdapter);
                return;
            }
        }
        List<Object> list = new ArrayList<>();
        for (int i = 0; i < 20; i++) {
            list.add("Simple item +" + i);
        }
        mListView.setAdapter(new SimpleAdapter<>(getActivity(), list));
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        mOnItemClickListener = listener;
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
        if (mOnItemClickListener != null) {
            mOnItemClickListener.onItemClick(adapterView, view, position, id);
        }
    }
}
