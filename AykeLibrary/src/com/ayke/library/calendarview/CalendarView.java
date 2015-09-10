package com.ayke.library.calendarview;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;

import com.ayke.library.util.DateUtils;

public class CalendarView extends LinearLayout {

	/** The tag. */
	public String TAG = "CalendarView";

	/** The context. */
	private Context context;

	/** The m linear layout header. */
	private LinearLayout mHeaderLayout = null;

	/** The m calendar header. */
	private CalendarHeader mCalendarHeader = null;

	/** The m linear layout content. */
	private LinearLayout mContentLayout = null;

	/** 星期头的行高. */
	private int headerHeight = 80;

	/** The row height. */
	private int rowHeight = 100;

	/** 当前显示日历的第一天. */
	private static Calendar mStartDate = Calendar.getInstance();

	/** The Calendar selected. */
	private Calendar mSelectedDate = null;

	/** The current month. */
	private int currentMonth = 0;

	/** The current year. */
	private int currentYear = 0;

	// 当前显示的单元格
	/** The m calendar cells. */
	private ArrayList<CalendarCell> mCalendarCells = new ArrayList<CalendarCell>();

	/** The m on item click listener. */
	private OnItemClickListener mOnItemClickListener;

	public interface MeasureWidthListener {
		public void onMeasureResult(int width);
	}

	/**
	 * Instantiates a new ab grid view.
	 * 
	 * @param context
	 *            the context
	 */
	public CalendarView(Context context) {
		this(context, null);
	}

	/**
	 * Instantiates a new calendar view.
	 * 
	 * @param context
	 *            the context
	 * @param attrs
	 *            the attrs
	 */
	public CalendarView(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.context = context;

		this.setOrientation(LinearLayout.VERTICAL);
		this.setBackgroundColor(Color.rgb(255, 255, 255));

		mHeaderLayout = new LinearLayout(context);
		mHeaderLayout.setLayoutParams(new LayoutParams(
				LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
		mHeaderLayout.setOrientation(LinearLayout.VERTICAL);

		mCalendarHeader = new CalendarHeader(context);
		mCalendarHeader.setLayoutParams(new LayoutParams(
				LayoutParams.MATCH_PARENT, headerHeight));
		mCalendarHeader.setTextSize(headerHeight * 3 / 5);

		mHeaderLayout.addView(mCalendarHeader);

		mContentLayout = new LinearLayout(context);
		mContentLayout.setOrientation(LinearLayout.VERTICAL);

		addView(mHeaderLayout);
		addView(mContentLayout);

		mCalendarHeader.setOnMeasureWidthListener(new MeasureWidthListener() {
			@Override
			public void onMeasureResult(int width) {
				rowHeight = width / 7;
				rebuildCalendar(2015, 1);
			}
		});

		// 初始化选中今天
		mSelectedDate = Calendar.getInstance();
	}

	/**
	 * Rebuild calendar.
	 * 
	 * @param calendar
	 *            the calendar
	 */
	public void rebuildCalendar(int year, int month) {
		currentYear = year;
		currentMonth = month - 1;

		mStartDate.set(Calendar.YEAR, currentYear);
		mStartDate.set(Calendar.MONTH, currentMonth);
		mStartDate.set(Calendar.DAY_OF_MONTH, 1);
		mStartDate.set(Calendar.HOUR_OF_DAY, 0);
		mStartDate.set(Calendar.MINUTE, 0);
		mStartDate.set(Calendar.SECOND, 0);

		// 初始化选中1号
		mSelectedDate.setTimeInMillis(mStartDate.getTimeInMillis());

		int iDay = 0;
		int iStartDay = Calendar.SUNDAY;

		iDay = mStartDate.get(Calendar.DAY_OF_WEEK) - iStartDay;
		if (iDay < 0)
			iDay = 6;

		mStartDate.add(Calendar.DAY_OF_WEEK, -iDay);

		initRow();
		updateCalendar();
	}

	private void initRow() {
		mContentLayout.removeAllViews();
		mCalendarCells.clear();
		int row = getCalRowCount();
		for (int iRow = 0; iRow < row; iRow++) {
			LinearLayout mLinearLayoutRow = new LinearLayout(context);
			mLinearLayoutRow.setLayoutParams(new LayoutParams(
					LayoutParams.MATCH_PARENT, rowHeight));
			mLinearLayoutRow.setOrientation(LinearLayout.HORIZONTAL);
			for (int iDay = 0; iDay < 7; iDay++) {
				CalendarCell dayCell = new CalendarCell(context, rowHeight);
				dayCell.setOnClickListener(mOnDayCellClick);
				mLinearLayoutRow.addView(dayCell);
				mCalendarCells.add(dayCell);
			}
			mContentLayout.addView(mLinearLayoutRow);
		}
	}

	/**
	 * 描述：更新日历.
	 */
	private void updateCalendar() {
		final boolean bIsSelection = (mSelectedDate.getTimeInMillis() != 0);
		final int iSelectedYear = mSelectedDate.get(Calendar.YEAR);
		final int iSelectedMonth = mSelectedDate.get(Calendar.MONTH);
		final int iSelectedDay = mSelectedDate.get(Calendar.DAY_OF_MONTH);

		// 今天在当前月，则去掉默认选中的1号
		Calendar today = Calendar.getInstance();
		boolean isThisMonth = today.get(Calendar.YEAR) == iSelectedYear
				&& today.get(Calendar.MONTH) == iSelectedMonth;

		Calendar tempCalendar = Calendar.getInstance();
		tempCalendar.setTimeInMillis(mStartDate.getTimeInMillis());
		for (int i = 0; i < mCalendarCells.size(); i++) {
			CalendarCell dayCell = mCalendarCells.get(i);

			final int year = tempCalendar.get(Calendar.YEAR);
			final int month = tempCalendar.get(Calendar.MONTH);
			final int day = tempCalendar.get(Calendar.DAY_OF_MONTH);
			final int dayOfWeek = tempCalendar.get(Calendar.DAY_OF_WEEK);
			// 判断是否当天
			boolean isToday = false;
			// 是否被选中
			boolean isSelected = false;
			// check holiday
			boolean isHoliday = false;
			// 是否有记录
			boolean hasRecord = false;

			if (isThisMonth && today.get(Calendar.DAY_OF_MONTH) == day) {
				isToday = true;
			}

			if (dayOfWeek == Calendar.SATURDAY || dayOfWeek == Calendar.SUNDAY) {
				isHoliday = true;
			}

			if (bIsSelection) {
				if (iSelectedDay == day && iSelectedMonth == month
						&& iSelectedYear == year)
					isSelected = true;
			}
			if (day == 1 && isThisMonth) {
				isSelected = false;
			}

			dayCell.setThisCellDate(currentYear, month, day, isToday,
					isSelected, isHoliday, currentMonth, hasRecord);
			tempCalendar.add(Calendar.DAY_OF_MONTH, 1);
		}

		invalidate();
	}

	/**
	 * 描述：设置日历的行数
	 */
	private int getCalRowCount() {
		Calendar calendar = (Calendar) mSelectedDate.clone();
		calendar.set(Calendar.DAY_OF_MONTH, 1);
		int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
		int days = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
		if (dayOfWeek + days - 1 > 35) {
			return 6;
		} else {
			return 5;
		}
	}

	/**
	 * Sets the on item click listener.
	 * 
	 * @param listener
	 *            the new on item click listener
	 */
	public void setOnItemClickListener(OnItemClickListener listener) {
		this.mOnItemClickListener = listener;
	}

	/**
	 * Sets the header height.
	 * 
	 * @param height
	 *            the new header height
	 */
	public void setHeaderHeight(int height) {
		headerHeight = height;
		mHeaderLayout.removeAllViews();
		mCalendarHeader.setLayoutParams(new LayoutParams(
				LayoutParams.MATCH_PARENT, headerHeight));
		mHeaderLayout.addView(mCalendarHeader, new LayoutParams(
				LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));
		invalidate();
	}

	/**
	 * Sets the header text size.
	 * 
	 * @param size
	 *            the new header text size
	 */
	public void setHeaderTextSize(int size) {
		mCalendarHeader.setTextSize(size);
		this.invalidate();
	}

	/** 点击日历，触发事件. */
	private OnClickListener mOnDayCellClick = new OnClickListener() {
		@Override
		public void onClick(View v) {
			CalendarCell mCalendarCell = (CalendarCell) v;
			if (mCalendarCell.isActiveMonth()) {
				mSelectedDate.setTimeInMillis(mCalendarCell.getCellDate()
						.getTimeInMillis());
				for (int i = 0; i < mCalendarCells.size(); i++) {
					CalendarCell mCalendarCellOther = mCalendarCells.get(i);
					mCalendarCellOther.setSelected(false);
				}
				mCalendarCell.setSelected(true);
			}
			if (mOnItemClickListener != null)
				mOnItemClickListener.onClick(mCalendarCell.getCellDate());
		}
	};

	/**
	 * 描述：设置标题背景.
	 * 
	 * @param resid
	 *            the new header background resource
	 */
	public void setHeaderBackgroundResource(int resid) {
		mCalendarHeader.setHeaderBackgroundResource(resid);
	}

	/**
	 * 描述：根据索引获取选择的日期.
	 * 
	 * @param position
	 *            the position
	 * @return the str date at position
	 */
	public String getStrDateAtPosition(int position) {
		CalendarCell mCalendarCell = mCalendarCells.get(position);
		Calendar calendar = mCalendarCell.getCellDate();
		return DateUtils
				.convertDateToString(new Date(calendar.getTimeInMillis()));
		/*
		 * int year = calendar.get(Calendar.YEAR); int month =
		 * calendar.get(Calendar.MONTH) + 1; int day =
		 * calendar.get(Calendar.DATE); return year + "-" + month + "-" + day;
		 */
	}

	/**
	 * 描述：获取这个日历的总日期数.
	 * 
	 * @return the calendar cell size
	 */
	public int getCalendarCellSize() {
		return mCalendarCells.size();
	}

	/**
	 * 描述：获取当前日历的所有条目.
	 * 
	 * @return the calendar cells
	 */
	public ArrayList<CalendarCell> getCalendarCells() {
		return mCalendarCells;
	}

	/**
	 * 描述：获取选中的日期，默认为今天.
	 * 
	 * @return the cal selected
	 */
	public String getCalSelected() {
		final int iYear = mSelectedDate.get(Calendar.YEAR);
		final int iMonth = mSelectedDate.get(Calendar.MONTH) + 1;
		final int iDay = mSelectedDate.get(Calendar.DAY_OF_MONTH);
		return iYear + "-" + iMonth + "-" + iDay;
	}
}
