package com.lk.qf.pay.beans;

import java.io.Serializable;

public class NoticeBean implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -7869249286256023933L;
	private String title;
	private String content;
	private String time;
	private String id;
	
	public NoticeBean(){}
	public NoticeBean(String title,String content,String id,String time){
		this.id=id;
		this.content=content;
		this.title=title;
		this.time=time;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public String getTime() {
		return time;
	}
	public void setTime(String time) {
		this.time = time;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	

}
