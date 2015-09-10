package com.ayke.library.timer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.annotation.SuppressLint;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;

public class TimerManage {
	private static final String TAG = "TimerManage";

	private static boolean log = false;
	private static final long STEP = 10;

	private HashMap<String, TimerInfo> mTimerList;
	private List<TimerInfo> mRunTaskList;

	private static TimerManage mInstance;

	public static TimerManage getInstance() {
		if (mInstance == null) {
			mInstance = new TimerManage();
		}
		return mInstance;
	}

	@SuppressLint("HandlerLeak")
	private Handler mHandler = new Handler() {
		public void handleMessage(Message msg) {
			String tag = (String) msg.obj;
			if (TextUtils.isEmpty(tag))
				return;
			TimerInfo info = mTimerList.get(tag);
			if (info != null) {
				TimerTaskListener listener = info.getListener();
				if (listener != null) {
					logD("Timer task " + info.getTag() + " complete");
					listener.onTimer(info.getTag(), info.getCount());
				}
				if (info.isRemove()) {
					mTimerList.remove(tag);
				}
			}
			if (mTimerList.size() == 0) {
				isRunning = false;
				mInstance = null;
			}
		};
	};

	public TimerManage() {
		mTimerList = new HashMap<String, TimerManage.TimerInfo>();
		mRunTaskList = new ArrayList<TimerManage.TimerInfo>();
	}

	private void logD(String msg) {
		if (log)
			Log.d(TAG, msg);
	}

	/**
	 * 添加计时器，时间以10ms为倍数
	 * 
	 * @param tag
	 *            计时器标识
	 * @param count
	 *            计时次数，count != 0,大于0时为计时次数，小于0时，为无限循环
	 * @param delay
	 *            延迟时间
	 * @param period
	 *            计时周期，period > 10
	 * @param listener
	 *            计时回调 listener != null;
	 */
	public void addTimerTask(String tag, int count, long delay, long period,
			TimerTaskListener listener) {
		if (TextUtils.isEmpty(tag) || delay < 0 || period < 10 || count == 0
				|| listener == null) {
			throw new IllegalArgumentException();
		}
		synchronized (mTimerList) {
			if (!mTimerList.containsKey(tag)) {
				logD("Timer task add " + tag);
				mTimerList.put(tag, new TimerInfo(tag, count, delay, period,
						listener));
			} else {
				Log.e(TAG, "Timer task " + tag + " already exist!");
			}
		}
	}

	/**
	 * 启动计时器
	 * 
	 * @param tag
	 *            计时器标识
	 */
	public void startTimerTask(String tag) {
		if (TextUtils.isEmpty(tag)) {
			throw new IllegalArgumentException();
		}
		if (!isRunning) {
			new Thread(mTask).start();
		}
		logD("Timer task start " + tag);
		if (mTimerList.containsKey(tag)) {
			addTask(mTimerList.get(tag));
		} else {
			Log.e(TAG, "Timer task start " + tag + " no exist");
		}
	}

	/**
	 * 停止计时器
	 * 
	 * @param tag
	 *            计时器标识
	 */
	public void stopTimerTask(String tag) {
		if (TextUtils.isEmpty(tag)) {
			throw new IllegalArgumentException();
		}
		synchronized (mTimerList) {
			if (mTimerList.containsKey(tag)) {
				logD("Timer task stop " + tag);
				TimerInfo info = mTimerList.remove(tag);
				info.setRemove(true);
			} else {
				Log.e(TAG, "Timer task stop " + tag + " no exist");
			}
		}
	}

	/**
	 * 停止所有计时器
	 */
	public void stopTimerAll() {
		logD("Timer task remove all");
		isRunning = false;
		mTimerList.clear();
		mRunTaskList.clear();
		mInstance = null;
	}

	public interface TimerTaskListener {
		public void onTimer(String tag, int count);
	}

	private boolean isRunning = false;

	private void addTask(TimerInfo info) {
		long current = System.currentTimeMillis();
		info.setStartTime(current + info.getStartTime() + info.getPeriodTime());
		synchronized (mRunTaskList) {
			mRunTaskList.add(info);
		}
	}

	private Runnable mTask = new Runnable() {
		@Override
		public void run() {
			isRunning = true;
			long startTime = System.currentTimeMillis();
			while (isRunning) {
				if (System.currentTimeMillis() - startTime >= STEP) {
					startTime = System.currentTimeMillis();
					synchronized (mRunTaskList) {
						for (int i = 0; i < mRunTaskList.size();) {
							TimerInfo info = mRunTaskList.get(i);

							if (info.isRemove()) {
								mRunTaskList.remove(info);
								continue;
							}

							if (startTime >= info.getStartTime()) {
								if (info.mCount > 0) {
									if (--info.mCount == 0)
										info.setRemove(true);
								}
								info.setStartTime(info.getStartTime()
										+ info.getPeriodTime());
								Message msg = Message.obtain();
								msg.what = 0;
								msg.obj = info.getTag();
								mHandler.sendMessage(msg);
							}
							i++;
						}
					}
				}
			}
		}
	};

	private class TimerInfo {
		private boolean isRemove = false;
		private String mTag;
		private long mPeriodTime;
		private long startTime;
		private TimerTaskListener mListener;

		protected int mCount;

		public TimerInfo(String tag, int count, long delay, long period,
				TimerTaskListener listener) {
			mTag = tag;
			mCount = count;
			startTime = delay;
			mPeriodTime = period;
			mListener = listener;
		}

		public String getTag() {
			return mTag;
		}

		public int getCount() {
			return mCount;
		}

		public long getPeriodTime() {
			return mPeriodTime;
		}

		public TimerTaskListener getListener() {
			return mListener;
		}

		public boolean isRemove() {
			return isRemove;
		}

		public void setRemove(boolean move) {
			isRemove = move;
		}

		public long getStartTime() {
			return startTime;
		}

		public void setStartTime(long startTime) {
			this.startTime = startTime;
		}
	}

}
