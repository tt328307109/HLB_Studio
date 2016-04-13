package com.lk.qf.pay.jhl;

import java.io.UnsupportedEncodingException;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import com.lk.bhb.pay.R;
import com.lk.qf.pay.activity.BaseActivity;
import com.lk.qf.pay.activity.EquAddConfirmActivity;
import com.lk.qf.pay.activity.swing.SignaturePadActivity;
import com.lk.qf.pay.beans.PosData;
import com.lk.qf.pay.beans.SwingpayParams;
import com.lk.qf.pay.golbal.Actions;
import com.lk.qf.pay.tool.Logger;
import com.lk.qf.pay.utils.AmountUtils;
import com.lk.qf.pay.wedget.BaseDialog;
import com.lk.qf.pay.wedget.CommonTitleBar;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

public class PayByBuleCardActivity extends BaseActivity implements
		BlueCommangerCallback {

	private TextView cashin_account_text;// 充值钱数
	private TextView cashin_show_msg_text;// 刷卡状态
	private CommonTitleBar title;
	// private TextView restBtn;// 重置
	private String period = "";// 有效期
	private String crdnum = "";// 银行卡序列号去
	private String amount = "";// 充值数量
	private String Encrytrack2len = "", Encrytrack3len = "", Encrytrack2 = "",
			Encrytrack3 = "", Track55 = "";
	private SwingpayParams params = new SwingpayParams();
	private String cardNo = "", randomNum = "",
			encTrackData = "", rateTypeID = "", icNumber = "", pinblock = "";
	private Handler mMainMessageHandler;
	private BluetoothCommmanager BluetoothComm = null;
	private static final int Language = 0x01;
	private static final long WAIT_TIMEOUT = 15000; // 超时时间
	private boolean bOpenDevice = false;
	private static final String DEBUG_TAG = "BluetoothTest";
	private static final int connectmodem = 0x01; // 连接方式 00 自动连接:需要手动先配对 0x01
	// 选择设备连接,打开设备选择框,用户选择蓝牙连接
	private static final int REQUEST_CONNECT_DEVICE = 1;
	// private String buletooth;
	private Button checkBtn;
	private String action, blueTootchAddress, payType = "";
	Intent intentSer;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.swing_card_s_bule);
		amount = PosData.getPosData().getPayAmt();
		payType = PosData.getPosData().getPayType();
//		Log.i("result", "--------------------oncreat-amount-----------"+amount);
		initData();
	}

	private void initData() {
		action = getIntent().getAction();

		mMainMessageHandler = new MessageHandler(Looper.myLooper());
		// 启动蓝牙服务,必须启动
		intentSer = new Intent(this, BluetoothConnController.class);
		startService(intentSer);
		Intent i = new Intent(
				BluetoothConnController.GET_SERIVICE_STATUS_ACTION);
		sendBroadcast(i);

		BluetoothComm = BluetoothCommmanager.getInstance(this, this);

		if (connectmodem == 0x00) {
			// new CheckDeviceThread().start();
			Log.i("result", "----------connectmodem---start-");
		} else {
			// 8C:DE:52:C6:DE:FE
			// BluetoothComm.ConnectDevice("8C:DE:52:C6:DE:FE");
			Log.i("result", "----------ConnectDevice---start-");
			new ChecksetHandle().start();
			// 1:直接通过页面选择连接蓝牙 2:通过搜索函数搜索
			// ,BluetoothComm.ScanDevice(DEVICE_ADDRESS_FILETER,10); //搜索超时时间 秒
			// 在onDeviceFound 回调搜索结果
			// Intent serverIntent = new Intent(this, DeviceListActivity.class);
			// startActivityForResult(serverIntent,
			// REQUEST_CONNECT_DEVICE);//////////////////////////////////////////
		}

		title = (CommonTitleBar) findViewById(R.id.titlebar_bule_ldcard);
		title.setCanClickDestory(this, true);
		cashin_account_text = (TextView) findViewById(R.id.cashin_account_text_bule);
		cashin_account_text.setText(AmountUtils.changeFen2Yuan(amount) + "元");
//		Log.i("result", "--------------------amount-amount-----------"+amount);
//		Log.i("result", "--------------------AmountUtils.changeFen2Yuan(amount)-----------"+AmountUtils.changeFen2Yuan(amount));
		cashin_show_msg_text = (TextView) findViewById(R.id.cashin_show_msg_text_bule1);
		// restBtn = titlebar_swing_ldcard.showTvMore();
		// checkBtn = (Button) findViewById(R.id.btnBT);
		// checkBtn.setOnClickListener(new MyOnClickListener());
		if (action.equals(Actions.ACTION_CASHIN)) {
			if (payType!=null) {
				if (payType.equals("03")) {
					title.setActName("即刷即到");
				} else {
					title.setActName("刷卡支付");
				}
			}else{
				title.setActName("刷卡支付");
			}
		} else {
			title.setActName("设备绑定");
		}
//		Log.i("result", "--------------------jhl--a------------" + action);
		cashin_show_msg_text.setText("正在配对连接MPOS设备...");
		Intent intent1 = getIntent();
		if (intent1!= null) {

			blueTootchAddress = intent1.getStringExtra("deviceAdd");
//			Log.i("result", "---------add----------" + blueTootchAddress);
		}
		
		
		if (BluetoothComm == null) {
			BluetoothComm = BluetoothCommmanager
					.getInstance(this, this);
			new ChecksetHandle().start();
		}
		BluetoothComm.DisConnectBlueDevice();
		BluetoothComm.ConnectDevice(blueTootchAddress);
//		Log.i("result",
//				"---------blueTootchAddress----------"
//						+ BluetoothComm.ConnectDevice(blueTootchAddress));
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

		}
	}

	/*
	 * public void onActivityResult(int requestCode, int resultCode, Intent
	 * data) { // Log.d(DEBUG_TAG, "onActivityResult: requestCode=" +
	 * requestCode // + ", resultCode=" + resultCode); switch (requestCode) {
	 * case REQUEST_CONNECT_DEVICE: // When DeviceListActivity returns with a
	 * device to connect if (resultCode == Activity.RESULT_OK) { // Get the
	 * device MAC address // BluetoothComm.DisConnectBlueDevice();
	 * cashin_show_msg_text.setText("正在配对连接MPOS设备..."); blueTootchAddress =
	 * data.getExtras().getString( DeviceListActivity.EXTRA_DEVICE_ADDRESS);
	 * 
	 * BluetoothComm.ConnectDevice(blueTootchAddress); } break; } }
	 */

	private void startSwing() {
		cashin_show_msg_text.setText("请刷卡/插卡...");
		BluetoothComm.MagnNoAmountNoPasswordCard(WAIT_TIMEOUT, 100);
//		Log.i("result", "-------startSwing----------------");

	}

	class MessageHandler extends Handler {
		private long mLogCount = 0;

		public MessageHandler(Looper looper) {
			super(looper);
		}

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
		case BluetoothCommmanager.ICBRUSH:
		case BluetoothCommmanager.BRUSHDATA: {
			Log.i("result", "-------BRUSHDATA-------");
			if (data[1] == 0x00) {

				String strCard = "";
				int nCardLen = data[2];
				for (int i = 0; i < nCardLen; ++i) {
					strCard = strCard + String.format("%02x", data[i + 3]);
				}
				strCard = hexStr2Str(strCard);
				System.out.println("卡号:" + strCard.toString());
				/*
				 * int nCardtye = data[3 + nCardLen]; if(nCardtye == 0x01){
				 * showDialog("IC卡不能降级交易！"); } else {
				 * BluetoothComm.InputPassword("123456", 6); }
				 */
				BluetoothComm.InputPassword("123456", 6);
			} else {
				if (Language == 0)
					System.out.println("Credit Failure,ErrorCode:"
							+ Integer.toString(data[1]));
				else
					System.out
							.println("刷卡失败,错误代码:" + Integer.toString(data[1]));
			}
		}
			break;
		case BluetoothCommmanager.CHECK_IC: {
			Log.i("result", "-------CHECK_IC-------");
			String strError = "";
			strError = String.format("%02x", data[1]);
			if (strError.equals("00"))
				strError = "在待机界面插入IC卡";

			else
				strError = "交易功能插入IC卡";

			cashin_show_msg_text.setText(strError);
		}
			break;
		case BluetoothCommmanager.GETCARDDATA:
		case BluetoothCommmanager.GETTRACKDATA_CMD: {
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
				cashin_show_msg_text.setText("操作失败,错误代码:" + strError);
		}
			break;
		case (byte) BluetoothCommmanager.GETENCARDDATA: {
			Log.i("result", "-------GETENCARDDATA-------");
			String strData = "";
			for (int i = 0; i < data.length; ++i) {
				strData = strData + String.format("%02x", data[i]);
			}
			System.out.println("CardData :" + strData.toString());
		}
			break;
		case BluetoothCommmanager.GETMAC:
			Log.i("result", "-------GETMAC-------");
			sb.setLength(0);
			if (data[1] == 0x00) {
				for (int i = 2; i < 10; ++i) {
					sb.append(String.format("%02x", data[i]));
				}
				System.out.println("MAC Sucess:" + sb.toString());
			} else {
				System.out.println("MAC failure:" + Integer.toString(data[1]));
			}
			break;
		// case BluetoothCommmanager.GETSNVERSION:
		// Log.i("result", "-------GETSNVERSION-------");
		// if (data[1] == 0x00) {
		// String strDeviceSn = "";
		// for (int i = 3; i < data[2] + 3; ++i) {
		// strDeviceSn = strDeviceSn + String.format("%02x", data[i]);
		// }
		// strDeviceSn = toStringHex(strDeviceSn);
		// System.out.println("SN:" + strDeviceSn.toString());
		// params.setKsn(strDeviceSn.toString());
		// /*
		// * strDeviceSn = ""; for (int i = 20; i < 36; ++i) { strDeviceSn
		// * = strDeviceSn + String.format("%02x", data[i]); } strDeviceSn
		// * = toStringHex(strDeviceSn); System.out.println("Version:" +
		// * strDeviceSn.toString());
		// */
		// if (action.equals(Actions.ACTION_CASHIN)) {
		// startSwing();
		// } else {
		// Intent it = new Intent(this, EquAddConfirmActivity.class);
		// it.putExtra("ksn", params.getKsn());
		// it.putExtra("blueTootchAddress", blueTootchAddress);
		// startActivity(it);
		// // try {
		// // Log.i("result", "-------dddddduan-------");
		// // BluetoothComm.DisConnectBlueDevice();
		// // } catch (Exception e) {
		// // // TODO: handle exception
		// // }
		// finish();
		// }
		//
		// } else {
		// System.out.println("SN Failure:" + Integer.toString(data[1]));
		// }
		//
		// // AudioCommmanager.MagnCard(12000,nMonery,0x01);
		// break;

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
				System.out.println("SN:" + strDeviceSn.toString());
				params.setKsn(strDeviceSn.toString());
				if (action.equals(Actions.ACTION_CASHIN)) {
					startSwing();
				} else {
					Intent it = new Intent(this, EquAddConfirmActivity.class);
					it.putExtra("ksn", params.getKsn());
					it.putExtra("blueTootchAddress", blueTootchAddress);
					startActivity(it);
					try {
						Log.i("result", "-------dddddduan-------");
						BluetoothComm.DisConnectBlueDevice();
					} catch (Exception e) {
						// TODO: handle exception
					}
					finish();
				}

				//
				int SNLen = strDeviceSn.length();
				strDeviceSn = "";
				for (int i = data[2] + 4; i < data[2] + 4 + 16; ++i) {
					strDeviceSn = strDeviceSn + String.format("%02x", data[i]);
				}
				strDeviceSn = toStringHex(strDeviceSn);
				System.out.println("应用版本号:" + strDeviceSn.toString());

				// 增加字段 boot版本号

				switch (SNLen) {
				case 12:
					strDeviceSn = "";
					int k = data[32];

					if (k == 16) {
						for (int i = data[2] + 4 + 17; i < data[2] + 4 + 16 + 17; ++i) {
							strDeviceSn = strDeviceSn
									+ String.format("%02x", data[i]);
						}
						strDeviceSn = toStringHex(strDeviceSn);
						System.out.println("BOOT版本:" + strDeviceSn.toString());
					} else {
						return;
					}

					break;
				case 16:

					strDeviceSn = "";
					int j = data[36];

					if (j == 16) {
						for (int i = data[2] + 4 + 17; i < data[2] + 4 + 16 + 17; ++i) {
							strDeviceSn = strDeviceSn
									+ String.format("%02x", data[i]);
						}
						strDeviceSn = toStringHex(strDeviceSn);
						System.out.println("BOOT版本:" + strDeviceSn.toString());
					} else {
						return;
					}
				}
			} else {
				System.out.println("SN Failure:" + Integer.toString(data[1]));
			}

			break;

		case BluetoothCommmanager.GETTERNO:
			Log.i("result", "-------GETTERNO-------");
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
		case BluetoothCommmanager.Battery:
			Log.i("result", "-------Battery-------");
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
		case BluetoothCommmanager.IC_GETSTATUS:
			Log.i("result", "-------IC_GETSTATUS-------");
			if (data[1] == 0x00) {
				if (Language == 0)
					System.out.println("GetStatus  IC 	Sucess");
				else
					cashin_show_msg_text.setText("有卡插入");
			} else {
				if (Language == 0)
					System.out.println("GetStatus  IC 	FAILD");
				else
					cashin_show_msg_text.setText("没有卡插入,错误代码:"
							+ Integer.toString(data[1]));
			}
			break;
		case BluetoothCommmanager.IC_CLOSE:
			Log.i("result", "-------IC_CLOSE-------");
			if (data[1] == 0x00) {
				if (Language == 0)
					cashin_show_msg_text.setText("Close  IC 	Sucess");
				else
					cashin_show_msg_text.setText("关闭IC卡成功");
			} else {
				if (Language == 0)
					cashin_show_msg_text.setText("Close  IC 	FAILD");
				else
					cashin_show_msg_text.setText("关闭IC卡失败,错误代码:"
							+ Integer.toString(data[1]));
			}
			break;
		case BluetoothCommmanager.IC_OPEN:
			if (data[1] == 0x00) {
				if (Language == 0) {
					System.out.println("Open  IC  Sucess");
				} else {
					cashin_show_msg_text.setText("IC卡打开成功");
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
					cashin_show_msg_text.setText("IC卡上电失败");
			} else if (data[1] == 0x04) {
				if (Language == 0)
					System.out.println("Find  IC  FAILD");
				else
					cashin_show_msg_text.setText("无卡");
			} else {
				if (Language == 0)
					System.out.println("IC Open failure:"
							+ Integer.toString(data[1]));
				else
					cashin_show_msg_text.setText("IC卡打开失败,错误代码:"
							+ Integer.toString(data[1]));
			}
			break;
		case BluetoothCommmanager.IC_WRITEAPDU:
			if (data[1] == 0x00) {
				if (Language == 0) {
					System.out.println("Write  APDU 	Sucess");
				} else {
					cashin_show_msg_text.setText("IC卡Apdu命令成功");
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
					cashin_show_msg_text.setText("无卡");
			} else if (data[1] == 0x05) {
				if (Language == 0)
					System.out.println("Data  Exchange  FAILD");
				else
					cashin_show_msg_text.setText("数据交换错误");
			} else {
				if (Language == 0)
					System.out.println("Write  Apdu  FAILD:"
							+ Integer.toString(data[1]));
				else
					cashin_show_msg_text.setText("Apdu命令失败,错误代码:"
							+ Integer.toString(data[1]));
			}
			break;
		case BluetoothCommmanager.ProofIcParm:
			if (data[1] == 0x00) {
				if (Language == 0)
					System.out.println("IC  PROOF 	Sucess");
				else
					cashin_show_msg_text.setText("IC二次论证成功");
				for (int i = 2; i < data.length; ++i) {
					sb.append(String.format("%02x", data[i]));
				}
				cashin_show_msg_text.setText("二次论证收到数据:" + sb.toString());
			} else {
				if (Language == 0)
					System.out.println("IC  PROOF 	FAILD");
				else
					cashin_show_msg_text.setText("IC二次论证失败,错误代码:"
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

		case BluetoothCommmanager.MAINKEY:
			if (code == 0x00) {
				if (Language == 0)
					System.out.println("Set MainKey Sucess");
				else
					cashin_show_msg_text.setText("主密钥设置成功");
			} else {
				if (Language == 0)
					System.out.println("Set MainKey Failure,ErrorCode:"
							+ Integer.toString(code));
				else
					cashin_show_msg_text.setText("主密钥设置失败,错误代码:"
							+ Integer.toString(code));
			}
			break;
		case BluetoothCommmanager.GETMAC:
			if (Language == 0)
				System.out.println("MAC Failure,ErrorCode:"
						+ Integer.toString(code));
			else
				cashin_show_msg_text.setText("MAC  获取失败,错误代码:"
						+ Integer.toString(code));
			break;
		case BluetoothCommmanager.WORKEY:
			if (code == 0x00) {
				if (Language == 0)
					System.out.println("Set Workkey Sucess");
				else
					cashin_show_msg_text.setText("工作密钥设置成功");
			} else {
				if (Language == 0)
					System.out.println("Set Workkey Failure,ErrorCode:"
							+ Integer.toString(code));
				else
					cashin_show_msg_text.setText("工作密钥设置失败,错误代码:"
							+ Integer.toString(code));
			}
			break;

		case BluetoothCommmanager.SETTERNO:
			if (code == 0x00) {
				if (Language == 0)
					System.out.println("Set  TerNo 	Sucess");
				else
					cashin_show_msg_text.setText("商户号终端号设置成功");

				if (Language == 0)
					System.out.println("Read TerNo...");
				else
					cashin_show_msg_text.setText("正在读取TerNo...");

				BluetoothComm.ReadTernumber();
			} else {
				if (Language == 0)
					System.out.println("Set TERNO Failure,ErrorCode:"
							+ Integer.toString(code));
				else
					cashin_show_msg_text.setText("商户号终端号设置失败,错误代码:"
							+ Integer.toString(code));
			}
			break;
		case BluetoothCommmanager.SETAIDPAMR:
			if (code == 0x00) {
				if (Language == 0)
					System.out.println("Set  AID 	Sucess");
				else
					cashin_show_msg_text.setText("AID参数设置成功");
			} else if (Language == 0)
				System.out.println("Set AID Failure,ErrorCode:"
						+ Integer.toString(code));
			else
				cashin_show_msg_text.setText("AID参数设置失败,错误代码:"
						+ Integer.toString(code));
			break;
		case BluetoothCommmanager.CLEARAID:
			if (code == 0x00) {
				if (Language == 0)
					System.out.println("Clear  AID 	Sucess");
				else
					cashin_show_msg_text.setText("AID清空成功");
			} else {
				if (Language == 0)
					System.out.println("Clear  AID 	FAILD");
				else
					cashin_show_msg_text.setText("AID清空失败,错误代码:"
							+ Integer.toString(code));

			}
			break;
		case BluetoothCommmanager.SETPUBKEYPARM:
			if (code == 0x00) {
				if (Language == 0)
					System.out.println("SET  PUBKEY 	Sucess");
				else
					cashin_show_msg_text.setText("公钥设置成功");
			} else {
				if (Language == 0)
					System.out.println("SET  PUBKEY 	FAILD");
				else
					cashin_show_msg_text.setText("公钥设置失败,错误代码:"
							+ Integer.toString(code));
			}
			break;

		case BluetoothCommmanager.ClEARPUBKEY:
			if (code == 0x00) {
				if (Language == 0)
					System.out.println("Clear  PUBKEY 	Sucess");
				else
					cashin_show_msg_text.setText("公钥清空成功");
			} else {
				if (Language == 0)
					System.out.println("CLEAR  PUBKEY 	FAILD");
				else
					cashin_show_msg_text.setText("公钥清空失败,错误代码:"
							+ Integer.toString(code));
			}
			break;

		default:
			if (Language == 0)
				System.out.println("Failure,ErrorCode:"
						+ Integer.toString(code));
			else
				cashin_show_msg_text.setText("获取失败,错误类型 :"
						+ Integer.toString(ntype) + ",错误代码:" + code);
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
		if (nState == -1) {
			cashin_show_msg_text.setText("未找到MPOS...");
			dismissLoadingDialog();
		} else if (nState == 0) {
			cashin_show_msg_text.setText("连接MPOS失败...");
			dismissLoadingDialog();
		} else if (nState == 2) {
			cashin_show_msg_text.setText("MPOS未连接,连接中...");
			dismissLoadingDialog();
		} else if (nState == 1) {
			System.out.println("连接MPOS成功,正在获取SN号...");
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
		Log.i("result", "----updateMessage1------" + hashcard);
		Message updateMessage = mMainMessageHandler.obtainMessage();

		updateMessage.obj = "";
		// updateMessage.what = R.id.btnAPass;
		updateMessage.sendToTarget();
		Log.i("result", "----updateMessage2------" + hashcard);
		System.out.println("hashcard");

		Set set = hashcard.entrySet();
		Iterator iterator = set.iterator();
		while (iterator.hasNext()) {
			Map.Entry<String, String> entry1 = (Map.Entry<String, String>) iterator
					.next();
			if (entry1.getKey().equals("PAN")) {
				cardNo = entry1.getValue();
				Log.i("result", "----cardNo------" + cardNo);

			}

			if (entry1.getKey().equals("Track3")) {// 未加密磁道
				Encrytrack3 = entry1.getValue();

			}
			if (entry1.getKey().equals("Track2")) {
				Encrytrack2 = entry1.getValue();

			}
			// if (entry1.getKey().equals("Encrytrack3")) { //加密磁道
			// Encrytrack3 = entry1.getValue();
			//
			// }
			// if (entry1.getKey().equals("Encrytrack2")) {
			// Encrytrack2 = entry1.getValue();
			// }
			if (entry1.getKey().equals("Track55")) {
				Track55 = entry1.getValue();
			}
			if (entry1.getKey().equals("Encrytrack2len")) {
				Encrytrack2len = entry1.getValue();
			}
			if (entry1.getKey().equals("Encrytrack3len")) {
				Encrytrack3len = entry1.getValue();
			}
			if (entry1.getKey().equals("Random")) {
				randomNum = entry1.getValue();
			}
			if (entry1.getKey().equals("PanSeqNo")) {
				if (entry1.getValue().equals("ff")) {
					icNumber = "00";
				} else {

					icNumber = entry1.getValue();
				}
			}
			if (entry1.getKey().equals("AsciiPwd")) {
				pinblock = entry1.getValue();
			}
			if (entry1.getKey().equals("ExpireDate")) {
				period = entry1.getValue();
			}
			String trackdata = Encrytrack2 + "|" + Encrytrack3;
			params.setEncTrackData(trackdata);
			params.setDCDATA(Track55);
			if ("".equals(Track55)) {
				PosData.getPosData().setMediaType("01");

			} else {
				PosData.getPosData().setMediaType("02");// ic

			}
		}
		 if (cardNo.equals("") && Encrytrack2 !=
		 null&&!Encrytrack2.equals("")) {
		 cardNo = Encrytrack2.substring(0, Encrytrack2.indexOf("d"));
		 }

		PosData.getPosData().setCardNo(cardNo);

		// Log.i("result",
		// "----------Encrytrack2-d------------"+Encrytrack2.substring(0,
		// Encrytrack2.indexOf("d")));

		PosData.getPosData().setTermType("01");// 蓝牙
		PosData.getPosData().setRate("1");
		PosData.getPosData().setTermNo(params.getKsn());//sn号
		PosData.getPosData().setPayAmt(amount);
		PosData.getPosData().setTrack(params.getEncTrackData());
		PosData.getPosData().setRandom("");//随机数
		PosData.getPosData().setPeriod(period);//有效期
		PosData.getPosData().setCrdnum(icNumber);//序列号
		PosData.getPosData().setIcdata(Track55);//55
		PosData.getPosData().setPinblok(pinblock);
		PosData.getPosData().setType(action);
		/*************************************************************/
		Logger.d("PinKey-->" + PosData.getPosData().getPinKey());
		Logger.d("Pinblok-->" + PosData.getPosData().getPinblok());
		Logger.d("TermNo-->" + PosData.getPosData().getTermNo());
		Logger.d("EncTrackData-->" + PosData.getPosData().getTrack());
		/*************************************************************/
		gotoPay();

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

	private void gotoPay() {
		/*
		 * if(PosData.getPosData().getCrdnum().equals("1") &
		 * PosData.getPosData().getIcdata() == null){ showDialog("IC卡不能降级交易！");
		 * return; }
		 */
//		Log.i("result", "------------mmmm--------");
		Intent intent = new Intent(PayByBuleCardActivity.this,
				SignaturePadActivity.class);
		// intent.setAction("PAY_BY_JHL");
		startActivity(intent);
		try {
			BluetoothComm.DisConnectBlueDevice();
		} catch (Exception e) {
			// TODO: handle exception
		}

		if (Language == 0)
			System.out.println("Disconnect Wait...");
		else
			System.out.println("正在断开连接...");
		finish();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		// BluetoothComm.DisConnectBlueDevice();
//		Intent intent = new Intent(this, BluetoothConnController.class);
//		stopService(intent);
		if (BluetoothComm != null) {
			BluetoothComm.DisScanDevice();
			BluetoothComm.DisConnectBlueDevice();
		}
		Log.i("result", "--------------------onDestroy------------");
	}

	@Override
	public void onDeviceFound(List<BluetoothDevice> list) {
		if (list.size() == 0)
			checkBtn.setText("查找到无设备");
		// TODO Auto-generated method stub
		for (BluetoothDevice device : list) {
			String address = device.getAddress();
			BluetoothComm.ConnectDevice(address);
		}
	}

	public void showDialog(String msg) {
		BaseDialog mBackDialog = BaseDialog.getDialog(this, "提示", msg, "确定",
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.cancel();
						finish();
					}
				});
		mBackDialog.setCancelable(false);
		mBackDialog.show();
	}
}
