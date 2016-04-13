package com.lk.qf.pay.posloan;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

import org.apache.http.entity.StringEntity;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;

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
import com.lk.qf.pay.utils.ExpresssoinValidateUtil;
import com.lk.qf.pay.utils.StringUtils;
import com.lk.qf.pay.wedget.CommonTitleBar;

public class PosLoanFriendsInfoActivity extends BaseActivity implements
		OnClickListener {
	private CommonTitleBar title;
	private EditText edName1, edName2, edName3, edPhoneNum1, edPhoneNum2,
			edPhoneNum3;
	private String name1 = "", name2 = "", name3 = "", phoneNum1 = "",
			phoneNum2 = "", phoneNum3 = "";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.friends_message_layout);
		init();
	}

	private void init() {
		title = (CommonTitleBar) findViewById(R.id.titlebar_posLoan_friends_message);
		title.setActName("亲友信息");
		title.setCanClickDestory(this, true);
		edName1 = (EditText) findViewById(R.id.ed_posLoan_friends_name1);
		edName2 = (EditText) findViewById(R.id.ed_posLoan_friends_name2);
		edName3 = (EditText) findViewById(R.id.ed_posLoan_friends_name3);
		edPhoneNum1 = (EditText) findViewById(R.id.ed_posLoan_friends_num1);
		edPhoneNum2 = (EditText) findViewById(R.id.ed_posLoan_friends_num2);
		edPhoneNum3 = (EditText) findViewById(R.id.ed_posLoan_friends_num3);
		// edRelationship1 = (EditText)
		// findViewById(R.id.ed_posLoan_friends_guanxi1);
		// edRelationship2 = (EditText)
		// findViewById(R.id.ed_posLoan_friends_guanxi2);
		// edRelationship3 = (EditText)
		// findViewById(R.id.ed_posLoan_friends_guanxi3);

		findViewById(R.id.btn_friends_message_next).setOnClickListener(this);
	}

	@Override
	public void onClick(View arg0) {
		// TODO Auto-generated method stub
		getData();
	}

	private void getData() {
		name1 = edName1.getText().toString();
		name2 = edName2.getText().toString();
		name3 = edName3.getText().toString();
		phoneNum1 = edPhoneNum1.getText().toString();
		phoneNum2 = edPhoneNum2.getText().toString();
		phoneNum3 = edPhoneNum3.getText().toString();
		// relationship1 = edRelationship1.getText().toString();
		// relationship2 = edRelationship2.getText().toString();
		// relationship3 = edRelationship3.getText().toString();

		if (name1 == null || name1.equals("")) {
			T.ss("请输入第一联系人姓名");
			return;
		}
		if (phoneNum1 == null || phoneNum1.equals("")) {
			T.ss("请输入第一联系人联系电话");
			return;
		}
		// if (relationship1 == null || relationship1.equals("")) {
		// T.ss("请输入与第一联系人的关系");
		// return;
		// }

		if (name2 == null || name2.equals("")) {
			T.ss("请输入第二联系人姓名");
			return;
		}
		if (phoneNum2 == null || phoneNum2.equals("")) {
			T.ss("请输入第二联系人联系电话");
			return;
		}
		// if (relationship2 == null || relationship2.equals("")) {
		// T.ss("请输入与第二联系人的关系");
		// return;
		// }
		if (name3 == null || name3.equals("")) {
			T.ss("请输入第三联系人姓名");
			return;
		}
		if (phoneNum3 == null || phoneNum3.equals("")) {
			T.ss("请输入第三联系人联系电话");
			return;
		}
		if (!ExpresssoinValidateUtil.isMobilePhone(phoneNum1)
				|| !ExpresssoinValidateUtil.isMobilePhone(phoneNum2)
				|| !ExpresssoinValidateUtil.isMobilePhone(phoneNum3)) {
			T.ss("请输入合法的手机号码");
			return;
		}
		
		// if (relationship3 == null || relationship3.equals("")) {
		// T.ss("请输入与第三联系人的关系");
		// return;
		// }

		sendMerPoslan();
	}

	/**
	 * 信息上传
	 */
	private void sendMerPoslan() {
		showLoadingDialog();
		RequestParams params = new RequestParams();

		String url = MyUrls.POSLOANADD;

		Map<String, String> map = new HashMap<String, String>();
		map.put("username", MApplication.mSharedPref
				.getSharePrefString(SharedPrefConstant.USERNAME));
		map.put("sex", "");
		map.put("nation", "");// 国籍
		map.put("birth", "");// 出生时间
		map.put("idtype", "");// 证件类型
		map.put("clazz", "");// 现住地址
		map.put("unit", "");// 单位
		map.put("site", "");// 婚姻状态
		map.put("sitename", "");// 民族
		map.put("unitclazz", "");// 单位地址
		map.put("unitphone", "");// 单位电话
		map.put("contact1phone", phoneNum1);// 第一联系人电话
		map.put("contact1name", name1);// 第一联系人姓名
		map.put("contact2name", name2);// 2姓名
		map.put("contact2phone", phoneNum2);// 2电话
		map.put("contact3phone", phoneNum3);
		map.put("contact3name", name3);
		map.put("posairimg", "");// 户口本复印件
		map.put("posrailwayimg", "");// 结婚证
		map.put("poscashimg2", "");// 房产证
		map.put("posotherimg2", "");// 单位水电气票据
		map.put("posotherimg3", "");// 住址水电气
		map.put("fathersort", "");// 邮箱

		String json = JSON.toJSONString(map);
		Log.i("result", "----ddd----s--PosLoan-----" + json);
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
				dismissLoadingDialog();
				Log.i("result", "----PosLoan----s---s----" + str);
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

					Intent intent = new Intent(PosLoanFriendsInfoActivity.this,
							PosLoanPhotoActivity.class);
					startActivity(intent);
				}
			}
		});
	}
}
