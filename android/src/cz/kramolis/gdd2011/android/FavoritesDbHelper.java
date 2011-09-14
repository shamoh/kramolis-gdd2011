package cz.kramolis.gdd2011.android;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * @author Ondrej Kosatka
 */
public class FavoritesDbHelper extends SQLiteOpenHelper {

	private static final String TAG = "FavoritesDbHelper";

	private static final int VERSION = 1;
	private static final String DATABASE = "favorites.db";

	private Context ctx;

	//
	// init
	//

	public FavoritesDbHelper(Context context) {
		super(context, DATABASE, null, VERSION);
		this.ctx = context;
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		Log.i(TAG, "Creating favorites db ...");
		db.execSQL("create table " + FavoritesDbAdapter.TABLE + " (" + FavoritesDbAdapter.ID
				+ " integer primary key autoincrement, " + FavoritesDbAdapter.MESSAGE + " text, "
				+ FavoritesDbAdapter.UPDATED_WHEN + " text)");

		// TODO - naplnit vzorovymi texty
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		Log.i(TAG, String.format("On upgrade (v%d -> v%d) favorites db ...",
				oldVersion, newVersion));
		if (oldVersion < 1) {
			onCreate(db);
		}
	}

}
