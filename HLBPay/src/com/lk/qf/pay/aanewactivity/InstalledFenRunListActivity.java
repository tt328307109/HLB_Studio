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
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.DatePicker.OnDateChangedListener;

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
import com.lk.qf.pay.adapter.JiaoYiAdapter;
import com.lk.qf.pay.adapter.InstalledFenrunListAdapter;
import com.lk.qf.pay.beans.TradingRunInfo;
import com.lk.qf.pay.beans.OrderInfo;
import com.lk.qf.pay.golbal.MApplication;
import com.lk.qf.pay.golbal.MyUrls;
import com.lk.qf.pay.sharedpref.SharedPrefConstant;
import com.lk.qf.pay.tool.T;
import com.lk.qf.pay.wedget.CommonTitleBar;

public class InstalledFenRunListActivity extends BaseActivity implements
		OnClickListener, OnRefreshListener2<ListView> {

	private ImageButton back;// 返回
	// ListView lv;
	private PullToRefreshListView lv;
	private String startTime = "";
	private String endTime;
	private Button btnQuery;
	private String phone;
	private List<TradingRunInfo> list;
	private List<TradingRunInfo> listReturn;
	private int page = 1;
	private int dataCount = 10;
	private Map<String, String> map;
	private TradingRunInfo orderInfo;// 登录后返回的用户信息
	private TextView tvStartTime;
	private InstalledFenrunListAdapter adapter;
	private String accsort, typeId = "101";
	private static int APPID = 2;// 0机构 1代理 2商户
	private Date date;
	private SimpleDateFormat sdf;
	private Calendar cal;
	private boolean timeIsOk = true;
	private static int TYPE = 0;// 起始时间
	private CommonTitleBar title;
	private String action = "";
	private String strTitle = "";
	private TextView tvTitle1, tvTitle2,tvTime1;
	private EditText edTime;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.installed_fenrun_list_layout);
		init();
	}

	@Override
	public void onClick(View arg0) {
		// TODO Auto-generated method stub
		switch (arg0.getId()) {

		case R.id.btn_zj_list_query1:

			list.clear();
			startTime = tvStartTime.getText().toString();

			if (TextUtils.isEmpty(startTime)) {

				startTime = sdf.format(date);
			}
			if (startTime.length()!=6) {
				
				T.ss("时间格式输入有误");
				return;
			}

			postQueryOrderHttp();
			break;

		default:
			break;
		}
	}

	private void init() {
		date = new Date();
		sdf = new SimpleDateFormat("yyyyMM");

		tvTitle2 = (TextView) findViewById(R.id.tv_install_title3);
		tvTitle1 = (TextView) findViewById(R.id.tv_install_title2);
		tvTime1 = (TextView) findViewById(R.id.tv_install_title1);
		Intent intent = getIntent();
		if (intent != null) {
			action = intent.getAction();
			if (action.equals("zj")) {
				strTitle = "装机分润查询";
			} else {
				strTitle = "交易分润查询";
			}
		}

		tvStartTime = (EditText) findViewById(R.id.tv_zj_list_startTime1);
		tvStartTime.setText("" + sdf.format(date));
		startTime = sdf.format(date);
		title = (CommonTitleBar) findViewById(R.id.titlebar_installen_list_title);
		title.setActName(strTitle);
		title.setCanClickDestory(this, true);

		btnQuery = (Button) findViewById(R.id.btn_zj_list_query1);
		btnQuery.setOnClickListener(this);
		lv = (PullToRefreshListView) findViewById(R.id.myPull_refresh_order_zj_list);
		lv.setOnRefreshListener(this);
		list = new ArrayList<TradingRunInfo>();

		adapter = new InstalledFenrunListAdapter(InstalledFenRunListActivity.this,
				list);
		lv.setAdapter(adapter);
		postQueryOrderHttp();

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
		if (action.equals("zj")) {// 装机
			url = MyUrls.ZHUANGJIJIANG;
		} else {
			url = MyUrls.MERFENRUN;
		}

		map = new HashMap<String, String>();

		map.put("username", MApplication.mSharedPref
				.getSharePrefString(SharedPrefConstant.USERNAME));
		map.put("token", MApplication.mSharedPref
				.getSharePrefString(SharedPrefConstant.TOKEN));
		map.put("begintime", startTime);
		map.put("endtime", startTime);
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
						Intent intent = new Intent(
								InstalledFenRunListActivity.this,
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
			orderInfo = new TradingRunInfo();
			orderInfo.setCode(obj.optString("CODE"));
			orderInfo.setMessage(obj.optString("MESSAGE"));
			listReturn = new ArrayList<TradingRunInfo>();
			int count = obj.optInt("count");
			Log.i("result", "---------Count-------" + count);
			if (count > 0) {
				tvTitle1.setText("一级分润");
				tvTitle2.setText("二级分润");
				tvTime1.setText("时间");
				Log.i("result", "---------page-------" + page);
				Log.i("result", "---------dataCount-------" + dataCount);
				for (int i = 0; i < count; i++) {
					String zjdaytotal="";
					String managezidaytotal="";
					String date="";
					if (action.equals("zj")) {
						zjdaytotal = obj.optJSONArray("data")
								.optJSONObject(i).optString("zjdaytotal");
						managezidaytotal = obj.optJSONArray("data")
								.optJSONObject(i).optString("managezjdaytotal");
						date = obj.optJSONArray("data").optJSONObject(i)
								.optString("date");
					} else {
						zjdaytotal = obj.optJSONArray("data")
								.optJSONObject(i).optString("managedaytotal");
						managezidaytotal = obj.optJSONArray("data")
								.optJSONObject(i).optString("tradedaytotal");
						date = obj.optJSONArray("data").optJSONObject(i)
								.optString("date");
					}
					Log.i("result", "---------zjdaytotal-------" + zjdaytotal);
					Log.i("result", "---------managezidaytotal-------" + managezidaytotal);

					// 给info设置数据
					TradingRunInfo info = new TradingRunInfo();
					info.setTime(date);// 交易日期
					info.setJyFenRun(zjdaytotal);// 一级
					info.setZjFenRun(managezidaytotal);// 二级
					listReturn.add(info);
				}
			}else{
				tvTitle1.setText("");
				tvTitle2.setText("");
				tvTime1.setText("");
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

		page = 1;
		postQueryOrderHttp();
	}

	@Override
	public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
		// TODO Auto-generated method stub
		// pageSize+=5;
		page++;
		postQueryOrderHttp();
	}

}
