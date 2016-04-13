package com.lk.qf.pay.indiana.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.lk.bhb.pay.R;
import com.lk.qf.pay.activity.BaseActivity;
import com.lk.qf.pay.tool.T;

public class MyPwdActivity extends Activity implements OnClickListener{

//	private ImageButton ibClose;
	private TextView tvShowMoney;
	private EditText edPwd;
	private String pwd,account;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.pwd_show_dialog_layout);
		init();
	}
	
	private void init(){
		findViewById(R.id.imgbtn_pwd_close1).setOnClickListener(this);
		findViewById(R.id.pay_cancel1).setOnClickListener(this);
		findViewById(R.id.pay_sure1).setOnClickListener(this);
		tvShowMoney = (TextView) findViewById(R.id.pay_content_1);
		edPwd = (EditText) findViewById(R.id.ed_pwd_dialog);
		Intent intent = getIntent();
		if (intent!=null) {
			account = intent.getStringExtra("account");
			Log.i("result", "----------account--------"+account);
			tvShowMoney.setText("夺宝币"+account+"个");
		}
	}
	
	@Override
	public void onClick(View arg0) {
		// TODO Auto-generated method stub
		switch (arg0.getId()) {
		case R.id.imgbtn_pwd_close1:
			
			finish();
			break;
		case R.id.pay_cancel1:
			finish();
			break;
		case R.id.pay_sure1:
			pwd = edPwd.getText().toString();
			if (pwd.length()!=6) {
				T.ss("请输入6位数的支付密码");
				return;
			}
			Intent intent = new Intent();
			intent.putExtra("pwd", pwd);
			setResult(Activity.RESULT_OK, intent);
			finish();
			break;

		default:
			break;
		}
	}

}
