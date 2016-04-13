package com.lk.qf.pay.utils;

import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Scanner;

import org.apache.http.entity.StringEntity;
import org.json.JSONException;
import org.json.JSONObject;

import com.alibaba.fastjson.JSON;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;
import com.lk.qf.pay.golbal.MApplication;
import com.lk.qf.pay.golbal.MyUrls;
import com.lk.qf.pay.sharedpref.SharedPrefConstant;
import com.lk.qf.pay.tool.T;
import com.mining.app.zxing.MipcaActivityCapture;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;

public class CreatePayCodeUtils {

	private static int QR_WIDTH = 0;
	private static int QR_HEIGHT = 0;
	private Context context;
	private static String STRVALUE = "23c56b886b5be869567dd389b3e5d1d6";// 易付宝keyValue

	public CreatePayCodeUtils(Context context) {
		this.context = context;
	}

	/**
	 * 生成签名 易付宝
	 * 
	 * @param phone
	 * @return
	 */
	public static String createSign(String[] array) {
		String strToSort = "";
		StringBuffer sb = new StringBuffer();;
		Arrays.sort(array, String.CASE_INSENSITIVE_ORDER);
		for (int i = 0; i < array.length; i++) {
			sb.append(array[i] + "&");
			
			Log.i("result", "---------s------------" + sb);
		}
		
		strToSort = sb.toString();
		String sign = strToSort + "key=" + STRVALUE;
		Log.i("result", "---------sign------------" + sign);
		sign = MyMdFivePassword.MD5(sign);

		return sign;
	}

	/**
	 * 钱包转账生成二维码
	 * 
	 * @param phone
	 *            电话号码
	 * @param account
	 *            金额
	 * @param beizhu
	 *            备注
	 * @param type
	 *            类型
	 * @param width
	 *            宽度
	 * @return
	 */
	public static Bitmap toJsonPayCode(String phone, String account,
			String beizhu, String type, int width) {

		HashMap<String, String> map = new HashMap<String, String>();
		map.put("phone", phone);
		map.put("total", account);// 金额
		map.put("beizhu", beizhu);
		map.put("type", type);
		String json = JSON.toJSONString(map);
		Log.i("result", "-------------json-----------" + json);
		String pwdJson = "";
		try {
			pwdJson = JsonUtil.toURLEncoded(json);
			Log.i("result", "-------------pwdJson-----------" + pwdJson);
			Log.i("result",
					"-------------pwdJson-jie----------"
							+ JsonUtil.toURLDecoded(pwdJson));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return createQRImage(pwdJson, width);
	}


	public static Bitmap createQRImage(String url, int width) {
		Bitmap bitmap = null;

		QR_WIDTH = width;
		QR_HEIGHT = QR_WIDTH;

		try {
			// 判断URL合法性
			if (url == null || "".equals(url) || url.length() < 1) {
				return null;
			}
			Hashtable<EncodeHintType, String> hints = new Hashtable<EncodeHintType, String>();
			hints.put(EncodeHintType.CHARACTER_SET, "utf-8");
			// 图像数据转换，使用了矩阵转换
			BitMatrix bitMatrix = new QRCodeWriter().encode(url,
					BarcodeFormat.QR_CODE, QR_WIDTH, QR_HEIGHT, hints);
			int[] pixels = new int[QR_WIDTH * QR_HEIGHT];
			// 下面这里按照二维码的算法，逐个生成二维码的图片，
			// 两个for循环是图片横列扫描的结果
			for (int y = 0; y < QR_HEIGHT; y++) {
				for (int x = 0; x < QR_WIDTH; x++) {
					if (bitMatrix.get(x, y)) {
						pixels[y * QR_WIDTH + x] = 0xff000000;
					} else {
						pixels[y * QR_WIDTH + x] = 0xffffffff;
					}
				}
			}
			// 生成二维码图片的格式，使用ARGB_8888
			bitmap = Bitmap.createBitmap(QR_WIDTH, QR_HEIGHT,
					Bitmap.Config.ARGB_8888);
			bitmap.setPixels(pixels, 0, QR_WIDTH, 0, 0, QR_WIDTH, QR_HEIGHT);
			// 显示到一个ImageView上面
			// imgPayCode.setImageBitmap(bitmap);
		} catch (WriterException e) {
			e.printStackTrace();
		}
		return bitmap;
	}
}
