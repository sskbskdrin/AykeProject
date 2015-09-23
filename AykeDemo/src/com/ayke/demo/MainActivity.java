package com.ayke.demo;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.widget.AdapterView;

import com.ayke.demo.common.SampleListFragment;
import com.ayke.demo.slidingmenu.MenuPropertyFragment;
import com.ayke.library.abstracts.IFragment;
import com.ayke.library.slidingmenu.SlidingFragmentActivity;
import com.ayke.library.slidingmenu.SlidingMenu;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class MainActivity extends SlidingFragmentActivity implements AdapterView.OnItemClickListener {
	private SlidingMenu mSlidingMenu;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		mSlidingMenu = getSlidingMenu();
		mSlidingMenu.setShadowWidthRes(R.dimen.shadow_width);
		mSlidingMenu.setShadowDrawable(R.drawable.slidingmenu_left_shadow);
		mSlidingMenu.setBehindOffsetRes(R.dimen.slidingmenu_offset);
		mSlidingMenu.setFadeDegree(0.35f);
		mSlidingMenu.setMode(SlidingMenu.LEFT_RIGHT);
		mSlidingMenu.setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);
		initLeftMenu();
		initRightMenu();
		changeMainView(new MenuPropertyFragment());
	}

	private void initLeftMenu() {
		setBehindContentView(R.layout.activity_slidingmenu_left);
		FragmentTransaction t = getSupportFragmentManager()
				.beginTransaction();
		SampleListFragment fragment = new SampleListFragment();
		ArrayList<ClassItem> mLeftList = ReadPropertiesUtil.getFragmentList();
		Collections.sort(mLeftList, mComparator);
		Bundle bundle = new Bundle();
		bundle.putSerializable(SampleListFragment.LIST_DATA, mLeftList);
		fragment.setArguments(bundle);
		fragment.setOnItemClickListener(this);
		t.replace(R.id.menu_frame, fragment);
		t.commit();
	}

	private void initRightMenu() {
		mSlidingMenu.setSecondaryMenu(R.layout
				.activity_slidingmenu_right);
		FragmentTransaction t = getSupportFragmentManager()
				.beginTransaction();
		SampleListFragment fragment = new SampleListFragment();
		ArrayList<ClassItem> mRightList = ReadPropertiesUtil.getActivityList();
		Collections.sort(mRightList, mComparator);
		Bundle bundle = new Bundle();
		bundle.putSerializable(SampleListFragment.LIST_DATA, mRightList);
		fragment.setArguments(bundle);
		fragment.setOnItemClickListener(this);
		t.replace(R.id.menu_frame_two, fragment);
		t.commit();
	}

	private Comparator<ClassItem> mComparator = new Comparator<ClassItem>() {
		@Override
		public int compare(ClassItem lhs, ClassItem rhs) {
			return lhs.getText().compareToIgnoreCase(rhs.getText());
		}
	};

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		ClassItem item = (ClassItem) parent.getAdapter().getItem(position);
		if (item.isActivity()) {
			Intent intent = new Intent();
			intent.setClassName(this, item.getClassName());
			startActivity(intent);
		} else {
			try {
				IFragment fragment = (IFragment) Class.forName(item.getClassName())
						.newInstance();
				changeMainView(fragment);
			} catch (InstantiationException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
		}
	}

	public void changeMainView(IFragment fragment) {
		if (fragment instanceof MenuPropertyFragment) {
			((MenuPropertyFragment) fragment).setSlidingMenu(mSlidingMenu);
		}
		getSupportFragmentManager().beginTransaction().replace(R.id.main_frame, fragment).commit();
		showContent();
	}

}
