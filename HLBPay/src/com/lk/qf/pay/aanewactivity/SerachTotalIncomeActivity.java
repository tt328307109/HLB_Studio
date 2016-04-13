package com.lk.qf.pay.aanewactivity;

import java.io.UnsupportedEncodingException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

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
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.DatePicker.OnDateChangedListener;

import com.alibaba.fastjson.JSON;
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
import com.lk.qf.pay.wedget.CommonTitleBar;

public class SerachTotalIncomeActivity extends BaseActivity implements
		OnClickListener {

	private String startTime;
	private String endTime;
	private Button btnQuery;
	private String phone;
	private TextView tvStartTime, tvEndTime,tvAccount;
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
	private Date date;
	private SimpleDateFormat sdf;
	private View view;
	private DatePicker datePicker;
	private CommonTitleBar title;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.serach_total_income_layout);
		init();
	}

	private void init() {
		title = (CommonTitleBar) findViewById(R.id.titlebar_posLoan_total);
		title.setActName("总收益");
		title.setCanClickDestory(this, true);

		date = new Date();
		sdf = new SimpleDateFormat("yyyyMM");
		startTime = sdf.format(date)+"01";
		endTime = sdf.format(date)+"31";
		tvAccount = (TextView) findViewById(R.id.tv_total_incom_search_account);
		tvStartTime = (TextView) findViewById(R.id.ed_total_income_startTime1);
		tvEndTime = (TextView) findViewById(R.id.ed_total_income_endTime1);
		tvStartTime.setText(startTime);
		tvEndTime.setText(endTime);

		btnQuery = (Button) findViewById(R.id.btn_total_income_query1);
		btnQuery.setOnClickListener(this);
		findViewById(R.id.rl_total_income_startDate).setOnClickListener(this);
		findViewById(R.id.rl_total_income_endDate).setOnClickListener(this);
		
		queryTotalIncome();

	}

	@Override
	public void onClick(View arg0) {
		// TODO Auto-generated method stub
		switch (arg0.getId()) {
		case R.id.rl_total_income_startDate:
			TYPE = 0;
			showDateDialog();
			break;
		case R.id.rl_total_income_endDate:
			TYPE = 0;
			showDateDialog();
			break;
		case R.id.btn_total_income_query1:
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
			queryTotalIncome();
			break;

		default:
			break;
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
	 * 总收益查询
	 */
	private void queryTotalIncome() {
		showLoadingDialog();
		RequestParams params = new RequestParams();
		HashMap<String, String> map = new HashMap<String, String>();

		map.put("username", MApplication.mSharedPref
				.getSharePrefString(SharedPrefConstant.USERNAME));
		map.put("token", MApplication.mSharedPref
				.getSharePrefString(SharedPrefConstant.TOKEN));
		map.put("aglevel", MApplication.mSharedPref
				.getSharePrefString(SharedPrefConstant.AGLEVEL));
		map.put("dealtype", "0");
		map.put("begintime", startTime);
		map.put("endtime", endTime);
		map.put("type", "02");
		String json = JSON.toJSONString(map);
		Log.i("result", "----ddd-----------" + json);
		try {
			StringEntity bodyEntity = new StringEntity(json, "UTF-8");
			params.setBodyEntity(bodyEntity);
		} catch (UnsupportedEncodingException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		HttpUtils httpUtils = new HttpUtils();
		String url = MyUrls.MERFENRUN;

		httpUtils.send(HttpMethod.POST, url, params,
				new RequestCallBack<String>() {

					@Override
					public void onFailure(HttpException arg0, String arg1) {
						// TODO Auto-generated method stub

						T.ss("操作超时");
						dismissLoadingDialog();
					}

					@Override
					public void onSuccess(ResponseInfo<String> response) {
						// TODO Auto-generated method stub
						dismissLoadingDialog();
						String str = response.result;
						Log.i("result", "----ddd-----------" + str);
						String code = "";
						String message = "";
						JSONObject obj = null;
						try {
							obj = new JSONObject(str);
							code = obj.optString("CODE");
							message = obj.optString("MESSAGE");
						} catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						if (code.equals("00")) {
							tvAccount.setText(obj.optString("totalMoney"));
						} else {
							T.ss(message);
						}
					}
				});
	}


}
