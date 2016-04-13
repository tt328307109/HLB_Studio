package com.lk.qf.pay.jhl;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.lk.bhb.pay.R;
import com.lk.qf.pay.activity.BaseActivity;
import com.lk.qf.pay.activity.EquAddConfirmActivity;
import com.lk.qf.pay.beans.PosData;
import com.lk.qf.pay.beans.SwingpayParams;
import com.lk.qf.pay.golbal.Constant;
import com.lk.qf.pay.golbal.MApplication;
import com.lk.qf.pay.tool.Logger;
import com.lk.qf.pay.wedget.ShowDialog;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

public class CheckActivity extends BaseActivity implements BlueCommangerCallback{
	private ListView m_ListView;
	private ImageView imvAnimScan;
	private AnimationDrawable animScan;
	private BluetoothAdapter mAdapter;
	private List<BluetoothDevice> lstDevScanned;
	private BroadcastReceiver recvBTScan = null;
	private String blueTootchAddress = "";
	private Button checkBtn;
	private String posId;
	private String posType;
	private MyListViewAdapter m_Adapter;
	BluetoothCommmanager BluetoothComm = null;
	private Handler mMainMessageHandler;
	private ShowDialog dialog;
	
	private static final int BRUSHDATA = 0x12;
	private static final int GETCARDDATA = 0x20;
	private static final int GETTRACKDATA_CMD = 0x22; // 操作状态 00成功 E1用户取消 E2超时退出
														// E3 IC卡处理失败 E4:无IC卡参数
														// E5：交易终止 E6： 操作失败请重试
	private static final int GETENCARDDATA = 0x46;
	private static final int MAINKEY = 0x34;
	private static final int WORKEY = 0x38;
	private static final int GETMAC = 0x37;
	private static final int GETSNVERSION = 0x40;
	private static final int GETTERNO = 0x41;
	private static final int SETTERNO = 0x42;

	private static final int SETAIDPAMR = 0x33;
	private static final int CLEARAID = 0x3A;
	private static final int SETPUBKEYPARM = 0x32;
	private static final int ClEARPUBKEY = 0x39;
	private static final int ProofIcParm = 0x23;
	private static final int Battery = 0x45;
	private static final int IC_GETSTATUS = 0x13;
	private static final int IC_OPEN = 0x14;
	private static final int IC_CLOSE = 0x15;
	private static final int IC_WRITEAPDU = 0x16;
	private static final int Language = 0x01;
	private static final long WAIT_TIMEOUT = 15000; // 超时时间
	private boolean bOpenDevice = false;
	private String strPan = "";
	private static final String DEBUG_TAG = "BluetoothTest";
	private String trmmodno;
	private String action;
	
	private static final int connectmodem = 0x01; // 连接方式 00 自动连接:需要手动先配对 0x01
	// 选择设备连接,打开设备选择框,用户选择蓝牙连接
	
	private static final int REQUEST_CONNECT_DEVICE = 1;
	
	Handler hdStopScan = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			if (msg.what == 10024) {
				StopScanBTPos();
			}
		}
	};
	
	private Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case 1002:
				try {
					BluetoothComm.DisConnectBlueDevice();
					showLoadingDialog("正在配对连接MPOS设备...");
					BluetoothComm.ConnectDevice(blueTootchAddress);
					//checkBtn.setText(R.string.connecting_bt_pos);
				} catch (Exception e) {
					// TODO: handle exception
				}
					
				break;
			case 1003:
				break;
			case 1004:
				check();
				break;
			default:
				break;
			}
		}
	};
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.acticity_check);
		action = getIntent().getAction();
		mMainMessageHandler = new MessageHandler(Looper.myLooper());
		// 启动蓝牙服务,必须启动
			Intent intent = new Intent(this, BluetoothConnController.class);
			startService(intent);
			Intent i = new Intent(
			BluetoothConnController.GET_SERIVICE_STATUS_ACTION);
			sendBroadcast(i);
		BluetoothComm = BluetoothCommmanager.getInstance(this, this);
		checkBtn = (Button) findViewById(R.id.btnBT);
		checkBtn.setOnClickListener(new MyOnClickListener());
		m_ListView = (ListView) findViewById(R.id.lv_indicator_BTPOS);
		imvAnimScan = (ImageView) findViewById(R.id.img_anim_scanbt);
		animScan = (AnimationDrawable) getResources().getDrawable(
				R.anim.progressanmi);
		imvAnimScan.setBackgroundDrawable(animScan);
		m_ListView.setOnItemClickListener(new OnItemClickListener() {
			
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				onBTPosSelected(CheckActivity.this, view, position);
				animScan.stop();
				imvAnimScan.setVisibility(View.GONE);
			}

		});
	}
	
	private void check(){
		/**************************************************/
		/*((AppContext) this.getApplicationContext()).setBlueTootchAddress(blueTootchAddress);
		AppContext.mSharedPref.putSharePrefString("blueTootchAddress", blueTootchAddress);*/
		//MApplication.mSharedPref.putSharePrefString("blueTootchAddress", blueTootchAddress);
		//MApplication.mApplicationContext.setBlutooth(blueTootchAddress);
		//System.out.println("blueTootchAddress=======>"+MApplication.mApplicationContext.getBlutooth());
		/*if(action.equals("JHLA60蓝牙刷卡器")){
			MApplication.mSharedPref.putSharePrefString("blueTootchAddress", blueTootchAddress);
			startActivity(new Intent(this,PayByBuleCardActivity.class).setAction(action));
			finish();
		} else {*/
		MApplication.mSharedPref.putSharePrefString("blueTootchAddress", blueTootchAddress);
			Intent it = new Intent(this, EquAddConfirmActivity.class);
			it.putExtra("ksn", trmmodno);
			it.putExtra("blueTootchAddress", blueTootchAddress);
			startActivity(it);
			finish();
		//}
		
		/**************************************************/		
	}
	
	private void onBTPosSelected(Activity activity, View itemView, int index) {
		StopScanBTPos();
		Map<String, ?> dev = (Map<String, ?>) m_Adapter.getItem(index);
		blueTootchAddress = (String) dev.get("ADDRESS");
		Logger.d("onBTPosSelected-->" + blueTootchAddress);
		if(action.equals("JHLA60蓝牙刷卡器")){
			MApplication.mSharedPref.putSharePrefString("blueTootchAddress", blueTootchAddress);
			startActivity(new Intent(this,PayByBuleCardActivity.class).setAction(action));
			finish();
		}else {
			mHandler.sendEmptyMessage(1002);
		}
		
	}
	
	class MyOnClickListener implements OnClickListener {

		@Override
		public void onClick(View v) {
			doScanBTPos();
		}
	}
	
	private void doScanBTPos() {
		if (lstDevScanned == null) {
			lstDevScanned = new ArrayList<BluetoothDevice>();
		}
		lstDevScanned.clear();		
		try {
			Thread.sleep(500);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		//
		refreshAdapter();
		//
		if(mAdapter == null){
			mAdapter = BluetoothAdapter.getDefaultAdapter();
		}	
		if (!mAdapter.isEnabled()) {
			// 弹出对话框提示用户是后打开
			Intent enabler = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
			startActivity(enabler);
			return;
			// 不做提示，强行打开
			// mAdapter.enable();
		}
		//
		if (recvBTScan != null){
			unregisterReceiver(recvBTScan);
		}
		recvBTScan = new BroadcastReceiver() {

			@Override
			public void onReceive(Context context, Intent intent) {
				String action = intent.getAction();
				if (BluetoothDevice.ACTION_FOUND.equals(action)) {
					BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
					boolean isFound = false;
					for (BluetoothDevice dev : lstDevScanned) {
						if (dev.getAddress().equals(device.getAddress())) {
							isFound = true;
							break;
						}
					}
					//
					if (!isFound)
						lstDevScanned.add(device);
					refreshAdapter();
				} else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
					onStopScanBTPos();
				}
			}

		};
		//
		IntentFilter localIntentFilter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
		localIntentFilter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
		registerReceiver(recvBTScan, localIntentFilter);
		//
		imvAnimScan.setVisibility(View.VISIBLE);
		animScan.start();
		hdStopScan.sendEmptyMessageDelayed(10240, 1000 * 20);
		mAdapter.startDiscovery();
	}
	
	private void refreshAdapter() {
		if (m_Adapter != null) {
			m_Adapter.clearData();
			m_Adapter = null;
		}
		//
		List<Map<String, ?>> data = generateAdapterData();
		m_Adapter = new MyListViewAdapter(this, data);
		//
		m_ListView.setAdapter(m_Adapter);
	}
	
	protected List<Map<String, ?>> generateAdapterData() {
		List<Map<String, ?>> data = new ArrayList<Map<String, ?>>();
		for (BluetoothDevice dev : lstDevScanned) {
			Map<String, Object> itm = new HashMap<String, Object>();
			itm.put("ICON",dev.getBondState() == BluetoothDevice.BOND_BONDED ? Integer
							.valueOf(R.drawable.bluetooth_blue) : Integer
							.valueOf(R.drawable.bluetooth_blue_unbond));
			itm.put("TITLE", dev.getName() + "(" + dev.getAddress() + ")");
			itm.put("ADDRESS", dev.getAddress());
			data.add(itm);
		}
		return data;
	}
	
	private class MyListViewAdapter extends BaseAdapter {
		private List<Map<String, ?>> m_DataMap;
		private LayoutInflater m_Inflater;

		public void clearData() {
			m_DataMap.clear();
			m_DataMap = null;
		}

		public MyListViewAdapter(Context context, List<Map<String, ?>> map) {
			this.m_DataMap = map;
			this.m_Inflater = LayoutInflater.from(context);
		}

		@Override
		public int getCount() {
		
			return m_DataMap.size();
		}

		@Override
		public Object getItem(int position) {
			return m_DataMap.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(final int position, View convertView,
				ViewGroup parent) {
			if (convertView == null) {
				convertView = m_Inflater.inflate(R.layout.bt_qpos_item, null);
			}
			ImageView m_Icon = (ImageView) convertView
					.findViewById(R.id.item_iv_icon);
			TextView m_TitleName = (TextView) convertView
					.findViewById(R.id.item_tv_lable);
			//
			Map<String, ?> itemdata = (Map<String, ?>) m_DataMap.get(position);
			int idIcon = (Integer) itemdata.get("ICON");
			String sTitleName = (String) itemdata.get("TITLE");
			//
			m_Icon.setBackgroundResource(idIcon);
			m_TitleName.setText(sTitleName);
			//
			return convertView;
		}

	}
	
	private void StopScanBTPos() {
		if(mAdapter!=null){
			mAdapter.cancelDiscovery();
			mAdapter = null;
		}
		
	}
	
	private void onStopScanBTPos() {
		animScan.stop();
		imvAnimScan.setVisibility(View.GONE);
		//
		unregisterReceiver(recvBTScan);
		refreshAdapter();
		recvBTScan = null;
	}
	
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		if (mAdapter != null) {
			mAdapter.cancelDiscovery();
		}
		dismissLoadingDialog();
		Intent intent = new Intent(this, BluetoothConnController.class);
		stopService(intent);
//		System.exit(0);
//		finish();
	}
	
	class MessageHandler extends Handler {
		private long mLogCount = 0;

		public MessageHandler(Looper looper) {
			super(looper);
		}
	
}

	@Override
	public void onSendOK(int packType) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onProgress(byte[] data) {
		// TODO Auto-generated method stub

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

		case BRUSHDATA:
			if (data[1] == 0x00) {

				String strCard = "";
				int nCardLen = data[2];
				for (int i = 0; i < nCardLen; ++i) {
					strCard = strCard + String.format("%02x", data[i + 3]);
				}
				strCard = hexStr2Str(strCard);
				System.out.println("卡号:" + strCard.toString());
				if (Language == 0)
					System.out.println("Credit Sucess,wait...");
				else
					System.out.println("刷卡成功");
				// AudioCommmanager.TRANS_Sale(15000,nMonery,5,
				// "12345",nencrymodem);
				System.out.println("正在加密数据...");
			} else {
				if (Language == 0)
					System.out.println("Credit Failure,ErrorCode:"
							+ Integer.toString(data[1]));
				else
					System.out
							.println("刷卡失败,错误代码:" + Integer.toString(data[1]));
			}
			break;
		case GETCARDDATA:
		case GETTRACKDATA_CMD: {
			String strError = "";
			strError = String.format("%02x", data[1]);
			if (strError.equals("e1"))
				strError = strError + ":用户取消";
			else if (strError.equals("E2"))
				strError = strError + ":超时退出";
			else if (strError.equals("e3"))
				strError = strError + ":IC卡处理数据失败";
			else if (strError.equals("e4"))
				strError = strError + ":无IC卡参数";
			else if (strError.equals("e5"))
				strError = strError + ":交易终止";
			else if (strError.equals("e6"))
				strError = strError + ":操作失败,请重试";
			if (Language == 0)
				System.out.println("Credit Failure,ErrorCode:" + strError);
			else
				System.out.println("操作失败,错误代码:" + strError);
		}
			break;
		case GETENCARDDATA: {
			String strData = "";
			for (int i = 0; i < data.length; ++i) {
				strData = strData + String.format("%02x", data[i]);
			}
			System.out.println("CardData :" + strData.toString());
		}
			break;
		case GETMAC:
			if (data[1] == 0x00) {
				for (int i = 2; i < 10; ++i) {
					sb.append(String.format("%02x", data[i]));
				}
				System.out.println("MAC Sucess:" + sb.toString());
			} else {
				System.out.println("MAC failure:" + Integer.toString(data[1]));
			}
			break;
		case GETSNVERSION:
			if (data[1] == 0x00) {
				String strDeviceSn = "";
				for (int i = 3; i < 19; ++i) {
					strDeviceSn = strDeviceSn + String.format("%02x", data[i]);
				}
				strDeviceSn = toStringHex(strDeviceSn);
				System.out.println("SN:" + strDeviceSn.toString());
				trmmodno = strDeviceSn.toString();
				strDeviceSn = "";
				for (int i = 20; i < 36; ++i) {
					strDeviceSn = strDeviceSn + String.format("%02x", data[i]);
				}
				strDeviceSn = toStringHex(strDeviceSn);
				System.out.println("Version:" + strDeviceSn.toString());

			} else {
				System.out.println("SN Failure:" + Integer.toString(data[1]));
			}
			mHandler.sendEmptyMessage(1004);
			break;
		case GETTERNO:
			if (data[1] == 0x00) {
				String strTerSn = "";
				try {
					strTerSn = new String(data, "GB2312");
				} catch (UnsupportedEncodingException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				strTerSn = strTerSn.substring(1, strTerSn.length() - 1);
				System.out.println("TerNo:" + strTerSn.toString());

			} else {
				System.out
						.println("Terid Failure:" + Integer.toString(data[1]));
			}

			break;
		case Battery:
			if (data[1] == 0x00) {
				StringBuilder sbBattery = new StringBuilder();
				for (int i = 2; i < 3; ++i) {
					sbBattery.append(String.format("%02x", data[i]));
				}
				int batty = Integer.parseInt(sbBattery.toString(), 16);
				System.out.println("Battery Sucess:" + Integer.toString(batty));
			} else {
				System.out.println("Battery failure:"
						+ Integer.toString(data[1]));
			}
			break;
		case IC_GETSTATUS:
			if (data[1] == 0x00) {
				if (Language == 0)
					System.out.println("GetStatus  IC 	Sucess");
				else
					System.out.println("有卡插入");
			} else {
				if (Language == 0)
					System.out.println("GetStatus  IC 	FAILD");
				else
					System.out.println("没有卡插入,错误代码:"
							+ Integer.toString(data[1]));
			}
			break;
		case IC_CLOSE:
			if (data[1] == 0x00) {
				if (Language == 0)
					System.out.println("Close  IC 	Sucess");
				else
					System.out.println("关闭IC卡成功");
			} else {
				if (Language == 0)
					System.out.println("Close  IC 	FAILD");
				else
					System.out.println("关闭IC卡失败,错误代码:"
							+ Integer.toString(data[1]));

			}
			break;
		case IC_OPEN:
			if (data[1] == 0x00) {
				if (Language == 0) {
					System.out.println("Open  IC  Sucess");
				} else {
					System.out.println("IC卡打开成功");
				}
				String openATR = "";
				int len = data[2];
				for (int i = 2; i < len + 3; ++i) {
					openATR = openATR + String.format("%02x", data[i]);
				}
				System.out.println("IC Open ATR:" + openATR.toString());
			} else if (data[1] == 0x13) {
				if (Language == 0)
					System.out.println("Charge  IC  FAILD");
				else
					System.out.println("IC卡上电失败");
			} else if (data[1] == 0x04) {
				if (Language == 0)
					System.out.println("Find  IC  FAILD");
				else
					System.out.println("无卡");
			} else {
				if (Language == 0)
					System.out.println("IC Open failure:"
							+ Integer.toString(data[1]));
				else
					System.out.println("IC卡打开失败,错误代码:"
							+ Integer.toString(data[1]));
			}
			break;
		case IC_WRITEAPDU:
			if (data[1] == 0x00) {
				if (Language == 0) {
					System.out.println("Write  APDU 	Sucess");
				} else {
					System.out.println("IC卡Apdu命令成功");
				}
				String RESP = "";
				int len = data[2];
				for (int i = 2; i < len + 3; ++i) {
					RESP = RESP + String.format("%02x", data[i]);
				}
				System.out.println("RESP:" + RESP.toString());

			} else if (data[1] == 0x04) {
				if (Language == 0)
					System.out.println("Find  IC  FAILD");
				else
					System.out.println("无卡");
			} else if (data[1] == 0x05) {
				if (Language == 0)
					System.out.println("Data  Exchange  FAILD");
				else
					System.out.println("数据交换错误");
			} else {
				if (Language == 0)
					System.out.println("Write  Apdu  FAILD:"
							+ Integer.toString(data[1]));
				else
					System.out.println("Apdu命令失败,错误代码:"
							+ Integer.toString(data[1]));
			}
			break;
		case ProofIcParm:
			if (data[1] == 0x00) {
				if (Language == 0)
					System.out.println("IC  PROOF 	Sucess");
				else
					System.out.println("IC二次论证成功");
				for (int i = 2; i < data.length; ++i) {
					sb.append(String.format("%02x", data[i]));
				}
				System.out.println("二次论证收到数据:" + sb.toString());
			} else {
				if (Language == 0)
					System.out.println("IC  PROOF 	FAILD");
				else
					System.out.println("IC二次论证失败,错误代码:"
							+ Integer.toString(data[1]));
			}

			break;
		default:
			break;

		}

	}

	@Override
	public void onTimeout() {
		// TODO Auto-generated method stub

	}

	@Override
	public void onError(int code, String msg) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onResult(int ntype, int code) {
		// TODO Auto-generated method stub
		switch (ntype) {

		case MAINKEY:
			if (code == 0x00) {
				if (Language == 0)
					System.out.println("Set MainKey Sucess");
				else
					System.out.println("主密钥设置成功");
			} else {
				if (Language == 0)
					System.out.println("Set MainKey Failure,ErrorCode:"
							+ Integer.toString(code));
				else
					System.out
							.println("主密钥设置失败,错误代码:" + Integer.toString(code));
			}
			break;
		case GETMAC:
			if (Language == 0)
				System.out.println("MAC Failure,ErrorCode:"
						+ Integer.toString(code));
			else
				System.out.println("MAC  获取失败,错误代码:" + Integer.toString(code));
			break;
		case WORKEY:
			if (code == 0x00) {
				if (Language == 0)
					System.out.println("Set Workkey Sucess");
				else
					System.out.println("工作密钥设置成功");
			} else {
				if (Language == 0)
					System.out.println("Set Workkey Failure,ErrorCode:"
							+ Integer.toString(code));
				else
					System.out.println("工作密钥设置失败,错误代码:"
							+ Integer.toString(code));
			}
			break;

		case SETTERNO:
			if (code == 0x00) {
				if (Language == 0)
					System.out.println("Set  TerNo 	Sucess");
				else
					System.out.println("商户号终端号设置成功");

				if (Language == 0)
					System.out.println("Read TerNo...");
				else
					System.out.println("正在读取TerNo...");

				BluetoothComm.ReadTernumber();
			} else {
				if (Language == 0)
					System.out.println("Set TERNO Failure,ErrorCode:"
							+ Integer.toString(code));
				else
					System.out.println("商户号终端号设置失败,错误代码:"
							+ Integer.toString(code));
			}
			break;
		case SETAIDPAMR:
			if (code == 0x00) {
				if (Language == 0)
					System.out.println("Set  AID 	Sucess");
				else
					System.out.println("AID参数设置成功");
			} else if (Language == 0)
				System.out.println("Set AID Failure,ErrorCode:"
						+ Integer.toString(code));
			else
				System.out.println("AID参数设置失败,错误代码:" + Integer.toString(code));
			break;
		case CLEARAID:
			if (code == 0x00) {
				if (Language == 0)
					System.out.println("Clear  AID 	Sucess");
				else
					System.out.println("AID清空成功");
			} else {
				if (Language == 0)
					System.out.println("Clear  AID 	FAILD");
				else
					System.out
							.println("AID清空失败,错误代码:" + Integer.toString(code));

			}
			break;
		case SETPUBKEYPARM:
			if (code == 0x00) {
				if (Language == 0)
					System.out.println("SET  PUBKEY 	Sucess");
				else
					System.out.println("公钥设置成功");
			} else {
				if (Language == 0)
					System.out.println("SET  PUBKEY 	FAILD");
				else
					System.out.println("公钥设置失败,错误代码:" + Integer.toString(code));
			}
			break;

		case ClEARPUBKEY:
			if (code == 0x00) {
				if (Language == 0)
					System.out.println("Clear  PUBKEY 	Sucess");
				else
					System.out.println("公钥清空成功");
			} else {
				if (Language == 0)
					System.out.println("CLEAR  PUBKEY 	FAILD");
				else
					System.out.println("公钥清空失败,错误代码:" + Integer.toString(code));
			}
			break;

		default:
			if (Language == 0)
				System.out.println("Failure,ErrorCode:"
						+ Integer.toString(code));
			else
				System.out.println("获取失败,错误类型 :" + Integer.toString(ntype)
						+ ",错误代码:" + code);
			break;
		}
		// System.out.println("onResult");
	}

	@Override
	public void onDevicePlugged() {
		// TODO Auto-generated method stub

	}

	@Override
	public void onDeviceUnplugged() {
		// TODO Auto-generated method stub

	}

	@Override
	public void onDeviceState(int nState) {
		// TODO Auto-generated method stub
		Log.d("onDeviceState --------", Integer.toString(nState));
		bOpenDevice = false;
		if (nState == -1){
		checkBtn.setText("未找到MPOS...");
		dismissLoadingDialog();
		}
		else if (nState == 0) {
			checkBtn.setText("连接失败,请重新连接。");
			dismissLoadingDialog();
		} else if (nState == 2) {
			checkBtn.setText("MPOS已断开,请重新连接...");
			dismissLoadingDialog();
		} else if (nState == 1) {
			bOpenDevice = true;
			BluetoothComm.GetSnVersion();

		} else if (nState == 3) {
			System.out.println("蓝牙手动启动");

		} else if (nState == 4) {
			bOpenDevice = false;
			System.out.println("手动关闭蓝牙");
			BluetoothComm.DisConnectBlueDevice();
		}
	}

	@Override
	public void onReadCardData(Map hashcard) {
		// TODO Auto-generated method stub
		Message updateMessage = mMainMessageHandler.obtainMessage();
		updateMessage.obj = "";
		updateMessage.sendToTarget();

		System.out.println("hashcard");

		Set set = hashcard.entrySet();
		Iterator iterator = set.iterator();
		while (iterator.hasNext()) {
			Map.Entry<String, String> entry1 = (Map.Entry<String, String>) iterator
					.next();
			if (entry1.getKey().equals("PAN")) {
				strPan = entry1.getValue();
			}
			System.out.println(entry1.getKey() + "==" + entry1.getValue());
			Log.e(DEBUG_TAG, entry1.getKey() + "==" + entry1.getValue());

		}

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
			// System.out.println("正在查找MPOS连接...");
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
				if (!bHandle)
					break;
				try {
					sleep(10);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

		}
	}

	@Override
	public void onDeviceFound(List<BluetoothDevice> list) {
		if (list.size() ==0)
			checkBtn.setText("查找到无设备");
		// TODO Auto-generated method stub
		 for (BluetoothDevice device : list) { 
		     String address = device.getAddress();
		     BluetoothComm.ConnectDevice(address);
		}
	}
}