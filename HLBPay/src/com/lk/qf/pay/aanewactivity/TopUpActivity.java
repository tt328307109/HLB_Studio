package com.lk.qf.pay.aanewactivity;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.http.entity.StringEntity;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

import com.alibaba.fastjson.JSON;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;
import com.lk.bhb.pay.R;
import com.lk.qf.pay.activity.BaseActivity;
import com.lk.qf.pay.activity.LoginActivity;
import com.lk.qf.pay.golbal.MApplication;
import com.lk.qf.pay.golbal.MyUrls;
import com.lk.qf.pay.sharedpref.SharedPrefConstant;
import com.lk.qf.pay.tool.T;
import com.lk.qf.pay.wedget.CommonTitleBar;

public class TopUpActivity extends BaseActivity implements OnClickListener {

	private CommonTitleBar title;
	private List<String> listSpinner;
	// private Spinner sp;
	private String type, account;
	private EditText edAccount;
	private int typeId;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);

		setContentView(R.layout.chongzhi_layout);
		init();
	}

	private void init() {
//		Intent intent = getIntent();
//		if (intent!=null) {
//			allAccount = intent.getStringExtra("allAccount");
//		}
		title = (CommonTitleBar) findViewById(R.id.titlebar_ChongZhi);
		title.setActName("充值");
		title.setCanClickDestory(this, true);
		// sp = (Spinner) findViewById(R.id.sp_cz_xiala);
		edAccount = (EditText) findViewById(R.id.ed_cz_account);
		
		findViewById(R.id.btn_cz).setOnClickListener(this);
		getUser();
		// findViewById(R.id.btn_cz_all).setOnClickListener(this);
		// addSpinner();
	}

	// /**
	// * 添加 设置spinner
	// */
	// private void addSpinner() {
	// listSpinner = new ArrayList<String>();
	//
	// listSpinner.add(getResources().getString(R.string.shuhui_to));
	// listSpinner.add(getResources().getString(R.string.congzhi_to_qianbao));
	// listSpinner.add(getResources().getString(R.string.congzhi_to_bankCard));
	//
	// ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this,
	// android.R.layout.simple_list_item_1, listSpinner);
	// sp.setAdapter(arrayAdapter);
	//
	// }

	private void getData() {
		// type = sp.getSelectedItem().toString();
		// typeId = sp.getSelectedItemPosition()+1;
		account = edAccount.getText().toString();
		if (account.equals("") || TextUtils.isEmpty(account)) {
			T.ss("请输入完整信息");// 信息未输完
			return;
		}
	}

	@Override
	public void onClick(View arg0) {
		// TODO Auto-generated method stub

		switch (arg0.getId()) {
		case R.id.btn_cz:
			getData();
			chongZhi(account);
			break;

		default:
			break;
		}
	}

	/**
	 * 充值
	 */
	private void chongZhi(String money) {
		Log.i("result", "----dd-----------");
		showLoadingDialog();
		RequestParams params = new RequestParams();
		HashMap<String, String> map = new HashMap<String, String>();

		map.put("username", MApplication.mSharedPref
				.getSharePrefString(SharedPrefConstant.USERNAME));
		map.put("money", money);
		map.put("type", "0");
		map.put("token", MApplication.mSharedPref
				.getSharePrefString(SharedPrefConstant.TOKEN));

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
		String url = MyUrls.LICAI_IN_OUT;

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
						dismissLoadingDialog();
						if (code.equals("00")) {
							T.ss(message);

							finish();
						} else {
							T.ss(message);
							if (message.equals("登录超时，请重新登录!")) {
								Intent intent = new Intent(TopUpActivity.this,
										LoginActivity.class);
								startActivity(intent);
								finish();
							}
						}

					}
				});
	}
	
	
	private void getUser() {

		RequestParams params = new RequestParams();
		HashMap<String, String> map = new HashMap<String, String>();

		map.put("username", MApplication.mSharedPref
				.getSharePrefString(SharedPrefConstant.USERNAME));
		map.put("pwd", MApplication.mSharedPref
				.getSharePrefString(SharedPrefConstant.PASSWORD));
		map.put("token", MApplication.mSharedPref
				.getSharePrefString(SharedPrefConstant.TOKEN));

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
		String url = MyUrls.TXMONEY;

		httpUtils.send(HttpMethod.POST, url, params,
				new RequestCallBack<String>() {

					@Override
					public void onFailure(HttpException arg0, String arg1) {
						// TODO Auto-generated method stub

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
								edAccount.setHint("最多可充值"+obj.optString("posuse"));
								
							} else {
								T.ss(message);
							}
							// Log.i("result", "----message-----------" +
							// message);
							if (message.equals("登录超时，请重新登录!")) {
								Intent intent = new Intent(TopUpActivity.this,
										LoginActivity.class);
								startActivity(intent);
								finish();
							}
						} catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				});
	}

}
