package com.lk.qf.pay.wedget.webview;

public class Constant {
	public static String AllianceId = "27305";
	public static String SId = "464067";
	public static String SecretKey = "183F3ADA-47D7-40E1-A511-44BDDBA6B9D5";
	private static String concatstr="&sid="+SId+"&allianceid="+AllianceId+"&ouid=";
//	http://u.ctrip.com/union/CtripRedirect.aspx?TypeID=615&sid=1&allianceid=1&ouid=
	private static String baseUrl="http://u.ctrip.com/union/CtripRedirect.aspx?TypeID=";
	
	/**
	 * 机票首页
	 */
	public static String airTicketHome=baseUrl+"615"+concatstr;
	/**
	 * 酒店
	 */
	public static String hotelHome=baseUrl+"636"+concatstr;
	
	/**
	 * 门票
	 */
	public static String scenicTicket=baseUrl+"650"+concatstr;
	
	

}
