package com.lk.qf.pay.jhl;

import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.lk.bhb.pay.R;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class MainActivity extends Activity implements BlueCommangerCallback {

	private static final boolean D = true;

	private static final String DEBUG_TAG = "BluetoothTest";
	private static final int connectmodem = 0x01; // 连接方式 00 自动连接:需要手动先配对 0x01
													// 选择设备连接,打开设备选择框,用户选择蓝牙连接

	private static final long WAIT_TIMEOUT = 15000; // 超时时间
	private static final int DIALOG = 3;

	private boolean bOpenDevice = false;
	BluetoothCommmanager BluetoothComm = null;
	private Handler mMainMessageHandler;
	private static final int Language = 0x01;

	private EditText m_editRecvData;
	Button WriteMainKey, WriteWKey, SetTerid, GetMAc;
	private String strPan = "";

	// Intent request codes
	private static final int REQUEST_CONNECT_DEVICE = 1;
	private static final int REQUEST_ENABLE_BT = 2;
	private static final int REQUEST_PREF_SETTING = 3;
	private static final int REQUEST_DISCONNECT_DEVICE = 4;
	private static final int REQUEST_ABOUT_INFO = 5;
	public static final String DEVICE_ADDRESS = "device_address";
	public static final String[] DEVICE_ADDRESS_FILETER = new String[] { "JHL" };

	private static final int nAmount = 100; // 默认传入金额 1元==100
	String message;

	/**
	 * 十六进制字符串转换成bytes
	 */
	private static byte uniteBytes(String src0, String src1) {
		byte b0 = Byte.decode("0x" + src0).byteValue();
		b0 = (byte) (b0 << 4);
		byte b1 = Byte.decode("0x" + src1).byteValue();
		byte ret = (byte) (b0 | b1);
		return ret;
	}

	public static byte[] hexStr2Bytes(String src) {
		int m = 0, n = 0;
		int l = src.length() / 2;
		byte[] ret = new byte[l];
		for (int i = 0; i < l; i++) {
			m = i * 2 + 1;
			n = m + 1;
			ret[i] = uniteBytes(src.substring(i * 2, m), src.substring(m, n));
		}
		return ret;
	}

	private byte[] getBytes(char[] chars) {
		Charset cs = Charset.forName("UTF-8");
		CharBuffer cb = CharBuffer.allocate(chars.length);
		cb.put(chars);
		cb.flip();
		ByteBuffer bb = cs.encode(cb);
		return bb.array();

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

	byte CapkList[] = { (byte) 0xA0, 0x00, 0x00, 0x03, 0x33, 0x01, 0x01, 0x01,
			(byte) 0x80, (byte) 0xBB, (byte) 0xE9, 0x06, 0x6D, 0x25, 0x17,
			0x51, 0x1D, 0x23, (byte) 0x9C, 0x7B, (byte) 0xFA, 0x77,
			(byte) 0x88, 0x41, 0x44, (byte) 0xAE, 0x20, (byte) 0xC7, 0x37,
			0x2F, 0x51, 0x51, 0x47, (byte) 0xE8, (byte) 0xCE, 0x65, 0x37,
			(byte) 0xC5, 0x4C, 0x0A, 0x6A, 0x4D, 0x45, (byte) 0xF8,
			(byte) 0xCA, 0x4D, 0x29, 0x08, 0x70, (byte) 0xCD, (byte) 0xA5,
			(byte) 0x9F, 0x13, 0x44, (byte) 0xEF, 0x71, (byte) 0xD1, 0x7D,
			0x3F, 0x35, (byte) 0xD9, 0x2F, 0x3F, 0x06, 0x77, (byte) 0x8D, 0x0D,
			0x51, 0x1E, (byte) 0xC2, (byte) 0xA7, (byte) 0xDC, 0x4F,
			(byte) 0xFE, (byte) 0xAD, (byte) 0xF4, (byte) 0xFB, 0x12, 0x53,
			(byte) 0xCE, 0x37, (byte) 0xA7, (byte) 0xB2, (byte) 0xB5,
			(byte) 0xA3, 0x74, 0x12, 0x27, (byte) 0xBE, (byte) 0xF7, 0x25,
			0x24, (byte) 0xDA, 0x7A, 0x2B, 0x7B, 0x1C, (byte) 0xB4, 0x26,
			(byte) 0xBE, (byte) 0xE2, 0x7B, (byte) 0xC5, 0x13, (byte) 0xB0,
			(byte) 0xCB, 0x11, (byte) 0xAB, (byte) 0x99, (byte) 0xBC, 0x1B,
			(byte) 0xC6, 0x1D, (byte) 0xF5, (byte) 0xAC, 0x6C, (byte) 0xC4,
			(byte) 0xD8, 0x31, (byte) 0xD0, (byte) 0x84, (byte) 0x87,
			(byte) 0x88, (byte) 0xCD, 0x74, (byte) 0xF6, (byte) 0xD5, 0x43,
			(byte) 0xAD, 0x37, (byte) 0xC5, (byte) 0xA2, (byte) 0xB4,
			(byte) 0xC5, (byte) 0xD5, (byte) 0xA9, 0x3B, 0x00, 0x00, 0x00,
			0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
			0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
			0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
			0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
			0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
			0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
			0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
			0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
			0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
			0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
			0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x01, 0x03, 0x00, 0x00,
			0x09, 0x12, 0x31, (byte) 0xE8, (byte) 0x81, (byte) 0xE3,
			(byte) 0x90, 0x67, 0x5D, 0x44, (byte) 0xC2, (byte) 0xDD,
			(byte) 0x81, 0x23, 0x4D, (byte) 0xCE, 0x29, (byte) 0xC3,
			(byte) 0xF5, (byte) 0xAB, 0x22, (byte) 0x97, (byte) 0xA0 };

	byte AidList[] = { 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
			0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
			0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
			0x00, 0x00, (byte) 0xA0, 0x00, 0x00, 0x03, 0x33, 0x01, 0x01, 0x01,
			0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x08, 0x00,
			0x00, 0x63, 0x63, 0x00, 0x01, 0x01, 0x00, 0x00, 0x00, 0x00, 0x00,
			0x00, 0x00, 0x00, 0x00, 0x10, 0x00, 0x00, 0x00, 0x00, (byte) 0xD8,
			0x40, 0x04, (byte) 0xF8, 0x00, 0x00, (byte) 0xD8, 0x40, 0x00,
			(byte) 0xA8, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
			0x03, (byte) 0x9F, 0x37, 0x04, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
			0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
			0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
			0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
			0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
			0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
			0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
			0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
			0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
			0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
			0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
			0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
			0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
			0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
			0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
			0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
			0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
			0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
			0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
			0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
			0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
			0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
			0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
			0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
			0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
			0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
			0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
			0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
			0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
			0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
			0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
			0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
			0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
			0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
			0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
			0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
			0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
			0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
			0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
			0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
			0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
			0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
			0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
			0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
			0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
			0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
			0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x20, 0x00, 0x00,
			0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x01,
			(byte) 0xA0, (byte) 0x86, 0x01, 0x00, 0x00, (byte) 0xA0,
			(byte) 0x86, 0x01, 0x00, (byte) 0xA0, (byte) 0x86, 0x01, 0x00,
			(byte) 0xA0, (byte) 0x86, 0x01, 0x00, 0x0a };

	class MessageHandler extends Handler {
		private long mLogCount = 0;

		public MessageHandler(Looper looper) {
			super(looper);
		}

		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case R.id.editRecvData:
				if (mLogCount > 100) {
					mLogCount = 0;
					m_editRecvData.setText("");
				}
				String messageString = (String) (msg.obj);
				int cursor = m_editRecvData.getSelectionStart();
				m_editRecvData.getText().insert(cursor, messageString + "\n");
				++mLogCount;
				break;
			case R.id.btnAPass:
				m_editRecvData.setText("");
				break;
			}

		}
	}

	class CheckDeviceThread extends Thread {
		@Override
		public void run() {
			// TODO Auto-generated method stub

			super.run();

			bOpenDevice = false;
			BluetoothComm.DisConnectBlueDevice(); // 首先断开服务中连接的设备,无论有没有连接,先断开,否则会造成连接不上
			try {
				sleep(100);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			showLogMessage("正在查找MPOS连接...");
			BluetoothComm.CheckBlueDevice();

		}
	}

	class ChecksetHandle extends Thread {
		@Override
		public void run() {
			// TODO Auto-generated method stub

			super.run();
			Boolean bHandle = false;
			while (true) {
				bHandle = BluetoothComm.setHandle();
				if (bHandle)
					break;
				try {
					sleep(20);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			// showLogMessage("正在搜索设备...");
			//

		}
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.zz_activity_main);
		mMainMessageHandler = new MessageHandler(Looper.myLooper());
		m_editRecvData = (EditText) findViewById(R.id.editRecvData);

		// 启动蓝牙服务,必须启动
		Intent intent = new Intent(this, BluetoothConnController.class);
		startService(intent);
		Intent i = new Intent(
				BluetoothConnController.GET_SERIVICE_STATUS_ACTION);
		sendBroadcast(i);

		// String str =toStringHex("4445364344344346");

		BluetoothComm = BluetoothCommmanager.getInstance(this, this);

		if (connectmodem == 0x00) {
			new CheckDeviceThread().start();
		} else {
			// 8C:DE:52:C6:DE:FE
			// BluetoothComm.ConnectDevice("8C:DE:52:C6:DE:FE");
			new ChecksetHandle().start();
			// 1:直接通过页面选择连接蓝牙 2:通过搜索函数搜索
			// ,BluetoothComm.ScanDevice(DEVICE_ADDRESS_FILETER,10); //搜索超时时间 秒
			// 在onDeviceFound 回调搜索结果
			Intent serverIntent = new Intent(this, DeviceListActivity.class);
			startActivityForResult(serverIntent, REQUEST_CONNECT_DEVICE);

		}

		WriteMainKey = ((Button) findViewById(R.id.btnWriteMainKey));
		WriteMainKey.setOnClickListener(btnClick);
		WriteWKey = ((Button) findViewById(R.id.btnWriteWKey));
		WriteWKey.setOnClickListener(btnClick);

		SetTerid = ((Button) findViewById(R.id.btnTerID));
		SetTerid.setOnClickListener(btnClick);

		GetMAc = ((Button) findViewById(R.id.btnGetMAc));
		GetMAc.setOnClickListener(btnClick);

		((Button) findViewById(R.id.btnAPass)).setOnClickListener(btnClick);
		((Button) findViewById(R.id.btnANoPass)).setOnClickListener(btnClick);
		((Button) findViewById(R.id.btnNoAPass)).setOnClickListener(btnClick);
		((Button) findViewById(R.id.btnNoANoPass)).setOnClickListener(btnClick);
		((Button) findViewById(R.id.btndispost)).setOnClickListener(btnClick);
		((Button) findViewById(R.id.btnGetBatty)).setOnClickListener(btnClick);

	}

	@Override
	public void onSendOK(int packType) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onProgress(byte[] data) {
		// TODO Auto-generated method stub

	}

	public void showLogMessage(String msg) {
		Message updateMessage = mMainMessageHandler.obtainMessage();
		updateMessage.obj = msg;
		updateMessage.what = R.id.editRecvData;
		updateMessage.sendToTarget();
	}

	public static String hexStr2Str(String hexStr) {
		String str = "0123456789ABCDEF";
		char[] hexs = hexStr.toCharArray();
		byte[] bytes = new byte[hexStr.length() / 2];
		int n;
		for (int i = 0; i < bytes.length; i++) {
			n = str.indexOf(hexs[2 * i]) * 16;
			n += str.indexOf(hexs[2 * i + 1]);
			bytes[i] = (byte) (n & 0xff);
		}
		return new String(bytes);
	}

	public static byte[] strToBcd(String asc, Integer mode) {
		int len = asc.length();
		int mod = len % 2;
		if (mod != 0) {
			if (mode == 1) {
				asc = asc + "0";
			} else {
				asc = "0" + asc;
			}
			len = asc.length();
		}
		byte abt[] = new byte[len];
		if (len >= 2) {
			len = len / 2;
		}
		byte bbt[] = new byte[len];
		abt = asc.getBytes();
		int j, k;
		for (int p = 0; p < asc.length() / 2; p++) {
			if ((abt[2 * p] >= '0') && (abt[2 * p] <= '9')) {
				j = abt[2 * p] - '0';
			} else if ((abt[2 * p] >= 'a') && (abt[2 * p] <= 'z')) {
				j = abt[2 * p] - 'a' + 0x0a;
			} else {
				j = abt[2 * p] - 'A' + 0x0a;
			}

			if ((abt[2 * p + 1] >= '0') && (abt[2 * p + 1] <= '9')) {
				k = abt[2 * p + 1] - '0';
			} else if ((abt[2 * p + 1] >= 'a') && (abt[2 * p + 1] <= 'z')) {
				k = abt[2 * p + 1] - 'a' + 0x0a;
			} else {
				k = abt[2 * p + 1] - 'A' + 0x0a;
			}

			int a = (j << 4) + k;
			byte b = (byte) a;
			bbt[p] = b;
		}
		return bbt;
	}

	@Override
	protected Dialog onCreateDialog(int id) {
		switch (id) {
		case DIALOG:
			return buildDialog(MainActivity.this);
		}
		return null;
	}

	private Dialog buildDialog(Context context) {
		LayoutInflater inflater = LayoutInflater.from(MainActivity.this);
		final View textEntryView = inflater.inflate(R.layout.zz_main, null);

		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		builder.setTitle("alert_dialog_text_entry");
		builder.setView(textEntryView);
		builder.setPositiveButton("alert_dialog_ok",
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {
						// setTitle("点击了对话框上的确定按钮");
						// 获取数据
						final EditText password = (EditText) textEntryView
								.findViewById(R.id.password);// 曾经出现空指针，加上textEntryView.指明是哪个XML文件中的值
						message = password.getText().toString();
						// 输入密码后,进行数据加密
						BluetoothComm.InputPassword(message, 6);

						// Log.e("对话窗口输入的密码值是+++++", message);

					}
				});
		builder.setNegativeButton("alert_dialog_cancel",
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {
						Toast.makeText(MainActivity.this, "密码输入错误!!",
								Toast.LENGTH_SHORT).show();

					}
				});
		return builder.create();
	}

	@Override
	public void onReceive(byte[] data) {
		// TODO Auto-generated method stub
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < data.length; ++i) {
			sb.append(String.format("%02x", data[i]));
		}
		Log.e("onReceive", sb.toString());
		switch (data[0]) {
		case BluetoothCommmanager.ICBRUSH:
		case BluetoothCommmanager.BRUSHDATA:
			if (data[1] == 0x00) {

				String strCard = "";
				if (data.length < 3) {
					showLogMessage("刷卡失败");
					return;
				}
				int nCardLen = data[2];
				for (int i = 0; i < nCardLen; ++i) {
					strCard = strCard + String.format("%02x", data[i + 3]);
				}
				strCard = hexStr2Str(strCard);
				showLogMessage("卡号:" + strCard.toString());
				int nCardtye = data[3 + nCardLen];

				if (Language == 0) {
					showLogMessage("Credit Sucess,wait...");
				} else {
					if (nCardtye == 0x01) // 降级
						showLogMessage("刷卡成功===降级");
					else
						showLogMessage("刷卡成功===正常");
				}

				// 提示输入密码
				showDialog(DIALOG);

			} else {
				if (Language == 0)
					showLogMessage("Credit Failure,ErrorCode:"
							+ Integer.toString(data[1]));
				else
					showLogMessage("刷卡失败,错误代码:" + Integer.toString(data[1]));
			}
			break;
		case BluetoothCommmanager.CHECK_IC: {
			String strError = "";
			strError = String.format("%02x", data[1]);
			if (strError.equals("00"))
				strError = "在待机界面插入IC卡";

			else
				strError = "交易功能插入IC卡";

			showLogMessage(strError);

		}
			break;
		case BluetoothCommmanager.GETCARDDATA:
		case BluetoothCommmanager.GETTRACKDATA_CMD: {
			String strError = "";
			strError = String.format("%02x", data[1]);

			if (strError.equals("e1"))
				strError = strError + ":用户取消";
			else if (strError.equals("e2"))
				strError = strError + ":超时退出";
			else if (strError.equals("e3"))
				strError = strError + ":IC卡处理数据失败";
			else if (strError.equals("e4"))
				strError = strError + ":无IC卡参数";
			else if (strError.equals("e5"))
				strError = strError + ":交易终止";
			else if (strError.equals("e6"))
				strError = strError + ":加密失败,用户拔出IC卡";
			else if (strError.equals("4c"))
				strError = strError + ":低电量,不允许交易";
			else if (strError.equals("46")) {
				strError = strError + ":已关机";
				/*
				 * BluetoothComm.DisConnectBlueDevice(); if (Language==0)
				 * showLogMessage("Disconnect Wait..."); else
				 * showLogMessage("正在断开连接...");
				 */
			}
			if (Language == 0)
				showLogMessage("Credit Failure,ErrorCode:" + strError);
			else
				showLogMessage("操作失败,错误代码:" + strError);
		}
			break;
		case (byte) BluetoothCommmanager.GETENCARDDATA: {
			String strData = "";
			for (int i = 0; i < data.length; ++i) {
				strData = strData + String.format("%02x", data[i]);
			}
			showLogMessage("CardData :" + strData.toString());
		}
			break;
		case BluetoothCommmanager.GETMAC:
			sb.setLength(0);
			if (data[1] == 0x00) {
				for (int i = 2; i < 10; i++) {
					sb.append(String.format("%02x", data[i]));
				}
				showLogMessage("MAC Sucess:" + sb.toString());

			} else {
				showLogMessage("MAC failure:" + Integer.toString(data[1]));
			}

			// String mac =toStringHex(sb.toString());
			break;

		case BluetoothCommmanager.GETSNVERSION:
			if (data[1] == 0x00) {
				if (data.length < 2)
					return;
				String strDeviceSn = "";
				for (int i = 0; i < data[2]; ++i) {
					strDeviceSn = strDeviceSn
							+ String.format("%02x", data[i + 3]);
				}
				strDeviceSn = toStringHex(strDeviceSn);
				showLogMessage("SN:" + strDeviceSn.toString());
				strDeviceSn = "";
				for (int i = data[2] + 4; i < data[2] + 4 + 16; ++i) {
					strDeviceSn = strDeviceSn + String.format("%02x", data[i]);
				}
				strDeviceSn = toStringHex(strDeviceSn);
				showLogMessage("Version:" + strDeviceSn.toString());

				BluetoothComm.MagnNoAmountNoPasswordCard(WAIT_TIMEOUT, 100);
				showLogMessage("刷卡");
			} else {
				showLogMessage("SN Failure:" + Integer.toString(data[1]));
			}

			// AudioCommmanager.MagnCard(12000,nMonery,0x01);
			break;
		case BluetoothCommmanager.GETTERNO:
			if (data[1] == 0x00) {
				String strTerSn = "";
				try {
					strTerSn = new String(data, "GB2312");
				} catch (UnsupportedEncodingException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				strTerSn = strTerSn.substring(1, strTerSn.length() - 1);
				showLogMessage("TerNo:" + strTerSn.toString());

			} else {
				showLogMessage("Terid Failure:" + Integer.toString(data[1]));
			}

			break;
		case BluetoothCommmanager.Battery:
			if (data[1] == 0x00) {
				StringBuilder sbBattery = new StringBuilder();
				for (int i = 2; i < 3; ++i) {
					sbBattery.append(String.format("%02x", data[i]));
				}
				int batty = Integer.parseInt(sbBattery.toString(), 16);
				showLogMessage("Battery Sucess:" + Integer.toString(batty));
			} else {
				showLogMessage("Battery failure:" + Integer.toString(data[1]));
			}
			break;
		case BluetoothCommmanager.IC_GETSTATUS:
			if (data[1] == 0x00) {
				if (Language == 0)
					showLogMessage("GetStatus  IC 	Sucess");
				else
					showLogMessage("有卡插入");
			} else {
				if (Language == 0)
					showLogMessage("GetStatus  IC 	FAILD");
				else
					showLogMessage("没有卡插入,错误代码:" + Integer.toString(data[1]));
			}
			break;
		case BluetoothCommmanager.IC_CLOSE:
			if (data[1] == 0x00) {
				if (Language == 0)
					showLogMessage("Close  IC 	Sucess");
				else
					showLogMessage("关闭IC卡成功");
			} else {
				if (Language == 0)
					showLogMessage("Close  IC 	FAILD");
				else
					showLogMessage("关闭IC卡失败,错误代码:" + Integer.toString(data[1]));

			}
			break;
		case BluetoothCommmanager.IC_OPEN:
			if (data[1] == 0x00) {
				if (Language == 0) {
					showLogMessage("Open  IC  Sucess");
				} else {
					showLogMessage("IC卡打开成功");
				}
				String openATR = "";
				int len = data[2];
				for (int i = 2; i < len + 3; ++i) {
					openATR = openATR + String.format("%02x", data[i]);
				}
				showLogMessage("IC Open ATR:" + openATR.toString());
			} else if (data[1] == 0x13) {
				if (Language == 0)
					showLogMessage("Charge  IC  FAILD");
				else
					showLogMessage("IC卡上电失败");
			} else if (data[1] == 0x04) {
				if (Language == 0)
					showLogMessage("Find  IC  FAILD");
				else
					showLogMessage("无卡");
			} else {
				if (Language == 0)
					showLogMessage("IC Open failure:"
							+ Integer.toString(data[1]));
				else
					showLogMessage("IC卡打开失败,错误代码:" + Integer.toString(data[1]));
			}
			break;
		case BluetoothCommmanager.IC_WRITEAPDU:
			if (data[1] == 0x00) {
				if (Language == 0) {
					showLogMessage("Write  APDU 	Sucess");
				} else {
					showLogMessage("IC卡Apdu命令成功");
				}
				String RESP = "";
				int len = data[2];
				for (int i = 2; i < len + 3; ++i) {
					RESP = RESP + String.format("%02x", data[i]);
				}
				showLogMessage("RESP:" + RESP.toString());

			} else if (data[1] == 0x04) {
				if (Language == 0)
					showLogMessage("Find  IC  FAILD");
				else
					showLogMessage("无卡");
			} else if (data[1] == 0x05) {
				if (Language == 0)
					showLogMessage("Data  Exchange  FAILD");
				else
					showLogMessage("数据交换错误");
			} else {
				if (Language == 0)
					showLogMessage("Write  Apdu  FAILD:"
							+ Integer.toString(data[1]));
				else
					showLogMessage("Apdu命令失败,错误代码:" + Integer.toString(data[1]));
			}
			break;
		case BluetoothCommmanager.ProofIcParm:
			if (data[1] == 0x00) {
				if (Language == 0)
					showLogMessage("IC  PROOF 	Sucess");
				else
					showLogMessage("IC二次论证成功");
				for (int i = 2; i < data.length; ++i) {
					sb.append(String.format("%02x", data[i]));
				}
				showLogMessage("二次论证收到数据:" + sb.toString());
			} else {
				if (Language == 0)
					showLogMessage("IC  PROOF 	FAILD");
				else
					showLogMessage("IC二次论证失败,错误代码:" + Integer.toString(data[1]));
			}

			break;
		default:
			break;

		}

	}

	@Override
	public void onTimeout() {
		// TODO Auto-generated method stub

		showLogMessage("超时");

	}

	@Override
	public void onError(int code, String msg) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onResult(int ntype, int code) {
		// TODO Auto-generated method stub
		switch (ntype) {

		case BluetoothCommmanager.MAINKEY:
			if (code == 0x00) {
				if (Language == 0)
					showLogMessage("Set MainKey Sucess");
				else
					showLogMessage("主密钥设置成功");
			} else {
				if (Language == 0)
					showLogMessage("Set MainKey Failure,ErrorCode:"
							+ Integer.toString(code));
				else
					showLogMessage("主密钥设置失败,错误代码:" + Integer.toString(code));
			}
			break;
		case BluetoothCommmanager.GETMAC:
			if (Language == 0)
				showLogMessage("MAC Failure,ErrorCode:"
						+ Integer.toString(code));
			else
				showLogMessage("MAC  获取失败,错误代码:" + Integer.toString(code));
			break;
		case BluetoothCommmanager.WORKEY:
			if (code == 0x00) {
				if (Language == 0)
					showLogMessage("Set Workkey Sucess");
				else
					showLogMessage("工作密钥设置成功");
			} else {
				if (Language == 0)
					showLogMessage("Set Workkey Failure,ErrorCode:"
							+ Integer.toString(code));
				else
					showLogMessage("工作密钥设置失败,错误代码:" + Integer.toString(code));
			}
			break;

		case BluetoothCommmanager.SETTERNO:
			if (code == 0x00) {
				if (Language == 0)
					showLogMessage("Set  TerNo 	Sucess");
				else
					showLogMessage("商户号终端号设置成功");

				if (Language == 0)
					showLogMessage("Read TerNo...");
				else
					showLogMessage("正在读取TerNo...");

				BluetoothComm.ReadTernumber();
			} else {
				if (Language == 0)
					showLogMessage("Set TERNO Failure,ErrorCode:"
							+ Integer.toString(code));
				else
					showLogMessage("商户号终端号设置失败,错误代码:" + Integer.toString(code));
			}
			break;

		default:
			if (Language == 0)
				showLogMessage("Failure,ErrorCode:" + Integer.toString(code));
			else
				showLogMessage("获取失败,错误类型 :" + Integer.toString(ntype)
						+ ",错误代码:" + code);
			break;
		}
		// showLogMessage("onResult");
	}

	@Override
	public void onDevicePlugged() {
		// TODO Auto-generated method stub

	}

	@Override
	public void onDeviceUnplugged() {
		// TODO Auto-generated method stub

	}

	private View.OnClickListener btnClick = new View.OnClickListener() {
		@SuppressWarnings("unused")
		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			// 有密码有金额
			case R.id.btnAPass: {
				m_editRecvData.setText("");
				if (Language == 0)
					showLogMessage("Input Amount +Brush/INSERT + PWD...");
				else
					showLogMessage("请输入金额+请刷卡/插卡+交易密码...");
				BluetoothComm.MagnAmountPasswordCard(WAIT_TIMEOUT, nAmount);
				// BluetoothComm.GetSnVersion();

			}
				break;
			// 有金额无密码
			case R.id.btnANoPass: {
				m_editRecvData.setText("");
				if (Language == 0)
					showLogMessage("Input Amount +Brush/INSERT...");
				else
					showLogMessage("请输入金额+请刷卡/插卡...");
				BluetoothComm.MagnAmountNoPasswordCard(WAIT_TIMEOUT, nAmount);
			}
				break;
			// 无金额有密码
			case R.id.btnNoAPass: {
				m_editRecvData.setText("");
				if (Language == 0)
					showLogMessage("Input  Brush/INSERT +PWD...");
				else
					showLogMessage("请刷卡/插卡 +输入密码...");
				BluetoothComm.MagnNoAmountPasswordCard(WAIT_TIMEOUT, nAmount);
			}
				break;
			// 无金额无密码
			case R.id.btnNoANoPass: {
				m_editRecvData.setText("");
				if (Language == 0)
					showLogMessage("Input  Brush/INSERT...");
				else
					showLogMessage("请刷卡/插卡...");
				BluetoothComm.MagnNoAmountNoPasswordCard(WAIT_TIMEOUT, nAmount);
			}
				break;

			case R.id.btnWriteMainKey: {
				String order;
				order = "31313131313131313232323232323232";
				if (order.length() != 32) {
					if (Language == 0)
						Toast.makeText(MainActivity.this, "MainKey Error",
								Toast.LENGTH_SHORT).show();
					else
						Toast.makeText(MainActivity.this, "主密钥输入不正确",
								Toast.LENGTH_SHORT).show();
					break;
				}

				byte[] sendBuf = hexStr2Bytes(order);
				if (Language == 0)
					showLogMessage("Set MainKey,wait...");
				else
					showLogMessage("正在设置主密钥..");
				BluetoothComm.WriteMainKey(16, sendBuf);
			}
				break;
			case R.id.btnWriteWKey: {
				String order = "FBC94E8506FECB63E31BDB62146A1D8960C15261FBC94E8506FECB63E31BDB62146A1D8960C15261FBC94E8506FECB63E31BDB62146A1D8960C15261";
				byte[] sendBuf = hexStr2Bytes(order);
				if (Language == 0)
					showLogMessage("Set WorkKey,wait...");
				else
					showLogMessage("正在设置工作密钥..");
				BluetoothComm.WriteWorkKey(sendBuf.length, sendBuf);
			}

				break;
			case R.id.btnGetMAc: {
				String order = "0200702406c020c09a111662177109000572090000000000000000010010082211051000000012376217710900057209d2211220000004270000003032313530303630383036303031303030303037323330313536c45ce78b6f7edbcb260000000000000001459f26086f4a29cdf75b76c09f2701809f101307010103a0a002010a01000000000048b967809f37049941001c9f36020a0c9505000004e0009a031508109c01009f02060000000000015f2a02015682027c009f1a0201569f03060000000000009f3303e0f1c89f34030203009f3501229f1e0830223031323436328408a0000003330101019f090200209f41040000000100152200001300050500";
				byte[] sendBuf = order.getBytes();
				if (Language == 0)
					showLogMessage("Get Mac,wait...");
				else
					showLogMessage("正在获取MAC..");

				byte[] sendBuf1 = hexStr2Bytes(order);
				BluetoothComm.GetMac(sendBuf1.length, sendBuf1);
			}
				break;
			// 设置终端ID
			case R.id.btnTerID:
			// 开始设置终端号商户号
			{

				String order = "12345678987654321012345";
				byte[] sendBuf = order.getBytes();
				if (Language == 0)
					showLogMessage("Set Terid,wait...");
				else
					showLogMessage("正在设置终端号商户号..");
				BluetoothComm.WriteTernumber(sendBuf);

			}
				break;

			case R.id.btndispost: {

				if (Language == 0)
					showLogMessage("Cancel,wait...");
				else
					showLogMessage("正在取消操作..");
				// BluetoothComm.MagnCancel();

				BluetoothComm.DisScanDevice();
			}

				break;

			case R.id.btnGetBatty: {
				if (Language == 0)
					showLogMessage("Get Battery wait...");
				else
					showLogMessage("正在获取电池电量..");

				BluetoothComm.ReadBattery();

			}
				break;
			default:
				break;
			}
		}
	};

	@Override
	public void onReadCardData(Map hashcard) {
		// TODO Auto-generated method stub
		Message updateMessage = mMainMessageHandler.obtainMessage();
		updateMessage.obj = "";
		updateMessage.what = R.id.btnAPass;
		updateMessage.sendToTarget();

		showLogMessage("hashcard");

		Set set = hashcard.entrySet();
		Iterator iterator = set.iterator();
		while (iterator.hasNext()) {
			Map.Entry<String, String> entry1 = (Map.Entry<String, String>) iterator
					.next();
			if (entry1.getKey().equals("PAN")) {
				strPan = entry1.getValue();
			}
			showLogMessage(entry1.getKey() + "==" + entry1.getValue());
			Log.e(DEBUG_TAG, entry1.getKey() + "==" + entry1.getValue());

		}

	}

	@Override
	public void onDeviceState(int nState) {
		// TODO Auto-generated method stub
		Log.d("onDeviceState --------", Integer.toString(nState));
		bOpenDevice = false;
		if (nState == -1)
			showLogMessage("未找到MPOS...");
		else if (nState == 0) {
			showLogMessage("连接MPOS失败...");
		} else if (nState == 2) {
			showLogMessage("MPOS已断开,请重新连接...");
		} else if (nState == 1) {
			showLogMessage("连接MPOS成功,正在获取SN号...");
			bOpenDevice = true;
			BluetoothComm.GetSnVersion();

		} else if (nState == 3) {
			showLogMessage("蓝牙手动启动");

		} else if (nState == 4) {
			bOpenDevice = false;
			showLogMessage("手动关闭蓝牙");
			BluetoothComm.DisConnectBlueDevice();

		}
	}

	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		Log.d(DEBUG_TAG, "onActivityResult: requestCode=" + requestCode
				+ ", resultCode=" + resultCode);
		switch (requestCode) {
		case REQUEST_CONNECT_DEVICE:
			// When DeviceListActivity returns with a device to connect
			if (resultCode == Activity.RESULT_OK) {
				// Get the device MAC address
				// BluetoothComm.DisConnectBlueDevice();
				showLogMessage("正在配对连接MPOS设备...");
				String address = data.getExtras().getString(
						DeviceListActivity.EXTRA_DEVICE_ADDRESS);

				BluetoothComm.ConnectDevice(address);

			}
			break;
		case REQUEST_DISCONNECT_DEVICE:
			if (resultCode == Activity.RESULT_OK) {
				/*
				 * String address = data.getExtras()
				 * .getString(ConnectedDeviceListActivity
				 * .EXTRA_CONNECTED_ADDRESS); Intent i = new
				 * Intent(BluetoothConnController.DISCONNECT_REQUEST_ACTION);
				 * i.putExtra(DISCONNECT_DEVICE_ADDRESS, address);
				 * sendBroadcast(i);
				 */

			}
			break;
		case REQUEST_ENABLE_BT:
			// When the request to enable Bluetooth returns
			if (resultCode == Activity.RESULT_OK) {
				// Bluetooth is now enabled, so set up a chat session
				// setupChat();
			} else {
				// User did not enable Bluetooth or an error occured
				Log.d(DEBUG_TAG, "BT not enabled");
				Toast.makeText(this, "bt_not_enabled_leaving",
						Toast.LENGTH_SHORT).show();
				finish();
			}
			break;

		case REQUEST_PREF_SETTING:
			// When the request to enable Bluetooth returns
			if (resultCode == Activity.RESULT_OK) {

			}
			break;

		case REQUEST_ABOUT_INFO:
			if (resultCode == Activity.RESULT_OK) {
				// Log.e(DEBUG_TAG,
				// "[onActivityResult] Current version is = "+versionNo);
			}
			break;

		}
	}

	@Override
	public void onDeviceFound(final List<BluetoothDevice> list) {
		if (list.size() == 0)
			showLogMessage("查找到无设备");
		// TODO Auto-generated method stub
		for (BluetoothDevice device : list) {
			String map = device.getName() + "=" + device.getAddress();
			// System.out.println(map);
			showLogMessage(map);
			System.out.println(map);
			showLogMessage("正在配对连接:" + device.getName());
			String address = device.getAddress();
			BluetoothComm.ConnectDevice(address);
		}
	}

	@Override
	protected void onDestroy() {

		BluetoothComm.DisConnectBlueDevice();
		super.onDestroy();
	}
}
