package com.lk.qf.pay.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;

import com.lk.bhb.pay.R;
import com.lk.qf.pay.wedget.CommonTitleBar;

public class PwdManageActivity extends BaseActivity implements OnClickListener {

	CommonTitleBar title;
	private Context ctx;

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.pwd_manage);
		ctx = this;
		title = (CommonTitleBar) findViewById(R.id.titlebar_pwdmanage);
		title.setActName("密码管理");
		title.setCanClickDestory(this, true);
		// findViewById(R.id.btn_pwd_manage_revise_login_pwd).setOnClickListener(
		// this);
		// findViewById(R.id.btn_pwd_manage_revise_pay_pwd).setOnClickListener(
		// this);
		// findViewById(R.id.btn_pwd_manage_find_pay_pwd).setOnClickListener(this);
		findViewById(R.id.pwd_findpay).setOnClickListener(this);//找回支付密码
		findViewById(R.id.pwd_reviselogin).setOnClickListener(this);//修改登录密码
		findViewById(R.id.pwd_revisepay).setOnClickListener(this);//修改支付密码
		findViewById(R.id.pwd_setpay).setOnClickListener(this);//设置支付密码
	}

	@Override
	public void onClick(View v) {
		Intent intent = new Intent();
		switch (v.getId()) {

		case R.id.pwd_reviselogin:
			// getVerify(VerifyType.REVISE_LOGIN_PWD);
			intent.setClass(ctx, PwdReviseActivity.class);
			intent.setAction(PwdReviseActivity.ACTION_REVISE_LOGIN_PWD);
			break;
		case R.id.pwd_revisepay:
			// getVerify(VerifyType.REVISE_PAY_PWD);
			intent.setClass(ctx, PwdReviseActivity.class);
			intent.setAction(PwdReviseActivity.ACTION_REVISE_PAY_PWD);
			break;
		case R.id.pwd_findpay:
			// getVerify(VerifyType.FIND_PAY_PWD);
			intent.setClass(ctx, FindPwdActivity.class);
			intent.setAction(FindPwdActivity.ACTION_FIND_PAY_PWD);
			// return;
			// intent.setClass(ctx, FindPayPwdActivity.class);
			
			break;
		case R.id.pwd_setpay:
			// getVerify(VerifyType.FIND_PAY_PWD);
			intent.setClass(ctx, FindPwdActivity.class);
			intent.setAction(FindPwdActivity.ACTION_SET_PAY_PWD);
			// return;
			// intent.setClass(ctx, FindPayPwdActivity.class);
			break;

		default:
			break;
		}
		startActivity(intent);
	}

	private void getVerify(int verifyType) {
		// Intent it = new Intent(this, PwdVerifyActivity.class);
		// it.putExtra("verifyType", verifyType);
		// it.putExtra("action", PwdVerifyActivity.ACTION_NOEDITABLE);
		// startActivity(it);

	}

}
