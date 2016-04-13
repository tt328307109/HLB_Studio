package com.lk.qf.pay.v50;

import android.app.AlertDialog;
import android.bluetooth.BluetoothSocket;
import android.content.Context;

public class Tools {
	
	public static BluetoothSocket socket;
	
	public static void showDialog(String info, Context context) {
		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		builder.setMessage(info);
		builder.setTitle("提示");
		builder.setCancelable(false);
		builder.setPositiveButton("确认", null).create().show();
	}
}
