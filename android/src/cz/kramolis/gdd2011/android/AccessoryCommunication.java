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

	private MainActivity mainActivity;
	private UsbManager mUsbManager;

	private UsbAccessory mAccessory;
	private ParcelFileDescriptor mFileDescriptor;
	private FileInputStream mInputStream;
	private FileOutputStream mOutputStream;

	public static final byte COMMAND_PLAY = 1;
	public static final byte COMMAND_SIMULATE = 2;

	private static final int MESSAGE_ERROR = 1;
	private static final int MESSAGE_KNOCK = 2;
	private static final int MESSAGE_MIC = 3;
	private static final int MESSAGE_MISSION_COMPLETED = 4;
	private static final int MESSAGE_PONG = 5;

	private final Handler mHandler;

	//
	// init
	//

	public AccessoryCommunication(MainActivity mainActivity, UsbManager usbManager) {
		Log.d(TAG, String.format("*** init *** Activity: %s;\nUsb: %s", mainActivity, usbManager));

		this.mainActivity = mainActivity;
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

					case MESSAGE_PONG:
						PongMsg pongMsg = (PongMsg) msg.obj;
						handlePongMessage(pongMsg);
						break;
				}
			}
		};
	}

	public boolean ready() {
		boolean ready = (mInputStream != null && mOutputStream != null);
		Log.d(TAG, "*** ready: " + ready);
		return ready;
	}

	public Object getAccessory() {
		return mAccessory;
	}

	public boolean openAccessory(UsbAccessory accessory) {
		Log.d(TAG, "*** openAccessory: [ " + System.identityHashCode(accessory.hashCode()) + " ] " + accessory);

		mFileDescriptor = mUsbManager.openAccessory(accessory);
		Log.d(TAG, "*** openAccessory: mFileDescriptor= " + mFileDescriptor);

		boolean opened = false;
		if (mFileDescriptor != null) {
			mAccessory = accessory;
			FileDescriptor fd = mFileDescriptor.getFileDescriptor();
			mInputStream = new FileInputStream(fd);
			Log.d(TAG, "*** mInputStream= " + mInputStream);

			mOutputStream = new FileOutputStream(fd);
			Log.d(TAG, "*** mOutputStream= " + mOutputStream);

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
		Log.d(TAG, "*** closeAccessory: " + accessory);

		if (accessory != null && accessory.equals(mAccessory)) {
			closeAccessory();
		} else {
			Log.d(TAG, "Request to close different Accessory: " + accessory);
		}
	}

	public void closeAccessory() {
		Log.d(TAG, "*** closeAccessory");

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
		LaPardonApplication app = (LaPardonApplication) mainActivity.getApplication();
		app.addJournalAccessoryMessage(text);

		Toast toast = Toast.makeText(mainActivity, "[journal] " + text, Toast.LENGTH_LONG);
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
		String journalText = String.format("Knock, knock! [%s]", m.getValue());
		addJournalAccessoryMessage(journalText);

		mainActivity.sendCommandPlayFirst();
	}

	protected void handleMicMessage(MicMsg m) {
		String journalText = String.format("Mick: %s", m.getValue());
		addJournalAccessoryMessage(journalText);
	}

	protected void handleMissionCompletedMessage(MissionCompletedMsg m) {
		String journalText = "Mission completed!";
		addJournalAccessoryMessage(journalText);
	}

	protected void handlePongMessage(PongMsg m) {
		String journalText = String.format("Pong: %s - Arduino still plays requested music.", m.getValue());
		Log.d(TAG, journalText);
	}

	//
	// Send Commands
	//

	public void sendCommand(byte command, byte... values) {
		if (mOutputStream != null) {
			try {
/*
				{
					byte[] buffer = new byte[2 + values.length];
					buffer[0] = command;
					buffer[1] = (byte) values.length;
					System.arraycopy(values, 0, buffer, 2, values.length);
					{
						StringBuilder sb = new StringBuilder("buffer[ ").append(Utilities.getHex(true, buffer)).append(']');
						Log.d(TAG, "sendCommand: " + sb);
					}

					mOutputStream.write(buffer);
				}
*/
				{
					byte[] buffer = new byte[2];
					buffer[0] = command;
					buffer[1] = (byte) values.length;

					{
						StringBuilder sb = new StringBuilder("buffer[ ").append(Utilities.getHex(true, buffer)).append(']');
						Log.d(TAG, "sendCommand: META " + sb);
					}

					mOutputStream.write(buffer);
				}
				{
					byte[] buffer = new byte[values.length];
					System.arraycopy(values, 0, buffer, 0, values.length);

					{
						StringBuilder sb = new StringBuilder("buffer[ ").append(Utilities.getHex(true, buffer)).append(']');
						Log.d(TAG, "sendCommand: DATA " + sb);
					}

					mOutputStream.write(buffer);
				}
			} catch (IOException e) {
				Log.e(TAG, "write failed", e);
				Toast toast = Toast.makeText(mainActivity, "ADK write failed!", Toast.LENGTH_SHORT);
				toast.show();
			}
		} else {
			Log.e(TAG, "Accessory probably not connected. Output stream is not initialized.");
			Toast toast = Toast.makeText(mainActivity, "ADK not connected!", Toast.LENGTH_SHORT);
			toast.show();
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
				Log.e(TAG, "read failed", e);
				break;
			}

			Log.d(TAG, "Read length: " + ret);

			i = 0;
			while (i < ret) {
				int len = ret - i;

				Log.d(TAG, "Read bytes: " + Utilities.getHex(true, buffer[i], buffer[i + 1]));

				switch (buffer[i]) {
					case MESSAGE_ERROR:
						if (len >= 2) {
							Message m = Message.obtain(mHandler, MESSAGE_ERROR);
							m.obj = new ErrorMsg(buffer[i + 1]);
							mHandler.sendMessage(m);
						}
						i += 2;
						break;

					case MESSAGE_KNOCK:
						if (len >= 2) {
							Message m = Message.obtain(mHandler, MESSAGE_KNOCK);
							m.obj = new KnockMsg(buffer[i + 1]);
							mHandler.sendMessage(m);
						}
						i += 2;
						break;

					case MESSAGE_MIC:
						if (len >= 2) {
							Message m = Message.obtain(mHandler, MESSAGE_MIC);
//							m.obj = new MicMsg(composeInt(buffer[i + 1], buffer[i + 2]));
							m.obj = new MicMsg(buffer[i + 1]);
							mHandler.sendMessage(m);
						}
						i += 2;
						break;

					case MESSAGE_MISSION_COMPLETED:
						if (len >= 2) {
							Message m = Message.obtain(mHandler, MESSAGE_MISSION_COMPLETED);
							m.obj = new MissionCompletedMsg();
							mHandler.sendMessage(m);
						}
						i += 2;
						break;

					case MESSAGE_PONG:
						if (len >= 2) {
							Message m = Message.obtain(mHandler, MESSAGE_PONG);
							m.obj = new PongMsg(buffer[i + 1]);
							mHandler.sendMessage(m);
						}
						i += 2;
						break;

					default:
						Log.d(TAG, "unknown msg: " + buffer[i]);
						i = len;
						break;
				}
			}
		}
		Log.e(TAG, "*run* finished: ret= " + ret);
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

		private byte value;

		public KnockMsg(byte value) {
			this.value = value;
		}

		public byte getValue() {
			return value;
		}

	} // class KnockMsg

	//
	// class MicMsg
	//

	protected class MicMsg {
		private byte value;

		public MicMsg(byte value) {
			this.value = value;
		}

		public byte getValue() {
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

	//
	// class PongMsg
	//

	protected class PongMsg {

		private byte value;

		public PongMsg(byte value) {
			this.value = value;
		}

		public byte getValue() {
			return value;
		}

	} // class PongMsg

}
