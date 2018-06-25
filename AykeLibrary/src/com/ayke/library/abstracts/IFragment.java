package com.ayke.library.abstracts;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ayke.library.util.L;

/**
 * Created by kay on 2015/5/25.
 * Fragment 基类
 */
public abstract class IFragment extends Fragment implements IController, Handler.Callback {
    protected final String TAG;
    /**
     * 在initData中可直接使用
     */
    protected Bundle mBundle;
    protected View mRootView;
    protected boolean isRunning;

    private Handler mHandler;
    private Activity mActivity;

    public IFragment() {
        super();
        TAG = getClass().getSimpleName();
    }

    protected abstract int getLayoutId();

    protected abstract void initView();

    protected abstract void initData();

    @Override
    public boolean handleMessage(Message msg) {
        return false;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof Activity) {
            mActivity = (Activity) context;
        }
        L.v(TAG, "onAttach");
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        isRunning = true;
        L.v(TAG, "onCreate");
    }

    @Override
    public final View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle
            savedInstanceState) {
        if (mRootView == null) {
            L.v(TAG, "onCreateView new");
            if (getLayoutId() > 0) {
                mRootView = inflater.inflate(getLayoutId(), null);
            }
        }
        if (mRootView == null) {
            mRootView = getContentView();
        }
        if (mRootView != null) {
            ViewGroup parent = (ViewGroup) mRootView.getParent();
            if (parent != null) {
                L.v(TAG, "onCreateView old");
                parent.removeView(mRootView);
            }
        }
        return mRootView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        initView();
    }

    protected View getContentView() {
        return null;
    }

    @Override
    public final void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        L.v(TAG, "onActivityCreated");
        mBundle = getArguments();
        initData();
    }

    public final View getRootView() {
        return mRootView;
    }

    @Override
    public void onStart() {
        super.onStart();
        L.v(TAG, "onStart");
    }

    @Override
    public void onResume() {
        super.onResume();
        L.v(TAG, "onResume");
    }

    @Override
    public void onPause() {
        super.onPause();
        L.v(TAG, "onPause");
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
        L.v(TAG, "onDestroy");
        mActivity = null;
        isRunning = false;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mActivity = null;
        L.v(TAG, "onDetach");
    }

    @Override
    public boolean isFinish() {
        return mActivity == null;
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T extends View> T getView(int id) {
        return (T) mRootView.findViewById(id);
    }

    public Handler getMainHandler() {
        if (mHandler == null) {
            mHandler = new Handler(Looper.getMainLooper(), this);
        }
        return mHandler;
    }
}
