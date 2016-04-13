package com.lk.qf.pay.activity;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;
import org.apache.http.entity.StringEntity;
import org.json.JSONException;
import org.json.JSONObject;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout.LayoutParams;
import android.widget.PopupWindow;
import android.widget.TextView;
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

public class RegisterActivity extends BaseActivity implements OnClickListener {

	private EditText etUserPwd, etUserPwdAgain, etMerName,etphoneVerify;
	private MyXEdittextView etPhone,etAgCode;
	private TextView etprovince, etcity;
	private CommonTitleBar title;
	private Button btnNextStep, btnGetVerify;
	private String userName;// 商户名
	private String strCaptcha;// 验证码
	private String code, accsort="", acc_sort="", oemname="", agname="", niname="",agCode="";
	private String message;
	private CheckBox cb;

	// public LocationClient mLocationClient = null;
	// public BDLocationListener myListener = new MyLocationListener();
	private String city;
	private Context ctx;
	private static int APPID = 2;//0 组织  1代理  2商户

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.register);
		ctx = this;
		// mobile = getIntent().getExtras().getString("mobile");
		initView();
	}

	private void initView() {

		etUserPwd = (EditText) findViewById(R.id.et_register_user_pwd);
		etUserPwdAgain = (EditText) findViewById(R.id.et_register_user_pwd_again);
		findViewById(R.id.btn_register_confirm).setOnClickListener(this);
		title = (CommonTitleBar) findViewById(R.id.titlebar_register);
		title.setActName("注册");
		title.setCanClickDestory(this, true);

		btnGetVerify = (Button) findViewById(R.id.btn_regist_verify_getverify);
		btnGetVerify.setOnClickListener(this);

		etMerName = (EditText) findViewById(R.id.et_regist_merJianCheng);
		etPhone = (MyXEdittextView) findViewById(R.id.et_regist_verify_phone);
		etAgCode = (MyXEdittextView) findViewById(R.id.et_regist_yaoqingma);
		etphoneVerify = (EditText) findViewById(R.id.et_regist_verify_phoneverify);
		findViewById(R.id.tv_regist_verify_agree).setOnClickListener(this);
		cb = (CheckBox) findViewById(R.id.cb_regist_card_add_agree);

	}

	@Override
	public void onClick(View arg0) {
		// TODO Auto-generated method stub
		switch (arg0.getId()) {
		case R.id.btn_register_confirm:
			register();
			break;
		case R.id.btn_regist_verify_getverify:
			getVerifyCode();
			break;
		case R.id.tv_regist_verify_agree:
			Intent it = new Intent(RegisterActivity.this,
					ProtocolActivity.class);
			startActivity(it);
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
		userName = etPhone.getNonSeparatorText();
		if (userName == null || (userName != null && userName.equals(""))) {
			T.ss("请输入手机号码");
			return;
		} else if (!ExpresssoinValidateUtil.isMobilePhone(userName)) {
			T.ss("手机号码有误");
			return;
		}

		RequestParams params = new RequestParams();
		HashMap<String, String> map = new HashMap<String, String>();

		map.put("smstyp", "00");
		map.put("phonenum", userName);
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
						} else {

							T.ss(message);
							btnGetVerify.setText("发送失败");
							btnGetVerify.setEnabled(true);
						}
					}
				});
	}

	private void register() {

		String userPasswd = etUserPwd.getText().toString().trim();
		strCaptcha = etphoneVerify.getText().toString().trim();
		niname = etMerName.getText().toString().trim();
		agCode = etAgCode.getNonSeparatorText();
		
		if (userPasswd == null || (userPasswd != null && userPasswd.equals(""))) {
			T.ss("请输入登录密码");
			return;
		}
		String userPasswdAgain = etUserPwdAgain.getText().toString().trim();
		if (!userPasswd.equals(userPasswdAgain)) {
			T.ss("登录密码二次输入不一致");
			return;
		}
		if (!cb.isChecked()) {
			T.ss("未同意服务协议");
			return;
		}
		if (strCaptcha==null || (strCaptcha!=null &&strCaptcha.equals(""))) {
			T.ss("请输入验证码");
			return;
		}
		if (niname == null || niname.equals("")) {
			T.ss("请输入商户名称");
			return;
		}
		if (agCode == null || agCode.equals("")) {
			T.ss("请输入邀请码");
			return;
		}

		if (APPID == 0) {// O单
			accsort = "oem";
			acc_sort = "";
			oemname = MApplication.mSharedPref
					.getSharePrefString(SharedPrefConstant.USERNAME);
			agname = "";
		} else if (APPID == 1) {// 代理
			accsort = "ag";
			acc_sort = "";
			oemname = "";
			agname = MApplication.mSharedPref
					.getSharePrefString(SharedPrefConstant.USERNAME);

		} else if (APPID == 2) {// 商户
			accsort = "mer";
			acc_sort = "mpos";
			oemname = "";
			agname = "";
		}

		RequestParams params = new RequestParams();
		String url = MyUrls.MERADD;
		showLoadingDialog();
		Map<String, String> map = new HashMap<String, String>();
		map.put("username", userName);
		map.put("pwd", MyMdFivePassword.MD5(MyMdFivePassword.MD5(userPasswd)));
		map.put("strCaptcha", strCaptcha);
		map.put("niname", niname);
		map.put("realname", "");
		map.put("epayotherimg1", "");
		map.put("epayotherimg2", "0");
		map.put("epayotherimg3", "0");
		map.put("autopospaytax", "0");
		map.put("xiafatax", "0");
		map.put("tixiantax", "0.25");
		map.put("oemname", oemname);
		map.put("agname", agname);
		map.put("accsort", accsort);
		map.put("acc_sort", acc_sort);
		map.put("edit", "0");
		map.put("agcode", agCode);
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
				// String code = "";
				// String message = "";
				String str = response.result;
				Log.i("result", "----注册成功----s-------" + str);
				try {
					JSONObject obj = new JSONObject(str);
					code = obj.optString("CODE");
					message = obj.optString("MESSAGE");
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				T.ss(message);
				if (code.equals("00")) {

//					Intent intent = new Intent(RegisterActivity.this,
//							AddDaiLiMerchantActivity.class);
//					intent.setAction("MER");
//					intent.putExtra("userName", userName);
//					intent.putExtra("type", "0");
//					startActivity(intent);
					finish();
				}
				dismissLoadingDialog();
			}
		});

	}

	private void sh(Context ctx, String msg) {
		LayoutInflater inflater = LayoutInflater.from(ctx);
		View view = inflater.inflate(R.layout.network_error_popwindow, null);
		final PopupWindow pop = new PopupWindow(view,
				LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, false);
		Button btn = (Button) view.findViewById(R.id.btn_retry_pop);
		if (!TextUtils.isEmpty(msg)) {
			((TextView) (view.findViewById(R.id.tv_error_pop))).setText(msg);
		}
		pop.setBackgroundDrawable(new BitmapDrawable());
		pop.setOutsideTouchable(true);
		pop.setFocusable(true);
		btn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				pop.showAsDropDown(v);

			}
		});
	}

	private void gotoLogin() {
		Intent it = new Intent(this, LoginActivity.class);
		startActivity(it);
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
