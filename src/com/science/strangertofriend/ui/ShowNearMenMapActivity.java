package com.science.strangertofriend.ui;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Toast;
import android.widget.ZoomControls;

import com.avos.avoscloud.AVCloud;
import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVFile;
import com.avos.avoscloud.AVGeoPoint;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.FindCallback;
import com.avos.avoscloud.FunctionCallback;
import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.cloud.NearbySearchInfo;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BaiduMap.OnMarkerClickListener;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.MyLocationConfiguration;
import com.baidu.mapapi.map.MyLocationConfiguration.LocationMode;
import com.baidu.mapapi.map.offline.MKOfflineMap;
import com.baidu.mapapi.map.offline.MKOfflineMapListener;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.model.LatLng;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.science.strangertofriend.AppContext;
import com.science.strangertofriend.R;
import com.science.strangertofriend.bean.LocationMenList;
import com.science.strangertofriend.bean.Task;
import com.science.strangertofriend.game.puzzle.PuzzleActivity;
import com.science.strangertofriend.listener.MyOrientationListener;
import com.science.strangertofriend.utils.AVService;
import com.science.strangertofriend.utils.Utils;
import com.science.strangertofriend.widget.RevealLayout;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * @description 地图显示附近的任务
 * 
 * @author 赵鑫
 * @school University of South China
 * @email apologizetoher@Gmail.com
 * @2015-10-21
 * 
 */

public class ShowNearMenMapActivity extends BaseActivity implements
		OnClickListener {
	private View view;
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
	private static double mLatitude;
	private static double mLongtitude;
	private ImageView mMapLocation;
	private AVGeoPoint mMyPoint;
	private MKOfflineMap mOfflineMap;

	// 覆盖物相关
	private BitmapDescriptor mMarkDescriptor;
	// 列表数据
	private ArrayList<LocationMenList> mLocationMenList;

	// 自定义定位图标
	private BitmapDescriptor mIconLocation;
	// 定位图表方向角
	private float currentX;
	private MyOrientationListener myOrientationListener;
	private ImageView add_task;// 添加任务
	private List<Task> taskNearBy = new ArrayList<Task>();// 检索到的附近素有符合条件的任务
	private CircleImageView circleImageView;
	private String cityCode;

	// 头像集合相关
	HashMap<String, String> hash_avaterUrls = new HashMap<>();
	HashMap<String, BitmapDescriptor> avaterMarkers = new HashMap<String, BitmapDescriptor>();
	HashMap<String, Bitmap> bitMaps = new HashMap<>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// 在使用SDK各组件之前初始化context信息，传入ApplicationContext
		// 注法意该方法要再setContentView方之前实现
		SDKInitializer.initialize(getApplicationContext());

		setContentView(R.layout.near_men_map);
		this.context = this;

		initComponent();
		initListener();
		// 初始化定位
		initLocation();

		setMarkerClickListener();
		// Log.e("ShowNearMenMapActivity", "onCreate被执行");
	}

	/**
	 * 获取离线地图
	 */
	private void getOffLineMap() {
		mOfflineMap = new MKOfflineMap();
		mOfflineMap.init(new MKOfflineMapListener() {

			@Override
			public void onGetOfflineMapState(int type, int state) {
				switch (type) {
				case MKOfflineMap.TYPE_NEW_OFFLINE:
					Log.i("offlineMap", "下载了" + state + "个新离线地图");
					break;
				case MKOfflineMap.TYPE_DOWNLOAD_UPDATE:
					Log.i("offlineMap", "有离线地图可更新");
					break;
				case MKOfflineMap.TYPE_VER_UPDATE:
					Log.i("offlineMap", "有新版本可更新");
					break;
				default:
					break;
				}
			}
		});

	}

	public void downloadOfflineMap(int cityCode) {
		boolean flag = mOfflineMap.start(cityCode);
		if (flag) {
			Log.i("offlineMap", "下载完成");
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
	}

	private void initComponent() {

		add_task = (ImageView) findViewById(R.id.add_task);
		add_task.setOnClickListener(this);
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
		// 设置透明色
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

	/**
	 * 初始化定位信息
	 */
	private void initLocation() {

		mLocationClient = new LocationClient(getApplicationContext());
		mLocationListener = new MyLocationListener();
		mLocationClient.registerLocationListener(mLocationListener);
		// 配置定位器参数
		LocationClientOption option = new LocationClientOption();
		// option.setLocationMode(LocationMode.Hight_Accuracy);//设置定位模式
		// option.setNeedDeviceDirect(true);//返回的定位结果包含手机机头的方向
		option.setCoorType("bd09ll");// 返回的定位结果是百度经纬度,默认值gcj02
		option.setIsNeedAddress(true);// 返回的定位结果包含地址信息
		option.setOpenGps(true);
		option.setScanSpan(1000 * 60);// 每隔1分钟发一次定位请求
		mLocationClient.setLocOption(option);

		// 给方向箭头添加方向传感器监听
		myOrientationListener = new MyOrientationListener(context);

		myOrientationListener
				.setOnOrientationListener(new com.science.strangertofriend.listener.MyOrientationListener.OnOrientationListener() {

					@Override
					public void onOrientationChanged(float x) {
						currentX = x;
					}
				});

		// 定位到我的位置
		mMapLocation.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				centerToMyLocation();
			}
		});

	}

	private class MyLocationListener implements BDLocationListener {

		@Override
		public void onReceiveLocation(BDLocation location) {
			
			
			MyLocationData data = new MyLocationData.Builder()
					.direction(currentX).accuracy(location.getRadius())
					.latitude(location.getLatitude())
					.longitude(location.getLongitude()).build();
			mBaiduMap.setMyLocationData(data);

			// 获取城市code
			cityCode = location.getCityCode();
			getOffLineMap();
			downloadOfflineMap(Integer.parseInt(cityCode));
			// 自定义方向箭头
			mIconLocation = BitmapDescriptorFactory
					.fromResource(R.drawable.navi_map_gps_locked);

			// 设置自定义图标
			MyLocationConfiguration configuration = new MyLocationConfiguration(
					LocationMode.NORMAL, true, mIconLocation);
			mBaiduMap.setMyLocationConfigeration(configuration);
			
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


				// 查找附近1000米的人
				// findMenNearby();
				// findTaskNearBy();
				findTaskNearBy();
			}
		}
	}

	

	/**
	 * 查找附近的任务
	 */
	public void findTaskNearBy() {
		taskNearBy.clear();
		// initMarker();
		new Thread(new Runnable() {

			@Override
			public void run() {
				AVQuery<AVObject> query = new AVQuery<>("Task");
				// 查找附近10km内的任务
				query.include("pub_user.userAvater");
				query.whereNotEqualTo("isAccomplished", true);
				query.whereWithinKilometers("geoPoint", mMyPoint, 10);
				// query.whereNotEqualTo("publisherName", mUsername);
				try {
					List<AVObject> taskList = query.find();
					Task taskBean = null;
					for (AVObject task : taskList) {
						taskBean = new Task();
						taskBean.setPublisherName(task
								.getString("publisherName"));
						taskBean.setEndTime(task.getString("endTime"));
						taskBean.setPrice(task.getString("price"));
						taskBean.setTheme(task.getString("theme"));
						taskBean.setTaskDescription(task
								.getString("TaskDescription"));
						taskBean.setLatitude(task.getAVGeoPoint("geoPoint")
								.getLatitude());
						taskBean.setLongitude(task.getAVGeoPoint("geoPoint")
								.getLongitude());
						taskBean.setLocation(task.getString("location"));
						taskBean.setType(task.getString("service_type"));
						taskBean.setAccepted(task.getBoolean("isAccepted"));
						taskBean.setAccomplished(task
								.getBoolean("isAccomplished"));
						taskBean.setPub_user(task.getAVUser("pub_user"));
						taskBean.setCredits(task.getInt("credits"));
						taskNearBy.add(taskBean);
						// 获取头像url
						AVUser user = (AVUser) task.getAVUser("pub_user");
						if (null != user&&null!=user.getAVFile("userAvater")) {
							String avaterUrl = user.getAVFile("userAvater")
									.getUrl();
							hash_avaterUrls.put(taskBean.getPublisherName(),
									avaterUrl);
						}
					}
					//showAllMarkersOnMap();
					//loadAllAvaters();
				} catch (AVException e) {
					e.printStackTrace();
				}
				
				Log.i("TAG", "taskNearBy大小为："+taskNearBy.size());
				Log.i("TAG", "hash_avaterUrls大小为："+hash_avaterUrls.size());
				for(int i=0;i<taskNearBy.size();i++){
					String name=taskNearBy.get(i).getPublisherName();
					downloadAvaterBitmaps(name, hash_avaterUrls.get(name));
				}
				
			}
		}).start();
		
		
	}
	
	/**
	 * 
	 * @param username  用户名
	 * @param url   头像url
	 */
	public void downloadAvaterBitmaps(final String username,final String url){
		DisplayImageOptions option = new DisplayImageOptions.Builder()
		.showImageForEmptyUri(R.drawable.default_load)// 设置图片Uri为空或是错误的时候显示的图片
		.showImageOnFail(R.drawable.default_load)// 设置图片加载或解码过程中发生错误显示的图片
		.bitmapConfig(Bitmap.Config.RGB_565).build();
		//String urlString=hash_avaterUrls.get(taskNearBy.get(i).getPublisherName());
		ImageLoader.getInstance().loadImage(url, option, new ImageLoadingListener() {
			@Override
			public void onLoadingStarted(String arg0, View arg1) {
			}
			@Override
			public void onLoadingFailed(String arg0, View arg1, FailReason arg2) {
			}
			@Override
			public void onLoadingComplete(String arg0, View arg1, Bitmap arg2) {
				bitMaps.put(username, arg2);
				Log.i("TAG", "downloadAvaterBitmaps");
				if(bitMaps.size()==hash_avaterUrls.size()){
					showAllMarkersOnMap();
					Log.i("TAG", "加载完成");
				}
			}
			@Override
			public void onLoadingCancelled(String arg0, View arg1) {
				
			}
		});
	}
	
	
	/**
	 * 将所有任务以发布人头像显示在地图上
	 */
	private void showAllMarkersOnMap() {
		mBaiduMap.clear();
		Bitmap bitmap = null;
		 Marker marker = null;
		LatLng latLng = null;
		 OverlayOptions options;
		for (int i = 0; i < taskNearBy.size(); i++) {
			initMarker();
			latLng = new LatLng(taskNearBy.get(i).getLatitude(), taskNearBy
					.get(i).getLongitude());
			
			if (bitMaps.get(taskNearBy.get(i).getPublisherName()) != null) {

				bitmap = Bitmap.createBitmap(bitMaps.get(taskNearBy.get(i)
						.getPublisherName()));
			} else {
				bitmap = BitmapFactory.decodeResource(getResources(),
						R.drawable.default_user_img);
			}
			circleImageView.setImageBitmap(bitmap);
			circleImageView.setImageAlpha(0);
			mMarkDescriptor=BitmapDescriptorFactory.fromView(circleImageView);
			if(mMarkDescriptor!=null){
						options = new MarkerOptions().position(latLng)
								.icon(mMarkDescriptor).zIndex(5);
				marker = (Marker) mBaiduMap.addOverlay(options);
			}else {
				Log.i("TAG", "加载头像出错");
			}
			Bundle arg0 = new Bundle();
			arg0.putSerializable("info", taskNearBy.get(i));
			marker.setExtraInfo(arg0);
		}

		MapStatusUpdate msu = MapStatusUpdateFactory.newLatLng(latLng);
		mBaiduMap.setMapStatus(msu);
	}

	

	private void initMarker() {

		view = LayoutInflater.from(context).inflate(R.layout.circleimage, null);
		circleImageView = (CircleImageView) view.findViewById(R.id.avatar);
	}

	
	private void setMarkerClickListener() {

		mBaiduMap.setOnMarkerClickListener(new OnMarkerClickListener() {

			@Override
			public boolean onMarkerClick(Marker marker) {
				// D改的 这个地方是传送一些信息到DetailTaskActivity
				Bundle taskinfo = marker.getExtraInfo();
				Task task = (Task) taskinfo.get("info");
				Intent intent = new Intent(ShowNearMenMapActivity.this,
						DetailedTaskActivity.class);
				bitMaps.get(task.getPublisherName());
				intent.putExtra("bitmap", bitMaps.get(task.getPublisherName()));
				intent.putExtra("theme", task.getTheme());
				intent.putExtra("publisherName", task.getPublisherName());
				intent.putExtra("type", task.getType());
				intent.putExtra("taskDescription", task.getTaskDescription());
				intent.putExtra("location", task.getLocation());
				intent.putExtra("price", task.getPrice());
				intent.putExtra("endtime", task.getEndTime());
				intent.putExtra("isAccepted", task.isAccepted());
				intent.putExtra("isAccomplished", task.isAccomplished());
				intent.putExtra("credits", task.getCredits());

				// 将AVUser传递过去
				intent.putExtra("pub_user", task.getPub_user().toString());

				startActivity(intent);
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
		int itemId = item.getItemId();
		if(itemId == R.id.map_common){
			mBaiduMap.setMapType(BaiduMap.MAP_TYPE_NORMAL);

		}else if(itemId ==R.id.map_site){
			mBaiduMap.setMapType(BaiduMap.MAP_TYPE_SATELLITE);

		}else if (itemId == R.id.map_traffic){
			if (mBaiduMap.isTrafficEnabled()) {
				mBaiduMap.setTrafficEnabled(false);
				item.setTitle("实时地图(off)");
			} else {
				mBaiduMap.setTrafficEnabled(true);
				item.setTitle("实时地图(on)");
			}
		}

		return super.onOptionsItemSelected(item);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		// 在activity执行onDestroy时执行mMapView.onDestroy()，实现地图生命周期管理
		mMapView.onDestroy();
		mOfflineMap.destroy();
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

		// 开启方向传感器
		myOrientationListener.start();
	}

	@Override
	protected void onStop() {
		super.onStop();
		// 停止定位
		mBaiduMap.setMyLocationEnabled(false);
		mLocationClient.stop();

		// 停止方向传感器
		myOrientationListener.stop();
	}

	@Override
	protected void onPause() {
		super.onPause();
		// 在activity执行onPause时执行mMapView. onPause ()，实现地图生命周期管理
		mMapView.onPause();
	}

	/**
	 * 添加任务
	 * 
	 * @param v
	 */
	@Override
	public void onClick(View v) {
		Intent intent = new Intent(this, AddTaskActivity.class);
		startActivity(intent);
	}

	public static double getLatitude() {
		return mLatitude;
	}

	public static double getLongitude() {
		return mLongtitude;
	}
	
}