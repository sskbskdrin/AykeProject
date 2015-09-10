package com.ayke.demo.listview;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.Transformation;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.ayke.library.abstracts.IFragment;

public class ClickDelItemFragment extends IFragment {

	static final int ANIMATION_DURATION = 200;
	private List<String> mAnimList = new ArrayList<String>();
	private ListView mListView;

	@Override
	protected int getLayoutId() {
		return 0;
	}

	@Override
	protected void initView() {

	}

	@Override
	protected void initData() {

	}

	@Override
	public View setContentView() {
		mListView = new ListView(getActivity());
		for (int i = 0; i < 50; i++) {
			String cell = "Cell No." + Integer.toString(i);
			mAnimList.add(cell);
		}

		mListView.setAdapter(new ListAdapter(getActivity(), mAnimList));
		mListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int
				position, long id) {
				deleteCell(view, position);
			}
		});
		return mListView;
	}

	private void deleteCell(final View v, final int index) {
		AnimationListener al = new AnimationListener() {
			@Override
			public void onAnimationEnd(Animation arg0) {
				if (mAnimList.size() > index) mAnimList.remove(index);
				v.setTag(true);
				((BaseAdapter) mListView.getAdapter()).notifyDataSetChanged();
			}

			@Override
			public void onAnimationRepeat(Animation animation) {
			}

			@Override
			public void onAnimationStart(Animation animation) {
			}
		};
		collapse(v, al);
	}

	private void collapse(final View v, AnimationListener al) {
		final int initialHeight = v.getMeasuredHeight();
		Animation anim = new Animation() {
			@Override
			protected void applyTransformation(float interpolatedTime,
			                                   Transformation t) {
				if (interpolatedTime == 1) {
					v.setVisibility(View.GONE);
				} else {
					v.getLayoutParams().height = initialHeight - (int)
						(initialHeight * interpolatedTime);
					v.requestLayout();
				}
			}
		};
		if (al != null) {
			anim.setAnimationListener(al);
		}
		anim.setDuration(ANIMATION_DURATION);
		v.startAnimation(anim);
	}

	private class ListAdapter extends BaseAdapter {

		private Context mContext;
		private List<String> mList;

		public ListAdapter(Context context, List<String> list) {
			mContext = context;
			mList = list;
		}

		@Override
		public int getCount() {
			return mList.size();
		}

		@Override
		public Object getItem(int position) {
			return mList.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			if (convertView == null || ((Boolean) convertView.getTag())) {
				convertView = View.inflate(mContext, android.R.layout
					.simple_list_item_1, null);
			}
			convertView.setTag(false);
			((TextView) convertView).setText(mList.get(position));
			return convertView;
		}

	}
}
