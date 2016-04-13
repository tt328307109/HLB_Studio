package com.lk.qf.pay.aanewactivity;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;

import org.apache.http.entity.StringEntity;
import org.json.JSONException;
import org.json.JSONObject;

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

public class UserInformationActivity extends BaseActivity implements
		OnClickListener {

	private TextView tvUserPhone, tvUserName, tvIdCard, tvBankCard;
	private String userPhone, userName, userIdCard, userBankCard;
	private CommonTitleBar title;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);

		setContentView(R.layout.userimformation_layout);
		userPhone = MApplication.mSharedPref
				.getSharePrefString(SharedPrefConstant.USERNAME);
		init();
		getUserInformation();
	}

	private void init() {
		tvUserPhone = (TextView) findViewById(R.id.tv_user_userPhone);
		tvUserName = (TextView) findViewById(R.id.tv_user_name);
		tvIdCard = (TextView) findViewById(R.id.tv_user_idCard);
		tvBankCard = (TextView) findViewById(R.id.tv_user_bankCard);
		tvUserPhone.setText(userPhone);
		title = (CommonTitleBar) findViewById(R.id.titlebar_user);
		title.setActName("用户信息");
		title.setCanClickDestory(this, true);
		Log.i("result", "-------------d----"+userPhone);
		tvUserPhone.setText(userPhone);
	}

	@Override
	public void onClick(View arg0) {
		// TODO Auto-generated method stub

	}

	/**
	 * 获取用户信息
	 */
	private void getUserInformation() {
		Log.i("result", "----dd-----------");

		RequestParams params = new RequestParams();
		HashMap<String, String> map = new HashMap<String, String>();

		map.put("username", userPhone);
		

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
		String url = MyUrls.MERVIEW;

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
						Log.i("result", "----ddd-----------" + str);
						String code = "";
						String message = "";
//						userPhone, userName, userIdCard, userBankCard
						JSONObject obj;
						try {
							obj = new JSONObject(str);
							code = obj.optString("CODE");
							message = obj.optString("MESSAGE");
							if (code.equals("00")) {
								userName = obj.optString("niname");
								userIdCard = obj.optString("mercard");
								userBankCard = obj.optString("bank");
								int idCardLength = userIdCard.length();
								int idBankLength = userBankCard.length();
								// 用于显示的加*身份证
								if (idCardLength>9) {
									
									userIdCard = userIdCard.substring(0, 3) + "*******" + userIdCard.substring(idCardLength-4);
								}
								
								Log.i("result", "----------d---------"+idBankLength);
								if (idBankLength>9) {
									
									userBankCard = userBankCard.substring(0, 3) + "******" + userBankCard.substring(idBankLength-4);
									Log.i("result", "----------d-是--------"+userBankCard);
								}else if (5<idBankLength && idBankLength<9) {
									
									userBankCard = userBankCard.substring(0, 2) + "****" + userBankCard.substring(idBankLength-1);
								}
								
								
								tvUserName.setText(userName);
								tvIdCard.setText(userIdCard);
								tvBankCard.setText(userBankCard);
							}else{
								T.ss(message);
							}

						} catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						
					}
				});
	}

}
