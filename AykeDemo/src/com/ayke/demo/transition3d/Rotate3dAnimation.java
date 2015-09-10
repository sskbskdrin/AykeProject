package com.ayke.demo.transition3d;

import android.graphics.Camera;
import android.graphics.Matrix;
import android.util.Log;
import android.view.animation.Animation;
import android.view.animation.Transformation;

/**
 * An animation that rotates the view on the Y axis between two specified
 * angles. This animation also adds a translation on the Z axis (depth) to
 * improve the effect.
 */
public class Rotate3dAnimation extends Animation {
	private float mFromDegrees;
	private float mToDegrees;
	private float mCenterX;
	private float mCenterY;
	@SuppressWarnings("unused")
	private float mDepthZ;
	public boolean mReverse;
	private Camera mCamera;
	private Matrix mMatrix;

	/**
	 * Creates a new 3D rotation on the Y axis. The rotation is defined by its
	 * start angle and its end angle. Both angles are in degrees. The rotation
	 * is performed around a center point on the 2D space, definied by a pair of
	 * X and Y coordinates, called centerX and centerY. When the animation
	 * starts, a translation on the Z axis (depth) is performed. The length of
	 * the translation can be specified, as well as whether the translation
	 * should be reversed in time.
	 * 
	 * @param fromDegrees
	 *            the start angle of the 3D rotation
	 * @param toDegrees
	 *            the end angle of the 3D rotation
	 * @param centerX
	 *            the X center of the 3D rotation
	 * @param centerY
	 *            the Y center of the 3D rotation
	 * @param reverse
	 *            true if the translation should be reversed, false otherwise
	 */
	public Rotate3dAnimation(float fromDegrees, float toDegrees, float centerX,
			float centerY, float depthZ, boolean reverse) {
		mFromDegrees = fromDegrees;
		mToDegrees = toDegrees;
		mCenterX = centerX;
		mCenterY = centerY;
		mDepthZ = depthZ;
		mReverse = reverse;
	}

	@Override
	public void initialize(int width, int height, int parentWidth,
			int parentHeight) {
		super.initialize(width, height, parentWidth, parentHeight);
		mCamera = new Camera();
	}

	@Override
	protected void applyTransformation(float interpolatedTime, Transformation t) {
		float degrees = mFromDegrees
				+ ((mToDegrees - mFromDegrees) * interpolatedTime);

		mMatrix = t.getMatrix();
		mCamera.save();
		// if (mReverse) {
		// mCamera.translate(0.0f, 0.0f, mDepthZ * interpolatedTime);
		// } else {
		// mCamera.translate(0.0f, 0.0f, mDepthZ * (1.0f - interpolatedTime));
		// }
//		mCamera.translate(0, 0, 200*interpolatedTime);
//		mCamera.rotateX(interpolatedTime*180);
//		mCamera.rotateY(degrees);
//		mCamera.rotateZ(degrees);
		mCamera.rotateY(degrees);
		mCamera.getMatrix(mMatrix);
		mCamera.restore();

		mMatrix.preTranslate(-mCenterX, -mCenterY);
		mMatrix.postTranslate(mCenterX, mCenterY);
//		mMatrix.preScale(1, interpolatedTime);
//		mMatrix.postScale(1, 0.5f);
		Log.d("Rotate3dAnimation",
				"interpolatedTime = " + interpolatedTime + "centerX,Y + "
						+ mCenterX + "," + mCenterY + " matrix = "
						+ t.getMatrix());
	}

	public void setDegress(float fromDegrees, float toDegrees) {
		mFromDegrees = fromDegrees;
		mToDegrees = toDegrees;
	}
}
