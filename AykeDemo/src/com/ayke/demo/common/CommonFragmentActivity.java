package com.ayke.demo.common;

import java.util.ArrayList;
import java.util.List;

import com.ayke.demo.R;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;

public class CommonFragmentActivity extends FragmentActivity {
	private List<Fragment> mFragments;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_fragment);
		mFragments = new ArrayList<Fragment>();
		Bundle bundle = getIntent().getExtras();
		if (bundle != null) {
			String name = bundle.getString("fragment");
			try {
				Fragment fragment = (Fragment) Class.forName(name)
						.newInstance();
				replaceFragment(fragment);
			} catch (InstantiationException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
		}
	}

	public void replaceFragment(Fragment fragment) {
		mFragments.add(fragment);
		FragmentTransaction transaction = getSupportFragmentManager()
				.beginTransaction();
		transaction.setCustomAnimations(R.anim.from_right_to_left_in,
				R.anim.from_right_to_left_out);
		transaction.replace(R.id.activity_fragment_content, fragment).commit();
		// 根据当前fragment页面改变菜单按钮功能
	}

	private void forwardFragment() {
		FragmentTransaction transaction = getSupportFragmentManager()
				.beginTransaction();
		transaction.setCustomAnimations(R.anim.from_left_to_right_in,
				R.anim.from_left_to_right_out);
		transaction.replace(R.id.activity_fragment_content,
				getCurrentFragment()).commit();
		// 根据当前fragment页面改变菜单按钮功能
	}

	private Fragment getCurrentFragment() {
		return mFragments.get(mFragments.size() - 1);
	}

	@Override
	public void onBackPressed() {
		if (mFragments.size() > 1) {
			mFragments.remove(mFragments.size() - 1);
			forwardFragment();
			return;
		}
		super.onBackPressed();
	}

}
