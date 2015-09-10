package com.ayke.demo.listview;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.ayke.library.abstracts.IFragment;
import com.ayke.library.listview.refresh.RefreshableListView;
import com.ayke.library.listview.refresh.RefreshableListView.IXListViewListener;

import java.util.ArrayList;
import java.util.List;

public class RefreshListFragment extends IFragment implements
	IXListViewListener {

	private RefreshableListView mListView;
	private ListAdapter mListAdapter;
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
		list = new ArrayList<String>();
		for (int i = 0; i < 15; i++) {
			list.add("Sample List " + i);
		}
		mListAdapter = new ListAdapter(getActivity(), list);
		mListView.setAdapter(mListAdapter);
	}

	@Override
	public View setContentView() {
		mListView = new RefreshableListView(getActivity());
		mListView.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT,
			LayoutParams.MATCH_PARENT));
		mListView.setPullRefreshEnable(true);
		mListView.setPullLoadEnable(true);
		mListView.setXListViewListener(this);
		return mListView;
	}

	public class ListAdapter extends BaseAdapter {

		private List<String> mList;
		private Context mContext;

		public ListAdapter(Context context, List<String> list) {
			mContext = context;
			mList = list;
		}

		@Override
		public int getCount() {
			return mList.size();
		}

		@Override
		public String getItem(int position) {
			return mList.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		public View getView(int position, View convertView, ViewGroup parent) {
			if (convertView == null) {
				convertView = LayoutInflater.from(mContext).inflate(android.R
					.layout.simple_list_item_1, null);
			}
			((TextView) convertView).setText(getItem(position));
			return convertView;
		}
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
