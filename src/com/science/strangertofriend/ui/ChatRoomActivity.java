package com.science.strangertofriend.ui;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;

import com.avos.avoscloud.im.v2.messages.AVIMLocationMessage;
import com.avoscloud.leanchatlib.activity.ChatActivity;
import com.science.strangertofriend.AppManager;
import com.science.strangertofriend.MainActivity;

/**
 * @description �������
 * 
 * @author ����Science ������
 * @school University of South China
 * @email chentushen.science@gmail.com,274240671@qq.com
 * @2015-5-18
 * 
 */

public class ChatRoomActivity extends ChatActivity {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addLocationBtn.setVisibility(View.VISIBLE);
		// addLocationBtn.setVisibility(View.GONE);
		// ��activity���뵽AppManager��ջ��
		AppManager.getAppManager().addActivity(this);
	}

	@Override
	protected void onAddLocationButtonClicked(View v) {
		// toast("���������ת����ͼ���棬ѡȡ��ַ");
	}

	@Override
	protected void onLocationMessageViewClicked(
			AVIMLocationMessage locationMessage) {
		// toast("onLocationMessageViewClicked");
	}

	public void backImg() {
		super.backImg();
		backImg.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				if (getLastMessage() != null) {
					intentMessage();
				}
				finish();
			}

		});
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
			if (getLastMessage() != null) {
				intentMessage();
			}
			finish();
			return false;
		}
		return super.onKeyDown(keyCode, event);
	}

	private void intentMessage() {

		Intent intent = new Intent(ChatRoomActivity.this, MainActivity.class);
		intent.putExtra("messsage", getLastMessage());
		intent.putExtra("position", getIntent().getIntExtra("position", 0));
		Log.e("12ee3eee", "-----------------------wfeewdedew:"
				+ getIntent().getIntExtra("position", 0));
		setResult(-1, intent);
	}

}
