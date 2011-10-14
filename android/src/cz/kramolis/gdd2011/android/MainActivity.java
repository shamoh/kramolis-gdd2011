package cz.kramolis.gdd2011.android;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.*;
import android.widget.*;

import java.util.*;

/**
 * @author Libor Kramolis
 */
public class MainActivity extends LaPardonActivity {

	private static final String TAG = "LaPardon.MainActivity";

	private ViewType viewType;
	private MyListener myListener;
	//
	// runtime
	//
	private LinearLayout runtimeContainer;
	private TextView runtimeInputLabel;
	//
	// simulate
	//
	private LinearLayout simulateContainer;
	private TextView simulateSliderValue;
	private JournalAdapter journalAdapter;
	private ListView journalList;
	//
	// queue
	//
	private ListView queueContainer;
	private QueueAdapter queueAdapter;
	private Map<Long, PlayRequest> queueMap;
	//
	//
	// about
	private ListView aboutContainer;

	public MainActivity() {
		this.viewType = ViewType.RUNTIME;
	}

	/**
	 * Called when the activity is first created.
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		Log.d(TAG, "*** onCreate");

		super.onCreate(savedInstanceState);

		setContentView(R.layout.main);
		setTitle(R.string.app_name);

		this.myListener = new MyListener();

		initRuntimeContainer();
		initSimulateContainer();
		initQueueContainer();
		initAboutContainer();

		setViewType(this.viewType);

		checkNetwork();
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

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		Log.d(TAG, "onOptionsItemSelected - " + item.getItemId());
		boolean retValue = false;
		switch (item.getItemId()) {
			case R.id.runtime:
				setViewType(ViewType.RUNTIME);
				retValue = true;
				break;
			case R.id.queue:
				setViewType(ViewType.QUEUE);
				retValue = true;
				break;
			case R.id.simulate:
				setViewType(ViewType.SIMULATE);
				retValue = true;
				break;
			case R.id.preferences:
				startActivity(LaPardonPreferencesActivity.class);
				retValue = true;
				break;
			case R.id.about:
				setViewType(ViewType.ABOUT);
				retValue = true;
				break;
			case R.id.refresh:
				handleRefresh(item);
				retValue = true;
				break;
			case R.id.removeAll:
				handleRemoveAll(item);
				retValue = true;
				break;
			case R.id.quit:
				handleQuit();
				retValue = true;
				break;
			default:
				retValue = super.onOptionsItemSelected(item);
		}
		return retValue;
	}

	private void handleQuit() {
		Log.d(TAG, "----------- QUIT -----------");

		getLaPardonApplication().cancelAlarmManager();
		getLaPardonApplication().getQueue().clear();

		finish();
		System.exit(0);
	}

	private LaPardonApplication getLaPardonApplication() {
		LaPardonApplication app = (LaPardonApplication) this.getApplication();
		return app;
	}

	private void handleRemoveAll(MenuItem item) {
		if (ViewType.QUEUE == viewType) {
			queueRemoveAll();
		} else if (ViewType.SIMULATE == viewType) {
			simulateRemoveAll();
		}
	}

	private void handleRefresh(MenuItem item) {
		if (ViewType.QUEUE == viewType) {
			queueRefresh();
		} else if (ViewType.SIMULATE == viewType) {
			simulateRefresh();
		}
	}

	//
	//
	//

	void setViewType(ViewType viewType) {
		this.viewType = viewType;
		runtimeContainer.setVisibility(findVisibility(ViewType.RUNTIME == viewType));
		queueContainer.setVisibility(findVisibility(ViewType.QUEUE == viewType));
		simulateContainer.setVisibility(findVisibility(ViewType.SIMULATE == viewType));
		aboutContainer.setVisibility(findVisibility(ViewType.ABOUT == viewType));

		// TODO - zneviditelnit nektere menu

		// TODO - zmenit titulek
		setTitle(viewType.name());

		//
		if (ViewType.QUEUE == viewType) {
			queueRefresh();
		} else if (ViewType.SIMULATE == viewType) {
			simulateRefresh();
		}

	}

	private int findVisibility(boolean visible) {
		return (visible ? View.VISIBLE : View.GONE);
	}

	private void startActivity(Class<? extends Activity> activityClass) {
		Intent i = new Intent(this, activityClass);
		i.setAction(Intent.ACTION_VIEW);
		i.addCategory(Intent.CATEGORY_DEFAULT);
		startActivity(i);
	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();

		Log.d(TAG, "onContextItemSelected: " + item);
		Log.d(TAG, "- itemId: " + Integer.toHexString(item.getItemId()));
		Log.d(TAG, "- order : " + item.getOrder());
		Log.d(TAG, "- info.id        : " + info.id);
		Log.d(TAG, "- info.position  : " + info.position);
		Log.d(TAG, "- info.targetView: " + info.targetView);

		boolean retValue = false;
		switch (item.getItemId()) {
			case R.id.execute:
				handleExecute(item);
				retValue = true;
				break;
			default:
				retValue = super.onContextItemSelected(item);
		}
		return retValue;
	}

	private void handleExecute(MenuItem item) {
		if (ViewType.QUEUE == viewType) {
			AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
			TextView idView = (TextView) info.targetView.findViewById(R.id.id);
			long id = Long.parseLong(idView.getText().toString());
			Log.d(TAG, "handleExecute: #" + id);
			queueExecute(id);
		}
	}

	public PlayRequest queueFindPlayRequest(long id) {
		PlayRequest request = getLaPardonApplication().findPlayRequest(id);
		if ((request == null) && (queueMap != null)) {
			request = queueMap.get(id);
		}
		return request;
	}

	private void queueExecute(long id) {
		PlayRequest request = queueFindPlayRequest(id);
		Log.d(TAG, "PlayRequest: " + request);
		if (request != null) {
			MainActivity.this.sendCommandPlay(request);
		}
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);

		Log.d(TAG, "onCreateContextMenu: " + menu);
		Log.d(TAG, "- View: " + Integer.toHexString(v.getId()));
		Log.d(TAG, "  - queue   : " + (R.id.queueContainer == v.getId()));
		Log.d(TAG, "  - simulate: " + (R.id.simulateContainer == v.getId()));
		Log.d(TAG, "- ContextMenuInfo: " + menuInfo);

		MenuInflater inflater = getMenuInflater();
		switch (v.getId()) {
			case R.id.queueContainer:
				inflater.inflate(R.menu.tweetcontextmenu, menu);
				break;
		}
	}

	private void sendCommandTest() {
		Log.d(TAG, "!!! sendCommandTest !!!");

		MainActivity.this.sendCommandSimulate(222);
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
	}

	//
	// Runtime
	//

	private void initRuntimeContainer() {
		runtimeContainer = (LinearLayout) findViewById(R.id.runtimeContainer);

		runtimeInputLabel = (TextView) runtimeContainer.findViewById(R.id.runtimeInputLabel);
		runtimeInputLabel.setOnClickListener(myListener);

	}

	//
	// Simulate
	//

	private void initSimulateContainer() {
		simulateContainer = (LinearLayout) findViewById(R.id.simulateContainer);

		journalAdapter = new JournalAdapter(this, R.layout.journalrow, getSimulateLastJournalItems());
		journalList = (ListView) simulateContainer.findViewById(R.id.journalList);
		journalList.setAdapter(journalAdapter);

		SeekBar slider = (SeekBar) findViewById(R.id.slider);
		simulateSliderValue = (TextView) findViewById(R.id.sliderValue);

		Log.d(TAG, "slider.progress " + slider.getProgress());
		simulateSliderValue.setText(String.valueOf(slider.getProgress()));

		slider.setOnSeekBarChangeListener(myListener);
	}

	private void simulateRefresh() {
		journalAdapter.clear();
		journalAdapter.addAll(getSimulateLastJournalItems());
		journalAdapter.notifyDataSetChanged();
	}

	private List<LaPardonApplication.JournalItem> getSimulateLastJournalItems() {
		List<LaPardonApplication.JournalItem> lastItems = new ArrayList<LaPardonApplication.JournalItem>();
		LaPardonApplication app = (LaPardonApplication) this.getApplication();
		int i = 0;
		while (i < 333 && i < app.getJournal().size()) {
			lastItems.add(app.getJournal().get(i++));
		}
		return lastItems;
	}

	private void simulateRemoveAll() {
		LaPardonApplication app = (LaPardonApplication) this.getApplication();
		app.getJournal().clear();
		journalAdapter.clear();
	}

	//
	// Queue
	//

	private void initQueueContainer() {
		queueContainer = (ListView) findViewById(R.id.queueContainer);

		List<PlayRequest> lastTweets = getQueueLastTweets();

		queueAdapter = new QueueAdapter(this, R.layout.queuerow, lastTweets);
		queueContainer.setAdapter(queueAdapter);
		registerForContextMenu(queueContainer);
//		queueContainer.setTextFilterEnabled(true);
	}

	private List<PlayRequest> getQueueLastTweets() {
		final int MAX = 20;
		List<PlayRequest> lastTweets = new ArrayList<PlayRequest>();
		LaPardonApplication app = (LaPardonApplication) this.getApplication();
		int i = 0;
		while (i < MAX && i < app.getQueue().size()) {
			lastTweets.add(app.getQueue().get(i++));
		}
		{
			this.queueMap = new HashMap<Long, PlayRequest>();
			{
				String text = "Test request - octave 1 [cCdDefFgGabh] #lapardon";
				PlayRequest testRequest = new PlayRequest(-1L, text, "lapardon", new Date(), MusicNotation.lookup(text));
				lastTweets.add(testRequest);
				queueMap.put(testRequest.getId(), testRequest);
			}
			{
				String text = "Test request - octave 2 [c2C2d2D2e2f2F2g2G2a2b2h2] #lapardon";
				PlayRequest testRequest = new PlayRequest(-1L, text, "lapardon", new Date(), MusicNotation.lookup(text));
				lastTweets.add(testRequest);
				queueMap.put(testRequest.getId(), testRequest);
			}
			{
				String text = "Test request - both octaves [cCdDefFgGabh|c2C2d2D2e2f2F2g2G2a2b2h2] #lapardon";
				PlayRequest testRequest = new PlayRequest(-1L, text, "lapardon", new Date(), MusicNotation.lookup(text));
				lastTweets.add(testRequest);
				queueMap.put(testRequest.getId(), testRequest);
			}
		}

		return lastTweets;
	}

	private void queueRemoveAll() {
		getLaPardonApplication().getQueue().clear();
		queueAdapter.clear();
	}

	private void queueRefresh() {
		queueAdapter.clear();
		queueAdapter.addAll(getQueueLastTweets());
		queueAdapter.notifyDataSetChanged();

		checkNetwork();
	}

	//
	// About
	//

	private void initAboutContainer() {
		aboutContainer = (ListView) findViewById(R.id.aboutContainer);

		List<Map<String, String>> list = new ArrayList<Map<String, String>>();
		{
			{
				Map<String, String> item = new HashMap<String, String>();
				item.put("title", "About");
				item.put("description", "Lorem ipsum dolor sit amet, consectetur adipisicing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum.");
				item.put("link", "http://gdd2011.kramolis.cz");
				list.add(item);
			}
			{
				Map<String, String> item = new HashMap<String, String>();
				item.put("title", "Libor Kramolis");
				item.put("description", "TODO");
				item.put("link", "https://plus.google.com/115270016494231681069/about");
				list.add(item);
			}
			{
				Map<String, String> item = new HashMap<String, String>();
				item.put("title", "Ondrej Kosatka");
				item.put("description", "TODO");
				item.put("link", "https://plus.google.com/117246369712480977490/about");
				list.add(item);
			}
			{
				Map<String, String> item = new HashMap<String, String>();
				item.put("title", "Martin Mares");
				item.put("description", "TODO");
				item.put("link", "https://plus.google.com/117017068727290273829/about");
				list.add(item);
			}
			{
				Map<String, String> item = new HashMap<String, String>();
				item.put("title", "Petr Blazek");
				item.put("description", "TODO");
				item.put("link", "https://plus.google.com/100342760152037874082/about");
				list.add(item);
			}
		}
//		Log.d(TAG, "- aboutContainer= " + aboutContainer);
//		ListView about = (ListView) aboutContainer.findViewById(R.id.about);
//		Log.d(TAG, "- about= " + about);

		SimpleAdapter adapter = new SimpleAdapter(this, list, R.layout.list_item,
				new String[]{"title", "description", "link"},
				new int[]{R.id.text1, R.id.text2, R.id.text3});
		aboutContainer.setAdapter(adapter);
		aboutContainer.setTextFilterEnabled(true);
	}

	//
	// enum ViewType
	//

	public static enum ViewType {

		RUNTIME,
		QUEUE,
		SIMULATE,
		ABOUT;

	} // enum ViewType

	//
	// class MyListener
	//

	private class MyListener implements SeekBar.OnSeekBarChangeListener, View.OnClickListener {

		private static final long SEND_COMMAND_INTERVAL = 200;

		private long lastProgressChangedMillis = -1;
		private int lastProgress;

		//
		// SeekBar.OnSeekBarChangeListener
		//

		@Override
		public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
			MainActivity.this.simulateSliderValue.setText(String.valueOf(progress));

			if (System.currentTimeMillis() > (lastProgressChangedMillis + SEND_COMMAND_INTERVAL)) {
				Log.d(TAG, "Slider::onProgressChanged: " + progress + " - " + System.currentTimeMillis());

				sendCommandSimulate(progress);
			}
		}

		@Override
		public void onStartTrackingTouch(SeekBar seekBar) {
			Log.d(TAG, "Slider::onStartTrackingTouch: " + seekBar.getProgress());
		}

		@Override
		public void onStopTrackingTouch(SeekBar seekBar) {
			Log.d(TAG, "Slider::onStopTrackingTouch: " + seekBar.getProgress());

			sendCommandSimulate(seekBar.getProgress());
		}

		private void sendCommandSimulate(int progress) {
			if (lastProgress != progress) {
				MainActivity.this.sendCommandSimulate(progress);
				MainActivity.this.simulateRefresh();
			}
			lastProgress = progress;
			lastProgressChangedMillis = System.currentTimeMillis();
		}

		//
		// View.OnClickListener
		//

		public void onClick(View v) {
			int vId = v.getId();
			switch (vId) {
				case R.id.runtimeInputLabel:
					MainActivity.this.sendCommandTest();
					break;
			}
		}

	} // class MyListener

}
