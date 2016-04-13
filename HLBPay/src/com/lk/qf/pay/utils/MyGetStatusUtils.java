package com.lk.qf.pay.utils;

import android.content.Context;

public class MyGetStatusUtils {

	/**
	 * 用于获取状态栏的高度。 使用Resource对象获取（推荐这种方式）
	 * 
	 * @return 返回状态栏高度的像素值。
	 */
	public static int getStatusBarHeight(Context context) {
		int result = 0;
		int resourceId = context.getResources().getIdentifier(
				"status_bar_height", "dimen", "android");
		if (resourceId > 0) {
			result = context.getResources().getDimensionPixelSize(resourceId);
		}
		return result;
	}
}
