package com.lk.qf.pay.activity.swing;

import java.util.HashMap;

import org.apache.http.Header;
import org.json.JSONException;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.lk.bhb.pay.R;
import com.lk.pay.communication.AsyncHttpResponseHandler;
import com.lk.pay.utils.PinDes;
import com.lk.qf.pay.activity.BaseActivity;
import com.lk.qf.pay.activity.MainTabActivity;
import com.lk.qf.pay.activity.ShowMsgActivity;
import com.lk.qf.pay.beans.BasicResponse;
import com.lk.qf.pay.beans.Entity;
import com.lk.qf.pay.beans.PosData;
import com.lk.qf.pay.golbal.MApplication;
import com.lk.qf.pay.golbal.Urls;
import com.lk.qf.pay.golbal.User;
import com.lk.qf.pay.tool.Logger;
import com.lk.qf.pay.tool.MyHttpClient;
import com.lk.qf.pay.tool.T;

public class PayByCardConfirmActivity extends BaseActivity implements
		OnClickListener {

	private EditText etPwd;
	private PopupWindow popupWindow;
	private Button btnPay;
	// 持卡人手机号
	private String mobile;
	private LinearLayout pwdLayout;
	private EditText mobileEdit;

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.pay_by_card_confirm);
		initView();
		downloadPineky();
	}

	TextView tv;

	private void initView() {
		pwdLayout = (LinearLayout) findViewById(R.id.pwd_layout);
		btnPay = (Button) findViewById(R.id.btn_pay_by_card_confirm_pay);
		etPwd = (EditText) findViewById(R.id.tv_pay_by_card_confirm_card_pwd);
		mobileEdit = (EditText) findViewById(R.id.tv_pay_order_show_order_phone);
		mobileEdit.setText(User.uAccount);
		TextView tvOrderNumber = (TextView) findViewById(R.id.tv_pay_order_show_order_number);
		// img_one = (ImageView) findViewById(R.id.img_one);
		// img_two = (ImageView) findViewById(R.id.img_two);
		tvOrderNumber.setText(PosData.getPosData().getPrdordNo());
		tv = (TextView) findViewById(R.id.textView1);
		btnPay.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				gotoPay();
			}
		});

	}

	@Override
	public void onClick(View v) {
		if (v.getId() == R.id.btn_pay_by_card_confirm_pay) {
			
			gotoPay();
		}
	}

	private void gotoPay() {
		
		String payPwd=etPwd.getText().toString();
		if(TextUtils.isEmpty(payPwd)){
			T.ss("请输入支付密码");
			return;
		}
		if(payPwd.length()<6){
			T.ss("密码一般为6位数字");
			return;
		}
		String pinkey = PosData.getPosData().getPinKey();
		System.out.println("pinkey--->" + pinkey);
		if(TextUtils.isEmpty(pinkey)){
			downloadPineky();

		} else {
			String pinblk= null;
			try {
				pinblk = PinDes.pinResultMak(PinDes.ZMK, pinkey,"", payPwd);
			} catch (Exception e) {
				
			}
			
			HashMap<String, String> params = new HashMap<String, String>();
			params.put("prdordNo", PosData.getPosData().getPrdordNo());
			params.put("payType", PosData.getPosData().getPayType());
			params.put("rateType", PosData.getPosData().getRate());
			params.put("termNo",PosData.getPosData().getTermNo());
			params.put("termType", PosData.getPosData().getTermType());
			params.put("payAmt", PosData.getPosData().getPayAmt());

			params.put("track", PosData.getPosData().getTrack());
			params.put("pinblk", pinblk);
			params.put("random", PosData.getPosData().getRandom());
			params.put("mediaType", PosData.getPosData().getMediaType());
			params.put("period", PosData.getPosData().getPeriod());
			params.put("icdata", PosData.getPosData().getIcdata());
			params.put("crdnum", PosData.getPosData().getCrdnum());
			params.put("mac", "");
			/*String temp = "";
			for (String key : params.keySet()) {
				temp += "[" + key + "]  =  " + params.get(key) + "\n";
			}
			tv.setText(temp);*/
			MyHttpClient.post(PayByCardConfirmActivity.this, Urls.TRADE_PAY,
					params, new AsyncHttpResponseHandler() {

						@Override
						public void onSuccess(int statusCode, Header[] headers,
								byte[] responseBody) {
							Logger.json(responseBody);
							try {
								BasicResponse r = new BasicResponse(responseBody)
										.getResult();
								if (r.isSuccess()) {
									T.showCustomeOk(PayByCardConfirmActivity.this,
											"交易成功!",3500);
									startActivity(new Intent(PayByCardConfirmActivity.this,MainTabActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
									finish();
								} else {
									T.ss(r.getMsg());
									showMsg(r.getMsg().toString());
									finish();
								}
							} catch (JSONException e) {
								// TODO 自动生成的 catch 块
								e.printStackTrace();
							}
							//tv.setText(tv.getText() + new String(responseBody));
						}

						@Override
						public void onFailure(int statusCode, Header[] headers,
								byte[] responseBody, Throwable error) {
							T.ss(error.getMessage());
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
	
	private void showMsg( String msg) {
		Intent it = new Intent(this, ShowMsgActivity.class);
		it.putExtra("msg", msg);
		startActivity(it);
	}

	private void downloadPineky(){
		//终端签到
				HashMap<String, String> params1 = new HashMap<String, String>();
				params1.put("termNo", PosData.getPosData().getTermNo());
				params1.put("termType", PosData.getPosData().getTermType());
				MyHttpClient.post(this, Urls.BLUETOOTH_SIGN, params1,
						new AsyncHttpResponseHandler() {

							@Override
							public void onSuccess(int statusCode, Header[] headers,
									byte[] responseBody) {
								try {
									BasicResponse re = new BasicResponse(responseBody)
											.getResult();
									if(re.isSuccess()){
										String pinkey=re.getJsonBody().optString("zpinkey");
										PosData.getPosData().setPinKey(pinkey);
										
									}else{
										T.ss(re.getMsg());
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
}
