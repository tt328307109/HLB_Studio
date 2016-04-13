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
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener2;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;
import com.lk.bhb.pay.R;
import com.lk.qf.pay.aanewactivity.LicaiListActivity;
import com.lk.qf.pay.activity.BaseActivity;
import com.lk.qf.pay.activity.LoginActivity;
import com.lk.qf.pay.adapter.LiCaiListAdapter;
import com.lk.qf.pay.beans.LiCaiListItemInfo;
import com.lk.qf.pay.beans.Xinyongkainfo;
import com.lk.qf.pay.golbal.MApplication;
import com.lk.qf.pay.golbal.MyUrls;
import com.lk.qf.pay.indiana.adapter.IndianaIssueOpenAdapter;
import com.lk.qf.pay.indiana.adapter.IndianaParticipantsAdapter;
import com.lk.qf.pay.indiana.bean.IndianaGoodsInfo;
import com.lk.qf.pay.sharedpref.SharedPrefConstant;
import com.lk.qf.pay.tool.T;
import com.lk.qf.pay.utils.ImgOptions;
import com.lk.qf.pay.wedget.CommonTitleBarYellow;
import com.lk.qf.pay.wedget.MyClickListener;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

public class IndianaIssueOpenActivity extends IndianaBaseActivity implements
		OnRefreshListener2<ListView> {

	private PullToRefreshListView lv;
	private List<IndianaGoodsInfo> list;
	private List<IndianaGoodsInfo> listReturn;
	private int page = 1;
	private int dataCount = 10;
	private Map<String, String> map;
	private IndianaGoodsInfo info;// 登录后返回的用户信息
	private DisplayImageOptions options;
	private ImageLoader imageLoader;
	private IndianaIssueOpenAdapter adapter;
	private CommonTitleBarYellow title;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.indiana_issue_open_layout);
		init();
	}

	private void init() {
		imageLoader = ImageLoader.getInstance();
		imageLoader.init(ImageLoaderConfiguration.createDefault(this));
		options = ImgOptions.initImgOptions();

		title = (CommonTitleBarYellow) findViewById(R.id.titlebar_issue_open_title);
		title.setActName("往期揭晓");
		title.setCanClickDestory(this, true);

		lv = (PullToRefreshListView) findViewById(R.id.ll_indiana_issue_openlottery);
		lv.setOnRefreshListener(this);
		list = new ArrayList<IndianaGoodsInfo>();
		queryIssueOpenInfo();
		adapter = new IndianaIssueOpenAdapter(this, list, options, imageLoader);
		lv.setAdapter(adapter);
		lv.setOnItemClickListener(itemClickListener);
		// Date date = new Date();
		// SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
		//
		// Calendar cal = Calendar.getInstance();
		// cal.add(Calendar.MONTH, -1);
		// Date lastDate = cal.getTime();
		// queryIssueOpenInfo();
	}

	OnItemClickListener itemClickListener = new OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
				long arg3) {
			// TODO Auto-generated method stub
			IndianaGoodsInfo info = (IndianaGoodsInfo) arg0
					.getItemAtPosition(arg2);
			Intent intent = new Intent(IndianaIssueOpenActivity.this,
					IndianaGoodsInfoActivity.class);
			intent.putExtra("goodsInfo", info);
			intent.setAction("issueOpen");
			startActivity(intent);
		}
	};

	// /**
	// * listview中button的事件
	// */
	// private MyClickListener mListener = new MyClickListener() {
	// @Override
	// public void myOnClick(int positio, View v) {
	// Object tag = v.getTag();
	// IndianaGoodsInfo info = list.get(positio);
	// switch (v.getId()) {
	// case R.id.img_indiana_announced_goods_icon:
	// Intent intent = new Intent(IndianaIssueOpenActivity.this,
	// IndianaGoodsInfoActivity.class);
	// intent.putExtra("goodsInfo", info);
	// intent.setAction("shouye");
	// startActivity(intent);
	// break;
	// }
	// }
	// };
	/**
	 * 查询往期详情
	 * 
	 * @return
	 */
	private void queryIssueOpenInfo() {
		showLoadingDialog();
		RequestParams params = new RequestParams();
		String url = MyUrls.ROOT_URL_WILL_LOTTERY;

		map = new HashMap<String, String>();
		map.put("pageSize", "15");
		map.put("page", "" + page);

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

				String returnCode = info.getCode();

				if (returnCode.equals("00")) {
					if (listReturn == null || listReturn.size() == 0) {
						T.ss(getResources().getString(
								R.string.query_nothing_more));
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
					T.ss(info.getMessage());

				}
				lv.onRefreshComplete();// 告诉它 我们已经在后台数据请求完毕
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
			listReturn = new ArrayList<IndianaGoodsInfo>();
			int count = obj.optInt("count");
			Log.i("result", "---------Count-------" + count);
			if (count > 0) {
				Log.i("result", "---------page-------" + page);
				Log.i("result", "---------dataCount-------" + dataCount);
				for (int i = 0; i < count; i++) {

					String goodsName = obj.optJSONArray("date")
							.optJSONObject(i).optString("indiana_name");
					String goodsId = obj.optJSONArray("date").optJSONObject(i)
							.optString("id");
					String winnerName = obj.optJSONArray("date")
							.optJSONObject(i).optString("winner_name");
					String winnerNum = obj.optJSONArray("date")
							.optJSONObject(i).optString("dbnum");
					String openTime = obj.optJSONArray("date").optJSONObject(i)
							.optString("lotterytime");
					int strCount = obj.optJSONArray("date").optJSONObject(i)
							.optInt("count");
					String imgUrl = obj.optJSONArray("date").optJSONObject(i)
							.optString("imgurl1");
					String imgUrl2 = obj.optJSONArray("date").optJSONObject(i)
							.optString("imgurl3");
					String imgUrl3 = obj.optJSONArray("date").optJSONObject(i)
							.optString("imgurl4");
					String imgUrl4 = obj.optJSONArray("date").optJSONObject(i)
							.optString("imgurl5");
					String imgUrl5 = obj.optJSONArray("date").optJSONObject(i)
							.optString("imgurl6");

					// 给info设置数据
					IndianaGoodsInfo info = new IndianaGoodsInfo();
					info.setGoodsId(goodsId);
					info.setGoodsName(goodsName);
					info.setWinnerName(winnerName);
					info.setWinningNumber(winnerNum);
					info.setGoodsTotal(strCount);
					info.setOpenTime(openTime);
					info.setImgUrl(imgUrl);
					info.setImgUrl2(imgUrl2);
					info.setImgUrl3(imgUrl3);
					info.setImgUrl4(imgUrl4);
					info.setImgUrl5(imgUrl5);
					info.setType("jiexiao");
					listReturn.add(info);
				}
			}

		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
		// TODO Auto-generated method stub

		page = 1;
		queryIssueOpenInfo();
	}

	@Override
	public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
		// TODO Auto-generated method stub
		// pageSize+=5;
		page++;
		Log.i("result", "--------ss------" + page);
		queryIssueOpenInfo();
		Log.i("result", "--------sss------" + page);
	}

}
