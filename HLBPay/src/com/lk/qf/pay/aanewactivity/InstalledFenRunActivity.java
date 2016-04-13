package com.lk.qf.pay.aanewactivity;

import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import org.apache.http.entity.StringEntity;
import org.json.JSONException;
import org.json.JSONObject;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
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
import com.lk.qf.pay.activity.LoginActivity;
import com.lk.qf.pay.dialog.MonPickerDialog;
import com.lk.qf.pay.golbal.MApplication;
import com.lk.qf.pay.golbal.MyUrls;
import com.lk.qf.pay.sharedpref.SharedPrefConstant;
import com.lk.qf.pay.tool.T;
import com.lk.qf.pay.utils.MyMdFivePassword;
import com.lk.qf.pay.wedget.CommonTitleBar;
import com.lk.qf.pay.wedget.view.PassWdDialog;
import com.lk.qf.pay.wedget.view.PayListener;

public class InstalledFenRunActivity extends BaseActivity implements
		OnClickListener, PayListener {

	private TextView tvInstallFRJJ1, tvInstallFRJJ2, tvNum1, tvNum2, tvNewNum1,
			tvNewNum2;
	private EditText edTixian;
	private String strInstallFR1 = "", strInstallFR2 = "", strNum1, strNum2,
			strNewNum1, strNewNum2;
	private String action = "";
	private String strTitle = "";
	private String tixianAccount = "", pwd = "", allTotalAccount = "";
	private CommonTitleBar title;

	private Date date;
	private SimpleDateFormat sdf;
	private Calendar cal;
	private AlertDialog.Builder builder;
	private boolean timeIsOk = true;
	private static int TYPE = 0;// 起始时间
	private View view;
	private MonPickerDialog datePicker;
	/**
	 * 付款对话框
	 */
	private PassWdDialog mPassWdDialog;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.installed_fenrun1_layout);
		init();
	}

	private void init() {
		Intent intent = getIntent();
		if (intent != null) {
			action = intent.getAction();
			if (action.equals("zj")) {
				strTitle = "装机收益";
			} else {
				strTitle = "交易分润";
			}
		}
		title = (CommonTitleBar) findViewById(R.id.titlebar_installen_title1);
		title.setActName(strTitle);
		title.setCanClickDestory(this, true);

		tvInstallFRJJ1 = (TextView) findViewById(R.id.tv_installen_zj1_account);
		tvInstallFRJJ2 = (TextView) findViewById(R.id.tv_installen_zj2_account);
		tvNum1 = (TextView) findViewById(R.id.tv_installen_zj_num);
		tvNum2 = (TextView) findViewById(R.id.tv_installen_zj2_num);
		tvNewNum1 = (TextView) findViewById(R.id.tv_installen_zj_new_num);
		tvNewNum2 = (TextView) findViewById(R.id.tv_installen_zj2_new_num);
		edTixian = (EditText) findViewById(R.id.ed_zj_tixian);

		findViewById(R.id.btn_zj_list_query).setOnClickListener(this);
		findViewById(R.id.btn_zj_tixian).setOnClickListener(this);
		findViewById(R.id.btn_zj_list_query_tixian).setOnClickListener(this);
		// date = new Date();
		// sdf = new SimpleDateFormat("yyyyMM");
		// startTime = sdf.format(date);
		// endTime = sdf.format(date);
		queryFenrun();
	}

	@Override
	public void onClick(View arg0) {
		// TODO Auto-generated method stub

		switch (arg0.getId()) {

		case R.id.btn_zj_list_query:
			Intent intent = new Intent(InstalledFenRunActivity.this,
					InstalledFenRunListActivity.class);
			intent.setAction(action);
			startActivity(intent);
			break;
		case R.id.btn_zj_tixian:
			tixianAccount = edTixian.getText().toString();
			if (tixianAccount.equals("")) {
				T.ss("请输入提现金额");
				return;
			}
			float account = Float.parseFloat(tixianAccount);
			if (account < 10) {
				T.ss("提现金额必须大于10元");
				return;
			}
			mPassWdDialog = PassWdDialog.getInstance(InstalledFenRunActivity.this,
					tixianAccount,PassWdDialog.YUAN_MARK);
			mPassWdDialog.setPayListener(InstalledFenRunActivity.this);
			mPassWdDialog.show();
			break;
		case R.id.btn_zj_list_query_tixian:
			Intent intent2 = new Intent(InstalledFenRunActivity.this,
					TiXianListActivity.class);
			intent2.putExtra("payType", "ZJT1");
			startActivity(intent2);
			break;

		default:
			break;
		}

	}

	/**
	 * 分润查询
	 */
	private void queryFenrun() {
		Log.i("result", "----dd-----------");
		showLoadingDialog();
		RequestParams params = new RequestParams();
		HashMap<String, String> map = new HashMap<String, String>();

		map.put("username", MApplication.mSharedPref
				.getSharePrefString(SharedPrefConstant.USERNAME));
		map.put("token", MApplication.mSharedPref
				.getSharePrefString(SharedPrefConstant.TOKEN));
		map.put("begintime", "");
		map.put("endtime", "");
		map.put("type", "00");

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
		String url = "";
		if (action.equals("zj")) {// 装机
			url = MyUrls.ZHUANGJIJIANG;
		} else {
			url = MyUrls.MERFENRUN;
		}

		httpUtils.send(HttpMethod.POST, url, params,
				new RequestCallBack<String>() {

					@Override
					public void onFailure(HttpException arg0, String arg1) {
						// TODO Auto-generated method stub
						dismissLoadingDialog();
						T.ss("操作超时");
					}

					@Override
					public void onSuccess(ResponseInfo<String> response) {
						// TODO Auto-generated method stub

						String str = response.result;
						Log.i("result", "----ddd-----------" + str);
						String code = "";
						String message = "";
						String fenrun1 = "";
						String fenrun2 = "";
						JSONObject obj;

						try {
							obj = new JSONObject(str);
							code = obj.optString("CODE");
							message = obj.optString("MESSAGE");
							if (code.equals("00")) {
								if (action.equals("zj")) {// 装机
									allTotalAccount = obj
											.optString("totalmoney");// 总奖金额
									edTixian.setHint("累计总金额 " + allTotalAccount);
									fenrun1 = obj.optString("managezjtotal");// 一级
									fenrun2 = obj.optString("zjtotal");// 二级
									strNum1 = obj.optString("managecount");// 一级数量
									strNum2 = obj.optString("zjcount");// 二级数量
									strNewNum1 = obj
											.optString("managenewcount");// 一级新增数量
									strNewNum2 = obj.optString("zjnewcount");// 二级新增数量
									tvNum1.setText(strNum1);
									tvNum2.setText(strNum2);
									tvNewNum1.setText(strNewNum1);
									tvNewNum2.setText(strNewNum2);
								} else {
									fenrun1 = obj.optString("tradetotal");// 交易分润
									fenrun2 = obj.optString("managetotal");// 管理分润
								}

								tvInstallFRJJ1.setText(fenrun1);
								tvInstallFRJJ2.setText(fenrun2);
							} else {
								T.ss(message);
							}
						} catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						dismissLoadingDialog();
					}
				});
	}

	/**
	 * 提现
	 */
	private void sendTixian() {

		RequestParams params = new RequestParams();
		showLoadingDialog();
		Map<String, String> map = new HashMap<String, String>();
		map.put("username", MApplication.mSharedPref
				.getSharePrefString(SharedPrefConstant.USERNAME));

		map.put("pwd", MyMdFivePassword.MD5(MyMdFivePassword.MD5(pwd)));
		map.put("money", tixianAccount);
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
		utils.send(HttpMethod.POST, MyUrls.ZJTIXIAN, params,
				new RequestCallBack<String>() {

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
						Log.i("result", "----查询成功----s-------" + str);
						try {
							JSONObject obj = new JSONObject(str);
							code = obj.optString("CODE");
							message = obj.optString("MESSAGE");

						} catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}

						if (code.equals("00")) {
							edTixian.setText("");
							queryFenrun();
							// getUser();
							// finish();
						}

						if (message.equals(getResources().getString(
								R.string.login_outtime))) {
							Intent intent = new Intent(
									InstalledFenRunActivity.this,
									LoginActivity.class);
							startActivity(intent);
							finish();
						}
						T.ss(message);
						dismissLoadingDialog();
					}
				});
	}

	@Override
	public void sure(String password) {
		// TODO Auto-generated method stub
		mPassWdDialog.dismiss();
		mPassWdDialog = null;
		// payTextView.setText(password);
		pwd = password;
		sendTixian();
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
