package com.lk.qf.pay.aanewactivity;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

import org.apache.http.entity.StringEntity;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;
import com.lk.bhb.pay.R;
import com.lk.qf.pay.activity.BaseActivity;
import com.lk.qf.pay.golbal.MApplication;
import com.lk.qf.pay.golbal.MyUrls;
import com.lk.qf.pay.sharedpref.SharedPrefConstant;
import com.lk.qf.pay.tool.T;
import com.lk.qf.pay.utils.MyGetStatusUtils;
import com.lk.qf.pay.wedget.CommonTitleBar;

public class LiCaiActivity extends BaseActivity implements OnClickListener {

	private TextView tvYesterdayEarnings, tvAllEarnings, tvAllMoney, tvRate;// 昨日收益/总收益/总余额/年化收益
	private String yesterdayEarnings, allEarnings, allMoney,rate;// 昨日收益/总收益/我的总额
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.licai_layout);
		init();
	}

	private void init() {
		findViewById(R.id.licai_back).setOnClickListener(this);
		findViewById(R.id.btn_licai_chongzhi).setOnClickListener(this);
		findViewById(R.id.btn_licai_shuhui).setOnClickListener(this);
		findViewById(R.id.rl_licai_3).setOnClickListener(this);
		findViewById(R.id.ll_licai).setOnClickListener(this);
		
		tvYesterdayEarnings = (TextView) findViewById(R.id.tv_licai_shouyi);//昨日收益
		tvAllEarnings = (TextView) findViewById(R.id.tv_licai_leijishouyi);//收益
		tvAllMoney = (TextView) findViewById(R.id.tv_licai_wode_account);//总额
		tvRate = (TextView) findViewById(R.id.tv_licai_nianhua_sy);
		 //透明状态栏  
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);  
        //透明导航栏  
//        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);  
        findViewById(R.id.rl_licai_1).setPadding(0, MyGetStatusUtils.getStatusBarHeight(this), 0, 0);
		getUserAccount();
	}
	
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		getUserAccount();
		
	}

	@Override
	public void onClick(View arg0) {
		// TODO Auto-generated method stub
		switch (arg0.getId()) {
		case R.id.licai_back:
			finish();
			break;
		case R.id.btn_licai_chongzhi:
			Intent intent1 = new Intent(LiCaiActivity.this, TopUpActivity.class);
//			intent1.putExtra("allAccount", allMoney);
			startActivity(intent1);

			break;
		case R.id.btn_licai_shuhui:
			Intent intent2 = new Intent(LiCaiActivity.this,
					ShuhuiActivity.class);
			intent2.putExtra("allAccount", allMoney);
			startActivity(intent2);
			break;
		case R.id.rl_licai_3:

			Intent intent3 = new Intent(LiCaiActivity.this,
					LicaiListActivity.class);
			startActivity(intent3);
			break;
		case R.id.ll_licai:
			
			Intent intent4 = new Intent(LiCaiActivity.this,
					EarningsListActivity.class);
			startActivity(intent4);
			break;

		default:
			break;
		}
	}

	/**
	 * 获取数据
	 */
	private void getUserAccount() {

		RequestParams params = new RequestParams();
		String url = MyUrls.LICAI_DATA;
		showLoadingDialog();
		Map<String, String> map = new HashMap<String, String>();
		map.put("username", MApplication.mSharedPref
				.getSharePrefString(SharedPrefConstant.USERNAME));
		map.put("token", MApplication.mSharedPref
				.getSharePrefString(SharedPrefConstant.TOKEN));

		String json = JSON.toJSONString(map);
		Log.i("result", "----ddd----s-------" + json);
		try {
			StringEntity bodyEntity = new StringEntity(json, "UTF-8");
			params.setBodyEntity(bodyEntity);
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		HttpUtils utils = new HttpUtils();
		utils.send(HttpMethod.POST, url, params, new RequestCallBack<String>() {

			@Override
			public void onFailure(HttpException arg0, String arg1) {
				dismissLoadingDialog();
//				if (arg0.getExceptionCode() == 0) {
//					T.ss("请检查网络");
//				} else {
//
//					T.ss("获取数据失败" + arg0.getExceptionCode());
//				}
			}

			@Override
			public void onSuccess(ResponseInfo<String> response) {
				// TODO Auto-generated method stub
				String code = "";
				String message = "";

				String str = response.result;
				try {
					JSONObject obj = new JSONObject(str);
					code = obj.optString("CODE");
					message = obj.optString("MESSAGE");
					yesterdayEarnings = obj.optString("yestodayshouyi");
					allEarnings = obj.optString("totalshouyi");
					allMoney = obj.optString("totalmoney");
					rate = obj.optString("rate");

				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				if (code.equals("00")) {
//					double allAccount = Double.parseDouble(allMoney);
//					double yAccount= Double.parseDouble(yesterdayEarnings);
					tvYesterdayEarnings.setText(yesterdayEarnings);
					tvAllEarnings.setText(allEarnings);
					tvAllMoney.setText(allMoney);
					tvRate.setText(rate+"%");
				} else {
					T.ss(message);
				}
				dismissLoadingDialog();
			}
		});
	}

}
