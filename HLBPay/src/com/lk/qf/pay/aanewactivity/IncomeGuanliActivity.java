package com.lk.qf.pay.aanewactivity;

import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

import org.apache.http.entity.StringEntity;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.DatePicker;
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
import com.lk.qf.pay.activity.LoginActivity;
import com.lk.qf.pay.golbal.MApplication;
import com.lk.qf.pay.golbal.MyUrls;
import com.lk.qf.pay.sharedpref.SharedPrefConstant;
import com.lk.qf.pay.tool.T;
import com.lk.qf.pay.wedget.CommonTitleBar;

public class IncomeGuanliActivity extends BaseActivity implements
		OnClickListener {

	private CommonTitleBar title;
	private TextView tvTotalAccount, tvMposAccount, tvZjAccount, tvT0Account,
			tvUpAccount, tvSuperpPayAccount, tvXykAccount;// 收益金额
	private SimpleDateFormat sdf;
	private Date date;
	private String startTime = "", endTime = "", type = "";
	private String appType = "0";// 0表示商户版 1表示分公司版
	private String action = "all";// 总分润

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.fenrun_guanli_layout);
		init();
	}

	private void init() {
		appType = MApplication.mSharedPref
				.getSharePrefString(SharedPrefConstant.AGLEVEL);
		tvTotalAccount = (TextView) findViewById(R.id.tv_sy_all_account);
		tvMposAccount = (TextView) findViewById(R.id.tv_sy_mpos_account);
		tvZjAccount = (TextView) findViewById(R.id.tv_sy_zj_account);
		tvXykAccount = (TextView) findViewById(R.id.tv_sy_xyk_account);
		tvT0Account = (TextView) findViewById(R.id.tv_sy_t0_account);
		tvUpAccount = (TextView) findViewById(R.id.tv_sy_up_account);
		tvSuperpPayAccount = (TextView) findViewById(R.id.tv_sy_superpay_account);

		findViewById(R.id.rl_sy_all).setOnClickListener(this);
		findViewById(R.id.rl_sy_mpos).setOnClickListener(this);
		findViewById(R.id.rl_sy_zhuangji).setOnClickListener(this);
		findViewById(R.id.rl_sy_xyk).setOnClickListener(this);
		findViewById(R.id.rl_sy_t0).setOnClickListener(this);
		findViewById(R.id.rl_sy_up).setOnClickListener(this);
		findViewById(R.id.rl_sy_superpay).setOnClickListener(this);

		title = (CommonTitleBar) findViewById(R.id.titlebar_fen_run_guanli);
		title.setActName("收益管理");
		title.setCanClickDestory(this, true);

		date = new Date();
		sdf = new SimpleDateFormat("yyyyMM");
		startTime = sdf.format(date) + "01";
		endTime = sdf.format(date) + "31";
		queryIncome();

	}

	@Override
	public void onClick(View arg0) {
		// TODO Auto-generated method stub
		Log.i("result", "-------appType------" + appType);
		switch (arg0.getId()) {
		case R.id.rl_sy_all:
			action = "all";
			break;
		case R.id.rl_sy_mpos:
			action = ShouyiListActivity.MPOSINCOME;

			break;
		case R.id.rl_sy_zhuangji:
			action = ShouyiListActivity.ZJINCOME;
			break;
		 case R.id.rl_sy_xyk:
		 action = "xyk";
		
		 break;
		case R.id.rl_sy_t0:
			action = ShouyiListActivity.T0INCOME;

			break;
		case R.id.rl_sy_up:
			action = ShouyiListActivity.UPMER;

			break;
		case R.id.rl_sy_superpay:
			action = ShouyiListActivity.SUPERPAY;
			break;

		default:
			break;
		}
		Intent intent = null;

		if (action.equals("all")) {
			intent = new Intent(IncomeGuanliActivity.this,
					SerachTotalIncomeActivity.class);// 总收益
		} else if (appType.equals("4") && !action.equals("all")) {
			intent = new Intent(IncomeGuanliActivity.this,
					ShouyiFGSListActivity.class);// 分公司
		} else if (!appType.equals("4") && !action.equals("all")) {
			intent = new Intent(IncomeGuanliActivity.this,
					ShouyiListActivity.class);// 其他
		}
		intent.setAction(action);
		startActivity(intent);
	}

	/**
	 * 收益查询
	 */
	private void queryIncome() {

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
					}

					@Override
					public void onSuccess(ResponseInfo<String> response) {
						// TODO Auto-generated method stub

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
							tvTotalAccount.setText(obj.optString("totalMoney"));
							tvMposAccount.setText(obj.optString("dealtotal"));
							tvZjAccount.setText(obj.optString("zhuangjitotal"));
							tvXykAccount.setText(obj
									.optString("createcardtotal"));
							tvT0Account.setText(obj.optString("t0total"));
							tvUpAccount.setText(obj.optString("upleveltotal"));
							tvSuperpPayAccount.setText(obj
									.optString("superpay"));
						} else {
							T.ss(message);
						}
					}
				});
	}

}
