package cz.kramolis.gdd2011.android;

import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;

/**
 * @author Libor Kramolis
 */
public class MainActivity extends LaPardonActivity {

	private static final String TAG = "MainActivity";

	/**
	 * Called when the activity is first created.
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		Log.d(TAG, "onCreate");

		super.onCreate(savedInstanceState);

		setContentView(R.layout.borderlayout);
		setTitle(R.string.main);
	}

	//
	// options
	//

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.mainmenu, menu);
		return true;
	}

}
