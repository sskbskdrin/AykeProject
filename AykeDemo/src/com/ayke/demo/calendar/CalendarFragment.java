package com.ayke.demo.calendar;

import java.util.Calendar;
import java.util.Date;

import com.ayke.demo.R;
import com.ayke.library.abstracts.IFragment;
import com.ayke.library.calendarview.CalendarView;
import com.ayke.library.calendarview.OnCellClickListener;
import com.ayke.library.util.DateUtils;
import com.ayke.library.util.ToastUtil;
import com.ayke.library.wheel.AbstractWheel;
import com.ayke.library.wheel.OnWheelChangedListener;
import com.ayke.library.wheel.OnWheelScrollListener;
import com.ayke.library.wheel.WheelHorizontalView;
import com.ayke.library.wheel.adapters.NumericWheelAdapter;

public class CalendarFragment extends IFragment {

	private CalendarView mCalendarView;
	private WheelHorizontalView yearSwitcher;
	private WheelHorizontalView monthSwitcher;

	private int year = 2015;
	private int month = 1;

	@Override
	protected int getLayoutId() {
		return R.layout.fragment_calendar_layout;
	}

	@Override
	protected void initView() {
		mCalendarView = $(R.id.calendarview);
		mCalendarView.setOnItemClickListener(new OnCellClickListener() {

			@Override
			public void onClick(Calendar calendar) {
				ToastUtil.show(getActivity(), DateUtils.convertDateToString
					(new Date(calendar.getTimeInMillis())));
			}
		});

		yearSwitcher = $(R.id.calendar_year);
		monthSwitcher = $(R.id.calendar_month);

		NumericWheelAdapter adapter = new NumericWheelAdapter(getActivity(),
			1980, 2100, "%4d");
		adapter.setItemResource(R.layout.item_wheel_text_dark);
		adapter.setItemTextResource(R.id.text);
		yearSwitcher.setViewAdapter(adapter);
		yearSwitcher.setCyclic(false);

		adapter = new NumericWheelAdapter(getActivity(), 1, 12, "%02d");
		adapter.setItemResource(R.layout.item_wheel_text_dark);
		adapter.setItemTextResource(R.id.text);
		monthSwitcher.setViewAdapter(adapter);
		monthSwitcher.setCyclic(true);

		yearSwitcher.addScrollingListener(mOnScrollListener);
		monthSwitcher.addScrollingListener(mOnScrollListener);
		monthSwitcher.addChangingListener(mOnChangedListener);

		yearSwitcher.setCurrentItem(35);
		monthSwitcher.setCurrentItem(0);
	}

	@Override
	protected void initData() {

	}

	private OnWheelScrollListener mOnScrollListener = new
		OnWheelScrollListener() {

		@Override
		public void onScrollingStarted(AbstractWheel wheel) {

		}

		@Override
		public void onScrollingFinished(AbstractWheel wheel) {
			if (yearSwitcher == wheel) {
				year = wheel.getCurrentItem() + 1980;
			} else {
				month = wheel.getCurrentItem() + 1;
			}
			mCalendarView.rebuildCalendar(year, month);
		}
	};

	private OnWheelChangedListener mOnChangedListener = new
		OnWheelChangedListener() {

		@Override
		public void onChanged(AbstractWheel wheel, int oldValue, int
			newValue) {
			if (oldValue == 11 && newValue == 0) {
				yearSwitcher.setCurrentItem(yearSwitcher.getCurrentItem() + 1,
					true);
			} else if (oldValue == 0 && newValue == 11) {
				yearSwitcher.setCurrentItem(yearSwitcher.getCurrentItem() - 1,
					true);
			}
		}
	};
}
