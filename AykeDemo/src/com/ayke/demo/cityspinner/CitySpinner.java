package com.ayke.demo.cityspinner;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.ayke.library.abstracts.IBaseAdapter;
import com.ayke.library.abstracts.IFragment;
import com.ayke.library.util.ToastUtil;

import java.util.ArrayList;
import java.util.List;

public class CitySpinner extends IFragment implements OnItemSelectedListener {

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
    protected View getContentView() {
        spinner1 = new Spinner(getActivity());
        spinner2 = new Spinner(getActivity());
        spinner3 = new Spinner(getActivity());
        LinearLayout ll = new LinearLayout(getActivity());
        ll.setOrientation(LinearLayout.VERTICAL);
        ll.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT));
        ll.addView(spinner1, new LinearLayout.LayoutParams(LinearLayout.LayoutParams
                .MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        ll.addView(spinner2, new LinearLayout.LayoutParams(LinearLayout.LayoutParams
                .MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        ll.addView(spinner3, new LinearLayout.LayoutParams(LinearLayout.LayoutParams
                .MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        spinner1.setPrompt("省");
        spinner2.setPrompt("城市");
        spinner3.setPrompt("地区");

        spinner1.setAdapter(new ListAdapter(getActivity(), getList(1, ""), 1));
        spinner2.setAdapter(new ListAdapter(getActivity(), new ArrayList<CityItem>(), 2));
        spinner3.setAdapter(new ListAdapter(getActivity(), new ArrayList<CityItem>(), 3));

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
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        ListAdapter adapter = (ListAdapter) parent.getAdapter();
        CityItem item = (CityItem) parent.getItemAtPosition(position);
        int adapterId = adapter.getId();
        Log.d(TAG, "spinner " + adapterId);
        if (adapterId == 1) {
            province = item.getName();
            spinner2.setAdapter(new ListAdapter(getActivity(), getList(2, item.getPcode()), 2));
        } else if (adapterId == 2) {
            city = item.getName();
            spinner3.setAdapter(new ListAdapter(getActivity(), getList(3, item.getPcode()), 3));
        } else if (adapterId == 3) {
            district = item.getName();
            ToastUtil.show(getActivity(), province + " " + city + " " + district);
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

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

    private class ListAdapter extends IBaseAdapter<CityItem> {

        private int id;

        public ListAdapter(Context context, List<CityItem> list, int id) {
            super(context, list, android.R.layout.simple_list_item_1);
            this.id = id;
        }

        @Override
        protected void convert(View view, int position, CityItem cityItem) {
            setText((TextView) view, cityItem.getName());
        }

        public int getId() {
            return id;
        }
    }

}