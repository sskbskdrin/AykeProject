package com.ayke.demo.wheel;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ayke.demo.R;
import com.ayke.library.abstracts.IFragment;
import com.ayke.library.wheel.AbstractWheel;
import com.ayke.library.wheel.adapters.AbstractWheelTextAdapter;
import com.ayke.library.wheel.adapters.ArrayWheelAdapter;
import com.ayke.library.wheel.adapters.NumericWheelAdapter;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class CustomWheelFragment extends IFragment {

	@Override
	protected int getLayoutId() {
		return R.layout.fragment_wheel_custom_layout;
	}

	@Override
	protected void initView() {

		final AbstractWheel hours = getView(R.id.hour_horizontal);
		NumericWheelAdapter hourAdapter = new NumericWheelAdapter(getActivity
			(), 0, 23, "%02d");
		hourAdapter.setItemResource(R.layout.item_wheel_text_dark);
		hourAdapter.setItemTextResource(R.id.text);
		hours.setViewAdapter(hourAdapter);

		final AbstractWheel mins = getView(R.id.mins);
		NumericWheelAdapter minAdapter = new NumericWheelAdapter(getActivity()
			, 0, 59, "%02d");
		minAdapter.setItemResource(R.layout.item_wheel_text_dark);
		minAdapter.setItemTextResource(R.id.text);
		mins.setViewAdapter(minAdapter);

		final AbstractWheel ampm = getView(R.id.ampm);
		ArrayWheelAdapter<String> ampmAdapter = new ArrayWheelAdapter<String>
			(getActivity(), new String[]{"01", "02", "03"});
		ampmAdapter.setItemResource(R.layout.item_wheel_text_dark);
		ampmAdapter.setItemTextResource(R.id.text);
		ampm.setViewAdapter(ampmAdapter);

		// set current time
		Calendar calendar = Calendar.getInstance(Locale.US);
		hours.setCurrentItem(calendar.get(Calendar.HOUR_OF_DAY));
		mins.setCurrentItem(calendar.get(Calendar.MINUTE));
		ampm.setCurrentItem(calendar.get(Calendar.AM_PM));

		final AbstractWheel day = getView(R.id.day);
		DayArrayAdapter dayAdapter = new DayArrayAdapter(getActivity(),
			calendar);
		day.setViewAdapter(dayAdapter);
		day.setCurrentItem(dayAdapter.getToday());
	}

	@Override
	protected void initData() {

	}

	/**
	 * Day adapter
	 */
	private class DayArrayAdapter extends AbstractWheelTextAdapter {
		// Count of days to be shown
		private final int daysCount = 4;

		// Calendar
		Calendar calendar;

		/**
		 * Constructor
		 */
		protected DayArrayAdapter(Context context, Calendar calendar) {
			super(context, R.layout.item_time_picker_day, NO_RESOURCE);
			this.calendar = calendar;

			setItemTextResource(R.id.time2_monthday);
		}

		public int getToday() {
			return daysCount / 2;
		}

		@SuppressLint("SimpleDateFormat")
		@Override
		public View getItem(int index, View cachedView, ViewGroup parent) {
			int day = -daysCount / 2 + index;
			Calendar newCalendar = (Calendar) calendar.clone();
			newCalendar.roll(Calendar.DAY_OF_YEAR, day);

			View view = super.getItem(index, cachedView, parent);

			TextView weekday = (TextView) getView(R.id.time2_weekday);
			if (day == 0) {
				weekday.setText("");
			} else {
				DateFormat format = new SimpleDateFormat("EEE");
				weekday.setText(format.format(newCalendar.getTime()));
			}

			TextView monthday = (TextView) getView(R.id.time2_monthday);
			if (day == 0) {
				monthday.setText("Today");
				monthday.setTextColor(0xFF0000F0);
			} else {
				DateFormat format = new SimpleDateFormat("MMM d");
				monthday.setText(format.format(newCalendar.getTime()));
				monthday.setTextColor(0xFF111111);
			}
			DateFormat dFormat = new SimpleDateFormat("MMM d");
			view.setTag(dFormat.format(newCalendar.getTime()));
			return view;
		}

		@Override
		public int getItemsCount() {
			return daysCount + 1;
		}

		@Override
		protected CharSequence getItemText(int index) {
			return "";
		}
	}
}
