package com.lk.qf.pay.zxb;

import java.text.SimpleDateFormat;
import java.util.Date;

import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.icardpay.zxbbluetooth.api.Scheme;
import com.icardpay.zxbbluetooth.api.ZxbListener;
import com.icardpay.zxbbluetooth.api.ZxbManager;
import com.lk.bhb.pay.R;
import com.lk.qf.pay.activity.BaseActivity;
import com.lk.qf.pay.activity.EquAddConfirmActivity;
import com.lk.qf.pay.activity.swing.CardBalanceConfirmActivity;
import com.lk.qf.pay.activity.swing.SignaturePadActivity;
import com.lk.qf.pay.beans.PosData;
import com.lk.qf.pay.golbal.Actions;
import com.lk.qf.pay.utils.AmountUtils;
import com.lk.qf.pay.wedget.CommonTitleBar;

public class ZXBBTDeviceActivity extends BaseActivity {

	private Context mContext;
	private String amount = "";// 充值金额
	private TextView showAccountText;// 充值钱数
	private TextView showMsgText;// 刷卡状态
	private CommonTitleBar commonTitleBar;
	private LinearLayout amountLayout;
	private String action;
	private BluetoothDevice mDevice;
	private ZxbManager mZxbManager;

	private Handler mHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case 101:
				mZxbManager.getDeviceInfo();
				break;
			case 102:
				// 开启读卡器：方案0
				mZxbManager.openCardReader(0, 2);
				showMsgText.setText("请刷卡或插卡");
				break;
			case 103:
				// 读取磁道数据
				mZxbManager.readTrackData();
				break;
			case 104:
				// 获取PBOC交易数据
				mZxbManager.getPbocTradeData("1", getTradeDate(), 60);
				break;
			default:
				break;
			}
		};
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.swing_hx_card);
		initViews();
		init();
	}

	private void initViews() {
		mContext = this;
		action = getIntent().getAction();
		showMsgText = (TextView) findViewById(R.id.cashin_show_msg_text);
		commonTitleBar = (CommonTitleBar) findViewById(R.id.titlebar_swing_ldcard);
		commonTitleBar.setCanClickDestory(this, true);
		amountLayout = (LinearLayout) findViewById(R.id.cashin_amount_layout);
		mDevice = getIntent().getParcelableExtra("device");

		if (action.equals(Actions.ACTION_CASHIN)) {
			commonTitleBar.setActName("刷卡支付");
			amountLayout.setVisibility(View.VISIBLE);
			amount = PosData.getPosData().getPayAmt();
			showAccountText = (TextView) findViewById(R.id.cashin_account_text);
			showAccountText.setText(AmountUtils.changeFen2Yuan(amount) + "元");
		} else if (action.equals(Actions.ACTION_CHECK)) {
			commonTitleBar.setActName("设备绑定");
		} else if (action.equals(Actions.ACTION_QUERY_BALANCE)) {
			commonTitleBar.setActName("余额查询");
		}
	}

	
	private void init() {
		// 初始化掌芯宝-蓝牙SDK
		mZxbManager = ZxbManager.getInstance(this);
		mZxbManager.setZxbListener(mListener);
		// 设置日志打印
		mZxbManager.setDebug(true);
		mZxbManager.connect(mDevice);

	}

	@Override
	protected void onStop() {
		// 断开连接
		mZxbManager.disconnect();
		super.onStop();
	}

	private ZxbListener mListener = new ZxbListener() {

		@Override
		public void onStateChanged(int state) {
		}

		@Override
		public void onDeviceFound(BluetoothDevice device, short rssi) {
		}

		@Override
		public void onDiscoveryStarted() {
		}

		@Override
		public void onDiscoveryFinished() {
		}

		@Override
		public void onBondStateChanged(BluetoothDevice device, int bondState) {
		}

		@Override
		public void onConnectStateChanged(BluetoothDevice device, int state) {
			switch (state) {
			case ZxbManager.STATE_NONE:
				showMsgText.setText("连接状态:未连接");
				break;
			case ZxbManager.STATE_CONNECTING:
				showMsgText.setText("连接状态:正在连接...");
				break;
			case ZxbManager.STATE_CONNECTED:
				showMsgText.setText("连接状态:已连接");
				// 获取设备信息
				// mZxbManager.getDeviceInfo();
				mHandler.sendEmptyMessage(101);
				/*
				 * if(action.equals(Actions.ACTION_CHECK)){ //获取设备信息
				 * mZxbManager.getDeviceInfo(); }else { //开启读卡器：方案0
				 * mZxbManager.openCardReader(0, Scheme.SCHEME_0);
				 * showMsgText.setText("请刷卡或插卡"); }
				 */
				break;
			}
		}

		@Override
		public void onError(int errorCode) {
			Toast.makeText(mContext, "刷卡器错误码：" + errorCode, Toast.LENGTH_SHORT)
					.show();

		}

		@Override
		public void onGetDeviceInfo(String deviceVersion,
				String firmwareVersion, String deviceSn, int adc) {
			/*
			 * setLogMsg("设备版本号 = " + deviceVersion); setLogMsg("固件版本号 = " +
			 * firmwareVersion); setLogMsg("设备序列号 = " + deviceSn);
			 * setLogMsg("电量 = " + adc);
			 */
			if (action.equals(Actions.ACTION_CHECK)) {
				Intent it = new Intent(mContext, EquAddConfirmActivity.class);
				it.putExtra("ksn", deviceSn);
				startActivity(it);
				finish();
			} else {
				PosData.getPosData().setTermNo(deviceSn);
				// 开启读卡器：方案0
				// mZxbManager.openCardReader(0, Scheme.SCHEME_0);
				mHandler.sendEmptyMessage(102);
				// showMsgText.setText("请刷卡或插卡");
			}

		}

		@Override
		public void onUpgradeProgress(int progress) {
			/*
			 * if (!mProgressDialog.isShowing()) mProgressDialog.show();
			 * mProgressDialog.setProgress(progress);
			 */
		}

		@Override
		public void onUpgradeFirmware(boolean success, String respCode) {
			/*
			 * mProgressDialog.dismiss(); if (success) {
			 * Toast.makeText(DeviceActivity.this, "固件升级成功！请重新开关设备！",
			 * Toast.LENGTH_LONG).show(); } else {
			 * Toast.makeText(DeviceActivity.this, "固件升级失败！响应码 = " + respCode,
			 * Toast.LENGTH_LONG).show(); }
			 */
		}

		@Override
		public void onUpdateKey() {
			showMsgText.setText("更新工作密钥成功！");
		}

		@Override
		public void onVerifyMac(boolean result) {
		}

		@Override
		public void onOpenCardReader(String statusCode) {
			if (statusCode.equals("00")) {
				showMsgText.setText("刷卡成功");
				mHandler.sendEmptyMessage(103);
			} else if (statusCode.equals("01"))
				showMsgText.setText("刷卡成功，但有IC");
			else if (statusCode.equals("02"))
				showMsgText.setText("刷卡失败");
			else if (statusCode.equals("03"))
				showMsgText.setText("刷卡超时");
			else if (statusCode.equals("04"))
				showMsgText.setText("IC卡已插入，但读卡失败");
			else if (statusCode.equals("05")) {
				showMsgText.setText("IC卡已插入");
				mHandler.sendEmptyMessage(104);
			}

		}

		@Override
		public void onReadCardNumber(String random, String encryptCardNumber) {
			// setLogMsg("随机数 = " + random);
			// setLogMsg("加密卡号 = " + encryptCardNumber);
		}

		@Override
		public void onReadTrackData(String account, String random,
				String encryptTrack2, String encryptTrack3) {
			PosData.getPosData().setCardNo(account);
			PosData.getPosData().setRandom("10");
			PosData.getPosData().setMediaType("01");
			PosData.getPosData().setCrdnum("00");
			PosData.getPosData().setIcdata("");
			PosData.getPosData().setPeriod("");
//			Log.i("result", "-----------encr2-----------" + encryptTrack2);
//			Log.i("result", "-----------encr3-----------" + encryptTrack3);
			PosData.getPosData().setTrack(encryptTrack2 + "|" + encryptTrack3);
			/*************************************************/
			goNext();

			/*************************************************/
			/*
			 * if (encryptTrack3 != null) { setLogMsg("主账号 = " + account);
			 * setLogMsg("随机数 = " + random); setLogMsg("二磁道数据密文 = " +
			 * encryptTrack2); setLogMsg("三磁道数据密文 = " + encryptTrack3); } else {
			 * setLogMsg("主账号 = " + account); setLogMsg("随机数 = " + random);
			 * setLogMsg("二三磁道数据密文 = " + encryptTrack2); }
			 */
		}

		@Override
		public void onGetPbocTradeData(String result, String tradeDate,
				String random, String account, String icSerialNo,
				String cardExpire, String encryptTrack2, String data55) {
			/*
			 * setLogMsg("执行结果 = " + result); setLogMsg("终端交易时间 = " +
			 * tradeDate); setLogMsg("随机数 = " + random); setLogMsg("主账号 = " +
			 * account); setLogMsg("IC卡序列号 = " + icSerialNo);
			 * setLogMsg("卡有效期 = " + cardExpire); setLogMsg("等效二磁数据密文 = " +
			 * encryptTrack2); setLogMsg("55域数据 = " + data55);
			 */
			PosData.getPosData().setRandom("10");
			PosData.getPosData().setCardNo(account);
			PosData.getPosData().setCrdnum(icSerialNo);
			PosData.getPosData().setPeriod(cardExpire);

			PosData.getPosData().setTrack(encryptTrack2 + "|");
			PosData.getPosData().setIcdata(data55);
			PosData.getPosData().setMediaType("02");
			/*************************************************/
			goNext();

			/*************************************************/
		}

		@Override
		public void onExecutePbocSecondAuth(String result, String tradeDate,
				String random, String tradeData, String mac) {

		}

		public void onEndProcess() {

		}

		@Override
		public void onEncryptPin(String random, String encryptPin) {
			/*
			 * setLogMsg("随机数 = " + random); setLogMsg("PIN密文数据 = " +
			 * encryptPin);
			 */
		}

		@Override
		public void onCalculateMac(String random, String mac) {
			/*
			 * setLogMsg("随机数 = " + random); setLogMsg("MAC = " + mac);
			 */
		}

		@Override
		public void onChangeToUpgradeMode() {
			// setLogMsg("切换至升级模式成功！");
		}

		@Override
		public void onWriteDeviceId() {
			// setLogMsg("写终端号成功！");
		}

		@Override
		public void onWriteMainKey() {
//			Log.i("result", "----------ddddd------写主密钥成功！--");

		}

		@Override
		public void onTestCommunicate() {
		}

		@Override
		public void onCancelCardReader() {
			// TODO Auto-generated method stub
			
		}
	};

	private String getTradeDate() {
		return new SimpleDateFormat("yyyyMMddHHmmss").format(new Date(System
				.currentTimeMillis()));
	}

	private void goNext() {
//		PosData.getPosData().setPayType("02");
		PosData.getPosData().setPayAmt(amount);
		PosData.getPosData().setTermType("02");
		PosData.getPosData().setType("蓝牙刷卡器");

		Intent intent = new Intent();
		if (action.equals(Actions.ACTION_CASHIN)) {
			intent.setClass(mContext, SignaturePadActivity.class);
		} else {
			intent.setClass(mContext, CardBalanceConfirmActivity.class);
		}

		startActivity(intent);
		finish();

	}

}

