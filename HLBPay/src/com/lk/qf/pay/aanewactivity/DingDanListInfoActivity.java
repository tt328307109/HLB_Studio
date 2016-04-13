package com.lk.qf.pay.aanewactivity;

import java.io.UnsupportedEncodingException;
import java.text.DecimalFormat;
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
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.DatePicker.OnDateChangedListener;

import com.alibaba.fastjson.JSON;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener2;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;
import com.lk.bhb.pay.R;
import com.lk.qf.pay.activity.BaseActivity;
import com.lk.qf.pay.activity.LoginActivity;
import com.lk.qf.pay.adapter.JiaoYiAdapter;
import com.lk.qf.pay.beans.OrderInfo;
import com.lk.qf.pay.golbal.MApplication;
import com.lk.qf.pay.golbal.MyUrls;
import com.lk.qf.pay.sharedpref.SharedPrefConstant;
import com.lk.qf.pay.tool.T;
import com.lk.qf.pay.utils.TimeUtils;

public class DingDanListInfoActivity extends BaseActivity implements
		OnClickListener, OnRefreshListener2<ListView> {

	private ImageButton back;// 返回
	private PullToRefreshListView lv;
	private String startTime;
	private String endTime;
	private Button btnQuery;
	private String phone;
	private List<OrderInfo> list;
	private List<OrderInfo> listReturn;
	private int page = 1;
	private int pageSize = 15;
	private int dataCount = 10;
	private Map<String, String> map;
	private OrderInfo orderInfo;// 登录后返回的用户信息
	private TextView tvStartTime, tvEndTime;
	private JiaoYiAdapter adapter;
	private String accsort, typeId = "101";
	private static int APPID = 2;// 0机构 1代理 2商户
	private Date date;
	private SimpleDateFormat sdf;
	private Calendar cal;
	private AlertDialog.Builder builder;
	private int mYear;
	private int mMonth;
	private int mDay;
	private int startYear;
	private int startMonth;
	private int startDay;
	private int endDay;
	private boolean timeIsOk = true;
	private static int TYPE = 0;// 起始时间
	private View view;
	private DatePicker datePicker;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.dingdan_info_layout);
		init();
	}

	@Override
	public void onClick(View arg0) {
		// TODO Auto-generated method stub
		switch (arg0.getId()) {
		case R.id.ibtn_jiaoyi_dingdan_back:
			finish();

			break;
		case R.id.rl_jy_startDate:
			TYPE = 0;
			showDateDialog();
			break;
		case R.id.rl_jy_endDate:
			TYPE = 1;
			showDateDialog();
			break;
		case R.id.btn_jy_query1:

			// date = new Date();
			// sdf = new SimpleDateFormat("yyyyMMdd");
			// Calendar cal = Calendar.getInstance();
			// cal.add(Calendar.MONTH, -1);
			// Date lastDate = cal.getTime();
			list.clear();
			startTime = tvStartTime.getText().toString();
			endTime = tvEndTime.getText().toString();

			if (!TextUtils.isEmpty(startTime) && !TextUtils.isEmpty(endTime)) {
				int date1 = Integer.parseInt(startTime);
				int date2 = Integer.parseInt(endTime);
				if (date2 < date1) {
					T.ss("开始时间不能大于结束时间");
					return;
				}
			}
			if (TextUtils.isEmpty(startTime) || TextUtils.isEmpty(endTime)) {

				endTime = sdf.format(date);
				startTime = sdf.format(date);
			}

			postQueryOrderHttp();
			break;

		default:
			break;
		}
	}

	private void init() {
		date = new Date();
		sdf = new SimpleDateFormat("yyyyMMdd");

		tvStartTime = (TextView) findViewById(R.id.ed_jy_startTime1);
		tvEndTime = (TextView) findViewById(R.id.ed_jy_endTime1);
		tvStartTime.setText("" + sdf.format(date));
		tvEndTime.setText("" + sdf.format(date));
		startTime = sdf.format(date);
		endTime = sdf.format(date);

		btnQuery = (Button) findViewById(R.id.btn_jy_query1);
		btnQuery.setOnClickListener(this);
		lv = (PullToRefreshListView) findViewById(R.id.myPull_refresh_list_order_jiaoyi);

		list = new ArrayList<OrderInfo>();
		findViewById(R.id.ibtn_jiaoyi_dingdan_back).setOnClickListener(this);
		findViewById(R.id.rl_jy_startDate).setOnClickListener(this);
		findViewById(R.id.rl_jy_endDate).setOnClickListener(this);

		adapter = new JiaoYiAdapter(DingDanListInfoActivity.this, list);
		lv.setAdapter(adapter);
		lv.setOnRefreshListener(this);
		if (APPID == 0) {
			accsort = "oem";
		} else if (APPID == 1) {
			accsort = "ag";
		} else if (APPID == 2) {
			accsort = "mer";
		}

		Intent intent = getIntent();
		if (intent != null) {
			typeId = intent.getStringExtra("id");
		} else {
			typeId = "102";
		}
	}

	private void showDateDialog() {

		cal = Calendar.getInstance();
		mYear = cal.get(Calendar.YEAR);
		mMonth = cal.get(Calendar.MONTH);
		mDay = cal.get(Calendar.DAY_OF_MONTH);
		builder = new AlertDialog.Builder(this);
		view = getLayoutInflater().inflate(R.layout.custom_date_picker_layout,
				null);
		datePicker = (DatePicker) view.findViewById(R.id.custom_datePicker);
		datePicker.setEnabled(false);
		// 设置当前时间
		cal.setTimeInMillis(System.currentTimeMillis());
		datePicker.init(mYear, mMonth, mDay, dateChangedListener);
		builder.setView(view);
		builder.setTitle(getResources().getString(R.string.choose_start_time));
		builder.setPositiveButton(getResources().getString(R.string.confirm),
				new DialogInterface.OnClickListener() {
					// 确定
					@Override
					public void onClick(DialogInterface dialog, int arg1) {

						DecimalFormat mFormat = new DecimalFormat("00");
						String month = mFormat.format(Double
								.valueOf(mMonth + 1));
						String day = mFormat.format(Double.valueOf(mDay));
						if (timeIsOk == true) {

							if (TYPE == 0) {

								tvStartTime.setText(mYear + month + day);
								startYear = mYear;
								startMonth = mMonth;
								startDay = mDay;
							} else if (TYPE == 1) {

								tvEndTime.setText(mYear + month + day);
							}
						} else {
							if (TYPE == 0) {
								tvStartTime.setText("");
							} else {
								tvEndTime.setText("");
							}
						}

						dialog.cancel();
					}
				});
		// 取消
		builder.setNegativeButton(getResources().getString(R.string.cancel),
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int arg1) {
						// TODO Auto-generated method stub
						dialog.cancel();
					}
				});

		builder.create().show();
		handler.obtainMessage(0).sendToTarget(); 
	}
	private Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			if (msg.what == 0) {
				datePicker.setEnabled(true);
			}
		}
	};

	OnDateChangedListener dateChangedListener = new OnDateChangedListener() {

		@Override
		public void onDateChanged(DatePicker arg0, int year, int month, int day) {
			// TODO Auto-generated method stub
			mYear = year;
			mMonth = month;
			mDay = day;

			if (isDateAfter(datePicker)) {
				datePicker.init(2015, 8, 1, this);
				T.ss("无效时间");
				timeIsOk = false;
			} else {

				if (TYPE == 1 && isDateBefor(datePicker)) {

					datePicker.init(cal.get(Calendar.YEAR),
							cal.get(Calendar.MONTH),
							cal.get(Calendar.DAY_OF_MONTH), this);
					timeIsOk = false;
					T.ss("无效时间");
				} else {
					timeIsOk = true;
				}
			}
		}
	};

	private boolean isDateAfter(DatePicker tempView) {
		Calendar mCalendar = Calendar.getInstance();
		Calendar tempCalendar = Calendar.getInstance();
		tempCalendar.set(tempView.getYear(), tempView.getMonth(),
				tempView.getDayOfMonth(), 0, 0, 0);
		if (tempCalendar.after(mCalendar)) {
			return true;
		} else {
			return false;
		}
	}

	private boolean isDateBefor(DatePicker eView) {
		Calendar sCalendar = Calendar.getInstance();
		sCalendar.set(startYear, startMonth, startDay);
		Calendar eCalendar = Calendar.getInstance();
		eCalendar.set(eView.getYear(), eView.getMonth(), eView.getDayOfMonth());
		if (eCalendar.before(sCalendar)) {

			return true;
		} else {
			return false;
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
		String url = MyUrls.SELECTTRADELIST;

		map = new HashMap<String, String>();

		map.put("username", MApplication.mSharedPref
				.getSharePrefString(SharedPrefConstant.USERNAME));
		map.put("intime", startTime);
		map.put("outtime", endTime);
		map.put("pageSize", "" + pageSize);
		map.put("page", "" + page);
		map.put("id", "" + typeId);
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
				boolean hasMoreData = false;
				if (returnCode.equals("00")) {
					if (listReturn == null || listReturn.size() == 0) {
						T.ss(getResources().getString(
								R.string.query_nothing_more));
						// finish();
					} else {
						hasMoreData = true;
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
								DingDanListInfoActivity.this,
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
			int count = obj.optInt("count");
			Log.i("result", "---------Count-------" + count);
			if (count < pageSize) {
				lv.setMode(Mode.PULL_DOWN_TO_REFRESH);
			}
			if (count > 0) {
				// Log.i("result", "---------page-------" + page);
				// Log.i("result", "---------dataCount-------" + dataCount);
				for (int i = 0; i < count; i++) {

					String tranding = obj.optJSONArray("date").optJSONObject(i)
							.optString("addtime");
					String account = obj.optJSONArray("date").optJSONObject(i)
							.optString("total");
					String batchNum = obj.optJSONArray("date").optJSONObject(i)
							.optString("carnum");
					String tax = obj.optJSONArray("date").optJSONObject(i)
							.optString("tax");
					String tradingInformation = obj.optJSONArray("date")
							.optJSONObject(i).optString("tradestate");
					// String notTime = judgeConsumptionType(nottime);
					// String tradingInformation = PosTradeState(tradestate,
					// plusstate);
					// 给info设置数据
					OrderInfo info = new OrderInfo();
					info.setTradingTime(tranding);// 交易日期
					info.setConsumptionAmount(account);// 交易金额
					// info.setCardNum(crdno);// 银行卡
					info.setOrderNum(batchNum);// 订单号
					info.setSxAccount(tax);// 手续费
					// info.setMerchantsName(merchantsName);// 商户
					info.setTradingInformation(tradingInformation);// 交易状态
					// info.setNottime(notTime);// 交易状态
					listReturn.add(info);
				}
			}

		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	@Override
	public void onPullDownToRefresh(
			com.handmark.pulltorefresh.library.PullToRefreshBase<ListView> refreshView) {
		// TODO Auto-generated method stub
		page = 1;
		postQueryOrderHttp();
	}

	@Override
	public void onPullUpToRefresh(
			com.handmark.pulltorefresh.library.PullToRefreshBase<ListView> refreshView) {
		// TODO Auto-generated method stub
		page++;
		postQueryOrderHttp();
	}
}
