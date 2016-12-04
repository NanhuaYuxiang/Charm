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
	private Task task;// �ӵ�ͼ��������task���󣬿����ڻ�ȡ�����˵�AVUser���������Ķ�ͨ��intent���ݹ�����
	private AVUser pub_user;// ���񷢲��˵�AVUser����
	private String selfId = "";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_detailed_task);
		intent = getIntent();
		currentUser = AVUser.getCurrentUser();

		init();

		if (isTaskAccepted()) {
			acceptTaskBtn.setText("�����ѱ�������Ŷ");
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
				dialog.setContentText("ȷ����������");
				dialog.setCancelable(true);
				dialog.setCancelText("ȡ��");
				dialog.setConfirmClickListener(new OnSweetClickListener() {

					@Override
					public void onClick(SweetAlertDialog sweetAlertDialog) {
						if (!isTaskBelongToMyself()) {
							acceptTask();
						} else {
							new SweetAlertDialog(DetailedTaskActivity.this,
									SweetAlertDialog.ERROR_TYPE)
									.setTitleText("Sorry")
									.setContentText("�����Խ����Լ�������Ŷ").show();
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
						.setContentText("�����Ѿ���������Ŷ���´����Ŷ��").show();
				acceptTaskBtn.setText("�����ѱ�����");
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
	 * ������������������ݿⱣ������
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
					// arg0.get(0).put("State", "������");
					AVObject currentTask = arg0.get(arg0.size() - 1);
					currentTask.put("acceptedUser", currentUser);
					currentTask.put("isAccepted", true);
					currentTask.put("acceptedName", currentUser.getUsername());
					currentTask.saveInBackground();
					// String pub_user = currentTask.get("pub_user").toString();
					// androidPush(pub_user);// ����
					AVObject pub_user = currentTask.getAVObject("pub_user");
					String objectId = pub_user.getObjectId();
					// String objectId = pub_user.get("objectId").toString();
					// String int1 = pub_user.getString("objectId");
					// System.out.println("Objectint1:" + int1);
					androidPush(objectId);// ����
				}
			}
		});
		acceptTaskBtn.setText("�����ѽ���");

		new SweetAlertDialog(DetailedTaskActivity.this,
				SweetAlertDialog.SUCCESS_TYPE).setTitleText("Good job!")
				.setContentText("���ճɹ�").show();

	}

	/**
	 * ����
	 */
	private void androidPush(String pub_user) {
		AVQuery<AVObject> query = new AVQuery<>("_User");
		query.getInBackground(pub_user, new GetCallback<AVObject>() {
			@Override
			public void done(AVObject user, AVException e) {
				// object ���� id Ϊ 558e20cbe4b060308e3eb36c �� Todo ����ʵ��
				String installationId = user.getString("installationId");

				AVPush push = new AVPush();
				// ������Ϣ
				push.setMessage("�����·��������񱻽���");
				// ���ò�ѯ������
				push.setQuery(AVInstallation.getQuery().whereEqualTo(
						"installationId", installationId));
				// ����
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
						// ���Ĵ󵨵�show�����Ǳ�֤ callback ������ UI �̡߳�
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
			taskType.append("��������");
			break;
		case TaskType.SERVICE_EDUCATION:
			taskType.append("��������");
			break;
		case TaskType.SERVICE_EXPRESS:
			taskType.append("��������");
			break;
		case TaskType.SERVICE_FIX:
			taskType.append("ά�޷���");
			break;
		case TaskType.SERVICE_HOUSEWORK:
			taskType.append("��������");
			break;
		case TaskType.SERVICE_INTERNET:
			taskType.append("�������");
			break;
		case TaskType.SERVICE_OTHERS:
			taskType.append("��������");
			break;
		case TaskType.SERVICE_ELDERLY:
			taskType.append("���˷���");
			break;
		default:
			break;
		}
	}

	/*
	 * �ж������Ƿ��Լ����ģ��Լ����ܽ��Լ���������
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
			throw new NullPointerException("���񷢲�������Ϊ��");
		}
	}

	/*
	 * �ж������Ƿ��Ѿ�������
	 */
	public boolean isTaskAccepted() {
		boolean isAccepted = intent.getBooleanExtra("isAccepted", false);
		return isAccepted;
	}

}
