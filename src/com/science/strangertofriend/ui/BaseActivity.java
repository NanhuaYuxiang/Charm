package com.science.strangertofriend.ui;

import java.util.List;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Build.VERSION;
import android.os.Build.VERSION_CODES;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.DisplayMetrics;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;
import cn.pedant.SweetAlert.SweetAlertDialog;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVFile;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.FindCallback;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.science.strangertofriend.AppContext;
import com.science.strangertofriend.AppManager;
import com.science.strangertofriend.R;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * @description ����
 * 
 * @author ����Science ������
 * @school University of South China
 * @email chentushen.science@gmail.com,274240671@qq.com
 * @2015-4-26
 * 
 */

public class BaseActivity extends Activity {

	private AppContext appContext;// ȫ��Context
	// ����һ������������ʶ�Ƿ��˳�
	private static boolean isExit = false;
	public int i = -1;
	private View mView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		appContext = (AppContext) getApplication();
		// ���������ж�
		if (!appContext.isNetworkConnected())
			Toast.makeText(this, R.string.network_not_connected,
					Toast.LENGTH_LONG).show();

		// ����ʽ״̬������
		initSystemBar();
		// ��activity���뵽AppManager��ջ��
		AppManager.getAppManager().addActivity(this);
	}

	@TargetApi(Build.VERSION_CODES.KITKAT)
	public void initSystemBar() {
		if (VERSION.SDK_INT >= VERSION_CODES.KITKAT) {
			// ͸��״̬��
			getWindow().addFlags(
					WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
			// ͸��������
			getWindow().addFlags(
					WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
		}
	}

	// ��������ɫ
	public void colorProgress(SweetAlertDialog pDialog) {
		i++;
		switch (i) {
		case 0:
			pDialog.getProgressHelper().setBarColor(
					getResources().getColor(android.R.color.holo_blue_bright));
			break;

		case 1:
			pDialog.getProgressHelper().setBarColor(
					getResources().getColor(android.R.color.holo_green_light));
			break;
		case 2:
			pDialog.getProgressHelper().setBarColor(
					getResources().getColor(android.R.color.holo_orange_light));
			break;

		case 3:
			pDialog.getProgressHelper().setBarColor(
					getResources().getColor(android.R.color.holo_red_light));
			break;
		}
	}

	// ���һص�
	public FindCallback<AVObject> findGenderCallback(final Context context,
			View view) {

		mView = view;

		FindCallback<AVObject> findCallback = new FindCallback<AVObject>() {
			public void done(List<AVObject> avObjects, AVException e) {
				if (e == null) {
					Message msg = new Message();
					msg.what = 1;
					msg.obj = avObjects;
					mUsernameHandler.sendMessage(msg);
				} else {
					Toast.makeText(context, "�������磡", Toast.LENGTH_LONG).show();
				}
			}
		};
		return findCallback;
	}

	@SuppressLint("HandlerLeak")
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
				mHandlerLoad.sendMessage(msg);
				// imageFile.getDataInBackground(new GetDataCallback() {
				// public void done(byte[] data, AVException e) {
				// if (data != null) {
				// // Success; data has the file
				// bitmap = BitmapFactory.decodeByteArray(data, 0,
				// data.length);
				// ((CircleImageView) mView).setImageBitmap(bitmap);
				// } else {
				// }
				// }
				// });
			}

		}).start();
	}

	// ���߳�Handlerˢ��UI����
	@SuppressLint("HandlerLeak")
	private Handler mHandlerLoad = new Handler() {

		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 1:
				@SuppressWarnings("deprecation")
				DisplayImageOptions options = new DisplayImageOptions.Builder()
						.showStubImage(R.drawable.default_load)
						.showImageForEmptyUri(R.drawable.default_load)
						.showImageOnFail(R.drawable.default_load)
						.cacheInMemory(true).cacheOnDisc(true)
						.bitmapConfig(Bitmap.Config.RGB_565).build();
				ImageLoader.getInstance().displayImage((String) msg.obj,
						((CircleImageView) mView), options);
			}
		};
	};

	// ��ȡ��Ļ�Ŀ��
	public int getScreenWidth() {
		DisplayMetrics dm = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(dm);
		int screenWidth = dm.widthPixels;
		return screenWidth;
	}

	// ��ȡ��Ļ�ĸ߶�
	public int getScreenHeight() {
		DisplayMetrics dm = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(dm);
		int screenHeight = dm.heightPixels;
		return screenHeight;
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
			BaseActivity.this.finish();
			return false;
		}
		return super.onKeyDown(keyCode, event);
	}

	// LayoutParams laParams = (LayoutParams)
	// mCameraAvatar
	// .getLayoutParams();
	// laParams.width = (getScreenWidth() / 3) + 20;
	// laParams.height = (getScreenHeight() / 3) + 20;
	// mCameraAvatar.setLayoutParams(laParams);
	// ---------------
	// mCameraAvatar
	// .setMaxHeight((getScreenHeight() / 3) + 20);
	// mCameraAvatar
	// .setMaxWidth((getScreenWidth() / 3) + 20);
	// mCameraAvatar
	// .setMinimumHeight((getScreenWidth() / 3) + 20);
	// mCameraAvatar
	// .setMinimumWidth((getScreenWidth() / 3) + 20);
}
