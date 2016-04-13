package com.lk.qf.pay.tool;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

import org.apache.http.entity.StringEntity;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.alibaba.fastjson.JSON;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;
import com.lk.pay.communication.AsyncHttpClient;
import com.lk.pay.communication.AsyncHttpResponseHandler;
import com.lk.pay.communication.RequestParams;
import com.lk.pay.utils.MD5Util;
import com.lk.pay.utils.RequestUtil;
import com.lk.qf.pay.golbal.Constant;
import com.lk.qf.pay.golbal.MApplication;
import com.lk.qf.pay.golbal.Urls;
import com.lk.qf.pay.golbal.User;
import com.lk.qf.pay.tool.Logger;
import com.lk.qf.pay.utils.MyMdFivePassword;
import com.lk.qf.pay.utils.MyUtilss;

/**
 * 支付
 * 
 * @param custId
 *            商户id
 * @param custMobile
 *            手机号
 * @param prdordNo
 *            订单号
 * @param payType
 *            支付方式
 * @param rateType
 *            费率类型
 * @param termNo
 *            终端号
 * @param termType
 *            终端类型
 * @param payAmt
 *            交易金额
 * @param track
 *            磁道信息
 * @param pinblk
 *            密码密文
 * @param random
 *            随机数 C 音频设备时使用
 * @param mediaType
 *            介质类型 C 01 磁条卡 02 IC卡
 * @param period
 *            有效期 C mediaType 02时必填
 * @param icdata
 *            55域 C mediaType 02时必填
 * @param crdnum
 *            卡片序列号 C mediaType 02时必填
 * @param mac
 *            Mac C 设备计算的mac
 * @param areaCode
 *            定位参数
 * @param complete
 *            block回传
 */

public class MyHttpClient {
	private static AsyncHttpClient httpClient = new AsyncHttpClient();

	private MyHttpClient() {
		Logger.init("[]").setMethodCount(0).hideThreadInfo();
	}

	public static void postcesi(Context context, String url,
			HashMap<String, String> params,
			AsyncHttpResponseHandler responseHandler) {

		if (params.containsKey("custPwd")) {
			params.put("custPwd",
					MD5Util.generatePassword(params.get("custPwd")));
		}

		if (params.containsKey("newPwd")) {
			params.put("newPwd", MD5Util.generatePassword(params.get("newPwd")));
		}
		if (params.containsKey("payPwd")) {
			params.put("payPwd", MD5Util.generatePassword(params.get("payPwd")));
		}
		params.put("sysType", Constant.SYS_TYPE);
		params.put("sysVersion", Constant.SYS_VERSIN);
		params.put("appVersion", MyUtilss.getVersion(context));
		params.put("sysTerNo", MyUtilss.getLocalIpAddress());
		params.put("txnDate", MyUtilss.getCurrentDate("yyMMdd"));
		params.put("txnTime", MyUtilss.getCurrentDate("HHmmss"));
		if (User.login) {
			// params.put("custId", User.uId);
			params.put("custMobile", User.uAccount);
		}
		 RequestParams requestParams = RequestUtil.getRequest(params);
		 
		String json = "REQ_MESSAGE:{\"REQ_HEAD\":{\"SIGN\":\"8F20FB96DEE17E399A3B78C7F6214E07\"},\"REQ_BODY\":{\"custId\":\"102124058120001\",\"sysType\":\"Android\",\"termType\":\"01\",\"sysTerNo\":\"10.14.8.152\",\"appVersion\":\"1.1.1\",\"termNo\":\"JHLA601508000014\",\"custMobile\":\"15923254686\",\"sysVersion\":\"4.4.4\",\"txnTime\":\"123745\",\"txnDate\":\"151016\"}}11111111111111110123456789ABCDEF";
		String json2 = MyMdFivePassword.MD5(json);
//		RequestParams requestParams = new RequestParams();
//		StringEntity bodyEntity = new StringEntity(json, "UTF-8");

		 httpClient.post(MApplication.mApplicationContext, url, requestParams,
		 responseHandler);


//		Log.i("result", "----------请求-----------" + requestParams);
	}

	/**
	 * 发送验证码请求
	 * 
	 * @return code
	 */
	/*
	 * public void postSMSHttp(String smstyp) { params = new RequestParams();
	 * Map<String, String> map = new HashMap<String, String>();
	 * 
	 * map.put("phonenum", ""); map.put("brand", "0001"); map.put("smstyp",
	 * smstyp); map.put("signature", "00");
	 * 
	 * String json = JSON.toJSONString(map);
	 * 
	 * try { StringEntity bodyEntity = new StringEntity(json, "UTF-8");
	 * params.setBodyEntity(bodyEntity); } catch (UnsupportedEncodingException
	 * e1) { // TODO Auto-generated catch block e1.printStackTrace(); }
	 * 
	 * HttpUtils httpUtils = new HttpUtils();
	 * 
	 * httpUtils.send(HttpMethod.POST, "", params, new RequestCallBack<String>()
	 * {
	 * 
	 * @Override public void onFailure(HttpException arg0, String arg1) { //
	 * TODO Auto-generated method stub
	 * 
	 * }
	 * 
	 * @Override public void onSuccess(ResponseInfo<String> response) { // TODO
	 * Auto-generated method stub
	 * 
	 * } });
	 * 
	 * }
	 */

	public static void getcesi(Context context, String url,
			HashMap<String, String> params,
			AsyncHttpResponseHandler responseHandler) {

		if (params.containsKey("custPwd")) {
			params.put("custPwd",
					MD5Util.generatePassword(params.get("custPwd")));
		}

		if (params.containsKey("newPwd")) {
			params.put("newPwd", MD5Util.generatePassword(params.get("newPwd")));
		}
		if (params.containsKey("payPwd")) {
			params.put("payPwd", MD5Util.generatePassword(params.get("payPwd")));
		}
		params.put("sysType", Constant.SYS_TYPE);
		params.put("sysVersion", Constant.SYS_VERSIN);
		params.put("appVersion", MyUtilss.getVersion(context));
		params.put("sysTerNo", MyUtilss.getLocalIpAddress());
		params.put("txnDate", MyUtilss.getCurrentDate("yyMMdd"));
		params.put("txnTime", MyUtilss.getCurrentDate("HHmmss"));
		if (User.login) {
			// params.put("custId", User.uId);
			params.put("custMobile", User.uAccount);
		}

		Log.i("result", "----------请求-----------" + params);
		RequestParams requestParams = RequestUtil.getRequest(params);

		httpClient.get(MApplication.mApplicationContext, url, requestParams,
				responseHandler);

		Log.i("result", "----------请求-----------" + requestParams);
	}

	public static void post(Context context, String url,
			HashMap<String, String> params,
			AsyncHttpResponseHandler responseHandler) {

		if (params.containsKey("custPwd")) {
			params.put("custPwd",
					MD5Util.generatePassword(params.get("custPwd")));
		}

		if (params.containsKey("newPwd")) {
			params.put("newPwd", MD5Util.generatePassword(params.get("newPwd")));
		}
		if (params.containsKey("payPwd")) {
			params.put("payPwd", MD5Util.generatePassword(params.get("payPwd")));
		}
		params.put("sysType", Constant.SYS_TYPE);
		params.put("sysVersion", Constant.SYS_VERSIN);
		params.put("appVersion", MyUtilss.getVersion(context));
		params.put("sysTerNo", MyUtilss.getLocalIpAddress());
		params.put("txnDate", MyUtilss.getCurrentDate("yyMMdd"));
		params.put("txnTime", MyUtilss.getCurrentDate("HHmmss"));
		if (User.login) {
			params.put("custId", User.uId);
			params.put("custMobile", User.uAccount);
		}
		/*
		 * Gson gson = new GsonBuilder().serializeNulls().create();
		 * HashMap<String, Object> data = new HashMap<String, Object>();
		 * HashMap<String, Object> signMap = new HashMap<String, Object>();
		 * //signMap.put("SIGN", MD5Util.generateParams(gson.toJson(params)));
		 * try { //String sign = gson.toJson(params);
		 * //System.out.println("sign-->" + sign); String sign =
		 * MD5Util.generateParams(new
		 * String(JUtil.toJsonString(params).getBytes(),"UTF-8")); //
		 * System.out.println("sign-->" + sign); signMap.put("SIGN", sign); }
		 * catch (UnsupportedEncodingException e) { // TODO Auto-generated catch
		 * block e.printStackTrace(); }
		 * 
		 * 
		 * data.put("REQ_BODY", params); data.put("REQ_HEAD", signMap);
		 * RequestParams requestParams = new RequestParams();
		 * System.out.println("[请求参数]" + gson.toJson(data));
		 * requestParams.put("REQ_MESSAGE", gson.toJson(data));
		 * Logger.json("[请求参数]", gson.toJson(data)); System.out.println("[请求路径]"
		 * + Urls.ROOT_URL + url);
		 */

		// Log.i("result", "----------g1-----------"+params);
		RequestParams requestParams = RequestUtil.getRequest(params);

		// Log.i("result", "----------g2-----------"+requestParams);

		httpClient.post(MApplication.mApplicationContext, Urls.ROOT_URL + url,
				requestParams, responseHandler);

		Log.i("result", "----------g3-----------" + requestParams);
	}

	public static void cancleRequest(Context ctx) {
		httpClient.cancelRequests(ctx, true);
	}

	public static void cancleAllRequest() {
		httpClient.cancelAllRequests(true);
	}

}
