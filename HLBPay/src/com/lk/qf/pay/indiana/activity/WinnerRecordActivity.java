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
import android.widget.LinearLayout;
import android.widget.ListView;

import com.alibaba.fastjson.JSON;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener2;
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
import com.lk.qf.pay.indiana.adapter.MyAddressAdapter;
import com.lk.qf.pay.indiana.adapter.WinnerRecordAdapter;
import com.lk.qf.pay.indiana.bean.IndianaGoodsInfo;
import com.lk.qf.pay.sharedpref.SharedPrefConstant;
import com.lk.qf.pay.tool.T;
import com.lk.qf.pay.wedget.CommonTitleBarYellow;
import com.lk.qf.pay.wedget.MyClickListener;

public class WinnerRecordActivity extends IndianaBaseActivity implements
		OnRefreshListener2<ListView> ,OnClickListener{

	private CommonTitleBarYellow title;
	private PullToRefreshListView lv;
	private List<IndianaGoodsInfo> list;
	private List<IndianaGoodsInfo> listReturn;
	private int page = 1;
	private int pageSize = 10;
	private Map<String, String> map;
	private IndianaGoodsInfo orderInfo;// 登录后返回的用户信息
	private WinnerRecordAdapter adapter;
	private String code = "", message = "";
	private LinearLayout ll;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.winner_record_list_layout);
		init();

	}

	private void init() {
		title = (CommonTitleBarYellow) findViewById(R.id.titlebar_winner_record_list);
		title.setActName("中奖记录");
		title.setCanClickDestory(this, true);
		ll = (LinearLayout) findViewById(R.id.ll_indiana_winnerList_default);
		lv = (PullToRefreshListView) findViewById(R.id.ll_indiana_winner_record_listview);
//		findViewById(R.id.tv_winnerList_goShop).setOnClickListener(this);
		list = new ArrayList<IndianaGoodsInfo>();
		adapter = new WinnerRecordAdapter(this, list);
		lv.setAdapter(adapter);
		lv.setOnItemClickListener(itemClickListener);
		lv.setOnRefreshListener(this);
	}

	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		Log.i("result", "-------onResume---");
		getWinnerRecordList();
	}

	@Override
	public void onClick(View arg0) {
		// TODO Auto-generated method stub
		Intent intent = new Intent(WinnerRecordActivity.this, IndianaMainActivity.class);
		startActivity(intent);
		finish();
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
				String isxd = info.getIsxd();
				if (isxd.equals("0")) {
					Intent intent = new Intent(WinnerRecordActivity.this,
							WinnerGoodsActivity.class);
					intent.putExtra("info", info);
					startActivity(intent);
				} else {
					Intent intent = new Intent(WinnerRecordActivity.this,
							WinnerInfoActivity.class);
					intent.putExtra("infoGoods", info);
					intent.setAction("queryInfo");
					startActivity(intent);
				}
			}
		}
	};

	/**
	 * 获取中奖列表
	 * 
	 */
	private void getWinnerRecordList() {
		showLoadingDialog();
		RequestParams params = new RequestParams();
		String url = MyUrls.ROOT_URL_WINNINGLIST;
		map = new HashMap<String, String>();
		map.put("username", MApplication.mSharedPref
				.getSharePrefString(SharedPrefConstant.USERNAME));
		map.put("pageSize", ""+pageSize);
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
						if (page == 1) {
							ll.setVisibility(View.VISIBLE);
							lv.setVisibility(View.GONE);
						}
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
			if (count < pageSize) {
				lv.setMode(Mode.PULL_DOWN_TO_REFRESH);
			}
			if (count > 0) {
				for (int i = 0; i < count; i++) {
					String goodsName = obj.optJSONArray("date")
							.optJSONObject(i).optString("indiana_name");// 商品名
					String userName = obj.optJSONArray("date")
							.optJSONObject(i).optString("winner_name");
					String goodsId = obj.optJSONArray("date").optJSONObject(i)
							.optString("id");// id
					int goodsTotalNum = obj.optJSONArray("date")
							.optJSONObject(i).optInt("count");// 总数
					int goodsBuyNum = obj.optJSONArray("date").optJSONObject(i)
							.optInt("current_count");// 已购买
					String imgUrl = obj.optJSONArray("date").optJSONObject(i)
							.optString("imgurl1");// 图片
					String dbnum = obj.optJSONArray("date").optJSONObject(i)
							.optString("dbnum");// 夺宝号
					String systrNo = obj.optJSONArray("date").optJSONObject(i)
							.optString("indiana_systr");// 夺宝号
					String lotterytime = obj.optJSONArray("date")
							.optJSONObject(i).optString("lotterytime");// 时间
					String isxd = obj.optJSONArray("date").optJSONObject(i)
							.optString("isxd");// 是否下单 0没下单 1下单 2已确认收货
					int shengyu = goodsTotalNum - goodsBuyNum;
					// 给info设置数据
					IndianaGoodsInfo info = new IndianaGoodsInfo();
					info.setGoodsName(goodsName);
					info.setUserName(userName);
					info.setGoodsId(goodsId);
					info.setGoodsTotal(goodsTotalNum);
					info.setRemainingNum(shengyu);
					info.setBoughtNum(goodsBuyNum);
					info.setImgUrl(imgUrl);
					info.setWinningNumber(dbnum);
					info.setOpenTime(lotterytime);
					info.setIsxd(isxd);
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
		page=1;
		getWinnerRecordList();
	}

	@Override
	public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
		// TODO Auto-generated method stub
		page++;
		getWinnerRecordList();
	}

	
}
