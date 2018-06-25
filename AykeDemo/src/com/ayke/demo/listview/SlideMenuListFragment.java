package com.ayke.demo.listview;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.util.TypedValue;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.TextView;

import com.ayke.library.abstracts.IBaseAdapter;
import com.ayke.library.abstracts.IFragment;
import com.ayke.library.listview.slidemenu.SwipeMenu;
import com.ayke.library.listview.slidemenu.SwipeMenuCreator;
import com.ayke.library.listview.slidemenu.SwipeMenuItem;
import com.ayke.library.listview.slidemenu.SwipeMenuListView;
import com.ayke.library.listview.slidemenu.SwipeMenuListView.OnMenuItemClickListener;
import com.ayke.library.listview.slidemenu.SwipeMenuListView.OnSwipeListener;
import com.ayke.library.util.ToastUtil;

import java.util.List;

public class SlideMenuListFragment extends IFragment {

    private List<ApplicationInfo> mAppList;

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
    public View getContentView() {
        mAppList = getActivity().getPackageManager().getInstalledApplications(0);

        SwipeMenuListView listView = new SwipeMenuListView(getActivity());
        AppAdapter mAdapter = new AppAdapter(getActivity(), mAppList);
        listView.setAdapter(mAdapter);

        // step 1. create a MenuCreator
        SwipeMenuCreator creator = new SwipeMenuCreator() {

            @Override
            public void create(SwipeMenu menu) {
                // Create different menus depending on the view type
                switch (menu.getViewType()) {
                    case 0:
                        createMenu1(menu);
                        break;
                    case 1:
                        createMenu2(menu);
                        break;
                    case 2:
                        createMenu3(menu);
                        break;
                }
            }

            private void createMenu1(SwipeMenu menu) {
                SwipeMenuItem item1 = new SwipeMenuItem(getActivity());
                item1.setBackground(new ColorDrawable(Color.rgb(0xE5, 0x18, 0x5E)));
                item1.setWidth(dp2px(90));
                item1.setIcon(android.R.drawable.ic_menu_add);
                menu.addMenuItem(item1);
                SwipeMenuItem item2 = new SwipeMenuItem(getActivity());
                item2.setBackground(new ColorDrawable(Color.rgb(0xC9, 0xC9, 0xCE)));
                item2.setWidth(dp2px(90));
                item2.setIcon(android.R.drawable.ic_menu_delete);
                menu.addMenuItem(item2);
            }

            private void createMenu2(SwipeMenu menu) {
                SwipeMenuItem item1 = new SwipeMenuItem(getActivity());
                item1.setBackground(new ColorDrawable(Color.rgb(0xE5, 0xE0, 0x3F)));
                item1.setWidth(dp2px(90));
                item1.setIcon(android.R.drawable.ic_menu_call);
                menu.addMenuItem(item1);
                SwipeMenuItem item2 = new SwipeMenuItem(getActivity());
                item2.setBackground(new ColorDrawable(Color.rgb(0xF9, 0x3F, 0x25)));
                item2.setWidth(dp2px(90));
                item2.setIcon(android.R.drawable.ic_menu_compass);
                menu.addMenuItem(item2);
            }

            private void createMenu3(SwipeMenu menu) {
                SwipeMenuItem item1 = new SwipeMenuItem(getActivity());
                item1.setBackground(new ColorDrawable(Color.rgb(0x30, 0xB1, 0xF5)));
                item1.setWidth(dp2px(90));
                item1.setIcon(android.R.drawable.ic_menu_directions);
                menu.addMenuItem(item1);
                SwipeMenuItem item2 = new SwipeMenuItem(getActivity());
                item2.setBackground(new ColorDrawable(Color.rgb(0xC9, 0xC9, 0xCE)));
                item2.setWidth(dp2px(90));
                item2.setIcon(android.R.drawable.ic_menu_share);
                menu.addMenuItem(item2);
            }
        };
        // set creator
        listView.setMenuCreator(creator);

        // step 2. listener item click event
        listView.setOnMenuItemClickListener(new OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(int position, SwipeMenu menu, int index) {
                ApplicationInfo item = mAppList.get(position);
                ToastUtil.show(getActivity(), item.loadLabel(getActivity().getPackageManager()) +
                        " menu " + index);
                return false;
            }
        });
        listView.setOnSwipeListener(new OnSwipeListener() {

            @Override
            public void onSwipeStart(int position) {
            }

            @Override
            public void onSwipeEnd(int position) {
            }
        });
        listView.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ToastUtil.show(getActivity(), "onItemClick " + position);
            }
        });
        return listView;
    }

    class AppAdapter extends IBaseAdapter<ApplicationInfo> {

        public AppAdapter(Context context, List<ApplicationInfo> list) {
            super(context, list, android.R.layout.simple_list_item_1);
        }

        @Override
        public int getViewTypeCount() {
            return 3;
        }

        @Override
        public int getItemViewType(int position) {
            return position % 3;
        }

        @Override
        protected void convert(View view, int position, ApplicationInfo info) {
            setTextColor((TextView) view, Color.GRAY);
            setText((TextView) view, info.loadLabel(getActivity().getPackageManager()));
        }
    }

    private int dp2px(int dp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, getResources()
                .getDisplayMetrics());
    }
}
