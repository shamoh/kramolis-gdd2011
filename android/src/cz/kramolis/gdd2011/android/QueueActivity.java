package cz.kramolis.gdd2011.android;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.*;
import android.view.ContextMenu.ContextMenuInfo;
import android.widget.AdapterView.AdapterContextMenuInfo;
import twitter4j.Tweet;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Ondrej Kosatka
 */
public class QueueActivity extends ListActivity {

	private QueueAdapter adapter;


	@Override
	public void onCreate(Bundle savedInstanceState) {
		Log.d(LaPardonApplication.TAG, "QueueActivity.onCreate");

		super.onCreate(savedInstanceState);

		setContentView(R.layout.queueactivity);
		registerForContextMenu(getListView());

		adapter = new QueueAdapter(this, R.layout.queuerow, getLastTweets());
		setListAdapter(adapter);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		LaPardonApplication app = (LaPardonApplication) this.getApplication();
		app.getQ().clear();
		app.cancelAlarmManager();
	}

	private void openPreferences() {
		Intent i = new Intent(this, LaPardonPreferencesActivity.class);
		i.setAction(Intent.ACTION_VIEW);
		i.addCategory(Intent.CATEGORY_DEFAULT);
		this.startActivity(i);
	}

	private void about() {
		Intent i = new Intent(this, LaPardonAboutActivity.class);
		i.setAction(Intent.ACTION_VIEW);
		i.addCategory(Intent.CATEGORY_DEFAULT);
		this.startActivity(i);
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
									ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.tweetcontextmenu, menu);
	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		AdapterContextMenuInfo info = (AdapterContextMenuInfo) item
				.getMenuInfo();
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
	}

	private void removeAll() {
		LaPardonApplication app = (LaPardonApplication) this.getApplication();
		app.getQ().clear();
		adapter.clear();
	}

	private List<Tweet> getLastTweets() {
		List<Tweet> lastTweets = new ArrayList<Tweet>();
		LaPardonApplication app = (LaPardonApplication) this.getApplication();
		int i = 0;
		while (i < 20 && i < app.getQ().size()) {
			lastTweets.add(app.getQ().get(i++));
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
		Log.d(LaPardonApplication.TAG, "onOptionsItemSelected - " + item.getItemId());
		// Handle item selection
		switch (item.getItemId()) {
			case R.id.refresh:
				refresh();
				return true;
			case R.id.removeAll:
				removeAll();
				return true;
			case R.id.preferences:
				openPreferences();
				return true;
			case R.id.about:
				about();
				return true;
			default:
				return super.onOptionsItemSelected(item);
		}
	}


}
