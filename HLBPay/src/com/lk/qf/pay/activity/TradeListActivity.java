package com.lk.qf.pay.activity;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.LinearLayout.LayoutParams;

import com.lk.bhb.pay.R;
import com.lk.pay.communication.AsyncHttpResponseHandler;
import com.lk.qf.pay.activity.trade.WithdrawDetailsActivity;
import com.lk.qf.pay.adapter.DealRecordAdapter;
import com.lk.qf.pay.beans.BasicResponse;
import com.lk.qf.pay.beans.TradeBean;
import com.lk.qf.pay.golbal.Urls;
import com.lk.qf.pay.tool.Logger;
import com.lk.qf.pay.tool.MyHttpClient;
import com.lk.qf.pay.tool.T;
import com.lk.qf.pay.utils.AmountUtils;
import com.lk.qf.pay.wedget.CommonTitleBar;
import com.lk.qf.pay.wedget.CustomListView;
import com.lk.qf.pay.wedget.CustomListView.OnLoadMoreListener;
import com.lk.qf.pay.wedget.CustomListView.OnRefreshListener;

public class TradeListActivity extends BaseActivity implements OnClickListener {

	private Context ctx;
	private CustomListView listview;
	private DealRecordAdapter adapter;
	private CommonTitleBar title;
	// private CommonLoadingComponent loading;
	private int currentPage = 0, totalPage = 0;
	private HashMap<String, String> params = null;
	ArrayList<TradeBean> adaValues = new ArrayList<TradeBean>();
	private final int PAGE_SIZE = 20;
	DealRecordAdapter ada;
	private final int ACTION_LOADMORE = 2;
	private final int ACTION_REFRESH = 1;
	private int select_Id = 0;
	private TradeBean bean;
	private String type = "00";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.deal_record_refresh);
		ctx = this;
		initView();
		initData(ACTION_REFRESH,currentPage);
		check = getResources().getDrawable(R.drawable.ok32);
		check.setBounds(0, 0, check.getMinimumWidth(), check.getMinimumHeight());
	}

	private String getCurrentDate(int type) {
		Calendar c = Calendar.getInstance();
		String temp = "";
		if (type == 1) {
			temp = "" + c.get(Calendar.YEAR) + (c.get(Calendar.MONTH) + 1)
					+ c.get(Calendar.DAY_OF_MONTH) + " 00:00:00";
		} else {
			temp = "" + c.get(Calendar.YEAR) + (c.get(Calendar.MONTH)) + "01"
					+ " 23:59:59";

		}
		return temp;
	}
	
	

	private void initData(final int action,int start) {
		// loading.setIsLoading("加载中...");
		params = new HashMap<String, String>();
		params.put("busType", type);
		// params.put("prdOrdNo", "");
		params.put("pageSize", "20");
		params.put("start", start+"");
		// params.put("startTime", "");
		// params.put("endTime", "");
		// params.put("BUSITYPE", "");
		// params.put("PAGENUM", currentPage + "");
		MyHttpClient.post(ctx, Urls.TRADE_RECORDS, params,
				new AsyncHttpResponseHandler() {

					@Override
					public void onSuccess(int statusCode, Header[] headers,
							byte[] responseBody) {
						Logger.json("[交易记录]", responseBody);
						try {
							BasicResponse r = new BasicResponse(responseBody)
									.getResult();
							if (r.isSuccess()) {
								JSONArray array = r.getJsonBody().getJSONArray(
										"tranList");
								if (action == ACTION_REFRESH) {// 如果是刷新操作
									if (adaValues.size() > 0) {
										adaValues.clear();
									}
								}
								
								try {
									for (int i = 0; i < array.length(); i++) {
										JSONObject obj = array.getJSONObject(i);
										TradeBean bean = new TradeBean();
										bean.setBusType(obj.optString("prdordtype"));
										bean.setAgentId(obj.optString("custId"));
										bean.setTarnTime(datePaser(obj.optString("ordtime")));
										bean.setPrdOrdNO(obj.optString("prdordno"));
										bean.setTranState(obj.optString("ordstatus"));
										bean.setTranAmt(AmountUtils.changeFen2Yuan(obj.optString("ordamt")));
										bean.setBankCardNo(obj.optString("PAY_CARDNO"));
										if(!obj.isNull("paySignPic"))
											bean.setSignPath(obj.optString("paySignPic"));
										adaValues.add(bean);		
									}
								} catch (Exception e) {
									// TODO: handle exception
								}
								if (null == adapter) {
									adapter = new DealRecordAdapter(ctx,
											adaValues);
									listview.setAdapter(adapter);
								} else {
									adapter.refreshValues(adaValues);
									adapter.notifyDataSetChanged();
								}
								if (array.length() == 0) {
									T.ss("暂无交易记录");
									listview.setCanLoadMore(false);
									//adapter.notifyDataSetChanged();
									listview.hideFooterView();
								}else if(array.length()<PAGE_SIZE){
									listview.setCanLoadMore(false);
									//adapter.notifyDataSetChanged();
									listview.hideFooterView();
								}
								if (action == ACTION_REFRESH) {
									handler.sendEmptyMessage(1);
								} else{
									handler.sendEmptyMessage(2);
								}
							} else {
								T.ss(r.getMsg());
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
	
	

	private String datePaser(String str) {
		if (null == str)
			return "--";
		SimpleDateFormat d = new SimpleDateFormat("yyyyMMddHHmmss");
		try {
			Date date = d.parse(str);
			SimpleDateFormat temp = new SimpleDateFormat("MM月dd日 HH:mm:ss");
			return temp.format(date);
		} catch (ParseException e) {
			return "";
		}

	}

	OnRefreshListener onrefresh = new OnRefreshListener() {

		@Override
		public void onRefresh() {
			currentPage = 0;
			initData(ACTION_REFRESH,currentPage);
			//handler.sendEmptyMessage(1);
		}
	};
	OnLoadMoreListener onloadmore = new OnLoadMoreListener() {

		@Override
		public void onLoadMore() {
			currentPage++;
			initData(ACTION_LOADMORE, currentPage);
			// initData(ACTION_LOADMORE);
			//handler.sendEmptyMessage(2);

		}
	};

	private void initView() {
		listview = (CustomListView) findViewById(R.id.listview_reade_records);
		listview.setCanRefresh(true);
		listview.setCanLoadMore(false);
		listview.setOnRefreshListener(onrefresh);
		listview.setOnLoadListener(onloadmore);
		listview.setOnItemClickListener(onItemClick);
		title = (CommonTitleBar) findViewById(R.id.titlebar_trade_record);
		title.setActName("交易记录").setCanClickDestory(this, true);
		title.getTv_more().setVisibility(View.VISIBLE);
		title.getTv_more().setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if (null == pop) {
					initPopwindow();
				}
				pop.showAsDropDown(v, (v.getWidth()-pop.getWidth())/2, 0);
			}
		});
		title.getTv_more().setText("交易类型");
		

	}

	Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case 1:
				listview.onRefreshComplete();
				break;
			case 2:
				listview.onLoadMoreComplete();
				break;
			}
		};
	};

	protected void onDestroy() {
		super.onDestroy();
	};

	OnItemClickListener onItemClick = new OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			if (position < 0)
				return;
			goDetail(position);
		}
	};

	private void goDetail(int arg2) {
		TradeBean temp = adaValues.get(arg2 - 1);
		if(temp.getTranState().equals("01")){
//			if (temp.getBusType().equals("01") || temp.getBusType().equals("02")) {
				startActivity(new Intent(this, SalesSlipActivity.class).putExtra(
						"data", temp));

//			} else {
//				startActivity(new Intent(this, WithdrawDetailsActivity.class)
//						.putExtra("data", temp));
//			}
		}/*else {
			showToast("该订单未完成支付!");
		}*/

	}

	@Override
	public void onClick(View v) {

		switch (v.getId()) {
		case R.id.titlebar_trade_record:
			
			break;
		}

	}

	private Drawable check;
	private PopupWindow pop;
	private TextView tv_all, tv_consume, tv_withdraw, tv_cashin;

	public void initPopwindow() {

		View view = LayoutInflater.from(ctx).inflate(R.layout.popwindow_tradelist,
				null);
		pop = new PopupWindow(view, LayoutParams.WRAP_CONTENT,
				LayoutParams.WRAP_CONTENT, true);
		tv_all = (TextView) view.findViewById(R.id.pop_tv_all);
		tv_all.setOnClickListener(popClick);
		tv_consume = (TextView) view.findViewById(R.id.pop_tv_consume);
		tv_consume.setOnClickListener(popClick);

		tv_withdraw = (TextView) view.findViewById(R.id.pop_tv_withdraw);
		tv_withdraw.setOnClickListener(popClick);
		tv_cashin = (TextView) view.findViewById(R.id.pop_tv_cashin);
		tv_cashin.setOnClickListener(popClick);
		pop.setBackgroundDrawable(new BitmapDrawable());
		pop.setOutsideTouchable(true);
		pop.setFocusable(true);

	}

	OnClickListener popClick = new View.OnClickListener() {

		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.pop_tv_all:
				type="00";
				tv_all.setCompoundDrawables(null, null, check, null);
				tv_cashin.setCompoundDrawables(null, null, null, null);
				tv_consume.setCompoundDrawables(null, null, null, null);
				tv_withdraw.setCompoundDrawables(null, null, null, null);
                
				break;
			case R.id.pop_tv_cashin:
				type="01";
				tv_all.setCompoundDrawables(null, null, null, null);
				tv_cashin.setCompoundDrawables(null, null, check, null);
				tv_consume.setCompoundDrawables(null, null, null, null);
				tv_withdraw.setCompoundDrawables(null, null, null, null);
				break;
			case R.id.pop_tv_consume:
				type="02";
				tv_all.setCompoundDrawables(null, null, null, null);
				tv_cashin.setCompoundDrawables(null, null, null, null);
				tv_consume.setCompoundDrawables(null, null, check, null);
				tv_withdraw.setCompoundDrawables(null, null, null, null);
				break;
			case R.id.pop_tv_withdraw:
				type="03";
				tv_all.setCompoundDrawables(null, null, null, null);
				tv_cashin.setCompoundDrawables(null, null, null, null);
				tv_consume.setCompoundDrawables(null, null, null, null);
				tv_withdraw.setCompoundDrawables(null, null, check, null);
				break;
			}
			currentPage=0;
			initData(ACTION_REFRESH,currentPage);
			pop.dismiss();
		}
	};
}
