package com.lk.qf.pay.indiana.saidan;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.entity.StringEntity;
import org.json.JSONException;
import org.json.JSONObject;

import com.alibaba.fastjson.JSON;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;
import com.lk.bhb.pay.R;
import com.lk.qf.pay.aanewactivity.ShuhuiActivity;
import com.lk.qf.pay.activity.BaseActivity;
import com.lk.qf.pay.activity.LoginActivity;
import com.lk.qf.pay.golbal.MApplication;
import com.lk.qf.pay.golbal.MyUrls;
import com.lk.qf.pay.indiana.activity.IndianaBaseActivity;
import com.lk.qf.pay.sharedpref.SharedPrefConstant;
import com.lk.qf.pay.tool.T;
import com.lk.qf.pay.wedget.CommonTitleBarYellow;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

public class PublishedActivity extends IndianaBaseActivity implements OnClickListener {
	private CommonTitleBarYellow title;
	private GridView noScrollgridview;
	private GridAdapter adapter;
	private HashMap<String, String> map;
	private ArrayList<String> iamgeUrlList;
	private EditText et_saidan;
	private String goodsId = "", id = "", type;
	/**
	 * 保存选中图片的路径
	 */
	List<String> listh;
	int successNum = 0;
	private TextView activity_selectimg_send, tishi_saidan, fabiao_saidan;
	String photoName = "";

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_selectimg);
		Init();
	}

	public void Init() {
		Intent intent = getIntent();
		if (intent != null) {
			goodsId = intent.getStringExtra("goodsId");
			id = intent.getStringExtra("sdId");
		}
		listh = new ArrayList<String>();
		iamgeUrlList = new ArrayList<String>();
		title = (CommonTitleBarYellow) findViewById(R.id.titlebar_indiana_record_list);
		title.setActName("我要晒单");
		et_saidan = (EditText) findViewById(R.id.et_saidan);
		title.setCanClickDestory(this, true);
		tishi_saidan = (TextView) findViewById(R.id.tishi_saidan);

		fabiao_saidan = (TextView) findViewById(R.id.fabiao_saidan);
		fabiao_saidan.setOnClickListener(this);
		noScrollgridview = (GridView) findViewById(R.id.noScrollgridview);
		noScrollgridview.setSelector(new ColorDrawable(Color.TRANSPARENT));
		adapter = new GridAdapter(this);
		adapter.update();
		noScrollgridview.setAdapter(adapter);
		noScrollgridview.setOnItemClickListener(new OnItemClickListener() {

			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				if (arg2 == Bimp.bmp.size()) {
					new PopupWindows(PublishedActivity.this, noScrollgridview);
				} else {
					Intent intent = new Intent(PublishedActivity.this,
							PhotoActivity.class);
					intent.putExtra("ID", arg2);
					startActivity(intent);
				}
			}
		});

		// 发送
		// activity_selectimg_send = (TextView)
		// findViewById(R.id.activity_selectimg_send);
		// activity_selectimg_send.setOnClickListener(new OnClickListener() {
		//
		// public void onClick(View v) {
		// List<String> list = new ArrayList<String>();
		// for (int i = 0; i < Bimp.drr.size(); i++) {
		// String Str = Bimp.drr.get(i).substring(
		// Bimp.drr.get(i).lastIndexOf("/") + 1,
		// Bimp.drr.get(i).lastIndexOf("."));
		// list.add(FileUtils.SDPATH+Str+".JPEG");
		// }
		// // 高清的压缩图片全部就在 list 路径里面了
		// // 高清的压缩过的 bmp 对象 都在 Bimp.bmp里面
		// // 完成上传服务器后 .........
		// FileUtils.deleteDir();
		// }
		// });
	}

	@SuppressLint("HandlerLeak")
	public class GridAdapter extends BaseAdapter {
		private LayoutInflater inflater; // 视图容器
		private int selectedPosition = -1;// 选中的位置
		private boolean shape;

		public boolean isShape() {
			return shape;
		}

		public void setShape(boolean shape) {
			this.shape = shape;
		}

		public GridAdapter(Context context) {
			inflater = LayoutInflater.from(context);
		}

		public void update() {
			loading();
		}

		public int getCount() {
			if ((Bimp.bmp.size() + 1) < 4) {
				if ((Bimp.bmp.size() + 1) <= 1) {
					tishi_saidan.setText("添加奖品照片(最多3张)");
				} else {
					tishi_saidan.setText("");
				}
				return (Bimp.bmp.size() + 1);
			} else {
				return 3;
			}

		}

		public Object getItem(int arg0) {

			return null;
		}

		public long getItemId(int arg0) {

			return 0;
		}

		public void setSelectedPosition(int position) {
			selectedPosition = position;
		}

		public int getSelectedPosition() {
			return selectedPosition;
		}

		/**
		 * ListView Item设置
		 */
		public View getView(int position, View convertView, ViewGroup parent) {
			final int coord = position;
			ViewHolder holder = null;
			if (convertView == null) {

				convertView = inflater.inflate(R.layout.item_published_grida,
						parent, false);
				holder = new ViewHolder();
				holder.image = (ImageView) convertView
						.findViewById(R.id.item_grida_image);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}

			if (position == Bimp.bmp.size()) {
				holder.image.setImageBitmap(BitmapFactory.decodeResource(
						getResources(), R.drawable.icon_addpic_unfocused));
				if (position == 9) {
					holder.image.setVisibility(View.GONE);
				}
			} else {
				holder.image.setImageBitmap(Bimp.bmp.get(position));
			}

			return convertView;
		}

		public class ViewHolder {
			public ImageView image;
		}

		Handler handler = new Handler() {
			public void handleMessage(Message msg) {
				switch (msg.what) {
				case 1:
					adapter.notifyDataSetChanged();
					break;
				}
				super.handleMessage(msg);
			}
		};

		public void loading() {
			new Thread(new Runnable() {
				public void run() {
					while (true) {
						if (Bimp.max == Bimp.drr.size()) {
							Message message = new Message();
							message.what = 1;
							handler.sendMessage(message);
							break;
						} else {
							try {
								String path = Bimp.drr.get(Bimp.max);
								System.out.println(path);
								Bitmap bm = Bimp.revitionImageSize(path);
								Bimp.bmp.add(bm);
								String newStr = path.substring(
										path.lastIndexOf("/") + 1,
										path.lastIndexOf("."));
								FileUtils.saveBitmap(bm, "" + newStr);
								Bimp.max += 1;
								Message message = new Message();
								message.what = 1;
								handler.sendMessage(message);
							} catch (IOException e) {

								e.printStackTrace();
							}
						}
					}
				}
			}).start();
		}
	}

	public String getString(String s) {
		String path = null;
		if (s == null)
			return "";
		for (int i = s.length() - 1; i > 0; i++) {
			s.charAt(i);
		}
		return path;
	}
	
	protected void onRestart() {
		adapter.update();
		super.onRestart();
	}

	public class PopupWindows extends PopupWindow {

		public PopupWindows(Context mContext, View parent) {

			View view = View
					.inflate(mContext, R.layout.item_popupwindows, null);
			view.startAnimation(AnimationUtils.loadAnimation(mContext,
					R.anim.fade_ins));
			LinearLayout ll_popup = (LinearLayout) view
					.findViewById(R.id.ll_popup);
			ll_popup.startAnimation(AnimationUtils.loadAnimation(mContext,
					R.anim.push_bottom_in_2));

			setWidth(LayoutParams.FILL_PARENT);
			setHeight(LayoutParams.FILL_PARENT);
			setBackgroundDrawable(new BitmapDrawable());
			setFocusable(true);
			setOutsideTouchable(true);
			setContentView(view);
			showAtLocation(parent, Gravity.BOTTOM, 0, 0);
			update();

			Button bt1 = (Button) view
					.findViewById(R.id.item_popupwindows_camera);
			Button bt2 = (Button) view
					.findViewById(R.id.item_popupwindows_Photo);
			Button bt3 = (Button) view
					.findViewById(R.id.item_popupwindows_cancel);
			bt1.setOnClickListener(new OnClickListener() {
				public void onClick(View v) {
					photo();
					dismiss();
				}
			});
			bt2.setOnClickListener(new OnClickListener() {
				public void onClick(View v) {
					Intent intent = new Intent(PublishedActivity.this,
							TestPicActivity.class);
					startActivity(intent);
					dismiss();
				}
			});
			bt3.setOnClickListener(new OnClickListener() {
				public void onClick(View v) {
					dismiss();
				}
			});

		}
	}

	private static final int TAKE_PICTURE = 0x000000;
	private String path = "";

	public void photo() {
		Intent openCameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		openCameraIntent.setAction(MediaStore.ACTION_IMAGE_CAPTURE);

		StringBuffer sDir = new StringBuffer();
		if (hasSDcard()) {
			sDir.append(Environment.getExternalStorageDirectory()
					+ "/MyPicture/");
		} else {
			String dataPath = Environment.getRootDirectory().getPath();
			sDir.append(dataPath + "/MyPicture/");
		}

		File fileDir = new File(sDir.toString());
		if (!fileDir.exists()) {
			fileDir.mkdirs();
		}
		File file = new File(fileDir,
				String.valueOf(System.currentTimeMillis()) + ".jpg");
		if (file.exists())
			file.delete();

		path = file.getPath();
		Uri imageUri = Uri.fromFile(file);
		openCameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
		startActivityForResult(openCameraIntent, TAKE_PICTURE);
	}

	public static boolean hasSDcard() {
		String status = Environment.getExternalStorageState();
		if (status.equals(Environment.MEDIA_MOUNTED)) {
			return true;
		} else {
			return false;
		}
	}

	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (requestCode) {
		case TAKE_PICTURE:
			if (Bimp.drr.size() < 9 && resultCode == -1) {
				Bimp.drr.add(path);
			}
			break;
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
		if ((Bimp.bmp.size() + 1) <= 1) {

		}
	}

	@Override
	public void onClick(View v) {
		if (v.getId() == R.id.fabiao_saidan) {
			for (int i = 0; i < Bimp.drr.size(); i++) {
				String Str = Bimp.drr.get(i).substring(
						Bimp.drr.get(i).lastIndexOf("/") + 1,
						Bimp.drr.get(i).lastIndexOf("."));
				listh.add(FileUtils.SDPATH + Str + ".JPEG");
			}
			// 高清的压缩图片全部就在 list 路径里面了
			// 高清的压缩过的 bmp 对象 都在 Bimp.bmp里面
			// 完成上传服务器后 .........
			for (int n = 0; n < listh.size(); n++) {
				sendPhoto(listh.get(n), n);
			}
			// FileUtils.deleteDir();
		}
		// Intent intent = new Intent(PublishedActivity.this ,
		// SaiDanAfterActivity.class);
		// startActivity(intent);
		// finish();
	}

	private String sendPhoto(String photoPath, int photoNum) {
		showLoadingDialog();
		final int num = photoNum;
		Log.i("result", "----photoPath----s-------" + photoPath);

		Bitmap bmp = BitmapFactory.decodeFile(photoPath);
		if (bmp == null) {
			return "";
		}
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
						successNum++;

						String image = "";
						String str = response.result;
						Log.i("result", "----上传成功----s-------" + str);
						JSONObject jsonObject;
						try {
							jsonObject = new JSONObject(str);
							code = jsonObject.optString("CODE");
							image = jsonObject.optString("ImageName");
							iamgeUrlList.add(image);
						} catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						if (code.equals("00")) {
							photoName = image;
							// selectPhoto(num, photoName);
							Log.i("result", "---------------------" + photoName);
						} else {
							T.ss("操作超时");
						}
						dismissLoadingDialog();
						if (successNum == listh.size()) {
							saiDan(iamgeUrlList);
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
		bit.compress(CompressFormat.JPEG, 20, bos);// 参数100表示不压缩
		byte[] bytes = bos.toByteArray();
		return Base64.encodeToString(bytes, Base64.DEFAULT);
	}

	/**
	 * 晒单
	 */
	private void saiDan(ArrayList<String> imageUrlList) {
		Log.i("result", "----dd-----------");

		RequestParams params = new RequestParams();
		HashMap<String, String> map = new HashMap<String, String>();

		map.put("username", MApplication.mSharedPref
				.getSharePrefString(SharedPrefConstant.USERNAME));
		map.put("goodsid", goodsId);
		if (et_saidan.getText().toString().length() != 0) {
			map.put("note", et_saidan.getText().toString());
		} else {
			map.put("note", "");
		}
		if (id == null) {
			id = "0";
			type = "0";
		} else {
			if (id.equals("")) {
				id = "0";
				type = "0";
			} else {
				type = "1";
			}
		}
		map.put("type", type);
		map.put("id", "" + id);
		for (int i = 0; i < imageUrlList.size(); i++) {
			map.put("imgurl" + (i + 1) + "", imageUrlList.get(i));
		}
		for (int j = imageUrlList.size(); j < 5; j++) {
			map.put("imgurl" + (j + 1) + "", "");
		}
		map.put("token", MApplication.mSharedPref
				.getSharePrefString(SharedPrefConstant.TOKEN));

		String json = JSON.toJSONString(map);
		Log.i("result", "----ddd----------->" + json);
		try {
			StringEntity bodyEntity = new StringEntity(json, "UTF-8");
			params.setBodyEntity(bodyEntity);
		} catch (UnsupportedEncodingException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		HttpUtils httpUtils = new HttpUtils();
		String url = MyUrls.ROOT_URL_INDIANA_ADD_SDRECORD;

		httpUtils.send(HttpMethod.POST, url, params,
				new RequestCallBack<String>() {

					@Override
					public void onFailure(HttpException arg0, String arg1) {
						// TODO Auto-generated method stub

						T.ss("操作超时");
					}

					@Override
					public void onSuccess(ResponseInfo<String> response) {
						// TODO Auto-generated method stub

						String str = response.result;
						Log.i("result", "----ddd-----------" + str);
						String code = "";
						String message = "";

						JSONObject obj;
						try {
							obj = new JSONObject(str);
							code = obj.optString("CODE");
							message = obj.optString("MESSAGE");

						} catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						if (code.equals("00")) {
							FileUtils.deleteDir();
							// noScrollgridview;
							Bimp.bmp.clear();
							Bimp.drr.clear();
							Bimp.max = 0;
							listh.clear();
							adapter.notifyDataSetChanged();
							Intent intent = new Intent(PublishedActivity.this,
									SaiDanAfterActivity.class);
							startActivity(intent);
							finish();
						} else {

						}
						T.ss(message);
						//
					}
				});
	}
}
