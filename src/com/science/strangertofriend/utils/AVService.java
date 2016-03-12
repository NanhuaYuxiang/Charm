package com.science.strangertofriend.utils;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

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
import com.science.strangertofriend.callback.onAvaterUrlGet;

/**
 * @description avos�Ʒ������
 * 
 */

public class AVService {
	private static onAvaterUrlGet listener;

	// ע��
	public static void signUp(String username, String password, String email,
			String gender, String installationId, SignUpCallback signUpCallback) {

		AVUser user = new AVUser();
		user.setUsername(username);
		user.setPassword(password);
		user.setEmail(email);
		user.put("gender", gender);
		user.put("installationId", installationId);
		user.put("totalGolds", 100);//ÿ����ע��ʱ����100���
		user.put("credit", 100);
		user.signUpInBackground(signUpCallback);
	}

	/**
	 * ��Ϣ�б�
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

	// ������Ϣ�б�
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

	// ɾ����Ϣ�б�
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
	 * ����ͨѶ¼�б�
	 * 
	 * @param friend
	 *            ��������
	 * @param currentUser
	 *            ��ǰ�û�
	 * @param avaterUrl
	 *            ����ͷ��url
	 * @param email
	 *            ����email
	 * @param gender
	 *            �����Ա�
	 * @param sendTime
	 *            ����ʱ��
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

	// ɾ������
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

	// �ϴ�ͼƬ��ͷ��
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

	}

	public static void getAvaterUrl(AVUser user) {

		AVQuery<AVObject> query = new AVQuery<>("Gender");
		query.whereEqualTo("email", user.getEmail());
		query.findInBackground(new FindCallback<AVObject>() {

			@Override
			public void done(List<AVObject> arg0, AVException arg1) {
				if (arg1 == null && arg0.size() > 0) {
					String url = arg0.get(arg0.size() - 1).getAVFile("avater")
							.getUrl();
					listener.avaterUrlGet(url);
				}
			}
		});

	}

	/**
	 * �жϵ�ǰ�û����û������Ƿ���userAvaterͷ����Ϣ
	 * 
	 * @param user
	 * @return
	 */
	public static boolean isUserContainsAvater(AVUser user) {
		Object avater = user.get("userAvater");
		if (null != avater) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * �ϴ�ͷ��_user��
	 * 
	 * @param user
	 *            �ϴ�ͷ����û�
	 * @param avaterUrl
	 *            ͷ��url
	 * @param username
	 *            ��ǰuser��username
	 */
	public static void upLoadAvater(AVUser user, String avaterUrl,
			String username, SignUpCallback callback) {
		AVFile avater = null;
		avater = new AVFile(username + "_avater.jpg", avaterUrl, null);
		if (null != user && null != avater) {
			user.put("userAvater", avater);
			// user.put("test", 1);
			user.signUpInBackground(callback);
		}
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

	// �û���������
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
	 * @param user
	 * 				���񷢲���
	 * @param publisherName
	 *            ��������������
	 * @param acceptedName
	 *            ��������˵�����
	 * @param endTime
	 *            �����ֹʱ��
	 * @param geoPoint
	 *            ���񷢲��ص�
	 * @param price
	 *            ���������
	 * @param des
	 *            ��������
	 * @param theme
	 *            ��������
	 * @param des
	 *            ��������
	 * @param service_type
	 *            ��������
	 * @param location
	 *            ����ص�
	 * @param isAccepted
	 *            �Ƿ��ѱ�����
	 * @param isAccomplished
	 *            �Ƿ��ѱ����
	 */
	public static void addNewTask(AVUser user, String publisherName, String acceptedName,
			String theme, String des, String endTime, AVGeoPoint geoPoint,
			String location, String price, String service_type,
			boolean isAccepted, boolean isAccomplished,
			SaveCallback saveCallback) {
		AVObject task = new AVObject("Task");
		task.put("pub_user", user);
		task.put("publisherName", publisherName);
		task.put("acceptedName", acceptedName);
		task.put("theme", theme);
		task.put("TaskDescription", des);
		task.put("endTime", endTime);
		task.put("geoPoint", geoPoint);
		task.put("location", location);
		task.put("price", price);
		task.put("service_type", service_type);
		task.put("isAccepted", isAccepted);
		task.put("isAccomplished", isAccomplished);
		task.saveInBackground(saveCallback);
	}

	// APPÿ��ǩ��
	public static void dailySign(String username, int signTimes,
			String signDate, String signPosition, SaveCallback saveCallback) {
		AVObject userInformation = new AVObject("Sign");
		userInformation.put("username", username);
		userInformation.put("signTimes", signTimes);
		userInformation.put("signTime", signDate);
		userInformation.put("signPosition", signPosition);
		userInformation.saveInBackground(saveCallback);
	}

	// �˳���¼
	public static void logout() {
		AVUser.logOut();
	}

	static String avaterURL;
	static BitmapDescriptor bitmapDescriptor;

	/**
	 * 
	 * @param publisherName
	 *            ����������
	 */
	public static void getNearbyTaskAvaters(final String publisherName) {
		AVQuery<AVObject> query = new AVQuery<AVObject>("Gender");

		query.whereEqualTo("username", publisherName);
		query.findInBackground(new FindCallback<AVObject>() {

			@Override
			public void done(List<AVObject> arg0, AVException arg1) {
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
					// Log.e("avaterURL", "load����");
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
				bitmap = loadToBitmap();
				break;
			case 3:
				break;
			default:
				break;
			}
		};
	};

	/**
	 * 
	 * @return ��BitmapDescriptor�����������ͷ����Ϣ
	 */
	public static BitmapDescriptor load() {
		DisplayImageOptions options = new DisplayImageOptions.Builder()
				.showImageForEmptyUri(R.drawable.default_load)// ����ͼƬUriΪ�ջ��Ǵ����ʱ����ʾ��ͼƬ
				.showImageOnFail(R.drawable.default_load)// ����ͼƬ���ػ��������з���������ʾ��ͼƬ
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
	 * @return ��bitmap �����������ͷ����Ϣ
	 */
	public static Bitmap loadToBitmap() {
		DisplayImageOptions options = new DisplayImageOptions.Builder()
				.showImageForEmptyUri(R.drawable.default_load)// ����ͼƬUriΪ�ջ��Ǵ����ʱ����ʾ��ͼƬ
				.showImageOnFail(R.drawable.default_load)// ����ͼƬ���ػ��������з���������ʾ��ͼƬ
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
						bitmap = arg2;

					}

					@Override
					public void onLoadingCancelled(String arg0, View arg1) {

					}
				});

		return bitmap;
	}

	public static Bitmap getBitmap() {
		return bitmap;
	}

	public static BitmapDescriptor getBitmapDescriper() {
		Log.e("bitmapDescriptor", bitmapDescriptor + "");
		return bitmapDescriptor;

	}

	public static void setOnAvaterUrlListener(onAvaterUrlGet listener) {
		AVService.listener = listener;
	}

}
