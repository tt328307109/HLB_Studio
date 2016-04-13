package com.lk.qf.pay.aanewactivity;

import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.entity.StringEntity;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

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
import com.lk.qf.pay.adapter.ShouyiListAdapter;
import com.lk.qf.pay.adapter.ShouyiListFGSAdapter;
import com.lk.qf.pay.beans.IncomeInfo;
import com.lk.qf.pay.golbal.MApplication;
import com.lk.qf.pay.golbal.MyUrls;
import com.lk.qf.pay.sharedpref.SharedPrefConstant;
import com.lk.qf.pay.tool.T;
import com.lk.qf.pay.wedget.CommonTitleBar;

public class ShouyiFGSListActivity extends BaseActivity implements
		OnClickListener, OnRefreshListener2<ListView> {

	private PullToRefreshListView lv;
	private String startTime = "";
	private List<IncomeInfo> list;
	private List<IncomeInfo> listReturn;
	private int page = 1;
	private int dataCount = 10;
	private Map<String, String> map;
	private IncomeInfo orderInfo;// 登录后返回的用户信息
	private EditText tvStartTime;
	private TextView tvTrading,tvIncome,tvTime;
	private ShouyiListFGSAdapter adapter;
	private Date date;
	private SimpleDateFormat sdf;
	private Calendar cal;
	private boolean timeIsOk = true;
	private CommonTitleBar title;
	private String action = "";
	private String strTitle = "";
	private String dealtype = "";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.sy_fgs_list_layout);
		init();
	}

	private void init() {
		date = new Date();
		sdf = new SimpleDateFormat("yyyyMM");

		Intent intent = getIntent();
		if (intent != null) {
			action = intent.getAction();
			if (action.equals(ShouyiListActivity.MPOSINCOME)) {
				strTitle = "刷卡收益明细";
				dealtype = "0";
			} else if (action.equals(ShouyiListActivity.ZJINCOME)) {
				strTitle = "装机收益明细";
				dealtype = "1";
			} else if (action.equals(ShouyiListActivity.XYKINCOME)) {
				strTitle = "信用卡收益明细";
				dealtype = "2";
			} else if (action.equals(ShouyiListActivity.T0INCOME)) {
				strTitle = "T0收益明细";
				dealtype = "3";
			}else if (action.equals(ShouyiListActivity.SUPERPAY)) {
				strTitle = "即刷即到收益明细";
				dealtype = "5";
			}
		}

		tvTrading = (TextView) findViewById(R.id.tv_sylist_jyAccount_fgs);
		tvIncome = (TextView) findViewById(R.id.tv_sylist_syAccount_fgs);
		tvTime = (TextView) findViewById(R.id.tv_sylist_time_fgs);
		
		tvStartTime = (EditText) findViewById(R.id.tv_income_list_startTime1_fgs);
		tvStartTime.setText("" + sdf.format(date));
		startTime = sdf.format(date);
		title = (CommonTitleBar) findViewById(R.id.titlebar_income_list_fgs);
		title.setActName(strTitle);
		title.setCanClickDestory(this, true);

		findViewById(R.id.btn_sy_list_query1_fgs).setOnClickListener(this);
		lv = (PullToRefreshListView) findViewById(R.id.myPull_refresh_list_order_income_fgs);
		lv.setOnRefreshListener(this);
		list = new ArrayList<IncomeInfo>();

		adapter = new ShouyiListFGSAdapter(ShouyiFGSListActivity.this, list);
		lv.setAdapter(adapter);
		postQueryOrderHttp();

	}

	@Override
	public void onClick(View arg0) {
		// TODO Auto-generated method stub
		switch (arg0.getId()) {

		case R.id.btn_sy_list_query1_fgs:

			list.clear();
			startTime = tvStartTime.getText().toString();

			if (TextUtils.isEmpty(startTime)) {

				startTime = sdf.format(date);
			}
			if (startTime.length() != 6) {

				T.ss("时间格式输入有误");
				return;
			}

			postQueryOrderHttp();
			break;

		default:
			break;
		}
	}

	/**
	 * 查询请求
	 * 
	 * @return
	 */
	private void postQueryOrderHttp() {
		showLoadingDialog();
		RequestParams params = new RequestParams();
		String url = "";
		url = MyUrls.MERFENRUN;

		map = new HashMap<String, String>();

		map.put("username", MApplication.mSharedPref
				.getSharePrefString(SharedPrefConstant.USERNAME));
		map.put("token", MApplication.mSharedPref
				.getSharePrefString(SharedPrefConstant.TOKEN));
		map.put("aglevel", MApplication.mSharedPref
				.getSharePrefString(SharedPrefConstant.AGLEVEL));
		map.put("dealtype", dealtype);
		map.put("begintime", startTime+"01");
		map.put("endtime", startTime+"31");
		map.put("type", "01");
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
						list.clear();
						adapter.notifyDataSetChanged();
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
						Intent intent = new Intent(ShouyiFGSListActivity.this,
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
			orderInfo = new IncomeInfo();
			orderInfo.setCode(obj.optString("CODE"));
			orderInfo.setMessage(obj.optString("MESSAGE"));
			listReturn = new ArrayList<IncomeInfo>();
			int count = obj.optInt("COUNT");
			Log.i("result", "---------Count-------" + count);
			if (count > 0) {
				Log.i("result", "---------page-------" + page);
				Log.i("result", "---------dataCount-------" + dataCount);
				tvTrading.setText("交易金额");
				tvIncome.setText("收益");
				tvTime.setText("时间");
				for (int i = 0; i < count; i++) {
					String tradingAccount = obj.optJSONArray("DATA").optJSONObject(i)
							.optString("total");
					String incomeAccount = obj.optJSONArray("DATA").optJSONObject(i)
							.optString("oem_fen");
					String addtime = obj.optJSONArray("DATA").optJSONObject(i)
							.optString("addtime");

					// 给info设置数据
					IncomeInfo info = new IncomeInfo();
					info.setTime(addtime);// 交易日期
					info.setTradingAccount(tradingAccount);
					info.setIncomeAccount(incomeAccount);
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
//		page = 1;
//		postQueryOrderHttp();
		adapter.notifyDataSetChanged();
		lv.onRefreshComplete();
	}

	@Override
	public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
		// TODO Auto-generated method stub
//		page++;
//		postQueryOrderHttp();
		adapter.notifyDataSetChanged();
		lv.onRefreshComplete();
	}

}
