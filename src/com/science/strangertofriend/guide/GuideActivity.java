package com.science.strangertofriend.guide;

import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.DisplayMetrics;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.WindowManager;
import android.widget.ImageView;

import com.science.strangertofriend.AppManager;
import com.science.strangertofriend.R;
import com.science.strangertofriend.ui.BaseActivity;
import com.science.strangertofriend.ui.WelcomeActivity;

/**
 * @description 引导界面
 * 
 * @author 幸运Science 陈土燊
 * @school University of South China
 * @email chentushen.science@gmail.com,274240671@qq.com
 * @2015-5-27
 * 
 */

public class GuideActivity extends BaseActivity implements OnTouchListener {

	private ViewPager mGuidePage;
	private ADPagerAdapter mAdapter;
	private CirclePageIndicator mIndicator;
	private int flaggingWidth;
	private int size = 0;
	private int lastX = 0;
	private int currentIndex = 0;
	private boolean locker = true;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);

		setContentView(R.layout.guide);

		// 将activity加入到AppManager堆栈中
		AppManager.getAppManager().addActivity(this);

		SharedPreferences settingPreferences = getSharedPreferences(
				"WelcomeActivity", 0);
		settingPreferences.edit().putBoolean("isFirstIn", false).commit();

		DisplayMetrics dm = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(dm);
		flaggingWidth = dm.widthPixels / 3;

		mGuidePage = (ViewPager) findViewById(R.id.pager_splash);

		List<View> views = new ArrayList<View>();
		View view = LayoutInflater.from(this).inflate(R.layout.view_splash_ad,
				null);
		ImageView iv_ad = (ImageView) view.findViewById(R.id.iv_ad);
		iv_ad.setImageResource(R.drawable.guide_a);
		views.add(view);
		View view1 = LayoutInflater.from(this).inflate(R.layout.view_splash_ad,
				null);
		ImageView iv_ad1 = (ImageView) view1.findViewById(R.id.iv_ad);
		iv_ad1.setImageResource(R.drawable.guide_b);
		views.add(view1);
		View view2 = LayoutInflater.from(this).inflate(R.layout.view_splash_ad,
				null);
		ImageView iv_ad2 = (ImageView) view2.findViewById(R.id.iv_ad);
		iv_ad2.setImageResource(R.drawable.guide_c);
		views.add(view2);
		View view3 = LayoutInflater.from(this).inflate(R.layout.view_splash_ad,
				null);
		ImageView iv_ad3 = (ImageView) view3.findViewById(R.id.iv_ad);
		iv_ad3.setImageResource(R.drawable.guide_d);
		views.add(view3);

		size = views.size();

		mAdapter = new ADPagerAdapter(this, views);
		mGuidePage.setAdapter(mAdapter);
		mIndicator = (CirclePageIndicator) findViewById(R.id.viewflowindic);
		mIndicator.setmListener(new MypageChangeListener());
		mIndicator.setViewPager(mGuidePage);

		if (views.size() == 1) {
			Handler handler = new Handler();
			handler.postDelayed(new Runnable() {

				@Override
				public void run() {
					gotoMain();
				}
			}, 1000);
		} else {
			// pager_splash_ad.setOnPageChangeListener(new
			// MypageChangeListener());
			mGuidePage.setOnTouchListener(this);
		}
	}

	private class MypageChangeListener implements OnPageChangeListener {

		@Override
		public void onPageScrollStateChanged(int position) {
			// System.err.println("------position---"+position);
			// currentItem = position;
		}

		@Override
		public void onPageScrolled(int arg0, float arg1, int arg2) {
			// TODO Auto-generated method stub
		}

		@Override
		public void onPageSelected(int arg0) {
			currentIndex = arg0;
		}

	}

	private void gotoMain() {
		if (getIntent().getIntExtra("set", 0) == 1) {
			finish();
			overridePendingTransition(R.anim.alpha_in_anim,
					R.anim.alpha_out_anim);
		} else {

			Intent intent = new Intent(GuideActivity.this,
					WelcomeActivity.class);
			startActivity(intent);
			finish();
			overridePendingTransition(R.anim.alpha_in_anim,
					R.anim.alpha_out_anim);
		}
	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			lastX = (int) event.getX();
			break;
		case MotionEvent.ACTION_MOVE:
			if ((lastX - event.getX()) > flaggingWidth
					&& (currentIndex == size - 1) && locker) {
				locker = false;
				System.err.println("-------1111-------");
				gotoMain();
			}
			break;
		default:
			break;
		}
		return false;
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
			GuideActivity.this.finish();
			return false;
		}
		return super.onKeyDown(keyCode, event);
	}

}
