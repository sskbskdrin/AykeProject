package com.ayke.demo.transition3d;

import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;

import com.ayke.demo.R;
import com.ayke.library.abstracts.IFragment;

public class Transition3d extends IFragment implements OnClickListener {
	private ViewGroup mContainer;
	private View mImageView0;
	private View mImageView1;
	private Rotate3dAnimation mRotate;
	private DisplayNextView mListener;

	@Override
	protected int getLayoutId() {
		return R.layout.fragment_transition3d_layout;
	}

	@Override
	protected void initView() {

		mImageView0 = getView(R.id.picture0);
		mImageView1 = getView(R.id.picture1);
		mContainer = getView(R.id.container);

		mImageView0.setClickable(true);
		mImageView0.setFocusable(true);
		mImageView0.setOnClickListener(this);
		mImageView1.setClickable(true);
		mImageView1.setFocusable(true);
		mImageView1.setOnClickListener(this);

		mContainer.setPersistentDrawingCache(ViewGroup
			.PERSISTENT_ANIMATION_CACHE);
	}

	@Override
	protected void initData() {

	}

	/**
	 * 请求旋转
	 *
	 * @param direction 从上往下看旋转方向，为true则顺时针旋转
	 * @param time      完成旋转的时间，毫秒
	 */
	private void applyRotation(boolean direction, long time) {
		final float centerX = mContainer.getWidth() / 2.0f;
		final float centerY = mContainer.getHeight() / 2.0f;
		float end_degree;
		float start_degree;
		if (direction) {
			end_degree = 270;
			start_degree = 360;
		} else {
			end_degree = 90;
			start_degree = 0;
		}
		if (mRotate == null) {
			mRotate = new Rotate3dAnimation(start_degree, end_degree, centerX,
				centerY, 120.0f, true);
		}
		mRotate.setDegress(start_degree, end_degree);
		mRotate.setDuration(time / 2);
		mRotate.setFillAfter(true);
		mRotate.setInterpolator(new LinearInterpolator());
		if (mListener == null) mListener = new DisplayNextView(direction);
		mListener.mDirection = direction;
		mRotate.setAnimationListener(mListener);

		mContainer.startAnimation(mRotate);
	}

	@Override
	public void onClick(View v) {
		if (v.getId() == R.id.picture0) applyRotation(true, 2000);
		else applyRotation(false, 2000);
	}

	private final class DisplayNextView implements Animation
		.AnimationListener {
		public boolean mDirection;

		private DisplayNextView(boolean direction) {
			mDirection = direction;
		}

		public void onAnimationStart(Animation animation) {
			mImageView0.setClickable(false);
			mImageView1.setClickable(false);
		}

		public void onAnimationEnd(Animation animation) {
			mContainer.post(new SwapViews(mDirection));
			mImageView0.setClickable(true);
			mImageView1.setClickable(true);
		}

		public void onAnimationRepeat(Animation animation) {

		}
	}

	private final class SwapViews implements Runnable {
		private final boolean mDirection;

		public SwapViews(boolean direction) {
			mDirection = direction;
		}

		public void run() {
			if (mImageView0.getVisibility() == View.GONE) {
				mImageView1.setVisibility(View.GONE);
				mImageView0.setVisibility(View.VISIBLE);
				mImageView0.requestFocus();
			} else {
				mImageView0.setVisibility(View.GONE);
				mImageView1.setVisibility(View.VISIBLE);
				mImageView1.requestFocus();
			}
			if (mDirection) {
				mRotate.setDegress(90, 0);
			} else {
				mRotate.setDegress(270, 360);
			}
			mRotate.setAnimationListener(null);
			mContainer.startAnimation(mRotate);
		}
	}

}
