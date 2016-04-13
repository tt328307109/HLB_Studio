package com.lk.qf.pay.indiana.activity;

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
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
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
import com.lk.qf.pay.golbal.MApplication;
import com.lk.qf.pay.golbal.MyUrls;
import com.lk.qf.pay.indiana.adapter.IndianaLatesAnnouncedAdapter;
import com.lk.qf.pay.indiana.adapter.MyAddressAdapter;
import com.lk.qf.pay.indiana.bean.IndianaGoodsInfo;
import com.lk.qf.pay.sharedpref.SharedPrefConstant;
import com.lk.qf.pay.tool.T;
import com.lk.qf.pay.wedget.CommonTitleBarYellow;
import com.lk.qf.pay.wedget.MyClickListener;

public class MyShippingAddressActivity extends IndianaBaseActivity implements
		OnClickListener, OnRefreshListener2<ListView> {

	private CommonTitleBarYellow title;
	private PullToRefreshListView lv;
	private List<IndianaGoodsInfo> list;
	private List<IndianaGoodsInfo> listReturn;
	private int page = 1;
	private int pageSize = 15;
	private Map<String, String> map;
	private IndianaGoodsInfo orderInfo;// 登录后返回的用户信息
	private MyAddressAdapter adapter;
	private String code = "", message = "",action;
	private IndianaGoodsInfo infoGoods;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.indiana_my_address_layout);
		init();
	}

	private void init() {
		Intent intent = getIntent();
		if (intent!=null) {
			action = intent.getAction();
			
			if (action!=null && action.equals("win")) {
				infoGoods = intent.getParcelableExtra("info");
			}
		}
		title = (CommonTitleBarYellow) findViewById(R.id.titlebar_my_address);
		title.setActName("我的地址");
		title.setCanClickDestory(this, true);
		findViewById(R.id.ll_add_address).setOnClickListener(this);
		lv = (PullToRefreshListView) findViewById(R.id.ll_indiana_address_listview);
		list = new ArrayList<IndianaGoodsInfo>();
		adapter = new MyAddressAdapter(this, list,mListener);
		lv.setAdapter(adapter);
		lv.setOnItemClickListener(itemClickListener);
	}

	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		getAddress();
	}

	@Override
	public void onClick(View arg0) {
		// TODO Auto-generated method stub
		switch (arg0.getId()) {
		case R.id.ll_add_address:
			Intent intent = new Intent(MyShippingAddressActivity.this, EditAddressActivity.class);
			startActivity(intent);
			
			break;

		default:
			break;
		}
	}
	OnItemClickListener itemClickListener = new OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
				long arg3) {
			// TODO Auto-generated method stub
			IndianaGoodsInfo info = (IndianaGoodsInfo) arg0
					.getItemAtPosition(arg2);
			Log.i("result", "-----------info-------"+(info==null?0:1));
			if (info != null) {
				
				if (action!=null && action.equals("win")) {
					Intent intent = new Intent(MyShippingAddressActivity.this,
							WinnerInfoActivity.class);
					intent.putExtra("info", info);
					intent.putExtra("infoGoods", infoGoods);
					intent.setAction("win");
					startActivity(intent);
					finish();
				}
			}
		}
	};

	/**
	 * listview中button的事件
	 */
	private MyClickListener mListener = new MyClickListener() {
		@Override
		public void myOnClick(int position, View v) {
			IndianaGoodsInfo info = list.get(position);
//			Intent intent = new Intent(MyShippingAddressActivity.this, EditAddressActivity.class);
//			intent.putExtra("addressInfo", info);
//			startActivity(intent);
			addMyAddress(info.getUserAddressId());
		}
	};
	
	/**
	 * 删除地址
	 */
	private void addMyAddress(String addressId) {
		showLoadingDialog();
		RequestParams params = new RequestParams();
		String url = MyUrls.ROOT_URL_ADRESSDELE;
		Map<String, String> map = new HashMap<String, String>();
		map.put("username", MApplication.mSharedPref
				.getSharePrefString(SharedPrefConstant.USERNAME));
		map.put("address", addressId);
		map.put("token", MApplication.mSharedPref
				.getSharePrefString(SharedPrefConstant.TOKEN));
		String json = JSON.toJSONString(map);
		Log.i("result", "----ddd----s-------" + json);
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
				String code = "";
				String message = "";
				String str = response.result;
				Log.i("result", "----保存成功----s-------" + str);
				try {
					JSONObject obj = new JSONObject(str);
					code = obj.optString("CODE");
					message = obj.optString("MESSAGE");
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				T.ss(message);
				if (code.equals("00")) {
					finish();
				} 
				dismissLoadingDialog();
			}
		});
	}


	/**
	 * 获取收货地址列表
	 */
	private void getAddress() {
		showLoadingDialog();
		RequestParams params = new RequestParams();
		String url = MyUrls.ROOT_URL_ADRESSLIST;
		map = new HashMap<String, String>();
		map.put("username", MApplication.mSharedPref
				.getSharePrefString(SharedPrefConstant.USERNAME));
		map.put("pageSize", "15");
		map.put("page", "" + page);
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
				Log.i("result", "---------------11---");

				if (code.equals("00")) {
					if (listReturn == null || listReturn.size() == 0) {
						// T.ss(getResources().getString(
						// R.string.query_nothing_more));
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
					T.ss(message);

				}
				lv.onRefreshComplete();
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
			Log.i("result", "---------22-------");
			listReturn = new ArrayList<IndianaGoodsInfo>();
			int count = obj.optInt("count");
			Log.i("result", "---------Count-------" + count);
			if (count > 0) {
				for (int i = 0; i < count; i++) {
					String userName = obj.optJSONArray("date")
							.optJSONObject(i).optString("realname");//收货人
					String province = obj.optJSONArray("date").optJSONObject(i)
							.optString("pro");
					String city = obj.optJSONArray("date")
							.optJSONObject(i).optString("city");
					String clazz = obj.optJSONArray("date").optJSONObject(i)
							.optString("clazz");//详细地址
					String phone = obj.optJSONArray("date").optJSONObject(i)
							.optString("phone");
					String isdef = obj.optJSONArray("date").optJSONObject(i)
							.optString("isdef");//是否为默认地址(0:false,1:true)
					String addressId = obj.optJSONArray("date").optJSONObject(i)
							.optString("id");
					// 给info设置数据
					IndianaGoodsInfo info = new IndianaGoodsInfo();
					info.setUserPhoneNum(phone);
					info.setUserName(userName);
					info.setProvince(province);
					info.setCity(city);
					info.setUserAddress(clazz);
					info.setIsdefAddress(isdef);
					info.setUserAddressId(addressId);
					listReturn.add(info);
				}
			}

		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	@Override
	public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
		// TODO Auto-generated method stub

	}
}
