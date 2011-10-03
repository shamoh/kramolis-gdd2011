package cz.kramolis.gdd2011.android;

import android.app.AlarmManager;
import android.app.Application;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.preference.PreferenceManager;
import android.util.Log;
import twitter4j.Tweet;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @author Ondrej Kosatka
 * @author Libor Kramolis
 */
public class LaPardonApplication extends Application implements OnSharedPreferenceChangeListener {

	private static final String TAG = "LaPardonApplication";

	final DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");

	private TwitterAdapter twitter = new TwitterAdapter();

	private SharedPreferences prefs;

	private int interval = 0;

	private String hashtag = "";

	private Date lastSearch = null;

	private LinkedList<Tweet> queue = new LinkedList<Tweet>();

	private PendingIntent pendingIntent;

	private AlarmManager alarmManager;


	@Override
	public void onCreate() {
		super.onCreate();
		this.prefs = PreferenceManager.getDefaultSharedPreferences(this);
		this.prefs.registerOnSharedPreferenceChangeListener(this);

		startAlarmManager();
	}

	public TwitterAdapter getTwitter() {
		return this.twitter;
	}

	private void startAlarmManager() {
		interval = Integer.parseInt(prefs.getString("interval", "30"));
		hashtag = prefs.getString("hashtag", "#lapardon");
		{
			String lastSearchText = prefs.getString("lastSearch", null);
			if (lastSearchText != null) {
				try {
					lastSearch = dateFormat.parse(lastSearchText);
				} catch (ParseException e) {
					Log.e(TAG, "Wrong lastSearch format", e);
					resetLastSearch();
				}
			} else {
				resetLastSearch();
			}
			Map<String, ?> map = prefs.getAll();
			for (String key : map.keySet()) {
				Log.d(TAG, String.format("+ property %s: %s", key, map.get(key)));
			}
		}

		Log.d(TAG, "AlarmManager is starting... " + interval);

		// Check if we should do anything at boot at all
		if (interval == 0)  // <2>
			return;

		// Create the pending intent
		Intent intent = new Intent(this, TwitterService.class);  // <3>
		pendingIntent = PendingIntent.getService(this, -1, intent,
				PendingIntent.FLAG_UPDATE_CURRENT); // <4>

		// Setup alarm service to wake up and start service periodically
		alarmManager = (AlarmManager) this.getSystemService(Context.ALARM_SERVICE); // <5>
		alarmManager.setRepeating(AlarmManager.RTC_WAKEUP,
				System.currentTimeMillis(), interval * 1000, pendingIntent); // <6>
		Log.d(TAG, "AlarmManager has started");
	}

	public void cancelAlarmManager() {
		pendingIntent.cancel();
		alarmManager.cancel(pendingIntent);
	}

	public void fetchStatuses() {
		Log.d(TAG, String.format("searching for tag %s", hashtag));
		List<Tweet> tweets = getTwitter().search(hashtag);
		if (tweets != null) {
			Collections.reverse(tweets);
			for (Tweet tweet : tweets) {
				Log.d(TAG, "@" + tweet.getFromUser() + " - " + tweet.getText());
				queue.add(tweet);
			}
		}
	}

	private void resetLastSearch() {
		lastSearch = new Date();
		String lastSearchText = dateFormat.format(lastSearch);
		prefs.edit().putString("lastSearch", lastSearchText).commit();
		{
			Map<String, ?> map = prefs.getAll();
			for (String key : map.keySet()) {
				Log.d(TAG, String.format("~ property %s: %s", key, map.get(key)));
			}
		}
	}

	@Override
	public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
		startAlarmManager();
	}

	public LinkedList<Tweet> getQueue() {
		return queue;
	}

}
