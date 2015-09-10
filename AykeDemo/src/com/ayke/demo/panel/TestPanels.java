package com.ayke.demo.panel;

import android.util.Log;

import com.ayke.demo.R;
import com.ayke.library.abstracts.IFragment;
import com.ayke.library.interpolator.BackInterpolator;
import com.ayke.library.interpolator.BounceInterpolator;
import com.ayke.library.interpolator.EasingType.Type;
import com.ayke.library.interpolator.ElasticInterpolator;
import com.ayke.library.interpolator.ExpoInterpolator;
import com.ayke.library.widget.Panel;
import com.ayke.library.widget.Panel.OnPanelListener;

public class TestPanels extends IFragment implements OnPanelListener {

	@Override
	protected int getLayoutId() {
		return R.layout.fragment_panel_layout;
	}

	@Override
	protected void initView() {
		Panel panel;

		panel = $(R.id.topPanel);
		panel.setOnPanelListener(this);
		panel.setInterpolator(new BounceInterpolator(Type.OUT));

		panel = $(R.id.leftPanel1);
		panel.setOnPanelListener(this);
		panel.setInterpolator(new BackInterpolator(Type.OUT, 2));

		panel = $(R.id.leftPanel2);
		panel.setOnPanelListener(this);
		panel.setInterpolator(new BackInterpolator(Type.OUT, 2));

		panel = $(R.id.rightPanel);
		panel.setOnPanelListener(this);
		panel.setInterpolator(new ExpoInterpolator(Type.OUT));

		panel = $(R.id.bottomPanel);
		panel.setOnPanelListener(this);
		panel.setInterpolator(new ElasticInterpolator(Type.OUT, 1.0f, 0.3f));

	}

	@Override
	protected void initData() {

	}

//    @Override
//    public boolean onKeyDown(int keyCode, KeyEvent event) {
//    	if (keyCode == KeyEvent.KEYCODE_T) {
//    		topPanel.setOpen(!topPanel.isOpen(), false);
//    		return false;
//    	}
//    	if (keyCode == KeyEvent.KEYCODE_B) {
//    		bottomPanel.setOpen(!bottomPanel.isOpen(), true);
//    		return false;
//    	}
//    	return super.onKeyDown(keyCode, event);
//    }

	public void onPanelClosed(Panel panel) {
		String panelName = getResources().getResourceEntryName(panel.getId());
		Log.d("TestPanels", "Panel [" + panelName + "] closed");
	}

	public void onPanelOpened(Panel panel) {
		String panelName = getResources().getResourceEntryName(panel.getId());
		Log.d("TestPanels", "Panel [" + panelName + "] opened");
	}
}
