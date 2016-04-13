package com.lk.qf.pay.indiana.fragment;

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
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.alibaba.fastjson.JSON;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener2;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;
import com.lk.bhb.pay.R;
import com.lk.qf.pay.fragment.BaseFragment;
import com.lk.qf.pay.golbal.MyUrls;
import com.lk.qf.pay.indiana.activity.IndianaGoodsInfoActivity;
import com.lk.qf.pay.indiana.adapter.IndianaLatesAnnouncedAdapter;
import com.lk.qf.pay.indiana.bean.IndianaGoodsInfo;
import com.lk.qf.pay.tool.SystemBarTintManager;
import com.lk.qf.pay.tool.T;
import com.lk.qf.pay.utils.ImgOptions;
import com.lk.qf.pay.utils.MyGetStatusUtils;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

public class LatestAnnouncedFragment extends BaseFragment implements
		OnRefreshListener2<ListView> {

	private View layoutView;
	private PullToRefreshListView lv;
	private List<IndianaGoodsInfo> list;
	private List<IndianaGoodsInfo> listReturn;
	private int page = 1;
	private int pageSize = 6;
	private Map<String, String> map;
	private IndianaGoodsInfo orderInfo;// 登录后返回的用户信息
	private IndianaLatesAnnouncedAdapter adapter;
	private DisplayImageOptions options;
	private ImageLoader imageLoader;
	private String code = "", message = "";
	private static String strShenyuTime;
	private LinearLayout ll;
	int result = 0;
	private int nowResult = 0;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		layoutView = inflater.inflate(R.layout.lates_announce_fragment_layout,
				container, false);
		init();
		return layoutView;
	}

	private void init() {
		imageLoader = ImageLoader.getInstance();
		imageLoader.init(ImageLoaderConfiguration.createDefault(getActivity()));
		options = ImgOptions.initImgOptions();
		ll = (LinearLayout) layoutView
				.findViewById(R.id.ll_indiana_announce_default);
		lv = (PullToRefreshListView) layoutView
				.findViewById(R.id.ll_indiana_latestAnnounce_listview);
		list = new ArrayList<IndianaGoodsInfo>();
		adapter = new IndianaLatesAnnouncedAdapter(getActivity(), list,
				options, imageLoader);
		lv.setAdapter(adapter);
		lv.setOnItemClickListener(itemClickListener);
		lv.setOnRefreshListener(this);

		// 沉浸式状态栏
//		SystemBarTintManager tintManager = new SystemBarTintManager(
//				getActivity());
//		tintManager.setStatusBarTintEnabled(true);
//		tintManager.setStatusBarTintResource(R.color.titleBar_yellow);
		layoutView.findViewById(R.id.ll_indiana_latest).setPadding(0,
				MyGetStatusUtils.getStatusBarHeight(getActivity()), 0, 0);
	}

	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		Log.i("result", "------------onResume----------");
		list.clear();
		nowResult = 0;
		result = 0;
		page = 1;
		postAnnounceGoodsListHttp();
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
			intent.setAction("issueOpen");
			startActivity(intent);
		}
	};

	/**
	 * 获取商品列表
	 */
	private void postAnnounceGoodsListHttp() {
		showLoadingDialog();
		RequestParams params = new RequestParams();
		String url = MyUrls.ROOT_URL_WILL_LOTTERY;
		map = new HashMap<String, String>();
		map.put("pagesize", "" + pageSize);
		map.put("page", "" + page);
		String json = JSON.toJSONString(map);
		Log.i("result", "-------订单请求---d--" + json);
		Log.i("result", "-------url---d--" + url);

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

				if (code.equals("00")) {
					if (listReturn == null || listReturn.size() == 0) {
						if (page == 1) {
							ll.setVisibility(View.VISIBLE);
							lv.setVisibility(View.GONE);
						}
					} else {
						if (page == 1) {
							list.clear();
							list = listReturn;
						} else {
							list.addAll(listReturn);
						}
						start();
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
			int count = obj.optInt("count");
			Log.i("result", "---------Count-------" + count);
			if (count < pageSize) {
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
					String time = obj.optJSONArray("date").optJSONObject(i)
							.optString("imgurl2");// 毫秒开奖
					String winnerNum = obj.optJSONArray("date")
							.optJSONObject(i).optString("dbnum");// 中奖号
					time = time.substring(0, time.indexOf("."));
					// Log.i("result", "---------time-------==" + time);
					// long ltime = 60000;
					long ltime = Long.parseLong(time);
					String goodsType;
					if (ltime > 0) {
						goodsType = "";
					} else {
						goodsType = "jiexiao";
					}
					// Log.i("result", "---------ltime-------==" + ltime);
					String winnerName = obj.optJSONArray("date")
							.optJSONObject(i).optString("winner_name");// 中奖者
					String openTime = obj.optJSONArray("date").optJSONObject(i)
							.optString("lotterytime");// 开奖时间
					int shengyu = goodsTotalNum - goodsBuyNum;
					// 给info设置数据
					IndianaGoodsInfo info = new IndianaGoodsInfo();
					info.setGoodsName(goodsName);
					info.setGoodsId(goodsId);
					info.setGoodsTotal(goodsTotalNum);
					info.setType(goodsType);
					info.setRemainingNum(shengyu);
					info.setBoughtNum(goodsBuyNum);
					info.setImgUrl(imgUrl);
					info.setShengyuTime(ltime);
					info.setWinnerName(winnerName);
					info.setWinningNumber(winnerNum);
					info.setOpenTime(openTime);
					listReturn.add(info);
				}
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void start() {
		// Log.i("result", "===============list=======" + list.size());
		// Log.i("result", "================result=====" + result);
		Thread thread = new Thread() {
			public void run() {
				while (true) {
					try {
						if (list == null || result == list.size()) {
							// Log.i("result",
							// "------------list == null----------"
							// + (list == null ? 0 : 1));
							// Log.i("result",
							// "------------list == null--result--------"
							// + result);
							break;
						}
						sleep(71);
						for (int i = 0; i < list.size(); i++) {
							if (list.get(i).getShengyuTime() != 0) {
								// Log.i("TAG",
								// "------------getShengyuTime()------listitem----"
								// + list.get(i).getShengyuTime());
								if (list.get(i).getShengyuTime() <= 10
										|| 0 == list.get(i).getShengyuTime()) {
									// Log.i("result",
									// "------------getShengyuTime()----------");
									list.get(i).setShengyuTime(0);
									result++;
									nowResult = result;
									// Log.i("TAG",
									// "------------getShengyuTime()----------"
									// + result);
								} else {
									// Log.i("result",
									// "------------else----------");
									list.get(i).setShengyuTime(
											list.get(i).getShengyuTime() - 71);
								}
							}
						}
						mHander.sendEmptyMessage(0);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
		};
		thread.start();
	}

	Handler mHander = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			adapter.sendSata(list);
			adapter.notifyDataSetChanged();
		}
	};

	@Override
	public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
		// TODO Auto-generated method stub
		list.clear();
		nowResult = 0;
		result = 0;
		page = 1;
		postAnnounceGoodsListHttp();
	}

	@Override
	public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
		// TODO Auto-generated method stub
		result = nowResult;
		page++;
		postAnnounceGoodsListHttp();
	}

}
