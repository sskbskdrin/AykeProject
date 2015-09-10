package com.ayke.demo.common;

import java.util.ArrayList;
import java.util.List;

import com.ayke.demo.R;
import com.ayke.library.abstracts.IFragment;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.ViewGroup.LayoutParams;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

public class SampleListFragment extends IFragment {

	private ListView mListView1;
	private ListView mListView2;

	@Override
	protected int getLayoutId() {
		return 0;
	}

	@Override
	protected void initData() {

	}

	public View setContentView() {
		LinearLayout ll = new LinearLayout(getActivity());
		ll.setOrientation(LinearLayout.HORIZONTAL);
		LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams
			(LayoutParams.MATCH_PARENT, 400);
		ll.setLayoutParams(lp);
		lp = new LinearLayout.LayoutParams(0, LayoutParams.MATCH_PARENT);
		lp.weight = 1;
		mListView1 = new ListView(getActivity());
		mListView1.setLayoutParams(lp);

		lp = new LinearLayout.LayoutParams(0, 100);
		lp.weight = 1;
		mListView2 = new ListView(getActivity());
		mListView2.setLayoutParams(lp);

		ll.addView(mListView1);
		ll.addView(mListView2);
		return ll;
	}

	@Override
	protected void initView() {

		List<String> list = new ArrayList<String>();
		for (int i = 0; i < 40; i++) {
			list.add("List " + i);
		}
		mListView1.setAdapter(new ListAdapter(getActivity(), list));

		list = new ArrayList<String>();
		for (int i = 0; i < 40; i++) {
			list.add("Sample List " + i);
		}
		mListView2.setAdapter(new ListAdapter(getActivity(), list));

		mListView1.setFastScrollEnabled(false);
		mListView1.setDividerHeight(0);
		mListView1.setCacheColorHint(0x99999999);
		mListView1.setFadingEdgeLength(350);

		mListView1.setOnScrollListener(new OnScrollListener() {

			@Override
			public void onScrollStateChanged(AbsListView view, int
				scrollState) {
				if (scrollState == 0) {
					if (mListView2.getChildAt(0).getTop() != 0) {
						mListView2.setSelectionFromTop(mListView2
							.getFirstVisiblePosition(), 0);
					}
				}
			}

			@Override
			public void onScroll(AbsListView view, int firstVisibleItem, int
				visibleItemCount, int totalItemCount) {
				int he = 0;
				if (mListView1.getChildCount() > 0) {
					for (int i = 0; i < mListView1.getChildCount(); i++) {
						View view1 = mListView1.getChildAt(i);
						float scale = 1.0f - Math.abs(175 - view1.getTop()) /
							226.0f;
						if (scale < 0.2) continue;

//						view1.getLayoutParams().height = (int) (scale * 40);
						he += view1.getLayoutParams().height;
						view1.setScaleY(scale);
						view1.invalidate();
						Log.d("", "first top= " + view1.getTop() + " scale=" +
							(1.0f - Math.abs((350 - view1.getTop()) / 451f)));
					}
					aaa++;
					if (aaa == 2) {
						// mListView1.getLayoutParams().height = he;
					}
					mListView1.invalidate();
					Log.d("", "first position top=" + mListView1.getChildAt(1)
						.getTop());
					mListView2.setSelectionFromTop(firstVisibleItem,
						mListView1.getChildAt(0).getTop());
				}
			}
		});

		mListView2.setOnScrollListener(new OnScrollListener() {

			@Override
			public void onScrollStateChanged(AbsListView view, int
				scrollState) {

			}

			@Override
			public void onScroll(AbsListView view, int firstVisibleItem, int
				visibleItemCount, int totalItemCount) {

			}
		});
	}

	int aaa = 0;

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
				convertView = LayoutInflater.from(mContext).inflate(R.layout
					.simple_text_center_view, null);
			}
			// if (position == 1)
			// convertView.setScaleY(0.5f);
			((TextView) convertView).setText(getItem(position));
			final View view = convertView;
			// if (position == 1)
			view.getViewTreeObserver().addOnPreDrawListener(new
				                                                ViewTreeObserver.OnPreDrawListener() {
				@Override
				public boolean onPreDraw() {
					int height = view.getMeasuredHeight();
					view.getLayoutParams().height = 50;
					view.getViewTreeObserver().removeOnPreDrawListener(this);
					view.invalidate();
					return true;
				}
			});
			return convertView;
		}
	}
}
