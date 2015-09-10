package com.ayke.demo.timer;

import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import com.ayke.demo.R;
import com.ayke.library.abstracts.IFragment;
import com.ayke.library.timer.TimerManage;
import com.ayke.library.timer.TimerManage.TimerTaskListener;

public class TimerFragment extends IFragment implements OnClickListener,
	TimerTaskListener {

	private TextView[] timer;
	private boolean[] timerRun;
	private int[] timerCount;

	@Override
	protected int getLayoutId() {
		return R.layout.fragment_timer_layout;
	}

	@Override
	protected void initView() {
		timer = new TextView[5];
		timerRun = new boolean[5];
		timerCount = new int[5];
		$(R.id.timer_1_ms_btn).setOnClickListener(this);
		$(R.id.timer_100_ms_btn).setOnClickListener(this);
		$(R.id.timer_1000_ms_btn).setOnClickListener(this);
		$(R.id.timer_1s_1_ms_btn).setOnClickListener(this);
		$(R.id.timer_1s_10_ms_btn).setOnClickListener(this);
		timer[0] = $(R.id.timer_1_ms);
		timer[1] = $(R.id.timer_100_ms);
		timer[2] = $(R.id.timer_1000_ms);
		timer[3] = $(R.id.timer_1s_1_ms);
		timer[4] = $(R.id.timer_1s_10_ms);
		$(R.id.timer_all).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				for (int i = 0; i < 5; i++) {
					if (timerRun[i]) {
						TimerManage.getInstance().stopTimerTask("timer" + i);
						((Button) v).setText("开始");
					} else {
						TimerManage.getInstance().addTimerTask("timer" + i,
							-1, 0, 10, TimerFragment.this);
						((Button) v).setText("停止");
						timer[i].setText("0");
						timerCount[i] = 0;
						TimerManage.getInstance().startTimerTask("timer" + i);
					}
					timerRun[i] = !timerRun[i];
				}
			}
		});
	}

	@Override
	protected void initData() {

	}

	private int index;
	private int count;
	private int delay;
	private int period;

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.timer_1_ms_btn:
				index = 0;
				count = -1;
				delay = 0;
				period = 10;
				break;
			case R.id.timer_100_ms_btn:
				index = 1;
				count = 5;
				delay = 0;
				period = 1000;
				break;
			case R.id.timer_1000_ms_btn:
				index = 2;
				count = -1;
				delay = 0;
				period = 1000;
				break;
			case R.id.timer_1s_1_ms_btn:
				index = 3;
				count = -1;
				delay = 1000;
				period = 1000;
				break;
			case R.id.timer_1s_10_ms_btn:
				index = 4;
				count = 10;
				delay = 1000;
				period = 1000;
				break;
			default:
				break;
		}
		if (timerRun[index]) {
			TimerManage.getInstance().stopTimerTask("timer" + index);
			((Button) v).setText("开始");
		} else {
			TimerManage.getInstance().addTimerTask("timer" + index, count,
				delay, period, this);
			((Button) v).setText("停止");
			timer[index].setText("0");
			timerCount[index] = 0;
			TimerManage.getInstance().startTimerTask("timer" + index);
		}
		timerRun[index] = !timerRun[index];
	}

	@Override
	public void onTimer(String tag, int count) {
		if ("timer0".equals(tag)) {
			timerCount[0]++;
			timer[0].setText("" + timerCount[0]);
		} else if ("timer1".equals(tag)) {
			timerCount[1]++;
			timer[1].setText("" + timerCount[1]);
		} else if ("timer2".equals(tag)) {
			timerCount[2]++;
			timer[2].setText("" + timerCount[2]);
		} else if ("timer3".equals(tag)) {
			timerCount[3]++;
			timer[3].setText("" + timerCount[3]);
		} else if ("timer4".equals(tag)) {
			timerCount[4]++;
			timer[4].setText("" + timerCount[4]);
		}
	}

	@Override
	public void onDestroyView() {
		TimerManage.getInstance().stopTimerAll();
		super.onDestroyView();
	}
}
