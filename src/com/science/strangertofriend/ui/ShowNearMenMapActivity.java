package com.science.strangertofriend.ui;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
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

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVFile;
import com.avos.avoscloud.AVGeoPoint;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.FindCallback;
import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.SDKInitializer;
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
	
	//头像集合相关
	HashMap<String, BitmapDescriptor> avaterMarkers=new HashMap<String, BitmapDescriptor>();
	HashMap<String, Bitmap> bitMaps=new HashMap<>();
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
//		initMarker();
		setMarkerClickListener();
		Log.e("ShowNearMenMapActivity", "onCreate被执行");
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

				Toast.makeText(context, location.getAddrStr(),
						Toast.LENGTH_LONG).show();

				// 查找附近1000米的人
				// findMenNearby();
				// findTaskNearBy();
				Log.e("ShowNearMenMapActivity", "onReceiveLocation被执行");
				findTaskNearBy();
			}
		}
	}

	public void showAvaterOnMap(String username, String email) {
		// initMarker();
		AVQuery<AVObject> query = new AVQuery<>("Gender");
		if (LoginActivity.isEmail(email)) {
			query.whereEqualTo("email", email);
		} else {
			query.whereEqualTo("username", username);
		}
		query.findInBackground(showCircleAvaterByImageLoader());

	}

	/**
	 * 查找附近的服务
	 */
	public void findTaskNearBy() {
		taskNearBy.clear();
		// initMarker();
		new Thread(new Runnable() {

			@Override
			public void run() {
				AVQuery<AVObject> query = new AVQuery<>("Task");
				// 查找附近10km内的任务
				query.whereWithinKilometers("geoPoint", mMyPoint, 10);
				// query.whereNotEqualTo("publisherName", mUsername);
				try {
					List<AVObject> taskList = query.find();
					Task taskBean = null;
					for (AVObject task : taskList) {
						taskBean = new Task();
						taskBean.setPublisherName(task
								.getString("publisherName"));
						taskBean.setAccepted(false);
						taskBean.setAccomplished(false);
						taskBean.setEndTime(task.getString("endTime"));
						taskBean.setPrice(task.getString("price"));
						taskBean.setTheme(task.getString("theme"));
						taskBean.setTaskDescription(task
								.getString("TaskDescription"));
						taskBean.setType(task.getString("service_task"));
						taskBean.setLatitude(task.getAVGeoPoint("geoPoint")
								.getLatitude());
						taskBean.setLongitude(task.getAVGeoPoint("geoPoint")
								.getLongitude());
						taskBean.setLocation(task.getString("location"));
						taskBean.setType(task.getString("service_type"));
						taskNearBy.add(taskBean);
					}
//					mHandler.obtainMessage(1).sendToTarget();
					loadAllAvaters();
					Log.e("task", taskNearBy.size() + "");
					Log.e("task", taskNearBy.toString());
				} catch (AVException e) {
					e.printStackTrace();
				}
			}
		}).start();
		Log.e("ShowNearMenMapActivity", "findTaskNearBy被执行");

	}
	/**
	 * 异步加载所有的头像
	 */
	public void loadAllAvaters(){
		for(int i=0;i<taskNearBy.size();i++){
			AVService.getNearbyTaskAvaters(taskNearBy.get(i).getPublisherName());
			
			//主线程跑的太快，让他等子线程异步加载完头像信息，不然获取的头像都为空
			//sb线程跑的太快了，等等imageloader
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			Bitmap bitmap= AVService.getBitmap();
			if(bitmap!=null){
				bitMaps.put(taskNearBy.get(i).getPublisherName(),
						bitmap);
			}
		}
		Log.e("avaterMarkers", avaterMarkers.size()+"\n"+avaterMarkers.toString());
		Log.e("loadAllAvaters", "方法被执行");
		
		showAllMarkersOnMap();
	}
	/**
	 * 将所有任务以发布人头像显示在地图上
	 */
	private void showAllMarkersOnMap(){
		mBaiduMap.clear();
		Marker marker = null;
		LatLng latLng = null;
		OverlayOptions options;
		for(int i=0;i<taskNearBy.size();i++){
			latLng = new LatLng(taskNearBy.get(i).getLatitude(), taskNearBy.get(i).getLongitude());
//			options = new MarkerOptions().position(latLng)
//					.icon(avaterMarkers.get(taskNearBy.get(i).getPublisherName())).zIndex(5);
			initMarker();
			Bitmap bitmap=Bitmap.createBitmap(bitMaps.get(taskNearBy.get(i).getPublisherName()));
			circleImageView.setImageBitmap(bitmap);
			circleImageView.setImageAlpha(0);
			mMarkDescriptor = BitmapDescriptorFactory
					.fromView(circleImageView);
			options = new MarkerOptions().position(latLng)
					.icon(mMarkDescriptor).zIndex(5);
			marker = (Marker) mBaiduMap.addOverlay(options);
			marker = (Marker) mBaiduMap.addOverlay(options);
			Bundle arg0 = new Bundle();
			arg0.putSerializable("info", taskNearBy.get(i));
			marker.setExtraInfo(arg0);
		}
		
		MapStatusUpdate msu = MapStatusUpdateFactory.newLatLng(latLng);
		mBaiduMap.setMapStatus(msu);
	} 
	
	private Handler mHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case 1:
				addTaskOnMap();
				break;

			default:
				break;
			}
		};
	};

	private void initMarker() {

		 view = LayoutInflater.from(context).inflate(R.layout.circleimage,
		 null);
		 circleImageView = (CircleImageView) view.findViewById(R.id.avatar);
	}

	/**
	 * 添加任务到地图上，以发布任务人的头像显示在地图上
	 */
	public void addTaskOnMap() {
		mBaiduMap.clear();
		Marker marker = null;
		LatLng latLng = null;
		for (Task info : taskNearBy) {
			showAvaterOnMap(info.getPublisherName(), null);
			// 经纬度
			latLng = new LatLng(info.getLatitude(), info.getLongitude());
			// 图标
			
			if(mMarkDescriptor!=null){
			
				OverlayOptions options = new MarkerOptions().position(latLng)
						.icon(mMarkDescriptor).zIndex(5);
				marker = (Marker) mBaiduMap.addOverlay(options);
				Bundle arg0 = new Bundle();
				arg0.putSerializable("info", info);
				marker.setExtraInfo(arg0);
			}else {
				Log.e("mMarkDescriptor", "mMarkDescriptor为空");
			}
		}

		MapStatusUpdate msu = MapStatusUpdateFactory.newLatLng(latLng);
		mBaiduMap.setMapStatus(msu);

	}

	private void setMarkerClickListener() {

		mBaiduMap.setOnMarkerClickListener(new OnMarkerClickListener() {

			@Override
			public boolean onMarkerClick(Marker marker) {
				//D改的 这个地方是传送一些信息到DetailTaskActivity
				Bundle taskinfo = marker.getExtraInfo();
				Task task = (Task) taskinfo.get("info");
				Toast.makeText(ShowNearMenMapActivity.this, " "+task.toString(), 10).show();
				Intent intent = new Intent(ShowNearMenMapActivity.this,DetailedTaskActivity.class);
				bitMaps.get(task.getPublisherName());
				intent.putExtra("bitmap", bitMaps.get(task.getPublisherName()));
				intent.putExtra("theme", task.getTheme());
				intent.putExtra("publisherName", task.getPublisherName());
				intent.putExtra("type", task.getType());
				intent.putExtra("taskDescription",task.getTaskDescription());
				intent.putExtra("location", task.getLocation());
				intent.putExtra("price",task.getPrice());
				intent.putExtra("endtime", task.getEndTime());
				startActivity(intent);
				// final LocationMenList menList = (LocationMenList) extraInfo;
				// .getSerializable("menList");
				//
				// InfoWindow infoWindow;
				// final LatLng latLng = marker.getPosition();
				// TextView textView = new TextView(context);
				// textView.setBackgroundResource(R.drawable.location_tips);
				// textView.setPadding(23, 20, 20, 40);
				// textView.setTextColor(Color.WHITE);
				// textView.setText(menList.getUsername());
				//
				// infoWindow = new InfoWindow(BitmapDescriptorFactory
				// .fromView(textView), latLng, -45,
				// new OnInfoWindowClickListener() {
				//
				// @Override
				// public void onInfoWindowClick() {
				// // 解密游戏
				// decodeGame(menList.getUsername(),
				// menList.getLatitude(),
				// menList.getLongtitude(),
				// menList.getUserEmail(),
				// menList.getGender(),
				// menList.getLocationTime());
				// }
				// });
				// mBaiduMap.showInfoWindow(infoWindow);

				// InfoWindow infoWindow;
				// final LatLng latLng=marker.getPosition();

				// infoWindow=new
				// InfoWindow(BitmapDescriptorFactory.fromView(imageView),
				// latLng, -30, new OnInfoWindowClickListener() {
				//
				// @Override
				// public void onInfoWindowClick() {
//				Toast.makeText(context, task.getPublisherName(),
//						Toast.LENGTH_SHORT).show();
				// }
				// });
				// mBaiduMap.showInfoWindow(infoWindow);
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

	/**
	 * 后台加载头像并显示的回调接口
	 * 
	 * @return
	 */
	public FindCallback<AVObject> showCircleAvaterByImageLoader() {
		FindCallback<AVObject> findCallback = new FindCallback<AVObject>() {

			@Override
			public void done(List<AVObject> arg0, AVException arg1) {
				if (arg1 == null) {

					Message msg = Message.obtain();
					msg.what = 1;
					msg.obj = arg0;
					mUsernameHandler.sendMessage(msg);
				} else {
					// Toast.makeText(context, "任务加载失败，请检查网络",
					// Toast.LENGTH_LONG);
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
				// Log.e("avaterURL", imageFile.getUrl());
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
						.showImageForEmptyUri(R.drawable.default_load)// 设置图片Uri为空或是错误的时候显示的图片
						.showImageOnFail(R.drawable.default_load)// 设置图片加载或解码过程中发生错误显示的图片
						// .displayer(new RoundedBitmapDisplayer(30))// 设置成圆角图片
						.bitmapConfig(Bitmap.Config.RGB_565).build();
				// ImageLoader
				// .getInstance()
				// .displayImage(
				// avaterURL,
				// imageView, options);
				ImageLoader.getInstance().loadImage(avaterURL, options,
						new ImageLoadingListener() {

							@Override
							public void onLoadingStarted(String arg0, View arg1) {
								Log.e("ImageLoader", "onLoadingStarted");
							}

							@Override
							public void onLoadingFailed(String arg0, View arg1,
									FailReason arg2) {
								Log.e("ImageLoader", "onLoadingFailed");
							}

							@Override
							public void onLoadingComplete(String arg0,
									View arg1, Bitmap arg2) {
								// Log.e("mMarkDescriptor", mMarkDescriptor+"");
								// imageView.setImageBitmap(arg2);
								initMarker();
								Log.e("arg2", arg2+"");
								circleImageView.setImageBitmap(arg2);
								mMarkDescriptor = BitmapDescriptorFactory
										.fromView(circleImageView);

								Log.e("ImageLoader", "onLoadingComplete");
							}

							@Override
							public void onLoadingCancelled(String arg0,
									View arg1) {
								Log.e("ImageLoader", "onLoadingCancelled");
							}
						});
				Log.e("showAvater", "ImagerLoader运行了");
				break;

			default:
				break;
			}
		};
	};
}
