package com.lk.qf.pay.aanewactivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
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
import com.lk.qf.pay.golbal.MApplication;
import com.lk.qf.pay.golbal.MyUrls;
import com.lk.qf.pay.sharedpref.SharedPrefConstant;
import com.lk.qf.pay.tool.T;
import com.lk.qf.pay.utils.MyMdFivePassword;
import com.lk.qf.pay.wedget.CommonTitleBar;

import org.apache.http.entity.StringEntity;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

public class AddBasicDailiActivity extends BaseActivity implements
		OnClickListener {

	private EditText etUserNum, etUserPwd1, etUserPwd2, etMerName;
	private ImageButton next;
	private String userName, merName, userPwd1, userPwd2, tixian, accsort,
			acc_sort, oemname, agname;
	private CommonTitleBar title;
	// private RadioGroup rg;
	private static int APPID = 1;// 0为o单 1代理
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.add_daili_basic_layout);

		etUserNum = (EditText) findViewById(R.id.et_basic_loginNum_daili);
		etUserPwd1 = (EditText) findViewById(R.id.et_basic_loginPwd_daili);
		etUserPwd2 = (EditText) findViewById(R.id.et_basic_loginPwd2_daili);
		etMerName = (EditText) findViewById(R.id.et_basic_merJianCheng_daili);
		title = (CommonTitleBar) findViewById(R.id.titlebar_add_daili);
		title.setActName("基本信息");
		title.setCanClickDestory(this, true);

		next = (ImageButton) findViewById(R.id.btn_basic_next_daili);
		// rg = (RadioGroup) findViewById(R.id.rg_add_mer);
		// rg.setOnCheckedChangeListener(this);
		next.setOnClickListener(this);
	}

	@Override
	public void onClick(View arg0) {
		// TODO Auto-generated method stub
		switch (arg0.getId()) {
		case R.id.btn_basic_next_daili:
			userName = etUserNum.getText().toString();
			userPwd1 = etUserPwd1.getText().toString();
			userPwd2 = etUserPwd2.getText().toString();
			merName = etMerName.getText().toString();

			if (userName.equals("") || userPwd2.equals("")
					|| userPwd1.equals("") || merName.equals("")) {
				T.ss("信息未填完整");

			} else {
				if (userPwd1.equals(userPwd2)) {

					addMerchants();
				}else{
					T.ss("两次输入密码不一致");
				}
			}
			break;

		default:
			break;
		}
	}

	private void addMerchants() {

		if (APPID == 1) {// 代理
			accsort = "ag";
			acc_sort = "";
			// oemname = "";
			agname = MApplication.mSharedPref
					.getSharePrefString(SharedPrefConstant.USERNAME);

		} else if (APPID == 2) {// 商户
			accsort = "mer";
			acc_sort = "pos";
			// oemname = "";
			agname = "";
		}

		showLoadingDialog();

		RequestParams params = new RequestParams();
		String url = MyUrls.MERADD;
		oemname = MApplication.mSharedPref
				.getSharePrefString(SharedPrefConstant.USERNAME);// O单名
		Map<String, String> map = new HashMap<String, String>();
		map.put("username", userName);
		map.put("pwd", MyMdFivePassword.MD5(MyMdFivePassword.MD5(userPwd1)));
		map.put("strCaptcha", "");
		map.put("niname", merName);
		map.put("realname", "");
		map.put("epayotherimg1", "");
		map.put("epayotherimg2", "0");
		map.put("epayotherimg3", "0");
		map.put("autopospaytax", "0");
		map.put("xiafatax", "0");
		map.put("tixiantax", "40");
		map.put("oemname", oemname);
		map.put("agname", agname);
		map.put("accsort", accsort);
		map.put("acc_sort", acc_sort);
		map.put("edit", "0");
		String json = JSON.toJSONString(map);
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
				Log.i("result", "----添加代理成功----s-------" + str);
				try {
					JSONObject obj = new JSONObject(str);
					code = obj.optString("CODE");
					message = obj.optString("MESSAGE");
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				if (code.equals("00")) {
					T.ss("操作成功");
					Intent intent = null;

					intent = new Intent(AddBasicDailiActivity.this,
							SetRateDaiLiActivity.class);

					intent.putExtra("userName", userName);
					startActivity(intent);
				} else {
					T.ss(message);
				}
				dismissLoadingDialog();
			}
		});
	}

}
