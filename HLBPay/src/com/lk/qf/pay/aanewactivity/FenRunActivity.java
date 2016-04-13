package com.lk.qf.pay.aanewactivity;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.http.entity.StringEntity;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
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
import com.lk.qf.pay.golbal.MApplication;
import com.lk.qf.pay.golbal.MyUrls;
import com.lk.qf.pay.sharedpref.SharedPrefConstant;
import com.lk.qf.pay.tool.T;
import com.lk.qf.pay.wedget.CommonTitleBar;

public class FenRunActivity extends BaseActivity implements OnClickListener {

	private Spinner sp;
	private RadioGroup rg;
	private EditText edStartTime, edEndTime;
	private TextView tvHuizongAccount, tvFenRunAccount;
	private String money, posmoney, mposmoney, weixinmoney, zhifubaomoney,
			baidumoney, suningmoney, type;
	private Button btnFenRun, btnSearch;
	private List<String> listSpinner;
	private CommonTitleBar title;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);

		setContentView(R.layout.fenrun_layout);
		init();
	}

	private void init() {
		// edStartTime = (EditText) findViewById(R.id.ed_fenrun_startTime);
		// edEndTime = (EditText) findViewById(R.id.ed_fenrun_endTime);
		sp = (Spinner) findViewById(R.id.sp_fenrun);
		// rg = (RadioGroup) findViewById(R.id.rg_fenrun);
		tvHuizongAccount = (TextView) findViewById(R.id.tv_fenrun_huizongAccount);
		tvFenRunAccount = (TextView) findViewById(R.id.tv_fenrun_allAccount);
		// btnFenRun = (Button) findViewById(R.id.btn_fenrun_mx);
		// btnSearch = (Button) findViewById(R.id.btn_fenrun_query);
		title = (CommonTitleBar) findViewById(R.id.titlebar_fenrun);
		title.setActName("分润查询");
		title.setCanClickDestory(this, true);
		queryFenrun();
		addSpinner();

	}

	/**
	 * 添加 设置spinner
	 */
	private void addSpinner() {
		listSpinner = new ArrayList<String>();

//		 listSpinner.add(getResources().getString(R.string.fenrunType));
		listSpinner.add(getResources().getString(R.string.xianxiajiaoyi));//线下pos
		listSpinner.add(getResources().getString(R.string.yidongzhifu));//手刷
		listSpinner.add(getResources().getString(R.string.weixinzhifu));//微信
		listSpinner.add(getResources().getString(R.string.zhifubao));//支付宝
		listSpinner.add(getResources().getString(R.string.baiduqianbao));//百度钱包
		listSpinner.add(getResources().getString(R.string.suningyifubao));//苏宁易付宝

		ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_list_item_1, listSpinner);
		sp.setAdapter(arrayAdapter);

		sp.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {
				// TODO Auto-generated method stub
				switch (arg2) {
				case 0:
					
					tvHuizongAccount.setText(posmoney);
					break;
				case 1:
					tvHuizongAccount.setText(mposmoney);

					break;
				case 2:

					tvHuizongAccount.setText(weixinmoney);
					break;
				case 3:
					tvHuizongAccount.setText(zhifubaomoney);

					break;
				case 4:

					tvHuizongAccount.setText(baidumoney);
					break;
				case 5:

					tvHuizongAccount.setText(suningmoney);
					break;

				default:
					break;
				}
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				// TODO Auto-generated method stub

			}
		});
	}

	@Override
	public void onClick(View arg0) {
		// TODO Auto-generated method stub

	}

	/**
	 * 分润查询
	 */
	private void queryFenrun() {
		Log.i("result", "----dd-----------");

		RequestParams params = new RequestParams();
		HashMap<String, String> map = new HashMap<String, String>();

		map.put("username", MApplication.mSharedPref
				.getSharePrefString(SharedPrefConstant.USERNAME));

		String json = JSON.toJSONString(map);
		Log.i("result", "----ddd-----------" + json);
		try {
			StringEntity bodyEntity = new StringEntity(json, "UTF-8");
			params.setBodyEntity(bodyEntity);
		} catch (UnsupportedEncodingException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		HttpUtils httpUtils = new HttpUtils();
		String url = MyUrls.FENRUN;

		httpUtils.send(HttpMethod.POST, url, params,
				new RequestCallBack<String>() {

					@Override
					public void onFailure(HttpException arg0, String arg1) {
						// TODO Auto-generated method stub

						T.ss("操作超时");
					}

					@Override
					public void onSuccess(ResponseInfo<String> response) {
						// TODO Auto-generated method stub

						String str = response.result;
						Log.i("result", "----ddd-----------" + str);
						String code = "";
						String message = "";
						// money, posmoney, mposmoney, weixinmoney,
						// zhifubaomoney,
						// baidumoney, suningmoney
						JSONObject obj;
						try {
							obj = new JSONObject(str);
							code = obj.optString("CODE");
							message = obj.optString("MESSAGE");
							money = obj.optString("money");
							posmoney = obj.optString("posmoney");
							mposmoney = obj.optString("mposmoney");
							weixinmoney = obj.optString("weixinmoney");
							zhifubaomoney = obj.optString("zhifubaomoney");
							baidumoney = obj.optString("baidumoney");
							suningmoney = obj.optString("suningmoney");
						} catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						if (code.equals("00")) {
							tvFenRunAccount.setText(money);
							tvHuizongAccount.setText(posmoney);
						}else{
							T.ss(message);
						}
						//
					}
				});
	}

}
