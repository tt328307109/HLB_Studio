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
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.Spinner;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;
import com.lk.bhb.pay.R;
import com.lk.qf.pay.activity.BaseActivity;
import com.lk.qf.pay.adapter.CityAdapter;
import com.lk.qf.pay.adapter.ProvinceInfoAdapter;
import com.lk.qf.pay.beans.CityInfo;
import com.lk.qf.pay.beans.ProvinceInfo;
import com.lk.qf.pay.golbal.MApplication;
import com.lk.qf.pay.golbal.MyUrls;
import com.lk.qf.pay.sharedpref.SharedPrefConstant;
import com.lk.qf.pay.tool.T;
import com.lk.qf.pay.wedget.CommonTitleBar;
import com.lk.qf.pay.wedget.view.MyXEdittextView;

public class JieSuanActivity extends BaseActivity implements OnClickListener {

	private CommonTitleBar title;
	private EditText edBankUserName;
	private MyXEdittextView edBankNum1, edBankNum2;
	private String bankName, bankzhName, bankUserName, bankNum1, bankNum2,
			bankAddr, type = "个人账户", bankId = "", bankZhId = "";
	private RadioGroup rgJiesuan;
	private Spinner sp1, sp2;
	private String userName;
	private List<ProvinceInfo> listSpinner;
	private int spinnerId1, spinnerId2;
	private List<ProvinceInfo> firstList;// 省
	private List<CityInfo> secondList;// 市
	private CityAdapter cityAdapter;
	private ProvinceInfoAdapter provinceAdapter;
	private ProvinceInfo provinceInfo;
	private CityInfo cityInfo;
	private List<String> threeList;
	private String provinceId, provinceName;
	private String cityId, cityName, type1 = "";// type1 跳转信息
	private static final int REQUEST_BANKNAME = 2;// 银行名
	private static final int REQUEST_BANKNAMEZH = 3;// 银行支行
	private TextView tvBankName, tvBankZHName;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.jiesuan_layout);
		init();
	}

	private void init() {
		Intent intent = getIntent();
		if (intent != null) {
			userName = intent.getStringExtra("userName");
			type1 = intent.getStringExtra("type");
		} else {
			Log.i("result", "----------dgs---" + userName);
			userName = MApplication.mSharedPref
					.getSharePrefString(SharedPrefConstant.USERNAME);
		}
		// edBank = (EditText) findViewById(R.id.ed_jiesuan_kaihuBank);
		tvBankZHName = (TextView) findViewById(R.id.tv_jiesuan_kaihuzhBank);
		edBankUserName = (EditText) findViewById(R.id.ed_jiesuan_kaihuName);
		edBankNum1 = (MyXEdittextView) findViewById(R.id.ed_jiesuan_kahao1);
		edBankNum2 = (MyXEdittextView) findViewById(R.id.ed_jiesuan_kahao2);
		edBankNum1.setPatten(" ", MyXEdittextView.BANK_NUM);
		edBankNum2.setPatten(" ", MyXEdittextView.BANK_NUM);

		rgJiesuan = (RadioGroup) findViewById(R.id.rg_jiesuan_type);
		tvBankName = (TextView) findViewById(R.id.tv_fuzzyQuery_select_bankName);
		sp1 = (Spinner) findViewById(R.id.sp_jiesuan_sheng);
		sp2 = (Spinner) findViewById(R.id.sp_jiesuan_shi);
		// spBank = (Spinner) findViewById(R.id.sp_jiesuan_bankName);

		setSpinnerListener();
		findViewById(R.id.btn_jiesuan_next).setOnClickListener(this);
		findViewById(R.id.rl_fuzzy_query_select_bankName).setOnClickListener(
				this);
		findViewById(R.id.rl_fuzzy_query_select_bankNamezh).setOnClickListener(
				this);// 支行

		title = (CommonTitleBar) findViewById(R.id.titlebar_jiesuan);
		title.setActName("结算信息");
		title.setCanClickDestory(this, true);
		// provinceUtils = new ProvinceUtils(this);
		// firstList = new ArrayList<ProvinceInfo>();
		// secondList = new ArrayList<CityInfo>();
		postProvinceHttp();
		// postBankHttp();
		// firstList = provinceUtils.postProvinceHttp();
		// Log.i("result", "-------s-----------"+firstList);
		// provinceAdapter = new ProvinceInfoAdapter(
		// JieSuanActivity.this, firstList);
		// sp1.setAdapter(provinceAdapter);
	}

	OnCheckedChangeListener changeListener = new OnCheckedChangeListener() {

		@Override
		public void onCheckedChanged(RadioGroup arg0, int arg1) {
			// TODO Auto-generated method stub
			switch (arg1) {
			case R.id.rb_jiesuan_1:
				type = "个人账户";
				break;
			case R.id.rb_jiesuan_2:

				type = "法人";
				break;
			case R.id.rb_jiesuan_3:

				type = "对公";

				break;

			default:
				break;
			}
		}
	};

	@Override
	public void onClick(View arg0) {
		// TODO Auto-generated method stub
		switch (arg0.getId()) {
		case R.id.btn_jiesuan_next:
			getData();
			break;
		case R.id.rl_fuzzy_query_select_bankName:
			Intent intent = new Intent(JieSuanActivity.this,
					FuzzyQueryActivity.class);
			intent.putExtra("showType", "bank");
			startActivityForResult(intent, REQUEST_BANKNAME);
			break;
		case R.id.rl_fuzzy_query_select_bankNamezh:
			if (bankName == null || bankName.equals("")) {
				T.ss("请先选择开户银行名");
				return;
			}
			Intent intentzh = new Intent(JieSuanActivity.this,
					FuzzyQueryActivity.class);
			intentzh.putExtra("bankName", bankName);
			intentzh.putExtra("showType", "bankzh");
			startActivityForResult(intentzh, REQUEST_BANKNAMEZH);
			break;

		default:
			break;
		}

	}

	private void setSpinnerListener() {
		sp1.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {
				// TODO Auto-generated method stub
				Log.i("result", "---------sgs-------o---");
				ProvinceInfo pInfo = firstList.get(arg2);
				provinceId = pInfo.getId();
				provinceName = pInfo.getName();
				// secondList = provinceUtils.postCityHttp(provinceId);
				postCityHttp(provinceId);
				Log.i("result", "---------sgs-------o---" + provinceId);
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				// TODO Auto-generated method stub

			}
		});
		sp2.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {
				// TODO Auto-generated method stub
				CityInfo cInfo = secondList.get(arg2);
				cityId = cInfo.getId();
				cityName = cInfo.getName();
				Log.i("result", "---------sgs-------od---" + cityId);
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				// TODO Auto-generated method stub

			}
		});

		// spBank.setOnItemSelectedListener(new OnItemSelectedListener() {
		//
		// @Override
		// public void onItemSelected(AdapterView<?> arg0, View arg1,
		// int arg2, long arg3) {
		// // TODO Auto-generated method stub
		//
		// }
		//
		// @Override
		// public void onNothingSelected(AdapterView<?> arg0) {
		// // TODO Auto-generated method stub
		//
		// }
		// });
	}

	private void getData() {
		// bankName = spBank.getSelectedItem().toString();
		// bankzhName = edzhBank.getText().toString();
		bankUserName = edBankUserName.getText().toString();
		bankNum1 = edBankNum1.getNonSeparatorText();
		bankNum2 = edBankNum2.getNonSeparatorText();
		// spinnerId = sp.getSelectedItemPosition();
		// bankAddr = listSpinner.get(spinnerId);

		if (bankUserName == null || bankUserName.equals("")) {
			T.ss("请输入开户名");
			return;
		}
		if (bankName == null || bankName.equals("")) {
			T.ss("请输入开户银行");
			return;
		}

		if (bankzhName == null || bankzhName.equals("")) {
			T.ss("请输入开户支行");
			return;
		}

		if (bankNum1 != null && !bankzhName.equals("") && bankNum2 != null
				&& !bankNum2.equals("")) {

			if (!bankNum1.equals(bankNum2)) {
				T.ss("两次输入的银行卡号不同，请重新输入");
			} else {
				if (bankNum1.length() < 12) {
					T.ss("请输入正确的银行卡号");
					return;
				}
				sendMerBank();
			}
		} else {
			T.ss("请输入银行卡号");
		}
	}

	/**
	 * 商户结算信息上传
	 */
	private void sendMerBank() {
		showLoadingDialog();
		RequestParams params = new RequestParams();
		String url = MyUrls.MERBANKADD;

		Map<String, String> map = new HashMap<String, String>();
		map.put("username", userName);
		map.put("combanksort1", type);
		map.put("combankname1", bankName);
		map.put("combank2id", bankZhId);// 支行id
		// map.put("combankaddr2", "");// 开户银行支行联行号
		map.put("combankprov2", provinceName);// 开户支行地址 省
		map.put("combankcity2", cityName);// 开户支行地址市
		map.put("combankcarname1", bankUserName);// 开户名
		map.put("combankcarnum1", bankNum1);// 银行账号
		map.put("token", MApplication.mSharedPref
				.getSharePrefString(SharedPrefConstant.TOKEN));

		String json = JSON.toJSONString(map);
		Log.i("result", "----ddd----s--都-----" + json);
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
				Log.i("result", "----设置结算失败-------" + arg0.getExceptionCode());
				dismissLoadingDialog();
			}

			@Override
			public void onSuccess(ResponseInfo<String> response) {
				// TODO Auto-generated method stub
				String code = "";
				String message = "";
				String str = response.result;
				dismissLoadingDialog();
				Log.i("result", "----费率----s---s----" + str);
				try {
					JSONObject obj = new JSONObject(str);
					code = obj.optString("CODE");
					message = obj.optString("MESSAGE");
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				if (code.equals("00")) {
					MApplication.mSharedPref.putSharePrefString(
							SharedPrefConstant.MERBANKADD, "1");
					if (type1 != null && !type1.equals("") && type1.equals("0")) {
						Intent intent = new Intent(JieSuanActivity.this,
								PoPhotoActivity.class);
						intent.putExtra("userName", userName);
						startActivity(intent);
						finish();
					} else {
						finish();
					}
					T.ss("保存成功");
				} else {
					T.ss(message);
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
						"-------failure-----------" + arg0.getExceptionCode());
			}

			@Override
			public void onSuccess(ResponseInfo<String> response) {
				// TODO Auto-generated method stub
				String strReturnLogin = response.result;
				firstList = new ArrayList<ProvinceInfo>();
				jsonReturnProvince(0, strReturnLogin);
				if (firstList == null || firstList.size() == 0) {

				} else {
					Collections.reverse(firstList);
					provinceAdapter = new ProvinceInfoAdapter(
							JieSuanActivity.this, firstList);
					sp1.setAdapter(provinceAdapter);
					Log.i("result", "----------省----------" + firstList.size());
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
				secondList = new ArrayList<CityInfo>();
				jsonReturnProvince(1, strReturnLogin);

				if (secondList == null || secondList.size() == 0) {
					T.ss("无数据");
				} else {
					Collections.reverse(secondList);
					cityAdapter = new CityAdapter(JieSuanActivity.this,
							secondList);

					sp2.setAdapter(cityAdapter);
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

							String p_id = obj.optJSONArray("date")
									.optJSONObject(i).optString("p_id");
							String p_name = obj.optJSONArray("date")
									.optJSONObject(i).optString("p_name");

							// 给info设置数据
							provinceInfo = new ProvinceInfo();
							provinceInfo.setId(p_id);// 省份id
							provinceInfo.setName(p_name);// 省份名
							firstList.add(provinceInfo);
							// Log.i("result", "----------sheng----------"
							// + firstList.size());
							break;
						case 1:

							String c_id = obj.optJSONArray("date")
									.optJSONObject(i).optString("c_id");
							String c_name = obj.optJSONArray("date")
									.optJSONObject(i).optString("c_name");

							// 给info设置数据
							cityInfo = new CityInfo();
							cityInfo.setId(c_id);// 省份id
							cityInfo.setName(c_name);// 省份名
							secondList.add(cityInfo);
							break;
						// case 2:
						//
						// String bankName = obj.optJSONArray("date")
						// .optJSONObject(i).optString("bankname");
						// // 给info设置数据
						// threeList.add(bankName);
						// break;

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

	@Override
	protected void onActivityResult(int arg0, int arg1, Intent arg2) {
		// TODO Auto-generated method stub
		super.onActivityResult(arg0, arg1, arg2);
		switch (arg0) {
		case REQUEST_BANKNAME:
			if (arg1 == Activity.RESULT_OK) {
				bankName = arg2.getStringExtra("bankName");
				bankId = arg2.getStringExtra("bankId");
				Log.i("result", "----------bankName---s--" + bankName);
				if (bankName.length() > 10) {
					String strName = bankName.substring(0, 10);
					// if (strName.length()>10) {
					// strName = strName.substring(0,10);
					// Log.i("result", "------hang0---"+strName);
					// }
					Log.i("result", "------hang1---" + strName);
					tvBankName.setText(strName + "...");
				} else {

					Log.i("result", "------hang2---" + bankName);
					tvBankName.setText(bankName);
				}
			}
			break;
		case REQUEST_BANKNAMEZH:
			if (arg1 == Activity.RESULT_OK) {
				bankzhName = arg2.getStringExtra("bankName");
				bankZhId = arg2.getStringExtra("bankId");
				Log.i("result", "----------bankName-zh--s--" + bankzhName);
				Log.i("result", "----------bankName-zh--id--" + bankZhId);
				if (bankName.length() > 10) {
					String strName = bankzhName.substring(0, 10);
					// if (strName.length()>10) {
					// strName = strName.substring(0,10);
					// Log.i("result", "------hang0---"+strName);
					// }
					Log.i("result", "------hang1-zh--" + strName);
					tvBankZHName.setText(strName + "...");
				} else {

					Log.i("result", "------hang2-zh--" + bankName);
					tvBankZHName.setText(bankzhName);
				}
			}
			break;

		default:
			break;
		}

	}
}
