package com.lk.qf.pay.beans;

import java.util.List;

public class CountyInfo {

	private String id;
    private String name;
    private String secondListItemID;

    public CountyInfo(){

    }

    public CountyInfo(String name,String secondListItemID) {
    	this.name = name;
    	this.secondListItemID = secondListItemID;
    	
    }
    public CountyInfo(String id, String name,String secondListItemID) {
        this.id = id;
        this.name = name;
        this.secondListItemID = secondListItemID;
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
    
    

	public String getSecondListItemID() {
		return secondListItemID;
	}

	public void setSecondListItemID(String secondListItemID) {
		this.secondListItemID = secondListItemID;
	}

	@Override
    public String toString() {
        return "SecondClassItem{" +
                "id=" + id +
                ", name='" + name + '\'' +
                '}';
    }
}
