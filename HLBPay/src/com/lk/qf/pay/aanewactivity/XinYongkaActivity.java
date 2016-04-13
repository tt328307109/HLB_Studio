package com.lk.qf.pay.aanewactivity;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;

import org.apache.http.entity.StringEntity;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
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
import com.lk.qf.pay.activity.LoginActivity;
import com.lk.qf.pay.golbal.MApplication;
import com.lk.qf.pay.golbal.MyUrls;
import com.lk.qf.pay.sharedpref.SharedPrefConstant;
import com.lk.qf.pay.tool.T;
import com.lk.qf.pay.utils.MyMdFivePassword;
import com.lk.qf.pay.wedget.CommonTitleBar;
import com.lk.qf.pay.wedget.view.PassWdDialog;
import com.lk.qf.pay.wedget.view.PayListener;

public class XinYongkaActivity extends BaseActivity implements OnClickListener,
		PayListener {

	private EditText edName, edCardNum1, edBank, edAccount, edPhone, edPwd,
			edYzCode;
	private String name, cardNum1, cardNum2, bankName, account, phone, pwd,
			topTitle, yzCode;
	private CommonTitleBar title;
	private Button btnGetCode;
	private PassWdDialog mPassWdDialog;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.xinyongka_huankuan_layout);
		init();
	}

	private void init() {
		topTitle = "信用卡还款";

		edName = (EditText) findViewById(R.id.ed_xinyong_name);
		edCardNum1 = (EditText) findViewById(R.id.ed_xinyong_cardNUm);
		// edCardNum2 = (EditText) findViewById(R.id.ed_xinyong_cardNUm2);
		edBank = (EditText) findViewById(R.id.ed_xinyong_bank);
		edAccount = (EditText) findViewById(R.id.ed_xinyong_account);
		edYzCode = (EditText) findViewById(R.id.ed_xinyongka_yzCode);
		btnGetCode = (Button) findViewById(R.id.btn_xinyongka_getCode);
		btnGetCode.setOnClickListener(this);
		// edPhone = (EditText) findViewById(R.id.ed_xinyong_phone);
		// edPwd = (EditText) findViewById(R.id.ed_xinyong_pwd);
		findViewById(R.id.btn_xinyong_t0).setOnClickListener(this);
		title = (CommonTitleBar) findViewById(R.id.titlebar_xinyong1);
		title.setActName(topTitle);
		title.setCanClickDestory(this, true);
	}

	private void getData() {
		name = edName.getText().toString();
		cardNum1 = edCardNum1.getText().toString();
		// cardNum2 = edCardNum2.getText().toString();
		bankName = edBank.getText().toString();
		account = edAccount.getText().toString();
		yzCode = edYzCode.getText().toString();

		// phone = edPhone.getText().toString();
		// pwd = edPwd.getText().toString();

		if (name.equals("") || name == null) {
			T.ss("请输入持卡人姓名");
			return;
		}
		if (cardNum1.equals("") || cardNum1 == null) {
			T.ss("请输入银行卡号");
			return;
		}
		if (bankName.equals("") || bankName == null) {
			T.ss("请输入开户银行");
			return;
		}
		if (account.equals("") || account == null) {
			T.ss("请输入转账金额");
			return;
		}
		if (yzCode.equals("") || yzCode == null) {
			T.ss("请输入验证码");
			return;
		}
		mPassWdDialog = PassWdDialog.getInstance(XinYongkaActivity.this,
				account, PassWdDialog.YUAN_MARK);
		mPassWdDialog.setPayListener(XinYongkaActivity.this);
		mPassWdDialog.show();

	}

	@Override
	public void onClick(View arg0) {
		// TODO Auto-generated method stub

		switch (arg0.getId()) {
		case R.id.btn_xinyongka_getCode:
			getVerifyCode();
			break;
		case R.id.btn_xinyong_t0:
			getData();
			break;

		default:
			break;
		}

	}

	private void transferAccount() {
		showLoadingDialog();
		Log.i("result", "----dd-----------");
		RequestParams params = new RequestParams();
		HashMap<String, String> map = new HashMap<String, String>();

		map.put("username", MApplication.mSharedPref
				.getSharePrefString(SharedPrefConstant.USERNAME));
		map.put("pwd", MyMdFivePassword.MD5(MyMdFivePassword.MD5(pwd)));
		map.put("phone", phone);
		map.put("money", account);
		map.put("cardname", name);
		map.put("bankname", bankName);
		map.put("bankname1", "");
		map.put("cardnum", cardNum1);
		map.put("banksort", "");
		map.put("strCaptcha", yzCode);
		map.put("paytyre", "credit");
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
		String url = MyUrls.SUPERTRANSFER;

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

						String str = response.result;
						String code = "";
						String message = "";
						Log.i("result", "----ddd-----------" + str);
						try {
							JSONObject obj = new JSONObject(str);
							message = obj.optString("MESSAGE");
							code = obj.optString("CODE");

						} catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}

						if (code.equals("00")) {
							T.ss(message);
							finish();
						} else {
							if (message.equals(getResources().getString(
									R.string.login_outtime))) {
								Intent intent = new Intent(
										XinYongkaActivity.this,
										LoginActivity.class);
								startActivity(intent);
								finish();
							}
							T.ss(message);
						}
						dismissLoadingDialog();
					}
				});
	}

	/**
	 * 获取验证码
	 */
	private void getVerifyCode() {

		RequestParams params = new RequestParams();
		HashMap<String, String> map = new HashMap<String, String>();

		map.put("smstyp", "00");
		map.put("phonenum", MApplication.mSharedPref
				.getSharePrefString(SharedPrefConstant.USERNAME));
		map.put("brand", "0001");
		map.put("signature", "00");
		map.put("biaozhi", "wuyou");

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
		String url = MyUrls.GETVERIFYCODE;

		httpUtils.send(HttpMethod.POST, url, params,
				new RequestCallBack<String>() {

					@Override
					public void onFailure(HttpException arg0, String arg1) {
						// TODO Auto-generated method stub
						btnGetCode.setText("重新发送");
						T.ss("操作超时");
					}

					@Override
					public void onSuccess(ResponseInfo<String> response) {
						// TODO Auto-generated method stub

						String str = response.result;
						String code = "";
						String message = "";
						try {
							JSONObject obj = new JSONObject(str);
							code = obj.optString("CODE");
							message = obj.optString("MESSAGE");

						} catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}

						if (code.equals("00")) {
							btnGetCode.setText("已发送");
							sms = new SmsCodeCount(60000, 1000);
							sms.start();
							T.ss("已发送");
						} else {

							T.ss(message);
							btnGetCode.setText("发送失败");
							btnGetCode.setEnabled(true);
						}
					}
				});
	}

	private SmsCodeCount sms;

	/**
	 * @ClassName: SmsCodeCount
	 * @Description: 定义一个倒计时的内部类
	 * 
	 */
	class SmsCodeCount extends CountDownTimer {

		/**
		 * Title:SmsCodeCount Description: 倒计时
		 * 
		 * @param millisInFuture
		 * @param countDownInterval
		 */
		public SmsCodeCount(long millisInFuture, long countDownInterval) {
			super(millisInFuture, countDownInterval);
		}

		@Override
		public void onFinish() {
			btnGetCode.setText(getString(R.string.get_again));
			btnGetCode.setEnabled(true);
		}

		@Override
		public void onTick(long millisUntilFinished) {
			btnGetCode.setText(millisUntilFinished / 1000
					+ getString(R.string.resume));
			// btnGetVerify.setBackgroundColor(Color
			// .parseColor(getString(R.color.btn_bg_grey)));
			btnGetCode.setEnabled(false);
		}
	}

	@Override
	public void sure(String password) {
		// TODO Auto-generated method stub
		mPassWdDialog.dismiss();
		mPassWdDialog = null;
		pwd = password;
		transferAccount();

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
