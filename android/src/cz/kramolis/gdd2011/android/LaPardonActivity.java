package cz.kramolis.gdd2011.android;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;
import com.android.future.usb.UsbAccessory;

/**
 * @author Libor Kramolis
 */
public abstract class LaPardonActivity extends Activity {

	private static final String TAG = "LaPardon.LaPardonActivity";

	private AccessoryAdapter accessoryAdapter;


	/**
	 * Called when the activity is first created.
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		Log.d(TAG, "*** onCreate");

		this.accessoryAdapter = new AccessoryAdapter(this);
		{
			UsbAccessory accessory = (UsbAccessory) getLastNonConfigurationInstance();
			accessoryAdapter.onCreate(accessory);

			enableControls(false);
		}
	}

	//
	// options
	//

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		Log.d(TAG, "onOptionsItemSelected - " + item.getItemId());
		// Handle item selection
		switch (item.getItemId()) {
			default:
				return ActivityUtils.onOptionsItemSelected(this, item);
		}
	}

	//
	// COPIED
	//

	@Override
	public Object onRetainNonConfigurationInstance() {
		if (accessoryAdapter.getAccessory() != null) {
			return accessoryAdapter.getAccessory();
		} else {
			return super.onRetainNonConfigurationInstance();
		}
	}

	@Override
	public void onResume() {
		super.onResume();

		Log.d(TAG, "*** onResume");

		accessoryAdapter.onResume();
	}

	@Override
	public void onPause() {
		super.onPause();

		Log.d(TAG, "*** onPause");

		accessoryAdapter.onPause();
	}

	@Override
	public void onDestroy() {
		Log.d(TAG, "*** onDestroy");

		accessoryAdapter.onDestroy();

		super.onDestroy();
	}

	protected void enableControls(boolean enable) {
		if (enable) {
			showControls();
		} else {
			hideControls();
		}
	}

	//
	// dalsi kopie
	//

	protected void hideControls() {
		Log.d(TAG, "hideControls");

//		mInputController = null;

//		Toast toast = Toast.makeText(this, R.string.no_arduino_text, Toast.LENGTH_LONG);
//		toast.show();
	}

	protected void showControls() {
		Log.d(TAG, "showControls");
		setContentView(R.layout.main);

//		mInputController = new InputController(this);
//		mInputController.accessoryAttached();

		Toast toast = Toast.makeText(this, R.string.arduino_text, Toast.LENGTH_SHORT);
		toast.show();
	}

	//
	// accessory
	//

	protected void sendCommandSimulate(int value) {
		accessoryAdapter.sendCommandSimulate(value);
	}

}
