package com.lk.qf.pay.aanewactivity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import com.lk.bhb.pay.R;
import com.lk.qf.pay.activity.BaseActivity;
import com.lk.qf.pay.golbal.Actions;
import com.lk.qf.pay.tool.T;
import com.lk.qf.pay.utils.CreatePayCodeUtils;
import com.lk.qf.pay.wedget.CommonTitleBar;
import com.mining.app.zxing.MipcaActivityCapture;

public class CreatePaytCodeAccountActivity extends BaseActivity implements
		OnClickListener {

	private String beizhu = "", account = "", action;
	private EditText edAccount;
	private CommonTitleBar title;
	private boolean type=false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.create_pay_code_account_layout);
		init();
	}

	private void init() {
		edAccount = (EditText) findViewById(R.id.ed_payCode_account);
		findViewById(R.id.tv_payCode_addBeizhu).setOnClickListener(this);
		findViewById(R.id.btn_payCode_queren).setOnClickListener(this);

		title = (CommonTitleBar) findViewById(R.id.titlebar_create_pay_code_account);
		title.setActName("设置金额");
		title.setCanClickDestory(this, true);
		Intent intent = getIntent();
		if (intent != null) {
			action = intent.getAction();
			type = intent.getBooleanExtra("type", false);
			Log.i("result","-----------action-w-------"+action);
			Log.i("result","-----------type-------"+type);
		}

	}

	@Override
	public void onClick(View arg0) {
		// TODO Auto-generated method stub

		switch (arg0.getId()) {
		case R.id.tv_payCode_addBeizhu:
			inputTitleDialog();
			break;
		case R.id.btn_payCode_queren:

			account = edAccount.getText().toString();
			if (account.equals("")) {
				T.ss("请输入收款金额");
				return;
			}
			Log.i("result","-----------action--------"+action);
			if (action != null && !action.equals("")) {
				if (action.equals(Actions.ACTION_ZHUANZHANG)) {// 钱包转账
					tranferQb();
				} else {
					Log.i("result","-----------account--------"+account);
					if (type) {
						tranferPayCodeYFB();
					}else{
						tranferQb();
					}
				}
			}
			break;

		default:
			break;
		}
	}

	/**
	 * 钱包转账 设置金额
	 */
	private void tranferQb() {
		Intent intent = new Intent();
		intent.putExtra("account", account);
		intent.putExtra("beizhu", beizhu);
		if (action != null && !action.equals("")) {
			intent.setAction(action);
//			if (action.equals(Actions.ACTION_ZHUANZHANG)) {// 钱包转账
//			}
		}
		setResult(Activity.RESULT_OK, intent);
		finish();
	}
	
//	/**
//	 * 其他转账 设置金额
//	 */
//	private void tranferOther() {
//		Intent intent = new Intent();
//		intent.putExtra("account", account);
//		intent.putExtra("beizhu", beizhu);
//		setResult(Activity.RESULT_OK, intent);
//		finish();
//	}
	
	/**
	 * 易付宝  扫码设置金额
	 */
	private void tranferPayCodeYFB(){
		Intent intent = new Intent(CreatePaytCodeAccountActivity.this, MipcaActivityCapture.class);
		intent.setAction(action);
		intent.putExtra("money", account);
		startActivity(intent);
		finish();
	}

	private void inputTitleDialog() {

		final EditText inputServer = new EditText(this);
		inputServer.setFocusable(true);
		inputServer.setBackground(null);
		inputServer.setText(beizhu);
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle("请输入备注").setView(inputServer)
				.setNegativeButton("取消", new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface arg0, int arg1) {
						// TODO Auto-generated method stub

					}
				});
		builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {

			public void onClick(DialogInterface dialog, int which) {
				beizhu = inputServer.getText().toString();
			}
		});
		builder.show();
	}

}
