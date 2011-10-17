package cz.kramolis.gdd2011.android;

import android.app.ListActivity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.*;
import android.view.ContextMenu.ContextMenuInfo;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Ondrej Kosatka
 */
public abstract class QueueActivity extends ListActivity {

	private static final String TAG = "LaPardon.QueueActivity";

	private QueueAdapter adapter;


	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		Log.d(TAG, "onCreate");

		setContentView(R.layout.queue);
		setTitle(R.string.queue);

		registerForContextMenu(getListView());

//		adapter = new QueueAdapter(this, R.layout.queuerow, getLastTweets());
//		setListAdapter(adapter);
	}

	/*
	@Override
	protected void onDestroy() {
		super.onDestroy();
		LaPardonApplication app = (LaPardonApplication) this.getApplication();
		app.getQueue().clear();
		app.cancelAlarmManager();
	}
	*/

	@Override
	protected void onResume() {
		super.onResume();

		checkNetwork();
	}

	private void checkNetwork() {
		boolean connected = false;
		boolean available = false;
		ConnectivityManager connectivityManager = (ConnectivityManager) getApplication().getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
		Log.d(TAG, "ActiveNetworkInfo: " + networkInfo);
		if (networkInfo != null) {
			connected = networkInfo.isConnected();
			available = networkInfo.isAvailable();
		}

		Log.d(TAG, "Network connected / available? [ " + connected + " / " + available + " ]");
		if (connected == false) {
			showNoConnectionInfo();
		}
	}

	private void showNoConnectionInfo() {
		Toast toast = Toast.makeText(this, R.string.no_connection_text, Toast.LENGTH_LONG);
		toast.show();
//		Intent i = new Intent(this, NoConnectionActivity.class);
//		i.setAction(Intent.ACTION_VIEW);
//		i.addCategory(Intent.CATEGORY_DEFAULT);
//		startActivity(i);
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.tweetcontextmenu, menu);
	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		AdapterContextMenuInfo info = (AdapterContextMenuInfo) item.getMenuInfo();
		switch (item.getItemId()) {
/*
			case R.id.moveFirst:
				//moveFirst(info.id);
				return true;
*/
			case R.id.prioritize:
				return true;
			case R.id.remove:
				//remove(info.id);
				return true;
			case R.id.execute:
				return true;
			default:
				return super.onContextItemSelected(item);
		}
	}

	private void refresh() {
		adapter.clear();
		adapter.addAll(getLastTweets());
		adapter.notifyDataSetChanged();

		checkNetwork();
	}

	private void removeAll() {
		LaPardonApplication app = (LaPardonApplication) this.getApplication();
		app.getQueue().clear();
		adapter.clear();
	}

	private List<PlayRequest> getLastTweets() {
		List<PlayRequest> lastTweets = new ArrayList<PlayRequest>();
		LaPardonApplication app = (LaPardonApplication) this.getApplication();
		int i = 0;
		while (i < 20 && i < app.getQueue().size()) {
			lastTweets.add(app.getQueue().get(i++));
		}
		return lastTweets;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.queuemenu, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		Log.d(TAG, "onOptionsItemSelected - " + item.getItemId());
		// Handle item selection
		switch (item.getItemId()) {
			case R.id.menuRefresh:
				refresh();
				return true;
			case R.id.menuRemoveAll:
				removeAll();
				return true;
			default:
				return ActivityUtils.onOptionsItemSelected(this, item);
		}
	}

}
