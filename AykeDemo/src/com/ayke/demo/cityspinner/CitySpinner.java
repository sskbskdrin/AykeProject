package com.ayke.demo.cityspinner;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.ayke.demo.Entity.DictionaryEntity;
import com.ayke.library.abstracts.IFragment;
import com.ayke.library.util.ToastUtil;
import com.open.volley.Request;
import com.open.volley.VolleyError;
import com.open.volley.wrapper.Entity;
import com.open.volley.wrapper.IParseResponse;
import com.open.volley.wrapper.VolleyEncapsulation;
import com.open.volley.wrapper.VolleyJsonObjectRequest;

public class CitySpinner extends IFragment implements OnItemSelectedListener,
		VolleyJsonObjectRequest.IVolleyRequestListener<DictionaryEntity> {

	private static final String TAG = "CitySpinner";

	private DBManager dbm;
	private SQLiteDatabase db;
	private Spinner spinner1 = null;
	private Spinner spinner2 = null;
	private Spinner spinner3 = null;
	private String province = null;
	private String city = null;
	private String district = null;

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
		spinner1 = new Spinner(getActivity());
		spinner2 = new Spinner(getActivity());
		spinner3 = new Spinner(getActivity());
		LinearLayout ll = new LinearLayout(getActivity());
		ll.setOrientation(LinearLayout.VERTICAL);
		ll.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout
				.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams
				.WRAP_CONTENT));
		ll.addView(spinner1, new LinearLayout.LayoutParams(LinearLayout
				.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams
				.WRAP_CONTENT));
		ll.addView(spinner2, new LinearLayout.LayoutParams(LinearLayout
				.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams
				.WRAP_CONTENT));
		ll.addView(spinner3, new LinearLayout.LayoutParams(LinearLayout
				.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams
				.WRAP_CONTENT));
		spinner1.setPrompt("省");
		spinner2.setPrompt("城市");
		spinner3.setPrompt("地区");

		spinner1.setAdapter(new ListAdapter(getActivity(), getList(1, ""), 1));
		spinner2.setAdapter(new ListAdapter(getActivity(), new
				ArrayList<CityItem>(), 2));
		spinner3.setAdapter(new ListAdapter(getActivity(), new
				ArrayList<CityItem>(), 3));

		spinner1.setOnItemSelectedListener(this);
		spinner2.setOnItemSelectedListener(this);
		spinner3.setOnItemSelectedListener(this);
		return ll;
	}

	private List<CityItem> getList(int id, String pCode) {
		dbm = new DBManager(getActivity());
		dbm.openDatabase();
		db = dbm.getDatabase();
		List<CityItem> list = new ArrayList<CityItem>();

		try {
			String sql = "";
			if (id == 1) {
				sql = "select * from province";
			} else if (id == 2) {
				sql = "select * from city where pCode='" + pCode + "'";
			} else if (id == 3) {
				sql = "select * from district where pCode='" + pCode + "'";
			}
			Cursor cursor = db.rawQuery(sql, null);
			while (cursor.moveToNext()) {
				String code = cursor.getString(cursor.getColumnIndex("code"));
				byte bytes[] = cursor.getBlob(2);
				list.add(new CityItem(new String(bytes, "gbk"), code));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		dbm.closeDatabase();
		db.close();

		return list;
	}

	@Override
	public void onItemSelected(AdapterView<?> parent, View view, int position,
	                           long id) {
		ListAdapter adapter = (ListAdapter) parent.getAdapter();
		CityItem item = (CityItem) parent.getItemAtPosition(position);
		int adapterId = adapter.getId();
		Log.d(TAG, "spinner " + adapterId);
		if (adapterId == 1) {
			province = item.getName();
			spinner2.setAdapter(new ListAdapter(getActivity(), getList(2, item
					.getPcode()), 2));
		} else if (adapterId == 2) {
			city = item.getName();
			spinner3.setAdapter(new ListAdapter(getActivity(), getList(3, item
					.getPcode()), 3));
		} else if (adapterId == 3) {
			district = item.getName();
			ToastUtil.show(getActivity(), province + " " + city + " " + district);
		}
	}

	@Override
	public void onNothingSelected(AdapterView<?> parent) {

	}

	@Override
	public void onVolleySuccess(DictionaryEntity result, boolean isSuccess, int id) {
		ToastUtil.show(getActivity(), result.toString(), true);
	}

	@Override
	public void onVolleyError(VolleyError error, int id) {

	}

	private class CityItem {
		private String name;
		private String pcode;

		public CityItem(String name, String pcode) {
			this.name = name;
			this.pcode = pcode;
		}

		public String getName() {
			return name;
		}

		public String getPcode() {
			return pcode;
		}

	}

	private class ListAdapter extends BaseAdapter {

		private Context mContext;
		private List<CityItem> mList;
		private int id;

		public ListAdapter(Context context, List<CityItem> list, int id) {
			this.mContext = context;
			this.mList = list;
			this.id = id;
		}

		public int getCount() {
			return mList.size();
		}

		public int getId() {
			return id;
		}

		public CityItem getItem(int position) {
			return mList.get(position);
		}

		public long getItemId(int position) {
			return position;
		}

		public View getView(int position, View convertView, ViewGroup parent) {
			if (convertView == null) {
				convertView = View.inflate(mContext, android.R.layout
						.simple_list_item_1, null);
			}
			((TextView) convertView).setText(mList.get(position).getName());
			return convertView;
		}
	}

}