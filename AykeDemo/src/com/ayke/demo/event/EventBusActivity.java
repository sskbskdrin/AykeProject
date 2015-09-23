package com.ayke.demo.event;

import java.util.ArrayList;

import android.app.Activity;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.ayke.demo.R;
import com.open.event.EventBus;
import com.open.event.ThreadMode;

public class EventBusActivity extends Activity {

	@SuppressWarnings("rawtypes")
	static final Class[] TEST_CLASSES_EVENTBUS = { PerfTestEventBus.Post.class,//
			PerfTestEventBus.RegisterOneByOne.class,//
			PerfTestEventBus.RegisterAll.class, //
			PerfTestEventBus.RegisterFirstTime.class };
	private EventBus controlBus;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_event);
		controlBus = new EventBus();
		controlBus.register(this);

		Spinner spinnerRun = (Spinner) findViewById(R.id.spinnerTestToRun);
		spinnerRun
				.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

					public void onItemSelected(AdapterView<?> adapter, View v,
							int pos, long lng) {
						int eventsVisibility = pos == 0 ? View.VISIBLE
								: View.GONE;
						findViewById(R.id.relativeLayoutForEvents)
								.setVisibility(eventsVisibility);
						findViewById(R.id.spinnerThread).setVisibility(
								eventsVisibility);
					}

					public void onNothingSelected(AdapterView<?> arg0) {
					}
				});
		textViewResult = (TextView) findViewById(R.id.event_result);
	}

	TextView textViewResult;

	public void checkEventBus(View v) {
		Spinner spinnerThread = (Spinner) findViewById(R.id.spinnerThread);
		CheckBox checkBoxEventBus = (CheckBox) findViewById(R.id.checkBoxEventBus);
		int visibility = checkBoxEventBus.isChecked() ? View.VISIBLE
				: View.GONE;
		spinnerThread.setVisibility(visibility);
	}

	public void startClick(View v) {
		TestParams params = new TestParams();
		Spinner spinnerThread = (Spinner) findViewById(R.id.spinnerThread);
		String threadModeStr = spinnerThread.getSelectedItem().toString();
		ThreadMode threadMode = ThreadMode.valueOf(threadModeStr);
		params.setThreadMode(threadMode);

		params.setEventInheritance(((CheckBox) findViewById(R.id.checkBoxEventBusEventHierarchy))
				.isChecked());

		EditText editTextEvent = (EditText) findViewById(R.id.editTextEvent);
		params.setEventCount(Integer.parseInt(editTextEvent.getText()
				.toString()));

		EditText editTextSubscriber = (EditText) findViewById(R.id.editTextSubscribe);
		params.setSubscriberCount(Integer.parseInt(editTextSubscriber.getText()
				.toString()));

		Spinner spinnerTestToRun = (Spinner) findViewById(R.id.spinnerTestToRun);
		int testPos = spinnerTestToRun.getSelectedItemPosition();
		params.setTestNumber(testPos + 1);
		ArrayList<Class<? extends Test>> testClasses = initTestClasses(testPos);
		params.setTestClasses(testClasses);

		run(params);
	}

	private TestRunner testRunner;

	private void run(TestParams testParams) {
		if (testRunner == null) {
			testRunner = new TestRunner(getApplicationContext(), testParams,
					controlBus);

			if (testParams.getTestNumber() == 1) {
				textViewResult.append("Events: " + testParams.getEventCount()
						+ "\n");
			}
			textViewResult.append("Subscribers: "
					+ testParams.getSubscriberCount() + "\n\n");
			testRunner.start();
		}
	}

	public void onEventMainThread(TestFinishedEvent event) {
		Test test = event.test;
		String text = "<b>" + test.getDisplayName() + "</b><br/>" + //
				test.getPrimaryResultMicros() + " micro seconds<br/>" + //
				((int) test.getPrimaryResultRate()) + "/s<br/>";
		if (test.getOtherTestResults() != null) {
			text += test.getOtherTestResults();
		}
		text += "<br/>----------------<br/>";
		textViewResult.append(Html.fromHtml(text));
		// if (event.isLastEvent) {
		// findViewById(R.id.buttonCancel).setVisibility(View.GONE);
		// findViewById(R.id.textViewTestRunning).setVisibility(View.GONE);
		// findViewById(R.id.buttonKillProcess).setVisibility(View.VISIBLE);
		// }
		if (testRunner != null) {
			testRunner = null;
		}
	}

	@SuppressWarnings("unchecked")
	private ArrayList<Class<? extends Test>> initTestClasses(int testPos) {
		ArrayList<Class<? extends Test>> testClasses = new ArrayList<Class<? extends Test>>();
		// the attributes are putted in the intent (eventbus, otto, broadcast,
		// local broadcast)
		final CheckBox checkBoxEventBus = (CheckBox) findViewById(R.id.checkBoxEventBus);
		final CheckBox checkBoxBroadcast = (CheckBox) findViewById(R.id.checkBoxBroadcast);
		final CheckBox checkBoxLocalBroadcast = (CheckBox) findViewById(R.id.checkBoxLocalBroadcast);
		if (checkBoxEventBus.isChecked()) {
			testClasses.add(TEST_CLASSES_EVENTBUS[testPos]);
		}
		// if (checkBoxOtto.isChecked()) {
		// testClasses.add(TEST_CLASSES_OTTO[testPos]);
		// }
		if (checkBoxBroadcast.isChecked()) {
		}
		if (checkBoxLocalBroadcast.isChecked()) {
		}

		return testClasses;
	}
}