package com.lk.qf.pay.fragment;

import java.util.ArrayList;
import java.util.List;

import com.lk.bhb.pay.R;
import com.lk.qf.pay.adapter.MallAdapter;
import com.lk.qf.pay.beans.MallSpInfo;
import com.lk.qf.pay.tool.T;
import com.lk.qf.pay.wedget.view.AutoPagerView;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.ListView;
 

public class MallFragment extends BaseFragment{

	private View layoutView;
	private AutoPagerView pagerView;
	private ListView lv;
	private List<MallSpInfo> list;
	private MallSpInfo mallSpInfo;
//	PullToRefreshListView mPullRefreshListView;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		layoutView = inflater.inflate(R.layout.mall_layout, container,false);
		lv = (ListView) layoutView.findViewById(R.id.lv_mall);
		addData();
		MallAdapter adapter = new MallAdapter(getActivity(), list);
		lv.setAdapter(adapter);
		lv.setOnItemClickListener(itemClickListener);
		initGallery();
		pagerView.setOnTouchListener(onTouch);
		return layoutView;
	}
	private void addData(){
		list = new ArrayList<MallSpInfo>();
		
//		mallSpInfo = new MallSpInfo();
//		mallSpInfo.setImgMall(R.drawable.shangpin_01);
//		list.add(mallSpInfo);
//		mallSpInfo = new MallSpInfo();
//		mallSpInfo.setImgMall(R.drawable.shangpin_02);
//		list.add(mallSpInfo);
//		mallSpInfo = new MallSpInfo();
//		mallSpInfo.setImgMall(R.drawable.shangpin_03);
//		list.add(mallSpInfo);
//		mallSpInfo = new MallSpInfo();
//		mallSpInfo.setImgMall(R.drawable.shangpin_04);
//		list.add(mallSpInfo);
//		
//		mallSpInfo = new MallSpInfo();
//		mallSpInfo.setImgMall(R.drawable.shangpin_05);
//		list.add(mallSpInfo);
	}
	
	private void initGallery() {
//		final int[] imgs = new int[] { R.drawable.banner1, R.drawable.banner2, R.drawable.banner3 };
		List<View> pagePic = new ArrayList<View>();
//		for (int i = 0; i < imgs.length; i++) {
//			ImageView imageview = new ImageView(getActivity());
//			imageview.setScaleType(ScaleType.FIT_XY);
//			imageview.setImageResource(imgs[i]);
//			pagePic.add(imageview);
//		}
		pagerView = (AutoPagerView) layoutView
				.findViewById(R.id.cash_auto_pagerview);
		pagerView.setPageViewPics(pagePic);
	}
	
	OnTouchListener onTouch = new OnTouchListener() {

		@Override
		public boolean onTouch(View v, MotionEvent event) {

			return true;
		}
	};


	
	OnItemClickListener itemClickListener = new OnItemClickListener(
			) {

				@Override
				public void onItemClick(AdapterView<?> arg0, View view,
						int position, long arg3) {
					// TODO Auto-generated method stub
					switch (position) {
					case 0:
						
						break;
					case 1:
						no();
						break;
					case 2:
						
						no();
						break;
					case 3:
						
						no();
						break;
					case 4:
						
						no();
						break;

					default:
						break;
					}
				}
	};
	
	
	public void no(){
		T.ss("建设中");
	}
}
