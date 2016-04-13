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
import com.lk.qf.pay.utils.ExpresssoinValidateUtil;
import com.lk.qf.pay.utils.MyMdFivePassword;
import com.lk.qf.pay.wedget.CommonTitleBar;
import com.lk.qf.pay.wedget.view.MyXEdittextView;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.InputType;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.Toast;

/**
 * 修改密码
 * 
 * @author Ding
 * 
 */
public class PwdReviseActivity extends BaseActivity implements OnClickListener {

	private EditText oldPwdEdit, newPwdEdit, newPwdAEdit;
	private MyXEdittextView phoneNumEdit;
	// private Button btnGetCode;
	/**
	 * 修改登录密码
	 */
	public static final String ACTION_REVISE_LOGIN_PWD = "1";

	/**
	 * 修改支付密码
	 */
	public static final String ACTION_REVISE_PAY_PWD = "2";
	private String action, oldPwd, newPwd, rePwd, phoneNum, smsCode, type;
	private CommonTitleBar title;

	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		setContentView(R.layout.revise_pwd);
		action = getIntent().getAction();
		if (null == action) {
			throw new NullPointerException("修改密码类型为空[PwdReviseActivity.class]");
		}
		initView();
	}

	private void initView() {
		// btnGetCode = (Button) findViewById(R.id.btn_change_verify_getverify);
		// btnGetCode.setOnClickListener(this);
		phoneNumEdit = (MyXEdittextView) findViewById(R.id.et_change_verify_phone);
		// smsCodeEdit = (EditText)
		// findViewById(R.id.et_change_verify_phoneverify);
		oldPwdEdit = (EditText) findViewById(R.id.old_pwd_edit);
		newPwdEdit = (EditText) findViewById(R.id.new_pwd_edit);
		newPwdAEdit = (EditText) findViewById(R.id.new_pwda_edit);
		this.findViewById(R.id.revise_pwd_btn).setOnClickListener(this);
		title = (CommonTitleBar) findViewById(R.id.titlebar_revisepwd);
		title.setCanClickDestory(this, true);

		phoneNum = MApplication.mSharedPref
				.getSharePrefString(SharedPrefConstant.USERNAME);
		if (action.equals(ACTION_REVISE_LOGIN_PWD)) {
			title.setActName("修改登录密码");
			phoneNumEdit.setText(phoneNum);
			phoneNumEdit.setEnabled(false);
		} else {
			title.setActName("修改支付密码");
			InputFilter[] filters = { new InputFilter.LengthFilter(6) };
			phoneNumEdit.setText(phoneNum);
			phoneNumEdit.setEnabled(false);
			oldPwdEdit.setHint("请输入原6位数的密码");
			oldPwdEdit.setInputType(InputType.TYPE_CLASS_NUMBER);
			oldPwdEdit.setFilters(filters);
			oldPwdEdit.setTransformationMethod(PasswordTransformationMethod
					.getInstance());
			newPwdEdit.setHint("请输入新6位数的密码");
			newPwdEdit.setInputType(InputType.TYPE_CLASS_NUMBER);
			newPwdEdit.setFilters(filters);
			newPwdEdit.setTransformationMethod(PasswordTransformationMethod
					.getInstance());
			newPwdAEdit.setInputType(InputType.TYPE_CLASS_NUMBER);
			newPwdAEdit.setFilters(filters);
			newPwdAEdit.setTransformationMethod(PasswordTransformationMethod
					.getInstance());
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_back:
			finish();
			break;
		// case R.id.btn_change_verify_getverify:
		//
		// getVerifyCode();
		// break;
		case R.id.revise_pwd_btn:
//			revisePwd();
			getData();
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
		// smsCode = smsCodeEdit.getText().toString();
		if (phoneNum == null || (phoneNum != null && phoneNum.equals(""))) {
			Toast.makeText(this, "请输入手机号码", Toast.LENGTH_SHORT).show();
			return;
		} else if (!ExpresssoinValidateUtil.isMobilePhone(phoneNum)) {
			T.ss("手机号码有误");
			return;
		}

		if (!phoneNum.equals(MApplication.mSharedPref
				.getSharePrefString(SharedPrefConstant.USERNAME))) {
			T.ss("手机号码有误");
			return;
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
						// btnGetCode.setText("重新发送");
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
							// btnGetCode.setText("已发送");
							// sms = new SmsCodeCount(60000, 1000);
							// sms.start();
							T.ss("已发送");
						} else {

							T.ss(message);
							// btnGetCode.setText("发送失败");
							// btnGetCode.setEnabled(true);
						}
					}
				});
	}

	private void getData() {
		phoneNum = phoneNumEdit.getNonSeparatorText();
		// smsCode = smsCodeEdit.getText().toString().trim();
		oldPwd = oldPwdEdit.getText().toString().trim();
		newPwd = newPwdEdit.getText().toString().trim();
		rePwd = newPwdAEdit.getText().toString().trim();
		// oldPwd=MD5Util.generatePassword(oldPwd);

		System.out.println("=====================>" + oldPwd);
		if (oldPwd.length() == 0) {
			T.ss("请输入原密码");
			return;
		} else if (oldPwd.length() < 6) {
			T.ss("密码最短为6位");
			return;
		} else if (newPwd.length() == 0) {
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
		
		revisePwd();
	}

	private void revisePwd() {
		
		HashMap<String, String> map = new HashMap<String, String>();
		map.put("username", phoneNum);
		map.put("pwd", MyMdFivePassword.MD5(MyMdFivePassword.MD5(oldPwd)));
		map.put("newpwd", MyMdFivePassword.MD5(MyMdFivePassword.MD5(newPwd)));
		map.put("strCaptcha", "");
		map.put("type", "1");

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
		if (action.equals(ACTION_REVISE_LOGIN_PWD)) {
			url = MyUrls.CHANGEPASSWORD;
		} else {
			url = MyUrls.T0ACTIVATION;
		}

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
						
							// if (action.equals(ACTION_REVISE_LOGIN_PWD)) {
							// T.ss("登录密码修改成功,请重新登录!");
							// Intent intent = new
							// Intent(PwdReviseActivity.this,
							// LoginActivity.class);
							// startActivity(intent);
							// finish();
							// }else{
								T.ss(message);
								finish();
//							}
						} else {

							T.ss(message);
						}
					}
				});
	}

	// private SmsCodeCount sms;

	/**
	 * @ClassName: SmsCodeCount
	 * @Description: 定义一个倒计时的内部类
	 * 
	 */
	// class SmsCodeCount extends CountDownTimer {
	//
	// /**
	// * Title:SmsCodeCount Description: 倒计时
	// *
	// * @param millisInFuture
	// * @param countDownInterval
	// */
	// public SmsCodeCount(long millisInFuture, long countDownInterval) {
	// super(millisInFuture, countDownInterval);
	// }
	//
	// @Override
	// public void onFinish() {
	// // btnGetCode.setText(getString(R.string.get_again));
	// // btnGetCode.setEnabled(true);
	// }
	//
	// @Override
	// public void onTick(long millisUntilFinished) {
	// // btnGetCode.setText(millisUntilFinished / 1000
	// // + getString(R.string.resume));
	// // btnGetVerify.setBackgroundColor(Color
	// // .parseColor(getString(R.color.btn_bg_grey)));
	// // btnGetCode.setEnabled(false);
	// }
	// }
}
