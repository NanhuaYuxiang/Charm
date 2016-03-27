package com.science.strangertofriend.widget;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.science.strangertofriend.ui.Task_List_Publish_Complete_ListView_Activity;

public class DataGetCompleteBroadcastReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		Intent intent2=new Intent(context, Task_List_Publish_Complete_ListView_Activity.class);
		intent2.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		intent2.putExtra("isFinished", intent.getStringExtra("isFinished"));
		context.startActivity(intent2);
	}

}
