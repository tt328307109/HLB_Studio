package com.lk.qf.pay.posloan;

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

public class PosloanConfirmActivity extends BaseActivity implements
		OnClickListener {

	private TextView tvAccount, tvDateLimit, tvInterest, tvReimbursementTime,
			tvReimbursementAccount;
	private CommonTitleBar title;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.confirm_posloan_layout);
		init();
	}

	private void init() {
		tvAccount = (TextView) findViewById(R.id.tv_posLoan_account_show);
		tvDateLimit = (TextView) findViewById(R.id.tv_posLoan_fenqi_show);
		tvInterest = (TextView) findViewById(R.id.tv_posLoan_lixi_show);
		tvReimbursementTime = (TextView) findViewById(R.id.tv_posLoan_huankuan_date);
		tvReimbursementAccount = (TextView) findViewById(R.id.tv_posLoan_huankuan_account);
		
		title = (CommonTitleBar) findViewById(R.id.titlebar_posLoan_confirm_agree);
		title.setActName("贷款");
		title.setCanClickDestory(this, true);
		findViewById(R.id.ibtn_posloan_confirm).setOnClickListener(this);
		findViewById(R.id.ibtn_posloan_refused).setOnClickListener(this);
	}

	@Override
	public void onClick(View arg0) {
		// TODO Auto-generated method stub
		switch (arg0.getId()) {
		case R.id.ibtn_posloan_confirm:

			break;
		case R.id.ibtn_posloan_refused:

			break;

		default:
			break;
		}
	}
	
	/**
	 * 获取分润贷款信息
	 */
	private void getMerPoslanMessage() {
		showLoadingDialog();
		RequestParams params = new RequestParams();

		String url = MyUrls.LOANADD;

		Map<String, String> map = new HashMap<String, String>();
		map.put("username", MApplication.mSharedPref
				.getSharePrefString(SharedPrefConstant.USERNAME));
		map.put("money", "");
		map.put("limit", "");// 期限
		map.put("bei1", "");// 贷款理由
		map.put("signature", "");// 签名
		map.put("state", "");// 状态
		map.put("type", "0");// 操作

		String json = JSON.toJSONString(map);
		Log.i("result", "----ddd----s--PosLoan-----" + json);
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
				dismissLoadingDialog();
			}

			@Override
			public void onSuccess(ResponseInfo<String> response) {
				// TODO Auto-generated method stub
				String code = "";
				String message = "";
				String str = response.result;
				dismissLoadingDialog();
				Log.i("result", "----PosLoan----s---s----" + str);
				try {
					JSONObject obj = new JSONObject(str);
					code = obj.optString("CODE");
					message = obj.optString("MESSAGE");
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				T.ss(message);
				
			}
		});

	}

}
