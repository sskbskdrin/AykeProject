package com.ayke.library;

import android.app.Application;

import com.ayke.library.util.SharePreferenceUtil;
import com.ayke.library.util.SysUtils;

/**
 * Created by Administrator on 2015/9/25.
 */
public abstract class BaseApplication extends Application {

	@Override
	public void onCreate() {
		super.onCreate();
		SysUtils.init(this);
		initialize();
		if(!SharePreferenceUtil.isInit()){
			SharePreferenceUtil.init(this);
		}
	}

	protected abstract void initialize();

}
