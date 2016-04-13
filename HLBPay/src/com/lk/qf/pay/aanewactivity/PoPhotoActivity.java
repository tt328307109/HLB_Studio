package com.lk.qf.pay.aanewactivity;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;
import org.apache.http.entity.StringEntity;
import org.json.JSONException;
import org.json.JSONObject;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
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
import com.lk.qf.pay.utils.FileUtil;
import com.lk.qf.pay.wedget.CommonTitleBar;

public class PoPhotoActivity extends BaseActivity implements OnClickListener {

	private Button btnOk, btnPhoto1, btnPhoto2, btnPhoto3, btnPhoto4,
			btnPhoto5, btnPhoto6, btnPhoto7, btnPhoto8, btnPhoto9, btnPhoto10,
			btnPhoto11, btnPhoto12, btnPhoto13;
	private String userName;
	private String photoName1="", photoName2="", photoName3="", photoName4="", photoName5="",
			photoName6="", photoName7="", photoName8="", photoName9="", photoName10="",
			photoName11="", photoName12="", photoName13="";

	private String strPhoto;// 图片字符
	private Bitmap bitmap;
	private final int ADD_ID_CARD_ONE = 100;
	private final int ADD_ID_CARD_TWO = 101;
	private final int ADD_ID_CARD_HOLD = 102;
	private final int ADD_BANK_CARD_ONE = 103;
	private final int ADD_BANK_CARD_TWO = 104;
	private final int ADD_BANK_CARD_HOLD = 105;
	private final int ADD_YINGYEZHIZHAO = 106;
	private final int ADD_YINGYEZHIZHAO_HOLD = 107;
	private final int ADD_SHUIWU = 108;
	private final int ADD_ZUZHIJIGOU = 109;
	private final int ADD_KAIHUXUKE = 110;
	private final int ADD_MENGTOU = 111;
	private final int ADD_DIANNEIZHAO = 112;

	private int screenWidth = 0;
	private int screenHeight = 0;
	private String photoPath;// 图片的路径
	private String localTempImgFileName = "pic.jpg";
	private MApplication mApplication;
	String photoName = "";
	private HashMap<String, String> map;
	private CommonTitleBar title;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.add_zj_phono_layout);
		Log.i("result", "--------------oncreate------------");
		init();
	}
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		Log.i("result", "--------------onResume------------");
	}
	@Override
	protected void onRestart() {
		// TODO Auto-generated method stub
		super.onRestart();
		Log.i("result", "--------------onRestart------------");
		
	}
	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		Log.i("result", "--------------onPause------------");
	}

	private void init() {
		Intent intent = getIntent();
		if (intent != null ) {
			userName = intent.getStringExtra("userName");
		}else {
			userName = MApplication.mSharedPref
					.getSharePrefString(SharedPrefConstant.USERNAME);
		}
		photoPath = FileUtil.getTdPath(this) + localTempImgFileName;// 图片的路径
		mApplication = (MApplication) getApplication();

		btnPhoto1 = (Button) findViewById(R.id.btn_photo_1);
		btnPhoto2 = (Button) findViewById(R.id.btn_photo_2);
		btnPhoto3 = (Button) findViewById(R.id.btn_photo_3);
		btnPhoto4 = (Button) findViewById(R.id.btn_photo_4);
		btnPhoto5 = (Button) findViewById(R.id.btn_photo_5);
		btnPhoto6 = (Button) findViewById(R.id.btn_photo_6);
		btnPhoto7 = (Button) findViewById(R.id.btn_photo_7);
		btnPhoto8 = (Button) findViewById(R.id.btn_photo_8);
		btnPhoto9 = (Button) findViewById(R.id.btn_photo_9);
		btnPhoto10 = (Button) findViewById(R.id.btn_photo_10);
		btnPhoto11 = (Button) findViewById(R.id.btn_photo_11);
		btnPhoto12 = (Button) findViewById(R.id.btn_photo_12);
		btnPhoto13 = (Button) findViewById(R.id.btn_photo_13);
		btnPhoto1.setOnClickListener(this);
		btnPhoto2.setOnClickListener(this);
		btnPhoto3.setOnClickListener(this);
		btnPhoto4.setOnClickListener(this);
		btnPhoto5.setOnClickListener(this);
		btnPhoto6.setOnClickListener(this);
		btnPhoto7.setOnClickListener(this);
		btnPhoto8.setOnClickListener(this);
		btnPhoto9.setOnClickListener(this);
		btnPhoto10.setOnClickListener(this);
		btnPhoto11.setOnClickListener(this);
		btnPhoto12.setOnClickListener(this);
		btnPhoto13.setOnClickListener(this);
		findViewById(R.id.btn_photo_wancheng).setOnClickListener(this);
		map = new HashMap<String, String>();
		title = (CommonTitleBar) findViewById(R.id.titlebar_zizhi_photo);
		title.setActName("上传证件照");
		title.setCanClickDestory(this, true);
	}

	@Override
	public void onClick(View arg0) {
		// TODO Auto-generated method stub
		switch (arg0.getId()) {
		case R.id.btn_photo_1:
			takePicture(ADD_ID_CARD_ONE);
			break;
		case R.id.btn_photo_2:
			takePicture(ADD_ID_CARD_TWO);
			break;
		case R.id.btn_photo_3:
			takePicture(ADD_BANK_CARD_ONE);
			break;
		case R.id.btn_photo_4:
			takePicture(ADD_BANK_CARD_TWO);
			break;
		case R.id.btn_photo_5:
			takePicture(ADD_ID_CARD_HOLD);
			break;
		case R.id.btn_photo_6:
			takePicture(ADD_BANK_CARD_HOLD);
			break;
		case R.id.btn_photo_7:
			takePicture(ADD_YINGYEZHIZHAO);
			break;
		case R.id.btn_photo_8:
			takePicture(ADD_YINGYEZHIZHAO_HOLD);
			break;
		case R.id.btn_photo_9:
			takePicture(ADD_SHUIWU);
			break;
		case R.id.btn_photo_10:
			takePicture(ADD_ZUZHIJIGOU);
			break;
		case R.id.btn_photo_11:
			takePicture(ADD_KAIHUXUKE);
			break;
		case R.id.btn_photo_12:
			takePicture(ADD_MENGTOU);
			break;
		case R.id.btn_photo_13:
			takePicture(ADD_DIANNEIZHAO);
			break;
		case R.id.btn_photo_wancheng:
			judgePhotoAll();
			break;

		default:
			break;
		}
	}

	// /**
	// * 拍照
	// *
	// * @param code
	// */
	// private void takePicture(int code) {
	//
	// Intent intent = new Intent(
	// android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
	//
	// intent.putExtra(MediaStore.Images.Media.ORIENTATION, 0);
	// intent.putExtra(MediaStore.EXTRA_OUTPUT, photoPath);
	// startActivityForResult(intent, code);
	//
	// }
	/**
	 * 拍照
	 * 
	 * @param code
	 */
	private void takePicture(int code) {
		Log.i("result", "-----------------");
		File dir = FileUtil.mkdir(this);
		Intent intent = new Intent(
				android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
		File f = new File(dir, localTempImgFileName);
		Uri u = Uri.fromFile(f);
		intent.putExtra(MediaStore.Images.Media.ORIENTATION, 0);
		intent.putExtra(MediaStore.EXTRA_OUTPUT, u);
		startActivityForResult(intent, code);
		

	}

//	/*
//	 * 得到拍摄的图片
//	 */
//	private Bitmap getBitmap(int code) {
//		File dir = FileUtil.mkdir(this); // 设置存放目录
//		File f = new File(dir.getAbsoluteFile(), localTempImgFileName);
//
//		BitmapFactory.Options opts = new BitmapFactory.Options();// 获取缩略图显示到屏幕
//		opts.inSampleSize = 5;
//		Bitmap cbitmap = BitmapFactory.decodeFile(f.getAbsolutePath(), opts);
//
//		return cbitmap;
//	}

	private boolean selected1 = false, selected2 = false, selected3 = false,
			selected4 = false, selected5 = false, selected6 = false,
			selected7 = false, selected8 = false, selected9 = false,
			selected10 = false, selected11 = false, selected12 = false,
			selected13 = false;

	@SuppressLint("NewApi")
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {

		Log.i("result", "-----------------pppd----------");
		if (resultCode == RESULT_OK) {
			// Bitmap bitmap = getBitmapFromUrl(getPhotopath(), 313.5, 462.0);
			// saveScalePhoto(bitmap);
			Log.i("result", "-----------------ppp-------ok---");
			if (requestCode == ADD_ID_CARD_ONE) {
				// setBitmapToImageView(photoPath, btnPhoto1, 1);
				selected1 = true;
				sendPhoto(photoPath, 1);
				Log.i("result", "-----------------ppp----------");

			} else if (requestCode == ADD_ID_CARD_TWO) {

				// setBitmapToImageView(photoPath, btnPhoto2, 2);
				selected2 = true;
				sendPhoto(photoPath, 2);

			} else if (requestCode == ADD_BANK_CARD_ONE) {
				selected3 = true;
				// setBitmapToImageView(photoPath, btnPhoto3, 3);
				sendPhoto(photoPath, 3);

			} else if (requestCode == ADD_BANK_CARD_TWO) {
				selected4 = true;
				// setBitmapToImageView(photoPath, btnPhoto4, 4);
				sendPhoto(photoPath, 4);
			} else if (requestCode == ADD_ID_CARD_HOLD) {
				selected5 = true;
				// setBitmapToImageView(photoPath, btnPhoto5, 5);
				sendPhoto(photoPath, 5);
			} else if (requestCode == ADD_BANK_CARD_HOLD) {
				selected6 = true;
				// setBitmapToImageView(photoPath, btnPhoto6, 6);
				sendPhoto(photoPath, 6);
			} else if (requestCode == ADD_YINGYEZHIZHAO) {
				selected7 = true;
				// setBitmapToImageView(photoPath, btnPhoto7, 7);
				sendPhoto(photoPath, 7);
			} else if (requestCode == ADD_YINGYEZHIZHAO_HOLD
					&& resultCode == RESULT_OK) {
				selected8 = true;
				// setBitmapToImageView(photoPath, btnPhoto8, 8);
				sendPhoto(photoPath, 8);
			} else if (requestCode == ADD_SHUIWU) {
				selected9 = true;
				// setBitmapToImageView(photoPath, btnPhoto9, 9);
				sendPhoto(photoPath, 9);
			} else if (requestCode == ADD_ZUZHIJIGOU) {
				selected10 = true;
				// setBitmapToImageView(photoPath, btnPhoto10, 10);
				sendPhoto(photoPath, 10);
			} else if (requestCode == ADD_KAIHUXUKE) {
				selected11 = true;
				// setBitmapToImageView(photoPath, btnPhoto11, 11);
				sendPhoto(photoPath, 11);
			} else if (requestCode == ADD_MENGTOU) {
				selected12 = true;
				// setBitmapToImageView(photoPath, btnPhoto12, 12);
				sendPhoto(photoPath, 12);
			} else if (requestCode == ADD_DIANNEIZHAO) {
				selected13 = true;
				// setBitmapToImageView(photoPath, btnPhoto13, 13);
				sendPhoto(photoPath, 13);
			}
		}

	}

	private void selectPhoto(int photoNum, String photoName) {

		switch (photoNum) {
		case 1:
//			map.put("posfarenimg", photoName);
			photoName1 = photoName;
			setBitmapToImageView(photoPath, btnPhoto1, 1);
			break;
		case 2:
			setBitmapToImageView(photoPath, btnPhoto2, 2);

			photoName2 = photoName;
//			map.put("posoilimg", photoName);
			break;
		case 3:
			setBitmapToImageView(photoPath, btnPhoto3, 3);

			photoName3 = photoName;
//			map.put("epayzhizhaoimg", photoName);
			break;
		case 4:
			setBitmapToImageView(photoPath, btnPhoto4, 4);

			photoName4 = photoName;
//			map.put("postravelimg", photoName);
			break;
		case 5:
			setBitmapToImageView(photoPath, btnPhoto5, 5);

			photoName5 = photoName;
//			map.put("epaytaximg", photoName);

			break;
		case 6:
//			map.put("posparkimg", photoName);
			photoName6 = photoName;
			setBitmapToImageView(photoPath, btnPhoto6, 6);

			break;
		case 7:
			photoName7 = photoName;
//			map.put("poszhizhaoimg", photoName);
			setBitmapToImageView(photoPath, btnPhoto7, 7);

			break;
		case 8:

			photoName8 = photoName;
//			map.put("pospublicimg", photoName);
			setBitmapToImageView(photoPath, btnPhoto8, 8);
			break;
		case 9:

			photoName9 = photoName;
//			map.put("postaximg", photoName);
			setBitmapToImageView(photoPath, btnPhoto9, 9);
			break;
		case 10:
//			map.put("posorgimg", photoName);
			photoName10 = photoName;
			setBitmapToImageView(photoPath, btnPhoto10, 10);
			break;
		case 11:

			photoName11 = photoName;
//			map.put("epayfarenimg", photoName);
			setBitmapToImageView(photoPath, btnPhoto11, 11);

			break;
		case 12:
			photoName12 = photoName;
//			map.put("poshouseimg1", photoName);
			setBitmapToImageView(photoPath, btnPhoto12, 12);

			break;
		case 13:
			photoName13 = photoName;
//			map.put("posshopimg1", photoName);
			setBitmapToImageView(photoPath, btnPhoto13, 13);
			break;

		default:
			break;
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
	Bitmap b1, b2, b3, b4, b5, b6, b7, b8, b9, b10, b11, b12, b13;

	private void setBitmapToImageView(String imagePath, Button btn, int whitch) {

		// Bitmap tempValue = BitmapUtil.resizeImageSecondMethod(imagePath,
		// 250, 250);
		// btn.setBackgroundDrawable(new BitmapDrawable(bitmap));
		// btn.setLayoutParams(new LinearLayout.LayoutParams(250, 250));
		// Log.i("result", "----ddd----1---dss----" +btn.getWidth()+"--"+btn
		// .getHeight());
		Log.i("result", "------------setBitmapToImageView-------");
		File dir = FileUtil.mkdir(this); // 设置存放目录
		File f = new File(dir.getAbsoluteFile(), localTempImgFileName);
		BitmapFactory.Options opts = new BitmapFactory.Options();// 获取缩略图显示到屏幕
		opts.inSampleSize = 5;
		// opts.inSampleSize = computeScale(opts, btn.getWidth(),
		// btn.getHeight());
		Bitmap cbitmap = BitmapFactory.decodeFile(f.getAbsolutePath(), opts);
		btn.setBackgroundDrawable(new BitmapDrawable(cbitmap));

	}

	

	private String sendPhoto(String photoPath, int photoNum) {
		showLoadingDialog();
		final int num = photoNum;
		Log.i("result", "----photoPath----s-------" + photoPath);
		Bitmap bmp = BitmapFactory.decodeFile(photoPath);
		String str = Bitmap2StrByBase64(bmp);
		// String str = BitmapUtil.Bitmap2String(bmp, 60);//////////////////
		RequestParams requestParams = new RequestParams();
		StringBuilder picFileName = new StringBuilder();
		requestParams.addBodyParameter("picFileName", picFileName.toString());
		Map<String, String> map = new HashMap<String, String>();
		map.put("photo", str);
		String json = JSON.toJSONString(map);
		try {
			StringEntity bodyEntity = new StringEntity(json, "UTF-8");
			requestParams.setBodyEntity(bodyEntity);
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
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
						String image = "";
						String str = response.result;
						Log.i("result", "----上传成功----s-------" + str);
						JSONObject jsonObject;
						try {
							jsonObject = new JSONObject(str);
							code = jsonObject.optString("CODE");
							image = jsonObject.optString("ImageName");
						} catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						if (code.equals("00")) {
							photoName = image;
							selectPhoto(num, photoName);
//							Log.i("result", "---------------------" + photoName);
						} else {
							T.ss("操作超时");
						}
						dismissLoadingDialog();
					}
				});
		return photoName;

	}

	/**
	 * 判断图片是否传完
	 */
	private void judgePhotoAll() {
		
		if (!selected1) {
			T.ss("请上传身份证正面照");
			return;
		}
		if (!selected2) {
			T.ss("请上传身份证反面照");
			return;
		}
		if (!selected3) {
			T.ss("请上传银行卡正面照");
			return;
		}
		if (!selected4) {
			T.ss("请上传银行卡反面照");
			return;
		}
		if (!selected5) {
			T.ss("请上传手持身份证照");
			return;
		}
		
		sendZhengjian();
		
	}

	/**
	 * 上传证件照
	 */
	private void sendZhengjian() {

		showLoadingDialog();
		RequestParams requestParams = new RequestParams();
		// Map<String, String> map = new HashMap<String, String>();
		map.put("username", userName);
		 map.put("posfarenimg", photoName1);// 身份证正面
		 map.put("posoilimg", photoName2);// 身份证反面
		 map.put("epayzhizhaoimg", photoName3);// 银行卡正面
		 map.put("postravelimg", photoName4);// 银行卡反面
		 map.put("epaytaximg", photoName5);// 手持身份证
		 map.put("posparkimg", photoName6);// 手持银行卡
		 map.put("poszhizhaoimg", photoName7);// 营业执照
		 map.put("pospublicimg", photoName8);// 手持营业执照
		 map.put("postaximg", photoName9);// 税务登记证
		 map.put("posorgimg", photoName10);// 组织机构证
		 map.put("epayfarenimg", photoName11);// 开户许可证
		 map.put("poshouseimg1", photoName12);// 门头照
		map.put("posshopimg1", photoName13);
		map.put("poshouseimg3", "");// 店内照
		map.put("poshouseimg2", "");// 店内照
		map.put("poszizhiimg", "");// 资质
		map.put("posshopimg2", "");
		map.put("posshopimg3", "");
		map.put("posairimg", "");
		map.put("posmobimg2", "");
		map.put("posmobimg3", "");
		map.put("epayorgimg", "");
		map.put("postaxiimg2", "");
		map.put("postaxiimg1", "");
		map.put("postaxiimg3", "");
		map.put("posrailwayimg", "");
		map.put("poscashimg1", "");
		map.put("posPOS3img", "");
		map.put("posotherimg1", "");
		map.put("posmobimg1", "");
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
		utils.send(HttpMethod.POST, MyUrls.MERCERTIFICATEADD, requestParams,
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
						String message = "";
						String str = response.result;
						Log.i("result", "----上传成功----s-------" + str);
						JSONObject jsonObject;
						try {
							jsonObject = new JSONObject(str);
							code = jsonObject.optString("CODE");
							message = jsonObject.optString("MESSAGE");
						} catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						if (code.equals("00")) {
							T.ss("上传成功");
							MApplication.mSharedPref
							.putSharePrefString(SharedPrefConstant.MERCERTIFICATEADD, "1");
							finish();
//							Intent intent = new Intent(PoPhotoActivity.this,
//									MenuActivity.class);
//							startActivity(intent);
						} else {

							T.ss(message);
						}
						dismissLoadingDialog();
					}
				});
	}

	/**
	 * 通过Base32将Bitmap转换成Base64字符串
	 * 
	 * @param bit
	 * @return
	 */
	private String Bitmap2StrByBase64(Bitmap bit) {
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		bit.compress(CompressFormat.JPEG, 20, bos);// 参数100表示不压缩
		byte[] bytes = bos.toByteArray();
		return Base64.encodeToString(bytes, Base64.DEFAULT);
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putString("photoPath", photoPath);
		Log.i("result", "-----------------onSaveInstanceState------");
	}

	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState) {
		super.onRestoreInstanceState(savedInstanceState);
		if (TextUtils.isEmpty(photoPath)) {
			photoPath = savedInstanceState.getString("photoPath");
		}
	}
	
	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		// TODO Auto-generated method stub
		super.onConfigurationChanged(newConfig);
		
	}
	
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		Log.i("result", "-----------------onDestroy------");
	}
}
