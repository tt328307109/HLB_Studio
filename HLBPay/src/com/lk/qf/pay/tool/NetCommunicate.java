/**
 * 功能：网络请求连接类,https请求要信任X509证书
 * 类名：NetCommunicate.java
 * 日期：2013-12-30
 * 作者：lukejun
 */
package com.lk.qf.pay.tool;

import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Date;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

/**
 * @ClassName: NetCommunicate
 * @Description: 网络请求连接类,https请求要信任X509证书
 * @author lukejun
 * @date 2013-12-30 上午10:45:42
 * 
 */
public class NetCommunicate {

	private static final int CONNECT_TIMEOUT = 5000;
	private static final int READ_TIMEOUT = 10000;
	static MyHostnameVerifier myHostnameVerifier;
	static MyTrustManager myTrustManager = new MyTrustManager();

	static {
		myHostnameVerifier = new MyHostnameVerifier();
	}

	/**
	 * @Title: https
	 * @Description: https请求
	 * @param url
	 * @return
	 */
	public static Bitmap https(String url) {

		Bitmap bitmap = null;
		try {
			// 取得SSL的SSLContext实例
			SSLContext localSSLContext = SSLContext.getInstance("TLS");
			// 取得TrustManager的X509密钥管理器
			TrustManager[] arrayOfTrustManager = new TrustManager[1];
			MyTrustManager localMyTrustManager = myTrustManager;
			arrayOfTrustManager[0] = localMyTrustManager;
			SecureRandom localSecureRandom = new SecureRandom();
			// 初始化SSLContext
			localSSLContext.init(null, arrayOfTrustManager, localSecureRandom);
			// 生成客户端SSLSocket
			HttpsURLConnection.setDefaultSSLSocketFactory(localSSLContext
					.getSocketFactory());
			HttpsURLConnection.setDefaultHostnameVerifier(myHostnameVerifier);

			HttpsURLConnection conn = (HttpsURLConnection) new URL(url)
					.openConnection();
			conn.setRequestProperty("Cookie",
					String.valueOf(new Date().getTime()));
			conn.setDoOutput(true);
			conn.setDoInput(true);
			conn.setConnectTimeout(5000);
			conn.connect();

			bitmap = BitmapFactory.decodeStream(conn.getInputStream());

		} catch (Exception e) {
			e.printStackTrace();
		}
		return bitmap;
	}

	private static class MyHostnameVerifier implements HostnameVerifier {
		public boolean verify(String paramString, SSLSession paramSSLSession) {
			return true;
		}
	}

	private static class MyTrustManager implements X509TrustManager {
		public void checkClientTrusted(
				X509Certificate[] paramArrayOfX509Certificate,
				String paramString) throws CertificateException {
		}

		public void checkServerTrusted(
				X509Certificate[] paramArrayOfX509Certificate,
				String paramString) throws CertificateException {
		}

		public X509Certificate[] getAcceptedIssuers() {
			return null;
		}
	}

	public static Bitmap getBitmapFromUrl(String url) {
		Bitmap bitmap = null;

		try {
			URLConnection conn = new URL(url).openConnection();
			conn.setConnectTimeout(CONNECT_TIMEOUT);
			conn.setReadTimeout(READ_TIMEOUT);
			bitmap = BitmapFactory
					.decodeStream((InputStream) conn.getContent());
		} catch (Exception e) {
			e.printStackTrace();
		}

		return bitmap;
	}

}
