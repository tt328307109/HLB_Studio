package com.lk.qf.pay.v50;

import android.app.Activity;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import com.cbapay.app.CBApplication;

public class PairStateChangeReceiver extends BroadcastReceiver {
	private Activity mActivity;
	private BluetoothDevice device;
	private int n = 0;
	private boolean isV50e;

	public PairStateChangeReceiver(Activity activity, BluetoothDevice device,boolean isV50e) {
		this.mActivity = activity;
		this.device = device;
		this.isV50e = isV50e;
	}
	
	public void setDevice(BluetoothDevice device){
		this.device = device;
	}

	@Override
	public void onReceive(Context context, Intent intent) {
		if (intent.getAction()
				.equals(BluetoothDevice.ACTION_BOND_STATE_CHANGED)) {
			//Tools.ShowSysout("进来changge了");
			// if(null == mAdapterManager){
			// mAdapterManager = mApplication.getAdapterManager();
			// }
			// if(null == mTouchObject){
			// mTouchObject = mApplication.getTouchObject();
			// }
			// BluetoothDevice device =
			// intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
			// mAdapterManager.updateDeviceAdapter();
			n++;
			if(BluetoothAdapter.getDefaultAdapter().getRemoteDevice(device.getAddress()).getBondState() != BluetoothDevice.BOND_BONDED){
				if(n>1){
					Toast.makeText(context, "配对失败，请重试！", Toast.LENGTH_LONG).show();
					n = 0;
				}
				return;
			}
			ProgressDialog waitDialog = ProgressDialog.show(mActivity,
					"正在连接设备,请耐心等待", "连接中,请稍候..", true, true);
			BluetoothConnectionBluetoothTask task = new BluetoothConnectionBluetoothTask(
					device, mActivity, waitDialog,isV50e);
			task.execute();
			mActivity.unregisterReceiver(this);
		}
	}

}
