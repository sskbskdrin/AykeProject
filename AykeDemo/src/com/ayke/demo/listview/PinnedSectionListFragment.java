package com.ayke.demo.listview;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;

import com.ayke.library.abstracts.IFragment;
import com.ayke.library.listview.pinnedsection.PinnedSectionListView;
import com.ayke.library.listview.pinnedsection.SectionAdapter;
import com.ayke.library.listview.pinnedsection.SectionItem;

import java.util.ArrayList;
import java.util.List;

public class PinnedSectionListFragment extends IFragment implements OnItemClickListener {

    private class ListAdapter extends SectionAdapter<Item> {

        private final int[] COLORS = new int[]{0xffff4444, 0xff99cc00, 0xffffbb33, 0xff33b5e5};

        public ListAdapter(Context context, List<Item> list) {
            super(context, list, android.R.layout.simple_list_item_1);
        }

        @Override
        public void convertItemView(View view, int position, Item item) {
            setText((TextView) view, item.text);
            setTextColor((TextView) view, Color.DKGRAY);
            view.setTag("" + position);
            setBackgroundColor(view, Color.WHITE);
        }

        @Override
        public void convertSectionView(View view, int position, Item item) {
            setBackgroundColor(view, COLORS[item.sectionPosition % COLORS.length]);
            setTextColor((TextView) view, Color.DKGRAY);
            setText((TextView) view, item.text);
            view.setTag("" + position);
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
    public View getContentView() {
        mListView = new PinnedSectionListView(getActivity(), null);
        mListView.setId(android.R.id.list);
        mListView.setOnItemClickListener(this);
        initializeHeaderAndFooter();
        initializeAdapter();
        return mListView;
    }

    public List<Item> generateDataset(char from, char to, boolean clear) {
        List<Item> mList = new ArrayList<>();
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
            TextView header1 = (TextView) inflater.inflate(android.R.layout.simple_list_item_1,
                    list, false);
            header1.setText("First header");
            list.addHeaderView(header1);

            TextView header2 = (TextView) inflater.inflate(android.R.layout.simple_list_item_1,
                    list, false);
            header2.setText("Second header");
            list.addHeaderView(header2);

            TextView footer = (TextView) inflater.inflate(android.R.layout.simple_list_item_1,
                    list, false);
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
        mListView.setAdapter(new ListAdapter(getActivity(), generateDataset('A', 'Z', true)));
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Item item = (Item) mListView.getAdapter().getItem(position);
        if (item != null) {
            showToast("Item " + position + ": " + item.text);
        } else {
            showToast("Item " + position);
        }
    }

}