package com.science.strangertofriend.bean;

/**
 * @description
 * 
 * @author 幸运Science 陈土燊
 * @school University of South China
 * @email chentushen.science@gmail.com,274240671@qq.com
 * @2015-5-19
 * 
 */

public class SortModel {

	private String name;// 好友用户名
	private String sortLetters;// 显示数据拼音的首字母
	private String avaterUrl;// 好友头像URL
	private String email;// 好友Email
	private String gender;// 好友性别

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getSortLetters() {
		return sortLetters;
	}

	public void setSortLetters(String sortLetters) {
		this.sortLetters = sortLetters;
	}

	public String getAvaterUrl() {
		return avaterUrl;
	}

	public void setAvaterUrl(String avaterUrl) {
		this.avaterUrl = avaterUrl;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getGender() {
		return gender;
	}

	public void setGender(String gender) {
		this.gender = gender;
	}

}
