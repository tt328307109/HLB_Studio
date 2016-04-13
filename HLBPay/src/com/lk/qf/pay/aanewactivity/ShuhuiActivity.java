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

public class ShuhuiActivity extends BaseActivity implements OnClickListener {

	private CommonTitleBar title;
	private List<String> listSpinner;
	private Spinner sp;
	private String type, account, allAccount;
	private EditText edAccount;
	private int typeId;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);

		setContentView(R.layout.shuhui_layout);
		init();
	}

	private void init() {
		title = (CommonTitleBar) findViewById(R.id.titlebar_shuhui);
		title.setActName("赎回");
		title.setCanClickDestory(this, true);
		sp = (Spinner) findViewById(R.id.sp_shuhui_xiala);
		edAccount = (EditText) findViewById(R.id.ed_shuhui_account);
		findViewById(R.id.btn_shuhui).setOnClickListener(this);
		findViewById(R.id.btn_shuhui_all).setOnClickListener(this);
		addSpinner();
		Intent intent = getIntent();
		allAccount = intent.getStringExtra("allAccount");
		edAccount.setHint("可赎回"+allAccount);
	}

	/**
	 * 添加 设置spinner
	 */
	private void addSpinner() {
		listSpinner = new ArrayList<String>();
		String cardNum = MApplication.mSharedPref
				.getSharePrefString(SharedPrefConstant.USER_BANK_NUM);
		// listSpinner.add(getResources().getString(R.string.shuhui_to));
		listSpinner.add(getResources().getString(R.string.shuhui_to_qianbao));
		Log.i("result", "-------cardNum------"+cardNum);
		if (cardNum.equals("")&& TextUtils.isEmpty(cardNum)) {
			
			listSpinner.add(getResources().getString(R.string.shuhui_to_bankCard));
		}else if(cardNum.length()>4){
			String num=cardNum.substring(cardNum.length()-3,cardNum.length());
			listSpinner.add(getResources().getString(R.string.shuhui_to_bankCard)+"("+num+")");
		}else{
			
			listSpinner.add(getResources().getString(R.string.shuhui_to_bankCard)+"("+cardNum+")");
		}

		ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_list_item_1, listSpinner);
		sp.setAdapter(arrayAdapter);

	}

	private void getData() {
		type = sp.getSelectedItem().toString();
		typeId = sp.getSelectedItemPosition() + 1;
		account = edAccount.getText().toString();
		Log.i("result", "------------s------");
		if (account.equals("") || TextUtils.isEmpty(account)) {
			T.ss("请输入完整信息");// 信息未输完
			return;
		}
	}

	@Override
	public void onClick(View arg0) {
		// TODO Auto-generated method stub

		switch (arg0.getId()) {
		case R.id.btn_shuhui:
			Log.i("result", "------------s------" + account);
			getData();
			shuhui(account);
			break;
		case R.id.btn_shuhui_all:
			shuhui(allAccount);
			break;

		default:
			break;
		}
	}

	/**
	 * 赎回
	 */
	private void shuhui(String money) {
		Log.i("result", "----dd-----------");

		RequestParams params = new RequestParams();
		HashMap<String, String> map = new HashMap<String, String>();

		map.put("username", MApplication.mSharedPref
				.getSharePrefString(SharedPrefConstant.USERNAME));
		map.put("money", money);
		map.put("type", "" + typeId);
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
							finish();
						}else{
							if (message.equals(getResources().getString(R.string.login_outtime))) {
								Intent intent = new Intent(ShuhuiActivity.this, LoginActivity.class);
								startActivity(intent);
								finish();
							}
						}
						T.ss(message);
						//
					}
				});
	}

}
