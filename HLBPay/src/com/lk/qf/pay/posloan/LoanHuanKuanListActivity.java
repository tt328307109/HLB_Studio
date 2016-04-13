package com.lk.qf.pay.posloan;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.http.entity.StringEntity;
import org.json.JSONException;
import org.json.JSONObject;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.widget.ListView;
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
import com.lk.qf.pay.adapter.LoanHKAdapter;
import com.lk.qf.pay.beans.IncomeInfo;
import com.lk.qf.pay.beans.LoanHuanInfo;
import com.lk.qf.pay.golbal.MApplication;
import com.lk.qf.pay.golbal.MyUrls;
import com.lk.qf.pay.sharedpref.SharedPrefConstant;
import com.lk.qf.pay.tool.T;
import com.lk.qf.pay.wedget.CommonTitleBar;

public class LoanHuanKuanListActivity extends BaseActivity implements
		OnRefreshListener2<ListView> {

	private PullToRefreshListView lv;
	private List<LoanHuanInfo> list;
	private List<LoanHuanInfo> listReturn;
	private int page = 1;
	private int dataCount = 10;
	private Map<String, String> map;
	private IncomeInfo orderInfo;// 登录后返回的用户信息
	private LoanHKAdapter adapter;
	private CommonTitleBar title;
	private String action = "";
	private String strTitle = "还款记录";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.loan_huankuan_list_layout);
		init();
	}

	private void init() {
		title = (CommonTitleBar) findViewById(R.id.titlebar_income_list_loan_huank);
		title.setActName(strTitle);
		title.setCanClickDestory(this, true);

		lv = (PullToRefreshListView) findViewById(R.id.myPull_refresh_list_loan_huankuan);
		lv.setOnRefreshListener(this);
		list = new ArrayList<LoanHuanInfo>();

		adapter = new LoanHKAdapter(LoanHuanKuanListActivity.this, list);
		lv.setAdapter(adapter);
		postQueryHkList();

	}

	/**
	 * 还款记录
	 * 
	 * @return
	 */
	private void postQueryHkList() {
		showLoadingDialog();
		RequestParams params = new RequestParams();
		String url = "";
		url = MyUrls.BL_HUANLIST;

		map = new HashMap<String, String>();

		map.put("username", MApplication.mSharedPref
				.getSharePrefString(SharedPrefConstant.USERNAME));
		map.put("pageSize", "15");
		map.put("page", ""+page);
		String json = JSON.toJSONString(map);
		Log.i("result", "-------记录请求-----" + json);
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
				Log.i("result", "---------------还款记录-returnjson---"
						+ strReturnLogin);
				jsonDetail(strReturnLogin);

				String returnCode = orderInfo.getCode();

				if (returnCode.equals("00")) {
					if (listReturn == null || listReturn.size() == 0) {
						T.ss(getResources().getString(
								R.string.query_nothing_more));
						list.clear();
						adapter.notifyDataSetChanged();
						 finish();
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
								LoanHuanKuanListActivity.this,
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
			orderInfo = new IncomeInfo();
			orderInfo.setCode(obj.optString("CODE"));
			orderInfo.setMessage(obj.optString("MESSAGE"));
			listReturn = new ArrayList<LoanHuanInfo>();
			int count = obj.optInt("count");
			Log.i("result", "---------Count-------" + count);
			if (count > 0) {
				for (int i = 0; i < count; i++) {
				
//					String reimMoney = obj.optJSONArray("date").optJSONObject(i)
//							.optString("reimMoney");//需还款金额
					String actualMoney = obj.optJSONArray("date").optJSONObject(i)
							.optString("actualMoney");//实际还款金额
					String addtime = obj.optJSONArray("date").optJSONObject(i)
							.optString("addtime");
//					String state = obj.optJSONArray("date").optJSONObject(i)
//							.optString("state");//状态 off/not
					String loan_str = obj.optJSONArray("date").optJSONObject(i)
							.optString("loan_str");//贷款订单号
//					String lastdata = obj.optJSONArray("date").optJSONObject(i)
//							.optString("lastdata");//还款时间
//					String reimuser = obj.optJSONArray("date").optJSONObject(i)
//							.optString("reimuser");//需还总金额
					String payType = obj.optJSONArray("date").optJSONObject(i)
							.optString("paytype");//还款类型

					// 给info设置数据
					LoanHuanInfo info = new LoanHuanInfo();
					info.setHkTime(addtime);
					info.setDingdanNum(loan_str);
					info.setHkType(payType);
					info.setReimMoney(actualMoney);
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

	@Override
	public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
		// TODO Auto-generated method stub
		page = 1;
		postQueryHkList();
	}

	@Override
	public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
		// TODO Auto-generated method stub
		page++;
		postQueryHkList();
	}

}
