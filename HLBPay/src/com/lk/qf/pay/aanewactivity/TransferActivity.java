package com.lk.qf.pay.aanewactivity;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
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
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.SimpleAdapter;
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
import com.lk.qf.pay.utils.MyMdFivePassword;
import com.lk.qf.pay.utils.MyUtilss;
import com.lk.qf.pay.wedget.CommonTitleBar;
import com.lk.qf.pay.wedget.view.PassWdDialog;
import com.lk.qf.pay.wedget.view.PayListener;

public class TransferActivity extends BaseActivity implements OnClickListener,
		PayListener {

	private CommonTitleBar title;
	private EditText edAccount, edRemark;
	private TextView tvName;
	private AutoCompleteTextView autoPhone;
	private String transferPhone = "", transferName = "", transferAccount = "",
			remark = "";
	private String state = "", pwd = "";// 对方商户状态//密码
	private String userName;// 本人账户
	private CharSequence temp = "";
	private ArrayList<HashMap<String, String>> list;
	private HashMap<String, String> item;
	private static final int REQUEST_GET_PHONE = 0;
	private PassWdDialog mPassWdDialog;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.transfer_layout);
		init();
	}

	private void init() {
		userName = MApplication.mSharedPref
				.getSharePrefString(SharedPrefConstant.USERNAME);
		title = (CommonTitleBar) findViewById(R.id.titlebar_transfer_title);
		title.setActName("转账");
		title.setCanClickDestory(this, true);

		autoPhone = (AutoCompleteTextView) findViewById(R.id.auto_transfer_phoneNum);
		edAccount = (EditText) findViewById(R.id.ed_transfer_account);
		edRemark = (EditText) findViewById(R.id.ed_transfer_remark);
		tvName = (TextView) findViewById(R.id.tv_transfer_name);
		findViewById(R.id.btn_transfer_next).setOnClickListener(this);
		findViewById(R.id.ib_transfer_refresh).setOnClickListener(this);
		findViewById(R.id.btn_transfer_mx).setOnClickListener(this);
		findViewById(R.id.ib_transfer_phone).setOnClickListener(this);
		list = new ArrayList<HashMap<String, String>>();
		SimpleAdapter av = new SimpleAdapter(this, list,
				R.layout.main_item_three_line_row_layout, new String[] {
						"phone", "name" }, new int[] { R.id.tv_com_userPhone,
						R.id.tv_com_userName });
		autoPhone.setAdapter(av);
		autoPhone.setThreshold(1);

		queryCommonlyUserPhone();// 查询常用用户列表
		autoPhone.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				TextView tv = (TextView) arg1
						.findViewById(R.id.tv_com_userPhone);
				autoPhone.setText(tv.getText().toString() + "");
				// autoPhone.setSelection((autoPhone.getText().toString())
				// .length());
			}

		});

		/**
		 * 监听edittext
		 */
		autoPhone.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence arg0, int arg1, int arg2,
					int arg3) {
				// TODO Auto-generated method stub
				temp = arg0;
			}

			@Override
			public void beforeTextChanged(CharSequence arg0, int start,
					int before, int count) {
				// TODO Auto-generated method stub

			}

			@Override
			public void afterTextChanged(Editable arg0) {
				// TODO Auto-generated method stub

				if (temp.length() == 11) {
					transferPhone = temp.toString();
					if (transferPhone.equals(userName)) {
						T.ss("不能向本账户转账");
					}
					getUserName();
				}
			}
		});

	}

	@Override
	public void onClick(View arg0) {
		// TODO Auto-generated method stub
		switch (arg0.getId()) {
		case R.id.ib_transfer_refresh:
			if (transferPhone.equals("")) {
				T.ss("请输入对方账户");
				return;
			}
			getUserName();
			break;
		case R.id.ib_transfer_phone:
			Intent intent1 = new Intent(TransferActivity.this,
					AddCommonlyUsedUserActivity.class);
			startActivityForResult(intent1, REQUEST_GET_PHONE);
			break;
		case R.id.btn_transfer_next:
			if (MyUtilss.noPayYajin()) {
				T.ss("商户未缴纳押金");
				return;
			}
			getData();
			break;
		case R.id.btn_transfer_mx:
			Intent intent = new Intent(TransferActivity.this,
					TransferListActivity.class);
			startActivity(intent);
			break;

		default:
			break;
		}

	}

	private void addItems(String name, String phone) {
		item = new HashMap<String, String>();
		item.put("name", name);
		item.put("phone", phone);
		list.add(item);
	}

	private void getData() {
		transferPhone = autoPhone.getText().toString();
		transferAccount = edAccount.getText().toString();
		remark = edRemark.getText().toString();
		if (transferPhone.equals("")) {
			T.ss("请输入对方账户");
			return;
		}
		if (transferPhone.equals(userName)) {
			T.ss("不能向本账户转账");
			return;
		}
		if (transferName.equals("")) {
			T.ss("请确认对方姓名");
			return;
		}
		if (!state.equals("en")) {
			T.ss("对方账户处于非正常状态,暂不能向对方转账!");
			return;
		}

		if (transferAccount.equals("")) {
			T.ss("请输入转账金额");
			return;
		} else {
			double money = Double.parseDouble(transferAccount);
			if (money == 0) {
				T.ss("转账金额不能为0");
				return;
			}
		}
		if (remark.equals("")) {
			T.ss("请输入备注");
			return;
		}
		mPassWdDialog = PassWdDialog.getInstance(TransferActivity.this,
				transferAccount, "¥");
		mPassWdDialog.setPayListener(TransferActivity.this);
		mPassWdDialog.show();

	}

	/**
	 * 查询用户名
	 */
	private void getUserName() {

		if (!transferPhone.startsWith("1")) {
			return;
		}
		RequestParams params = new RequestParams();
		HashMap<String, String> map = new HashMap<String, String>();

		map.put("username", MApplication.mSharedPref
				.getSharePrefString(SharedPrefConstant.USERNAME));
		map.put("endusername", transferPhone);
		map.put("token", MApplication.mSharedPref
				.getSharePrefString(SharedPrefConstant.TOKEN));

		String json = JSON.toJSONString(map);
		Log.i("result", "----ddd--c---------" + json);
		try {
			StringEntity bodyEntity = new StringEntity(json, "UTF-8");
			params.setBodyEntity(bodyEntity);
		} catch (UnsupportedEncodingException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		HttpUtils httpUtils = new HttpUtils();
		String url = MyUrls.USERINFO;

		httpUtils.send(HttpMethod.POST, url, params,
				new RequestCallBack<String>() {

					@Override
					public void onFailure(HttpException arg0, String arg1) {
						// TODO Auto-generated method stub
						// T.ss("转账失败!" + arg0.getExceptionCode());
					}

					@Override
					public void onSuccess(ResponseInfo<String> response) {
						// TODO Auto-generated method stub

						String str = response.result;
						String code = "";
						String message = "";
						String name = "";

						Log.i("result", "----ddd-----------" + str);

						JSONObject obj;
						try {
							obj = new JSONObject(str);
							code = obj.optString("CODE");
							message = obj.optString("MESSAGE");
							if (code.equals("00")) {
								transferName = obj.optString("niname");
								state = obj.optString("state");
								if (!state.equals("en")) {
									T.ss("对方账户处于非正常状态,暂不能向对方转账!");
								}
								tvName.setText(transferName);
							} else {
								T.ss(message);
							}
						} catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				});
	}

	/**
	 * 查询常用转账人列表
	 */
	private void queryCommonlyUserPhone() {

		RequestParams params = new RequestParams();
		HashMap<String, String> map = new HashMap<String, String>();

		map.put("username", MApplication.mSharedPref
				.getSharePrefString(SharedPrefConstant.USERNAME));
		map.put("pagesize", "20");
		map.put("page", "1");
		map.put("token", MApplication.mSharedPref
				.getSharePrefString(SharedPrefConstant.TOKEN));

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
		String url = MyUrls.COMMONUSER;

		httpUtils.send(HttpMethod.POST, url, params,
				new RequestCallBack<String>() {

					@Override
					public void onFailure(HttpException arg0, String arg1) {
						// TODO Auto-generated method stub
						// T.ss("转账失败!" + arg0.getExceptionCode());
					}

					@Override
					public void onSuccess(ResponseInfo<String> response) {
						// TODO Auto-generated method stub

						String str = response.result;
						String code = "";
						String message = "";

						Log.i("result", "----ddd-----------" + str);

						JSONObject obj;
						try {
							obj = new JSONObject(str);
							code = obj.optString("CODE");
							message = obj.optString("MESSAGE");
							if (code.equals("00")) {
								jsonDetail(str);

							} else {
								T.ss(message);
							}
						} catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
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
			int count = obj.optInt("COUNT");
			Log.i("result", "---------Count-------" + count);
			if (count > 0) {

				for (int i = 0; i < count; i++) {

					String name = obj.optJSONArray("DATA").optJSONObject(i)
							.optString("comm_name");
					String phone = obj.optJSONArray("DATA").optJSONObject(i)
							.optString("comm_username");
					addItems(name, phone);
				}
			}

		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	/**
	 * 转账
	 */
	private void transferMoney() {

		RequestParams params = new RequestParams();
		HashMap<String, String> map = new HashMap<String, String>();

		map.put("username", MApplication.mSharedPref
				.getSharePrefString(SharedPrefConstant.USERNAME));
		map.put("pwd", MyMdFivePassword.MD5(MyMdFivePassword.MD5(pwd)));
		map.put("endusername", transferPhone);
		map.put("total", transferAccount);// 金额
		map.put("remark", remark);
		map.put("token", MApplication.mSharedPref
				.getSharePrefString(SharedPrefConstant.TOKEN));

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
		String url = MyUrls.TRANSFER_OP;

		httpUtils.send(HttpMethod.POST, url, params,
				new RequestCallBack<String>() {

					@Override
					public void onFailure(HttpException arg0, String arg1) {
						// TODO Auto-generated method stub
						// T.ss("转账失败!" + arg0.getExceptionCode());
					}

					@Override
					public void onSuccess(ResponseInfo<String> response) {
						// TODO Auto-generated method stub

						String str = response.result;
						String code = "";
						String message = "";
						String name = "";
						Log.i("result", "----ddd-----------" + str);

						JSONObject obj;
						try {
							obj = new JSONObject(str);
							code = obj.optString("CODE");
							message = obj.optString("MESSAGE");
							if (code.equals("00")) {
								T.ss("转账成功");
								edAccount.setText("");
								edRemark.setText("");
								tvName.setText("");
								autoPhone.setText("");
							} else {
								T.ss(message);
							}
						} catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				});
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);

		switch (requestCode) {
		case REQUEST_GET_PHONE:
			if (resultCode == Activity.RESULT_OK) {
				transferPhone = data.getStringExtra("phone");
				Log.i("result", "--------dddss----" + transferPhone);
				autoPhone.setText(transferPhone);
			}
			break;

		default:
			break;
		}
	}

	@Override
	public void sure(String password) {
		// TODO Auto-generated method stub
		mPassWdDialog.dismiss();
		mPassWdDialog = null;
		pwd = password;
		transferMoney();
	}

	@Override
	public void cacel() {
		// TODO Auto-generated method stub
	}

	@Override
	public void close() {
		// TODO Auto-generated method stub
		
	}

}
