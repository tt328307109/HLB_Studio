package com.lk.qf.pay.utils;

import java.text.NumberFormat;

public class PercentUtils {

	public static int getPercent(int x, int total) {
		 // 创建一个数值格式化对象  
		  
        NumberFormat numberFormat = NumberFormat.getInstance();  
        // 设置精确到小数点后2位  
        numberFormat.setMaximumFractionDigits(2);  
        String result = numberFormat.format((float) x / (float) total * 100);  
		return (int) (Double.parseDouble(result));
	}
}
