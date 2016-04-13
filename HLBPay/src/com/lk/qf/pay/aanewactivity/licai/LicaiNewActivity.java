package com.lk.qf.pay.aanewactivity.licai;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.http.entity.StringEntity;
import org.json.JSONException;
import org.json.JSONObject;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ScrollView;
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
import com.lk.qf.pay.adapter.LicaiGoodsNewAdapter;
import com.lk.qf.pay.beans.LicaiNewGoodsInfo;
import com.lk.qf.pay.golbal.MApplication;
import com.lk.qf.pay.golbal.MyUrls;
import com.lk.qf.pay.sharedpref.SharedPrefConstant;
import com.lk.qf.pay.tool.T;
import com.lk.qf.pay.wedget.MyClickListener;
import com.lk.qf.pay.wedget.view.ListViewForScrollView;

public class LicaiNewActivity extends LicaiBaseActivity implements OnClickListener {

	private ListViewForScrollView lv;
	private String phone, typeId,code="",message="";
	private List<LicaiNewGoodsInfo> list;
	private List<LicaiNewGoodsInfo> listReturn;
	private int page = 1;
	private int dataCount = 15;
	private Map<String, String> map;
//	private LicaiNewGoodsInfo info;
	private LicaiGoodsNewAdapter adapter;
	private Context context;
	private TextView tvEarnings, tvTotal,tvpolyLoanAccount,tvTotalEarningsAccount;
	private ScrollView sl;

	// private String earnings, totalAccount;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.licai_new_layout);
		init();
	}

	private void init() {
		context = LicaiNewActivity.this;
		tvEarnings = (TextView) findViewById(R.id.tv_licai_new_account_earnings);
		tvTotal = (TextView) findViewById(R.id.tv_licai_new_total_account);
		tvpolyLoanAccount = (TextView) findViewById(R.id.tv_licai_new_polyloan_account);
		tvTotalEarningsAccount = (TextView) findViewById(R.id.tv_licai_new_account_total_earningsAccount);

		findViewById(R.id.common_title_back_licai).setOnClickListener(this);
		findViewById(R.id.rl_licai_new_list_jilu).setOnClickListener(this);
		lv = (ListViewForScrollView) findViewById(R.id.ll_list_order_liai_new);
		sl = (ScrollView) findViewById(R.id.sl_licai_new);
		list = new ArrayList<LicaiNewGoodsInfo>();

		// adapter = new LicaiGoodsNewAdapter(context, list, mListener);
		adapter = new LicaiGoodsNewAdapter(context, list);
		lv.setAdapter(adapter);
		lv.setOnItemClickListener(itemClickListener);
		sl.smoothScrollTo(0, 0);
		// postQueryLicaiHttp();
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		postQueryLicaiHttp();
	}

	@Override
	public void onClick(View arg0) {
		// TODO Auto-generated method stub
		switch (arg0.getId()) {
		case R.id.common_title_back_licai:
			finish();
			break;
		case R.id.rl_licai_new_list_jilu:
			Intent intent = new Intent(LicaiNewActivity.this,
					LicaiNewRecordListActivity.class);
			startActivity(intent);
			break;
		default:
			break;
		}
	}

	/**
	 * listview中button的事件
	 */
	private MyClickListener mListener = new MyClickListener() {
		@Override
		public void myOnClick(int position, View v) {
			Intent intent = new Intent(context, AddLicaiGoodsActivity.class);
			intent.putExtra("info", list.get(position));
			startActivity(intent);
		}
	};
	OnItemClickListener itemClickListener = new OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> arg0, View arg1, int position,
				long arg3) {
			// TODO Auto-generated method stub
			Intent intent = new Intent(context, AddLicaiGoodsActivity.class);
			intent.putExtra("info", list.get(position));
			startActivity(intent);
		}
	};

	/**
	 * 查询理财产品列表
	 * 
	 * @return
	 */
	private void postQueryLicaiHttp() {
		showLoadingDialog();
		RequestParams params = new RequestParams();
		String url = MyUrls.LICAIPROLIST;

		map = new HashMap<String, String>();

		map.put("username", MApplication.mSharedPref
				.getSharePrefString(SharedPrefConstant.USERNAME));
		map.put("pageSize", "15");
		map.put("page", "" + page);
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
				Log.i("result", "-------查询失败-----");
				T.ss("操作超时");
				dismissLoadingDialog();
			}

			@Override
			public void onSuccess(ResponseInfo<String> response) {
				// TODO Auto-generated method stub
				String strReturnLogin = response.result;
				Log.i("result", "---------------定单-returnjson---"
						+ strReturnLogin);
				jsonDetail(strReturnLogin);
//				String returnCode = info.getCode();

				if (code.equals("00")) {
					if (listReturn == null || listReturn.size() == 0) {
						T.ss(getResources().getString(
								R.string.query_nothing_more));
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
					setListViewHeightBasedOnChildren(lv);
				} else {
					T.ss(message);

					if (message.equals(
							getResources().getString(R.string.login_outtime))) {
						Intent intent = new Intent(context, LoginActivity.class);
						startActivity(intent);
						finish();
					}
				}
				// lv.onRefreshComplete();// 告诉它 我们已经在后台数据请求完毕
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
			code = obj.optString("CODE");
			message = obj.optString("MESSAGE");
			listReturn = new ArrayList<LicaiNewGoodsInfo>();
			int count = 0;
			if (code.equals("00")) {
				String shouyi = obj.optString("SHOUYI");
				double sy = Double.parseDouble(shouyi);
				if (sy>0) {
					tvEarnings.setText(shouyi);
				}
				tvTotal.setText(obj.optString("TOTALMONEY"));
				tvpolyLoanAccount.setText(obj.optString("LOANMONEY"));
				tvTotalEarningsAccount.setText(obj.optString("TOTALSHOUYI"));
				count = obj.optInt("COUNT");
			}
			Log.i("result", "---------Count-------" + count);
			if (count > 0) {

				// if ((page * 10) >= count) {
				// dataCount = count - ((page - 1) * 10);
				// } else if (count > (page * 10)) {
				// dataCount = (page * 10);
				// }

				Log.i("result", "---------page-------" + page);
				Log.i("result", "---------dataCount-------" + dataCount);
				for (int i = 0; i < count; i++) {

					String nameTitle = obj.optJSONArray("DATA")
							.optJSONObject(i).optString("pro_name");
					String timeLimit = obj.optJSONArray("DATA")
							.optJSONObject(i).optString("licaidays");
					String yearEarnings = obj.optJSONArray("DATA")
							.optJSONObject(i).optString("yearrate");
					String qgAccount = obj.optJSONArray("DATA")
							.optJSONObject(i).optString("minmoney");
					String proid = obj.optJSONArray("DATA").optJSONObject(i)
							.optString("id");
					String buyAccount = obj.optJSONArray("DATA")
							.optJSONObject(i).optString("usertotal");

					// 给info设置数据
					LicaiNewGoodsInfo info = new LicaiNewGoodsInfo();
					info.setNameTitle(nameTitle);
					info.setTimeLimit(timeLimit);
					info.setYearEarnings(yearEarnings);
					info.setProid(proid);
					info.setQgAccount(qgAccount);
					info.setBuyAccount(buyAccount);
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

	// @Override
	// public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView)
	// {
	// // TODO Auto-generated method stub
	// page = 1;
	// postQueryLicaiHttp();
	// }
	//
	// @Override
	// public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
	// // TODO Auto-generated method stub
	// page++;
	// postQueryLicaiHttp();
	// }

	/**
	 * 动态设置ListView的高度
	 * 
	 * @param listView
	 */
	private static void setListViewHeightBasedOnChildren(ListView listView) {
		if (listView == null) {
			return;
		}
		ListAdapter listAdapter = listView.getAdapter();
		if (listAdapter == null) {
			return;
		}

		int totalHeight = 0;
		for (int i = 0; i < listAdapter.getCount(); i++) {
			View listItem = listAdapter.getView(i, null, listView);
			listItem.measure(0, 0);
			totalHeight += listItem.getMeasuredHeight();
		}

		ViewGroup.LayoutParams params = listView.getLayoutParams();
		params.height = totalHeight
				+ (listView.getDividerHeight() * (listAdapter.getCount() - 1));
		Log.i("result", "-----------sghi--------" + params.height);
		listView.setLayoutParams(params);
	}
}
