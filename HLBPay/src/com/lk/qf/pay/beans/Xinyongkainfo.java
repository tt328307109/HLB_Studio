package com.lk.qf.pay.beans;

import android.os.Parcel;
import android.os.Parcelable;

public class Xinyongkainfo implements Parcelable {

	private String code;
	private String message;

	/**
	 * 银行log
	 */
	private String imgUrl;
	/**
	 * 用户名
	 */
	private String userName;
	/**
	 * 信用卡号
	 */
	private String cardNum;
	/**
	 * 还款时间状态
	 */
	private String date;
	/**
	 * 还款状态
	 */
	private String type;

	/**
	 * 银行名
	 */
	private String bankName;
	/**
	 * 信用卡额度
	 */
	private String bankmoney;

	/**
	 * 账单金额
	 */
	private String billmoney;
	/**
	 * 账单日
	 */
	private String billtime;
	/**
	 * 还款日下月
	 */
	private String reimtime;
	/**
	 * 还款日当月
	 */
	private String bei;
	/**
	 * 未还金额
	 */
	private String weihuanAccount;
	/**
	 * id
	 */
	private String id;
	/**
	 * bankCode
	 */
	private String bankCode;
	/**
	 * 身份证号
	 */
	private String idCardNum;
	/**
	 * 持卡人手机号
	 */
	private String phoneNum;
	/**
	 * 还款金额
	 */
	private String reimmoney;
	/**
	 * 手续费
	 */
	private String poundage;
	/**
	 * cardCode
	 */
	private String cardCode;
	/**
	 * 还款日
	 */
	private String refundDay;
	/**
	 * 退回地址
	 */
	private String address;
	/**
	 * 还款期数
	 */
	private String repayDate;
	
	
	
	public String getRepayDate() {
		return repayDate;
	}

	public void setRepayDate(String repayDate) {
		this.repayDate = repayDate;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getBankCode() {
		return bankCode;
	}

	public void setBankCode(String bankCode) {
		this.bankCode = bankCode;
	}

	public String getIdCardNum() {
		return idCardNum;
	}

	public void setIdCardNum(String idCardNum) {
		this.idCardNum = idCardNum;
	}

	public String getPhoneNum() {
		return phoneNum;
	}

	public void setPhoneNum(String phoneNum) {
		this.phoneNum = phoneNum;
	}

	public String getReimmoney() {
		return reimmoney;
	}

	public void setReimmoney(String reimmoney) {
		this.reimmoney = reimmoney;
	}

	public String getPoundage() {
		return poundage;
	}

	public void setPoundage(String poundage) {
		this.poundage = poundage;
	}

	public String getCardCode() {
		return cardCode;
	}

	public void setCardCode(String cardCode) {
		this.cardCode = cardCode;
	}

	public String getRefundDay() {
		return refundDay;
	}

	public void setRefundDay(String refundDay) {
		this.refundDay = refundDay;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getWeihuanAccount() {
		return weihuanAccount;
	}

	public void setWeihuanAccount(String weihuanAccount) {
		this.weihuanAccount = weihuanAccount;
	}

	public String getBei() {
		return bei;
	}

	public void setBei(String bei) {
		this.bei = bei;
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

	public String getImgUrl() {
		return imgUrl;
	}

	public void setImgUrl(String imgUrl) {
		this.imgUrl = imgUrl;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getCardNum() {
		return cardNum;
	}

	public void setCardNum(String cardNum) {
		this.cardNum = cardNum;
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

	public String getBankName() {
		return bankName;
	}

	public void setBankName(String bankName) {
		this.bankName = bankName;
	}

	public String getBankmoney() {
		return bankmoney;
	}

	public void setBankmoney(String bankmoney) {
		this.bankmoney = bankmoney;
	}

	public String getBillmoney() {
		return billmoney;
	}

	public void setBillmoney(String billmoney) {
		this.billmoney = billmoney;
	}

	public String getBilltime() {
		return billtime;
	}

	public void setBilltime(String billtime) {
		this.billtime = billtime;
	}

	public String getReimtime() {
		return reimtime;
	}

	public void setReimtime(String reimtime) {
		this.reimtime = reimtime;
	}

	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void writeToParcel(Parcel parcel, int arg1) {
		// TODO Auto-generated method stub
		parcel.writeString(imgUrl);
		parcel.writeString(userName);
		parcel.writeString(cardNum);
		parcel.writeString(date);
		parcel.writeString(type);
		parcel.writeString(bankName);
		parcel.writeString(bankmoney);
		parcel.writeString(billmoney);
		parcel.writeString(billtime);
		parcel.writeString(reimtime);
		parcel.writeString(bei);
		parcel.writeString(weihuanAccount);
		parcel.writeString(id);
		parcel.writeString(bankCode);
		parcel.writeString(idCardNum);
		parcel.writeString(phoneNum);
		parcel.writeString(reimmoney);
		parcel.writeString(poundage);
		parcel.writeString(cardCode);
		parcel.writeString(refundDay);
		parcel.writeString(address);
		parcel.writeString(repayDate);
	}

	public static Creator<Xinyongkainfo> CREATOR = new Creator<Xinyongkainfo>() {

		@Override
		public Xinyongkainfo[] newArray(int arg0) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public Xinyongkainfo createFromParcel(Parcel parcel) {
			// TODO Auto-generated method stub

			Xinyongkainfo info = new Xinyongkainfo();
			info.setImgUrl(parcel.readString());
			info.setUserName(parcel.readString());
			info.setCardNum(parcel.readString());
			info.setDate(parcel.readString());
			info.setType(parcel.readString());
			info.setBankName(parcel.readString());
			info.setBankmoney(parcel.readString());
			info.setBillmoney(parcel.readString());
			info.setBilltime(parcel.readString());
			info.setReimtime(parcel.readString());
			info.setBei(parcel.readString());
			info.setWeihuanAccount(parcel.readString());
			info.setId(parcel.readString());
			info.setBankCode(parcel.readString());
			info.setIdCardNum(parcel.readString());
			info.setPhoneNum(parcel.readString());
			info.setReimmoney(parcel.readString());
			info.setPoundage(parcel.readString());
			info.setCardCode(parcel.readString());
			info.setRefundDay(parcel.readString());
			info.setAddress(parcel.readString());
			info.setRepayDate(parcel.readString());

			return info;
		}
	};

}
