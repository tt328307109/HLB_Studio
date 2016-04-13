package com.lk.qf.pay.indiana.bean;

import android.os.Parcel;
import android.os.Parcelable;

public class IndianaGoodsInfo implements Parcelable {

	private String code;
	private String message;

	/**
	 * 商品图片log url
	 */
	private String imgUrl;
	/**
	 * 商品图片log url
	 */
	private String imgUrl2;
	/**
	 * 商品图片log url
	 */
	private String imgUrl3;
	/**
	 * 商品图片log url
	 */
	private String imgUrl4;
	/**
	 * 商品图片log url
	 */
	private String imgUrl5;
	/**
	 * 商品名
	 */
	private String goodsName;
	/**
	 * 总需好多钱
	 */
	private String totalAccount;
	/**
	 * 已购多少
	 */
	private int boughtNum;
	/**
	 * 剩余数量
	 */
	private int remainingNum;

	/**
	 * 商品id
	 */
	private String goodsId;
	/**
	 * 商品id2
	 */
	private String goodsId2;
	/**
	 * 商品状态
	 */
	private String type;
	/**
	 * 商品描述
	 */
	private String describe;
	
	/**
	 * 用户名
	 */
	private String userName;
	/**
	 * 用户地址
	 */
	private String userAddress;
	/**
	 * 用户地址id
	 */
	private String userAddressId;
	/**
	 * 用户电话
	 */
	private String userPhoneNum;
	/**
	 * 用户IP
	 */
	private String userIP;
	/**
	 * 用户参与次数
	 */
	private int userCanyuNum;
	
	/**
	 * 本次开奖时间
	 */
	private String openTime;
	/**
	 * 本次开奖剩余时间
	 */
	private long shengyuTime;
	/**
	 * 中奖者
	 */
	private String winnerName;
	/**
	 * 中奖时间
	 */
	private String winningNumber;
	/**
	 * 总共参与人次
	 */
	private int totalParticipateNum;
	/**
	 * 期数
	 */
	private String periodsNum;
	/**
	 * 商品总需数量
	 */
	private int goodsTotal;
	/**
	 * 商品单价
	 */
	private String goodsPrice;
	/**
	 * 省
	 */
	private String province;
	/**
	 * 市
	 */
	private String city;
	/**
	 * 区
	 */
	private String area;
	/**
	 * 是否为默认地址
	 */
	private String isdefAddress;
	/**
	 * 中奖状态
	 */
	private String isxd;
	/**
	 * 订单号
	 */
	private String orderNum;
	/**
	 * 购买时间
	 */
	private String buyTime;

	
	

	

	public long getShengyuTime() {
		return shengyuTime;
	}

	public void setShengyuTime(long shengyuTime) {
		this.shengyuTime = shengyuTime;
	}

	public String getWinnerName() {
		return winnerName;
	}

	public void setWinnerName(String winnerName) {
		this.winnerName = winnerName;
	}

	public String getBuyTime() {
		return buyTime;
	}

	public void setBuyTime(String buyTime) {
		this.buyTime = buyTime;
	}

	public String getOrderNum() {
		return orderNum;
	}

	public void setOrderNum(String orderNum) {
		this.orderNum = orderNum;
	}

	public String getGoodsId2() {
		return goodsId2;
	}

	public void setGoodsId2(String goodsId2) {
		this.goodsId2 = goodsId2;
	}

	public String getIsxd() {
		return isxd;
	}

	public void setIsxd(String isxd) {
		this.isxd = isxd;
	}

	public String getUserAddressId() {
		return userAddressId;
	}

	public void setUserAddressId(String userAddressId) {
		this.userAddressId = userAddressId;
	}

	public String getIsdefAddress() {
		return isdefAddress;
	}

	public void setIsdefAddress(String isdefAddress) {
		this.isdefAddress = isdefAddress;
	}

	public String getDescribe() {
		return describe;
	}

	public void setDescribe(String describe) {
		this.describe = describe;
	}

	public int getRemainingNum() {
		return remainingNum;
	}

	public void setRemainingNum(int remainingNum) {
		this.remainingNum = remainingNum;
	}

	public int getUserCanyuNum() {
		return userCanyuNum;
	}

	public void setUserCanyuNum(int userCanyuNum) {
		this.userCanyuNum = userCanyuNum;
	}

	public int getTotalParticipateNum() {
		return totalParticipateNum;
	}

	public void setTotalParticipateNum(int totalParticipateNum) {
		this.totalParticipateNum = totalParticipateNum;
	}

	public int getGoodsTotal() {
		return goodsTotal;
	}

	public void setGoodsTotal(int goodsTotal) {
		this.goodsTotal = goodsTotal;
	}

	public String getProvince() {
		return province;
	}

	public void setProvince(String province) {
		this.province = province;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getArea() {
		return area;
	}

	public void setArea(String area) {
		this.area = area;
	}

	public String getUserPhoneNum() {
		return userPhoneNum;
	}

	public void setUserPhoneNum(String userPhoneNum) {
		this.userPhoneNum = userPhoneNum;
	}


	public int getBoughtNum() {
		return boughtNum;
	}

	public void setBoughtNum(int boughtNum) {
		this.boughtNum = boughtNum;
	}

	public String getPeriodsNum() {
		return periodsNum;
	}

	public void setPeriodsNum(String periodsNum) {
		this.periodsNum = periodsNum;
	}

	public String getWinningNumber() {
		return winningNumber;
	}

	public void setWinningNumber(String winningNumber) {
		this.winningNumber = winningNumber;
	}


	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getUserAddress() {
		return userAddress;
	}

	public void setUserAddress(String userAddress) {
		this.userAddress = userAddress;
	}

	public String getUserIP() {
		return userIP;
	}

	public void setUserIP(String userIP) {
		this.userIP = userIP;
	}


	public String getOpenTime() {
		return openTime;
	}

	public void setOpenTime(String openTime) {
		this.openTime = openTime;
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

	public String getGoodsName() {
		return goodsName;
	}

	public void setGoodsName(String goodsName) {
		this.goodsName = goodsName;
	}

	public String getTotalAccount() {
		return totalAccount;
	}

	public void setTotalAccount(String totalAccount) {
		this.totalAccount = totalAccount;
	}

	public String getGoodsId() {
		return goodsId;
	}

	public void setGoodsId(String goodsId) {
		this.goodsId = goodsId;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}
	
	

	public String getGoodsPrice() {
		return goodsPrice;
	}

	public void setGoodsPrice(String goodsPrice) {
		this.goodsPrice = goodsPrice;
	}
	
	

	public String getImgUrl2() {
		return imgUrl2;
	}

	public void setImgUrl2(String imgUrl2) {
		this.imgUrl2 = imgUrl2;
	}

	public String getImgUrl3() {
		return imgUrl3;
	}

	public void setImgUrl3(String imgUrl3) {
		this.imgUrl3 = imgUrl3;
	}

	public String getImgUrl4() {
		return imgUrl4;
	}

	public void setImgUrl4(String imgUrl4) {
		this.imgUrl4 = imgUrl4;
	}

	public String getImgUrl5() {
		return imgUrl5;
	}

	public void setImgUrl5(String imgUrl5) {
		this.imgUrl5 = imgUrl5;
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
		parcel.writeString(imgUrl2);
		parcel.writeString(imgUrl3);
		parcel.writeString(imgUrl4);
		parcel.writeString(imgUrl5);
		parcel.writeString(goodsName);
		parcel.writeString(totalAccount);
		parcel.writeInt(boughtNum);
		parcel.writeString(type);
		parcel.writeInt(remainingNum);
		parcel.writeString(goodsId);
		parcel.writeString(userName);
		parcel.writeString(userAddress);
		parcel.writeString(userIP);
		parcel.writeInt(userCanyuNum);
		parcel.writeString(openTime);
		parcel.writeString(winningNumber);
		parcel.writeInt(totalParticipateNum);
		parcel.writeString(periodsNum);
		parcel.writeInt(goodsTotal);
		parcel.writeString(userPhoneNum);
		parcel.writeString(province);
		parcel.writeString(city);
		parcel.writeString(area);
		parcel.writeString(goodsPrice);
		parcel.writeString(describe);
		parcel.writeString(isdefAddress);
		parcel.writeString(userAddressId);
		parcel.writeString(isxd);
		parcel.writeString(goodsId2);
		parcel.writeString(orderNum);
		parcel.writeString(buyTime);
		parcel.writeString(winnerName);
		parcel.writeLong(shengyuTime);
		
		
	}

	public static Creator<IndianaGoodsInfo> CREATOR = new Creator<IndianaGoodsInfo>() {

		@Override
		public IndianaGoodsInfo[] newArray(int arg0) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public IndianaGoodsInfo createFromParcel(Parcel parcel) {
			// TODO Auto-generated method stub

			IndianaGoodsInfo info = new IndianaGoodsInfo();
			info.setImgUrl(parcel.readString());
			info.setImgUrl2(parcel.readString());
			info.setImgUrl3(parcel.readString());
			info.setImgUrl4(parcel.readString());
			info.setImgUrl5(parcel.readString());
			info.setGoodsName(parcel.readString());
			info.setTotalAccount(parcel.readString());
			info.setBoughtNum(parcel.readInt());
			info.setType(parcel.readString());
			info.setRemainingNum(parcel.readInt());
			info.setGoodsId(parcel.readString());
			info.setUserName(parcel.readString());
			info.setUserAddress(parcel.readString());
			info.setUserIP(parcel.readString());
			info.setUserCanyuNum(parcel.readInt());
			info.setOpenTime(parcel.readString());
			info.setWinningNumber(parcel.readString());
			info.setTotalParticipateNum(parcel.readInt());
			info.setPeriodsNum(parcel.readString());
			info.setGoodsTotal(parcel.readInt());
			info.setUserPhoneNum(parcel.readString());
			info.setProvince(parcel.readString());
			info.setCity(parcel.readString());
			info.setArea(parcel.readString());
			info.setGoodsPrice(parcel.readString());
			info.setDescribe(parcel.readString());
			info.setIsdefAddress(parcel.readString());
			info.setUserAddressId(parcel.readString());
			info.setIsxd(parcel.readString());
			info.setGoodsId2(parcel.readString());
			info.setOrderNum(parcel.readString());
			info.setBuyTime(parcel.readString());
			info.setWinnerName(parcel.readString());
			info.setShengyuTime(parcel.readLong());
			return info;
		}
	};

}
