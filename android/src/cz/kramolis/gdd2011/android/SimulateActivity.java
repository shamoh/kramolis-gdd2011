package cz.kramolis.gdd2011.android;

import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Libor Kramolis
 */
public class SimulateActivity extends LaPardonActivity {

	private static final String TAG = "LaPardon.SimulateActivity";

	private SeekBar slider;
	private TextView sliderValue;
	private ListView journalList;

	private JournalAdapter journalAdapter;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		Log.d(TAG, "*** onCreate");

		setContentView(R.layout.simulate);
		setTitle(R.string.simulate);

		initListeners();

		journalAdapter = new JournalAdapter(this, R.layout.journalrow, getLastJournalItems());
		journalList = (ListView) findViewById(R.id.journalList);
		journalList.setAdapter(journalAdapter);
	}

	private List<LaPardonApplication.JournalItem> getLastJournalItems() {
		List<LaPardonApplication.JournalItem> lastItems = new ArrayList<LaPardonApplication.JournalItem>();
		LaPardonApplication app = (LaPardonApplication) this.getApplication();
		int i = 0;
		while (i < 333 && i < app.getJournal().size()) {
			lastItems.add(app.getJournal().get(i++));
		}
		return lastItems;
	}

	private void refresh() {
		journalAdapter.clear();
		journalAdapter.addAll(getLastJournalItems());
		journalAdapter.notifyDataSetChanged();
	}

	private void removeAll() {
		LaPardonApplication app = (LaPardonApplication) this.getApplication();
		app.getJournal().clear();
		journalAdapter.clear();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.simulatemenu, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		Log.d(TAG, "onOptionsItemSelected - " + item.getItemId());
		// Handle item selection
		switch (item.getItemId()) {
			case R.id.refresh:
				refresh();
				return true;
			case R.id.removeAll:
				removeAll();
				return true;
			default:
				return ActivityUtils.onOptionsItemSelected(this, item);
		}
	}

	private void initListeners() {
		slider = (SeekBar) findViewById(R.id.slider);
		sliderValue = (TextView) findViewById(R.id.sliderValue);

		Log.d(TAG, "slider.progress " + slider.getProgress());
		sliderValue.setText(String.valueOf(slider.getProgress()));

		slider.setOnSeekBarChangeListener(new MyListener());
	}


	//
	// class MyListener
	//

	private class MyListener implements SeekBar.OnSeekBarChangeListener {

		private static final long SEND_COMMAND_INTERVAL = 200;

		private long lastProgressChangedMillis = -1;
		private int lastProgress;

		@Override
		public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
			sliderValue.setText(String.valueOf(progress));

			if (System.currentTimeMillis() > (lastProgressChangedMillis + SEND_COMMAND_INTERVAL)) {
				Log.d(TAG, "Slider::onProgressChanged: " + progress + " - " + System.currentTimeMillis());

				sendCommandSimulate(progress);
			}
		}

		@Override
		public void onStartTrackingTouch(SeekBar seekBar) {
			Log.d(TAG, "Slider::onStartTrackingTouch: " + seekBar.getProgress());
		}

		@Override
		public void onStopTrackingTouch(SeekBar seekBar) {
			Log.d(TAG, "Slider::onStopTrackingTouch: " + seekBar.getProgress());

			sendCommandSimulate(seekBar.getProgress());
		}

		private void sendCommandSimulate(int progress) {
			if (lastProgress != progress) {
				SimulateActivity.this.sendCommandSimulate(progress);
			}
			lastProgress = progress;
			lastProgressChangedMillis = System.currentTimeMillis();
		}

	} // class MyListener

}
