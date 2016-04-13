package com.lk.qf.pay.fragment;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.http.entity.StringEntity;
import org.json.JSONException;
import org.json.JSONObject;
import com.alibaba.fastjson.JSON;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;
import com.lk.bhb.pay.R;
import com.lk.qf.pay.adapter.LoanHKAdapter;
import com.lk.qf.pay.beans.LoanHuanInfo;
import com.lk.qf.pay.golbal.MApplication;
import com.lk.qf.pay.golbal.MyUrls;
import com.lk.qf.pay.sharedpref.SharedPrefConstant;
import com.lk.qf.pay.tool.T;
import com.lk.qf.pay.wedget.view.DialogWidget;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

public class PolyLoanHKListFragment extends ListFragment {

	// private PullToRefreshListView lv;
	private ListView lv;
	private List<LoanHuanInfo> list;
	private List<LoanHuanInfo> listReturn;
	private int page = 1;
	private int dataCount = 30;
	private Map<String, String> map;
	private LoanHuanInfo info;
	private LoanHKAdapter adapter;
	private Context context;
	private View layoutView;
	private String huantype = "";
	private String account = "", pid = "", pwd = "", licaiMoney = "";
	private DialogWidget mDialogWidget;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		layoutView = inflater.inflate(R.layout.poly_hk_list_loan_layout,
				container, false);
		context = getActivity();
		lv = (ListView) layoutView.findViewById(android.R.id.list);
		list = new ArrayList<LoanHuanInfo>();
		adapter = new LoanHKAdapter(getActivity(), list);
		setListAdapter(adapter);
		return layoutView;
	}

	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		postQueryHkList();
	}

	/**
	 * 还款记录
	 * 
	 * @return
	 */
	private void postQueryHkList() {
		RequestParams params = new RequestParams();
		String url = "";
		url = MyUrls.BL_HUANLIST;

		map = new HashMap<String, String>();

		map.put("username", MApplication.mSharedPref
				.getSharePrefString(SharedPrefConstant.USERNAME));
		map.put("pageSize", "15");
		map.put("page", "" + page);
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
			}

			@Override
			public void onSuccess(ResponseInfo<String> response) {
				// TODO Auto-generated method stub
				String strReturnLogin = response.result;
				Log.i("result", "---------------还款记录-returnjson---"
						+ strReturnLogin);
				jsonDetail(strReturnLogin);

				String returnCode = info.getCode();

				if (returnCode.equals("00")) {
					if (listReturn == null || listReturn.size() == 0) {
						T.ss(getResources().getString(
								R.string.query_nothing_more));
						list.clear();
						adapter.notifyDataSetChanged();

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
			info = new LoanHuanInfo();
			info.setCode(obj.optString("CODE"));
			info.setMessage(obj.optString("MESSAGE"));
			listReturn = new ArrayList<LoanHuanInfo>();
			int count = obj.optInt("count");
			Log.i("result", "---------Count-------" + count);
			if (count > 0) {
				for (int i = 0; i < count; i++) {

					String actualMoney = obj.optJSONArray("date")
							.optJSONObject(i).optString("actualMoney");// 实际还款金额
					String addtime = obj.optJSONArray("date").optJSONObject(i)
							.optString("addtime");
					String loan_str = obj.optJSONArray("date").optJSONObject(i)
							.optString("loan_str");// 贷款订单号
					String payType = obj.optJSONArray("date").optJSONObject(i)
							.optString("paytype");// 还款类型

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
}
