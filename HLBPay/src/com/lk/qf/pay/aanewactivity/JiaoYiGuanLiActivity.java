package com.lk.qf.pay.aanewactivity;

import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.apache.http.entity.StringEntity;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;
import com.lk.bhb.pay.R;
import com.lk.qf.pay.activity.BaseActivity;
import com.lk.qf.pay.adapter.MySpinnerAdapter;
import com.lk.qf.pay.golbal.MApplication;
import com.lk.qf.pay.golbal.MyUrls;
import com.lk.qf.pay.sharedpref.SharedPrefConstant;
import com.lk.qf.pay.tool.T;
import com.lk.qf.pay.wedget.CommonTitleBar;

public class JiaoYiGuanLiActivity extends BaseActivity implements
		OnClickListener {

	private Spinner sp;
	private EditText edStartTime, edEndTime;
	private TextView tvSuccessCount, tvFenRunAccount;
	private String startTime, endTime, type, typeId = "101", page, accsort,
			time;
	private Button btnHuiZong, btnSearch;
	private List<String> listSpinner;
	private CommonTitleBar title;
	private static int APPID = 2;// 0机构 1代理 2商户
	private Date date;
	private SimpleDateFormat sdf;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.jiaoyi_guanli_layout);
		init();
	}

	private void init() {
		date = new Date();
		sdf = new SimpleDateFormat("yyyyMMdd");
		
		edStartTime = (EditText) findViewById(R.id.ed_jiaoyi_startTime);
		edEndTime = (EditText) findViewById(R.id.ed_jiaoyi_endTime);
		edStartTime.setHint("开始时间:"+sdf.format(date));
		edEndTime.setHint("结束时间:"+sdf.format(date));
		sp = (Spinner) findViewById(R.id.sp_jiaoyi);
//		tvHuizongAccount = (TextView) findViewById(R.id.tv_jiaoyi_huizongAccount);
		// tvSuccessCount = (TextView) findViewById(R.id.tv_jiaoyi_count);
		findViewById(R.id.btn_jiaoyi_query).setOnClickListener(this);
//		findViewById(R.id.btn_jiaoyi_huizong).setOnClickListener(this);
		title = (CommonTitleBar) findViewById(R.id.titlebar_jiaoyi);
		title.setActName("交易管理");
		title.setCanClickDestory(this, true);
		addSpinner();

		if (APPID == 0) {
			accsort = "oem";
		} else if (APPID == 1) {
			accsort = "ag";
		} else if (APPID == 2) {
			accsort = "mer";
		}

		// queryDingDan();

		sp.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {
				// TODO Auto-generated method stub
				type = listSpinner.get(arg2);
				Log.i("result", "----------type-----------" + type);
				getData();
//				queryDingDan();
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				// TODO Auto-generated method stub
			}
		});
	}

	@Override
	public void onClick(View arg0) {
		// TODO Auto-generated method stub

		getData();
		switch (arg0.getId()) {
//		case R.id.btn_jiaoyi_huizong:
//			if (startTime.length() < 8 || endTime.length() < 8) {
//				T.ss("起始时间格式不对");
//				return;
//			}
//			queryDingDan();
//			break;
		case R.id.btn_jiaoyi_query:
			if (startTime.length() < 8 || endTime.length() < 8) {
				T.ss("起始时间格式不对");
				return;
			}
			Intent intent = new Intent(JiaoYiGuanLiActivity.this,
					DingDanListInfoActivity.class);
			intent.putExtra("id", typeId);
			intent.putExtra("startTime", startTime);
			intent.putExtra("endTime", endTime);
			startActivity(intent);
			break;

		default:
			break;
		}
	}

	private void getData() {
		startTime = edStartTime.getText().toString();
		endTime = edEndTime.getText().toString();

		getType();

		if (TextUtils.isEmpty(startTime) || TextUtils.isEmpty(endTime)) {

//			Calendar cal = Calendar.getInstance();
//			cal.add(Calendar.MONTH, -1);
//			Date lastDate = cal.getTime();

			endTime = sdf.format(date);
			startTime = sdf.format(date);
			return;
		}

	}

	private void getType() {
		// type = sp.getSelectedItem().toString();

		if (type.equals(getResources().getString(R.string.yidongzhifu))) {

			typeId = "102";
		} else if (type.equals(getResources().getString(R.string.weixinzhifu))) {

			typeId = "103";
		} else if (type.equals(getResources().getString(R.string.zhifubao))) {

			typeId = "104";
		} else if (type.equals(getResources().getString(R.string.baiduqianbao))) {
			typeId = "105";
		} else if (type
				.equals(getResources().getString(R.string.suningyifubao))) {
			typeId = "106";
		} else {
			typeId = "101";
		}
	}

	/**
	 * 添加 设置spinner
	 */
	private void addSpinner() {
		listSpinner = new ArrayList<String>();

		// listSpinner.add(getResources().getString(R.string.fenrunType));
		listSpinner.add(getResources().getString(R.string.xianxiajiaoyi));
		listSpinner.add(getResources().getString(R.string.yidongzhifu));
		listSpinner.add(getResources().getString(R.string.weixinzhifu));
		listSpinner.add(getResources().getString(R.string.zhifubao));
		listSpinner.add(getResources().getString(R.string.baiduqianbao));
		listSpinner.add(getResources().getString(R.string.suningyifubao));

		MySpinnerAdapter arrayAdapter = new MySpinnerAdapter(this, listSpinner);
		sp.setAdapter(arrayAdapter);

	}
}
