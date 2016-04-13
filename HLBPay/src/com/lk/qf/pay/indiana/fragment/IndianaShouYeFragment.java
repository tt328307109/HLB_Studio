package com.lk.qf.pay.indiana.fragment;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.entity.StringEntity;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioGroup;
import android.widget.ScrollView;
import android.widget.ImageView.ScaleType;
import android.widget.RadioGroup.OnCheckedChangeListener;

import com.alibaba.fastjson.JSON;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener2;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;
import com.lk.bhb.pay.R;
import com.lk.qf.pay.activity.FindPwdActivity;
import com.lk.qf.pay.fragment.BaseFragment;
import com.lk.qf.pay.golbal.MApplication;
import com.lk.qf.pay.golbal.MyUrls;
import com.lk.qf.pay.indiana.activity.IndianaGoodsInfoActivity;
import com.lk.qf.pay.indiana.activity.IndianaProtocolActivity;
import com.lk.qf.pay.indiana.adapter.IndianaGoodsListAdapter;
import com.lk.qf.pay.indiana.bean.IndianaGoodsInfo;
import com.lk.qf.pay.sharedpref.SharedPrefConstant;
import com.lk.qf.pay.tool.SystemBarTintManager;
import com.lk.qf.pay.tool.T;
import com.lk.qf.pay.utils.ImgOptions;
import com.lk.qf.pay.utils.MyGetStatusUtils;
import com.lk.qf.pay.wedget.MyClickListener;
import com.lk.qf.pay.wedget.view.AutoPagerView;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;

public class IndianaShouYeFragment extends BaseFragment implements
OnClickListener, OnRefreshListener2<ListView> {

	private View layoutView1;
	private AutoPagerView pagerView;
	// private PullToRefreshListView lv;
	private PullToRefreshListView lv;
	private List<IndianaGoodsInfo> list;
	private List<IndianaGoodsInfo> listReturn;
	private List<String> adList;
	private int page = 1;
	private Map<String, String> map;
	private IndianaGoodsInfo orderInfo;// 登录后返回的用户信息
	private IndianaGoodsListAdapter adapter;
	// private LinearLayout ll;
	private IndianaGoodsInfo info;
	private DisplayImageOptions options;
	private ImageLoader imageLoader;
	private ImageLoader imageLoaderVp;
	private String code = "", message = "", cmd = "All", orderCmd = "";
	private RadioGroup rg;
	private LinearLayout ll;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		layoutView1 = inflater.inflate(R.layout.indiana_shouye_fragment_layout,
				container, false);
		init();
		//		initGallery();
		return layoutView1;
	}

	private void init() {
		adList = new ArrayList<String>();
		imageLoader = ImageLoader.getInstance();
		imageLoader.init(ImageLoaderConfiguration.createDefault(getActivity()));
		options = ImgOptions.initImgOptions();
		rg = (RadioGroup) layoutView1.findViewById(R.id.indiana_rg_fragment);
		rg.setOnCheckedChangeListener(changeListener);

		// ll = (LinearLayout) layoutView1.findViewById(R.id.ll_main_indiana);
		// ll.setFitsSystemWindows(false);
		lv = (PullToRefreshListView) layoutView1
				.findViewById(R.id.ll_list_indiana_goods);
		list = new ArrayList<IndianaGoodsInfo>();
		postQueryGoodsListHttp();
		postQueryAdListHttp();

		adapter = new IndianaGoodsListAdapter(getActivity(), list, mListener,
				options, imageLoader);
		lv.setAdapter(adapter);
		lv.setOnItemClickListener(itemClickListener);
		lv.setOnRefreshListener(this);

		//		//沉浸式状态栏
		//		SystemBarTintManager tintManager = new SystemBarTintManager(
		//				getActivity());
		//		tintManager.setStatusBarTintEnabled(true);
		//		tintManager.setStatusBarTintResource(R.color.actionsheet_gray);
		//		layoutView1.findViewById(R.id.auto_pagerview_indiana_shouye).setPadding(0,
		//				MyGetStatusUtils.getStatusBarHeight(getActivity()), 0, 0);
	}

	OnItemClickListener itemClickListener = new OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
				long arg3) {
			// TODO Auto-generated method stub
			IndianaGoodsInfo info = (IndianaGoodsInfo) arg0
					.getItemAtPosition(arg2);
			Intent intent = new Intent(getActivity(),
					IndianaGoodsInfoActivity.class);
			intent.putExtra("goodsInfo", info);
			intent.putExtra("action", "shouye");
			Log.i("result",
					"-----------goodsInfo--------" + info.getGoodsTotal());
			startActivity(intent);
		}
	};

	OnCheckedChangeListener changeListener = new OnCheckedChangeListener() {

		@Override
		public void onCheckedChanged(RadioGroup arg0, int checkedId) {
			// TODO Auto-generated method stub
			switch (checkedId) {
			case R.id.indiana_1:
				cmd = "All";
				break;
			case R.id.indiana_2:
				cmd = "Newest";
				break;
			case R.id.indiana_3:
				cmd = "GetOrderCion";
				orderCmd = "ASC";
				break;
			case R.id.indiana_4:
				cmd = "GetOrderCion";
				orderCmd = "DESC";
				break;
			}
			postQueryGoodsListHttp();
		}
	};
	/**
	 * listview中button的事件
	 */
	private MyClickListener mListener = new MyClickListener() {
		@Override
		public void myOnClick(int position, View v) {
			IndianaGoodsInfo pInfo = list.get(position);
			switch (v.getId()) {
			case R.id.ib_add_buycar_item:
				addToBuyCar(pInfo.getGoodsId(), 1, 1, "Add");
				break;
			case R.id.img_indiana_goods_icon:
				Intent intent = new Intent(getActivity(),
						IndianaGoodsInfoActivity.class);
				intent.putExtra("goodsInfo", pInfo);
				intent.setAction("shouye");
				Log.i("result",
						"-----------goodsInfo--ad------"
								+ pInfo.getGoodsTotal());
				startActivity(intent);
				break;

			default:
				break;
			}

		}

	};


	private void postQueryAdListHttp() {    
		RequestParams params = new RequestParams();
		String url = MyUrls.VPLIST;
		map = new HashMap<String, String>();
		map.put("type", "02");
		String json = JSON.toJSONString(map);
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
//				T.ss("查询失败");
				SharedPreferences sp = getActivity().getSharedPreferences("VPList", Context.MODE_PRIVATE);
				adList.clear();
				int size = sp.getInt("Status_size", 0);
				if(size != 0) {
					for(int i=0;i<size;i++) {  
						adList.add(sp.getString("Status_" + i, null));
					}  
					initGallery2();
				}
			}

			@Override
			public void onSuccess(ResponseInfo<String> response) {
				// TODO Auto-generated method stub
				String strReturnLogin = response.result;
				Log.i("result", "---------------定单-returnjson---"
						+ strReturnLogin);
				jsonDetailVp(strReturnLogin);

				if (code.equals("00")) {
					if (listReturn == null || listReturn.size() == 0) {
						// T.ss(getResources().getString(
						// R.string.query_nothing_more));
						// finish();
					} else { }
				} else {
					T.ss(message);

				}
			}
		});
	}

	/**
	 * 获取商品列表
	 */
	private void postQueryGoodsListHttp() {
		showLoadingDialog();
		RequestParams params = new RequestParams();
		String url = MyUrls.ROOT_URL_INDIANA_LIST;
		map = new HashMap<String, String>();
		map.put("username", MApplication.mSharedPref
				.getSharePrefString(SharedPrefConstant.USERNAME));
		map.put("pageSize", "10");
		map.put("page", "" + page);
		map.put("Cmd", cmd);
		map.put("OrderCmd", orderCmd);
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
				T.ss("操作超时");
				dismissLoadingDialog();
				lv.onRefreshComplete();
			}

			@Override
			public void onSuccess(ResponseInfo<String> response) {
				// TODO Auto-generated method stub
				String strReturnLogin = response.result;
				Log.i("result", "---------------定单-returnjson---"
						+ strReturnLogin);
				jsonDetail(strReturnLogin);
				Log.i("result", "---------------11---");

				if (code.equals("00")) {
					if (listReturn == null || listReturn.size() == 0) {
						// T.ss(getResources().getString(
						// R.string.query_nothing_more));
						// finish();
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
					T.ss(message);

				}
				lv.onRefreshComplete();
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
			code = obj.optString("CODE");
			message = obj.optString("MESSAGE");
			Log.i("result", "---------22-------");
			listReturn = new ArrayList<IndianaGoodsInfo>();
			int count = obj.optInt("Total");
			if (count < 10) {
				lv.setMode(Mode.PULL_DOWN_TO_REFRESH);
			}
			if (count > 0) {
				for (int i = 0; i < count; i++) {
					String goodsName = obj.optJSONArray("date")
							.optJSONObject(i).optString("indiana_name");// 商品名
					String goodsId = obj.optJSONArray("date").optJSONObject(i)
							.optString("id");// id
					int goodsTotalNum = obj.optJSONArray("date")
							.optJSONObject(i).optInt("count");// 总数
					int goodsBuyNum = obj.optJSONArray("date").optJSONObject(i)
							.optInt("current_count");// 已购买
					String imgUrl = obj.optJSONArray("date").optJSONObject(i)
							.optString("imgurl1");// 图片
					String imgUrl2 = obj.optJSONArray("date").optJSONObject(i)
							.optString("imgurl2");// 图片
					String imgUrl3 = obj.optJSONArray("date").optJSONObject(i)
							.optString("imgurl3");// 图片
					String imgUrl4 = obj.optJSONArray("date").optJSONObject(i)
							.optString("imgurl4");// 图片
					String imgUrl5 = obj.optJSONArray("date").optJSONObject(i)
							.optString("imgurl5");// 图片
					int shengyu = goodsTotalNum - goodsBuyNum;
					// 给info设置数据
					IndianaGoodsInfo info = new IndianaGoodsInfo();
					info.setGoodsName(goodsName);
					info.setGoodsId(goodsId);
					info.setGoodsTotal(goodsTotalNum);
					info.setRemainingNum(shengyu);
					info.setBoughtNum(goodsBuyNum);
					info.setImgUrl(imgUrl);
					info.setImgUrl2(imgUrl2);
					info.setImgUrl3(imgUrl3);
					info.setImgUrl4(imgUrl4);
					info.setImgUrl5(imgUrl5);
					listReturn.add(info);
				}
			}

		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

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
				try {
					obj = new JSONObject(strReturnLogin);
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				code = obj.optString("CODE");
				message = obj.optString("MESSAGE");

				if (code.equals("00")) {
					T.ss("已加入购物车");
				} else {
					T.ss(message);
				}
				dismissLoadingDialog();
			}
		});
	}


	@Override
	public void onClick(View arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onPullDownToRefresh(
			com.handmark.pulltorefresh.library.PullToRefreshBase<ListView> refreshView) {
		// TODO Auto-generated method stub
		page = 1;
		postQueryGoodsListHttp();
	}

	@Override
	public void onPullUpToRefresh(
			com.handmark.pulltorefresh.library.PullToRefreshBase<ListView> refreshView) {
		// TODO Auto-generated method stub
		page++;
		postQueryGoodsListHttp();
	}
	
	public class CropSquareTransformation implements Transformation {  
		   
		  @Override 
		  public Bitmap transform(Bitmap source) {  
		   
		    int size = Math.min(source.getWidth(), source.getHeight());  
		   
		   
		    Bitmap result = Bitmap.createBitmap(source);
		   
		    if (result != source) {  
		   
		      source.recycle();  
		   
		    }  
		   
		    return result;  
		   
		  }  
		   
		  
		  @Override public String key() { return "square()"; }  
		   
		}  

	
	/**
	 * 初始化viewPager
	 */
	private void initGallery() {
		List<View> pagePic = new ArrayList<View>();
		for (int i = 0; i < adList.size(); i++) {
			ImageView imageview = new ImageView(getActivity());

			Picasso.with(getActivity()).load(MyUrls.ROOT_URL2 + adList.get(i)).fit().into(imageview);
			
			//把List保存到sharedPref中
			SharedPreferences sp = getActivity().getSharedPreferences("VPList", Context.MODE_PRIVATE);
			SharedPreferences.Editor editor = sp.edit();
			editor.clear().commit();
			editor.putInt("Status_size", adList.size());
			for(int j =0 ; j < adList.size() ; j ++) {
//				editor.remove("Status_" + i);
				editor.putString("Status_" + j, adList.get(j));
			}
			editor.commit();
			pagePic.add(imageview);
		}
		pagerView = (AutoPagerView) layoutView1
				.findViewById(R.id.auto_pagerview_indiana_shouye);
		pagerView.setPageViewPics(pagePic);
		pagerView.setOnTouchListener(onTouch);
	}


	OnTouchListener onTouch = new OnTouchListener() {

		@Override
		public boolean onTouch(View v, MotionEvent event) {

			return true;
		}
	};
	/**
	 * 联网失败的时候这样初始化vp
	 */
	private void initGallery2() {
		List<View> pagePic = new ArrayList<View>();
		for (int i = 0; i < adList.size(); i++) {
			ImageView imageview = new ImageView(getActivity());
			Picasso.with(getActivity()).load(MyUrls.ROOT_URL2 + adList.get(i)).fit().into(imageview);
			
			pagePic.add(imageview);
		}
		pagerView = (AutoPagerView) layoutView1
				.findViewById(R.id.auto_pagerview_indiana_shouye);
		pagerView.setPageViewPics(pagePic);
		pagerView.setOnTouchListener(onTouch);
	}
	
	/**
	 * 解析 Json字符串 登录返回结果
	 * 
	 * @param str
	 * @return
	 */
	private void jsonDetailVp(String str) {

		try {
			JSONObject obj = new JSONObject(str);
			code = obj.optString("CODE");
			message = obj.optString("MESSAGE");
			Log.i("result", "---------22-------");
			//			adList = new ArrayList<String>();
			int count = obj.optInt("count");
			if (count > 0) {
				for (int i = 0; i < count; i++) {	
					String imgUrl1 = obj.optJSONArray("date").optJSONObject(i)
							.optString("image1");// 图片
					String imgUrl2 = obj.optJSONArray("date").optJSONObject(i)
							.optString("image2");// 图片
					String imgUrl3 = obj.optJSONArray("date").optJSONObject(i)
							.optString("image3");// 图片
					String imgUrl4 = obj.optJSONArray("date").optJSONObject(i)
							.optString("image4");// 图片
					String imgUrl5 = obj.optJSONArray("date").optJSONObject(i)
							.optString("image5");// 图片
					String imgUrl6 = obj.optJSONArray("date").optJSONObject(i)
							.optString("image6");// 图片

					String note = obj.optJSONArray("date").optJSONObject(i)
							.optString("note");// note
					String type = obj.optJSONArray("date").optJSONObject(i)
							.optString("type");// type

					String id = obj.optJSONArray("date").optJSONObject(i)
							.optString("id");// id
					if(imgUrl1.length() > 0) {
						adList.add(imgUrl1);
					}
					if(imgUrl2.length() > 0) {
						adList.add(imgUrl2);
					}
					if(imgUrl3.length() > 0) {
						adList.add(imgUrl3);
					}
					if(imgUrl4.length() > 0) {
						adList.add(imgUrl4);
					}
					if(imgUrl5.length() > 0) {
						adList.add(imgUrl5);
					}
					if(imgUrl6.length() > 0) {
						adList.add(imgUrl6);
					}
					initGallery();
					//					adList.add(imgUrl2);
					//					adList.add(imgUrl3);
					//					adList.add(imgUrl4);
					//					adList.add(imgUrl5);
					//					adList.add(imgUrl6);
				}
			}

		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
}
