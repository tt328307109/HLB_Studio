package com.lk.qf.pay.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import android.util.Log;

public class TimeUtils {

	public boolean isBeforeDate(String strDate) {
		boolean isBefore = true;
		Date date = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
		String nowDate = sdf.format(date);
		Log.i("result", "--------date-----" + nowDate);
		long nowNum = Long.parseLong(nowDate);
		long num = Long.parseLong(strDate);
		if (num > nowNum) {
			// 之后
			isBefore = false;
		}
		return isBefore;
	}

	/**
	 * 账单日判断
	 * 
	 * @param date
	 * @return
	 */
	public String[] myFormatDate(String date) {
		String[] str = new String[2];
		String strDate = "";
		SimpleDateFormat sdf1 = new SimpleDateFormat("MMdd");
		SimpleDateFormat sdf2 = new SimpleDateFormat("yyyyMMdd");
		try {
			strDate = sdf1.format(sdf2.parse(date));
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if (isBeforeDate(date)) {
			if (strDate.startsWith("12")) {
				str[0] = strDate;
				str[1] = "01" + strDate.substring(2, 4);
			} else {

				long num1 = Long.parseLong(strDate);
				long num2 = num1 + 100;
				str[0] = strDate;
				str[1] = "" + num2;
			}
		} else {
			if (strDate.startsWith("01")) {
				str[1] = strDate;
				str[0] = "12" + strDate.substring(2, 4);
			} else {
				long num1 = Long.parseLong(strDate);
				long num2 = num1 - 100;
				str[0] = "" + num2;
				str[1] = strDate;
			}

		}
		SimpleDateFormat sdf3 = new SimpleDateFormat("MM/dd");
		SimpleDateFormat sdf4 = new SimpleDateFormat("MMdd");
		try {
			str[0] = sdf3.format(sdf4.parse(str[0]));
			str[1] = sdf3.format(sdf4.parse(str[1]));
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return str;
	}

	/**
	 * 当前时间后几天
	 * 
	 * @param day
	 * @return
	 * @throws ParseException
	 */
	public static String getStatetime(String dateFormat, int day)
			throws ParseException {

		SimpleDateFormat sdf = new SimpleDateFormat(dateFormat);
		Calendar c = Calendar.getInstance();
		c.add(Calendar.DATE, day);
		Date monday = c.getTime();
		String preMonday = sdf.format(monday);
		return preMonday;
	}

	/**
	 * 判断给定字符串时间是否为今日
	 * 
	 * @param sdate
	 * @return boolean
	 */
	public static boolean isToday(String date) {
		boolean b = false;
		String strDate = changeDateFormat("yyyyMMdd", date);
		Date today = new Date();
		SimpleDateFormat sdf1 = new SimpleDateFormat("yyyyMMdd");
		String strToday = sdf1.format(today);
		if (strDate != null) {
			if (strDate.equals(strToday)) {
				b = true;
			}
		}
		return b;
	}

	/**
	 * 判断给定字符串是否为昨日
	 * 
	 * @param date
	 * @return
	 */
	public static int isYesterday(String date) {
		int day = 0;
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddhhmmss");
		try {
			Date d1 = new Date();// 当前时间
			Date d2 = sdf.parse(date);// 传进的时间
			// Log.i("result", "------------d2--s---------="+d2.getTime());
			// Log.i("result", "------------d1--s---------="+d1.getTime());
			long cha = d1.getTime() - d2.getTime();
			// Log.i("result", "------------cha--s---------="+cha);
			day = new Long(cha / (1000 * 60 * 60 * 24)).intValue();

		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return day;
	}

	/**
	 * 格式化时间
	 * 
	 * @param mDateFormat
	 *            格式样式
	 * @return
	 */
	public static String changeDateFormat(String dateFormate, String date) {
		String strDate = "";
		SimpleDateFormat sdf1 = new SimpleDateFormat(dateFormate);
		SimpleDateFormat sdf2 = null;
		if (date.length() == 17) {
			sdf2 = new SimpleDateFormat("yyyyMMddHHmmssSSS");
		} else {
			sdf2 = new SimpleDateFormat("yyyyMMddHHmmss");
		}
		try {
			strDate = sdf1.format(sdf2.parse(date));
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return strDate;
	}

	/**
	 * 
	 * @param dateFormate
	 * @param strDate
	 * @param num
	 *            当前时间后几天
	 * @return
	 */
	public static String getTomorrowOrOther(String dateFormate, int num) {
		String str = "";
		Date date = new Date();// 取时间
		Calendar calendar = new GregorianCalendar();
		calendar.setTime(date);
		calendar.add(calendar.DATE, num);// 把日期往后增加一天.整数往后推,负数往前移动
		date = calendar.getTime(); // 这个时间就是日期往后推一天的结果
		SimpleDateFormat formatter = new SimpleDateFormat("dd");
		str = formatter.format(date);
		return str;
	}

	/**
	 * 格式化当前时间
	 * 
	 * @param mDateFormat
	 *            格式样式
	 * @return
	 */
	public static String getFormatDate(String mDateFormat) {
		SimpleDateFormat sdf = new SimpleDateFormat(mDateFormat);
		Date date = new Date();
		sdf.format(date);
		return sdf.format(date);
	}

	// 字符串类型日期转化成date类型
	public static Date strToDate(String style, String date) {
		SimpleDateFormat formatter = new SimpleDateFormat(style);
		try {
			return formatter.parse(date);
		} catch (ParseException e) {
			e.printStackTrace();
			return new Date();
		}
	}

	public static String clanderTodatetime(Calendar style, String date) {

		SimpleDateFormat formatter = new SimpleDateFormat(date);
		return formatter.format(style.getTime());
	}

	/*
	 * 判断当前日期是星期几
	 * 
	 * @param pTime 修要判断的时间
	 * 
	 * @return dayForWeek 判断结果
	 * 
	 * @Exception 发生异常
	 */
	public static int dayForWeek(String pTime) throws Exception {
		SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");
		Calendar c = Calendar.getInstance();
		c.setTime(format.parse(pTime));
		int dayForWeek = 0;
		if (c.get(Calendar.DAY_OF_WEEK) == 1) {
			dayForWeek = 7;
		} else {
			dayForWeek = c.get(Calendar.DAY_OF_WEEK) - 1;
		}
		return dayForWeek;
	}
}
