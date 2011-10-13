package cz.kramolis.gdd2011.android;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;
import android.widget.Toast;
import com.android.future.usb.UsbAccessory;
import com.android.future.usb.UsbManager;

/**
 * @author Libor Kramolis
 */
public class AccessoryAdapter {

	private static final String TAG = "LaPardon.AccessoryAdapter";

	private LaPardonActivity laPardonActivity;

	private final AccessoryCommunication accessoryCommunication;

	private UsbManager mUsbManager;

	private PendingIntent mPermissionIntent;

	private static final String ACTION_USB_PERMISSION = "cz.kramolis.gdd2011.android.USB_PERMISSION";
	private boolean mPermissionRequestPending;

	private final BroadcastReceiver mUsbReceiver;

	//
	// init
	//

	public AccessoryAdapter(LaPardonActivity laPardonActivity) {
		this.laPardonActivity = laPardonActivity;
		this.mUsbManager = UsbManager.getInstance(laPardonActivity);
		this.mPermissionIntent = PendingIntent.getBroadcast(laPardonActivity, 0, new Intent(ACTION_USB_PERMISSION), 0);

		this.accessoryCommunication = new AccessoryCommunication(laPardonActivity, mUsbManager);

		this.mUsbReceiver = new BroadcastReceiver() {
			@Override
			public void onReceive(Context context, Intent intent) {
				String action = intent.getAction();
				Log.d(TAG, "onReceive: action= " + action);

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
					UsbAccessory accessory = UsbManager.getAccessory(intent);
					Log.d(TAG, "ACTION_USB_ACCESSORY_DETACHED: accessory= " + accessory);
					accessoryCommunication.closeAccessory(accessory);
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
		Log.d(TAG, "onCreate");

		IntentFilter filter = new IntentFilter(ACTION_USB_PERMISSION);
		filter.addAction(UsbManager.ACTION_USB_ACCESSORY_DETACHED);
		laPardonActivity.registerReceiver(mUsbReceiver, filter);

		if (accessory != null) {
			accessoryCommunication.openAccessory(accessory);
		}
	}

	public void onPause() {
		accessoryCommunication.closeAccessory();
	}

	public void onResume() {
		if (accessoryCommunication.ready()) {
			return;
		}

		UsbAccessory[] accessories = mUsbManager.getAccessoryList();
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
		laPardonActivity.unregisterReceiver(mUsbReceiver);
	}

	//
	// utils
	//

	private void addJournalAccessoryCommand(String text) {
		LaPardonApplication app = (LaPardonApplication) laPardonActivity.getApplication();
		app.addJournalAccessoryCommand(text);

		Toast toast = Toast.makeText(laPardonActivity, "[journal] " + text, Toast.LENGTH_SHORT);
		toast.show();
	}

	//
	// accessory
	//

	protected void sendCommandSimulate(int value) {
		accessoryCommunication.sendCommand(AccessoryCommunication.COMMAND_SIMULATE, (byte) value);

		String journalText = String.format("Simulate: %s", value);
		addJournalAccessoryCommand(journalText);
	}

}
