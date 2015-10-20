package com.science.strangertofriend.ui;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.ocpsoft.prettytime.PrettyTime;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ZoomControls;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVGeoPoint;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.AVUser;
import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BaiduMap.OnMarkerClickListener;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.InfoWindow;
import com.baidu.mapapi.map.InfoWindow.OnInfoWindowClickListener;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.model.LatLng;
import com.science.strangertofriend.AppContext;
import com.science.strangertofriend.R;
import com.science.strangertofriend.bean.LocationMenList;
import com.science.strangertofriend.game.puzzle.PuzzleActivity;
import com.science.strangertofriend.utils.AVService;
import com.science.strangertofriend.utils.Utils;
import com.science.strangertofriend.widget.RevealLayout;

/**
 * @description 地图显示附近的人
 * 
 * @author 幸运Science 陈土燊
 * @school University of South China
 * @email chentushen.science@gmail.com,274240671@qq.com
 * @2015-5-1
 * 
 */

public class ShowNearMenMapActivity extends BaseActivity {

	private MapView mMapView = null;
	private BaiduMap mBaiduMap;
	private RevealLayout mRevealLayout;
	private FrameLayout mMapLayout;

	private Context context;
	private String mUserEmail, mUsername, mGender;

	// 定位相关
	private LocationClient mLocationClient;
	private MyLocationListener mLocationListener;
	private boolean isFirstIn = true;
	private double mLatitude;
	private double mLongtitude;
	private ImageView mMapLocation;
	private AVGeoPoint mMyPoint;

	// 覆盖物相关
	private BitmapDescriptor mMarkDescriptor;
	// 列表数据
	private ArrayList<LocationMenList> mLocationMenList;

	// 自定义定位图标
	// private BitmapDescriptor mIconLocation;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// 在使用SDK各组件之前初始化context信息，传入ApplicationContext
		// 注意该方法要再setContentView方法之前实现
		SDKInitializer.initialize(getApplicationContext());

		setContentView(R.layout.near_men_map);

		this.context = this;

		initComponent();
		initListener();
		// 初始化定位
		initLocation();
		initMarker();
		setMarkerClickListener();
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
	}

	private void initComponent() {
		// 获取地图控件引用
		mMapView = (MapView) findViewById(R.id.bmapView);
		mBaiduMap = mMapView.getMap();
		// 隐藏百度logo
		View child = mMapView.getChildAt(1);
		if (child != null
				&& (child instanceof ImageView || child instanceof ZoomControls)) {
			child.setVisibility(View.INVISIBLE);
		}
		// 地图比例200m
		MapStatusUpdate mapStatusUpdate = MapStatusUpdateFactory.zoomTo(16.0f);
		mBaiduMap.setMapStatus(mapStatusUpdate);
		// 获取定位我的位置的图标
		mMapLocation = (ImageView) findViewById(R.id.map_location);
		mLocationMenList = new ArrayList<LocationMenList>();
		mRevealLayout = (RevealLayout) findViewById(R.id.reveal_layout);
		mMapLayout = (FrameLayout) findViewById(R.id.map_layout);
		mMapLayout.setBackgroundColor(Color.TRANSPARENT);

		AVUser currentUser = AVUser.getCurrentUser();
		if (currentUser != null) {
			mUserEmail = currentUser.getEmail();
			mUsername = currentUser.getUsername();
			mGender = currentUser.getString("gender");
		} else {
			Toast.makeText(context, "您还没有登陆喔", Toast.LENGTH_LONG).show();
		}
	}

	private void initLocation() {

		mLocationClient = new LocationClient(getApplicationContext());
		mLocationListener = new MyLocationListener();
		mLocationClient.registerLocationListener(mLocationListener);

		LocationClientOption option = new LocationClientOption();
		// option.setLocationMode(LocationMode.Hight_Accuracy);//设置定位模式
		// option.setNeedDeviceDirect(true);//返回的定位结果包含手机机头的方向
		option.setCoorType("bd09ll");// 返回的定位结果是百度经纬度,默认值gcj02
		option.setIsNeedAddress(true);// 返回的定位结果包含地址信息
		option.setOpenGps(true);
		option.setScanSpan(3600000);// 设置发起定位请求的间隔时间为1000ms
		mLocationClient.setLocOption(option);

		// 定位到我的位置
		mMapLocation.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				centerToMyLocation();
			}
		});

		// mIconLocation = BitmapDescriptorFactory
		// .fromResource(R.drawable.navi_map_gps_locked);
	}

	private void initMarker() {

		mMarkDescriptor = BitmapDescriptorFactory
				.fromResource(R.drawable.maker);
	}

	private class MyLocationListener implements BDLocationListener {

		@Override
		public void onReceiveLocation(BDLocation location) {

			MyLocationData data = new MyLocationData.Builder()
					.accuracy(location.getRadius())
					.latitude(location.getLatitude())
					.longitude(location.getLongitude()).build();
			mBaiduMap.setMyLocationData(data);

			// 设置自定义图标
			// MyLocationConfiguration configuration = new
			// MyLocationConfiguration(
			// LocationMode.NORMAL, true, mIconLocation);
			// mBaiduMap.setMyLocationConfigeration(configuration);

			// 更新经纬度
			mLatitude = location.getLatitude();
			mLongtitude = location.getLongitude();

			if (isFirstIn) {

				if (AppContext.isThisLocation) {
					AVService.myLocation(mUserEmail, mUsername, mGender,
							mLatitude, mLongtitude, location.getAddrStr());
					AppContext.isThisLocation = false;
				}

				mMyPoint = new AVGeoPoint(mLatitude, mLongtitude);
				LatLng latLng = new LatLng(mLatitude, mLongtitude);
				MapStatusUpdate msu = MapStatusUpdateFactory.newLatLng(latLng);
				mBaiduMap.animateMapStatus(msu);
				isFirstIn = false;

				Toast.makeText(context, location.getAddrStr(),
						Toast.LENGTH_LONG).show();

				// 查找附近1000米的人
				findMenNearby();
			}
		}
	}

	private void findMenNearby() {
		new Thread(new Runnable() {

			@Override
			public void run() {
				try {
					AVQuery<AVObject> query = new AVQuery<>("MyLocation");
					// 查找附近1000米的人
					query.whereWithinKilometers("locationPoint", mMyPoint, 1);
					query.whereNotEqualTo("username", mUsername);
					List<AVObject> placeList = query.find();

					for (AVObject avo : placeList) {
						mLocationMenList.add(new LocationMenList(avo
								.getString("userEmail"), avo
								.getString("username"),
								avo.getString("gender"), avo.getAVGeoPoint(
										"locationPoint").getLatitude(), avo
										.getAVGeoPoint("locationPoint")
										.getLongitude(), new PrettyTime()
										.format(avo.getUpdatedAt())));
					}
					mMenListHandler.obtainMessage(1).sendToTarget();
				} catch (AVException e) {
					e.printStackTrace();
				}
			}
		}).start();
	}

	@SuppressLint("HandlerLeak")
	private Handler mMenListHandler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 1:
				addOverLays(mLocationMenList);
				break;
			}
		}
	};

	// 添加覆盖物
	private void addOverLays(List<LocationMenList> avObjects) {

		mBaiduMap.clear();
		LatLng latLng = null;
		Marker marker = null;
		OverlayOptions options;

		for (final LocationMenList menList : avObjects) {

			// 经纬度
			latLng = new LatLng(menList.getLatitude(), menList.getLongtitude());
			// 图标
			options = new MarkerOptions().position(latLng)
					.icon(mMarkDescriptor).zIndex(5);
			marker = (Marker) mBaiduMap.addOverlay(options);

			Bundle bundle = new Bundle();
			bundle.putSerializable("menList", (Serializable) menList);
			marker.setExtraInfo(bundle);

			TextView textView = new TextView(context);
			textView.setBackgroundResource(R.drawable.location_tips);
			textView.setPadding(23, 20, 20, 40);
			textView.setTextColor(Color.WHITE);
			textView.setText(menList.getUsername());

			InfoWindow infoWindow;
			infoWindow = new InfoWindow(
					BitmapDescriptorFactory.fromView(textView), latLng, -45,
					new OnInfoWindowClickListener() {

						@Override
						public void onInfoWindowClick() {
							// 解密游戏
							decodeGame(menList.getUsername(),
									menList.getLatitude(),
									menList.getLongtitude(),
									menList.getUserEmail(),
									menList.getGender(),
									menList.getLocationTime());
						}
					});
			mBaiduMap.showInfoWindow(infoWindow);
		}

	}

	private void setMarkerClickListener() {

		mBaiduMap.setOnMarkerClickListener(new OnMarkerClickListener() {

			@Override
			public boolean onMarkerClick(Marker marker) {

				Bundle extraInfo = marker.getExtraInfo();
				final LocationMenList menList = (LocationMenList) extraInfo
						.getSerializable("menList");

				InfoWindow infoWindow;
				final LatLng latLng = marker.getPosition();
				TextView textView = new TextView(context);
				textView.setBackgroundResource(R.drawable.location_tips);
				textView.setPadding(23, 20, 20, 40);
				textView.setTextColor(Color.WHITE);
				textView.setText(menList.getUsername());

				infoWindow = new InfoWindow(BitmapDescriptorFactory
						.fromView(textView), latLng, -45,
						new OnInfoWindowClickListener() {

							@Override
							public void onInfoWindowClick() {
								// 解密游戏
								decodeGame(menList.getUsername(),
										menList.getLatitude(),
										menList.getLongtitude(),
										menList.getUserEmail(),
										menList.getGender(),
										menList.getLocationTime());
							}
						});
				mBaiduMap.showInfoWindow(infoWindow);

				return true;
			}
		});
	}

	private void decodeGame(String receiveUser, double latitude,
			double longtitude, String email, String gender, String locationTime) {

		Intent intent = new Intent(context, PuzzleActivity.class);
		intent.putExtra("receiveUser", receiveUser); // 接收验证的
		intent.putExtra("sendUsername", mUsername); // 发送验证的(当前用户)
		// int distance = (int) DistanceUtil.getDistance(new LatLng(mLatitude,
		// mLongtitude), new LatLng(latitude, longtitude));
		double distance = Utils.DistanceOfTwoPoints(mLatitude, mLongtitude,
				latitude, longtitude);
		intent.putExtra("distance", Utils.getPrettyDistance(distance));
		intent.putExtra("email", email);
		intent.putExtra("gender", gender);
		intent.putExtra("locationTime", locationTime);
		startActivity(intent);
	}

	/**
	 * 定位到我的位置
	 */
	private void centerToMyLocation() {
		LatLng latLng = new LatLng(mLatitude, mLongtitude);
		MapStatusUpdate msu = MapStatusUpdateFactory.newLatLng(latLng);
		mBaiduMap.animateMapStatus(msu);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		switch (item.getItemId()) {
		case R.id.map_common:
			mBaiduMap.setMapType(BaiduMap.MAP_TYPE_NORMAL);
			break;
		case R.id.map_site:
			mBaiduMap.setMapType(BaiduMap.MAP_TYPE_SATELLITE);
			break;
		case R.id.map_traffic:
			if (mBaiduMap.isTrafficEnabled()) {
				mBaiduMap.setTrafficEnabled(false);
				item.setTitle("实时地图(off)");
			} else {
				mBaiduMap.setTrafficEnabled(true);
				item.setTitle("实时地图(on)");
			}
			break;
		default:
			break;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		// 在activity执行onDestroy时执行mMapView.onDestroy()，实现地图生命周期管理
		mMapView.onDestroy();
	}

	@Override
	protected void onResume() {
		super.onResume();
		// 在activity执行onResume时执行mMapView. onResume ()，实现地图生命周期管理
		mMapView.onResume();
	}

	@Override
	protected void onStart() {
		super.onStart();
		// 开始定位
		mBaiduMap.setMyLocationEnabled(true);
		if (!mLocationClient.isStarted()) {
			mLocationClient.start();
		}
		mLocationClient.requestLocation();
	}

	@Override
	protected void onStop() {
		super.onStop();
		// 停止定位
		mBaiduMap.setMyLocationEnabled(false);
		mLocationClient.stop();
	}

	@Override
	protected void onPause() {
		super.onPause();
		// 在activity执行onPause时执行mMapView. onPause ()，实现地图生命周期管理
		mMapView.onPause();
	}

}
