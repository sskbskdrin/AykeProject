package com.open.zxing.decode;

import com.google.zxing.LuminanceSource;

/**
 * Created by sskbskdrin on 2015/十月/28.
 */
public class PortraitLuminanceSource extends LuminanceSource {
	private static final int THUMBNAIL_SCALE_FACTOR = 2;

	private final byte[] yuvData;
	private final int dataWidth;
	private final int dataHeight;
	private final int left;
	private final int top;

	public PortraitLuminanceSource(byte[] yuvData,
	                               int dataWidth,
	                               int dataHeight,
	                               int left,
	                               int top,
	                               int width,
	                               int height) {
		super(width, height);
		if (left + width > dataWidth || top + height > dataHeight) {
			throw new IllegalArgumentException("Crop rectangle does not fit within image data.");
		}

		this.yuvData = yuvData;
		this.dataWidth = dataWidth;
		this.dataHeight = dataHeight;
		this.left = left;
		this.top = top;
	}

	@Override
	public byte[] getRow(int y, byte[] row) {
		if (y < 0 || y >= getHeight()) {
			throw new IllegalArgumentException("Requested row is outside the image: " + y);
		}
		int width = getWidth();
		if (row == null || row.length < width) {
			row = new byte[width];
		}
		int offset = (y + top) * dataWidth + left;
		System.arraycopy(yuvData, offset, row, 0, width);
		return row;
	}

	@Override
	public byte[] getMatrix() {
		int width = getWidth();
		int height = getHeight();
		if (width == dataWidth && height == dataHeight) {
			return yuvData;
		}
		int area = width * height;
		byte[] matrix = new byte[area];
		int inputOffset = top * dataWidth + left;

		// If the width matches the full width of the underlying data, perform a single copy.
		if (width == dataWidth) {
			System.arraycopy(yuvData, inputOffset, matrix, 0, area);
			return matrix;
		}
		// Otherwise copy one cropped row at a time.
		byte[] yuv = yuvData;
		for (int y = 0; y < height; y++) {
			int outputOffset = y * width;
			System.arraycopy(yuv, inputOffset, matrix, outputOffset, width);
			inputOffset += dataWidth;
		}
		return matrix;
	}

	@Override
	public boolean isCropSupported() {
		return true;
	}

	@Override
	public LuminanceSource crop(int left, int top, int width, int height) {
		return new PortraitLuminanceSource(yuvData,
				dataWidth,
				dataHeight,
				this.left + left,
				this.top + top,
				width,
				height);
	}

	@Override
	public boolean isRotateSupported() {
		return true;
	}

	@Override
	public LuminanceSource rotateCounterClockwise() {
		return rotate(90);
	}

	public PortraitLuminanceSource rotate(int degree) {
		if (degree == 0)
			return this;
		byte[] data = getMatrix();
		int w = getWidth();
		int h = getHeight();
		switch (degree) {
			case 90:
				data = rotate90(data);
				w = getHeight();
				h = getWidth();
				break;
			case 180:
				data = rotate180(data);
				break;
			case 270:
				data = rotate270(data);
				w = getHeight();
				h = getWidth();
				break;
		}
		return new PortraitLuminanceSource(data, w, h, 0, 0, w, h);
	}

	private byte[] rotate90(byte[] data) {
		byte[] tempData = new byte[data.length];
		int width = getWidth();
		int height = getHeight();
		int n = 0;
		for (int i = 0; i < width; i++) {
			for (int j = 0; j < height; j++) {
				tempData[n++] = data[(height - j - 1) * width + i];
			}
		}
		return tempData;
	}

	private byte[] rotate180(byte[] data) {
		byte temp;
		int count = data.length - 1;
		for (int i = 0; i < data.length / 2; i++) {
			temp = data[i];
			data[i] = data[count - i];
			data[count - i] = temp;
		}
		return data;
	}

	private byte[] rotate270(byte[] data) {
		byte[] tempData = new byte[data.length];
		int width = getWidth();
		int height = getHeight();
		int n = 0;
		for (int i = 0; i < width; i++) {
			for (int j = 0; j < height; j++) {
				tempData[n++] = data[j * width + (width - i - 1)];
			}
		}
		return tempData;
	}

	public int[] renderThumbnail() {
		int width = getThumbnailWidth();
		int height = getThumbnailHeight();
		int[] pixels = new int[width * height];
		byte[] yuv = yuvData;
		int inputOffset = top * dataWidth + left;

		for (int y = 0; y < height; y++) {
			int outputOffset = y * width;
			for (int x = 0; x < width; x++) {
				int grey = yuv[inputOffset + x * THUMBNAIL_SCALE_FACTOR] & 0xff;
				pixels[outputOffset + x] = 0xFF000000 | (grey * 0x00010101);
			}
			inputOffset += dataWidth * THUMBNAIL_SCALE_FACTOR;
		}
		return pixels;
	}

	/**
	 * @return width of image from {@link #renderThumbnail()}
	 */
	public int getThumbnailWidth() {
		return getWidth() / THUMBNAIL_SCALE_FACTOR;
	}

	/**
	 * @return height of image from {@link #renderThumbnail()}
	 */
	public int getThumbnailHeight() {
		return getHeight() / THUMBNAIL_SCALE_FACTOR;
	}


}
