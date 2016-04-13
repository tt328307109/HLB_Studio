package com.lk.qf.pay.activity;

import java.util.HashMap;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.lk.bhb.pay.R;
import com.lk.pay.communication.AsyncHttpResponseHandler;
import com.lk.qf.pay.adapter.DealRecordAdapter;
import com.lk.qf.pay.beans.BasicResponse;
import com.lk.qf.pay.beans.PosData;
import com.lk.qf.pay.beans.TradeBean;
import com.lk.qf.pay.golbal.Constant;
import com.lk.qf.pay.golbal.Urls;
import com.lk.qf.pay.golbal.User;
import com.lk.qf.pay.tool.AsyncImageLoader;
import com.lk.qf.pay.tool.AsyncImageLoader.ImageCallback;
import com.lk.qf.pay.tool.Logger;
import com.lk.qf.pay.tool.MyHttpClient;
import com.lk.qf.pay.tool.T;
import com.lk.qf.pay.utils.AmountUtils;
import com.lk.qf.pay.utils.MyUtilss;
import com.lk.qf.pay.wedget.CircularImage;
import com.lk.qf.pay.wedget.CommonTitleBar;

public class SalesSlipActivity extends BaseActivity implements OnClickListener{

	private CircularImage signImage;
	private int height, width;
	private TradeBean detail;
	private TextView merchantNameText, merchantNoText, ordIdText, termIdText,
			cardNoText, ordDateText, ordAmtText;
	private String userAccount, userName;
	private Button withdraw;
	private HashMap<String, String> params;
	private AsyncImageLoader imageDownloader = new AsyncImageLoader();
	private Context ctx;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.tradesales_slip);
		detail = (TradeBean) getIntent().getSerializableExtra("data");
		init();
		//initDate();
	}

	private void init() {
		((CommonTitleBar) findViewById(R.id.titlebar_sales))
				.setCanClickDestory(this, true);
		merchantNameText = (TextView) findViewById(R.id.slip_merchant_name);
		merchantNoText = (TextView) findViewById(R.id.slip_merchant_no);
		ordIdText = (TextView) findViewById(R.id.slip_ord_id);
		termIdText = (TextView) findViewById(R.id.slip_term_no);
		cardNoText = (TextView) findViewById(R.id.slip_card_no);
		ordDateText = (TextView) findViewById(R.id.slip_ord_date);
		ordAmtText = (TextView) findViewById(R.id.slip_ord_amt);
		withdraw = (Button) findViewById(R.id.btn_my_account_withdraw);
		withdraw.setOnClickListener(this);
		merchantNameText.setText(User.uName);
		merchantNoText.setText(User.uAccount);
		ordIdText.setText(tos(detail.getAgentId()));
		termIdText.setText(tos(detail.getTerNo()));
		
		cardNoText.setText(MyUtilss.hiddenCardNo(detail.getBankCardNo()));
		ordDateText.setText(detail.getTarnTime());
		ordAmtText.setText(tos(detail.getTranAmt()) + "元");
		signImage = (CircularImage) findViewById(R.id.sales_slip_image);
		String imageUrl = detail.getSignPath();
		
		System.out.println("-------------------------------------->>>"+imageUrl);
		if (imageUrl != null && !imageUrl.equals("")) {
			Drawable cachedImage = imageDownloader.loadDrawable(
					Urls.ROOT_URL + imageUrl, new ImageCallback() {

						@Override
						public void imageLoaded(Drawable imageDrawable,
								String imageUrl) {
							if (imageDrawable != null) {
								signImage.setImageDrawable(imageDrawable);
							}
						}
					});
			if (cachedImage != null) {
				signImage.setImageDrawable(cachedImage);
			}
		}
	}
	
	private void cashOut(){
		TradeBean bean = new TradeBean();
		params = new HashMap<String, String>();
		params.put("custId",detail.getAgentId());
		params.put("cardNO",detail.getBankCardNo());
		MyHttpClient.post(ctx, Urls.WITHFRAWBAWHITELIST, params,
				new AsyncHttpResponseHandler() {

					@Override
					public void onSuccess(int statusCode, Header[] headers,
							byte[] responseBody) {
						try {
							BasicResponse r = new BasicResponse(responseBody)
									.getResult();
							if (r.isSuccess()) {
								//T.ss(r.getMsg());
								System.out.println("获取白名单成功");
								initwithfrew();
							} else {
//								System.out.println("获取白名单失败");
//								Intent in = new Intent(SalesSlipActivity.this,WithdrawDepositActivity.class);
//								in.putExtra("custId", detail.getAgentId());
//								in.putExtra("cardNO", detail.getBankCardNo());
//								in.putExtra("PrdOrdNO",detail.getPrdOrdNO());
//								in.putExtra("TranAmt",detail.getTranAmt());
//								startActivity(in);
							}
						} catch (JSONException e) {
							e.printStackTrace();
						}

					}

					@Override
					public void onStart() {

						showLoadingDialog();

					}

					@Override
					public void onFinish() {

						dismissLoadingDialog();

					}

					@Override
					public void onFailure(int statusCode, Header[] headers,
							byte[] responseBody, Throwable error) {
						networkError(statusCode, error);
					}
				});
	}

	private String tos(String str) {
		if (null == str)
			return "";
		return str;
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_my_account_withdraw:
			cashOut();
			break;

		default:
			break;
		}
		
	}
	
	
	private void initwithfrew() {
		// loading.setIsLoading("加载中...");
		TradeBean bean = new TradeBean();
		params = new HashMap<String, String>();
		params.put("custId",detail.getAgentId());
		//params.put("prdOrdNO",detail.getPrdOrdNO());
		params.put("txamt", detail.getTranAmt());
		params.put("casType", "00");
		System.out.println("--------------------------------->>>"+params.toString());
		MyHttpClient.post(ctx, Urls.WITHFRAW, params,
				new AsyncHttpResponseHandler() {

					@Override
					public void onSuccess(int statusCode, Header[] headers,
							byte[] responseBody) {
						System.out.println("提现");
						try {
							BasicResponse re = new BasicResponse(responseBody).getResult();
							if(re.isSuccess()){
								T.showCustomeOk(SalesSlipActivity.this,
										"已提交审核", 3500);
								//T.ss(re.getMsg());
							}else{
								T.ss(re.getMsg());
							}
						} catch (JSONException e) {
							
							e.printStackTrace();
						}

					}

					@Override
					public void onStart() {

						showLoadingDialog();

					}

					@Override
					public void onFinish() {

						dismissLoadingDialog();

					}

					@Override
					public void onFailure(int statusCode, Header[] headers,
							byte[] responseBody, Throwable error) {
						networkError(statusCode, error);
					}
				});

	}

}
