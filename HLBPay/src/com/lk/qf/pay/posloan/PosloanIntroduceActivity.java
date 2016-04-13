package com.lk.qf.pay.posloan;

import android.os.Bundle;

import com.lk.bhb.pay.R;
import com.lk.qf.pay.activity.BaseActivity;
import com.lk.qf.pay.wedget.CommonTitleBar;

public class PosloanIntroduceActivity extends BaseActivity {

	private CommonTitleBar title;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.posloan_introduce_layout);
		title = (CommonTitleBar) findViewById(R.id.titlebar_posLoan_introdu_title);
		title.setActName("详情");
		title.setCanClickDestory(this, true);
	}
}
