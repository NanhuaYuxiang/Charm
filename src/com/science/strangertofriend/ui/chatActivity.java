package com.science.strangertofriend.ui;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVFile;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.FindCallback;
import com.avos.avoscloud.GetDataCallback;
import com.avos.avoscloud.im.v2.AVIMClient;
import com.avos.avoscloud.im.v2.AVIMConversation;
import com.avos.avoscloud.im.v2.AVIMConversationQuery;
import com.avos.avoscloud.im.v2.AVIMMessage;
import com.avos.avoscloud.im.v2.callback.AVIMClientCallback;
import com.avos.avoscloud.im.v2.callback.AVIMConversationCallback;
import com.avos.avoscloud.im.v2.callback.AVIMConversationCreatedCallback;
import com.avos.avoscloud.im.v2.callback.AVIMConversationQueryCallback;
import com.avos.avoscloud.im.v2.callback.AVIMMessagesQueryCallback;
import com.avos.avoscloud.im.v2.messages.AVIMTextMessage;
import com.science.strangertofriend.R;
import com.science.strangertofriend.adapter.ChatAdapter;
import com.science.strangertofriend.bean.ChatMessage;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

public class ChatActivity extends Activity implements OnClickListener {

	private static ChatActivity instance;
	private Intent intent;
	private String currentClientName = "";
	private String otherClientName = "";
	private ImageView chatBackImg;
	private EditText chatEt;
	private SwipeRefreshLayout chatFreshLayout;
	private ListView chatListView;
	private Button chatImgBtn;
	private AVIMConversation connecation;
	private List<ChatMessage> messageList = new ArrayList<ChatMessage>();
	private ChatMessage chatMessage = new ChatMessage();
	private ChatAdapter chatAdapter;
	private Bitmap currentClientBitmap, otherClientBitmap;
	private AVIMClient currentClient, otherClient;
	private AVIMTextMessage message;
	private String sendMessage;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_chat);
		instance = this;
		init();
		// chatMessageAddOtherClientBitmap(otherClientName);

	}

	public void init() {
		intent = getIntent();
		AVUser user = AVUser.getCurrentUser();
		AVFile file = (AVFile) user.get("userAvater");

		currentClientName = user.getString("username");
		otherClientName = intent.getStringExtra("taskPubliName");
		chatBackImg = (ImageView) findViewById(R.id.chatback_img);
		chatEt = (EditText) findViewById(R.id.chatEt);
		// chatEt.setEnabled(false);
		chatFreshLayout = (SwipeRefreshLayout) findViewById(R.id.chatFreshLayout);
		chatFreshLayout.setEnabled(false);
		chatListView = (ListView) findViewById(R.id.chatListView);
		chatImgBtn = (Button) findViewById(R.id.chatBtn);

		chatBackImg.setOnClickListener(this);
		chatImgBtn.setOnClickListener(this);

		currentClient = AVIMClient.getInstance(currentClientName);
		otherClient = AVIMClient.getInstance(otherClientName);
		// chatMessageAddCurrentClientBitmap(file);
		getConversation(otherClientName);

	}

	public void sendMessageToClient() {
		
		currentClient.open(new AVIMClientCallback() {
			
			@Override
			public void done(AVIMClient client, AVException e) {
				// TODO Auto-generated method stub
				if(e==null){
					sendMessage = chatEt.getText().toString().trim();
					message = new AVIMTextMessage();
					message.setText(sendMessage);
					if (!TextUtils.isEmpty(sendMessage)) {
						Toast.makeText(ChatActivity.this, sendMessage, 1).show();
						connecation.sendMessage(message, new AVIMConversationCallback() {

							@Override
							public void done(AVException e) {
								// TODO Auto-generated method stub
								if (e == null) {
									Toast.makeText(ChatActivity.this, "发送成功",
											Toast.LENGTH_SHORT).show();
									chatEt.setText("");
									messageList.add(new ChatMessage(ChatMessage.MESSAGE_TO,
											sendMessage));
									chatAdapter.notifyDataSetChanged();
								}
							}
						});
					} else {
						Toast.makeText(ChatActivity.this, "消息不能为空", Toast.LENGTH_SHORT)
								.show();
					}
				}
			}
		});
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.chatback_img:
			finish();
			break;
		case R.id.chatBtn:
			sendMessageToClient();
			break;
		}

	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		chatAdapter = new ChatAdapter(this, messageList);
		chatAdapter.notifyDataSetChanged();
	}

	public static ChatActivity getCurrentActivity() {
		return instance;
	}

	public ChatAdapter getCurrentAdapter() {
		return chatAdapter;
	}

	public List<ChatMessage> getMessageList() {
		return messageList;
	}

	public void getConversation(final String otherClientName) {
		AVIMConversationQuery conversationQuery = currentClient.getQuery();
		conversationQuery.withMembers(Arrays.asList(otherClientName), true);
		conversationQuery.whereEqualTo("conversationType", 1);
		conversationQuery.findInBackground(new AVIMConversationQueryCallback() {

			@Override
			public void done(List<AVIMConversation> list, AVException e) {
				// TODO Auto-generated method stub
				if (e == null) {
					if (list.size() > 0 && (null != list)) {
						connecation = list.get(0);
						Toast.makeText(ChatActivity.this, "获取到原来的Conver", 1)
								.show();
						chatFreshLayout.setEnabled(true);
						fetchMessages();
						chatAdapter = new ChatAdapter(ChatActivity.this,
								messageList);
						chatListView.setAdapter(chatAdapter);
						chatAdapter.notifyDataSetChanged();
					} else {
						HashMap<String, Object> attributes = new HashMap<String, Object>();
						attributes.put("conversationType", 1);
						currentClient.createConversation(
								Arrays.asList(otherClientName), attributes,
								new AVIMConversationCreatedCallback() {

									@Override
									public void done(
											AVIMConversation conversation,
											AVException e) {
										// TODO Auto-generated method stub
										if (e == null) {
											Toast.makeText(ChatActivity.this,
													"得到Conver", 1).show();
											connecation = conversation;
											chatFreshLayout.setEnabled(true);
											fetchMessages();
											chatAdapter = new ChatAdapter(
													ChatActivity.this,
													messageList);
											chatListView
													.setAdapter(chatAdapter);
											chatAdapter.notifyDataSetChanged();
										}
									}
								});
					}
				} else {
					Toast.makeText(ChatActivity.this, "获取conver出现异常", 1).show();
				}
			}
		});
	}

	public void fetchMessages() {
		connecation.queryMessages(new AVIMMessagesQueryCallback() {

			@Override
			public void done(List<AVIMMessage> list, AVException e) {
				// TODO Auto-generated method stub
				if (e == null) {
					for (int i = 0; i < list.size(); i++) {
						if (list.get(i).getFrom().equals(otherClientName)) {
							messageList.add(new ChatMessage(
									ChatMessage.MESSAGE_FROM,
									((AVIMTextMessage) list.get(i)).getText()));
							chatAdapter.notifyDataSetChanged();
						} else {
							messageList.add(new ChatMessage(
									ChatMessage.MESSAGE_TO,
									((AVIMTextMessage) list.get(i)).getText()));
							chatAdapter.notifyDataSetChanged();
						}
					}
				}
			}
		});
	}

	public void chatMessageAddCurrentClientBitmap(AVFile file) {
		if (file.isDirty()) {
			file.getDataInBackground(new GetDataCallback() {

				@Override
				public void done(byte[] data, AVException e) {
					// TODO Auto-generated method stub
					if (e == null) {
						currentClientBitmap = BitmapFactory.decodeByteArray(
								data, 0, data.length);
						// chatMessage.setCurrentClientBitmap(currentClientBitmap);
					}
				}
			});
		}
	}

	public void chatMessageAddOtherClientBitmap(String otherClientName) {
		AVQuery<AVObject> userQuery = new AVQuery<AVObject>("User");
		userQuery.whereEqualTo("username", otherClientName);
		userQuery.findInBackground(new FindCallback<AVObject>() {

			@Override
			public void done(List<AVObject> list, AVException e) {
				// TODO Auto-generated method stub
				if (e != null) {
					AVUser user = (AVUser) list.get(0);
					AVFile file = (AVFile) user.get("userAvater");
					if (file.isDirty()) {

						file.getDataInBackground(new GetDataCallback() {

							@Override
							public void done(byte[] data, AVException e) {
								// TODO Auto-generated method stub
								if (e != null) {
									otherClientBitmap = BitmapFactory
											.decodeByteArray(data, 0,
													data.length);
									// chatMessage
									// .setOtherClientBitmap(otherClientBitmap);
								}
							}
						});
					}

				}
			}
		});
	}
}
