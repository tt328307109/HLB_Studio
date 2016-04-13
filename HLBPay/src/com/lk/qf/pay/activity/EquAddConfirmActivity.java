package com.lk.qf.pay.activity;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import org.apache.http.entity.StringEntity;
import org.json.JSONException;
import org.json.JSONObject;

import com.alibaba.fastjson.JSON;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;
import com.lk.bhb.pay.R;
import com.lk.qf.pay.golbal.MApplication;
import com.lk.qf.pay.golbal.MyUrls;
import com.lk.qf.pay.sharedpref.SharedPrefConstant;
import com.lk.qf.pay.tool.T;
import android.content.Context;
import android.media.AudioManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

public class EquAddConfirmActivity extends BaseActivity {

	private TextView ksnText;
	private String ksn, blueTootchAddress;
	private Context mContext;
	AudioManager localAudioManager;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		mContext = this;
		setContentView(R.layout.equ_add_confirm);
		ksn = getIntent().getStringExtra("ksn");
		blueTootchAddress = getIntent().getStringExtra("blueTootchAddress");
		ksnText = (TextView) findViewById(R.id.equ_add_ksn_text);
		ksnText.setText(ksn);
		localAudioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
		findViewById(R.id.btn_back).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				finish();
			}
		});

		findViewById(R.id.equ_add_confirm_btn).setOnClickListener(
				new OnClickListener() {

					@Override
					public void onClick(View v) {
						// addEqu();
						handPos();

					}
				});
	}

	/**
	 * 绑定机具
	 */
	private void handPos() {
		showLoadingDialog();
		RequestParams params = new RequestParams();
		HashMap<String, String> map = new HashMap<String, String>();

		map.put("username", MApplication.mSharedPref
				.getSharePrefString(SharedPrefConstant.USERNAME));
		map.put("strPosSN", ksn);

		String json = JSON.toJSONString(map);
		Log.i("result", "----ddd-----机具------" + json);
		try {
			StringEntity bodyEntity = new StringEntity(json, "UTF-8");
			params.setBodyEntity(bodyEntity);
		} catch (UnsupportedEncodingException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		HttpUtils httpUtils = new HttpUtils();
		String url = MyUrls.HANDPOS;

		httpUtils.send(HttpMethod.POST, url, params,
				new RequestCallBack<String>() {

					@Override
					public void onFailure(HttpException arg0, String arg1) {
						// TODO Auto-generated method stub
						dismissLoadingDialog();
						T.ss("操作超时");
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

						if (code.equals("00")) {
							MApplication.mSharedPref.putSharePrefString(
									"blueTootchAddress", blueTootchAddress);
							MApplication.mSharedPref.putSharePrefString(
									SharedPrefConstant.POSCOUNT, "1");// 绑定刷卡器数量

							T.ss("绑定成功");
							finish();
							// goPayYaJin();

						} else {
							T.ss(message);
						}
						dismissLoadingDialog();

					}
				});
	}
}
