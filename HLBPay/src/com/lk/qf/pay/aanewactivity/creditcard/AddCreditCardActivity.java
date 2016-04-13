package com.lk.qf.pay.aanewactivity.creditcard;

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
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;
import com.lk.bhb.pay.R;
import com.lk.qf.pay.aanewactivity.FuzzyQueryActivity;
import com.lk.qf.pay.aanewactivity.NumberSelectorActivity;
import com.lk.qf.pay.activity.BaseActivity;
import com.lk.qf.pay.golbal.MApplication;
import com.lk.qf.pay.golbal.MyUrls;
import com.lk.qf.pay.indiana.activity.EditAddressActivity;
import com.lk.qf.pay.sharedpref.SharedPrefConstant;
import com.lk.qf.pay.tool.T;
import com.lk.qf.pay.utils.ExpresssoinValidateUtil;
import com.lk.qf.pay.utils.PinDKey;
import com.lk.qf.pay.wedget.CommonTitleBar;
import com.lk.qf.pay.wedget.customdialog.AlertDialog;
import com.lk.qf.pay.wedget.view.MyXEdittextView;

import org.apache.http.entity.StringEntity;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;

public class AddCreditCardActivity extends BaseActivity implements
OnClickListener {

	private CommonTitleBar title;
	private TextView tvBankName, tvZdDay, tvHKDay, tvProvince, tvCity;
	private EditText edEdu, edName, edAddr, edValidityDateMonth,
	edValidityDateYear, edCvv2, edQueryPwd, edPayPwd, edZdAccount,
	edTemproEdu;
	private MyXEdittextView edCardNum1, edCardNum2, edPhone, edIdCardNum;
	private String bankName = "", userName = "", cardNum1 = "", cardNum2 = "",
			zdDay = "", eduAccount = "", phoneNum = "", idCardNum = "",
			userPhoneNum = "", address = "", bankcode = "00000000",
			validityDate = "", cvv2 = "", queryPwd = "", payPwd = "",
			zdAccount = "", hkDate = "", province = "北京市", city = "市辖区",
			provinceID = "110000", temproEdu;
	private String logoUrl = "/bank/yl.png";
	private String cardNumBefore6;
	private CharSequence temp;
	private static final int REQUEST_ZHANGDAN_DATE = 0;// 账单日
	private static final int REQUEST_HUANKUAN_DATE = 1;// 还款日
	private static final int REQUEST_BANKNAME_DATE = 2;// 银行名
	private boolean isBankNameClick = true;
	private String key = "D35BFBAB582CF7FB9ECBA164EA32D016";
	private String data8 = "FFFFFFFF";// 10
	private String data9 = "FFFFFFFFF";// 9
	private String data10 = "FFFFFFFFFF";// 8
	int  yy = 19920428;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.add_creditcard_layout);
		init();
	}

	private void init() {
		userPhoneNum = MApplication.mSharedPref
				.getSharePrefString(SharedPrefConstant.USERNAME);
		title = (CommonTitleBar) findViewById(R.id.titlebar_add_creditCard);
		title.setActName("添加信用卡");
		title.setCanClickDestory(this, true);
		tvBankName = (TextView) findViewById(R.id.tv_add_creditCard_bankName);
		edName = (EditText) findViewById(R.id.ed_add_creditCard_Name);
		edAddr = (EditText) findViewById(R.id.ed_add_creditCard_address);
		edPhone = (MyXEdittextView) findViewById(R.id.ed_add_creditCard_phoneNum);
		edCardNum1 = (MyXEdittextView) findViewById(R.id.ed_add_creditCard_bankCardNum);
		edCardNum2 = (MyXEdittextView) findViewById(R.id.ed_add_creditCard_bankCardNum2);
		edIdCardNum = (MyXEdittextView) findViewById(R.id.ed_add_creditCard_idcardNum);
		edCardNum1.setPatten(" ", MyXEdittextView.CREDIT_CARD);
		edCardNum2.setPatten(" ", MyXEdittextView.CREDIT_CARD);
		edIdCardNum.setPatten(" ", MyXEdittextView.ID_CARD);
//		setFilters(new InputFilter[] { new InputFilter.LengthFilter(maxLength) });
		tvProvince = (TextView) findViewById(R.id.tv_add_creditCard_addressPro);// 省
		tvCity = (TextView) findViewById(R.id.tv_add_creditCard_addressCity);// 市
		tvZdDay = (TextView) findViewById(R.id.tv_add_creditCard_zhangdanDate);
		tvHKDay = (TextView) findViewById(R.id.tv_add_creditCard_hkDate);
		edEdu = (EditText) findViewById(R.id.ed_add_creditCard_edu);
		edValidityDateMonth = (EditText) findViewById(R.id.ed_add_creditCard_bankCardValidityDateMonth);
		edValidityDateYear = (EditText) findViewById(R.id.ed_add_creditCard_bankCardValidityDateYear);
		edCvv2 = (EditText) findViewById(R.id.ed_add_creditCard_bankCardCvv2);
		edQueryPwd = (EditText) findViewById(R.id.ed_add_creditCard_queryPwd);
		edPayPwd = (EditText) findViewById(R.id.ed_add_creditCard_payPwd);
		edZdAccount = (EditText) findViewById(R.id.ed_add_creditCard_zdAccount);
		edTemproEdu = (EditText) findViewById(R.id.ed_add_creditCard_eduTemporary);
		findViewById(R.id.rl_add_creditCard_select_zdDay).setOnClickListener(
				this);
		findViewById(R.id.rl_add_creditCard_bankName).setOnClickListener(this);
		findViewById(R.id.btn_add_creditCard_tijiao).setOnClickListener(this);
		findViewById(R.id.rl_add_creditCard_select_hkDay).setOnClickListener(
				this);
		// tvUserName.setText(userName);// 用户名
		tvProvince.setOnClickListener(this);
		tvCity.setOnClickListener(this);
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

	}

	@Override
	public void onClick(View arg0) {
		// TODO Auto-generated method stub
		switch (arg0.getId()) {
		case R.id.rl_add_creditCard_select_zdDay:
			Intent intent = new Intent(AddCreditCardActivity.this,
					NumberSelectorActivity.class);
			intent.putExtra("type", 0);
			startActivityForResult(intent, REQUEST_ZHANGDAN_DATE);
			break;
		case R.id.rl_add_creditCard_select_hkDay:
			Intent intent3 = new Intent(AddCreditCardActivity.this,
					NumberSelectorActivity.class);
			intent3.putExtra("type", 1);
			startActivityForResult(intent3, REQUEST_HUANKUAN_DATE);
			break;
		case R.id.rl_add_creditCard_bankName:
			if (isBankNameClick) {
				Intent intent2 = new Intent(AddCreditCardActivity.this,
						NumberSelectorActivity.class);
				intent2.putExtra("type", 2);
				startActivityForResult(intent2, REQUEST_BANKNAME_DATE);
			}
			break;
		case R.id.btn_add_creditCard_tijiao:
			addBankCard();
			break;
		case R.id.tv_add_creditCard_addressPro:
			Intent intent1 = new Intent(AddCreditCardActivity.this,
					FuzzyQueryActivity.class);
			intent1.putExtra("showType", "province");
			startActivityForResult(intent1,
					EditAddressActivity.REQUEST_PROVINCE);
			break;
		case R.id.tv_add_creditCard_addressCity:
			Intent intent2 = new Intent(AddCreditCardActivity.this,
					FuzzyQueryActivity.class);
			intent2.putExtra("showType", "city");
			intent2.putExtra("provinceID", provinceID);
			startActivityForResult(intent2, EditAddressActivity.REQUEST_CITY);
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

				zdDay = data.getStringExtra("dateNum").substring(0,
						data.getStringExtra("dateNum").length() - 1);
				Log.i("result", "--------dddss----" + zdDay);
				tvZdDay.setText("每月" + zdDay + "日");
			}
			break;
		case REQUEST_HUANKUAN_DATE:
			if (resultCode == Activity.RESULT_OK) {
				hkDate = data.getStringExtra("dateNum").substring(0,
						data.getStringExtra("dateNum").length() - 1);
				tvHKDay.setText(hkDate + "天");
			}
			break;
		case REQUEST_BANKNAME_DATE:
			if (resultCode == Activity.RESULT_OK) {

				bankName = data.getStringExtra("dateNum");
				if (bankName.length() > 10) {
					String strName = bankName.substring(0, 10);
					// if (strName.length()>10) {
					// strName = strName.substring(0,10);
					// Log.i("result", "------hang0---"+strName);
					// }
					Log.i("result", "------hang1---" + strName);
					tvBankName.setText(strName + "...");
				} else {
					tvBankName.setText(bankName);
				}
			}
			break;

		case EditAddressActivity.REQUEST_PROVINCE:
			if (resultCode == Activity.RESULT_OK) {
				province = data.getStringExtra("bankName");
				provinceID = data.getStringExtra("bankId");
				Log.i("result", "----------province---s--" + province);
				if (province.length() > 5) {
					String strName = province.substring(0, 5);
					Log.i("result", "------hang1---" + strName);
					tvProvince.setText(strName + "...");
				} else {
					Log.i("result", "----------province--s-s--" + province);
					tvProvince.setText(province);
				}
			}
			break;
		case EditAddressActivity.REQUEST_CITY:
			if (resultCode == Activity.RESULT_OK) {
				city = data.getStringExtra("bankName");
				Log.i("result", "----------citys--" + city);
				if (city.length() > 10) {
					String strName = city.substring(0, 5);
					Log.i("result", "------hacity--" + strName);
					tvCity.setText(strName + "...");
				} else {
					Log.i("result", "------hcity-zh--" + city);
					tvCity.setText(city);
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
		phoneNum = edPhone.getNonSeparatorText();
		idCardNum = edIdCardNum.getNonSeparatorText();
		eduAccount = edEdu.getText().toString();
		address = edAddr.getText().toString();
		validityDate = edValidityDateMonth.getText().toString() + "/"
				+ edValidityDateYear.getText().toString();// 有效期
		cvv2 = edCvv2.getText().toString();
		queryPwd = edQueryPwd.getText().toString();
		payPwd = edPayPwd.getText().toString();
		zdAccount = edZdAccount.getText().toString();
		temproEdu = edTemproEdu.getText().toString();
	}

	/**
	 * 获取银行名
	 */
	private void getBankName() {
		RequestParams params = new RequestParams();
		HashMap<String, String> map = new HashMap<String, String>();

		map.put("BankCardId", cardNumBefore6);

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
		String url = MyUrls.ROOT_URL_CHECKBANK;

		httpUtils.send(HttpMethod.POST, url, params,
				new RequestCallBack<String>() {

			@Override
			public void onFailure(HttpException arg0, String arg1) {
				// TODO Auto-generated method stub
				// Log.i("result", "--------------failure------------");
				// T.ss("发送失败!" + arg0.getExceptionCode());
				isBankNameClick = true;
			}

			@Override
			public void onSuccess(ResponseInfo<String> response) {
				// TODO Auto-generated method stub

				String str = response.result;
				int code;
				Log.i("result", "----ddd-----------" + str);
				JSONObject obj;
				try {
					obj = new JSONObject(str);
					code = obj.optInt("Count");
					if (code > 0) {
						bankName = obj.optJSONArray("data")
								.optJSONObject(0).optString("bankname");
						logoUrl = obj.optJSONArray("data")
								.optJSONObject(0)
								.optString("markcode1");
						bankcode = obj.optJSONArray("data")
								.optJSONObject(0).optString("bankcode");
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
		} else if (!ExpresssoinValidateUtil.isChineseString(userName)) {
			T.ss("请输入合法的姓名");
			return;
		}

		if (phoneNum.equals("") || TextUtils.isEmpty(phoneNum)) {
			T.ss("请输入持卡人手机号码");
			return;
		} else if (!ExpresssoinValidateUtil.isMobilePhone(phoneNum)) {
			T.ss("请输入合法的手机号码");
			return;
		}
		if (idCardNum.equals("") || TextUtils.isEmpty(idCardNum)) {
			T.ss("请输入持卡人身份证号码");
			return;
		} else if (!ExpresssoinValidateUtil.isIdCard(idCardNum)) {
			T.ss("请输入合法的身份证号码");
			return;
		}

		if (cardNum1.equals("") || TextUtils.isEmpty(cardNum1)) {
			T.ss("请输入信用卡号");
			return;
		}
		if (cardNum1.length() != 16) {
			T.ss("请正确的信用卡号");
			return;
		}
		if (!cardNum1.equals(cardNum2)) {
			T.ss("两次输入的信用卡号不一致");
			return;
		}
		if (bankName.equals("") || TextUtils.isEmpty(bankName)) {
			T.ss("请选择开户银行");
			return;
		}
		if (validityDate.equals("") || TextUtils.isEmpty(validityDate)) {
			T.ss("请输入信用卡有效期(格式:月/年)");
			return;
		}
		if (!ExpresssoinValidateUtil.isValidityDate(validityDate)) {
			T.ss("信用卡有效期格式错误(格式:月/年)");
			return;
		}
		if (cvv2.equals("") || TextUtils.isEmpty(cvv2)) {
			T.ss("请输入信用卡CVV2");
			return;
		} else if (cvv2.length() != 3) {
			T.ss("输入的CVV2位数不正确");
			return;
		}
		if (queryPwd.equals("") || TextUtils.isEmpty(queryPwd)) {
			T.ss("请输入查询密码");
			return;
		} else if (6 > queryPwd.length() || queryPwd.length() > 8) {
			T.ss("请输入6-8位数纯数字的查询密码");
			return;
		}
		if (payPwd.equals("") || TextUtils.isEmpty(payPwd)) {
			T.ss("请输入支付密码");
			return;
		} else if (payPwd.length() != 6) {
			T.ss("请输入6位数纯数字的支付密码");
			return;
		}

		if (zdDay.equals("") || TextUtils.isEmpty(zdDay)) {
			T.ss("请选择账单日");
			return;
		}
		if (hkDate.equals("") || TextUtils.isEmpty(hkDate)) {
			T.ss("请选择还款天数");
			return;
		}
		if (eduAccount.equals("") || TextUtils.isEmpty(eduAccount)) {
			T.ss("请输入信用卡额度");
			return;
		}
		if (temproEdu.equals("") || TextUtils.isEmpty(temproEdu)) {
			temproEdu = "0";
		}

		// if (province.equals("") || TextUtils.isEmpty(province)) {
		// T.ss("请选择地址");
		// return;
		// }
		// if (city.equals("") || TextUtils.isEmpty(city)) {
		// T.ss("请选择地区");
		// return;
		// }
		// if (address.equals("") || TextUtils.isEmpty(address)) {
		// T.ss("请输入信用卡寄回地址");
		// return;
		// }

		if (zdAccount.equals("") || TextUtils.isEmpty(zdAccount)) {
			T.ss("请输入账单金额");
			return;
		}

		showLoadingDialog();
		RequestParams params = new RequestParams();
		HashMap<String, String> map = new HashMap<String, String>();
		map.put("username", userPhoneNum);
		map.put("token", MApplication.mSharedPref
				.getSharePrefString(SharedPrefConstant.TOKEN));
		map.put("Cmd", "AddCard");
		map.put("bankcard", cardNum1);
		map.put("name", userName);
		map.put("phone", phoneNum);
		map.put("get_address", "");
		map.put("bankmoney", eduAccount);
		map.put("billtime", zdDay);
		map.put("idcardno", idCardNum);
		map.put("logo", logoUrl);
		map.put("bankcode", bankcode);
		map.put("pwd", PinDKey.UnionEncryptData(key, payPwd + data10));
		int queryPwdLenght = queryPwd.length();
		if (queryPwdLenght == 6) {
			queryPwd = queryPwd + data10;
		} else if (queryPwdLenght == 7) {
			queryPwd = queryPwd + data9;
		} else if (queryPwdLenght == 8) {
			queryPwd = queryPwd + data8;
		}
		map.put("query_pwd", PinDKey.UnionEncryptData(key, queryPwd));
		map.put("credit_valid_date", validityDate);
		map.put("bank", bankName);
		map.put("credit_cvv2", cvv2);
		map.put("refund_day", hkDate);
		map.put("billmoney", zdAccount);
		map.put("reimmoney", eduAccount);
		map.put("temp", temproEdu);// 临时额度

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
		String url = MyUrls.ROOT_URL_CREDIT_HANDLE;

		httpUtils.send(HttpMethod.POST, url, params,
				new RequestCallBack<String>() {

			@Override
			public void onFailure(HttpException arg0, String arg1) {
				// TODO Auto-generated method stub
				Log.i("result", "--------------failure------------"
						+ arg0.getExceptionCode());
				T.ss("操作超时!");
				isBankNameClick = true;
				dismissLoadingDialog();
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
						String name = obj.optString("name");
						String cardCode = obj.optString("card_code");
						showLoanState(name, cardCode);
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
	 * 提示添加信用卡状态
	 */
	private void showLoanState(String name, String code) {
		new AlertDialog(AddCreditCardActivity.this)
		.builder()
		.setTitle("添加成功")
		.setCancelable(false)
		// 点击周边不消失
		.setMsg("持卡人姓名:" + name + "\n\n" + "信用卡标识:" + code + "\n\n"
				+ "请将信用卡寄至:"
				+ getResources().getString(R.string.apply_ransom_text)
				+ "\n\n" + "收件电话:"
				+ getResources().getString(R.string.apply_ransom_phone))
				.setPositiveButton("确定", new OnClickListener() {
					@Override
					public void onClick(View v) {
						finish();
					}
				}).show();
	}

}
