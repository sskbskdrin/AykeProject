package com.ayke.library.residemenu;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.graphics.Rect;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;

import com.nineoldandroids.animation.Animator;
import com.nineoldandroids.animation.AnimatorSet;
import com.nineoldandroids.animation.ObjectAnimator;
import com.nineoldandroids.view.ViewHelper;

public class ResideMenu extends FrameLayout {

	private static final int DIRECTION_LEFT = 0;
	private static final int DIRECTION_RIGHT = 1;
	private static final int PRESSED_MOVE_HORIZANTAL = 2;
	private static final int PRESSED_DOWN = 3;
	private static final int PRESSED_DONE = 4;
	private static final int PRESSED_MOVE_VERTICAL = 5;

	public interface OnMenuListener {

		/**
		 * the method will call on the finished time of opening menu's
		 * animation.
		 */
		public void openMenu();

		/**
		 * the method will call on the finished time of closing menu's animation
		 * .
		 */
		public void closeMenu();
	}

	/** the decorview of the activity */
	private ViewGroup viewDecor;
	/** the viewgroup of the activity */
	private TouchDisableView viewActivity;
	/** the flag of menu open status */
	private boolean isOpened;
//	private float shadowAdjustScaleX;
//	private float shadowAdjustScaleY;
	/** the view which don't want to intercept touch event */
	private List<View> ignoredViews;
	private OnMenuListener menuListener;

	private float lastRawX;
	private boolean isInIgnoredView = false;
	private int scaleDirection = DIRECTION_LEFT;
	private int pressedState = PRESSED_DOWN;

	private View mMenuView;
	private View mLeftMenuView;
	private View mRightMenuView;

	private boolean leftEnable = true;
	private boolean rightEnable = false;

	private int screenHeight;
	private int screenWidth;

	private float scaleLeftX = 0.8f;
	private float scaleLeftY = 0.1f;
	private float scaleLeftMax = 0.8f;
	private float scaleRightX = 0.8f;
	private float scaleRightY = 0.1f;
	private float scaleRightMax = 0.8f;
	private float scaleValue = 0.8f;

	public ResideMenu(Context context) {
		super(context);
		DisplayMetrics metrics = new DisplayMetrics();
		((Activity) getContext()).getWindowManager().getDefaultDisplay()
				.getMetrics(metrics);
		screenHeight = metrics.heightPixels;
		screenWidth = metrics.widthPixels;
	}

	@Override
	protected boolean fitSystemWindows(Rect insets) {
		this.setPadding(viewActivity.getPaddingLeft() + insets.left,
				viewActivity.getPaddingTop() + insets.top,
				viewActivity.getPaddingRight() + insets.right,
				viewActivity.getPaddingBottom() + insets.bottom);
		insets.left = insets.top = insets.right = insets.bottom = 0;
		return true;
	}

	/**
	 * use the method to set up the activity which residemenu need to show;
	 * 
	 * @param activity
	 */
	public void attachToActivity() {
		ignoredViews = new ArrayList<View>();
		viewDecor = (ViewGroup) ((Activity) getContext()).getWindow()
				.getDecorView();
		viewActivity = new TouchDisableView(getContext());

		View mContent = viewDecor.getChildAt(0);
		viewDecor.removeViewAt(0);
		viewActivity.setContent(mContent);
		addView(viewActivity);
		setShadowAdjustScaleXByOrientation();
		viewDecor.addView(this, 0);
	}

	private void setShadowAdjustScaleXByOrientation() {
//		int orientation = getResources().getConfiguration().orientation;
//		if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
//			shadowAdjustScaleX = 0.034f;
//			shadowAdjustScaleY = 0.12f;
//		} else if (orientation == Configuration.ORIENTATION_PORTRAIT) {
//			shadowAdjustScaleX = 0.06f;
//			shadowAdjustScaleY = 0.07f;
//		}
	}

	/**
	 * 显示区域大小与原始大小的比值，0.0到1.0之间
	 */
	public void setLeftScaleX(float x) {
		scaleLeftX = 1.0f - x;
	}

	/**
	 * 显示区域大小与原始大小的比值，0.0到1.0之间
	 */
	public void setLeftScaleY(float y) {
		scaleLeftY = (1 - y) / 2f;
		scaleLeftMax = y;
	}

	/**
	 * 显示区域大小与原始大小的比值，0.0到1.0之间
	 */
	public void setRightScaleX(float x) {
		scaleRightX = 1.0f - x;
	}

	/**
	 * 显示区域大小与原始大小的比值，0.0到1.0之间
	 */
	public void setRightScaleY(float y) {
		scaleRightY = (1 - y) / 2f;
		scaleRightMax = y;
	}

	public void setLeftMenuView(int res) {
		mLeftMenuView = View.inflate(getContext(), res, null);
	}

	public void setLeftMenuView(View menu) {
		mLeftMenuView = menu;
	}

	public void setRightMenuView(int res) {
		mRightMenuView = View.inflate(getContext(), res, null);
	}

	public void setRightMenuView(View menu) {
		mRightMenuView = menu;
	}

	/**
	 * set the menu background picture;
	 * 
	 * @param imageResrouce
	 */
	public void setBackground(int imageResrouce) {
		this.setBackgroundResource(imageResrouce);
	}

	/**
	 * the visiblity of shadow under the activity view;
	 * 
	 * @param isVisible
	 */
	// public void setShadowVisible(boolean isVisible) {
	// if (isVisible)
	// imageViewShadow.setBackgroundResource(R.drawable.shadow);
	// else
	// imageViewShadow.setBackgroundResource(0);
	// }

	/**
	 * if you need to do something on the action of closing or opening menu, set
	 * the listener here.
	 * 
	 * @return
	 */
	public void setMenuListener(OnMenuListener menuListener) {
		this.menuListener = menuListener;
	}

	public OnMenuListener getMenuListener() {
		return menuListener;
	}

	/**
	 * show the reside left menu;
	 */
	public void openLeftMenu() {
//		if (leftEnable)
			openMenu(DIRECTION_LEFT);
	}

	/**
	 * show the reside right menu;
	 */
	public void openRightMenu() {
//		if (rightEnable)
			openMenu(DIRECTION_RIGHT);
	}

	private void openMenu(int direction) {
		setScaleDirection(direction);
		isOpened = true;
		AnimatorSet scaleDown_activity = buildScaleDownAnimation(viewActivity,
				scaleValue, scaleValue);
		// AnimatorSet scaleDown_shadow =
		// buildScaleDownAnimation(imageViewShadow,
		// mScaleValue + shadowAdjustScaleX, mScaleValue
		// + shadowAdjustScaleY);
		AnimatorSet alpha_menu = buildMenuAnimation(mMenuView, 1.0f);
		scaleDown_activity.addListener(animationListener);
		// scaleDown_shadow.addListener(animationListener);
		// scaleDown_activity.playTogether(scaleDown_shadow);
		scaleDown_activity.playTogether(alpha_menu);
		scaleDown_activity.start();
	}

	/**
	 * close the menu;
	 */
	public void closeMenu() {
		isOpened = false;
		AnimatorSet scaleUp_activity = buildScaleUpAnimation(viewActivity,
				1.0f, 1.0f);
		// AnimatorSet scaleUp_shadow = buildScaleUpAnimation(imageViewShadow,
		// 1.0f, 1.0f);
		AnimatorSet alpha_menu = buildMenuAnimation(mMenuView, 0.0f);
		scaleUp_activity.addListener(animationListener);
		// scaleUp_activity.playTogether(scaleUp_shadow);
		scaleUp_activity.playTogether(alpha_menu);
		scaleUp_activity.start();
	}

	private boolean isAbleDirection(int direction) {
		return direction == DIRECTION_LEFT ? leftEnable : rightEnable;
	}

	public void setEnableLeftMenu(boolean yes) {
		leftEnable = yes;
	}

	public void setEnableRightMenu(boolean yes) {
		rightEnable = yes;
	}

	private void setScaleDirection(int direction) {
		float pivotX;
		float pivotY = screenHeight * 0.5f;
		if (direction == DIRECTION_LEFT) {
			scaleValue = scaleLeftMax;
			mMenuView = mLeftMenuView;
			pivotX = screenWidth * (scaleLeftX / (scaleLeftY * 2));
		} else {
			scaleValue = scaleRightMax;
			mMenuView = mRightMenuView;
			pivotX = screenWidth * (1.0f - (scaleRightX / (scaleRightY * 2)));
		}
		ViewHelper.setPivotX(viewActivity, pivotX);
		ViewHelper.setPivotY(viewActivity, pivotY);
		// ViewHelper.setPivotX(imageViewShadow, pivotX);
		// ViewHelper.setPivotY(imageViewShadow, pivotY);
		scaleDirection = direction;
	}

	/**
	 * return the flag of menu status;
	 * 
	 * @return
	 */
	public boolean isOpened() {
		return isOpened;
	}

	private OnClickListener viewActivityOnClickListener = new OnClickListener() {
		@Override
		public void onClick(View view) {
			if (isOpened())
				closeMenu();
		}
	};

	private Animator.AnimatorListener animationListener = new Animator.AnimatorListener() {
		@Override
		public void onAnimationStart(Animator animation) {
			if (isOpened()) {
				showMenuView();
				if (menuListener != null)
					menuListener.openMenu();
			}
		}

		@Override
		public void onAnimationEnd(Animator animation) {
			// reset the view;
			if (isOpened()) {
				viewActivity.setTouchDisable(true);
				viewActivity.setOnClickListener(viewActivityOnClickListener);
			} else {
				viewActivity.setTouchDisable(false);
				viewActivity.setOnClickListener(null);
				hideMenuView();
				if (menuListener != null)
					menuListener.closeMenu();
			}
		}

		@Override
		public void onAnimationCancel(Animator animation) {

		}

		@Override
		public void onAnimationRepeat(Animator animation) {

		}
	};

	/**
	 * a helper method to build scale down animation;
	 * 
	 * @param target
	 * @param targetScaleX
	 * @param targetScaleY
	 * @return
	 */
	private AnimatorSet buildScaleDownAnimation(View target,
			float targetScaleX, float targetScaleY) {

		AnimatorSet scaleDown = new AnimatorSet();
		scaleDown.playTogether(
				ObjectAnimator.ofFloat(target, "scaleX", targetScaleX),
				ObjectAnimator.ofFloat(target, "scaleY", targetScaleY));

		scaleDown.setInterpolator(AnimationUtils.loadInterpolator(getContext(),
				android.R.anim.decelerate_interpolator));
		scaleDown.setDuration(250);
		return scaleDown;
	}

	/**
	 * a helper method to build scale up animation;
	 * 
	 * @param target
	 * @param targetScaleX
	 * @param targetScaleY
	 * @return
	 */
	private AnimatorSet buildScaleUpAnimation(View target, float targetScaleX,
			float targetScaleY) {
		AnimatorSet scaleUp = new AnimatorSet();
		scaleUp.playTogether(
				ObjectAnimator.ofFloat(target, "scaleX", targetScaleX),
				ObjectAnimator.ofFloat(target, "scaleY", targetScaleY));
		scaleUp.setDuration(250);
		return scaleUp;
	}

	private AnimatorSet buildMenuAnimation(View target, float alpha) {
		AnimatorSet alphaAnimation = new AnimatorSet();
		alphaAnimation.playTogether(ObjectAnimator.ofFloat(target, "alpha",
				alpha));
		alphaAnimation.setDuration(250);
		return alphaAnimation;
	}

	/**
	 * if there ware some view you don't want reside menu to intercept their
	 * touch event,you can use the method to set.
	 * 
	 * @param v
	 */
	public void addIgnoredView(View v) {
		ignoredViews.add(v);
	}

	/**
	 * remove the view from ignored view list;
	 * 
	 * @param v
	 */
	public void removeIgnoredView(View v) {
		ignoredViews.remove(v);
	}

	/**
	 * clear the ignored view list;
	 */
	public void clearIgnoredViewList() {
		ignoredViews.clear();
	}

	/**
	 * if the motion evnent was relative to the view which in ignored view
	 * list,return true;
	 * 
	 * @param ev
	 * @return
	 */
	private boolean isInIgnoredView(MotionEvent ev) {
		Rect rect = new Rect();
		for (View v : ignoredViews) {
			v.getGlobalVisibleRect(rect);
			if (rect.contains((int) ev.getX(), (int) ev.getY()))
				return true;
		}
		return false;
	}

	private void setScaleDirectionByRawX(float currentRawX) {
		if (currentRawX < lastRawX)
			setScaleDirection(DIRECTION_RIGHT);
		else
			setScaleDirection(DIRECTION_LEFT);
	}

	private float getTargetScale(float currentRawX) {
		float max = DIRECTION_LEFT == scaleDirection ? scaleLeftMax
				: scaleRightMax;
		float scale = DIRECTION_LEFT == scaleDirection ? scaleLeftX
				: scaleRightX;
		float scaleFloatX = ((currentRawX - lastRawX) / (screenWidth * scale))
				* (1.0f - max);
		scaleFloatX = scaleDirection == DIRECTION_RIGHT ? -scaleFloatX
				: scaleFloatX;

		float targetScale = ViewHelper.getScaleX(viewActivity) - scaleFloatX;
		targetScale = targetScale > 1.0f ? 1.0f : targetScale;
		targetScale = targetScale < max ? max : targetScale;
		return targetScale;
	}

	private float lastActionDownX, lastActionDownY;

	@Override
	public boolean dispatchTouchEvent(MotionEvent ev) {
		float currentActivityScaleX = ViewHelper.getScaleX(viewActivity);
		if (currentActivityScaleX == 1.0f)
			setScaleDirectionByRawX(ev.getRawX());

		switch (ev.getAction()) {
		case MotionEvent.ACTION_DOWN:
			lastActionDownX = ev.getX();
			lastActionDownY = ev.getY();
			isInIgnoredView = isInIgnoredView(ev) && !isOpened();
			pressedState = PRESSED_DOWN;
			break;

		case MotionEvent.ACTION_MOVE:
			if (isInIgnoredView || !isAbleDirection(scaleDirection))
				break;

			if (pressedState != PRESSED_DOWN
					&& pressedState != PRESSED_MOVE_HORIZANTAL)
				break;
			int xOffset = (int) (ev.getX() - lastActionDownX);
			int yOffset = (int) (ev.getY() - lastActionDownY);

			if (pressedState == PRESSED_DOWN) {
				if (yOffset > 25 || yOffset < -25) {
					pressedState = PRESSED_MOVE_VERTICAL;
					break;
				}
				if (xOffset < -50 || xOffset > 50) {
					pressedState = PRESSED_MOVE_HORIZANTAL;
					ev.setAction(MotionEvent.ACTION_CANCEL);
				}
			} else if (pressedState == PRESSED_MOVE_HORIZANTAL) {
				if (currentActivityScaleX < 0.95)
					showMenuView();

				float targetScale = getTargetScale(ev.getRawX());
				ViewHelper.setScaleX(viewActivity, targetScale);
				ViewHelper.setScaleY(viewActivity, targetScale);
				// ViewHelper.setScaleX(imageViewShadow, targetScale
				// + shadowAdjustScaleX);
				// ViewHelper.setScaleY(imageViewShadow, targetScale
				// + shadowAdjustScaleY);
				ViewHelper.setAlpha(mMenuView, (1 - targetScale) * 2.0f);

				lastRawX = ev.getRawX();
				return true;
			}

			break;

		case MotionEvent.ACTION_UP:
			if (isInIgnoredView)
				break;
			if (pressedState != PRESSED_MOVE_HORIZANTAL)
				break;

			pressedState = PRESSED_DONE;
			if (isOpened()) {
				if (currentActivityScaleX > 0.56f)
					closeMenu();
				else
					openMenu(scaleDirection);
			} else {
				if (currentActivityScaleX < 0.94f) {
					openMenu(scaleDirection);
				} else {
					closeMenu();
				}
			}
			break;
		}
		lastRawX = ev.getRawX();
		return super.dispatchTouchEvent(ev);
	}

	private void showMenuView() {
		if (mMenuView != null && mMenuView.getParent() == null)
			addView(mMenuView, 0);
	}

	private void hideMenuView() {
		removeView(mMenuView);
	}
}
