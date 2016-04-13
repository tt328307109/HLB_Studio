package com.lk.qf.pay.activity;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

import org.apache.http.Header;
import org.apache.http.entity.StringEntity;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;
import com.lk.bhb.pay.R;
import com.lk.pay.communication.AsyncHttpResponseHandler;
import com.lk.pay.communication.JsonHttpResponseHandler;
import com.lk.qf.pay.beans.BasicResponse;
import com.lk.qf.pay.golbal.MyUrls;
import com.lk.qf.pay.golbal.Urls;
import com.lk.qf.pay.tool.Logger;
import com.lk.qf.pay.tool.MyHttpClient;
import com.lk.qf.pay.tool.T;
import com.lk.qf.pay.utils.ExpresssoinValidateUtil;
import com.lk.qf.pay.wedget.CommonTitleBar;

/**
 * 获取手机验证码
 * @author Ding
 *
 */
public class MobileVerifyActivity extends BaseActivity implements
		OnClickListener {

	private Button btnNextStep, btnGetVerify;
	private EditText etPhone, etphoneVerify;
	private String userName, title;
	private boolean hasSend;
	PackageInfo pkg = null;
	private String oemName;
	private String code;
	private String message;

	/**
	 * 注册获取验证码
	 */
	public static final String ACTION_REGISTER = "0";
	/**
	 * 找回登录密码获取验证码
	 */
	public static final String ACTION_FORGET_LOGIN_PWD = "1";
	/**
	 * 找回支付密码获取验证码
	 */
	public static final String ACTION_FORGET_PAY_PWD = "2";

	private CommonTitleBar titleBar;
	private String action = "-1";

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.mobile_verify);
		action = getIntent().getAction();
		Log.i("result", "------2----------"+action);
		if (null == action) {
				throw new NullPointerException("获取手机验证码，请求类型不能为空");
		}
		initView();
	}

	private void initView() {
		titleBar = (CommonTitleBar) findViewById(R.id.titlebar_mobile_verify);
		if(action.equals(ACTION_REGISTER)){
			title = "获取验证码";
		} else if(action.equals(ACTION_FORGET_LOGIN_PWD)){
			title = "找回登录密码";
		} else if(action.equals(ACTION_FORGET_PAY_PWD)){
			title = "找回支付密码";
		}
		titleBar.setActName(title);
		titleBar.setCanClickDestory(MobileVerifyActivity.this, true);
		// btnBack = (Button) findViewById(R.id.btn_mobile_verify_back);
		// btnBack.setOnClickListener(this);
		btnNextStep = (Button) findViewById(R.id.btn_mobile_verify_next_step);
		btnNextStep.setOnClickListener(this);
		btnGetVerify = (Button) findViewById(R.id.btn_mobile_verify_getverify);
		btnGetVerify.setOnClickListener(this);

		etPhone = (EditText) findViewById(R.id.et_mobile_verify_phone);
		etPhone.addTextChangedListener(textWatcher);
		etphoneVerify = (EditText) findViewById(R.id.et_mobile_verify_phoneverify);
		findViewById(R.id.tv_mobile_verify_agree).setOnClickListener(this);
		try {
			pkg = getPackageManager().getPackageInfo(getApplication().getPackageName(), 0);
		} catch (NameNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}            
//		oemName = pkg.applicationInfo.loadLabel(getPackageManager()).toString();  
		oemName = getResources().getString(R.string.oemName);  
		
	}

	TextWatcher textWatcher = new TextWatcher() {

		@Override
		public void onTextChanged(CharSequence s, int start, int before,
				int count) {
			// int length = count + start;
			// if (length == 11) {
			// // UserNameStatusTask task = new UserNameStatusTask();
			// // task.execute(etPhone.getText().toString());
			// } else {
			// tvIsExist.setText("");
			// btnNextStep.setEnabled(false);
			// }

		}

		@Override
		public void beforeTextChanged(CharSequence s, int start, int count,
				int after) {

		}

		@Override
		public void afterTextChanged(Editable s) {

		}
	};

	@Override
	public void onClick(View v) {
		switch (v.getId()) {

		case R.id.btn_mobile_verify_getverify:
			getVerify();
			break;
		case R.id.btn_mobile_verify_next_step:
			nextStep(); // 下一步
			break;
		case R.id.tv_mobile_verify_agree:
			goProtocol();
			break;
		default:
			break;
		}

	}

	private void goProtocol() {
		Intent it = new Intent(MobileVerifyActivity.this, ProtocolActivity.class);
		startActivity(it);
	}

	private void getVerify() {
		mobile = etPhone.getText().toString();
		Log.i("result", "------1----------");
		userName = etPhone.getText().toString();
		if (userName == null || (userName != null && userName.equals(""))) {
			Toast.makeText(this, "请输入手机号码", Toast.LENGTH_SHORT).show();
			return;
		}else if(!ExpresssoinValidateUtil.isMobilePhone(userName)){
			T.ss("手机号码有误");
			return;
		}
		
		getVerifyCode();

	}

	String mobileVerify;

	private void nextStep() {
		if (!hasSend) {
			T.ss("请获取验证码后操作");
		}
		mobileVerify = etphoneVerify.getText().toString();
		if (mobileVerify == null
				|| (mobileVerify != null && mobileVerify.equals(""))) {
			Toast.makeText(this, "请输入手机验证码", Toast.LENGTH_SHORT).show();
			return;
		}else if(mobileVerify.length()<6){
			T.ss("验证码最少为6位");
			return;
		}
		checkVerifyCode();

	}

	private void gotoRegister() {
		Intent it = new Intent(MobileVerifyActivity.this,
				RegisterActivity.class);
		it.putExtra("userName", userName);
		startActivity(it);

	}

	private String mobile;

	/**
	 * 获取验证码
	 */
	private void getVerifyCode() {
		RequestParams params = new RequestParams();
		HashMap<String, String> map = new HashMap<String, String>();
		if (action.equals(ACTION_FORGET_LOGIN_PWD)) {
			map.put("smstyp", "01");
		} else if (action.equals(ACTION_FORGET_PAY_PWD)) {
			map.put("smstyp", "01");
		} else {
			map.put("smstyp", "00");
		}
		map.put("phonenum", userName);
		map.put("brand", "0001");
		map.put("signature", "00");
		map.put("biaozhi", "wuyou");
		
		String json = JSON.toJSONString(map);
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
						btnGetVerify.setText("重新发送");
						T.ss("操作超时");
					}

					@Override
					public void onSuccess(ResponseInfo<String> response) {
						// TODO Auto-generated method stub
						
						String str = response.result;
						
						try {
							JSONObject obj = new JSONObject(str);
							code = obj.optString("CODE");
							message = obj.optString("MESSAGE");

						} catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						
						if (code.equals("00")) {
							btnGetVerify.setText("已发送");
							sms = new SmsCodeCount(60000, 1000);
							sms.start();
							T.ss("已发送");
						}else{
							
							T.ss(message);
							btnGetVerify.setText("发送失败");
							btnGetVerify.setEnabled(true);
						}
					}
				});
	}
	
	
	private void checkVerifyCode() {
		HashMap<String, String> map = new HashMap<String, String>();
		map.put("custMobile", etPhone.getText().toString().trim());
		map.put("msgCode", etphoneVerify.getText().toString());
		if (action.equals(ACTION_FORGET_LOGIN_PWD)) {
			map.put("codeType", "02");
		} else if (action.equals(ACTION_FORGET_PAY_PWD)) {
			map.put("codeType", "03");
		} else {
			map.put("codeType", "01");
		}
		map.put("oemName", oemName);
		MyHttpClient.post(this, Urls.CHECK_VERIFY, map,
				new JsonHttpResponseHandler() {
					@Override
					public void onSuccess(int statusCode, Header[] headers,
							JSONObject response) {
						super.onSuccess(statusCode, headers, response);
						Logger.json(response.toString());
						try {
							JSONObject obj = response.getJSONObject("REP_BODY");
							if (obj.getString(
									com.lk.qf.pay.beans.Entity.RSP_COD).equals(
									com.lk.qf.pay.beans.Entity.RSP_SUCC)) {
								gotoNext();
								finish();
							} else {
								T.sl("" + obj.optString("RSPMSG"));
							}
						} catch (JSONException e) {
							e.printStackTrace();
						}

					}

					@Override
					public void onFailure(int statusCode, Header[] headers,
							String responseString, Throwable throwable) {
						super.onFailure(statusCode, headers, responseString,
								throwable);
						networkError(statusCode, throwable);
					}
					@Override
					public void onStart() {
						super.onStart();
						showLoadingDialog();
					}
					@Override
					public void onFinish() {
						super.onFinish();
						dismissLoadingDialog();
					}
				});

	}

	private void gotoNext() {
		if (action.equals(ACTION_REGISTER)) {
			startActivity(new Intent(this, RegisterActivity.class).putExtra(
					"mobile", mobile));
		} else if (action.equals(ACTION_FORGET_LOGIN_PWD)) {
			startActivity(new Intent(this, FindPwdActivity.class)
					.setAction(ACTION_FORGET_LOGIN_PWD)
					.putExtra("code", mobileVerify).putExtra("mobile", mobile));
		} else if (action.equals(ACTION_FORGET_PAY_PWD)) {
			startActivity(new Intent(this, FindPwdActivity.class)
					.setAction(ACTION_FORGET_PAY_PWD)
					.putExtra("code", mobileVerify).putExtra("mobile", mobile));
		}

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
			btnGetVerify.setText(getString(R.string.get_again));
			btnGetVerify.setEnabled(true);
		}

		@Override
		public void onTick(long millisUntilFinished) {
			btnGetVerify.setText(millisUntilFinished / 1000
					+ getString(R.string.resume));
			// btnGetVerify.setBackgroundColor(Color
			// .parseColor(getString(R.color.btn_bg_grey)));
			btnGetVerify.setEnabled(false);
		}
	}
}
