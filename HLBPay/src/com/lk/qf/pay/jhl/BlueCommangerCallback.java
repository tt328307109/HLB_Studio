package com.lk.qf.pay.jhl;

import android.bluetooth.BluetoothDevice;

import java.util.List;
import java.util.Map;

public interface BlueCommangerCallback {
	public void onSendOK(int packType);
	public  void onProgress(byte[] data);
	public  void onReceive(byte[] data);
	public  void onTimeout();
	public  void onError(int code, String msg);
	public  void onResult(int ntype, int code);
	public void onDevicePlugged();
	public void onDeviceUnplugged();
	public void onDeviceState(int nState);  // -1断开 0 :连接失败 1:连接成功
	public  void onReadCardData(Map hashcard);
	public  void onDeviceFound(final List<BluetoothDevice> list);
}
