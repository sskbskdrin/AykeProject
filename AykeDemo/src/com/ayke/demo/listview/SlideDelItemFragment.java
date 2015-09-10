/*
 * Copyright 2013 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.ayke.demo.listview;

import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.ayke.demo.R;
import com.ayke.library.abstracts.IFragment;
import com.ayke.library.listview.slidedelete.SlideDeleteListTouchListener;
import com.ayke.library.listview.slidedelete.SlideDeleteViewTouchListener;

import java.util.ArrayList;
import java.util.Arrays;

public class SlideDelItemFragment extends IFragment implements
	OnItemClickListener {
	ArrayAdapter<String> mAdapter;

	@Override
	protected int getLayoutId() {
		return R.layout.fragment_list_slide_delete_layout;
	}

	@Override
	protected void initView() {
		// Set up ListView example
		String[] items = new String[20];
		for (int i = 0; i < items.length; i++) {
			items[i] = "Item " + (i + 1);
		}

		mAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout
			.simple_list_item_1, android.R.id.text1, new ArrayList<String>
			(Arrays.asList(items)));

		ListView listView = $(android.R.id.list);
		listView.setAdapter(mAdapter);

		SlideDeleteListTouchListener touchListener = new
			SlideDeleteListTouchListener(listView, new
			SlideDeleteListTouchListener.DismissCallbacks() {
			@Override
			public boolean canDismiss(int position) {
				return true;
			}

			@Override
			public void onDismiss(ListView listView, int[]
				reverseSortedPositions) {
				for (int position : reverseSortedPositions) {
					mAdapter.remove(mAdapter.getItem(position));
				}
				mAdapter.notifyDataSetChanged();
			}
		});
		listView.setOnTouchListener(touchListener);
		listView.setOnScrollListener(touchListener.makeScrollListener());

		// Set up normal ViewGroup example
		final ViewGroup dismissContainer = $(R.id.dismissable_container);
		for (int i = 0; i < items.length; i++) {
			Button dismissButton = new Button(getActivity());
			dismissButton.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup
				.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams
				.WRAP_CONTENT));
			dismissButton.setText("Button " + (i + 1));
			dismissButton.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View view) {
					Toast.makeText(getActivity(), "Clicked " + ((Button) view)
						.getText(), Toast.LENGTH_SHORT).show();
				}
			});
			// Create a generic swipe-to-dismiss touch listener.
			dismissButton.setOnTouchListener(new SlideDeleteViewTouchListener
				(dismissButton, null, new SlideDeleteViewTouchListener
					.DismissCallbacks() {

				@Override
				public void onDismiss(View view, Object token) {
					dismissContainer.removeView(view);
				}

				@Override
				public boolean canDismiss(Object token) {
					return true;
				}
			}));
			dismissContainer.addView(dismissButton);
		}
	}

	@Override
	protected void initData() {

	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
	                        long id) {
		Toast.makeText(getActivity(), "Clicked " + parent.getAdapter().getItem
			(position).toString(), Toast.LENGTH_SHORT).show();
	}
}
