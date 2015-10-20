package com.science.strangertofriend.ui;

import android.annotation.TargetApi;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TableRow;
import android.widget.TextView;
import cn.pedant.SweetAlert.SweetAlertDialog;

import com.avos.avoscloud.feedback.FeedbackAgent;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.readystatesoftware.systembartint.SystemBarTintManager;
import com.science.strangertofriend.R;
import com.science.strangertofriend.guide.GuideActivity;
import com.science.strangertofriend.widget.RevealLayout;

/**
 * @description 设置界面
 * 
 * @author 幸运Science 陈土燊
 * @school University of South China
 * @email chentushen.science@gmail.com,274240671@qq.com
 * @2015-5-27
 * 
 */

public class SettingActivity extends BaseActivity {

	private RevealLayout mRevealLayout;
	private RelativeLayout mLayout;
	private ImageView mBackImg;
	private TextView mTitle;
	private TableRow mNumberSafe, mNumberBound, mMessageTip, mSetClearCache,
			mSetHelp, mSetUpdate, mSetVersion, mUserDeal, mAboutUs;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.setting);

		initView();
		initListener();
	}

	private void initView() {
		mBackImg = (ImageView) findViewById(R.id.back_img);
		mTitle = (TextView) findViewById(R.id.title);
		mTitle.setText("设置");

		mNumberSafe = (TableRow) findViewById(R.id.number_safe);
		mNumberBound = (TableRow) findViewById(R.id.number_bound);
		mMessageTip = (TableRow) findViewById(R.id.message_tip);
		mSetClearCache = (TableRow) findViewById(R.id.clear_cache);
		mSetHelp = (TableRow) findViewById(R.id.set_help);
		mSetUpdate = (TableRow) findViewById(R.id.set_update);
		mSetVersion = (TableRow) findViewById(R.id.set_version);
		mUserDeal = (TableRow) findViewById(R.id.user_deal);
		mAboutUs = (TableRow) findViewById(R.id.about_us);

		mRevealLayout = (RevealLayout) findViewById(R.id.reveal_layout);
		mLayout = (RelativeLayout) findViewById(R.id.layout);
		mLayout.setBackgroundColor(Color.WHITE);
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

		// 左上角退出当前activity
		mBackImg.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				SettingActivity.this.finish();
			}
		});

		mNumberSafe.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				new SweetAlertDialog(SettingActivity.this,
						SweetAlertDialog.WARNING_TYPE)
						.setTitleText("账号安全")
						.setContentText("账号安全功能攻城狮正在总攻中..")
						.setConfirmText("ok")
						.setConfirmClickListener(
								new SweetAlertDialog.OnSweetClickListener() {
									@Override
									public void onClick(SweetAlertDialog sDialog) {
										sDialog.dismiss();
									}
								}).show();
			}

		});
		mNumberBound.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				new SweetAlertDialog(SettingActivity.this,
						SweetAlertDialog.WARNING_TYPE)
						.setTitleText("账号绑定")
						.setContentText("账号绑定功能攻城狮正在总攻中..")
						.setConfirmText("ok")
						.setConfirmClickListener(
								new SweetAlertDialog.OnSweetClickListener() {
									@Override
									public void onClick(SweetAlertDialog sDialog) {
										sDialog.dismiss();
									}
								}).show();
			}

		});
		mMessageTip.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

			}

		});
		mSetClearCache.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				new SweetAlertDialog(SettingActivity.this,
						SweetAlertDialog.WARNING_TYPE)
						.setTitleText("确定清除缓存？")
						.setContentText("不可恢复！")
						.setCancelText("取消")
						.setConfirmText("确定")
						.showCancelButton(true)
						.setCancelClickListener(
								new SweetAlertDialog.OnSweetClickListener() {
									@Override
									public void onClick(SweetAlertDialog sDialog) {

										sDialog.dismiss();
										new SweetAlertDialog(
												SettingActivity.this,
												SweetAlertDialog.ERROR_TYPE)
												.setTitleText("成功取消").show();
									}
								})
						.setConfirmClickListener(
								new SweetAlertDialog.OnSweetClickListener() {
									@Override
									public void onClick(SweetAlertDialog sDialog) {
										ImageLoader.getInstance()
												.clearMemoryCache();
										ImageLoader.getInstance()
												.clearDiskCache();
										sDialog.dismiss();
										new SweetAlertDialog(
												SettingActivity.this,
												SweetAlertDialog.SUCCESS_TYPE)
												.setTitleText("成功清除").show();
									}
								}).show();
			}

		});
		mSetHelp.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// avos用户反馈系统
				FeedbackAgent agent = new FeedbackAgent(SettingActivity.this);
				agent.startDefaultThreadActivity();
			}

		});
		mSetUpdate.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				final SweetAlertDialog nAlertDialog = new SweetAlertDialog(
						SettingActivity.this, SweetAlertDialog.PROGRESS_TYPE);
				nAlertDialog.setTitleText("正在检查更新").setContentText("请稍后");
				nAlertDialog.show();
				nAlertDialog.setCancelable(false);
				new CountDownTimer(800 * 4, 800) {
					public void onTick(long millisUntilFinished) {
						colorProgress(nAlertDialog);
					}

					public void onFinish() {
						i = -1;
						nAlertDialog.dismiss();
						new SweetAlertDialog(SettingActivity.this,
								SweetAlertDialog.WARNING_TYPE)
								.setTitleText("已是最新版本")
								.setConfirmText("ok")
								.setConfirmClickListener(
										new SweetAlertDialog.OnSweetClickListener() {
											@Override
											public void onClick(
													SweetAlertDialog sDialog) {
												sDialog.dismiss();
											}
										}).show();
					}
				}.start();
			}

		});
		mSetVersion.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(SettingActivity.this,
						GuideActivity.class);
				intent.putExtra("set", 1);
				startActivity(intent);
			}

		});
		mUserDeal.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(SettingActivity.this,
						UserDealActivity.class);
				startActivity(intent);
			}

		});
		mAboutUs.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				new SweetAlertDialog(SettingActivity.this,
						SweetAlertDialog.WARNING_TYPE)
						.setTitleText("关于我们")
						.setContentText("本软件由南华大学经纬度团队制作，更多成就，请关注微博 @南华大学经纬度团队")
						.setConfirmText("ok")
						.setConfirmClickListener(
								new SweetAlertDialog.OnSweetClickListener() {
									@Override
									public void onClick(SweetAlertDialog sDialog) {
										sDialog.dismiss();
									}
								}).show();
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
