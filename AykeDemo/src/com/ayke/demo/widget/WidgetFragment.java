package com.ayke.demo.widget;

import android.widget.ArrayAdapter;

import com.ayke.demo.R;
import com.ayke.library.abstracts.IFragment;
import com.ayke.library.widget.Switcher;

public class WidgetFragment extends IFragment {

	@Override
	protected int getLayoutId() {
		return R.layout.fragment_toggle_button_layout;
	}

	@Override
	protected void initView() {
		String[] data0 = {"january", "february", "march", "april", "may",
			"june", "july", "august", "september", "october", "november",
			"december",};
		ArrayAdapter<String> adapter0 = new ArrayAdapter<String>(getActivity()
			, R.layout.simple_text_center_view, data0);
		final Switcher switcher0 = $(R.id.switcher0);
		switcher0.setAdapter(adapter0);
	}

	@Override
	protected void initData() {

	}

}
