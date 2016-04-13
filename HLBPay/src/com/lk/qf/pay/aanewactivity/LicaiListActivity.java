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
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
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
import com.lk.qf.pay.adapter.DingDanListAdapter;
import com.lk.qf.pay.adapter.LiCaiListAdapter;
import com.lk.qf.pay.beans.LiCaiListItemInfo;
import com.lk.qf.pay.beans.OrderInfo;
import com.lk.qf.pay.golbal.MApplication;
import com.lk.qf.pay.golbal.MyUrls;
import com.lk.qf.pay.sharedpref.SharedPrefConstant;
import com.lk.qf.pay.tool.T;

public class LicaiListActivity extends BaseActivity implements OnClickListener,
		OnRefreshListener2<ListView> {

	private PullToRefreshListView lv;
	private List<LiCaiListItemInfo> list;
	private List<LiCaiListItemInfo> listReturn;
	private int page = 1;
	private int dataCount = 10;
	private Map<String, String> map;
	private LiCaiListItemInfo info;// 登录后返回的用户信息

	private LiCaiListAdapter adapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.licai_list_layout);
		init();
	}

	@Override
	public void onClick(View arg0) {
		// TODO Auto-generated method stub
		finish();
	}

	private void init() {
		lv = (PullToRefreshListView) findViewById(R.id.myPull_refresh_list_order_licai);
		lv.setOnRefreshListener(this);
		list = new ArrayList<LiCaiListItemInfo>();
		findViewById(R.id.ibtn_licai_back).setOnClickListener(this);

		adapter = new LiCaiListAdapter(LicaiListActivity.this, list);
		lv.setAdapter(adapter);
		postQueryLicaiHttp();
	}

	/**
	 * 理财查询请求
	 * 
	 * @return
	 */
	private void postQueryLicaiHttp() {
		showLoadingDialog();
		RequestParams params = new RequestParams();
		String url = MyUrls.LICAI_SELECT;

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

				String returnCode = info.getCode();

				if (returnCode.equals("00")) {
					if (listReturn == null || listReturn.size() == 0) {
						T.ss(getResources().getString(R.string.query_nothing_more));
//						finish();
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
					T.ss(info.getMessage());
					
					if (info.getMessage().equals(getResources().getString(R.string.login_outtime))) {
						Intent intent = new Intent(LicaiListActivity.this, LoginActivity.class);
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
			info = new LiCaiListItemInfo();
			info.setCode(obj.optString("CODE"));
			info.setMessage(obj.optString("MESSAGE"));
			listReturn = new ArrayList<LiCaiListItemInfo>();
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

					String addtime = obj.optJSONArray("date").optJSONObject(i)
							.optString("addtime");
					String totalmoney = obj.optJSONArray("date").optJSONObject(i)
							.optString("lixi");
					String costtype  = obj.optJSONArray("date").optJSONObject(i)
							.optString("costtype");
					
					// 给info设置数据
					LiCaiListItemInfo info = new LiCaiListItemInfo();
					info.setTime(addtime);// 交易日期
					Log.i("result", "---------addtime-------" + addtime);
					info.setAccount(totalmoney);// 交易金额
					info.setLiCaitype(costtype);//类型
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
		postQueryLicaiHttp();
	}

	@Override
	public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
		// TODO Auto-generated method stub
		// pageSize+=5;
		page++;
		Log.i("result", "--------ss------" + page);
		postQueryLicaiHttp();
		Log.i("result", "--------sss------" + page);
	}

}
