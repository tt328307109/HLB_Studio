package com.lk.qf.pay.aanewactivity;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.entity.StringEntity;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Adapter;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import com.alibaba.fastjson.JSON;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;
import com.lk.bhb.pay.R;
import com.lk.qf.pay.activity.BaseActivity;
import com.lk.qf.pay.adapter.BankZHAdapter;
import com.lk.qf.pay.adapter.CityAdapter;
import com.lk.qf.pay.adapter.ProvinceInfoAdapter;
import com.lk.qf.pay.beans.BankBranch;
import com.lk.qf.pay.beans.CityInfo;
import com.lk.qf.pay.beans.ProvinceInfo;
import com.lk.qf.pay.beans.Xinyongkainfo;
import com.lk.qf.pay.golbal.MyUrls;
import com.lk.qf.pay.tool.T;
import com.lk.qf.pay.wedget.CommonTitleBar;

public class FuzzyQueryActivity extends BaseActivity {

	private ListView lv;
	private EditText edBankName;
	private CommonTitleBar title;
	private BankZHAdapter adapter;
	private List<BankBranch> list;
	private String bankName = "", showType, provinceID;
	private CharSequence temp;
	private String bank = "";
	private boolean isZH = false;
	private BankBranch bankInfo;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.fuzzy_query_layout);
		edBankName = (EditText) findViewById(R.id.ed_fuzzy_query_bankName);
		lv = (ListView) findViewById(R.id.ll_fuzzy_query);
		title = (CommonTitleBar) findViewById(R.id.titlebar_fuzzy_query_back);
		title.setCanClickDestory(this, true);
		list = new ArrayList<BankBranch>();
		// adapter = new SimpleAdapter(this, data, resource, from, to)
		adapter = new BankZHAdapter(this, list);
		lv.setAdapter(adapter);
		lv.setOnItemClickListener(itemClickListener);

		Intent intent = getIntent();
		if (intent != null) {
			bank = intent.getStringExtra("bankName");
			showType = intent.getStringExtra("showType");
			if (showType.equals("bankzh")) {
				isZH = true;
				postBankZHHttp();
				title.setActName("选择开户银行支行");
			} else if (showType.equals("bank")) {
				postBankHttp();
				title.setActName("选择开户银行");
			} else if (showType.equals("city")) {
				provinceID = intent.getStringExtra("provinceID");
				edBankName.setVisibility(View.GONE);
				postCityHttp(provinceID);
				title.setActName("选择地址");
			} else {
				title.setActName("选择地址");
				edBankName.setVisibility(View.GONE);
				postProvinceHttp();
			}

		}
		edBankName.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence arg0, int arg1, int arg2,
					int arg3) {
				// TODO Auto-generated method stub
				temp = arg0;
				bankName = "" + temp;
			}

			@Override
			public void beforeTextChanged(CharSequence arg0, int start,
					int before, int count) {
				// TODO Auto-generated method stub

			}

			@Override
			public void afterTextChanged(Editable arg0) {
				// TODO Auto-generated method stub
				list.clear();
				if (isZH) {
					postBankZHHttp();
				} else {
					postBankHttp();
				}
			}
		});
	}

	OnItemClickListener itemClickListener = new OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> arg0, View view, int position,
				long arg3) {
			// TODO Auto-generated method stub
			BankBranch bInfo = (BankBranch) arg0.getItemAtPosition(position);
			bankName = bInfo.getBankbankName();
			String bankId = bInfo.getBankbankId();
			Log.i("result", "----------bankName-----" + bankName);
			Intent intent = new Intent();
			intent.putExtra("bankName", bankName);
			intent.putExtra("bankId", bankId);
			setResult(Activity.RESULT_OK, intent);
			finish();
		}
	};

	/**
	 * 银行
	 * 
	 * @return
	 */
	private void postBankHttp() {
		// showLoadingDialog(getResources().getString(R.string.logining));
		RequestParams params = new RequestParams();
		String url = MyUrls.BANKLIST;
		// pInfo = new ProvinceInfo();
		Map<String, String> map = new HashMap<String, String>();
		map.put("bank", bankName);

		String json = JSON.toJSONString(map);
		Log.i("result", "----------bankName-json-zh---" + json);
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
				// returnInfo.setCode("01");
				// dismissLoadingDialog();
				Log.i("result",
						"--------secondFailure-----------"
								+ arg0.getExceptionCode());
			}

			@Override
			public void onSuccess(ResponseInfo<String> response) {
				// TODO Auto-generated method stub
				String strReturnLogin = response.result;
				// threeList = new ArrayList<String>();
				Log.i("result", "----dddf---bank--------" + strReturnLogin);
				jsonReturnProvince(2, strReturnLogin);

				if (list == null || list.size() == 0) {
					T.ss("无数据");
				} else {
					// Collections.reverse(list);
					// lv.setAdapter(adapter);
					adapter.notifyDataSetChanged();
				}
			}
		});
		// return secondList;
	}

	/**
	 * 支行银行
	 * 
	 * @return
	 */
	private void postBankZHHttp() {
		showLoadingDialog();
		// showLoadingDialog(getResources().getString(R.string.logining));
		RequestParams params = new RequestParams();
		String url = MyUrls.BANK2LIST;

		Map<String, String> map = new HashMap<String, String>();
		map.put("mainbank", bank);
		map.put("bankname", bankName);
		String json = JSON.toJSONString(map);
		Log.i("result", "-----d-----bankName-json----" + json);
		try {
			StringEntity bodyEntity = new StringEntity(json, "UTF-8");
			params.setBodyEntity(bodyEntity);
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		HttpUtils utils = new HttpUtils();
		utils.configSoTimeout(1000 * 60);
		utils.send(HttpMethod.POST, url, params, new RequestCallBack<String>() {

			@Override
			public void onFailure(HttpException arg0, String arg1) {

				T.ss("操作超时");
				dismissLoadingDialog();
				Log.i("result",
						"--------secondFailure-----------"
								+ arg0.getExceptionCode());
			}

			@Override
			public void onSuccess(ResponseInfo<String> response) {
				// TODO Auto-generated method stub
				String strReturnLogin = response.result;
				// threeList = new ArrayList<String>();
				Log.i("result", "----dddf---bank--------" + strReturnLogin);
				jsonReturnProvince(2, strReturnLogin);
				dismissLoadingDialog();
				if (list == null || list.size() == 0) {
					T.ss("无数据");
				} else {
					// Collections.reverse(list);
					// lv.setAdapter(adapter);
					adapter.notifyDataSetChanged();
				}
			}
		});
	}

	/**
	 * 省
	 * 
	 * @return
	 */
	private void postProvinceHttp() {
		// showLoadingDialog(getResources().getString(R.string.logining));
		RequestParams params = new RequestParams();
		String url = MyUrls.PROVINCE;
		Map<String, String> map = new HashMap<String, String>();

		String json = JSON.toJSONString(map);
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
				Log.i("result",
						"-------failure------" + arg0.getExceptionCode());
			}

			@Override
			public void onSuccess(ResponseInfo<String> response) {
				// TODO Auto-generated method stub
				String strReturnLogin = response.result;
				jsonReturnProvince(0, strReturnLogin);
				// dismissLoadingDialog();
				Log.i("result", "-------onSuccess------"+strReturnLogin);
				if (list == null || list.size() == 0) {
					T.ss("无数据");
				} else {
					adapter.notifyDataSetChanged();
				}
			}
		});
		// return firstList;
	}
	

	/**
	 * 市
	 * 
	 * @return
	 */
	private void postCityHttp(String provinceID) {
		// showLoadingDialog(getResources().getString(R.string.logining));
		RequestParams params = new RequestParams();
		String url = MyUrls.CITY;
		// pInfo = new ProvinceInfo();
		Map<String, String> map = new HashMap<String, String>();
		map.put("provinceID", provinceID);

		String json = JSON.toJSONString(map);
		Log.i("result", "-------onjson-----"+json);
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
				// returnInfo.setCode("01");
				// dismissLoadingDialog();
				Log.i("result",
						"--------secondFailure-----------"
								+ arg0.getExceptionCode());
			}

			@Override
			public void onSuccess(ResponseInfo<String> response) {
				// TODO Auto-generated method stub
				String strReturnLogin = response.result;
				jsonReturnProvince(1, strReturnLogin);
				// dismissLoadingDialog();
				Log.i("result", "-------onSuccess-a-----"+strReturnLogin);
				if (list == null || list.size() == 0) {
					T.ss("无数据");
				} else {
					// Collections.reverse(list);
					// lv.setAdapter(adapter);
					adapter.notifyDataSetChanged();
				}
			}
		});
		// return secondList;
	}

	/**
	 * 解析 Json字符串 登录返回结果
	 * 
	 * @param str
	 * @return
	 */
	private void jsonReturnProvince(int addressType, String str) {
		try {
			JSONObject obj = new JSONObject(str);
			String code = obj.optString("CODE");
			String message = obj.optString("MESSAGE");
			// returnInfo.setCode(obj.optString("CODE"));
			// returnInfo.setMessage(obj.optString("MESSAGE"));

			if (code.equals("00")) {

				int count = obj.optInt("count");
				if (count > 0) {

					for (int i = 0; i < count; i++) {
						switch (addressType) {
						case 0:

							bankInfo = new BankBranch();
							String p_name = obj.optJSONArray("date")
									.optJSONObject(i).optString("p_name");
							String p_id = obj.optJSONArray("date")
									.optJSONObject(i).optString("p_id");
							bankInfo.setBankbankName(p_name);
							bankInfo.setBankbankId(p_id);
							list.add(bankInfo);
							break;
						case 1:

							String c_id = obj.optJSONArray("date")
									.optJSONObject(i).optString("c_id");
							String c_name = obj.optJSONArray("date")
									.optJSONObject(i).optString("c_name");
							bankInfo = new BankBranch();
							bankInfo.setBankbankName(c_name);
							bankInfo.setBankbankId(c_id);
							list.add(bankInfo);
							break;
						case 2:

							bankInfo = new BankBranch();
							String bankName = obj.optJSONArray("date")
									.optJSONObject(i).optString("bankname");
							String bankId = obj.optJSONArray("date")
									.optJSONObject(i).optString("id");
							bankInfo.setBankbankName(bankName);
							bankInfo.setBankbankId(bankId);
							// 给info设置数据
							list.add(bankInfo);
							break;

						default:
							break;
						}
					}
				}
			} else {
				T.ss(message);
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
}
