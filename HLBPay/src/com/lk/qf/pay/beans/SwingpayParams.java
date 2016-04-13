package com.lk.qf.pay.beans;

import java.io.Serializable;

public class SwingpayParams implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8301503144266762652L;
	private String tradeType;
	private String cardNo;
	private String amount;
	private String ordNo;
	private String randomNum;
	private String encTrackData;
	private String cardType;
	private String mediaType;
	private String rateType;
	private String ksn;
	private String DCDATA;
	private String blueTootchAddress;
	private String Pinblock;
	
	

	public String getPinblock() {
		return Pinblock;
	}


	public void setPinblock(String pinblock) {
		Pinblock = pinblock;
	}


	public String getBlueTootchAddress() {
		return blueTootchAddress;
	}


	public void setBlueTootchAddress(String blueTootchAddress) {
		this.blueTootchAddress = blueTootchAddress;
	}


	@Override
	public String toString() {
		String sb = "[rateTypoe]="+rateType+ "[tradeType]=" + tradeType + "\n[cardNo]=" + cardNo
				+ "[ordNo]=" + ordNo + "\n[amount]=" + amount + "[randomNum]="
				+ randomNum + "[ksn]=" + ksn + "[cardtype=]" + cardType
				+ "[encTrackData]=" + encTrackData;

		return sb;
	}

	
	public String getRateType() {
		return rateType;
	}

	public void setRateType(String rateType) {
		this.rateType = rateType;
	}

	public String getMediaType() {
		return mediaType;
	}

	public void setMediaType(String mediaType) {
		this.mediaType = mediaType;
	}

	public String getKsn() {
		return ksn;
	}

	public void setKsn(String ksn) {
		this.ksn = ksn;
	}

	public String getTradeType() {
		return tradeType;
	}

	public void setTradeType(String tradeType) {
		this.tradeType = tradeType;
	}

	public String getCardNo() {
		return cardNo;
	}

	public void setCardNo(String cardNo) {
		this.cardNo = cardNo;
	}

	public String getAmount() {
		return amount;
	}

	public void setAmount(String amount) {
		this.amount = amount;
	}

	public String getOrdNo() {
		return ordNo;
	}

	public void setOrdNo(String ordNo) {
		this.ordNo = ordNo;
	}

	public String getRandomNum() {
		return randomNum;
	}

	public void setRandomNum(String randomNum) {
		this.randomNum = randomNum;
	}

	public String getEncTrackData() {
		return encTrackData;
	}

	public void setEncTrackData(String encTrackData) {
		this.encTrackData = encTrackData;
	}

	public String getCardType() {
		return cardType;
	}

	public void setCardType(String cardType) {
		this.cardType = cardType;
	}


	public String getDCDATA() {
		return DCDATA;
	}


	public void setDCDATA(String dCDATA) {
		DCDATA = dCDATA;
	}

}
