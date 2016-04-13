package com.lk.qf.pay.hxbluetooth;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;

import com.lk.bhb.pay.R;
import com.lk.qf.pay.activity.BaseActivity;
import com.lk.qf.pay.beans.PosData;
import com.lk.qf.pay.beans.SwingpayParams;
import com.lk.qf.pay.golbal.Actions;
import com.lk.qf.pay.jhl.BluetoothCommmanager;
import com.lk.qf.pay.utils.AmountUtils;
import com.lk.qf.pay.wedget.CommonTitleBar;
import com.msafepos.sdk.HXPos;
import com.msafepos.sdk.listener.PosEvent;

public class PayByHXBluetoothActivity extends BaseActivity implements PosEvent{

	private TextView cashin_account_text;// 充值钱数
	private TextView cashin_show_msg_text;// 刷卡状态
	private CommonTitleBar title;
	private String period = "";// 有效期
	private String crdnum = "";// 银行卡序列号去
	private String amount = "";// 充值数量
	private String Encrytrack2len = "", Encrytrack3len = "", Encrytrack2 = "",
			Encrytrack3 = "", Track55 = "";
	private SwingpayParams params = new SwingpayParams();
	private String cardNo = "", randomNum = "", encTrackData = "",
			rateTypeID = "", icNumber = "", pinblock = "";
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
		if (payType == null) {
			payType = "";
		}
		Log.i("result", "--------------------oncreat-amount-----------"
				+ amount);
		initData();
		connBlue();
	}

	/**
	 * 初始化
	 */
	private void initData() {
		// TODO Auto-generated method stub
		title = (CommonTitleBar) findViewById(R.id.titlebar_bule_ldcard);
		title.setCanClickDestory(this, true);
		cashin_account_text = (TextView) findViewById(R.id.cashin_account_text_bule);
		cashin_account_text.setText(AmountUtils.changeFen2Yuan(amount) + "元");
		cashin_show_msg_text = (TextView) findViewById(R.id.cashin_show_msg_text_bule1);

		if (action.equals(Actions.ACTION_CASHIN)) {
			if (payType.equals("03")) {
				title.setActName("即刷即到");
			} else {
				title.setActName("刷卡支付");
			}
		} else {
			title.setActName("设备绑定");
		}
	}

	/**
	 * 连接蓝牙pos
	 */
	private void connBlue() {
		Intent intent1 = getIntent();
		if (intent1 != null) {
			blueTootchAddress = intent1.getStringExtra("deviceAdd");
			Log.i("result", "---------add----------" + blueTootchAddress);
			HXPos.getObj().ConnBlueWithMacAddr(blueTootchAddress);
		}
	}

	@Override
	public void OnLogInfo(String arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void OnPosConnect(boolean arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onRecvData(int arg0, byte[] arg1) {
		// TODO Auto-generated method stub
		
	}
}
