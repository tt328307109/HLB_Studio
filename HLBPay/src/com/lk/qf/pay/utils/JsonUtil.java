package com.lk.qf.pay.utils;

import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;

public class JsonUtil {
	/**
	 * 把数据源HashMap转换成json
	 * 
	 * @param map
	 */
	public static String hashMapToJson(HashMap map) {
		String string = "{";
		for (Iterator it = map.entrySet().iterator(); it.hasNext();) {
			Entry e = (Entry) it.next();
			string += "'" + e.getKey() + "':";
			string += "'" + e.getValue() + "',";
		}
		string = string.substring(0, string.lastIndexOf(","));
		string += "}";
		return string;
	}

	public static void main(String[] args) {
		HashMap<String, String> map = new HashMap<String, String>();
		map.put("username", "xiaoming");
		map.put("password", "123445");
		System.out.println(JsonUtil.hashMapToJson(map));
	}

	/**
	 * url加密json
	 * 
	 * @param paramString
	 * @return
	 */
	public static String toURLEncoded(String paramString) {
		if (paramString == null || paramString.equals("")) {
			// LogD("toURLEncoded error:" + paramString);
			return "";
		}

		try {
			String str = new String(paramString.getBytes(), "UTF-8");
			str = URLEncoder.encode(str, "UTF-8");
			return str;
		} catch (Exception localException) {
			// LogE("toURLEncoded error:" + paramString, localException);
		}

		return "";
	}

	/**
	 * url解密json
	 * 
	 * @param paramString
	 * @return
	 */
	public static String toURLDecoded(String paramString) {
		if (paramString == null || paramString.equals("")) {
			// LogD("toURLDecoded error:"+paramString);
			return "";
		}

		try {
			String str = new String(paramString.getBytes(), "UTF-8");
			str = URLDecoder.decode(str, "UTF-8");
			return str;
		} catch (Exception localException) {
			// LogE("toURLDecoded error:"+paramString, localException);
		}

		return "";
	}
}
