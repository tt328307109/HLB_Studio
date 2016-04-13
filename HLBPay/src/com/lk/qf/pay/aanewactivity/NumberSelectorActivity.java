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
import android.util.Log;
import android.view.View;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import com.alibaba.fastjson.JSON;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;
import com.lk.bhb.pay.R;
import com.lk.qf.pay.golbal.MyUrls;
import com.lk.qf.pay.tool.T;

public class NumberSelectorActivity extends Activity {

	private ArrayAdapter<String> numberAdapter;
	private List<String> list;
	private String title = "选择日期";
	private String date = "";
	private ListView lv;
	private static int TYPE = 0;// 0为账单日 1为还款日

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);

		setContentView(R.layout.number_select_layout);
		lv = (ListView) findViewById(R.id.lv_number);
		Intent intent = getIntent();
		list = new ArrayList<String>();
		if (intent != null) {
			TYPE = intent.getIntExtra("type", 0);
//			Log.i("result", "-----------type-----" + TYPE);
			if (TYPE == 0 || TYPE == 1 || TYPE == 3) {

				if (TYPE == 0) {
					for (int i = 1; i < 29; i++) {
						list.add("" + i + "日");
					}
					title = "选择账单日";
				} else if (TYPE == 1) {
					for (int i = 15; i < 57; i++) {

						list.add("" + i + "天");
					}
					title = "选择还款期";
//					Log.i("result", "-----------type---2--" + title);

				} else {
					for (int i = 1; i < 25; i++) {

						list.add("" + i + "个月");
					}
					title = "选择贷款期限";
//					Log.i("result", "-----------type---3--" + title);

				}
				numberAdapter = new ArrayAdapter<String>(this,
						R.layout.number_item_layout, R.id.tv_number_list, list);
				lv.setAdapter(numberAdapter);

			} else if (TYPE == 2) {
				title = "选择银行";
//				Log.i("result", "-----------type---2--" + title);
				postBankHttp();
			}else if (TYPE == 4){
				
			}
		}
		lv.setOnItemClickListener(itemClickListener);
		setTitle(title);

	}

	OnItemClickListener itemClickListener = new OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
				long arg3) {
			// TODO Auto-generated method stub
			// date = ((TextView) arg1).getText().toString();
			Adapter adapter = arg0.getAdapter();
			date = adapter.getItem(arg2).toString();
//			Log.i("result", "----------------num----------" + date);
			Intent intent = new Intent();
			intent.putExtra("dateNum", date);
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
		map.put("bank", "");

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
				Log.i("result", "----dddf-----------" + strReturnLogin);
				jsonReturnProvince(2, strReturnLogin);

				if (list == null || list.size() == 0) {
					T.ss("无数据");
				} else {
					Collections.reverse(list);
					numberAdapter = new ArrayAdapter<String>(
							NumberSelectorActivity.this,
							R.layout.number_item_layout, R.id.tv_number_list,
							list);
					lv.setAdapter(numberAdapter);

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

						case 2:

							String bankName = obj.optJSONArray("date")
									.optJSONObject(i).optString("bankname");
							// 给info设置数据
							list.add(bankName);
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
