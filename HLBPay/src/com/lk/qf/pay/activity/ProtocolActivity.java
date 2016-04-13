package com.lk.qf.pay.activity;

import com.lk.bhb.pay.R;
import com.lk.qf.pay.wedget.CommonTitleBar;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

public class ProtocolActivity extends BaseFragmentActivity {

	private TextView contentText;
	private CommonTitleBar title;
	private String strTitle = "服务协议";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_protocol);
		Intent intent = getIntent();
		if (intent != null) {
			strTitle = intent.getStringExtra("title");
		}
		title = (CommonTitleBar) findViewById(R.id.titlebar_posLoan_xieyi);
		title.setActName(strTitle);
		title.setCanClickDestory(this, true);

		contentText = (TextView) findViewById(R.id.protocol_content_text);
		if (strTitle==null) {
			contentText.setText(getString(R.string.service_agreement_content));
		}else{
			if (strTitle.equals("快易付")) {
				contentText.setText(getString(R.string.service_agreement_content));
			} else if (strTitle.equals("保理贷产品服务协议")) {
				contentText.setText(getString(R.string.poly_loan_xieyi));
				
			} else if (strTitle.equals("快易付理财服务协议")) {
				contentText.setText(getString(R.string.licai_xieyi));
				
			} else if (strTitle.equals("信用卡贷款还款服务协议")) {
				contentText.setText(getString(R.string.licai_xieyi));
			}
		}
	}
}
