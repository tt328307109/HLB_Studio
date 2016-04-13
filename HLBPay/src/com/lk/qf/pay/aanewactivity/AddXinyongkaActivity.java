package com.lk.qf.pay.aanewactivity;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;

import org.apache.http.entity.StringEntity;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.RelativeLayout;
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
import com.lk.qf.pay.wedget.CommonTitleBar;
import com.lk.qf.pay.wedget.XEditText;
import com.lk.qf.pay.wedget.view.MyXEdittextView;

public class AddXinyongkaActivity extends BaseActivity implements
		OnClickListener {

	private CommonTitleBar title;
	private TextView tvBankName, tvZdDay, tvHKDay;
	private EditText edEdu,edName;
	private MyXEdittextView edCardNum1, edCardNum2;
	private String bankName = "", userName, cardNum1, cardNum2, zdDay, hkDay,
			eduAccount,phoneNum;
	private String logoUrl="";
	private String cardNumBefore6;
	private CharSequence temp;
	private RelativeLayout rlZDDay, rlHHDay, rlBankName;
	private static final int REQUEST_ZHANGDAN_DATE = 0;// 账单日
	private static final int REQUEST_HUANKUAN_DATE = 1;// 还款日
	private static final int REQUEST_BANKNAME_DATE = 2;// 银行名
	private boolean isBankNameClick = true;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.add_xinyongka_layout);
		init();
//		addBankCard1();
//		addBankCard2();
	}


	private void init() {
		phoneNum = MApplication.mSharedPref
				.getSharePrefString(SharedPrefConstant.USERNAME);

		title = (CommonTitleBar) findViewById(R.id.titlebar_add_xinyongka);
		title.setActName("添加信用卡");
		title.setCanClickDestory(this, true);
		tvBankName = (TextView) findViewById(R.id.tv_add_xyk_bankName);
		edName = (EditText) findViewById(R.id.ed_add_xyk_Name);
		edCardNum1 = (MyXEdittextView) findViewById(R.id.ed_add_xyk_bankCardNum);
		edCardNum2 = (MyXEdittextView) findViewById(R.id.ed_add_xyk_bankCardNum2);
		edCardNum1.setPatten(" ", MyXEdittextView.CREDIT_CARD);
		edCardNum2.setPatten(" ", MyXEdittextView.CREDIT_CARD);
		
		edEdu = (EditText) findViewById(R.id.ed_add_xyk_bankEdu);
		rlZDDay = (RelativeLayout) findViewById(R.id.rl_add_xyk_select_zdDay);
		rlHHDay = (RelativeLayout) findViewById(R.id.rl_add_xyk_select_hhDay);
		rlBankName = (RelativeLayout) findViewById(R.id.rl_add_xyk_bankName);
		findViewById(R.id.btn_add_xinyongka_tijiao).setOnClickListener(this);
		rlZDDay.setOnClickListener(this);
		rlHHDay.setOnClickListener(this);
		rlBankName.setOnClickListener(this);

//		tvUserName.setText(userName);// 用户名

		edCardNum1.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence arg0, int arg1, int arg2,
					int arg3) {
				// TODO Auto-generated method stub
				temp = arg0;
			}

			@Override
			public void beforeTextChanged(CharSequence arg0, int start,
					int before, int count) {
				// TODO Auto-generated method stub

			}

			@Override
			public void afterTextChanged(Editable arg0) {
				// TODO Auto-generated method stub
				if (temp.length() == 7) {
					cardNumBefore6 = edCardNum1.getNonSeparatorText();
					Log.i("result", "-----------ca----------");
					getBankName();
				}
				if (temp.length() < 8) {
					isBankNameClick = true;
					tvBankName.setText("");
				}
			}
		});

		tvZdDay = (TextView) findViewById(R.id.tv_add_xyk_zhangdanDate);
		tvHKDay = (TextView) findViewById(R.id.tv_add_xyk_huankuanDay);
	}

	@Override
	public void onClick(View arg0) {
		// TODO Auto-generated method stub
		switch (arg0.getId()) {
		case R.id.rl_add_xyk_select_zdDay:
			Intent intent = new Intent(AddXinyongkaActivity.this,
					NumberSelectorActivity.class);
			intent.putExtra("type", 0);
			startActivityForResult(intent, REQUEST_ZHANGDAN_DATE);
			break;
		case R.id.rl_add_xyk_select_hhDay:
			Intent intent1 = new Intent(AddXinyongkaActivity.this,
					NumberSelectorActivity.class);
			intent1.putExtra("type", 1);
			startActivityForResult(intent1, REQUEST_HUANKUAN_DATE);

			break;
		case R.id.rl_add_xyk_bankName:
			if (isBankNameClick) {
				Intent intent2 = new Intent(AddXinyongkaActivity.this,
						NumberSelectorActivity.class);
				intent2.putExtra("type", 2);
				startActivityForResult(intent2, REQUEST_BANKNAME_DATE);
			}
			break;
		case R.id.btn_add_xinyongka_tijiao:
			addBankCard();
			break;

		default:
			break;
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);

		switch (requestCode) {
		case REQUEST_ZHANGDAN_DATE:
			if (resultCode == Activity.RESULT_OK) {

				zdDay = data.getStringExtra("dateNum").substring(0, data.getStringExtra("dateNum").length()-1);
				Log.i("result", "--------dddss----"+zdDay);
				tvZdDay.setText("每月" + zdDay+"日");
			}
			break;
		case REQUEST_HUANKUAN_DATE:
			if (resultCode == Activity.RESULT_OK) {

				hkDay = data.getStringExtra("dateNum").substring(0, data.getStringExtra("dateNum").length()-1);
				tvHKDay.setText(hkDay+"天");
			}
			break;
		case REQUEST_BANKNAME_DATE:
			if (resultCode == Activity.RESULT_OK) {

				bankName = data.getStringExtra("dateNum");
				logoUrl = "";
				if (bankName.length() > 10) {
					String strName = bankName.substring(0, 10);
					// if (strName.length()>10) {
					// strName = strName.substring(0,10);
					// Log.i("result", "------hang0---"+strName);
					// }
					Log.i("result", "------hang1---" + strName);
					tvBankName.setText(strName + "...");
				} else {

					Log.i("result", "------hang2---" + bankName);
					tvBankName.setText(bankName);
				}
			}
			break;

		default:
			break;
		}
	}
	
	/**
	 * 获得输入的数据
	 */
	private void getData() {
		userName = edName.getText().toString();
		cardNum1 = edCardNum1.getNonSeparatorText();
		cardNum2 = edCardNum2.getNonSeparatorText();
		eduAccount = edEdu.getText().toString();
	}

	private void getBankName() {

		RequestParams params = new RequestParams();
		HashMap<String, String> map = new HashMap<String, String>();

		map.put("card", cardNumBefore6);

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
		String url = MyUrls.BANKSELECT;

		httpUtils.send(HttpMethod.POST, url, params,
				new RequestCallBack<String>() {

					@Override
					public void onFailure(HttpException arg0, String arg1) {
						// TODO Auto-generated method stub
						Log.i("result", "--------------failure------------");
						// T.ss("发送失败!" + arg0.getExceptionCode());
						isBankNameClick = true;
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
								bankName = obj.optJSONArray("date")
										.optJSONObject(0).optString("bankname");
								logoUrl = obj.optJSONArray("date")
										.optJSONObject(0).optString("markcode1");
								if (bankName.length() > 10) {
									String strName = bankName.substring(0, 10);
									Log.i("result", "-----------10d---------"
											+ strName);

									tvBankName.setText(strName + "...");
								} else {

									tvBankName.setText(bankName);
									Log.i("result", "-----------10s----------"
											+ bankName);
								}
								isBankNameClick = false;
							} else {
								isBankNameClick = true;
								// T.ss(message);
							}

						} catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				});
	}

	private void addBankCard() {
		getData();
		if (userName.equals("") || TextUtils.isEmpty(userName)) {
			T.ss("请输入开户名");
			return;
		}
		if (cardNum1.equals("") || TextUtils.isEmpty(cardNum1)) {
			T.ss("请输入信用卡号");
			return;
		}
		if (cardNum1.length()<14 || cardNum1.length()>16) {
			T.ss("请正确的信用卡号");
			return;
		}
		if (!cardNum1.equals(cardNum2)) {
			T.ss("两次输入的信用卡号不一致");
			return;
		}
		
		if (zdDay.equals("")|| TextUtils.isEmpty(zdDay)) {
			T.ss("请选择账单日");
			return;
		}
		if (hkDay.equals("")|| TextUtils.isEmpty(hkDay)) {
			T.ss("请选择还款日");
			return;
		}
		if (eduAccount.equals("")|| TextUtils.isEmpty(eduAccount)) {
			T.ss("请输入信用卡额度");
			return;
		}
		RequestParams params = new RequestParams();
		HashMap<String, String> map = new HashMap<String, String>();

		map.put("username", phoneNum);
		map.put("name", userName);
		map.put("bankmoney", eduAccount);
		map.put("bank", bankName);
		map.put("logo", logoUrl);
		map.put("billmoney", "0");
		map.put("bankcard", cardNum1);
		map.put("billtime", zdDay);
		map.put("reimtime", hkDay);
		map.put("type", "0");//0添加 1修改

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
		String url = MyUrls.CREDITCARDADD;

		httpUtils.send(HttpMethod.POST, url, params,
				new RequestCallBack<String>() {

					@Override
					public void onFailure(HttpException arg0, String arg1) {
						// TODO Auto-generated method stub
						Log.i("result", "--------------failure------------");
						// T.ss("发送失败!" + arg0.getExceptionCode());
						isBankNameClick = true;
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
								T.ss("添加成功");
								finish();
							}else{
								T.ss(message);
							}

						} catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				});
	}

	private void addBankCard1() {
		RequestParams params = new RequestParams();
		HashMap<String, String> map = new HashMap<String, String>();
		map.put("username", phoneNum);
		map.put("name", "孙俪");
		map.put("id", "50");
		map.put("count", "1");
		map.put("bnakmoney", "20000");
		map.put("billmoney", "10000");
		map.put("reimmoney", "10000");
		map.put("refund_day", "12");
		map.put("bankcard", "6228480412345678");
		map.put("billtime", "27");
		map.put("reimtime", "11");
		map.put("pwd", "123456");
		map.put("bank", "农业银行");
		map.put("logo", "/bank/6.png");
		map.put("phone", "15923214235");
		map.put("bankcode", "1251456");
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
		String url = "http://219.146.70.110:1234/Credit/RefundApply.ashx";

		httpUtils.send(HttpMethod.POST, url, params,
				new RequestCallBack<String>() {
					@Override
					public void onFailure(HttpException arg0, String arg1) {
						// TODO Auto-generated method stub
						Log.i("result", "--------------failure------------"+arg0.getExceptionCode());
						// T.ss("发送失败!" + arg0.getExceptionCode());
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
								T.ss("添加成功");
							}else{
								T.ss(message);
							}

						} catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				});
	}
	private void addBankCard2() {
		RequestParams params = new RequestParams();
		HashMap<String, String> map = new HashMap<String, String>();
		map.put("username", phoneNum);
		map.put("token", MApplication.mSharedPref
				.getSharePrefString(SharedPrefConstant.TOKEN));
		map.put("pwd", "123456");
		map.put("payMoney", "10000");
		map.put("id", "50");

		String json = JSON.toJSONString(map);
		 Log.i("result", "----ddd----ss-------" + json);
		try {
			StringEntity bodyEntity = new StringEntity(json, "UTF-8");
			params.setBodyEntity(bodyEntity);
		} catch (UnsupportedEncodingException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		HttpUtils httpUtils = new HttpUtils();
		String url = "http://219.146.70.110:1234/Credit/PayRefundOrder.ashx";

		httpUtils.send(HttpMethod.POST, url, params,
				new RequestCallBack<String>() {

					@Override
					public void onFailure(HttpException arg0, String arg1) {
						// TODO Auto-generated method stub
						Log.i("result", "--------------failure------------"+arg0.getExceptionCode());
						// T.ss("发送失败!" + arg0.getExceptionCode());
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
								T.ss("添加成功");
							}else{
								T.ss(message);
							}

						} catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				});
	}


}
