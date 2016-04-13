package com.lk.qf.pay.activity;

import java.text.DecimalFormat;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Vibrator;
import android.text.InputType;
import android.text.TextUtils;
import android.text.method.NumberKeyListener;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import com.landicorp.android.mpos.reader.LandiMPos;
import com.lk.bhb.pay.R;
import com.lk.qf.pay.activity.swing.SwingHXCardActivity;
import com.lk.qf.pay.beans.PosData;
import com.lk.qf.pay.golbal.Actions;
import com.lk.qf.pay.golbal.MApplication;
import com.lk.qf.pay.sharedpref.SharedPrefConstant;
import com.lk.qf.pay.tool.T;
import com.lk.qf.pay.utils.AmountUtils;
import com.lk.qf.pay.v50.PayByV50CardActivity;
import com.lk.qf.pay.wedget.CommonTitleBar;
import com.lk.qf.pay.wedget.customdialog.ActionSheetDialog;
import com.lk.qf.pay.wedget.customdialog.ActionSheetDialog.OnSheetItemClickListener;
import com.lk.qf.pay.wedget.customdialog.ActionSheetDialog.SheetItemColor;
import com.lk.qf.pay.zxb.ZXBDeviceListActivity;

public class CashInActivity extends BaseActivity implements OnClickListener {

	private ImageButton btnDel, btnPay;
	private Button btn00, btn0, btn1, btn2, btn3, btn4, btn5, btn6, btn7, btn8,
			btn9, btnPoint;
	private EditText amountEdit;
	private StringBuilder sb;
	private boolean isDian = false;
	private Vibrator vibrator;
	private DecimalFormat nf;
	private LandiMPos reader;
	private String buletooth;
	private Context ctx;
	private String state;
	private String payType = "03";
	private CommonTitleBar title;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.cashin_new_layout);
		ctx = CashInActivity.this;
		initView();
		reader = LandiMPos.getInstance(this);
		vibrator = (Vibrator) getSystemService(Service.VIBRATOR_SERVICE);
		nf = new DecimalFormat("0.00"); // 保留几位小数
		buletooth = MApplication.mSharedPref.getSharePrefString(
				"blueTootchAddress", null);
		state = MApplication.mSharedPref
				.getSharePrefString(SharedPrefConstant.STATE);
	}

	private void initView() {

		title = (CommonTitleBar) findViewById(R.id.titlebar_cashin_new);
		title.setActName("即刷即到");
		title.setCanClickDestory(this, true);

		amountEdit = (EditText) this.findViewById(R.id.cashin_amount_edit_1);
		amountEdit.setText("0.00");
		btn00 = (Button) this.findViewById(R.id.btn00_1);
		btn0 = (Button) this.findViewById(R.id.btn0_1);
		btn1 = (Button) this.findViewById(R.id.btn1_1);
		btn2 = (Button) this.findViewById(R.id.btn2_1);
		btn3 = (Button) this.findViewById(R.id.btn3_1);
		btn4 = (Button) this.findViewById(R.id.btn4_1);
		btn5 = (Button) this.findViewById(R.id.btn5_1);
		btn6 = (Button) this.findViewById(R.id.btn6_1);
		btn7 = (Button) this.findViewById(R.id.btn7_1);
		btn8 = (Button) this.findViewById(R.id.btn8_1);
		btn9 = (Button) this.findViewById(R.id.btn9_1);
		btnPoint = (Button) this.findViewById(R.id.btn_point_1);
		btnDel = (ImageButton) this.findViewById(R.id.btn_del_1);
		btnPay = (ImageButton) this.findViewById(R.id.btn_pay_1);
		
		// LayoutParams lp= (LayoutParams) ll.getLayoutParams();
		// lp.width = 0;
		// lp.height = 0;
		// ll.setLayoutParams(lp);
		// if ( ll!=null ) {
		// ViewGroup parent = ( ViewGroup ) ll.getParent() ;
		// parent.removeView ( ll ) ;
		// }
		// btnWeixin = (ImageButton) findViewById(R.id.btn_sk_weixin);
		// btnZhifubao = (ImageButton) findViewById(R.id.btn_sk_zhifubao);
		// btnyifubao = (ImageButton) findViewById(R.id.btn_sk_yifubao);
		// btnBaidu = (ImageButton) findViewById(R.id.btn_sk_baidu);
		btn00.setOnClickListener(this);
		btn0.setOnClickListener(this);
		btn1.setOnClickListener(this);
		btn2.setOnClickListener(this);
		btn3.setOnClickListener(this);
		btn4.setOnClickListener(this);
		btn5.setOnClickListener(this);
		btn6.setOnClickListener(this);
		btn7.setOnClickListener(this);
		btn8.setOnClickListener(this);
		btn9.setOnClickListener(this);
		btnPoint.setOnClickListener(this);
		btnDel.setOnClickListener(this);
		btnPay.setOnClickListener(this);
		// findViewById(R.id.btn_back).setOnClickListener(
		// new View.OnClickListener() {
		//
		// @Override
		// public void onClick(View v) {
		// finish();
		// }
		// });
	}

	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		amountEdit.setText("0.00");
		sb = new StringBuilder();
		amountEdit.setKeyListener(new MNumberKeyListener());
		amountEdit.setFocusable(true);
	}

	public class MNumberKeyListener extends NumberKeyListener {
		@Override
		protected char[] getAcceptedChars() {
			char[] numberChars = { '.', '0', '1', '2', '3', '4', '5', '6', '7',
					'8', '9' };
			return numberChars;
		}
		@Override
		public int getInputType() {
			// return InputType.TYPE_NUMBER_FLAG_DECIMAL;
			return InputType.TYPE_DATETIME_VARIATION_NORMAL;
		}
	}

	private void insert(Button btn) {
		String str = btn.getText().toString();
		if (sb.length() == 0 && str.equals("00")) {
			return;
		}
		if (isDian) {
			if (sb.toString().contains(".")) {
				String sbStr = new StringBuilder(sb.toString()).reverse()
						.toString();
				if (sbStr.indexOf(".") == 1) {
					sb.append(str);
				}
			} else {
				if (sb.length() == 0) {
					sb.append("0." + str);
				} else {
					sb.append("." + str);
				}
			}
		} else {
			sb.append(str);
		}
		String amount = nf.format(Double.parseDouble(sb.toString()));
		if (amount.length() < 14) {
			amountEdit.setText(amount);
		}
	}

	@Override
	public void onClick(View v) {
		vibrator.vibrate(new long[] { 0, 15 }, -1);
		switch (v.getId()) {
		case R.id.btn00_1:
			insert(btn00);
			break;
		case R.id.btn0_1:
			insert(btn0);
			break;
		case R.id.btn1_1:
			insert(btn1);
			break;
		case R.id.btn2_1:
			insert(btn2);
			break;
		case R.id.btn3_1:
			insert(btn3);
			break;
		case R.id.btn4_1:
			insert(btn4);
			break;
		case R.id.btn5_1:
			insert(btn5);
			break;
		case R.id.btn6_1:
			insert(btn6);
			break;
		case R.id.btn7_1:
			insert(btn7);
			break;
		case R.id.btn8_1:
			insert(btn8);
			break;
		case R.id.btn9_1:
			insert(btn9);
			break;
		case R.id.btn_point_1:
			isDian = true;

			break;
		case R.id.btn_del_1:
			isDian = false;
			sb.delete(0, sb.length());
			amountEdit.setText("");
			break;
		case R.id.btn_pay_1:
			goBrush();
			// if (state.equals("en")) {
			// goBrush();
			// } else {
			// T.ss("该商户尚未通过审核");
			// }
			break;

		}
	}

	private void goBrush() {
		String amount = amountEdit.getText().toString().trim();
		amount = AmountUtils.changeY2F(amount);
		if (TextUtils.isEmpty(amount)) {
			T.ss("金额格式不正确");
		} else {
			// int money = Integer.valueOf(amount) / 100;
			long money = Long.valueOf(amount);
			// double money = Double.valueOf(amount);
			if (money > 0) {
				goStepTwo(amount);
			} else {
				T.ss("金额不能小于0元!");
			}
		}
	}

	private void goStepTwo(String amount) {
		nextStep();
		PosData.getPosData().setPayAmt(amount);

	}


	private void nextStep() {
		new ActionSheetDialog(CashInActivity.this)
				.builder()
				.setTitle("请选择刷卡器类型")
				.setCancelable(false)
				.setCanceledOnTouchOutside(true)
				.addSheetItem("音频刷卡器", SheetItemColor.Blue,
						new OnSheetItemClickListener() {
							@Override
							public void onClick(int which) {
								PosData.getPosData().setPayType(payType);
								Intent intent1 = new Intent(ctx,
										SwingHXCardActivity.class);
								intent1.setAction(Actions.ACTION_CASHIN);
								startActivity(intent1);
							}
						})
				.addSheetItem("蓝牙刷卡器", SheetItemColor.Blue,
						new OnSheetItemClickListener() {
							@Override
							public void onClick(int which) {
								PosData.getPosData().setPayType(payType);
								Intent it = new Intent(ctx,
										ZXBDeviceListActivity.class);
								it.setAction(Actions.ACTION_CASHIN);
								startActivity(it);
							}
						})

				.addSheetItem("键盘蓝牙刷卡器", SheetItemColor.Blue,
						new OnSheetItemClickListener() {
							@Override
							public void onClick(int which) {
								PosData.getPosData().setPayType(payType);
								Intent intent2 = new Intent(ctx,
										PayByV50CardActivity.class);
								intent2.setAction(Actions.ACTION_CASHIN);
								startActivity(intent2);
							}
						}).show();
	}

}
