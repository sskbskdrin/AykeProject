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
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.widget.RelativeLayout;

import com.ayke.library.util.L;
import com.ayke.library.util.SysUtils;
import com.google.zxing.ResultPoint;

import java.util.Collection;
import java.util.HashSet;

/**
 * This view is overlaid on top of the camera preview. It adds the viewfinder
 * rectangle and partial transparency outside it, as well as the laser scanner
 * animation and result points.
 */
public final class ViewfinderView extends RelativeLayout {
	private static final String TAG = "ViewfinderView";
	/**
	 * 刷新界面的时间
	 */
	private static final long ANIMATION_DELAY = 10L;
	private static final int OPAQUE = 0xFF;
	/**
	 * 四个绿色边角对应的长度
	 */
	private final int ScreenRate = SysUtils.dip2px(15);
	/**
	 * 四个绿色边角对应的宽度
	 */
	private final int CORNER_WIDTH = SysUtils.dip2px(2);
	/**
	 * 扫描框中的中间线的宽度
	 */
	private final int MIDDLE_WIDTH = SysUtils.dip2px(4);
	/**
	 * 中间那条线每次刷新移动的距离
	 */
	private final int SPEED_DISTANCE = SysUtils.dip2px(3);
	/**
	 * 画笔对象的引用
	 */
	private Paint paint;
	/**
	 * 中间滑动线的最顶端位置
	 */
	private int slideTop;
	private static final int maskColor = 0x80000000;
	private static final int resultPointColor = 0xC099CC00;
	private Collection<ResultPoint> possibleResultPoints;
	private Collection<ResultPoint> lastPossibleResultPoints;
	private Rect mFrame;

	public ViewfinderView(Context context, AttributeSet attrs) {
		super(context, attrs);
		paint = new Paint();
		setBackgroundColor(Color.TRANSPARENT);
		possibleResultPoints = new HashSet<ResultPoint>(5);
	}

	@Override
	public void onDraw(Canvas canvas) {
		//中间的扫描框，你要修改扫描框的大小，去CameraManager里面修改
		Rect frame = ScanManager.getInstance().getViewfinderRect();
		if (frame == null) {
			return;
		}
		if (frame != mNewRect) {
			mOldRect = mNewRect;
			mNewRect = frame;
			ani.setDuration(300);
			ani.setInterpolator(new AccelerateDecelerateInterpolator());
			startAnimation(ani);
		}
		if (mFrame == null)
			mFrame = new Rect(mNewRect);
		if (mFrame.width() == 0 || mFrame.height() == 0)
			return;
		//初始化中间线滑动的最上边和最下边
		int width = canvas.getWidth();
		int height = canvas.getHeight();//获取屏幕的宽和高
		paint.setColor(maskColor);

		//画出扫描框外面的阴影部分，共四个部分，扫描框的上面到屏幕上面，扫描框的下面到屏幕下面
		//扫描框的左边面到屏幕左边，扫描框的右边到屏幕右边
		canvas.drawRect(0, 0, width, mFrame.top, paint);
		canvas.drawRect(0, mFrame.top, mFrame.left, mFrame.bottom + 1, paint);
		canvas.drawRect(mFrame.right + 1, mFrame.top, width, mFrame.bottom + 1, paint);
		canvas.drawRect(0, mFrame.bottom + 1, width, height, paint);

		paint.setColor(0x90ffffff);
		paint.setStrokeWidth(1);
		paint.setStyle(Paint.Style.STROKE);
		canvas.drawRect(mFrame, paint);
		paint.setStyle(Paint.Style.FILL);

		//画扫描框边上的角，总共8个部分
		paint.setColor(Color.GREEN);
		canvas.drawRect(mFrame.left, mFrame.top, mFrame.left + ScreenRate,
				mFrame.top + CORNER_WIDTH, paint);
		canvas.drawRect(mFrame.left, mFrame.top, mFrame.left + CORNER_WIDTH, mFrame.top
				+ ScreenRate, paint);
		canvas.drawRect(mFrame.right - ScreenRate, mFrame.top, mFrame.right,
				mFrame.top + CORNER_WIDTH, paint);
		canvas.drawRect(mFrame.right - CORNER_WIDTH, mFrame.top, mFrame.right, mFrame.top
				+ ScreenRate, paint);
		canvas.drawRect(mFrame.left, mFrame.bottom - CORNER_WIDTH, mFrame.left
				+ ScreenRate, mFrame.bottom, paint);
		canvas.drawRect(mFrame.left, mFrame.bottom - ScreenRate,
				mFrame.left + CORNER_WIDTH, mFrame.bottom, paint);
		canvas.drawRect(mFrame.right - ScreenRate, mFrame.bottom - CORNER_WIDTH,
				mFrame.right, mFrame.bottom, paint);
		canvas.drawRect(mFrame.right - CORNER_WIDTH, mFrame.bottom - ScreenRate,
				mFrame.right, mFrame.bottom, paint);
		//绘制中间的线,每次刷新界面，中间的线往下移动SPEED_DISTANCE
		if (ScanManager.getInstance().getScanMode() != ScanManager.ScanMode.WORD) {
			slideTop += SPEED_DISTANCE;
			slideTop %= mFrame.height();
			canvas.drawRect(mFrame.left + CORNER_WIDTH, mFrame.top + slideTop - MIDDLE_WIDTH / 2,
					mFrame.right - CORNER_WIDTH, mFrame.top + slideTop + MIDDLE_WIDTH / 2, paint);
		}
		Collection<ResultPoint> currentPossible = possibleResultPoints;
		Collection<ResultPoint> currentLast = lastPossibleResultPoints;
		if (currentPossible.isEmpty()) {
			lastPossibleResultPoints = null;
		} else {
			possibleResultPoints = new HashSet<ResultPoint>(5);
			lastPossibleResultPoints = currentPossible;
			paint.setAlpha(OPAQUE);
			paint.setColor(resultPointColor);
			for (ResultPoint point : currentPossible) {
				canvas.drawCircle(mFrame.left + point.getX(), mFrame.top + point.getY(), 6.0f,
						paint);
			}
		}
		if (currentLast != null) {
			paint.setAlpha(OPAQUE / 2);
			paint.setColor(resultPointColor);
			for (ResultPoint point : currentLast) {
				canvas.drawCircle(mFrame.left + point.getX(), mFrame.top + point.getY(), 3.0f,
						paint);
			}
		}
		//只刷新扫描框的内容，其他地方不刷新
		postInvalidateDelayed(ANIMATION_DELAY, mFrame.left, mFrame.top, mFrame.right, mFrame
				.bottom);
		super.onDraw(canvas);
	}

	private Rect mOldRect;
	private Rect mNewRect;
	private Animation ani = new Animation() {
		@Override
		protected void applyTransformation(float interpolatedTime, Transformation t) {
			L.d(TAG, "interpolatedTime=" + interpolatedTime);
			if (mOldRect != null && mNewRect != null) {
				mFrame.left = mOldRect.left + (int) ((mNewRect.left - mOldRect.left) *
						interpolatedTime);
				mFrame.top = mOldRect.top + (int) ((mNewRect.top - mOldRect.top) *
						interpolatedTime);
				mFrame.right = mOldRect.right + (int) ((mNewRect.right - mOldRect.right) *
						interpolatedTime);
				mFrame.bottom = mOldRect.bottom + (int) ((mNewRect.bottom - mOldRect
						.bottom) *
						interpolatedTime);
			}
		}
	};

	public void addPossibleResultPoint(ResultPoint point) {
		possibleResultPoints.add(point);
	}

}
