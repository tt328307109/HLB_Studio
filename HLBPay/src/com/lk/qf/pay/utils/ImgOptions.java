package com.lk.qf.pay.utils;

import com.lk.bhb.pay.R;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;

public class ImgOptions {
	float latitude;
	float longitude;

	// RoutePlanSearch mSearch;

	public static DisplayImageOptions initImgOptions() {
		DisplayImageOptions options = new DisplayImageOptions.Builder()
		.showStubImage(R.drawable.baise)			// 设置图片下载期间显示的图片
		.showImageForEmptyUri(R.drawable.baise)	// 设置图片Uri为空或是错误的时候显示的图片
		.showImageOnFail(R.drawable.baise)		// 设置图片加载或解码过程中发生错误显示的图片	
		.cacheInMemory(true)						// 设置下载的图片是否缓存在内存中
		.cacheOnDisc(true)							// 设置下载的图片是否缓存在SD卡中
		.displayer(new RoundedBitmapDisplayer(20))	// 设置成圆角图片
		.build();									// 创建配置过得DisplayImageOption对象
		return options;
	}
}
