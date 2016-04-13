package com.lk.qf.pay.indiana.activity;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.entity.StringEntity;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager.LayoutParams;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.RadioGroup;
import android.widget.TextView;

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
import com.lk.qf.pay.golbal.MApplication;
import com.lk.qf.pay.golbal.MyUrls;
import com.lk.qf.pay.indiana.adapter.IndianaNumAdapter;
import com.lk.qf.pay.indiana.adapter.IndianaRecordListAdapter;
import com.lk.qf.pay.indiana.bean.IndianaGoodsInfo;
import com.lk.qf.pay.sharedpref.SharedPrefConstant;
import com.lk.qf.pay.tool.T;
import com.lk.qf.pay.utils.ImgOptions;
import com.lk.qf.pay.wedget.CommonTitleBarYellow;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

public class ShowIndianaNumActivity extends Activity implements
		OnClickListener, OnRefreshListener2<ListView> {

	private PullToRefreshListView lv;
	private List<IndianaGoodsInfo> list;
	private List<IndianaGoodsInfo> listReturn;
	// private List<String> listReturn;
	private int page = 1;
	private int pageSize = 15;
	private Map<String, String> map;
	private IndianaGoodsInfo orderInfo;// 登录后返回的用户信息
	private String code = "", message = "", orderId = "", goodsId = "",userCanyu="0";
	private IndianaNumAdapter adapter;
	private TextView tvShowNum;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.show_indiananum_layout);

		init();
	}

	private void init() {
		tvShowNum = (TextView) findViewById(R.id.tv_canyu_num);
		findViewById(R.id.tv_indianaNum_quren).setOnClickListener(this);
		lv = (PullToRefreshListView) findViewById(R.id.ll_indiana_num_listview);
		list = new ArrayList<IndianaGoodsInfo>();
		Intent intent = getIntent();
		if (intent != null) {
			orderId = intent.getStringExtra("orderNum");
			goodsId = intent.getStringExtra("goodsId");
			userCanyu = intent.getStringExtra("totalNum");
		}
		getIndianaNum();
		tvShowNum.setText(userCanyu);
		adapter = new IndianaNumAdapter(this, list);
		lv.setAdapter(adapter);
		lv.setOnRefreshListener(this);
	}

	@Override
	public void onClick(View arg0) {
		// TODO Auto-generated method stub
		finish();
	}

	/**
	 * 夺宝号
	 */
	private void getIndianaNum() {
		RequestParams params = new RequestParams();
		String url = MyUrls.ROOT_URL_INDIANA_GET_DBNUMBER;
		map = new HashMap<String, String>();
		map.put("username", MApplication.mSharedPref
				.getSharePrefString(SharedPrefConstant.USERNAME));
		map.put("pagesize", ""+pageSize);
		map.put("page", "" + page);
		map.put("orderId", "" + orderId);
		map.put("goodsId", "" + goodsId);
		map.put("token", MApplication.mSharedPref
				.getSharePrefString(SharedPrefConstant.TOKEN));
		String json = JSON.toJSONString(map);
		Log.i("result", "-------夺宝号-----" + json);
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
				lv.onRefreshComplete();
			}

			@Override
			public void onSuccess(ResponseInfo<String> response) {
				// TODO Auto-generated method stub
				String strReturnLogin = response.result;
				Log.i("result", "---------------定单-returnjson---"
						+ strReturnLogin);
				jsonDetail(strReturnLogin);

				if (code.equals("00")) {
					if (listReturn == null || listReturn.size() == 0) {

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
			listReturn = new ArrayList<IndianaGoodsInfo>();
			int count = obj.optInt("Total");
			if (count<15) {
				lv.setMode(Mode.PULL_DOWN_TO_REFRESH);
			}
			if (count > 0) {
				for (int i = 0; i < count; i++) {
					String indianaNum = obj.optJSONArray("date")
							.optJSONObject(i).optString("dbNum");
					orderInfo = new IndianaGoodsInfo();
					orderInfo.setPeriodsNum(indianaNum);
					listReturn.add(orderInfo);
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
		getIndianaNum();
	}

	@Override
	public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
		// TODO Auto-generated method stub
		page++;
		getIndianaNum();
	}
}
