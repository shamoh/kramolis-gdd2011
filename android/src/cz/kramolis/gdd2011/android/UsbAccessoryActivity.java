package cz.kramolis.gdd2011.android;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

/* This Activity does nothing but receive USB_DEVICE_ATTACHED (or maybe better USB_ACCESSORY_ATTACHED) events from the
 * USB service and springboards to the main Gallery activity
 */
public final class UsbAccessoryActivity extends Activity {

	static final String TAG = "LaPardon.UsbAccessoryActivity";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		Intent intent = new Intent(this, MainActivity.class);
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
		try {
			Log.d(TAG, "Start Activity Intent: " + intent);
			startActivity(intent);
		} catch (ActivityNotFoundException e) {
			Log.e(TAG, "unable to start main activity", e);
		}
		finish();
	}

}
