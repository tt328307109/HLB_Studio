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
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
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
import com.lk.qf.pay.adapter.PosloanListAdapter;
import com.lk.qf.pay.beans.PosloanListInfo;
import com.lk.qf.pay.golbal.MApplication;
import com.lk.qf.pay.golbal.MyUrls;
import com.lk.qf.pay.sharedpref.SharedPrefConstant;
import com.lk.qf.pay.tool.T;
import com.lk.qf.pay.wedget.CommonTitleBar;

public class PosloanListActivity extends BaseActivity implements
		OnClickListener, OnRefreshListener2<ListView> {

	private ImageButton back;// 返回
	// ListView lv;
	private PullToRefreshListView lv;
	private String phone;
	private List<PosloanListInfo> list;
	private List<PosloanListInfo> listReturn;
	private int page = 1;
	private int dataCount = 10;
	private Map<String, String> map;
	private PosloanListInfo loaninfo;// 登录后返回的用户信息
	private TextView tvStartTime, tvEndTime;
	private PosloanListAdapter adapter;
	private String accsort, typeId = "101";
	private CommonTitleBar title;
	private TextView tvMX, tvShowNothing;
	private View viewLine;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.posloan_list_layout);
		init();
	}

	private void init() {
		title = (CommonTitleBar) findViewById(R.id.titlebar_posLoan_list_title1);
		title.setActName("我的贷款");
		title.setCanClickDestory(this, true);
		tvMX = (TextView) findViewById(R.id.tv_posloan_list_mx);
		tvShowNothing = (TextView) findViewById(R.id.tv_posloan_list_mxNum);
		viewLine = (View) findViewById(R.id.v_horizontal_line);
		findViewById(R.id.btn_myPosloan_jindu).setOnClickListener(this);
		lv = (PullToRefreshListView) findViewById(R.id.myPull_refresh_list_posloan);
		lv.setOnRefreshListener(this);
		list = new ArrayList<PosloanListInfo>();

		adapter = new PosloanListAdapter(PosloanListActivity.this, list);
		lv.setAdapter(adapter);
		postQueryOrderHttp();
	}

	@Override
	public void onClick(View arg0) {
		// TODO Auto-generated method stub
		Intent intent = new Intent(PosloanListActivity.this,
				PosloanQueryProgressActivity.class);
		startActivity(intent);

	}

	/**
	 * 查询请求
	 * 
	 * @return
	 */
	private void postQueryOrderHttp() {
		showLoadingDialog();
		RequestParams params = new RequestParams();
		String url = MyUrls.LOANLIST;

		map = new HashMap<String, String>();

		map.put("username", MApplication.mSharedPref
				.getSharePrefString(SharedPrefConstant.USERNAME));
		map.put("pageSize", "10");
		map.put("page", "" + page);
		// map.put("token", MApplication.mSharedPref
		// .getSharePrefString(SharedPrefConstant.TOKEN));
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

				String returnCode = loaninfo.getCode();

				if (returnCode.equals("00")) {
					if (listReturn == null || listReturn.size() == 0) {

						if (page == 1) {
							tvShowNothing.setVisibility(View.VISIBLE);
							viewLine.setVisibility(View.GONE);
							Log.i("result", "============1==========");
						} else {
							T.ss(getResources().getString(
									R.string.query_nothing_more));
						}
					} else {
						tvShowNothing.setVisibility(View.GONE);
						viewLine.setVisibility(View.VISIBLE);
						tvMX.setVisibility(View.VISIBLE);
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
					T.ss(loaninfo.getMessage());
					if (loaninfo.getMessage().equals(
							getResources().getString(R.string.login_outtime))) {
						Intent intent = new Intent(PosloanListActivity.this,
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
			loaninfo = new PosloanListInfo();
			loaninfo.setCode(obj.optString("CODE"));
			loaninfo.setMessage(obj.optString("MESSAGE"));
			listReturn = new ArrayList<PosloanListInfo>();
			int count = obj.optInt("count");
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

					String totalAccount = obj.optJSONArray("date")
							.optJSONObject(i).optString("loanMoney");
					String xuhuanAccount = obj.optJSONArray("date")
							.optJSONObject(i).optString("reimuser");
					String yihuanAccount = obj.optJSONArray("date")
							.optJSONObject(i).optString("reimMoney");
					String loanTime = obj.optJSONArray("date").optJSONObject(i)
							.optString("issuedtime");
					String huankuanDate = obj.optJSONArray("date")
							.optJSONObject(i).optString("reimdata");
					String fenqi = obj.optJSONArray("date").optJSONObject(i)
							.optString("limit");
					String mmhuanAccount = obj.optJSONArray("date")
							.optJSONObject(i).optString("mm_reimMoney");
					String state = obj.optJSONArray("date").optJSONObject(i)
							.optString("state");

					PosloanListInfo info = new PosloanListInfo();
					info.setTotalAccount(totalAccount);
					info.setXuhuanAccount(xuhuanAccount);
					// info.setCardNum(crdno);//
					info.setYihuanAccount(yihuanAccount);
					info.setLoanTime(loanTime);
					// info.setMerchantsName(merchantsName);//
					info.setHuankuanDate(huankuanDate);
					info.setHuankuanDate(huankuanDate);
					info.setMmhuanAccount(mmhuanAccount);
					info.setFenqi(fenqi);
					info.setState(state);
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
		postQueryOrderHttp();
	}

	@Override
	public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
		// TODO Auto-generated method stub
		// pageSize+=5;
		page++;
		Log.i("result", "--------ss------" + page);
		postQueryOrderHttp();
		Log.i("result", "--------sss------" + page);
	}

}
