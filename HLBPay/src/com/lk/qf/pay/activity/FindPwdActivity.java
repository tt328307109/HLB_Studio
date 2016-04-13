package com.lk.qf.pay.activity;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;

import org.apache.http.entity.StringEntity;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.InputFilter;
import android.text.InputType;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

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
import com.lk.qf.pay.utils.ExpresssoinValidateUtil;
import com.lk.qf.pay.utils.MyMdFivePassword;
import com.lk.qf.pay.wedget.CommonTitleBar;
import com.lk.qf.pay.wedget.view.MyXEdittextView;

/**
 * 找回密码
 * 
 * @author
 * 
 */
public class FindPwdActivity extends BaseActivity implements OnClickListener {

	private EditText newPwdEdit, newPwdAEdit, smsCodeEdit;
	private MyXEdittextView phoneNumEdit;
	private Button btnGetCode;
	/**
	 * 找回登录密码
	 */
	public static final String ACTION_FIND_LOGIN_PWD = "1";

	/**
	 * 找回支付密码
	 */
	public static final String ACTION_FIND_PAY_PWD = "2";
	/**
	 * 设置支付密码
	 */
	public static final String ACTION_SET_PAY_PWD = "3";
	private String action, newPwd, rePwd, smsCode, phoneNum;
	private CommonTitleBar title;
	private String type="1";

	@Override
	protected void onCreate(Bundle arg0) {

		super.onCreate(arg0);
		setContentView(R.layout.find_pwd);
		
		initView();
	}

	private void initView() {
		btnGetCode = (Button) findViewById(R.id.btn_vv_verify_getverify);
		btnGetCode.setOnClickListener(this);
		phoneNumEdit = (MyXEdittextView) findViewById(R.id.et_forget_verify_phone);
		smsCodeEdit = (EditText) findViewById(R.id.et_forget_verify_phoneverify);
		newPwdEdit = (EditText) findViewById(R.id.et_pw1);
		newPwdAEdit = (EditText) findViewById(R.id.et_pw2);
		this.findViewById(R.id.btn_next).setOnClickListener(this);
		title=(CommonTitleBar) findViewById(R.id.titlebar_findpwd);
		phoneNum = MApplication.mSharedPref
				.getSharePrefString(SharedPrefConstant.USERNAME);
		action = getIntent().getAction();
//		action = getIntent().getStringExtra("type");
		if (action.equals(ACTION_FIND_LOGIN_PWD)) {
			title.setActName("找回登录密码");
			Log.i("result", "-----------a----------");
			type = "0";
		}
		
		if (action.equals(ACTION_FIND_PAY_PWD)) {
			title.setActName("找回支付密码");
			InputFilter[] filters = {new InputFilter.LengthFilter(6)};
			newPwdEdit.setHint("请输入6位数的密码");
			newPwdEdit.setInputType(InputType.TYPE_CLASS_NUMBER);
			newPwdEdit.setFilters(filters);
			newPwdEdit.setTransformationMethod(PasswordTransformationMethod.getInstance());
			newPwdAEdit.setInputType(InputType.TYPE_CLASS_NUMBER);
			newPwdAEdit.setFilters(filters);
			newPwdAEdit.setTransformationMethod(PasswordTransformationMethod.getInstance());
			phoneNumEdit.setText(phoneNum);
			phoneNumEdit.setEnabled(false);
			type = "0";
		}
		if (action.equals(ACTION_SET_PAY_PWD)) {
			title.setActName("设置支付密码");
			InputFilter[] filters = {new InputFilter.LengthFilter(6)};
			newPwdEdit.setHint("请输入6位数的密码");
			newPwdEdit.setInputType(InputType.TYPE_CLASS_NUMBER);
			newPwdEdit.setFilters(filters);
			newPwdEdit.setTransformationMethod(PasswordTransformationMethod.getInstance());
			newPwdAEdit.setInputType(InputType.TYPE_CLASS_NUMBER);
			newPwdAEdit.setFilters(filters);
			newPwdAEdit.setTransformationMethod(PasswordTransformationMethod.getInstance());
			phoneNumEdit.setText(phoneNum);
			phoneNumEdit.setEnabled(false);
			type = "0";
		}
		title.setCanClickDestory(this, true);
		
		phoneNum=getIntent().getStringExtra("mobile");
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {

		case R.id.btn_next:
			revisePwd();
			break;
		case R.id.btn_vv_verify_getverify:
			getVerifyCode();

			break;

		default:
			break;
		}

	}

	/**
	 * 获取验证码
	 */
	private void getVerifyCode() {
		Log.i("result", "----dd-----------");
		phoneNum = phoneNumEdit.getNonSeparatorText();
		if (phoneNum == null || (phoneNum != null && phoneNum.equals(""))) {
			Toast.makeText(this, "请输入手机号码", Toast.LENGTH_SHORT).show();
			return;
		} else if (!ExpresssoinValidateUtil.isMobilePhone(phoneNum)) {
			Log.i("result", "----phonenum-----------"+phoneNum);
			T.ss("手机号码有误");
			return;
		}
		
		if (!action.equals(ACTION_FIND_LOGIN_PWD)) {
			if (!phoneNum.equals(MApplication.mSharedPref
					.getSharePrefString(SharedPrefConstant.USERNAME))) {
				T.ss("手机号码有误,与登录账号不一致");
				return;
			}
		}

		RequestParams params = new RequestParams();
		HashMap<String, String> map = new HashMap<String, String>();

		map.put("smstyp", "01");
		map.put("phonenum", phoneNum);
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

	private void revisePwd() {
		newPwd = newPwdEdit.getText().toString().trim();
		rePwd = newPwdAEdit.getText().toString().trim();
		smsCode = smsCodeEdit.getText().toString().trim();
		if (newPwd.length() == 0) {
			T.ss("请输入新密码");
			return;
		} else if (rePwd.length() == 0) {
			T.ss("请输入新密码");
			return;
		}
		if (newPwd.length() < 6 || rePwd.length() < 6) {
			T.ss("新密码长度最少为6位");
			return;
		}
		if (!newPwd.equals(rePwd)) {
			T.ss("两次输入的密码不一致");
			return;
		}
		if (smsCode.length() == 0) {
			T.ss("请输入验证码");
			return;
			
		}
		HashMap<String, String> map = new HashMap<String, String>();
		map.put("username", phoneNum);
		map.put("pwd", MyMdFivePassword.MD5(MyMdFivePassword.MD5(newPwd)));
		map.put("newpwd", MyMdFivePassword.MD5(MyMdFivePassword.MD5(newPwd)));
		map.put("strCaptcha", smsCode);
		map.put("type", type);

		RequestParams params = new RequestParams();
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
		String url = "";
		if (action.equals(ACTION_FIND_LOGIN_PWD)) {

			url = MyUrls.CHANGEPASSWORD;
		} else {

			url = MyUrls.T0ACTIVATION;
			
		}

		httpUtils.send(HttpMethod.POST, url, params,
				new RequestCallBack<String>() {

					@Override
					public void onFailure(HttpException arg0, String arg1) {
						// TODO Auto-generated method stub
						T.ss("操作超时" + arg0.getExceptionCode());
						dismissLoadingDialog();
					}

					@Override
					public void onSuccess(ResponseInfo<String> response) {
						// TODO Auto-generated method stub

						String str = response.result;
						String code = "";
						String message = "";
						Log.i("result", "----修改登录密码-----------" + str);
						try {
							JSONObject obj = new JSONObject(str);
							code = obj.optString("CODE");
							message = obj.optString("MESSAGE");

						} catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}

						if (code.equals("00")) {
							T.ss(message);
							finish();
						} else {

							T.ss(message);
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

}
