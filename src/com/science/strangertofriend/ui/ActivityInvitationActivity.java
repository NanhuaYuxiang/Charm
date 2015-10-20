package com.science.strangertofriend.ui;

import java.util.ArrayList;

import android.annotation.TargetApi;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewTreeObserver;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.readystatesoftware.systembartint.SystemBarTintManager;
import com.science.strangertofriend.R;
import com.science.strangertofriend.widget.RevealLayout;

/**
 * @description
 * 
 * @author ����Science ������
 * @school University of South China
 * @email chentushen.science@gmail.com,274240671@qq.com
 * @2015-8-18
 * 
 */

public class ActivityInvitationActivity extends BaseActivity {

	private RevealLayout mRevealLayout;
	private LinearLayout mLayout;
	private ImageView mBackImg;
	private TextView mTitle;

	private EditText numberText;
	private EditText contentText;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_invitation);

		initView();
		initListener();

	}

	@Override
	@TargetApi(19)
	public void initSystemBar() {
		super.initSystemBar();
		// ����״̬���Ĺ���ʵ��
		SystemBarTintManager tintManager = new SystemBarTintManager(this);
		// ����״̬������
		tintManager.setStatusBarTintEnabled(true);
		// �����������
		tintManager.setNavigationBarTintEnabled(true);
		// ����һ����ɫ��ϵͳ��
		tintManager.setTintColor(Color.parseColor("#f698b2"));
	}

	private void initView() {

		mRevealLayout = (RevealLayout) findViewById(R.id.reveal_layout);
		mLayout = (LinearLayout) findViewById(R.id.layout);
		mLayout.setBackgroundColor(Color.WHITE);

		mBackImg = (ImageView) findViewById(R.id.back_img);
		mTitle = (TextView) findViewById(R.id.title);
		mTitle.setText("�����");

		numberText = (EditText) findViewById(R.id.number);
		contentText = (EditText) findViewById(R.id.content);
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

		// ���Ͻ��˳���ǰactivity
		mBackImg.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				ActivityInvitationActivity.this.finish();
			}
		});

	}

	public void sendClick(View v) {
		String numbers = numberText.getText().toString();
		String content = contentText.getText().toString();
		SmsManager smsManager = SmsManager.getDefault();
		ArrayList<String> texts = smsManager.divideMessage(content);
		String[] number = numbers.split(";");
		for (int i = 0; i < number.length; i++) {
			for (String text : texts) {
				smsManager.sendTextMessage(number[i], null, text, null, null);
			}
		}
		Toast.makeText(ActivityInvitationActivity.this, "�ɹ�", Toast.LENGTH_LONG)
				.show();
		contentText.setText("");
	}

	public void addClick(View v) {
		Intent intent = new Intent();
		intent.setClassName("com.science.strangertofriend",
				"com.science.strangertofriend.ui.ContactActivity");
		Bundle bundle = new Bundle();
		bundle.putString("number", numberText.getText().toString());
		intent.putExtras(bundle);
		startActivityForResult(intent, 200);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		try {
			String result = data.getStringExtra("result");
			numberText.setText(result);
		} catch (Exception e) {

		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

}
