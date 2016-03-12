package com.science.strangertofriend.ui;

import java.util.ArrayList;
import java.util.List;

import android.annotation.TargetApi;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import cn.pedant.SweetAlert.SweetAlertDialog;
import cn.pedant.SweetAlert.SweetAlertDialog.OnSweetClickListener;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVGeoPoint;
import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.SaveCallback;
import com.readystatesoftware.systembartint.SystemBarTintManager;
import com.science.strangertofriend.R;
import com.science.strangertofriend.TaskType;
import com.science.strangertofriend.adapter.MySpinnerAdapter.onItemClickListener;
import com.science.strangertofriend.bean.Task;
import com.science.strangertofriend.utils.AVService;
import com.science.strangertofriend.widget.RevealLayout;

public class AddTaskActivity extends BaseActivity implements OnClickListener {
	private AVUser currentUser;
	private RevealLayout mRevealLayout;
	private RelativeLayout mLayout, mAdd_task_layout;
	private ImageView mBackImg;// 返回按钮
	private TextView mTitle;// 标题
	private EditText theme, description, endTime, publishedLocation,
			publishedPrice;// 任务主题，任务描述，发布时间，发布地点，任务价格
	private String publisherName, latitude, longitude;// 发布人姓名，维度，经度
	// private ImageView img_task;//任务相关图片
	private boolean isAccepted, isaccomplished;// 是否被接受，是否被完成
	private Button bt_publish;// 发布按钮

	private RelativeLayout spinnner;// spinner的布局
	private TextView mSpinnerTitle;
	private List<String> task_types;
	private String service_type;// 服务类型

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.add_task);
		currentUser=AVUser.getCurrentUser();
		initView();
		initListener();
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

		// 左上角退出当前activity
		mBackImg.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				AddTaskActivity.this.finish();
			}
		});

		bt_publish.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (!theme.getText().toString().isEmpty()) {

					if (!description.getText().toString().isEmpty()) {

						if (!endTime.getText().toString().isEmpty()) {

							if (!publishedPrice.getText().toString().isEmpty()) {
								
								if(!mSpinnerTitle.getText().equals("请选择服务类型")){
									
									progressDialogShow();
								}else {
									Toast.makeText(AddTaskActivity.this, "请选择任务类型", Toast.LENGTH_SHORT).show();
								}
								

							} else {
								Toast.makeText(AddTaskActivity.this,
										"请填写任务香金数", Toast.LENGTH_SHORT).show();
							}

						} else {
							Toast.makeText(AddTaskActivity.this, "请填写任务截止时间",
									Toast.LENGTH_SHORT).show();
						}

					} else {
						Toast.makeText(AddTaskActivity.this, "请填写任务描述",
								Toast.LENGTH_SHORT).show();
					}

				} else {
					Toast.makeText(AddTaskActivity.this, "请填写任务主题",
							Toast.LENGTH_SHORT).show();
				}

				
			}

		});

		spinnner.setOnClickListener(this);

	}

	public void progressDialogShow() {
		final SweetAlertDialog pDialog = new SweetAlertDialog(
				AddTaskActivity.this, SweetAlertDialog.PROGRESS_TYPE)
				.setTitleText("拼命加载中");
		pDialog.show();
		pDialog.setCancelable(false);
		new CountDownTimer(800 * 4, 800) {
			public void onTick(long millisUntilFinished) {
				colorProgress(pDialog);
			}

			public void onFinish() {
				i = -1;
				pDialog.dismiss();
				publishTask();
			}
		}.start();
	}

	public void publishTask() {
		String themeString = theme.getText().toString();
		String descriptionString = description.getText().toString();
		String endTimeString = endTime.getText().toString();
		String locationString = publishedLocation.getText().toString();
		publisherName = AVUser.getCurrentUser().getUsername();
		String price = publishedPrice.getText().toString();

		double latitude = ShowNearMenMapActivity.getLatitude();
		double longitude = ShowNearMenMapActivity.getLongitude();
		AVGeoPoint geoPoint = new AVGeoPoint(latitude, longitude);
		AVService.addNewTask(currentUser,publisherName,"", themeString, descriptionString,
				endTimeString, geoPoint, locationString, price,
				service_type,false,false, new SaveCallback() {

					@Override
					public void done(AVException exception) {
						if (exception == null) {
							mHandler.obtainMessage(1).sendToTarget();
							Log.e("AddTaskActivity", "保存成功");
						} else {
							mHandler.obtainMessage(2).sendToTarget();
							Log.e("AddTaskActivity", "保存失败");
						}
					}
				});
	}

	private Handler mHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case 1:
				final SweetAlertDialog dialog = new SweetAlertDialog(
						AddTaskActivity.this, SweetAlertDialog.SUCCESS_TYPE);
				dialog.setTitleText("Good job");
				dialog.setContentText("Saved successfully");
				dialog.show();
				dialog.setCancelable(false);
				Log.e("AddTaskActivity", "保存成功");

				dialog.setConfirmText("确认");
				dialog.setConfirmClickListener(new OnSweetClickListener() {

					public void onClick(SweetAlertDialog sweetAlertDialog) {

						AddTaskActivity.this.finish();
						Intent intent=new Intent(AddTaskActivity.this,ShowNearMenMapActivity.class);
						startActivity(intent);
					}
				});
				break;
			case 2:
				Log.e("AddTaskActivity", "保存失败");
				final SweetAlertDialog dialogError = new SweetAlertDialog(
						AddTaskActivity.this, SweetAlertDialog.ERROR_TYPE);
				dialogError.setTitleText("sorry");
				dialogError.setConfirmText("Saved filed");
				dialogError.show();
				break;
			default:
				break;
			}
		};
	};

	@Override
	@TargetApi(19)
	public void initSystemBar() {
		super.initSystemBar();
		// 创建状态栏的管理实例
		SystemBarTintManager tintManager = new SystemBarTintManager(this);
		// 激活状态栏设置
		tintManager.setStatusBarTintEnabled(true);
		// 激活导航栏设置
		tintManager.setNavigationBarTintEnabled(true);
		// 设置一个颜色给系统栏
		tintManager.setTintColor(Color.parseColor("#f698b2"));
	}

	private void initView() {
		mRevealLayout = (RevealLayout) findViewById(R.id.reveal_layout);
		mAdd_task_layout = (RelativeLayout) findViewById(R.id.add_task_layout);
		mLayout = (RelativeLayout) findViewById(R.id.layout);
		mLayout.setBackgroundColor(Color.WHITE);

		mBackImg = (ImageView) findViewById(R.id.back_img);
		mTitle = (TextView) findViewById(R.id.title);
		mTitle.setText("发布新任务");

		theme = (EditText) findViewById(R.id.theme);
		description = (EditText) findViewById(R.id.description);
		endTime = (EditText) findViewById(R.id.time_task);
		publishedPrice = (EditText) findViewById(R.id.price);
		bt_publish = (Button) findViewById(R.id.distribute);
		publishedLocation = (EditText) findViewById(R.id.location);
		spinnner = (RelativeLayout) findViewById(R.id.rl_spinner);
		mSpinnerTitle = (TextView) findViewById(R.id.tv_text);
		task_types = new ArrayList<String>();
		task_types.add("家政服务");
		task_types.add("维修服务");
		task_types.add("物流服务");
		task_types.add("教育服务");
		task_types.add("餐饮服务");
		task_types.add("网络服务");
		task_types.add("其他服务");
	}

	/**
	 * spinner的点击监听
	 */
	@Override
	public void onClick(View v) {
		mAdd_task_layout.setVisibility(View.GONE);
		bt_publish.setVisibility(View.GONE);
		com.science.strangertofriend.utils.MyPopupWindow window = new com.science.strangertofriend.utils.MyPopupWindow(
				this, spinnner.getWidth(), task_types);
		window.showAsDropDown(spinnner, 0, 0);
		window.setOnItemClickListener(new onItemClickListener() {

			@Override
			public void click(int position, View view) {
				mAdd_task_layout.setVisibility(View.VISIBLE);
				bt_publish.setVisibility(View.VISIBLE);
				mSpinnerTitle.setText(task_types.get(position));
				// Toast.makeText(AddTaskActivity.this,
				// "你点击了第"+(position+1)+"项", Toast.LENGTH_SHORT).show();
				switch (position) {
				case 0:
					service_type = TaskType.SERVICE_HOUSEWORK;
					break;
				case 1:
					service_type = TaskType.SERVICE_FIX;
					break;
				case 2:
					service_type = TaskType.SERVICE_EXPRESS;

					break;
				case 3:
					service_type = TaskType.SERVICE_EDUCATION;
					break;
				case 4:
					service_type = TaskType.SERVICE_CATERING;
					break;
				case 5:
					service_type = TaskType.SERVICE_INTERNET;
					break;
				case 6:
					service_type = TaskType.SERVICE_OTHERS;
					break;
				default:

					break;
				}
			}
		});
	}

}
