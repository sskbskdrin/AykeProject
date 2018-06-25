package com.ayke.demo.listview;

import android.annotation.SuppressLint;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.BaseAdapter;

import com.ayke.demo.common.SimpleAdapter;
import com.ayke.library.abstracts.IFragment;
import com.ayke.library.listview.refresh.RefreshableListView;
import com.ayke.library.listview.refresh.RefreshableListView.IXListViewListener;

import java.util.ArrayList;
import java.util.List;

public class RefreshListFragment extends IFragment implements IXListViewListener {

    private RefreshableListView mListView;
    private BaseAdapter mListAdapter;
    private List<String> list;

    @Override
    protected int getLayoutId() {
        return 0;
    }

    @Override
    protected void initView() {

    }

    @Override
    protected void initData() {
        list = new ArrayList<>();
        for (int i = 0; i < 15; i++) {
            list.add("Sample List " + i);
        }
        mListAdapter = new SimpleAdapter<>(getActivity(), list);
        mListView.setAdapter(mListAdapter);
    }

    @Override
    public View getContentView() {
        mListView = new RefreshableListView(getActivity());
        mListView.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams
                .MATCH_PARENT));
        mListView.setPullRefreshEnable(true);
        mListView.setPullLoadEnable(true);
        mListView.setXListViewListener(this);
        return mListView;
    }

    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            mListView.stopRefresh();
            mListView.stopLoadMore();
            mListAdapter.notifyDataSetChanged();
        }
    };

    @Override
    public void onRefresh() {
        new Thread(new Runnable() {

            @Override
            public void run() {
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                list.add(0, "Sample list refresh");
                mHandler.sendEmptyMessage(0);
            }
        }).start();
    }

    @Override
    public void onLoadMore() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                list.add("Sample list load more");
                mHandler.sendEmptyMessage(0);
            }
        }).start();
    }
}
