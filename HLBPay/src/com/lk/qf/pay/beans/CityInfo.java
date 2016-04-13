package com.lk.qf.pay.beans;

import java.util.List;

/**
 * 二级分类，相当于右侧菜单
 * Created by hanj on 14-9-25.
 */
public class CityInfo {
    private String id;
    private String name;
    private String firatListItemID;

    public CityInfo(){

    }

    public CityInfo(String name,String firatListItemID) {
    	this.name = name;
    	this.firatListItemID = firatListItemID;
    }
    public CityInfo(String id, String name,String firatListItemID) {
        this.id = id;
        this.name = name;
        this.firatListItemID = firatListItemID;
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
    
    

	public String getFiratListItemID() {
		return firatListItemID;
	}

	public void setFiratListItemID(String firatListItemID) {
		this.firatListItemID = firatListItemID;
	}


	@Override
    public String toString() {
        return "ThreeClassItem{" +
                "id=" + id +
                ", name='" + name + '\'' +
                '}';
    }
}
