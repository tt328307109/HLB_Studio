package com.lk.qf.pay.indiana.saidan;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import org.apache.http.entity.StringEntity;
import org.json.JSONException;
import org.json.JSONObject;
import com.alibaba.fastjson.JSON;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener2;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.lidroid.xutils.BitmapUtils;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lk.bhb.pay.R;
import com.lk.qf.pay.activity.BaseActivity;
import com.lk.qf.pay.golbal.MApplication;
import com.lk.qf.pay.golbal.MyUrls;
import com.lk.qf.pay.indiana.activity.IndianaBaseActivity;
import com.lk.qf.pay.indiana.activity.IndianaMainActivity;
import com.lk.qf.pay.indiana.bean.IndianaGoodsInfo;
import com.lk.qf.pay.sharedpref.SharedPrefConstant;
import com.lk.qf.pay.tool.T;
import com.lk.qf.pay.utils.ImgOptions;
import com.lk.qf.pay.utils.TimeUtils;
import com.lk.qf.pay.wedget.MyClickListener;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class SaiDanAfterActivity extends IndianaBaseActivity implements
		OnClickListener, OnRefreshListener2<ListView> {
	// private ListView lv_activity_saidan_after;
	public MyAdapter mMyAdapter;
	ArrayList<ArrayList<String>> mList;
	private ArrayList<MSG> arryItem;
	private ArrayList<MSG> list;
	private BitmapUtils bitmapUtils;
	private PullToRefreshListView lv;
	private int page = 1;
	private String code;
	private String message;
	private LinearLayout llDefault;
	private DisplayImageOptions options;
	private ImageLoader imageLoader;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_saidan_after);
		bitmapUtils = new BitmapUtils(this);

		initView();
		mList = new ArrayList<ArrayList<String>>();
		arryItem = new ArrayList<MSG>();
		mMyAdapter = new MyAdapter(getApplicationContext(), arryItem,
				myClickListener);
		initData();
		lv.setAdapter(mMyAdapter);
		lv.setOnRefreshListener(this);
	}

	private void initView() {
		lv = (PullToRefreshListView) findViewById(R.id.lv_activity_saidan_after);
		findViewById(R.id.ll_shaidan_back2).setOnClickListener(this);
		llDefault = (LinearLayout) findViewById(R.id.ll_indiana_shaidan_default);
		imageLoader = ImageLoader.getInstance();
		imageLoader.init(ImageLoaderConfiguration.createDefault(this));
		options = ImgOptions.initImgOptions();
	}

	private void initData() {
		LoadsaiDanData();
	}

	private MyClickListener myClickListener = new MyClickListener() {

		@Override
		public void myOnClick(int position, View v) {
			// TODO Auto-generated method stub
			MSG msgInfo = list.get(position);
			Intent intent = new Intent(SaiDanAfterActivity.this,
					PublishedActivity.class);
			intent.putExtra("goodsId", "" + msgInfo.getGoodsId());
			intent.putExtra("sdId", "" + msgInfo.getId());
			startActivity(intent);
			finish();
		}
	};

	public class MyAdapter extends BaseAdapter {
		private Context mContext;
		private ArrayList<MSG> arryList;
		private LayoutInflater layoutInflater;
		private MyClickListener mListener;

		public MyAdapter(Context context, ArrayList<MSG> arryItem,
				MyClickListener mListener) {
			this.layoutInflater = LayoutInflater.from(context);
			this.arryList = arryItem;
			this.mContext = context;
			this.mListener = mListener;
		}

		public void sendSata(ArrayList<MSG> arryItem) {
			this.arryList = arryItem;
		}

		@Override
		public int getCount() {
			return arryItem.size();
		}

		@Override
		public Object getItem(int position) {
			return arryItem.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder viewHolder;
			if (convertView == null) {
				viewHolder = new ViewHolder();
				convertView = layoutInflater.inflate(
						R.layout.indiana_show_item_layout, parent, false);
				viewHolder.saiDanIcon = (ImageView) convertView
						.findViewById(R.id.img_indiana_show_icon);
				viewHolder.saiDanName = (TextView) convertView
						.findViewById(R.id.tv_indiana_show_name);
				viewHolder.saiDanTime = (TextView) convertView
						.findViewById(R.id.tv_indiana_show_time);
				viewHolder.saiDanInfo = (TextView) convertView
						.findViewById(R.id.tv_indiana_show_info);
				viewHolder.tvGoBuy = (TextView) convertView
						.findViewById(R.id.tv_indiana_show_goBuy);
				viewHolder.tvShowState = (TextView) convertView
						.findViewById(R.id.tv_indiana_show_state);

				viewHolder.mMyGridView = (MyGridView) convertView
						.findViewById(R.id.gv_indiana_show);

				convertView.setTag(viewHolder);

			} else {
				viewHolder = (ViewHolder) convertView.getTag();
			}

			String saiDanName = arryItem.get(position).getName();// 中奖者电话
			if (saiDanName.length() == 11) {
				saiDanName = saiDanName.substring(0, 3)
						+ "****"
						+ saiDanName.substring(saiDanName.length() - 4,
								saiDanName.length());
			}

			
			viewHolder.saiDanName.setText(saiDanName);
			viewHolder.saiDanTime.setText(TimeUtils.changeDateFormat(
					"yyyy-MM-dd", arryItem.get(position).getTime()));
			viewHolder.saiDanInfo.setText(arryItem.get(position).getInfo());

			if (viewHolder.mMyGridView != null) {
				ArrayList<String> arrayListForEveryGridView = this.arryList
						.get(position).getList();
				GridViewAdapter gridViewAdapter = new GridViewAdapter(mContext,
						arrayListForEveryGridView, position);
				viewHolder.mMyGridView.setAdapter(gridViewAdapter);
			}
			String strState = arryItem.get(position).getState();
			if (strState.equals("th")) {
				viewHolder.tvShowState.setText("重新晒单(已退回)");
				viewHolder.tvShowState.setTag(position);
				viewHolder.tvShowState.setOnClickListener(mListener);
			} else if (strState.equals("first")) {
				viewHolder.tvShowState.setText("审核中");
			}
			
			return convertView;
		}

		String str2 = "   ";

		public class ViewHolder {
			MyGridView mMyGridView;

			ImageView saiDanIcon;
			TextView saiDanName;
			TextView saiDanTime;
			TextView saiDanInfo;
			TextView tvGoBuy;
			TextView tvShowState;
		}
	}

	public class GridViewAdapter extends BaseAdapter {
		private Context mContext;
		private ArrayList<String> mList;
		int p;

		public GridViewAdapter(Context mContext, ArrayList<String> mList, int p) {
			super();
			this.mContext = mContext;
			this.mList = mList;
			this.p = p;
		}

		@Override
		public int getCount() {
			if (mList == null) {
				return 0;
			}
			if (mList.size() < 4) {
				return this.mList.size();
			} else {
				return 3;
			}
		}

		@Override
		public Object getItem(int position) {
			if (mList == null) {
				return null;
			} else {
				return this.mList.get(position);
			}
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(final int position, View convertView,
				ViewGroup parent) {
			ViewHolder holder = null;
			if (convertView == null) {
				holder = new ViewHolder();
				convertView = LayoutInflater.from(this.mContext).inflate(
						R.layout.saidan_gridview_item, null, false);
				holder.tv = (ImageView) convertView
						.findViewById(R.id.gridview_item_button);
				Display display = getWindowManager().getDefaultDisplay();
				int mScreenWidth = display.getWidth();
				LayoutParams para = holder.tv.getLayoutParams();
				para.width = (mScreenWidth - 80) / 3;// 一屏显示两列
				para.height = para.width;
				holder.tv.setLayoutParams(para);

				convertView.setTag(holder);

			} else {
				holder = (ViewHolder) convertView.getTag();
			}

			if (mList.get(position) == null || mList.get(position).equals("")) {
				// imageLoader.displayImage(MyUrls.ROOT_URL2 + "/bank/yl.png",
				// img, options);
			} else {
				imageLoader.displayImage(
						MyUrls.ROOT_URL2 + mList.get(position), holder.tv,
						options);
				holder.tv.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						MyAlertFinger(MyUrls.ROOT_URL2 + mList.get(position));
					}
				});
			}
			return convertView;
		}

		private class ViewHolder {
			@ViewInject(R.id.gridview_item_button)
			ImageView tv;
		}

	}

	@ViewInject(R.id.iv_enlarge_activity)
	ImageView iv;

	public void MyAlertFinger(String url) {
		View vi;

		final AlertDialog builder = new AlertDialog.Builder(
				SaiDanAfterActivity.this).create();
		if (!builder.isShowing()) {
			builder.show();
		}
		builder.setView(null, 0, 0, 0, 0);
		vi = (SaiDanAfterActivity.this).getLayoutInflater().inflate(
				R.layout.enlarge_photo, null);

		iv = (ImageView) vi.findViewById(R.id.iv_enlarge_activity);
		builder.setContentView(vi);
		ViewUtils.inject(this);
		bitmapUtils = new BitmapUtils(this);
		bitmapUtils.display(iv, url);
		builder.getWindow().setLayout(LayoutParams.MATCH_PARENT,
				LayoutParams.MATCH_PARENT);
		vi.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				builder.dismiss();
			}
		});
	}

	/**
	 * 进入晒单界面。加载数据
	 */
	private void LoadsaiDanData() {
		Log.i("result", "----dd-----------");

		RequestParams params = new RequestParams();
		HashMap<String, String> map = new HashMap<String, String>();

		map.put("username", MApplication.mSharedPref
				.getSharePrefString(SharedPrefConstant.USERNAME));
		map.put("pagesize", "15");
		map.put("page", "" + page);

		String json = JSON.toJSONString(map);
		Log.i("result", "----ddd----------->" + json);
		try {
			StringEntity bodyEntity = new StringEntity(json, "UTF-8");
			params.setBodyEntity(bodyEntity);
		} catch (UnsupportedEncodingException e1) {
			e1.printStackTrace();
		}

		HttpUtils httpUtils = new HttpUtils();
		String url = MyUrls.ROOT_URL_INDIANA_LOAD_SDRECORD;

		httpUtils.send(HttpMethod.POST, url, params,
				new RequestCallBack<String>() {

					@Override
					public void onFailure(HttpException arg0, String arg1) {

						T.ss("操作超时");
						lv.onRefreshComplete();
					}

					@Override
					public void onSuccess(ResponseInfo<String> response) {

						String str = response.result;
						Log.i("result", "----ddd-----------" + str);
						jsonDetail(str);
						if (code.equals("00")) {
							if (list == null || list.size() == 0) {
								if (page == 1) {
									lv.setVisibility(View.GONE);
									llDefault.setVisibility(View.VISIBLE);
								}
							} else {
								lv.setVisibility(View.VISIBLE);
								llDefault.setVisibility(View.GONE);

								if (page == 1) {
									arryItem.clear();
									arryItem = list;
								} else {
									arryItem.addAll(list);// 追加跟新的数据
								}
								mMyAdapter.sendSata(arryItem);
								mMyAdapter.notifyDataSetChanged();
							}
						} else {
							lv.setVisibility(View.GONE);
							T.ss(message);
						}
						lv.onRefreshComplete();
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
			list = new ArrayList<MSG>();
			int count = obj.optInt("count");
			Log.i("result", "---------Count-------" + count);
			if (count < 15) {
				lv.setMode(Mode.PULL_DOWN_TO_REFRESH);
			}
			if (count > 0) {
				for (int i = 0; i < count; i++) {
					ArrayList<String> localArrayList = new ArrayList<String>();
					MSG msg = new MSG();
					String name = obj.optJSONArray("date").optJSONObject(i)
							.optString("username");// 名字
					msg.setName(name);

					String time = obj.optJSONArray("date").optJSONObject(i)
							.optString("addtime");// 时间
					msg.setTime(time);
					msg.setId(obj.optJSONArray("date").optJSONObject(i)
							.optString("id"));//晒单id
					msg.setGoodsId(obj.optJSONArray("date").optJSONObject(i)
							.optString("goodsid"));//商品id

					String info = obj.optJSONArray("date").optJSONObject(i)
							.optString("note");// 备注
					msg.setInfo(info);
					String state = obj.optJSONArray("date").optJSONObject(i)
							.optString("state");// 状态
					msg.setState(state);

					String imgurl1 = obj.optJSONArray("date").optJSONObject(i)
							.optString("imgurl1");// 照片1信息
					if (imgurl1.length() > 1) {
						localArrayList.add(imgurl1);
					}

					String imgurl2 = obj.optJSONArray("date").optJSONObject(i)
							.optString("imgurl2");// 照片2信息
					if (imgurl2.length() > 1) {
						localArrayList.add(imgurl2);
					}

					String imgurl3 = obj.optJSONArray("date").optJSONObject(i)
							.optString("imgurl3");// 照片3信息
					if (imgurl3.length() > 1) {
						localArrayList.add(imgurl3);
					}

					String imgurl4 = obj.optJSONArray("date").optJSONObject(i)
							.optString("imgurl4");// 照片4信息
					if (imgurl4.length() > 1) {
						localArrayList.add(imgurl4);
					}
					//
					String imgurl5 = obj.optJSONArray("date").optJSONObject(i)
							.optString("imgurl5");// 照片5信息
					if (imgurl5.length() > 1) {
						localArrayList.add(imgurl5);
					}

					// mList.add(localArrayList);
					msg.setList(localArrayList);
					list.add(msg);
				}
			}

		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	@Override
	public void onClick(View arg0) {
		// TODO Auto-generated method stub
		finish();
	}

	@Override
	public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
		// TODO Auto-generated method stub
		page = 1;
		LoadsaiDanData();
	}

	@Override
	public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
		// TODO Auto-generated method stub
		page++;
		LoadsaiDanData();
	}

}
