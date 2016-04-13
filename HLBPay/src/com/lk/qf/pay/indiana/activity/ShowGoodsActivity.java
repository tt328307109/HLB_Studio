package com.lk.qf.pay.indiana.activity;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;

import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.lk.bhb.pay.R;
import com.lk.qf.pay.activity.BaseActivity;
import com.lk.qf.pay.indiana.adapter.IndianaRecordListAdapter;
import com.lk.qf.pay.indiana.bean.IndianaGoodsInfo;
import com.lk.qf.pay.utils.ImgOptions;
import com.lk.qf.pay.wedget.CommonTitleBarYellow;
import com.lk.qf.pay.wedget.MyClickListener;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

public class ShowGoodsActivity extends IndianaBaseActivity implements OnClickListener {

	private CommonTitleBarYellow title;
	private PullToRefreshListView lv;
	private List<IndianaGoodsInfo> list;
	private List<IndianaGoodsInfo> listReturn;
	private int page = 1;
	private int pageSize = 15;
	private Map<String, String> map;
	private IndianaGoodsInfo orderInfo;// 登录后返回的用户信息
	private IndianaRecordListAdapter adapter;
	private String code = "", message = "", cmd;
	private ImageLoader imageLoader;
	private DisplayImageOptions options;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.show_goods_layout);
		init();
	}

	private void init() {
		title = (CommonTitleBarYellow) findViewById(R.id.titlebar_indiana_showGoods);
		title.setActName("晒单");
		title.setCanClickDestory(this, true);
		imageLoader = ImageLoader.getInstance();
		imageLoader.init(ImageLoaderConfiguration.createDefault(this));
		options = ImgOptions.initImgOptions();

		lv = (PullToRefreshListView) findViewById(R.id.ll_indiana_showGoods_listview);
		list = new ArrayList<IndianaGoodsInfo>();
		
		adapter = new IndianaRecordListAdapter(this, list, mListener, options,
				imageLoader);
		lv.setAdapter(adapter);
	}

	@Override
	public void onClick(View arg0) {
		// TODO Auto-generated method stub

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
				break;

			default:
				break;
			}

		}
	};

}
