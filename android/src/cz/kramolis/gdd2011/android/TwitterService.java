package cz.kramolis.gdd2011.android;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

/**
 * @author Ondrej Kosatka
 */
public class TwitterService extends IntentService {

	public TwitterService() {
		super("TwitterService");
		Log.d(LaPardonApplication.TAG, "TwitterService constructor");
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		Log.d(LaPardonApplication.TAG, "onHandleIntent");
		LaPardonApplication app = (LaPardonApplication) getApplication();
		app.fetchStatuses();
	}

}
