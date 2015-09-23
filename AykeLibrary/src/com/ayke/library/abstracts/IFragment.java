package com.ayke.library.abstracts;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ayke.library.util.L;

import java.lang.ref.WeakReference;

/**
 * Created by kay on 2015/5/25.
 * Fragment 基类
 */
public abstract class IFragment extends Fragment {
	protected String TAG = "";
	protected Bundle mBundle; //在initData中可直接使用
	protected View mRootView;
	protected boolean isRunning;

	public IFragment() {
		super();
		TAG = this.getClass().getSimpleName();
	}

	protected abstract int getLayoutId();

	protected abstract void initView();

	protected abstract void initData();

	/**
	 * 使用 MyHandler 时，可重写此方法处理消息
	 *
	 * @param msg 接收到的消息
	 */
	protected void handMessage(Message msg) {
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		L.v(TAG, "onAttach");
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		isRunning = true;
		L.v(TAG, "onCreate");
	}

	public final View onCreateView(LayoutInflater inflater, ViewGroup
			container, Bundle savedInstanceState) {
		if (mRootView == null) {
			L.v(TAG, "onCreateView new");
			if (getLayoutId() > 0)
				mRootView = inflater.inflate(getLayoutId(), null);
		}
		if (mRootView == null) {
			mRootView = setContentView();
		}
		if (mRootView == null) {
			mRootView = super.onCreateView(inflater, container,
					savedInstanceState);
		}
		ViewGroup parent = (ViewGroup) mRootView.getParent();
		if (parent != null) {
			L.v(TAG, "onCreateView old");
			parent.removeView(mRootView);
		}
		return mRootView;
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		initView();
	}

	public View setContentView() {
		return null;
	}

	@Override
	public final void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		L.v(TAG, "onActivityCreated");
		mBundle = getArguments();
		initData();
	}

	public View getRootView() {
		return mRootView;
	}

	@Override
	public void onStart() {
		super.onStart();
		L.v(TAG, "onStart");
	}

	@Override
	public void onPause() {
		super.onPause();
		L.v(TAG, "onPause");
	}

	@Override
	public void onResume() {
		super.onResume();
		L.v(TAG, "onResume");
	}

	@Override
	public void onStop() {
		super.onStop();
		L.v(TAG, "onStop");
	}

	@Override
	public void onDestroyView() {
		super.onDestroyView();
		L.v(TAG, "onDestroyView");
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		isRunning = false;
		L.v(TAG, "onDestroy");
	}

	@Override
	public void onDetach() {
		super.onDetach();
		L.v(TAG, "onDetach");
	}

	@SuppressWarnings("unchecked")
	protected static <T extends View> T $(View parent, int id) {
		return (T) parent.findViewById(id);
	}

	@SuppressWarnings("unchecked")
	protected <T extends View> T $(int id) {
		return (T) mRootView.findViewById(id);
	}

	protected static class MyHandler extends Handler {
		private WeakReference<Activity> mActivity;
		private WeakReference<IFragment> mFragment;

		public MyHandler(Activity activity, IFragment fragment) {
			mActivity = new WeakReference<Activity>(activity);
			mFragment = new WeakReference<IFragment>(fragment);
		}

		@Override
		public void handleMessage(Message msg) {
			IFragment fragment = mFragment.get();
			if (fragment != null) {
				fragment.handMessage(msg);
			}
		}
	}
}
