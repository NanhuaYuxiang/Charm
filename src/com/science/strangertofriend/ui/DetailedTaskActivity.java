package com.science.strangertofriend.ui;

import java.util.List;

import cn.pedant.SweetAlert.SweetAlertDialog;
import cn.pedant.SweetAlert.SweetAlertDialog.OnSweetClickListener;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.FindCallback;
import com.avos.avoscloud.SaveCallback;
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

public class DetailedTaskActivity extends Activity implements OnClickListener{

	private AVUser currentUser;
	private ImageView back,publisherAvaterImage;
	private TextView taskTheme,taskType,taskPrice,taskDescription,taskPubliName,taskLocation,taskEndTime;
	private Button contactTaskPeopleBtn,acceptTaskBtn;
	private Intent intent;
	private CircleImageView mCircleImageView;
	private Task task;//从地图传过来的task对象，可用于获取发布人的AVUser对象，其他的都通过intent传递过来了
	private AVUser pub_user;//任务发布人的AVUser对象
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_detailed_task);
		intent = getIntent();
		currentUser=AVUser.getCurrentUser();
		
		init();
		
		
	}

	@Override
	public void onClick(View v) {
		switch(v.getId()){
			case R.id.back_img:
				finish();
				break;
			case R.id.contactTaskPeopleBtn:
				Intent chatIntent = new Intent(DetailedTaskActivity.this,chatActivity.class);
				startActivity(chatIntent);
				break;
			case R.id.acceptTaskBtn:
				if(!isTaskAccepted()){
				final SweetAlertDialog dialog=new SweetAlertDialog(DetailedTaskActivity.this, SweetAlertDialog.WARNING_TYPE);
				dialog.setTitleText("");
				dialog.setContentText("确定接收任务？");
				dialog.setCancelable(true);
				dialog.setCancelText("取消");
				dialog.setConfirmClickListener(new OnSweetClickListener() {
					
					@Override
					public void onClick(SweetAlertDialog sweetAlertDialog) {
						if(!isTaskBelongToMyself()){
							acceptTask();
							//Toast.makeText(DetailedTaskActivity.this, "接受成功", Toast.LENGTH_LONG).show();
						}else {
							//Toast.makeText(DetailedTaskActivity.this, "不可以接受自己的任务哦", Toast.LENGTH_LONG).show();
							new SweetAlertDialog(DetailedTaskActivity.this,SweetAlertDialog.ERROR_TYPE)
							.setTitleText("Sorry")
							.setContentText("不可以接收自己的任务哦")
							.show();
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
				}else {
					new SweetAlertDialog(DetailedTaskActivity.this,SweetAlertDialog.ERROR_TYPE)
					.setTitleText("sorry")
					.setContentText("任务已经被接受了哦，下次早点哦！")
					.show();
					acceptTaskBtn.setText("任务已被接受");
				}
				break;
		}
	}

	public void init() {
		mCircleImageView=(CircleImageView) findViewById(R.id.avatar);
		back = (ImageView) findViewById(R.id.back_img);
		//publisherAvaterImage = (ImageView) findViewById(R.id.publisherAvaterImage);

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
		//taskType.append(intent.getStringExtra("type"));
		taskPrice.append(intent.getStringExtra("price"));
		taskDescription.append(intent.getStringExtra("taskDescription"));
		taskPubliName.append(intent.getStringExtra("publisherName"));
		taskLocation.append(intent.getStringExtra("location"));
		taskEndTime.append(intent.getStringExtra("endtime"));
//		publisherAvaterImage.setImageBitmap((Bitmap)intent.getParcelableExtra("bitmap"));
		mCircleImageView.setImageBitmap((Bitmap)intent.getParcelableExtra("bitmap"));
		showTaskType();
	}
	/*
	 * 向服务器数据库保存数据
	 */
	public void acceptTask(){
		AVQuery<AVObject> taskQuery = new AVQuery<AVObject>("Task");
		taskQuery.whereEqualTo("publisherName", intent.getStringExtra("publisherName"));
		taskQuery.whereEqualTo("theme",intent.getStringExtra("theme") );
		taskQuery.findInBackground(new FindCallback<AVObject>() {
			
			@Override
			public void done(List<AVObject> arg0, AVException arg1) {
				if(arg1==null){
					//arg0.get(0).put("State", "任务中");
					AVObject currentTask=arg0.get(arg0.size()-1);
					currentTask.put("acceptedUser", currentUser);
					currentTask.put("isAccepted", true);
					currentTask.put("acceptedName", currentUser.getUsername());
					currentTask.saveInBackground();
				}
			}
		});
		acceptTaskBtn.setText("任务已接受");
		
		new SweetAlertDialog(DetailedTaskActivity.this, SweetAlertDialog.SUCCESS_TYPE)
		.setTitleText("Good job!")
		.setContentText("接收成功")
		.show();
		
	}
	
	
	public void showTaskType(){
		String type=intent.getStringExtra("type");
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
			taskType.append("替他服务");
			break;
		default:
			break;
		}
	}
	/*
	 * 判断任务是否自己发的，自己不能接自己发的任务
	 */
	public boolean isTaskBelongToMyself(){
		String username=currentUser.getUsername();
		String pub_username=intent.getStringExtra("publisherName");
		Log.i("DetailedTaskActivity", "pub_username="+pub_username);
		Log.i("DetailedTaskActivity", "username="+username);
		if(null!=pub_username&&null!=username){
			if(!username.equals(pub_username)){
				return false;
			}else {
				return true;
			}
		}else {
			throw new NullPointerException("任务发布人姓名为空");
		}
	}
	/*
	 * 判断任务是否已经被接受
	 */
	public boolean isTaskAccepted(){
		boolean isAccepted=intent.getBooleanExtra("isAccepted", false);
		return isAccepted;
	}
	
	
}
