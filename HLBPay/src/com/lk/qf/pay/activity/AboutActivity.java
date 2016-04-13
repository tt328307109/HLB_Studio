package com.lk.qf.pay.activity;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import com.lk.bhb.pay.R;
import com.lk.qf.pay.utils.GetAppVersion;
import com.lk.qf.pay.wedget.CommonTitleBar;

public class AboutActivity extends BaseActivity implements OnClickListener {

	private CommonTitleBar title;
	private TextView agreementText,tvVersion;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_about);
		title = (CommonTitleBar) findViewById(R.id.titlebar_abountWe);
		title.setActName("关于我们");
		title.setCanClickDestory(this, true);
		agreementText = (TextView) findViewById(R.id.about_service_agreement_text);
		tvVersion = (TextView) findViewById(R.id.about_app_version);
		agreementText.setText(Html.fromHtml("<u>"+getResources().getString(R.string.kuaiyifu_xieyi)+"</u>")); 
		agreementText.setOnClickListener(this);
		
		GetAppVersion v = new GetAppVersion(this);
		tvVersion.setText(getResources().getString(R.string.kuaiyifu_v) + v.getVersion());
		
	}
	
	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		finish();
		super.onBackPressed();
	}

	@Override
	public void onClick(View v) {
		
		switch (v.getId()) {
			case R.id.about_service_agreement_text:
				goProtocol();
				break;
		}
		
	}
	
	private void goProtocol() {
		Intent it = new Intent(this, ProtocolActivity.class);
		it.putExtra("title", "快易付");
		startActivity(it);
	}
	
}
