/*
 * Copyright (C) 2010 ZXing authors
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

package com.open.zxing.decode;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

import com.google.zxing.BinaryBitmap;
import com.google.zxing.DecodeHintType;
import com.google.zxing.LuminanceSource;
import com.google.zxing.MultiFormatReader;
import com.google.zxing.ReaderException;
import com.google.zxing.Result;
import com.google.zxing.common.HybridBinarizer;
import com.googlecode.tesseract.android.TessBaseAPI;
import com.open.zxing.tools.MessageBox;
import com.open.zxing.tools.ScanManager;

import java.io.ByteArrayOutputStream;
import java.util.Map;

final class DecodeHandler extends Handler {
	private static final String TAG = DecodeHandler.class.getSimpleName();
	private final MultiFormatReader multiFormatReader;
	TessBaseAPI baseApi;
	private boolean running = true;

	public DecodeHandler(Map<DecodeHintType, Object> hints) {
		multiFormatReader = new MultiFormatReader();
		multiFormatReader.setHints(hints);
	}

	private static void bundleThumbnail(LuminanceSource sou, Bundle bundle) {
		PortraitLuminanceSource source = (PortraitLuminanceSource) sou;
		int[] pixels = source.renderThumbnail();
		int width = source.getThumbnailWidth();
		int height = source.getThumbnailHeight();
		Bitmap bitmap = Bitmap.createBitmap(pixels, 0, width, width, height, Bitmap.Config
				.ARGB_8888);
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		bitmap.compress(Bitmap.CompressFormat.JPEG, 50, out);
		bundle.putByteArray(DecodeThread.BARCODE_BITMAP, out.toByteArray());
		bundle.putFloat(DecodeThread.BARCODE_SCALED_FACTOR, (float) width / source.getWidth());
	}

	@Override
	public void handleMessage(Message message) {
		if (!running) {
			return;
		}
		switch (message.what) {
			case MessageBox.decode:
				PortraitLuminanceSource source = ScanManager.getInstance().getCameraManager()
						.buildPorLuminanceSource((byte[]) message.obj, message.arg1, message.arg2)
						.rotate(ScanManager.getInstance().getOrientationDegree());
				if (ScanManager.getInstance().getScanMode() == ScanManager.ScanMode.CODE) {
					decode(source);
				} else if (ScanManager.getInstance().getScanMode() == ScanManager.ScanMode.WORD) {
					decodeWord(source);
				}
				break;
			case MessageBox.quit:
				running = false;
				if (baseApi != null)
					baseApi.end();
				Looper.myLooper().quit();
				break;
		}
	}

	/**
	 * Decode the data within the viewfinder rectangle, and time how long it took. For efficiency,
	 * reuse the same reader objects from one decode to the next.
	 */
	private void decode(PortraitLuminanceSource source) {
		long start = System.currentTimeMillis();
		Result rawResult = null;
		if (source != null) {
			BinaryBitmap bitmap = new BinaryBitmap(new HybridBinarizer(source));
			try {
				rawResult = multiFormatReader.decodeWithState(bitmap);
			} catch (ReaderException re) {
			} finally {
				multiFormatReader.reset();
			}
		}

		Handler handler = ScanManager.getInstance().getCaptureActivityHandler();
		if (rawResult != null) {
			// Don't log the barcode contents for security.
			long end = System.currentTimeMillis();
			Log.d(TAG, "Found barcode in " + (end - start) + " ms");
			if (handler != null) {
				Message message = Message.obtain(handler, MessageBox.decode_succeeded, rawResult);
				Bundle bundle = new Bundle();
				bundleThumbnail(source, bundle);
				message.setData(bundle);
				message.sendToTarget();
			}
		} else {
			if (handler != null) {
				Message message = Message.obtain(handler, MessageBox.decode_failed);
				message.sendToTarget();
//				message = Message.obtain(handler, MessageBox.decode_succeeded, "test");
//				Bundle bundle = new Bundle();
//				bundleThumbnail(source, bundle);
//				message.setData(bundle);
//				message.sendToTarget();
			}
		}
	}

	private void decodeWord(PortraitLuminanceSource source) {
		int[] pixels = source.renderThumbnail();
		int w = source.getThumbnailWidth();
		int h = source.getThumbnailHeight();
		Bitmap bitmap = Bitmap.createBitmap(pixels, 0, w, w, h, Bitmap.Config.ARGB_8888);
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		if (baseApi == null) {
			baseApi = new TessBaseAPI();
			baseApi.init(ScanManager.getInstance().getFilePath(), "eng");
		}
		baseApi.setImage(bitmap);
		String text = baseApi.getUTF8Text();
		text = text.replaceAll("\\W", "");
		Log.d("ayke", " text=" + text);
		baseApi.clear();
		Handler handler = ScanManager.getInstance().getCaptureActivityHandler();
		if (handler != null) {
			Message message = Message.obtain(handler, MessageBox.decode_word, text);
			Bundle bundle = new Bundle();
			bitmap.compress(Bitmap.CompressFormat.JPEG, 50, out);
			bundle.putByteArray(DecodeThread.BARCODE_BITMAP, out.toByteArray());
			message.setData(bundle);
			message.sendToTarget();
		}
	}

}
