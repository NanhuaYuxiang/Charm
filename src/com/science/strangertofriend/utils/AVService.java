package com.science.strangertofriend.utils;

import java.io.FileNotFoundException;
import java.io.IOException;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVFile;
import com.avos.avoscloud.AVGeoPoint;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.RequestPasswordResetCallback;
import com.avos.avoscloud.SaveCallback;
import com.avos.avoscloud.SignUpCallback;

/**
 * @description avos云服务操作
 * 
 * @author 幸运Science 陈土燊
 * @school University of South China
 * @email chentushen.science@gmail.com,274240671@qq.com
 * @2015-4-26
 * 
 */

public class AVService {

	// 注册
	public static void signUp(String username, String password, String email,
			String gender, String installationId, SignUpCallback signUpCallback) {

		AVUser user = new AVUser();
		user.setUsername(username);
		user.setPassword(password);
		user.setEmail(email);
		user.put("gender", gender);
		user.put("installationId", installationId);
		user.signUpInBackground(signUpCallback);
	}

	// 消息列表
	public static void messageList(String friend, String urlAvater,
			String currentUser, String sendTime, String messsage) {
		AVObject avObject = new AVObject("MessageList");
		avObject.put("friend", friend);
		avObject.put("urlAvater", urlAvater);
		avObject.put("currentUser", currentUser);
		avObject.put("sendTime", sendTime);
		avObject.put("messsage", messsage);
		avObject.saveInBackground();
	}

	// 更新消息列表
	public static void updateMessageList(String objID, String sendTime,
			String messsage) {

		AVObject messageList = new AVObject("MessageList");
		AVQuery<AVObject> query = new AVQuery<AVObject>("MessageList");
		try {
			messageList = query.get(objID);
		} catch (AVException e) {
			e.printStackTrace();
		}
		messageList.put("messsage", messsage);
		messageList.put("sendTime", sendTime);
		messageList.saveInBackground();
	}

	// 删除消息列表
	public static void removeMessage(String objectId) {
		AVQuery<AVObject> query = new AVQuery<AVObject>("MessageList");
		AVObject avObj = null;
		try {
			avObj = query.get(objectId);
			avObj.delete();
		} catch (AVException e) {
			e.printStackTrace();
		}
	}

	// 好友通讯录列表
	public static void addressList(String friend, String currentUser,
			String avaterUrl, String email, String gender, String sendTime) {
		AVObject avObject = new AVObject("AddressList");
		avObject.put("currentUser", currentUser);
		avObject.put("friend", friend);
		avObject.put("friendAvaterUrl", avaterUrl);
		avObject.put("friendEmail", email);
		avObject.put("friendGender", gender);
		avObject.put("sendTime", sendTime);
		avObject.saveInBackground();
	}

	// 删除好友
	public static void removeFriends(String objectId) {
		AVQuery<AVObject> query = new AVQuery<AVObject>("AddressList");
		AVObject avObj = null;
		try {
			avObj = query.get(objectId);
			avObj.delete();
		} catch (AVException e) {
			e.printStackTrace();
		}
	}

	public static void requestPasswordReset(String email,
			RequestPasswordResetCallback callback) {
		AVUser.requestPasswordResetInBackground(email, callback);
	}

	// 上传图片或头像
	public static void uploadImage(String usernameString, String emailString,
			String urlpath, String gender) {

		AVFile imageFile = null;
		try {
			imageFile = AVFile.withAbsoluteLocalPath(usernameString
					+ "_avater.jpg", urlpath);
			try {
				imageFile.save();
			} catch (AVException e) {
				e.printStackTrace();
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		AVObject po = new AVObject("Gender");
		po.put("username", usernameString);
		po.put("email", emailString);
		po.put("avater", imageFile);
		po.put("gender", gender);
		po.saveInBackground();
		// ByteArrayOutputStream stream = new ByteArrayOutputStream();
		// genderPhoto.compress(Bitmap.CompressFormat.JPEG, 100, stream);
		// byte[] data = stream.toByteArray();
		// AVFile imageFile = new AVFile("gender", data);
		// try {
		// imageFile.save();
		// } catch (AVException e) {
		// e.printStackTrace();
		// }
		// Associate image with AVOS Cloud object
	}

	public static void myLocation(String userEmail, String username,
			String gender, double latitude, double longititude, String location) {

		AVGeoPoint point = new AVGeoPoint(latitude, longititude);
		AVObject myPlace = new AVObject("MyLocation");
		myPlace.put("locationPoint", point);
		myPlace.put("userEmail", userEmail);
		myPlace.put("username", username);
		myPlace.put("gender", gender);
		myPlace.put("location", location);
		myPlace.saveInBackground();

		// AVObject myLocation = new AVObject("MyLocation");
		// myLocation.put("userObjectId", userObjectId);
		// myLocation.put("username", username);
		// myLocation.put("gender", gender);
		// myLocation.put("latitude", latitude);
		// myLocation.put("longtitude", longititude);
		// myLocation.saveInBackground();
	}

	// 用户基本资料
	public static void alterUserInformation(String username, String email,
			String gender, String birth, String hometown, String inlove,
			String constellation, String personalStatement,
			SaveCallback saveCallback) {
		AVObject userInformation = new AVObject("UserInformation");
		userInformation.put("username", username);
		userInformation.put("email", email);
		userInformation.put("gender", gender);
		userInformation.put("birth", birth);
		userInformation.put("hometown", hometown);
		userInformation.put("inlove", inlove);
		userInformation.put("constellation", constellation);
		userInformation.put("personalStatement", personalStatement);
		userInformation.saveInBackground(saveCallback);
	}

	// APP每天签到
	public static void dailySign(String username, int signTimes,
			String signDate, String signPosition, SaveCallback saveCallback) {
		AVObject userInformation = new AVObject("Sign");
		userInformation.put("username", username);
		userInformation.put("signTimes", signTimes);
		userInformation.put("signTime", signDate);
		userInformation.put("signPosition", signPosition);
		userInformation.saveInBackground(saveCallback);
	}

	// 退出登录
	public static void logout() {
		AVUser.logOut();
	}
}
