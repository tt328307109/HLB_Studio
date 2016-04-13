package com.lk.qf.pay.indiana.activity;

import java.io.UnsupportedEncodingException;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import org.apache.http.entity.StringEntity;
import org.json.JSONException;
import org.json.JSONObject;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
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
import com.lk.qf.pay.golbal.MApplication;
import com.lk.qf.pay.golbal.MyUrls;
import com.lk.qf.pay.sharedpref.SharedPrefConstant;
import com.lk.qf.pay.tool.T;
import com.lk.qf.pay.utils.TimeUtils;
import com.lk.qf.pay.utils.MyUtilss;
import com.lk.qf.pay.wedget.CommonTitleBar;

public class SignInActivity extends BaseActivity implements
		OnClickListener {

	private TextView tvTotalNum, tvGetNum, tvDate;
	private ImageView imgSign;
	private String isSign;
	private CommonTitleBar title;
	private int continuousDays;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.signin_layout);
		tvTotalNum = (TextView) findViewById(R.id.tv_signIn_kycoin);
		tvGetNum = (TextView) findViewById(R.id.tv_signIn_getNum);
		tvDate = (TextView) findViewById(R.id.tv_signIn_date);
		imgSign = (ImageView) findViewById(R.id.img_signIn_signIn);
		imgSign.setOnClickListener(this);
		isSign = MApplication.mSharedPref
				.getSharePrefString(SharedPrefConstant.ISSIGN);
		title = (CommonTitleBar) findViewById(R.id.titlebar_signin);
		title.setActName("签到");
		title.setCanClickDestory(this, true);
		qianbao();
		if (isSign.equals("True")) {
			imgSign.setBackground(getResources().getDrawable(
					R.drawable.meiriqiandao_2));

			imgSign.setClickable(false);
		} else {

			imgSign.setBackground(getResources().getDrawable(
					R.drawable.meiriqiandao_1));
			imgSign.setClickable(true);
		}
	}

	@Override
	public void onClick(View arg0) {
		// TODO Auto-generated method stub
		if (MyUtilss.noPayYajin()) {
			T.ss("商户未缴纳押金");
			return;
		}
		signIn();
	}

	/**
	 * 签到
	 */
	private void signIn() {
		showLoadingDialog();
		RequestParams params = new RequestParams();
		HashMap<String, String> map = new HashMap<String, String>();
		map.put("username", MApplication.mSharedPref
				.getSharePrefString(SharedPrefConstant.USERNAME));
		map.put("token", MApplication.mSharedPref
				.getSharePrefString(SharedPrefConstant.TOKEN));

		String json = JSON.toJSONString(map);
//		Log.i("result", "----ddd-----------" + json);
		try {
			StringEntity bodyEntity = new StringEntity(json, "UTF-8");
			params.setBodyEntity(bodyEntity);
		} catch (UnsupportedEncodingException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		HttpUtils httpUtils = new HttpUtils();
		String url = MyUrls.SIGNIN_SIGNIN;
		httpUtils.send(HttpMethod.POST, url, params,
				new RequestCallBack<String>() {

					@Override
					public void onFailure(HttpException arg0, String arg1) {
						// TODO Auto-generated method stub
						dismissLoadingDialog();
//						T.ss("操作超时");
					}

					@Override
					public void onSuccess(ResponseInfo<String> response) {
						// TODO Auto-generated method stub
						dismissLoadingDialog();
						String str = response.result;
						String code = "";
						String message = "";
//						Log.i("result", "----ddd-----------" + str);

						JSONObject obj;
						try {
							obj = new JSONObject(str);
							code = obj.optString("CODE");
							message = obj.optString("MESSAGE");
							if (code.equals("00")) {
								MApplication.mSharedPref.putSharePrefString(
										SharedPrefConstant.ISSIGN, "True");// 是否签到
								imgSign.setBackground(getResources()
										.getDrawable(R.drawable.meiriqiandao_2));
								imgSign.setClickable(false);
								qianbao();
							} else {
								T.ss(message);
							}
						} catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				});
	}

	/**
	 * 获取余额
	 */
	private void qianbao() {

		RequestParams params = new RequestParams();
		String url = MyUrls.TXMONEY;
		showLoadingDialog();
		Map<String, String> map = new HashMap<String, String>();
		map.put("username", MApplication.mSharedPref
				.getSharePrefString(SharedPrefConstant.USERNAME));
		map.put("pwd", MApplication.mSharedPref
				.getSharePrefString(SharedPrefConstant.PASSWORD));
		map.put("token", MApplication.mSharedPref
				.getSharePrefString(SharedPrefConstant.TOKEN));

		String json = JSON.toJSONString(map);
//		Log.i("result", "----ddd----s-------" + json);
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
				dismissLoadingDialog();

//				T.ss("操作超时");
			}

			@Override
			public void onSuccess(ResponseInfo<String> response) {
				// TODO Auto-generated method stub
				String code = "";
				String message = "";

				String str = response.result;
				JSONObject obj = null;
//				Log.i("result", "----qianbao----s-------" + str);
				try {
					obj = new JSONObject(str);
					code = obj.optString("CODE");
					message = obj.optString("MESSAGE");

				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				if (code.equals("00")) {
					if (obj != null) {
						tvTotalNum.setText(obj.optString("k_use"));
						String signDate = obj.optString("sigdate");
						int isToday = TimeUtils.isYesterday(signDate);// 上次签到距离今天多久
						continuousDays = obj.optInt("signcount");
						isSign = MApplication.mSharedPref
								.getSharePrefString(SharedPrefConstant.ISSIGN);
						Log.i("result", "------------isToday--s---------=="
								+ isToday);
						Calendar c = Calendar.getInstance();
						// 得到本月的那一天
						int today = c.get(c.DAY_OF_MONTH);
						// 然后判断是不是本月的第一天
						if (today == 1) {
							if (isSign.equals("True")) {
								tvDate.setText("明日");
								tvGetNum.setText("2");
							} else {
								tvDate.setText("今日");
								tvGetNum.setText("1");
							}
						} else {
							if (isSign.equals("True")) {
								tvDate.setText("明日");
								// 如果上次签到不是昨天
								if (isToday > 1) {
									tvGetNum.setText("2");
								} else {
									// 如果上次签到是昨天 连续签到
									if (continuousDays < 10) {
										Log.i("result",
												"------------continuousDays--s---------"
														+ (continuousDays + 1));
										tvGetNum.setText(""
												+ (continuousDays + 1));
									} else {
										tvGetNum.setText("10");
									}
								}
							} else {
								tvDate.setText("今日");
								// 如果上次签到不是昨天
								if (isToday > 1) {
									tvGetNum.setText("1");
									Log.i("result",
											"------------如果上次签到不是昨天----------");
								} else {
									// 如果上次签到是昨天 连续签到
									if (continuousDays < 10) {
										Log.i("result",
												"-------continuousDays----"
														+ (continuousDays + 1));
										tvGetNum.setText(""
												+ (continuousDays + 1));
									} else {
										tvGetNum.setText("10");
									}
								}
							}
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
