package com.ayke.demo.residemenu;

import com.ayke.demo.R;
import com.ayke.demo.common.SampleListFragment;
import com.ayke.library.residemenu.ResideMenu;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

public class MenuActivity extends FragmentActivity implements
		View.OnClickListener {

	private ResideMenu resideMenu;
	private MenuActivity mContext;

	/**
	 * Called when the activity is first created.
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_fragment);
		mContext = this;
		setUpMenu();
		changeFragment(new SampleListFragment());
	}

	private void setUpMenu() {
		resideMenu = new ResideMenu(this);
		resideMenu.attachToActivity();
		resideMenu.setMenuListener(menuListener);

		resideMenu.setLeftScaleX(0.2f);
		resideMenu.setRightScaleX(0.2f);
		resideMenu.setEnableRightMenu(true);

		TextView tv = new TextView(this);
		tv.setText("Left");
		resideMenu.setLeftMenuView(tv);
		tv = new TextView(this);
		tv.setText("Right");
		resideMenu.setRightMenuView(tv);
	}

	@Override
	public boolean dispatchTouchEvent(MotionEvent ev) {
		return resideMenu.dispatchTouchEvent(ev);
	}

	@Override
	public void onClick(View view) {
		resideMenu.closeMenu();
	}

	private ResideMenu.OnMenuListener menuListener = new ResideMenu.OnMenuListener() {
		@Override
		public void openMenu() {
			Toast.makeText(mContext, "Menu is opened!", Toast.LENGTH_SHORT)
					.show();
		}

		@Override
		public void closeMenu() {
			Toast.makeText(mContext, "Menu is closed!", Toast.LENGTH_SHORT)
					.show();
		}
	};

	private void changeFragment(Fragment targetFragment) {
		resideMenu.clearIgnoredViewList();
		getSupportFragmentManager()
				.beginTransaction()
				.replace(R.id.activity_fragment_content, targetFragment,
						"fragment")
				.setTransitionStyle(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
				.commit();
	}

	public ResideMenu getResideMenu() {
		return resideMenu;
	}
}
