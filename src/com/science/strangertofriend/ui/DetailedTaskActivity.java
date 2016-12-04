package com.science.strangertofriend.ui;

import java.util.List;

import cn.pedant.SweetAlert.SweetAlertDialog;
import cn.pedant.SweetAlert.SweetAlertDialog.OnSweetClickListener;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVInstallation;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVPush;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.FindCallback;
import com.avos.avoscloud.GetCallback;
import com.avos.avoscloud.SaveCallback;
import com.avos.avoscloud.SendCallback;
import com.avos.avoscloud.im.v2.AVIMClient;
import com.avos.avoscloud.im.v2.callback.AVIMClientCallback;
import com.science.strangertofriend.R;
import com.science.strangertofriend.TaskType;
import com.science.strangertofriend.bean.Task;

import de.hdodenhof.circleimageview.CircleImageView;
import android.R.bool;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.BaseBundle;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class DetailedTaskActivity extends Activity implements OnClickListener {

	private AVUser currentUser;
	private ImageView back, publisherAvaterImage;
	private TextView taskTheme, taskType, taskPrice, taskDescription,
			taskPubliName, taskLocation, taskEndTime;
	private Button contactTaskPeopleBtn, acceptTaskBtn;
	private Intent intent;

	private CircleImageView mCircleImageView;
	private Task task;// 从地图传过来的task对象，可用于获取发布人的AVUser对象，其他的都通过intent传递过来了
	private AVUser pub_user;// 任务发布人的AVUser对象
	private String selfId = "";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_detailed_task);
		intent = getIntent();
		currentUser = AVUser.getCurrentUser();

		init();

		if (isTaskAccepted()) {
			acceptTaskBtn.setText("任务已被接受了哦");
		}

	}

	@Override
	public void onClick(View v) {
		int id = v.getId();
		if (id == R.id.back_img) {
			finish();
		} else if (id == R.id.contactTaskPeopleBtn) {
			Intent chatIntent = new Intent(DetailedTaskActivity.this,
					chatActivity.class);
			chatIntent.putExtra("taskPubliName",
					intent.getStringExtra("publisherName"));
			startActivity(chatIntent);
		} else if (id == R.id.acceptTaskBtn) {
			if (!isTaskAccepted()) {
				final SweetAlertDialog dialog = new SweetAlertDialog(
						DetailedTaskActivity.this,
						SweetAlertDialog.WARNING_TYPE);
				dialog.setTitleText("");
				dialog.setContentText("确定接收任务？");
				dialog.setCancelable(true);
				dialog.setCancelText("取消");
				dialog.setConfirmClickListener(new OnSweetClickListener() {

					@Override
					public void onClick(SweetAlertDialog sweetAlertDialog) {
						if (!isTaskBelongToMyself()) {
							acceptTask();
						} else {
							new SweetAlertDialog(DetailedTaskActivity.this,
									SweetAlertDialog.ERROR_TYPE)
									.setTitleText("Sorry")
									.setContentText("不可以接收自己的任务哦").show();
						}
						dialog.cancel();
					}
				});
				dialog.setCancelClickListener(new OnSweetClickListener() {

					@Override
					public void onClick(SweetAlertDialog sweetAlertDialog) {
						dialog.cancel();
					}
				});
				dialog.show();
			} else {
				new SweetAlertDialog(DetailedTaskActivity.this,
						SweetAlertDialog.ERROR_TYPE).setTitleText("sorry")
						.setContentText("任务已经被接受了哦，下次早点哦！").show();
				acceptTaskBtn.setText("任务已被接受");
			}
		}
   	}

	public void init() {
		mCircleImageView = (CircleImageView) findViewById(R.id.avatar);
		back = (ImageView) findViewById(R.id.back_img);
		// publisherAvaterImage = (ImageView)
		// findViewById(R.id.publisherAvaterImage);

		taskTheme = (TextView) findViewById(R.id.taskTheme);
		taskType = (TextView) findViewById(R.id.taskType);
		taskPrice = (TextView) findViewById(R.id.taskPrice);
		taskDescription = (TextView) findViewById(R.id.taskDescription);
		taskPubliName = (TextView) findViewById(R.id.taskPubliName);
		taskLocation = (TextView) findViewById(R.id.taskLocation);
		taskEndTime = (TextView) findViewById(R.id.taskEndTime);

		contactTaskPeopleBtn = (Button) findViewById(R.id.contactTaskPeopleBtn);
		acceptTaskBtn = (Button) findViewById(R.id.acceptTaskBtn);

		contactTaskPeopleBtn.setOnClickListener(this);
		acceptTaskBtn.setOnClickListener(this);
		back.setOnClickListener(this);

		taskTheme.append(intent.getStringExtra("theme"));
		// taskType.append(intent.getStringExtra("type"));
		taskPrice.append(intent.getStringExtra("price"));
		taskDescription.append(intent.getStringExtra("taskDescription"));
		taskPubliName.append(intent.getStringExtra("publisherName"));
		taskLocation.append(intent.getStringExtra("location"));
		taskEndTime.append(intent.getStringExtra("endtime"));
		// publisherAvaterImage.setImageBitmap((Bitmap)intent.getParcelableExtra("bitmap"));
		mCircleImageView.setImageBitmap((Bitmap) intent
				.getParcelableExtra("bitmap"));
		showTaskType();
	}

	/*
	 * 接收任务，向服务器数据库保存数据
	 */
	public void acceptTask() {
		AVQuery<AVObject> taskQuery = new AVQuery<AVObject>("Task");
		taskQuery.whereEqualTo("publisherName",
				intent.getStringExtra("publisherName"));
		taskQuery.whereEqualTo("theme", intent.getStringExtra("theme"));
		taskQuery.findInBackground(new FindCallback<AVObject>() {

			@Override
			public void done(List<AVObject> arg0, AVException arg1) {
				if (arg1 == null) {
					// arg0.get(0).put("State", "任务中");
					AVObject currentTask = arg0.get(arg0.size() - 1);
					currentTask.put("acceptedUser", currentUser);
					currentTask.put("isAccepted", true);
					currentTask.put("acceptedName", currentUser.getUsername());
					currentTask.saveInBackground();
					// String pub_user = currentTask.get("pub_user").toString();
					// androidPush(pub_user);// 推送
					AVObject pub_user = currentTask.getAVObject("pub_user");
					String objectId = pub_user.getObjectId();
					// String objectId = pub_user.get("objectId").toString();
					// String int1 = pub_user.getString("objectId");
					// System.out.println("Objectint1:" + int1);
					androidPush(objectId);// 推送
				}
			}
		});
		acceptTaskBtn.setText("任务已接受");

		new SweetAlertDialog(DetailedTaskActivity.this,
				SweetAlertDialog.SUCCESS_TYPE).setTitleText("Good job!")
				.setContentText("接收成功").show();

	}

	/**
	 * 推送
	 */
	private void androidPush(String pub_user) {
		AVQuery<AVObject> query = new AVQuery<>("_User");
		query.getInBackground(pub_user, new GetCallback<AVObject>() {
			@Override
			public void done(AVObject user, AVException e) {
				// object 就是 id 为 558e20cbe4b060308e3eb36c 的 Todo 对象实例
				String installationId = user.getString("installationId");

				AVPush push = new AVPush();
				// 设置消息
				push.setMessage("你有新发布的任务被接收");
				// 设置查询条件，
				push.setQuery(AVInstallation.getQuery().whereEqualTo(
						"installationId", installationId));
				// 推送
				push.sendInBackground(new SendCallback() {
					@Override
					public void done(AVException e) {
						// Toast toast = null;
						if (e == null) {
							// toast = Toast.makeText(
							// getApplicationContext(),
							// "Send successfully.",
							// Toast.LENGTH_SHORT);
						} else {
							// toast = Toast.makeText(
							// getApplicationContext(),
							// "Send fails with :"
							// + e.getMessage(),
							// Toast.LENGTH_LONG);
						}
						// 放心大胆地show，我们保证 callback 运行在 UI 线程。
						// toast.show();
					}
				});
			}
		});

	}

	public void showTaskType() {
		String type = intent.getStringExtra("type");
		switch (type) {
		case TaskType.SERVICE_CATERING:
			taskType.append("餐饮服务");
			break;
		case TaskType.SERVICE_EDUCATION:
			taskType.append("教育服务");
			break;
		case TaskType.SERVICE_EXPRESS:
			taskType.append("物流服务");
			break;
		case TaskType.SERVICE_FIX:
			taskType.append("维修服务");
			break;
		case TaskType.SERVICE_HOUSEWORK:
			taskType.append("家政服务");
			break;
		case TaskType.SERVICE_INTERNET:
			taskType.append("网络服务");
			break;
		case TaskType.SERVICE_OTHERS:
			taskType.append("其它服务");
			break;
		case TaskType.SERVICE_ELDERLY:
			taskType.append("老人服务");
			break;
		default:
			break;
		}
	}

	/*
	 * 判断任务是否自己发的，自己不能接自己发的任务
	 */
	public boolean isTaskBelongToMyself() {
		String username = currentUser.getUsername();
		String pub_username = intent.getStringExtra("publisherName");
		Log.i("DetailedTaskActivity", "pub_username=" + pub_username);
		Log.i("DetailedTaskActivity", "username=" + username);
		if (null != pub_username && null != username) {
			if (!username.equals(pub_username)) {
				return false;
			} else {
				return true;
			}
		} else {
			throw new NullPointerException("任务发布人姓名为空");
		}
	}

	/*
	 * 判断任务是否已经被接受
	 */
	public boolean isTaskAccepted() {
		boolean isAccepted = intent.getBooleanExtra("isAccepted", false);
		return isAccepted;
	}

}
