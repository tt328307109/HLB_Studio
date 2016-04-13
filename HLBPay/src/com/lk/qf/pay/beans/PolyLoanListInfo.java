package com.lk.qf.pay.beans;

import android.os.Parcel;
import android.os.Parcelable;

public class PolyLoanListInfo implements Parcelable{

	private String code;
	private String message;
	/**
	 * 贷款金额
	 */
	private String loanAccount;
	/**
	 * 贷款时间
	 */
	private String loanTime;
	/**
	 * 还款状态
	 */
	private String loanType;
	/**
	 * 贷款单号
	 */
	private String loanOrderNum;
	/**
	 * id号
	 */
	private String pid;
	
	
	
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
	public String getLoanAccount() {
		return loanAccount;
	}
	public void setLoanAccount(String loanAccount) {
		this.loanAccount = loanAccount;
	}
	public String getLoanTime() {
		return loanTime;
	}
	public void setLoanTime(String loanTime) {
		this.loanTime = loanTime;
	}
	public String getLoanType() {
		return loanType;
	}
	public void setLoanType(String loanType) {
		this.loanType = loanType;
	}
	public String getLoanOrderNum() {
		return loanOrderNum;
	}
	public void setLoanOrderNum(String loanOrderNum) {
		this.loanOrderNum = loanOrderNum;
	}
	public String getPid() {
		return pid;
	}
	public void setPid(String pid) {
		this.pid = pid;
	}
	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}
	@Override
	public void writeToParcel(Parcel parcel, int arg1) {
		// TODO Auto-generated method stub
		parcel.writeString(loanAccount);
		parcel.writeString(loanTime);
		parcel.writeString(loanType);
		parcel.writeString(loanOrderNum);
		parcel.writeString(pid);
		
	}
	
	public static Creator<PolyLoanListInfo> CREATOR = new Creator<PolyLoanListInfo>() {

		@Override
		public PolyLoanListInfo[] newArray(int arg0) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public PolyLoanListInfo createFromParcel(Parcel parcel) {
			// TODO Auto-generated method stub

			PolyLoanListInfo info = new PolyLoanListInfo();
			info.setLoanAccount(parcel.readString());
			info.setLoanTime(parcel.readString());
			info.setLoanType(parcel.readString());
			info.setLoanOrderNum(parcel.readString());
			info.setPid(parcel.readString());

			return info;
		}
	};
}
