package com.lk.qf.pay.aanewactivity;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;

import org.apache.http.entity.StringEntity;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
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

public class ModifyXinyongkaActivity extends BaseActivity implements
		OnClickListener {

	private TextView tvName, tvCardNum;
	private TextView tvZdDay, tvHkDay;
	private String zdDay, hkDay, zdDay2="", hkDay2="", cardNum,name="";
	private CommonTitleBar title;
	private static final int REQUEST_ZHANGDAN_DATE = 0;// 账单日
	private static final int REQUEST_HUANKUAN_DATE = 1;// 还款日
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.xyk_modify_message_layout);
		init();
	}

	private void init() {
		
		tvName = (TextView) findViewById(R.id.tv_modif_xyk_userName1);
		tvCardNum = (TextView) findViewById(R.id.tv_modif_xyk_user_userPhone);
		tvZdDay = (TextView) findViewById(R.id.tv_modif_xyk_zdDay);
		tvHkDay = (TextView) findViewById(R.id.tv_modif_xyk_hkDay);
		tvZdDay.setOnClickListener(this);
		tvHkDay.setOnClickListener(this);
		title = (CommonTitleBar) findViewById(R.id.titlebar_xinyongka_modify);
		title.setActName("修改信用卡");
		title.setCanClickDestory(this, true);
		
		findViewById(R.id.btn_modif_xyk_queren).setOnClickListener(this);
		Intent intent = getIntent();

		if (intent != null) {
			cardNum = intent.getStringExtra("cardNum");
			tvCardNum.setText(cardNum.substring(0, 3) + "****"
					+ cardNum.substring(cardNum.length() - 4));
			zdDay = intent.getStringExtra("zdDate");
			hkDay = intent.getStringExtra("hkDate");
			name = intent.getStringExtra("name");
			tvName.setText(name);
			tvZdDay.setHint("每月" + zdDay + "日");
			tvHkDay.setHint(hkDay + "天");
		}
	}

	@Override
	public void onClick(View arg0) {
		// TODO Auto-generated method stub
		switch (arg0.getId()) {
		case R.id.tv_modif_xyk_zdDay:
			Intent intent = new Intent(ModifyXinyongkaActivity.this,
					NumberSelectorActivity.class);
			intent.putExtra("type", 0);
			startActivityForResult(intent, REQUEST_ZHANGDAN_DATE);
			break;
		case R.id.tv_modif_xyk_hkDay:
			Intent intent1 = new Intent(ModifyXinyongkaActivity.this,
					NumberSelectorActivity.class);
			intent1.putExtra("type", 1);
			startActivityForResult(intent1, REQUEST_HUANKUAN_DATE);
			break;
		case R.id.btn_modif_xyk_queren:
			
			addBankCard();
			break;

		default:
			break;
		}
	}
	
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);

		switch (requestCode) {
		case REQUEST_ZHANGDAN_DATE:
			if (resultCode == Activity.RESULT_OK) {

				zdDay2 = data.getStringExtra("dateNum").substring(0, data.getStringExtra("dateNum").length()-1);
				Log.i("result", "--------dddss----"+zdDay2);
				tvZdDay.setText("每月" + zdDay2+"日");
			}
			break;
		case REQUEST_HUANKUAN_DATE:
			if (resultCode == Activity.RESULT_OK) {

				hkDay2 = data.getStringExtra("dateNum").substring(0, data.getStringExtra("dateNum").length()-1);
				tvHkDay.setText(hkDay2+"天");
			}
			break;

		default:
			break;
		}
	}
	
	

	private void addBankCard() {
		if (zdDay2.equals("")) {
			zdDay2 = zdDay;
		}

		if (hkDay2.equals("")) {
			hkDay2 = hkDay;
		}
		
		showLoadingDialog();
		RequestParams params = new RequestParams();
		HashMap<String, String> map = new HashMap<String, String>();

		map.put("username", MApplication.mSharedPref
				.getSharePrefString(SharedPrefConstant.USERNAME));
		map.put("bankmoney", "");// 额度
		map.put("name", "");
		map.put("bank", "");
		map.put("logo", "");
		map.put("billmoney", "");// 账单金额
		map.put("bankcard", cardNum);
		map.put("billtime", zdDay2);
		map.put("reimtime", hkDay2);
		map.put("type", "1");// 0添加 1修改

		String json = JSON.toJSONString(map);
		Log.i("result", "----ddd--d---------" + json);
		try {
			StringEntity bodyEntity = new StringEntity(json, "UTF-8");
			params.setBodyEntity(bodyEntity);
		} catch (UnsupportedEncodingException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		HttpUtils httpUtils = new HttpUtils();
		String url = MyUrls.CREDITCARDADD;

		httpUtils.send(HttpMethod.POST, url, params,
				new RequestCallBack<String>() {

					@Override
					public void onFailure(HttpException arg0, String arg1) {
						// TODO Auto-generated method stub
						Log.i("result", "--------------failure------------");
						T.ss("操作超时");

					}

					@Override
					public void onSuccess(ResponseInfo<String> response) {
						// TODO Auto-generated method stub

						String str = response.result;
						String code = "";
						String message = "";
						Log.i("result", "----ddd-----------" + str);

						JSONObject obj;
						try {
							obj = new JSONObject(str);
							code = obj.optString("CODE");
							message = obj.optString("MESSAGE");
							if (code.equals("00")) {
								T.ss(message);
								finish();
							} else {
								T.ss(message);
							}

						} catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						dismissLoadingDialog();
					}
				});
	}

}
