package com.ayke.demo.common;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import com.ayke.library.abstracts.IBaseAdapter;

import java.util.List;

public class SimpleAdapter<T> extends IBaseAdapter<T> {
    public SimpleAdapter(Context context, List<T> list) {
        super(context, list, android.R.layout.simple_list_item_1);
    }

    @Override
    protected void convert(View view, int position, T t) {
        setText((TextView) view, t.toString());
    }
}
