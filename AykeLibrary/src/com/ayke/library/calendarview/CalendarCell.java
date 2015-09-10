package com.ayke.library.calendarview;

import java.util.Calendar;
import java.util.Date;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.widget.LinearLayout.LayoutParams;

import com.ayke.library.util.DateUtils;
import com.ayke.library.util.SysUtils;

/**
 * 
 * Copyright (c) 2012 All rights reserved 名称：CalendarCell.java 描述：日历控件单元格绘制类
 * 
 * @author zhaoqp
 * @date：2013-7-9 下午3:54:16
 * @version v1.0
 */
public class CalendarCell extends View {
	/**
	 * 绘制的类型,yangch
	 */
	private int style = 0;

	// 字体大小
	/** The text size. */
	private int textSize = 22;

	/** The pt. */
	private Paint pt = new Paint();

	/** The rect. */
	private RectF rect = new RectF();

	// 显示的文字
	/** The text date value. */
	private String textDateValue = "";

	// 当前日期
	/** The i date year. */
	private int iDateYear = 0;

	/** The i date month. */
	private int iDateMonth = 0;

	/** The i date day. */
	private int iDateDay = 0;

	// 布尔变量
	/** The is selected. */
	private boolean isSelected = false;

	/** The is active month. */
	private boolean isActiveMonth = false;

	/** The is today. */
	private boolean isToday = false;

	/** The b touched down. */
	private boolean bTouchedDown = false;

	/** The is holiday. */
	private boolean isHoliday = false;

	/** The has record. */
	private boolean hasRecord = false;

	/** The anim alpha duration. */
	public static int ANIM_ALPHA_DURATION = 100;

	/* 被选中的cell颜色 */
	/** The select cell color. */
	private int selectCellColor = Color.rgb(179, 220, 250);//

	/* 最大背景颜色 */
	/** The bg color. */
	private int bgColor = Color.rgb(255, 255, 255);// 232,242,252

	/* 数字颜色 */
	/** The number color. */
	private int numberColor = Color.rgb(51, 51, 51);//

	/* cell背景颜色 */
	/** The cell color. */
	private int cellColor = Color.rgb(232, 242, 252);

	/* 非本月的数字颜色 */
	/** The not active month color. */
	private int notActiveMonthColor = Color.rgb(152, 152, 152);//

	/* 今天cell颜色 */
	/** The today color. */
	private int todayColor = Color.rgb(215, 238, 253);//

	public CalendarCell(Context context) {
		super(context);
	}

	// 构造函数
	/**
	 * Instantiates a new calendar cell.
	 * 
	 * @param context
	 *            the context
	 * @param position
	 *            the position
	 * @param iWidth
	 *            the i width
	 * @param iHeight
	 *            the i height
	 */
	public CalendarCell(Context context,int height) {
		super(context);
		setFocusable(true);
		LayoutParams lp = new LayoutParams(0, height);
		lp.weight = 1;
		setLayoutParams(lp);
		textSize = SysUtils.dip2px(context, 15);// yangch
	}

	/**
	 * 描述：获取这个Cell的日期.
	 * 
	 * @return the this cell date
	 */
	public Calendar getCellDate() {
		Calendar calDate = Calendar.getInstance();
		calDate.clear();
		calDate.set(Calendar.YEAR, iDateYear);
		calDate.set(Calendar.MONTH, iDateMonth);
		calDate.set(Calendar.DAY_OF_MONTH, iDateDay);
		return calDate;
	}

	/**
	 * 描述:获取这个Cell的日期字符串
	 * 
	 * @return
	 */
	public String getThisCellStrDate() {
		Calendar calendar = getCellDate();
		return DateUtils
				.convertDateToString(new Date(calendar.getTimeInMillis()));
	}

	/**
	 * 描述：设置这个Cell的日期.
	 * 
	 * @param iYear
	 *            the i year
	 * @param iMonth
	 *            the i month
	 * @param iDay
	 *            the i day
	 * @param isToday
	 *            the is today
	 * @param isSelected
	 *            the is selected
	 * @param isHoliday
	 *            the is holiday
	 * @param isActiveMonth
	 *            the is active month
	 * @param hasRecord
	 *            the has record
	 */
	public void setThisCellDate(int iYear, int iMonth, int iDay,
			Boolean isToday, Boolean isSelected, Boolean isHoliday,
			int isActiveMonth, boolean hasRecord) {
		iDateYear = iYear;
		iDateMonth = iMonth;
		iDateDay = iDay;

		this.textDateValue = Integer.toString(iDateDay);
		this.isActiveMonth = (iDateMonth == isActiveMonth);
		this.isToday = isToday;
		this.isHoliday = isHoliday;
		this.hasRecord = hasRecord;
		this.isSelected = isSelected;
	}

	/**
	 * 描述：重载绘制方法.
	 * 
	 * @param canvas
	 *            the canvas
	 * @see android.view.View#onDraw(android.graphics.Canvas)
	 */
	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);

		canvas.drawColor(bgColor);
		rect.set(0, 0, this.getWidth(), this.getHeight());
		rect.inset(0.5f, 0.5f);

		final boolean bFocused = IsViewFocused();

		drawDayView(canvas, bFocused);
		drawDayNumber(canvas);
	}

	/**
	 * Checks if is view focused.
	 * 
	 * @return true, if successful
	 */
	public boolean IsViewFocused() {
		return (this.isFocused() || bTouchedDown);
	}

	/**
	 * 描述：绘制日历方格.
	 * 
	 * @param canvas
	 *            the canvas
	 * @param bFocused
	 *            the b focused
	 */
	private void drawDayView(Canvas canvas, boolean bFocused) {
		pt.setColor(getCellColor());
		canvas.drawRect(rect, pt);
		if (hasRecord) {
			drawMark(canvas);
		}
	}

	public void setDrawStyle(int i) {
		style = i;
	}

	/**
	 * 描述：绘制日历中的数字.
	 * 
	 * @param canvas
	 *            the canvas
	 */
	private void drawDayNumber(Canvas canvas) {
		// draw day number
		pt.setTypeface(null);
		pt.setAntiAlias(true);
		pt.setShader(null);
		pt.setFakeBoldText(true);
		pt.setTextSize(textSize);
		pt.setColor(numberColor);
		pt.setUnderlineText(false);

		if (!isActiveMonth) {
			pt.setColor(notActiveMonthColor);
		}

		final int iPosX = (int) rect.left + ((int) rect.width() >> 1)
				- ((int) pt.measureText(textDateValue) >> 1);
		final int iPosY = (int) (this.getHeight()
				- (this.getHeight() - getTextHeight()) / 2 - pt
				.getFontMetrics().bottom);
		canvas.drawText(textDateValue, iPosX, iPosY, pt);
	}

	/**
	 * 描述：得到字体高度.
	 * 
	 * @return the text height
	 */
	private int getTextHeight() {
		return (int) (-pt.ascent() + pt.descent());
	}

	/**
	 * 描述：根据条件返回不同颜色值.
	 * 
	 * @return the cell color
	 */
	private int getCellColor() {
		if (isToday) {
			return todayColor;
		}
		if (isSelected) {
			return selectCellColor;
		}
		// 如需周末有特殊背景色
		if (isHoliday) {
			return cellColor;
		}
		// 默认是白色的单元格
		return cellColor;
	}

	/**
	 * 描述：设置是否被选中.
	 * 
	 * @param selected
	 *            the new selected
	 */
	@Override
	public void setSelected(boolean selected) {
		if (this.isSelected != selected) {
			this.isSelected = selected;
			this.invalidate();
		}
	}

	/**
	 * 描述：设置是否有数据.
	 * 
	 * @param hasRecord
	 *            the new checks for record
	 */
	public void setHasRecord(boolean hasRecord) {
		if (this.hasRecord != hasRecord) {
			this.hasRecord = hasRecord;
			this.invalidate();
		}
	}

	/**
	 * 描述：日历单元点击时间
	 * 
	 * @param event
	 *            the event
	 * @return true, if successful
	 * @see android.view.View#onTouchEvent(android.view.MotionEvent)
	 * @author: zhaoqp
	 * @date：2013-7-19 下午4:31:18
	 * @version v1.0
	 */
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		boolean bHandled = false;
		if (event.getAction() == MotionEvent.ACTION_DOWN) {
			bHandled = true;
			bTouchedDown = true;
			invalidate();
			startAlphaAnimIn(CalendarCell.this);
		}
		if (event.getAction() == MotionEvent.ACTION_CANCEL) {
			bHandled = true;
			bTouchedDown = false;
			invalidate();
		}
		if (event.getAction() == MotionEvent.ACTION_UP) {
			bHandled = true;
			bTouchedDown = false;
			invalidate();
			performClick();
		}
		return bHandled;
	}

	/**
	 * 描述：动画不透明度渐变.
	 * 
	 * @param view
	 *            the view
	 */
	private static void startAlphaAnimIn(View view) {
		AlphaAnimation anim = new AlphaAnimation(0.5F, 1);
		anim.setDuration(ANIM_ALPHA_DURATION);
		anim.startNow();
		view.startAnimation(anim);
	}

	/**
	 * 描述：有记录时的样子.
	 * 
	 * @param canvas
	 *            the canvas
	 * @param Color
	 *            the color
	 */
	private void createReminder(Canvas canvas, int Color) {
		pt.setUnderlineText(true);
		pt.setStyle(Paint.Style.FILL_AND_STROKE);
		pt.setColor(Color);
		Path path = new Path();
		path.moveTo(rect.right - rect.width() / 4, rect.top);
		path.lineTo(rect.right, rect.top);
		path.lineTo(rect.right, rect.top + rect.width() / 4);
		path.lineTo(rect.right - rect.width() / 4, rect.top);
		path.close();
		canvas.drawPath(path, pt);
		pt.setUnderlineText(true);
	}

	private void drawMark(Canvas canvas) {
		if (style == 0) {
			// canvas.drawBitmap(BitmapFactory.decodeResource(getResources(),
			// R.drawable.mark_my_duty), 0, 0, new Paint());
			createReminder(canvas, Color.RED);
		} else if (style == 1) {
			// canvas.drawBitmap(BitmapFactory.decodeResource(getResources(),
			// R.drawable.mark_other_duty), 0, 0, new Paint());
			createReminder(canvas, Color.BLUE);
		} else if (style == 2) {
			// canvas.drawBitmap(BitmapFactory.decodeResource(getResources(),
			// R.drawable.mark_work), 0, 0, new Paint());
			createReminder(canvas, Color.YELLOW);
		} else {
			// canvas.drawBitmap(BitmapFactory.decodeResource(getResources(),
			// R.drawable.mark_work), 0, 0, new Paint());
			createReminder(canvas, Color.BLACK);
		}
	}

	/**
	 * 描述：是否为活动的月.
	 * 
	 * @return true, if is active month
	 */
	public boolean isActiveMonth() {
		return isActiveMonth;
	}
}