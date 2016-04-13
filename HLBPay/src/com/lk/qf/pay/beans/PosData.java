package com.lk.qf.pay.beans;

import java.io.Serializable;

import com.lk.qf.pay.golbal.Constant;

public class PosData implements Serializable {

	private static final long serialVersionUID = -8535989799281050288L;
	private static PosData posData;
	private PosData(){
		
	}

	public static PosData getPosData(){
		if(posData == null){
			posData = new PosData();
		}
		return posData;
	}
	
	public void clearPosData(){
		posData = null;
	}
	
	private String prdordNo;   //订单号
	private String payType;    //支付方式：01支付账户02终端03快捷
	private String rate;       //费率类型1 民生类2 一般类3 餐娱类4 批发类5 房产类
	private String termNo;     //终端号

	private String termType;   //01蓝牙02音频
	private String payAmt;     //交易金额
	private String track;      //磁道信息
	private String random;     //随机数
	private String mediaType;  //磁卡类型01 磁条卡 02 IC卡
	private String period;     //有效期
	private String icdata;     //55域
	private String crdnum;     //卡序列号
	private String cardNo;
	private String pinKey;     //密码密钥
	private String mac;
	private String pinblok;
	private String type;
	private String tarckTwo;
	private String tarckThree;

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getPinblok() {
		return pinblok;
	}

	public void setPinblok(String pinblok) {
		this.pinblok = pinblok;
	}

	public String getPrdordNo() {
		return prdordNo;
	}

	public void setPrdordNo(String prdordNo) {
		this.prdordNo = prdordNo;
	}

	public String getPayType() {
		return payType;
	}

	public void setPayType(String payType) {
		this.payType = payType;
	}

	public String getRate() {
		return rate;
	}

	public void setRate(String rate) {
		this.rate = rate;
	}

	public String getTermNo() {
		return termNo;
	}

	public void setTermNo(String termNo) {
		this.termNo = termNo;
	}

	public String getTermType() {
		return termType;
	}

	public void setTermType(String termType) {
		this.termType = termType;
	}

	public String getPayAmt() {
		return payAmt;
	}

	public void setPayAmt(String payAmt) {
		this.payAmt = payAmt;
	}

	public String getTrack() {
		return track;
	}

	public void setTrack(String track) {
		this.track = track;
	}

	public String getRandom() {
		return random;
	}

	public void setRandom(String random) {
		this.random = random;
	}

	public String getMediaType() {
		return mediaType;
	}

	public void setMediaType(String mediaType) {
		this.mediaType = mediaType;
	}

	public String getPeriod() {
		return period;
	}

	public void setPeriod(String period) {
		this.period = period;
	}

	public String getIcdata() {
		return icdata;
	}

	public void setIcdata(String icdata) {
		this.icdata = icdata;
	}

	public String getCrdnum() {
		return crdnum;
	}

	public void setCrdnum(String crdnum) {
		this.crdnum = crdnum;
	}

	public String getMac() {
		return mac;
	}

	public void setMac(String mac) {
		this.mac = mac;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	public String getPinKey() {
		return pinKey;
	}

	public void setPinKey(String pinKey) {
		this.pinKey = pinKey;
	}

	public String getCardNo() {
		return cardNo;
	}

	public void setCardNo(String cardNo) {
		this.cardNo = cardNo;
	}

	public String getTarckTwo() {
		return tarckTwo;
	}

	public void setTarckTwo(String tarckTwo) {
		this.tarckTwo = tarckTwo;
	}

	public String getTarckThree() {
		return tarckThree;
	}

	public void setTarckThree(String tarckThree) {
		this.tarckThree = tarckThree;
	}

	
}
