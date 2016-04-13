package com.lk.qf.pay.indiana.saidan;

import java.util.ArrayList;
import java.util.HashMap;

public class MSG {
	private String id;
	private String goodsId;
	private String name;
	private String time;
	private String info;
	private String state;
	private ArrayList<String> mList;
	
	
	public String getGoodsId() {
		return goodsId;
	}

	public void setGoodsId(String goodsId) {
		this.goodsId = goodsId;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public ArrayList<String> getList() {
		return mList;
	}
	
	public void setList(ArrayList<String> mList) {
		this.mList = mList;
	}  
	
	public String getInfo() {
		return info;
	}
	
	public void setInfo(String info) {
		this.info = info;
	}

	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	
}
