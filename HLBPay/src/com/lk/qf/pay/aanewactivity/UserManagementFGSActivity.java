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

public class UserManagementFGSActivity extends BaseActivity implements
		OnClickListener {

	private TextView tvGoldUserNum, tvSilverNum, tvOrdinaryNum;
	private String action = "";
	private CommonTitleBar title;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.user_management_fgs_layout);
		init();
	}

	private void init() {
		title = (CommonTitleBar) findViewById(R.id.titlebar_userManagement_fgs);
		title.setActName("用户管理");
		title.setCanClickDestory(this, true);
		tvGoldUserNum = (TextView) findViewById(R.id.tv_gold_user);
		tvSilverNum = (TextView) findViewById(R.id.tv_silver_user);
		tvOrdinaryNum = (TextView) findViewById(R.id.tv_ordinary_user);
		queryIncome();
	}

	@Override
	public void onClick(View arg0) {
		// TODO Auto-generated method stub
	}
	
	/**
	 * 用户管理 用户个数
	 */
	private void queryIncome() {

		RequestParams params = new RequestParams();
		HashMap<String, String> map = new HashMap<String, String>();

		map.put("username", MApplication.mSharedPref
				.getSharePrefString(SharedPrefConstant.USERNAME));
		map.put("accsort", "fen");

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
					}

					@Override
					public void onSuccess(ResponseInfo<String> response) {
						// TODO Auto-generated method stub

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
							tvGoldUserNum.setText(obj.optString("jin"));
							tvSilverNum.setText(obj.optString("yin"));
							tvOrdinaryNum.setText(obj.optString("pu"));
						} else {
							T.ss(message);
						}
					}
				});
	}

}
