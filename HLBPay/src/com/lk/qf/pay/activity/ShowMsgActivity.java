package com.lk.qf.pay.activity;

import com.lk.bhb.pay.R;
import com.lk.qf.pay.aanewactivity.TixianSelectActivity;
import com.lk.qf.pay.aanewactivity.creditcard.CreditCardsListActivity;
import com.lk.qf.pay.aanewactivity.licai.LicaiNewActivity;
import com.lk.qf.pay.golbal.Actions;
import com.lk.qf.pay.golbal.MApplication;
import com.lk.qf.pay.sharedpref.SharedPrefConstant;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * 类名: ShowMsgActivity 作者:rehentLiu 时间：2014年2月13日 下午3:51:06 说明:
 */
public class ShowMsgActivity extends BaseActivity {
	private TextView tvMsg;
	private ImageView ivCode;
	private String action, msg, returnCode;
	private Button backBtn, btnConfirm;
	private boolean code = false;
	private String type = "", payType = "";

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.show_msg);
		Intent intent = getIntent();
		if (intent != null) {
			action = intent.getAction();
			type = intent.getStringExtra("type");
			payType = intent.getStringExtra("payType");
		}
		backBtn = (Button) findViewById(R.id.btn_back);
		code = getIntent().getBooleanExtra("code", false);
		msg = getIntent().getStringExtra("msg");
		returnCode = getIntent().getStringExtra("code");
		btnConfirm = (Button) findViewById(R.id.btn_show_msg_confirm);
		ivCode = (ImageView) findViewById(R.id.iv_show_msg_code);
		if (type.equals("00")) {
			btnConfirm.setVisibility(View.GONE);
		}
		if (returnCode.equals("00")) {
			MApplication.mSharedPref.putSharePrefString(
					SharedPrefConstant.USESORT, "1");
			ivCode.setImageResource(R.drawable.iv_success);
		}else{
			ivCode.setImageResource(R.drawable.iv_fail);
		}
		
		if (action.equals("ACTION_CARD_QUERY")) {
			backBtn.setText("银行卡查询");
			if (code) {
				ivCode.setImageResource(R.drawable.iv_balance);
			} else {
				ivCode.setImageResource(R.drawable.iv_fail);
			}
		} else if (action.equals("ACTION_CASH_IN")) {
			sendUpdateBanlanceBroadcast();
		}
		tvMsg = (TextView) findViewById(R.id.tv_show_msg_info);
		tvMsg.setText(msg + "\n[返回码" + returnCode + "]");
		btnConfirm.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				Intent intent = new Intent(ShowMsgActivity.this,
						TixianSelectActivity.class);
				startActivity(intent);

				finish();
			}

		});
		backBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// Intent intent = new Intent(ShowMsgActivity.this,
				// MenuActivity.class);
				// startActivity(intent);
				Log.i("result","------payType---------"+payType);
				if (payType!=null) {
					if (payType.equals("07")) {
						Intent intent = new Intent(ShowMsgActivity.this,
								LicaiNewActivity.class);
						startActivity(intent);
					}
					if (payType.equals("08")) {
						Intent intent = new Intent(ShowMsgActivity.this,
								CreditCardsListActivity.class);
						startActivity(intent);
					}
				}
				finish();
			}
		});
	}

	/**
	 * 刷新用户余额广播
	 */
	private void sendUpdateBanlanceBroadcast() {
		Intent broadCastIntent = new Intent();
		broadCastIntent.setAction(Actions.ACTION_QUERY_BALANCE);
		this.sendBroadcast(broadCastIntent);
	}
}
