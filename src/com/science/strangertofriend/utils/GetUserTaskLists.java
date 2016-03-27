package com.science.strangertofriend.utils;

import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.util.Log;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.AVUser;
import com.science.strangertofriend.adapter.Task_Accept_Complete_Adapter;
import com.science.strangertofriend.adapter.Task_Accept_UnComplete_Adapter;
import com.science.strangertofriend.adapter.Task_Publish_Complete_Adapter;
import com.science.strangertofriend.adapter.Task_Publish_UnComplete_Adapter;
import com.science.strangertofriend.bean.Task;
import com.science.strangertofriend.widget.DataGetCompleteBroadcastReceiver;

/**
 * 获取个人任务历史清单的工具类
 * 
 * @author lilin
 * @date 2015年11月12日 .下午10:58:34
 * @blog www.gaosililn.iteye.com
 * @email gaosi0812@gamil.com
 * @school usc
 *
 */
public class GetUserTaskLists {
	private HandlerThread handlerThread;
	private Context context;
	/**
	 * 当前用户名
	 */
	public static String myUserName = "";

	public GetUserTaskLists(Context context) {
		this.context=context;
		myUserName = getUserName();
		// 获取当前用户
		AVUser avUser = AVUser.getCurrentUser();
		// 创建HandlerThread 线程
		handlerThread = new HandlerThread("getUserTaskList");
		// 启动线程
		handlerThread.start();
		Handler handler = new Handler(handlerThread.getLooper()) {
			@Override
			public void handleMessage(Message msg) {
				super.handleMessage(msg);
				switch (msg.what) {
				case 1:// 刚开始获取数据
					getValues();
					break;
				case 2:// 实时监控数据

					break;
				default:

					break;
				}

			}
		};

		// 发送标志启动相应的操作
		handler.sendEmptyMessage(1);
	}

	/**
	 * 获取当前用户用户名
	 * 
	 * @return 返回用户名
	 */
	private String getUserName() {
		String username = null;
		AVUser currentUser = AVUser.getCurrentUser();
		if (currentUser != null) {
			username = currentUser.getUsername();
		} else {
			// 缓存用户对象为空时， 可打开用户注册界面…
		}
		return username;
	}

	
	/**
	 * 获取数据
	 */
	private void getValues() {
		while (true) {
			try {
				// 移除数据
				Task_Accept_Complete_Adapter.removeAll();
				Task_Accept_UnComplete_Adapter.removeAll();
				Task_Publish_UnComplete_Adapter.removeAll();
				Task_Publish_Complete_Adapter.removeAll();
				// 获取数据
				getValuesAtBinOfPub();
				getValuesAtBinOfAcce();
				// 10分钟更新一次
				Thread.sleep(600000);

			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	/**
	 * 一开始获取所发布的全部数据
	 */
	public void getValuesAtBinOfPub() {
		AVQuery<AVObject> query = new AVQuery<>("Task");
		query.whereEqualTo("publisherName", myUserName);
		Task taskBean = null;
		try {
			List<AVObject> tasks = query.find();
			int len = tasks.size();
			Log.i("GetUserTaskLists", len+"");
			for (int i = 0; i < len; i++) {
				AVObject task = tasks.get(i);
				taskBean = new Task();
				taskBean.setObjectId(task.getObjectId());
				taskBean.setPublisherName(task.getString("publisherName"));
				taskBean.setAcceptedName(task.getString("acceptedName"));
				taskBean.setAccepted(task.getBoolean("isAccepted"));
				boolean isAccomplished = task.getBoolean("isAccomplished");
				Log.i("GetUserTaskLists", taskBean.isAccepted()+"");
				Log.i("GetUserTaskLists","isAccomplished="+ isAccomplished);
				taskBean.setAccomplished(isAccomplished);
				taskBean.setEndTime(task.getString("endTime"));
				taskBean.setPrice(task.getString("price"));
				taskBean.setTheme(task.getString("theme"));
				taskBean.setTaskDescription(task.getString("TaskDescription"));
				taskBean.setLatitude(task.getAVGeoPoint("geoPoint")
						.getLatitude());
				taskBean.setLongitude(task.getAVGeoPoint("geoPoint")
						.getLongitude());
				taskBean.setLocation(task.getString("location"));
				taskBean.setType(task.getString("service_type"));
				// 加到相应的队列当中
				if (isAccomplished) {// 已经完成的
					// 将任务添加到需需的队列
					Task_Publish_Complete_Adapter.addVector(taskBean);
				} else {// 还未完成的
					// 将任务添加到所需的任务队列当中
					Task_Publish_UnComplete_Adapter.addVector(taskBean);
				}
				
				if(i==len-1){
					Intent intent=new Intent();
					intent.setAction("com.science.strangertofriend.action");
					intent.putExtra("isFinished", true);
					context.sendBroadcast(intent);
					Log.i("broadcast", "发送广播咯");
				}

			}

		} catch (AVException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 一开始获取所接收的任务的数据
	 */
	public void getValuesAtBinOfAcce() {
		AVQuery<AVObject> query = new AVQuery<>("Task");
		
		query.whereEqualTo("acceptedName", myUserName);
		Task taskBean = null;
		try {
			List<AVObject> tasks = query.find();
			int len = tasks.size();
			for (int i = 0; i < len; i++) {
				AVObject task = tasks.get(i);
				taskBean = new Task();
				taskBean.setObjectId(task.getObjectId());
				taskBean.setPublisherName(task.getString("publisherName"));
				taskBean.setAcceptedName(task.getString("acceptedName"));
				taskBean.setAccepted(task.getBoolean("isAccepted"));
				boolean isAccomplished = task.getBoolean("isAccomplished");
				taskBean.setAccomplished(isAccomplished);
				taskBean.setEndTime(task.getString("endTime"));
				taskBean.setPrice(task.getString("price"));
				taskBean.setTheme(task.getString("theme"));
				taskBean.setTaskDescription(task.getString("TaskDescription"));
				//taskBean.setType(task.getString("service_task"));
				taskBean.setLatitude(task.getAVGeoPoint("geoPoint")
						.getLatitude());
				taskBean.setLongitude(task.getAVGeoPoint("geoPoint")
						.getLongitude());
				taskBean.setLocation(task.getString("location"));
				taskBean.setType(task.getString("service_type"));
				// 加到相应的队列当中
				if (isAccomplished) {// 已经完成的
					// 将任务添加到需需的队列
					Task_Accept_Complete_Adapter.addVector(taskBean);
				} else {// 还未完成的
					// 将任务添加到所需的任务队列当中
					Task_Accept_UnComplete_Adapter.addVector(taskBean);
				}

			}

		} catch (AVException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 获取数据是否已发生改变
	 */
	public void getChangeValuesInPub() {

	}
	
}
