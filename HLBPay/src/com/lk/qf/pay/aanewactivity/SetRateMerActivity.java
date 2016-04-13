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

public class SetRateMerActivity extends BaseActivity implements
		OnClickListener, OnCheckedChangeListener {

	private EditText edRate1, edRate2;
	private String setRate1, setRate2, setRate3, setRate4;// 设置的费率

	private TextView tvrateJieSuan;
	private RadioGroup rg;
	private CommonTitleBar title;
	private float[] rate;
	private float[] rateMax;
	private String feilvType = "max";// 费率方式
	private String userName = "";
	private String mcc = "";
	private float setRate11,setRate22;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);

		setContentView(R.layout.set_rate_mer_layout);
		init();
	}

	private void init() {
		Intent intent = getIntent();
		if (intent != null) {
			userName = intent.getStringExtra("userName");
		}
		edRate1 = (EditText) findViewById(R.id.ed_mer_rate_1);
		edRate2 = (EditText) findViewById(R.id.ed_mer_rate_2);
		rg = (RadioGroup) findViewById(R.id.rg_rate_mer);
		rg.setOnCheckedChangeListener(this);
		findViewById(R.id.btn_mer_rate_next).setOnClickListener(this);

		title = (CommonTitleBar) findViewById(R.id.titlebar_rate_mer);
		title.setActName("设置商户费率");
		title.setCanClickDestory(this, true);
	}

	private void getData() {
		setRate1 = edRate1.getText().toString();
		setRate2 = edRate2.getText().toString();
		
		if (setRate1.equals("") || setRate2.equals("") ) {
			T.ss("信息未输入完整");
			
		}else{
			setRate11 = Float.parseFloat(setRate1);
			setRate22 = Float.parseFloat(setRate2);
			
			if (mcc.equals("5411")) {
				if (setRate11<0.38) {
					T.ss("民生类费率不低于0.38");
					return;
				}
			}
			if (mcc.equals("5947")) {
				if (setRate11<0.78) {
					T.ss("一般类费率不低于0.78");
					return;
				}
			}
			if (mcc.equals("5813")) {
				if (setRate11<1.25) {
					T.ss("餐娱类不低于1.25");
					return;
				}
			}
			if (mcc.equals("5072")) {
				if (setRate11<0.78) {
					T.ss("封顶费率不低于0.78");
					return;
				}
				if (setRate22<26) {
					T.ss("封顶费率不低于26");
				}
			}
			setMerRate();
		}

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
		case R.id.rbtn_rate_minsheng:
			mcc = "5411";
			edRate1.setHint("0.38");
			edRate2.setHint("");
			edRate2.setBackground(null);
			edRate2.setFocusable(false);
			break;
		case R.id.rbtn_rate_yiban:

			edRate1.setHint("0.78");
			edRate2.setHint("");
			edRate2.setBackground(null);
			edRate2.setFocusable(false);
			mcc = "5947";
			break;
		case R.id.rbtn_rate_canyin:
			mcc = "5813";
			edRate1.setHint("1.25");
			edRate2.setHint("");
			edRate2.setBackground(null);
			edRate2.setFocusable(false);
			break;
		case R.id.rbtn_rate_pifa:
			mcc = "5072";
			edRate1.setHint("0.78");
			edRate2.setHint("26(元)");
			edRate2.setFocusableInTouchMode(true);
			edRate2.setBackground(getResources().getDrawable(
					R.drawable.biankuang_dailixinxi_1));
			break;

		default:
			break;
		}
	}


	/**
	 * 设置费率 商户
	 */
	private void setMerRate() {

		RequestParams params = new RequestParams();
		String url = MyUrls.MERTAXADD;

		Map<String, String> map = new HashMap<String, String>();
		map.put("username", userName);
		map.put("mcc", mcc);// 费率方式(max:费率+封顶,per:费率)
		map.put("postaxrate", setRate1);// 费率
		map.put("postaxtop", setRate2);// 封顶
		map.put("autopospaymoney", "1");// 起结金额

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
					Intent intent = new Intent(SetRateMerActivity.this, AddDaiLiMerchantActivity.class);
					intent.putExtra("userName", userName);
					intent.setAction("MER");
					startActivity(intent);
				} else {
					T.ss(message);
				}
			}
		});

	}
}
