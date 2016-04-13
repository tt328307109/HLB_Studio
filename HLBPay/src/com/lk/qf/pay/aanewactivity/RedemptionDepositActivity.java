package com.lk.qf.pay.aanewactivity;

import java.io.UnsupportedEncodingException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
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
import com.lk.qf.pay.golbal.MApplication;
import com.lk.qf.pay.golbal.MyUrls;
import com.lk.qf.pay.sharedpref.SharedPrefConstant;
import com.lk.qf.pay.tool.T;
import com.lk.qf.pay.utils.MyMdFivePassword;
import com.lk.qf.pay.utils.TimeUtils;
import com.lk.qf.pay.wedget.CommonTitleBar;
import com.lk.qf.pay.wedget.view.RoundProgressBar;
import com.lk.qf.pay.wedget.view.TasksCompletedView;

public class RedemptionDepositActivity extends BaseActivity implements
		OnClickListener {

	private CommonTitleBar title;
	private float ratio;
	private TasksCompletedView mTasksView;
	private RoundProgressBar rpBar;
	private TextView tvShowAccount,tvTime;
	private Button btnShow;
	private int progress = 0;
	private int num = 0;

	// private RelativeLayout rl;
	// private String isFree;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.redemption_deposit_layout);

		title = (CommonTitleBar) findViewById(R.id.titlebar_redemption);
		title.setActName("押金管理");
		title.setCanClickDestory(this, true);
		rpBar = (RoundProgressBar) findViewById(R.id.roundProgressBar01_id);
		btnShow = (Button) findViewById(R.id.btn_show_isCanShuhui);
		tvShowAccount = (TextView) findViewById(R.id.tv_yajin_account);
		tvTime = (TextView) findViewById(R.id.tv_show_shuhui_time);

		// rl = (RelativeLayout) findViewById(R.id.rl_yajin_beizhu);//1为免押金
		// 0为未免押金
		// isFree = MApplication.mSharedPref
		// .getSharePrefString(SharedPrefConstant.STITE);
		// if (isFree.equals("1")) {
		// rl.setVisibility(View.GONE);
		// }
		btnShow.setOnClickListener(this);
		getAccount();
	}

	@Override
	public void onClick(View arg0) {
		// TODO Auto-generated method stub

		if (btnShow.getText().equals("赎回押金")) {
			getBackAccount();
		}
	}

	private void getAccount() {
		float account = 0;
		showLoadingDialog();

		RequestParams params = new RequestParams();
		String url = MyUrls.MPOSPERCOUNT;
		Map<String, String> map = new HashMap<String, String>();
		map.put("username", MApplication.mSharedPref
				.getSharePrefString(SharedPrefConstant.USERNAME));
		String json = JSON.toJSONString(map);
		Log.i("result", "----------------dd--------" + json);
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
				tvShowAccount.setText("操作超时");
				dismissLoadingDialog();
			}

			@Override
			public void onSuccess(ResponseInfo<String> response) {
				// TODO Auto-generated method stub
				dismissLoadingDialog();
				String code = "";
				String message = "";
				String str = response.result;
				JSONObject obj = null;
				Log.i("result", "----------------dd--------" + response.result);
				try {
					obj = new JSONObject(str);
					code = obj.optString("CODE");
					message = obj.optString("MESSAGE");
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				if (code.equals("00")) {
					String isSh = obj.optString("isSH");// en已赎回，no未赎回
					// account
					String account = obj.optString("total");
					String time = obj.optString("addtime");
					
					// String account = "100.98";
					float progre1 = Float.parseFloat(account);
					DecimalFormat df = new DecimalFormat("0.00");
					String userAccount = df.format(progre1);

					float progre = Float.parseFloat(userAccount);
					tvShowAccount.setText("您目前已刷费率金额为" + progre1 + "元");

					float totalAccount = 300000.00f;
					float p = progre / totalAccount;
					Log.i("result", "-----------p--------" + p);
					String baifen = df.format(p * 100) + "%";
					progress = (int) (p * 100);
					Log.i("result", "-----------progress--------" + progress);
					new Thread(new ProgressRunable()).start();
					if (p > 0.0001) {
						btnShow.setText(baifen);
					} else if (p < 0.0001 && p > 0) {
						btnShow.setText("0.00+%");
					} else {
						btnShow.setText("暂无");
					}
					if (isSh.equals("en")) {
						tvTime.setText("赎回时间:"+TimeUtils.changeDateFormat("yyyy-MM-dd/HH:mm:ss", time));
						btnShow.setText("已赎回");
						btnShow.setBackgroundResource(R.drawable.btn_white);
					} else {
						if (progress == 100) {
							btnShow.setText("赎回押金");
							btnShow.setBackgroundResource(R.drawable.btn_white);
						}
					}
				} else {
					T.ss(message);
				}
			}
		});
	}

	/**
	 * 赎回
	 */
	private void getBackAccount() {
		showLoadingDialog();
		RequestParams params = new RequestParams();
		String url = MyUrls.YAJINSHUHUI;
		Map<String, String> map = new HashMap<String, String>();
		map.put("username", MApplication.mSharedPref
				.getSharePrefString(SharedPrefConstant.USERNAME));

		String json = JSON.toJSONString(map);
		Log.i("result", "----------------dd--------" + json);
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
			}

			@Override
			public void onSuccess(ResponseInfo<String> response) {
				// TODO Auto-generated method stub
				dismissLoadingDialog();
				String code = "";
				String message = "";
				String str = response.result;
				JSONObject obj = null;
				Log.i("result", "----------------dd--------" + response.result);
				try {
					obj = new JSONObject(str);
					code = obj.optString("CODE");
					message = obj.optString("MESSAGE");
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				T.ss(message);
			}
		});
	}

	class ProgressRunable implements Runnable {

		@Override
		public void run() {
			while (num < progress) {
				// if (num==100) {
				// tvShow.setText("赎回押金");
				// }else{
				// tvShow.setText(""+num+"%");
				// }
				Log.i("result", "----------num------------" + num);
				num += 1;
				rpBar.setProgress(num);
				try {
					Thread.sleep(20);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}
}
