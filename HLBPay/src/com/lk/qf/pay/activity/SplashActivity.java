package com.lk.qf.pay.activity;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.entity.StringEntity;
import org.json.JSONException;
import org.json.JSONObject;

import cn.jpush.android.api.JPushInterface;

import com.alibaba.fastjson.JSON;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;
import com.lk.bhb.pay.R;
import com.lk.qf.pay.aanewactivity.GuideActivity;
import com.lk.qf.pay.golbal.MyUrls;
import com.lk.qf.pay.wedget.view.RoundProgressBar;
import com.squareup.picasso.Picasso;
import com.umeng.analytics.MobclickAgent;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;

public class SplashActivity extends Activity implements OnClickListener {

	private static final int GO_HOME = 1000;
	private static final int GO_GUIDE = 1001;
	// 延迟3秒
	private static final long SPLASH_DELAY_MILLIS = 3000;
	private static final String SHAREDPREFERENCES_NAME = "cft_first_pref";
	private ImageView img;
	private String code;
	private List<String> adList;
	private int progress = 100;
	private RoundProgressBar rpb;
	private boolean isFirstIn;
	private RelativeLayout rl;
	/**
	 * Handler:跳转到不同界面
	 */
	private Handler mHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case GO_HOME:
				goHome();
				break;
			case GO_GUIDE:
				goGuide();
				break;
			}
			super.handleMessage(msg);
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_splash);

		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
			// //透明状态栏
			getWindow().addFlags(
					WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
			// setTranslucentStatus(true);
		}

		MobclickAgent.updateOnlineConfig(this);
		init();
		getAdList();
	}

	@Override
	protected void onResume() {
		super.onResume();
		JPushInterface.onResume(SplashActivity.this);

	}

	@Override
	protected void onPause() {
		super.onPause();
		JPushInterface.onPause(SplashActivity.this);
	}

	private void init() {
		adList = new ArrayList<String>();
		img = (ImageView) findViewById(R.id.img_spash);
		rpb = (RoundProgressBar) findViewById(R.id.spash_pb);
		// 读取SharedPreferences中需要的数据
		// 使用SharedPreferences来记录程序的使用次数
		SharedPreferences preferences = getSharedPreferences(
				SHAREDPREFERENCES_NAME, MODE_PRIVATE);
		// 取得相应的值，如果没有该值，说明还未写入，用true作为默认值
		isFirstIn = preferences.getBoolean("isFirstIn", true);
		// 判断程序与第几次运行，如果是第一次运行则跳转到引导界面，否则跳转到主界面
		if (isFirstIn) {
			mHandler.sendEmptyMessageDelayed(GO_GUIDE, SPLASH_DELAY_MILLIS);
		} else {
			// 使用Handler的postDelayed方法，3秒后执行跳转到MainActivity
			mHandler.sendEmptyMessageDelayed(GO_HOME, SPLASH_DELAY_MILLIS);
		}
		rl = (RelativeLayout) findViewById(R.id.rl_spash);
		rl.setOnClickListener(this);
	}

	/**
	 * 设置启动也图片
	 */
	private void setImage(String imgUrl) {
		if (imgUrl == null || imgUrl.equals("")) {
			// Picasso.with(this).load(MyUrls.ROOT_URL1 + "/bank/yl.png").fit()
			// .into(holder.imgLogo);
			img.setBackgroundResource(R.drawable.spash);
		} else {
			Picasso.with(this).load(MyUrls.ROOT_URL2 + imgUrl).fit().into(img);
		}
	}

	private void goGuide() {
		Intent intent = new Intent(this, GuideActivity.class);
		startActivity(intent);
		finish();
	}

	private void goHome() {
		Intent intent = new Intent(this, LoginActivity.class);
		startActivity(intent);
		finish();
	}

	private void getAdList() {
		
		Map<String, String> map = new HashMap<String, String>();
		RequestParams params = new RequestParams();
		String url = MyUrls.VPLIST;
		map = new HashMap<String, String>();
		map.put("type", "06");// 01是首页轮播图 02夺宝轮播图 03贷款轮播图 04商城轮播图 05安装引导页
								// 06app启动图
		String json = JSON.toJSONString(map);
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
				// T.ss("查询失败");
				adList.clear();
				setImage("");
			}

			@Override
			public void onSuccess(ResponseInfo<String> response) {
				// TODO Auto-generated method stub
				String strReturnLogin = response.result;
				Log.i("result", "---------------定单-returnjson---"
						+ strReturnLogin);
				jsonDetailVp(strReturnLogin);
				
			}
		});

	}

	/**
	 * 解析 Json字符串 登录返回结果
	 * 
	 * @param str
	 * @return
	 */
	private void jsonDetailVp(String str) {

		try {
			JSONObject obj = new JSONObject(str);
			code = obj.optString("CODE");
			int count = obj.optInt("count");
			if (count > 0) {
				for (int i = 0; i < count; i++) {
					String imgUrl1 = obj.optJSONArray("date").optJSONObject(i)
							.optString("image1");// 图片
//					String imgUrl2 = obj.optJSONArray("date").optJSONObject(i)
//							.optString("image2");// 图片
//					String imgUrl3 = obj.optJSONArray("date").optJSONObject(i)
//							.optString("image3");// 图片
					if (imgUrl1.length() > 0) {
						adList.add(imgUrl1);
					}
//					if (imgUrl2.length() > 0) {
//						adList.add(imgUrl2);
//					}
//					if (imgUrl3.length() > 0) {
//						adList.add(imgUrl3);
//					}
				}
				rl.setVisibility(View.VISIBLE);
				setThre();
				setImage(adList.get(0));
			} else {
				setImage("");
			}

		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	private void setThre() {
		new Thread(new Runnable() {

			@Override
			public void run() {
				while (progress >= 0) {
					progress -= 3;
					rpb.setProgress(progress);
					try {
						Thread.sleep(120);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}

			}
		}).start();
	}

	@Override
	public void onClick(View arg0) {
		// TODO Auto-generated method stub
		if (isFirstIn) {
			goGuide();
		} else {
			goHome();
		}
	}

}
