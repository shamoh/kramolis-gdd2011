package cz.kramolis.gdd2011.android;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

/**
 * @author Ondrej Kosatka
 */
public class TwitterService extends IntentService {

	private static final String TAG = "TwitterService";

	public TwitterService() {
		super("TwitterService");
		Log.d(TAG, "TwitterService constructor");
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		Log.d(TAG, "onHandleIntent");
		LaPardonApplication app = (LaPardonApplication) getApplication();
		app.fetchStatuses();
	}

}
