package com.open.zxing.tools;

import android.graphics.Bitmap;

/**
 * Created by sskbskdrin on 2015/十月/28.
 */
public interface IScanResultListener {

	public void onScanResult(String result,Bitmap bitmap);

	public ViewfinderView getFinderView();
}
