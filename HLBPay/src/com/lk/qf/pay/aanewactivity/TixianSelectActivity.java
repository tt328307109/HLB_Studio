package com.lk.qf.pay.aanewactivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.RelativeLayout;

import com.lk.bhb.pay.R;
import com.lk.qf.pay.activity.BaseActivity;
import com.lk.qf.pay.wedget.CommonTitleBar;

public class TixianSelectActivity extends BaseActivity implements OnClickListener{

	private RelativeLayout rlT0,rlT1,rlListT0,rlListT1;
	private CommonTitleBar title;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.tixian_layout);
		init();
	}
	
	private void init(){
		rlT0 = (RelativeLayout) findViewById(R.id.rl_tixian_t0);
		rlT1 = (RelativeLayout) findViewById(R.id.rl_tixian_t1);
		rlListT0 = (RelativeLayout) findViewById(R.id.rl_t0_tixian_list);
//		rlListT1 = (RelativeLayout) findViewById(R.id.rl_t1_tixian_list);
		rlT0.setOnClickListener(this);
		rlT1.setOnClickListener(this);
		rlListT0.setOnClickListener(this);
//		rlListT1.setOnClickListener(this);
		title = (CommonTitleBar) findViewById(R.id.titlebar_tixiannew_title);
		title.setActName("提现");
		title.setCanClickDestory(this, true);
	}
	
	
	@Override
	public void onClick(View arg0) {
		// TODO Auto-generated method stub
		Intent intent = null;
		switch (arg0.getId()) {
		
		case R.id.rl_tixian_t0:
			intent = new Intent(TixianSelectActivity.this,TixianNewActivity.class);
			intent.setAction(TixianNewActivity.T0TIXIAN);
			break;
		case R.id.rl_tixian_t1:
			
			intent = new Intent(TixianSelectActivity.this,TixianNewActivity.class);
			intent.setAction(TixianNewActivity.T1TIXIAN);
			break;
		case R.id.rl_t0_tixian_list:
			intent = new Intent(TixianSelectActivity.this,TiXianListActivity.class);
//			intent.putExtra("payType", "T0");
			break;
//		case R.id.rl_t1_tixian_list:
//			intent = new Intent(TixianSelectActivity.this,TiXianListActivity.class);
//			intent.putExtra("payType", "T1");
//			break;

		default:
			break;
		}
		startActivity(intent);
	}

}
