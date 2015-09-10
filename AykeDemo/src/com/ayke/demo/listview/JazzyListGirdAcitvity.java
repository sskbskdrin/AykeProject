package com.ayke.demo.listview;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.ayke.demo.R;
import com.ayke.library.listview.jazzy.JazzyGridView;
import com.ayke.library.listview.jazzy.JazzyHelper;
import com.ayke.library.listview.jazzy.JazzyListView;

public class JazzyListGirdAcitvity extends Activity {

	private static final String KEY_TRANSITION_EFFECT = "transition_effect";

	private JazzyListView mList;
	private JazzyGridView mGrid;
	private HashMap<String, Integer> mEffectMap;
	private int mCurrentTransitionEffect = JazzyHelper.HELIX;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_list_jazzy);
		mList = (JazzyListView) findViewById(R.id.activity_list_jazzy_list);
		mList.setAdapter(new ListAdapter(this));
		mGrid = (JazzyGridView) findViewById(R.id.activity_list_jazzy_grid);
		mGrid.setAdapter(new ListAdapter(this));

		if (savedInstanceState != null) {
			mCurrentTransitionEffect = savedInstanceState.getInt(
					KEY_TRANSITION_EFFECT, JazzyHelper.HELIX);
			setupJazziness(mCurrentTransitionEffect);
		}
	}

	private String[] effects = new String[] { "standard", "Grow", "Cards",
			"Curl", "Wave", "Flip", "Fly", "ReverseFly", "Helix", "Fan",
			"Tilt", "Zipper", "Fade", "Twirl", "SlideIn" };

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		mEffectMap = new HashMap<String, Integer>();
		int i = 0;
		for (String effect : effects) {
			mEffectMap.put(effect, i++);
		}

		List<String> effectList = new ArrayList<String>(Arrays.asList(effects));
		Collections.sort(effectList);
		effectList.remove("standard");
		effectList.add(0, "standard");
		for (String effect : effectList) {
			menu.add(effect);
		}

		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		String strEffect = item.getTitle().toString();
		Toast.makeText(this, strEffect, Toast.LENGTH_SHORT).show();
		setupJazziness(mEffectMap.get(strEffect));
		return true;
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putInt(KEY_TRANSITION_EFFECT, mCurrentTransitionEffect);
	}

	private void setupJazziness(int effect) {
		mCurrentTransitionEffect = effect;
		mGrid.setTransitionEffect(mCurrentTransitionEffect);
		mList.setTransitionEffect(mCurrentTransitionEffect);
	}

	class ListAdapter extends ArrayAdapter<String> {

		public ListAdapter(Context context) {
			super(context, android.R.layout.simple_list_item_1,
					android.R.id.text1, new ListModel().getModel());
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder holder;
			if (convertView == null) {
				convertView = View.inflate(getContext(),
						android.R.layout.simple_list_item_1, null);
				holder = new ViewHolder(convertView);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}

			int color = position % 2 == 0 ? 0xffFFFFFF : 0xffeeeeee;
			holder.text.setBackgroundColor(color);
			holder.text.setText(new ListModel().getModelItem(position));

			return convertView;
		}

		class ViewHolder {
			final TextView text;

			ViewHolder(View view) {
				text = (TextView) view.findViewById(android.R.id.text1);
			}
		}
	}

	class ListModel {

		private final String[] MODEL = { "Zero", "One", "Two", "Three", "Four",
				"Five", "Six", "Seven", "Eight", "Nine", "Ten", "Eleven",
				"Twelve", "Thirteen", "Fourteen", "Fifteen", "Sixteen",
				"Seventeen", "Eighteen", "Nineteen", "Twenty", "Twentyone",
				"Twentytwo", "Twentythree", "Twentyfour", "Twentyfive",
				"Twentysix", "Twentyseven", "Twentyeight", "Twentynine",
				"Thirty", "Thirtyone", "Thirtytwo", "Thirtythree",
				"Thirtyfour", "Thirtyfive", "Thirtysix", "Thirtyseven",
				"Thirtyeight", "Thirtynine", "Forty", "Fortyone", "Fortytwo",
				"Fortythree", "Fortyfour", "Fortyfive", "Fortysix",
				"Fortyseven", "Fortyeight", "Fortynine", "Fifty", "Fiftyone",
				"Fiftytwo", "Fiftythree", "Fiftyfour", "Fiftyfive", "Fiftysix",
				"Fiftyseven", "Fiftyeight", "Fiftynine", "Sixty", "Sixtyone",
				"Sixtytwo", "Sixtythree", "Sixtyfour", "Sixtyfive", "Sixtysix",
				"Sixtyseven", "Sixtyeight", "Sixtynine", "Seventy",
				"Seventyone", "Seventytwo", "Seventythree", "Seventyfour",
				"Seventyfive", "Seventysix", "Seventyseven", "Seventyeight",
				"Seventynine", "Eighty", "Eightyone", "Eightytwo",
				"Eightythree", "Eightyfour", "Eightyfive", "Eightysix",
				"Eightyseven", "Eightyeight", "Eightynine", "Ninety",
				"Ninetyone", "Ninetytwo", "Ninetythree", "Ninetyfour",
				"Ninetyfive", "Ninetysix", "Ninetyseven", "Ninetyeight",
				"Ninetynine" };

		public String[] getModel() {
			return MODEL;
		}

		public String getModelItem(int position) {
			return MODEL[position];
		}

	}
}
