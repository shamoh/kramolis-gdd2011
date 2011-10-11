package cz.kramolis.gdd2011.android;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

/**
 * @author Ondrej Kosatka
 */
public class TwitterService extends IntentService {

	private static final String TAG = "LaPardon.TwitterService";

	public TwitterService() {
		super("TwitterService");
		Log.d(TAG, "TwitterService constructor");
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		Log.d(TAG, "onHandleIntent");
		LaPardonApplication app = (LaPardonApplication) getApplication();

		boolean connected = checkNetwork();
		Log.d(TAG, "Network connected? [ " + connected + " ]");
		if (connected) {
			app.fetchStatuses();
		}
	}

	private boolean checkNetwork() {
		boolean connected = false;
		ConnectivityManager connectivityManager = (ConnectivityManager) getApplication().getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
		Log.d(TAG, "ActiveNetworkInfo: " + networkInfo);
		if (networkInfo != null) {
			connected = networkInfo.isConnected();
		}
		return connected;
	}

}
