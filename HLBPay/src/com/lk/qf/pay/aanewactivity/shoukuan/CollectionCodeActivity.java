package com.lk.qf.pay.aanewactivity.shoukuan;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import com.lk.bhb.pay.R;
import com.lk.qf.pay.aanewactivity.CreatePayCodeActivity;
import com.lk.qf.pay.aanewactivity.CreatePaytCodeAccountActivity;
import com.lk.qf.pay.activity.BaseActivity;
import com.lk.qf.pay.golbal.Actions;
import com.lk.qf.pay.wedget.CommonTitleBar;

public class CollectionCodeActivity extends BaseActivity implements
		OnClickListener {

	private CommonTitleBar title;
	private String strTitle, action = "", account = "";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.shoukuan_code_layout);

		findViewById(R.id.rl_sk_code).setOnClickListener(this);
		findViewById(R.id.rl_sk_saosao).setOnClickListener(this);
		title = (CommonTitleBar) findViewById(R.id.titlebar_sk_Code_back);
		init();
		title.setActName(strTitle);
		title.setCanClickDestory(this, true);
	}

	private void init(){
		Intent intent = getIntent();
		if (intent != null) {
			action = intent.getAction();
//			Log.i("result","-----------action11--------"+action);
			account = intent.getStringExtra("account");
			if (account == null || account.equals("")) {
				account = "";
			}
			if (action.equals(Actions.ACTION_WEIXIN)) {// 微信
				strTitle = "微信收款";

			} else if (action.equals(Actions.ACTION_ZHIFUBAO)) {// 支付宝
				strTitle = "支付宝收款";

			} else if (action.equals(Actions.ACTION_YIFUBAO)) {// 易付宝

				strTitle = "易付宝收款";
			} else if (action.equals(Actions.ACTION_BAIDU)) {// 百度钱包

				strTitle = "百付宝收款";
			}
		}
	}

	@Override
	public void onClick(View arg0) {
		// TODO Auto-generated method stub
		switch (arg0.getId()) {
		case R.id.rl_sk_code:// 收款码
			Intent intent = new Intent(CollectionCodeActivity.this, CreatePayCodeActivity.class);
//			Log.i("result","-----------action1--------"+action);
			intent.setAction(action);
			intent.putExtra("account", account);
			startActivity(intent);
			break;
		case R.id.rl_sk_saosao:// 扫一扫收款
			Intent intent2 = new Intent(CollectionCodeActivity.this, CreatePaytCodeAccountActivity.class);
//			Log.i("result","-----------action2--------"+action);
			intent2.setAction(action);
			intent2.putExtra("type", true);
			startActivity(intent2);
			break;

		default:
			break;
		}
	}

}
