/**
 * 功能：Constant
 * 类名：Constant.java
 * 日期：2013-11-26
 * 作者：lukejun
 */
package com.lk.qf.pay.golbal;


/**
 * @ClassName: Constant
 * @Description: Constant
 * @author lukejun
 * @date 2013-11-26 下午1:43:35
 * 
 */
public class Constant {
	public static final String BACKGROUND_KEY = "123456";
	public static final String SYS_TYPE = "Android";
	public static final String SYS_VERSIN = android.os.Build.VERSION.RELEASE;
	
	public static final String MD5_KEY_VALUE = "0123456789ABCDEF";
	public static boolean DEBUG = true;

	public static final String SHOW_TITLE = "SHOW_TITLE";
	public static final String SHOW_MSG = "SHOW_MSG";

	public static final boolean IS_HTTP = true;
	public static final String URL_TEST = "http://61.161.222.6:8091/mpos/";
	public static final String URL_DEV = "http://116.228.51.133:7001/RMobPay/";
	public static final String URL_ONLINE = "";
	public static String ROOT_URL = URL_TEST;
	public static int KSN_LENGTH = 12;

	/**
	 * 
	 */
	public static int LOGIN_PWD_LENGTH_MIN=6;
	/**
	 * 
	 */
	public static int LOGIN_PWD_LENGTH_MAX=20;
	/**
	 * 
	 */
	public static int PAY_PWD_LENGTH_MIN=6;
	/**
	 * 
	 */
	public static int PAY_PWD_LENGTH_MAX=20;
}
