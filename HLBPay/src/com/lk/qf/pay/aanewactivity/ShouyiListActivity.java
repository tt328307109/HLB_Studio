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

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.DatePicker;
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
import com.lk.qf.pay.beans.IncomeInfo;
import com.lk.qf.pay.dialog.MonPickerDialog;
import com.lk.qf.pay.golbal.MApplication;
import com.lk.qf.pay.golbal.MyUrls;
import com.lk.qf.pay.sharedpref.SharedPrefConstant;
import com.lk.qf.pay.tool.T;
import com.lk.qf.pay.utils.TimeUtils;
import com.lk.qf.pay.wedget.CommonTitleBar;
import com.lk.qf.pay.wedget.customdialog.CustomerDatePickerDialog;

public class ShouyiListActivity extends BaseActivity implements
		OnClickListener, OnRefreshListener2<ListView> {

	private PullToRefreshListView lv;
	private String startTime = "";
	private List<IncomeInfo> list;
	private List<IncomeInfo> listReturn;
	private int page = 1;
	private int dataCount = 10;
	private Map<String, String> map;
	private IncomeInfo orderInfo;// 登录后返回的用户信息
	private TextView tvStartTime;
	private TextView tvTrading, tvLevel, tvIncome, tvTime;
	private ShouyiListAdapter adapter;
	private String typeId = "101";
	private static int APPID = 2;// 0机构 1代理 2商户
	private Date date;
	private SimpleDateFormat sdf;
	private Calendar cal;
	private boolean timeIsOk = true;
	private CommonTitleBar title;
	private String action = "";
	private String strTitle = "";
	private String dealtype = "";
	public static String MPOSINCOME = "mpos";
	public static String ZJINCOME = "zj";
	public static String XYKINCOME = "xyk";
	public static String T0INCOME = "t0";
	public static String UPMER = "upmer";
	public static String SUPERPAY = "superpay";
	private CustomerDatePickerDialog mDialog;

	private int mYear;
	private int mMonth;
	private int mDay;
	private AlertDialog.Builder builder;
	private DatePicker dp;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.sy_list_layout);
		init();
	}

	private void init() {
		date = new Date();
		sdf = new SimpleDateFormat("yyyyMM");

		Intent intent = getIntent();
		if (intent != null) {
			action = intent.getAction();
			if (action.equals(MPOSINCOME)) {
				strTitle = "刷卡收益明细";
				dealtype = "0";
			} else if (action.equals(ZJINCOME)) {
				strTitle = "装机收益明细";
				dealtype = "1";
			} else if (action.equals(XYKINCOME)) {
				strTitle = "信用卡收益明细";
				dealtype = "2";
			} else if (action.equals(T0INCOME)) {
				strTitle = "T0收益明细";
				dealtype = "3";
			} else if (action.equals(UPMER)) {
				strTitle = "升级收益明细";
				dealtype = "4";
			} else if (action.equals(SUPERPAY)) {
				strTitle = "即刷即到收益明细";
				dealtype = "5";
			}
		}

		tvTrading = (TextView) findViewById(R.id.tv_sylist_jyAccount);
		tvLevel = (TextView) findViewById(R.id.tv_sylist_type);
		tvIncome = (TextView) findViewById(R.id.tv_sylist_syAccount);
		tvTime = (TextView) findViewById(R.id.tv_sylist_time);
		tvStartTime = (TextView) findViewById(R.id.tv_income_list_startTime1);
		tvStartTime.setText("" + sdf.format(date));
		tvStartTime.setOnClickListener(this);
		startTime = sdf.format(date);
		title = (CommonTitleBar) findViewById(R.id.titlebar_income_list);
		title.setActName(strTitle);
		title.setCanClickDestory(this, true);

		findViewById(R.id.btn_sy_list_query1).setOnClickListener(this);
		lv = (PullToRefreshListView) findViewById(R.id.myPull_refresh_list_order_income);
		lv.setOnRefreshListener(this);
		list = new ArrayList<IncomeInfo>();

		adapter = new ShouyiListAdapter(ShouyiListActivity.this, list);
		lv.setAdapter(adapter);
		postQueryOrderHttp();

	}

	@Override
	public void onClick(View arg0) {
		// TODO Auto-generated method stub
		switch (arg0.getId()) {

		case R.id.btn_sy_list_query1:

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
		case R.id.tv_income_list_startTime1:
//			showDialog();
			showMonPicker();
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
		map.put("begintime", startTime + "01");
		map.put("endtime", startTime + "31");
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
						Intent intent = new Intent(ShouyiListActivity.this,
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
				// TextView tvTrading,tvLevel,tvIncome,tvTime;
				tvTrading.setText("交易金额");
				tvLevel.setText("级别");
				tvIncome.setText("收益");
				tvTime.setText("时间");
				for (int i = 0; i < count; i++) {
					String tradingAccount = "";
					String level = "";
					String incomeAccount = "";
					String addtime = "";
					// if (appType.equals("0")) {
					tradingAccount = obj.optJSONArray("DATA").optJSONObject(i)
							.optString("total");
					level = obj.optJSONArray("DATA").optJSONObject(i)
							.optString("level");
					incomeAccount = obj.optJSONArray("DATA").optJSONObject(i)
							.optString("agid_fen");
					addtime = obj.optJSONArray("DATA").optJSONObject(i)
							.optString("addtime");
					// } else {
					// tradingAccount = obj.optJSONArray("DATA")
					// .optJSONObject(i).optString("total");
					// // level = obj.optJSONArray("DATA").optJSONObject(i)
					// // .optString("tradedaytotal");
					// incomeAccount = obj.optJSONArray("DATA").optJSONObject(i)
					// .optString("oem_fen");
					// addtime = obj.optJSONArray("DATA").optJSONObject(i)
					// .optString("addtime");
					// }

					// 给info设置数据
					IncomeInfo info = new IncomeInfo();
					info.setTime(addtime);// 交易日期
					info.setTradingAccount(tradingAccount);
					info.setLevel(level);
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
		// page = 1;
		postQueryOrderHttp();
		// adapter.notifyDataSetChanged();
		// lv.onRefreshComplete();
	}

	@Override
	public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
		// TODO Auto-generated method stub
		// page++;
		postQueryOrderHttp();
		// adapter.notifyDataSetChanged();
		// lv.onRefreshComplete();
	}
	
	private void showMonPicker() {  
	    final Calendar localCalendar = Calendar.getInstance();  
	    localCalendar.setTime(TimeUtils.strToDate("yyyyMM", tvStartTime.getText().toString()));  
	    new MonPickerDialog(this,new DatePickerDialog.OnDateSetListener() {  
	                @Override  
	                public void onDateSet(DatePicker view, int year,int monthOfYear, int dayOfMonth) {  
	                    localCalendar.set(1, year);  
	                    localCalendar.set(2, monthOfYear);  
	                    tvStartTime.setText(TimeUtils.clanderTodatetime(localCalendar, "yyyyMM"));  
	                }  
	            }, localCalendar.get(1), localCalendar.get(2),localCalendar.get(5)).show();  
	}  

//	@SuppressWarnings("deprecation")
//	private void showDialog() {
//		final Calendar cal = Calendar.getInstance();
//		mDialog = new CustomerDatePickerDialog(this, callBackListener,
//				cal.get(Calendar.YEAR), cal.get(Calendar.MONTH),
//				cal.get(Calendar.DAY_OF_MONTH));
//		mDialog.setTitle("选择时间");
//		mDialog.setButton("确定", new DialogInterface.OnClickListener() {
//
//			@Override
//			public void onClick(DialogInterface arg0, int arg1) {
//				// TODO Auto-generated method stub
//				mDialog.dismiss();
//				String strMonth ="";
//				Log.i("result", "-------1-----------"+mYear);
//				if (mMonth<10) {
//					strMonth = "0"+mMonth;
//				}else{
//					strMonth = ""+mMonth;
//				}
//				Log.i("result", "-------mYear-----------"+mYear+"----"+strMonth);
//				tvStartTime.setText(""+mYear+strMonth);
//			}
//		});
//		
//		mDialog.show();
//		dp = findDatePicker((ViewGroup) mDialog.getWindow()
//				.getDecorView());
//		if (dp != null) {
//			((ViewGroup) ((ViewGroup) dp.getChildAt(0)).getChildAt(0))
//					.getChildAt(2).setVisibility(View.GONE);
//			Log.i("result", "-------dp !----------");
//		}
//	}
//
//	private DatePicker findDatePicker(ViewGroup group) {
//		if (group != null) {
//			for (int i = 0, j = group.getChildCount(); i < j; i++) {
//				View child = group.getChildAt(i);
//				if (child instanceof DatePicker) {
//					Log.i("result", "-------if-----------");
//					return (DatePicker) child;
//				} else if (child instanceof ViewGroup) {
//					Log.i("result", "-------else-----------");
//					DatePicker result = findDatePicker((ViewGroup) child);
//					if (result != null)
//						return result;
//				}
//			}
//		}
//		return null;
//	}
//
//	OnDateSetListener callBackListener = new OnDateSetListener() {
//
//		@Override
//		public void onDateSet(DatePicker arg0, int year, int month, int day) {
//			// TODO Auto-generated method stub
//			Log.i("result", "-------DatePicker-----------"+year+"----"+month);
//			mYear = year;
//			mMonth = month + 1;
//			Log.i("result", "-------DatePicker-----------"+mYear+"----="+mMonth+"="+mDay);
//			mDay = day;
//		}
//	};
}
