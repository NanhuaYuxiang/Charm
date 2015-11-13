package com.science.strangertofriend.utils;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;

import android.R.bool;
import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVFile;
import com.avos.avoscloud.AVGeoPoint;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.FindCallback;
import com.avos.avoscloud.RequestPasswordResetCallback;
import com.avos.avoscloud.SaveCallback;
import com.avos.avoscloud.SignUpCallback;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.science.strangertofriend.R;
import com.science.strangertofriend.ui.LoginActivity;

/**
 * @description avos云服务操作
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

	/**
	 * 消息列表
	 * 
	 * @param friend
	 * @param urlAvater
	 * @param currentUser
	 * @param sendTime
	 * @param messsage
	 */
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

	/**
	 * 好友通讯录列表
	 * 
	 * @param friend
	 *            好友姓名
	 * @param currentUser
	 *            当前用户
	 * @param avaterUrl
	 *            好友头像url
	 * @param email
	 *            好友email
	 * @param gender
	 *            好友性别
	 * @param sendTime
	 *            发送时间
	 */
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

	/**
	 * 
	 * @param publisherName
	 *            发布任务人姓名
	 * @param endTime
	 *            任务截止时间
	 * @param geoPoint
	 *            任务发布地点
	 * @param price
	 *            任务香金数
	 * @param des
	 *            任务描述
	 * @param theme
	 *            任务主题
	 * @param des
	 *            任务描述
	 * @param service_type
	 *            任务类型
	 * @param location
	 *            任务地点
	 */
	public static void addNewTask(String publisherName, String theme,
			String des, String endTime, AVGeoPoint geoPoint, String location,
			String price, String service_type, SaveCallback saveCallback) {
		AVObject task = new AVObject("Task");
		task.put("publisherName", publisherName);
		task.put("theme", theme);
		task.put("TaskDescription", des);
		task.put("endTime", endTime);
		task.put("geoPoint", geoPoint);
		task.put("location", location);
		task.put("price", price);
		task.put("service_type", service_type);
		task.saveInBackground(saveCallback);
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

	static String avaterURL;
	static BitmapDescriptor bitmapDescriptor;

	/**
	 * 
	 * @param publisherName
	 *            发布人姓名
	 */
	public static void getNearbyTaskAvaters(final String publisherName) {
		AVQuery<AVObject> query = new AVQuery<AVObject>("Gender");

		query.whereEqualTo("username", publisherName);
		query.findInBackground(new FindCallback<AVObject>() {

			@Override
			public void done(List<AVObject> arg0, AVException arg1) {
				Log.e("avaterURL", "共有" + arg0.size() + "个任务");
				if (arg0 != null && arg0.size() != 0) {
					AVObject publisher = arg0.get(arg0.size() - 1);
					AVFile avaterFile = publisher.getAVFile("avater");
					avaterURL = avaterFile.getUrl();
					Message msg = Message.obtain();
					Log.e("avaterURL", avaterURL + "");
					msg.obj = avaterURL;
					msg.what = 2;
					handler.sendMessage(msg);
					// bitmapDescriptor=load();
					// Log.e("avaterURL", "load完了");
				}
			}
		});

	}

	private static Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case 1:
				bitmapDescriptor = load();
				break;
			case 2:
				bitmap= loadToBitmap();
				break;
			default:
				break;
			}
		};
	};

	/**
	 * 
	 * @return 以BitmapDescriptor来返还请求的头像信息
	 */
	public static BitmapDescriptor load() {
		DisplayImageOptions options = new DisplayImageOptions.Builder()
				.showImageForEmptyUri(R.drawable.default_load)// 设置图片Uri为空或是错误的时候显示的图片
				.showImageOnFail(R.drawable.default_load)// 设置图片加载或解码过程中发生错误显示的图片
				.bitmapConfig(Bitmap.Config.RGB_565).build();
		ImageLoader.getInstance().loadImage(avaterURL, options,
				new ImageLoadingListener() {

					@Override
					public void onLoadingStarted(String arg0, View arg1) {

					}

					@Override
					public void onLoadingFailed(String arg0, View arg1,
							FailReason arg2) {

					}

					@Override
					public void onLoadingComplete(String arg0, View arg1,
							Bitmap arg2) {

						Log.e("arg2", arg2 + "");
						BitmapDescriptor bitmapDescriptor = BitmapDescriptorFactory
								.fromBitmap(arg2);
						AVService.bitmapDescriptor = bitmapDescriptor;
						Log.e("AVService.bitmapDescriptor",
								AVService.bitmapDescriptor + "");
					}

					@Override
					public void onLoadingCancelled(String arg0, View arg1) {

					}
				});

		return bitmapDescriptor;
	}

	private static Bitmap bitmap;
	/**
	 * 
	 * @return 以bitmap 来返回请求的头像信息
	 */
	public static Bitmap loadToBitmap() {
		DisplayImageOptions options = new DisplayImageOptions.Builder()
				.showImageForEmptyUri(R.drawable.default_load)// 设置图片Uri为空或是错误的时候显示的图片
				.showImageOnFail(R.drawable.default_load)// 设置图片加载或解码过程中发生错误显示的图片
				.bitmapConfig(Bitmap.Config.RGB_565).build();
		ImageLoader.getInstance().loadImage(avaterURL, options,
				new ImageLoadingListener() {

					@Override
					public void onLoadingStarted(String arg0, View arg1) {

					}

					@Override
					public void onLoadingFailed(String arg0, View arg1,
							FailReason arg2) {

					}

					@Override
					public void onLoadingComplete(String arg0, View arg1,
							Bitmap arg2) {
						bitmap=arg2;
						
					}

					@Override
					public void onLoadingCancelled(String arg0, View arg1) {

					}
				});

		return bitmap;
	}
	public static Bitmap getBitmap(){
		return bitmap;
	}
	public static BitmapDescriptor getBitmapDescriper() {
		Log.e("bitmapDescriptor", bitmapDescriptor + "");
		return bitmapDescriptor;

	}
}
