package com.ayke.demo.viewpagerimage;

import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;

import com.ayke.demo.R;
import com.ayke.library.abstracts.IFragment;
import com.ayke.library.autoviewpage.AutoScrollViewPager;

public class ViewPagerImage extends IFragment implements OnPageChangeListener {
	/**
	 * 装点点的ImageView数组
	 */
	private ImageView[] tips;

	/**
	 * 装ImageView数组
	 */
	private ImageView[] mImageViews;

	/**
	 * 图片资源id
	 */
	private int[] imgIdArray = new int[]{R.drawable.viewpager_item0, R
		.drawable.viewpager_item1, R.drawable.viewpager_item2, R.drawable
		.viewpager_item3,};

	@Override
	protected int getLayoutId() {
		return R.layout.fragmnet_viewpager_layout;
	}

	@Override
	protected void initView() {
		ViewGroup group = $(R.id.viewGroup);
		AutoScrollViewPager mViewPager = $(R.id.viewPager);
		// 将点点加入到ViewGroup中
		tips = new ImageView[imgIdArray.length];
		for (int i = 0; i < tips.length; i++) {
			ImageView imageView = new ImageView(getActivity());
			imageView.setLayoutParams(new LayoutParams(10, 10));
			tips[i] = imageView;
			if (i == 0) {
				tips[i].setBackgroundResource(R.drawable.viewpager_focused);
			} else {
				tips[i].setBackgroundResource(R.drawable.viewpager_unfocused);
			}
			group.addView(imageView);
		}
		// 将图片装载到数组中
		mImageViews = new ImageView[imgIdArray.length];
		for (int i = 0; i < mImageViews.length; i++) {
			ImageView imageView = new ImageView(getActivity());
			mImageViews[i] = imageView;
			imageView.setImageResource(imgIdArray[i]);
		}
		// 设置Adapter
		mViewPager.setScrollDuration(1000);
		mViewPager.setCycleScroll(true);
		mViewPager.setAutoScroll(true);
		mViewPager.setAdapter(new MyAdapter());
		// 设置监听，主要是设置点点的背景
		mViewPager.setOnPageChangeListener(this);
	}

	@Override
	protected void initData() {

	}

	public class MyAdapter extends PagerAdapter {

		@Override
		public int getCount() {
			return mImageViews.length;
		}

		@Override
		public boolean isViewFromObject(View arg0, Object arg1) {
			return arg0 == arg1;
		}

		/**
		 * 载入图片进去，用当前的position 除以 图片数组长度取余数是关键
		 */
		@Override
		public Object instantiateItem(ViewGroup container, int position) {
			return mImageViews[position];
		}

	}

	@Override
	public void onPageScrollStateChanged(int arg0) {

	}

	@Override
	public void onPageScrolled(int arg0, float arg1, int arg2) {

	}

	@Override
	public void onPageSelected(int arg0) {
		setImageBackground(arg0 % mImageViews.length);
		Log.e("log", "你当前选择的是  " + arg0);
	}

	/**
	 * 设置选中的tip的背景
	 *
	 * @param selectItems 选择项
	 */
	private void setImageBackground(int selectItems) {
		for (int i = 0; i < tips.length; i++) {
			if (i == selectItems) {
				tips[i].setBackgroundResource(R.drawable.viewpager_focused);
			} else {
				tips[i].setBackgroundResource(R.drawable.viewpager_unfocused);
			}
		}
	}

}
