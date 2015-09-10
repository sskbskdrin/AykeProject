package com.ayke.library.autoviewpage;

import java.lang.reflect.Field;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Interpolator;
import android.widget.Scroller;

public class AutoScrollViewPager extends ViewPager {
	private static final int SCROLL = 1001;

	private CycleAdapter mAdapter;
	private OnPageChangeListener viewOnPageChangeListener;

	private OnPageChangeListener mOnPageChangeListener = new OnPageChangeListener() {

		@Override
		public void onPageSelected(int position) {
			if (viewOnPageChangeListener != null) {
				viewOnPageChangeListener.onPageSelected(isCycle ? position
						% mAdapter.getCount() : position);
			}
		}

		@Override
		public void onPageScrolled(int position, float positionOffset,
				int positionOffsetPixels) {
			if (viewOnPageChangeListener != null) {
				viewOnPageChangeListener.onPageScrolled(isCycle ? position
						% mAdapter.getCount() : position,
						isCycle ? positionOffset % mAdapter.getCount()
								: positionOffset, positionOffsetPixels);
			}
			mHandler.removeMessages(SCROLL);
		}

		@Override
		public void onPageScrollStateChanged(int state) {
			if (viewOnPageChangeListener != null) {
				viewOnPageChangeListener.onPageScrollStateChanged(state);
			}
			if (state == 0)
				mHandler.sendEmptyMessageDelayed(SCROLL, scrollPeriod);
		}
	};

	public AutoScrollViewPager(Context context) {
		this(context, null);
	}

	public AutoScrollViewPager(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	private void init() {
		super.setOnPageChangeListener(mOnPageChangeListener);
	}

	@Override
	public void setOnPageChangeListener(OnPageChangeListener listener) {
		viewOnPageChangeListener = listener;
		super.setOnPageChangeListener(mOnPageChangeListener);
	}

	@Override
	public void setAdapter(PagerAdapter adapter) {
		mAdapter = new CycleAdapter(adapter);
		super.setAdapter(mAdapter);
		if (isCycle)
			setCurrentItem(1000000 * adapter.getCount());
		else
			setCurrentItem(0, true);
		mHandler.sendEmptyMessageDelayed(SCROLL, scrollPeriod);
	}

	/**
	 * 设置是否循环显示 必须在设置adapter之前设置
	 * 
	 * @param cycle
	 */
	public void setCycleScroll(boolean cycle) {
		isCycle = cycle;
	}

	private boolean isAutoScroll;

	public void setAutoScroll(boolean auto) {
		isAutoScroll = auto;
		if (isAutoScroll) {
			isCycle = true;
		}
	}

	private int scrollPeriod = 3000;

	private Handler mHandler = new Handler() {
		public void handleMessage(Message msg) {
			AutoScrollViewPager pager = AutoScrollViewPager.this;
			pager.setCurrentItem(pager.getCurrentItem() + 1, true);
		};
	};

	// 反射机制 控制 viewpager滑动时间 为800
	public void setScrollDuration(int duration) {
		try {
			Field mField = ViewPager.class.getDeclaredField("mScroller");
			mField.setAccessible(true);
			FixedSpeedScroller scroller;
			scroller = new FixedSpeedScroller(getContext(),
					new AccelerateDecelerateInterpolator());
			scroller.setDuration(duration);
			mField.set(this, scroller);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private class FixedSpeedScroller extends Scroller {
		private int mDuration = 1000;

		public FixedSpeedScroller(Context context) {
			super(context);
		}

		public FixedSpeedScroller(Context context, Interpolator interpolator) {
			super(context, interpolator);
		}

		@Override
		public void startScroll(int startX, int startY, int dx, int dy,
				int duration) {
			super.startScroll(startX, startY, dx, dy, mDuration);
		}

		@Override
		public void startScroll(int startX, int startY, int dx, int dy) {
			super.startScroll(startX, startY, dx, dy, mDuration);
		}

		public void setDuration(int time) {
			mDuration = time;
		}

	};

	private boolean isCycle;

	private class CycleAdapter extends PagerAdapter {
		private PagerAdapter mAdapter;

		public CycleAdapter(PagerAdapter adapter) {
			mAdapter = adapter;
		}

		@Override
		public int getCount() {
			return isCycle ? Integer.MAX_VALUE : mAdapter.getCount();
		}

		@Override
		public boolean isViewFromObject(View arg0, Object arg1) {
			return mAdapter.isViewFromObject(arg0, arg1);
		}

		@Override
		public void destroyItem(ViewGroup container, int position, Object object) {
			View view = (View) mAdapter.instantiateItem(container,
					isCycle ? position % mAdapter.getCount() : position);
			((ViewPager) container).removeView(view);
		}

		/**
		 * 载入图片进去，用当前的position 除以 图片数组长度取余数是关键
		 */
		@Override
		public Object instantiateItem(ViewGroup container, int position) {
			View view = (View) mAdapter.instantiateItem(container,
					isCycle ? position % mAdapter.getCount() : position);
			((ViewPager) container).addView(view);
			return view;
		}
	}

}
