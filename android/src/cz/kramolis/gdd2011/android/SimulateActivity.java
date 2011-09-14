package cz.kramolis.gdd2011.android;

import android.app.ListActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import twitter4j.Tweet;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Libor Kramolis
 * @todo
 */
public class SimulateActivity extends ListActivity {

	private static final String TAG = "SimulateActivity";

	@Override
	public void onCreate(Bundle savedInstanceState) {
		Log.d(TAG, "onCreate");

		super.onCreate(savedInstanceState);

		setContentView(R.layout.simulate);
		setTitle(R.string.simulate);

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

}
