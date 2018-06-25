package com.open.zxing.tools;

import android.app.Activity;
import android.app.AlertDialog;
import android.graphics.Point;
import android.graphics.Rect;
import android.util.Log;
import android.view.Surface;
import android.view.SurfaceHolder;

import com.ayke.library.util.SysUtils;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.DecodeHintType;
import com.open.zxing.camera.AmbientLightManager;
import com.open.zxing.camera.BeepManager;
import com.open.zxing.camera.CameraManager;

import java.io.IOException;
import java.util.Collection;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.Map;

/**
 * Created by Administrator on 2015/10/9.
 */
public class ScanManager {
	private static final String TAG = "ScanManager";
	private static final Point CODE_RECT = new Point(SysUtils.dip2px(200), SysUtils.dip2px(200));
	private static final Point WORD_RECT = new Point(SysUtils.dip2px(200), SysUtils.dip2px(40));
	private static final Point CODE_LANDSCAPE_RECT = new Point(SysUtils.dip2px(200), SysUtils
			.dip2px(200));
	private static final Point WORD_LANDSCAPE_RECT = new Point(SysUtils.dip2px(250), SysUtils
			.dip2px(40));
	private Rect mCurrentRect;

	private static ScanManager mInstance;
	private Activity mActivity;
	private IScanResultListener mListener;
	private CaptureActivityHandler mCaptureActivityHandler;

	private BeepManager mBeepManager;
	private AmbientLightManager mAmbientLightManager;

	private CameraManager mCameraManager;
	private Collection<BarcodeFormat> mDecodeFormats;
	private Map<DecodeHintType, Object> mDecodeHints;

	private boolean isLandscape;

	private ScanMode mMode = ScanMode.CODE;

	public enum ScanMode {
		CODE,
		WORD
	}

	private ScanManager(Activity activity, IScanResultListener listener) {
		mActivity = activity;
		mBeepManager = new BeepManager(mActivity);
		mAmbientLightManager = new AmbientLightManager(mActivity);
		mListener = listener;
	}

	public static void init(Activity activity, IScanResultListener listener) {
		mInstance = new ScanManager(activity, listener);
	}

	public static ScanManager getInstance() {
		return mInstance;
	}

	public synchronized Rect getViewfinderRect() {
		if (mCurrentRect == null) {
			if (mCameraManager == null || mCameraManager.getSurfacePoint() == null)
				return null;
			Point p;
			Point surfaceP = mCameraManager.getSurfacePoint();
			int offset = 2;
			int leftOffset;
			int topOffset;
			if (isLandscape) {
				if (mMode == ScanMode.WORD) {
					p = WORD_LANDSCAPE_RECT;
					offset = 3;
				} else {
					p = CODE_LANDSCAPE_RECT;
				}
//				leftOffset = (surfaceP.y - p.x) / 2;
//				topOffset = (surfaceP.x - p.y) / offset;
			} else {
				if (mMode == ScanMode.WORD) {
					p = WORD_RECT;
					offset = 3;
				} else {
					p = CODE_RECT;
				}
			}
			leftOffset = (surfaceP.x - p.x) / 2;
			topOffset = (surfaceP.y - p.y) / offset;
			mCurrentRect = new Rect(leftOffset, topOffset, leftOffset + p.x, topOffset + p.y);
		}
		return mCurrentRect;
	}

	public synchronized void changeOrientation() {
		int or = getOrientationDegree();
		isLandscape = or == 0 || 180 == or;
		mCurrentRect = null;
		mCameraManager.updateOrientation(or);
		restartPreviewAfterDelay(0);
	}

	public synchronized void changeMode(ScanMode mode) {
		if (mMode == mode)
			return;
		mMode = mode;
		changeOrientation();
	}

	public void onResume() {
		mCameraManager = new CameraManager(mActivity);
		mCaptureActivityHandler = null;
		mBeepManager.updatePrefs(true, false);
		mAmbientLightManager.start(mCameraManager);
	}

	public void onPause() {
		if (mCaptureActivityHandler != null) {
			mCaptureActivityHandler.quitSynchronously();
			mCaptureActivityHandler = null;
		}
		mAmbientLightManager.stop();
		mBeepManager.close();
		mCameraManager.closeDriver();
	}

	public void addDecodeFormat(BarcodeFormat format) {
		if (mDecodeFormats == null)
			mDecodeFormats = EnumSet.noneOf(BarcodeFormat.class);
		if (format != null)
			mDecodeFormats.add(format);
	}

	public void addDecodeHintType(DecodeHintType key, Object object) {
		if (mDecodeHints == null)
			mDecodeHints = new EnumMap<DecodeHintType, Object>(DecodeHintType.class);
		if (key != null && object != null)
			mDecodeHints.put(key, object);
	}

	public ScanMode getScanMode() {
		return mMode;
	}

	public BeepManager getBeepManager() {
		return mBeepManager;
	}

	public AmbientLightManager getAmbientLightManager() {
		return mAmbientLightManager;
	}

	public CameraManager getCameraManager() {
		return mCameraManager;
	}

	public CaptureActivityHandler getCaptureActivityHandler() {
		return mCaptureActivityHandler;
	}

	public int getOrientationDegree() {
		int rotation = mActivity.getWindowManager().getDefaultDisplay().getRotation();
		switch (rotation) {
			case Surface.ROTATION_0:
				return 90;
			case Surface.ROTATION_90:
				return 0;
			case Surface.ROTATION_180:
				return 270;
			case Surface.ROTATION_270:
				return 180;
			default:
				return 0;
		}
	}

	public void initCamera(SurfaceHolder surfaceHolder) {
		if (surfaceHolder == null) {
			throw new IllegalStateException("No SurfaceHolder provided");
		}
		if (mCameraManager.isOpen()) {
			Log.w(TAG, "initCamera() while already open -- late SurfaceView callback?");
			return;
		}
		try {
			mCameraManager.openDriver(surfaceHolder);
			// Creating the handler starts the preview, which can also throw a RuntimeException.
			if (mCaptureActivityHandler == null) {
				mCaptureActivityHandler = new CaptureActivityHandler(mActivity,
						mDecodeFormats, mDecodeHints,
						null, mCameraManager, mListener);
			}
			mCameraManager.updateOrientation(90);
			restartPreviewAfterDelay(100);
		} catch (IOException ioe) {
			Log.w(TAG, ioe);
			displayFrameworkBugMessageAndExit();
		} catch (RuntimeException e) {
			// Barcode Scanner has seen crashes in the wild of this variety:
			// java.?lang.?RuntimeException: Fail to connect to camera service
			Log.w(TAG, "Unexpected error initializing camera", e);
			displayFrameworkBugMessageAndExit();
		}
	}

	private void displayFrameworkBugMessageAndExit() {
		AlertDialog.Builder builder = new AlertDialog.Builder(mActivity);
		builder.setTitle("扫描器");
		builder.setMessage("很遗憾，Android 相机出现问题。你可能需要重启设备。");
		builder.setPositiveButton("确定", new FinishListener(mActivity));
		builder.setOnCancelListener(new FinishListener(mActivity));
		builder.show();
	}

	public void restartPreviewAfterDelay(long delayMS) {
		if (mCaptureActivityHandler != null) {
			mCaptureActivityHandler.sendEmptyMessageDelayed(MessageBox.restart_preview, delayMS);
		}
	}

	public String getFilePath() {
		if (mActivity != null) {
			return mActivity.getFilesDir().getAbsolutePath();
		}
		return "";
	}
}
