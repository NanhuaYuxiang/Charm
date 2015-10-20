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
 * @description 用户协议
 * 
 * @author 幸运Science 陈土燊
 * @school University of South China
 * @email chentushen.science@gmail.com,274240671@qq.com
 * @2015-5-27
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
		mTitle.setText("用户协议");

		textView = (TextView) findViewById(R.id.textView);
		InputStream inputStream = getResources().openRawResource(
				R.raw.user_deal);
		String string = Utils.getString(inputStream);
		textView.setText(string);

		// 左上角退出当前activity
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
		// 创建状态栏的管理实例
		SystemBarTintManager tintManager = new SystemBarTintManager(this);
		// 激活状态栏设置
		tintManager.setStatusBarTintEnabled(true);
		// 激活导航栏设置
		tintManager.setNavigationBarTintEnabled(true);
		// 设置一个颜色给系统栏
		tintManager.setTintColor(Color.parseColor("#f698b2"));
	}

}
