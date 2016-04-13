package com.lk.qf.pay.v50;

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
import com.lk.qf.pay.activity.BaseActivity;
import com.lk.qf.pay.activity.ShowMsgActivity;
import com.lk.qf.pay.beans.PosData;
import com.lk.qf.pay.golbal.MApplication;
import com.lk.qf.pay.golbal.MyUrls;
import com.lk.qf.pay.sharedpref.SharedPrefConstant;
import com.lk.qf.pay.utils.AmountUtils;
import com.lk.qf.pay.utils.MyJson;
import com.lk.qf.pay.utils.MyUtilss;
import com.lk.qf.pay.wedget.CommonTitleBar;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.LinearLayout.LayoutParams;

public class V50CashInConfirmActivity extends BaseActivity implements
		OnClickListener {

	private TextView cardNoText, payAmtText, payRateText;
	private String cardPwd;
	private Context mContext;
	private final String[] rates = new String[] { "民生类", "批发类" };
	private int rate = 0;
	HashMap<String, String> map;
	private String imageName;
	private CommonTitleBar title;
	private String payType="";
	private String type="01";
	private LinearLayout llRate;
	private String rateType="1";
	

	// List<Record> records = null;
	// private String code;
	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.cashin_v50_layout);

		mContext = this;
		init();
	}

	private void init() {
		cardNoText = (TextView) findViewById(R.id.cashin_card_no_text_v50);
		payAmtText = (TextView) findViewById(R.id.cashin_pay_amt_text_v50);
		cardNoText
				.setText(MyUtilss.hiddenCardNo(PosData.getPosData().getCardNo()));
		payAmtText.setText(AmountUtils.changeFen2Yuan(PosData.getPosData()
				.getPayAmt()) + "元");
		payRateText = (TextView) findViewById(R.id.cashin_pay_rate_text_v50);
		llRate = (LinearLayout) findViewById(R.id.ll_cashin_rate);
		payRateText.setText(rates[rate]);
		payRateText.setOnClickListener(this);
		// findViewById(R.id.btn_back).setOnClickListener(this);
		findViewById(R.id.cashin_confirm_btn_v50).setOnClickListener(this);
		
		Intent intent = getIntent();
		if (intent != null) {
			imageName = intent.getStringExtra("imagename");
		}
		title = (CommonTitleBar) findViewById(R.id.titlebar_confirm_v50);
		title.setCanClickDestory(this, true);
		title.setActName("支付");

		payType = PosData.getPosData().getPayType();
		Log.i("result", "----------payType-------"+payType);
		if (payType!=null) {
			
			if (payType.equals("00") || payType.equals("03")||payType.equals("07")||payType.equals("08")) {
				type = "00";
				llRate.setVisibility(View.GONE);
			}
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.cashin_confirm_btn_v50:
			goPay();
			break;
		case R.id.cashin_pay_rate_text_v50:
			queryRate();
			break;
		default:
			break;
		}
	}

	private void goPay() {
		showLoadingDialog();
		try {
			// cardPwd = PinDes.pinResultMak(PinDes.PINZMK, pinkey, "",
			// cardPwd);D35BFBAB582CF7FB9ECBA164EA32D016
			String cardNO = PosData.getPosData().getCardNo();
			// 加密密码
			// cardPwd =
			// PinDKey.pinResultMak("6B97E9D9F21F016BBF01DF9BDACD3238",
			// pinkey, cardNO, cardPwd);
		} catch (Exception e) {

		}
		System.out.println("cardPwd--->" + cardPwd);
		if (rate == 0) {
			rateType="1";
		} else if (rate == 1) {
			rateType = "4";
		}
		HashMap<String, String> params = new HashMap<String, String>();
		String prdordNo = PosData.getPosData().getPrdordNo();
		if (prdordNo==null || prdordNo.equals("")) {
			prdordNo="";
		}
		params.put("prdordNo", prdordNo);
		
		payType = PosData.getPosData().getPayType();//03是闪电收款  07为理财
		String payAmt = PosData.getPosData().getPayAmt();//金额
		
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
		
		params.put("payType", PosData.getPosData().getPayType());
		params.put("rateType", rateType);
		params.put("termNo", PosData.getPosData().getTermNo());
		params.put("termType", PosData.getPosData().getTermType());
		params.put("payAmt", PosData.getPosData().getPayAmt());
		params.put("sysType", "Android");

		Log.i("result", "=========支付上track============="
				+ PosData.getPosData().getTrack());
		params.put("track", PosData.getPosData().getTrack());
//		params.put("pinblk", "F4CB29D0736B4130");
		params.put("pinblk", PosData.getPosData().getPinblok());
		params.put("random", "");
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
							it.putExtra("type", type);
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

	private void queryRate() {
		Dialog dialog = new AlertDialog.Builder(this)
				.setTitle("选择费率类型")
				.setSingleChoiceItems(rates, 0,
						new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								rate = which;
								payRateText.setText(rates[rate]);
								dialog.dismiss();

							}
						}).create();
		dialog.show();
	}

}
