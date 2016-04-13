package com.lk.qf.pay.v50;

import java.util.UUID;

import com.lk.qf.pay.golbal.Actions;
import com.lk.qf.pay.wedget.webview.T;

import android.app.Activity;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

public class BluetoothConnectionBluetoothTask extends
		AsyncTask<BluetoothDevice, Void, BluetoothSocket> {

	private BluetoothDevice device = null;
	private Activity activity = null;
	private ProgressDialog waitDialog = null;
//	private String action;
	private boolean isV50e;

	public BluetoothConnectionBluetoothTask(BluetoothDevice device,
			Activity activity, ProgressDialog waitDialog,boolean isV50e) {
		// this.device = device;
		this.device = BluetoothAdapter.getDefaultAdapter().getRemoteDevice(
				device.getAddress());
		this.activity = activity;
		this.waitDialog = waitDialog;
//		this.action = action;
		this.isV50e =isV50e;
	}

	@Override
	protected BluetoothSocket doInBackground(BluetoothDevice... params) {
		String uuid = "00001101-0000-1000-8000-00805F9B34FB";
		BluetoothSocket temp = null;
		BluetoothSocket btSocket = null;
		UUID uuid2 = UUID.fromString(uuid);
		// 以上取得socket方法不能正常使用， 用以下方法代替
		try {
			// temp = device.createRfcommSocketToServiceRecord(uuid2);
			// Method m = device.getClass().getMethod("createRfcommSocket",
			// new Class[] { int.class });
			// temp = (BluetoothSocket) m.invoke(device, 1);
			// 怪异错误： 直接赋值给socket,对socket操作可能出现异常，
			// 要通过中间变量temp赋值给socket
			temp = device.createInsecureRfcommSocketToServiceRecord(uuid2);
			btSocket = temp;
			btSocket.connect();
		} catch (Exception e) {
			e.printStackTrace();
			return null; // 返回失败
		}
		return btSocket;
	}

	@Override
	protected void onPostExecute(BluetoothSocket result) {
		if (result == null) {
			waitDialog.cancel();
			Toast.makeText(activity, "连接设备失败,请重试", Toast.LENGTH_SHORT).show();
			return;
		}
		Tools.socket = result;
		Intent intent = new Intent();
		intent.putExtra("isV50e", isV50e);
		activity.setResult(Activity.RESULT_OK, intent);
		activity.finish();
		waitDialog.dismiss();
		Log.i("result", "--------------dismiss---------");
//		intent.setClass(activity, PayByV50CardActivity.class);
//		if (action.equals(Actions.ACTION_CHECK)) {
//			intent.setAction(Actions.ACTION_CHECK_1);// 绑定设备
//		} else {
//			intent.setAction(Actions.ACTION_CASHIN_1);// 收款
//		}
//		activity.startActivity(intent);
	}

}
