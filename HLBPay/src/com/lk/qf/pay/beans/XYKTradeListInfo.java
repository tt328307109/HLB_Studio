package com.lk.qf.pay.beans;

public class XYKTradeListInfo {

	private String code;
	private String message;
	private String hkAccount;//还款金额
	private String sxAccount;//手续费
	private String date;//時間
	private String type;//類型
	private String amType;//上午類型
	private String pmType;//下午類型
	private String amAccount;//下午金额
	private String pmAccount;//下午金额

	
	
	public String getAmType() {
		return amType;
	}

	public void setAmType(String amType) {
		this.amType = amType;
	}

	public String getPmType() {
		return pmType;
	}

	public void setPmType(String pmType) {
		this.pmType = pmType;
	}

	public String getAmAccount() {
		return amAccount;
	}

	public void setAmAccount(String amAccount) {
		this.amAccount = amAccount;
	}

	public String getPmAccount() {
		return pmAccount;
	}

	public void setPmAccount(String pmAccount) {
		this.pmAccount = pmAccount;
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

	public String getHkAccount() {
		return hkAccount;
	}

	public void setHkAccount(String hkAccount) {
		this.hkAccount = hkAccount;
	}

	public String getSxAccount() {
		return sxAccount;
	}

	public void setSxAccount(String sxAccount) {
		this.sxAccount = sxAccount;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

}
