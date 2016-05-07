package com.science.strangertofriend.ui;

import com.avos.avoscloud.im.v2.AVIMClient;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;

public class NotificationBroadcastReceiver extends BroadcastReceiver{

	@Override
	public void onReceive(Context context, Intent intent) {
		// TODO Auto-generated method stub
		String otherClientName = intent.getStringExtra("otherClientId");
		String conversationId = intent.getStringExtra("conversation");
		AVIMClient client = AVIMClient.getInstance(otherClientName);
		if(client==null){
			gotoLoginActivity(context);
		}else{
			if(!TextUtils.isEmpty(conversationId)){
				gotoChatActivity(context);
			}
		}
	}
	
	public void gotoLoginActivity(Context context){
		Intent intent = new Intent(context,LoginActivity.class);
		context.startActivity(intent);
	}
	
	public void gotoChatActivity(Context context){
		Intent intent = new Intent(context,chatActivity.class);
		context.startActivity(intent);
	}

}
