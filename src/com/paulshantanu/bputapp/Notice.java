package com.paulshantanu.bputapp;

import java.util.ArrayList;

public class Notice {
	
	ArrayList<String> url = new ArrayList<String>();
    ArrayList<String> notice_head = new ArrayList<String>();
    ArrayList<String> table_head = new ArrayList<String>();
    ArrayList<String> table_body = new ArrayList<String>();
	StringBuffer notice_body = new StringBuffer();
	Boolean has_table = false;
	
	public ArrayList<String> getUrl() {
		return url;
	}
	public void setUrl(int pos, String url) {
		this.url.add(pos,url);
	}
	public ArrayList<String> getNotice_head() {
		return notice_head;
	}
	public void setNotice_head(int pos,String notice_head) {
		this.notice_head.add(pos,notice_head);
	}
	public ArrayList<String> getTable_head() {
		return table_head;
	}
	public void setTable_head( String table_head) {
		this.table_head.add(table_head);
	}
	public ArrayList<String> getTable_body() {
		return table_body;
	}
	public void setTable_body(String table_body) {
		this.table_body.add(table_body);
	}
	public String getNotice_body() {
		
		return notice_body.toString().replaceAll("<br>", "\n");
	}
	public void setNotice_body(String notice_body) {
		this.notice_body.append(notice_body);
	}
	public Boolean getHas_table() {
		return has_table;
	}
	public void setHas_table(Boolean has_table) {
		this.has_table = has_table;
	}
	
	
	

}
