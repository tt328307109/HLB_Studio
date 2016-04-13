package com.lk.qf.pay.beans;

public class IncomeInfo {

	private String code;
	private String message;
	/**
	 * 交易金额
	 */
	private String tradingAccount;
	/**
	 * 级别
	 */
	private String level;
	
	/**
	 * 收益金额
	 */
	private String incomeAccount;
	
	/**
	 * 时间
	 */
	private String time;

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


	public String getTradingAccount() {
		return tradingAccount;
	}

	public void setTradingAccount(String tradingAccount) {
		this.tradingAccount = tradingAccount;
	}

	public String getLevel() {
		return level;
	}

	public void setLevel(String level) {
		this.level = level;
	}

	public String getIncomeAccount() {
		return incomeAccount;
	}

	public void setIncomeAccount(String incomeAccount) {
		this.incomeAccount = incomeAccount;
	}

	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time;
	}

	
}
