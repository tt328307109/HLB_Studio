package com.lk.qf.pay.beans;

public class Record {
	private String city;
	private String code;
	@Override
	public String toString() {
		return "Record [city=" + city + ", code=" + code + "]";
	}
	public String getCity() {
		return city;
	}
	public void setCity(String city) {
		this.city = city;
	}
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}

}
