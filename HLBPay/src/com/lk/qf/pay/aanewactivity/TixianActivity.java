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
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

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
import com.lk.qf.pay.utils.MyMdFivePassword;
import com.lk.qf.pay.wedget.CommonTitleBar;

public class TixianActivity extends BaseActivity implements OnClickListener {

	private EditText edAccount, edStartTime, edEndTime, edPwd;
	private String account, pwd, startTime, endTime;
	private Button btnT0Tixian, btnT1Tixian;
	private Button btnQuery;
	private CommonTitleBar title;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);

		setContentView(R.layout.tixian_t0_t1_layout);

		init();
	}

	private void init() {

		edAccount = (EditText) findViewById(R.id.ed_tixian_account);
		edPwd = (EditText) findViewById(R.id.ed_tixian_pwd);
		edStartTime = (EditText) findViewById(R.id.ed_tixian_startTime);
		edEndTime = (EditText) findViewById(R.id.ed_tixian_endTime);
		btnT0Tixian = (Button) findViewById(R.id.btn_tixian_t00);
		btnT0Tixian.setOnClickListener(this);
		btnT1Tixian = (Button) findViewById(R.id.btn_tixian_t11);
		btnT1Tixian.setOnClickListener(this);
		btnQuery = (Button) findViewById(R.id.btn_tixian_query);
		btnQuery.setOnClickListener(this);
		title = (CommonTitleBar) findViewById(R.id.titlebar_tixian_title);
		title.setActName("提现");
		title.setCanClickDestory(this, true);
		getUser();
	}

	private void getData(int code) {
		account = edAccount.getText().toString();
		pwd = edPwd.getText().toString();
		startTime = edStartTime.getText().toString();
		endTime = edEndTime.getText().toString();

		if (account == null || account.equals("")) {
			T.ss("请输入提现金额");
			return;
		}
		if (pwd == null || pwd.equals("")) {
			T.ss("请输入提现密码");
			return;
		}
		sendTixian(code);
	}

	@Override
	public void onClick(View arg0) {
		// TODO Auto-generated method stub
		switch (arg0.getId()) {
		case R.id.btn_tixian_t00:
			getData(0);
			break;
		case R.id.btn_tixian_t11:

			getData(1);
			break;
		case R.id.btn_tixian_query:
			startTime = edStartTime.getText().toString();
			endTime = edEndTime.getText().toString();
			Intent intent = new Intent(TixianActivity.this,
					TiXianListActivity.class);
			intent.setAction("tixian");
			intent.putExtra("startTime", startTime);
			intent.putExtra("endTime", endTime);
			startActivity(intent);
			break;

		default:
			break;
		}
	}

	/**
	 * 提现
	 */
	private void sendTixian(int code) {

		RequestParams params = new RequestParams();
		String url = "";
		if (code == 0) {
			url = MyUrls.T0TIXIAN;
			// url = MyUrls.MINSHENGPAY;

		} else {

			url = MyUrls.T1TIXIAN;
		}
		showLoadingDialog();
		Map<String, String> map = new HashMap<String, String>();
		map.put("username", MApplication.mSharedPref
				.getSharePrefString(SharedPrefConstant.USERNAME));

		map.put("pwd", MyMdFivePassword.MD5(MyMdFivePassword.MD5(pwd)));
		map.put("money", account);
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
		Log.i("result", "----ddd----url-------" + url);
		utils.send(HttpMethod.POST, url, params, new RequestCallBack<String>() {

			@Override
			public void onFailure(HttpException arg0, String arg1) {
				dismissLoadingDialog();
				T.ss("操作超时");
			}

			@Override
			public void onSuccess(ResponseInfo<String> response) {
				// TODO Auto-generated method stub
				String code = "";
				String message = "";

				String str = response.result;
				Log.i("result", "----查询成功----s-------" + str);
				try {
					JSONObject obj = new JSONObject(str);
					code = obj.optString("CODE");
					message = obj.optString("MESSAGE");

				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				if (code.equals("00")) {
					edAccount.setText("");
					edPwd.setText("");
					getUser();
					// finish();
				}

				if (message.equals(getResources().getString(
						R.string.login_outtime))) {
					Intent intent = new Intent(TixianActivity.this,
							LoginActivity.class);
					startActivity(intent);
					finish();
				}
				T.ss(message);
				dismissLoadingDialog();

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
		// Log.i("result", "----ddd-----------" + json);
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
						// Log.i("result", "----ddd-----------" + str);

						JSONObject obj;
						try {
							obj = new JSONObject(str);
							code = obj.optString("CODE");
							message = obj.optString("MESSAGE");
							if (code.equals("00")) {
								edAccount.setHint("最多可提"
										+ obj.optString("posuse"));
							} else {
								T.ss(message);
							}
							// Log.i("result", "----message-----------" +
							// message);
							if (message.equals(getResources().getString(
									R.string.login_outtime))) {
								Intent intent = new Intent(TixianActivity.this,
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
