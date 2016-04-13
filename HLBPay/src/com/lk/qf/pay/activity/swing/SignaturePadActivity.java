package com.lk.qf.pay.activity.swing;

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
import com.lk.qf.pay.golbal.MApplication;
import com.lk.qf.pay.golbal.MyUrls;
import com.lk.qf.pay.golbal.Urls;
import com.lk.qf.pay.nfc.NfcPayActivity;
import com.lk.qf.pay.signature.SignaturePad;
import com.lk.qf.pay.tool.Logger;
import com.lk.qf.pay.tool.MyHttpClient;
import com.lk.qf.pay.tool.T;
import com.lk.qf.pay.utils.BitmapUtil;
import com.lk.qf.pay.v50.V50CashInConfirmActivity;
import com.lk.qf.pay.wedget.CommonTitleBar;

public class SignaturePadActivity extends BaseActivity {
	private SignaturePad mSignaturePad;
	private Button mClearButton, mSaveButton;
	private String data;
	private TextView showText, showText2;
	private String action = null;
	private String photoName, termType, strImageName;
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
		action = getIntent().getAction();
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

	Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			if (msg.what == 10) {
				HashMap<String, String> params = new HashMap<String, String>();
				params.put("prdordNo", PosData.getPosData().getPrdordNo());
				params.put("content", data);
				MyHttpClient.post(SignaturePadActivity.this,
						Urls.UPLOAD_SIGNTURE, params,
						new AsyncHttpResponseHandler() {

							@Override
							public void onSuccess(int statusCode,
									Header[] headers, byte[] responseBody) {
								Logger.json("[上传电子签名]", responseBody);
								try {
									BasicResponse r = new BasicResponse(
											responseBody).getResult();
									if (r.isSuccess()) {

										if (action != null) {

											if (action.equals("ACTION_NFC_PAY")) {
												startActivity(new Intent(
														SignaturePadActivity.this,
														NfcPayActivity.class));
												finish();
											}
											if (action.equals("ACTION_V50_PAY")) {
												startActivity(new Intent(
														SignaturePadActivity.this,
														V50CashInConfirmActivity.class));
												finish();
											}
											
										} else {
											startActivity(new Intent(
													SignaturePadActivity.this,
													CashInConfirmActivity.class));
											finish();
										}
									} else {
										showDialog(r.getMsg());
									}
								} catch (JSONException e) {
									// TODO 自动生成的 catch 块
									e.printStackTrace();
								}

							}

							@Override
							public void onFailure(int statusCode,
									Header[] headers, byte[] responseBody,
									Throwable error) {
								T.ss("" + error.getMessage());

							}

							public void onFinish() {
								super.onFinish();
								dismissLoadingDialog();
							};
						});
			}
		};
	};

	/**
	 * 上传签名
	 * @return
	 */
	private String sendPhoto() {
		showLoadingDialog();
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
		HttpUtils utils = new HttpUtils();
		utils.send(HttpMethod.POST, MyUrls.RECEIPTS, requestParams,
				new RequestCallBack<String>() {

					@Override
					public void onFailure(HttpException arg0, String arg1) {
						T.ss("上传失败,错误代码:" + arg0.getExceptionCode());
						Log.i("result",
								"----设置失败---s-------" + arg0.getExceptionCode());
						dismissLoadingDialog();
					}

					@Override
					public void onSuccess(ResponseInfo<String> response) {
						// TODO Auto-generated method stub
						dismissLoadingDialog();
						String code = "";
						String imagename = "";
						String str = response.result;
						Log.i("result", "----上传成功----s-------" + str);
						JSONObject jsonObject;
						try {
							jsonObject = new JSONObject(str);
							code = jsonObject.optString("CODE");
							if (code.equals("00")) {
								imagename = jsonObject.optString("imagename");
								String payType = PosData.getPosData()
										.getPayType();
								if (action != null) {

									if (action.equals("ACTION_NFC_PAY")) {
										startActivity(new Intent(
												SignaturePadActivity.this,
												NfcPayActivity.class));
										finish();
									}

									if (action.equals("ACTION_V50_PAY")) {
										Intent intent = new Intent(
												SignaturePadActivity.this,
												V50CashInConfirmActivity.class);
										
										strImageName = imagename.substring(0,
												imagename.lastIndexOf("."));
										intent.putExtra("imagename",
												strImageName);
										intent.putExtra("payType", payType);
										startActivity(intent);
										finish();
									}

								} else {
									Log.i("result", "-------payType--p-d-" + payType);
									if (payType==null) {
										T.ss("操作失败,请重新操作!");
										finish();
										return;
									}
									if (!payType.equals("01")
											&& !payType.equals("02")) {
										Log.i("result", "---------p-t-"
												+ payType);
										Intent intent1 = new Intent(
												SignaturePadActivity.this,
												CashInConfimSuperActivity.class);
										strImageName = imagename.substring(0,
												imagename.lastIndexOf("."));
										intent1.putExtra("imagename",
												strImageName);
										startActivity(intent1);
										finish();
									} else {

										Intent intent = new Intent(
												SignaturePadActivity.this,
												CashInConfirmActivity.class);
										strImageName = imagename.substring(0,
												imagename.lastIndexOf("."));
										intent.putExtra("imagename",
												strImageName);
										startActivity(intent);
										finish();
									}

								}
								T.ss("上传成功");
							} else {
								T.ss("上传失败");
							}
						} catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						
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

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}

}
