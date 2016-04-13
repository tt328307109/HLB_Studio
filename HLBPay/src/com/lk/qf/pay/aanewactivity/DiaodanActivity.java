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
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.Spinner;

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

public class DiaodanActivity extends BaseActivity implements OnClickListener,OnCheckedChangeListener{

	
	private CommonTitleBar title;
	private RadioGroup rg;
	private EditText edDanNum,edLoginNum,edSN,edStartTime,edEndTime;
//	private Spinner sp;
	private Button btnSearch;
	private String danNum,loginNum,sn,fenlei,type,startTime,endTime,page;
	private List<String> listSpinner;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.diaodan_layout);
		init();
	}
	private void getData(){
//		danNum = edDanNum.getText().toString();
//		loginNum = edLoginNum.getText().toString();
//		sn = edSN.getText().toString();
		startTime = edStartTime.getText().toString();
		endTime = edEndTime.getText().toString();
//		fenlei = sp.getSelectedItem().toString();
		if (startTime.length()<8 || endTime.length()<8) {
			T.ss("请输入符合规范的时间格式");
			return;
		}else{
			queryDiaodan();
		}
		
	}
	
	
	
	private void init(){
		title = (CommonTitleBar) findViewById(R.id.titlebar_diaodan);
		title.setActName("调单查询");
		title.setCanClickDestory(this, true);
//		edDanNum = (EditText) findViewById(R.id.ed_diaodan_danNum);
//		edLoginNum = (EditText) findViewById(R.id.ed_diaodan_loginName);
//		edSN = (EditText) findViewById(R.id.ed_diaodan_sn);
		edStartTime = (EditText) findViewById(R.id.ed_diaodan_startTime);
		edEndTime = (EditText) findViewById(R.id.ed_diaodan_endTime);
//		sp = (Spinner) findViewById(R.id.sp_diaodan_zt);
		rg = (RadioGroup) findViewById(R.id.rg_diaodan);
		rg.setOnCheckedChangeListener(this);
		findViewById(R.id.btn_diaodan_query).setOnClickListener(this);
		
	}
	
//	/**
//	 * 添加 设置spinner
//	 */
//	private void addSpinner() {
//		listSpinner = new ArrayList<String>();
//		
//		    listSpinner.add(getResources().getString(R.string.fenrunType));
//			listSpinner.add(getResources().getString(R.string.yidongzhifu));
//			listSpinner.add(getResources().getString(R.string.xianxiajiaoyi));
//			listSpinner.add(getResources().getString(R.string.weixinzhifu));
//			listSpinner.add(getResources().getString(R.string.zhifubao));
//			listSpinner.add(getResources().getString(R.string.baiduqianbao));
//			listSpinner.add(getResources().getString(R.string.suningyifubao));
//		
//		ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this,
//				android.R.layout.simple_list_item_1, listSpinner);
//		sp.setAdapter(arrayAdapter);
//		
//		fenlei = sp.getSelectedItem().toString();
//	}
	
	@Override
	public void onClick(View arg0) {
		// TODO Auto-generated method stub
		queryDiaodan();
	}
	@Override
	public void onCheckedChanged(RadioGroup arg0, int arg1) {
		// TODO Auto-generated method stub
		switch (arg1) {
		case R.id.rb_diaodan_1:
			type = "";
			break;
		case R.id.rb_diaodan_2:
			type = "";
			
			break;

		default:
			break;
		}
	}
	
	/**
	 * 调单查询
	 */
	private void queryDiaodan() {
		Log.i("result", "----dd-----------");
		
		RequestParams params = new RequestParams();
		HashMap<String, String> map = new HashMap<String, String>();

		map.put("username", MApplication.mSharedPref.getSharePrefString(SharedPrefConstant.USERNAME));
		map.put("intime",startTime);
		map.put("outtime",endTime);
		map.put("pageSize","10");
		map.put("page","1");

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
		String url = MyUrls.ORDTRLIST;

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
						
						JSONObject obj;
						try {
							obj = new JSONObject(str);
							
						} catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
//				
					}
				});
	}

}
