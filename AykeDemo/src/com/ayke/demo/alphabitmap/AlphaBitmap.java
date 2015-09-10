package com.ayke.demo.alphabitmap;

import java.io.IOException;
import java.io.InputStream;

import com.ayke.demo.R;
import com.ayke.library.abstracts.IFragment;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Shader;
import android.graphics.Bitmap.Config;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;

public class AlphaBitmap extends IFragment {

	@Override
	protected int getLayoutId() {
		return 0;
	}

	@Override
	protected void initView() {

	}

	@Override
	protected void initData() {

	}

	@Override
	public View setContentView() {
		try {
			return new SampleView(getActivity());
		} catch (IOException e) {
			e.printStackTrace();
		}
		return super.setContentView();
	}

	private static class SampleView extends ImageView {
		private Bitmap mBitmap1;
		private Bitmap mBitmap2;
		private Bitmap mBitmap3;
		private Bitmap mBitmap4;
		private Shader mShader;

		private static void drawIntoBitmap(Bitmap bm) {
			float width = bm.getWidth();
			float height = bm.getHeight();
			Canvas c = new Canvas(bm);
			Paint p = new Paint();
			p.setAntiAlias(true);

			p.setAlpha(0x40);
			c.drawCircle(width / 2, height / 2, width / 2, p);

			p.setAlpha(0x30);
			p.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC));
			p.setTextSize(60);
			p.setTextAlign(Paint.Align.CENTER);
			Paint.FontMetrics fm = p.getFontMetrics();
			c.drawText("Alpha", width / 2, (height - fm.ascent) / 2, p);
		}

		public SampleView(Context context) throws IOException {
			super(context);
			setFocusable(true);

			InputStream is = context.getResources()
				.openRawResource(R.drawable.ic_launcher);
			mBitmap1 = BitmapFactory.decodeStream(is);
			mBitmap2 = mBitmap1.extractAlpha();
			mBitmap3 = Bitmap.createBitmap(200, 200, Bitmap.Config.ALPHA_8);
			BitmapDrawable bd = new BitmapDrawable(context.getResources(), is);
			mBitmap4 = bd.getBitmap();
			drawIntoBitmap(mBitmap3);
			mShader = new LinearGradient(0, 0, 100, 70, new int[]{Color.RED,
				Color.GREEN, Color.BLUE}, null, Shader.TileMode.MIRROR);
			this.setLayoutParams(new LayoutParams(200, 200));

			int bitmap_w = mBitmap1.getWidth();
			int bitmap_h = mBitmap1.getHeight();
			int[] arrayColor = new int[bitmap_w * bitmap_h];
			int count = 0;
			int color1 = 0;
			int color = 0;
			for (int i = 0; i < bitmap_h; i++) {
				for (int j = 0; j < bitmap_w; j++) {
					color1 = mBitmap1.getPixel(j, i);
					// 这里也可以取出 R G B 可以扩展一下 做更多的处理，
					// 暂时我只是要处理除了透明的颜色，改变其他的颜色
					if (color1 != 0) {
						color = 0;
					}
					if ((color1 & 0xff000000) != 0) {
						color = 0xffff0000;
					} else {
						color = 0;
					}
					arrayColor[count++] = color;
				}
			}
			mBitmap2 = Bitmap.createBitmap(arrayColor, bitmap_w, bitmap_h,
				Config.ARGB_8888);
			this.setImageBitmap(mBitmap2);

		}

		@SuppressLint("DrawAllocation")
		@Override
		protected void onDraw(Canvas canvas) {
			canvas.drawColor(Color.WHITE);
			super.onDraw(canvas);

			Paint p = new Paint();
			float y = 10;

			p.setColor(Color.RED);
			canvas.drawBitmap(mBitmap1, 10, y, p);
			y += mBitmap1.getHeight() + 10;
			canvas.drawBitmap(mBitmap2, 10, y, p);
			y += mBitmap3.getHeight() + 10;
			canvas.drawBitmap(mBitmap4, 10, y, p);
			y += mBitmap2.getHeight() + 10;
			p.setShader(mShader);
			canvas.drawBitmap(mBitmap3, 10, y, p);
		}
	}
}
