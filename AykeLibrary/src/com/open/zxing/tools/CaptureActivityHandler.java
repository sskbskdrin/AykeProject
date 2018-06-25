/*
 * Copyright (C) 2008 ZXing authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.open.zxing.tools;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.DecodeHintType;
import com.google.zxing.ResultPoint;
import com.google.zxing.ResultPointCallback;
import com.open.zxing.camera.CameraManager;
import com.open.zxing.decode.DecodeThread;
import com.open.zxing.tesseract.DictionaryUtil;

import java.util.Collection;
import java.util.Map;

/**
 * This class handles all the messaging which comprises the state machine for capture.
 *
 * @author dswitkin@google.com (Daniel Switkin)
 */
public final class CaptureActivityHandler extends Handler {

	private static final String TAG = CaptureActivityHandler.class.getSimpleName();

	private final Context mContext;
	private final DecodeThread decodeThread;
	private final CameraManager cameraManager;
	private IScanResultListener mListener;

	public CaptureActivityHandler(Context context,
	                              Collection<BarcodeFormat> decodeFormats,
	                              Map<DecodeHintType, ?> baseHints,
	                              String characterSet,
	                              CameraManager cameraManager, IScanResultListener listener) {
		this.mContext = context;
		decodeThread = new DecodeThread(decodeFormats, baseHints, characterSet,
				new ResultPointCallback() {
					@Override
					public void foundPossibleResultPoint(ResultPoint point) {
						mListener.getFinderView().addPossibleResultPoint(point);
					}
				});
		decodeThread.start();
		this.cameraManager = cameraManager;
		mListener = listener;
	}

	@Override
	public void handleMessage(Message message) {
		switch (message.what) {
			case MessageBox.decode_word:
				Bundle bundle1 = message.getData();
				Bitmap barcode1 = null;
				if (bundle1 != null) {
					byte[] compressedBitmap = bundle1.getByteArray(DecodeThread.BARCODE_BITMAP);
					if (compressedBitmap != null) {
						barcode1 = BitmapFactory.decodeByteArray(compressedBitmap, 0,
								compressedBitmap
										.length, null);
						barcode1 = barcode1.copy(Bitmap.Config.ARGB_8888, true);
					}
				}
				cameraManager.requestPreviewFrame(decodeThread.getHandler(), MessageBox.decode);
				Log.d("ayke", message.obj.toString());
				String str = DictionaryUtil.clipWord(message.obj.toString());
				if (DictionaryUtil.isWord(mContext, str) && mListener != null)
					mListener.onScanResult(str, barcode1);
				cameraManager.requestPreviewFrame(decodeThread.getHandler(), MessageBox.decode);
				break;
			case MessageBox.restart_preview:
				restartPreviewAndDecode();
				break;
			case MessageBox.decode_succeeded:
				Bundle bundle = message.getData();
				Bitmap barcode = null;
				float scaleFactor = 1.0f;
				if (bundle != null) {
					byte[] compressedBitmap = bundle.getByteArray(DecodeThread.BARCODE_BITMAP);
					if (compressedBitmap != null) {
						barcode = BitmapFactory.decodeByteArray(compressedBitmap, 0,
								compressedBitmap.length, null);
						// Mutable copy:
						barcode = barcode.copy(Bitmap.Config.ARGB_8888, true);
					}
					scaleFactor = bundle.getFloat(DecodeThread.BARCODE_SCALED_FACTOR);
				}
				Log.d("ayke", message.obj.toString());
				if (mListener != null)
					mListener.onScanResult(message.obj.toString(), barcode);
				break;
			case MessageBox.decode_failed:
				cameraManager.requestPreviewFrame(decodeThread.getHandler(), MessageBox.decode);
				break;
//			case MessageBox.launch_product_query:
//				String url = (String) message.obj;
//				Intent intent = new Intent(Intent.ACTION_VIEW);
//				intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
//				intent.setData(Uri.parse(url));
//				ResolveInfo resolveInfo =
//						mContext.getPackageManager().resolveActivity(intent, PackageManager
//								.MATCH_DEFAULT_ONLY);
//				String browserPackageName = null;
//				if (resolveInfo != null && resolveInfo.activityInfo != null) {
//					browserPackageName = resolveInfo.activityInfo.packageName;
//					Log.d(TAG, "Using browser in package " + browserPackageName);
//				}
//				// Needed for default Android browser / Chrome only apparently
//				if ("com.android.browser".equals(browserPackageName) || "com.android.chrome"
//						.equals(browserPackageName)) {
//					intent.setPackage(browserPackageName);
//					intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//					intent.putExtra(Browser.EXTRA_APPLICATION_ID, browserPackageName);
//				}
//				try {
//					mContext.startActivity(intent);
//				} catch (ActivityNotFoundException ignored) {
//					Log.w(TAG, "Can't find anything to handle VIEW of URI " + url);
//				}
//				break;
		}
	}

	public void quitSynchronously() {
		cameraManager.stopPreview();
		Message quit = Message.obtain(decodeThread.getHandler(), MessageBox.quit);
		quit.sendToTarget();
		try {
			// Wait at most half a second; should be enough time, and onPause will timeout quickly
			decodeThread.join(500L);
		} catch (InterruptedException e) {

		}
		// Be absolutely sure we don't send any queued up messages
		removeMessages(MessageBox.decode_succeeded);
		removeMessages(MessageBox.decode_failed);
	}

	private void restartPreviewAndDecode() {
		cameraManager.requestPreviewFrame(decodeThread.getHandler(), MessageBox.decode);
		if (mListener != null)
			mListener.getFinderView().postInvalidate();
	}

}
