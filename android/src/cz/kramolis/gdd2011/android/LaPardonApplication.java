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

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @author Ondrej Kosatka
 * @author Libor Kramolis
 */
public class LaPardonApplication extends Application implements OnSharedPreferenceChangeListener {

	private static final String TAG = "LaPardon.LaPardonApplication";

	final DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");

	private TwitterAdapter twitter = new TwitterAdapter();

	private SharedPreferences prefs;
	private int interval = 0;
	private String hashtag = "";
	private String gddHashtag = "";
	private String playHashtag = "";
	private String infoHashtag = "";
	private String warnHashtag = "";
	private String errorHashtag = "";

	private List<PlayRequest> queue;
	private Map<Long, PlayRequest> queueMap;
	private List<JournalItem> journal;

	private PendingIntent pendingIntent;

	private AlarmManager alarmManager;


	@Override
	public void onCreate() {
		super.onCreate();

		this.queue = new LinkedList<PlayRequest>();
		this.queueMap = new HashMap<Long, PlayRequest>();
		this.journal = new ArrayList<JournalItem>();

		this.prefs = PreferenceManager.getDefaultSharedPreferences(this);
		this.prefs.registerOnSharedPreferenceChangeListener(this);
		resetPrefs();

		startAlarmManager();
	}

	public TwitterAdapter getTwitter() {
		return this.twitter;
	}

	private void startAlarmManager() {
		Log.d(TAG, "AlarmManager is starting... " + interval);

		// Check if we should do anything at boot at all
		if (interval == 0) { // <2>
			return;
		}

		// Create the pending intent
		Intent intent = new Intent(this, TwitterService.class);  // <3>
		pendingIntent = PendingIntent.getService(this, -1, intent, PendingIntent.FLAG_UPDATE_CURRENT); // <4>

		// Setup alarm service to wake up and start service periodically
		alarmManager = (AlarmManager) this.getSystemService(Context.ALARM_SERVICE); // <5>
		alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), interval * 1000, pendingIntent); // <6>
		Log.d(TAG, "AlarmManager has started");
	}

	public void cancelAlarmManager() {
		pendingIntent.cancel();
		alarmManager.cancel(pendingIntent);
	}

	public void fetchStatuses() {
		Log.d(TAG, String.format("searching for tag %s", hashtag));
		List<PlayRequest> requests = getTwitter().search(this, hashtag);
		if (requests != null) {
			Collections.reverse(requests);
//			queue.addAll(requests);
			for (PlayRequest request : requests) {
				queue.add(request);
				queueMap.put(request.getId(), request);
			}
		}
	}

	@Override
	public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
		resetPrefs();

		startAlarmManager();
	}

	public List<PlayRequest> getQueue() {
		return queue;
	}

	public PlayRequest findPlayRequest(long id) {
		return queueMap.get(id);
	}

	public List<JournalItem> getJournal() {
		return journal;
	}

	//
	// Preferences
	//

	private void resetPrefs() {
		interval = Integer.parseInt(prefs.getString("interval", "30"));
		hashtag = prefs.getString("hashtag", "#lapardon");

		gddHashtag = prefs.getString("gddHashtag", "#gddcz");
		playHashtag = prefs.getString("playHashtag", "#play");
		infoHashtag = prefs.getString("infoHashtag", "#info");
		warnHashtag = prefs.getString("warnHashtag", "#warn");
		errorHashtag = prefs.getString("errorHashtag", "#error");
	}

	public Integer getPrefInterval() {
		return interval;
	}

	public String getPrefHashtag() {
		return hashtag;
	}

	public String getPrefGddHashtag() {
		return gddHashtag;
	}

	public String getPrefPlayHashtag() {
		return playHashtag;
	}

	public String getPrefInfoHashtag() {
		return infoHashtag;
	}

	public String getPrefWarnHashtag() {
		return warnHashtag;
	}

	public String getPrefErrorHashtag() {
		return errorHashtag;
	}


	//
	// journal
	//

	public void addJournalAccessoryMessage(String text) {
		addJournal(new JournalItem(JournalType.ACCESSORY_MESSAGE, text));
	}

	public void addJournalAccessoryCommand(String text) {
		addJournal(new JournalItem(JournalType.ACCESSORY_COMMAND, text));
	}

	public void addJournalTwitterSearch(String text) {
		addJournal(new JournalItem(JournalType.TWITTER_SEARCH, text));
	}

	private void addJournal(JournalItem item) {
		journal.add(0, item);
		if (journal.size() > 333) {
			journal.remove(journal.size() - 1);
		}
	}

	//
	// class JournalItem
	//

	public static class JournalItem {

		private JournalType type;
		private String text;
		private Date createdAt;

		private JournalItem(JournalType type, String text) {
			this.type = type;
			this.text = text;
			this.createdAt = new Date();
		}

		public JournalType getType() {
			return type;
		}

		public String getText() {
			return text;
		}

		public Date getCreatedAt() {
			return createdAt;
		}

	} // class JournalItem

	//
	// enum JournalType
	//

	public static enum JournalType {
		ACCESSORY_MESSAGE("adk message ---->"),
		ACCESSORY_COMMAND("<---- adk command"),
		TWITTER_SEARCH("@twitter #search");

		private String displayText;

		private JournalType(String displayText) {
			this.displayText = displayText;
		}

		public String getDisplayText() {
			return displayText;
		}
	} // enum JournalType

}
