package com.lk.qf.pay.beans;

public class OrderInfo {

	private String tradingTime;// ����ʱ��
	private String orderNum;// ������
	private String merchantsName;// �̻���
	private String consumptionAmount;// ��ѽ��
	private String cardNum;// ֧������
	private String code;
	private String message;
	private String tradingInformation;//����״̬
	private String nottime;//����״̬
	private String sxAccount;//手续费
	private String state;//手续费
	private String t0Ort1;//t0还是t1
	

	
	public String getT0Ort1() {
		return t0Ort1;
	}

	public void setT0Ort1(String t0Ort1) {
		this.t0Ort1 = t0Ort1;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	public String getTradingTime() {
		return tradingTime;
	}

	public void setTradingTime(String tradingTime) {
		this.tradingTime = tradingTime;
	}

	public String getOrderNum() {
		return orderNum;
	}

	public void setOrderNum(String orderNum) {
		this.orderNum = orderNum;
	}

	public String getMerchantsName() {
		return merchantsName;
	}

	public void setMerchantsName(String merchantsName) {
		this.merchantsName = merchantsName;
	}

	public String getConsumptionAmount() {
		return consumptionAmount;
	}

	public void setConsumptionAmount(String consumptionAmount) {
		this.consumptionAmount = consumptionAmount;
	}

	public String getCardNum() {
		return cardNum;
	}

	public void setCardNum(String cardNum) {
		this.cardNum = cardNum;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String getTradingInformation() {
		return tradingInformation;
	}

	public void setTradingInformation(String tradingInformation) {
		this.tradingInformation = tradingInformation;
	}

	public String getNottime() {
		return nottime;
	}

	public void setNottime(String nottime) {
		this.nottime = nottime;
	}

	public String getSxAccount() {
		return sxAccount;
	}

	public void setSxAccount(String sxAccount) {
		this.sxAccount = sxAccount;
	}
	
	
}
