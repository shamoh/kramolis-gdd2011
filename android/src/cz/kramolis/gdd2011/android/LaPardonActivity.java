package cz.kramolis.gdd2011.android;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.ParcelFileDescriptor;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;
import com.android.future.usb.UsbAccessory;
import com.android.future.usb.UsbManager;

import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * @author Libor Kramolis
 */
public abstract class LaPardonActivity extends Activity implements Runnable {

	private static final String TAG = "LaPardon.LaPardonActivity";

	public static final byte SIMULATE_COMMAND = 9;

	private static final int MESSAGE_KNOCK = 1;
	private static final int MESSAGE_MIC = 2;
	private static final int MESSAGE_NO_WATER = 3;

	private static final String ACTION_USB_PERMISSION = "cz.kramolis.gdd2011.android.USB_PERMISSION";

	private UsbManager mUsbManager;
	private PendingIntent mPermissionIntent;
	private boolean mPermissionRequestPending;

	UsbAccessory mAccessory;
	ParcelFileDescriptor mFileDescriptor;
	FileInputStream mInputStream;
	FileOutputStream mOutputStream;

	private InputController mInputController;

	private final BroadcastReceiver mUsbReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			Log.d(TAG, "onReceive: action= " + action);

			if (ACTION_USB_PERMISSION.equals(action)) {
				synchronized (this) {
					UsbAccessory accessory = UsbManager.getAccessory(intent);
					if (intent.getBooleanExtra(UsbManager.EXTRA_PERMISSION_GRANTED, false)) {
						openAccessory(accessory);
					} else {
						Log.d(TAG, "permission denied for accessory " + accessory);
					}
					mPermissionRequestPending = false;
				}
			} else if (UsbManager.ACTION_USB_ACCESSORY_DETACHED.equals(action)) {
				UsbAccessory accessory = UsbManager.getAccessory(intent);
				Log.d(TAG, "ACTION_USB_ACCESSORY_DETACHED: accessory= " + accessory);
				if (accessory != null && accessory.equals(mAccessory)) {
					closeAccessory();
				}
			}
		}
	};


	/**
	 * Called when the activity is first created.
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		Log.d(TAG, "onCreate");

		super.onCreate(savedInstanceState);

		mUsbManager = UsbManager.getInstance(this);
		mPermissionIntent = PendingIntent.getBroadcast(this, 0, new Intent(ACTION_USB_PERMISSION), 0);
		IntentFilter filter = new IntentFilter(ACTION_USB_PERMISSION);
		filter.addAction(UsbManager.ACTION_USB_ACCESSORY_DETACHED);
		registerReceiver(mUsbReceiver, filter);

		if (getLastNonConfigurationInstance() != null) {
			mAccessory = (UsbAccessory) getLastNonConfigurationInstance();
			Log.d(TAG, "onCreate: getLastNonConfigurationInstance()/mAccessory= " + mAccessory);
			openAccessory(mAccessory);
		}

		Log.d(TAG, "onCreate: mAccessory= " + mAccessory);

		enableControls(false);

		if (mAccessory != null) {
			showControls();
		} else {
			hideControls();
		}
	}

	//
	// options
	//

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		Log.d(TAG, "onOptionsItemSelected - " + item.getItemId());
		// Handle item selection
		switch (item.getItemId()) {
			default:
				return ActivityUtils.onOptionsItemSelected(this, item);
		}
	}

	//
	// COPIED
	//

	@Override
	public Object onRetainNonConfigurationInstance() {
		if (mAccessory != null) {
			return mAccessory;
		} else {
			return super.onRetainNonConfigurationInstance();
		}
	}

	@Override
	public void onResume() {
		super.onResume();

		Log.d(TAG, "onResume");

//		Intent intent = getIntent();
		if (mInputStream != null && mOutputStream != null) {
			return;
		}

		UsbAccessory[] accessories = mUsbManager.getAccessoryList();
		UsbAccessory accessory = (accessories == null ? null : accessories[0]);

		Log.d(TAG, "onResume: accessory= " + accessory);

		if (accessory != null) {
			if (mUsbManager.hasPermission(accessory)) {
				openAccessory(accessory);
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

	@Override
	public void onPause() {
		super.onPause();
		closeAccessory();
	}

	@Override
	public void onDestroy() {
		unregisterReceiver(mUsbReceiver);
		super.onDestroy();
	}

	private void openAccessory(UsbAccessory accessory) {
		Log.d(TAG, "openAccessory: " + accessory);
		mFileDescriptor = mUsbManager.openAccessory(accessory);
		Log.d(TAG, "openAccessory: mFileDescriptor= " + mFileDescriptor);
		if (mFileDescriptor != null) {
			mAccessory = accessory;
			FileDescriptor fd = mFileDescriptor.getFileDescriptor();
			mInputStream = new FileInputStream(fd);
			mOutputStream = new FileOutputStream(fd);
			Thread thread = new Thread(null, this, "LaPardon Thread");
			thread.start();
			Log.d(TAG, "accessory opened: mAccessory= " + mAccessory);
			enableControls(true);
		} else {
			Log.d(TAG, "accessory open fail");
		}
	}

	private void closeAccessory() {
		Log.d(TAG, "closeAccessory", new RuntimeException());

		enableControls(false);

		try {
			if (mFileDescriptor != null) {
				mFileDescriptor.close();
			}
		} catch (IOException e) {
		} finally {
			mFileDescriptor = null;
			mAccessory = null;
		}
	}

	protected void enableControls(boolean enable) {
	}

	private int composeInt(byte hi, byte lo) {
		int val = (int) hi & 0xff;
		val *= 256;
		val += (int) lo & 0xff;
		return val;
	}

	public void run() {
		int ret = 0;
		byte[] buffer = new byte[16384];
		int i;

		while (ret >= 0) {
			try {
				ret = mInputStream.read(buffer);
			} catch (IOException e) {
				break;
			}

			i = 0;
			while (i < ret) {
				int len = ret - i;

				switch (buffer[i]) {
					case 0x1:
						if (len >= 1) {
							Message m = Message.obtain(mHandler, MESSAGE_KNOCK);
							m.obj = new KnockMsg();
							mHandler.sendMessage(m);
						}
						i += 1;
						break;

					case 0x2:
						if (len >= 3) {
							Message m = Message.obtain(mHandler, MESSAGE_MIC);
							m.obj = new MicMsg(composeInt(buffer[i + 1], buffer[i + 2]));
							mHandler.sendMessage(m);
						}
						i += 3;
						break;

					case 0x3:
						if (len >= 1) {
							Message m = Message.obtain(mHandler, MESSAGE_NO_WATER);
							m.obj = new NoWaterMsg();
							mHandler.sendMessage(m);
						}
						i += 1;
						break;

					default:
						Log.d(TAG, "unknown msg: " + buffer[i]);
						i = len;
						break;
				}
			}

		}
	}

	Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
				case MESSAGE_KNOCK:
					KnockMsg knockMsg = (KnockMsg) msg.obj;
					handleKnockMessage(knockMsg);
					break;

				case MESSAGE_MIC:
					MicMsg micMsg = (MicMsg) msg.obj;
					handleMicMessage(micMsg);
					break;

				case MESSAGE_NO_WATER:
					NoWaterMsg noWaterMsg = (NoWaterMsg) msg.obj;
					handleNoWaterMessage(noWaterMsg);
					break;
			}
		}
	};

	public void sendCommand(byte command, int value) {
		byte[] buffer = new byte[2];
		if (value > 255) {
			value = 255;
		}

		buffer[0] = command;
		buffer[1] = (byte) value;
		if (mOutputStream != null && buffer[1] != -1) {
			try {
				mOutputStream.write(buffer);
			} catch (IOException e) {
				Log.e(TAG, "write failed", e);
			}
		}
	}

	protected void handleKnockMessage(KnockMsg m) {
	}

	protected void handleMicMessage(MicMsg m) {
	}

	protected void handleNoWaterMessage(NoWaterMsg m) {
	}

//	public void onStartTrackingTouch(SeekBar seekBar) {
//	}
//
//	public void onStopTrackingTouch(SeekBar seekBar) {
//	}

	//
	// dalsi kopie
	//

	protected void hideControls() {
		Log.d(TAG, "hideControls", new RuntimeException());
		showNoArduinoInfo();
		mInputController = null;
	}

	private void showNoArduinoInfo() {
		Toast toast = Toast.makeText(this, R.string.no_arduino_text, Toast.LENGTH_LONG);
		toast.show();
//		Intent i = new Intent(this, NoArduinoActivity.class);
//		i.setAction(Intent.ACTION_VIEW);
//		i.addCategory(Intent.CATEGORY_DEFAULT);
//		startActivity(i);
	}

	protected void showControls() {
		Log.d(TAG, "showControls");
		setContentView(R.layout.main);

		mInputController = new InputController(this);
		mInputController.accessoryAttached();
	}

	//
	// class KnockMsg
	//

	protected class KnockMsg {

		public KnockMsg() {
		}

	} // class KnockMsg

	//
	// class MicMsg
	//

	protected class MicMsg {
		private int value;

		public MicMsg(int value) {
			this.value = value;
		}

		public int getValue() {
			return value;
		}
	} // class MicMsg

	//
	// class NoWaterMsg
	//

	protected class NoWaterMsg {

		public NoWaterMsg() {
		}

	} // class NoWaterMsg

}
