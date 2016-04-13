package com.lk.qf.pay.beans;

import java.util.List;

public class BindDeviceInfo {
	
	private String agentId;
	private String termNo;
	private String terminalNo;
	private String terminalType;
	public String getTerminalNo() {
		return terminalNo;
	}

	public void setTerminalNo(String terminalNo) {
		this.terminalNo = terminalNo;
	}

	public String getTerminalType() {
		return terminalType;
	}

	public void setTerminalType(String terminalType) {
		this.terminalType = terminalType;
	}

	private List<PosRate> rate;

	public String getAgentId() {
		return agentId;
	}

	public void setAgentId(String agentId) {
		this.agentId = agentId;
	}

	public String getTermNo() {
		return termNo;
	}

	public void setTermNo(String termNo) {
		this.termNo = termNo;
	}

	public List<PosRate> getRate() {
		return rate;
	}

	public void setRate(List<PosRate> rate) {
		this.rate = rate;
	}
	
	
	
	
	

}
