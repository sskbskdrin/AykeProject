package com.ayke.library.calendarview;

import java.util.Calendar;

import com.ayke.library.calendarview.CalendarView.MeasureWidthListener;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.FontMetrics;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewTreeObserver;

/**
 * Copyright (c) 2012 All rights reserved 名称：CalendarHeader.java 描述：日历控件头部绘制类
 * 
 * @author ayke
 * @date：2015-01-13 下午2:07:52
 * @version v1.0
 */
public class CalendarHeader extends View {

	/** The tag. */
	public String TAG = "CalendarHeader";

	/** The m paint. */
	private final Paint mPaint;

	/** The rect. */
	private RectF rect = new RectF();

	// 星期几
	// /** The week day. */
	// private int weekDay = Calendar.SUNDAY;

	// 星期的数据
	/** The day name. */
	private String[] dayName = new String[10];

	/** The width. */
	private int width = 320;

	/** 每个单元格的宽度. */
	private int cellWidth = 40;

	/** 文字颜色. */
	private int defaultTextColor = Color.rgb(51, 51, 51);

	/** 特别文字颜色. */
	private int specialTextColor = Color.rgb(207, 0, 0);

	/** 字体大小. */
	private int defaultTextSize = 25;

	/** 字体是否加粗. */
	private boolean defaultTextBold = false;

	/** 是否有设置头部背景. */
	private boolean hasBg = false;

	private MeasureWidthListener widthListener;
	
	public void setOnMeasureWidthListener(MeasureWidthListener listener){
		widthListener = listener;
	}

	/**
	 * 日历头.
	 * 
	 * @param context
	 *            the context
	 */
	public CalendarHeader(Context context) {
		this(context, null);
	}

	/**
	 * Instantiates a new calendar header.
	 * 
	 * @param context
	 *            the context
	 * @param attributeset
	 *            the attributeset
	 */
	public CalendarHeader(Context context, AttributeSet attributeset) {
		super(context);
		dayName[Calendar.SUNDAY] = "日";
		dayName[Calendar.MONDAY] = "一";
		dayName[Calendar.TUESDAY] = "二";
		dayName[Calendar.WEDNESDAY] = "三";
		dayName[Calendar.THURSDAY] = "四";
		dayName[Calendar.FRIDAY] = "五";
		dayName[Calendar.SATURDAY] = "六";
		mPaint = new Paint();
		mPaint.setColor(defaultTextColor);
		mPaint.setAntiAlias(true);
		mPaint.setTypeface(Typeface.DEFAULT);
		mPaint.setTextSize(defaultTextSize);

		cellWidth = width / 7;

		getViewTreeObserver().addOnPreDrawListener(
				new ViewTreeObserver.OnPreDrawListener() {
					@Override
					public boolean onPreDraw() {
						CalendarHeader header = CalendarHeader.this;
						width = header.getMeasuredWidth();
						cellWidth = width / 7;
						if (widthListener != null) {
							widthListener.onMeasureResult(width);
						}
						header.getViewTreeObserver().removeOnPreDrawListener(
								this);
						invalidate();
						return true;
					}
				});
	}

	/**
	 * 描述：设置背景.
	 * 
	 * @param resid
	 *            the new header background resource
	 */
	public void setHeaderBackgroundResource(int resid) {
		setBackgroundResource(resid);
		hasBg = true;
	}

	/**
	 * 描述：文字大小.
	 * 
	 * @return the text size
	 */
	public int getTextSize() {
		return defaultTextSize;
	}

	/**
	 * 描述：设置文字大小.
	 * 
	 * @param mTextSize
	 *            the new text size
	 */
	public void setTextSize(int mTextSize) {
		this.defaultTextSize = mTextSize;
		mPaint.setTextSize(defaultTextSize);
		this.invalidate();
	}

	/**
	 * @param canvas
	 *            the canvas
	 * @see android.view.View#onDraw(android.graphics.Canvas)
	 * @author: zhaoqp
	 * @date：2013-7-19 下午4:30:45
	 * @version v1.0
	 */
	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);

		if (!hasBg) {
			canvas.drawColor(Color.WHITE);
			// 设置矩形大小
			rect.set(0, 0, this.getWidth(), this.getHeight());
			rect.inset(0.5f, 0.5f);
		}
		// 绘制日历头部
		drawDayHeader(canvas);

	}

	/**
	 * Draw day header.
	 * 
	 * @param canvas
	 *            the canvas
	 */
	private void drawDayHeader(Canvas canvas) {
		// 写入日历头部，设置画笔参数
		if (!hasBg) {
			// 画矩形，并设置矩形画笔的颜色
			mPaint.setColor(Color.rgb(150, 195, 70));
			canvas.drawRect(rect, mPaint);
		}

		if (defaultTextBold) {
			mPaint.setFakeBoldText(true);
		}
		mPaint.setColor(defaultTextColor);

		for (int iDay = 1; iDay < 8; iDay++) {
			if (iDay == 1 || iDay == 7) {
				mPaint.setColor(specialTextColor);
			}
			// draw day name
			final String sDayName = getWeekDayName(iDay);

			TextPaint mTextPaint = new TextPaint(Paint.ANTI_ALIAS_FLAG);
			mTextPaint.setTypeface(Typeface.DEFAULT_BOLD);
			mTextPaint.setTextSize(defaultTextSize);
			FontMetrics fm = mTextPaint.getFontMetrics();
			// 得到行高
			int textHeight = (int) Math.ceil(fm.descent - fm.ascent);
			int textWidth = (int) getStringWidth(sDayName, mTextPaint);

			final int iPosX = (int) rect.left + cellWidth * (iDay - 1)
					+ (cellWidth - textWidth) / 2;
			final int iPosY = (int) (this.getHeight()
					- (this.getHeight() - textHeight) / 2 - mPaint
					.getFontMetrics().bottom);
			canvas.drawText(sDayName, iPosX, iPosY, mPaint);
			mPaint.setColor(defaultTextColor);
		}

	}

	/**
	 * 描述：获取星期的文字描述.
	 * 
	 * @param calendarDay
	 *            the calendar day
	 * @return the week day name
	 */
	public String getWeekDayName(int calendarDay) {
		return dayName[calendarDay];
	}

	/**
	 * 描述：获取文字的像素宽.
	 * 
	 * @param str
	 *            the str
	 * @param paint
	 *            the paint
	 * @return the string width
	 */
	public static float getStringWidth(String str, TextPaint paint) {
		float strWidth = paint.measureText(str);
		return strWidth;
	}

}