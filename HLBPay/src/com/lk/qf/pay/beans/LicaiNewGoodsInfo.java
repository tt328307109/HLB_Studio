package com.lk.qf.pay.beans;

import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;

public class LicaiNewGoodsInfo implements Parcelable{

	private String code;
	private String message;
	/**
	 * 理财产品id
	 */
	private String proid;
	/**
	 * 理财产品名称
	 */
	private String nameTitle;
	/**
	 * 理财年化收益率
	 */
	private String yearEarnings;
	/**
	 * 理财期限
	 */
	private String timeLimit;
	/**
	 * 理财购买最低限额
	 */
	private String qgAccount;
	/**
	 * 理财产品买入时间
	 */
	private String buyTime;
	/**
	 * 理财产品买入金额
	 */
	private String buyAccount;
	/**
	 * 昨日收益
	 */
	private String yesterDayEarningsAccount;
	/**
	 * 总收益
	 */
	private String totalEarningsAccount;
	/**
	 * 理财详情待定
	 */
	private String strInfo;
	/**
	 * 起息日
	 */
	private String qixiDate;
	/**
	 * 到期日
	 */
	private String daoqiDate;
	
	/**
	 * 贷款状态
	 */
	private String loanType;
	/**
	 * 理财订单id
	 */
	private String crodId;
	/**
	 * 理财预期收益
	 */
	private String earnings;
	
	
	
	public String getEarnings() {
		return earnings;
	}
	public void setEarnings(String earnings) {
		this.earnings = earnings;
	}
	public String getCrodId() {
		return crodId;
	}
	public void setCrodId(String crodId) {
		this.crodId = crodId;
	}
	public String getProid() {
		return proid;
	}
	public void setProid(String proid) {
		this.proid = proid;
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
	public String getYearEarnings() {
		return yearEarnings;
	}
	public void setYearEarnings(String yearEarnings) {
		this.yearEarnings = yearEarnings;
	}
	public String getTimeLimit() {
		return timeLimit;
	}
	public void setTimeLimit(String timeLimit) {
		this.timeLimit = timeLimit;
	}
	public String getNameTitle() {
		return nameTitle;
	}
	public void setNameTitle(String nameTitle) {
		this.nameTitle = nameTitle;
	}
	public String getQgAccount() {
		return qgAccount;
	}
	public void setQgAccount(String qgAccount) {
		this.qgAccount = qgAccount;
	}
	public String getBuyTime() {
		return buyTime;
	}
	public void setBuyTime(String buyTime) {
		this.buyTime = buyTime;
	}
	public String getBuyAccount() {
		return buyAccount;
	}
	public void setBuyAccount(String buyAccount) {
		this.buyAccount = buyAccount;
	}
	
	
	public String getYesterDayEarningsAccount() {
		return yesterDayEarningsAccount;
	}
	public void setYesterDayEarningsAccount(String yesterDayEarningsAccount) {
		this.yesterDayEarningsAccount = yesterDayEarningsAccount;
	}
	public String getTotalEarningsAccount() {
		return totalEarningsAccount;
	}
	public void setTotalEarningsAccount(String totalEarningsAccount) {
		this.totalEarningsAccount = totalEarningsAccount;
	}
	
	
	public String getStrInfo() {
		return strInfo;
	}
	public void setStrInfo(String strInfo) {
		this.strInfo = strInfo;
	}
	
	public String getQixiDate() {
		return qixiDate;
	}
	public void setQixiDate(String qixiDate) {
		this.qixiDate = qixiDate;
	}
	public String getDaoqiDate() {
		return daoqiDate;
	}
	public void setDaoqiDate(String daoqiDate) {
		this.daoqiDate = daoqiDate;
	}
	
	
	public String getLoanType() {
		return loanType;
	}
	public void setLoanType(String loanType) {
		this.loanType = loanType;
	}
	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}
	@Override
	public void writeToParcel(Parcel parcel, int arg1) {
		// TODO Auto-generated method stub
		parcel.writeString(nameTitle);
		parcel.writeString(yearEarnings);
		parcel.writeString(timeLimit);
		parcel.writeString(qgAccount);
		parcel.writeString(buyTime);
		parcel.writeString(buyAccount);
		parcel.writeString(yesterDayEarningsAccount);
		parcel.writeString(totalEarningsAccount);
		parcel.writeString(strInfo);
		parcel.writeString(proid);
		parcel.writeString(qixiDate);
		parcel.writeString(daoqiDate);
		parcel.writeString(loanType);
		parcel.writeString(crodId);
		parcel.writeString(earnings);
	}
	
	public static Creator<LicaiNewGoodsInfo> CREATOR = new Creator<LicaiNewGoodsInfo>() {

		@Override
		public LicaiNewGoodsInfo[] newArray(int arg0) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public LicaiNewGoodsInfo createFromParcel(Parcel parcel) {
			// TODO Auto-generated method stub

			LicaiNewGoodsInfo info = new LicaiNewGoodsInfo();
			info.setNameTitle(parcel.readString());
			info.setYearEarnings(parcel.readString());
			info.setTimeLimit(parcel.readString());
			info.setQgAccount(parcel.readString());
			info.setBuyTime(parcel.readString());
			info.setBuyAccount(parcel.readString());
			info.setYesterDayEarningsAccount(parcel.readString());
			info.setTotalEarningsAccount(parcel.readString());
			info.setStrInfo(parcel.readString());
			info.setProid(parcel.readString());
			info.setQixiDate(parcel.readString());
			info.setDaoqiDate(parcel.readString());
			info.setLoanType(parcel.readString());
			info.setCrodId(parcel.readString());
			info.setEarnings(parcel.readString());
			return info;
		}
	};
}
