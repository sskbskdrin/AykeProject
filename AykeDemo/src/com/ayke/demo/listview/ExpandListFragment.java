package com.ayke.demo.listview;

import android.content.Context;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListAdapter;
import android.widget.TextView;

import com.ayke.demo.R;
import com.ayke.library.abstracts.IFragment;
import com.ayke.library.listview.expandable.ExpandBaseAdapter;
import com.ayke.library.listview.expandable.ExpandListView;
import com.ayke.library.util.ToastUtil;

import java.util.ArrayList;
import java.util.List;

public class ExpandListFragment extends IFragment {

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
		ExpandListView list = new ExpandListView(getActivity());

		List<String> list1 = new ArrayList<String>();
		for (int i = 0; i < 20; i++) {
			list1.add("Item " + i);
		}
		list.setAdapter(new ExpandListAdapter(getActivity(), list1));

		list.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int
				position, long id) {
				ToastUtil.show(getActivity(), " list item " + position);
			}
		});
		return list;
	}

	public ListAdapter buildDummyData() {
		final int SIZE = 20;
		List<String> list = new ArrayList<String>();
		for (int i = 0; i < SIZE; i++) {
			list.add("Item " + i);
		}
		return new ExpandListAdapter(getActivity(), list);
	}

	private class ExpandListAdapter extends ExpandBaseAdapter {
		private List<String> mList;
		private Context mContext;

		public ExpandListAdapter(Context context, List<String> list) {
			mContext = context;
			mList = list;
		}

		@Override
		public View getView(final int position, View view, ViewGroup
			viewGroup) {
			TextView tv = null;
			if (view == null) {
				view = View.inflate(mContext, R.layout
					.fragment_list_expand_item, null);
				tv = (TextView) view.findViewById(R.id.text);
				((TextView) view.findViewById(R.id.buttonA)).setText("action "
					+ "A");
				((TextView) view.findViewById(R.id.buttonB)).setText("action "
					+ "B");
				((TextView) view.findViewById(R.id.expandable_toggle_button))
					.setText("more");
			}
			tv = (TextView) view.findViewById(R.id.text);
			tv.setText(mList.get(position));
			view.findViewById(R.id.buttonA).setOnClickListener(new
				                                                   OnClickListener() {

				@Override
				public void onClick(View v) {
					ToastUtil.show(getActivity(), " list item buttonA" +
						position);
				}
			});
			return super.getView(position, view, viewGroup);
		}

		@Override
		public int getCount() {
			return mList.size();
		}

		@Override
		public Object getItem(int position) {
			return mList.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public int[] getExpandToggleViewsID() {
			return new int[]{R.id.expandable_toggle_button, R.id.buttonB};
		}

		@Override
		public int getExpandViewId() {
			return R.id.expandable;
		}
	}

}
