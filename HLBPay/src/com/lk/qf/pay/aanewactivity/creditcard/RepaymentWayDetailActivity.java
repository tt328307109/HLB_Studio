package com.lk.qf.pay.aanewactivity.creditcard;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.lk.bhb.pay.R;
import com.lk.qf.pay.activity.BaseActivity;
import com.lk.qf.pay.beans.Xinyongkainfo;
import com.lk.qf.pay.tool.T;
import com.lk.qf.pay.wedget.CommonTitleBar;

public class RepaymentWayDetailActivity extends BaseActivity implements OnClickListener{

	RelativeLayout sqhkbg , klsbg , hkmxbg;
	Xinyongkainfo info;
	Button sqhk , kls , hkmx;
	private CommonTitleBar title;
	String type = "";
	TextView tv1 , tv2 , tv3;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_repaymentway_detail);
		init();
		
	}
	
	private void init(){
		sqhkbg = (RelativeLayout) findViewById(R.id.sqhkbg);
		klsbg = (RelativeLayout) findViewById(R.id.klsbg);
		hkmxbg = (RelativeLayout) findViewById(R.id.hkmxbg);
		sqhk = (Button) findViewById(R.id.sqhk);
		kls = (Button) findViewById(R.id.kls);
		hkmx = (Button) findViewById(R.id.hkmx);
		tv1 = (TextView) findViewById(R.id.left_tv_repayment1);
		tv2 = (TextView) findViewById(R.id.left_tv_repayment2);
		tv3 = (TextView) findViewById(R.id.left_tv_repayment3);
		sqhk.setOnClickListener(this);
		kls.setOnClickListener(this);
		hkmx.setOnClickListener(this);
		title = (CommonTitleBar) findViewById(R.id.titlebar_choose_repaymentway2);
		title.setCanClickDestory(this, true);
	
		Intent intent = getIntent();
		if (intent!=null) {
			info = (Xinyongkainfo)intent.getParcelableExtra("info");
			type = intent.getStringExtra("type");
			String types = info.getType();
			if((!types.equals("8"))) {
				//type既不为8的时候。不能点-申请还款按钮变灰 - 卡立刷灰//8的时候才能点
//				sqhk.setClickable(false);
				tv1.setBackgroundResource(R.drawable.arrow1_1);
				tv2.setBackgroundResource(R.drawable.arrow1_1);
				sqhkbg.setBackgroundResource(R.drawable.button1_1);
				klsbg.setBackgroundResource(R.drawable.button1_1);
				sqhk.setTextColor(Color.parseColor("#5f5f5f"));
				kls.setTextColor(Color.parseColor("#5f5f5f"));
			} 
			if((!types.equals("6"))&&(!types.equals("7"))) {
				hkmxbg.setBackgroundResource(R.drawable.button1_1);
				hkmx.setTextColor(Color.parseColor("#5f5f5f"));
				tv3.setBackgroundResource(R.drawable.arrow1_1);
			}
		}
		if(type.equals("0")) {
			title.setActName("我要贷款");
		}else if(type.equals("1")) {
			title.setActName("我要代还");
		}
	}

	@Override
	public void onClick(View v) {
		if(v.getId() == R.id.sqhk) {
			String types = info.getType();
			if((!types.equals("8"))) {
				T.ss("当前状态不能申请贷款！");
				return;
			}
			Intent i = new Intent(RepaymentWayDetailActivity.this , ApplyRepaymentDetailActivity.class);
			i.putExtra("info", info);
			startActivity(i);
		}else if(v.getId() ==R.id.kls) {
			String types = info.getType();
			if((!types.equals("8"))) {
				T.ss("当前状态不能使用卡立刷！");
			}

		}else if(v.getId() ==R.id.hkmx) {
			String types = info.getType();
			if((!types.equals("6"))&&(!types.equals("7"))) {
				T.ss("暂无明细！");
				
			} else {
				Intent i = new Intent(RepaymentWayDetailActivity.this , CreditCardRepayDateListActivity.class);
				i.putExtra("info", info);
				startActivity(i);
			}
			
		}
	}
}
