package cz.kramolis.gdd2011.android;

import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.view.MenuItem;

/**
 * @author Libor Kramolis
 */
public final class ActivityUtils {

	private static final String TAG = "ActivityUtils";

	private ActivityUtils() {
	}

	//
	// start activity
	//

	public static boolean onOptionsItemSelected(Activity activity, MenuItem item) {
		Log.d(TAG, "onOptionsItemSelected - " + item.getItemId());
		// Handle item selection
		boolean retValue = false;
		switch (item.getItemId()) {
			case R.id.preferences:
				startPreferencesActivity(activity);
				retValue = true;
				break;
			case R.id.queue:
				startQueueActivity(activity);
				retValue = true;
				break;
			case R.id.simulate:
				startSimulateActivity(activity);
				retValue = true;
				break;
			case R.id.about:
				startAboutActivity(activity);
				retValue = true;
				break;
			case R.id.main:
				startMainActivity(activity);
				retValue = true;
				break;
			default:
				retValue = activity.onOptionsItemSelected(item);
		}
		return retValue;
	}

	private static void startPreferencesActivity(Activity activity) {
		Intent i = new Intent(activity, LaPardonPreferencesActivity.class);
		i.setAction(Intent.ACTION_VIEW);
		i.addCategory(Intent.CATEGORY_DEFAULT);
		activity.startActivity(i);
	}

	private static void startQueueActivity(Activity activity) {
		Intent i = new Intent(activity, QueueActivity.class);
		i.setAction(Intent.ACTION_VIEW);
		i.addCategory(Intent.CATEGORY_DEFAULT);
		activity.startActivity(i);
	}

	private static void startSimulateActivity(Activity activity) {
		Intent i = new Intent(activity, SimulateActivity.class);
		i.setAction(Intent.ACTION_VIEW);
		i.addCategory(Intent.CATEGORY_DEFAULT);
		activity.startActivity(i);
	}

	private static void startAboutActivity(Activity activity) {
		Intent i = new Intent(activity, AboutActivity.class);
		i.setAction(Intent.ACTION_VIEW);
		i.addCategory(Intent.CATEGORY_DEFAULT);
		activity.startActivity(i);
	}

	private static void startMainActivity(Activity activity) {
		Intent i = new Intent(activity, MainActivity.class);
		i.setAction(Intent.ACTION_VIEW);
		i.addCategory(Intent.CATEGORY_DEFAULT);
		activity.startActivity(i);
	}

}