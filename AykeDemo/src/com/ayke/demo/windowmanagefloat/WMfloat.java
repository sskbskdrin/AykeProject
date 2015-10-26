package com.ayke.demo.windowmanagefloat;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;

import com.ayke.demo.DemoApplication;
import com.ayke.demo.R;
import com.ayke.library.abstracts.IFragment;

public class WMfloat extends IFragment {
	private WindowManager mWindowManager;
	private FloatView mLayout;

	@Override
	protected int getLayoutId() {
		return R.layout.activity_main;
	}

	@Override
	protected void initView() {
	}

	@Override
	protected void initData() {
		showView();
	}

	private void showView() {
		mLayout = new FloatView(getActivity().getApplicationContext());
		mLayout.setBackgroundResource(R.drawable.ic_launcher);
		// 获取WindowManager
		mWindowManager = (WindowManager) getActivity().getApplicationContext()
				.getSystemService(Context.WINDOW_SERVICE);
		// 设置LayoutParams(全局变量）相关参数
		WindowManager.LayoutParams param = new WindowManager.LayoutParams();

		param.type = WindowManager.LayoutParams.TYPE_SYSTEM_ALERT; // 系统提示类型,重要
		param.format = 1;
		param.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE; // 不能抢占聚焦点
		param.flags = param.flags
				| WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH;
		param.flags = param.flags
				| WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS; // 排版不受限制

		param.alpha = 1.0f;

		param.gravity = Gravity.LEFT | Gravity.TOP; // 调整悬浮窗口至左上角
		// 以屏幕左上角为原点，设置x、y初始值
		param.x = 0;
		param.y = 0;

		// 设置悬浮窗口长宽数据
		param.width = 140;
		param.height = 140;

		// 显示myFloatView图像
		mLayout.setWmParams(param);
		mWindowManager.addView(mLayout, param);

	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		// 在程序退出(Activity销毁）时销毁悬浮窗口
		mWindowManager.removeView(mLayout);
	}
}
