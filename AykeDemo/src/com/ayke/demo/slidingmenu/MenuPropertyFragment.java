package com.ayke.demo.slidingmenu;

import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;

import com.ayke.demo.R;
import com.ayke.library.abstracts.IFragment;
import com.ayke.library.slidingmenu.SlidingMenu;

public class MenuPropertyFragment extends IFragment {

	private SlidingMenu mSlidingMenu;

	@Override
	protected int getLayoutId() {
		return R.layout.fragment_menu_layout;
	}

	@Override
	protected void initView() {
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
						mSlidingMenu.setSecondaryShadowDrawable(R.drawable
								.slidingmenu_right_shadow);
						mSlidingMenu.setShadowDrawable(R.drawable
								.slidingmenu_left_shadow);
				}
			}
		});

		// touch mode stuff
		RadioGroup touchAbove = $(R.id.touch_above);
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
		SeekBar scrollScale = $(R.id.scroll_scale);
		scrollScale.setMax(1000);
		scrollScale.setProgress(333);
		scrollScale.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
			@Override
			public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
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
		SeekBar behindWidth = $(R.id.behind_width);
		behindWidth.setMax(1000);
		behindWidth.setProgress(750);
		behindWidth.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
			@Override
			public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
			}

			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {
			}

			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
				float percent = (float) seekBar.getProgress() / seekBar.getMax();
				mSlidingMenu.setBehindWidth((int) (percent * mSlidingMenu.getWidth()));
				mSlidingMenu.requestLayout();
			}
		});

		// shadow stuff
		CheckBox shadowEnabled = $(R.id.shadow_enabled);
		shadowEnabled.setChecked(true);
		shadowEnabled.setOnCheckedChangeListener(new CompoundButton
				.OnCheckedChangeListener() {
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if (isChecked) {
					int mode = mSlidingMenu.getMode();
					if (mode == SlidingMenu.LEFT)
						mSlidingMenu.setShadowDrawable(R.drawable.slidingmenu_left_shadow);
					else if (mode == SlidingMenu.RIGHT)
						mSlidingMenu.setShadowDrawable(R.drawable.slidingmenu_right_shadow);
					else {
						mSlidingMenu.setShadowDrawable(R.drawable.slidingmenu_left_shadow);
						mSlidingMenu.setSecondaryShadowDrawable(R.drawable.slidingmenu_right_shadow);
					}
				} else {
					mSlidingMenu.setShadowDrawable(null);
				}
			}
		});
		SeekBar shadowWidth = $(R.id.shadow_width);
		shadowWidth.setMax(1000);
		shadowWidth.setProgress(75);
		shadowWidth.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
			@Override
			public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

			}

			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {

			}

			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
				float percent = (float) seekBar.getProgress() / (float) seekBar.getMax();
				int width = (int) (percent * (float) mSlidingMenu.getWidth());
				mSlidingMenu.setShadowWidth(width);
				mSlidingMenu.invalidate();
			}
		});

		// fading stuff
		CheckBox fadeEnabled = $(R.id.fade_enabled);
		fadeEnabled.setChecked(true);
		fadeEnabled.setOnCheckedChangeListener(new CompoundButton
				.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				mSlidingMenu.setFadeEnabled(isChecked);
			}
		});
		SeekBar fadeDeg = $(R.id.fade_degree);
		fadeDeg.setMax(1000);
		fadeDeg.setProgress(666);
		fadeDeg.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
			@Override
			public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
			}

			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {
			}

			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
				mSlidingMenu.setFadeDegree((float) seekBar.getProgress() / seekBar.getMax());
			}
		});
	}

	public void setSlidingMenu(SlidingMenu menu){
		mSlidingMenu = menu;
	}

	@Override
	protected void initData() {

	}
}
