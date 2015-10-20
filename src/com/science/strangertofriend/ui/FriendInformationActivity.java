package com.science.strangertofriend.ui;

import java.util.List;

import org.ocpsoft.prettytime.PrettyTime;

import android.annotation.SuppressLint;
import android.os.Build.VERSION;
import android.os.Build.VERSION_CODES;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;
import cn.pedant.SweetAlert.SweetAlertDialog;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.FindCallback;
import com.science.strangertofriend.R;
import com.science.strangertofriend.utils.Utils;
import com.science.strangertofriend.widget.DampView;

/**
 * @description 好友资料
 * 
 * @author 幸运Science 陈土燊
 * @school University of South China
 * @email chentushen.science@gmail.com,274240671@qq.com
 * @2015-5-22
 * 
 */

public class FriendInformationActivity extends BaseActivity {

	private ImageView mUserBackgroundImg;
	private ImageView mBackImg;

	private ImageView mAvatar;
	private TextView mUsername;
	private ImageView mGender;
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
			setContentView(R.layout.friend_information_kitkat);
		} else {
			setContentView(R.layout.friend_information);
		}

		// 初始化
		initView();
		initData();
		initListener();
	}

	private void initView() {
		// 背景下拉变大
		mUserBackgroundImg = (ImageView) findViewById(R.id.user_background_img);
		DampView view = (DampView) findViewById(R.id.dampview);
		view.setImageView(mUserBackgroundImg);

		mBackImg = (ImageView) findViewById(R.id.back);

		mAvatar = (ImageView) findViewById(R.id.avatar);
		mUsername = (TextView) findViewById(R.id.username);
		mGender = (ImageView) findViewById(R.id.gender);
		mMyStatement = (TextView) findViewById(R.id.my_sign);
		mUserAcount = (TextView) findViewById(R.id.user_number_content);
		mUserPosition = (TextView) findViewById(R.id.user_position_content);
		mUserBirth = (TextView) findViewById(R.id.user_birth_content);
		mUserHome = (TextView) findViewById(R.id.user_home_content);
		mUserConstellation = (TextView) findViewById(R.id.user_constellation);
		mUserInlove = (TextView) findViewById(R.id.user_inlove);
	}

	private void initData() {

		String friendUsername = getIntent().getStringExtra("username");
		mUsername.setText(friendUsername);
		mUserAcount.setText(getIntent().getStringExtra("email"));

		switch (getIntent().getStringExtra("gender")) {
		case "男":
			mGender.setImageDrawable(getResources().getDrawable(
					R.drawable.user_boy));
			break;

		case "女":
			mGender.setImageDrawable(getResources().getDrawable(
					R.drawable.user_girl));
			break;
		}

		// 用户头像(大图)
		AVQuery<AVObject> queryAvater = new AVQuery<AVObject>("Gender");
		queryAvater.whereEqualTo("username", friendUsername);
		queryAvater.findInBackground(findGenderCallback(this, mAvatar));

		AVQuery<AVObject> query = new AVQuery<AVObject>("UserInformation");
		query.whereEqualTo("username", friendUsername);
		query.findInBackground(new FindCallback<AVObject>() {
			public void done(List<AVObject> avObjects, AVException e) {
				if (e == null) {
					Message msg = new Message();
					msg.what = 1;
					msg.obj = avObjects;
					mFriendHandler.sendMessage(msg);
				} else {
					// Toast.makeText(AlterActivity.this, "请检查网络！",
					// Toast.LENGTH_LONG).show();
				}
			}
		});

		// 查找好友距离
		findLocation(AVUser.getCurrentUser().getUsername(), friendUsername);

	}

	private void findLocation(final String currentUser,
			final String friendUsername) {
		new Thread(new Runnable() {

			@Override
			public void run() {
				try {
					double lat1 = 0, lng1 = 0, lat2 = 0, lng2 = 0;
					String locationTime = null;
					AVQuery<AVObject> queryLocation = new AVQuery<AVObject>(
							"MyLocation");
					queryLocation.whereEqualTo("username", currentUser);
					queryLocation.orderByDescending("updatedAt");// 按照时间降序
					queryLocation.setLimit(1);// 最大1个
					List<AVObject> placeList1 = queryLocation.find();
					for (AVObject avo : placeList1) {

						lat1 = avo.getAVGeoPoint("locationPoint").getLatitude();
						lng1 = avo.getAVGeoPoint("locationPoint")
								.getLongitude();
					}
					queryLocation.whereEqualTo("username", friendUsername);
					queryLocation.orderByDescending("updatedAt");// 按照时间降序
					queryLocation.setLimit(1);// 最大1个
					List<AVObject> placeList2 = queryLocation.find();
					for (AVObject avo : placeList2) {

						lat2 = avo.getAVGeoPoint("locationPoint").getLatitude();
						lng2 = avo.getAVGeoPoint("locationPoint")
								.getLongitude();
						locationTime = new PrettyTime().format(avo
								.getUpdatedAt());
					}
					double distance = Utils.DistanceOfTwoPoints(lat1, lng1,
							lat2, lng2);
					Message msg = new Message();
					msg.what = 2;
					// 这三句可以传递数据
					Bundle data = new Bundle();
					data.putString("distance",
							Utils.getPrettyDistance(distance));// distance是标签,handleMessage中使用
					data.putString("locationTime", locationTime);// locationTime是标签,handleMessage中使用
					msg.setData(data);
					mFriendHandler.sendMessage(msg);
				} catch (AVException e) {
					e.printStackTrace();
				}
			}
		}).start();
	}

	@SuppressLint("HandlerLeak")
	private Handler mFriendHandler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 1:
				showOldInformation((List<AVObject>) msg.obj);
				break;

			case 2:
				showPosition(msg.getData().getString("distance"), msg.getData()
						.getString("locationTime"));
				break;
			default:
				break;
			}
		}
	};

	private void showPosition(final String distance, final String locationTime) {

		mUserPosition.setText(distance);
		mUserPosition.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				new SweetAlertDialog(FriendInformationActivity.this,
						SweetAlertDialog.CUSTOM_IMAGE_TYPE)
						.setTitleText("最近登录时间")
						.setContentText(locationTime + "\n" + distance)
						.setCustomImage(R.drawable.recent_location).show();
			}
		});
	}

	// 显示已填写内容
	private void showOldInformation(List<AVObject> responseList) {
		if (responseList != null && responseList.size() != 0) {
			mMyStatement.setText(responseList.get(responseList.size() - 1)
					.getString("personalStatement"));
			mUserBirth.setText(responseList.get(responseList.size() - 1)
					.getString("birth"));
			mUserHome.setText(responseList.get(responseList.size() - 1)
					.getString("hometown"));
			mUserInlove.setText(responseList.get(responseList.size() - 1)
					.getString("inlove"));
			mUserConstellation.setText(responseList
					.get(responseList.size() - 1).getString("constellation"));
		} else {
			mUserBirth.setText("未完善");
			mUserHome.setText("未完善");
			mUserInlove.setText("未完善");
			mUserConstellation.setText("未完善");
		}
	}

	private void initListener() {

		mBackImg.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				FriendInformationActivity.this.finish();
			}
		});
	}

}
