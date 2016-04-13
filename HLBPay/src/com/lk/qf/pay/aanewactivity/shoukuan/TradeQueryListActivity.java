package com.lk.qf.pay.aanewactivity.shoukuan;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.entity.StringEntity;
import org.json.JSONException;
import org.json.JSONObject;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import com.alibaba.fastjson.JSON;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener2;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;
import com.lk.bhb.pay.R;
import com.lk.qf.pay.activity.BaseActivity;
import com.lk.qf.pay.adapter.YiFuBaoListAdapter;
import com.lk.qf.pay.beans.OrderInfo;
import com.lk.qf.pay.golbal.Actions;
import com.lk.qf.pay.golbal.MApplication;
import com.lk.qf.pay.golbal.MyUrls;
import com.lk.qf.pay.sharedpref.SharedPrefConstant;
import com.lk.qf.pay.tool.T;
import com.lk.qf.pay.utils.CreatePayCodeUtils;
import com.lk.qf.pay.wedget.CommonTitleBar;

public class TradeQueryListActivity extends BaseActivity implements
		OnRefreshListener2<ListView> {

	private PullToRefreshListView lv;
	private String phoneNum;
	private List<OrderInfo> list;
	private List<OrderInfo> listReturn;
	private int page = 0, position = 0;
	private int dataCount = 10;
	private Map<String, String> map;
	private OrderInfo orderInfo;// 登录后返回的用户信息
	private YiFuBaoListAdapter adapter;
	private String typeId = "103", action, strTitle = "订单详情";
	private CommonTitleBar title;
	private String appSign, queryUrl;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.trade_query_list_layout);
		init();
	}

	private void init() {
		Log.i("result", "-------weixin-----");
		title = (CommonTitleBar) findViewById(R.id.titlebar_trade_query_back);
		phoneNum = MApplication.mSharedPref
				.getSharePrefString(SharedPrefConstant.USERNAME);
		Intent intent = getIntent();
		if (intent != null) {
			action = intent.getAction();
			typeId = intent.getStringExtra("id");
			if (action != null && typeId != null) {
				postQueryOrderHttp();
			}
			if (action != null) {
				if (action.equals(Actions.ACTION_WEIXIN)) {
					queryUrl = MyUrls.WEIXIN_ORDERPAYPAGE;
				} else if (action.equals(Actions.ACTION_ZHIFUBAO)) {
					queryUrl = MyUrls.ZHIFUBAO_ORDERPAYPAGE;
				} else if (action.equals(Actions.ACTION_BAIDU)) {
					queryUrl = MyUrls.BAIDU_QUERYORDER;
				} else if (action.equals(Actions.ACTION_YIFUBAO)) {
					queryUrl = MyUrls.QUERYORDER;
				}
			}

		}
		title.setActName(strTitle);
		title.setCanClickDestory(this, true);
		lv = (PullToRefreshListView) findViewById(R.id.myPull_refresh_list_trade_query_list);
		lv.setOnRefreshListener(this);
		lv.setOnItemClickListener(clickListener);
		list = new ArrayList<OrderInfo>();

		adapter = new YiFuBaoListAdapter(TradeQueryListActivity.this, list,
				action);
		lv.setAdapter(adapter);

	}

	/**
	 * 查询请求
	 * 
	 * @return
	 */
	private void postQueryOrderHttp() {
		showLoadingDialog();
		String[] str = { "phoneNum=" + phoneNum, "Count=" + dataCount,
				"Page=" + page, "TradeWay=" + typeId };
		String appSign = CreatePayCodeUtils.createSign(str);// 签名
		RequestParams params = new RequestParams();
		String url = MyUrls.OTHERTRADEDETAIL;
		map = new HashMap<String, String>();
		map.put("phoneNum", phoneNum);
		map.put("appSign", appSign);
		map.put("Count", "" + dataCount);
		map.put("Page", "" + page);
		map.put("TradeWay", typeId);
		String json = JSON.toJSONString(map);
		Log.i("result", "-------订单请求-----" + json);
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
				T.ss("操作超时");
				dismissLoadingDialog();
				lv.onRefreshComplete();

			}

			@Override
			public void onSuccess(ResponseInfo<String> response) {
				// TODO Auto-generated method stub
				String strReturnLogin = response.result;
				Log.i("result", "---------------定单-returnjson---"
						+ strReturnLogin);
				jsonDetail(strReturnLogin);
				String returnCode = orderInfo.getCode();

				if (returnCode.equals("0")) {
					if (listReturn == null || listReturn.size() == 0) {
						T.ss(getResources().getString(
								R.string.query_nothing_more));
						// finish();
					} else {

						if (page == 0) {
							list.clear();
							list = listReturn;
						} else {
							list.addAll(listReturn);// 追加跟新的数据
						}
						adapter.sendSata(list);
						adapter.notifyDataSetChanged();
					}
				}
				lv.onRefreshComplete();// 告诉它 我们已经在后台数据请求完毕
				dismissLoadingDialog();
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
			orderInfo = new OrderInfo();
			orderInfo.setCode(obj.optString("CODE"));
			orderInfo.setMessage(obj.optString("MESSAGE"));
			listReturn = new ArrayList<OrderInfo>();
			int count = obj.optInt("COUNT");
			Log.i("result", "---------Count-------" + count);
			if (count > 0) {
				// Log.i("result", "---------page-------" + page);
				// Log.i("result", "---------dataCount-------" + dataCount);

				for (int i = 0; i < count; i++) {

					String time = obj.optJSONArray("DATA").optJSONObject(i)
							.optString("startTime");// 时间
					String account = obj.optJSONArray("DATA").optJSONObject(i)
							.optString("total_fee");// 金额
					String batchNum = obj.optJSONArray("DATA").optJSONObject(i)
							.optString("out_trade_no");// 订单号
					String tradeState = obj.optJSONArray("DATA")
							.optJSONObject(i).optString("tradeState");// 订单状态
					// 给info设置数据
					OrderInfo info = new OrderInfo();
					info.setTradingTime(time);// 交易日期
					info.setConsumptionAmount(account);// 交易金额
					info.setOrderNum(batchNum);// 订单号
					info.setTradingInformation(tradeState);// 交易状态
					listReturn.add(info);
				}
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
		// TODO Auto-generated method stub

		page = 0;
		postQueryOrderHttp();
	}

	@Override
	public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
		// TODO Auto-generated method stub
		// pageSize+=5;
		page++;
		postQueryOrderHttp();
	}

	OnItemClickListener clickListener = new OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
				long arg3) {
			// TODO Auto-generated method stub
			OrderInfo info = (OrderInfo) arg0.getItemAtPosition(arg2);
			Log.i("result", "------------arg2-position----------" + (arg2 - 1));
			position = arg2 - 1;
			if (info.getTradingInformation().equals("0")
					|| info.getTradingInformation().equals("1")) {

				queryPayType(info.getOrderNum());
			}

		}
	};

	/**
	 * 查询支付状态
	 */
	private void queryPayType(String outTradeNo) {
		showLoadingDialog();
		String[] str = { "phoneNum=" + phoneNum, "OutTradeNo=" + outTradeNo };
		appSign = CreatePayCodeUtils.createSign(str);// 签名
		RequestParams params = new RequestParams();
		Map<String, String> map = new HashMap<String, String>();
		map.put("phoneNum", phoneNum);
		map.put("appSign", appSign);
		map.put("OutTradeNo", outTradeNo);
		String json = JSON.toJSONString(map);
		try {
			StringEntity bodyEntity = new StringEntity(json, "UTF-8");
			params.setBodyEntity(bodyEntity);
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		HttpUtils utils = new HttpUtils();
		utils.send(HttpMethod.POST, queryUrl, params,
				new RequestCallBack<String>() {

					@Override
					public void onFailure(HttpException arg0, String arg1) {

						dismissLoadingDialog();
						T.ss("操作超时");

					}

					@Override
					public void onSuccess(ResponseInfo<String> response) {
						// TODO Auto-generated method stub
						String code = "";
						String message = "";
						String str = response.result;
						Log.i("result", "----获取----s-------" + str);
						JSONObject obj = null;
						try {
							obj = new JSONObject(str);
							code = obj.optString("Code");
							message = obj.optString("Message");

						} catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						if (code.equals("0007")) {
							list.get(position).setTradingInformation("0"); // 修改为支付失败
						} else if (code.equals("0006")) {
							list.get(position).setTradingInformation("2"); // 修改为支付成功
						}
						adapter.notifyDataSetChanged();
						dismissLoadingDialog();

					}
				});
	}

}
