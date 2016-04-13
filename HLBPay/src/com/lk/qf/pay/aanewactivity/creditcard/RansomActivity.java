package com.lk.qf.pay.aanewactivity.creditcard;

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
import com.lk.qf.pay.aanewactivity.FuzzyQueryActivity;
import com.lk.qf.pay.activity.BaseActivity;
import com.lk.qf.pay.beans.Xinyongkainfo;
import com.lk.qf.pay.golbal.MApplication;
import com.lk.qf.pay.golbal.MyUrls;
import com.lk.qf.pay.indiana.activity.EditAddressActivity;
import com.lk.qf.pay.sharedpref.SharedPrefConstant;
import com.lk.qf.pay.tool.T;
import com.lk.qf.pay.wedget.CommonTitleBar;

public class RansomActivity extends BaseActivity implements OnClickListener {
	private CommonTitleBar title;
	private Button commit, btn_et;
	private EditText et;
	private TextView tvConfirm;
	/**
	 * Edit可编辑的状态
	 */
	boolean state = false;
	Xinyongkainfo info;
	String addr = "", type = "";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_ransomactivity);
		Intent intent = getIntent();
		if (intent != null) {
			info = (Xinyongkainfo) intent.getParcelableExtra("info");
			type = info.getType();
			addr = info.getAddress();
		}

		init();
	}

	private void init() {
		title = (CommonTitleBar) findViewById(R.id.titlebar_apply_ransom);
		title.setActName("申请赎回");
		title.setCanClickDestory(this, true);
		btn_et = (Button) findViewById(R.id.btn_et);
		btn_et.setOnClickListener(this);
		et = (EditText) findViewById(R.id.tv_apply_ransom);
		et.setEnabled(false);
		commit = (Button) findViewById(R.id.btn_apply_ransom);
		commit.setOnClickListener(this);
//		et.setText(""+addr);
		tvConfirm = (TextView) findViewById(R.id.tv_creditCard_confirmGetCard);
		tvConfirm.setOnClickListener(this);
//		type = "0";
		if (!type.equals("0")) {
			tvConfirm.setClickable(false);
		}

		if(type.equals("0")) {
			et.setText(""+addr);
			btn_et.setVisibility(View.GONE);
			commit.setVisibility(View.GONE);
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_apply_ransom:
			if (!type.equals("0")) {
				RedeemCard();
			}
			break;
		case R.id.btn_et:
			state = !state;

			if (!type.equals("0")) {
				if (state) {
					et.setText("");
					et.setHint("请输入地址");
					et.setEnabled(true);
					btn_et.setText("保存");
					// "  保  存     "
					// " 修改地址  "
					commit.setVisibility(View.GONE);
					//选择省市
					Intent intent1 = new Intent(RansomActivity.this,
							FuzzyQueryActivity.class);
					intent1.putExtra("showType", "province");
					startActivityForResult(intent1,
							EditAddressActivity.REQUEST_PROVINCE);

				} else {
					et.setEnabled(false);
					btn_et.setText("修改");
					commit.setVisibility(View.VISIBLE);
				}
			} else {
				if (state) {
					et.setHint("请输入地址");
					et.setEnabled(true);
					btn_et.setText("保存");
					commit.setVisibility(View.GONE);
				} else {
					et.setEnabled(false);
					btn_et.setText("修改");
					commit.setVisibility(View.VISIBLE);
				}
			}
			break;
		case R.id.tv_creditCard_confirmGetCard:
			confirmGetCard();
			break;

		default:
			break;
		}
	}

	private void RedeemCard() {
		RequestParams params = new RequestParams();
		HashMap<String, String> map = new HashMap<String, String>();

		map.put("username", MApplication.mSharedPref
				.getSharePrefString(SharedPrefConstant.USERNAME));
		map.put("token", MApplication.mSharedPref
				.getSharePrefString(SharedPrefConstant.TOKEN));

		map.put("id", info.getId());
		if (et.getText().toString().length() < 1) {
			T.ss("请输入收卡地址");
			return;
		}
		map.put("get_address", et.getText().toString());
		showLoadingDialog();
		String json = JSON.toJSONString(map);
		Log.i("result", "----ddd-----------" + json);
		try {
			StringEntity bodyEntity = new StringEntity(json, "UTF-8");
			params.setBodyEntity(bodyEntity);
		} catch (UnsupportedEncodingException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		HttpUtils httpUtils = new HttpUtils();
		String url = MyUrls.ROOT_URL_PAYSH;
		Log.i("result", "----url-----------" + url);

		httpUtils.send(HttpMethod.POST, url, params,
				new RequestCallBack<String>() {

			@Override
			public void onFailure(HttpException arg0, String arg1) {
				// TODO Auto-generated method stub
				Log.i("result", "--------------failure------------");
				dismissLoadingDialog();
				T.ss("操作超时");
			}

			@Override
			public void onSuccess(ResponseInfo<String> response) {
				// TODO Auto-generated method stub
				dismissLoadingDialog();
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
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * 确认收货
	 */
	private void confirmGetCard() {
		RequestParams params = new RequestParams();
		HashMap<String, String> map = new HashMap<String, String>();
		map.put("username", MApplication.mSharedPref
				.getSharePrefString(SharedPrefConstant.USERNAME));
		map.put("token", MApplication.mSharedPref
				.getSharePrefString(SharedPrefConstant.TOKEN));
		map.put("cid", info.getId());

		showLoadingDialog();
		String json = JSON.toJSONString(map);
		Log.i("result", "----ddd-----------" + json);
		try {
			StringEntity bodyEntity = new StringEntity(json, "UTF-8");
			params.setBodyEntity(bodyEntity);
		} catch (UnsupportedEncodingException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		HttpUtils httpUtils = new HttpUtils();
		String url = MyUrls.ROOT_URL_CREDITCARDAOG;
		httpUtils.send(HttpMethod.POST, url, params,
				new RequestCallBack<String>() {

			@Override
			public void onFailure(HttpException arg0, String arg1) {
				// TODO Auto-generated method stub
				T.ss("操作超时");
				dismissLoadingDialog();
			}

			@Override
			public void onSuccess(ResponseInfo<String> response) {
				// TODO Auto-generated method stub
				dismissLoadingDialog();
				String str = response.result;
				Log.i("result", "----ddd-----------" + str);
				String code = "";
				String message = "";

				JSONObject obj;
				try {
					obj = new JSONObject(str);
					code = obj.optString("CODE");
					message = obj.optString("MESSAGE");
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				T.ss(message);
				if (code.endsWith("00")) {
					Intent intent = new Intent(RansomActivity.this, CreditCardsListActivity.class);
					startActivity(intent);
					finish();
				}
			}
		});
	}

	String province = "北京市" , city = "市辖区" , provinceID = "110000";
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		switch (requestCode) {
		case EditAddressActivity.REQUEST_PROVINCE:
			if (resultCode == Activity.RESULT_OK) {
				province = data.getStringExtra("bankName");
				provinceID = data.getStringExtra("bankId");
				Intent intent2 = new Intent(RansomActivity.this,
						FuzzyQueryActivity.class);
				intent2.putExtra("showType", "city");
				intent2.putExtra("provinceID", provinceID);
				startActivityForResult(intent2, EditAddressActivity.REQUEST_CITY);
			}
			
			break;
		case EditAddressActivity.REQUEST_CITY:
			if (resultCode == Activity.RESULT_OK) {
				city = data.getStringExtra("bankName");
				et.setText(province + city);
				et.setSelection((province+ city).length());
			}
			break;
		}
	}
}
