package com.lk.qf.pay.indiana.activity;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.apache.http.entity.StringEntity;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;
import com.lk.bhb.pay.R;
import com.lk.qf.pay.aanewactivity.AddDaiLiMerchantActivity;
import com.lk.qf.pay.activity.BaseActivity;
import com.lk.qf.pay.golbal.MApplication;
import com.lk.qf.pay.golbal.MyUrls;
import com.lk.qf.pay.indiana.bean.IndianaGoodsInfo;
import com.lk.qf.pay.indiana.saidan.PublishedActivity;
import com.lk.qf.pay.sharedpref.SharedPrefConstant;
import com.lk.qf.pay.tool.T;
import com.lk.qf.pay.utils.ImgOptions;
import com.lk.qf.pay.wedget.CommonTitleBarYellow;
import com.lk.qf.pay.wedget.customdialog.AlertDialog;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

public class WinnerInfoActivity extends IndianaBaseActivity implements OnClickListener {

	private CommonTitleBarYellow title;
	private TextView tvUserName, tvAddress, tvPhone, tvGoodsName, tvTotalNum,
			tvWinnerNum, tvCanyuNum, tvTime;
	private ImageView img;
	private TextView tvSetp2, tvSetp3, tvSetp4, tvSetp5, tvSetpType2,
			tvSetpType3, tvSetpType4, tvSetpType5,tvLogisticsName,tvLogisticsNum;
	private ImageView imgSetp2, imgSetp3, imgSetp4, imgSetp5, imgSetp22,
	imgSetp33, imgSetp44;
	private IndianaGoodsInfo info;
	private IndianaGoodsInfo infoGoods;
	private String goodsId, action, addressId, imgUrl,sdState;
	private Button btn;
	private String state;
	private DisplayImageOptions options;
	private ImageLoader imageLoader;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.winner_info_type_layout);
		init();
	}

	private void init() {
		imageLoader = ImageLoader.getInstance();
		imageLoader.init(ImageLoaderConfiguration.createDefault(this));
		options = ImgOptions.initImgOptions();
		tvUserName = (TextView) findViewById(R.id.tv_winner_info_name);
		tvAddress = (TextView) findViewById(R.id.tv_winner_info_address);
		tvPhone = (TextView) findViewById(R.id.tv_winner_info_phone);
		tvGoodsName = (TextView) findViewById(R.id.tv_winner_info_goodsName);
		tvTotalNum = (TextView) findViewById(R.id.tv_winner_info_totalNum);
		tvWinnerNum = (TextView) findViewById(R.id.tv_winner_info_winnerNum);
		tvCanyuNum = (TextView) findViewById(R.id.tv_winner_info_canyuNum);
		tvLogisticsName = (TextView) findViewById(R.id.tv_winner_info_logisticsName);
		tvLogisticsNum = (TextView) findViewById(R.id.tv_winner_info_logisticsNum);
		tvTime = (TextView) findViewById(R.id.tv_winner_info_openTime);
		btn = (Button) findViewById(R.id.btn_winnerInfo_ok);
		img = (ImageView) findViewById(R.id.img_winner_info);
		imgSetp2 = (ImageView) findViewById(R.id.img_winnerInfo_tep2);
		imgSetp22 = (ImageView) findViewById(R.id.img_winnerInfo_tep22);
		imgSetp3 = (ImageView) findViewById(R.id.img_winnerInfo_tep3);
		imgSetp33 = (ImageView) findViewById(R.id.img_winnerInfo_tep33);
		imgSetp4 = (ImageView) findViewById(R.id.img_winnerInfo_tep4);
		imgSetp44 = (ImageView) findViewById(R.id.img_winnerInfo_tep44);
		imgSetp5 = (ImageView) findViewById(R.id.img_winnerInfo_tep5);

		tvSetp2 = (TextView) findViewById(R.id.tv_winnerInfo_tep2);
		tvSetp3 = (TextView) findViewById(R.id.tv_winnerInfo_tep3);
		tvSetp4 = (TextView) findViewById(R.id.tv_winnerInfo_tep4);
		tvSetp5 = (TextView) findViewById(R.id.tv_winnerInfo_tep5);
		tvSetpType2 = (TextView) findViewById(R.id.tv_winnerInfo_time_tep2);
		tvSetpType3 = (TextView) findViewById(R.id.tv_winnerInfo_time_tep3);
		tvSetpType4 = (TextView) findViewById(R.id.tv_winnerInfo_time_tep4);
		tvSetpType5 = (TextView) findViewById(R.id.tv_winnerInfo_time_tep5);

		Intent intent = getIntent();
		if (intent != null) {

			info = intent.getParcelableExtra("info");
			infoGoods = intent.getParcelableExtra("infoGoods");
			if (infoGoods != null) {
				action = intent.getAction();
				tvGoodsName.setText("" + infoGoods.getGoodsName());
				tvTotalNum.setText("" + infoGoods.getGoodsTotal());
				tvWinnerNum.setText(infoGoods.getWinningNumber());
				tvCanyuNum.setText("" + infoGoods.getBoughtNum());
				tvTime.setText(infoGoods.getOpenTime());
				goodsId = infoGoods.getGoodsId();
				imgUrl = infoGoods.getImgUrl();
				sdState = infoGoods.getIsxd();
				
				Log.i("result", "---------------action-----------" + action);
				if (action != null && !action.equals("win")) {
//					btn.setVisibility(View.VISIBLE);
					btn.setText("确认收货");
					getAddress();
					// tvSetp3.setTextColor(getResources().getColor(
					// R.color.winner_goods_yellow));
				} else {
					if (info != null) {
//						btn.setVisibility(View.VISIBLE);
						btn.setText("确认地址");
						tvUserName.setText(info.getUserName());
						tvAddress.setText(info.getUserAddress());
						tvPhone.setText(info.getUserPhoneNum());
						addressId = info.getUserAddressId();
						imgUrl = info.getImgUrl();
						Log.i("result", "---------------addressId-----------"
								+ addressId);
						tvSetp2.setTextColor(getResources().getColor(
								R.color.winner_goods_yellow));
					}
				}

			}
			if (imgUrl == null || imgUrl.equals("")) {
				// imageLoader.displayImage(MyUrls.ROOT_URL2 +
				// "/bank/yl.png",
				// img, options);
			} else {
				imageLoader.displayImage(MyUrls.ROOT_URL2 + imgUrl, img,
						options);
			}
		}
		title = (CommonTitleBarYellow) findViewById(R.id.titlebar_my_winnerInfo);
		title.setActName("奖品详情");
		title.setCanClickDestory(this, true);
		findViewById(R.id.btn_winnerInfo_ok).setOnClickListener(this);
	}

	@Override
	public void onClick(View arg0) {
		// TODO Auto-generated method stub
		if (action != null && action.equals("win")) {
			addDuihuanAddress();
		} else {
			if (state.equals("dh")) {
				Intent intent = new Intent(WinnerInfoActivity.this,
						PublishedActivity.class);
				intent.putExtra("goodsId", goodsId);
				startActivity(intent);
			}else{
				confirmGetGoods();
			}
		}
	}

	/**
	 * 查看中奖地址
	 */
	private void getAddress() {
		showLoadingDialog();

		RequestParams params = new RequestParams();
		String url = MyUrls.ROOT_URL_ADRESSVIEW;
		Map<String, String> map = new HashMap<String, String>();
		map.put("goods", goodsId);
		String json = JSON.toJSONString(map);
		Log.i("result", "----ddd-查看中奖地址---s-------" + json);
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
				Log.i("result", "----保存成功----s-------" + str);

				JSONObject obj = null;
				try {
					obj = new JSONObject(str);
					code = obj.optString("CODE");
					message = obj.optString("MESSAGE");
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				if (code.equals("00")) {
					int count = obj.optInt("count");
					if (count > 0) {
						for (int i = 0; i < count; i++) {

							tvUserName.setText(obj.optJSONArray("date")
									.optJSONObject(i).optString("realname"));
							String province = obj.optJSONArray("date")
									.optJSONObject(i).optString("pro");
							String city = obj.optJSONArray("date")
									.optJSONObject(i).optString("city");
							String clazz = obj.optJSONArray("date")
									.optJSONObject(i).optString("clazz");
							String courier = obj.optJSONArray("date")
									.optJSONObject(i).optString("courier");//物流名
							String logisticsnum = obj.optJSONArray("date")
									.optJSONObject(i).optString("logisticsnum");//物流单号
							if (courier!=null && !courier.equals("")) {
								tvLogisticsName.setVisibility(View.VISIBLE);
								tvLogisticsName.setText(courier);
							}
							if (logisticsnum!=null && !logisticsnum.equals("")) {
								tvLogisticsNum.setVisibility(View.VISIBLE);
								tvLogisticsNum.setText(logisticsnum);
							}
							
							tvAddress.setText(province + city + clazz);
							tvPhone.setText(obj.optJSONArray("date")
									.optJSONObject(i).optString("phone"));
							state = obj.optJSONArray("date").optJSONObject(i)
									.optString("state");
//							Log.i("result", "-----------state-----"+state);
//							Log.i("result", "-----------sdState-----"+sdState);
							if (sdState.equals("3")) {
								state = "wc";
							}
//							Log.i("result", "-----------state--s---"+state);
							judgeProgress(state);
							
						}
					}
				} else {
					T.ss(message);
				}
				dismissLoadingDialog();
			}
		});
	}
	
	/**
	 * 判断状态  并显示
	 * @param state
	 */
	private void judgeProgress(String state){
		if (state.equals("xd")) {
			tvSetp3.setTextColor(getResources().getColor(
					R.color.winner_goods_yellow));
			tvSetpType3.setTextColor(getResources()
					.getColor(R.color.winner_goods_yellow));
			tvSetpType3.setText("待发货");
			imgSetp2.setBackgroundResource(R.drawable.indiana_dian_1);
			imgSetp22.setBackgroundResource(R.drawable.indiana_jinduxian_1);
			imgSetp3.setBackgroundResource(R.drawable.indiana_dian_2);
		} else if (state.equals("fh")) {
			tvSetp3.setTextColor(getResources().getColor(
					R.color.winner_goods_yellow));
			tvSetpType3.setTextColor(getResources()
					.getColor(R.color.winner_goods_yellow));
			tvSetpType3.setText("已发货");
			imgSetp2.setBackgroundResource(R.drawable.indiana_dian_1);
			imgSetp22.setBackgroundResource(R.drawable.indiana_jinduxian_1);
			imgSetp3.setBackgroundResource(R.drawable.indiana_dian_2);
		} else if (state.equals("dh")) {
//			btn.setVisibility(View.VISIBLE);
			btn.setText("晒单");
			tvSetp4.setTextColor(getResources().getColor(
					R.color.winner_goods_yellow));
			tvSetpType4.setTextColor(getResources()
					.getColor(R.color.winner_goods_yellow));
			tvSetpType4.setText("已收货");
			
			imgSetp2.setBackgroundResource(R.drawable.indiana_dian_1);
			imgSetp22.setBackgroundResource(R.drawable.indiana_jinduxian_1);
			imgSetp3.setBackgroundResource(R.drawable.indiana_dian_1);
			imgSetp33.setBackgroundResource(R.drawable.indiana_jinduxian_1);
			imgSetp4.setBackgroundResource(R.drawable.indiana_dian_2);

		} else {
			btn.setVisibility(View.GONE);
			tvSetp5.setTextColor(getResources().getColor(
					R.color.winner_goods_yellow));
			imgSetp2.setBackgroundResource(R.drawable.indiana_dian_1);
			imgSetp22.setBackgroundResource(R.drawable.indiana_jinduxian_1);
			imgSetp3.setBackgroundResource(R.drawable.indiana_dian_1);
			imgSetp33.setBackgroundResource(R.drawable.indiana_jinduxian_1);
			imgSetp4.setBackgroundResource(R.drawable.indiana_dian_1);
			imgSetp44.setBackgroundResource(R.drawable.indiana_jinduxian_1);
			imgSetp5.setBackgroundResource(R.drawable.indiana_dian_2);
		}
	}

	/**
	 * 确认中奖地址
	 */
	private void addDuihuanAddress() {
		showLoadingDialog();

		RequestParams params = new RequestParams();
		String url = MyUrls.ROOT_URL_DELIVERYEDIT;
		Map<String, String> map = new HashMap<String, String>();
		map.put("username", MApplication.mSharedPref
				.getSharePrefString(SharedPrefConstant.USERNAME));
		map.put("address", addressId);
		map.put("goods", goodsId);
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
				Log.i("result", "----保存成功----s-------" + str);
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
					Intent intent = new Intent(WinnerInfoActivity.this,
							WinnerRecordActivity.class);
					startActivity(intent);
					finish();
				}
				dismissLoadingDialog();
			}
		});
	}

	/**
	 * 确认收货
	 */
	private void confirmGetGoods() {
		showLoadingDialog();

		RequestParams params = new RequestParams();
		String url = MyUrls.ROOT_URL_QURENSHOUHUO;
		Map<String, String> map = new HashMap<String, String>();
		map.put("username", MApplication.mSharedPref
				.getSharePrefString(SharedPrefConstant.USERNAME));
		map.put("goods", goodsId);
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
				Log.i("result", "----保存成功----s-------" + str);
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
					isShaidan();
					getAddress();
				}
				dismissLoadingDialog();
			}
		});
	}
	/**
	 * 是否晒单
	 */
	private void isShaidan() {

		new AlertDialog(WinnerInfoActivity.this).builder().setTitle("晒单成功送10个快易币")
				.setMsg("是否立即晒单?")
				.setPositiveButton("确定", new OnClickListener() {
					@Override
					public void onClick(View v) {
						Intent intent = new Intent(WinnerInfoActivity.this,
								PublishedActivity.class);
						intent.putExtra("goodsId", goodsId);
						startActivity(intent);
					}
				}).setNegativeButton("取消", new OnClickListener() {
					@Override
					public void onClick(View v) {

					}
				}).show();
	}

}
