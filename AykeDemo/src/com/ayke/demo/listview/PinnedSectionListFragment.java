package com.ayke.demo.listview;

import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.ayke.library.abstracts.IFragment;
import com.ayke.library.listview.pinnedsection.PinnedSectionListView;
import com.ayke.library.listview.pinnedsection.SectionAdapter;
import com.ayke.library.listview.pinnedsection.SectionItem;

public class PinnedSectionListFragment extends IFragment implements
	OnItemClickListener {

	private class ListAdapter extends SectionAdapter {

		private final int[] COLORS = new int[]{0xffff4444, 0xff99cc00,
			0xffffbb33, 0xff33b5e5};

		public ListAdapter(Context context, List<SectionItem> list) {
			super(context, list);

		}

		@Override
		public View getItemView(int position, View convertView, Object obj) {
			Item item = (Item) obj;
			if (convertView == null)
				convertView = View.inflate(mContext, android.R.layout
					.simple_list_item_1, null);
			((TextView) convertView).setText(item.text);
			((TextView) convertView).setTextColor(Color.DKGRAY);
			convertView.setTag("" + position);
			convertView.setBackgroundColor(Color.WHITE);
			return convertView;
		}

		@Override
		public View getSectionView(int position, View convertView, Object
			obj) {
			Item item = (Item) obj;
			if (convertView == null)
				convertView = View.inflate(mContext, android.R.layout
					.simple_list_item_1, null);
			convertView.setBackgroundColor(COLORS[item.sectionPosition %
				COLORS.length]);
			((TextView) convertView).setTextColor(Color.DKGRAY);
			((TextView) convertView).setText(item.text);
			convertView.setTag("" + position);
			return convertView;
		}

	}

	private class Item implements SectionItem {

		public int sectionPosition = 0;
		public int listPosition = 0;

		public boolean type;
		public final String text;

		public Item(boolean type, String text) {
			this.type = type;
			this.text = text;
		}

		@Override
		public String toString() {
			return text;
		}

		@Override
		public boolean isSection() {
			return type;
		}

		@Override
		public int getSectionPosition() {
			return sectionPosition;
		}

		@Override
		public int getlistPosition() {
			return listPosition;
		}
	}

	private boolean hasHeaderAndFooter = true;
	private boolean isFastScroll = true;
	private PinnedSectionListView mListView;

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
		mListView = new PinnedSectionListView(getActivity(), null);
		mListView.setId(android.R.id.list);
		initializeHeaderAndFooter();
		initializeAdapter();
		return mListView;
	}

	public List<SectionItem> generateDataset(char from, char to, boolean
		clear) {
		List<SectionItem> mList = new ArrayList<SectionItem>();
		if (clear) mList.clear();
		final int sectionsNumber = to - from + 1;
		// sections = new Item[sectionsNumber];
		int sectionPosition = 0, listPosition = 0;
		for (char i = 0; i < sectionsNumber; i++) {
			Item section = new Item(true, String.valueOf((char) ('A' + i)));
			section.sectionPosition = sectionPosition;
			section.listPosition = listPosition++;
			// sections[sectionPosition] = section;
			mList.add(section);
			for (int j = 0; j < 10; j++) {
				Item item = new Item(false, section.text + " - " + j);
				item.sectionPosition = sectionPosition;
				item.listPosition = listPosition++;
				mList.add(item);
			}
			sectionPosition++;
		}
		return mList;
	}

	private void initializeHeaderAndFooter() {
		mListView.setAdapter(null);
		if (hasHeaderAndFooter) {
			ListView list = mListView;

			LayoutInflater inflater = LayoutInflater.from(getActivity());
			TextView header1 = (TextView) inflater.inflate(android.R.layout
				.simple_list_item_1, list, false);
			header1.setText("First header");
			list.addHeaderView(header1);

			TextView header2 = (TextView) inflater.inflate(android.R.layout
				.simple_list_item_1, list, false);
			header2.setText("Second header");
			list.addHeaderView(header2);

			TextView footer = (TextView) inflater.inflate(android.R.layout
				.simple_list_item_1, list, false);
			footer.setText("Single footer");
			list.addFooterView(footer);
		}
		initializeAdapter();
	}

	@SuppressLint("NewApi")
	private void initializeAdapter() {
		mListView.setFastScrollEnabled(isFastScroll);
		if (isFastScroll) {
			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
				mListView.setFastScrollAlwaysVisible(true);
			}
		}
		mListView.setAdapter(new ListAdapter(getActivity(), generateDataset
			('A', 'Z', true)));
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
	                        long id) {
		Item item = (Item) mListView.getAdapter().getItem(position);
		if (item != null) {
			Toast.makeText(getActivity(), "Item " + position + ": " + item
				.text, Toast.LENGTH_SHORT).show();
		} else {
			Toast.makeText(getActivity(), "Item " + position, Toast
				.LENGTH_SHORT).show();
		}
	}

}