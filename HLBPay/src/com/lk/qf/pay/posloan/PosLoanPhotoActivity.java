package com.lk.qf.pay.posloan;

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

public class PosLoanPhotoActivity extends BaseActivity implements
		OnClickListener {

	private Button btnOk, btnPhoto1, btnPhoto2, btnPhoto3, btnPhoto4,
			btnPhoto5;
	private String userName;
	private String photoName1 = "", photoName2 = "", photoName3 = "",
			photoName4 = "", photoName5 = "";

	private String strPhoto;// 图片字符
	private Bitmap bitmap;
	private final int ADD_HUKOUBEN_ONE = 100;
	private final int ADD_MARRAGE_TWO = 101;
	private final int ADD_HOUSE_THREE = 102;
	private final int ADD_UTILITY_FOUR = 103;
	private final int ADD_UTILITY_FIVE = 104;

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
		setContentView(R.layout.posloan_attachment);
		init();
	}

	private void init() {
		Intent intent = getIntent();
		if (intent != null) {
			userName = intent.getStringExtra("userName");
		} else {
			userName = MApplication.mSharedPref
					.getSharePrefString(SharedPrefConstant.USERNAME);
		}
		photoPath = FileUtil.getTdPath(this) + "pic.jpg";// 图片的路径
		mApplication = (MApplication) getApplication();

		btnPhoto1 = (Button) findViewById(R.id.btn_posLoan_photo_1);
		btnPhoto2 = (Button) findViewById(R.id.btn_posLoan_photo_2);
		btnPhoto3 = (Button) findViewById(R.id.btn_posLoan_photo_3);
		btnPhoto4 = (Button) findViewById(R.id.btn_posLoan_photo_4);
		btnPhoto5 = (Button) findViewById(R.id.btn_posLoan_photo_5);

		btnPhoto1.setOnClickListener(this);
		btnPhoto2.setOnClickListener(this);
		btnPhoto3.setOnClickListener(this);
		btnPhoto4.setOnClickListener(this);
		btnPhoto5.setOnClickListener(this);
		findViewById(R.id.btn_posLoan_photo_next).setOnClickListener(this);
		map = new HashMap<String, String>();
		title = (CommonTitleBar) findViewById(R.id.titlebar_posLoan_attachment);
		title.setActName("附件(照片)");
		title.setCanClickDestory(this, true);
	}

	@Override
	public void onClick(View arg0) {
		// TODO Auto-generated method stub
		switch (arg0.getId()) {
		case R.id.btn_posLoan_photo_1:
			takePicture(ADD_HUKOUBEN_ONE);
			break;
		case R.id.btn_posLoan_photo_2:
			takePicture(ADD_MARRAGE_TWO);
			break;
		case R.id.btn_posLoan_photo_3:
			takePicture(ADD_HOUSE_THREE);
			break;
		case R.id.btn_posLoan_photo_4:
			takePicture(ADD_UTILITY_FOUR);
			break;
		case R.id.btn_posLoan_photo_5:
			takePicture(ADD_UTILITY_FIVE);
			break;
		case R.id.btn_posLoan_photo_next:
			judgePhotoAll();
			break;

		default:
			break;
		}
	}

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

	private boolean selected1 = false, selected2 = false, selected3 = false,
			selected4 = false, selected5 = false;

	@SuppressLint("NewApi")
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {

		if (resultCode == RESULT_OK) {
			// Bitmap bitmap = getBitmapFromUrl(getPhotopath(), 313.5, 462.0);
			// saveScalePhoto(bitmap);
			Log.i("result", "-----------------ppp-------ok---");
			if (requestCode == ADD_HUKOUBEN_ONE) {
				// setBitmapToImageView(photoPath, btnPhoto1, 1);
				selected1 = true;
				sendPhoto(photoPath, 1);

			} else if (requestCode == ADD_MARRAGE_TWO) {

				// setBitmapToImageView(photoPath, btnPhoto2, 2);
				selected2 = true;
				sendPhoto(photoPath, 2);

			} else if (requestCode == ADD_HOUSE_THREE) {
				selected3 = true;
				// setBitmapToImageView(photoPath, btnPhoto3, 3);
				sendPhoto(photoPath, 3);

			} else if (requestCode == ADD_UTILITY_FOUR) {
				selected4 = true;
				// setBitmapToImageView(photoPath, btnPhoto4, 4);
				sendPhoto(photoPath, 4);
			} else if (requestCode == ADD_UTILITY_FIVE) {
				selected5 = true;
				// setBitmapToImageView(photoPath, btnPhoto5, 5);
				sendPhoto(photoPath, 5);
			}
		}

	}

	private void selectPhoto(int photoNum, String photoName) {

		switch (photoNum) {
		case 1:
			// map.put("posfarenimg", photoName);
			photoName1 = photoName;
			setBitmapToImageView(photoPath, btnPhoto1, 1);
			break;
		case 2:
			setBitmapToImageView(photoPath, btnPhoto2, 2);

			photoName2 = photoName;
			// map.put("posoilimg", photoName);
			break;
		case 3:
			setBitmapToImageView(photoPath, btnPhoto3, 3);

			photoName3 = photoName;
			// map.put("epayzhizhaoimg", photoName);
			break;
		case 4:
			setBitmapToImageView(photoPath, btnPhoto4, 4);

			photoName4 = photoName;
			// map.put("postravelimg", photoName);
			break;
		case 5:
			setBitmapToImageView(photoPath, btnPhoto5, 5);

			photoName5 = photoName;
			// map.put("epaytaximg", photoName);

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
	Bitmap b1, b2, b3, b4, b5;

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
							Log.i("result", "---------------------" + photoName);
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
			T.ss("请上传户口本照片");
			return;
		}

		if (!selected3) {
			T.ss("请上传房产证照片");
			return;
		}
		if (!selected4) {
			T.ss("请上传住宅水电煤发票");
			return;
		}
		if (!selected5) {
			T.ss("请上传公司水电煤发票");
			return;
		}
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
		map.put("contact1phone", "");// 第一联系人电话
		map.put("contact1name", "");// 第一联系人姓名
		map.put("contact2name", "");// 2姓名
		map.put("contact2phone", "");// 2电话
		map.put("contact3phone", "");
		map.put("contact3name", "");
		map.put("posairimg", photoName1);// 户口本复印件
		map.put("posrailwayimg", photoName2);// 结婚证
		map.put("poscashimg2", photoName3);// 房产证
		map.put("posotherimg3", photoName4);// 住址水电气
		map.put("posotherimg2", photoName5);// 单位水电气票据
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
					Intent intent = new Intent(PosLoanPhotoActivity.this,
							PosLoanBorrowingActivity.class);
					startActivity(intent);
				}
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
	}
}
