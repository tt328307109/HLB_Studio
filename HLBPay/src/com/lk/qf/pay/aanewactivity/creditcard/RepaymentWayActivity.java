package com.lk.qf.pay.aanewactivity.creditcard;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;

import org.apache.http.entity.StringEntity;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.alibaba.fastjson.JSON;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;
import com.lk.bhb.pay.R;
import com.lk.qf.pay.activity.BaseActivity;
import com.lk.qf.pay.beans.Xinyongkainfo;
import com.lk.qf.pay.golbal.MApplication;
import com.lk.qf.pay.golbal.MyUrls;
import com.lk.qf.pay.sharedpref.SharedPrefConstant;
import com.lk.qf.pay.tool.T;
import com.lk.qf.pay.wedget.CommonTitleBar;
import com.lk.qf.pay.wedget.customdialog.AlertDialog;

public class RepaymentWayActivity extends BaseActivity implements
		OnClickListener {

	private Button self, kls, ransom, hkmx;
	private Xinyongkainfo info;
	private CommonTitleBar title;
	private String type;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_repaymentway);
		self = (Button) findViewById(R.id.btn_creditCard_applyRepay);
		kls = (Button) findViewById(R.id.btn_creditCard_payByCardNow);
		ransom = (Button) findViewById(R.id.btn_creditCard_redemption);
		ransom.setOnClickListener(this);
		hkmx = (Button) findViewById(R.id.btn_creditCard_repayList);
		hkmx.setOnClickListener(this);
		self.setOnClickListener(this);
		kls.setOnClickListener(this);
		findViewById(R.id.btn_creditCard_queryAddress).setOnClickListener(this);
		title = (CommonTitleBar) findViewById(R.id.titlebar_choose_repaymentway);
		title.setActName("详情");
		title.setCanClickDestory(this, true);

		Intent intent = getIntent();
		if (intent != null) {
			info = (Xinyongkainfo) intent.getParcelableExtra("info");
			type = info.getType();
			if (!type.equals("8")) {
				// type既不为8的时候。不能点-我要还款按钮变灰
				// self.setBackgroundResource(R.drawable.button1_1);
				// self.setTextColor(Color.parseColor("#5f5f5f"));
				// 卡立刷灰//8的时候才能点
				// kls.setBackgroundResource(R.drawable.button1_1);
				// kls.setTextColor(Color.parseColor("#5f5f5f"));
			}
			if (!type.equals("6") && !type.equals("7") && !type.equals("0")) {
				// hkmx.setBackgroundResource(R.drawable.button1_1);
				// hkmx.setTextColor(Color.parseColor("#5f5f5f"));
			}
			if (type.equals("0") || type.equals("7") || type.equals("8")
					|| type.equals("3")) {
				// 0 和 7 才能赎回
			} else {
				// 其他不能赎回。按钮变色。字体变色
				// ransom.setBackgroundResource(R.drawable.button1_1);
				// ransom.setTextColor(Color.parseColor("#5f5f5f"));
			}
		}

	}

	@Override
	public void onClick(View v) {
		if (v.getId() == R.id.btn_creditCard_applyRepay) {
			if (!type.equals("8")) {
				T.ss("当前状态不能申请贷款！");
				return;
			}
			Intent i = new Intent(RepaymentWayActivity.this,
					ApplyRepaymentDetailActivity.class);
			i.putExtra("info", info);
			startActivity(i);

		} else if (v.getId() == R.id.btn_creditCard_payByCardNow) {
			if (!type.equals("8")) {
				T.ss("当前状态不能使用卡立刷！");
			} else {
				T.ss("建设中");
			}
		} else if (v.getId() == R.id.btn_creditCard_redemption) {
			if (type.equals("0") || type.equals("7") || type.equals("8")
					|| type.equals("3")) {
				// 0 ,3,7,8 才能赎回
				Intent intent = new Intent(RepaymentWayActivity.this,
						RansomActivity.class);
				intent.putExtra("info", info);
				startActivity(intent);
			} else {
				T.ss("暂不能赎回");
			}

		} else if (v.getId() == R.id.btn_creditCard_repayList) {
			if (!type.equals("6") && !type.equals("7") && !type.equals("0")) {
				T.ss("暂无明细！");
			} else {
				Intent i = new Intent(RepaymentWayActivity.this,
						CreditCardRepayDateListActivity.class);
				i.putExtra("info", info);
				startActivity(i);
			}
		} else if (v.getId() == R.id.btn_creditCard_queryAddress) {// 查看邮寄地址
			getAddress();
		}
	}

	/**
	 * 获取收件地址
	 */
	private void getAddress() {
		RequestParams params = new RequestParams();
		HashMap<String, String> map = new HashMap<String, String>();
		map.put("username", MApplication.mSharedPref
				.getSharePrefString(SharedPrefConstant.USERNAME));

		showLoadingDialog();
		String json = JSON.toJSONString(map);
		try {
			StringEntity bodyEntity = new StringEntity(json, "UTF-8");
			params.setBodyEntity(bodyEntity);
		} catch (UnsupportedEncodingException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		HttpUtils httpUtils = new HttpUtils();
		String url = MyUrls.ROOT_URL_GETADDRESS;

		httpUtils.send(HttpMethod.POST, url, params,
				new RequestCallBack<String>() {

					@Override
					public void onFailure(HttpException arg0, String arg1) {
						// TODO Auto-generated method stub
						T.ss("操作超时!");
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
						if (code.endsWith("00")) {
							String address = obj.optString("Addrdss");
							String name = obj.optString("Name");
							String phoneNum = obj.optString("TelPhone");
							showLoanState(name, phoneNum, address);
						} else {
							T.ss(message);
						}
					}
				});
	}

	/**
	 * 提示添加信用卡状态
	 */
	private void showLoanState(String name, String phoneNum, String address) {
		new AlertDialog(RepaymentWayActivity.this)
				.builder()
				.setTitle("邮寄地址")
				.setCancelable(false)
				// 点击周边不消失
				.setMsg("收件地址:" + address + "\n\n" + "收件人:" + name + "\n\n"
						+ "收件电话:" + phoneNum)
				.setPositiveButton("确定", new OnClickListener() {
					@Override
					public void onClick(View v) {
						
					}
				}).show();
	}
}
