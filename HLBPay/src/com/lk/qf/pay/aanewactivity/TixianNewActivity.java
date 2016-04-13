package com.lk.qf.pay.aanewactivity;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

import org.apache.http.entity.StringEntity;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
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
import com.lk.qf.pay.activity.LoginActivity;
import com.lk.qf.pay.golbal.MApplication;
import com.lk.qf.pay.golbal.MyUrls;
import com.lk.qf.pay.sharedpref.SharedPrefConstant;
import com.lk.qf.pay.tool.T;
import com.lk.qf.pay.utils.MyMdFivePassword;
import com.lk.qf.pay.utils.MyUtilss;
import com.lk.qf.pay.wedget.CommonTitleBar;
import com.lk.qf.pay.wedget.customdialog.AlertDialog;
import com.lk.qf.pay.wedget.view.PassWdDialog;
import com.lk.qf.pay.wedget.view.PayListener;

public class TixianNewActivity extends BaseActivity implements OnClickListener, PayListener {

	public static String T0TIXIAN = "t0_tixian";
	public static String T1TIXIAN = "t1_tixian";
	private String action;
	private CommonTitleBar title;
	private String url, pwd;
	private EditText edAccount;
	private String account;
	private Button btnOk;
	private TextView tvBeizhu1, tvBeizhu2, tvEDu, tvBeifujin;
	private PassWdDialog mPassWdDialog;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.tixian_account_pwd_layout);
		title = (CommonTitleBar) findViewById(R.id.titlebar_tixian_account);
		title.setCanClickDestory(this, true);
		tvBeizhu1 = (TextView) findViewById(R.id.tv_tixian_beizhu1);
		tvBeizhu2 = (TextView) findViewById(R.id.tv_tixian_beizhu2);
		findViewById(R.id.tv_apply_tixian_edu).setOnClickListener(this);
		tvEDu = (TextView) findViewById(R.id.tv_tixian_merxinyongdu);
		tvBeifujin = (TextView) findViewById(R.id.tv_tixian_beifujin_account);
		edAccount = (EditText) findViewById(R.id.ed_tixian_account_2);
		btnOk = (Button) findViewById(R.id.btn_tixian_qr);
		btnOk.setOnClickListener(this);
		Intent intent = getIntent();
		if (intent != null) {
			action = intent.getAction();
			if (action.equals(T0TIXIAN)) {
				title.setActName("T0提现");
				url = MyUrls.T0TIXIAN;
			} else {
				title.setActName("T1提现");
				url = MyUrls.T1TIXIAN;
				tvBeizhu1.setText("");
				tvBeizhu2.setText("");
			}
		}
		getUser();
//		isApplyEdu();
//		queryTixianEdu();
	}

	@Override
	public void onClick(View arg0) {
		// TODO Auto-generated method stub
		switch (arg0.getId()) {
		case R.id.tv_apply_tixian_edu:
			applyTixianEdu();
			break;
		case R.id.btn_tixian_qr:
			account = edAccount.getText().toString();
			if (account.equals("")) {
				T.ss("请输入提现金额");
				return;
			}
			if (MyUtilss.noPayYajin()) {
				T.ss("商户未缴纳押金");
				return;
			}
			mPassWdDialog = PassWdDialog.getInstance(TixianNewActivity.this ,
					account, "¥");
				mPassWdDialog.setPayListener(TixianNewActivity.this);
				mPassWdDialog.show();
			break;

		default:
			break;
		}
	}

	/**
	 * 提现
	 */
	private void sendTixian() {

		RequestParams params = new RequestParams();
		showLoadingDialog();
		Map<String, String> map = new HashMap<String, String>();
		map.put("username", MApplication.mSharedPref
				.getSharePrefString(SharedPrefConstant.USERNAME));
		map.put("pwd", MyMdFivePassword.MD5(MyMdFivePassword.MD5(pwd)));
		map.put("money", account);
		map.put("token", MApplication.mSharedPref
				.getSharePrefString(SharedPrefConstant.TOKEN));

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
		Log.i("result", "----ddd----url-------" + url);
		utils.send(HttpMethod.POST, url, params, new RequestCallBack<String>() {

			@Override
			public void onFailure(HttpException arg0, String arg1) {
				dismissLoadingDialog();
				T.ss("操作超时");
			}

			@Override
			public void onSuccess(ResponseInfo<String> response) {
				// TODO Auto-generated method stub
				String code = "";
				String message = "";

				String str = response.result;
				Log.i("result", "----查询成功----s-------" + str);
				try {
					JSONObject obj = new JSONObject(str);
					code = obj.optString("CODE");
					message = obj.optString("MESSAGE");

				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				if (code.equals("00")) {
					edAccount.setText("");
					getUser();
					// finish();
				}else if(code.equals("02")){
					isApplyEdu();
				}

				if (message.equals(getResources().getString(
						R.string.login_outtime))) {
					Intent intent = new Intent(TixianNewActivity.this,
							LoginActivity.class);
					startActivity(intent);
					finish();
				}
				T.ss(message);
				dismissLoadingDialog();
			}
		});
	}

	

	private void getUser() {

		RequestParams params = new RequestParams();
		HashMap<String, String> map = new HashMap<String, String>();

		map.put("username", MApplication.mSharedPref
				.getSharePrefString(SharedPrefConstant.USERNAME));
		map.put("pwd", MApplication.mSharedPref
				.getSharePrefString(SharedPrefConstant.PASSWORD));
		map.put("token", MApplication.mSharedPref
				.getSharePrefString(SharedPrefConstant.TOKEN));

		String json = JSON.toJSONString(map);
		// Log.i("result", "----ddd-----------" + json);
		try {
			StringEntity bodyEntity = new StringEntity(json, "UTF-8");
			params.setBodyEntity(bodyEntity);
		} catch (UnsupportedEncodingException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		HttpUtils httpUtils = new HttpUtils();
		String url = MyUrls.TXMONEY;

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
						String code = "";
						String message = "";
						Log.i("result", "----ddd-----------" + str);

						JSONObject obj;
						try {
							obj = new JSONObject(str);
							code = obj.optString("CODE");
							message = obj.optString("MESSAGE");
							if (code.equals("00")) {
								tvBeifujin.setText(obj.optString("zijinchi"));
								edAccount.setHint("最多可提"
										+ obj.optString("posuse"));
								tvEDu.setText(obj.optString("posuse"));
							}else{
								T.ss(message);
							}
							
							if (message.equals(getResources().getString(
									R.string.login_outtime))) {
								Intent intent = new Intent(
										TixianNewActivity.this,
										LoginActivity.class);
								startActivity(intent);
								finish();
							}
						} catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				});
	}
	
	/**
	 * 提示提现额度 不够
	 */
	private void isApplyEdu() {
		new AlertDialog(TixianNewActivity.this).builder().setTitle("提示")
				.setMsg("您现在提现额度不够,是否立即申请提额?")
				.setPositiveButton("申请提额", new OnClickListener() {
					@Override
					public void onClick(View v) {
						applyTixianEdu();
					}
				}).setNegativeButton("取消", new OnClickListener() {
					@Override
					public void onClick(View v) {
						
					}
				}).show();
	}

	/**
	 * 申请提现额度
	 */
	private void applyTixianEdu() {
		showLoadingDialog();
		RequestParams params = new RequestParams();
		HashMap<String, String> map = new HashMap<String, String>();
		map.put("username", MApplication.mSharedPref
				.getSharePrefString(SharedPrefConstant.USERNAME));
		map.put("token", MApplication.mSharedPref
				.getSharePrefString(SharedPrefConstant.TOKEN));

		String json = JSON.toJSONString(map);
		
		 Log.i("result", "----ddd-----s---阿---" + json);
		try {
			StringEntity bodyEntity = new StringEntity(json, "UTF-8");
			params.setBodyEntity(bodyEntity);
		} catch (UnsupportedEncodingException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		HttpUtils httpUtils = new HttpUtils();
		String url = MyUrls.TIXIANLIMIT;
		Log.i("result", "----ddd-----s------" + url);
		httpUtils.send(HttpMethod.POST, url, params,
				new RequestCallBack<String>() {

					@Override
					public void onFailure(HttpException arg0, String arg1) {
						// TODO Auto-generated method stub
						dismissLoadingDialog();
						Log.i("result", "----ddd-----s------" + arg0.getExceptionCode());
						T.ss("操作超时");
					}

					@Override
					public void onSuccess(ResponseInfo<String> response) {
						// TODO Auto-generated method stub
						dismissLoadingDialog();
						String str = response.result;
						String code = "";
						String message = "";
						Log.i("result", "----ddd-----------" + str);

						JSONObject obj;
						try {
							obj = new JSONObject(str);
							code = obj.optString("CODE");
							message = obj.optString("MESSAGE");
							T.ss(message);

						} catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				});
	}
	/**
	 * 查询提现额度
	 */
	private void queryTixianEdu() {
		RequestParams params = new RequestParams();
		HashMap<String, String> map = new HashMap<String, String>();
		map.put("username", MApplication.mSharedPref
				.getSharePrefString(SharedPrefConstant.USERNAME));
		map.put("token", MApplication.mSharedPref
				.getSharePrefString(SharedPrefConstant.TOKEN));
		String json = JSON.toJSONString(map);
		
		Log.i("result", "----ddd-----s---阿---" + json);
		try {
			StringEntity bodyEntity = new StringEntity(json, "UTF-8");
			params.setBodyEntity(bodyEntity);
		} catch (UnsupportedEncodingException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		HttpUtils httpUtils = new HttpUtils();
		String url = MyUrls.LIMITEDU;
		Log.i("result", "----ddd-----s------" + url);
		httpUtils.send(HttpMethod.POST, url, params,
				new RequestCallBack<String>() {
			
			@Override
			public void onFailure(HttpException arg0, String arg1) {
				// TODO Auto-generated method stub
				dismissLoadingDialog();
				Log.i("result", "----ddd-----s------" + arg0.getExceptionCode());
				T.ss("操作超时");
			}
			
			@Override
			public void onSuccess(ResponseInfo<String> response) {
				// TODO Auto-generated method stub
				dismissLoadingDialog();
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
						
					}
					T.ss(message);
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});
	}

	@Override
	public void sure(String password) {
		// TODO Auto-generated method stub
		mPassWdDialog.dismiss();
		mPassWdDialog = null;
		pwd = password;
		sendTixian();
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
