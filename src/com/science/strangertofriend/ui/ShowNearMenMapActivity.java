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
 * @description ��ͼ��ʾ����������
 * 
 * @author ����
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

	// ��λ���
	private LocationClient mLocationClient;
	private MyLocationListener mLocationListener;
	private boolean isFirstIn = true;
	private static double mLatitude;
	private static double mLongtitude;
	private ImageView mMapLocation;
	private AVGeoPoint mMyPoint;

	// ���������
	private BitmapDescriptor mMarkDescriptor;
	// �б�����
	private ArrayList<LocationMenList> mLocationMenList;

	// �Զ��嶨λͼ��
	private BitmapDescriptor mIconLocation;
	// ��λͼ�����
	private float currentX;
	private MyOrientationListener myOrientationListener;
	private ImageView add_task;// �������
	private List<Task> taskNearBy = new ArrayList<Task>();// �������ĸ������з�������������
	private CircleImageView circleImageView;
	
	//ͷ�񼯺����
	HashMap<String, BitmapDescriptor> avaterMarkers=new HashMap<String, BitmapDescriptor>();
	HashMap<String, Bitmap> bitMaps=new HashMap<>();
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// ��ʹ��SDK�����֮ǰ��ʼ��context��Ϣ������ApplicationContext
		// ע��÷���Ҫ��setContentView����֮ǰʵ��
		SDKInitializer.initialize(getApplicationContext());

		setContentView(R.layout.near_men_map);
		this.context = this;

		initComponent();
		initListener();
		// ��ʼ����λ
		initLocation();
//		initMarker();
		setMarkerClickListener();
		Log.e("ShowNearMenMapActivity", "onCreate��ִ��");
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
		// ��ȡ��ͼ�ؼ�����
		mMapView = (MapView) findViewById(R.id.bmapView);
		mBaiduMap = mMapView.getMap();
		// ���ذٶ�logo
		View child = mMapView.getChildAt(1);
		if (child != null
				&& (child instanceof ImageView || child instanceof ZoomControls)) {
			child.setVisibility(View.INVISIBLE);
		}
		// ��ͼ����200m
		MapStatusUpdate mapStatusUpdate = MapStatusUpdateFactory.zoomTo(16.0f);
		mBaiduMap.setMapStatus(mapStatusUpdate);
		// ��ȡ��λ�ҵ�λ�õ�ͼ��
		mMapLocation = (ImageView) findViewById(R.id.map_location);
		mLocationMenList = new ArrayList<LocationMenList>();
		mRevealLayout = (RevealLayout) findViewById(R.id.reveal_layout);
		mMapLayout = (FrameLayout) findViewById(R.id.map_layout);
		// ����͸��ɫ
		mMapLayout.setBackgroundColor(Color.TRANSPARENT);

		AVUser currentUser = AVUser.getCurrentUser();
		if (currentUser != null) {
			mUserEmail = currentUser.getEmail();
			mUsername = currentUser.getUsername();
			mGender = currentUser.getString("gender");
		} else {
			Toast.makeText(context, "����û�е�½�", Toast.LENGTH_LONG).show();
		}

	}

	private void initLocation() {

		mLocationClient = new LocationClient(getApplicationContext());
		mLocationListener = new MyLocationListener();
		mLocationClient.registerLocationListener(mLocationListener);
		// ���ö�λ������
		LocationClientOption option = new LocationClientOption();
		// option.setLocationMode(LocationMode.Hight_Accuracy);//���ö�λģʽ
		// option.setNeedDeviceDirect(true);//���صĶ�λ��������ֻ���ͷ�ķ���
		option.setCoorType("bd09ll");// ���صĶ�λ����ǰٶȾ�γ��,Ĭ��ֵgcj02
		option.setIsNeedAddress(true);// ���صĶ�λ���������ַ��Ϣ
		option.setOpenGps(true);
		option.setScanSpan(1000 * 60);// ÿ��1���ӷ�һ�ζ�λ����
		mLocationClient.setLocOption(option);

		// �������ͷ��ӷ��򴫸�������
		myOrientationListener = new MyOrientationListener(context);

		myOrientationListener
				.setOnOrientationListener(new com.science.strangertofriend.listener.MyOrientationListener.OnOrientationListener() {

					@Override
					public void onOrientationChanged(float x) {
						currentX = x;
					}
				});

		// ��λ���ҵ�λ��
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

			// �Զ��巽���ͷ
			mIconLocation = BitmapDescriptorFactory
					.fromResource(R.drawable.navi_map_gps_locked);

			// �����Զ���ͼ��
			MyLocationConfiguration configuration = new MyLocationConfiguration(
					LocationMode.NORMAL, true, mIconLocation);
			mBaiduMap.setMyLocationConfigeration(configuration);

			// ���¾�γ��
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

				// ���Ҹ���1000�׵���
				// findMenNearby();
				// findTaskNearBy();
				Log.e("ShowNearMenMapActivity", "onReceiveLocation��ִ��");
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
	 * ���Ҹ����ķ���
	 */
	public void findTaskNearBy() {
		taskNearBy.clear();
		// initMarker();
		new Thread(new Runnable() {

			@Override
			public void run() {
				AVQuery<AVObject> query = new AVQuery<>("Task");
				// ���Ҹ���10km�ڵ�����
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
		Log.e("ShowNearMenMapActivity", "findTaskNearBy��ִ��");

	}
	/**
	 * �첽�������е�ͷ��
	 */
	public void loadAllAvaters(){
		for(int i=0;i<taskNearBy.size();i++){
			AVService.getNearbyTaskAvaters(taskNearBy.get(i).getPublisherName());
			
			//���߳��ܵ�̫�죬���������߳��첽������ͷ����Ϣ����Ȼ��ȡ��ͷ��Ϊ��
			//sb�߳��ܵ�̫���ˣ��ȵ�imageloader
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
		Log.e("loadAllAvaters", "������ִ��");
		
		showAllMarkersOnMap();
	}
	/**
	 * �����������Է�����ͷ����ʾ�ڵ�ͼ��
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
	 * ������񵽵�ͼ�ϣ��Է��������˵�ͷ����ʾ�ڵ�ͼ��
	 */
	public void addTaskOnMap() {
		mBaiduMap.clear();
		Marker marker = null;
		LatLng latLng = null;
		for (Task info : taskNearBy) {
			showAvaterOnMap(info.getPublisherName(), null);
			// ��γ��
			latLng = new LatLng(info.getLatitude(), info.getLongitude());
			// ͼ��
			
			if(mMarkDescriptor!=null){
			
				OverlayOptions options = new MarkerOptions().position(latLng)
						.icon(mMarkDescriptor).zIndex(5);
				marker = (Marker) mBaiduMap.addOverlay(options);
				Bundle arg0 = new Bundle();
				arg0.putSerializable("info", info);
				marker.setExtraInfo(arg0);
			}else {
				Log.e("mMarkDescriptor", "mMarkDescriptorΪ��");
			}
		}

		MapStatusUpdate msu = MapStatusUpdateFactory.newLatLng(latLng);
		mBaiduMap.setMapStatus(msu);

	}

	private void setMarkerClickListener() {

		mBaiduMap.setOnMarkerClickListener(new OnMarkerClickListener() {

			@Override
			public boolean onMarkerClick(Marker marker) {
				//D�ĵ� ����ط��Ǵ���һЩ��Ϣ��DetailTaskActivity
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
				// // ������Ϸ
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
		intent.putExtra("receiveUser", receiveUser); // ������֤��
		intent.putExtra("sendUsername", mUsername); // ������֤��(��ǰ�û�)
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
	 * ��λ���ҵ�λ��
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
				item.setTitle("ʵʱ��ͼ(off)");
			} else {
				mBaiduMap.setTrafficEnabled(true);
				item.setTitle("ʵʱ��ͼ(on)");
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
		// ��activityִ��onDestroyʱִ��mMapView.onDestroy()��ʵ�ֵ�ͼ�������ڹ���
		mMapView.onDestroy();
	}

	@Override
	protected void onResume() {
		super.onResume();
		// ��activityִ��onResumeʱִ��mMapView. onResume ()��ʵ�ֵ�ͼ�������ڹ���
		mMapView.onResume();
	}

	@Override
	protected void onStart() {
		super.onStart();
		// ��ʼ��λ
		mBaiduMap.setMyLocationEnabled(true);
		if (!mLocationClient.isStarted()) {
			mLocationClient.start();
		}
		mLocationClient.requestLocation();

		// �������򴫸���
		myOrientationListener.start();
	}

	@Override
	protected void onStop() {
		super.onStop();
		// ֹͣ��λ
		mBaiduMap.setMyLocationEnabled(false);
		mLocationClient.stop();

		// ֹͣ���򴫸���
		myOrientationListener.stop();
	}

	@Override
	protected void onPause() {
		super.onPause();
		// ��activityִ��onPauseʱִ��mMapView. onPause ()��ʵ�ֵ�ͼ�������ڹ���
		mMapView.onPause();
	}

	/**
	 * �������
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
	 * ��̨����ͷ����ʾ�Ļص��ӿ�
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
					// Toast.makeText(context, "�������ʧ�ܣ���������",
					// Toast.LENGTH_LONG);
					Log.e("avaterURL", "��������avaterΪnull");
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
						.showImageForEmptyUri(R.drawable.default_load)// ����ͼƬUriΪ�ջ��Ǵ����ʱ����ʾ��ͼƬ
						.showImageOnFail(R.drawable.default_load)// ����ͼƬ���ػ��������з���������ʾ��ͼƬ
						// .displayer(new RoundedBitmapDisplayer(30))// ���ó�Բ��ͼƬ
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
				Log.e("showAvater", "ImagerLoader������");
				break;

			default:
				break;
			}
		};
	};
}
