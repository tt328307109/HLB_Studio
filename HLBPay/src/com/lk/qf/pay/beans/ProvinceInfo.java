package com.lk.qf.pay.beans;

import java.util.List;

/**
 * 一级分类，相当于左侧菜单
 * Created by hanj on 14-9-25.
 */
public class ProvinceInfo {
    private String id;
    private String name;
    private String code;
    private String message;
    private List<CityInfo> secondList;
    
    public ProvinceInfo(){

    }

    public ProvinceInfo(String name) {
    	this.name = name;
    }
    public ProvinceInfo(String id, String name) {
        this.id = id;
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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
	
	

	public List<CityInfo> getSecondList() {
		return secondList;
	}

	public void setSecondList(List<CityInfo> secondList) {
		this.secondList = secondList;
	}

	@Override
    public String toString() {
        return "FirstClassItem{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", secondList="  +
                '}';
    }
}
