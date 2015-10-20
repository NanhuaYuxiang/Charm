package com.science.strangertofriend.ui;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build.VERSION;
import android.os.Build.VERSION_CODES;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import cn.pedant.SweetAlert.SweetAlertDialog;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVFile;
import com.avos.avoscloud.AVInstallation;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVPush;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.FindCallback;
import com.avos.avoscloud.SendCallback;
import com.science.strangertofriend.MainActivity;
import com.science.strangertofriend.R;
import com.science.strangertofriend.utils.AVService;
import com.science.strangertofriend.widget.DampView;
import com.science.strangertofriend.widget.RevealLayout;

/**
 * @description ������Ϸ����ѽ���
 * 
 * @author ����Science ������
 * @school University of South China
 * @email chentushen.science@gmail.com,274240671@qq.com
 * @2015-5-7
 * 
 */

public class FriendInformationAddActivity extends BaseActivity {

	private ImageView mUserBackgroundImg;
	private ImageView mBackImg;
	private RevealLayout mRevealLayout;
	private RelativeLayout mLayout;

	private ImageView mAvatar;
	private TextView mUsername;
	private ImageView mGender;
	public Button mAddButton;
	private TextView mMyStatement;
	private TextView mUserAcount;
	private TextView mUserPosition;
	private TextView mUserBirth;
	private TextView mUserHome;
	private TextView mUserInlove;
	private TextView mUserConstellation;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		if (VERSION.SDK_INT >= VERSION_CODES.KITKAT) {
			setContentView(R.layout.friend_information_add_kitkat);
		} else {
			setContentView(R.layout.friend_information_add);
		}

		initView();
		initData();
		initListener();

	}

	private void initView() {

		mAddButton = (Button) findViewById(R.id.user_add);
		// �����������
		mUserBackgroundImg = (ImageView) findViewById(R.id.user_background_img);
		DampView view = (DampView) findViewById(R.id.dampview);
		view.setImageView(mUserBackgroundImg);

		mRevealLayout = (RevealLayout) findViewById(R.id.reveal_layout);
		mLayout = (RelativeLayout) findViewById(R.id.user_container);
		mLayout.setBackgroundColor(Color.WHITE);

		mBackImg = (ImageView) findViewById(R.id.back);
		mAvatar = (ImageView) findViewById(R.id.avatar);
		mUsername = (TextView) findViewById(R.id.username);
		mGender = (ImageView) findViewById(R.id.gender);
		mMyStatement = (TextView) findViewById(R.id.friend_mystatement);
		mUserAcount = (TextView) findViewById(R.id.friend_number_content);
		mUserPosition = (TextView) findViewById(R.id.friend_position_content);
		mUserBirth = (TextView) findViewById(R.id.friend_birth_content);
		mUserHome = (TextView) findViewById(R.id.friend_home_content);
		mUserConstellation = (TextView) findViewById(R.id.friend_constellation);
		mUserInlove = (TextView) findViewById(R.id.friend_inlove);

	}

	private void initData() {

		mUsername.setText(getIntent().getStringExtra("receiveUser"));
		mUserAcount.setText(getIntent().getStringExtra("email"));
		mUserPosition.setText(getIntent().getStringExtra("distance"));
		switch (getIntent().getStringExtra("gender").toString()) {
		case "��":
			mGender.setImageDrawable(getResources().getDrawable(
					R.drawable.user_boy));
			break;

		case "Ů":
			mGender.setImageDrawable(getResources().getDrawable(
					R.drawable.user_girl));
			break;
		}

		// ���Һ���ͷ��
		AVQuery<AVObject> queryGender = new AVQuery<AVObject>("Gender");
		queryGender.whereEqualTo("username",
				getIntent().getStringExtra("receiveUser"));
		queryGender.findInBackground(findGenderCallback(this, mAvatar));

		// ���Һ�����Ϣ
		AVQuery<AVObject> query = new AVQuery<AVObject>("UserInformation");
		query.whereEqualTo("username", getIntent()
				.getStringExtra("receiveUser"));
		query.findInBackground(new FindCallback<AVObject>() {
			public void done(List<AVObject> avObjects, AVException e) {
				if (e == null) {
					Message msg = new Message();
					msg.what = 1;
					msg.obj = avObjects;
					mFriendHandler.sendMessage(msg);
				} else {
					// Toast.makeText(AlterActivity.this, "�������磡",
					// Toast.LENGTH_LONG).show();
				}
			}
		});
	}

	@SuppressLint("HandlerLeak")
	private Handler mFriendHandler = new Handler() {
		@SuppressWarnings("unchecked")
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 1:
				showOldInformation((List<AVObject>) msg.obj);
				break;
			default:
				break;
			}
		}
	};

	// ��ʾ����д����
	private void showOldInformation(List<AVObject> responseList) {
		if (responseList != null && responseList.size() != 0) {
			mMyStatement.setText(responseList.get(responseList.size() - 1)
					.getString("personalStatement"));
			// mUserAcount.setText(responseList.get(responseList.size() - 1)
			// .getString("email"));
			mUserBirth.setText(responseList.get(responseList.size() - 1)
					.getString("birth"));
			mUserHome.setText(responseList.get(responseList.size() - 1)
					.getString("hometown"));
			mUserInlove.setText(responseList.get(responseList.size() - 1)
					.getString("inlove"));
			mUserConstellation.setText(responseList
					.get(responseList.size() - 1).getString("constellation"));
		} else {
			mMyStatement.setText("δ����");
			mUserBirth.setText("δ����");
			mUserHome.setText("δ����");
			mUserInlove.setText("δ����");
			mUserConstellation.setText("δ����");
		}
	}

	private void initListener() {

		mRevealLayout.setContentShown(false);
		mRevealLayout.getViewTreeObserver().addOnGlobalLayoutListener(
				new ViewTreeObserver.OnGlobalLayoutListener() {
					@SuppressWarnings("deprecation")
					@Override
					public void onGlobalLayout() {
						mRevealLayout.getViewTreeObserver()
								.removeGlobalOnLayoutListener(this);
						mRevealLayout.postDelayed(new Runnable() {
							@Override
							public void run() {
								mRevealLayout.show(2000);
							}
						}, 50);
					}
				});
		mRevealLayout.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {

			}
		});

		mBackImg.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				FriendInformationAddActivity.this.finish();
			}
		});

		mAddButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				addFriend();
			}
		});

		mUserPosition.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (mUserPosition.getText().toString() != null) {
					new SweetAlertDialog(FriendInformationAddActivity.this,
							SweetAlertDialog.CUSTOM_IMAGE_TYPE)
							.setTitleText("�����¼ʱ��")
							.setContentText(
									getIntent().getStringExtra("locationTime")
											+ "\n"
											+ mUserPosition.getText()
													.toString())
							.setCustomImage(R.drawable.recent_location).show();
				}
			}
		});

		mMyStatement.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (mMyStatement.getText().toString() != null) {

					new SweetAlertDialog(FriendInformationAddActivity.this,
							SweetAlertDialog.CUSTOM_IMAGE_TYPE)
							.setTitleText("Ta��ǩ��")
							.setContentText(mMyStatement.getText().toString())
							.setCustomImage(R.drawable.add_friend_statement)
							.show();
				}
			}
		});
	}

	// ��ӵȴ�
	private void addFriend() {

		new SweetAlertDialog(FriendInformationAddActivity.this,
				SweetAlertDialog.WARNING_TYPE)
				.setTitleText("ȷ�����Ϊ����?")
				.setCancelText("ȡ��")
				.setConfirmText("ȷ��")
				.showCancelButton(true)
				.setCancelClickListener(
						new SweetAlertDialog.OnSweetClickListener() {
							@Override
							public void onClick(SweetAlertDialog sDialog) {
								// reuse previous dialog instance, keep
								// widget user state, reset them if you need
								sDialog.setTitleText("��ȡ��!")
										.setConfirmText("OK")
										.showCancelButton(false)
										.setCancelClickListener(null)
										.setConfirmClickListener(null)
										.changeAlertType(
												SweetAlertDialog.SUCCESS_TYPE);
							}
						})
				.setConfirmClickListener(
						new SweetAlertDialog.OnSweetClickListener() {
							@Override
							public void onClick(final SweetAlertDialog sDialog) {
								sDialog.dismiss();

								final SweetAlertDialog nAlertDialog = new SweetAlertDialog(
										FriendInformationAddActivity.this,
										SweetAlertDialog.PROGRESS_TYPE)
										.setTitleText("��������,����Ը��");
								nAlertDialog.show();
								nAlertDialog.setCancelable(false);
								new CountDownTimer(800 * 4, 800) {
									public void onTick(long millisUntilFinished) {
										// you can change the progress bar color
										// by ProgressHelper
										// every 800 millis
										colorProgress(nAlertDialog);
									}

									public void onFinish() {
										i = -1;
										nAlertDialog.dismiss();
										friendValidation();
									}
								}.start();
							}
						}).show();
	}

	// ���ͺ�����֤
	private void friendValidation() {

		final String receiveUser = getIntent().getStringExtra("receiveUser");
		final String sendUsername = getIntent().getStringExtra("sendUsername");
		AVQuery<AVUser> query = AVUser.getQuery();
		query.whereEqualTo("username", receiveUser);
		query.findInBackground(new FindCallback<AVUser>() {

			@Override
			public void done(List<AVUser> list, AVException arg1) {
				if (list != null && list.size() != 0) {
					friendValidationPassthrough(sendUsername, receiveUser, list
							.get(list.size() - 1).getString("installationId"));
				}
			}
		});
	}

	private void friendValidationPassthrough(final String sendUsername,
			final String receiveUser, String receiveUserID) {

		AVPush push = new AVPush();
		// ����Ƶ��
		push.setChannel("public");
		// ������Ϣ
		push.setMessage(sendUsername + "�������Ϊ����");
		// ���ò�ѯ����
		push.setQuery(AVInstallation.getQuery().whereEqualTo("installationId",
				receiveUserID));
		// ����
		push.sendInBackground(new SendCallback() {
			@Override
			public void done(AVException e) {
				Toast toast = null;
				if (e == null) {
					toast = Toast.makeText(FriendInformationAddActivity.this,
							"���ͳɹ�", Toast.LENGTH_SHORT);
					findFriendObjId(receiveUser, sendUsername);
				} else {
					toast = Toast.makeText(FriendInformationAddActivity.this,
							"����ʧ�ܣ���������", Toast.LENGTH_SHORT);
				}
				// callback ������ UI �̡߳�
				toast.show();
			}
		});
	}

	private void findFriendObjId(final String receiveUser,
			final String sendUsername) {
		AVQuery<AVObject> queryFriend = new AVQuery<AVObject>("Gender");
		queryFriend.whereEqualTo("username", receiveUser);
		queryFriend.findInBackground(new FindCallback<AVObject>() {

			@Override
			public void done(List<AVObject> list, AVException arg1) {
				if (list != null && list.size() != 0) {
					findFriendAvaterUrl(receiveUser, sendUsername,
							list.get(list.size() - 1).getObjectId(),
							list.get(list.size() - 1).getString("email"), list
									.get(list.size() - 1).getString("gender"),
							1);
				}
			}
		});

		AVQuery<AVObject> queryCurrentUser = new AVQuery<AVObject>("Gender");
		queryCurrentUser.whereEqualTo("username", sendUsername);
		queryCurrentUser.findInBackground(new FindCallback<AVObject>() {

			@Override
			public void done(List<AVObject> list, AVException arg1) {
				if (list != null && list.size() != 0) {
					findFriendAvaterUrl(receiveUser, sendUsername,
							list.get(list.size() - 1).getObjectId(),
							list.get(list.size() - 1).getString("email"), list
									.get(list.size() - 1).getString("gender"),
							2);
				}
			}
		});
	}

	boolean flag = true;

	private void findFriendAvaterUrl(final String receiveUser,
			final String sendUsername, final String objId, final String email,
			final String gender, final int id) {

		new Thread(new Runnable() {

			@SuppressLint("SimpleDateFormat")
			@Override
			public void run() {

				// ȡ�÷���ʱ��
				Date date = new Date();
				SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss");
				String sendTime = format.format(date);

				AVQuery<AVObject> query = new AVQuery<AVObject>("Gender");
				AVObject avObj = null;
				try {
					avObj = query.get(objId);
				} catch (AVException e) {
					e.printStackTrace();
				}
				// Retrieving the file
				switch (id) {
				case 1:
					AVFile imageFileFriend = (AVFile) avObj.get("avater");
					String avaterUrlFriend = imageFileFriend.getThumbnailUrl(
							false, 120, 120);
					// ������Ϣ
					AVService.messageList(receiveUser, avaterUrlFriend,
							sendUsername, sendTime, receiveUser + "�ѳ�Ϊ�ҵĺ���");
					// �������ͨѶ¼
					AVService.addressList(receiveUser, sendUsername,
							avaterUrlFriend, email, gender, sendTime);
					break;

				case 2:
					AVFile imageFileCurrent = (AVFile) avObj.get("avater");
					String avaterUrlCurrent = imageFileCurrent.getThumbnailUrl(
							false, 120, 120);
					AVService.messageList(sendUsername, avaterUrlCurrent,
							receiveUser, sendTime, sendUsername + "�ѳ�Ϊ�ҵĺ���");
					AVService.addressList(sendUsername, receiveUser,
							avaterUrlCurrent, email, gender, sendTime);
					break;
				}

				if (flag) {
					Intent intent = new Intent(
							FriendInformationAddActivity.this,
							MainActivity.class);
					startActivity(intent);
					// AppManager.getAppManager().finishActivity(
					// MainActivity.class);
					// AppManager.getAppManager().finishActivity(
					// ShowNearMenMapActivity.class);
					// AppManager.getAppManager().finishActivity(
					// PuzzleActivity.class);
					FriendInformationAddActivity.this.finish();
					flag = false;
				}

			}

		}).start();
	}
}
