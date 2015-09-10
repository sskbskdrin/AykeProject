package com.ayke.demo.slidingmenu;

import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;

import com.ayke.demo.R;
import com.ayke.demo.common.SampleListFragment;
import com.ayke.library.slidingmenu.SlidingFragmentActivity;
import com.ayke.library.slidingmenu.SlidingMenu;

public class SlidingMenuActivity extends SlidingFragmentActivity {

	private SlidingMenu mSlidingMenu;

	@SuppressWarnings("unchecked")
	public <T extends View> T $(int id) {
		return (T) super.findViewById(id);
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setBehindContentView(R.layout.activity_slidingmenu_left);
		if (savedInstanceState == null) {
			FragmentTransaction t = this.getSupportFragmentManager()
				.beginTransaction();
			t.replace(R.id.menu_frame, new SampleListFragment());
			t.commit();
		}

		// customize the SlidingMenu
		mSlidingMenu = getSlidingMenu();
		mSlidingMenu.setShadowWidthRes(R.dimen.shadow_width);
		mSlidingMenu.setShadowDrawable(R.drawable.slidingmenu_left_shadow);
		mSlidingMenu.setBehindOffsetRes(R.dimen.slidingmenu_offset);
		mSlidingMenu.setFadeDegree(0.35f);
		mSlidingMenu.setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);

		setContentView(R.layout.activity_slidingmenu_layout);

		// left and right modes
		RadioGroup mode = $(R.id.mode);
		mode.check(R.id.left);
		mode.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				switch (checkedId) {
					case R.id.left:
						mSlidingMenu.setMode(SlidingMenu.LEFT);
						mSlidingMenu.setShadowDrawable(R.drawable
							.slidingmenu_left_shadow);
						break;
					case R.id.right:
						mSlidingMenu.setMode(SlidingMenu.RIGHT);
						mSlidingMenu.setShadowDrawable(R.drawable
							.slidingmenu_right_shadow);
						break;
					case R.id.left_right:
						mSlidingMenu.setMode(SlidingMenu.LEFT_RIGHT);
						mSlidingMenu.setSecondaryMenu(R.layout
							.activity_slidingmenu_right);
						getSupportFragmentManager().beginTransaction().replace
							(R.id.menu_frame_two, new SampleListFragment())
							.commit();
						mSlidingMenu.setSecondaryShadowDrawable(R.drawable
							.slidingmenu_right_shadow);
						mSlidingMenu.setShadowDrawable(R.drawable
							.slidingmenu_left_shadow);
				}
			}
		});

		// touch mode stuff
		RadioGroup touchAbove = (RadioGroup) findViewById(R.id.touch_above);
		touchAbove.check(R.id.touch_above_full);
		touchAbove.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				switch (checkedId) {
					case R.id.touch_above_full:
						mSlidingMenu.setTouchModeAbove(SlidingMenu
							.TOUCHMODE_FULLSCREEN);
						break;
					case R.id.touch_above_margin:
						mSlidingMenu.setTouchModeAbove(SlidingMenu
							.TOUCHMODE_MARGIN);
						break;
					case R.id.touch_above_none:
						mSlidingMenu.setTouchModeAbove(SlidingMenu
							.TOUCHMODE_NONE);
						break;
				}
			}
		});

		// scroll scale stuff
		SeekBar scrollScale = (SeekBar) findViewById(R.id.scroll_scale);
		scrollScale.setMax(1000);
		scrollScale.setProgress(333);
		scrollScale.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
			@Override
			public void onProgressChanged(SeekBar seekBar, int progress,
			                              boolean fromUser) {
			}

			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {
			}

			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
				mSlidingMenu.setBehindScrollScale((float) seekBar.getProgress
					() / seekBar.getMax());
			}
		});

		// behind width stuff
		SeekBar behindWidth = (SeekBar) findViewById(R.id.behind_width);
		behindWidth.setMax(1000);
		behindWidth.setProgress(750);
		behindWidth.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
			@Override
			public void onProgressChanged(SeekBar seekBar, int progress,
			                              boolean fromUser) {
			}

			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {
			}

			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
				float percent = (float) seekBar.getProgress() / seekBar
					.getMax();
				mSlidingMenu.setBehindWidth((int) (percent * mSlidingMenu
					.getWidth()));
				mSlidingMenu.requestLayout();
			}
		});

		// shadow stuff
		CheckBox shadowEnabled = (CheckBox) findViewById(R.id.shadow_enabled);
		shadowEnabled.setChecked(true);
		shadowEnabled.setOnCheckedChangeListener(new CompoundButton
			.OnCheckedChangeListener() {
			public void onCheckedChanged(CompoundButton buttonView, boolean
				isChecked) {
				if (isChecked)
					mSlidingMenu.setShadowDrawable(mSlidingMenu.getMode() ==
						SlidingMenu.LEFT ? R.drawable.slidingmenu_left_shadow
						: R.drawable.slidingmenu_right_shadow);
				else mSlidingMenu.setShadowDrawable(null);
			}
		});
		SeekBar shadowWidth = (SeekBar) findViewById(R.id.shadow_width);
		shadowWidth.setMax(1000);
		shadowWidth.setProgress(75);
		shadowWidth.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
			@Override
			public void onProgressChanged(SeekBar arg0, int arg1, boolean
				arg2) {
			}

			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {
			}

			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
				float percent = (float) seekBar.getProgress() / (float)
					seekBar.getMax();
				int width = (int) (percent * (float) mSlidingMenu.getWidth());
				mSlidingMenu.setShadowWidth(width);
				mSlidingMenu.invalidate();
			}
		});

		// fading stuff
		CheckBox fadeEnabled = (CheckBox) findViewById(R.id.fade_enabled);
		fadeEnabled.setChecked(true);
		fadeEnabled.setOnCheckedChangeListener(new CompoundButton
			.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean
				isChecked) {
				mSlidingMenu.setFadeEnabled(isChecked);
			}
		});
		SeekBar fadeDeg = (SeekBar) findViewById(R.id.fade_degree);
		fadeDeg.setMax(1000);
		fadeDeg.setProgress(666);
		fadeDeg.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
			@Override
			public void onProgressChanged(SeekBar seekBar, int progress,
			                              boolean fromUser) {
			}

			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {
			}

			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
				mSlidingMenu.setFadeDegree((float) seekBar.getProgress() /
					seekBar.getMax());
			}
		});
	}

}
