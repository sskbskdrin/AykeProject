/**
 * @file XListViewHeader.java
 * @create Apr 18, 2012 5:22:27 PM
 * @author Maxwin
 * @description XListView's header
 */
package com.ayke.library.listview.refresh;

import android.content.Context;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.ayke.library.R;
import com.ayke.library.util.SysUtils;

public class RefreshableListViewHeader extends LinearLayout {
//	private static final String TAG = "RefreshableListViewHeader";

	private final int ROTATE_ANIM_DURATION = 180;

	public final static int STATE_NORMAL = 0;
	public final static int STATE_READY = 1;
	public final static int STATE_REFRESHING = 2;

	private ImageView mRefreshArrowView;
	private ProgressBar mProgressBar;
	private TextView mHintTextView;
	private TextView mRefreshTimeView;
	private LinearLayout mContainer;

	private Animation mRotateUpAnim;
	private Animation mRotateDownAnim;

	private int mState = STATE_NORMAL;

	public RefreshableListViewHeader(Context context) {
		super(context);
		View.inflate(context, R.layout.refreshable_listview_header, this);
		initView();
	}

	private void initView() {
		// 初始情况，设置下拉刷新view高度为0
		mContainer = (LinearLayout) findViewById(R.id.refreshable_list_header_layout);
		mContainer.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT,
				0));
		mContainer.findViewById(R.id.refreshable_list_header_content_layout)
				.getLayoutParams().height = SysUtils.dip2px(getContext(), 50);
		mContainer.findViewById(R.id.refreshable_list_header_content_layout)
				.requestLayout();
		setGravity(Gravity.BOTTOM);
		requestLayout();

		mRefreshArrowView = (ImageView) findViewById(R.id.refreshable_list_header_arrow);
		mRefreshArrowView.getLayoutParams().height = SysUtils.dip2px(getContext(), 46);
		mRefreshArrowView.getLayoutParams().width = SysUtils.dip2px(getContext
			(), 32);
		mHintTextView = (TextView) findViewById(R.id.refreshable_list_header_hint_text);
		mHintTextView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 16);
		mRefreshTimeView = (TextView) findViewById(R.id.refreshable_list_header_time);
		mRefreshTimeView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 14);
		mProgressBar = (ProgressBar) findViewById(R.id.refreshable_list_header_progressbar);
		mProgressBar.getLayoutParams().height = mProgressBar.getLayoutParams().width = SysUtils
				.dip2px(getContext(), 50);

		mRotateUpAnim = new RotateAnimation(0.0f, -180.0f,
				Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF,
				0.5f);
		mRotateUpAnim.setDuration(ROTATE_ANIM_DURATION);
		mRotateUpAnim.setFillAfter(true);
		mRotateDownAnim = new RotateAnimation(-180.0f, 0.0f,
				Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF,
				0.5f);
		mRotateDownAnim.setDuration(ROTATE_ANIM_DURATION);
		mRotateDownAnim.setFillAfter(true);
	}

	public void setState(int state) {
		if (state == mState)
			return;
		if (state == STATE_REFRESHING) { // 显示进度
			mRefreshArrowView.clearAnimation();
			mRefreshArrowView.setVisibility(View.INVISIBLE);
			mProgressBar.setVisibility(View.VISIBLE);
		} else { // 显示箭头图片
			mRefreshArrowView.setVisibility(View.VISIBLE);
			mProgressBar.setVisibility(View.INVISIBLE);
		}
		switch (state) {
		case STATE_NORMAL:
			if (mState == STATE_READY) {
				mRefreshArrowView.startAnimation(mRotateDownAnim);
			}
			if (mState == STATE_REFRESHING) {
				mRefreshArrowView.clearAnimation();
			}
			mHintTextView.setText(R.string.refreshable_list_header_hint_normal);
			break;
		case STATE_READY:
			if (mState != STATE_READY) {
				mRefreshArrowView.clearAnimation();
				mRefreshArrowView.startAnimation(mRotateUpAnim);
				mHintTextView
						.setText(R.string.refreshable_list_header_hint_ready);
			}
			break;
		case STATE_REFRESHING:
			mHintTextView
					.setText(R.string.refreshable_list_header_hint_loading);
			break;
		default:
		}
		mState = state;
	}

	public void setVisibleHeight(int height) {
		mContainer.getLayoutParams().height = height >= 0 ? height : 0;
		mContainer.requestLayout();
	}

	public int getVisibleHeight() {
		return mContainer.getHeight();
	}

	public void setContainerVisibility(int visible) {
		mContainer.setVisibility(visible);
	}

	public void setRefreshTime(String time) {
		mRefreshTimeView.setText(R.string.refreshable_list_header_last_time);
		mRefreshTimeView.append(time);
	}

}
