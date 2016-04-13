package com.lk.qf.pay.posloan;

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

import com.alibaba.fastjson.JSON;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;
import com.lk.bhb.pay.R;
import com.lk.qf.pay.aanewactivity.AddXinyongkaActivity;
import com.lk.qf.pay.aanewactivity.NumberSelectorActivity;
import com.lk.qf.pay.activity.BaseActivity;
import com.lk.qf.pay.golbal.MApplication;
import com.lk.qf.pay.golbal.MyUrls;
import com.lk.qf.pay.sharedpref.SharedPrefConstant;
import com.lk.qf.pay.tool.T;
import com.lk.qf.pay.wedget.CommonTitleBar;

public class PosLoanBorrowingActivity extends BaseActivity implements
		OnClickListener {

	private EditText edPosLoanAccount, edPosloanReason;
	private TextView tvMonth;
	private String account = "", month = "", reason = "", photoName = "";
	private static final int REQUEST_POSLOAN_DATE = 3;
	private static final int REQUEST_POSLOAN_SIGNATURE = 4;
	private CommonTitleBar title;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);

		setContentView(R.layout.borrowing_layout);
		init();
	}

	private void init() {
		title = (CommonTitleBar) findViewById(R.id.titlebar_posLoan_borrowing);
		title.setActName("借款");
		title.setCanClickDestory(this, true);
		edPosLoanAccount = (EditText) findViewById(R.id.ed_posLoan_account);
		edPosloanReason = (EditText) findViewById(R.id.ed_posloan_message);
		tvMonth = (TextView) findViewById(R.id.tv_posLoan_month);
		tvMonth.setOnClickListener(this);
		findViewById(R.id.btn_borrowing_confirm).setOnClickListener(this);
	}

	@Override
	public void onClick(View arg0) {
		// TODO Auto-generated method stub
		switch (arg0.getId()) {
		case R.id.tv_posLoan_month:
			Intent intent1 = new Intent(PosLoanBorrowingActivity.this,
					NumberSelectorActivity.class);
			intent1.putExtra("type", 3);
			startActivityForResult(intent1, REQUEST_POSLOAN_DATE);
			break;
		case R.id.btn_borrowing_confirm:

			account = edPosLoanAccount.getText().toString();
			reason = edPosloanReason.getText().toString();

			if (account.equals("")) {
				T.ss("请输入贷款金额");
				return;
			}
			if (reason.equals("")) {
				T.ss("请输入贷款理由");
				return;
			}
			if (month.equals("")) {
				T.ss("请选择贷款期限");
				return;
			}
			sendMerPoslanMessage();
			
//			Intent intent2 = new Intent(PosLoanBorrowingActivity.this,
//					PosloanSignatureActivity.class);
//			intent2.setAction(PosloanSignatureActivity.FENRUNLOAN);
//			intent2.putExtra("account", account);
//			intent2.putExtra("reason", reason);
//			intent2.putExtra("month", month);
//			startActivity(intent2);
			break;

		default:
			break;
		}

	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);

		switch (requestCode) {
		case REQUEST_POSLOAN_DATE:
			if (resultCode == Activity.RESULT_OK) {

				month = data.getStringExtra("dateNum").substring(0,
						data.getStringExtra("dateNum").length() - 2);
				Log.i("result", "--------dddss----" + month);
				tvMonth.setText(data.getStringExtra("dateNum"));
			}
			break;
		// case REQUEST_POSLOAN_SIGNATURE:
		// if (resultCode == Activity.RESULT_OK) {
		//
		// month = data.getStringExtra("dateNum").substring(0,
		// data.getStringExtra("dateNum").length()-2);
		// Log.i("result", "--------dddss----"+month);
		// tvMonth.setText(data.getStringExtra("dateNum"));
		// }
		// break;

		default:
			break;
		}
	}

	/**
	 * 提交分润贷款信息
	 */
	private void sendMerPoslanMessage() {
		showLoadingDialog();
		RequestParams params = new RequestParams();

		String url = MyUrls.LOANADD;

		Map<String, String> map = new HashMap<String, String>();
		map.put("username", MApplication.mSharedPref
				.getSharePrefString(SharedPrefConstant.USERNAME));
		map.put("money", account);
		map.put("limit", month);// 期限
		map.put("bei1", reason);// 贷款理由
		map.put("signature", "");// 签名
		map.put("state", "");// 状态
		map.put("type", "0");// 操作

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
					Intent intent = new Intent(PosLoanBorrowingActivity.this,
							PosLoanActivity.class);
					startActivity(intent);
					finish();
				}
			}
		});
	}
}