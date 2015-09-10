package com.ayke.library.listview.refresh;

import android.content.Context;
import android.util.TypedValue;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ayke.library.R;
import com.ayke.library.util.SysUtils;

public class RefreshableListViewFooter extends LinearLayout {
	public final static int STATE_NORMAL = 0;
	public final static int STATE_READY = 1;
	public final static int STATE_LOADING = 2;

	private View mContentView;
	private View mProgressBar;
	private TextView mHintView;

	public RefreshableListViewFooter(Context context) {
		super(context);
		View.inflate(context, R.layout.refreshable_listview_footer, this);
		initView();
	}

	private void initView() {
		mContentView = findViewById(R.id.refreshable_list_footer_content);
		mProgressBar = findViewById(R.id.refreshable_list_footer_progressbar);
		mProgressBar.getLayoutParams().height = mProgressBar.getLayoutParams().width = SysUtils
				.dip2px(getContext(), 50);
		mHintView = (TextView) findViewById(R.id.refreshable_list_footer_hint_text);
		mHintView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 18);
		// Helper.adaptTextSize(mHintView, 30);
	}

	public void setState(int state) {
		mHintView.setVisibility(View.INVISIBLE);
		mProgressBar.setVisibility(View.INVISIBLE);
		mHintView.setVisibility(View.INVISIBLE);
		if (state == STATE_READY) {
			mHintView.setVisibility(View.VISIBLE);
			mHintView.setText(R.string.refreshable_list_footer_hint_ready);
		} else if (state == STATE_LOADING) {
			mProgressBar.setVisibility(View.VISIBLE);
		} else {
			mHintView.setVisibility(View.VISIBLE);
			mHintView.setText(R.string.refreshable_list_footer_hint_normal);
		}
	}

	public void setBottomMargin(int height) {
		if (height < 0)
			return;
		LayoutParams lp = (LayoutParams) mContentView.getLayoutParams();
		lp.bottomMargin = height;
		mContentView.setLayoutParams(lp);
	}

	public int getBottomMargin() {
		LayoutParams lp = (LayoutParams) mContentView.getLayoutParams();
		return lp.bottomMargin;
	}

	public void normal() {
		mHintView.setVisibility(View.VISIBLE);
		mProgressBar.setVisibility(View.GONE);
	}

	public void loading() {
		mHintView.setVisibility(View.GONE);
		mProgressBar.setVisibility(View.VISIBLE);
	}

	/**
	 * hide footer when disable pull load more
	 */
	public void hide() {
		LayoutParams lp = (LayoutParams) mContentView.getLayoutParams();
		lp.height = 0;
		mContentView.setLayoutParams(lp);
	}

	/**
	 * show footer
	 */
	public void show() {
		LayoutParams lp = (LayoutParams) mContentView.getLayoutParams();
		lp.height = LayoutParams.WRAP_CONTENT;
		mContentView.setLayoutParams(lp);
	}

}
