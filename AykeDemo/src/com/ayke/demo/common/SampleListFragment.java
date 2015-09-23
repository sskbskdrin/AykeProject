package com.ayke.demo.common;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;

import com.ayke.demo.R;
import com.ayke.library.abstracts.IBaseAdapter;
import com.ayke.library.abstracts.IFragment;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

public class SampleListFragment extends IFragment implements OnItemClickListener {

	public static final String LIST_DATA = "list_data";
	public static final String LIST_ADAPTER = "list_adapter";
	private ListView mListView;
	private IBaseAdapter<Object> mListAdapter;
	private OnItemClickListener mOnItemClickListener;

	@Override
	protected int getLayoutId() {
		return R.layout.simple_list_view;
	}

	@Override
	protected void initView() {
		mListView = (ListView) getRootView();
		mListView.setOnItemClickListener(this);
	}

	@Override
	protected void initData() {
		if (mBundle != null) {
			List<Object> list = (List<Object>) mBundle.getSerializable(LIST_DATA);
			String adapterName = mBundle.getString(LIST_ADAPTER);
			if (list != null) {
				if (TextUtils.isEmpty(adapterName)) {
					mListAdapter = new ListAdapter(getActivity(), list);
				} else {
					try {
						Class c = Class.forName(adapterName);
						c.getMethod(adapterName, new Class[]{Context.class, List.class}).invoke(this, new Object[]{getActivity(), list});
						mListAdapter = (ListAdapter) Class.forName(adapterName)
								.newInstance();
						mListAdapter.updateList(list);
					} catch (java.lang.InstantiationException e) {
						e.printStackTrace();
						mListAdapter = new ListAdapter(getActivity(), list);
					} catch (IllegalAccessException e) {
						e.printStackTrace();
						mListAdapter = new ListAdapter(getActivity(), list);
					} catch (ClassNotFoundException e) {
						e.printStackTrace();
						mListAdapter = new ListAdapter(getActivity(), list);
					} catch (NoSuchMethodException e) {
						e.printStackTrace();
						mListAdapter = new ListAdapter(getActivity(), list);
					} catch (InvocationTargetException e) {
						e.printStackTrace();
						mListAdapter = new ListAdapter(getActivity(), list);
					}
				}
				mListView.setAdapter(mListAdapter);
				return;
			}
		}
		List<Object> list = new ArrayList<Object>();
		for (int i = 0; i < 20; i++) {
			list.add("Simple item +" + i);
		}
		mListAdapter = new ListAdapter(getActivity(), list);
		mListView.setAdapter(mListAdapter);
	}

	public void setOnItemClickListener(OnItemClickListener listener) {
		mOnItemClickListener = listener;
	}

	@Override
	public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
		if (mOnItemClickListener != null) {
			mOnItemClickListener.onItemClick(adapterView, view, position, id);
		}
	}

	public class ListAdapter extends IBaseAdapter<Object> {

		protected ListAdapter(Context context, List<Object> list) {
			super(context, list);
		}

		public View getView(int position, View convertView, ViewGroup parent) {
			if (convertView == null) {
				convertView = LayoutInflater.from(mContext).inflate(R.layout
						.simple_text_center_view, null);
			}
			((TextView) convertView).setText(getItem(position).toString());
			return convertView;
		}
	}
}
