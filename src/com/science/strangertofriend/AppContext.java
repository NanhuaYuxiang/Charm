package com.science.strangertofriend;

import java.util.List;

import android.app.Application;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.widget.Toast;

import com.avos.avoscloud.AVAnalytics;
import com.avos.avoscloud.AVOSCloud;
import com.avos.avoscloud.PushService;
import com.avos.avoscloud.im.v2.AVIMConversation;
import com.avos.avoscloud.im.v2.AVIMMessageManager;
import com.avos.avoscloud.im.v2.AVIMTypedMessage;
import com.avoscloud.leanchatlib.controller.ChatManager;
import com.avoscloud.leanchatlib.controller.ChatManagerAdapter;
import com.avoscloud.leanchatlib.model.UserInfo;
import com.iflytek.cloud.SpeechUtility;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.science.strangertofriend.ui.ChatRoomActivity;
import com.science.strangertofriend.ui.DetailedTaskActivity;
import com.science.strangertofriend.ui.ReceiveMessageHandler;
import com.science.strangertofriend.ui.ShowNearMenMapActivity;

/**
 * @description ȫ��Ӧ�ó����ࣺ���ڱ���͵���ȫ��Ӧ�����ü�������������
 * 
 */
public class AppContext extends Application {

	public static boolean isThisLocation = true;

	@Override
	public void onCreate() {
		super.onCreate();
		//Ѷ������
		SpeechUtility.createUtility(AppContext.this, "appid=5835a709");
		
		// ImageLoaderͼƬ����
		initImageLoader(getApplicationContext());

		// U need your AVOS key and so on to run the code.
		AVOSCloud.initialize(getApplicationContext(),
				"u30ryjydwuqfa8edh8oo6af2kf78cgh3uatpa3bj5s837rla",
				"u44at27q33q07n14i0xdp7clqgq0d5x9a8o4trssw3t7gija");
		// ���ñ�������ͳ��
		AVAnalytics.enableCrashReport(this.getApplicationContext(), true);
		AVOSCloud.setDebugLogEnabled(true);
		ChatManager.setDebugEnabled(true);// tag leanchatlib

		AVIMMessageManager.registerMessageHandler(AVIMTypedMessage.class,
				new ReceiveMessageHandler(this));
		final ChatManager chatManager = ChatManager.getInstance();
		chatManager.init(this);
		chatManager.setChatManagerAdapter(new ChatManagerAdapter() {
			@Override
			public UserInfo getUserInfoById(String userId) {
				UserInfo userInfo = new UserInfo();
				userInfo.setUsername(userId);
				userInfo.setAvatarUrl("http://ac-x3o016bx.clouddn.com/86O7RAPx2BtTW5zgZTPGNwH9RZD5vNDtPm1YbIcu");
				return userInfo;
			}

			@Override
			public void cacheUserInfoByIdsInBackground(List<String> userIds)
					throws Exception {
			}

			// �������������� leanchat Ӧ���е� ChatManagerAdapterImpl.java
			@Override
			public void shouldShowNotification(Context context, String selfId,
					AVIMConversation conversation, AVIMTypedMessage message) {
				Toast.makeText(context, "���յ�һ����Ϣ������գ�", Toast.LENGTH_LONG)
						.show();
			}
		});

		/**
		 * ��Ϣ͸��
		 */
		// ����Ĭ�ϴ򿪵� Activity
		PushService.setDefaultPushCallback(getApplicationContext(),
				ShowNearMenMapActivity.class);
		// ����Ƶ��������Ƶ����Ϣ������ʱ�򣬴򿪶�Ӧ�� Activity
		PushService.subscribe(getApplicationContext(), "public",
				MainActivity.class);
		PushService.subscribe(getApplicationContext(), "private",
				ChatRoomActivity.class);

	}

	/**
	 * ��������Ƿ����
	 * 
	 * @return
	 */
	public boolean isNetworkConnected() {
		ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo ni = cm.getActiveNetworkInfo();
		return ni != null && ni.isConnectedOrConnecting();
	}

	/** ��ʼ��ͼƬ������������Ϣ **/
	@SuppressWarnings("deprecation")
	public static void initImageLoader(Context context) {

		// This configuration tuning is custom. You can tune every option, you
		// may tune some of them,
		// or you can create default configuration by
		// ImageLoaderConfiguration.createDefault(this);
		// method.
		ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(
				context).threadPriority(Thread.NORM_PRIORITY - 2)// ����ͼƬ���߳���
				.denyCacheImageMultipleSizesInMemory() // ����ͼ��Ĵ�ߴ罫���ڴ��л�����ǰ����ͼ���С�ߴ硣
				.diskCacheFileNameGenerator(new Md5FileNameGenerator())// ���ô��̻����ļ�����
				.tasksProcessingOrder(QueueProcessingType.LIFO)// ���ü�����ʾͼƬ���н���
				.writeDebugLogs() // Remove for release app
				.build();
		// Initialize ImageLoader with configuration.
		ImageLoader.getInstance().init(config);
	}

}
