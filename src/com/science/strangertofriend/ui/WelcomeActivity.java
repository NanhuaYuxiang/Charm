package com.science.strangertofriend.ui;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.widget.ImageView;

import com.avos.avoscloud.AVUser;
import com.science.strangertofriend.AppManager;
import com.science.strangertofriend.MainActivity;
import com.science.strangertofriend.R;
import com.science.strangertofriend.guide.GuideActivity;

/**
 * @description 欢迎界面
 * 
 * @author 幸运Science 陈土燊
 * @school University of South China
 * @email chentushen.science@gmail.com,274240671@qq.com
 * @2015-5-26
 * 
 */

public class WelcomeActivity extends Activity {

	private ImageView mWelcomeImg;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// 将activity加入到AppManager堆栈中
		AppManager.getAppManager().addActivity(this);

		SharedPreferences settingPreferences = getSharedPreferences(
				"WelcomeActivity", 0);
		boolean isFirstIn = settingPreferences.getBoolean("isFirstIn", true);
		if (isFirstIn) {
			settingPreferences.edit().putBoolean("isFirstIn", false).commit();
			Intent intent = new Intent(WelcomeActivity.this,
					GuideActivity.class);
			startActivity(intent);
			WelcomeActivity.this.finish();
		} else {
			if (AVUser.getCurrentUser() == null) {

				Intent intent = new Intent(WelcomeActivity.this,
						LoginActivity.class);
				startActivity(intent);
				WelcomeActivity.this.finish();
			} else {

				getWindow().setFlags(
						WindowManager.LayoutParams.FLAG_FULLSCREEN,
						WindowManager.LayoutParams.FLAG_FULLSCREEN);

				setContentView(R.layout.welcome);

				mWelcomeImg = (ImageView) findViewById(R.id.welcome);
				AlphaAnimation aa = new AlphaAnimation(0f, 1f);
				aa.setDuration(4000);
				mWelcomeImg.startAnimation(aa);
				aa.setAnimationListener(new AnimationListener() {

					@Override
					public void onAnimationStart(Animation animation) {
					}

					@Override
					public void onAnimationRepeat(Animation animation) {
					}

					@Override
					public void onAnimationEnd(Animation animation) {
						Intent intent = new Intent(WelcomeActivity.this,
								MainActivity.class);
						startActivity(intent);
						WelcomeActivity.this.finish();
					}
				});
			}
		}
	}

}
