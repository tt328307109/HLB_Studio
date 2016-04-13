package com.lk.qf.pay.utils;

/**
 * 功能：工具类
 * 类名：Utils.java
 * 日期：2013-11-26
 * 作者：lukejun
 */

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Random;

import org.apache.http.util.EncodingUtils;

import com.alibaba.fastjson.JSON;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.lk.pay.communication.RequestParams;
import com.lk.pay.utils.JUtil;
import com.lk.pay.utils.MD5Util;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;

/**
 * @ClassName: Utils
 * @Description: 工具类
 * @author lukejun
 * @date 2013-11-26 下午4:36:05
 * 
 */
public class MyJson {
	
	public static String getRequest(HashMap<String, String> params) {
		Gson gson = new GsonBuilder().serializeNulls().create();
		HashMap<String, Object> data = new HashMap<String, Object>();
		HashMap<String, Object> signMap = new HashMap<String, Object>();
		HashMap<String, Object> messageMap = new HashMap<String, Object>();
		
//		String sign;
//		try {
//			sign = MD5Util.generateParams(new String(JUtil.toJsonString(params).getBytes(),"UTF-8"));
//			signMap.put("SIGN", sign);
//		} catch (UnsupportedEncodingException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
		// String sign = MD5Util.generateParams(new
		// String(JUtil.toJsonString(params).getBytes(),"UTF-8"));
//		String json = JSON.toJSONString(params);
		String json = gson.toJson(params);
//		Log.i("result","-----jia-----"+json+"11111111111111110123456789ABCDEF");
//		Log.i("result","-----params-----"+params);
		String postSign = json+"11111111111111110123456789ABCDEF";
//		Log.i("result","-----postSign-----"+postSign);
		String sign = MyMdFivePassword.MD5(postSign);
//		Log.i("result","-----sign-----"+sign);
		signMap.put("SIGN", sign);

		data.put("REQ_BODY", params);
		data.put("REQ_HEAD", signMap);
		messageMap.put("REQ_MESSAGE", data);
		// gson.toJson(data);
		return gson.toJson(messageMap);
	}
	
	
	/*public static String PinEncrypt(String account, String passwd) {
		String result = "";
		String accountTemp1 = "";
		int passwdLen = passwd.length();
		if (passwdLen == 0) {
			passwd = "FFFFFF";
		} else if (passwdLen < 6) {
			for (int i = 0; i < 6 - passwdLen; i++) {
				passwd += "F";
			}
		}
		// 密码临时数据长度为16
		String passwdTemp1 = "0" + passwdLen + passwd + "FFFFFFFF";
		// 账户临时数据也为16
		if (account != null && !"".equals(account)) {
			int len = account.length();
			String accountTemp = account.substring(len - 13, len - 1);
			accountTemp1 = "0000" + accountTemp;
		}

		// 如果账户为空
		if (accountTemp1.equals("")) {
			result = passwdTemp1;
		} else {
			// 账户与密码首先进行BCD压缩(计算pinblock)
			byte[] accountByte = str2Bcd(accountTemp1);
			byte[] passwdByte = str2Bcd(passwdTemp1);

			byte[] resultByte = new byte[8];

			// 账户与密码异或
			for (int i = 0; i < 8; i++) {
				resultByte[i] = (byte) (accountByte[i] ^ passwdByte[i]);
			}
			// 异或的结果转16进制字符串
			result = bytesToHexString(resultByte);
		}

		return result.toUpperCase();
	}*/

	
	
}
