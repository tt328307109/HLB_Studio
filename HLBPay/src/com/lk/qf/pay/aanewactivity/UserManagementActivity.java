package com.lk.qf.pay.aanewactivity;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;

import org.apache.http.entity.StringEntity;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
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
import com.lk.qf.pay.wedget.CommonTitleBar;

public class UserManagementActivity extends BaseActivity implements
		OnClickListener {

	private TextView tvGoldUserNum1, tvGoldUserNum2, tvSilverNum1,
			tvSilverNum2, tvOrdinaryNum1, tvOrdinaryNum2;
	private String action = "";
	private CommonTitleBar title;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.user_management_layout);
		init();
	}

	private void init() {
		title = (CommonTitleBar) findViewById(R.id.titlebar_userManagement);
		title.setActName("用户管理");
		title.setCanClickDestory(this, true);
		tvGoldUserNum1 = (TextView) findViewById(R.id.tv_gold_user1);
		tvGoldUserNum2 = (TextView) findViewById(R.id.tv_gold_user2);
		tvSilverNum1 = (TextView) findViewById(R.id.tv_silver_user1);
		tvSilverNum2 = (TextView) findViewById(R.id.tv_silver_user2);
		tvOrdinaryNum1 = (TextView) findViewById(R.id.tv_ordinary_user1);
		tvOrdinaryNum2 = (TextView) findViewById(R.id.tv_ordinary_user2);
		findViewById(R.id.rl_upto_goldUser).setOnClickListener(this);
		findViewById(R.id.rl_upto_silverUser).setOnClickListener(this);
		queryIncome();
	}

	@Override
	public void onClick(View arg0) {
		// TODO Auto-generated method stub
		Intent intent = new Intent(UserManagementActivity.this,
				UpgradeUserActivity.class);
		switch (arg0.getId()) {
		case R.id.rl_upto_goldUser:
			action = "gold";
			break;
		case R.id.rl_upto_silverUser:
			action = "silver";
			break;

		default:
			break;
		}
		intent.setAction(action);
		startActivity(intent);
	}

	/**
	 * 用户管理 用户个数
	 */
	private void queryIncome() {
		showLoadingDialog();
		RequestParams params = new RequestParams();
		HashMap<String, String> map = new HashMap<String, String>();

		map.put("username", MApplication.mSharedPref
				.getSharePrefString(SharedPrefConstant.USERNAME));
		map.put("accsort", "mer");

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
		String url = MyUrls.MERLEVEL;

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
						JSONObject obj = null;
						try {
							obj = new JSONObject(str);
							code = obj.optString("CODE");
							message = obj.optString("MESSAGE");
						} catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						if (code.equals("00")) {
							tvGoldUserNum1.setText(obj.optString("jin"));
							tvGoldUserNum2.setText(obj.optString("jin2"));
							tvSilverNum1.setText(obj.optString("yin"));
							tvSilverNum2.setText(obj.optString("yin2"));
							tvOrdinaryNum1.setText(obj.optString("pu"));
							tvOrdinaryNum2.setText(obj.optString("pu2"));
						} else {
							T.ss(message);
						}
					}
				});
	}

}
