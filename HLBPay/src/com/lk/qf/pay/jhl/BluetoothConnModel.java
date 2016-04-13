/*
 * Copyright (C) 2011 Wireless Network and Multimedia Laboratory, NCU, Taiwan
 * 
 * You can reference http://wmlab.csie.ncu.edu.tw
 * 
 * This class is used to process connection operation, including server side or client side. * 
 * 
 * @author Fiona
 * @version 0.0.1
 *
 */

package com.lk.qf.pay.jhl;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.util.Log;

public class BluetoothConnModel {
	// Debugging
	private static final boolean D = false;
	private static final String TAG = "BluetoothConnModel";
	private static final String NAME = "BluetoothConn";
	private static final UUID CUSTOM_UUID = UUID
			.fromString("00001101-0000-1000-8000-00805F9B34FB");
	// private static final UUID CUSTOM_UUID =
	// UUID.fromString("e5b152ed-6b46-09e9-4678-665e9a972cbc");
	public static final String MONITOR_OUTPUT_NAME = "output.txt";

	private final BluetoothAdapter mAdapter;
	private final Handler mHandler;
	private final Context mContext;
	// private Map<BluetoothDevice, BluetoothSocketConfig> mBluetoothSocekts;
	private ServerSocketThread mServerSocketThread;
	private BluetoothSocketConfig mSocketConfig = null;
	private FileOutputStream mOutputFile;
	private boolean mMonitor = false;
	private int mTxBytes = 0;
	private int mRxBytes = 0;
	private int mMonitorBytes = 0;
	private int mRecount = 0;
	private static Timer mExTimeOutTimer = null;
	private static int WAIT_TIMEOUT = 16000; // 超时10秒
	private static byte[] readbuffer = new byte[1024];

	public int getTxBytes() {
		return mTxBytes;
	}

	public int getRxBytes() {
		return mRxBytes;
	}

	public boolean getFileMonitor() {
		return mMonitor;
	}

	public void startFileMonitor(boolean b) {
		Log.d(TAG, "startFileMonitor " + b);
		mMonitor = b;
		if (mMonitor == true) {
			File root = Environment.getExternalStorageDirectory();
			try {
				mOutputFile = new FileOutputStream(root + "/"
						+ MONITOR_OUTPUT_NAME, false);
			} catch (Exception e) {
				Log.e(TAG, "new FileOutputStream fail", e);
			}
		} else {
			try {
				mOutputFile.close();
			} catch (Exception e) {

			}
		}
	}

	public BluetoothConnModel(Context context, Handler handler) {
		mAdapter = BluetoothAdapter.getDefaultAdapter();
		mHandler = handler;
		mContext = context;
		// mBluetoothSocekts = new HashMap<BluetoothDevice,
		// BluetoothSocketConfig> ();

	}

	public synchronized void startSession() {
		if (D)
			Log.d(TAG, "[startSession] ServerSocketThread start...");

		if (mServerSocketThread == null) {
			Log.i(TAG, "[startSession] mServerSocketThread is dead");
			mServerSocketThread = new ServerSocketThread();
			mServerSocketThread.start();
		} else {
			Log.i(TAG, "[startSession] mServerSocketThread is alive : " + this);
		}

		mSocketConfig = BluetoothSocketConfig.getInstance();
	}

	public synchronized void connectTo(BluetoothDevice device) {
		if (D)
			Log.d(TAG, "[connectTo] ClientSocketThread start...");
		SocketThread mSocketThread = new SocketThread(device);
		mSocketThread.start();
	}

	public synchronized void connected(BluetoothSocket socket) {
		if (D)
			Log.d(TAG, "[connected]");
		// BluetoothSocketConfig socketConfig = new BluetoothSocketConfig();
		// notifyUiFromToast(socket.getRemoteDevice().getName()+" has connected.");

		// mHandler.obtainMessage(BluetoothConnController.MESSAGE_ALERT_DIALOG,
		// -1, -1,
		// socket.getRemoteDevice().getName()+" has connected.").sendToTarget();
		ConnectedThread connectedThread = new ConnectedThread(socket);
		if (mSocketConfig.registerSocket(socket, connectedThread,
				BluetoothSocketConfig.SOCKET_CONNECTED) == false) {
			if (mHandler != null)
				mHandler.obtainMessage(
						BluetoothConnController.MESSAGE_ALERT_DIALOG, -1, -1,
						"Device link back again!").sendToTarget();
		}
		Log.e(TAG,
				"[connected] connectedThread hashcode = "
						+ connectedThread.toString());
		// socketConfig.setBluetoothSocket(socket);
		// //
		// socketConfig.setSocketState(BluetoothSocketConfig.SOCKET_CONNECTED);
		// socketConfig.setConnectedThread(connectedThread);
		// mBluetoothSocekts.put(socket.getRemoteDevice(), socketConfig);

		if (mHandler != null) {
			mHandler.obtainMessage(
					BluetoothConnController.MESSAGE_STATE_CHANGE, 1, 0, 0)
					.sendToTarget();
			if (mExTimeOutTimer != null) {
				mExTimeOutTimer.cancel();
				mExTimeOutTimer = null;
			}
		}
		connectedThread.start();
	}

	/*
	 * public void write(byte[] out) { Set<BluetoothDevice> devices = null; for
	 * (BluetoothDevice device : mBluetoothSocekts.keySet()) { BluetoothSocket
	 * btSocket= socketConfig.getBluetoothSocket(); if
	 * (!devices.contains(device)) devices.add(device); }
	 * writeToSockets(sockets, out); }
	 */

	public void writecmd(byte[] cmdData) {
		if (D)
			Log.d(TAG, "writecmd writeToAllDevices start...");
		for (BluetoothSocket socket : mSocketConfig.getConnectedSocketList()) {
			synchronized (this) {
				// if (mState != STATE_CONNECTED) return;
				if (BluetoothCommmanager.ConnectDevice) {
					writeToSocketCmd(socket, cmdData);
					Log.e(TAG,
							"[writeToAllDevices] writecmd currentTimeMillis: "
									+ System.currentTimeMillis());
				}
			}
		}
	}

	public void SendFileToSocket(BluetoothSocket socket, String file) {
		SendFileThread sendFile = new SendFileThread(socket, file);
		sendFile.start();
	}

	public void SendFileToAllSockets(String file) {
		if (D)
			Log.d(TAG, "SendFileAllSockets start...");
		for (BluetoothSocket socket : mSocketConfig.getConnectedSocketList()) {
			synchronized (this) {
				// if (mState != STATE_CONNECTED) return;
				SendFileToSocket(socket, file.toString());
			}
		}
	}

	public void writeToSocketCmd(BluetoothSocket socket, byte[] cmdData) {
		if (D)
			Log.d(TAG, "writeToDevice start...");
		ConnectedThread connectedThread = mSocketConfig
				.getConnectedThread(socket);
		Log.e(TAG, "[writeToDevice] connectedThread hashcode = "
				+ connectedThread.toString());
		if (mSocketConfig.isSocketConnected(socket)) {
			Log.w(TAG, "[writeToDevice] The socket is alived.");
			connectedThread.writecmd(cmdData);
			// writeToSocketCmd(socket,cmdData);

		} else
			Log.w(TAG, "[writeToDevice] The socket has been closed.");
	}

	public void writeToSocket(BluetoothSocket socket, String out) {
		if (D)
			Log.d(TAG, "writeToDevice start...");
		ConnectedThread connectedThread = mSocketConfig
				.getConnectedThread(socket);
		Log.e(TAG, "[writeToDevice] connectedThread hashcode = "
				+ connectedThread.toString());
		if (mSocketConfig.isSocketConnected(socket)) {
			Log.w(TAG, "[writeToDevice] The socket is alived.");
			connectedThread.write(out);

		} else
			Log.w(TAG, "[writeToDevice] The socket has been closed.");
	}

	public void writeToSockets(Set<BluetoothSocket> sockets, String out) {
		if (D)
			Log.d(TAG, "writeToDevices start...");
		for (BluetoothSocket socket : sockets) {
			synchronized (this) {
				// if (mState != STATE_CONNECTED) return;
				writeToSocket(socket, out);
			}
		}
	}

	public void writeToAllSockets(String out) {
		if (D)
			Log.d(TAG, "writeToAllDevices start...");
		for (BluetoothSocket socket : mSocketConfig.getConnectedSocketList()) {
			synchronized (this) {
				// if (mState != STATE_CONNECTED) return;
				writeToSocket(socket, out);
				Log.e(TAG,
						"[writeToAllDevices] currentTimeMillis: "
								+ System.currentTimeMillis());
			}
		}
	}

	public void disconnectServerSocket() {
		Log.d(TAG, "[disconnectServerSocket] ----------------");
		/*
		 * try { serverSocket.close(); Log.w(TAG,
		 * "[disconnectServerSocket] Close "+serverSocket.toString()); } catch
		 * (IOException e) { Log.e(TAG, "close() of server failed", e); }
		 */

		if (mServerSocketThread != null) {
			mServerSocketThread.disconnect();
			mServerSocketThread = null;
			Log.w(TAG, "[disconnectServerSocket] NULL mServerSocketThread");
		}
	}

	public void disconnectSocketFromAddress(String address) {
		Set<BluetoothSocket> socketSets = mSocketConfig.containSockets(address);
		for (BluetoothSocket socket : socketSets) {
			disconnectSocket(socket);
		}
	}

	public synchronized void disconnectSocket(BluetoothSocket socket) {
		Log.w(TAG, "[disconnectSocket] ------------------" + socket.toString()
				+ " ; device name is " + socket.getRemoteDevice().getName());
		if (!mSocketConfig.isSocketConnected(socket)) { // 锟给锟教┣┣拷...锟絖锟絬锟缴筹拷锟絴锟絔锟絠锟斤拷aaa
														// (锟绊锟絔锟斤拷锟斤拷锟狡禲锟絠锟接锟解Ω,
														// 锟侥锟斤拷锟絅 always
														// 锟絠锟斤拷, exception
														// 锟斤拷锟斤拷Y)
			Log.w(TAG,
					"[disconnectSocket] mSocketConfig doesn't contain the socket: "
							+ socket.toString() + " ; device name is "
							+ socket.getRemoteDevice().getName());
			return;
		}
		// BluetoothSocket bluetoothSocket =
		// mBluetoothSocekts.get(device).getBluetoothSocket();
		Log.d(TAG, socket.getRemoteDevice().getName()
				+ " connection was disconnected!");
		// notifyUiFromToast(socket.getRemoteDevice().getName()+" connection was lost");
		try {
			Thread.sleep(10);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		mSocketConfig.unregisterSocket(socket);
	}

	public void terminated() {
		Log.w(TAG, "[terminated] --------------");

		disconnectServerSocket();
		for (BluetoothSocket socket : mSocketConfig.getConnectedSocketList()) {
			Log.w(TAG, "[terminated] Left Socket(s): "
					+ mSocketConfig.getConnectedSocketList().size());
			disconnectSocket(socket);
		}
		/*
		 * if (mSocketConfig.getConnectedSocketList().size()>0) { try {
		 * mBluetoothSocekts.clear(); }catch(UnsupportedOperationException e) {
		 * Log.i(TAG, "[terminated] Clear Socket Map error."); } }
		 */
		Log.w(TAG, "[terminated] Final Left Socket(s): "
				+ mSocketConfig.getConnectedSocketList().size());
	}

	/*
	 * public synchronized void stop() { if (D) Log.d(TAG, "stop");
	 * ConnectedThread tmpThread; int i = 0; for (BluetoothSocket socket :
	 * mBluetoothSocekts.keySet()) { synchronized (this) { // if (mState !=
	 * STATE_CONNECTED) return; tmpThread = mBluetoothSocekts.get(socket); if
	 * (tmpThread!= null) { tmpThread.cancel(); ++i; Log.w(TAG,
	 * "[stop] Close "+i+" socket(s)"); tmpThread = null; Log.w(TAG,
	 * "[stop] Stop the thread"); } mBluetoothSocekts.remove(socket);
	 * 
	 * } } // if (mAcceptThread != null) {mAcceptThread.cancel(); mAcceptThread
	 * = null; Log.w(TAG, "[stop] Killed mAcceptThread");}
	 * 
	 * if (mServerSocketThread != null) { // try { mServerSocketThread.cancel();
	 * // mServerSocketThread.thread.join(); mServerSocketThread = null;
	 * Log.w(TAG, "[stop] Killed mServerSocketThread"); // }catch
	 * (InterruptedException e){ // Log.w(TAG,
	 * "[stop] mServerSocketThread close error" + e); // } } mBluetoothSocekts =
	 * null;
	 * 
	 * }
	 */
	private void notifyUiFromToast(String str) {
		// Log.d(TAG, "test123 " + str);
		if (mHandler != null) {
			Message msg = mHandler
					.obtainMessage(BluetoothConnController.MESSAGE_TOAST);
			Bundle bundle = new Bundle();
			bundle.putString(BluetoothConnController.TOAST, str);
			msg.setData(bundle);
			mHandler.sendMessage(msg);
		}
	}

	private class ServerSocketThread implements Runnable {
		private BluetoothServerSocket mmServerSocket = null;
		private Thread thread = null;
		private boolean isServerSocketValid = false;

		// private final ExecutorService pool;
		public ServerSocketThread() {
			this.thread = new Thread(this);

			BluetoothServerSocket serverSocket = null;
			try {
				Log.i(TAG,
						"[ServerSocketThread] Enter the listen server socket");
				// issc
				if (DeviceDependency.shouldUseSecure()) {
					serverSocket = mAdapter.listenUsingRfcommWithServiceRecord(
							NAME, CUSTOM_UUID);
				} else {
					serverSocket = mAdapter
							.listenUsingInsecureRfcommWithServiceRecord(NAME,
									CUSTOM_UUID);
				}
				Log.i(TAG, "[ServerSocketThread] serverSocket hash code = "
						+ serverSocket.hashCode());
				isServerSocketValid = true;

			} catch (IOException e) {
				Log.e(TAG,
						"[ServerSocketThread] Constructure: listen() failed", e);
				e.printStackTrace();
				notifyUiFromToast("Listen failed. Restart application again");
				isServerSocketValid = false;
				mServerSocketThread = null;
				// BluetoothConnService.this.startSession();
			}
			mmServerSocket = serverSocket;

			if (mmServerSocket != null) {
				String serverSocketName = mmServerSocket.toString();
				Log.i(TAG, "[ServerSocketThread] serverSocket name = "
						+ serverSocketName);
			} else {
				Log.i(TAG, "[ServerSocketThread] serverSocket = null");
			}
		}

		public void start() {
			this.thread.start();
		}

		public void run() {
			if (D)
				Log.d(TAG, "BEGIN ServerSocketThread " + this
						+ ", thread id = ");
			BluetoothSocket socket = null;

			while (isServerSocketValid) {
				try {
					Log.i(TAG, "[ServerSocketThread] Enter while loop");
					Log.i(TAG, "[ServerSocketThread] serverSocket hash code = "
							+ mmServerSocket.hashCode());
					socket = mmServerSocket.accept();

					Log.i(TAG, "[ServerSocketThread] Got client socket");
				} catch (IOException e) {
					Log.e(TAG, "accept() failed", e);
					break;
				}

				if (socket != null) {
					synchronized (BluetoothConnModel.this) {
						Log.i(TAG,
								"[ServerSocketThread] "
										+ socket.getRemoteDevice()
										+ " is connected.");

						connected(socket);
						/*
						 * if (mServerSocketThread != null) {
						 * mServerSocketThread = null; Log.w(TAG,
						 * "[ServerSocketThread] NULL mServerSocketThread"); }
						 */
						BluetoothConnModel.this.disconnectServerSocket();
						break;
					}
				}
			}
			Log.i(TAG, "[ServerSocketThread] break from while");
			BluetoothConnModel.this.startSession();
		}

		public void disconnect() {
			if (D)
//				Log.d(TAG, "[ServerSocketThread] disconnect " + this);
			try {
//				Log.i(TAG,
//						"[ServerSocketThread] disconnect serverSocket name = "
//								+ mmServerSocket.toString());
				mmServerSocket.close();
//				Log.i(TAG, "[ServerSocketThread] mmServerSocket is closed.");
			} catch (IOException e) {
//				Log.e(TAG, "close() of server failed", e);
			}
		}

	}

	private class SocketThread implements Runnable {
		private final BluetoothSocket mmSocket;
		private final BluetoothDevice mmDevice;
		private Thread thread = null;

		// private final ExecutorService pool;
		public SocketThread(BluetoothDevice device) {
			this.thread = new Thread(this);
			Log.i(TAG, "[SocketThread] Enter these server sockets");
			mmDevice = device;
			BluetoothSocket tmp = null;

			// Get a BluetoothSocket for a connection with the given
			// BluetoothDevice
			try {
				if (DeviceDependency.shouldUseFixChannel()) {
					Method m;
					try {
						m = device.getClass().getMethod(
								"createInsecureRfcommSocket",
								new Class[] { int.class });
						tmp = (BluetoothSocket) m.invoke(device, 6);
					} catch (SecurityException e1) {
						e1.printStackTrace();
					} catch (NoSuchMethodException e1) {
						e1.printStackTrace();
					} catch (IllegalArgumentException e) {
						e.printStackTrace();
					} catch (IllegalAccessException e) {
						e.printStackTrace();
					} catch (InvocationTargetException e) {
						e.printStackTrace();
					}
				}
				// issc
				else if (DeviceDependency.shouldUseSecure()) {
					tmp = device.createRfcommSocketToServiceRecord(CUSTOM_UUID);
				} else {
					tmp = device
							.createInsecureRfcommSocketToServiceRecord(CUSTOM_UUID);
				}
				/* issc2.1 */
				/*
				 * Method m =
				 * mmDevice.getClass().getMethod("createInsecureRfcommSocket"
				 * ,new Class[] { int.class }); tmp =
				 * (BluetoothSocket)m.invoke(mmDevice,6);
				 */
				Log.i(TAG,
						"[SocketThread] Constructure: Get a BluetoothSocket for a connection, create Rfcomm");
			} catch (Exception e) {
				Log.e(TAG, "create() failed", e);
			}
			mmSocket = tmp;

		}

		public void start() {
			this.thread.start();
		}

		public void run() {
			if (D)
				Log.d(TAG, "BEGIN SocketThread" + this);
			int connectTimes = 0;
			mAdapter.cancelDiscovery();
			// Make a connection to the BluetoothSocket

			if (mExTimeOutTimer != null) {
				mExTimeOutTimer.cancel();
				mExTimeOutTimer.purge();
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

				}
			}, WAIT_TIMEOUT);

			while (true) {

				try {
					// This is a blocking call and will only return on a
					// successful connection or an exception
					// issc
					/*
					 * issc2.1 Method createBondMethod =
					 * mmDevice.getClass().getMethod("createBond");
					 * createBondMethod.invoke(mmDevice);
					 */

					mmSocket.connect();

					Log.i(TAG, "[SocketThread] Return a successful connection");
				} catch (Exception e) {
					// notifyUiFromToast("Unable to connect device: "+mmDevice.getName());
					Log.i(TAG, "[SocketThread] Connection failed", e);
					// Close the socket
					try {
						if (connectTimes < 2) {
							connectTimes = connectTimes + 1;
							continue;
						}
						mmSocket.close();
						Log.i(TAG,
								"[SocketThread] Connect fail, close the client socket");
						// if (mHandler !=null)
						// mHandler.obtainMessage(BluetoothConnController.MESSAGE_STATE_CHANGE,
						// 0, 0, 0).sendToTarget();

					} catch (IOException e2) {
						Log.e(TAG,
								"unable to close() socket during connection failure",
								e2);
					} catch (Exception e1) {
						e1.printStackTrace();
					}

					// disconnectSocket(mmSocket);
					// Start the service over to restart listening mode
					// BluetoothChatService.this.start();
					this.thread = null;
					return;
				}
				break;
			}

			// Reset the ConnectThread because we're done
			synchronized (BluetoothConnModel.this) {
				try {
					Thread.sleep(10);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				connected(mmSocket);

				// if (mHandler !=null)
				// mHandler.obtainMessage(BluetoothConnController.MESSAGE_STATE_CHANGE,
				// 1, 0, 0).sendToTarget();

				Log.i(TAG, "[SocketThread 11] " + mmDevice + " is connected.");

				/*
				 * byte[] cmd =new byte[4]; cmd[0] =0x00; cmd[1] =0x02; cmd[2]
				 * =(byte) 0xA0; cmd[3] =0x01; writecmd(cmd); if (D) Log.i(TAG,
				 * "write mConnectThread cmd"); try { Thread.sleep(5); } catch
				 * (InterruptedException e) { // TODO Auto-generated catch block
				 * e.printStackTrace(); }
				 */
			}

			this.thread = null;
			if (D)
				Log.i(TAG, "END mConnectThread");

		}

		/*
		 * public void cancel() { if (D) Log.d(TAG, "cancel " + this); try {
		 * mmSocket.close(); Log.i(TAG, "[SocketThread] mmSocket is closed."); }
		 * catch (IOException e) { Log.e(TAG,
		 * "[SocketThread] close() of client failed", e); } }
		 */

	}

	public class ConnectedThread implements Runnable {
		protected BluetoothSocket mmSocket;
		private InputStream mmInStream;
		private OutputStream mmOutStream;
		private Thread thread = null;

		private ConnectedThread(BluetoothSocket socket) {
			this.thread = new Thread(this, socket.getRemoteDevice().toString());
			mmSocket = socket;
			InputStream tmpIn = null;
			OutputStream tmpOut = null;

			try {
				tmpIn = socket.getInputStream();
				tmpOut = socket.getOutputStream();
				Log.i(TAG,
						"[ConnectedThread] Constructure: Set up bluetooth socket i/o stream");
			} catch (IOException e) {
				Log.e(TAG, "[ConnectedThread] temp sockets not created", e);
			}

			mmInStream = tmpIn;
			mmOutStream = tmpOut;

		}

		public void start() {
			this.thread.start();
		}

		public void run() {
			if (D)
				Log.d(TAG, "BEGIN ConnectedThread" + this);
			byte[] buffer = new byte[1024];
			int bytes = 0, nTotallen = 0;
			mTxBytes = mRxBytes = 0;
			// int itmp = 0;
			while (mSocketConfig.isSocketConnected(mmSocket)) {
				/*
				 * try { if (D) Log.d(TAG, "Socket available"); }
				 * catch(Exception e){
				 * mHandler.obtainMessage(BluetoothConnController
				 * .MESSAGE_ALERT_DIALOG, -1, -1,
				 * "Exception during available()\n" + e).sendToTarget();
				 * disconnectSocket(mmSocket); Log.e(TAG,
				 * "Exception during available()\n", e); break;
				 * 
				 * }
				 */
				try {
					Arrays.fill(buffer, (byte) 0);
					bytes = mmInStream.read(buffer);
					if (bytes <= 0) {
						try {
							Thread.sleep(10);
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						continue;
					}
					if (D) {
						StringBuilder sb = new StringBuilder();
						for (int i = 0; i < bytes; ++i) {
							sb.append(String.format("%02x", buffer[i]));
						}
						Log.e("mmInStream read", sb.toString());
					}

					if (buffer[0] == 0x00 && buffer[1] == 0x02
							&& buffer[2] == 0x22 && buffer[3] == 0x46) {
						BluetoothCommmanager.ConnectDevice = false;
						Log.e("mmInStream read",
								"BluetoothCommmanager.ConnectDevice=false");
					}

					int nlen = buffer[2] & 0xff;
					if (bytes == 2 && (buffer[0] & 0xff) == 0x00
							&& (buffer[1] & 0xff) == 0x02
							&& (buffer[2] & 0xff) == 0xA0) // 如果为ACK则直接返回
					{

						// if (mHandler !=null)
						// mHandler.obtainMessage(BluetoothConnController.MESSAGE_READ,
						// bytes, bytes, buffer).sendToTarget();

						/*
						 * Message msg = new Message(); Bundle data = new
						 * Bundle(); data.putByteArray("keydata", buffer);
						 * msg.what = BluetoothConnController.MESSAGE_READ;
						 * msg.arg1 =bytes; msg.arg2 =bytes; msg.setData(data);
						 * mHandler.sendMessage(msg);
						 */
						try {
							Thread.sleep(100);
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						Arrays.fill(buffer, (byte) 0);
						Arrays.fill(readbuffer, (byte) 0);
						mRxBytes = nTotallen = mRecount = 0;
						continue;
					}

					System.arraycopy(buffer, 0, readbuffer, mRxBytes, bytes);

					if (mRxBytes == 0) // 判断为第一包数据,则取第二个字节为大小
					{
						nTotallen = readbuffer[0] & 0xFF;
						nTotallen <<= 8;
						nTotallen |= readbuffer[1] & 0xFF; // 需要接收的总的大小
					}

					mRxBytes = mRxBytes + bytes;
					if (nTotallen == 0) // 重发数据
					{
						continue;
					}
					nlen = mRxBytes;
					if (nTotallen + 2 > nlen) {
						try {
							Thread.sleep(10);
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}

						continue;
					} else {

						if (mExTimeOutTimer != null) {
							mExTimeOutTimer.cancel();
							mExTimeOutTimer = null;
						}

						if (mHandler != null)
							mHandler.obtainMessage(
									BluetoothConnController.MESSAGE_READ,
									bytes, mRxBytes, readbuffer).sendToTarget();

						/*
						 * Message msg = new Message(); Bundle data = new
						 * Bundle(); data.putByteArray("keydata", readbuffer);
						 * msg.what = BluetoothConnController.MESSAGE_READ;
						 * msg.arg1 =mRxBytes; msg.arg2 =mRxBytes;
						 * msg.setData(data); mHandler.sendMessage(msg);
						 */

						try {
							Thread.sleep(100);
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						Arrays.fill(buffer, (byte) 0);
						Arrays.fill(readbuffer, (byte) 0);
						mRxBytes = nTotallen = mRecount = 0;
					}

				} catch (IOException e) {

					Log.e(TAG, "[ConnectedThread] connection lost", e);
					// mHandler.obtainMessage(BluetoothConnController.MESSAGE_ALERT_DIALOG,
					// 1, -1, "Exception during read()\n" + e).sendToTarget();
					disconnectSocket(mmSocket);
					Log.w(TAG, "[ConnectedThread] disconnect the socket");

					BluetoothCommmanager.ConnectDevice = false;
					// e.printStackTrace();
					// notifyUiFromToast(mmSocket.getRemoteDevice().getName()+" was disconnected.");
					// if (mHandler !=null)
					// mHandler.obtainMessage(BluetoothConnController.MESSAGE_ALERT_DIALOG,
					// -1, 2,
					// mmSocket.getRemoteDevice().getName()+" was disconnected.").sendToTarget();

					break;
				}

			}
			// notifyUiFromToast("Socket is disconnected");
			// if (!socketConfig.isSocketConnected()) {
			// disconnectSocket(mmSocket.getRemoteDevice());
			// / Log.w(TAG, "[ConnectedThread] disconnect the socket");
			// }
			if (D)
				Log.i(TAG, "[ConnectedThread] break from while");

		}

		public boolean write(String msg) {
			try {
				mTxBytes += msg.length();
				mmOutStream.write(msg.toString().getBytes());
				// Share the sent message back to the UI Activity
				if (mHandler != null)
					mHandler.obtainMessage(
							BluetoothConnController.MESSAGE_WRITE, -1,
							mTxBytes, msg.toString().getBytes()).sendToTarget();

			} catch (IOException e) {
				Log.e(TAG, "[ConnectedThread] Exception during write", e);
				if (mHandler != null)
					mHandler.obtainMessage(
							BluetoothConnController.MESSAGE_ALERT_DIALOG, 1,
							-1, "Exception during write\n" + e).sendToTarget();
				return false;
			}
			return true;
		}

		public boolean writecmd(byte[] cmdData) {
			try {
				mTxBytes += cmdData.length;
				mmOutStream.write(cmdData);
				// Share the sent message back to the UI Activity
				if (mHandler != null)
					mHandler.obtainMessage(
							BluetoothConnController.MESSAGE_WRITE, 0, mTxBytes,
							cmdData).sendToTarget();

				try {
					Thread.sleep(10);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			} catch (IOException e) {
				Log.e(TAG, "[ConnectedThread] Exception during write", e);
				if (mHandler != null)
					mHandler.obtainMessage(
							BluetoothConnController.MESSAGE_ALERT_DIALOG, -1,
							-1, "Exception during write\n" + e).sendToTarget();
				return false;
			}
			return true;
		}

		/*
		 * public void cancel() { try { if (mmInStream != null){
		 * mmInStream.close(); } if (mmOutStream != null){ mmOutStream.close();
		 * } if (mmSocket != null) { mmSocket.close(); Log.w(TAG,
		 * "[ConnectedThread] close() bluetooth socket"); } } catch (IOException
		 * e) { Log.e(TAG, "[ConnectedThread] close() of connect socket failed",
		 * e); } }
		 */

	}

	public class SendFileThread extends ConnectedThread {
		private String fileName;

		private SendFileThread(BluetoothSocket socket, String file) {
			super(socket);
			fileName = file;
			Log.d(TAG, "SendFileThread Create: " + file);
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see com.wmlab.bluetoothconn.BluetoothConnModel.ConnectedThread#run()
		 */
		@Override
		public void run() {
			// TODO Auto-generated method stub
			FileInputStream inputStream;
			if (D)
				Log.d(TAG, "BEGIN SendFileThread " + this);
			try {
				inputStream = new FileInputStream(fileName);
			} catch (Exception e) {
				Log.d(TAG, "Exception during new FileInputStream");
				return;
			}
			byte[] buffer = new byte[1024];
			int bytes = 0;
			while (mSocketConfig.isSocketConnected(mmSocket)) {
				try {
					bytes = inputStream.read(buffer, 0, 1024);
					if (bytes <= 0) { // -1: EOF
						if (mHandler != null)
							mHandler.obtainMessage(
									BluetoothConnController.MESSAGE_ALERT_DIALOG,
									-1, -1, "Send " + fileName + " completely")
									.sendToTarget();
						break;
					}
					Log.d(TAG, "length = " + bytes);
					String msg = new String(buffer, 0, bytes, "ISO-8859-1");
					if (write(msg) == false) {
						break;
					}
					Log.d(TAG, "[send file]write OK");
				} catch (Exception e) {
					Log.d(TAG, "[SendFile] Exception during send file", e);
					break;
				}

			}
		}

	}

}
