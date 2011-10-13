package cz.kramolis.gdd2011.android;

import android.os.Handler;
import android.os.Message;
import android.os.ParcelFileDescriptor;
import android.util.Log;
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
public class AccessoryCommunication implements Runnable {

	private static final String TAG = "LaPardon.AccessoryCommunication";

	private LaPardonActivity laPardonActivity;
	private UsbManager mUsbManager;

	private UsbAccessory mAccessory;
	private ParcelFileDescriptor mFileDescriptor;
	private FileInputStream mInputStream;
	private FileOutputStream mOutputStream;

	public static final byte COMMAND_PLAY = 1;
	public static final byte COMMAND_SIMULATE = 2;

	private static final int MESSAGE_ERROR = 1;
	private static final int MESSAGE_ERROR_LENGTH = 2;

	private static final int MESSAGE_KNOCK = 2;
	private static final int MESSAGE_KNOCK_LENGTH = 1;

	private static final int MESSAGE_MIC = 3;
	private static final int MESSAGE_MIC_LENGTH = 3;

	private static final int MESSAGE_MISSION_COMPLETED = 4;
	private static final int MESSAGE_MISSION_COMPLETED_LENGTH = 1;

	private final Handler mHandler;

	//
	// init
	//

	public AccessoryCommunication(LaPardonActivity laPardonActivity, UsbManager usbManager) {
		this.laPardonActivity = laPardonActivity;
		this.mUsbManager = usbManager;

		this.mHandler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				switch (msg.what) {
					case MESSAGE_ERROR:
						ErrorMsg errorMsg = (ErrorMsg) msg.obj;
						handleErrorMessage(errorMsg);
						break;

					case MESSAGE_KNOCK:
						KnockMsg knockMsg = (KnockMsg) msg.obj;
						handleKnockMessage(knockMsg);
						break;

					case MESSAGE_MIC:
						MicMsg micMsg = (MicMsg) msg.obj;
						handleMicMessage(micMsg);
						break;

					case MESSAGE_MISSION_COMPLETED:
						MissionCompletedMsg missionCompletedMsg = (MissionCompletedMsg) msg.obj;
						handleMissionCompletedMessage(missionCompletedMsg);
						break;
				}
			}
		};
	}

	public boolean ready() {
		boolean ready = (mInputStream != null && mOutputStream != null);
		return ready;
	}

	public Object getAccessory() {
		return mAccessory;
	}

	public boolean openAccessory(UsbAccessory accessory) {
		Log.d(TAG, "openAccessory: " + accessory);

		mFileDescriptor = mUsbManager.openAccessory(accessory);
		Log.d(TAG, "openAccessory: mFileDescriptor= " + mFileDescriptor);

		boolean opened = false;
		if (mFileDescriptor != null) {
			mAccessory = accessory;
			FileDescriptor fd = mFileDescriptor.getFileDescriptor();
			mInputStream = new FileInputStream(fd);
			mOutputStream = new FileOutputStream(fd);
			Thread thread = new Thread(null, this, "LaPardon Thread");
			thread.start();
			Log.d(TAG, "accessory opened: mAccessory= " + mAccessory);

			opened = true;
		} else {
			Log.d(TAG, "accessory open fail");
		}
		return opened;
	}

	public void closeAccessory(UsbAccessory accessory) {
		Log.d(TAG, "closeAccessory", new RuntimeException());

		if (accessory != null && accessory.equals(mAccessory)) {
			closeAccessory();
		} else {
			Log.d(TAG, "Request to close different Accessory: " + accessory);
		}
	}

	public void closeAccessory() {
		try {
			if (mFileDescriptor != null) {
				mFileDescriptor.close();
			}
		} catch (IOException ex) {
			Log.d(TAG, "closeAccessory", ex);
		} finally {
			mFileDescriptor = null;
			mAccessory = null;
		}
	}

	private void addJournalAccessoryMessage(String text) {
		LaPardonApplication app = (LaPardonApplication) laPardonActivity.getApplication();
		app.addJournalAccessoryMessage(text);

		Toast toast = Toast.makeText(laPardonActivity, "[journal] " + text, Toast.LENGTH_LONG);
		toast.show();
	}

	//
	// Handle Messages
	//

	protected void handleErrorMessage(ErrorMsg m) {
		String journalText = String.format("Error baby! [%s]", m.getErrorCode());
		addJournalAccessoryMessage(journalText);
	}

	protected void handleKnockMessage(KnockMsg m) {
		String journalText = "Knock, knock!";
		addJournalAccessoryMessage(journalText);
	}

	protected void handleMicMessage(MicMsg m) {
		String journalText = String.format("Mick: %s", m.getValue());
		addJournalAccessoryMessage(journalText);
	}

	protected void handleMissionCompletedMessage(MissionCompletedMsg m) {
		String journalText = "Mission completed!";
		addJournalAccessoryMessage(journalText);
	}

	//
	// Send Commands
	//

	public void sendCommand(byte command, byte... values) {
		byte[] buffer = new byte[1 + values.length];
		buffer[0] = command;
		System.arraycopy(values, 0, buffer, 1, values.length);

		{
			StringBuilder sb = new StringBuilder("buffer[ ").append(Utilities.getHex(true, buffer)).append(']');
//			for (byte b : buffer) {
//				sb.append(Integer.toString(b, 16)).append(" ");
//			}
			sb.append("]");
			Log.d(TAG, "sendCommand: " + sb);
		}

//		if (mOutputStream != null && buffer[1] != -1) {
		if (mOutputStream != null) {
			try {
				mOutputStream.write(buffer);
			} catch (IOException e) {
				Log.e(TAG, "write failed", e);
			}
		} else {
			Log.d(TAG, "Accessory probably not connected. Output stream is not initialized.");
		}
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
					case MESSAGE_ERROR:
						if (len >= MESSAGE_ERROR_LENGTH) {
							Message m = Message.obtain(mHandler, MESSAGE_ERROR);
							m.obj = new ErrorMsg(buffer[i + 1]);
							mHandler.sendMessage(m);
						}
						i += MESSAGE_ERROR_LENGTH;
						break;

					case MESSAGE_KNOCK:
						if (len >= MESSAGE_KNOCK_LENGTH) {
							Message m = Message.obtain(mHandler, MESSAGE_KNOCK);
							m.obj = new KnockMsg();
							mHandler.sendMessage(m);
						}
						i += MESSAGE_KNOCK_LENGTH;
						break;

					case MESSAGE_MIC:
						if (len >= MESSAGE_MIC_LENGTH) {
							Message m = Message.obtain(mHandler, MESSAGE_MIC);
							m.obj = new MicMsg(composeInt(buffer[i + 1], buffer[i + 2]));
							mHandler.sendMessage(m);
						}
						i += MESSAGE_MIC_LENGTH;
						break;

					case MESSAGE_MISSION_COMPLETED:
						if (len >= MESSAGE_MISSION_COMPLETED_LENGTH) {
							Message m = Message.obtain(mHandler, MESSAGE_MISSION_COMPLETED);
							m.obj = new MissionCompletedMsg();
							mHandler.sendMessage(m);
						}
						i += MESSAGE_MISSION_COMPLETED_LENGTH;
						break;

					default:
						Log.d(TAG, "unknown msg: " + buffer[i]);
						i = len;
						break;
				}
			}
		}
	}

	//
	// Messages
	//
	//
	// class ErrorMsg
	//

	protected class ErrorMsg {

		private byte errorCode;

		public ErrorMsg(byte errorCode) {
			this.errorCode = errorCode;
		}

		public byte getErrorCode() {
			return errorCode;
		}

	} // class ErrorMsg

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
	// class MissionCompletedMsg
	//

	protected class MissionCompletedMsg {

		public MissionCompletedMsg() {
		}

	} // class MissionCompletedMsg

}
