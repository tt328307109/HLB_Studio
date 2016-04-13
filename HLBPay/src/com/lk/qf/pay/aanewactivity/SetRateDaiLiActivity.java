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
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
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
import com.lk.qf.pay.wedget.CommonTitleBar;

public class SetRateDaiLiActivity extends BaseActivity implements OnClickListener,
		OnCheckedChangeListener {

	private EditText edRate1, edRate2, edRate3, edRate4, edRate5, edRate6;
	private String setRate1, setRate2, setRate3, setRate4, setRateJieSuan = "",
			setRateJieSuanTime = "", setRateJieSuanAccount = "";// 设置的费率
	private String rate1, rate2, rate3, rate4;// 获取的费率

	private TextView tvrateJieSuan;
	private RadioGroup rg;
	private CommonTitleBar title;
	private float[] rate;
	private float[] rateMax;
	private String feilvType = "max";// 费率方式
	private String userName="";
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);

		setContentView(R.layout.set_rate_layout);
		init();
	}

	private void init() {
		Intent intent = getIntent();
		if (intent!=null) {
			userName = intent.getStringExtra("userName");
		}
		edRate1 = (EditText) findViewById(R.id.ed_rate_1);
		edRate2 = (EditText) findViewById(R.id.ed_rate_2);
		edRate3 = (EditText) findViewById(R.id.ed_rate_3);
		edRate4 = (EditText) findViewById(R.id.ed_rate_4);
		rg = (RadioGroup) findViewById(R.id.rg_rate);
		rg.setOnCheckedChangeListener(this);
		findViewById(R.id.btn_rate_next).setOnClickListener(this);

		title = (CommonTitleBar) findViewById(R.id.titlebar_rate);
		title.setActName("设置费率");
		title.setCanClickDestory(this, true);
		getRate();
		getFengDingRate();
	}

	private void getData() {
		setRate1 = edRate1.getText().toString();
		setRate2 = edRate2.getText().toString();
		setRate3 = edRate3.getText().toString();
		setRate4 = edRate4.getText().toString();

		float setRate11;
		float setRate22;
		float setRate33;
		float setRate44;
		if (!setRate1.equals("") && !setRate2.equals("") && !setRate3.equals("")
				&& !setRate4.equals("")) {
			setRate11 = Float.parseFloat(setRate1);
			setRate22 = Float.parseFloat(setRate2);
			setRate33 = Float.parseFloat(setRate3);
			setRate44 = Float.parseFloat(setRate4);
			
		}else{
			T.ss("信息未输入完整");
			return;
			
		}

		if (rate!=null && rate.length!=0) {
			
			
			if (setRate11<0.22) {
				T.ss("民生类费率不能低于0.22");
				return;
			}
			if (setRate22<0.605) {
				T.ss("一般类费率不能低于0.605");
				return;
			}
			if (setRate33<1.05) {
				T.ss("餐娱类费率不能低于1.05");
				return;
			}
			if (setRate44<15) {
				T.ss("封顶费率不能低于15");
				return;
			}
		}

		setRate();
	}

	@Override
	public void onClick(View arg0) {
		// TODO Auto-generated method stub

		getData();
		
	}

	@Override
	public void onCheckedChanged(RadioGroup arg0, int checkID) {
		// TODO Auto-generated method stub
		switch (checkID) {
		case R.id.rbtn_rate_1:
			feilvType = "max";
			edRate4.setFocusableInTouchMode(true);
			break;
//		case R.id.rbtn_rate_2:
//
//			feilvType = "per";
//			edRate4.setFocusable(false);
//			break;

		default:
			break;
		}
	}

	/**
	 * 设置费率  O单 代理
	 */
	private void setRate() {

		RequestParams params = new RequestParams();
		String url = MyUrls.AGTAXADD;

		Map<String, String> map = new HashMap<String, String>();
		map.put("username", userName);
		map.put("postaxsort", feilvType);// 费率方式(max:费率+封顶,per:费率)
		map.put("postaxfen", "100");// 分润比例
		map.put("posagendsort", "time");// 结算方式
		map.put("posagendmon", "1");// 结算时间
		map.put("posagendmax", "");// 结算金额
		map.put("tax1", setRate1);// 费率1
		map.put("tax2", setRate2);
		map.put("tax3", setRate3);
		map.put("rate1", "" + rate[0]);
		map.put("rate2", "" + rate[1]);
		map.put("rate3", "" + rate[2]);
		map.put("max", "" + rateMax[0]);
		map.put("maxm", setRate4);
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
				T.ss("操作超时");
			}

			@Override
			public void onSuccess(ResponseInfo<String> response) {
				// TODO Auto-generated method stub
				String code = "";
				String message = "";
				String str = response.result;
				Log.i("result", "----设置成功----s-------" + str);
				try {
					JSONObject obj = new JSONObject(str);
					code = obj.optString("CODE");
					message = obj.optString("MESSAGE");
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				if (code.equals("00")) {
					T.ss("设置成功");
					Intent intent = new Intent(SetRateDaiLiActivity.this, AddDaiLiMerchantActivity.class);
					intent.putExtra("userName", userName);
					intent.setAction("AG");
					startActivity(intent);
				} else {
					T.ss(message);
				}
			}
		});

	}

	
	/**
	 * 获取费率
	 */
	private void getRate() {

		RequestParams params = new RequestParams();

		String url = MyUrls.AGTAXLIST;

		Map<String, String> map = new HashMap<String, String>();

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
				T.ss("操作超时");
				Log.i("result",
						"----费率----s获取失败-------" + arg0.getExceptionCode());
			}

			@Override
			public void onSuccess(ResponseInfo<String> response) {
				// TODO Auto-generated method stub
				String code = "";
				String message = "";
				String str = response.result;

				Log.i("result", "----费率----s-------" + str);
				try {
					JSONObject obj = new JSONObject(str);
					String strCount = obj.optString("count");
					int count = Integer.parseInt(strCount);
					// code = obj.optString("CODE");
					// message = obj.optString("MESSAGE");
					rate = new float[3];
					for (int i = 0; i < count; i++) {

						rate[i] = Float.parseFloat(obj.optJSONArray("date")
								.optJSONObject(i).optString("tax"));
					}
					edRate1.setHint("" + rate[0]);
					edRate2.setHint("" + rate[1]);
					edRate3.setHint("" + rate[2]);
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}
		});

	}

	/**
	 * 获取封顶费率
	 */
	private void getFengDingRate() {

		RequestParams params = new RequestParams();

		String url = MyUrls.AGMAXLIST;

		Map<String, String> map = new HashMap<String, String>();
		String json = JSON.toJSONString(map);
		Log.i("result", "----ddd----s--都-----" + json);
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
				T.ss("操作超时");
				Log.i("result",
						"----费率----s获取失败-------" + arg0.getExceptionCode());
			}

			@Override
			public void onSuccess(ResponseInfo<String> response) {
				// TODO Auto-generated method stub
				String code = "";
				String message = "";
				String str = response.result;

				Log.i("result", "----费率----s---s----" + str);
				try {
					JSONObject obj = new JSONObject(str);
					String strCount = obj.optString("count");
					int count = Integer.parseInt(strCount);
					// code = obj.optString("CODE");
					// message = obj.optString("MESSAGE");
					rateMax = new float[1];
					for (int i = 0; i < count; i++) {

						rateMax[i] = Float.parseFloat(obj.optJSONArray("date")
								.optJSONObject(i).optString("max"));
					}
					edRate4.setHint("" + rateMax[0]);
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});

	}
}
