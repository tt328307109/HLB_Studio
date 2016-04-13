package com.lk.qf.pay.v50;

import java.util.Set;

import com.lk.bhb.pay.R;
import com.lk.qf.pay.golbal.Actions;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

public class FindBluetoothActivity extends Activity implements OnTouchListener {

	private BluetoothAdapter adapter = null;
	private ProgressDialog waitDialog = null;
	private ProgressBar progressBar = null;
	private PairStateChangeReceiver mPairStateChangeReceiver;
	private ScanBluetoothReceiver mScanBluetoothReceiver = null;
	public static final int REQUEST_ENABLE = 10000;
	public static int REQUEST_BACKCODE = 10001;
	public static int REQUEST_CHSAIN = 10001;
	public static int REQUEST_BANDPOS = 10002;
	private AdapterManager mAdapterManager;
	private TouchObject mTouchObject;
	ListView mDeviceListView;
	TextView text_search;
	View layout_search;
	private String action = "";
	private boolean isV50e=false;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE); // 去掉标题栏
		setContentView(R.layout.v50_activity_findbluetooth);
		init();
	}

	private void init() {
		Intent intent1 = getIntent();
		if (intent1 != null) {
			action = intent1.getAction();
		}

		FindBluetoothActivity.this.setFinishOnTouchOutside(false);
		layout_search = findViewById(R.id.layout_search);
		layout_search.setOnTouchListener(this);
		text_search = (TextView) findViewById(R.id.text_search);
		progressBar = (ProgressBar) findViewById(R.id.progressBar);
		adapter = BluetoothAdapter.getDefaultAdapter();
		mDeviceListView = (ListView) findViewById(R.id.deviceListView);
		mAdapterManager = new AdapterManager(this);
		mDeviceListView.setAdapter(mAdapterManager.getDeviceListAdapter());
		
		if (!adapter.isEnabled()) {
			Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
			startActivityForResult(intent, FindBluetoothActivity.REQUEST_ENABLE);
		} else {
			// waitDialog = ProgressDialog.show(FindBluetoothActivity.this,
			// "正在搜索可用消费终端设备", "搜索中,请稍候..", true, true);

			Set<BluetoothDevice> pairedDevices = adapter.getBondedDevices();
			if (pairedDevices.size() > 0) {
				mDeviceListView.setVisibility(View.VISIBLE);
				for (BluetoothDevice device : pairedDevices) {
					mAdapterManager.addDevice(device);// 将已经配对的设备添加到list中
				}
			}

			IntentFilter intentFilter = new IntentFilter();
			intentFilter.addAction(BluetoothDevice.ACTION_FOUND);
			intentFilter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
			intentFilter.addAction(BluetoothDevice.ACTION_BOND_STATE_CHANGED);
			if (null == mScanBluetoothReceiver) {
				mScanBluetoothReceiver = new ScanBluetoothReceiver(this,
						mAdapterManager, progressBar);
			}
			registerReceiver(mScanBluetoothReceiver, intentFilter);
			adapter.startDiscovery();
		}
		mDeviceListView.setOnItemClickListener(new OnItemClickListener() {
			@SuppressLint("NewApi")
			@Override
			public void onItemClick(AdapterView<?> a, View v, int position,
					long id) {

				BluetoothDevice device = mAdapterManager.getDeviceList().get(
						position);
				String deciceName = device.getName();
				Log.i("result", "-------deciceName-------"+deciceName);
				if (deciceName!=null) {
					
					if (deciceName.startsWith("V50ES_")||deciceName.startsWith("V50SE_")) {
						Log.i("result", "-------deciceNameisV50e-------");
						isV50e = true;
					}else{
						isV50e = false;
					}
				}else{
					isV50e = true;
				}
				adapter.cancelDiscovery();
				// 未配对
				if (device.getBondState() != BluetoothDevice.BOND_BONDED) {
					try {
						Toast.makeText(getApplicationContext(), "正在配对...",
								Toast.LENGTH_LONG).show();
						device.createBond();
					} catch (Exception e) {
						e.printStackTrace();
					}
					/*
					 * remark = FindBluetoothActivity.this.getIntent()
					 * .getIntExtra("remark", Integer.MAX_VALUE);
					 */
					if (null == mPairStateChangeReceiver) {
						mPairStateChangeReceiver = new PairStateChangeReceiver(
								FindBluetoothActivity.this, device,isV50e);
						IntentFilter intentFilter = new IntentFilter();
						intentFilter
								.addAction(BluetoothDevice.ACTION_BOND_STATE_CHANGED);
						registerReceiver(mPairStateChangeReceiver, intentFilter);
					} else {
						mPairStateChangeReceiver.setDevice(device);
					}

				} else { // 已经配对
					/*
					 * remark = FindBluetoothActivity.this.getIntent()
					 * .getIntExtra("remark", Integer.MAX_VALUE);
					 */
					waitDialog = ProgressDialog.show(
							FindBluetoothActivity.this, "正在连接设备,请耐心等待",
							"连接中,请稍候..", true, false);
					Log.i("result", "------action----" + action);

//					Intent intent = new Intent();
//					intent.putExtra("device", device);
					// Set result and finish this Activity

					Log.i("result", "------action-d---"+isV50e);
					BluetoothConnectionBluetoothTask task = new BluetoothConnectionBluetoothTask(
							device, FindBluetoothActivity.this, waitDialog,isV50e);
					task.execute();
				}
			}
		});

	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == REQUEST_ENABLE) {
			if (resultCode == RESULT_OK) {
				beginss();
			} else {
				Toast.makeText(this, "", Toast.LENGTH_LONG).show();
			}
		}

	}

	private void beginss() {

		Set<BluetoothDevice> pairedDevices = adapter.getBondedDevices();

		if (pairedDevices.size() > 0) {
			mDeviceListView.setVisibility(View.VISIBLE);
			for (BluetoothDevice device : pairedDevices) {
				mAdapterManager.addDevice(device);// 将已经配对的设备添加到list中
			}
		}

		// ProgressDialog waitDialog2 = ProgressDialog.show(this,
		// "正在搜索可用消费终端设备",
		// "搜索中,请稍候..", true, true);
		progressBar.setVisibility(View.VISIBLE);

		IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction(BluetoothDevice.ACTION_FOUND);
		intentFilter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
		intentFilter.addAction(BluetoothDevice.ACTION_BOND_STATE_CHANGED);
		if (null == mScanBluetoothReceiver) {
			mScanBluetoothReceiver = new ScanBluetoothReceiver(this,
					mAdapterManager, progressBar);
		}
		registerReceiver(mScanBluetoothReceiver, intentFilter);
		adapter.startDiscovery();
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			adapter.cancelDiscovery();
			finish();
		}
		return super.onKeyDown(keyCode, event);
	}

	@Override
	protected void onStop() {
		finish();
		super.onStop();
	}

	public void search(View v) {
		adapter.cancelDiscovery();
		Intent intent = new Intent();
		intent.setClass(FindBluetoothActivity.this, FindBluetoothActivity.class);
		startActivity(intent);
		finish();
	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		// 改变最下方搜索布局的按键效果
		if (v.getId() == R.id.layout_search) {
			switch (event.getAction()) {
			case MotionEvent.ACTION_DOWN:
				text_search.setTextColor(getResources().getColor(
						R.color.color_title_bg));
				break;
			case MotionEvent.ACTION_UP:
				text_search.setTextColor(getResources().getColor(
						R.color.airplane_white));
				break;
			}
		}

		return false;
	}

}
