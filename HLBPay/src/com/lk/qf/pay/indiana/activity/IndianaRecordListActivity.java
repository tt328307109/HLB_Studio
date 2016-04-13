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
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;

import com.alibaba.fastjson.JSON;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener2;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;
import com.lk.bhb.pay.R;
import com.lk.qf.pay.activity.BaseActivity;
import com.lk.qf.pay.activity.swing.SwingHXCardActivity;
import com.lk.qf.pay.beans.PosData;
import com.lk.qf.pay.golbal.Actions;
import com.lk.qf.pay.golbal.MApplication;
import com.lk.qf.pay.golbal.MyUrls;
import com.lk.qf.pay.indiana.adapter.IndianaIssueOpenAdapter;
import com.lk.qf.pay.indiana.adapter.IndianaRecordListAdapter;
import com.lk.qf.pay.indiana.adapter.MyAddressAdapter;
import com.lk.qf.pay.indiana.bean.IndianaGoodsInfo;
import com.lk.qf.pay.sharedpref.SharedPrefConstant;
import com.lk.qf.pay.tool.T;
import com.lk.qf.pay.utils.ImgOptions;
import com.lk.qf.pay.v50.PayByV50CardActivity;
import com.lk.qf.pay.wedget.CommonTitleBarYellow;
import com.lk.qf.pay.wedget.MyClickListener;
import com.lk.qf.pay.wedget.customdialog.ActionSheetDialog;
import com.lk.qf.pay.wedget.customdialog.ActionSheetDialog.OnSheetItemClickListener;
import com.lk.qf.pay.wedget.customdialog.ActionSheetDialog.SheetItemColor;
import com.lk.qf.pay.zxb.ZXBDeviceListActivity;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

public class IndianaRecordListActivity extends IndianaBaseActivity implements
		OnRefreshListener2<ListView> {

	private RadioGroup rg;
	private CommonTitleBarYellow title;
	private PullToRefreshListView lv;
	private List<IndianaGoodsInfo> list;
	private List<IndianaGoodsInfo> listReturn;
	private int page = 1;
	private int pageSize = 10;
	private Map<String, String> map;
	private IndianaGoodsInfo orderInfo;// 登录后返回的用户信息
	private IndianaRecordListAdapter adapter;
	private String code = "", message = "", cmd="All",type;
	private ImageLoader imageLoader;
	private DisplayImageOptions options;
	private LinearLayout llShow;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.indiana_record_list_layout);
		init();
	}

	private void init() {
		imageLoader = ImageLoader.getInstance();
		imageLoader.init(ImageLoaderConfiguration.createDefault(this));
		options = ImgOptions.initImgOptions();

		title = (CommonTitleBarYellow) findViewById(R.id.titlebar_indiana_record_list);
		title.setActName("夺宝记录");
		title.setCanClickDestory(this, true);
		rg = (RadioGroup) findViewById(R.id.indiana_rg_record);
		rg.setOnCheckedChangeListener(changeListener);
		lv = (PullToRefreshListView) findViewById(R.id.ll_indiana_record_listview);
		llShow = (LinearLayout) findViewById(R.id.ll_indiana_recordList);
		list = new ArrayList<IndianaGoodsInfo>();

		adapter = new IndianaRecordListAdapter(this, list, mListener, options,
				imageLoader);
		lv.setAdapter(adapter);
		lv.setOnRefreshListener(this);
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		getIndianaRecordList(cmd);
	}

	/**
	 * listview中button的事件
	 */
	private MyClickListener mListener = new MyClickListener() {
		@Override
		public void myOnClick(int position, View v) {
			IndianaGoodsInfo info = list.get(position);
			switch (v.getId()) {
			case R.id.tv_indiana_record_Name:
				Intent intent = new Intent(IndianaRecordListActivity.this,
						EditAddressActivity.class);
				startActivity(intent);
				break;
			case R.id.tv_indiana_record_seeNum:
				Intent intent1 = new Intent(IndianaRecordListActivity.this,
						ShowIndianaNumActivity.class);
				intent1.putExtra("orderNum", info.getOrderNum());
				intent1.putExtra("goodsId", info.getGoodsId());
				intent1.putExtra("totalNum", ""+info.getUserCanyuNum());
				startActivity(intent1);

				break;
			case R.id.tv_indiana_record_GoBuy:
				Intent intent2 = new Intent(IndianaRecordListActivity.this,
						IndianaMainActivity.class);
				intent2.putExtra("action", "forFristIndex");
				Log.i("result", "--------------forFristIndex----------------");
				startActivity(intent2);
//				finish();
				break;

			default:
				break;
			}

		}
	};

	OnCheckedChangeListener changeListener = new OnCheckedChangeListener() {

		@Override
		public void onCheckedChanged(RadioGroup arg0, int checkedId) {
			// TODO Auto-generated method stub
			switch (checkedId) {
			case R.id.indiana_record_1:
				cmd = "All";
				break;
			case R.id.indiana_record_2:
				cmd = "Going";
				break;

			case R.id.indiana_record_3:
				cmd = "Publish";
				break;
			}
			getIndianaRecordList(cmd);
		}
	};

	/**
	 * 夺宝记录
	 */
	private void getIndianaRecordList(String cmd) {
		showLoadingDialog();
		RequestParams params = new RequestParams();
		String url = MyUrls.ROOT_URL_INDIANA_BUY_HISTORY_INFO;
		map = new HashMap<String, String>();
		map.put("username", MApplication.mSharedPref
				.getSharePrefString(SharedPrefConstant.USERNAME));
		map.put("pagesize", ""+pageSize);
		map.put("page", "" + page);
		map.put("Cmd", cmd);// ("All":"获取所有订单信息"，"Going":进行中的,"Publish":"已揭晓商品")
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
						if (page == 1) {
							llShow.setVisibility(View.VISIBLE);
							lv.setVisibility(View.GONE);
						}
						
					} else {
						llShow.setVisibility(View.GONE);
						lv.setVisibility(View.VISIBLE);

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
					llShow.setVisibility(View.VISIBLE);
					lv.setVisibility(View.GONE);
					T.ss(message);

				}
				lv.onRefreshComplete();
				// lv.onRefreshComplete();// 告诉它 我们已经在后台数据请求完毕
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
			Log.i("result", "---------Count-------" + count);
			if (count<pageSize) {
				lv.setMode(Mode.PULL_DOWN_TO_REFRESH);
			}
			if (count > 0) {
				for (int i = 0; i < count; i++) {
					String goodsName = obj.optJSONArray("date")
							.optJSONObject(i).optString("goodsName");// 商品名
					String goodsId = obj.optJSONArray("date").optJSONObject(i)
							.optString("goodsId");// id
					int goodsTotalNum = obj.optJSONArray("date")
							.optJSONObject(i).optInt("count");// 总数
					int goodsBuyNum = obj.optJSONArray("date").optJSONObject(i)
							.optInt("current_count");// 当前已购买
					int userCanyu = obj.optJSONArray("date").optJSONObject(i)
							.optInt("dbCount");// 本人已购买
					String winNum = obj.optJSONArray("date").optJSONObject(i)
							.optString("winNum");// 获奖号码
					String imgUrl = obj.optJSONArray("date").optJSONObject(i)
							.optString("imgurl1");// 图片
					String winnerName = obj.optJSONArray("date")
							.optJSONObject(i).optString("winner_name");// 中奖者姓名
					String winTime = obj.optJSONArray("date").optJSONObject(i)
							.optString("lotterytime");// 中奖时间
					String orderNum = obj.optJSONArray("date").optJSONObject(i)
							.optString("id");//订单号
					int shengyu = goodsTotalNum - goodsBuyNum;
					if (winNum!=null&&!winNum.equals("")) {
						type = "jiexiao";
					}
					// 给info设置数据
					IndianaGoodsInfo info = new IndianaGoodsInfo();
					info.setGoodsName(goodsName);
					info.setGoodsId(goodsId);
					info.setGoodsTotal(goodsTotalNum);
					info.setRemainingNum(shengyu);
					info.setBoughtNum(goodsBuyNum);
					info.setType(type);
					info.setImgUrl(imgUrl);
					Log.i("result", "-----------------winnerName------"
							+ winnerName);
					info.setUserName(winnerName);
					info.setOpenTime(winTime);
					info.setWinningNumber(winNum);
					info.setUserCanyuNum(userCanyu);
					info.setOrderNum(orderNum);
					listReturn.add(info);
				}
			}

		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}


	

	@Override
	public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
		// TODO Auto-generated method stub
		page=1;
		getIndianaRecordList(cmd);
	}

	@Override
	public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
		// TODO Auto-generated method stub
		page++;
		getIndianaRecordList(cmd);
	}

}
