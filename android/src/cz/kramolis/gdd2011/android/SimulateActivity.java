package cz.kramolis.gdd2011.android;

import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.SeekBar;
import android.widget.TextView;

/**
 * @author Libor Kramolis
 * @todo
 */
public class SimulateActivity extends LaPardonActivity {

	private static final String TAG = "SimulateActivity";

	private SeekBar slider;
	private TextView sliderValue;


	@Override
	public void onCreate(Bundle savedInstanceState) {
		Log.d(TAG, "onCreate");

		super.onCreate(savedInstanceState);

		setContentView(R.layout.simulate);
		setTitle(R.string.simulate);

		initListeners();

		//TODO
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		Log.d(TAG, "onOptionsItemSelected - " + item.getItemId());
		// Handle item selection
		switch (item.getItemId()) {
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

		@Override
		public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
			Log.d(TAG, "Slider::onProgressChanged: " + i);
			sliderValue.setText(String.valueOf(i));
		}

		@Override
		public void onStartTrackingTouch(SeekBar seekBar) {
		}

		@Override
		public void onStopTrackingTouch(SeekBar seekBar) {
		}
	} // class MyListener

}
