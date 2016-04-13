package com.science.strangertofriend.ui;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.pedant.SweetAlert.SweetAlertDialog;
import cn.pedant.SweetAlert.SweetAlertDialog.OnSweetClickListener;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVFile;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.FindCallback;
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
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.science.strangertofriend.R;
import com.science.strangertofriend.adapter.ChatAdapter;
import com.science.strangertofriend.bean.ChatMessage;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
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
	private ImageButton chatImgBtn;
	private AVIMConversation connecation;
	private List<ChatMessage> messageList = new ArrayList<ChatMessage>();
	private ChatMessage chatMessage = new ChatMessage();
	private ChatAdapter chatAdapter;
	private AVIMClient currentClient;
	private AVIMTextMessage message;
	private String sendMessage;
	private TextView convsClientNameTv;
	private List<AVIMMessage> messagePageList = new ArrayList<AVIMMessage>();
	private boolean messageIsNull = false;
	private ImageButton chatAddBtn;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
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
		downloadAvaterBitmaps(currentClientName, file.getUrl());
		currentClient = AVIMClient.getInstance(currentClientName);

		otherClientName = intent.getStringExtra("taskPubliName");
		chatMessageAddOtherClientBitmap(otherClientName);
		convsClientNameTv = (TextView) findViewById(R.id.convsClientNameTv);
		convsClientNameTv.setText(otherClientName);

		chatBackImg = (ImageView) findViewById(R.id.chatback_img);
		chatAddBtn = (ImageButton) findViewById(R.id.chatAdd_img);
		chatEt = (EditText) findViewById(R.id.chatEt);

		chatFreshLayout = (SwipeRefreshLayout) findViewById(R.id.chatFreshLayout);
		chatFreshLayout.setColorSchemeResources(
				android.R.color.holo_blue_light,
				android.R.color.holo_red_light,
				android.R.color.holo_orange_light,
				android.R.color.holo_green_light);
		chatFreshLayout.setOnRefreshListener(new OnRefreshListener() {

			@Override
			public void onRefresh() {
				// AVIMMessage message = chatAdapter.getFirstMssage();
				AVIMMessage oldMessage = messagePageList.get(messagePageList
						.size() - 1);
				searchPageSize(oldMessage);

			}
		});
		chatListView = (ListView) findViewById(R.id.chatListView);
		chatImgBtn = (ImageButton) findViewById(R.id.chatBtn);

		chatAdapter = new ChatAdapter(this, messageList);
		chatAdapter.addChatMessage(chatMessage);
		chatListView.setAdapter(chatAdapter);

		chatBackImg.setOnClickListener(this);
		chatImgBtn.setOnClickListener(this);
		chatAddBtn.setOnClickListener(this);

		currentClient.open(new AVIMClientCallback() {

			@Override
			public void done(AVIMClient arg0, AVException e) {
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
				if (e == null) {
					sendMessage = chatEt.getText().toString().trim();
					message = new AVIMTextMessage();
					message.setText(sendMessage);
					if (!TextUtils.isEmpty(sendMessage)) {
						connecation.sendMessage(message,
								new AVIMConversationCallback() {

									@Override
									public void done(AVException e) {
										if (e == null) {
											chatEt.setText("");
											messageList.add(new ChatMessage(
													ChatMessage.MESSAGE_TO,
													sendMessage));
											chatAdapter.reFresh(messageList);
											scrollToBottom();
										} else {
											Toast.makeText(ChatActivity.this,
													"发送消息失败",
													Toast.LENGTH_SHORT).show();
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
		switch (v.getId()) {
		case R.id.chatback_img:
			finish();
			break;
		case R.id.chatBtn:
			if (isFastDoubleClick()) {
				sendMessageToClient();
				scrollToBottom();
				break;
			} else {
				Toast.makeText(this, "消息发送不能相隔一秒", Toast.LENGTH_SHORT).show();
				break;
			}
		case R.id.chatAdd_img:
			SweetAlertDialog addDialog = new SweetAlertDialog(this,
					SweetAlertDialog.WARNING_TYPE);

			addDialog.setTitleText("").setContentText("是否添加对方为好友？");
			addDialog.setCancelText("取消");
			addDialog.setConfirmText("确定");
			addDialog.showCancelButton(true);
			addDialog.setCancelClickListener(new OnSweetClickListener() {

				@Override
				public void onClick(SweetAlertDialog sweetAlertDialog) {
					// TODO Auto-generated method stub
					sweetAlertDialog.dismiss();
				}
			}).setConfirmClickListener(new OnSweetClickListener() {

				@Override
				public void onClick(final SweetAlertDialog sweetAlertDialog) {
					// TODO Auto-generated method stub
					Boolean friendState = (Boolean) connecation.getAttribute("friendstate");
					if(friendState){
						sweetAlertDialog.setTitleText("")
						.setContentText("已经是你的好友了")
						.setConfirmClickListener(null)
						.changeAlertType(SweetAlertDialog.SUCCESS_TYPE);
					}else{
						connecation.setAttribute("conversationType", 1);
						connecation.setAttribute("friendstate", false);
						connecation.setAttribute("friendsender", currentClientName);
						connecation.setAttribute("friendrequester", otherClientName);
						connecation.updateInfoInBackground(new AVIMConversationCallback() {
							
							@Override
							public void done(AVException e) {
								// TODO Auto-generated method stub
								if(e==null){
									sweetAlertDialog.setTitleText("")
									.setContentText("已经发送请求给对方")
									.setConfirmClickListener(null)
									.changeAlertType(SweetAlertDialog.SUCCESS_TYPE);
								}
							}
						});
					}

				}
			}).show();

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

	// 查询当前对话
	public void getConversation(final String otherClientName) {
		AVIMConversationQuery conversationQuery = currentClient.getQuery();
		conversationQuery.withMembers(Arrays.asList(otherClientName), true);
		conversationQuery.whereEqualTo("conversationType", 1);
		conversationQuery.findInBackground(new AVIMConversationQueryCallback() {

			@Override
			public void done(List<AVIMConversation> list, AVException e) {
				if (e == null) {
					if (list.size() > 0 && (null != list)) {
						connecation = list.get(0);
						chatFreshLayout.setEnabled(true);
						fetchMessages(connecation);
					} else {
						HashMap<String, Object> attributes = new HashMap<String, Object>();
						attributes.put("conversationType", 1);
						attributes.put("friendsender", "");
						attributes.put("friendrequester", "");
						attributes.put("friendstate", false);
						currentClient.createConversation(
								Arrays.asList(otherClientName), attributes,
								new AVIMConversationCreatedCallback() {

									@Override
									public void done(
											AVIMConversation conversation,
											AVException e) {
										if (e == null) {
											connecation = conversation;
											chatFreshLayout.setEnabled(true);
										}
									}
								});
					}
					// scrollToBottom();
				} else {
				}
			}
		});
	}

	// 查询对话的消息
	public void fetchMessages(AVIMConversation connecation) {
		final int limit = 15;
		connecation.queryMessages(limit, new AVIMMessagesQueryCallback() {

			@Override
			public void done(List<AVIMMessage> list, AVException e) {
				if (e == null && list.size() > 0 && null != list) {
					if (list.size() < limit) {
						messageIsNull = true;
					}
					for (int j = 0; j < list.size(); j++) {
						if (!list.get(j).equals(null)) {
							AVIMMessage oldMessage = list.get(j);
							messagePageList.add(oldMessage);
							break;
						}
					}
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

	public void chatMessageAddOtherClientBitmap(final String otherClientName) {
		AVQuery<AVUser> userQuery = AVUser.getQuery();
		userQuery.whereEqualTo("username", otherClientName);
		userQuery.findInBackground(new FindCallback<AVUser>() {
			@Override
			public void done(List<AVUser> list, AVException e) {
				if (e == null && list != null && list.size() > 0) {
					AVUser user = (AVUser) list.get(0);
					AVFile file = (AVFile) user.get("userAvater");
					downloadAvaterBitmaps(otherClientName, file.getUrl());

				} else {
					System.out.println(e.toString());
				}
			}
		});
	}

	public void scrollToBottom() {
		chatListView.smoothScrollToPosition(messageList.size() - 1);
		((BaseAdapter) chatListView.getAdapter()).notifyDataSetChanged();
	}

	public void downloadAvaterBitmaps(final String username, final String url) {
		DisplayImageOptions option = new DisplayImageOptions.Builder()
				.showImageForEmptyUri(R.drawable.default_load)// 设置图片Uri为空或是错误的时候显示的图片
				.showImageOnFail(R.drawable.default_load)// 设置图片加载或解码过程中发生错误显示的图片
				.bitmapConfig(Bitmap.Config.RGB_565).build();
		// String
		// urlString=hash_avaterUrls.get(taskNearBy.get(i).getPublisherName());
		ImageLoader.getInstance().loadImage(url, option,
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
							Bitmap bitmap) {
						if (username.equals(AVUser.getCurrentUser()
								.getUsername())) {
							chatMessage.setCurrentClientBitmap(bitmap);
						} else {
							chatMessage.setOtherClientBitmap(bitmap);
						}
					}

					@Override
					public void onLoadingCancelled(String arg0, View arg1) {

					}
				});
	}

	private long lastClickTime;

	public boolean isFastDoubleClick() {
		long time = System.currentTimeMillis();
		if (time - lastClickTime > 1000) {
			return true;
		} else {
			lastClickTime = time;
			return false;
		}
	}

	public void searchPageSize(AVIMMessage oldMessage) {
		if (!messageIsNull) {
			connecation.queryMessages(oldMessage.getMessageId(),
					oldMessage.getTimestamp(), 20,
					new AVIMMessagesQueryCallback() {

						@Override
						public void done(List<AVIMMessage> list, AVException e) {
							// TODO Auto-generated method
							if (e == null && null != list && list.size() > 0
									&& !list.isEmpty()) {
								for (int j = 0; j < list.size(); j++) {
									if (!list.get(j).equals(null)) {
										AVIMMessage oldMessage = list.get(j);
										messagePageList.add(oldMessage);
										break;
									}
								}

								for (int i = 0; i < list.size(); i++) {
									if (list.size() < 20) {
										Toast.makeText(ChatActivity.this,
												"没有历史消息了", Toast.LENGTH_SHORT)
												.show();
										messageIsNull = true;
									}
									if (list.get(i).getFrom()
											.equals(otherClientName)) {
										messageList
												.add(0,
														new ChatMessage(
																ChatMessage.MESSAGE_FROM,
																((AVIMTextMessage) list
																		.get(i))
																		.getText()));
										break;
									} else {
										messageList.add(
												0,
												new ChatMessage(
														ChatMessage.MESSAGE_TO,
														((AVIMTextMessage) list
																.get(i))
																.getText()));
									}
								}
								chatAdapter = new ChatAdapter(
										ChatActivity.this, messageList);
								chatAdapter.reFresh(messageList);
								chatAdapter = (ChatAdapter) chatListView
										.getAdapter();
								// if (list.size() < 20) {
								// chatListView.smoothScrollByOffset(list
								// .size() - 1);
								//
								// } else {
								// chatListView.smoothScrollByOffset(19);
								// }
								chatAdapter.notifyDataSetChanged();
								chatFreshLayout.setRefreshing(false);
							} else if (null != list && list.size() > 0
									&& !list.isEmpty()) {
								Toast.makeText(ChatActivity.this, "没有历史消息了",
										Toast.LENGTH_SHORT).show();
								chatFreshLayout.setRefreshing(false);

							} else if (e != null) {
								System.out.println(e.toString());
								chatFreshLayout.setRefreshing(false);

							}
						}
					});
		} else {
			Toast.makeText(ChatActivity.this, "没有历史消息了", Toast.LENGTH_SHORT)
					.show();
			chatFreshLayout.setRefreshing(false);
		}

	}
}
