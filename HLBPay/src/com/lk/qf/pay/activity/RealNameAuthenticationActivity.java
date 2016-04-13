package com.lk.qf.pay.activity;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;
import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.lk.bhb.pay.R;
import com.lk.pay.communication.AsyncHttpResponseHandler;
import com.lk.qf.pay.beans.BasicResponse;
import com.lk.qf.pay.golbal.MApplication;
import com.lk.qf.pay.golbal.Urls;
import com.lk.qf.pay.golbal.User;
import com.lk.qf.pay.tool.Logger;
import com.lk.qf.pay.tool.MyHttpClient;
import com.lk.qf.pay.tool.T;
import com.lk.qf.pay.utils.BitmapUtil;
import com.lk.qf.pay.utils.DataParse;
import com.lk.qf.pay.utils.FileUtil;
import com.lk.qf.pay.wedget.ShowProvinceListDialog;
import com.lk.qf.pay.wedget.ShowProvinceListDialog.IGetProvinceIdAndCityId;

/*
 * 商户认证
 */
public class RealNameAuthenticationActivity extends BaseFragmentActivity
		implements OnClickListener, IGetProvinceIdAndCityId {

	private final int ADD_ID_CARD_HOLD = 100;
	private final int ADD_ID_CARD_FRONT = 101;
	private final int ADD_ID_CARD_SIDE = 102;
	private ImageView btn_hold_bca_front;// 手持照片按钮
	private ImageView btn_bca_front, btn_bca_back;// 正面,反面按钮
	private Button btnUpload;
	private EditText et_name;// 名字
	private EditText editTxtID;// 身份证号
	private EditText et_email;// 邮箱
	private EditText et_payPassword;// 密码
	private TextView txt_province;// 省
	private TextView txt_city;// 市 区
	private RelativeLayout recruitmentReLayout;// 省市的布局
	private LinearLayout ll_txtId;
	private ShowProvinceListDialog showProvinceListDialog;
	private ArrayList<HashMap<String, Object>> firstArrayList = null;// 存放省一类
	private String userName, idHold, idFront, idSide;
	public static final int ACTION_UPLOAD_IMG = 0;
	public static final int ACTION_UPLOAD_INFO = 1;
	private final int CERTIFICATION_FLAG = 2;// 认证标识
	private String custName;
	private String txtID;
	private String email;
	private String passWord;
	private String provId;
	private String cityId;
	private MApplication mApplication;
	private int screenWidth = 0;
	private int screenHeight = 0;
	private String photoPath;// 图片的路径
	private String localTempImgFileName = "pic.jpg";

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.real_name_authentication);
		mApplication = (MApplication) getApplication();
		screenWidth = mApplication.getScreenWidth();
		screenHeight = mApplication.getScreenHeight();
		photoPath = FileUtil.getTdPath(this) + "pic.jpg";
		et_name = (EditText) findViewById(R.id.et_name);
		editTxtID = (EditText) findViewById(R.id.editTxtID);
		et_email = (EditText) findViewById(R.id.et_email);
		et_payPassword = (EditText) findViewById(R.id.et_payPassword);
		txt_province = (TextView) findViewById(R.id.txt_province);
		txt_city = (TextView) findViewById(R.id.txt_city);
		recruitmentReLayout = (RelativeLayout) findViewById(R.id.recruitmentReLayout);
		recruitmentReLayout.setOnClickListener(this);
		
		btn_bca_front = (ImageView) findViewById(R.id.btn_bca_front);
		btn_bca_back = (ImageView) findViewById(R.id.btn_bca_back);
		btnUpload = (Button) findViewById(R.id.btn_bca_next);
		btn_hold_bca_front = (ImageView) findViewById(R.id.btn_hold_bca_front);
		btn_hold_bca_front.setOnClickListener(this);
		showProvinceListDialog = new ShowProvinceListDialog(
				RealNameAuthenticationActivity.this);
		btn_bca_front.setOnClickListener(this);
		btn_bca_back.setOnClickListener(this);
		btnUpload.setOnClickListener(this);
		((com.lk.qf.pay.wedget.CommonTitleBar) findViewById(R.id.titlebar_realname))
				.setActName("实名认证").setCanClickDestory(this, true);
		// userName = ((AppContext)
		// getApplicationContext()).getUserMobileNumber();
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_bca_front:
			takePicture(ADD_ID_CARD_FRONT);
			break;
		case R.id.btn_bca_back:
			takePicture(ADD_ID_CARD_SIDE);
			break;

		case R.id.btn_hold_bca_front:
			takePicture(ADD_ID_CARD_HOLD);
			break;
		case R.id.btn_bca_next:
			// upload();
			generateImg();
			break;
		case R.id.recruitmentReLayout:
			getProvincesAndCity();
			break;
		default:
			break;
		}
	}

	private void generateImg() {
		if (!selected1) {
			Toast.makeText(this, "请上传身份证正面照片", Toast.LENGTH_SHORT).show();
			return;
		}
		if (!selected2) {
			Toast.makeText(this, "请上传手持身份证照片", Toast.LENGTH_SHORT).show();
			return;
		}
		if (!selected3) {
			Toast.makeText(this, "请上传身份证反面照片", Toast.LENGTH_SHORT).show();
			return;
		}

		custName = et_name.getText().toString();
		if (TextUtils.isEmpty(custName)) {

			Toast.makeText(this, "用户名不能为空!", Toast.LENGTH_SHORT).show();
			return;

		}
		passWord = et_payPassword.getText().toString();
		if(TextUtils.isEmpty(passWord)){
			Toast.makeText(this, "支付密码不能为空!", Toast.LENGTH_SHORT).show();
			return;
		}
	
		email = et_email.getText().toString();
		txtID = editTxtID.getText().toString();
		if (TextUtils.isEmpty(txtID)) {

			Toast.makeText(this, "身份证号不能为空!", Toast.LENGTH_SHORT).show();
			return;

		}

		provId = txt_province.getHint().toString();
		cityId = txt_city.getHint().toString();
		if (TextUtils.isEmpty(provId) && TextUtils.isEmpty(cityId)) {

			Toast.makeText(this, "行政区划不能为空!", Toast.LENGTH_SHORT).show();
			return;

		}
		showLoadingDialog();
		new Thread(new Runnable() {

			@Override
			public void run() {
				params = new HashMap<String, String>();
				idHold = BitmapUtil.Bitmap2String(b3, 40);
				idFront = BitmapUtil.Bitmap2String(b1, 40);
				idSide = BitmapUtil.Bitmap2String(b2, 40);
				params.put("cardHandheld", idHold);
				params.put("cardFront", idFront);
				params.put("cardBack", idSide);
				h.sendEmptyMessage(22);
			}
		}).start();
	}

	Handler h = new Handler() {
		public void handleMessage(android.os.Message msg) {
			if (msg.what == 22) {
				upload();
			}
		};
	};
	HashMap<String, String> params = null;

	private void upload() {
		// idHold = BitmapUtil.Bitmap2String(b3, 15);
		//
		// idFront = BitmapUtil.Bitmap2String(b1, 15);
		// idSide = BitmapUtil.Bitmap2String(b2, 15);

		params.put("custName", custName);// 用户名
		params.put("certificateType", "01");// 证件类型
		params.put("certificateNo", txtID);
		params.put("userEmail", email);// 邮箱
		params.put("provinceId", provId);// 所在省ID
		params.put("cityId", cityId);// 所在市ID
		params.put("payPwd", passWord);
		// params.put("custMobile", User.uAccount);
		// post("SY0007", params);
		MyHttpClient.post(this, Urls.IDENTITY_CHECH, params,
				new AsyncHttpResponseHandler() {

					@Override
					public void onSuccess(int statusCode, Header[] headers,
							byte[] responseBody) {
						Logger.json(responseBody);
						try {
							BasicResponse r = new BasicResponse(responseBody)
									.getResult();
							if (r.isSuccess()) {
								T.showCustomeOk(
										RealNameAuthenticationActivity.this,
										"已提交审核");
								MApplication.mApplicationContext
										.refreshUserInfo();
								User.uStatus = 1;
								finish();
							} else {
								T.ss(r.getMsg());
							}
						} catch (JSONException e) {
							// TODO 自动生成的 catch 块
							e.printStackTrace();
						}

					}

					@Override
					public void onFailure(int statusCode, Header[] headers,
							byte[] responseBody, Throwable error) {
                          T.sl("网络错误:"+error.getMessage());
					}

					@Override
					public void onStart() {
						showLoadingDialog();
						firstArrayList = null;
					}

					@Override
					public void onFinish() {
						dismissLoadingDialog();
					}

				});

	}

	private void post(String tradeCode, HashMap<String, String> params) {
		MyHttpClient.post(this,tradeCode + ".json", params,
		// "http://192.168.0.59:8080/mpcctp/" + tradeCode + ".json",
		// params,
				new AsyncHttpResponseHandler() {
					@Override
					public void onStart() {
						showLoadingDialog();
					}

					@Override
					public void onFinish() {
						dismissLoadingDialog();
					}

					@Override
					public void onSuccess(int statusCode, Header[] headers,
							byte[] responseBody) {
						String content = new String(responseBody);
						HashMap<String, Object> result = DataParse
								.getInstance().parse(content);
						System.out.println("response-->" + result.get("RSPMSG"));
						StringBuilder sb = new StringBuilder();
						for (Entry<String, Object> entry : result.entrySet()) {
							sb.append(entry.getKey() + "-->" + entry.getValue()
									+ "\n");
						}
						setResult(
								CERTIFICATION_FLAG,
								new Intent().putExtra("RETURN_VALUE",
										sb.toString()));
						finish();
						
					}

					@Override
					public void onFailure(int statusCode, Header[] headers,
							byte[] responseBody, Throwable error) {
						Toast.makeText(RealNameAuthenticationActivity.this,
								"操作超时", Toast.LENGTH_SHORT).show();
						
					}
				});
	}

	/**
	 * 拍照
	 * 
	 * @param code
	 */
	private void takePicture(int code) {
//		Intent intent = new Intent(
//				android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
//		File f = new File(photoPath);
//		Uri u = Uri.fromFile(f);
//		intent.putExtra(MediaStore.Images.Media.ORIENTATION, 0);
//		intent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 0.5);
//		intent.putExtra(MediaStore.EXTRA_OUTPUT, u);
//		startActivityForResult(intent, code);
		
		File dir = FileUtil.mkdir(this);
		Intent intent = new Intent(
				android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
		File f = new File(dir, localTempImgFileName);
		Uri u = Uri.fromFile(f);
		intent.putExtra(MediaStore.Images.Media.ORIENTATION, 0);
		intent.putExtra(MediaStore.EXTRA_OUTPUT, u);
		startActivityForResult(intent, code);
	}

	private boolean selected1, selected2, selected3;

	@SuppressLint("NewApi")
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode != RESULT_OK) {
			return;
		} else if (requestCode == ADD_ID_CARD_FRONT && resultCode == RESULT_OK) {

			setBitmapToImageView(photoPath, btn_bca_front, 1);
			selected1 = true;
			// setBitmapTransformString(ADD_ID_CARD_FRONT, photoPath, 25);
		} else if (requestCode == ADD_ID_CARD_SIDE && resultCode == RESULT_OK) {

			setBitmapToImageView(photoPath, btn_bca_back, 2);
			// setBitmapTransformString(ADD_ID_CARD_SIDE, photoPath, 25);
			selected2 = true;
		} else if (requestCode == ADD_ID_CARD_HOLD && resultCode == RESULT_OK) {
			selected3 = true;
			setBitmapToImageView(photoPath, btn_hold_bca_front, 3);
			// setBitmapTransformString(ADD_ID_CARD_HOLD, photoPath, 25);

		}
	}

	/**
	 * 
	 * 给imageView设置Bitmap
	 * 
	 * @param imagePath
	 * @param iamge
	 * @param iamgeWidth
	 * @param imageHeight
	 */
	private void setBitmapToImageView(String imagePath, ImageView iamgeView,
			int whitch) {

		// Bitmap tempValue =
		// BitmapUtil.resizeImageFirstMethod(imagePath,iamgeView.getWidth(),
		// iamgeView.getHeight());
		// iamgeView.setImageBitmap(tempValue);
		// iamgeView.setLayoutParams(new
		// LinearLayout.LayoutParams(iamgeView.getWidth(),
		// iamgeView.getHeight()));
		Bitmap tempValue = BitmapUtil.resizeImageSecondMethod(imagePath,
				iamgeView.getWidth(), iamgeView.getHeight());
		iamgeView.setImageBitmap(tempValue);
		iamgeView.setLayoutParams(new LinearLayout.LayoutParams(iamgeView
				.getWidth(), iamgeView.getHeight()));
		if (whitch == 1) {
			b1 = tempValue;
		} else if (whitch == 2) {
			b2 = tempValue;
		} else if (whitch == 3) {
			b3 = tempValue;
		}
	}

	Bitmap b1, b2, b3;

	/**
	 * 获得图片的转换值(目前的宽度和高度暂时设定为手机屏幕的宽度和高度)
	 * 
	 * @param imagePath
	 * @param requSize
	 */
	@SuppressWarnings("unused")
	private void setBitmapTransformString(final int whereImage,
			final String imagePath, final int requSize) {

		new Thread() {

			public void run() {

				switch (whereImage) {
				case ADD_ID_CARD_HOLD:
					idHold = BitmapUtil.bitmapTransformString(imagePath,
							screenWidth, screenHeight, requSize);
					break;
				case ADD_ID_CARD_FRONT:
					idFront = BitmapUtil.bitmapTransformString(imagePath,
							screenWidth, screenHeight, requSize);
					break;
				case ADD_ID_CARD_SIDE:
					idSide = BitmapUtil.bitmapTransformString(imagePath,
							screenWidth, screenHeight, requSize);
					break;

				}
			};

		}.start();
	}

	/**
	 * 获取省市列表
	 */
	private void getProvincesAndCity() {

		HashMap<String, String> params = new HashMap<String, String>();
		postFromDing(Urls.PROVINCE, params);

	}

	@SuppressWarnings("unused")
	private void postFromDing(String url, HashMap<String, String> params) {

		MyHttpClient.post(RealNameAuthenticationActivity.this, url, params,
				new AsyncHttpResponseHandler() {

					@Override
					public void onStart() {
						showLoadingDialog();
					}

					@Override
					public void onFinish() {

						dismissLoadingDialog();

						if (firstArrayList != null && firstArrayList.size() > 0) {

							showProvinceListDialog.setContent("请选择省份",
									firstArrayList);
							if (!showProvinceListDialog.isVisible()) {

								showProvinceListDialog.show(
										getSupportFragmentManager(),
										"PROVINCE_DIALOG");
								// showProvinceListDialog.setCancelable(false);

							}

						}
					}

					@Override
					public void onSuccess(int statusCode, Header[] headers,
							byte[] responseBody) {
						ArrayList<HashMap<String, Object>> secondArrayList = null;// 存放区/市一级
						HashMap<String, Object> firstHashMap = null;
						HashMap<String, Object> secondHashMap = null;// 存放区/县一级

						try {
							BasicResponse result = new BasicResponse(
									responseBody).getResult();
							if (result.isSuccess()) {

								firstArrayList = new ArrayList<HashMap<String, Object>>();
								JSONObject jsonOneBody = result.getJsonBody();
								JSONArray jsonOneArray = jsonOneBody
										.getJSONArray("province");
								for (int i = 0; i < jsonOneArray.length(); i++) {

									firstHashMap = new HashMap<String, Object>();
									JSONObject jsonTwoBody = jsonOneArray
											.getJSONObject(i);
									JSONArray jsonSecondArray = jsonTwoBody
											.getJSONArray("cityList");
									if (jsonSecondArray != null) {

										secondArrayList = new ArrayList<HashMap<String, Object>>();
										for (int j = 0; j < jsonSecondArray
												.length(); j++) {

											secondHashMap = new HashMap<String, Object>();
											secondHashMap.put("cityId",
													jsonSecondArray
															.getJSONObject(j)
															.get("cityId"));
											secondHashMap.put("cityName",
													jsonSecondArray
															.getJSONObject(j)
															.get("cityName"));
											secondHashMap.put("provId",
													jsonSecondArray
															.getJSONObject(j)
															.get("provId"));
											secondArrayList.add(secondHashMap);
										}
									}
									firstHashMap.put("cityList",
											secondArrayList);
									firstHashMap.put("provName",
											jsonTwoBody.get("provName"));
									firstHashMap.put("provId",
											jsonTwoBody.get("provId"));
									firstArrayList.add(firstHashMap);
								}
							}
						} catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}

					}

					@Override
					public void onFailure(int statusCode, Header[] headers,
							byte[] responseBody, Throwable error) {

						firstArrayList = null;
						Toast.makeText(RealNameAuthenticationActivity.this,
								"网络连接超时", Toast.LENGTH_SHORT).show();

					}

				});
	}

	@Override
	public void onBackPressed() {
		onDestoryDialog();
		super.onBackPressed();
	}

	@Override
	public void getProvinceIdAndCityId(String provName, String provId,
			String cityName, String cityId) {

		showProvinceListDialog.dismiss();
		txt_province.setText(provName);
		txt_province.setHint(provId);
		txt_city.setText(cityName);
		txt_city.setHint(cityId);

	}

	private void onDestoryDialog() {

		if (showProvinceListDialog != null
				&& showProvinceListDialog.isVisible()) {

			showProvinceListDialog.dismiss();
			showProvinceListDialog = null;

		}

	}
	
	@Override  
    protected void onSaveInstanceState(Bundle outState) {  
        super.onSaveInstanceState(outState);  
        outState.putString("photoPath", photoPath); 
    }  
  
    @Override  
    protected void onRestoreInstanceState(Bundle savedInstanceState) {  
        super.onRestoreInstanceState(savedInstanceState);  
        if (TextUtils.isEmpty(photoPath)) {  
        	photoPath = savedInstanceState.getString("photoPath");  
        }  
    }  

}
