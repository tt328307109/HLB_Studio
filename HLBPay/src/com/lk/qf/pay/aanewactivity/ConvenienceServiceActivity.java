package com.lk.qf.pay.aanewactivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebView;
import android.widget.ImageButton;
import com.lk.bhb.pay.R;
import com.lk.qf.pay.activity.BaseActivity;
import com.lk.qf.pay.tool.T;
import com.lk.qf.pay.wedget.CommonTitleBar;
import com.lk.qf.pay.wedget.webview.CommonLoadingComponent;

public class ConvenienceServiceActivity extends BaseActivity implements
		OnClickListener {

	// private ImageButton
	// btnPhoneTopUp,btnLifePay,btnAirTicket,btnWuliu,btnZhenxin,
	// btnCardPayments;
	private CommonTitleBar title;
	
	private String phoneCZ="https://wt.taobao.com/?spm=a21bo.7724922.8442.1.QBA58s&ks-menu=cz";//手机充值
	private String lifeJF="https://jiaofei.alipay.com/home/jiaofei.htm";//生活缴费
	private String wuliu="http://www.kuaidi100.com/";//物流查询
	private String airTicket="http://flight.qunar.com/?q=%E5%8E%BB%E5%93%AA%E5%84%BF%E7%BD%91%E6%9C%BA%E7%A5%A8%E6%9F%A5%E8%AF%A2%E7%BD%91&qId=10656187480581158608&ex_track=bd_zhixin_flight_fgn_title";
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);

		setContentView(R.layout.bianminfuwu_layout);
		init();
	}

	private void init() {
		findViewById(R.id.btn_bm_phoneCZ).setOnClickListener(this);
		findViewById(R.id.btn_bm_airTicket).setOnClickListener(this);
		findViewById(R.id.btn_bm_huankuan).setOnClickListener(this);
		findViewById(R.id.btn_bm_lifePay).setOnClickListener(this);
		findViewById(R.id.btn_bm_wuliu).setOnClickListener(this);
		findViewById(R.id.btn_bm_zhengxinchaxun).setOnClickListener(this);
		title = (CommonTitleBar) findViewById(R.id.titlebar_bianmin);
		title.setActName("便民服务");
		title.setCanClickDestory(this, true);
	}

	@Override
	public void onClick(View arg0) {
		// TODO Auto-generated method stub
		switch (arg0.getId()) {
		case R.id.btn_bm_huankuan:
			Intent intent1 = new Intent(ConvenienceServiceActivity.this,
					XinYongkaActivity.class);
//			intent1.setAction("SERVICE");
			startActivity(intent1);
			break;
		case R.id.btn_bm_phoneCZ:
//			T.ss("建设中");
			Intent intent2 = new Intent(ConvenienceServiceActivity.this,
					MyWebViewActivity.class);
			intent2.putExtra("webAddress", phoneCZ);
			intent2.putExtra("titleName", "充值");
			startActivity(intent2);
			break;
		case R.id.btn_bm_airTicket:

			Intent intent3 = new Intent(ConvenienceServiceActivity.this,
					MyWebViewActivity.class);
			intent3.putExtra("webAddress", airTicket);
			intent3.putExtra("titleName", "便民");
			startActivity(intent3);
			break;
		case R.id.btn_bm_lifePay:
			Intent intent4 = new Intent(ConvenienceServiceActivity.this,
					MyWebViewActivity.class);
			intent4.putExtra("webAddress", lifeJF);
			intent4.putExtra("titleName", "便民");
			Log.i("result", "---------dd-------s---------");
			startActivity(intent4);

			break;
		case R.id.btn_bm_wuliu:
			Intent intent5 = new Intent(ConvenienceServiceActivity.this,
					MyWebViewActivity.class);
			intent5.putExtra("webAddress", wuliu);
			intent5.putExtra("titleName", "便民");
			startActivity(intent5);
			break;
		case R.id.btn_bm_zhengxinchaxun:

			T.ss("建设中");
			break;

		default:
			break;
		}
	}

}
