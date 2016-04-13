package com.lk.qf.pay.v50;

import java.util.ArrayList;
import java.util.List;

import com.lk.bhb.pay.R;

import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.os.Handler;

public class AdapterManager {
	private Context mContext;
	private DeviceListAdapter mDeviceListAdapter;   
	private List<BluetoothDevice> mDeviceList;   
	private Handler mainHandler;   
	List<BluetoothDevice> mList = new ArrayList<BluetoothDevice>();
	
	public AdapterManager(Context context){
		this.mContext = context;
	}
	
	public DeviceListAdapter getDeviceListAdapter(){
		if(null == mDeviceListAdapter){
			mDeviceList = new ArrayList<BluetoothDevice>();
			mDeviceListAdapter = new DeviceListAdapter(mContext, mDeviceList, R.layout.v50_device_list_item);
		}
		
		return mDeviceListAdapter;
	}
	
	
	
	public void updateDeviceAdapter() {
		if(null == mainHandler){
			mainHandler = new Handler(mContext.getMainLooper());
		}
		mainHandler.post(new Runnable() {
			
			@Override
			public void run() {
//				List<BluetoothDevice> mList = new ArrayList<BluetoothDevice>();   
//				List<String> macList = new ArrayList<String>();
//				for (BluetoothDevice bluetoothDevice : mDeviceList) {
//					if (!macList.contains(bluetoothDevice.getAddress())) {
//						macList.add(bluetoothDevice.getAddress());
//						mList.add(bluetoothDevice);
//					}
//				}
				mDeviceList.clear();
				mDeviceList.addAll(mList);
//				Collections.reverse(mDeviceList);
				mDeviceListAdapter.notifyDataSetChanged();
			}
		});
	}
	
	public void clearDevice(){
		if(null != mDeviceList){
			mDeviceList.clear();
			mList.clear();
		}
	}
	
	public void addDevice(BluetoothDevice bluetoothDevice){
		//LogUtil.d("blue", "search:"+bluetoothDevice.getName()+"-"+bluetoothDevice.getAddress()+" len:"+bluetoothDevice.getAddress().length());
		for (BluetoothDevice bt : mDeviceList) {
			if (bt.getAddress().equals(bluetoothDevice.getAddress())) {
				return;
			}
		}
		mList.add(bluetoothDevice);
		//LogUtil.d("blue", "add:"+bluetoothDevice.getName()+"-"+bluetoothDevice.getAddress()+" len:"+bluetoothDevice.getAddress().length());
		updateDeviceAdapter();
	}
	
	public void changeDevice(int listId, BluetoothDevice bluetoothDevice){
		mDeviceList.remove(listId);
		mDeviceList.add(listId, bluetoothDevice);
	}
	
	
	public List<BluetoothDevice> getDeviceList() {
		return mDeviceList;
	}

}
