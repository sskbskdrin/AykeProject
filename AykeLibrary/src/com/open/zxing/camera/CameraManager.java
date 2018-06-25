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

package com.open.zxing.camera;

import android.content.Context;
import android.graphics.Point;
import android.graphics.Rect;
import android.hardware.Camera;
import android.os.Handler;
import android.util.Log;
import android.view.SurfaceHolder;

import com.google.zxing.PlanarYUVLuminanceSource;
import com.open.zxing.camera.open.OpenCameraInterface;
import com.open.zxing.decode.PortraitLuminanceSource;
import com.open.zxing.tools.ScanManager;

import java.io.IOException;

/**
 * This object wraps the Camera service object and expects to be the only one talking to it. The
 * implementation encapsulates the steps needed to take preview-sized images, which are used for
 * both preview and decoding.
 *
 * @author dswitkin@google.com (Daniel Switkin)
 */
public final class CameraManager {
	private static final String TAG = CameraManager.class.getSimpleName();

	private final Context context;
	private final CameraConfigurationManager configManager;
	/**
	 * Preview frames are delivered here, which we pass on to the registered handler. Make sure to
	 * clear the handler so it will only receive one message.
	 */
	private final PreviewCallback previewCallback;
	boolean isPortrait = true;
	private Camera camera;
	private AutoFocusManager autoFocusManager;
	private Rect framingRectInPreview;
	private Point surfacePoint;
	private SurfaceHolder surfaceHolder;
	private boolean initialized;
	private boolean previewing;
	private int requestedCameraId = OpenCameraInterface.NO_REQUESTED_CAMERA;

	public CameraManager(Context context) {
		this.context = context;
		this.configManager = new CameraConfigurationManager();
		previewCallback = new PreviewCallback(configManager);
	}

	public Point getSurfacePoint() {
		return surfacePoint;
	}

	public void findBestSurfacePoint(Point maxPoint) {
		Point cameraResolution = configManager.getCameraResolution();
		if (cameraResolution == null || maxPoint == null || maxPoint.x == 0 || maxPoint.y == 0)
			return;
		double scaleX, scaleY, scale;
		if (maxPoint.x < maxPoint.y) {
			scaleX = cameraResolution.x * 1.0f / maxPoint.y;
			scaleY = cameraResolution.y * 1.0f / maxPoint.x;
		} else {
			scaleX = cameraResolution.x * 1.0f / maxPoint.x;
			scaleY = cameraResolution.y * 1.0f / maxPoint.y;
		}
		scale = scaleX > scaleY ? scaleX : scaleY;
		if (maxPoint.x < maxPoint.y) {
			surfacePoint.x = (int) (cameraResolution.y / scale);
			surfacePoint.y = (int) (cameraResolution.x / scale);
		} else {
			surfacePoint.x = (int) (cameraResolution.x / scale);
			surfacePoint.y = (int) (cameraResolution.y / scale);
		}
	}

	/**
	 * Opens the camera driver and initializes the hardware parameters.
	 *
	 * @param holder The surface object which the camera will draw preview frames into.
	 * @throws IOException Indicates the camera driver failed to open.
	 */
	public synchronized void openDriver(SurfaceHolder holder) throws IOException {
		Camera theCamera = camera;
		surfaceHolder = holder;
		if (theCamera == null) {
			theCamera = OpenCameraInterface.open(requestedCameraId);
			if (theCamera == null) {
				throw new IOException();
			}
			camera = theCamera;
		}
		theCamera.setPreviewDisplay(holder);
		if (!initialized) {
			initialized = true;
			int width = holder.getSurfaceFrame().width();
			int height = holder.getSurfaceFrame().height();
			surfacePoint = new Point(width, height);
			configManager.initFromCameraParameters(theCamera, surfacePoint);
			findBestSurfacePoint(surfacePoint);
		}

		Camera.Parameters parameters = theCamera.getParameters();
		String parametersFlattened = parameters == null ? null : parameters.flatten(); // Save
		// these, temporarily
		try {
			configManager.setDesiredCameraParameters(theCamera, false);
		} catch (RuntimeException re) {
			// Driver failed
			Log.w(TAG, "Camera rejected parameters. Setting only minimal safe-mode parameters");
			Log.i(TAG, "Resetting to saved camera params: " + parametersFlattened);
			// Reset:
			if (parametersFlattened != null) {
				parameters = theCamera.getParameters();
				parameters.unflatten(parametersFlattened);
				try {
					theCamera.setParameters(parameters);
					configManager.setDesiredCameraParameters(theCamera, true);
				} catch (RuntimeException re2) {
					// Well, darn. Give up
					Log.w(TAG, "Camera rejected even safe-mode parameters! No configuration");
				}
			}
		}
	}

	public synchronized void updateOrientation(int or) {
		isPortrait = (or == 90 || or == 270);
		framingRectInPreview = null;
		stopPreview();
		camera.setDisplayOrientation(or);
		startPreview();
	}

	public synchronized boolean isOpen() {
		return camera != null;
	}

	/**
	 * Closes the camera driver if still in use.
	 */
	public synchronized void closeDriver() {
		if (camera != null) {
			camera.release();
			camera = null;
			// Make sure to clear these each time we close the camera, so that any scanning rect
			// requested by intent is forgotten.
			framingRectInPreview = null;
		}
	}

	/**
	 * Asks the camera hardware to begin drawing preview frames to the screen.
	 */
	public synchronized void startPreview() {
		Camera theCamera = camera;
		if (theCamera != null && !previewing) {
			theCamera.startPreview();
			previewing = true;
			autoFocusManager = new AutoFocusManager(context, camera);
		}
	}

	/**
	 * Tells the camera to stop drawing preview frames.
	 */
	public synchronized void stopPreview() {
		if (autoFocusManager != null) {
			autoFocusManager.stop();
			autoFocusManager = null;
		}
		if (camera != null && previewing) {
			camera.stopPreview();
			previewCallback.setHandler(null, 0);
			previewing = false;
		}
	}

	/**
	 * @param newSetting if {@code true}, light should be turned on if currently off. And vice
	 *                   versa.
	 */
	public synchronized void setTorch(boolean newSetting) {
		if (newSetting != configManager.getTorchState(camera)) {
			if (camera != null) {
				if (autoFocusManager != null) {
					autoFocusManager.stop();
				}
				configManager.setTorch(camera, newSetting);
				if (autoFocusManager != null) {
					autoFocusManager.start();
				}
			}
		}
	}

	/**
	 * A single preview frame will be returned to the handler supplied. The data will arrive as
	 * byte[]
	 * in the message.obj field, with width and height encoded as message.arg1 and message.arg2,
	 * respectively.
	 *
	 * @param handler The handler to send the message to.
	 * @param message The what field of the message to be sent.
	 */
	public synchronized void requestPreviewFrame(Handler handler, int message) {
		Camera theCamera = camera;
		if (theCamera != null && previewing) {
			previewCallback.setHandler(handler, message);
			theCamera.setOneShotPreviewCallback(previewCallback);
		}
	}

	/**
	 * Calculates the framing rect which the UI should draw to show the user where to place the
	 * barcode. This target helps with alignment as well as forces the user to hold the device
	 * far enough away to ensure the image will be in focus.
	 *
	 * @return The rectangle to draw on screen in window coordinates.
	 */
//	public synchronized Rect getFramingRect() {
//		if (framingRect == null) {
//			if (camera == null || surfacePoint == null) {
//				return null;
//			}
//			int width;
//			int height;
//			if (isPortrait) {
//				width = SysUtils.dip2px(200);
//				height = SysUtils.dip2px(50);
//			} else {
//				width = SysUtils.dip2px(200);
//				height = SysUtils.dip2px(50);
//			}
//			int leftOffset = (surfacePoint.x - width) / 2;
//			int topOffset = (surfacePoint.y - height) / 2;
//			framingRect = new Rect(leftOffset, topOffset, leftOffset + width, topOffset + height);
//			framingRect = ScanManager.getInstance().getViewfinderRect();
//			Log.d("ayke", "Calculated framing rect: " + framingRect);
//		}
//		return framingRect;
//	}

	/**
	 * Like {@link #getFramingRectInPreview} but coordinates are in terms of the preview frame,
	 * not UI / screen.
	 *
	 * @return {@link Rect} expressing barcode scan area in terms of the preview size
	 */
	public synchronized Rect getFramingRectInPreview() {
		if (framingRectInPreview == null) {
			Rect framingRect = ScanManager.getInstance().getViewfinderRect();
			Point cameraResolution = configManager.getCameraResolution();
			if (framingRect == null || cameraResolution == null || surfacePoint == null) {
				return null;
			}
			Rect rect = new Rect(framingRect);
			float scaleX = cameraResolution.x * 1.0f / surfacePoint.y;
			float scaleY = cameraResolution.y * 1.0f / surfacePoint.x;
			if (isPortrait) {
				rect.left = (int) (framingRect.top * scaleY);
				rect.right = (int) (framingRect.bottom * scaleY);
				rect.top = (int) (framingRect.left * scaleX);
				rect.bottom = (int) (framingRect.right * scaleX);
			} else {
				scaleX = cameraResolution.x * 1.0f / surfacePoint.x;
				scaleY = cameraResolution.y * 1.0f / surfacePoint.y;
				rect.left = (int) (framingRect.left * scaleX);
				rect.right = (int) (framingRect.right * scaleX);
				rect.top = (int) (framingRect.top * scaleY);
				rect.bottom = (int) (framingRect.bottom * scaleY);
			}
			framingRectInPreview = rect;
		}
		return framingRectInPreview;
	}

	/**
	 * Allows third party apps to specify the camera ID, rather than determine
	 * it automatically based on available cameras and their orientation.
	 *
	 * @param cameraId camera ID of the camera to use. A negative value means "no preference".
	 */
	public synchronized void setManualCameraId(int cameraId) {
		requestedCameraId = cameraId;
	}

	/**
	 * Allows third party apps to specify the scanning rectangle dimensions, rather than determine
	 * them automatically based on screen resolution.
	 *
	 * @param width  The width in pixels to scan.
	 * @param height The height in pixels to scan.
	 */
	public synchronized void setManualFramingRect(int width, int height) {
		if (width > surfacePoint.x) {
			width = surfacePoint.x;
		}
		if (height > surfacePoint.y) {
			height = surfacePoint.y;
		}
		int leftOffset = (surfacePoint.x - width) / 2;
		int topOffset = (surfacePoint.y - height) / 2;
//		framingRect = new Rect(leftOffset, topOffset, leftOffset + width, topOffset + height);
//		Log.d(TAG, "Calculated manual framing rect: " + framingRect);
		framingRectInPreview = null;
	}

	/**
	 * A factory method to build the appropriate LuminanceSource object based on the format
	 * of the preview buffers, as described by Camera.Parameters.
	 *
	 * @param data   A preview frame.
	 * @param width  The width of the image.
	 * @param height The height of the image.
	 * @return A PlanarYUVLuminanceSource instance.
	 */
	public PlanarYUVLuminanceSource buildLuminanceSource(byte[] data, int width, int height) {
		Rect rect = getFramingRectInPreview();
		if (rect == null) {
			return null;
		}
		// Go ahead and assume it's YUV rather than die.
		return new PlanarYUVLuminanceSource(data, width, height, rect.left, rect.top,
				rect.width(), rect.height(), false);
	}

	public PortraitLuminanceSource buildPorLuminanceSource(byte[] data, int width, int height) {
		Rect rect = getFramingRectInPreview();
		if (rect == null || data == null) {
			return null;
		}
		// Go ahead and assume it's YUV rather than die.
		return new PortraitLuminanceSource(data, width, height, rect.left, rect.top,
				rect.width(), rect.height());
	}

}
