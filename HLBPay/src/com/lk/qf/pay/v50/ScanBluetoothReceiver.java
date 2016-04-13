package com.lk.qf.pay.v50;

import android.app.Activity;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.cbapay.app.CBApplication;
import com.lk.bhb.pay.R;

public class ScanBluetoothReceiver extends BroadcastReceiver {
	private FindBluetoothActivity mActivity;
	private AdapterManager mAdapterManager;
	private ProgressBar mProgressBar;
	private ProgressDialog mProgressDialog;
	private CBApplication mApplication;
	private TouchObject mTouchObject;
	// private BluetoothDevice bluetoothDevice = null;
	// private BluetoothDevice device = null;

	public ScanBluetoothReceiver(Activity activity,
			AdapterManager adapterManager, ProgressBar progressBar) {
		mApplication = CBApplication.getInstance();
		this.mActivity = (FindBluetoothActivity) activity;
		this.mAdapterManager = adapterManager;
		this.mProgressBar = progressBar;
	}

	// public ScanBluetoothReceiver(Activity activity, AdapterManager
	// adapterManager, ProgressDialog progressDialog,BluetoothDevice device){
	// mApplication = CBApplication.getInstance();
	// this.mActivity = (FindBluetoothActivity) activity;
	// this.mAdapterManager = adapterManager;
	// this.mProgressDialog = progressDialog;
	// this.device = device;
	// }

	@Override
	public void onReceive(Context context, final Intent intent) {
		// Tools.ShowSysout("开始进入"+intent.getAction());
		String action = intent.getAction();
		BluetoothDevice bluetoothDevice = intent
				.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
		if (action.equals(BluetoothDevice.ACTION_FOUND)) {

			// Tools.ShowSysout("搜到的设备" + bluetoothDevice.getName());
			if (bluetoothDevice != null) {
				mActivity.findViewById(R.id.deviceListView).setVisibility(View.VISIBLE);
				if (bluetoothDevice.getBondState() != BluetoothDevice.BOND_BONDED) {
					// 已经配对的已经添加到list中，所以不需要再次添加
					mAdapterManager.addDevice(bluetoothDevice);
					mAdapterManager.updateDeviceAdapter();
				} 
			}

		} else if (action.equals(BluetoothAdapter.ACTION_DISCOVERY_FINISHED)) {
			//Tools.ShowSysout("个数" + mAdapterManager.getDeviceList().size());
//			if (mAdapterManager.getDeviceList().size() != 0) {
//				mActivity.findViewById(R.id.deviceListView).setVisibility(
//						View.VISIBLE);
//			}
			if (mProgressBar != null) {
				//Tools.ShowSysout("waitlog进啦了");
				mProgressBar.setVisibility(View.INVISIBLE);
			}
			if (mAdapterManager.getDeviceList().size() == 0) {
				Toast.makeText(mActivity, "没有搜索到相关设备，请重试", Toast.LENGTH_LONG)
						.show();
			}

			mActivity.unregisterReceiver(this);
		} else if (action.equals(BluetoothDevice.ACTION_BOND_STATE_CHANGED)) {
			/*
			 * if(null == mAdapterManager){ mAdapterManager =
			 * mApplication.getAdapterManager(); } if(null == mTouchObject){
			 * mTouchObject = mApplication.getTouchObject(); }
			 * //鍙栧緱鐘舵�鏀瑰彉鐨勮澶囷紝鏇存柊璁惧鍒楄〃淇℃伅 锛堥厤瀵圭姸鎬侊級 BluetoothDevice device =
			 * intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
			 * mAdapterManager.changeDevice(mTouchObject.clickDeviceItemId,
			 * device); mAdapterManager.updateDeviceAdapter();
			 * mActivity.unregisterReceiver(this);
			 */
//			if (bluetoothDevice.getBondState() == BluetoothDevice.BOND_BONDED) { // 配对完成
//				//Tools.ShowSysout("调用配对完成");
//				mProgressDialog = ProgressDialog.show(mActivity, "正在连接设备",
//						"连接中,请稍候..", true, true);
//				BluetoothConnectionBluetoothTask task = new BluetoothConnectionBluetoothTask(
//						bluetoothDevice, mActivity, mProgressDialog);
//				task.executeOnExecutor(Utils.FULL_TASK_EXECUTOR);
//			}
		} else {
//			if (bluetoothDevice.getBondState() == BluetoothDevice.BOND_BONDED) { // 配对完成
//				//Tools.ShowSysout("调用配对完成");
//				mProgressDialog = ProgressDialog.show(mActivity, "正在连接设备",
//						"连接中,请稍候..", true, true);
//				BluetoothConnectionBluetoothTask task = new BluetoothConnectionBluetoothTask(
//						bluetoothDevice, mActivity, mProgressDialog);
//				task.executeOnExecutor(Utils.FULL_TASK_EXECUTOR);
//			}

		}
		// if(mProgressDialog!=null) {
		// Tools.ShowSysout("waitlog进啦了");
		// mProgressDialog.dismiss();
		// }
	}

}
