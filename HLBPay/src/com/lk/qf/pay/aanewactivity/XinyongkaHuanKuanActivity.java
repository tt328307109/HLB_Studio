package com.lk.qf.pay.aanewactivity;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import org.apache.http.entity.StringEntity;
import org.json.JSONException;
import org.json.JSONObject;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.CheckBox;
import android.widget.EditText;
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
import com.lk.qf.pay.utils.MyUtilss;
import com.lk.qf.pay.wedget.CommonTitleBar;
import com.lk.qf.pay.wedget.view.PassWdDialog;
import com.lk.qf.pay.wedget.view.PayListener;

public class XinyongkaHuanKuanActivity extends BaseActivity implements
		OnClickListener, PayListener {

	private EditText edHKAccount;
	private CheckBox cb;
	private String hkAccount;// 还款金额
	private String pwd;// 密码
	private String cardNum = "", decUse = "";// 卡号//最多能还
	private CommonTitleBar title;
	private PassWdDialog mPassWdDialog;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.huankuan_xyk_layout);
		init();
	}

	private void init() {
		edHKAccount = (EditText) findViewById(R.id.ed_xyk_huankuan_account);
		cb = (CheckBox) findViewById(R.id.cb_xyk_card_add_agree);
		title = (CommonTitleBar) findViewById(R.id.titlebar_xyk_huankuan);
		findViewById(R.id.btn_xyk_huankuan_queren).setOnClickListener(this);

		title.setActName("还款");
		title.setCanClickDestory(this, true);
		Intent intent = getIntent();
		if (intent != null) {
			cardNum = intent.getStringExtra("cardNum");
			decUse = intent.getStringExtra("decUse");
		}
		edHKAccount.setHint("最多能还" + decUse);
	}

	@Override
	public void onClick(View arg0) {
		// TODO Auto-generated method stub
		switch (arg0.getId()) {
		case R.id.btn_xyk_huankuan_queren:
			hkAccount = edHKAccount.getText().toString();
			if (hkAccount.equals("") || TextUtils.isEmpty(hkAccount)) {
				T.ss("请输入还款金额");
				return;
			}

			if (!cb.isChecked()) {
				T.ss("未授信借款协议");
				return;
			}
			if (MyUtilss.noPayYajin()) {
				T.ss("商户未缴纳押金");
				return;
			}
			mPassWdDialog = PassWdDialog.getInstance(
					XinyongkaHuanKuanActivity.this, hkAccount,
					PassWdDialog.YUAN_MARK);
			mPassWdDialog.setPayListener(XinyongkaHuanKuanActivity.this);
			mPassWdDialog.show();
			break;
		case R.id.btn_xyk_huankuan_weixin:
			T.ss("建设中");
			break;
		case R.id.btn_xyk_huankuan_zhifubao:

			T.ss("建设中");
			break;

		default:
			break;
		}
	}

	private void huankuanPay() {
		showLoadingDialog();
		RequestParams params = new RequestParams();
		HashMap<String, String> map = new HashMap<String, String>();

		map.put("username", MApplication.mSharedPref
				.getSharePrefString(SharedPrefConstant.USERNAME));
		map.put("pwd", MyMdFivePassword.MD5(MyMdFivePassword.MD5(pwd)));
		map.put("money", hkAccount);
		map.put("cardnum", cardNum);
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
		String url = MyUrls.CREDITCARDHUAN;

		httpUtils.send(HttpMethod.POST, url, params,
				new RequestCallBack<String>() {

					@Override
					public void onFailure(HttpException arg0, String arg1) {
						// TODO Auto-generated method stub
						Log.i("result", "--------------failure------------");
						T.ss("操作超时");
						dismissLoadingDialog();
					}

					@Override
					public void onSuccess(ResponseInfo<String> response) {
						// TODO Auto-generated method stub

						String str = response.result;
						String code = "";
						String message = "";
						Log.i("result", "----ddd-----------" + str);

						JSONObject obj;
						try {
							obj = new JSONObject(str);
							code = obj.optString("CODE");
							message = obj.optString("MESSAGE");
							if (code.equals("00")) {
								T.ss("还款成功");
								finish();
							} else {

								T.ss(message);
							}

						} catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						dismissLoadingDialog();
					}
				});
	}

	@Override
	public void sure(String password) {
		// TODO Auto-generated method stub
		mPassWdDialog.dismiss();
		mPassWdDialog = null;
		pwd = password;
		huankuanPay();
	}

	@Override
	public void cacel() {
		// TODO Auto-generated method stub
	}

	@Override
	public void close() {
		// TODO Auto-generated method stub
		
	}

}
