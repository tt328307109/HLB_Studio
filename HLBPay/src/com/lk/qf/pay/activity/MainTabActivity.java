package com.lk.qf.pay.activity;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.lk.bhb.pay.R;
import com.lk.pay.communication.AsyncHttpResponseHandler;
import com.lk.qf.pay.beans.BasicResponse;
import com.lk.qf.pay.beans.BindDeviceInfo;
import com.lk.qf.pay.dialog.MyDialog;
import com.lk.qf.pay.fragment.MainFragment;
import com.lk.qf.pay.fragment.MerchantFragment;
import com.lk.qf.pay.fragment.NoticeAFragment;
import com.lk.qf.pay.golbal.Urls;
import com.lk.qf.pay.golbal.User;
import com.lk.qf.pay.tool.AppManager;
import com.lk.qf.pay.tool.AppUpdateUtil;
import com.lk.qf.pay.tool.Logger;
import com.lk.qf.pay.tool.MyHttpClient;
import com.lk.qf.pay.tool.T;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MainTabActivity extends BaseActivity implements
		OnClickListener {

	private int[] ids = new int[] { R.id.main_tab_account,
			R.id.main_tab_function, R.id.main_tab_more };
	private ImageView iv4, iv1, iv3;
	private TextView tv4, tv1, tv3;
	private Context mContext;
	private FragmentManager fm;
	private String currentFName;
	private HashMap<String, Fragment> fragments;
	private NoticeAFragment notice;
	private MerchantFragment more;
	private MainFragment account;
	FragmentTransaction fragmentTransation;
	List<BindDeviceInfo> devices = new ArrayList<BindDeviceInfo>();
	private ArrayList<BindDeviceInfo> deviceList;

	private final int QUERY_SUCCESS = 99;
	private String mcityName;
	private String code;

	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		setContentView(R.layout.tab_main_bottom);	
		checkUpdate();
		mContext = MainTabActivity.this;
		initView();
		deviceList = new ArrayList<BindDeviceInfo>();
		getUserInfo();
	}

	private void initView() {
		iv1 = (ImageView) findViewById(R.id.main_tab_iv1);
		iv3 = (ImageView) findViewById(R.id.main_tab_iv3);
		iv4 = (ImageView) findViewById(R.id.main_tab_iv4);
		
		tv1 = (TextView) findViewById(R.id.main_tab_tv1);
		tv3 = (TextView) findViewById(R.id.main_tab_tv3);
		tv4 = (TextView) findViewById(R.id.main_tab_tv4);
		
		for (int i = 0; i < ids.length; i++) {
			findViewById(ids[i]).setOnClickListener(this);
		}
		
		fm = getSupportFragmentManager();
		fragments = new HashMap<String, Fragment>();
		account = new MainFragment();
		fragments.put("account", account);
		// 初始化第一个Fragment
		FragmentTransaction transation = fm.beginTransaction();
		transation.add(R.id.frame_content, account);
		transation.commit();
		currentFName = "account";
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.main_tab_function:
			
			
			break;
		case R.id.main_tab_account:
			fragmentTransation = fm.beginTransaction();
			if (null == account) {
				fragmentTransation.hide(fragments.get(currentFName));
				account = new MainFragment();
				fragmentTransation.add(R.id.frame_content, more);
				fragments.put("account", account);
				fragmentTransation.commit();
			} else {
				fragmentTransation.hide(fragments.get(currentFName));
				fragmentTransation.show(account);
				fragmentTransation.commit();
			}
			currentFName = "account";
			switchStatus(2);
			break;
		case R.id.main_tab_more:
			fragmentTransation = fm.beginTransaction();
			if (null == more) {
				fragmentTransation.hide(fragments.get(currentFName));
				more = new MerchantFragment();
				fragmentTransation.add(R.id.frame_content, more);
				fragments.put("more", more);
				fragmentTransation.commit();
			} else {
				fragmentTransation.hide(fragments.get(currentFName));
				fragmentTransation.show(more);
				fragmentTransation.commit();
			}
			currentFName = "more";
			switchStatus(3);
			break;
		
		}
	}

	private void switchStatus(int which) {
		if (which == 1) {
			((ImageView) findViewById(R.id.main_tab_iv1))
					.setImageDrawable(getResources().getDrawable(
							R.drawable.app_blue));
			((ImageView) findViewById(R.id.main_tab_iv2))
					.setImageDrawable(getResources().getDrawable(
							R.drawable.pe128));
			((ImageView) findViewById(R.id.main_tab_iv3))
					.setImageDrawable(getResources().getDrawable(
							R.drawable.more128));
			((TextView) findViewById(R.id.main_tab_tv1))
					.setTextColor(getResources().getColor(
							R.color.red));
			((TextView) findViewById(R.id.main_tab_tv2))
					.setTextColor(getResources().getColor(R.color.textblack));
			((TextView) findViewById(R.id.main_tab_tv3))
					.setTextColor(getResources().getColor(R.color.textblack));
		} else if (which == 2) {
			((ImageView) findViewById(R.id.main_tab_iv1))
					.setImageDrawable(getResources().getDrawable(
							R.drawable.app128));
			((ImageView) findViewById(R.id.main_tab_iv2))
					.setImageDrawable(getResources().getDrawable(
							R.drawable.pe128_blue));
			((ImageView) findViewById(R.id.main_tab_iv3))
					.setImageDrawable(getResources().getDrawable(
							R.drawable.more128));
			// findViewById(R.id.main_tab_iv1).setBackgroundResource(R.drawable.app128);
			// findViewById(R.id.main_tab_iv2).setBackgroundResource(R.drawable.pe128_blue);
			// findViewById(R.id.main_tab_iv3).setBackgroundResource(R.drawable.more128);
			((TextView) findViewById(R.id.main_tab_tv1))
					.setTextColor(getResources().getColor(R.color.textblack));
			((TextView) findViewById(R.id.main_tab_tv2))
					.setTextColor(getResources().getColor(
							R.color.red));
			((TextView) findViewById(R.id.main_tab_tv3))
					.setTextColor(getResources().getColor(R.color.textblack));
		} else if (which == 3) {
			((ImageView) findViewById(R.id.main_tab_iv1))
					.setImageDrawable(getResources().getDrawable(
							R.drawable.app128));
			((ImageView) findViewById(R.id.main_tab_iv2))
					.setImageDrawable(getResources().getDrawable(
							R.drawable.pe128));
			((ImageView) findViewById(R.id.main_tab_iv3))
					.setImageDrawable(getResources().getDrawable(
							R.drawable.more128_blue));
			
			((TextView) findViewById(R.id.main_tab_tv1))
					.setTextColor(getResources().getColor(R.color.textblack));
			((TextView) findViewById(R.id.main_tab_tv2))
					.setTextColor(getResources().getColor(R.color.textblack));
			((TextView) findViewById(R.id.main_tab_tv3))
					.setTextColor(getResources().getColor(
							R.color.red));
		} else {
			
		}
	}

	MyDialog dialog = null;

	private void getUserInfo() {
		dialog=new MyDialog(this);
		dialog.setCancelable(false);
		dialog.setText("请稍后...");
		dialog.show();
		HashMap<String, String> map = new HashMap<String, String>();
		map.put("custMobile", User.uAccount);
		MyHttpClient.post(mContext, Urls.GET_USER_INFO, map,
				new AsyncHttpResponseHandler() {

					@Override
					public void onSuccess(int statusCode, Header[] headers,
							byte[] responseBody) {
						if (responseBody != null) {
							Logger.json(new String(responseBody));
							try {
								JSONObject json = new JSONObject(new String(responseBody))
										.getJSONObject("REP_BODY");
								if (json.getString("RSPCOD").equals("000000")) {
									User.cardNum = json.optInt("cardNum");
									User.termNum = json.optInt("termNum");
									User.uStatus = json.optInt("custStatus");
									User.cardBundingStatus=json.optInt("cardBundingStatus");
								} else {
									showDialog(json.getString("RSPMSG"));
								}
									
							} catch (JSONException e) {
								e.printStackTrace();
							}

						}
					}
					 @Override
					public void onFinish() {
						 dialog.dismiss();
					}
					@Override
					public void onFailure(int statusCode, Header[] headers,
							byte[] responseBody, Throwable error) {
						T.showCustomeShort(mContext, "网络错误");
					}

				});

		/*MyHttpClient.post(mContext, Urls.BIND_DEVICE_LIST, map,
				new AsyncHttpResponseHandler() {

					@Override
					public void onSuccess(int statusCode, Header[] headers,
							byte[] responseBody) {
						Logger.json("[终端列表]", responseBody);
						try {
							BasicResponse res = new BasicResponse(responseBody)
									.getResult();
							if (res.isSuccess()) {
								JSONArray array = res.getJsonBody()
										.getJSONArray("termList");
								for (int i = 0; i < array.length(); i++) {
									BindDeviceInfo d = new BindDeviceInfo();
									d.setAgentId(array.getJSONObject(i)
											.optString("agentId"));
									d.setTermNo(array.getJSONObject(i)
											.optString("termNo"));
									JSONArray rates = array.getJSONObject(i)
											.optJSONArray("rate");
									List<PosRate> ar = new ArrayList<PosRate>();
									for (int j = 0; j < rates.length(); j++) {
										PosRate temp = new PosRate();
										temp.setRateDesc(rates.getJSONObject(j)
												.optString("rateDesc"));
										temp.setRateNo(rates.getJSONObject(j)
												.optString("rateNo"));
										temp.setRateMaximun(rates
												.getJSONObject(j).optString(
														"rateMaximun"));
										ar.add(temp);
									}
									d.setRate(ar);
									devices.add(d);
								}
								User.bindDevices=devices;
								// Gson g=new Gson();
								// devices=(List<BindDeviceInfo>)
								// g.fromJson(res.getJsonBody().toString(),
								// BindDeviceInfo.class);
								System.out.println(devices.size());
								User.bindDevices = devices;
								if (array.length() == 0) {
									User.bindStatus = 0;
								} else {
									User.bindStatus = 1;
								}
							} else {
								T.ss(res.getMsg());
							}
						} catch (JSONException e) {
							// TODO 自动生成的 catch 块
							e.printStackTrace();
						}

					}

					@Override
					public void onFailure(int statusCode, Header[] headers,
							byte[] responseBody, Throwable error) {

					}

					@Override
					public void onFinish() {
						super.onFinish();
						if (dialog.isShowing()) {
							dialog.dismiss();
						}
					}
				});*/
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		MyHttpClient.cancleRequest(mContext);
	}

	private long exit = System.currentTimeMillis();

	@Override
	public boolean dispatchKeyEvent(KeyEvent event) {
		if (event.getAction() == KeyEvent.ACTION_DOWN
				&& event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
			exitApp();
			return false;
		} else {
			return super.dispatchKeyEvent(event);
		}
	}

	/**
	 * @Title: exitApp
	 * @Description: 退出app
	 */
	private void exitApp() {
		if (!isExit) {
			isExit = true;
			Toast.makeText(this, R.string.quit_app, Toast.LENGTH_SHORT).show();
			mHandler.sendEmptyMessageDelayed(0, 2000);
		} else {
			onBackPressed();
			AppManager.getAppManager().finishActivity();
			AppManager.getAppManager().AppExit(mContext);
		}
	}

	private boolean isExit = false;
	Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			isExit = false;
		}
	};
	
	private void checkUpdate() {
		PackageInfo pkg = null;
		try {
			pkg = getPackageManager().getPackageInfo(getApplication().getPackageName(), 0);
		} catch (NameNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}            
			String appName = pkg.applicationInfo.loadLabel(getPackageManager()).toString();  
			HashMap<String, String> params = new HashMap<String, String>();
			params.put("appName",appName );
			MyHttpClient.post(MainTabActivity.this, Urls.CHECK_UPDATE, params,
					new AsyncHttpResponseHandler() {

						@Override
						public void onSuccess(int statusCode, Header[] headers,
								byte[] responseBody) {
							Logger.json(new String(responseBody));
	                        try {
								BasicResponse response=new BasicResponse(responseBody).getResult();
								if(response.isSuccess()){
									String status=response.getJsonBody().optString("checkState");
									
									if(status.equals("0")){
										//T.ss("已经是最新版本");
									}else if(status.equals("1")){
	                                     String note=response.getJsonBody().optString("fileDesc");
	                                     String url=response.getJsonBody().optString("fileUrl");
	                                     AppUpdateUtil update=new AppUpdateUtil(MainTabActivity.this, url);
	                                     update.showUpdateNoticeDialog(note);
									}else if(status.equals("2")){
										String note=response.getJsonBody().optString("fileDesc");
	                                    String url=response.getJsonBody().optString("fileUrl");
	                                    AppUpdateUtil update=new AppUpdateUtil(MainTabActivity.this, url);
	                                    update.showUpdateNoticeDialog(note);
									}
								}else{
									//T.ss("更新失败！！！");
									//T.ss(response.getMsg());
								}
							} catch (JSONException e) {
								e.printStackTrace();
							}
						}

						@Override
						public void onFailure(int statusCode, Header[] headers,
								byte[] responseBody, Throwable error) {
	                        T.ss("网络错误:"+error.getMessage());
						}
					});
	}
	}
