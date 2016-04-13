package com.lk.qf.pay.fragment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import com.lk.bhb.pay.R;
import com.lk.pay.communication.AsyncHttpResponseHandler;
import com.lk.qf.pay.activity.CashInActivity;
import com.lk.qf.pay.activity.TradeListActivity;
import com.lk.qf.pay.beans.BasicResponse;
import com.lk.qf.pay.golbal.Urls;
import com.lk.qf.pay.golbal.User;
import com.lk.qf.pay.tool.Logger;
import com.lk.qf.pay.tool.MyHttpClient;
import com.lk.qf.pay.tool.T;
import com.lk.qf.pay.utils.AmountUtils;
import com.lk.qf.pay.wedget.flashview.FlashView;
import com.lk.qf.pay.wedget.flashview.constants.EffectConstants;

public class MainFragment extends BaseFragment implements OnClickListener {

	private int[] ids = new int[] {
			R.id.function_menu_0,
			R.id.function_menu_1,
			R.id.function_menu_2,
			R.id.function_menu_3, 
			R.id.function_menu_4,
			R.id.function_menu_5};
	private View view;
	private FlashView flash;
	private List<String> imgs = new ArrayList<String>();
	private Context mContext;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//imgs.add("http://210.22.153.30:8098/mpcctp/file/upload/attachment/150421/150421000462.png");
		//imgs.add("http://210.22.153.30:8098/mpcctp/file/upload/attachment/150424/150424000617.jpg");
		
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		view = inflater.inflate(R.layout.fragment_main_layout, null);
		mContext = getActivity();
		getAdUrl(); 
		//getUserInfo();
		return view;
	}

	private void initView() {
		for (int i = 0; i < ids.length; i++) {
			view.findViewById(ids[i]).setOnClickListener(this);
		}
		flash = (FlashView) view.findViewById(R.id.cycleview_main);
		flash.setImageUris(imgs);
		flash.setEffect(EffectConstants.DEFAULT_EFFECT);
	}

	@Override
	public void onClick(View v) {
		
		switch (v.getId()) {
		case R.id.function_menu_0:
			if(User.cardNum==0){
				com.lk.qf.pay.tool.T.ss("请先绑定银行卡");
				return;
			}
			Intent cashInIntent = new Intent(mContext,CashInActivity.class);
			startActivity(cashInIntent);
			break;
		case R.id.function_menu_1:
			/*if (MApplication.mApplicationContext.chechStatus())//实名认证
				if(User.termNum==0){//绑定刷卡器
					T.ss("请先绑定刷卡器！");
					return;
				}else{
					Intent balanceIntent = new Intent(mContext,CardBalanceActivity.class);
					startActivity(balanceIntent);
				}
			else{
				return;
			}*/
			com.lk.qf.pay.tool.T.ss("暂未开通");
			break;
		case R.id.function_menu_4:
			Intent tradeIntent = new Intent(mContext, TradeListActivity.class);
			startActivity(tradeIntent);
			break;
		default:
			return;
			// break;
		}
	}
	
	private void getAdUrl() {
		imgs.clear();
		MyHttpClient.post(getActivity(), Urls.MAIN_AD_IMG,
				new HashMap<String, String>(), new AsyncHttpResponseHandler() {

					@Override
					public void onSuccess(int statusCode, Header[] headers,
							byte[] responseBody) {
						Logger.json(responseBody);
						try {
							BasicResponse json = new BasicResponse(responseBody)
									.getResult();
							if (json.isSuccess()) {
								JSONArray array = json.getJsonBody().optJSONArray("imgList");
								for (int i = 0; i < array.length(); i++) {
									String temp = array.getJSONObject(i)
											.optString("appimgPath");
									if (!TextUtils.isEmpty(temp)) {
										imgs.add(Urls.ROOT_URL + temp);
										
									}
								}
							}
						} catch (JSONException e) {
							e.printStackTrace();
						}

					}

					@Override
					public void onFailure(int statusCode, Header[] headers,
							byte[] responseBody, Throwable error) {

					}
					@Override
					public void onFinish() {
						super.onFinish();
						initView();
					}
				});

	}
	
	private void getUserInfo() {
		HashMap<String, String> map = new HashMap<String, String>();
		map.put("custMobile", User.uAccount);
		MyHttpClient.post(getActivity(), Urls.GET_USER_INFO, map,
				new AsyncHttpResponseHandler() {

					@Override
					public void onSuccess(int statusCode, Header[] headers,
							byte[] responseBody) {
						if (responseBody != null) {
							Logger.json(new String(responseBody));
							try {
								
								JSONObject json = new JSONObject(new String(responseBody))
										.getJSONObject("REP_BODY");
								if (json.getString("RSPCOD").equals("000000")) {
									User.uName = json.optString("custName");
									User.cardNum = json.optInt("cardNum");
									User.termNum = json.optInt("termNum");
									User.uStatus = json.optInt("custStatus");
									User.cardBundingStatus=json.optInt("cardBundingStatus");
								} else {
									showDialog(json.getString("RSPMSG"));
								}
								
							} catch (JSONException e) {
								e.printStackTrace();
							}

						}
					}
					
					@Override
					public void onFailure(int statusCode, Header[] headers,
							byte[] responseBody, Throwable error) {
						T.showCustomeShort(mContext, "网络错误");
					}

				});
	}
	
	private void queryBalance() {

		MyHttpClient.post(mContext, Urls.QUERY_BALANCE,
				new HashMap<String, String>(), new AsyncHttpResponseHandler() {

					@Override
					public void onSuccess(int statusCode, Header[] headers,
							byte[] responseBody) {
						Logger.json("[余额查询]", responseBody);
						try {
							BasicResponse r = new BasicResponse(responseBody)
									.getResult();
							if (r.isSuccess()) {
								JSONObject obj = r.getJsonBody();
								User.amtT0 = AmountUtils.changeFen2Yuan(obj
										.optString("acT0"));
								User.amtT1 = AmountUtils.changeFen2Yuan(obj
										.optString("acT1"));
								User.amtT1y = AmountUtils.changeFen2Yuan(obj
										.optString("acT1Y"));
								User.totalAmt = AmountUtils.changeFen2Yuan(obj
										.optString("acBal"));
								/*tv_detail.setText("已结算金额: " + User.amtT1y
										+ "元 | 未结算金额: " + User.amtT1 + "元");*/
								//balanceText.setText(User.totalAmt+"元");
							} else {
								T.ss(r.getMsg());
							}
						} catch (JSONException e) {
							T.showCustomeShort(mContext, "数据错误");
						}
					}

					@Override
					public void onFailure(int statusCode, Header[] headers,
							byte[] responseBody, Throwable error) {
						T.showCustomeShort(mContext, "网络错误");
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
