package com.lk.qf.pay.aanewactivity;

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
import android.view.View.OnClickListener;
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
import com.lk.qf.pay.activity.LoginActivity;
import com.lk.qf.pay.adapter.TransferAdapter;
import com.lk.qf.pay.beans.OrderInfo;
import com.lk.qf.pay.golbal.MApplication;
import com.lk.qf.pay.golbal.MyUrls;
import com.lk.qf.pay.sharedpref.SharedPrefConstant;
import com.lk.qf.pay.tool.T;
import com.lk.qf.pay.wedget.CommonTitleBar;

public class TransferListActivity extends BaseActivity implements
		OnClickListener, OnRefreshListener2<ListView> {

	private PullToRefreshListView lv;
	private String phone;
	private List<OrderInfo> list;
	private List<OrderInfo> listReturn;
	private int page = 1;
	private int dataCount = 10;
	private Map<String, String> map;
	private OrderInfo orderInfo;// 登录后返回的用户信息
	private TransferAdapter adapter;
	private View view;
	private CommonTitleBar title;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.transfer_list_layout);
		init();
	}


	private void init() {
		title = (CommonTitleBar) findViewById(R.id.titlebar_transferList);
		title.setActName("转账明细");
		title.setCanClickDestory(this, true);
		lv = (PullToRefreshListView) findViewById(R.id.myPull_refresh_list_order_transferlist);
		lv.setOnRefreshListener(this);
		list = new ArrayList<OrderInfo>();
		
		adapter = new TransferAdapter(TransferListActivity.this, list);
		lv.setAdapter(adapter);
		postQueryOrderHttp();
	}


	/**
	 * 查询请求
	 * 
	 * @return
	 */
	private void postQueryOrderHttp() {
		showLoadingDialog();
		RequestParams params = new RequestParams();
		String url = MyUrls.TRANSFER_DETAIL;

		map = new HashMap<String, String>();

		map.put("username", MApplication.mSharedPref
				.getSharePrefString(SharedPrefConstant.USERNAME));
		map.put("pagesize", "15");
		map.put("page", "" + page);
		map.put("type", "2");
		map.put("token", MApplication.mSharedPref
				.getSharePrefString(SharedPrefConstant.TOKEN));
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

				if (returnCode.equals("00")) {
					if (listReturn == null || listReturn.size() == 0) {
						T.ss(getResources().getString(
								R.string.query_nothing_more));
						// finish();
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
					T.ss(orderInfo.getMessage());
					if (orderInfo.getMessage().equals(
							getResources().getString(R.string.login_outtime))) {
						Intent intent = new Intent(
								TransferListActivity.this,
								LoginActivity.class);
						startActivity(intent);
						finish();
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

				for (int i = 0; i < count; i++) {

					String tranding = obj.optJSONArray("DATA").optJSONObject(i)
							.optString("addtime");
					String account = obj.optJSONArray("DATA").optJSONObject(i)
							.optString("total");
					String batchNum = obj.optJSONArray("DATA").optJSONObject(i)
							.optString("status");
					String tax = obj.optJSONArray("DATA").optJSONObject(i)
							.optString("end_name");//状态
					String type = obj.optJSONArray("DATA")
							.optJSONObject(i).optString("type");
					// 给info设置数据
					OrderInfo info = new OrderInfo();
					info.setTradingTime(tranding);// 交易日期
					info.setConsumptionAmount(account);// 交易金额
					info.setOrderNum(batchNum);// 订单号
					info.setSxAccount(tax);// 手续费
					info.setTradingInformation(type);//转入还是转出
					listReturn.add(info);
				}
			}else{
				finish();
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

		page = 1;
		postQueryOrderHttp();
	}

	@Override
	public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
		// TODO Auto-generated method stub
		// pageSize+=5;
		page++;
		Log.i("result", "--------ss------" + page);
		postQueryOrderHttp();
		Log.i("result", "--------sss------" + page);
	}


	@Override
	public void onClick(View arg0) {
		// TODO Auto-generated method stub
		
	}

}
