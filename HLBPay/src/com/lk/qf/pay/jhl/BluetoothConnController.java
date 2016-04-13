/*
 * Copyright (C) 2011 Wireless Network and Multimedia Laboratory, NCU, Taiwan
 * 
 * You can reference http://wmlab.csie.ncu.edu.tw
 * 
 * This class is used to handle process or transfer the control messages about connection * 
 * 
 * @author Fiona
 * @version 0.0.1
 *
 */

package com.lk.qf.pay.jhl;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;
import android.app.Notification;
import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Binder;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

public class BluetoothConnController extends Service {

	// Debugging
	private static final String TAG = "BluetoothConnController";
	private static final boolean D = false;

	public static final String SEND_MSG_FROM_BT_ACTION = "SEND_MSG_FROM_BT_ACTION";
	public static final String CONNECT_REQUEST_ACTION = "CONNECT_REQUEST_ACTION";
	public static final String DISCONNECT_REQUEST_ACTION = "DISCONNECT_REQUEST_ACTION";
	public static final String REQUEST_ECHO_ACTION = "REQUEST_ECHO_ACTION";
	public static final String TOAST = "toast";
	public static final String START_MONITOR_ACTION = "START_MONITOR_ACTION";
	public static final String GET_SERIVICE_STATUS_ACTION = "GET_SERIVICE_STATUS_ACTION";
	public static final String GET_SERIVICE_STATUS_EVENT = "GET_SERIVICE_STATUS_EVENT";
	public static final String MONITOR_STATUS = "MONITOR_STATUS";
	public static final String TX_BYTES = "TX_BYTES";
	public static final String RX_BYTES = "RX_BYTES";
	public static final String SCAN_STATUS = "SCAN_STATUS";
	public static final String DISSCAN_STATUS = "DISSCAN_STATUS";
	private BluetoothAdapter mBtAdapter;
	private List<BluetoothDevice> mNewDevicesArrayAdapter = new ArrayList<BluetoothDevice>();
	private static Timer mExTimeOutTimer = null;
	private static int WAIT_SCANTIMEOUT = 1000;
	private static Boolean bScan = false;
	private static String[] NAME_FILETER = new String[10];

	private MessageReceiver mBtMsgReceiver;
	// private BluetoothAdapter mBluetoothAdapter =
	// BluetoothAdapter.getDefaultAdapter();
	private BluetoothConnModel mConnService = null;
	private MessageHandler msgHandler;
	private BluetoothDevice mDevice = null;
	private static BluetoothConnController mInstance;

	// Message types sent from the BluetoothChatService Handler

	// -未找到 0 连接失败 1连接成功 2 断开 3 //蓝牙开启 4 蓝牙手动关闭
	public static final int MESSAGE_STATE_NoDevice = -1;
	public static final int MESSAGE_STATE_ConnectFaid = 0;
	public static final int MESSAGE_STATE_Connect = 1;
	public static final int MESSAGE_STATE_DisConnect = 2;
	public static final int MESSAGE_STATE_ON = 3;
	public static final int MESSAGE_STATE_OFF = 4;
	public static final int MSG_BLUE_SCAN = 10;

	public static final int MESSAGE_STATE_CHANGE = 1;
	public static final int MESSAGE_READ = 2;
	public static final int MESSAGE_WRITE = 3;
	public static final int MESSAGE_RWRITE = 8;
	public static final int MESSAGE_RWRITE_TIMEOUT = 9;
	public static final int MESSAGE_DEVICE_NAME = 4;
	public static final int MESSAGE_TOAST = 5;
	public static final int MESSAGE_ALERT_DIALOG = 6;
	public static final int MSG_MODE_SEND_STRING = 1;
	public static final int MSG_MODE_SEND_FILE = 2;
	public static final int MSG_MODE_SEND_HEX = 7;

	private static Handler mHandler;
	private static Context mContext;
	byte[] readbuffer = new byte[1024];
	byte[] RewritecmdBuf = new byte[256];
	private static Timer mExchangeTimer = null;
	private static int WAIT_TIMEOUT = 3000;
	private static int RESEMD_TIMES = 0;
	private static int RESEMD_TIMES_COUNT = 2;

	/**
	 * 对象转数组
	 * 
	 * @param obj
	 * @return
	 */
	public byte[] toByteArray(Object obj) {
		byte[] bytes = null;
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		try {
			ObjectOutputStream oos = new ObjectOutputStream(bos);
			oos.writeObject(obj);
			oos.flush();
			bytes = bos.toByteArray();
			oos.close();
			bos.close();
		} catch (IOException ex) {
			ex.printStackTrace();
		}
		return bytes;
	}

	private class MessageHandler extends Handler {
		public String deviceName = null;

		@Override
		public void handleMessage(Message msg) {

			switch (msg.what) {
			case MESSAGE_STATE_CHANGE:
				// 告诉前端有更新状态
				if (mHandler != null)
					mHandler.obtainMessage(MESSAGE_STATE_CHANGE, msg.arg1, 0, 0)
							.sendToTarget();

				break;
			case MESSAGE_DEVICE_NAME:
				// save the connected device's name
				mDevice = (BluetoothDevice) msg.obj;
				deviceName = mDevice.getName();
				Log.w(TAG, "[handleMessage] Device name: " + deviceName);
				break;
			case MESSAGE_WRITE:
				// byte[] writeBuf = toByteArray(msg.obj);
				// construct a string from the buffer
				// String writeMessage = new String(writeBuf);
				// Log.w(TAG, "[handleMessage] Write message: "+writeMessage);
				// Log.w(TAG, "[handleMessage] currentTimeMillis: "+
				// System.currentTimeMillis() );
				// sendBroadcast(writeMessage,
				// BluetoothCommmanager.OUTGOING_MSG, msg.arg2);
				break;
			case MESSAGE_READ:
				/*
				 * byte[] readBuf = (byte[]) msg.obj; // construct a string from
				 * the valid bytes in the buffer String readMessage = new
				 * String(readBuf, 0, msg.arg1); //aa Log.v(TAG,
				 * "[handleMessage] Read message: "+readMessage); //aa
				 * Log.v(TAG, "[handleMessage] currentTimeMillis: "+
				 * System.currentTimeMillis() ); // SharedPreferences settings =
				 * PreferenceManager
				 * .getDefaultSharedPreferences(getApplicationContext()); //
				 * boolean isEcho =
				 * settings.getBoolean(BluetoothConn.KEY_ECHO_PREF, false); //
				 * if (isEcho){ //
				 * BluetoothConnController.this.sendMessage(readMessage);//echo
				 * // } //
				 * BluetoothConnController.this.sendMessage(readMessage);//echo
				 * // sendBroadcast(readMessage,
				 * BluetoothCommmanager.INCOMING_MSG, msg.arg2); //
				 * sendBroadcast(readMessage,
				 * ServiceController.RECEIVE_MSG_FROM_BT_ACTION); //TODO:
				 * support only one remote device now
				 */

				Arrays.fill(readbuffer, (byte) 0);
				byte[] readBuf = (byte[]) msg.obj;
				System.arraycopy(readBuf, 0, readbuffer, 0, msg.arg2);

				/*
				 * Bundle data = msg.getData(); Arrays.fill(readbuffer, (byte)
				 * 0); byte[] readBuf =data.getByteArray("keydata");
				 * System.arraycopy(readBuf, 0, readbuffer, 0, msg.arg1);
				 */

				// readbuffer =new byte[msg.arg2];
				if (D) {
					StringBuilder sb = new StringBuilder();
					for (int i = 0; i < msg.arg2; ++i) {
						sb.append(String.format("%02x", readbuffer[i]));
					}
					Log.e("MESSAGE_READ", sb.toString());
				}

				if (msg.arg2 == 4) // 如果大小为4 A0 01连接成功
				{
					if ((readbuffer[0] & 0xFF) == 0x00
							&& (readbuffer[1] & 0xFF) == 0x02
							&& ((readbuffer[2] & 0xFF) == 0xA0) /*
																 * &&
																 * (readbuffer
																 * [3] &
																 * 0xFF)==0x00
																 */) {
						// 发送连接成功消息
						// if (mHandler !=null)
						// mHandler.obtainMessage(MESSAGE_STATE_CHANGE,
						// MESSAGE_STATE_Connect, 0, 0).sendToTarget();

					}
				}

				if ((readbuffer[0] & 0xFF) == 0x00
						&& (readbuffer[1] & 0xFF) == 0x00
						&& ((readbuffer[2] & 0xFF) == 0x00)
						&& (readbuffer[3] & 0xFF) == 0x00) {
					break;
				}

				if (mHandler != null) {
					if (mExchangeTimer != null) {
						mExchangeTimer.cancel();
					}
					mHandler.obtainMessage(MESSAGE_READ, msg.arg1, msg.arg2,
							readbuffer).sendToTarget();
				}

				deviceName = null;
				break;

			case MESSAGE_TOAST:
				// Toast.makeText(getApplicationContext(),
				// msg.getData().getString(TOAST),
				// Toast.LENGTH_SHORT).show();

				// mHandler.sendEmptyMessage(0);
				break;

			case MESSAGE_ALERT_DIALOG:
				Log.d(TAG, "MESSAGE_ALERT_DIALOG");
				String str = new String((String) msg.obj);
				/*
				 * AlertDialog.Builder builder = new
				 * AlertDialog.Builder(getApplicationContext());
				 * builder.setMessage
				 * (str).setCancelable(false).setNegativeButton("OK", new
				 * DialogInterface.OnClickListener() {
				 * 
				 * @Override public void onClick(DialogInterface arg0, int arg1)
				 * { // TODO Auto-generated method stub arg0.cancel(); } });
				 * AlertDialog alert = builder.create(); alert.show();
				 */
				// sendBroadcast(str, BluetoothCommmanager.ALERT_MSG, msg.arg1);
				break;
			case MESSAGE_RWRITE:
				RESEMD_TIMES++;
				sendHexMessage(RewritecmdBuf);
				break;
			case MESSAGE_RWRITE_TIMEOUT:
				if (mHandler != null)
					mHandler.obtainMessage(MESSAGE_RWRITE_TIMEOUT, 0, 0, 0)
							.sendToTarget();
				break;
			case MSG_BLUE_SCAN:
				if (mHandler != null)
					mHandler.obtainMessage(MSG_BLUE_SCAN, 0, 0, msg.obj)
							.sendToTarget();
				break;

			}
		}

		private void sendBroadcast(String str, String action, int num) {
			String displayString = null;
			if (action.equals(BluetoothCommmanager.OUTGOING_MSG)) {
				displayString = "Me : " + str;
			} else if (action.equals(BluetoothCommmanager.INCOMING_MSG)) {
				displayString = /* deviceName+" : "+ */str;
			} else {
				displayString = str;
			}
			Intent i = new Intent(action);
			i.putExtra("STR", displayString);
			i.putExtra("COUNTER", num);
			BluetoothConnController.this.sendBroadcast(i);
		}
		// private void sendBroadcast(String str, String action){
		// sendBroadcast(str, action, 0);
		// }

	} // MessageHandler

	private void sendFile(String file) {
		if (file.length() > 0) {
			mConnService.SendFileToAllSockets(file.toString());
		}
	}

	/**
	 * Sends a message.
	 * 
	 * @param message
	 *            A string of text to send.
	 */
	private void sendMessage(String message) {

		// Check that there's actually something to send
		if (message.length() > 0) {
			// Get the message bytes and tell the BluetoothChatService to write
			// byte[] send = message.getBytes();
			mConnService.writeToAllSockets(message.toString());

		}
	}

	/**
	 * Sends a message.
	 * 
	 * @param message
	 *            A string of text to send.
	 */
	private void sendHexMessage(byte[] cmdData) {

		if (BluetoothCommmanager.ConnectDevice == false)
			return;

		// Check that there's actually something to send
		if (cmdData.length > 0) {
			// Get the message bytes and tell the BluetoothChatService to write
			// byte[] send = message.getBytes();
			if (mConnService != null) {

				mConnService.writecmd(cmdData);
			}

			if (cmdData.length > 0 && (cmdData[2] & 0xff) == 0x22) {
				WAIT_TIMEOUT = WAIT_TIMEOUT * 20;
			} else if (cmdData.length > 0 && (cmdData[2] & 0xff) == 0x40) {
				WAIT_TIMEOUT = 3000;
			} else {
				WAIT_TIMEOUT = 2000;
			}

			if (mExchangeTimer != null) {
				mExchangeTimer.cancel();
			}
			mExchangeTimer = new Timer(true);
			mExchangeTimer.schedule(new TimerTask() {
				@Override
				public void run() {
					// TODO Auto-generated method stub
					if (RESEMD_TIMES > RESEMD_TIMES_COUNT) // 大于2次就返回超时,其他重复
					{
						if (mHandler != null)
							mHandler.obtainMessage(MESSAGE_RWRITE_TIMEOUT, 0,
									0, 0).sendToTarget();

					} else {

						if (msgHandler != null)
							msgHandler.obtainMessage(MESSAGE_RWRITE, 0, 0, 0)
									.sendToTarget();

					}
				}
			}, WAIT_TIMEOUT);

		}
	}

	private void connectTo(String deviceAddress) {
		// if
		// (!deviceAddress.matches("[A-Za-z0-9]{2}:[A-Za-z0-9]{2}:[A-Za-z0-9]{2}:[A-Za-z0-9]{2}:[A-Za-z0-9]{2}:[A-Za-z0-9]{2}"))
		if (!deviceAddress
				.matches("([0-9a-fA-F][0-9a-fA-F]:){5}([0-9a-fA-F][0-9a-fA-F])")) {
			Log.e(TAG, "address " + deviceAddress + " is wrong, length = "
					+ deviceAddress.length());
			return;
		}
		if (BluetoothAdapter.getDefaultAdapter().getRemoteDevice(deviceAddress) == null) {
			Log.e(TAG, "adapter is not exist");
			return;
		}
		BluetoothDevice device = BluetoothAdapter.getDefaultAdapter()
				.getRemoteDevice(deviceAddress);
		// BluetoothDevice btDevice =
		// intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);

		if (device.getBondState() != BluetoothDevice.BOND_BONDED) // 如果没有配对,需要自动配对
		{
			try {
				Log.d("mylog", "NOT BOND_BONDED");
				ClsUtils.setPin(device.getClass(), device, "0000"); // 手机和蓝牙采集器配对
				ClsUtils.createBond(device.getClass(), device);
				// remoteDevice = device; // 配对完毕就把这个设备对象传给全局的remoteDevice
				// result = true;

				if (mExTimeOutTimer != null) {
					mExTimeOutTimer.cancel();
					mExTimeOutTimer = null;
				}
				mExTimeOutTimer = new Timer(true);
				mExTimeOutTimer.schedule(new TimerTask() {
					@Override
					public void run() {
						// TODO Auto-generated method stub
						if (mHandler != null)
							mHandler.obtainMessage(
									BluetoothConnController.MESSAGE_STATE_CHANGE,
									0, 0, 0).sendToTarget();
						if (mExTimeOutTimer != null) {
							mExTimeOutTimer.cancel();
							mExTimeOutTimer = null;
						}
					}
				}, 20000); // 20秒如果配对失败,则失败

			} catch (Exception e) {
				// TODO Auto-generated catch block
				Log.d("mylog", "setPiN failed!");
				e.printStackTrace();
			} //
		} else {

			if (mConnService != null) {
				mConnService.connectTo(device);
				this.startForeground(1234, new Notification());
				Log.e(TAG, "connectTo begin!!!");
			}
		}
	}

	private void connectDeviceTo(BluetoothDevice device) {

		if (mConnService != null) {
			mConnService.connectTo(device);
			this.startForeground(1234, new Notification());
			Log.e(TAG, "connectTo begin!!!");
		}
	}

	private void disconnectTo(String address) {
		if (mConnService != null) {
			if (BluetoothCommmanager.ConnectDevice) {
				byte[] cmd = new byte[4];
				cmd[0] = 0x00;
				cmd[1] = 0x02;
				cmd[2] = (byte) 0xA0;
				cmd[3] = 0x00;
				mConnService.writecmd(cmd);
			}
			mConnService.disconnectSocketFromAddress(address);
		}
		this.stopForeground(true);
		Log.e(TAG, "disconnectTo!!!" + address);
	}

	private void terminatedAllSockets() {
		// mConnService.terminated();
		if (mConnService != null)
			mConnService.terminated();
		// mConnService = null;
		Log.e(TAG, "terminatedAllSockets!!!");
	}

	public void setHandle(Context context, Handler handler) {

		mHandler = handler;
		mContext = context;

	}

	public void setMsgHandle(Handler handler) {

		mHandler = handler;

	}

	public static BluetoothConnController getInstance() {
		Log.i("result", "----------------ins-------" + mInstance);
		return mInstance;
	}

	@Override
	public void onCreate() {
		if (D)
			Log.e(TAG, "[onCreate]");

		msgHandler = new MessageHandler();
		// mConnService = new BluetoothConnModel(this, msgHandler);
		// mConnService.startSession();

		IntentFilter mFilter01, mFilter02, mFilter03, stateChangedFilter;
		mFilter01 = new IntentFilter(SEND_MSG_FROM_BT_ACTION);
		mFilter02 = new IntentFilter(CONNECT_REQUEST_ACTION);
		mFilter03 = new IntentFilter(DISCONNECT_REQUEST_ACTION);
		stateChangedFilter = new IntentFilter(
				BluetoothAdapter.ACTION_STATE_CHANGED);
		mBtMsgReceiver = new MessageReceiver();
		registerReceiver(mBtMsgReceiver, mFilter01);
		registerReceiver(mBtMsgReceiver, mFilter02);
		registerReceiver(mBtMsgReceiver, mFilter03);
		registerReceiver(mBtMsgReceiver, new IntentFilter(START_MONITOR_ACTION));
		registerReceiver(mBtMsgReceiver, stateChangedFilter);
		registerReceiver(mBtMsgReceiver, new IntentFilter(
				BluetoothDevice.ACTION_ACL_DISCONNECTED));
		registerReceiver(mBtMsgReceiver, new IntentFilter(
				GET_SERIVICE_STATUS_ACTION));

		IntentFilter filterscan = new IntentFilter(SCAN_STATUS);
		registerReceiver(mBtMsgReceiver, filterscan);

		// Register for broadcasts when discovery has finished
		IntentFilter filterdis = new IntentFilter(DISSCAN_STATUS);
		registerReceiver(mBtMsgReceiver, filterdis);

		IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
		registerReceiver(mBtMsgReceiver, filter);

		// Register for broadcasts when discovery has finished
		filter = new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
		registerReceiver(mBtMsgReceiver, filter);

		IntentFilter filterREQUEST = new IntentFilter(
				BluetoothDevice.ACTION_BOND_STATE_CHANGED);
		registerReceiver(mBtMsgReceiver, filterREQUEST);

		mInstance = this;
		super.onCreate();

	}

	@Override
	public void onDestroy() {
		if (D)
			Log.e(TAG, "[onDestroy]");
		super.onDestroy();
		// if (mConnService != null) mConnService.terminated();
		mConnService = null;
		stopSelf();

		unregisterReceiver(mBtMsgReceiver);

	}

	@Override
	public void onStart(Intent intent, int startId) {
		if (D)
			Log.e(TAG, "[onStart]");
		super.onStart(intent, startId);
		if (mConnService == null) {
			mConnService = new BluetoothConnModel(this, msgHandler);
			mConnService.startSession();
		}

	}

	@Override
	public IBinder onBind(Intent arg0) {
		if (D)
			Log.e(TAG, "[onBind]");
		return null;
	}

	public class LocalBinder extends Binder {
		BluetoothConnController getService() {
			return BluetoothConnController.this;
		}
	}

	public class MessageReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			if (intent == null)
				return;
			String action = intent.getAction();
			if (D)
				Log.e(TAG, "** JHL ON RECEIVE **" + action);
			if (action.equals(SEND_MSG_FROM_BT_ACTION)) {
				String msg = intent.getExtras().getString("MESSAGE");
				int mode = intent.getExtras().getInt("MODE");
				switch (mode) {

				case MSG_MODE_SEND_STRING:
				case MSG_MODE_SEND_FILE:
					// sendMessage(msg);
					// sendFile(msg);
					// break;
				case MSG_MODE_SEND_HEX:
					byte[] writecmdBuf = intent.getExtras().getByteArray(
							"MESSAGE");
					RewritecmdBuf = intent.getExtras().getByteArray("MESSAGE");
					sendHexMessage(writecmdBuf);
					break;
				}

			} else if (action.equals(CONNECT_REQUEST_ACTION)) {
				String deviceAddress = intent.getExtras().getString(
						BluetoothCommmanager.DEVICE_ADDRESS);
				Log.i(TAG, "[onReceive] deviceAddress = " + deviceAddress);
				BluetoothConnController.this.connectTo(deviceAddress);
			} else if (action.equals(DISCONNECT_REQUEST_ACTION)) {
				Log.i(TAG, "[onReceive] DISCONNECT_REQUEST_ACTION");
				String deviceAddress = intent.getExtras().getString(
						BluetoothCommmanager.DISCONNECT_DEVICE_ADDRESS);
				Log.i(TAG, "[onReceive] disconnect device address = "
						+ deviceAddress);
				BluetoothConnController.this.disconnectTo(deviceAddress);

			} else if (action.equals(BluetoothAdapter.ACTION_STATE_CHANGED)) {
				Log.i(TAG, "[onReceive] ACTION_STATE_CHANGED");
				int currentState = intent.getIntExtra(
						BluetoothAdapter.EXTRA_STATE, BluetoothAdapter.ERROR);
				switch (currentState) {
				case BluetoothAdapter.STATE_ON:
					Log.i(TAG, "[onReceive] current state = ON");
					if (mHandler != null)
						mHandler.obtainMessage(MESSAGE_STATE_CHANGE,
								MESSAGE_STATE_DisConnect, 0, 0).sendToTarget();
					break;
				case BluetoothAdapter.STATE_OFF:
					Log.i(TAG, "[onReceive] current state = OFF");
					if (mHandler != null)
						mHandler.obtainMessage(MESSAGE_STATE_CHANGE,
								MESSAGE_STATE_OFF, 0, 0).sendToTarget();

					BluetoothConnController.this.terminatedAllSockets();
					break;
				case BluetoothAdapter.STATE_TURNING_ON:
					Log.i(TAG, "[onReceive] current state = TURNING_ON");
					if (mHandler != null)
						mHandler.obtainMessage(MESSAGE_STATE_CHANGE,
								MESSAGE_STATE_ON, 0, 0).sendToTarget();

					break;
				case BluetoothAdapter.STATE_TURNING_OFF:
					Log.i(TAG, "[onReceive] current state = TURNING_OFF");
					break;

				}

				// Log.i(TAG, "[onReceive] current state = "+currentState);
				// BluetoothConnController.this.disconnectTo(deviceAddress);
			} else if (action.equals(START_MONITOR_ACTION)) {
				Log.d(TAG, "START_MONITOR_ACTION");
				mConnService.startFileMonitor(intent.getBooleanExtra(
						MONITOR_STATUS, false));
			} else if (action.equals(GET_SERIVICE_STATUS_ACTION)) {
				Intent i = new Intent(GET_SERIVICE_STATUS_EVENT);
				i.putExtra(MONITOR_STATUS, true);// mConnService.getFileMonitor());
				i.putExtra(TX_BYTES, mConnService.getTxBytes());
				i.putExtra(RX_BYTES, mConnService.getRxBytes());
				BluetoothConnController.this.sendBroadcast(i);
			} else if (action.equals(BluetoothDevice.ACTION_ACL_DISCONNECTED)) {
				BluetoothDevice device = intent.getExtras().getParcelable(
						BluetoothDevice.EXTRA_DEVICE);
				// Toast.makeText(getApplicationContext(), device.getName() +
				// " was disconnected: " + device.getAddress(),
				// Toast.LENGTH_SHORT).show();

				if (BluetoothCommmanager.ConnectDevice)
					BluetoothConnController.this.disconnectTo(device
							.getAddress());
				Log.d(TAG,
						"BT connection was disconnected!" + device.getAddress());

				// if (mHandler !=null)
				// mHandler.obtainMessage(MESSAGE_STATE_CHANGE, -1, 0,
				// 0).sendToTarget();
				if (mHandler != null) {
					// mHandler.obtainMessage(MESSAGE_STATE_CHANGE,2, 0,
					// 0).sendToTarget();
					// BluetoothCommmanager.strAddress ="";
					BluetoothCommmanager.ConnectDevice = false;
				}

				// mHandler.sendBroadcast(device.getName() +
				// " was disconnected: " + device.getAddress(),
				// MESSAGE_STATE_CHANGE, -1);
			} else if (action.equals(SCAN_STATUS)) { // 搜索蓝牙
				String nTimeOut = intent.getExtras().getString(
						BluetoothCommmanager.DEVICE_ADDRESS);
				WAIT_SCANTIMEOUT = Integer.parseInt(nTimeOut);
				WAIT_SCANTIMEOUT = WAIT_SCANTIMEOUT * 1000;
				NAME_FILETER = intent.getExtras().getStringArray(
						BluetoothCommmanager.NAME_FILTER);
				bScan = true;
				for (String str : NAME_FILETER)
					System.out.println("开始搜索,搜索时间: " + nTimeOut + "  秒,过滤标志:"
							+ str);

				mNewDevicesArrayAdapter.clear();
				if (mExTimeOutTimer != null) {
					mExTimeOutTimer.cancel();
					mExTimeOutTimer = null;
				}
				mBtAdapter = BluetoothAdapter.getDefaultAdapter();
				if (!mBtAdapter.isEnabled()) {
					mBtAdapter.enable();

				}
				if (!mBtAdapter.isEnabled()) {
					return;
				}

				// Get a set of currently paired devices
				Set<BluetoothDevice> pairedDevices = mBtAdapter
						.getBondedDevices();
				/*
				 * int nCount=0; // If there are paired devices, add each one to
				 * the ArrayAdapter if (pairedDevices.size() > 0) { for
				 * (BluetoothDevice device : pairedDevices) {
				 * 
				 * for (String str : NAME_FILETER) { if
				 * (device.getName().length() >=str.length()) { if
				 * (device.getName().subSequence(0,str.length()).equals(str)) {
				 * // mNewDevicesArrayAdapter.add(device.getName() + "|" +
				 * device.getAddress()); // mNewDevicesArrayAdapter.add(device);
				 * nCount =0; for (BluetoothDevice Listdevice :
				 * mNewDevicesArrayAdapter) { if
				 * (Listdevice.getAddress().equals(device.getAddress())) nCount
				 * =1; } if (nCount ==0) { mNewDevicesArrayAdapter.add(device);
				 * // System.out.println("搜索: "+device.getName() +"==="
				 * +device.getAddress()); }
				 * 
				 * } }
				 * 
				 * } } } else {
				 * 
				 * }
				 */

				if (mBtAdapter.isDiscovering()) {
					mBtAdapter.cancelDiscovery();
				}

				// Request discover from BluetoothAdapter
				mBtAdapter.startDiscovery();

				mExTimeOutTimer = new Timer(true);
				mExTimeOutTimer.schedule(new TimerTask() {
					@Override
					public void run() {
						// TODO Auto-generated method stub
						if (mBtAdapter.isDiscovering()) {
							mBtAdapter.cancelDiscovery();
						}
						bScan = false;
						if (mHandler != null && mBtAdapter.isEnabled())
							mHandler.obtainMessage(
									BluetoothConnController.MSG_BLUE_SCAN, 0,
									0, mNewDevicesArrayAdapter).sendToTarget();
						System.out.println("搜索时间到,返回列表");

					}
				}, WAIT_SCANTIMEOUT);

			} else if (action.equals(DISSCAN_STATUS)) { // 断开搜索

				if (mExTimeOutTimer != null) {
					mExTimeOutTimer.cancel();
					mExTimeOutTimer = null;
				}
				if (mBtAdapter != null && mBtAdapter.isDiscovering()) {
					mBtAdapter.cancelDiscovery();
				}
				bScan = false;
				/*
				 * if (mBtAdapter !=null && mHandler !=null &&
				 * mBtAdapter.isEnabled())
				 * mHandler.obtainMessage(BluetoothConnController.MSG_BLUE_SCAN,
				 * 0, 0, mNewDevicesArrayAdapter).sendToTarget();
				 */
				System.out.println("停止搜索,返回列表");
			} else if (action.equals(BluetoothDevice.ACTION_FOUND)) { // 搜索蓝牙

				BluetoothDevice device = intent
						.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
				// If it's already paired, skip it, because it's been listed
				// already
				int nCount = 0;
				// if (device.getBondState() != BluetoothDevice.BOND_BONDED) {
				// �޸�һ�£������豸JHLM60����JHLA60
				// if (device.getName().subSequence(0,3).equals("JHL")){
				// mNewDevicesArrayAdapter.add(device.getName() + "|" +
				// device.getAddress());
				// mNewDevicesArrayAdapter.add(device);

				// }
				if (bScan) {
					for (String str : NAME_FILETER) {
						if (device.getName().length() >= str.length()) {
							if (device.getName().subSequence(0, str.length())
									.equals(str)) {
								// mNewDevicesArrayAdapter.add(device.getName()
								// + "|" + device.getAddress());
								// mNewDevicesArrayAdapter.add(device);
								nCount = 0;
								for (BluetoothDevice Listdevice : mNewDevicesArrayAdapter) {
									if (Listdevice.getAddress().equals(
											device.getAddress()))
										nCount = 1;
								}
								if (nCount == 0) {
									mNewDevicesArrayAdapter.add(device);
									// System.out.println("搜索: "+device.getName()
									// +"===" +device.getAddress());
								}

							}
						}

					}
				} else {

				}

				// }
			} else if (action
					.equals(BluetoothAdapter.ACTION_DISCOVERY_FINISHED)) { // 搜索蓝牙

				if (mNewDevicesArrayAdapter.size() == 0) {
					// String noDevices =
					// getResources().getText(R.string.none_found).toString();
					// mNewDevicesArrayAdapter.add(noDevices);
				}
			} else if (action.equals(BluetoothDevice.ACTION_BOND_STATE_CHANGED)) {

				BluetoothDevice device = intent
						.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
				switch (device.getBondState()) {
				case BluetoothDevice.BOND_BONDING: // 正在配对,
					break;
				case BluetoothDevice.BOND_BONDED: // 配对完成
					if (mExTimeOutTimer != null) {
						mExTimeOutTimer.cancel();
						mExTimeOutTimer = null;
					}
					connectDeviceTo(device);
					break;
				case BluetoothDevice.BOND_NONE: // 绑定

					break;
				}

			} else {
				Log.e(TAG, "another action: " + action);
			}
		}

	}

}
