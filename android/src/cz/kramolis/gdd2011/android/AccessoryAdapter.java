package cz.kramolis.gdd2011.android;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;
import com.android.future.usb.UsbAccessory;
import com.android.future.usb.UsbManager;

/**
 * @author Libor Kramolis
 */
public class AccessoryAdapter {

	private static final String TAG = "LaPardon.AccessoryAdapter";

	private MainActivity mainActivity;

	private final AccessoryCommunication accessoryCommunication;

	private UsbManager mUsbManager;

	private PendingIntent mPermissionIntent;

	private static final String ACTION_USB_PERMISSION = "cz.kramolis.gdd2011.android.USB_PERMISSION";
	private boolean mPermissionRequestPending;

	private final BroadcastReceiver mUsbReceiver;

	//
	// init
	//

	public AccessoryAdapter(MainActivity mainActivity) {
		Log.d(TAG, "*** init ***");

		this.mainActivity = mainActivity;
		this.mUsbManager = UsbManager.getInstance(mainActivity);
		this.mPermissionIntent = PendingIntent.getBroadcast(mainActivity, 0, new Intent(ACTION_USB_PERMISSION), 0);

		this.accessoryCommunication = new AccessoryCommunication(mainActivity, mUsbManager);

		this.mUsbReceiver = new BroadcastReceiver() {
			@Override
			public void onReceive(Context context, Intent intent) {
				String action = intent.getAction();
				Log.d(TAG, "*** onReceive: action= " + action);

				if (ACTION_USB_PERMISSION.equals(action)) {
					synchronized (this) {
						UsbAccessory accessory = UsbManager.getAccessory(intent);
						if (intent.getBooleanExtra(UsbManager.EXTRA_PERMISSION_GRANTED, false)) {
							accessoryCommunication.openAccessory(accessory);
						} else {
							Log.d(TAG, "permission denied for accessory " + accessory);
						}
						mPermissionRequestPending = false;
					}
				} else if (UsbManager.ACTION_USB_ACCESSORY_DETACHED.equals(action)) {
					{ // prvni varianta
						UsbAccessory accessory = UsbManager.getAccessory(intent);
						Log.d(TAG, "ACTION_USB_ACCESSORY_DETACHED: accessory= " + accessory);
						accessoryCommunication.closeAccessory(accessory);
					}
					/*
					{ // druha varianta (http://developer.android.com/guide/topics/usb/accessory.html#terminating-a)
						UsbAccessory accessory = (UsbAccessory) intent.getParcelableExtra("accessory" / *UsbManager.EXTRA_ACCESSORY* /);
						Log.d(TAG, "#2 UsbManager.EXTRA_ACCESSORY: accessory= " + accessory);
						if (accessory != null) {
							accessoryCommunication.closeAccessory(accessory);
						}
					}
					*/
				}
			}
		};
	}

	private boolean hasPermission(UsbAccessory accessory) {
		return mUsbManager.hasPermission(accessory);
	}

	public Object getAccessory() {
		return accessoryCommunication.getAccessory();
	}

	//
	// lifecycle
	//

	public void onCreate(UsbAccessory accessory) {
		Log.d(TAG, "*** onCreate: " + accessory);

		IntentFilter filter = new IntentFilter(ACTION_USB_PERMISSION);
		filter.addAction(UsbManager.ACTION_USB_ACCESSORY_DETACHED);
		mainActivity.registerReceiver(mUsbReceiver, filter);

		if (accessory != null) {
			accessoryCommunication.openAccessory(accessory);
		}
	}

	public void onPause() {
		Log.d(TAG, "*** onPause");

		accessoryCommunication.closeAccessory();
	}

	public void onResume() {
		Log.d(TAG, "*** onResume");

		if (accessoryCommunication.ready()) {
			return;
		}

		UsbAccessory[] accessories = mUsbManager.getAccessoryList();
		if (accessories != null) {
			for (UsbAccessory acc : accessories) {
				Log.d(TAG, "- next Accessory: " + acc);
			}
		}
		UsbAccessory accessory = (accessories == null ? null : accessories[0]);

		Log.d(TAG, "onResume: accessory= " + accessory);

		if (accessory != null) {
			if (hasPermission(accessory)) {
				accessoryCommunication.openAccessory(accessory);
			} else {
				synchronized (mUsbReceiver) {
					if (!mPermissionRequestPending) {
						mUsbManager.requestPermission(accessory, mPermissionIntent);
						mPermissionRequestPending = true;
					}
				}
			}
		} else {
			Log.d(TAG, "mAccessory is null");
		}
	}

	public void onDestroy() {
		Log.d(TAG, "*** onDestroy");

		mainActivity.unregisterReceiver(mUsbReceiver);
	}

	//
	// utils
	//

	private void addJournalAccessoryCommand(String text) {
		LaPardonApplication app = (LaPardonApplication) mainActivity.getApplication();
		app.addJournalAccessoryCommand(text);

		text = "[journal] " + text;
		Log.d(TAG, text);
//		Toast toast = Toast.makeText(mainActivity, text, Toast.LENGTH_SHORT);
//		toast.show();
	}

	//
	// accessory
	//

	protected void sendCommandSimulate(int value) {
		accessoryCommunication.sendCommand(AccessoryCommunication.COMMAND_SIMULATE, (byte) value);

		String journalText = String.format("Simulate: %s (0x%s)", value, Utilities.getHex(false, (byte) value));
		addJournalAccessoryCommand(journalText);
	}

	protected void sendCommandPlay(PlayRequest request) {
		String notation = request.getMusicNotation().getNotation();
		byte[] values = notation.getBytes();
		accessoryCommunication.sendCommand(AccessoryCommunication.COMMAND_PLAY, values);

		String journalText = String.format("Play: %s", notation);
		addJournalAccessoryCommand(journalText);
	}

}
