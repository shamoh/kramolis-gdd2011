package cz.kramolis.gdd2011.android;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.provider.BaseColumns;

import java.util.Date;

/**
 * @author Libor Kramolis
 */
public class TwitterDbAdapter {

	static final String TABLE = "twitter";
	static final String LAST_TWEET_ID = "last_tweet_id";
	static final String SEARCHED_WHEN = "searched_when";

	private static final String GET_ALL_ORDER_BY = SEARCHED_WHEN + " ASC";

	private static final String TAG = "LaPardon.TwitterDbAdapter";

	private Context context;
	private SQLiteDatabase db;
	private TwitterDbHelper dbHelper;

	//
	// init
	//

	public TwitterDbAdapter(Context context) {
		this.context = context;
	}

	//
	// business
	//

	public TwitterDbAdapter open() {
		dbHelper = new TwitterDbHelper(context);
		if (db == null || !db.isOpen()) {
			db = dbHelper.getWritableDatabase();
		}
		return this;
	}

	public void close() {
		if (db.isOpen()) {
			db.close();
		}
	}

	public void update (long lastTweetId, Date searchedWhen) {
		ContentValues values = new ContentValues();
		values.put(LAST_TWEET_ID, lastTweetId);
		values.put(SEARCHED_WHEN, searchedWhen.getTime());

		db.update(TABLE, values, BaseColumns._ID + " = ?", new String[]{"1"});
	}

	public void init (long lastTweetId, Date searchedWhen) {
		ContentValues values = new ContentValues();
		values.put(BaseColumns._ID, 1);
		values.put(LAST_TWEET_ID, lastTweetId);
		values.put(SEARCHED_WHEN, searchedWhen.getTime());

		db.insert(TABLE, null, values);
	}

	public long getLastTweetId() {
		Cursor c = db.query(TABLE, new String[]{LAST_TWEET_ID}, BaseColumns._ID + " = ?", new String[]{"1"}, null, null, null);
		long lastTweetId = -1;
		if (c.moveToNext()) {
			lastTweetId = c.getLong(c.getColumnIndex(LAST_TWEET_ID));
		}
		return lastTweetId;
	}

	/*
	public void insertOrIgnore(ContentValues values) {
		Log.d(TAG, "insertOrIgnore on " + values);
		db.insert(TABLE, null, values);
//		CallBlockerSingleton.getInstance().setBlockedmessages(getAllMessages());
	}

	public void deleteAll() {
		db.delete(TABLE, null, null);
//		CallBlockerSingleton.getInstance().setBlockedMessages(getAllMessages());
	}

	public void delete(int id) {
		if (!db.isOpen()) open();

		db.delete(TABLE, BaseColumns._ID + " = ?", new String[]{id + ""});
//		CallBlockerSingleton.getInstance().setBlockedMessages(getAllMessages());
	}

	public void update(int id, String message) {
		ContentValues values = new ContentValues();
		values.put(message, message);
		db.update(TABLE, values, BaseColumns._ID + " = ?", new String[]{id + ""});
//		CallBlockerSingleton.getInstance().setBlockedMessages(getAllMessages());
	}

	public Cursor getAll() {
		Log.d(TAG, "getAll");
		if (!db.isOpen()) {
			open();
		}
		return db.query(TABLE, new String[]{ID, MESSAGE}, null, null, null, null, GET_ALL_ORDER_BY);
	}

	public String[] getAllMessages() {
		Log.d(TAG, "getAllMessages");
		ArrayList<String> list = new ArrayList<String>();
		Cursor cursor = getAll();
		while (cursor.moveToNext()) {
			Log.d(TAG, "cursor.getString(0) = " + cursor.getString(cursor.getColumnIndex(MESSAGE)));
			list.add(cursor.getString(cursor.getColumnIndex(MESSAGE)));
		}
		cursor.close();
		return list.toArray(new String[]{});
	}

	public boolean messageAlreadyExists(String message) {
		Cursor c = db.query(TABLE, new String[]{ID, MESSAGE}, MESSAGE + " = ?", new String[]{message}, null, null, GET_ALL_ORDER_BY);
		boolean result = false;
		if (c.moveToNext()) {
			result = true;
		}
		c.close();
		return result;
	}

	public void insertOrIgnore(String message) {
		Log.d(TAG, "insertOrIgnore on " + message);

		if (!db.isOpen()) open();

		// return if message already exists
		if (messageAlreadyExists(message)) return;

		ContentValues values = new ContentValues();
		values.put(MESSAGE, message);
		//SQLiteDatabase db = this.dbHelper.getWritableDatabase();
		insertOrIgnore(values);
	}
	*/

}
