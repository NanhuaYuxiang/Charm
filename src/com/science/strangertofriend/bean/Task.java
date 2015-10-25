package com.science.strangertofriend.bean;

import java.io.Serializable;

import com.avos.avoscloud.AVGeoPoint;

/**
 * 
 * @author 赵鑫
 * @description 任务实体类
 * @email apologizetoher@Gmail.com / 491498408@qq.com
 * @date 2015-10-25 下午12:53:35
 */
public class Task implements Serializable {

	private static final long serialVersionUID = 1L;
	private String publisherName;
	private String acceptedName;
	private String theme;
	private String taskDescription;
	private String endTime;// 任务截止时间
	private AVGeoPoint geopoint;// 发布任务的地点
	private String location;// 任务地点
	private double latitude;
	private double longitude;
	private boolean isAccepted;
	private boolean isAccomplished;
	private String price;
	private String type;

	public Task() {
		super();
	}

	public double getLatitude() {
		return latitude;
	}

	public void setLatitude(double latitude) {
		this.latitude = latitude;
	}

	public double getLongitude() {
		return longitude;
	}

	public void setLongitude(double longitude) {
		this.longitude = longitude;
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getTheme() {
		return theme;
	}

	public void setTheme(String theme) {
		this.theme = theme;
	}

	public String getPublisherName() {
		return publisherName;
	}

	public void setPublisherName(String publisherName) {
		this.publisherName = publisherName;
	}

	public String getAcceptedName() {
		return acceptedName;
	}

	public void setAcceptedName(String acceptedName) {
		this.acceptedName = acceptedName;
	}

	public String getTaskDescription() {
		return taskDescription;
	}

	public void setTaskDescription(String taskDescription) {
		this.taskDescription = taskDescription;
	}

	public String getEndTime() {
		return endTime;
	}

	public void setEndTime(String endTime) {
		this.endTime = endTime;
	}

	public AVGeoPoint getGeopoint() {
		return geopoint;
	}

	public void setGeopoint(AVGeoPoint geopoint) {
		this.geopoint = geopoint;
	}

	public boolean isAccepted() {
		return isAccepted;
	}

	public void setAccepted(boolean isAccepted) {
		this.isAccepted = isAccepted;
	}

	public boolean isAccomplished() {
		return isAccomplished;
	}

	public void setAccomplished(boolean isAccomplished) {
		this.isAccomplished = isAccomplished;
	}

	public String getPrice() {
		return price;
	}

	public void setPrice(String price) {
		this.price = price;
	}

	@Override
	public String toString() {
		return "Task [publisherName=" + publisherName + ", acceptedName="
				+ acceptedName + ", theme=" + theme + ", taskDescription="
				+ taskDescription + ", endTime=" + endTime + ", location="
				+ location + ", latitude=" + latitude + ", longitude="
				+ longitude + ", isAccepted=" + isAccepted
				+ ", isAccomplished=" + isAccomplished + ", price=" + price
				+ ", type=" + type + "]";
	}

}
