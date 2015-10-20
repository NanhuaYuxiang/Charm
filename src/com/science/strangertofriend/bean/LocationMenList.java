package com.science.strangertofriend.bean;

import java.io.Serializable;

/**
 * @description
 * 
 * @author ÐÒÔËScience ³ÂÍÁŸö
 * @school University of South China
 * @email chentushen.science@gmail.com,274240671@qq.com
 * @2015-5-3
 * 
 */

public class LocationMenList implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -477707387657371625L;

	private String userEmail;
	private String username;
	private String gender;
	private double latitude;
	private double longtitude;
	private String locationTime;

	public LocationMenList() {
	}

	public LocationMenList(String userEmail, String username, String gender,
			double latitude, double longtitude, String locationTime) {
		this.userEmail = userEmail;
		this.username = username;
		this.gender = gender;
		this.latitude = latitude;
		this.longtitude = longtitude;
		this.locationTime = locationTime;
	}

	public String getUserEmail() {
		return userEmail;
	}

	public void setUserEmail(String userEmail) {
		this.userEmail = userEmail;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getGender() {
		return gender;
	}

	public void setGender(String gender) {
		this.gender = gender;
	}

	public double getLatitude() {
		return latitude;
	}

	public void setLatitude(double latitude) {
		this.latitude = latitude;
	}

	public double getLongtitude() {
		return longtitude;
	}

	public void setLongtitude(double longtitude) {
		this.longtitude = longtitude;
	}

	public String getLocationTime() {
		return locationTime;
	}

	public void setLocationTime(String locationTime) {
		this.locationTime = locationTime;
	}

}
