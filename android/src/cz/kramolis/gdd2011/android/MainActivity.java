package cz.kramolis.gdd2011.android;

import android.app.Activity;
import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.*;
import android.widget.AdapterView;
import twitter4j.Tweet;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Libor Kramolis
 * @todo
 */
public class MainActivity extends Activity {

	private static final String TAG = "MainActivity";

	@Override
	public void onCreate(Bundle savedInstanceState) {
		Log.d(TAG, "onCreate");

		super.onCreate(savedInstanceState);

		setContentView(R.layout.borderlayout);
		setTitle(R.string.main);

		//TODO
	}

	//
	// options
	//

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.mainmenu, menu);
		return true;
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
