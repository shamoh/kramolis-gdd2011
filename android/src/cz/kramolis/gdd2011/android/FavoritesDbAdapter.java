package cz.kramolis.gdd2011.android;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.provider.BaseColumns;
import android.util.Log;

import java.util.ArrayList;

/**
 * @author Ondrej Kosatka
 * @author Libor Kramolis
 */
public class FavoritesDbAdapter {

	static final String TABLE = "favorites";
	static final String ID = "_id";
	static final String MESSAGE = "message";
	static final String UPDATED_WHEN = "updated_when";

	private static final String GET_ALL_ORDER_BY = UPDATED_WHEN + " ASC";

	private static final String TAG = "FavoritesDbAdapter";

	private Context context;
	private SQLiteDatabase db;
	private FavoritesDbHelper dbHelper;

	//
	// init
	//

	public FavoritesDbAdapter(Context context) {
		this.context = context;
	}

	//
	// business
	//

	public FavoritesDbAdapter open() {
		dbHelper = new FavoritesDbHelper(context);
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
