package com.lk.qf.pay.aanewactivity.creditcard;

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
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener2;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;
import com.lk.bhb.pay.R;
import com.lk.qf.pay.activity.BaseActivity;
import com.lk.qf.pay.adapter.CreditCardRepayDateAdapter;
import com.lk.qf.pay.beans.Xinyongkainfo;
import com.lk.qf.pay.golbal.MApplication;
import com.lk.qf.pay.golbal.MyUrls;
import com.lk.qf.pay.sharedpref.SharedPrefConstant;
import com.lk.qf.pay.tool.T;
import com.lk.qf.pay.utils.StringUtils;
import com.lk.qf.pay.wedget.CommonTitleBar;
import com.lk.qf.pay.wedget.MyClickListener;

public class CreditCardRepayDateListActivity extends BaseActivity implements
		OnRefreshListener2<ListView> {

	private CommonTitleBar title;
	private PullToRefreshListView lv;
	private List<Xinyongkainfo> list;
	private List<Xinyongkainfo> listReturn;
	private int page = 1;
	private int dataCount = 10;
	private Map<String, String> map;
	private Xinyongkainfo info;
	private CreditCardRepayDateAdapter adapter;
	private Context context;
	private String id;
	private TextView tvShowUnit;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.credit_card_repaydate_list_layout);
		init();
	}

	private void init() {
		title = (CommonTitleBar) findViewById(R.id.titlebar_creditCard_repayDate_list);
		title.setActName("贷款记录");
		title.setCanClickDestory(this, true);
		context = CreditCardRepayDateListActivity.this;
		Intent intent = getIntent();
		if (intent != null) {
			Xinyongkainfo xykInfo = intent.getParcelableExtra("info");
			if (xykInfo != null) {
				id = xykInfo.getId();
			}
		}
		tvShowUnit = (TextView) findViewById(R.id.tv_credit_showYuan1);
		lv = (PullToRefreshListView) findViewById(R.id.myPull_refresh_list_order_creditCard_repayDate);
		lv.setOnRefreshListener(this);
		list = new ArrayList<Xinyongkainfo>();
		adapter = new CreditCardRepayDateAdapter(context, list, mListener);
		lv.setAdapter(adapter);
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		postQueryRecordHttp();
	}

	/**
	 * listview中控件的事件
	 */
	private MyClickListener mListener = new MyClickListener() {
		@Override
		public void myOnClick(int position, View v) {
			Xinyongkainfo info = list.get(position);
			Intent intent = new Intent(CreditCardRepayDateListActivity.this,
					CreditCardRepayListActivity.class);
			intent.putExtra("id", info.getId());
			startActivity(intent);
		}
	};

	/**
	 * 获取信用卡还款列表
	 * 
	 * @return
	 */
	private void postQueryRecordHttp() {
		RequestParams params = new RequestParams();
		String url = MyUrls.ROOT_URL_REFUNDRECORD;

		map = new HashMap<String, String>();
		map.put("username", MApplication.mSharedPref
				.getSharePrefString(SharedPrefConstant.USERNAME));
		map.put("count", "" + dataCount);
		map.put("page", "" + page);
		map.put("Cmd", "CreditRufundList");
		map.put("id", id);
		map.put("token", MApplication.mSharedPref
				.getSharePrefString(SharedPrefConstant.TOKEN));
		String json = JSON.toJSONString(map);
		Log.i("result", "-------信用卡list请求-----" + json);
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
				T.ss("操作超时!");
				Log.i("result", "---------------失败--" + arg0.getExceptionCode());
				lv.onRefreshComplete();
			}

			@Override
			public void onSuccess(ResponseInfo<String> response) {
				// TODO Auto-generated method stub
				String strReturnLogin = response.result;
				Log.i("result", "---------------信用卡-returnjson---"
						+ strReturnLogin);
				lv.onRefreshComplete();
				jsonDetail(strReturnLogin);
				String returnCode = info.getCode();
				if (returnCode.equals("00")) {
					if (listReturn == null || listReturn.size() == 0) {
						T.ss(getResources().getString(R.string.query_nothing));
					} else {

						if (page == 1) {
							list.clear();
							list = listReturn;
						} else {
							list.addAll(listReturn);// 追加跟新的数据
						}
						adapter.sendSata(list);
						adapter.notifyDataSetChanged();
					}
				} else {
					T.ss(info.getMessage());
				}

			}
		});
	}

	/**
	 * 解析 Json字符串 登录返回结果
	 * 
	 * @param str
	 * @return
	 */
	private void jsonDetail(String str) {

		try {
			JSONObject obj = new JSONObject(str);
			info = new Xinyongkainfo();
			info.setCode(obj.optString("CODE"));
			info.setMessage(obj.optString("MESSAGE"));
			listReturn = new ArrayList<Xinyongkainfo>();

			int count = obj.optInt("Count");
			if (count < dataCount) {
				lv.setMode(Mode.PULL_DOWN_TO_REFRESH);
			}
			if (count > 0) {
				tvShowUnit.setVisibility(View.VISIBLE);
				for (int i = 0; i < count; i++) {

					JSONObject jsObj = obj.optJSONArray("data")
							.optJSONObject(i);
					// 给info设置数据
					Xinyongkainfo xykinfo = new Xinyongkainfo();
					xykinfo.setCardNum(jsObj.optString("credit_card_num"));// 卡号
					xykinfo.setType(jsObj.optString("refunt_state"));// 还款状态
					xykinfo.setRepayDate(jsObj.optString("periods"));// 还款期数
					xykinfo.setId(jsObj.optString("id"));
					xykinfo.setRefundDay(jsObj.optString("refund_day"));// 还款日
					String refuntMoney = jsObj.optString("refunt_money");
					// String refuntMoney = StringUtils.moneyFormat(
					// jsObj.optString("refunt_money"), 4);
					String payMoney = jsObj.optString("pay_money");
					// String payMoney = StringUtils.moneyFormat(
					// jsObj.optString("pay_money"), 4);
					xykinfo.setReimmoney("" + refuntMoney);// 还款金额
					xykinfo.setPoundage("" + payMoney);// 手续费
					listReturn.add(xykinfo);
				}
			}

		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
		// TODO Auto-generated method stub
		page = 1;
		postQueryRecordHttp();

	}

	@Override
	public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
		// TODO Auto-generated method stub
		page++;
		postQueryRecordHttp();
	}
}
