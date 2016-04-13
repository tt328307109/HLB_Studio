package com.lk.qf.pay.activity;

import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

import org.apache.http.entity.StringEntity;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.res.Resources;
import android.graphics.drawable.AnimationDrawable;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
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
import com.lk.qf.pay.golbal.MApplication;
import com.lk.qf.pay.golbal.MyUrls;
import com.lk.qf.pay.golbal.Urls;
import com.lk.qf.pay.golbal.User;
import com.lk.qf.pay.sharedpref.SharedPrefConstant;
import com.lk.qf.pay.tool.MyHttpClient;
import com.lk.qf.pay.tool.SystemBarTintManager;
import com.lk.qf.pay.tool.T;
import com.lk.qf.pay.utils.ConnectionUtil;
import com.lk.qf.pay.utils.GetAppVersion;
import com.lk.qf.pay.utils.MyMdFivePassword;
import com.lk.qf.pay.utils.StringUtils;
import com.lk.qf.pay.wedget.XEditText;
import com.lk.qf.pay.wedget.view.MyXEdittextView;
import com.umeng.onlineconfig.OnlineConfigAgent;
import com.umeng.update.UmengDialogButtonListener;
import com.umeng.update.UmengUpdateAgent;
import com.umeng.update.UpdateStatus;

public class LoginActivity extends BaseActivity implements OnClickListener {

	private EditText userPwdEdit;
	private XEditText usernameEdit;
	private String username, userPwd, accsort, acc_sort;
	private TextView forgetPwdText, tvVersion;
	private ImageView logoImv;
	private AnimationDrawable animationDrawable;
	public static LoginActivity instance = null;
	private long exit = 0;
	private String code;
	private String message;
	private static int APPID = 2;// 0为o单 1代理 2为商户
	private GetAppVersion getAppVersion;
	public static boolean isForeground = false;

	@Override
	protected void onCreate(Bundle arg0) {

		super.onCreate(arg0);
		setContentView(R.layout.activity_login);
		registerMessageReceiver();
		judgeNet();
		init();

		// 版本高于4.4
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
			// //透明状态栏
			// getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
			setTranslucentStatus(true);

		}
		SystemBarTintManager tintManager = new SystemBarTintManager(this);
		tintManager.setStatusBarTintEnabled(true);
		tintManager.setNavigationBarTintEnabled(true);
		tintManager.setStatusBarTintResource(R.color.white);

	}

	/**
	 * 沉浸式状态栏utils
	 * 
	 * @param on
	 */
	@TargetApi(19)
	private void setTranslucentStatus(boolean on) {
		Window win = getWindow();
		WindowManager.LayoutParams winParams = win.getAttributes();
		final int bits = WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS;
		if (on) {
			winParams.flags |= bits;
		} else {
			winParams.flags &= ~bits;
		}
		win.setAttributes(winParams);
	}

	/**
	 * 判断网络是否开启
	 */
	private void judgeNet() {
		if (!ConnectionUtil.isConn(this)) {
			ConnectionUtil.setNetworkMethod(this);
		}
	}

	private void init() {
//		getver();
//		getBottomHeight();
//		Logger.init().hideThreadInfo().setLogLevel(LogLevel.FULL);
		usernameEdit = (XEditText) this
				.findViewById(R.id.et_login_username);
		SharedPreferences sp = this.getSharedPreferences("userName",
				Activity.MODE_PRIVATE);
		String name = sp.getString("username", "");
		boolean isChecked = sp.getBoolean("isRememberPassword", false);
		if (isChecked) {
			usernameEdit.setText(name);
		}
		userPwdEdit = (EditText) this.findViewById(R.id.et_login_pwd);
		tvVersion = (TextView) findViewById(R.id.tv_login_version);
		getAppVersion = new GetAppVersion(this);
		tvVersion.setText(getResources().getString(R.string.app_company1)
				+ getAppVersion.getVersion()
				+ getResources().getString(R.string.app_company2));

		findViewById(R.id.btn_login).setOnClickListener(this);
		findViewById(R.id.tv_forget_pwd).setOnClickListener(this);
		findViewById(R.id.login_register).setOnClickListener(this);
		logoImv = (ImageView) findViewById(R.id.login_logo_imv);
		// logoImv.setImageResource(R.drawable.app_logo_animation);
		// animationDrawable = (AnimationDrawable) logoImv.getDrawable();
		// animationDrawable.start();
		// UmengUpdateAgent.silentUpdate(this);//静默下载

		// usernameEdit.setOnClickListener(new OnClickListener() {
		//
		// @Override
		// public void onClick(View arg0) {
		// usernameEdit.setSelection(usernameEdit.getText().toString().length());
		// }
		// });
		prepare4UmengUpdate();
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_login:
			username = usernameEdit.getNonSeparatorText();
			userPwd = userPwdEdit.getText().toString().trim();

			if (TextUtils.isEmpty(username)) {
				T.ss("请输入登录手机号");
				return;
			}

			if (TextUtils.isEmpty(userPwd)) {
				T.ss("请输入登录密码");
				return;
			}

			login();
			break;
		case R.id.tv_forget_pwd:
			Intent intent = new Intent(LoginActivity.this,
					FindPwdActivity.class);
			intent.setAction(FindPwdActivity.ACTION_FIND_LOGIN_PWD);
			startActivity(intent);
//			getver();
			break;
		case R.id.login_register:
			// startActivity(new Intent(LoginActivity.this,
			// MobileVerifyActivity.class)
			// .setAction(MobileVerifyActivity.ACTION_REGISTER));
			startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
			break;
		default:
			break;
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
		// 更改环境
		Urls.initServer(MApplication.mApplicationContext.getSERVER_TYPE());
		// JPushInterface.onResume(LoginActivity.this);
		isForeground = true;
	}

	@Override
	protected void onPause() {
		super.onPause();
		// JPushInterface.onPause(LoginActivity.this);
		isForeground = false;
	}

	private void login() {
		showLoadingDialog();

		if (APPID == 0) {
			accsort = "oem";
			acc_sort = "";
		} else if (APPID == 1) {
			accsort = "ag";
			acc_sort = "";

		} else if (APPID == 2) {
			accsort = "mer";
			acc_sort = "mpos";
		}

		HashMap<String, String> map = new HashMap<String, String>();
		RequestParams params = new RequestParams();
		Date date = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
		String loginTime = sdf.format(date);

		map.put("phonenum", username);
		map.put("password", MyMdFivePassword.MD5(MyMdFivePassword.MD5(userPwd)));
		map.put("logintime", loginTime);
		map.put("brand", "0001");
		map.put("signature", "01");
		map.put("longitude", "");
		map.put("latitude", "");
		map.put("accsort", accsort);// oem mer ag
		map.put("acc_sort", acc_sort);

		String json = JSON.toJSONString(map);
		Log.i("result", "----------json---------" + json);
		try {
			StringEntity bodyEntity = new StringEntity(json, "UTF-8");
			params.setBodyEntity(bodyEntity);
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		HttpUtils utils = new HttpUtils();
		utils.send(HttpMethod.POST, MyUrls.LOGIN, params,
				new RequestCallBack<String>() {

					@Override
					public void onFailure(HttpException arg0, String arg1) {

						T.ss("操作超时");
						dismissLoadingDialog();
					}

					@Override
					public void onSuccess(ResponseInfo<String> response) {
						// TODO Auto-generated method stub

						String strReturnLogin = response.result;
						JSONObject obj = null;
						try {
							obj = new JSONObject(strReturnLogin);
						} catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						code = obj.optString("CODE");
						message = obj.optString("MESSAGE");
						Log.i("result", "----------strReturnLogin---------"
								+ strReturnLogin);

						if (code.equals("00")) {

							String[] phoneArry = new String[] { username };
							// JPushInterface.setTags(LoginActivity.this,
							// phoneArry, );
							MApplication.mSharedPref.putSharePrefString(
									SharedPrefConstant.USERNAME, username);// 商户电话账号
							Log.i("result", "----------strReturnLogin---------"
									+ username);
							MApplication.mSharedPref.putSharePrefString(
									SharedPrefConstant.PASSWORD,
									MyMdFivePassword.MD5(MyMdFivePassword
											.MD5(userPwd)));
							MApplication.mSharedPref.putSharePrefString(
									SharedPrefConstant.MERNAME,
									obj.optString("USERNAME"));// 商户名
							MApplication.mSharedPref.putSharePrefString(
									SharedPrefConstant.NINAME,
									obj.optString("USERNAME"));// 商户名
							MApplication.mSharedPref.putSharePrefString(
									SharedPrefConstant.TOKEN,
									obj.optString("TOKEN"));
							MApplication.mSharedPref.putSharePrefString(
									SharedPrefConstant.MERCHANTID,
									obj.optString("MERCHANTID"));// 商户id
							MApplication.mSharedPref.putSharePrefString(
									SharedPrefConstant.STATE,
									obj.optString("STATE"));
							MApplication.mSharedPref.putSharePrefString(
									SharedPrefConstant.POS_NOTEURL,
									obj.optString("pos_noteurl"));
							MApplication.mSharedPref.putSharePrefString(
									SharedPrefConstant.MERBANKADD,
									obj.optString("merBankAdd"));// 是否添加过银行卡信息
							MApplication.mSharedPref.putSharePrefString(
									SharedPrefConstant.MERENTERPRISERADD,
									obj.optString("merEnterpriserAdd"));// 是否添加企业信息
							MApplication.mSharedPref.putSharePrefString(
									SharedPrefConstant.MERCERTIFICATEADD,
									obj.optString("merCertificateAdd"));// 是否添加过证件照

							MApplication.mSharedPref.putSharePrefString(
									SharedPrefConstant.USESORT,
									obj.optString("usesort"));// 是否缴纳押金
							MApplication.mSharedPref.putSharePrefString(
									SharedPrefConstant.POSCOUNT,
									obj.optString("poscount"));// 绑定刷卡器数量
							MApplication.mSharedPref.putSharePrefString(
									SharedPrefConstant.AGLEVEL,
									obj.optString("aglevel"));// 用户级别
							MApplication.mSharedPref.putSharePrefString(
									SharedPrefConstant.ISPWD,
									obj.optString("ispwd"));// 是否设置支付密码
							MApplication.mSharedPref.putSharePrefString(
									SharedPrefConstant.ISPOSLOANPER,
									obj.optString("loanUp"));// 是否有权限申请pos贷
							MApplication.mSharedPref.putSharePrefString(
									SharedPrefConstant.LOANSTATE,
									obj.optString("loanState"));// 贷款订单状态
							MApplication.mSharedPref.putSharePrefString(
									SharedPrefConstant.LOANNOTE,
									obj.optString("loanNote"));// 贷款退回原因
							MApplication.mSharedPref.putSharePrefString(
									SharedPrefConstant.STITE,
									obj.optString("stite"));// 是否免押金
							MApplication.mSharedPref.putSharePrefString(
									SharedPrefConstant.ISSIGN,
									obj.optString("issign"));// 是否签到
							MApplication.mSharedPref.putSharePrefString(
									SharedPrefConstant.ISWIN,
									obj.optString("isWin"));// 是否中奖
							MApplication.mSharedPref.putSharePrefString(
									SharedPrefConstant.ISFIRSTININDIANA,
									obj.optString("xieyi"));// 是否第一次进入快夺宝
							MApplication.mSharedPref.putSharePrefBoolean(
									SharedPrefConstant.ISLOGIN, true);// 是否登录
							User.sign = obj.optString("TOKEN");
							User.login = true;

							saveUserName();
							startActivity(new Intent(LoginActivity.this,
									MenuActivity.class));
							finish();

						} else {
							T.ss(message);
						}
						dismissLoadingDialog();
					}
				});
	}

	private void saveUserName() {
		SharedPreferences sp = this.getSharedPreferences("userName",
				Activity.MODE_PRIVATE);
		Editor et = sp.edit();
		et.putString("username", username);
		et.putBoolean("isRememberPassword", true);
		et.commit();
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {

			long temp = System.currentTimeMillis();
			if ((temp - exit) < 2000) {
				MApplication.mSharedPref.putSharePrefBoolean(
						SharedPrefConstant.ISLOGIN, false);// 是否登录
				android.os.Process.killProcess(android.os.Process.myPid());
				LoginActivity.this.finish();
			} else {
				T.ss(getResources().getString(R.string.quit_app));
				exit = System.currentTimeMillis();
			}
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		MyHttpClient.cancleRequest(this);
		unregisterReceiver(mMessageReceiver);
	}

	private void prepare4UmengUpdate() {
		OnlineConfigAgent.getInstance().updateOnlineConfig(this);
		// 获取友盟在线参数
		String upgrade_mode = OnlineConfigAgent.getInstance().getConfigParams(
				this, "upgrade_mode");
		Log.i("result", "----------update_mode-------------=" + upgrade_mode);
		if (StringUtils.isEmpty(upgrade_mode)) {
			return;
		}
		String[] upgrade_mode_array = upgrade_mode.split(",");
		UmengUpdateAgent.setUpdateOnlyWifi(false);
		UmengUpdateAgent.update(LoginActivity.this);
		// UmengUpdateAgent.forceUpdate(LoginActivity.this);// 这行如果是强制更新就一定加上
		for (String mode : upgrade_mode_array) {
			int versionCode = getAppVersion.getVersionCode();
			Log.i("result", "----------String[]-------------=" + mode);
			// String upgradeCodeNew = mode;
			int upgradeCode = 0;
			if (mode != null && !mode.equals("")) {
				upgradeCode = Integer.parseInt(mode.substring(0,
						mode.length() - 1));
			}
			if (versionCode < upgradeCode && mode.endsWith("f")) {
				Log.i("result", "----------mode-------------=" + mode);
				UmengUpdateAgent.forceUpdate(LoginActivity.this);// 去掉忽略此版本
				UmengUpdateAgent
						.setDialogListener(new UmengDialogButtonListener() {
							@Override
							public void onClick(int status) {
								switch (status) {
								case UpdateStatus.Update:
									break;
								default:
									LoginActivity.this.finish();
								}
							}
						});
				return;
			} else {
				UmengUpdateAgent.update(this);
			}
		}
	}

	// for receive customer msg from jpush server
	private MessageReceiver mMessageReceiver;
	public static final String MESSAGE_RECEIVED_ACTION = "com.example.jpushdemo.MESSAGE_RECEIVED_ACTION";
	public static final String KEY_TITLE = "title";
	public static final String KEY_MESSAGE = "message";
	public static final String KEY_EXTRAS = "extras";

	public void registerMessageReceiver() {
		mMessageReceiver = new MessageReceiver();
		IntentFilter filter = new IntentFilter();
		filter.setPriority(IntentFilter.SYSTEM_HIGH_PRIORITY);
		filter.addAction(MESSAGE_RECEIVED_ACTION);
		registerReceiver(mMessageReceiver, filter);
	}

	public class MessageReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			if (MESSAGE_RECEIVED_ACTION.equals(intent.getAction())) {
				String messge = intent.getStringExtra(KEY_MESSAGE);
				String extras = intent.getStringExtra(KEY_EXTRAS);
				StringBuilder showMsg = new StringBuilder();
				showMsg.append(KEY_MESSAGE + " : " + messge + "\n");
				if (!(extras.length() < 1)) {
					showMsg.append(KEY_EXTRAS + " : " + extras + "\n");
				}
				setCostomMsg(showMsg.toString());
			}
		}
	}

	private void setCostomMsg(String msg) {
		Toast.makeText(LoginActivity.this, "----->setCostomMsg", 2000).show();
	}

	/**
	 * 获取底部菜单栏高度
	 */
	private void getBottomHeight() {
		Resources resources = this.getResources();
		int resourceId = resources.getIdentifier("navigation_bar_height",
				"dimen", "android");
		// int rid = resources.getIdentifier("config_showNavigationBar", "bool",
		// "android");
		if (resourceId > 0) {
			int h = resources.getDimensionPixelSize(resourceId);
			MApplication.mSharedPref.putSharePrefInteger(
					SharedPrefConstant.BOTTOMHEIGHT, h);//底部高度
			// Log.d("sam test",resources.getBoolean(rid) +""); //获取导航栏是否显示true
			// or false
			// Log.d("sam test",resources.getDimensionPixelSize(resourceId)
			// +""); //获取高度
		}
	}
	
	
	/**
	 * 测试 
	 */
	private void getver() {
		RequestParams params = new RequestParams();
		HashMap<String, String> map = new HashMap<String, String>();
		map.put("phonenum", "15923254686");
		map.put("brand", "0001");
		map.put("smstyp", "01");
		map.put("biaozhi", "wuyou");
		map.put("signature", "00");

		showLoadingDialog();
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
		String url = "http://219.146.70.110:258/app_ashx/User/Getverifycode.ashx";

		httpUtils.send(HttpMethod.POST, url, params,
				new RequestCallBack<String>() {

					@Override
					public void onFailure(HttpException arg0, String arg1) {
						// TODO Auto-generated method stub
						Log.i("result", "----ddd-----操作超时------" + arg0.getExceptionCode());
						dismissLoadingDialog();
					}

					@Override
					public void onSuccess(ResponseInfo<String> response) {
						// TODO Auto-generated method stub
						dismissLoadingDialog();
						String str = response.result;
						showDialog(str);
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
						if (code.endsWith("00")) {
						} else {
							T.ss(message);
						}
						//
					}
				});

	}
}
