package com.lk.qf.pay.posloan;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import com.lk.bhb.pay.R;
import com.lk.qf.pay.activity.BaseActivity;
import com.lk.qf.pay.activity.ProtocolActivity;
import com.lk.qf.pay.beans.LicaiNewGoodsInfo;
import com.lk.qf.pay.tool.T;
import com.lk.qf.pay.wedget.CommonTitleBar;

public class ApplyPolyLoanActivity extends BaseActivity implements
		OnClickListener {

	private TextView tvEdu;
	private EditText edloanAccount;
	private CheckBox cb;
	private String account = "",sxAccount="";
	private LicaiNewGoodsInfo info;
	private CommonTitleBar title;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.apply_loan_fragment_layout);

		tvEdu = (TextView) findViewById(R.id.tv_apply_poly_loan_eduAccount);
		edloanAccount = (EditText) findViewById(R.id.ed_apply_poly_loan_account);
		cb = (CheckBox) findViewById(R.id.cb_poly_loan_agree);
		findViewById(R.id.tv_poly_loan_agree).setOnClickListener(this);
		findViewById(R.id.btn_apply_polyloans).setOnClickListener(this);
		title = (CommonTitleBar) findViewById(R.id.titlebar_posLoan_apply);
		title.setActName("申请保理贷");
		title.setCanClickDestory(this, true);
		Intent intent = getIntent();
		if (intent!=null) {
			info=intent.getParcelableExtra("info");
			if (info!=null) {
				sxAccount = info.getBuyAccount();
				tvEdu.setText(sxAccount);
			}
		}
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		edloanAccount.setText("");
//		applyPolyLoan();
	}

	@Override
	public void onClick(View arg0) {
		// TODO Auto-generated method stub

		switch (arg0.getId()) {
		case R.id.tv_poly_loan_agree:
			Intent it2 = new Intent(ApplyPolyLoanActivity.this,
					ProtocolActivity.class);
			it2.putExtra("title", "保理贷产品服务协议");
			startActivity(it2);
			break;
		case R.id.btn_apply_polyloans:
			account = edloanAccount.getText().toString();
			if (account.equals("")) {
				T.ss("请输入贷款金额");
				return;
			}else{
				double sxAcc = Double.parseDouble(sxAccount);
				double acc = Double.parseDouble(account);
				if (acc>sxAcc) {
					T.ss("贷款金额不能大于授信金额");
					return;
				}
			}
			if (!cb.isChecked()) {
				T.ss("请勾选同意协议");
				return;
			}
			Intent intent = new Intent(ApplyPolyLoanActivity.this,
					PosloanSignatureActivity.class);
			intent.setAction(PosloanSignatureActivity.POLY_LOAN);
			intent.putExtra("account", account);
			intent.putExtra("lcid", info.getCrodId());
			startActivity(intent);
			finish();

			break;

		default:
			break;
		}
	}

//	/**
//	 * 查询授信额度
//	 */
//	private void applyPolyLoan() {
//
//		RequestParams params = new RequestParams();
//		String url = MyUrls.LINESCOUNT;
//		Map<String, String> map = new HashMap<String, String>();
//		map.put("username", MApplication.mSharedPref
//				.getSharePrefString(SharedPrefConstant.USERNAME));
//		map.put("token", MApplication.mSharedPref
//				.getSharePrefString(SharedPrefConstant.TOKEN));
//
//		String json = JSON.toJSONString(map);
//		Log.i("result", "----ddd----s-------" + json);
//		try {
//			StringEntity bodyEntity = new StringEntity(json, "UTF-8");
//			params.setBodyEntity(bodyEntity);
//		} catch (UnsupportedEncodingException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//
//		HttpUtils utils = new HttpUtils();
//		utils.send(HttpMethod.POST, url, params, new RequestCallBack<String>() {
//
//			@Override
//			public void onFailure(HttpException arg0, String arg1) {
//			}
//
//			@Override
//			public void onSuccess(ResponseInfo<String> response) {
//				// TODO Auto-generated method stub
//				String code = "";
//				String message = "";
//
//				String str = response.result;
//				Log.i("result", "----str----s-------" + str);
//				JSONObject obj = null;
//				try {
//					obj = new JSONObject(str);
//					code = obj.optString("CODE");
//					message = obj.optString("MESSAGE");
//
//				} catch (JSONException e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				}
//
//				if (code.equals("00")) {
//					tvEdu.setText(obj.optString("linesCount"));
//				}
//			}
//		});
//	}

}
