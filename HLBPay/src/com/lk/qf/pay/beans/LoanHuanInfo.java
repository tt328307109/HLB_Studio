package com.lk.qf.pay.beans;

public class LoanHuanInfo {

	private String code;
	private String message;
	/**
	 * 还款金额
	 */
	private String reimMoney;
	/**
	 * 实际还款金额
	 */
	private String actualMoney;
	
	/**
	 * 状态
	 */
	private String state;
	
	/**
	 * 订单号
	 */
	private String dingdanNum;
	/**
	 * 还款时间
	 */
	private String hkTime;
	/**
	 * 需还总金额
	 */
	private String xhTotalAccount;
	/**
	 * 还款类型
	 */
	private String hkType;
	
	
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
	public String getReimMoney() {
		return reimMoney;
	}
	public void setReimMoney(String reimMoney) {
		this.reimMoney = reimMoney;
	}
	public String getActualMoney() {
		return actualMoney;
	}
	public void setActualMoney(String actualMoney) {
		this.actualMoney = actualMoney;
	}
	public String getState() {
		return state;
	}
	public void setState(String state) {
		this.state = state;
	}
	public String getDingdanNum() {
		return dingdanNum;
	}
	public void setDingdanNum(String dingdanNum) {
		this.dingdanNum = dingdanNum;
	}
	public String getHkTime() {
		return hkTime;
	}
	public void setHkTime(String hkTime) {
		this.hkTime = hkTime;
	}
	public String getXhTotalAccount() {
		return xhTotalAccount;
	}
	public void setXhTotalAccount(String xhTotalAccount) {
		this.xhTotalAccount = xhTotalAccount;
	}
	public String getHkType() {
		return hkType;
	}
	public void setHkType(String hkType) {
		this.hkType = hkType;
	}
	
	
}
