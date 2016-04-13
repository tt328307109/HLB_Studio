package com.lk.qf.pay.posloan;

import android.os.Bundle;
import android.widget.TextView;

import com.lk.bhb.pay.R;
import com.lk.qf.pay.activity.BaseActivity;
import com.lk.qf.pay.wedget.CommonTitleBar;

public class LoanXieYiActivity extends BaseActivity{

	private CommonTitleBar title;
	private String strxieyi;
	private TextView tvXieYi;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.loan_xieyi_layout);
		title = (CommonTitleBar) findViewById(R.id.titlebar_posLoan_title1);
		title.setActName("服务协议");
		title.setCanClickDestory(this, true);
	}
	
}
