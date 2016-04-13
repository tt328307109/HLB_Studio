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
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
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
import com.lk.qf.pay.adapter.LicaiGoodsNewAdapter;
import com.lk.qf.pay.adapter.LicaiNewRecordAdapter;
import com.lk.qf.pay.beans.LicaiNewGoodsInfo;
import com.lk.qf.pay.beans.Xinyongkainfo;
import com.lk.qf.pay.golbal.MApplication;
import com.lk.qf.pay.golbal.MyUrls;
import com.lk.qf.pay.posloan.ApplyPolyLoanActivity;
import com.lk.qf.pay.sharedpref.SharedPrefConstant;
import com.lk.qf.pay.tool.T;
import com.lk.qf.pay.wedget.MyClickListener;

public class LicaiNewRecordListActivity extends LicaiBaseActivity implements
		OnClickListener, OnRefreshListener2<ListView> {

	private TextView tvGoodsNum, tvTotalEarningsAccount, tvMyLicai;
	private String goodsNum = "", totalEarningsAccoun = "";

	private PullToRefreshListView lv;
	private String phone, typeId;
	private List<LicaiNewGoodsInfo> list;
	private List<LicaiNewGoodsInfo> listReturn;
	private int page = 1;
	private int dataCount = 10;
	private int totalCount = 0;
	private Map<String, String> map;
	private LicaiNewGoodsInfo info;
	private LicaiNewRecordAdapter adapter;
	private Context context;
	private Button btnGobuy;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.licai_new_record_list_layout);
		init();
	}

	private void init() {
		context = LicaiNewRecordListActivity.this;
		tvMyLicai = (TextView) findViewById(R.id.tv_my_licai_goods);
		tvGoodsNum = (TextView) findViewById(R.id.tv_licai_total_num);
		tvTotalEarningsAccount = (TextView) findViewById(R.id.tv_licai_total_earnings_account);
		btnGobuy = (Button) findViewById(R.id.btn_licai_goBuy);

		findViewById(R.id.common_title_back_licairecord).setOnClickListener(
				this);
		btnGobuy.setOnClickListener(this);
		lv = (PullToRefreshListView) findViewById(R.id.myPull_refresh_licai_new_record);
		lv.setOnRefreshListener(this);
		list = new ArrayList<LicaiNewGoodsInfo>();

		adapter = new LicaiNewRecordAdapter(context, list, mListener);
		lv.setAdapter(adapter);
		lv.setOnItemClickListener(itemClickListener);
		// getUserAccount();
	

	}

	@Override
	public void onClick(View arg0) {
		// TODO Auto-generated method stub
		finish();
	}
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		postQueryLicaiHttp();
	}

	/**
	 * listview中button的事件
	 */
	private MyClickListener mListener = new MyClickListener() {
		@Override
		public void myOnClick(int position, View v) {
			LicaiNewGoodsInfo info = list.get(position);
			String stare = info.getLoanType();
			if (stare.equals("dai")) {
				T.ss("此款产品已申请过保理贷,不能重复申请!");
				return;
			}
			Intent intent = new Intent(context, ApplyPolyLoanActivity.class);
			intent.putExtra("info", info);
			startActivity(intent);
		}
	};

	OnItemClickListener itemClickListener = new OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
				long arg3) {
			// TODO Auto-generated method stub
			LicaiNewGoodsInfo info = (LicaiNewGoodsInfo) arg0
					.getItemAtPosition(arg2);

			Intent intent = new Intent(context, LiCaiGoodsInfoActivity.class);
			intent.putExtra("info", info);
			startActivity(intent);
		}
	};

	/**
	 * 查询理财记录列表 接口名称： 功能：理财记录列表 licaiprouser.ashx
	 * 
	 * 请求数据： username:发起转账人的登录名 token:token pagesize：每页显示的数量 page:页的索引 state: en
	 * - 正在理财的 stop - 已经结束的 all - 所有的记录（根据时间倒序排）
	 * 
	 * {"username":"18806331803","token":"123456","pagesize":"20","page":"1",
	 * "state":"en"}
	 * 
	 * @return
	 */
	private void postQueryLicaiHttp() {
		showLoadingDialog();
		RequestParams params = new RequestParams();
		String url = MyUrls.LICAIPROUSER;
		map = new HashMap<String, String>();
		map.put("username", MApplication.mSharedPref
				.getSharePrefString(SharedPrefConstant.USERNAME));
		map.put("pageSize", "10");
		map.put("page", "" + page);
		map.put("state", "en");
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
				} else {
					T.ss(info.getMessage());

					if (info.getMessage().equals(
							getResources().getString(R.string.login_outtime))) {
						Intent intent = new Intent(context, LoginActivity.class);
						startActivity(intent);
						finish();
					}
				}
				lv.onRefreshComplete();
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
			info = new LicaiNewGoodsInfo();
			info.setCode(obj.optString("CODE"));
			info.setMessage(obj.optString("MESSAGE"));
			listReturn = new ArrayList<LicaiNewGoodsInfo>();
			int count = obj.optInt("COUNT");
			totalCount = obj.optInt("TOTALCOUNT");
			tvGoodsNum.setText("共持有" + obj.optInt("TOTALCOUNT") + "种产品");
			if (totalCount == 0) {
				tvMyLicai.setText("您暂无理财产品");
			}
			tvTotalEarningsAccount.setText(obj.optString("TotalShouYi"));
			Log.i("result", "---------Count-------" + count);
			if (count > 0) {
				tvMyLicai.setText("我的理财");
				for (int i = 0; i < count; i++) {
					String nameTitle = obj.optJSONArray("DATA")
							.optJSONObject(i).optString("pro_name");
					String timeLimit = obj.optJSONArray("DATA")
							.optJSONObject(i).optString("licaidays");
					String yearEarnings = obj.optJSONArray("DATA")
							.optJSONObject(i).optString("yearrate");
					String buyAccount = obj.optJSONArray("DATA")
							.optJSONObject(i).optString("total");
					String buyTime = obj.optJSONArray("DATA").optJSONObject(i)
							.optString("addtime");
					String state = obj.optJSONArray("DATA").optJSONObject(i)
							.optString("state");
					String yestodaylixi = obj.optJSONArray("DATA")
							.optJSONObject(i).optString("yestodaylixi");
					String totalLixi = obj.optJSONArray("DATA")
							.optJSONObject(i).optString("totallixi");
					String qixiDate = obj.optJSONArray("DATA").optJSONObject(i)
							.optString("begintime");
					String daoqiDate = obj.optJSONArray("DATA")
							.optJSONObject(i).optString("endtime");
					String proid = obj.optJSONArray("DATA").optJSONObject(i)
							.optString("proid");
					String str3 = obj.optJSONArray("DATA").optJSONObject(i)
							.optString("str3");// 贷款状态 未贷款 un 贷款了 dai 还款了 ok
					String id = obj.optJSONArray("DATA").optJSONObject(i)
							.optString("id");// 订单id
					String deci3 = obj.optJSONArray("DATA").optJSONObject(i)
							.optString("deci3");//预期收益

					// 给info设置数据
					LicaiNewGoodsInfo info = new LicaiNewGoodsInfo();
					info.setNameTitle(nameTitle);
					info.setTimeLimit(timeLimit);
					info.setYearEarnings(yearEarnings);
					info.setBuyAccount(buyAccount);
					info.setBuyTime(buyTime);
					info.setStrInfo(state);
					info.setTotalEarningsAccount(totalLixi);
					info.setYesterDayEarningsAccount(yestodaylixi);
					info.setQixiDate(qixiDate);
					info.setDaoqiDate(daoqiDate);
					info.setProid(proid);
					info.setLoanType(str3);
					info.setCrodId(id);
					info.setEarnings(deci3);
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
		page++;
		postQueryLicaiHttp();
	}
}
