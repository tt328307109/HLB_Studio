package com.lk.qf.pay.indiana.activity;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
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
import android.widget.EditText;
import android.widget.TextView;
import android.widget.ToggleButton;
import com.alibaba.fastjson.JSON;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;
import com.lk.bhb.pay.R;
import com.lk.qf.pay.aanewactivity.FuzzyQueryActivity;
import com.lk.qf.pay.golbal.MApplication;
import com.lk.qf.pay.golbal.MyUrls;
import com.lk.qf.pay.indiana.bean.IndianaGoodsInfo;
import com.lk.qf.pay.sharedpref.SharedPrefConstant;
import com.lk.qf.pay.tool.T;
import com.lk.qf.pay.wedget.CommonTitleBarYellow;

public class EditAddressActivity extends IndianaBaseActivity implements
		OnClickListener {

	private CommonTitleBarYellow title;
	private EditText edName, edPhoneNum, edDetailedAddress;
	private TextView tvProvince, tvCity;
	private ToggleButton tb;
	private String name,phoneNum,detailAddress,province="北京市",city="市辖区",area;
	private IndianaGoodsInfo info;
	private String isdef="0",type="0",provinceID="110000";
	public static final int REQUEST_PROVINCE = 4;//省
	public static final int REQUEST_CITY = 5;//市
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.indiana_edit_address_layout);
		init();
	}

	private void init() {
		title = (CommonTitleBarYellow) findViewById(R.id.titlebar_editedit_address);
		title.setActName("编辑地址");
		title.setCanClickDestory(this, true);
		findViewById(R.id.btn_indiana_edit_addr_save).setOnClickListener(this);
		edName = (EditText) findViewById(R.id.ed_indiana_edit_addr_Name);
		edPhoneNum = (EditText) findViewById(R.id.ed_indiana_edit_addr_phoneNum);
		edDetailedAddress = (EditText) findViewById(R.id.ed_indiana_edit_addr_Detailededit_addr);
		tvProvince = (TextView) findViewById(R.id.tv_indiana_edit_addr_province);
		tvCity = (TextView) findViewById(R.id.tv_indiana_edit_addr_city);
		tvProvince.setOnClickListener(this);
		tvCity.setOnClickListener(this);
		tvProvince.setText(province);
		tvCity.setText(city);
//		tvArea = (TextView) findViewById(R.id.tv_indiana_edit_addr_area);
		tb = (ToggleButton) findViewById(R.id.tb_indiana_edit_addr);

		Intent intent = getIntent();
		if (intent!=null) {
			info = intent.getParcelableExtra("addressInfo");
			if (info!=null) {
				name = info.getUserName();
				phoneNum = info.getUserPhoneNum();
				province = info.getProvince();
				city = info.getCity();
				area = info.getArea();
				detailAddress = info.getUserAddress();
				isdef = info.getIsdefAddress();
				
				edName.setText(name);
				edPhoneNum.setText(phoneNum);
				tvProvince.setText(province);
				tvCity.setText(city);
				edDetailedAddress.setText(detailAddress);
				type = "1";
				if (isdef.equals("1")) {
					tb.setChecked(true);
				}
			}
		}
	}

	@Override
	public void onClick(View arg0) {
		// TODO Auto-generated method stub
		switch (arg0.getId()) {
		case R.id.btn_indiana_edit_addr_save:
			addAddress();
			break;
		case R.id.tv_indiana_edit_addr_province:
			Intent intent1 = new Intent(EditAddressActivity.this,
					FuzzyQueryActivity.class);
			intent1.putExtra("showType", "province");
			startActivityForResult(intent1, REQUEST_PROVINCE);
			break;
		case R.id.tv_indiana_edit_addr_city:
			Intent intent2 = new Intent(EditAddressActivity.this,
					FuzzyQueryActivity.class);
			intent2.putExtra("showType", "city");
			intent2.putExtra("provinceID", provinceID);
			startActivityForResult(intent2, REQUEST_CITY);
			
			break;
//		case R.id.tv_indiana_edit_addr_area:
//			
//			break;

		default:
			break;
		}
	}
	private void addAddress(){
		name = edName.getText().toString();
		phoneNum = edPhoneNum.getText().toString();
		detailAddress = edDetailedAddress.getText().toString();
		province = tvProvince.getText().toString();
		city = tvCity.getText().toString();
		boolean isChecked = tb.isChecked();
		if (isChecked) {
			isdef = "1";
		}
		
		if (name==null || name.equals("")) {
			T.ss("请输入收货人姓名");
			return;
		}
		if (phoneNum==null || phoneNum.equals("")) {
			T.ss("请输入收货人联系电话");
			return;
		}
		
		if (province==null || province.equals("")) {
			T.ss("请选择省");
			return;
		}
		if (city==null || city.equals("")) {
			T.ss("请选择市");
			return;
		}
		if (detailAddress==null || detailAddress.equals("")) {
			T.ss("请输入详细收货地址");
			return;
		}
		addMyAddress();
	}
	
	/**
	 * 添加地址
	 */
	private void addMyAddress() {
		showLoadingDialog();

		RequestParams params = new RequestParams();
		String url = MyUrls.ROOT_URL_ADRESSEIDT;
		Map<String, String> map = new HashMap<String, String>();
		map.put("username", MApplication.mSharedPref
				.getSharePrefString(SharedPrefConstant.USERNAME));
		map.put("pro", province);
		map.put("city", city);
		map.put("clazz", detailAddress);
		map.put("realname", name);
		map.put("phone", phoneNum);
		map.put("isdef", isdef);//(0:false,1:true)
		map.put("aid", "");
		map.put("type", type);//（修改：1 添加0）
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

	
	@Override
	protected void onActivityResult(int arg0, int arg1, Intent arg2) {
		// TODO Auto-generated method stub
		super.onActivityResult(arg0, arg1, arg2);
		switch (arg0) {
		case REQUEST_PROVINCE:
			if (arg1 == Activity.RESULT_OK) {
				province = arg2.getStringExtra("bankName");
				provinceID = arg2.getStringExtra("bankId");
				Log.i("result", "----------province---s--" + province);
				if (province.length() > 5) {
					String strName = province.substring(0, 5);
					Log.i("result", "------hang1---" + strName);
					tvProvince.setText(strName + "...");
				} else {
					Log.i("result", "----------province--s-s--" + province);
					tvProvince.setText(province);
				}
			}
			break;
		case REQUEST_CITY:
			if (arg1 == Activity.RESULT_OK) {
				city = arg2.getStringExtra("bankName");
				Log.i("result", "----------citys--" + city);
				if (city.length() > 10) {
					String strName = city.substring(0, 5);
					Log.i("result", "------hacity--" + strName);
					tvCity.setText(strName + "...");
				} else {
					Log.i("result", "------hcity-zh--" + city);
					tvCity.setText(city);
				}
			}
			break;

		default:
			break;
		}

	}
}
