package com.lk.qf.pay.aanewactivity.creditcard;

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
import android.widget.ListView;
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
import com.lk.qf.pay.activity.LoginActivity;
import com.lk.qf.pay.adapter.CreditCardRepayAdapter;
import com.lk.qf.pay.beans.XYKTradeListInfo;
import com.lk.qf.pay.beans.Xinyongkainfo;
import com.lk.qf.pay.golbal.MApplication;
import com.lk.qf.pay.golbal.MyUrls;
import com.lk.qf.pay.sharedpref.SharedPrefConstant;
import com.lk.qf.pay.tool.T;
import com.lk.qf.pay.utils.StringUtils;
import com.lk.qf.pay.wedget.CommonTitleBar;

public class CreditCardRepayListActivity extends BaseActivity implements
		OnRefreshListener2<ListView> {

	private CommonTitleBar title;
	private PullToRefreshListView lv;
	private List<XYKTradeListInfo> list;
	private List<XYKTradeListInfo> listReturn;
	private int page = 1;
	private int dataCount = 10;
	private Map<String, String> map;
	private XYKTradeListInfo info;
	private CreditCardRepayAdapter adapter;
	private Context context;
	private String id,yhAccount,whAccount,yhNum,whNum;
	private TextView tvWHAccount, tvYHAccount, tvYHNum, tvWHNum;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.credit_card_repay_list_layout);
		init();
	}

	private void init() {
		title = (CommonTitleBar) findViewById(R.id.titlebar_creditCard_repay_list);
		title.setActName("贷款明细");
		title.setCanClickDestory(this, true);
		context = CreditCardRepayListActivity.this;
		// tvRepayTotalAccount = (TextView)
		// findViewById(R.id.tv_creditCard_repayList_repayAccount);
		tvWHAccount = (TextView) findViewById(R.id.tv_creditCard_repayList_whpayAccount);
		tvYHAccount = (TextView) findViewById(R.id.tv_creditCard_repayList_yhrepayAccount);
		tvYHNum = (TextView) findViewById(R.id.tv_creditCard_repayList_yhrepayNum);
		tvWHNum = (TextView) findViewById(R.id.tv_creditCard_repayList_whpayNum);

		Intent intent = getIntent();
		if (intent != null) {
			id = intent.getStringExtra("id");
//			Log.i("result", "-------id-----" + id);
		}
		lv = (PullToRefreshListView) findViewById(R.id.myPull_refresh_list_order_creditCard_repay);
		lv.setOnRefreshListener(this);
		list = new ArrayList<XYKTradeListInfo>();
		adapter = new CreditCardRepayAdapter(context, list);
		lv.setAdapter(adapter);
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		postQueryRecordHttp();
	}

	private void postQueryRecordHttp() {
		RequestParams params = new RequestParams();
		String url = MyUrls.ROOT_URL_REFUNDRECORD;
		map = new HashMap<String, String>();
		map.put("username", MApplication.mSharedPref
				.getSharePrefString(SharedPrefConstant.USERNAME));
		map.put("count", "" + dataCount);
		map.put("page", "" + page);
		map.put("Cmd", "RefundDetailList");
		map.put("id", id);
		map.put("token", MApplication.mSharedPref
				.getSharePrefString(SharedPrefConstant.TOKEN));
		String json = JSON.toJSONString(map);
//		Log.i("result", "-------订单请求-----" + json);
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
				T.ss("操作超时!");
				dismissLoadingDialog();
				lv.onRefreshComplete();
			}

			@Override
			public void onSuccess(ResponseInfo<String> response) {
				// TODO Auto-generated method stub
				String strReturnLogin = response.result;
				Log.i("result", "---------------定单-returnjson---"
						+ strReturnLogin);
				lv.onRefreshComplete();
				jsonDetail(strReturnLogin);
				String returnCode = info.getCode();
				if (returnCode.equals("00")) {
					if (listReturn == null || listReturn.size() == 0) {
						T.ss(getResources().getString(
								R.string.query_nothing_more));
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
			info = new XYKTradeListInfo();
			info.setCode(obj.optString("CODE"));
			info.setMessage(obj.optString("MESSAGE"));
			listReturn = new ArrayList<XYKTradeListInfo>();
			if (info.getCode().equals("00")) {
				tvYHAccount.setText(""+obj.optString("yiHuanTotal"));
				tvYHNum.setText(""+obj.optString("yiHuanCount"));
				tvWHAccount.setText(""+obj.optString("weiHuanTotal"));
				tvWHNum.setText(""+obj.optString("weiHuanCount"));
			}
			
			int count = obj.optInt("Count");
			if (count < dataCount) {
				lv.setMode(Mode.PULL_DOWN_TO_REFRESH);
			}
			if (count > 0) {
				for (int i = 0; i < count; i++) {
					JSONObject jsObj = obj.optJSONArray("data")
							.optJSONObject(i);
					// 给info设置数据
					XYKTradeListInfo xykinfo = new XYKTradeListInfo();
					xykinfo.setDate(jsObj.optString("refund_time"));//还款时间
					String inMoney = jsObj.optString("in_money");
//					String inMoney = StringUtils.moneyFormat(jsObj.optString("in_money"),4);
					xykinfo.setHkAccount(""+inMoney);//还款金额
					xykinfo.setPmType(jsObj.optString("refund_type"));//上午还是下午
					xykinfo.setType(jsObj.optString("refund_state"));//还款状态
					listReturn.add(xykinfo);
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
		postQueryRecordHttp();
	}

	@Override
	public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
		// TODO Auto-generated method stub
		page++;
		postQueryRecordHttp();
	}
}
