package com.lk.qf.pay.aanewactivity;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

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
import com.lk.qf.pay.activity.BaseActivity;
import com.lk.qf.pay.golbal.MApplication;
import com.lk.qf.pay.golbal.MyUrls;
import com.lk.qf.pay.sharedpref.SharedPrefConstant;
import com.lk.qf.pay.tool.T;
import com.lk.qf.pay.wedget.CommonTitleBar;
import com.lk.qf.pay.wedget.view.MyXEdittextView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;

public class AddDaiLiMerchantActivity extends BaseActivity implements
		OnClickListener {

	private EditText edFarenName ;
	private MyXEdittextView edShenfenzheng;
	private String farenName, shenfenzheng;
	private String userName;
	private CommonTitleBar title;

	private String action;
	private String type;// 是注册跳转 还是实名认证跳转

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);

		setContentView(R.layout.add_daili_layout);
		init();
	}

	private void init() {
		Intent intent = getIntent();
		title = (CommonTitleBar) findViewById(R.id.titlebar_daili);
		if (intent != null) {
			userName = intent.getStringExtra("userName");
			type = intent.getStringExtra("type");
			action = intent.getAction();
			title.setActName("商户信息");
		} else {
			userName = MApplication.mSharedPref
					.getSharePrefString(SharedPrefConstant.USERNAME);
		}

		edFarenName = (EditText) findViewById(R.id.ed_daili_farenname);
		edShenfenzheng = (MyXEdittextView) findViewById(R.id.ed_daili_shenfenzheng);
		edShenfenzheng.setPatten(" ", MyXEdittextView.ID_CARD);
		findViewById(R.id.btn_daili_next).setOnClickListener(this);

		title.setCanClickDestory(this, true);
		// spAdd1.setAdapter(provinceAdapter);
	}

	@Override
	public void onClick(View arg0) {
		// TODO Auto-generated method stub
		getData();
		// setRate();
	}

	private void getData() {
		farenName = edFarenName.getText().toString();
		shenfenzheng = edShenfenzheng.getNonSeparatorText();

		if (farenName.equals("") || farenName == null) {
			T.ss("请输入姓名");
			return;
		}

		if (shenfenzheng.equals("") || shenfenzheng == null) {
			T.ss("请输入身份证号");
		} else if (shenfenzheng.length() == 18 || shenfenzheng.length() == 15) {
			setRate();
		} else {
			T.ss("请输入合法的身份证号");
		}
	}

	/**
	 * 商户企业信息
	 */
	private void setRate() {

		RequestParams params = new RequestParams();
		String url = MyUrls.MERENTERPRISEADD;
		showLoadingDialog();
		Map<String, String> map = new HashMap<String, String>();
		map.put("username", userName);
		map.put("comname", "");// 企业名
		map.put("comsort", "");// 企业类型
		map.put("addr", "");// 注册地址
		map.put("regmoney", "");// 注册资本
		map.put("comnum", "");// 营业执照编号
		map.put("farenname", farenName);// 姓名
		map.put("farenidnum", shenfenzheng);// 身份证
		map.put("farenaddr", "");// 法人身份证地址
		map.put("comfanwei", "");// 经营范围
		map.put("comendyear", "");// 营业期限
		map.put("orgnum", "");// 组织机构代码
		map.put("taxnum", "");// 税务登记证号
		map.put("dailipro", "");// 省
		map.put("dailicity", "");// 市
		map.put("dailicoun", "");// 区

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
				dismissLoadingDialog();
			}

			@Override
			public void onSuccess(ResponseInfo<String> response) {
				// TODO Auto-generated method stub
				String code = "";
				String message = "";
				String str = response.result;
				Log.i("result", "----设置成功----s-------" + str);
				try {
					JSONObject obj = new JSONObject(str);
					code = obj.optString("CODE");
					message = obj.optString("MESSAGE");
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				if (code.equals("00")) {
					T.ss("设置成功");
					MApplication.mSharedPref
					.putSharePrefString(SharedPrefConstant.MERENTERPRISERADD, "1");
					if (type != null && !type.equals("")) {
						if (type.equals("0")) {
							Intent intent = new Intent(
									AddDaiLiMerchantActivity.this,
									JieSuanActivity.class);
							intent.putExtra("userName", userName);
							intent.putExtra("type",type);
							startActivity(intent);
							Log.i("result", "--------------"+type);
							finish();
						} else {
							finish();
						}
					}

				} else {
					T.ss(message);
				}
				dismissLoadingDialog();
			}
		});

	}

}
