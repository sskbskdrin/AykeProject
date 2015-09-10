package com.ayke.demo.transition3d;

import android.content.Context;
import android.graphics.Camera;
import android.graphics.Matrix;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.Transformation;
import android.widget.LinearLayout;

import com.ayke.demo.R;

public class SwitchImage extends LinearLayout implements OnClickListener {

	private View mImageView0;
	private View mImageView1;
	private RotateAnimation mRotate;

	public SwitchImage(Context context) {
		super(context);
		((LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(
				R.layout.fragment_transition3d_layout, this, true);
		mImageView0 = findViewById(R.id.picture0);
		mImageView1 = findViewById(R.id.picture1);
		mImageView0.setOnClickListener(this);
		mImageView1.setOnClickListener(this);
		if (mRotate == null)
			mRotate = new RotateAnimation(0, 400, 0, 400, 700);
		mRotate.setDuration(6000);
		mRotate.setFillAfter(true);
		mRotate.setInterpolator(new LinearInterpolator());
	}

	@Override
	public void onClick(View v) {
		mImageView1.startAnimation(mRotate);

	}

	private class RotateAnimation extends Animation {
		// private float mFromDegrees;
		// private float mToDegrees;
		private float mCenterX;
		private float mCenterY;
		// private float mDepthZ;
		private Camera mCamera;
		private Matrix mMatrix;

		public RotateAnimation(float fromDegrees, float toDegrees,
				float centerX, float centerY, float depthZ) {
			// mFromDegrees = fromDegrees;
			// mToDegrees = toDegrees;
			mCenterX = centerX;
			mCenterY = centerY;
			// mDepthZ = depthZ;
		}

		@Override
		public void initialize(int width, int height, int parentWidth,
				int parentHeight) {
			super.initialize(width, height, parentWidth, parentHeight);
			mCamera = new Camera();
		}

		@Override
		protected void applyTransformation(float interpolatedTime,
				Transformation t) {

			mMatrix = t.getMatrix();
			mCamera.save();

			mCamera.translate(0.0f, 0.0f, (float) (240.0 * interpolatedTime));
			 mCamera.rotateZ(45);
			// mCamera.rotateX(45);
			mCamera.rotateY(0);
			mCamera.getMatrix(mMatrix);
			mCamera.restore();

			mMatrix.preTranslate(-mCenterX, -mCenterY);
			mMatrix.postTranslate(mCenterX, mCenterY);
			Log.d("Rotate3dAnimation", "interpolatedTime = " + interpolatedTime
					+ "centerX,Y + " + mCenterX + "," + mCenterY + " matrix = "
					+ t.getMatrix());
		}

	}

}
