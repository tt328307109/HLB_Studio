package com.lk.qf.pay.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import com.lk.bhb.pay.R;
import com.lk.pay.communication.AsyncHttpResponseHandler;
import com.lk.qf.pay.adapter.BankCardListViewAdapter;
import com.lk.qf.pay.beans.BankCardItem;
import com.lk.qf.pay.beans.BasicResponse;
import com.lk.qf.pay.golbal.MApplication;
import com.lk.qf.pay.golbal.Urls;
import com.lk.qf.pay.golbal.User;
import com.lk.qf.pay.sharedpref.SharedPrefConstant;
import com.lk.qf.pay.tool.Logger;
import com.lk.qf.pay.tool.MyHttpClient;
import com.lk.qf.pay.tool.T;
import com.lk.qf.pay.utils.MyUtilss;


public class AccountInfoActivity extends BaseActivity {

	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.account_info);
		bindViews();
	}
	  // Content View Elements

    private Button mBtn_back;
    private TextView mTv_info_account;
    private TextView mTv_info_name;
    private TextView bankNameText;
    private TextView mTv_info_bcno;
	private List<BankCardItem> bankList = new ArrayList<BankCardItem>();
    // End Of Content View Elements

    private void bindViews() {

        mBtn_back = (Button) findViewById(R.id.btn_back);
//        mTv_info_account = (TextView) findViewById(R.id.tv_info_account);
        mTv_info_name = (TextView) findViewById(R.id.tv_info_name);
        bankNameText = (TextView) findViewById(R.id.tv_info_bank_name);
        mTv_info_bcno = (TextView) findViewById(R.id.tv_info_bcno);
//        mTv_info_account.setText(MApplication.mSharedPref.getSharePrefInteger(SharedPrefConstant.MERNAME));
       
        mTv_info_name.setText(MApplication.mSharedPref.getSharePrefInteger(SharedPrefConstant.USER_BANK_NAME));
        bankNameText.setText(MApplication.mSharedPref.getSharePrefInteger(SharedPrefConstant.MERNAME));
        mBtn_back.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
            finish();				
			}
		});
        findViewById(R.id.accnount_info_confirm_btn).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				finish();	
				
			}
		});
//        getBankCardStatus();
    }

//    private void getBankCardStatus() {
//		HashMap<String, String> requestMap = new HashMap<String, String>();
//		MyHttpClient.post(AccountInfoActivity.this, Urls.GET_BANKCARD_LIST, requestMap,
//				new AsyncHttpResponseHandler() {
//
//					@Override
//					public void onSuccess(int statusCode, Header[] headers,
//							byte[] responseBody) {
//						Logger.json("[BankCardList]", responseBody);
//						try {
//							BasicResponse r = new BasicResponse(responseBody)
//									.getResult();
//							if (r.isSuccess()) {
//								JSONArray array = r.getJsonBody().optJSONArray(
//										"bankCardList");
//								
//								for (int i = 0; i < array.length(); i++) {
//									JSONObject temp = array.getJSONObject(i);
//									BankCardItem item = new BankCardItem();
//									item.setCardName(temp.optString("issnam"));
//									item.setCardNo(temp.optString("cardNo"));
//									bankList.add(item);
//								}
//							   if(bankList.size()>0){
//								   bankNameText.setText(bankList.get(0).getCardName());
//								   mTv_info_bcno.setText(Utils.hiddenCardNo(bankList.get(0).getCardNo()));
//							   }else{
//								   bankNameText.setText("--");
//								   mTv_info_bcno.setText("--");
//							   }
//								
//							} else {
//								T.ss(r.getMsg());
//							}
//						} catch (JSONException e) {
//							// TODO 自动生成的 catch 块
//							e.printStackTrace();
//						}
//
//					}
//
//					@Override
//					public void onFailure(int statusCode, Header[] headers,
//							byte[] responseBody, Throwable error) {
//                      networkError(statusCode, error);
//					}
//
//					@Override
//					public void onStart() {
//						super.onStart();
//						showLoadingDialog();
//					}
//
//					@Override
//					public void onFinish() {
//						// TODO 自动生成的方法存根
//						super.onFinish();
//						dismissLoadingDialog();
//					}
//				});
//
//	}
    private String sub(String s){
    	if(null==s)
    		return "";
    	if(s.length()>4){
    		return s.substring(s.length()-4, s.length());
    	}else{
    		return "";
    	}
    }
	private String toS(String s){
		if(null==s)
			return "";
		return s;
	}
}
