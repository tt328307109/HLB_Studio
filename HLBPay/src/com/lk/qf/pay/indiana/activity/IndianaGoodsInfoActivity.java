package com.lk.qf.pay.indiana.activity;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.http.entity.StringEntity;
import org.json.JSONException;
import org.json.JSONObject;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.ImageView.ScaleType;

import com.alibaba.fastjson.JSON;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener2;
import com.handmark.pulltorefresh.library.PullToRefreshScrollView;
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
import com.lk.qf.pay.indiana.adapter.IndianaParticipantsAdapter;
import com.lk.qf.pay.indiana.bean.IndianaGoodsInfo;
import com.lk.qf.pay.sharedpref.SharedPrefConstant;
import com.lk.qf.pay.tool.T;
import com.lk.qf.pay.utils.ImgOptions;
import com.lk.qf.pay.utils.PercentUtils;
import com.lk.qf.pay.wedget.CommonTitleBarYellow;
import com.lk.qf.pay.wedget.view.AutoPagerView;
import com.lk.qf.pay.wedget.view.ListViewForScrollView;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

public class IndianaGoodsInfoActivity extends IndianaBaseActivity implements
		OnClickListener, OnRefreshListener2<ScrollView> {

	private ListViewForScrollView lv;
	// private ScrollView mScrollView;
	private PullToRefreshScrollView mPullScrollView;
	private DisplayImageOptions options;
	private ImageLoader imageLoader;
	private List<IndianaGoodsInfo> list;
	private List<IndianaGoodsInfo> listReturn;
	private int page = 1;
	private int pageSize = 15;
	private Map<String, String> map;
	private IndianaGoodsInfo goodsInfo;// 登录后返回的用户信息
	private IndianaParticipantsAdapter adapter;
	private LinearLayout ll;
	private IndianaGoodsInfo info;
	private CommonTitleBarYellow title;
	private TextView tvGoodsName, tvGoodsType, tvGoodsTotal, tvGoodsShengyu;// 商品名,商品开奖状态,商品总需,剩余
	private TextView tvWinnerName, tvWinnerPhoneNum, tvWinnerCanyuNum,
			tvLotteryTime, tvWinnerNum, tvIsBuy;// 中奖者名,中奖地址,中奖者参与此事,开奖时间,中奖号码
	private String goodsName, goodsType = "", goodsId, action;
	private RelativeLayout rlShowWinner, rlBottom;
	private ProgressBar pb;
	private int payTotalAccount, goodsTotal, goodsShengyu;
	private AutoPagerView pagerView;
	private boolean isFirstIn = true;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.indiana_goods_info_layout);
		init();
	}

	private void init() {
		imageLoader = ImageLoader.getInstance();
		imageLoader.init(ImageLoaderConfiguration.createDefault(this));
		options = ImgOptions.initImgOptions();

		pagerView = (AutoPagerView) findViewById(R.id.auto_pagerview_indiana_goodsInfo);

		title = (CommonTitleBarYellow) findViewById(R.id.titlebar_goodsinfo_title);
		title.setActName("奖品详情");
		title.setCanClickDestory(this, true);

		lv = (ListViewForScrollView) findViewById(R.id.ll_list_indiana_goods_info);
		tvGoodsName = (TextView) findViewById(R.id.tv_indiana_goods_info_name);
		tvGoodsType = (TextView) findViewById(R.id.tv_indiana_goods_info_type);
		tvGoodsTotal = (TextView) findViewById(R.id.tv_total_need_indiana_num);
		tvGoodsShengyu = (TextView) findViewById(R.id.tv_total_remaining_indiana_num);
		tvIsBuy = (TextView) findViewById(R.id.tv_indiana_goodsInfo_show);

		tvWinnerName = (TextView) findViewById(R.id.tv_indiana_winner_name);
		// tvWinnerPhoneNum = (TextView)
		// findViewById(R.id.tv_indiana_winner_phoneNUm);
		// tvWinnerCanyuNum = (TextView) findViewById(R.id.tv_indiana_info_Num);
		tvLotteryTime = (TextView) findViewById(R.id.tv_indiana_winner_time);
		tvWinnerNum = (TextView) findViewById(R.id.tv_indiana_winner_num);
		pb = (ProgressBar) findViewById(R.id.pb_indiana_goods_info);

		rlShowWinner = (RelativeLayout) findViewById(R.id.rl_indiana_show_winner);
		findViewById(R.id.ib_indiana_goods_info_addBuycar).setOnClickListener(
				this);// 加入购物车
		findViewById(R.id.btn_indiana_goods_info_lijicanyu).setOnClickListener(
				this);// 立即购买
		findViewById(R.id.ib_indiana_goodsInfo).setOnClickListener(this);// 到购物车
		// findViewById(R.id.rl_indiana_showdan).setOnClickListener(this);//
		// 往期晒单
		rlBottom = (RelativeLayout) findViewById(R.id.rl_goodsInfo_bottom);// 立即参与
		// rlIssueOpen = (RelativeLayout)
		// findViewById(R.id.rl_Indiana_wangqijiexiao);// 往期揭晓
		// rlIssueOpen.setOnClickListener(this);

		Intent intent = getIntent();
		if (intent != null) {
			// goodsId= intent.getStringExtra("goodsId");

			goodsInfo = intent.getParcelableExtra("goodsInfo");
			action = intent.getAction();
			if (goodsInfo != null) {
				Log.i("result", "-----------action-------------" + action);
				if (action != null) {
					if (action.equals("shouye")) {
						goodsId = goodsInfo.getGoodsId();

					} else if (action.equals("issueOpen")) {// 往期揭晓
						goodsId = goodsInfo.getGoodsId();
						// rlIssueOpen.setVisibility(View.GONE);
						rlBottom.setVisibility(View.GONE);
					} else {
						goodsId = goodsInfo.getGoodsId2();
					}
				}
			}
		}

		// if (goodsType == null || goodsType.equals("")) {
		// rlShowWinner.setVisibility(View.GONE);
		// } else {
		// rlShowWinner.setVisibility(View.VISIBLE);
		// }
		mPullScrollView = (PullToRefreshScrollView) findViewById(R.id.psl_goods_info);
		mPullScrollView.setOnRefreshListener(this);
		list = new ArrayList<IndianaGoodsInfo>();
		postQueryPublishHttp();

		adapter = new IndianaParticipantsAdapter(this, list, options,
				imageLoader);
		lv.setAdapter(adapter);
		mPullScrollView.setOnRefreshListener(this);

	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		queryGoodsInfo();
	}

	@Override
	public void onClick(View arg0) {
		// TODO Auto-generated method stub
		switch (arg0.getId()) {
		case R.id.ib_indiana_goods_info_addBuycar:
			addToBuyCar(goodsId, 1, 1, "Add");
			break;
		case R.id.btn_indiana_goods_info_lijicanyu:
			addToBuyCar(goodsId, 1, 1, "Add");
			Intent intent3 = new Intent(IndianaGoodsInfoActivity.this,
					IndianaBuyCarActivity.class);
			startActivity(intent3);
			break;
		case R.id.ib_indiana_goodsInfo:
			Intent intent = new Intent(IndianaGoodsInfoActivity.this,
					IndianaBuyCarActivity.class);
			startActivity(intent);
			break;
		// case R.id.rl_Indiana_wangqijiexiao:// 往期揭晓
		// Intent intent1 = new Intent(IndianaGoodsInfoActivity.this,
		// IndianaIssueOpenActivity.class);
		// startActivity(intent1);
		// break;
		// case R.id.rl_indiana_showdan:// 往期晒单
		// Intent intent2 = new Intent(IndianaGoodsInfoActivity.this,
		// IndianaBuyCarActivity.class);
		// startActivity(intent2);
		// break;

		default:
			break;
		}
	}

	/**
	 * 查询商品详情
	 * 
	 * @param goodsId
	 * 
	 */
	private void queryGoodsInfo() {
		showLoadingDialog();
		RequestParams params = new RequestParams();
		String url = MyUrls.ROOT_URL_INDIANA_GET_ORDER_DETAIL;
		map = new HashMap<String, String>();
		map.put("username", MApplication.mSharedPref
				.getSharePrefString(SharedPrefConstant.USERNAME));
		map.put("goodsId", goodsId);// 商品id
		map.put("Cmd", "GetDetail");
		map.put("token", MApplication.mSharedPref
				.getSharePrefString(SharedPrefConstant.TOKEN));
		String json = JSON.toJSONString(map);
		Log.i("result", "-------加入购物车请求-----" + json);
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
				String strReturnLogin = response.result;
				Log.i("result", "---------------定单-returnjson---"
						+ strReturnLogin);
				JSONObject obj = null;
				String code = "", message = "";
				try {
					obj = new JSONObject(strReturnLogin);
					code = obj.optString("CODE");
					message = obj.optString("MESSAGE");
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				if (code.equals("00")) {
					goodsId = obj.optJSONArray("date").optJSONObject(0)
							.optString("id");
					goodsName = obj.optJSONArray("date").optJSONObject(0)
							.optString("indiana_name");
					goodsTotal = obj.optJSONArray("date").optJSONObject(0)
							.optInt("count");
					String winnerNum = obj.optJSONArray("date")
							.optJSONObject(0).optString("dbnum");// 中奖号码
					int buyNum = obj.optJSONArray("date").optJSONObject(0)
							.optInt("current_count");// 已买
					tvGoodsName.setText(goodsName);
					tvGoodsTotal.setText("" + goodsTotal);
					tvGoodsShengyu.setText("" + (goodsTotal - buyNum));
					pb.setProgress(PercentUtils.getPercent(buyNum, goodsTotal));// 进度条
					String winnerName = obj.optJSONArray("date")
							.optJSONObject(0).optString("winner_name");// 中奖者名字
					String iswin = obj.optJSONArray("date").optJSONObject(0)
							.optString("iswin");
					if (winnerName.length() == 11) {
						winnerName = winnerName.substring(0, 3)
								+ "****"
								+ winnerName.substring(winnerName.length() - 4,
										winnerName.length());
					}

					tvWinnerName.setText("" + winnerName);
					tvWinnerNum.setText("幸运号码:" + winnerNum);
					tvLotteryTime.setText(obj.optJSONArray("date")
							.optJSONObject(0).optString("lotterytime"));// 开奖时间

					if (iswin != null && iswin.equals("0")) {
						rlBottom.setVisibility(View.VISIBLE);
						rlShowWinner.setVisibility(View.GONE);
						tvGoodsType.setText("进行中");
						tvGoodsType.setBackground(getResources().getDrawable(
								R.drawable.btn_jinxingzhong));
					} else if (iswin != null && iswin.equals("2")) {
						tvGoodsType.setText("已揭晓");
						rlShowWinner.setVisibility(View.VISIBLE);
						rlBottom.setVisibility(View.GONE);
					} else {
						tvGoodsType.setText("开奖中");
						rlShowWinner.setVisibility(View.GONE);
						rlBottom.setVisibility(View.GONE);
					}
					// 获取网络图片 去除为空的
					String[] imgUrls = {
							""
									+ obj.optJSONArray("date").optJSONObject(0)
											.optString("imgurl1"),
							""
									+ obj.optJSONArray("date").optJSONObject(0)
											.optString("imgurl2"),
							""
									+ obj.optJSONArray("date").optJSONObject(0)
											.optString("imgurl3"),
							""
									+ obj.optJSONArray("date").optJSONObject(0)
											.optString("imgurl4"),
							""
									+ obj.optJSONArray("date").optJSONObject(0)
											.optString("imgurl5"),
							""
									+ obj.optJSONArray("date").optJSONObject(0)
											.optString("imgurl6") };
					Log.i("result", "-------------imgUrl-----" + imgUrls[0]);

					List<String> imgUrlList = new ArrayList<String>();
					for (int i = 0; i < imgUrls.length; i++) {
						if (imgUrls[i] != null && !imgUrls[i].equals("")) {
							imgUrlList.add(imgUrls[i]);
							Log.i("result",
									"-------------imgUrls2--imgUrls[i]-----"
											+ imgUrls[i]);
							Log.i("result", "-------------imgUrls2--dd-----"
									+ imgUrlList.size());
						}
					}
					String[] imgUrls2 = (String[]) imgUrlList
							.toArray(new String[imgUrlList.size()]);
					if (imgUrls2.length == 0) {

					}
					if (isFirstIn) {

						initGallery(imgUrls2);
						isFirstIn = false;
					}
					// T.ss("已加入购物车");
				} else {
					T.ss(message);
				}
				dismissLoadingDialog();
			}
		});
	}

	/**
	 * 加入购物车
	 * 
	 * @param goodsId
	 *            商品id
	 * @param totalNum
	 *            需要支付的快易币
	 * @param goodsNum
	 *            购买的商品数量
	 * @param cmd
	 *            操作命令Add(添加),Updata(更新),Delete(删除)
	 */
	private void addToBuyCar(String goodsId, int totalNum, int goodsNum,
			String cmd) {
		showLoadingDialog();
		RequestParams params = new RequestParams();
		String url = MyUrls.ROOT_URL_INDIANA_CART;
		map = new HashMap<String, String>();
		map.put("username", MApplication.mSharedPref
				.getSharePrefString(SharedPrefConstant.USERNAME));
		map.put("gmCount", "" + goodsNum);// 购买商品数量
		map.put("goodsId", goodsId);// 商品id
		map.put("tbCoin", "1");// 商品单价
		map.put("totalCoin", "" + totalNum);// 需要支付的夺宝币
		map.put("Cmd", cmd);
		map.put("token", MApplication.mSharedPref
				.getSharePrefString(SharedPrefConstant.TOKEN));
		String json = JSON.toJSONString(map);
		Log.i("result", "-------加入购物车请求-----" + json);
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
				String strReturnLogin = response.result;
				Log.i("result", "---------------定单-returnjson---"
						+ strReturnLogin);
				JSONObject obj = null;
				String code = "", message = "";
				try {
					obj = new JSONObject(strReturnLogin);
					code = obj.optString("CODE");
					message = obj.optString("MESSAGE");
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				if (code.equals("00")) {
					T.ss("已加入购物车");
				} else {
					T.ss(message);
				}
				dismissLoadingDialog();
			}
		});
	}

	/**
	 * 查询购买此商品用户信息
	 */
	private void postQueryPublishHttp() {
		showLoadingDialog();
		RequestParams params = new RequestParams();
		String url = MyUrls.ROOT_URL_INDIANA_GOING_OR_PUBLISH;
		map = new HashMap<String, String>();
		map.put("username", MApplication.mSharedPref
				.getSharePrefString(SharedPrefConstant.USERNAME));
		map.put("pageSize", "" + pageSize);
		map.put("page", "" + page);
		map.put("Cmd", "BuyUserInfo");
		map.put("goodsId", goodsId);
		map.put("token", MApplication.mSharedPref
				.getSharePrefString(SharedPrefConstant.TOKEN));
		String json = JSON.toJSONString(map);
		Log.i("result", "-------订单请求-----" + json);
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
				// T.ss("查询失败,错误码:" + arg0.getExceptionCode());
				dismissLoadingDialog();
				mPullScrollView.onRefreshComplete();
			}

			@Override
			public void onSuccess(ResponseInfo<String> response) {
				// TODO Auto-generated method stub
				String strReturnLogin = response.result;
				Log.i("result", "---------------购买-的用户信息--" + strReturnLogin);
				jsonDetail(strReturnLogin);

				String returnCode = info.getCode();

				if (returnCode.equals("00")) {
					if (listReturn == null || listReturn.size() == 0) {
						// T.ss(getResources().getString(
						// R.string.query_nothing_more));
					} else {

						if (page == 1) {
							list.clear();
							list = listReturn;
						} else {
							list.addAll(listReturn);// 追加跟新的数据
						}
						adapter.sendSata(list);
						adapter.notifyDataSetChanged();
					}
				} else {
					T.ss(info.getMessage());

				}
				mPullScrollView.onRefreshComplete();
				dismissLoadingDialog();
			}
		});
	}

	/**
	 * 解析 Json字符串 登录返回结果
	 * 
	 * @param str
	 * @return
	 */
	private void jsonDetail(String str) {

		try {
			JSONObject obj = new JSONObject(str);
			info = new IndianaGoodsInfo();
			info.setCode(obj.optString("CODE"));
			info.setMessage(obj.optString("MESSAGE"));
			String isBuy = obj.optString("isBuy");
			if (isBuy.equals("True")) {
				tvIsBuy.setVisibility(View.GONE);
			}
			listReturn = new ArrayList<IndianaGoodsInfo>();
			int count = obj.optInt("Total");
			Log.i("result", "---------Count----tvIsBuy---" + count);
			if (count < 15) {
				mPullScrollView.setMode(Mode.PULL_DOWN_TO_REFRESH);
			}
			if (count > 0) {
				for (int i = 0; i < count; i++) {
					String payIp = obj.optJSONArray("date").optJSONObject(i)
							.optString("payIp");
					String userName = obj.optJSONArray("date").optJSONObject(i)
							.optString("userName");
					String buyTime = obj.optJSONArray("date").optJSONObject(i)
							.optString("payTime");
					String payAdderss = obj.optJSONArray("date")
							.optJSONObject(i).optString("payAdderss");

					int dbCount = obj.optJSONArray("date").optJSONObject(i)
							.optInt("dbCount");

					// 给info设置数据
					IndianaGoodsInfo info = new IndianaGoodsInfo();
					info.setUserName(userName);
					info.setBuyTime(buyTime);
					info.setUserCanyuNum(dbCount);
					info.setUserIP(payIp);
					info.setUserAddress(payAdderss);
					listReturn.add(info);
				}
			}

		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	/**
	 * 初始化viewPager
	 */
	private void initGallery(String[] imgUrls) {
		final String[] imgs = imgUrls;
		List<View> pagePic = new ArrayList<View>();
		if (imgs.length != 0) {
			for (int i = 0; i < imgs.length; i++) {
				ImageView imageview = new ImageView(this);
				imageview.setScaleType(ScaleType.FIT_XY);

				if (imgUrls[i] == null || imgUrls[i].equals("")) {
					// imageLoader.displayImage(MyUrls.ROOT_URL2 +
					// "/bank/yl.png",
					// img, options);
				} else {
					Log.i("result", "---------imgUrls---imgUrls------"
							+ imgUrls.length);
					Log.i("result", "---------imgUrls---imgUrls-1-----"
							+ imgUrls[i]);
					imageLoader.displayImage(MyUrls.ROOT_URL2 + imgUrls[i],
							imageview, options);
				}
				pagePic.add(imageview);
			}
		} else {
			ImageView imageview = new ImageView(this);
			imageview.setScaleType(ScaleType.FIT_XY);
			imageview.setImageResource(R.drawable.morentupian_2);
			pagePic.add(imageview);
		}
		pagerView.setPageViewPics(pagePic);
		// pagerView.setOnTouchListener(onTouch);
	}

	@Override
	public void onPullDownToRefresh(PullToRefreshBase<ScrollView> refreshView) {
		// TODO Auto-generated method stub
		page = 1;
		postQueryPublishHttp();
	}

	@Override
	public void onPullUpToRefresh(PullToRefreshBase<ScrollView> refreshView) {
		// TODO Auto-generated method stub
		page++;
		postQueryPublishHttp();
	}

}
