package com.lk.qf.pay.posloan;

import java.io.ByteArrayOutputStream;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

import org.apache.http.Header;
import org.apache.http.entity.StringEntity;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Bitmap.CompressFormat;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;
import com.lk.bhb.pay.R;
import com.lk.pay.communication.AsyncHttpResponseHandler;
import com.lk.qf.pay.aanewactivity.CashInConfimSuperActivity;
import com.lk.qf.pay.activity.BaseActivity;
import com.lk.qf.pay.activity.CashInConfirmActivity;
import com.lk.qf.pay.beans.BasicResponse;
import com.lk.qf.pay.beans.PosData;
import com.lk.qf.pay.fragment.MyPolyLoanFragment;
import com.lk.qf.pay.golbal.MApplication;
import com.lk.qf.pay.golbal.MyUrls;
import com.lk.qf.pay.golbal.Urls;
import com.lk.qf.pay.nfc.NfcPayActivity;
import com.lk.qf.pay.sharedpref.SharedPrefConstant;
import com.lk.qf.pay.signature.SignaturePad;
import com.lk.qf.pay.tool.Logger;
import com.lk.qf.pay.tool.MyHttpClient;
import com.lk.qf.pay.tool.T;
import com.lk.qf.pay.utils.BitmapUtil;
import com.lk.qf.pay.v50.V50CashInConfirmActivity;
import com.lk.qf.pay.wedget.CommonTitleBar;

public class PosloanSignatureActivity extends BaseActivity {
	private SignaturePad mSignaturePad;
	private Button mClearButton, mSaveButton;
	private String data;
	private TextView showText, showText2;
	private String photoName, termType, strImageName, account = "", month = "",
			reason = "";
	private String imagename = "", action = "", lcid = "";
	public static final String POLY_LOAN = "poly_loan";// 保理贷
	public static final String FENRUNLOAN = "fenrun_loan";// 分润贷

	private CommonTitleBar title;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.signature_pad);
		showText = (TextView) findViewById(R.id.signature_showText);
		showText2 = (TextView) findViewById(R.id.signature_showText2);
		mSignaturePad = (SignaturePad) findViewById(R.id.signature_pad);

		title = (CommonTitleBar) findViewById(R.id.titlebar_signature);
		title.setActName("签名");
		title.setCanClickDestory(this, true);

		Intent intent = getIntent();
		if (intent != null) {
			action = intent.getAction();
			if (action.equals(POLY_LOAN)) {
				account = intent.getStringExtra("account");
				lcid = intent.getStringExtra("lcid");

			} else if (action.equals(FENRUNLOAN)) {

				account = intent.getStringExtra("account");
				month = intent.getStringExtra("month");
				reason = intent.getStringExtra("reason");
			}
		}
		Log.i("result", "------------onc--------");
		mSignaturePad.setOnSignedListener(new SignaturePad.OnSignedListener() {
			@Override
			public void onSigned() {
				mSaveButton.setEnabled(true);
				mClearButton.setEnabled(true);
				mSaveButton
						.setBackgroundResource(R.drawable.selector_next_normal);
				mClearButton
						.setBackgroundResource(R.drawable.selector_next_normal);
				showText.setVisibility(View.GONE);
				showText2.setVisibility(View.GONE);
			}

			@Override
			public void onClear() {
				mSaveButton.setEnabled(false);
				mClearButton.setEnabled(false);
				mSaveButton.setBackgroundResource(R.drawable.selector_nextstep);
				mClearButton
						.setBackgroundResource(R.drawable.selector_nextstep);
				showText.setVisibility(View.VISIBLE);
				showText2.setVisibility(View.VISIBLE);
			}
		});

		mSaveButton = (Button) findViewById(R.id.save_button);
		mSaveButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// uploadSignature();
				sendPhoto();
			}
		});
		mClearButton = (Button) findViewById(R.id.clear_button);
		mClearButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				mSignaturePad.clear();
			}
		});
	}

	private String sendPhoto() {

		Bitmap bmp = mSignaturePad.getSignatureBitmap();
		String str = Bitmap2StrByBase64(bmp);
		// String str = BitmapUtil.Bitmap2String(bmp, 60);//////////////////
		RequestParams requestParams = new RequestParams();
		StringBuilder picFileName = new StringBuilder();
		requestParams.addBodyParameter("picFileName", picFileName.toString());
		Map<String, String> map = new HashMap<String, String>();
		map.put("photo", str);
		String json = JSON.toJSONString(map);
		Log.i("result", "----ddd----s-------" + json);
		try {
			StringEntity bodyEntity = new StringEntity(json, "UTF-8");
			requestParams.setBodyEntity(bodyEntity);
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		showLoadingDialog();
		HttpUtils utils = new HttpUtils();
		utils.send(HttpMethod.POST, MyUrls.MERPHOTO, requestParams,
				new RequestCallBack<String>() {

					@Override
					public void onFailure(HttpException arg0, String arg1) {
						T.ss("操作超时");
						dismissLoadingDialog();
					}

					@Override
					public void onSuccess(ResponseInfo<String> response) {
						// TODO Auto-generated method stub
						String code = "";

						String str = response.result;
						Log.i("result", "----上传成功----s-------" + str);
						JSONObject jsonObject;
						try {
							jsonObject = new JSONObject(str);
							code = jsonObject.optString("CODE");
							if (code.equals("00")) {
								imagename = jsonObject.optString("ImageName");
								if (action.equals(FENRUNLOAN)) {

									sendMerPoslanMessage();
								} else if (action.equals(POLY_LOAN)) {
									applyPolyLoan();
								}

							} else {
								T.ss("操作超时");
							}
						} catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						dismissLoadingDialog();
					}
				});
		return photoName;
	}

	/**
	 * 通过Base32将Bitmap转换成Base64字符串
	 * 
	 * @param bit
	 * @return
	 */
	private String Bitmap2StrByBase64(Bitmap bit) {
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		bit.compress(CompressFormat.JPEG, 25, bos);// 参数100表示不压缩
		byte[] bytes = bos.toByteArray();
		return Base64.encodeToString(bytes, Base64.DEFAULT);
	}

	private void uploadSignature() {
		showLoadingDialogCannotCancle("请稍候...");
		new Thread(new Runnable() {

			@Override
			public void run() {
				// data = BitmapUtil.Bitmap2String(mSignaturePad
				// .getSignatureBitmap());
				// handler.sendEmptyMessage(10);

			}
		}).start();

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
		map.put("signature", imagename);// 签名
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
					Intent intent = new Intent(PosloanSignatureActivity.this,
							PosLoanActivity.class);
					startActivity(intent);
					finish();
				}
			}
		});

	}

	/**
	 * 保理贷 申请
	 */
	private void applyPolyLoan() {

		RequestParams params = new RequestParams();
		String url = MyUrls.BL_LOAHADD;
		Map<String, String> map = new HashMap<String, String>();
		map.put("username", MApplication.mSharedPref
				.getSharePrefString(SharedPrefConstant.USERNAME));
		map.put("token", MApplication.mSharedPref
				.getSharePrefString(SharedPrefConstant.TOKEN));
		map.put("money", account);
		map.put("signature", imagename);
		map.put("lcid", lcid);

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
			}

			@Override
			public void onSuccess(ResponseInfo<String> response) {
				// TODO Auto-generated method stub
				String code = "";
				String message = "";

				String str = response.result;
				Log.i("result", "----str----s-------" + str);
				try {
					JSONObject obj = new JSONObject(str);
					code = obj.optString("CODE");
					message = obj.optString("MESSAGE");

				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				if (code.equals("00")) {
					Intent intent = new Intent(PosloanSignatureActivity.this,
							PolyLoansTabHostActivity.class);
					intent.setAction("isApplySuccess");
					startActivity(intent);
					finish();
				}
				T.ss(message);
			}
		});
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}

}
