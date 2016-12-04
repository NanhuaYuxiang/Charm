package com.science.strangertofriend.ui;

import java.util.HashMap;
import java.util.Map;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.widget.ImageView;
import android.widget.Toast;

import com.avos.avoscloud.AVCloud;
import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVInstallation;
import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.FunctionCallback;
import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.location.LocationClientOption.LocationMode;
import com.baidu.mapapi.SDKInitializer;
import com.science.strangertofriend.AppManager;
import com.science.strangertofriend.MainActivity;
import com.science.strangertofriend.R;
import com.science.strangertofriend.guide.GuideActivity;

/**
 * @description 欢迎界面
 * 
 */

public class WelcomeActivity extends Activity {
	
	private ImageView mWelcomeImg;
	private double latitude, longitude;
	public LocationClient mLocationClient = null;
	public BDLocationListener myListener =null;
	
	public static final String GEOWIND="com.science.strangertofriend.geowind";//经纬度tag
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// 定位相关
		SDKInitializer.initialize(getApplicationContext());
		mLocationClient = new LocationClient(getApplicationContext()); // 声明LocationClient类
		myListener = new MyLocationListener();
		mLocationClient.registerLocationListener(myListener); // 注册监听函数
		initLocationOption();

		// 将activity加入到AppManager堆栈中
		AppManager.getAppManager().addActivity(this);

		SharedPreferences settingPreferences = getSharedPreferences(
				"WelcomeActivity", 0);
		boolean isFirstIn = settingPreferences.getBoolean("isFirstIn", true);
		// 首次打开app
		if (isFirstIn) {
			settingPreferences.edit().putBoolean("isFirstIn", false).commit();
			Intent intent = new Intent(WelcomeActivity.this,
					GuideActivity.class);
			startActivity(intent);
			WelcomeActivity.this.finish();
		} else {
			// 为登录情况
			if (AVUser.getCurrentUser() == null) {

				Intent intent = new Intent(WelcomeActivity.this,
						LoginActivity.class);
				startActivity(intent);
				WelcomeActivity.this.finish();
				// 已登录情况
			} else {

				getWindow().setFlags(
						WindowManager.LayoutParams.FLAG_FULLSCREEN,
						WindowManager.LayoutParams.FLAG_FULLSCREEN);

				setContentView(R.layout.welcome);

				mWelcomeImg = (ImageView) findViewById(R.id.welcome);
				AlphaAnimation aa = new AlphaAnimation(0f, 1f);
				aa.setDuration(1000);
				mWelcomeImg.startAnimation(aa);
				aa.setAnimationListener(new AnimationListener() {

					@Override
					public void onAnimationStart(Animation animation) {
					}

					@Override
					public void onAnimationRepeat(Animation animation) {
					}

					@Override
					public void onAnimationEnd(Animation animation) {
						
						//查看用户是否开启人脸识别功能
						SharedPreferences sharedPreferences=getSharedPreferences(SettingActivity.IS_OPEN_FACE_VERIFY, MODE_PRIVATE);
						boolean isOpenFaceVerify=sharedPreferences.getBoolean("isOpenFaceVerify", false);
						if(isOpenFaceVerify){
							
							Intent intent=new Intent(WelcomeActivity.this,OnlineFaceVerify.class);
							intent.putExtra("latitude", latitude);
							
							startActivity(intent);
						}else{
							
							Intent intent = new Intent(WelcomeActivity.this,
									MainActivity.class);
							startActivity(intent);
						}
						 WelcomeActivity.this.finish();
					}
				});
			}
		}
	}

	private  class MyLocationListener implements BDLocationListener {

		@Override
		public void onReceiveLocation(BDLocation location) {
			latitude = location.getLatitude();
			longitude = location.getLongitude();
			String addString=location.getAddrStr();
			//将经纬度信息保存起来
			SharedPreferences.Editor editor=getSharedPreferences(GEOWIND,MODE_PRIVATE).edit();
			editor.putString("latitude", latitude+"");
			editor.putString("longitude", longitude+"");
			editor.putString("addstr", addString);
			editor.commit();
			notifyLeancloud();
		}

	}

	private void initLocationOption() {
		LocationClientOption option = new LocationClientOption();
		// option.setLocationMode(LocationMode.Hight_Accuracy);//设置定位模式
		// option.setNeedDeviceDirect(true);//返回的定位结果包含手机机头的方向
		option.setCoorType("bd09ll");// 返回的定位结果是百度经纬度,默认值gcj02
		option.setIsNeedAddress(true);// 返回的定位结果包含地址信息
		option.setOpenGps(true);
		option.setScanSpan(1000 * 60);// 每隔1分钟发一次定位请求
		mLocationClient.setLocOption(option);
	}
	
	public void notifyLeancloud(){
		Map<String, Object> dicParameters = new HashMap<String, Object>();
		dicParameters.put("instalId", AVInstallation.getCurrentInstallation().getInstallationId());
		dicParameters.put("latitude", latitude);
		dicParameters.put("longitude", longitude);
		// 调用云函数 averageStars
		AVCloud.callFunctionInBackground("queryGeo", dicParameters, new FunctionCallback<Object>() {

			@Override
			public void done(Object object, AVException exception) {
				
			}
		});
	}
	
	@Override
	protected void onStart() {
		super.onStart();
		//开始定位
		mLocationClient.start();
	}
	
	@Override
	protected void onStop() {
		super.onStop();
		//停止定位
		mLocationClient.stop();
	}

}
