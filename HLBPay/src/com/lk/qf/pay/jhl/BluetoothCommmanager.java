package com.lk.qf.pay.jhl;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Formatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;

public class BluetoothCommmanager extends Activity {

	/*
	 * private ArrayList<String> mNewDevicesArrayAdapter = new
	 * ArrayList<String>(); private int WAIT_TIMEOUT =10000; //超时10秒 private
	 * static Timer mExTimeOutTimer = null;
	 */
	private static final int PASSWORD_INPUT_FLAG = 0x19;// 鏄惁杈揿叆瀵嗙爜镄勬爣璇?

	private static final int IC_MAG_GETSTATUE = 0x17;// 妫€娴娅C/纾佹浔鍗＄殑钟舵€?

	// Debugging
	private static final String TAG = "BluetoothConn";
	private static final boolean PrintLog = false;

	private static String versionNo = "1.0.4";

	// Key names received from the BluetoothChatService Handler
	public static final String DEVICE_NAME = "device_name";

	public static String strAddress = "";
	public static final String TOAST = "toast";

	public static final String NOTIFY_UI = "NOTIFY_UI";
	public static final String INCOMING_MSG = "INCOMING_MSG";
	public static final String OUTGOING_MSG = "OUTGOING_MSG";
	public static final String ALERT_MSG = "ALERT_MSG";
	public static final String KEY_ECHO_PREF = "KEY_ECHO_PREF";
	public static final String KEY_SHOW_RX_PREF = "KEY_SHOW_RX_PREF";
	public static final String DEVICE_ADDRESS = "device_address";
	public static final String NAME_FILTER = "name_filter";
	public static final String DISCONNECT_DEVICE_ADDRESS = "disconnected_device_address";

	private BluetoothAdapter mBluetoothAdapter = null;

	public static final String TEST_ACTION = "TEST_ACTION";

	private static BluetoothConnController mConnController = null;
	private static Context mContext;
	private static BluetoothCommmanager mInstance;
	private static MessageHandler msgHandler;

	public static BlueCommangerCallback mCallBackData = null;

	static boolean ConnectDevice = false;
	private static Object mWaitLock = new Object();

	// 闂备礁寮堕崹濂告倵阌燂拷
	public static int track2DataLen = 0;
	public static int track3DataLen = 0;
	public static int BrushCardMode = 0;
	static byte[] szTrack2 = new byte[64];
	static byte[] szTrack3 = new byte[128];
	static Map<String, String> hashmap = new HashMap<String, String>();

	private static final int EntryType = 0; // //==0 默认加密模式 1:北京尤银加密模式
	public static final int BRUSHDATA = 0x12;
	public static final int GETCARDDATA = 0x20;
	public static final int GETTRACKDATA_CMD = 0x22; // //操作状态 00成功 E1用户取消
														// E2超时退出 E3 IC卡处理失败
														// E4:无IC卡参数 E5：交易终止 E6：
														// 操作失败请重试
	public static final int GETENCARDDATA = 0xff;
	public static final int MAINKEY = 0x34;
	public static final int WORKEY = 0x38;
	public static final int GETMAC = 0x37;
	public static final int GETSNVERSION = 0x40;
	public static final int GETTERNO = 0x41;
	public static final int SETTERNO = 0x42;
	public static final int CHECK_IC = 0x24;

	public static final int SETAIDPAMR = 0x33;
	public static final int CLEARAID = 0x3A;
	public static final int SETPUBKEYPARM = 0x32;
	public static final int ClEARPUBKEY = 0x39;
	public static final int ProofIcParm = 0x23;
	public static final int Battery = 0x45;
	public static final int IC_GETSTATUS = 0x13;
	public static final int IC_OPEN = 0x14;
	public static final int IC_CLOSE = 0x15;
	public static final int IC_WRITEAPDU = 0x16;
	public static final int ICBRUSH = 0x18;// 镓嫔埛阔抽鍒峰崱鍚庤繑锲炵殑鏁版嵁

	// 镓揿紑IC/纾佹浔鍗″懡浠?
	public static final int IC_OPEN_MAG = 0x17;

	private static final int MAIN_KEY_ID = 1;
	private static final int PIN_KEY_ID = 2;
	private static final int TRACK_KEY_ID = 3;
	private static final int MAC_KEY_ID = 4;

	// private static final int MAmount = 0x00;
	// 磁道加密算法，0表示银联标准的只加密后8字节，1/2表示不包含长度补0/F组成8的倍数据加密，3/4表示包含长度补0/F组成8的倍数据加密，5表示尤银特殊加密）
	// 6 通过主密钥加密磁道 7 三磁道为空的情况下,补全部FF
	// 8 米袋 磁道随机数全部加密

	private static int TRACK_ENCRY_MODEM = 0x06;
	// 密码加密，0表示标准主账号异或加密，1表示不带主账号加密，2尤银特殊加密 03 密文明文
	private static int PASSWORD_ENCRY_MODEM = 0x03;
	private static int TRACK_MAC_DATA = 0x03; // mac算法
	private static int MAC_3DES_DATA = 0x00; // ==00 DES加密 01 3DES加密
	private static int WORK_ENCRY_MODEM = 0x00; // 更新工作密钥 00 ==3des /// 01=X9.18

	private static final int TRACK_ENCRY_DATA = 0x00; // 0x00 标准数据回应 0x01 多包数据回应
	public static final int EXCHANGE_NOT_OPEN = -1;

	public static final int SUCCESS = 0;

	private class MessageHandler extends Handler {

		@SuppressWarnings("unchecked")
		@Override
		public void handleMessage(Message msg) {
			if (mCallBackData == null)
				return;
			switch (msg.what) {
			case BluetoothConnController.MESSAGE_STATE_CHANGE:
				ConnectDevice = false;
				if (msg.arg1 == 1)
					ConnectDevice = true;
				if (mCallBackData != null) {

					if (msg.arg1 == 0x01) // 閺夆昼锅炵敮鎾箣阉邦剙颤?
					{
						ConnectDevice = true;
					} else {
						ConnectDevice = false;
					}

					mCallBackData.onDeviceState(msg.arg1);
				}
				break;
			case BluetoothConnController.MESSAGE_DEVICE_NAME:

				break;
			case BluetoothConnController.MESSAGE_WRITE:

				break;
			case BluetoothConnController.MESSAGE_RWRITE_TIMEOUT:
				if (mCallBackData != null) {
					mCallBackData.onTimeout();
				}
				break;
			case BluetoothConnController.MESSAGE_READ:
				// 获取到数据,进行处理
				byte[] readBuf = new byte[msg.arg2];
				byte[] readDataBuf = new byte[msg.arg2 - 2];
				readBuf = (byte[]) msg.obj;
				// 去除2个字节大小
				System.arraycopy(readBuf, 2, readDataBuf, 0, msg.arg2 - 2);
				onReceive(readDataBuf);
				break;

			case BluetoothConnController.MESSAGE_TOAST:

				break;

			case BluetoothConnController.MESSAGE_ALERT_DIALOG:

				break;
			case BluetoothConnController.MSG_BLUE_SCAN:
				List<BluetoothDevice> mNewDevicesArrayAdapter = new ArrayList<BluetoothDevice>();
				mNewDevicesArrayAdapter = (ArrayList<BluetoothDevice>) msg.obj;
				if (mCallBackData != null) {
					mCallBackData.onDeviceFound(mNewDevicesArrayAdapter);
				}

				break;
			}
		}
	}

	public static BluetoothCommmanager getInstance(BlueCommangerCallback cb,
			Context context) {

		mContext = context;
		if (mInstance == null) {
			mInstance = new BluetoothCommmanager();

		}
		mCallBackData = cb;
		mConnController = BluetoothConnController.getInstance();
		if (mConnController != null)
			mConnController.setHandle(mInstance, msgHandler);
		if (mCallBackData == null)
			mConnController.setHandle(null, null);

		if (msgHandler != null && mConnController != null)
			mConnController.setMsgHandle(msgHandler);

		return mInstance;
	}

	public Boolean setHandle() {

		if (mInstance == null) {
			mInstance = new BluetoothCommmanager();
		}
		mConnController = BluetoothConnController.getInstance();
		if (mConnController == null) {
			return false;
		} else {
			mConnController.setHandle(mInstance, msgHandler);
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return true;
		}

	}

	public BluetoothCommmanager() {
		msgHandler = new MessageHandler();
		mInstance = this;
	}

	// 相关API函数

	/********************************************************************
	 * 函 数 名：CheckBlueDevice 功能描述：检查蓝牙是否存在 入口参数：
	 * 
	 * 返回说明： -1：蓝牙未开启 0:未找打MPOS设备 1:找到正在连接
	 **********************************************************/

	public int DisConnectBlueDevice() {

		int iRet = -1;
		if (ConnectDevice == false) {
			if (mCallBackData != null) {
				mCallBackData.onDeviceState(0x02);
			}
			return 0x00;
		}

		mConnController = BluetoothConnController.getInstance();
		if (mConnController != null)
			mConnController.setHandle(mInstance, msgHandler);
		// Get local Bluetooth adapter
		mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
		if (!mBluetoothAdapter.isEnabled()) {
			mBluetoothAdapter.enable();
			return iRet;
		}
		if (!mBluetoothAdapter.isEnabled()) {
			return iRet;
		}

		iRet = 0;
		// If the adapter is null, then Bluetooth is not supported
		if (mBluetoothAdapter == null) {
			// Toast.makeText(this, "Bluetooth is not available",
			// Toast.LENGTH_LONG).show();
			// finish();
			return iRet;
		}

		Set<BluetoothDevice> pairedDevices = mBluetoothAdapter
				.getBondedDevices();

		// If there are paired devices, add each one to the ArrayAdapter

		if (pairedDevices.size() > 0) {
			for (BluetoothDevice device : pairedDevices) {
				// mPairedDevicesArrayAdapter.add(device.getName() + "\n" +
				// device.getAddress());
				// Intent i = new
				// Intent(BluetoothConnController.CONNECT_REQUEST_ACTION);
				// i.putExtra(DEVICE_ADDRESS, address);
				// sendBroadcast(i);
				// if (device.getName().equals("JHLM60"))
				{
					iRet = 1;
					String address = device.getAddress();
					if (address.equals(strAddress))
					// if (address ==strAddress)
					{
						Intent i = new Intent(
								BluetoothConnController.DISCONNECT_REQUEST_ACTION);
						i.putExtra(DISCONNECT_DEVICE_ADDRESS, address);
						mContext.sendBroadcast(i);
						if (mCallBackData != null) {
							mCallBackData.onDeviceState(0x02);
						}
						BluetoothCommmanager.strAddress = "";
						// BluetoothCommmanager.ConnectDevice=false;

						break;
					}

				}

			}
		} else {
			iRet = 0;
		}

		return iRet;

	}

	public int CheckBlueDevice() {
		int iRet = -1;
		mConnController = BluetoothConnController.getInstance();
		if (mConnController != null)
			mConnController.setHandle(mInstance, msgHandler);
		// Get local Bluetooth adapter
		mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
		if (!mBluetoothAdapter.isEnabled()) {
			mBluetoothAdapter.enable();
			return iRet;
		}
		if (!mBluetoothAdapter.isEnabled()) {
			return iRet;
		}

		iRet = 0;
		// If the adapter is null, then Bluetooth is not supported
		if (mBluetoothAdapter == null) {
			// Toast.makeText(this, "Bluetooth is not available",
			// Toast.LENGTH_LONG).show();
			// finish();
			return iRet;
		}

		Set<BluetoothDevice> pairedDevices = mBluetoothAdapter
				.getBondedDevices();

		// If there are paired devices, add each one to the ArrayAdapter
		if (pairedDevices.size() > 0) {
			for (BluetoothDevice device : pairedDevices) {
				// mPairedDevicesArrayAdapter.add(device.getName() + "\n" +
				// device.getAddress());
				// Intent i = new
				// Intent(BluetoothConnController.CONNECT_REQUEST_ACTION);
				// i.putExtra(DEVICE_ADDRESS, address);
				// sendBroadcast(i);

				// if (device.getName().equals("JHLM60"))
				{
					iRet = 1;
					String address = device.getAddress();

					// Get the BLuetoothDevice object
					// BluetoothDevice device =
					// mBluetoothAdapter.getRemoteDevice(address);
					// Attempt to connect to the device
					// mConnService.connectTo(device);
					Intent in = new Intent(
							BluetoothConnController.CONNECT_REQUEST_ACTION);
					in.putExtra(DEVICE_ADDRESS, address);
					mContext.sendBroadcast(in);
				}

			}
		} else {
			iRet = 0;
		}

		return iRet;
	}

	public int ConnectDevice(String address) {
		int iRet = -1;
		if (ConnectDevice /* && address.equals(strAddress) */) {
			if (mCallBackData != null) {
				mCallBackData.onDeviceState(0x01);
			}
			return 0x00;
		}

		mConnController = BluetoothConnController.getInstance();
		if (mConnController != null){
			mConnController.setHandle(mInstance, msgHandler);
		}
		// Get local Bluetooth adapter
		mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
		if (!mBluetoothAdapter.isEnabled()) {
			mBluetoothAdapter.enable();
			return iRet;
		}
		if (!mBluetoothAdapter.isEnabled()) {
			return iRet;
		}

		iRet = 0;
		// If the adapter is null, then Bluetooth is not supported
		if (mBluetoothAdapter == null) {
			// Toast.makeText(this, "Bluetooth is not available",
			// Toast.LENGTH_LONG).show();
			// finish();
			return iRet;
		}
		strAddress = address;
		Intent in = new Intent(BluetoothConnController.CONNECT_REQUEST_ACTION);
		in.putExtra(DEVICE_ADDRESS, address);
		mContext.sendBroadcast(in);

		return iRet;
	}

	/*
	 * 搜蓝牙设备, nScanTimer 搜索时间 秒类型
	 */
	public int ScanDevice(String[] namFilter, int nScanTimer) {

		Intent in = new Intent(BluetoothConnController.SCAN_STATUS);
		in.putExtra(DEVICE_ADDRESS, Integer.toString(nScanTimer));
		in.putExtra(NAME_FILTER, namFilter);
		mContext.sendBroadcast(in);
		return 0x00;

	}

	public int DisScanDevice() {
		Intent in = new Intent(BluetoothConnController.DISSCAN_STATUS);
		mContext.sendBroadcast(in);
		return 0x00;
	}

	/*
	 * public class MessageReceiver extends BroadcastReceiver {
	 * 
	 * @Override public void onReceive(Context context, Intent intent) { String
	 * action = intent.getAction();
	 * 
	 * 
	 * // When discovery finds a device if
	 * (BluetoothDevice.ACTION_FOUND.equals(action)) { // Get the
	 * BluetoothDevice object from the Intent BluetoothDevice device =
	 * intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE); // If it's
	 * already paired, skip it, because it's been listed already if
	 * (device.getBondState() != BluetoothDevice.BOND_BONDED) {
	 * //修改一下，搜索设备JHLM60或者JHLA60 //if
	 * (device.getName().subSequence(0,3).equals("JHL")){
	 * mNewDevicesArrayAdapter.add(device.getName() + "\n" +
	 * device.getAddress()); //} } // When discovery is finished, change the
	 * Activity title } else if
	 * (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
	 * setProgressBarIndeterminateVisibility(false);
	 * setTitle(R.string.select_device); if (mNewDevicesArrayAdapter.size()==0)
	 * { //String noDevices =
	 * getResources().getText(R.string.none_found).toString();
	 * //mNewDevicesArrayAdapter.add(noDevices); } } } };
	 */

	private static byte toByte(char c) {
		byte b = (byte) "0123456789ABCDEF".indexOf(c);
		return b;
	}

	public static byte[] hexStringToByte(String hex) {
		int len = (hex.length() / 2);
		byte[] result = new byte[len];
		char[] achar = hex.toCharArray();
		for (int i = 0; i < len; i++) {
			int pos = i * 2;
			result[i] = (byte) (toByte(achar[pos]) << 4 | toByte(achar[pos + 1]));
		}
		return result;
	}

	public static byte[] hexStr2Bytes(String src) {
		int m = 0, n = 0;
		int l = src.length() / 2;
		System.out.println(l);
		byte[] ret = new byte[l];
		for (int i = 0; i < l; i++) {
			m = i * 2 + 1;
			n = m + 1;
			ret[i] = Integer.decode(
					"0x" + src.substring(i * 2, m) + src.substring(m, n))
					.byteValue();
		}
		return ret;
	}

	// 转化十六进制编码为字符串
	public static String toStringHex(String s) {
		byte[] baKeyword = new byte[s.length() / 2];
		for (int i = 0; i < baKeyword.length; i++) {
			try {
				baKeyword[i] = (byte) (0xff & Integer.parseInt(
						s.substring(i * 2, i * 2 + 2), 16));
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		try {
			s = new String(baKeyword, "utf-8");// UTF-16le:Not
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		return s;
	}

	/****************************************************************************
	 * 
	 * 
	 * 
	 * 以下为金融相关函数
	 * 
	 * 
	 */
	public synchronized int StandbySALEBrushCard(byte[] data) {
		int nIndex = 0, nIndexlen = 0;
		int nTotalLen = 0, nDatalen = 0;
		String sTemp = "", szPAN = "", szExpireDate = "", szServiceCode = "", szServiceCodeCmd = "";

		nTotalLen = data.length;

		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < nTotalLen; ++i) {
			sb.append(String.format("%02x", data[i]));
		}
		Log.e("StandbySALEBrushCard", sb.toString());
		sb.setLength(0);

		if (EntryType == 1) {
			nDatalen = data[2] & 0xFF;
			nDatalen <<= 8;
			nDatalen |= data[3] & 0xFF;
			for (int i = 0; i < nDatalen + 4; ++i) {
				sTemp = sTemp + String.format("%02x", data[nIndex + i]);
			}
			nIndex = nIndex + nDatalen + 4;
			hashmap.put("YYData", sTemp);
			sTemp = "";

		}

		nIndex++;
		/*
		 * if (data[nIndex] !=0x00) return data[nIndex];
		 */

		nIndex++;
		// 20 00 0020 021ADF 040A030ADF 060A0B0C0D0E0F 060A0B0C0D0E1F 0004
		// 1A1B1C1D 0102030405060708
		if (nTotalLen < nIndex + 2) //
			return 0x02; //

		for (int i = 0; i < 2; ++i) {
			sTemp = sTemp + String.format("%02x", data[nIndex + i]);
		}
		nIndex++;
		nIndex++;

		hashmap.put("SzEntryMode", sTemp);
		if (sTemp.subSequence(0, 2).equals("02"))
			hashmap.put("CardMODE", "0");
		else
			hashmap.put("CardMODE", "1");

		sTemp = "";
		if (nTotalLen < nIndex + 1)
			return 0x02; //

		track2DataLen = data[nIndex];
		if (track2DataLen > 64)
			return 0x02; //

		nIndex++;
		if (nTotalLen < track2DataLen + nIndex) // 闂佸憡鐟遍幏锟?2闂备浇娉曢崰镒板几婵犳艾绠柨鐕傛嫹
			return 0x02; // 濡炪伇宥嗗

		for (int i = 0; i < track2DataLen; ++i) {
			sTemp = sTemp + String.format("%02x", data[nIndex + i]);
		}
		hashmap.put("Track2len", Integer.toString(track2DataLen));
		hashmap.put("Track2", sTemp);

		nIndexlen = sTemp.indexOf("d");
		if (nIndexlen > 0) {
			szPAN = sTemp.substring(0, nIndexlen);

			// szExpireDate =sTemp.substring(nIndexlen+3,nIndexlen+5);
			// szExpireDate =szExpireDate
			// +sTemp.substring(nIndexlen+1,nIndexlen+3);
			szExpireDate = sTemp.substring(nIndexlen + 1, nIndexlen + 5);

			szServiceCode = sTemp.substring(nIndexlen + 5, nIndexlen + 8);
			szServiceCodeCmd = szServiceCode.substring(0, 1);
			if ((szServiceCodeCmd.equals("2"))
					|| (szServiceCodeCmd.equals("6")))
				// IC
				hashmap.put("CardType", "1");
			else
				hashmap.put("CardType", "0"); //

			hashmap.put("PAN", szPAN);
			hashmap.put("ExpireDate", szExpireDate);
		}

		sTemp = "";
		nIndex = nIndex + track2DataLen;
		if (nTotalLen < nIndex + 1) //
			return 0x02; // 濡炪伇宥嗗

		track2DataLen = data[nIndex];
		if (track2DataLen > 128)
			return 0x02; //

		nIndex++;
		if (nTotalLen < track2DataLen + nIndex) //
			return 0x02; //

		for (int i = 0; i < track2DataLen; ++i) {
			sTemp = sTemp + String.format("%02x", data[nIndex + i]);
		}
		hashmap.put("Encrytrack2len", Integer.toString(track2DataLen));
		hashmap.put("Encrytrack2", sTemp);
		sTemp = "";

		nIndex = nIndex + track2DataLen;
		if (nTotalLen < nIndex + 1) //
			return 0x02; //

		track3DataLen = data[nIndex];
		if (track3DataLen > 128)
			return 0x02; //

		nIndex++;
		if (nTotalLen < track3DataLen + nIndex) //
			return 0x02; // 濡炪伇宥嗗

		for (int i = 0; i < track3DataLen; ++i) {
			sTemp = sTemp + String.format("%02x", data[nIndex + i]);
		}

		hashmap.put("Track3len", Integer.toString(track3DataLen));
		hashmap.put("Track3", sTemp);
		sTemp = "";

		nIndex = nIndex + track3DataLen;
		if (nTotalLen < nIndex + 1) //
			return 0x02; //

		track3DataLen = data[nIndex];
		if (track3DataLen > 128)
			return 0x02; // 濡炪伇宥嗗

		nIndex++;
		if (nTotalLen < track3DataLen + nIndex) //
			return 0x02; //

		for (int i = 0; i < track3DataLen; ++i) {
			sTemp = sTemp + String.format("%02x", data[nIndex + i]);
		}
		hashmap.put("Encrytrack3len", Integer.toString(track3DataLen));
		hashmap.put("Encrytrack3", sTemp);
		sTemp = "";
		nIndex = nIndex + track3DataLen;

		// 55

		if (nTotalLen < nIndex + 2) //
			return 0x02; // 濡炪伇宥嗗

		nDatalen = data[nIndex] & 0xFF;
		nDatalen <<= 8;
		nDatalen |= data[nIndex + 1] & 0xFF;

		// nDatalen = data[nIndex];
		if (nDatalen > 1024)
			return 0x02; // 闂备浇妗ㄩ懗鑸垫櫠濡わ拷阎ｅ灚绗熼敓钘夘潖瑜版帒鍨傛い镞傗拡閸炵儤绻涢幋镣村碍缂佸鎸崇瘬闁跨哕鎷?

		nIndex++;
		nIndex++;
		if (nTotalLen < nDatalen + nIndex) //
			return 0x02; // 闂备浇妗ㄩ懗鑸垫櫠濡わ拷阎ｅ灚绗熼敓钘夘潖瑜版帒鍨傛い镞傗拡閸炵儤绻涢幋镣村碍缂佸鎸崇瘬闁跨哕鎷?
		for (int i = 0; i < nDatalen; ++i) {
			sTemp = sTemp + String.format("%02x", data[nIndex + i]);
		}
		hashmap.put("Track55len", Integer.toString(nDatalen));
		hashmap.put("Track55", sTemp);
		sTemp = "";
		nIndex = nIndex + nDatalen;

		if (nTotalLen < nIndex + 9) //
			return 0x02; // 闂备浇妗ㄩ懗鑸垫櫠濡わ拷阎ｅ灚绗熼敓钘夘潖瑜版帒鍨傛い镞傗拡閸炵儤绻涢幋镣村碍缂佸鎸崇瘬闁跨哕鎷?

		nDatalen = data[nIndex];
		nIndex++;
		for (int i = 0; i < nDatalen; ++i) {
			sTemp = sTemp + String.format("%02x", data[nIndex + i]);
		}
		hashmap.put("Pinblock", sTemp);

		nIndex = nIndex + 8;

		/*
		 * if (nTotalLen < nIndex+17) return 0x02;
		 * //闂备浇妗ㄩ懗鑸垫櫠濡わ拷阎ｅ灚绗熼敓钘夘潖瑜版帒鍨傛い镞傗拡閸炵儤绻涢幋镣村碍缂佸瀚伴猕蹇涙晸阌燂拷
		 * 
		 * 
		 * nDatalen = data[nIndex]; nIndex ++; sTemp =""; for(int
		 * i=0;i<nDatalen;++i){ sTemp =sTemp +String.format("%02x",
		 * data[nIndex+i]); } hashmap.put("randrom", sTemp);
		 * 
		 * nIndex =nIndex +nDatalen;
		 */

		if (nTotalLen < nIndex + 1)
			return 0x02; // 闂备浇妗ㄩ懗鑸垫櫠濡わ拷阎ｅ灚绗熼敓钘夘潖瑜版帒鍨傛い镞傗拡閸炵儤绻涢幋镣村碍缂佸瀚伴猕蹇涙晸阌燂拷
		nDatalen = data[nIndex];
		nIndex++;
		sTemp = "";
		for (int i = 0; i < nDatalen; ++i) {

			sTemp = sTemp + String.format("%02x", data[nIndex + i]);
		}
		hashmap.put("PanSeqNo", sTemp);
		nIndex += nDatalen;

		if (nTotalLen < nIndex + 1) {
			if (mCallBackData != null) {
				mCallBackData.onReadCardData(hashmap);
			}
			return 0x02; // 闂备浇妗ㄩ懗鑸垫櫠濡わ拷阎ｅ灚绗熼敓钘夘潖瑜版帒鍨傛い镞傗拡閸炵儤绻涢幋镣村碍缂佸瀚伴猕蹇涙晸阌燂拷
		}
		nDatalen = data[nIndex];
		nIndex++;
		sTemp = "";
		for (int i = 0; i < nDatalen; ++i) {

			sTemp = sTemp + String.format("%02x", data[nIndex + i]);
		}
		hashmap.put("Amount", sTemp);
		nIndex += nDatalen;
		if (nTotalLen < nIndex + 1) {
			if (mCallBackData != null) {
				mCallBackData.onReadCardData(hashmap);
			}
			return 0x02;
		}

		if (PASSWORD_ENCRY_MODEM == 0x03) {
			nDatalen = data[nIndex];
			nIndex++;
			sTemp = "";
			for (int i = 0; i < nDatalen; ++i) {

				sTemp = sTemp + String.format("%02x", data[nIndex + i]);
			}
			hashmap.put("AsciiPwd", sTemp);

			nIndex += nDatalen;
			if (nTotalLen < nIndex + 1) {
				if (mCallBackData != null) {
					mCallBackData.onReadCardData(hashmap);
				}
				return 0x02;
			}

			nDatalen = data[nIndex];
			nIndex++;
			sTemp = "";
			for (int i = 0; i < nDatalen; ++i) {

				sTemp = sTemp + String.format("%02x", data[nIndex + i]);
			}

			sTemp = toStringHex(sTemp);
			hashmap.put("SnData", sTemp);

			nIndex += nDatalen;
			if (nTotalLen < nIndex + 1) {
				if (mCallBackData != null) {
					mCallBackData.onReadCardData(hashmap);
				}
				return 0x02;
			}

		}

		if (TRACK_ENCRY_MODEM == 0x05 || TRACK_ENCRY_MODEM == 0x08) {
			nDatalen = data[nIndex];
			nIndex++;
			sTemp = "";
			for (int i = 0; i < nDatalen; ++i) {

				sTemp = sTemp + String.format("%02x", data[nIndex + i]);
			}
			hashmap.put("Random", sTemp);
			nIndex += nDatalen;
		}

		/*
		 * 
		 * 
		 * 
		 * if (PrintLog) { sb.setLength( 0 ); for(int i=0;i<track2DataLen;++i){
		 * sb.append(String.format("%02x", szTrack2[i])); }
		 * Log.e("MainActivity",sb.toString()); sb.setLength( 0 );
		 * 
		 * 
		 * for(int i=0;i<track3DataLen;++i){ sb.append(String.format("%02x",
		 * szTrack3[i])); } Log.e("MainActivity",sb.toString()); sb.setLength( 0
		 * ); }
		 */
		if (mCallBackData != null) {
			mCallBackData.onReadCardData(hashmap);
		}
		return SUCCESS;
	}

	public void onReceive(byte[] data) {
		if (PrintLog) {
			StringBuilder sb = new StringBuilder();
			for (int i = 0; i < data.length; ++i) {
				sb.append(String.format("%02x", data[i]));
			}
			Log.e("onReceive Bluetooth", sb.toString());
		}

		synchronized (mWaitLock) {

			if (mCallBackData != null) {
				// mCallBackData.onReceive(data);
				switch (data[0]) {
				case PASSWORD_INPUT_FLAG:
				case ICBRUSH:
				case CHECK_IC:
				case BRUSHDATA: // 请求刷卡
				case IC_OPEN: // IC卡打开
				case IC_OPEN_MAG:
				case IC_CLOSE: // IC卡关闭
				case IC_WRITEAPDU: // IC卡写入APDU
				case IC_GETSTATUS: // IC卡状态
				case Battery: // 电池电量
				case ProofIcParm: // IC论证
				case GETTERNO: // 获取商户号终端号
					mCallBackData.onReceive(data);
					break;
				case GETTRACKDATA_CMD:
					if (data[1] == 0x00) {
						StandbySALEBrushCard(data);
					} else {
						if (data[1] == 0x46) {
							ConnectDevice = false;
							DisConnectBlueDevice();
						}
						mCallBackData.onReceive(data);
					}
					break;

				case (byte) GETENCARDDATA: // 加密卡号相关数据
					StandbySALEBrushCard(data);
					break;
				case GETCARDDATA:
					if (data[1] == 0x00) {
						int nRet = StandbySALEBrushCard(data);
					} else {
						mCallBackData.onResult(data[0], data[1]);

					}
					break;
				case MAINKEY: // 主密钥
				case 0x35:
				case WORKEY: // 工作密钥
				case SETPUBKEYPARM: // 写入公密钥
				case SETAIDPAMR: // 写入AID参数
				case ClEARPUBKEY: // 清除公钥
				case CLEARAID: // 清除AID
				case SETTERNO: // 设置终端号
					mCallBackData.onResult(data[0], data[1]);
					break;
				case 0x36:
				case GETMAC: // 获取MAC
				case GETSNVERSION: // 获取版本号
					if (data[1] == 0x00)
						mCallBackData.onReceive(data);
					else
						mCallBackData.onResult(data[0], data[1]);
					break;
				default:
					break;
				}

			}
		}
	}

	public synchronized static int WriteCmdData(byte[] data) {
		int nlen = 0;
		nlen = data.length;
		byte[] SendData = new byte[nlen + 2];
		SendData[0] = (byte) ((nlen) / 256);
		SendData[1] = (byte) ((nlen) % 256);
		System.arraycopy(data, 0, SendData, 2, nlen);
		if (ConnectDevice == false)
			return EXCHANGE_NOT_OPEN;

		if (PrintLog) {
			StringBuilder sb = new StringBuilder();
			for (int i = 0; i < data.length; ++i) {
				sb.append(String.format("%02x", data[i]));
			}
			Log.e("WriteCmdData", sb.toString());
		}
		Intent i = new Intent(BluetoothConnController.SEND_MSG_FROM_BT_ACTION);
		i.putExtra("MESSAGE", SendData);
		i.putExtra("MODE", BluetoothConnController.MSG_MODE_SEND_HEX);
		mContext.sendBroadcast(i);
		return SUCCESS;
	}

	/***
	 * 获取SN号码
	 * 
	 * @return
	 */
	public synchronized int GetSnVersion() {
		byte[] data = new byte[1];
		
		Log.i("result","--------获取SN号码1--------");
		data[0] = GETSNVERSION;
		// data[0]=0x0a;
		Log.i("result","--------获取SN号码2--------"+WriteCmdData(data));
		return WriteCmdData(data);

	}

	/********************************************************************
	 * 函 数 名：MagnAmountPasswordCard 功能描述：MPOS 设备上提示输入金额 刷卡 输入密码 入口参数： long
	 * timeout --刷卡交易超时时间(毫秒) 返回说明：
	 **********************************************************/

	public synchronized int MagnAmountPasswordCard(long timeout, long amount) {
		byte[] bAmont = new byte[12];
		byte[] bDate = new byte[3];
		byte[] SendData = new byte[24];
		track2DataLen = 0;
		track3DataLen = 0;
		hashmap.clear();

		SendData[0] = GETTRACKDATA_CMD;
		SendData[1] = 0x01;
		SendData[2] = 0x01;
		SendData[3] = 0x01;
		SendData[4] = (byte) TRACK_ENCRY_MODEM;
		SendData[5] = (byte) PASSWORD_ENCRY_MODEM;
		SendData[6] = TRACK_ENCRY_DATA;
		SendData[7] = TRACK_ENCRY_DATA;
		Formatter fmt = new Formatter();
		fmt.format("%012d", amount);
		bAmont = fmt.toString().getBytes();
		System.arraycopy(bAmont, 0, SendData, 8, 12);
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyMMdd");
		String strData = dateFormat.format(new Date()); // 141206
		bDate = hexStringToByte(strData);
		System.arraycopy(bDate, 0, SendData, 20, 3);

		if ((timeout < 20000) || (timeout > 60000))
			timeout = 60 * 1000;
		long ntimeout = timeout / 1000;
		SendData[23] = (byte) ntimeout;
		if (PrintLog) {
			StringBuilder sb = new StringBuilder();
			for (int i = 0; i < 24; ++i) {
				sb.append(String.format("%02x", SendData[i]));
			}
			Log.e("MagnAmountPasswordCard", sb.toString());
		}
		return WriteCmdData(SendData);

	}

	/********************************************************************
	 * 函 数 名：MagnAmountNoPasswordCard 功能描述：MPOS 设备上提示输入金额 刷卡 无密码 入口参数： long
	 * timeout --刷卡交易超时时间(毫秒) 返回说明：
	 **********************************************************/
	public synchronized int MagnAmountNoPasswordCard(long timeout, long amount) {
		byte[] bAmont = new byte[12];
		byte[] bDate = new byte[3];
		byte[] SendData = new byte[24];
		track2DataLen = 0;
		track3DataLen = 0;
		hashmap.clear();

		SendData[0] = GETTRACKDATA_CMD;
		SendData[1] = 0x01;
		SendData[2] = 0x00;
		SendData[3] = 0x01;
		SendData[4] = (byte) TRACK_ENCRY_MODEM;
		SendData[5] = (byte) PASSWORD_ENCRY_MODEM;
		SendData[6] = TRACK_ENCRY_DATA;
		SendData[7] = TRACK_ENCRY_DATA;
		Formatter fmt = new Formatter();
		fmt.format("%012d", amount);
		bAmont = fmt.toString().getBytes();
		System.arraycopy(bAmont, 0, SendData, 8, 12);
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyMMdd");
		String strData = dateFormat.format(new Date()); // 141206
		bDate = hexStringToByte(strData);
		System.arraycopy(bDate, 0, SendData, 20, 3);

		if ((timeout < 20000) || (timeout > 60000))
			timeout = 60 * 1000;
		long ntimeout = timeout / 1000;
		SendData[23] = (byte) ntimeout;
		if (PrintLog) {
			StringBuilder sb = new StringBuilder();
			for (int i = 0; i < 24; ++i) {
				sb.append(String.format("%02x", SendData[i]));
			}
			Log.e("MagnAmountNoPasswordCard", sb.toString());
		}
		return WriteCmdData(SendData);

	}

	/********************************************************************
	 * 函 数 名：MagnNoAmountPasswordCard 功能描述：MPOS 设备上提示刷卡 + 输入密码 无输入金额（例如查询余额）
	 * 入口参数： long timeout --刷卡交易超时时间(毫秒) 返回说明：
	 **********************************************************/
	public synchronized int MagnNoAmountPasswordCard(long timeout, long amount) {
		byte[] bAmont = new byte[12];
		byte[] bDate = new byte[3];
		byte[] SendData = new byte[24];
		track2DataLen = 0;
		track3DataLen = 0;
		hashmap.clear();

		SendData[0] = GETTRACKDATA_CMD;
		SendData[1] = 0x00;
		SendData[2] = 0x01;
		SendData[3] = 0x01;
		SendData[4] = (byte) TRACK_ENCRY_MODEM;
		SendData[5] = (byte) PASSWORD_ENCRY_MODEM;
		SendData[6] = TRACK_ENCRY_DATA;
		SendData[7] = TRACK_ENCRY_DATA;
		Formatter fmt = new Formatter();
		// fmt.format("%012d",MAmount);
		fmt.format("%012d", amount);
		bAmont = fmt.toString().getBytes();
		System.arraycopy(bAmont, 0, SendData, 8, 12);
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyMMdd");
		String strData = dateFormat.format(new Date()); // 141206
		bDate = hexStringToByte(strData);
		System.arraycopy(bDate, 0, SendData, 20, 3);

		if ((timeout < 20000) || (timeout > 60000))
			timeout = 60 * 1000;
		long ntimeout = timeout / 1000;
		SendData[23] = (byte) ntimeout;
		if (PrintLog) {
			StringBuilder sb = new StringBuilder();
			for (int i = 0; i < 24; ++i) {
				sb.append(String.format("%02x", SendData[i]));
			}
			Log.e("MagnNoAmountPasswordCard", sb.toString());
		}
		return WriteCmdData(SendData);

	}

	/********************************************************************
	 * 函 数 名：MagnNoAmountNoPasswordCard 功能描述：：MPOS 设备上提示刷卡 无输入金额
	 * 无密码（例如信用卡预授权完成等交易） 入口参数： long timeout --刷卡交易超时时间(毫秒) 返回说明：
	 **********************************************************/
	public synchronized int MagnNoAmountNoPasswordCard(long timeout, long amount) {
		byte[] bAmont = new byte[12];
		byte[] bDate = new byte[3];
		byte[] SendData = new byte[24];
		track2DataLen = 0;
		track3DataLen = 0;
		hashmap.clear();

		SendData[0] = GETTRACKDATA_CMD;
		SendData[1] = 0x00;
		SendData[2] = 0x00;
		SendData[3] = 0x01;
		SendData[4] = (byte) TRACK_ENCRY_MODEM;
		SendData[5] = (byte) PASSWORD_ENCRY_MODEM;
		SendData[6] = TRACK_ENCRY_DATA;
		SendData[7] = TRACK_ENCRY_DATA;
		Formatter fmt = new Formatter();
		// fmt.format("%012d",MAmount);
		fmt.format("%012d", amount);
		bAmont = fmt.toString().getBytes();
		System.arraycopy(bAmont, 0, SendData, 8, 12);
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyMMdd");
		String strData = dateFormat.format(new Date()); // 141206
		bDate = hexStringToByte(strData);
		System.arraycopy(bDate, 0, SendData, 20, 3);

		if ((timeout < 20000) || (timeout > 60000))
			timeout = 60 * 1000;
		long ntimeout = timeout / 1000;
		SendData[23] = (byte) ntimeout;
		if (PrintLog) {
			StringBuilder sb = new StringBuilder();
			for (int i = 0; i < 16; ++i) {
				sb.append(String.format("%02x", SendData[i]));
			}
			Log.e("MagnNoAmountNoPasswordCard", sb.toString());
		}
		return WriteCmdData(SendData);

	}

	/********************************************************************
	 * 刷卡取消交易
	 **********************************************************/
	public synchronized int MagnCancel() {
		byte[] SendData = new byte[1];
		SendData[0] = (byte) 0x99;
		return WriteCmdData(SendData);

	}

	/********************************************************************
	 * 函 数 名：MagnCard 功能描述：刷卡 入口参数： long timeout --刷卡超时时间(毫秒) long nAmount
	 * --交易金额,用于IC卡(1元==100), 查询余额送0即可 int BrushCardM --刷卡模式(0:不支持降级交易 1:支持降级交易
	 * 设置为0芯片卡刷磁条卡返回不允许降级) 返回说明：
	 **********************************************************/
	public synchronized int MagnCard(long timeout, long nAmount, int BrushCardM) {
		synchronized (mWaitLock) {
			byte[] bAmont = new byte[12];
			byte[] bDate = new byte[3];
			Arrays.fill(szTrack2, (byte) 0);
			Arrays.fill(szTrack3, (byte) 0);
			Arrays.fill(bAmont, (byte) 0);
			track2DataLen = 0;
			track3DataLen = 0;
			BrushCardMode = BrushCardM;
			hashmap.clear();
			byte[] data = new byte[1 + 12 + 3 + 1];
			data[0] = BRUSHDATA;
			Formatter fmt = new Formatter();
			fmt.format("%012d", nAmount);
			bAmont = fmt.toString().getBytes();
			System.arraycopy(bAmont, 0, data, 1, 12);
			SimpleDateFormat dateFormat = new SimpleDateFormat("yyMMdd");
			String strData = dateFormat.format(new Date()); // 141206
			bDate = hexStringToByte(strData);
			System.arraycopy(bDate, 0, data, 13, 3);

			if ((timeout < 20000) || (timeout > 60000))
				timeout = 60 * 1000;

			long ntimeout = timeout / 1000;
			data[16] = (byte) ntimeout;
			if (PrintLog) {
				StringBuilder sb = new StringBuilder();
				for (int i = 0; i < 16; ++i) {
					sb.append(String.format("%02x", data[i]));
				}
				Log.e("MagnCard", sb.toString());
			}
			return WriteCmdData(data);
		}

	}

	public synchronized static int InputPassword(String bPasskey,
			int nPasswordlen) {

		synchronized (mWaitLock) {
			int nPasLen = 0;
			byte[] data = new byte[10];// 涔嫔墠姝ゅ鏄痓yte[] data =
										// null;娌℃湁鍒濆鍖栵紝镓€浠ヤ竴鐩存姤寮傚父data[0] =
										// PASSWORD_INPUT_FLAG;杩欎釜涓虹┖
			byte[] bPass = new byte[8];
			byte[] bPassword = new byte[8];
			hashmap.clear();
			data[0] = PASSWORD_INPUT_FLAG;
			data[1] = 0x08;
			nPasLen = nPasswordlen;
			if (nPasswordlen % 2 != 0) {
				bPasskey = bPasskey + "f";
				nPasLen++;
			}

			Arrays.fill(bPassword, (byte) 0xff);
			Arrays.fill(bPass, (byte) 0xff);
			bPassword = hexStr2Bytes(bPasskey);
			bPass[0] = (byte) nPasswordlen;
			System.arraycopy(bPassword, 0, bPass, 1, nPasLen / 2);
			System.arraycopy(bPass, 0, data, 2, 8);
			if (PrintLog) {
				StringBuilder sb = new StringBuilder();
				for (int i = 0; i < data.length; ++i) {
					sb.append(String.format("%02x", data[i]));
				}
				Log.e("InputPassword", sb.toString());
			}

			return WriteCmdData(data);
		}

	}

	/********************************************************************
	 * 函 数 名：TRANS_Sale 加密数据 功能描述：消费,返回消费需要上送数据22域+35+36+IC磁道数据+PINBLOCK+磁道加密随机数
	 * long timeout --超时时间 毫秒 long nAmount --消费金额 int nPasswordlen
	 * --密码数据例如:12345 String bPassKey -密码数据例如:12345 int mode --磁道加密模式 // 0
	 * 全部加密模式 1：8字节加密 返回说明：
	 **********************************************************/
	public synchronized static int TRANS_Sale(long nTimeout, long nAmount,
			int nPasswordlen, String bPassKey, int mode) {
		synchronized (mWaitLock) {
			int nPasLen = 0;
			byte[] data = null;
			if (EntryType == 0){
				data = new byte[25];
			}else
				data = new byte[27];
			byte[] bPass = new byte[8];
			byte[] bAmont = new byte[12];// 金额是12位ASC码
			byte[] bPassword = new byte[8];

			hashmap.clear();
			
			data[0] = GETCARDDATA;// 0x20是磁道加密
			
			if (EntryType == 0) {

				data[1] = 0x01;// 0x01是指pin加密模式

				Formatter fmt = new Formatter();
				fmt.format("%012d", nAmount);
				bAmont = fmt.toString().getBytes();

				nPasLen = nPasswordlen;
				if (nPasswordlen % 2 != 0) // 濡傛灉娌″噾榻愭暣锟�?鍒欏悗琛
				{
					bPassKey = bPassKey + "f";
					nPasLen++;
				}

				Arrays.fill(bPassword, (byte) 0xff);
				Arrays.fill(bPass, (byte) 0xff);
				bPassword = hexStr2Bytes(bPassKey);
				bPass[0] = (byte) nPasswordlen;
				System.arraycopy(bPassword, 0, bPass, 1, nPasLen / 2);

				System.arraycopy(bAmont, 0, data, 2, 12);

				System.arraycopy(bPass, 0, data, 14, 8);
				data[22] = PIN_KEY_ID;
				data[23] = 0x01;
				data[24] = (byte) mode;

			} else {

				/*
				 * 20 为命令头 HEX格式 00 为传输模式 HEX格式 303030303030303030313030 为输入金额
				 * 12位ASC码 12345fffffffffff0001020101 3132333435ffffff
				 * 
				 * 外置终端接收数据格式 2000000000000000123456780001020101 20为命令头 HEX格式
				 * 01为传输模式 HEX格式 000000000000为输入金额 12位ASC码 12345678 输入密码。8位ASC码
				 * 123456FF（不足8位的时候在后面补F） 00 工作密钥索引 HEX格式 默认为0x00 01 PIN加密模式
				 * HEX格式 默认为 0x01 02磁道加密模式 HEX格式 默认为0x02 01 mac计算模式 HEX格式
				 * 默认为0x01 01 mac加密模式 HEX格式 默认为0x01
				 */

				data[1] = 0x01;
				Formatter fmt = new Formatter();
				fmt.format("%012d", nAmount);
				bAmont = fmt.toString().getBytes();
				System.arraycopy(bAmont, 0, data, 2, 12);
				nPasLen = nPasswordlen;

				/*
				 * //ASCii码的形式----密码 for(int i =0;i<8-nPasLen;i++) { bPassKey
				 * =bPassKey+"f"; }
				 * 
				 * System.arraycopy(bPassKey.getBytes(),0,data,14,8);
				 */

				for (int i = 0; i < 8 - nPasLen; i++) // 濡傛灉娌″噾榻愭暣锟�?鍒欏悗琛
				{

					bPassKey = bPassKey + "f";
					nPasLen++;
				}

				Arrays.fill(bPassword, (byte) 0xff);
				Arrays.fill(bPass, (byte) 0xff);
				bPassword = hexStr2Bytes(bPassKey);
				bPass[0] = (byte) nPasswordlen;
				System.arraycopy(bPassword, 0, bPass, 1, nPasLen / 2);

				System.arraycopy(bAmont, 0, data, 2, 12);

				System.arraycopy(bPass, 0, data, 14, 8);
				data[22] = 0x00;
				data[23] = 0x01;
				data[24] = 0x02;
				data[25] = 0x01;
				data[26] = 0x01;

				if (PrintLog) {
					StringBuilder sb = new StringBuilder();
					if (EntryType == 0) {
						for (int i = 0; i < 24; ++i) {
							sb.append(String.format("%02x", data[i]));
						}
					} else {
						for (int i = 0; i < 27; ++i) {
							sb.append(String.format("%02x", data[i]));
						}
					}
					Log.e("TRANS_Sale", sb.toString());
				}
			}
			return WriteCmdData(data);
		}

	}

	/********************************************************************
	 * 函 数 名：WriteMainKey 功能描述：写入主密钥 入口参数： int len --主密钥长度 Byte[] bMainKey
	 * --主密钥数据16个字节 返回说明：成功/失败
	 **********************************************************/
	public synchronized int WriteMainKey(int len, byte[] bMainKey) {
		synchronized (mWaitLock) {
			byte[] data = new byte[4 + len];
			data[0] = MAINKEY;
			data[1] = MAIN_KEY_ID;
			data[2] = (byte) len;
			System.arraycopy(bMainKey, 0, data, 3, len);
			if (PASSWORD_ENCRY_MODEM == 0x03) // 瀵嗙爜鏄庢枃
				data[3 + len] = (byte) 0x81;
			else
				data[3 + len] = 0x01;
			return WriteCmdData(data);
		}

	}

	/********************************************************************
	 * 函 数 名：WriteWorkKey 功能描述：写入工作密钥 入口参数： int len --主密钥长度 byte[] bWorkKey
	 * --工作密钥数据60个字节 16字节PIN密钥+4个字节校验码 +16字节MAC +4个字节MAC校验码 +磁道加密密钥+ 4磁道加密密钥校验码
	 * ==60 个字节
	 * 
	 * 返回说明：成功/失败
	 **********************************************************/
	public synchronized int WriteWorkKey(int len, byte[] bWorkKey) {
		synchronized (mWaitLock) {
			byte[] data = new byte[1 + len + 1];
			data[0] = WORKEY;
			System.arraycopy(bWorkKey, 0, data, 1, len);
			data[61] = (byte) WORK_ENCRY_MODEM; // 00 3DES 加密 0x01 X9.18
			return WriteCmdData(data);
		}
	}

	/********************************************************************
	 * 函 数 名：GetMac 功能描述：获取MAC值 Int len MAC数据大小 byte[] bDataKey MAC数据 返回说明：
	 **********************************************************/
	public synchronized int GetMac(int len, byte[] bDataKey) {
		synchronized (mWaitLock) {
			byte[] data = new byte[6 + len];
			data[0] = GETMAC;
			data[1] = MAC_KEY_ID;
			data[2] = (byte) ((len) / 256);
			data[3] = (byte) ((len) % 256);
			System.arraycopy(bDataKey, 0, data, 4, len);
			data[4 + len] = (byte) TRACK_MAC_DATA; // 03 单倍加密
			data[5 + len] = (byte) MAC_3DES_DATA; // 00 单倍加密 01 双倍加密
			return WriteCmdData(data);
		}

	}

	/********************************************************************
	 * 函 数 名：WriteTernumber 功能描述：设置终端号商户号 byte[] bTeridKey 终端号商户号
	 * 8个字符终端号+15个字节字符商户 =23字节 返回说明：
	 **********************************************************/
	public synchronized int WriteTernumber(byte[] bData) {

		synchronized (mWaitLock) {
			byte[] data = new byte[24];
			data[0] = SETTERNO;
			System.arraycopy(bData, 0, data, 1, 23);
			return WriteCmdData(data);
		}

	}

	/********************************************************************
	 * 函 数 名：ReadTernumber 功能描述：读取终端号商户号
	 * 
	 * 返回说明：
	 **********************************************************/
	public synchronized int ReadTernumber() {
		synchronized (mWaitLock) {
			byte[] data = new byte[1];
			data[0] = GETTERNO;
			return WriteCmdData(data);
		}
	}

	/********************************************************************
	 * 函 数 名：WriteEmvCapkParm 功能描述：写入公密钥 入口参数： int len --公密钥长度 Byte[] bMainKey
	 * --公密钥数据284个字节 返回说明：成功/失败
	 * 
	 * 数据结构体  typedef struct{
	 *     unsigned char AppName[33];       //本地应用名，以'\x00'结尾的字符串
	 *     unsigned char AID[17];           //应用标志
	 *     unsigned char AidLen;            //AID的长度
	 *     unsigned char SelFlag;           //选择标志( 部分匹配/全匹配)
	 *     unsigned char Priority;          //优先级标志
	 *     unsigned char TargetPer;         //目标百分比数
	 *     unsigned char MaxTargetPer;      //最大目标百分比数
	 *     unsigned char FloorLimitCheck;   //是否检查最低限额
	 *     unsigned char RandTransSel;      //是否进行随机交易选择
	 *     unsigned char VelocityCheck;     //是否进行频度检测
	 *     unsigned long FloorLimit;        //最低限额
	 *     unsigned long Threshold;         //阀值
	 *     unsigned char TACDenial[6];      //终端行为代码(拒绝)
	 *     unsigned char TACOnline[6];      //终端行为代码(联机)
	 *     unsigned char TACDefault[6];     //终端行为代码(缺省)
	 *     unsigned char AcquierId[7];      //收单行标志
	 *     unsigned char dDOL[256];         //终端缺省DDOL   len+data
	 *     unsigned char tDOL[256];         //终端缺省TDOL   len+data
	 *     unsigned char Version[3];        //应用版本
	 *     unsigned char RiskManData[10];   //风险管理数据   len+data
	 * unsigned char EC_bTermLimitCheck;      //是否支持终端交易限额
	 * unsigned long EC_TermLimit;            //终端交易限额，
	 * unsigned char CL_bStatusCheck;         //是否支持qPBOC状态检查
	 * unsigned long CL_FloorLimit;        //非接触终端最低限额
	 * unsigned long CL_TransLimit;        //非接触终端交易限额
	 * unsigned long CL_CVMLimit;          //非接触终端CVM限
	 * unsigned char TermQuali_byte2
	 * ;      //交易金额与每个AID限额的判断结果，在刷卡前处理，通过此变量缓存判断结果  }STRUCT_PACK EMV_APPLIST;
	 **********************************************************/
	public synchronized int WriteEmvCapkParm(int len, byte[] bWorkKey) {
		synchronized (mWaitLock) {
			byte[] data = new byte[1 + len];
			data[0] = SETPUBKEYPARM;
			System.arraycopy(bWorkKey, 0, data, 1, len);
			return WriteCmdData(data);

		}
	}

	/********************************************************************
	 * 函 数 名：WriteEmvAidParm 功能描述：设置（AID）参数 入口参数： int len --（AID）参数长度 Byte[]
	 * bMainKey --（AID）参数数据635个字节 返回说明：成功/失败
	 * 
	 * 
	 * 数据结构体
	 * 
	 * 
	 * typedef struct { unsigned char RID[5]; //应用注册服务商ID unsigned char KeyID;
	 * //密钥索引 unsigned char HashInd; //HASH算法标志 unsigned char ArithInd;
	 * //RSA算法标志 unsigned char ModulLen; //模长度 unsigned char Modul[248]; //模
	 * unsigned char ExponentLen; //指数长度 unsigned char Exponent[3]; //指数
	 * unsigned char ExpDate[3]; //有效期(YYMMDD) unsigned char CheckSum[20];
	 * //密钥校验和 }
	 **********************************************************/
	public synchronized int WriteEmvAidParm(int len, byte[] bWorkKey) {
		synchronized (mWaitLock) {
			byte[] data = new byte[1 + len];
			data[0] = SETAIDPAMR;
			System.arraycopy(bWorkKey, 0, data, 1, len);
			return WriteCmdData(data);
		}
	}

	/********************************************************************
	 * 函 数 名：ClearEmvAidParm 功能描述：清空AID 无入口参数 返回说明：成功/失败
	 **********************************************************/
	public synchronized int ClearEmvAidParm() {
		synchronized (mWaitLock) {
			byte[] data = new byte[2];
			data[0] = CLEARAID;
			return WriteCmdData(data);

		}
	}

	/********************************************************************
	 * 函 数 名：ClearCapkParm
	 * 
	 * 功能描述：清空公钥 无入口参数 返回说明：成功/失败
	 **********************************************************/
	public synchronized int ClearCapkParm() {
		synchronized (mWaitLock) {
			byte[] data = new byte[1];
			data[0] = ClEARPUBKEY;
			return WriteCmdData(data);
		}
	}

	/********************************************************************
	 * 函 数 名：ProofIcData 功能描述：交易后论证 入口参数： int len --交易后论证数据长度 Byte[] bMainKey
	 * --交易后论证数据 返回说明：成功/失败
	 **********************************************************/
	public synchronized int ProofIcData(int len, byte[] bWorkKey) {

		synchronized (mWaitLock) {
			byte[] data = new byte[1 + len];
			data[0] = ProofIcParm;
			System.arraycopy(bWorkKey, 0, data, 1, len);
			return WriteCmdData(data);
		}
	}

	/********************************************************************
	 * 函 数 名：ReadBattery 功能描述：获取电池电量 入口参数：
	 * 
	 * 返回说明：成功/失败
	 **********************************************************/
	public synchronized int ReadBattery() {

		synchronized (mWaitLock) {
			byte[] data = new byte[1];
			data[0] = Battery;
			return WriteCmdData(data);
		}
	}

	/********************************************************************
	 * 函 数 名：IC_GetStatus 功能描述：检查IC卡是否在位置 返回0 在位置 其他 ：不在位置 入口参数：
	 * 
	 * 返回说明：成功/失败
	 ********************************************************************/
	public synchronized int IC_GetStatus() {

		synchronized (mWaitLock) {
			byte[] data = new byte[1];
			data[0] = IC_GETSTATUS;
			return WriteCmdData(data);

		}
	}

	/********************************************************************
	 * 函 数 名：IC_Open 功能描述：IC打开,然后复位,返回复位数据 =1个字节大小+复位数据 入口参数：
	 * 
	 * 返回说明：成功/失败
	 ********************************************************************/
	public synchronized int IC_Open() {
		synchronized (mWaitLock) {
			byte[] data = new byte[1];
			data[0] = IC_OPEN;
			return WriteCmdData(data);

		}
	}

	// 镓揿紑IC/纾佹浔鍗℃搷浣滃嚱鏁?
	public synchronized int IC_Open_MAG(int timeout) {

		synchronized (mWaitLock) {
			byte[] data = new byte[2];
			data[0] = IC_OPEN_MAG;
			if ((timeout < 20000) || (timeout > 60000))
				timeout = 60 * 1000;
			long ntimeout = timeout / 1000;
			data[1] = (byte) ntimeout;

			return WriteCmdData(data);
		}
	}

	/********************************************************************
	 * 函 数 名：IC_WriteApdu 功能描述：IC卡APDU 发送 入口参数：
	 * 
	 * 返回说明：成功/失败
	 ********************************************************************/
	public synchronized int IC_WriteApdu(int len, byte[] bWorkKey) {

		synchronized (mWaitLock) {
			byte[] data = new byte[2 + len];
			data[0] = IC_WRITEAPDU;
			data[1] = (byte) len;
			System.arraycopy(bWorkKey, 0, data, 2, len);
			return WriteCmdData(data);
		}
	}

	/********************************************************************
	 * 函 数 名：IC_Close 功能描述：IC关闭 入口参数：
	 * 
	 * 返回说明：成功/失败
	 ********************************************************************/
	public synchronized int IC_Close() {

		synchronized (mWaitLock) {
			byte[] data = new byte[1];
			data[0] = IC_CLOSE;
			return WriteCmdData(data);

		}
	}

	/********************************************************************
	 * 函 数 名：InitSysteConfig 功能描述：初始化相关加密参数 入口参数：
	 * 
	 * 返回说明：成功/失败
	 ********************************************************************/
	public synchronized int InitSysteConfig(int Track_Moden,
			int Password_Moden, int Mac_Moden) {
		TRACK_ENCRY_MODEM = Track_Moden;
		PASSWORD_ENCRY_MODEM = Password_Moden;
		MAC_3DES_DATA = Mac_Moden;
		if (MAC_3DES_DATA == 0x00) // 如果是DES加密 那么更新工作密钥也是用单密钥
			WORK_ENCRY_MODEM = 0x01;
		return 0x00;
	}

}
