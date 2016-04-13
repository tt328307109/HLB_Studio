package com.lk.qf.pay.indiana.activity;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

import org.apache.http.entity.StringEntity;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.CheckBox;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;
import com.lk.bhb.pay.R;
import com.lk.qf.pay.activity.BaseFragmentActivity;
import com.lk.qf.pay.activity.MenuActivity;
import com.lk.qf.pay.golbal.MApplication;
import com.lk.qf.pay.golbal.MyUrls;
import com.lk.qf.pay.sharedpref.SharedPrefConstant;
import com.lk.qf.pay.tool.T;

public class IndianaProtocolActivity extends Activity implements
		OnClickListener {

	private TextView contentText;
	private CheckBox cb;

	@Override
	protected void onCreate(Bundle arg0) {
		// TODO Auto-generated method stub
		super.onCreate(arg0);
		setContentView(R.layout.indiana_protocol_layout);
		contentText = (TextView) findViewById(R.id.tv_indiana_protocol_content);
		contentText.setText(getString(R.string.indiana_xieye));
		findViewById(R.id.btn_indiana_xieyi_agree).setOnClickListener(this);
		findViewById(R.id.img_indiana_close).setOnClickListener(this);
		cb = (CheckBox) findViewById(R.id.cb_indiana_xieyi_agree);
	}

	@Override
	public void onClick(View arg0) {
		// TODO Auto-generated method stub
		switch (arg0.getId()) {
		case R.id.btn_indiana_xieyi_agree:
			if (cb.isChecked()) {
				agreeXieyi();
			} else {
				T.ss("请阅读服务协议");
			}
			break;
		case R.id.img_indiana_close:
			finish();
			break;

		default:
			break;
		}
	}

	/**
	 * 同意服务协议
	 */
	private void agreeXieyi() {

		RequestParams params = new RequestParams();
		String url = MyUrls.ROOT_URL_INDIANA_XIEYI;
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
				T.ss("操作超时");
			}

			@Override
			public void onSuccess(ResponseInfo<String> response) {
				// TODO Auto-generated method stub
				String code = "";
				String message = "";
				String str = response.result;
				Log.i("result", "----同意成功----s-------" + str);
				try {
					JSONObject obj = new JSONObject(str);
					code = obj.optString("CODE");
					message = obj.optString("MESSAGE");
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				if (code.equals("00")) {
					MApplication.mSharedPref.putSharePrefString(
							SharedPrefConstant.ISFIRSTININDIANA, "1");
					Intent intent = new Intent(IndianaProtocolActivity.this,
							IndianaMainActivity.class);
					startActivity(intent);
					finish();
				} else {
					T.ss(message);
				}
			}
		});
	}
}
