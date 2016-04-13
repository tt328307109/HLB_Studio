package com.lk.qf.pay.aanewactivity;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;

import org.apache.http.entity.StringEntity;
import org.json.JSONException;
import org.json.JSONObject;

import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;
import com.lk.bhb.pay.R;
import com.lk.pay.utils.MD5Util;
import com.lk.qf.pay.activity.BaseActivity;
import com.lk.qf.pay.activity.ShowMsgActivity;
import com.lk.qf.pay.beans.PosData;
import com.lk.qf.pay.golbal.Constant;
import com.lk.qf.pay.golbal.MApplication;
import com.lk.qf.pay.golbal.MyUrls;
import com.lk.qf.pay.golbal.User;
import com.lk.qf.pay.sharedpref.SharedPrefConstant;
import com.lk.qf.pay.utils.AmountUtils;
import com.lk.qf.pay.utils.MyJson;
import com.lk.qf.pay.utils.PinDKey;
import com.lk.qf.pay.utils.MyUtilss;
import com.lk.qf.pay.wedget.CommonTitleBar;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

public class CashInConfimSuperActivity extends BaseActivity implements
		OnClickListener {

	private TextView cardNoText, payAmtText;
	private EditText cardPwdEdit;
	private String cardPwd;
	private Context mContext;
	private int rate = 0;
	private String payType = "";
	private LinearLayout pwb;
	HashMap<String, String> map;
	private String imageName;
	private CommonTitleBar title;
	private String zpinkey;

	// List<Record> records = null;
	// private String code;
	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.cadhin_confirm_super_layout);
		mContext = this;
		init();
	}

	private void init() {
		cardNoText = (TextView) findViewById(R.id.cashin_card_no_text_super);
		payAmtText = (TextView) findViewById(R.id.cashin_pay_amt_text_super);
		cardPwdEdit = (EditText) findViewById(R.id.cash_bank_pwd_edit_super);

		pwb = (LinearLayout) findViewById(R.id.pwb_super);
		cardNoText
				.setText(MyUtilss.hiddenCardNo(PosData.getPosData().getCardNo()));
		payAmtText.setText(AmountUtils.changeFen2Yuan(PosData.getPosData()
				.getPayAmt()) + "元");
		// findViewById(R.id.btn_back).setOnClickListener(this);
		findViewById(R.id.cashin_confirm_btn_super).setOnClickListener(this);
		if (PosData.getPosData().getType() != null
				&& PosData.getPosData().getType().equals("JHL蓝牙刷卡器")) {
			pwb.setVisibility(View.GONE);
		}
		Intent intent = getIntent();
		if (intent != null) {
			imageName = intent.getStringExtra("imagename");
			
		}
		title = (CommonTitleBar) findViewById(R.id.titlebar_confirm_super);
		title.setCanClickDestory(this, true);
		title.setActName("支付");
		
		

	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.cashin_confirm_btn_super:
			if (!PosData.getPosData().getType().equals("JHL蓝牙刷卡器")) {
				cardPwd = cardPwdEdit.getText().toString().trim();
				// showDialog("测试"+cardPwd.length());
				if (TextUtils.isEmpty(cardPwd)) {
					showDialog(getResources().getString(
							R.string.inputbankCardPwd));
				} else if (cardPwd.length() != 6) {
					showDialog("银行卡密码长度应为6位数");
				} else {
					downloadPineky();
				}
			} else {
				downloadPineky();
			}
			/*
			 * cardPwd = cardPwdEdit.getText().toString().trim(); if
			 * (TextUtils.isEmpty(cardPwd)) {
			 * showDialog(getResources().getString( R.string.inputbankCardPwd));
			 * } else if (cardPwd.length() != 6) { showDialog("银行卡密码长度应为6位数"); }
			 * else { downloadPineky(); }
			 */

			break;
		
		default:
			break;
		}
	}

	private void downloadPineky() {
		showLoadingDialog();
		// 终端签到
		HashMap<String, String> params = new HashMap<String, String>();
		// params.put("termNo", PosData.getPosData().getTermNo());
		params.put("termNo", PosData.getPosData().getTermNo());

		System.out.println("---------------->"
				+ PosData.getPosData().getTermNo());
		params.put("termType", PosData.getPosData().getTermType());
		params.put("custId", MApplication.mSharedPref
				.getSharePrefString(SharedPrefConstant.MERCHANTID));
		// params.put("custId", "");

		postcesiqian(this, MyUrls.SIGN, params);
		// postcesiqian(this, "http://192.168.1.15/test.php", params);
	}

	/**
	 * 终端签到请求
	 * 
	 * @param context
	 * @param url
	 * @param params
	 */
	private void postcesiqian(Context context, String url,
			HashMap<String, String> params) {

		if (params.containsKey("custPwd")) {
			params.put("custPwd",
					MD5Util.generatePassword(params.get("custPwd")));
		}

		if (params.containsKey("newPwd")) {
			params.put("newPwd", MD5Util.generatePassword(params.get("newPwd")));
		}
		if (params.containsKey("payPwd")) {
			params.put("payPwd", MD5Util.generatePassword(params.get("payPwd")));
		}
		params.put("payType", PosData.getPosData().getPayType());
		params.put("sysType", Constant.SYS_TYPE);
		params.put("sysVersion", Constant.SYS_VERSIN);
		params.put("appVersion", MyUtilss.getVersion(context));
		params.put("sysTerNo", MyUtilss.getLocalIpAddress());
		params.put("txnDate", MyUtilss.getCurrentDate("yyMMdd"));
		params.put("txnTime", MyUtilss.getCurrentDate("HHmmss"));
		params.put("custMobile", MApplication.mSharedPref
				.getSharePrefString(SharedPrefConstant.USERNAME));
		params.put("token", MApplication.mSharedPref
				.getSharePrefString(SharedPrefConstant.TOKEN));
		if (User.login) {
			// params.put("custId", User.uId);
			params.put("custMobile", MApplication.mSharedPref
					.getSharePrefString(SharedPrefConstant.USERNAME));
		}

		String jsonAll = MyJson.getRequest(params);

		RequestParams requestParams = new RequestParams();
		StringEntity bodyEntity;
		try {
			bodyEntity = new StringEntity(jsonAll, "UTF-8");
			requestParams.setBodyEntity(bodyEntity);
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Log.i("result", "----------签到请求-----------" + jsonAll);
		// httpClient.post(MApplication.mApplicationContext, url, requestParams,
		// responseHandler);

		HttpUtils utils = new HttpUtils();
		utils.send(HttpMethod.POST, url, requestParams,
				new RequestCallBack<String>() {

					@Override
					public void onFailure(HttpException arg0, String arg1) {
						dismissLoadingDialog();
						Log.i("result",
								"-----------denglkk----------"
										+ arg0.getExceptionCode() + "  " + arg1);
						showDialog("终端签到异常:" + arg0.getExceptionCode());
					}

					@Override
					public void onSuccess(ResponseInfo<String> response) {
						// TODO Auto-generated method stub
						dismissLoadingDialog();
						String strReturnLogin = response.result;
						String code = "";
						String message = "";
						Log.i("result", "----------签到-result-------"
								+ strReturnLogin);
						// showDialog("签到"+strReturnLogin);
						try {
							JSONObject obj = new JSONObject(strReturnLogin);
							// code=obj.optString("CODE");
							// message=obj.optString("MESSAGE");
							code = obj.optJSONObject("REP_BODY")
									.optString("RSPCOD");
							String rspmsg = obj.optJSONObject("REP_BODY")
									.optString("RSPMSG");
							if (code.equals("000000")) {
								zpinkey = obj.optJSONObject("REP_BODY").optString(
										"zpinkey");
//								Log.i("result", "----------签到--------" + zpinkey);
								if (rspmsg.equals("签到成功")) {
									
									goPay(zpinkey);
								} else {
//									Log.i("result", "----------签到1--------"
//											+ strReturnLogin);
									showDialog(rspmsg);
								}
							}else{
								showDialog(rspmsg);
							}
						} catch (JSONException e) {

							e.printStackTrace();
						}
						// Log.i("result", "----------请求---1--------" +
						// strReturnLogin);
					}
				});

	}

	private void goPay(String pinkey) {

		/*
		 * if (PosData.getPosData().getType().equals("蓝牙刷卡器")) { cardPwd =
		 * PosData.getPosData().getPinblok(); cardPwd = cardPwd.substring(2, 8);
		 * }
		 */
		showLoadingDialog();
		System.out.println("pinkey--->" + pinkey);
		System.out.println("cardPwd--->" + cardPwd);
		try {
			// cardPwd = PinDes.pinResultMak(PinDes.PINZMK, pinkey, "",
			// cardPwd);D35BFBAB582CF7FB9ECBA164EA32D016
			String cardNO = PosData.getPosData().getCardNo();
			// 加密密码
			cardPwd = PinDKey.pinResultMak("6B97E9D9F21F016BBF01DF9BDACD3238",
					pinkey, cardNO, cardPwd);
			if (cardNO.endsWith(" ")) {
				cardNO = cardNO.replace(" ", "");
			}
			if (cardNO.endsWith("f")) {
				cardNO = cardNO.replace("f", "");
			}
			if (cardNO.endsWith("F")) {
				cardNO = cardNO.replace("F", "");
			}

		} catch (Exception e) {

		}
		System.out.println("cardPwd--->" + cardPwd);
		if (rate == 0) {
			rate = 1;
		} else if (rate == 1) {
			rate = 2;
		} else if (rate == 2) {
			rate = 3;
		} else {
			rate = 4;
		}

		// String trackTwo = PinDKey.UnionDecryptData(zpinkey,
		// PosData.getPosData().getTarckTwo());
		// if (trackTwo.length() > 38) {
		//
		// trackTwo = trackTwo.substring(0, trackTwo.length()
		// - (trackTwo.length() - 38));
		// }
		// String trackThree = PinDKey.UnionDecryptData(zpinkey,
		// PosData.getPosData().getTarckThree());
		// String track = trackTwo+"|"+trackThree;
		HashMap<String, String> params = new HashMap<String, String>();
		
		String prdordNo = PosData.getPosData().getPrdordNo();
		if (prdordNo==null || prdordNo.equals("")) {
			prdordNo="";
		}
		params.put("prdordNo", prdordNo);//理财id号
		payType = PosData.getPosData().getPayType();//03是闪电收款  07为理财
		String payAmt = PosData.getPosData().getPayAmt();//金额
		String rateType="1";
		if (payType.equals("07")) {
			double account = Double.parseDouble(payAmt);
			account = account/100;
//			Log.i("result", "-------------------account----"+account);
//			Log.i("result", "-------------------account-s---"+account*0.0035);
//			Log.i("result", "-------------------dddf----"+(account*0.0035>26));
			if (account*0.0035>26) {
				rateType = "4";
			}
		}
		params.put("payType", PosData.getPosData().getPayType());// 三级分销修改
		// params.put("payType", PosData.getPosData().getPayType());
		params.put("rateType", rateType);
		params.put("termNo", PosData.getPosData().getTermNo());
		params.put("termType", PosData.getPosData().getTermType());
		params.put("payAmt", payAmt);
		params.put("sysType", "Android");

		Log.i("result", "=========支付上track============="
				+ PosData.getPosData().getTrack());
		params.put("track", PosData.getPosData().getTrack());
		// params.put("track", track);
		params.put("pinblk", cardPwd);
		params.put("random", PosData.getPosData().getRandom());
		params.put("mediaType", PosData.getPosData().getMediaType());
		params.put("period", PosData.getPosData().getPeriod());
		params.put("icdata", PosData.getPosData().getIcdata());
		params.put("crdnum", PosData.getPosData().getCrdnum());
		params.put("custId", MApplication.mSharedPref
				.getSharePrefString(SharedPrefConstant.MERCHANTID));
		params.put("mac", "");
		params.put("imgurl", imageName);
		params.put("custMobile", MApplication.mSharedPref
				.getSharePrefString(SharedPrefConstant.USERNAME));
		params.put("token", MApplication.mSharedPref
				.getSharePrefString(SharedPrefConstant.TOKEN));

		// params.put("areaCode", code);
		String jsonAll = MyJson.getRequest(params);
		Log.i("result", "=========支付上传数据=============" + jsonAll);

		RequestParams requestParams = new RequestParams();
		StringEntity bodyEntity;
		try {
			bodyEntity = new StringEntity(jsonAll, "UTF-8");
			requestParams.setBodyEntity(bodyEntity);
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// httpClient.post(MApplication.mApplicationContext, url, requestParams,
		// responseHandler);
		Log.i("result", "=========支付上传requestParams============="
				+ requestParams.toString());
		HttpUtils utils = new HttpUtils();
		utils.configSoTimeout(1000 * 60);
		utils.send(HttpMethod.POST, MyUrls.POS_PAY, requestParams,
		// utils.send(HttpMethod.POST, MyUrls.POS_PAY_NFC, requestParams,
				new RequestCallBack<String>() {

					@Override
					public void onFailure(HttpException arg0, String arg1) {
						dismissLoadingDialog();
						showDialog("操作超时");
					}

					@Override
					public void onSuccess(ResponseInfo<String> response) {
						// TODO Auto-generated method stub
						dismissLoadingDialog();
						String strReturnLogin = response.result;
						Log.i("result", "=========strReturnLogin============="
								+ strReturnLogin);
						// showDialog("支付"+strReturnLogin);
						try {
							JSONObject obj = new JSONObject(strReturnLogin);
							String code = obj.optJSONObject("REP_BODY")
									.optString("RSPCOD");
							String rspmsg = obj.optJSONObject("REP_BODY")
									.optString("RSPMSG");

							PosData.getPosData().clearPosData();
							Intent it = new Intent(mContext,
									ShowMsgActivity.class);
							it.setAction("ACTION_CASH_IN");
							it.putExtra("code",
									code.substring(code.length() - 2));
							it.putExtra("msg", rspmsg);
							it.putExtra("type", "00");
							it.putExtra("payType", payType);
							startActivity(it);
							finish();
							Log.i("result", "----------请求--支付-成功--------"
									+ strReturnLogin);
						} catch (JSONException e) {
							e.printStackTrace();
							// showDialog("数据解析失败");
						}
					}
				});
	}
}
