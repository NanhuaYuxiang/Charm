package com.science.strangertofriend.ui;

import android.annotation.TargetApi;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import cn.pedant.SweetAlert.SweetAlertDialog;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.SaveCallback;
import com.avoscloud.leanchatlib.utils.Utils;
import com.readystatesoftware.systembartint.SystemBarTintManager;
import com.science.strangertofriend.R;
import com.science.strangertofriend.utils.AVService;
import com.science.strangertofriend.widget.RevealLayout;

public class AddTaskActivity extends BaseActivity {
	private RevealLayout mRevealLayout;
	private RelativeLayout mLayout;
	private ImageView mBackImg;// 返回按钮
	private TextView mTitle;// 标题
	private EditText theme, description, endTime, publishedLocation,
			publishedPrice;// 任务主题，任务描述，发布时间，发布地点，任务价格
	private String publisherName, latitude, longitude;// 发布人姓名，维度，经度
	// private ImageView img_task;//任务相关图片
	private boolean isAccepted, isaccomplished;// 是否被接受，是否被完成
	private Button bt_publish;// 发布按钮

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.add_task);
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
				// if (!theme.getText().toString().isEmpty()) {
				//
				// if (!description.getText().toString().isEmpty()) {
				//
				// if (!endTime.getText().toString().isEmpty()) {
				//
				// if (!publishedPrice.getText().toString().isEmpty()) {
				// // progressDialogShow();
				// // publishTask();
				// AVObject test = new AVObject("test");
				// test.put("test1", "33333");
				// test.put("test2", "444444");
				//
				// test.saveInBackground(new SaveCallback() {
				//
				// @Override
				// public void done(AVException arg0) {
				// if(arg0==null){
				// Toast.makeText(AddTaskActivity.this, "OK",
				// Toast.LENGTH_SHORT);
				// }else {
				//
				// Toast.makeText(AddTaskActivity.this, "failed",
				// Toast.LENGTH_SHORT);
				// }
				// }
				// });
				//
				// } else {
				// Toast.makeText(AddTaskActivity.this,
				// "请填写任务香金数", Toast.LENGTH_SHORT).show();
				// }
				//
				// } else {
				// Toast.makeText(AddTaskActivity.this, "请填写任务截止时间",
				// Toast.LENGTH_SHORT).show();
				// }
				//
				// } else {
				// Toast.makeText(AddTaskActivity.this, "请填写任务描述",
				// Toast.LENGTH_SHORT).show();
				// }
				//
				// } else {
				// Toast.makeText(AddTaskActivity.this, "请填写任务主题",
				// Toast.LENGTH_SHORT).show();
				// }
				
				Log.e("AddTaskActivity", "bt is clicked");
				Log.e("AddTaskActivity", theme.getText().toString());
				Log.e("AddTaskActivity", endTime.getText().toString());
				Log.e("AddTaskActivity", publishedLocation.getText().toString());
				Log.e("AddTaskActivity", publishedPrice.getText().toString());
				
				
//				if (!theme.getText().toString().isEmpty()
//						&& !description.getText().toString().isEmpty()
//						&& !endTime.getText().toString().isEmpty()
//						&& !publishedLocation.getText().toString().isEmpty()
//						&& !publishedPrice.getText().toString().isEmpty()) {
					Log.e("if", "无空值");
					AVObject test = new AVObject("test");
					test.put("test1", "555555555");
					test.put("test2", "666666666");
					
					test.saveInBackground(new SaveCallback() {
						
						@Override
						public void done(AVException arg0) {
							if (arg0 == null) {
								Toast.makeText(AddTaskActivity.this, "OK",
										Toast.LENGTH_SHORT);
							} else {
								
								Toast.makeText(AddTaskActivity.this, "failed",
										Toast.LENGTH_SHORT);
							}
						}
					});
//				}else {
//					Toast.makeText(AddTaskActivity.this, "请填写完整任务信息", Toast.LENGTH_SHORT).show();
//				}
////
			}

		});

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
		String lacationString = publishedLocation.getText().toString();
		publisherName = AVUser.getCurrentUser().getUsername();
		String price = publishedPrice.getText().toString();
		AVService.addNewTask(publisherName, endTimeString, 30.000, 40.000,
				price, new SaveCallback() {

					@Override
					public void done(AVException exception) {
						if (exception == null) {
							// mHandler.obtainMessage(1).sendToTarget();
							Log.e("AddTaskActivity", "保存成功");
						} else {
							// mHandler.obtainMessage(2).sendToTarget();
							Log.e("AddTaskActivity", "保存失败");
						}
					}
				});
	}

	private Handler mHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case 1:
				AddTaskActivity.this.finish();
				final SweetAlertDialog dialog = new SweetAlertDialog(
						AddTaskActivity.this, SweetAlertDialog.SUCCESS_TYPE);
				dialog.setTitleText("Good job");
				dialog.setConfirmText("Saved successfully");
				dialog.show();
				dialog.setCancelable(false);
				Log.e("AddTaskActivity", "保存成功");
				new CountDownTimer(800 * 4, 800) {
					public void onTick(long millisUntilFinished) {
						colorProgress(dialog);
					}

					public void onFinish() {
						i = -1;
						dialog.dismiss();
					}
				}.start();
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
		mLayout = (RelativeLayout) findViewById(R.id.layout);
		mLayout.setBackgroundColor(Color.WHITE);

		mBackImg = (ImageView) findViewById(R.id.back_img);
		mTitle = (TextView) findViewById(R.id.title);
		mTitle.setText("发布新任务");

		theme = (EditText) findViewById(R.id.theme);
		description = (EditText) findViewById(R.id.description);
		endTime = (EditText) findViewById(R.id.time);
		publishedPrice = (EditText) findViewById(R.id.price);
		bt_publish = (Button) findViewById(R.id.distribute);
		publishedLocation=(EditText) findViewById(R.id.location);
	}

}
