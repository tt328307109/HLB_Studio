package com.lk.qf.pay.nfc;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import com.lk.bhb.pay.R;
import com.lk.qf.pay.activity.BaseActivity;
import com.lk.qf.pay.activity.swing.SignaturePadActivity;
import com.lk.qf.pay.beans.PosData;
import com.lk.qf.pay.utils.AmountUtils;
import com.lk.qf.pay.utils.MyUtilss;

public class NfcCashinRateActivity extends BaseActivity implements
		OnClickListener {

	private TextView  payAmtText, payRateText;
	private Button btnConfirm;
	private Context mContext;
	private final String[] rates = new String[] { "民生类"};
	private int rate = 0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);

		setContentView(R.layout.nfc_cashin_rate_layout);
		mContext = this;
		init();
	}

	private void init() {
//		orderNoText = (TextView) findViewById(R.id.nfc_cashin_order_no_text);
		payAmtText = (TextView) findViewById(R.id.nfc_cashin_pay_amt_text);
		btnConfirm = (Button) findViewById(R.id.nfc_cashin_confirm_btn);
		
//		orderNoText
//				.setText(Utils.hiddenCardNo(PosData.getPosData().getPrdordNo()));
		payAmtText.setText(AmountUtils.changeFen2Yuan(PosData.getPosData()
				.getPayAmt()) + "元");
		payRateText = (TextView) findViewById(R.id.nfc_cashin_pay_rate_text);
		payRateText.setText(rates[rate]);
		payRateText.setOnClickListener(this);
		btnConfirm.setOnClickListener(this);
		findViewById(R.id.nfc_btn_back).setOnClickListener(this);

	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.nfc_btn_back:
			finish();
			break;
		case R.id.nfc_cashin_pay_rate_text:
			queryRate();
			break;
		case R.id.nfc_cashin_confirm_btn:
			Intent intent = new Intent(NfcCashinRateActivity.this, SignaturePadActivity.class);
			intent.setAction("ACTION_NFC_PAY");
			startActivity(intent);
			finish();
			break;

		default:
			break;
		}
	}

	private void queryRate() {
		Dialog dialog = new AlertDialog.Builder(this)
				.setTitle("选择费率类型")
				.setSingleChoiceItems(rates, 0,
						new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								rate = which;
								payRateText.setText(rates[rate]);
								dialog.dismiss();

							}
						}).create();
		dialog.show();
	}
	
}
