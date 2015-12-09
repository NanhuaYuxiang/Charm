package com.science.strangertofriend.ui;

import java.util.List;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.FindCallback;
import com.avos.avoscloud.im.v2.AVIMClient;
import com.avos.avoscloud.im.v2.callback.AVIMClientCallback;
import com.science.strangertofriend.R;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.BaseBundle;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class DetailedTaskActivity extends Activity implements OnClickListener{


	private ImageView back,publisherAvaterImage;
	private TextView taskTheme,taskType,taskPrice,taskDescription,taskPubliName,taskLocation,taskEndTime;
	private Button contactTaskPeopleBtn,acceptTaskBtn;
	private Intent intent;
	private String selfId="";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_detailed_task);
		intent = getIntent();
		init();
		
		
	}

	@Override
	public void onClick(View v) {
		switch(v.getId()){
			case R.id.back_img:
				finish();
				break;
			case R.id.contactTaskPeopleBtn:
				Intent chatIntent = new Intent(DetailedTaskActivity.this,ChatActivity.class);
				chatIntent.putExtra("taskPubliName", intent.getStringExtra("publisherName"));
				startActivity(chatIntent);
				break;
			case R.id.acceptTaskBtn:
				AVQuery<AVObject> taskQuery = new AVQuery<AVObject>("Task");
				taskQuery.whereEqualTo("publisherName", intent.getStringExtra("publisherName"));
				taskQuery.whereEqualTo("theme",intent.getStringExtra("theme") );
				taskQuery.findInBackground(new FindCallback<AVObject>() {
					
					@Override
					public void done(List<AVObject> arg0, AVException arg1) {
						// TODO Auto-generated method stub
						if(arg1==null){
							arg0.get(0).put("State", "任务中");
							arg0.get(0).saveInBackground();
						}
					}
				});
				acceptTaskBtn.setText("取消任务");
				break;
		}
	}

	public void init() {
		back = (ImageView) findViewById(R.id.back_img);
		publisherAvaterImage = (ImageView) findViewById(R.id.publisherAvaterImage);

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
		taskType.append(intent.getStringExtra("type"));
		taskPrice.append(intent.getStringExtra("price"));
		taskDescription.append(intent.getStringExtra("taskDescription"));
		taskPubliName.append(intent.getStringExtra("publisherName"));
		taskLocation.append(intent.getStringExtra("location"));
		taskEndTime.append(intent.getStringExtra("endtime"));
		publisherAvaterImage.setImageBitmap((Bitmap)intent.getParcelableExtra("bitmap"));
		
		AVUser user = AVUser.getCurrentUser();
		selfId = user.getString("username");

	}
}
