package com.science.strangertofriend.ui;

import java.io.InputStream;

import android.annotation.TargetApi;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;

import com.readystatesoftware.systembartint.SystemBarTintManager;
import com.science.strangertofriend.R;
import com.science.strangertofriend.utils.Utils;

/**
 * @description �û�Э��
 * 
 */

public class UserDealActivity extends BaseActivity {

	private ImageView mBackImg;
	private TextView mTitle;
	private TextView textView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.user_dael);

		mBackImg = (ImageView) findViewById(R.id.back_img);
		mTitle = (TextView) findViewById(R.id.title);
		mTitle.setText("�û�Э��");

		textView = (TextView) findViewById(R.id.textView);
		InputStream inputStream = getResources().openRawResource(
				R.raw.user_deal);
		String string = Utils.getString(inputStream);
		textView.setText(string);

		// ���Ͻ��˳���ǰactivity
		mBackImg.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				UserDealActivity.this.finish();
			}
		});
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

}
