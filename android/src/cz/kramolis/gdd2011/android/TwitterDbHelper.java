package cz.kramolis.gdd2011.android;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;
import android.util.Log;

/**
 * @author Ondrej Kosatka
 * @author Libor Kramolis
 */
public class TwitterDbHelper extends SQLiteOpenHelper {

	private static final String TAG = "LaPardon.TwitterDbHelper";

	private static final int VERSION = 1;
	private static final String DATABASE = "twitter.db";

//	private Context ctx;

	//
	// init
	//

	public TwitterDbHelper(Context context) {
		super(context, DATABASE, null, VERSION);
//		this.ctx = context;
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		Log.i(TAG, "Creating twitter db ...");
		db.execSQL("create table " + TwitterDbAdapter.TABLE + " (" +
				BaseColumns._ID + " integer primary key autoincrement, " +
				TwitterDbAdapter.LAST_TWEET_ID + " long, "
				+ TwitterDbAdapter.SEARCHED_WHEN + " long)");

		// TODO - naplnit vzorovymi texty
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		Log.i(TAG, String.format("On upgrade (v%d -> v%d) twitter db ...", oldVersion, newVersion));
		if (oldVersion < VERSION) {
			onCreate(db);
		}
	}

}
