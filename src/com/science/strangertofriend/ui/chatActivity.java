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
import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener;
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

	}

	public void init() {
		intent = getIntent();
		AVUser user = AVUser.getCurrentUser();
		AVFile file = (AVFile) user.get("userAvater");
		
		currentClientName = user.getUsername();
		currentClient = AVIMClient.getInstance(currentClientName);
		chatMessageAddCurrentClientBitmap(file);
		
		otherClientName = intent.getStringExtra("taskPubliName");
		otherClient = AVIMClient.getInstance(otherClientName);
		chatMessageAddOtherClientBitmap(otherClientName);
		
		chatBackImg = (ImageView) findViewById(R.id.chatback_img);
		chatEt = (EditText) findViewById(R.id.chatEt);
		
		chatFreshLayout = (SwipeRefreshLayout) findViewById(R.id.chatFreshLayout);
		chatFreshLayout.setEnabled(false);
		chatFreshLayout.setOnRefreshListener(new OnRefreshListener() {

			@Override
			public void onRefresh() {
				// TODO Auto-generated method stub
				AVIMMessage message = chatAdapter.getFirstMssage();
				connecation.queryMessages(message.getMessageId(),
						message.getTimestamp(), 20,
						new AVIMMessagesQueryCallback() {

							@Override
							public void done(List<AVIMMessage> list,
									AVException e) {
								// TODO Auto-generated method stub
								if (e == null) {
									if (null != list && list.size() > 0) {
										for (int i = 0; i < list.size(); i++) {
											if (list.get(i).getFrom()
													.equals(otherClientName)) {
												messageList
														.add(i,
																new ChatMessage(
																		ChatMessage.MESSAGE_FROM,
																		((AVIMTextMessage) list
																				.get(i))
																				.getText()));
											} else {
												messageList
														.add(i,
																new ChatMessage(
																		ChatMessage.MESSAGE_TO,
																		((AVIMTextMessage) list
																				.get(i))
																				.getText()));
											}
										}
									}
									chatAdapter = new ChatAdapter(
											ChatActivity.this, messageList);
									chatAdapter = (ChatAdapter) chatListView
											.getAdapter();
									chatAdapter.notifyDataSetChanged();
									chatListView.smoothScrollByOffset(list
											.size() - 1);
								}
							}
						});
			}
		});
		chatListView = (ListView) findViewById(R.id.chatListView);
		chatImgBtn = (Button) findViewById(R.id.chatBtn);

		chatAdapter = new ChatAdapter(this, messageList);
		chatListView.setAdapter(chatAdapter);

		chatBackImg.setOnClickListener(this);
		chatImgBtn.setOnClickListener(this);
		
		currentClient.open(new AVIMClientCallback() {

			@Override
			public void done(AVIMClient arg0, AVException e) {
				// TODO Auto-generated method stub
				if (e == null) {
					getConversation(otherClientName);
				}
			}
		});

	}

	public void sendMessageToClient() {

		currentClient.open(new AVIMClientCallback() {

			@Override
			public void done(AVIMClient client, AVException e) {
				// TODO Auto-generated method stub
				if (e == null) {
					sendMessage = chatEt.getText().toString().trim();
					message = new AVIMTextMessage();
					message.setText(sendMessage);
					if (!TextUtils.isEmpty(sendMessage)) {
						Toast.makeText(ChatActivity.this, sendMessage, 1)
								.show();
						connecation.sendMessage(message,
								new AVIMConversationCallback() {

									@Override
									public void done(AVException e) {
										// TODO Auto-generated method stub
										if (e == null) {
											Toast.makeText(ChatActivity.this,
													"发送成功", Toast.LENGTH_SHORT)
													.show();
											chatEt.setText("");
											messageList.add(new ChatMessage(
													ChatMessage.MESSAGE_TO,
													sendMessage));
											chatAdapter.reFresh(messageList);
											scrollToBottom();
										}
									}
								});
					} else {
						Toast.makeText(ChatActivity.this, "消息不能为空",
								Toast.LENGTH_SHORT).show();
					}
				}
			}
		});
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		if (null != chatListView && null != chatAdapter) {
			if (null != messageList && messageList.size() > 0) {
				chatAdapter.reFresh(messageList);
				scrollToBottom();
			}
		}
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		if (null != chatListView && null != chatAdapter) {
			if (null != messageList && messageList.size() > 0) {
				chatAdapter.reFresh(messageList);
				scrollToBottom();
			}
		}
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
			scrollToBottom();
			break;
		}

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

	public ListView getListView() {
		return chatListView;
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
						chatFreshLayout.setEnabled(true);
						fetchMessages();
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
											connecation = conversation;
											chatFreshLayout.setEnabled(true);
											chatListView
													.setAdapter(chatAdapter);
										}
									}
								});
					}
					scrollToBottom();
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
						} else {
							messageList.add(new ChatMessage(
									ChatMessage.MESSAGE_TO,
									((AVIMTextMessage) list.get(i)).getText()));
						}
					}
					chatAdapter.reFresh(messageList);
					scrollToBottom();
				}
			}
		});
	}

	public void chatMessageAddCurrentClientBitmap(AVFile file) {
		if (!file.equals(null)) {
			file.getDataInBackground(new GetDataCallback() {

				@Override
				public void done(byte[] data, AVException e) {
					// TODO Auto-generated method stub
					if (e == null) {
						currentClientBitmap = BitmapFactory.decodeByteArray(
								data, 0, data.length);
						chatMessage.setCurrentClientBitmap(currentClientBitmap);
						chatAdapter = (ChatAdapter) chatListView.getAdapter();
						chatAdapter.reFresh(messageList);
						Toast.makeText(ChatActivity.this, "添加图片成功123", Toast.LENGTH_SHORT).show();
					}
				}
			});
		}
	}

	public void chatMessageAddOtherClientBitmap(String otherClientName) {
		AVQuery<AVUser> userQuery = AVUser.getQuery();
		userQuery.whereEqualTo("username", otherClientName);
		userQuery.findInBackground(new FindCallback<AVUser>() {
			@Override
			public void done(List<AVUser> list, AVException e) {
				// TODO Auto-generated method stub
				if (e == null) {
					AVUser user = (AVUser) list.get(0);
					AVFile file = (AVFile) user.get("userAvater");
					if (!file.equals(null)) {

						file.getDataInBackground(new GetDataCallback() {

							@Override
							public void done(byte[] data, AVException e) {
								// TODO Auto-generated method stub
								if (e == null) {
									otherClientBitmap = BitmapFactory
											.decodeByteArray(data, 0,
													data.length);
									chatMessage
											.setOtherClientBitmap(otherClientBitmap);
									Toast.makeText(ChatActivity.this, "添加图片成功", Toast.LENGTH_SHORT).show();
								}else{
									Toast.makeText(ChatActivity.this, e.toString(), Toast.LENGTH_SHORT).show();
								}
							}
						});
					}

				}else{
					Toast.makeText(ChatActivity.this, e.toString(), Toast.LENGTH_SHORT).show();
				}
			}
		});
	}

	public void scrollToBottom() {
		chatListView.smoothScrollToPosition(messageList.size() - 1);
	}
}
