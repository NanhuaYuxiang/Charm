package com.science.strangertofriend.ui;

import java.util.List;
import java.util.Random;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.widget.ListView;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.im.v2.AVIMClient;
import com.avos.avoscloud.im.v2.AVIMConversation;
import com.avos.avoscloud.im.v2.AVIMTypedMessage;
import com.avos.avoscloud.im.v2.AVIMTypedMessageHandler;
import com.avos.avoscloud.im.v2.callback.AVIMClientCallback;
import com.avos.avoscloud.im.v2.messages.AVIMTextMessage;
import com.avos.avospush.notification.NotificationCompat;
import com.science.strangertofriend.R;
import com.science.strangertofriend.adapter.ChatAdapter;
import com.science.strangertofriend.bean.ChatMessage;

public class ReceiveMessageHandler extends
		AVIMTypedMessageHandler<AVIMTypedMessage> {

	private ChatActivity chatActivity;
	private List<ChatMessage> messageList;
	private ChatAdapter chatAdapter;
	private Context context;
	private AVIMTypedMessage receiveMessage;
	private ListView chatListView;

	public ReceiveMessageHandler(Context context) {
		this.context = context;
	}

	@Override
	public void onMessage(AVIMTypedMessage message,
			AVIMConversation conversation, AVIMClient client) {
		// TODO Auto-generated method stub
		super.onMessage(message, conversation, client);
		chatActivity = ChatActivity.getCurrentActivity();
		messageList = chatActivity.getMessageList();
		chatListView = chatActivity.getListView();
		chatAdapter = (ChatAdapter) chatListView.getAdapter();
		receiveMessage = message;
		AVUser user = AVUser.getCurrentUser();
		if (!(user.equals(null))) {
			AVIMClient currentClient = AVIMClient.getInstance(user
					.getString("username"));
			currentClient.open(new AVIMClientCallback() {

				@Override
				public void done(AVIMClient arg0, AVException e) {
					// TODO Auto-generated method stub
					if (e == null) {
						messageList.add(new ChatMessage(
								ChatMessage.MESSAGE_FROM,
								((AVIMTextMessage) receiveMessage).getText()));
						chatAdapter.reFresh(messageList);
						// chatListView.smoothScrollToPosition(messageList.size()-1);
						chatActivity.scrollToBottom();

					}
				}
			});
		}

		// messageList.add(new
		// ChatMessage(ChatMessage.MESSAGE_FROM,((AVIMTextMessage)
		// message).getText()));
		// thread.start();
		sendNotification(message, conversation);
	}

	public void sendNotification(AVIMTypedMessage message,
			AVIMConversation conversation) {
		String notificationContent = message instanceof AVIMTextMessage ? ((AVIMTextMessage) message)
				.getText() : "暂不支持此消息类型";
		String sound = null;
		Intent intent = new Intent(context, NotificationBroadcastReceiver.class);
		intent.putExtra("conversation", conversation.getConversationId());
		intent.putExtra("otherClientId", message.getFrom());
		intent.setFlags(0);
		int notificationId = (new Random()).nextInt();
		PendingIntent contentIntent = PendingIntent.getBroadcast(context,
				notificationId, intent, 0);
		NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(
				context)
				.setSmallIcon(R.drawable.notification_icon)
				.setContentTitle("")
				.setAutoCancel(true)
				.setContentIntent(contentIntent)
				.setDefaults(
						Notification.DEFAULT_VIBRATE
								| Notification.DEFAULT_SOUND)
				.setContentText(notificationContent);
		NotificationManager manager = (NotificationManager) context
				.getSystemService(Context.NOTIFICATION_SERVICE);
		Notification notification = mBuilder.build();
		if (sound != null && sound.trim().length() > 0) {
			notification.sound = Uri
					.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + "://"
							+ sound);
		}
		manager.notify(notificationId, notification);
	}

	Thread thread = new Thread(new Runnable() {

		@Override
		public void run() {
			// TODO Auto-generated method stub
			chatAdapter.notifyDataSetChanged();
		}
	});
}
