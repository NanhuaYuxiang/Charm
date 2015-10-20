package com.science.strangertofriend.fragment;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import yalantis.com.sidemenu.interfaces.ScreenShotable;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ImageView;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.FindCallback;
import com.avos.avoscloud.im.v2.AVIMClient;
import com.avos.avoscloud.im.v2.AVIMConversation;
import com.avos.avoscloud.im.v2.callback.AVIMClientCallback;
import com.avos.avoscloud.im.v2.callback.AVIMConversationCreatedCallback;
import com.avoscloud.leanchatlib.activity.ChatActivity;
import com.avoscloud.leanchatlib.controller.ChatManager;
import com.baoyz.swipemenulistview.SwipeMenu;
import com.baoyz.swipemenulistview.SwipeMenuCreator;
import com.baoyz.swipemenulistview.SwipeMenuItem;
import com.baoyz.swipemenulistview.SwipeMenuListView;
import com.baoyz.swipemenulistview.SwipeMenuListView.OnMenuItemClickListener;
import com.baoyz.swipemenulistview.SwipeMenuListView.OnSwipeListener;
import com.science.strangertofriend.R;
import com.science.strangertofriend.adapter.MessageListAdapter;
import com.science.strangertofriend.adapter.SwingBottomInAnimationAdapter;
import com.science.strangertofriend.ui.ChatRoomActivity;
import com.science.strangertofriend.utils.AVService;

/**
 * @description 消息界面
 * 
 * @author 幸运Science 陈土燊
 * @school University of South China
 * @email chentushen.science@gmail.com,274240671@qq.com
 * @2015-4-25
 * 
 */

public class MessageFragment extends Fragment implements ScreenShotable,
		OnRefreshListener {

	private ImageView mChatNofriend;
	private View mContainerView;
	private Bitmap mBitmap;
	private View mRootView;
	private SwipeRefreshLayout mSwipeRefreshLayout;
	private SwipeMenuListView mMessageList;
	public static MessageListAdapter mMessageListAdapter;
	public static List<Map<String, Object>> mRequestList;
	private static String mCurrentUsername;

	// private static String mFriend;
	// private static String mMessage;
	// private static String mSendTime;

	@Override
	public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		this.mContainerView = view.findViewById(R.id.message_container);
	}

	@Override
	public View onCreateView(LayoutInflater inflater,
			@Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

		mRootView = inflater.inflate(R.layout.message_fragment, container,
				false);

		initView();
		initData();
		initSwipeMenu();

		return mRootView;
	}

	@SuppressLint("ResourceAsColor")
	private void initView() {

		mChatNofriend = (ImageView) mRootView.findViewById(R.id.chat_nofriend);

		// 刷新初始化
		mSwipeRefreshLayout = (SwipeRefreshLayout) mRootView
				.findViewById(R.id.swipe_container);
		mSwipeRefreshLayout.setOnRefreshListener(this);
		mSwipeRefreshLayout.setColorSchemeResources(
				android.R.color.holo_blue_bright,
				android.R.color.holo_green_light,
				android.R.color.holo_orange_light,
				android.R.color.holo_red_light);

		mMessageList = (SwipeMenuListView) mRootView
				.findViewById(R.id.message_listView);
		mRequestList = new ArrayList<Map<String, Object>>();

		if (AVUser.getCurrentUser() != null) {
			mCurrentUsername = AVUser.getCurrentUser().getUsername();
		}
	}

	private void initData() {

		mMessageListAdapter = new MessageListAdapter(getActivity(),
				mRequestList);
		// 动态列表
		SwingBottomInAnimationAdapter swingBottomInAnimationAdapter = new SwingBottomInAnimationAdapter(
				mMessageListAdapter);
		swingBottomInAnimationAdapter.setAbsListView(mMessageList);
		mMessageList.setAdapter(swingBottomInAnimationAdapter);

		getMessageList();
	}

	// 云端查找信息列表
	public List<Map<String, Object>> getMessageList() {

		AVQuery<AVObject> query = new AVQuery<AVObject>("MessageList");
		query.whereEqualTo("currentUser", mCurrentUsername);
		query.orderByDescending("updatedAt");// 按照时间降序
		query.findInBackground(new FindCallback<AVObject>() {

			@Override
			public void done(List<AVObject> list, AVException e) {
				if (list != null && list.size() != 0) {
					mChatNofriend.setVisibility(View.GONE);
					mRequestList.clear();
					for (AVObject avo : list) {

						Map<String, Object> mapUsername = new HashMap<String, Object>();
						mapUsername.put("friend", avo.getString("friend"));
						mapUsername.put("urlAvater", avo.getString("urlAvater"));
						mapUsername.put("frienRequest",
								avo.getString("messsage"));
						mapUsername.put("requestTime",
								avo.getString("sendTime"));
						mRequestList.add(0, mapUsername);
					}
					mMessageListAdapter.notifyDataSetChanged();
				} else {
					mRequestList.clear();
					mMessageListAdapter.notifyDataSetChanged();
					mChatNofriend.setVisibility(View.VISIBLE);
					mChatNofriend.setImageDrawable(getResources().getDrawable(
							R.drawable.chat_nofriend));
				}
			}
		});

		return mRequestList;
	}

	private void initSwipeMenu() {
		// step 1. create a MenuCreator
		SwipeMenuCreator creator = new SwipeMenuCreator() {

			@Override
			public void create(SwipeMenu menu) {
				// create "open" item
				SwipeMenuItem openItem = new SwipeMenuItem(getActivity());
				// set item background
				openItem.setBackground(new ColorDrawable(Color.rgb(0xC9, 0xC9,
						0xCE)));
				// set item width
				openItem.setWidth(dp2px(90));
				// set item title
				openItem.setTitle("Open");
				// set item title fontsize
				openItem.setTitleSize(18);
				// set item title font color
				openItem.setTitleColor(Color.WHITE);
				// add to menu
				menu.addMenuItem(openItem);

				// create "delete" item
				SwipeMenuItem deleteItem = new SwipeMenuItem(getActivity());
				// set item background
				deleteItem.setBackground(new ColorDrawable(Color.rgb(0xF9,
						0x3F, 0x25)));
				// set item width
				deleteItem.setWidth(dp2px(90));
				// set a icon
				deleteItem.setIcon(R.drawable.ic_delete);
				// add to menu
				menu.addMenuItem(deleteItem);
			}
		};

		// set creator
		mMessageList.setMenuCreator(creator);

		// step 2. listener item click event
		mMessageList.setOnMenuItemClickListener(new OnMenuItemClickListener() {
			@Override
			public void onMenuItemClick(int position, SwipeMenu menu, int index) {
				switch (index) {
				case 0:
					// open
					open(position);
					break;
				case 1:
					// delete
					// delete(item);
					reomveMessageItem(mRequestList.get(position));
					mRequestList.remove(position);
					mMessageListAdapter.notifyDataSetChanged();
					break;
				}
			}
		});

		// set SwipeListener
		mMessageList.setOnSwipeListener(new OnSwipeListener() {

			@Override
			public void onSwipeStart(int position) {
				// swipe start
			}

			@Override
			public void onSwipeEnd(int position) {
				// swipe end
			}
		});

		// other setting
		// listView.setCloseInterpolator(new BounceInterpolator());

		// test item long click
		mMessageList.setOnItemLongClickListener(new OnItemLongClickListener() {

			@SuppressLint("ShowToast")
			@Override
			public boolean onItemLongClick(AdapterView<?> parent, View view,
					int position, long id) {
				// Toast.makeText(getActivity(), position + " long click", 0)
				// .show();
				return false;
			}
		});

		mMessageList.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				open(position);
			}
		});
	}

	// 删除消息item
	private void reomveMessageItem(Map<String, Object> item) {
		AVQuery<AVObject> query = new AVQuery<AVObject>("MessageList");
		query.whereEqualTo("friend", item.get("friend"));
		query.findInBackground(new FindCallback<AVObject>() {

			@Override
			public void done(final List<AVObject> list, AVException e) {
				if (list != null && list.size() != 0) {
					// 子线程访问网络
					new Thread(new Runnable() {

						@Override
						public void run() {
							AVService.removeMessage(list.get(list.size() - 1)
									.getObjectId());
						}
					}).start();
				}
			}
		});
	}

	// 进入聊天界面
	private void open(final int position) {
		// Toast.makeText(getActivity(), "This is " + position,
		// Toast.LENGTH_SHORT)
		// .show();
		ChatManager chatManager = ChatManager.getInstance();
		chatManager.setupDatabaseWithSelfId(AVUser.getCurrentUser()
				.getUsername());
		chatManager.openClientWithSelfId(mCurrentUsername,
				new AVIMClientCallback() {
					@Override
					public void done(AVIMClient avimClient, AVException e) {
						if (e != null) {
							e.printStackTrace();
						}
						System.out.print("-----------------------------:"
								+ e.getMessage());
						final ChatManager chatManager = ChatManager
								.getInstance();
						chatManager.fetchConversationWithUserId(mRequestList
								.get(position).get("friend").toString(),
								new AVIMConversationCreatedCallback() {
									@Override
									public void done(
											AVIMConversation conversation,
											AVException e) {
										if (e != null) {
											System.out.print("e.getMessage():"
													+ e.getMessage());
										} else {
											chatManager
													.registerConversation(conversation);
											Intent intent = new Intent(
													getActivity(),
													ChatRoomActivity.class);
											intent.putExtra(
													ChatActivity.CONVID,
													conversation
															.getConversationId());
											intent.putExtra("position",
													position);
											startActivityForResult(intent, 1);
										}
									}
								});
					}
				});
	}

	/**
	 * 查找当前用户聊天好友并显示到message页面
	 * 
	 * @param position
	 */
	private void findMessageListFriend(final String message, final int position) {
		mChatNofriend.setVisibility(View.GONE);
		AVQuery<AVObject> query = new AVQuery<AVObject>("MessageList");
		query.whereEqualTo("currentUser", mCurrentUsername);
		query.whereEqualTo("friend", mRequestList.get(position).get("friend")
				.toString());
		query.findInBackground(new FindCallback<AVObject>() {

			@SuppressLint("SimpleDateFormat")
			@Override
			public void done(final List<AVObject> list, AVException e) {

				// 发送时间
				Date date = new Date();
				SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss");
				final String sendTime = format.format(date);

				if (list != null && list.size() != 0) {
					// 子线程访问网络
					new Thread(new Runnable() {

						@Override
						public void run() {
							AVService.updateMessageList(
									list.get(list.size() - 1).getObjectId(),
									sendTime, message);
						}
					}).start();
				} else {
				}
			}
		});
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {

		if (requestCode == 1) {
			if (resultCode == -1) {

				String messsage = data.getStringExtra("messsage");
				int position = data.getIntExtra("position", 1);
				findMessageListFriend(messsage, position);
			}
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	@Override
	public void onRefresh() {
		getMessageList();
		new Handler().postDelayed(new Runnable() {
			@Override
			public void run() {
				mSwipeRefreshLayout.setRefreshing(false);
			}
		}, 3800);
	}

	@Override
	public void takeScreenShot() {
		new Handler().postDelayed(new Runnable() {
			@Override
			public void run() {
				Bitmap bitmap = Bitmap.createBitmap(mContainerView.getWidth(),
						mContainerView.getHeight(), Bitmap.Config.ARGB_8888);
				Canvas canvas = new Canvas(bitmap);
				mContainerView.draw(canvas);
				MessageFragment.this.mBitmap = bitmap;
			}
		}, 0);

		// Thread thread = new Thread() {
		// @Override
		// public void run() {
		// Bitmap bitmap = Bitmap.createBitmap(mContainerView.getWidth(),
		// mContainerView.getHeight(), Bitmap.Config.ARGB_8888);
		// Canvas canvas = new Canvas(bitmap);
		// mContainerView.draw(canvas);
		// MessageFragment.this.mBitmap = bitmap;
		// }
		// };
		// thread.start();
	}

	@Override
	public Bitmap getBitmap() {
		return mBitmap;
	}

	private int dp2px(int dp) {
		return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp,
				getResources().getDisplayMetrics());
	}

}
