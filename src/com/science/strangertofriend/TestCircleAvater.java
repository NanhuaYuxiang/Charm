package com.science.strangertofriend;

import java.util.List;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVFile;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.FindCallback;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.science.strangertofriend.ui.BaseActivity;

import de.hdodenhof.circleimageview.CircleImageView;

public class TestCircleAvater extends BaseActivity{
	private CircleImageView circleImageView;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.circleimage);
		circleImageView=(CircleImageView) findViewById(R.id.avatar);
		AVQuery<AVObject> query=new AVQuery<AVObject>("Gender");
		query.whereEqualTo("username", "哈登");
		query.findInBackground(showCircleAvaterByImageLoader());
	}
	/*
	 * 后台加载头像并显示的回调接口
	 * 
	 * @return
	 */
	public FindCallback<AVObject> showCircleAvaterByImageLoader() {
		FindCallback<AVObject> findCallback = new FindCallback<AVObject>() {

			@Override
			public void done(List<AVObject> arg0, AVException arg1) {
				if (arg1== null) {
					
					Message msg=Message.obtain();
					msg.what=1;
					msg.obj=arg0;
					mUsernameHandler.sendMessage(msg);
				} else {
//					Toast.makeText(context, "任务加载失败，请检查网络", Toast.LENGTH_LONG);
					Log.e("avaterURL", "检索到的avater为null");
				}
			}

		};

		return findCallback;
	}
	private Handler mUsernameHandler = new Handler() {
		@SuppressWarnings("unchecked")
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 1:
				List<AVObject> responseList = (List<AVObject>) msg.obj;
				if (responseList != null && responseList.size() != 0) {
					String objectId = responseList.get(responseList.size() - 1)
							.getObjectId();
					byteToDrawable(objectId);
				}
				break;
			}
		}
	};

	public void byteToDrawable(final String objectId) {

		new Thread(new Runnable() {

			@Override
			public void run() {

				AVQuery<AVObject> query = new AVQuery<AVObject>("Gender");
				AVObject gender = null;
				try {
					gender = query.get(objectId);
				} catch (AVException e) {
					e.printStackTrace();
				}
				// Retrieving the file
				AVFile imageFile = (AVFile) gender.get("avater");

				Message msg = new Message();
				msg.what = 1;
				msg.obj = imageFile.getUrl();
				Log.e("avaterURL", imageFile.getUrl());
				mHandler2.sendMessage(msg);
			}

		}).start();
	}

	

	private Handler mHandler2 = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 1:
				String avaterURL = (String) msg.obj;
				DisplayImageOptions options = new DisplayImageOptions.Builder()
						.showStubImage(R.drawable.default_load)
						.showImageForEmptyUri(R.drawable.default_load)
						.showImageOnFail(R.drawable.default_load)
						.cacheInMemory(true).cacheOnDisc(true)
						.bitmapConfig(Bitmap.Config.RGB_565).build();
				ImageLoader.getInstance().displayImage("http://ac-x3o016bx.clouddn.com/86O7RAPx2BtTW5zgZTPGNwH9RZD5vNDtPm1YbIcu",
						circleImageView, options);
				break;

			default:
				break;
			}
		};
	};
}
