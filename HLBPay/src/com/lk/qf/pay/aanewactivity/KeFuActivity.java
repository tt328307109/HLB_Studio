package com.lk.qf.pay.aanewactivity;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.http.entity.StringEntity;
import org.json.JSONException;
import org.json.JSONObject;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView.Adapter;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.ListView;
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
import com.lk.qf.pay.adapter.HelpExpandableListAdapter;
import com.lk.qf.pay.adapter.KefuListAdapter;
import com.lk.qf.pay.beans.KefuProblemInfo;
import com.lk.qf.pay.beans.OrderInfo;
import com.lk.qf.pay.golbal.MApplication;
import com.lk.qf.pay.golbal.MyUrls;
import com.lk.qf.pay.sharedpref.SharedPrefConstant;
import com.lk.qf.pay.tool.T;
import com.lk.qf.pay.wedget.CommonTitleBar;
import com.lk.qf.pay.wedget.customdialog.AlertDialog;

public class KeFuActivity extends BaseActivity implements OnClickListener {

	private EditText edMess;
	// private TextView tvZhuangTai;
	private String message;
	private CommonTitleBar title;
	private TextView tvPhone2, tvPhone3;
	private List<KefuProblemInfo> list;
	private ListView lv;
	private KefuListAdapter adapter;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);

		setContentView(R.layout.kefu_layout);
		init();
	}

	private void init() {
		edMess = (EditText) findViewById(R.id.ed_kefu);
		findViewById(R.id.btn_kefu_tijiao).setOnClickListener(this);
//		findViewById(R.id.tv_gsPhone).setOnClickListener(this);// 公司
		findViewById(R.id.tv_zhPhone).setOnClickListener(this);// 售后部
		findViewById(R.id.tv_jsPhone).setOnClickListener(this);// 技术部
		// tvZhuangTai = (TextView) findViewById(R.id.tv_kefu_zt);
		list = new ArrayList<KefuProblemInfo>();
		lv = (ListView) findViewById(R.id.lv_kefu);
		adapter = new KefuListAdapter(this, list);
		lv.setAdapter(adapter);
		title = (CommonTitleBar) findViewById(R.id.titlebar_kefu);
		title.setActName("客户服务");
		title.setCanClickDestory(this, true);
		getHistoricalProblem();
		
	}

	@Override
	public void onClick(View arg0) {
		// TODO Auto-generated method stub
		switch (arg0.getId()) {
		case R.id.btn_kefu_tijiao:
			message = edMess.getText().toString();
			if (!message.equals("")) {
				send();
				getHistoricalProblem();
			}else{
				T.ss("请输入您的需求");
			}
			break;
		case R.id.tv_zhPhone:
			dialTelephone(getResources().getString(R.string.gs_phone));

			break;
		case R.id.tv_jsPhone:
			dialTelephone(getResources().getString(R.string.js_phone));

			break;

		default:
			break;
		}

	}
	
	

	private void send() {

		RequestParams params = new RequestParams();
		String url = MyUrls.MEROPINIONADD;
		showLoadingDialog();
		Map<String, String> map = new HashMap<String, String>();
		map.put("username", MApplication.mSharedPref
				.getSharePrefString(SharedPrefConstant.USERNAME));
		map.put("problem", message);
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
				dismissLoadingDialog();
				if (arg0.getExceptionCode() == 0) {
					T.ss("请检查网络");
				} else {

					T.ss("操作超时");
				}
			}

			@Override
			public void onSuccess(ResponseInfo<String> response) {
				// TODO Auto-generated method stub
				String code = "";
				String message = "";
				String str = response.result;
				Log.i("result", "----提交成功----s-------" + str);
				try {
					JSONObject obj = new JSONObject(str);
					code = obj.optString("CODE");
					message = obj.optString("MESSAGE");

				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				if (code.equals("00")) {
					T.ss("提交成功,您的需求对我很重要,谢谢!");
					edMess.setText("");
					// tvZhuangTai.setText("处理中");
					// tvZhuangTai.setBackground(getResources().getDrawable(R.drawable.chulizhong));
				} else {
					T.ss(message);
				}
				dismissLoadingDialog();
			}
		});
	}
	private void getHistoricalProblem() {
		
		RequestParams params = new RequestParams();
		String url = MyUrls.MEROPINIONLIST;
		showLoadingDialog();
		Map<String, String> map = new HashMap<String, String>();
		map.put("username", MApplication.mSharedPref
				.getSharePrefString(SharedPrefConstant.USERNAME));
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
				dismissLoadingDialog();
			}
			
			@Override
			public void onSuccess(ResponseInfo<String> response) {
				// TODO Auto-generated method stub
				String code = "";
				String message = "";
				String str = response.result;
				Log.i("result", "----提交成功----s-------" + str);
				try {
					JSONObject obj = new JSONObject(str);
					code = obj.optString("CODE");
					message = obj.optString("MESSAGE");
					
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				if (code.equals("00")) {
					jsonDetail(str);
					// tvZhuangTai.setText("处理中");
					// tvZhuangTai.setBackground(getResources().getDrawable(R.drawable.chulizhong));
				} 
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
			int count = obj.optInt("count");
			List<KefuProblemInfo> list1 = new ArrayList<KefuProblemInfo>();
			if (count > 0) {
				for (int i = 0; i < count; i++) {

					String addTime = obj.optJSONArray("date").optJSONObject(i)
							.optString("addtime");
					String problem = obj.optJSONArray("date").optJSONObject(i)
							.optString("problem");
					String state = obj.optJSONArray("date").optJSONObject(i)
							.optString("state");
					
					KefuProblemInfo info = new KefuProblemInfo();
					info.setAddTime(addTime);
					info.setProblem(problem);
					info.setState(state);
					list1.add(info);
				}
				Log.i("result", "-----------list1------"+list1.size());
				Log.i("result", "-----------list------"+list.size());
				list.clear();
				list.addAll(list1);
				Log.i("result", "-----------list-22-----"+list.size());
				adapter.sendSata(list);
				adapter.notifyDataSetChanged();
			}

		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
	
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		
	}


	/**
	 * 弹出客服电话对话框
	 */
	private void dialTelephone(String phone) {
		final String phoneNum = phone;
		new AlertDialog(KeFuActivity.this).builder().setTitle("提示")
		.setMsg("服务热线:" + phone)
		.setPositiveButton("拨打", new OnClickListener() {
			@Override
			public void onClick(View v) {
				call(phoneNum);
			}
		}).setNegativeButton("取消", new OnClickListener() {
			@Override
			public void onClick(View v) {

			}
		}).show();
		
	}

	/**
	 * 进入拨打电话页面
	 */
	private void call(String phone) {
		Intent it = new Intent();
		it.setAction("android.intent.action.DIAL");
		it.setData(Uri.parse("tel:"+phone));
		startActivity(it);
	}
}
