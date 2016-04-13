package com.lk.qf.pay.aanewactivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;

import com.lk.bhb.pay.R;
import com.lk.qf.pay.aanewactivity.shoukuan.TradeQueryListActivity;
import com.lk.qf.pay.activity.BaseActivity;
import com.lk.qf.pay.golbal.Actions;
import com.lk.qf.pay.tool.T;
import com.lk.qf.pay.wedget.CommonTitleBar;

public class JiaoYiGuanLiNewActivity extends BaseActivity implements
		OnClickListener {

	private CommonTitleBar title;
	private String typeId = "101";
	private String action = "";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.jiaoyiguanli_new_layout);
		init();
	}

	private void init() {
		findViewById(R.id.rl_jiaoyi_mpos).setOnClickListener(this);
		findViewById(R.id.rl_jiaoyi_weixin).setOnClickListener(this);
		findViewById(R.id.rl_jiaoyi_zhifubao).setOnClickListener(this);
		findViewById(R.id.rl_jiaoyi_baidu).setOnClickListener(this);
		findViewById(R.id.rl_jiaoyi_yifubao).setOnClickListener(this);
		findViewById(R.id.rl_jiaoyi_pos).setOnClickListener(this);

		title = (CommonTitleBar) findViewById(R.id.titlebar_jiaoyiguanli);
		title.setActName("交易管理");
		title.setCanClickDestory(this, true);

	}

	@Override
	public void onClick(View arg0) {
		// TODO Auto-generated method stub

		Intent intent = null;

		switch (arg0.getId()) {
		case R.id.rl_jiaoyi_pos:
			typeId = "101";
			action = Actions.ACTION_POS;
			break;
		case R.id.rl_jiaoyi_mpos:
			typeId = "102";
			action = Actions.ACTION_MPOS;

			break;
		case R.id.rl_jiaoyi_weixin:
			typeId = "103";
			action = Actions.ACTION_WEIXIN;
			break;
		case R.id.rl_jiaoyi_zhifubao:
			typeId = "104";
			action = Actions.ACTION_ZHIFUBAO;
			break;
		case R.id.rl_jiaoyi_baidu:
			typeId = "105";
			action = Actions.ACTION_BAIDU;
			break;
		case R.id.rl_jiaoyi_yifubao:
			typeId = "106";
			action = Actions.ACTION_YIFUBAO;
			break;
		default:
			break;
		}
		if (action.equals(Actions.ACTION_MPOS) || action.equals(Actions.ACTION_POS)) {
			Log.i("result", "-------action1-----"+action);
			intent = new Intent(JiaoYiGuanLiNewActivity.this,
					DingDanListInfoActivity.class);
		}else{
//			if (action.equals(Actions.ACTION_ZHIFUBAO)) {
//				T.ss("建设中");
//				return;
//			}
			intent = new Intent(JiaoYiGuanLiNewActivity.this,
					TradeQueryListActivity.class);
		}
		intent.setAction(action);
		intent.putExtra("id", typeId);
		startActivity(intent);
	}
}
