package com.lk.qf.pay.activity.swing;

import java.util.HashMap;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;

import com.lk.bhb.pay.R;
import com.lk.pay.communication.AsyncHttpResponseHandler;
import com.lk.pay.utils.PinDes;
import com.lk.qf.pay.activity.BaseActivity;
import com.lk.qf.pay.activity.ShowMsgActivity;
import com.lk.qf.pay.beans.BasicResponse;
import com.lk.qf.pay.beans.CardBalance;
import com.lk.qf.pay.beans.PosData;
import com.lk.qf.pay.golbal.Urls;
import com.lk.qf.pay.tool.MyHttpClient;
import com.lk.qf.pay.utils.AmountUtils;
import com.lk.qf.pay.utils.MyUtilss;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.TextView;

public class CardBalanceConfirmActivity extends BaseActivity implements OnClickListener{
	
	private TextView cardNoText;
	private EditText cardPwdEdit;
	private String cardPwd;
	private Context mContext;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.balance_confirm);
		mContext = this;
		init();
	}

	private void init() {
		cardNoText = (TextView) findViewById(R.id.cashin_card_no_text);
		cardPwdEdit = (EditText) findViewById(R.id.cash_bank_pwd_edit);
		cardNoText.setText(MyUtilss.hiddenCardNo(PosData.getPosData().getCardNo()));

		findViewById(R.id.btn_back).setOnClickListener(this);
		findViewById(R.id.balance_confirm_btn).setOnClickListener(this);
		
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.balance_confirm_btn:
			cardPwd = cardPwdEdit.getText().toString().trim();
			if(TextUtils.isEmpty(cardPwd)){
				showDialog(getResources().getString(R.string.inputbankCardPwd));
			} else if(cardPwd.length() != 6){
				showDialog("银行卡密码长度应为6位数");
			} else {
				downloadPineky();
			}
			break;
		case R.id.btn_back:
			finish();
			break;
		default:
			break;
		}
		
	}
	
	private void downloadPineky(){
		//终端签到
		HashMap<String, String> params = new HashMap<String, String>();
		params.put("termNo", PosData.getPosData().getTermNo());
		params.put("termType", PosData.getPosData().getTermType());
		MyHttpClient.post(this, Urls.BLUETOOTH_SIGN, params,
				new AsyncHttpResponseHandler() {

					@Override
					public void onSuccess(int statusCode, Header[] headers,
							byte[] responseBody) {
						try {
							BasicResponse re = new BasicResponse(responseBody).getResult();
							if(re.isSuccess()){
								String pinkey=re.getJsonBody().optString("zpinkey");
								goPay(pinkey);
							}else{
								showDialog(re.getMsg());
							}
						} catch (JSONException e) {
							
							e.printStackTrace();
						}
					}

					@Override
					public void onFailure(int statusCode, Header[] headers,
							byte[] responseBody, Throwable error) {
						showDialog(responseBody.toString());

					}
					@Override
					public void onStart() {
						super.onStart();
						showLoadingDialog();
					}
					@Override
					public void onFinish() {
						super.onFinish();
						dismissLoadingDialog();
					}
			});		
	}
	
	private void goPay(String pinkey){
		try {
			cardPwd = PinDes.pinResultMak(PinDes.PINZMK, pinkey, "", cardPwd);
		} catch (Exception e) {
			
		}
		HashMap<String, String> params = new HashMap<String, String>();
		params.put("termNo",PosData.getPosData().getTermNo());
		params.put("termType", PosData.getPosData().getTermType());
		params.put("track", PosData.getPosData().getTrack());
		params.put("pinblk", cardPwd);
		params.put("random", PosData.getPosData().getRandom());
		params.put("mediaType", PosData.getPosData().getMediaType());
		params.put("period", PosData.getPosData().getPeriod());
		params.put("icdata", PosData.getPosData().getIcdata());
		params.put("crdnum", PosData.getPosData().getCrdnum());
		params.put("mac", "");
		MyHttpClient.post(mContext, Urls.CARD_QUERY,
				params, new AsyncHttpResponseHandler() {

					@Override
					public void onSuccess(int statusCode, Header[] headers,
							byte[] responseBody) {
//						Logger.json(responseBody);
						try {
							PosData.getPosData().clearPosData();
							CardBalance r = new CardBalance(responseBody).getResult();
							Intent it = new Intent(mContext, ShowMsgActivity.class);
							it.setAction("ACTION_CARD_QUERY");
							if (r.isSuccess()) {
								it.putExtra("code", true);
								String balance = r.getBalance();
								balance = AmountUtils.changeFen2Yuan(balance);
								it.putExtra("msg", balance + "元");
							} else {
								it.putExtra("code", false);
								it.putExtra("msg", r.getMsg());
							}
							startActivity(it);
							finish();
						} catch (JSONException e) {
							e.printStackTrace();
							showDialog("数据解析失败");
						}
						
					}

					@Override
					public void onFailure(int statusCode, Header[] headers,
							byte[] responseBody, Throwable error) {
						showDialog(error.getMessage());
					}

					@Override
					public void onStart() {
						super.onStart();
						showLoadingDialog();
					}

					@Override
					public void onFinish() {
						super.onFinish();
						dismissLoadingDialog();
					}
				});
	}
	
}
