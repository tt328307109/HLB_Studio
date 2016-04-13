package com.lk.qf.pay.zxb;

import android.app.Activity;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.icardpay.zxbbluetooth.api.ZxbListener;
import com.icardpay.zxbbluetooth.api.ZxbManager;
import com.lk.bhb.pay.R;
import com.lk.qf.pay.activity.BaseActivity;
import com.lk.qf.pay.jhl.BluetoothConnController;
import com.lk.qf.pay.jhl.PayByBuleCardActivity;
import com.lk.qf.pay.tool.T;
import com.lk.qf.pay.wedget.switchview.SwitchView;
import com.lk.qf.pay.wedget.switchview.SwitchView.OnSwitchChangeListener;

public class ZXBDeviceListActivity extends Activity {

	private ZxbManager mZxbManager;

	private SwitchView mBluetoothSw;

	private ScrollView mDeviceListSv;

	private ProgressBar mFoundPb;

	// found view
	private LinearLayout mFoundLL;

	private AlertDialog mDeviceDialog;
	private Intent intent = null;

	// 当前操作的设备
	private BluetoothDevice mCurrentDevice;
	private String action;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.zxb_device_list);
		action = getIntent().getAction();
		// 初始化掌芯宝-蓝牙SDK
		mZxbManager = ZxbManager.getInstance(this);
		mZxbManager.setZxbListener(mListener);
		// 设置日志打印
		mZxbManager.setDebug(true);
		initViews();

		if (mBluetoothSw.getSwitchStatus()) {
			// 已经打开蓝牙,显示设备列表
			mDeviceListSv.setVisibility(View.VISIBLE);
			mZxbManager.startDiscovery();
		} else {
			// 未打开蓝牙,隐藏设备列表
			mDeviceListSv.setVisibility(View.INVISIBLE);
		}
		Log.i("result", "-----------------liost----oncreat------");

	}

	@Override
	protected void onRestart() {
		// 重新设置掌芯宝的监听,防止多个连续Activity使用掌芯宝时造成找不到回调的错误
		mZxbManager.setZxbListener(mListener);
		// 设置蓝牙状态
		mBluetoothSw.setSwitchStatus(mZxbManager.isEnabled());// 设置状态
//		action = getIntent().getAction();
		Log.i("result", "-----------------liost----onstart------"+action);
		super.onRestart();
	}

	@Override
	protected void onDestroy() {
		// 释放掌芯宝-蓝牙SDK,APP需在最后一个Activity销毁时释放SDK
		if (mZxbManager!=null) {
			
			mZxbManager.destroy();
		}
		super.onDestroy();
	}

	/**
	 * 切换蓝牙开关
	 */
	/*
	 * public void onSwitchBluetooth(View view) {
	 * mBluetoothSw.setChecked(mZxbManager.isEnabled()); }
	 */

	private void initViews() {
		mBluetoothSw = (SwitchView) findViewById(R.id.sw_bluetooth);
		mBluetoothSw.setSwitchStatus(mZxbManager.isEnabled());
		mBluetoothSw.setOnSwitchChangeListener(new OnSwitchChangeListener() {

			@Override
			public void onSwitchChanged(boolean isChecked) {
				// TODO Auto-generated method stub
				if (isChecked)
					mZxbManager.openBluetooth();
				else
					mZxbManager.closeBluetooth();
			}
		});

		mDeviceListSv = (ScrollView) findViewById(R.id.sv_devicelist);
		mFoundLL = (LinearLayout) findViewById(R.id.ll_found);
		mFoundPb = (ProgressBar) findViewById(R.id.pb_found);
		mDeviceDialog = new AlertDialog.Builder(this).setTitle("是否连接设备")
				.setPositiveButton("连接", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						mZxbManager.cancelDiscovery();
						startDeviceView(mCurrentDevice);
					}
				})
				.setNegativeButton("取消", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						// DO NOTHING
					}
				}).create();
	}

	/**
	 * 显示操作设备的Activity
	 * 
	 * @param device
	 *            要操作的蓝牙设备
	 */
	private void startDeviceView(BluetoothDevice device) {

		Log.i("result", "-------namw----" + device.getName());
		if (device.getName().startsWith("350")
				|| device.getName().startsWith("JHL")) {

			Intent intent12 = new Intent(this, BluetoothConnController.class);
			startService(intent12);
			Intent i = new Intent(
					BluetoothConnController.GET_SERIVICE_STATUS_ACTION);
			sendBroadcast(i);
			// 锦弘霖
			intent = new Intent(this, PayByBuleCardActivity.class);
			intent.putExtra("deviceAdd", "" + device.getAddress());
			Log.i("result", "-------actionaction----" + action);
			intent.setAction(action);
		} else if (device.getName().startsWith("ZXB")) {
			// 支付通
			intent = new Intent(this, ZXBBTDeviceActivity.class);
			intent.putExtra("device", device);
			intent.setAction(action);

		} else {
			T.ss("设备不匹配");
			return;
		}
		startActivity(intent);
//		finish();
	}

	/**
	 * 获取已绑定的设备列表,并添加到View中
	 */
	private void getBondedDevices() {
		for (BluetoothDevice device : mZxbManager.getBondedDevices())
			addFoundDeviceView(device);
	}

	/**
	 * 增加一个搜索到的设备的View
	 * 
	 * @param device
	 *            蓝牙设备
	 */
	private void addFoundDeviceView(final BluetoothDevice device) {
		View foundView = getLayoutInflater().inflate(R.layout.zxb_item_device,
				null);
		final TextView nameTv = (TextView) foundView
				.findViewById(R.id.tv_device_name);

		device.getAddress();
		nameTv.setText(device.getName());

		foundView.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				mCurrentDevice = device;

				mDeviceDialog.setMessage(device.getName());
				mDeviceDialog.show();
			}
		});

		foundView.setTag(device);
		mFoundLL.addView(foundView);
	}

	/**
	 * 清除所有搜索到的设备的View
	 */
	private void removeAllFoundDeviceViews() {
		mFoundLL.removeAllViews();
	}

	private ZxbListener mListener = new ZxbListener() {

		@Override
		public void onStateChanged(int state) {
			switch (state) {
			case BluetoothAdapter.STATE_ON:
				mBluetoothSw.setSwitchStatus(true);
				// 蓝牙开启后自动搜索,显示设备列表
				mDeviceListSv.setVisibility(View.VISIBLE);
				mZxbManager.startDiscovery();
				break;
			case BluetoothAdapter.STATE_OFF:
				mBluetoothSw.setSwitchStatus(false);
				break;
			case BluetoothAdapter.STATE_TURNING_ON:
				mBluetoothSw.setSwitchStatus(true);
				break;
			case BluetoothAdapter.STATE_TURNING_OFF:
				mBluetoothSw.setSwitchStatus(false);
				// 隐藏设备列表
				mDeviceListSv.setVisibility(View.INVISIBLE);
				// 清除设备列表
				mFoundLL.removeAllViews();
				break;
			}
		}

		@Override
		public void onDeviceFound(BluetoothDevice device, short rssi) {
			addFoundDeviceView(device);
		}

		@Override
		public void onDiscoveryStarted() {
			mFoundPb.setVisibility(View.VISIBLE);
		}

		@Override
		public void onDiscoveryFinished() {
			mFoundPb.setVisibility(View.INVISIBLE);
		}

		@Override
		public void onBondStateChanged(BluetoothDevice device, int bondState) {
		}

		@Override
		public void onConnectStateChanged(BluetoothDevice device, int state) {
		}

		@Override
		public void onError(int errorCode) {
			Toast.makeText(ZXBDeviceListActivity.this, "刷卡器返回错误码：" + errorCode,
					Toast.LENGTH_SHORT).show();
		}

		@Override
		public void onGetDeviceInfo(String deviceVersion,
				String firmwareVersion, String deviceSn, int adc) {
		}

		@Override
		public void onUpgradeProgress(int progress) {
		}

		@Override
		public void onUpgradeFirmware(boolean success, String respCode) {
		}

		@Override
		public void onUpdateKey() {
		}

		@Override
		public void onVerifyMac(boolean result) {
		}

		@Override
		public void onOpenCardReader(String statusCode) {
		}

		@Override
		public void onReadCardNumber(String random, String encryptCardNumber) {
		}

		@Override
		public void onReadTrackData(String account, String random,
				String encryptTrack2, String encryptTrack3) {
		}

		@Override
		public void onGetPbocTradeData(String result, String tradeDate,
				String random, String account, String icSerialNo,
				String cardExpire, String encryptTrack2, String data55) {
		}

		@Override
		public void onExecutePbocSecondAuth(String result, String tradeDate,
				String random, String tradeData, String mac) {
		}

		public void onEndProcess() {
		}

		@Override
		public void onEncryptPin(String random, String encryptPin) {
		}

		@Override
		public void onCalculateMac(String random, String mac) {
		}

		@Override
		public void onChangeToUpgradeMode() {
		}

		@Override
		public void onWriteDeviceId() {
		}

		@Override
		public void onWriteMainKey() {
		}

		@Override
		public void onTestCommunicate() {
		}

		@Override
		public void onCancelCardReader() {
			// TODO Auto-generated method stub

		}
	};
}
