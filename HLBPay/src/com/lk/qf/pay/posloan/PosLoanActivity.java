package com.lk.qf.pay.posloan;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.entity.StringEntity;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.alibaba.fastjson.JSON;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;
import com.lk.bhb.pay.R;
import com.lk.qf.pay.aanewactivity.ConvenienceServiceActivity;
import com.lk.qf.pay.aanewactivity.MyWebViewActivity;
import com.lk.qf.pay.aanewactivity.creditcard.CreditCardsListActivity;
import com.lk.qf.pay.activity.BaseActivity;
import com.lk.qf.pay.golbal.MApplication;
import com.lk.qf.pay.golbal.MyUrls;
import com.lk.qf.pay.sharedpref.SharedPrefConstant;
import com.lk.qf.pay.tool.T;
import com.lk.qf.pay.wedget.CommonTitleBar;
import com.lk.qf.pay.wedget.view.AutoPagerView;
import com.msafepos.sdk.PBOC.TSIBit;
import com.squareup.picasso.Picasso;

public class PosLoanActivity extends BaseActivity implements OnClickListener {

	private CommonTitleBar title;
	private String isPosloanPer, phoneNum;
	private AutoPagerView pagerView;
	private List<String> adList;
	/**
	 * 查询轮播图code
	 */
	private String code1 = "";
	/**
	 * 查询轮播图message1
	 */
	private String message1 = "";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.posloan_layout);
		adList = new ArrayList<String>();
		init();// ISPOSLOANPER
		getAdList();
	}

	private void init() {
		isPosloanPer = MApplication.mSharedPref
				.getSharePrefString(SharedPrefConstant.ISPOSLOANPER);
		phoneNum = MApplication.mSharedPref
				.getSharePrefString(SharedPrefConstant.USERNAME);

		title = (CommonTitleBar) findViewById(R.id.titlebar_posLoan_title1);
		title.setActName("易贷");
		title.setCanClickDestory(this, true);
		findViewById(R.id.ib_posloan_details).setOnClickListener(this);
		findViewById(R.id.ib_posloan_apply).setOnClickListener(this);
		findViewById(R.id.btn_myPosloan).setOnClickListener(this);//我的贷款
		findViewById(R.id.ib_good_loan).setOnClickListener(this);//真好贷
		findViewById(R.id.ib_kaka_Loan).setOnClickListener(this);// 卡卡贷
		findViewById(R.id.ib_loan_polyLoan).setOnClickListener(this);// 保理贷
		// findViewById(R.id.rl_PayDay_loans).setVisibility(View.GONE);
	}

	@Override
	public void onClick(View arg0) {
		// TODO Auto-generated method stub
		Log.i("result", "---------isPosloanPer--------" + isPosloanPer);

		Intent intent = null;
		switch (arg0.getId()) {
		case R.id.ib_good_loan:
			intent = new Intent(PosLoanActivity.this, MyWebViewActivity.class);
			intent.putExtra("titleName", "真好贷");
			intent.putExtra("webAddress",
					"http://h5.faxindai.com/fxdWeb/fxd/v1/registe.action?userName="
							+ phoneNum + "&&tracking=4053");
			break;
		case R.id.ib_posloan_details:
			intent = new Intent(PosLoanActivity.this,
					PosloanIntroduceActivity.class);
			break;
		case R.id.ib_loan_polyLoan:// 保理贷
			intent = new Intent(PosLoanActivity.this,
					PolyLoansTabHostActivity.class);
			break;
		case R.id.ib_posloan_apply:// 快易贷
			if (!isPosloanPer.equals("EN")) {
				T.ss("本功能需连续三个月分润达到500元及以上给予使用!");
				return;
			}
			intent = new Intent(PosLoanActivity.this,
					PosloanBasicActivity.class);
			break;
		case R.id.btn_myPosloan:// 我的贷款
			if (!isPosloanPer.equals("EN")) {
				T.ss("本功能需连续三个月分润达到500元及以上给予使用!");
				return;
			}
			intent = new Intent(PosLoanActivity.this, PosloanListActivity.class);
			break;
		case R.id.ib_kaka_Loan:// 卡卡贷
			
			intent = new Intent(PosLoanActivity.this, CreditCardsListActivity.class);
			break;

		default:
			break;
		}
		startActivity(intent);

	}

	/**
	 * 初始化viewPager
	 */
	private void initGallery() {
		List<View> pagePic = new ArrayList<View>();
		for (int i = 0; i < adList.size(); i++) {
			ImageView imageview = new ImageView(PosLoanActivity.this);

			Picasso.with(PosLoanActivity.this)
					.load(MyUrls.ROOT_URL2 + adList.get(i)).fit()
					.into(imageview);

			// 把List保存到sharedPref中
			SharedPreferences sp = PosLoanActivity.this.getSharedPreferences(
					"VPList_Menue", Context.MODE_PRIVATE);
			SharedPreferences.Editor editor = sp.edit();
			editor.clear().commit();
			editor.putInt("Status_size", adList.size());
			for (int j = 0; j < adList.size(); j++) {
				// editor.remove("Status_" + i);
				editor.putString("Status_" + j, adList.get(j));
			}
			editor.commit();
			pagePic.add(imageview);
		}
		pagerView = (AutoPagerView) findViewById(R.id.auto_pagerview_loan);
		pagerView.setPageViewPics(pagePic);
		pagerView.setOnTouchListener(onTouch);
	}

	/**
	 * 联网失败的时候这样初始化vp
	 */
	private void initGallery2() {
		List<View> pagePic = new ArrayList<View>();
		for (int i = 0; i < adList.size(); i++) {
			ImageView imageview = new ImageView(PosLoanActivity.this);
			Picasso.with(PosLoanActivity.this)
					.load(MyUrls.ROOT_URL2 + adList.get(i)).fit()
					.into(imageview);

			pagePic.add(imageview);
		}
		pagerView = (AutoPagerView) findViewById(R.id.auto_pagerview_loan);
		pagerView.setPageViewPics(pagePic);
		pagerView.setOnTouchListener(onTouch);
	}

	/**
	 * 解析 Json字符串 登录返回结果
	 * 
	 * @param str
	 * @return
	 */
	private void jsonDetailVp(String str) {

		try {
			JSONObject obj = new JSONObject(str);

			code1 = obj.optString("CODE");
			message1 = obj.optString("MESSAGE");
			Log.i("result", "---------22-------");
			// adList = new ArrayList<String>();
			int count = obj.optInt("count");
			if (count > 0) {
				for (int i = 0; i < count; i++) {
					String imgUrl1 = obj.optJSONArray("date").optJSONObject(i)
							.optString("image1");// 图片
					String imgUrl2 = obj.optJSONArray("date").optJSONObject(i)
							.optString("image2");// 图片
					String imgUrl3 = obj.optJSONArray("date").optJSONObject(i)
							.optString("image3");// 图片
					String imgUrl4 = obj.optJSONArray("date").optJSONObject(i)
							.optString("image4");// 图片
					String imgUrl5 = obj.optJSONArray("date").optJSONObject(i)
							.optString("image5");// 图片
					String imgUrl6 = obj.optJSONArray("date").optJSONObject(i)
							.optString("image6");// 图片

					String note = obj.optJSONArray("date").optJSONObject(i)
							.optString("note");// note
					String type = obj.optJSONArray("date").optJSONObject(i)
							.optString("type");// type

					String id = obj.optJSONArray("date").optJSONObject(i)
							.optString("id");// id
					if (imgUrl1.length() > 0) {
						adList.add(imgUrl1);
					}
					if (imgUrl2.length() > 0) {
						adList.add(imgUrl2);
					}
					if (imgUrl3.length() > 0) {
						adList.add(imgUrl3);
					}
					if (imgUrl4.length() > 0) {
						adList.add(imgUrl4);
					}
					if (imgUrl5.length() > 0) {
						adList.add(imgUrl5);
					}
					if (imgUrl6.length() > 0) {
						adList.add(imgUrl6);
					}
					initGallery();
				}
			}

		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	private void getAdList() {
		Map<String, String> map = new HashMap<String, String>();
		RequestParams params = new RequestParams();
		String url = MyUrls.VPLIST;
		map = new HashMap<String, String>();
		map.put("type", "03");
		String json = JSON.toJSONString(map);
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
				// T.ss("查询失败");
				SharedPreferences sp = PosLoanActivity.this
						.getSharedPreferences("VPList_Menue",
								Context.MODE_PRIVATE);
				adList.clear();
				int size = sp.getInt("Status_size", 0);
				if (size != 0) {
					for (int i = 0; i < size; i++) {
						adList.add(sp.getString("Status_" + i, null));
					}
					initGallery2();
				}
			}

			@Override
			public void onSuccess(ResponseInfo<String> response) {
				// TODO Auto-generated method stub
				String strReturnLogin = response.result;
				Log.i("result", "---------------定单-returnjson---"
						+ strReturnLogin);
				jsonDetailVp(strReturnLogin);
				Log.i("result", "---------------11---");
				if (code1.equals("00")) {
				} else {
					T.ss(message1);
				}
			}
		});

	}
	OnTouchListener onTouch = new OnTouchListener() {

		@Override
		public boolean onTouch(View v, MotionEvent event) {

			return true;
		}
	};

}
