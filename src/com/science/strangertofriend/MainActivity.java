package com.science.strangertofriend;

import static android.view.Gravity.START;
import io.codetail.animation.SupportAnimator;
import io.codetail.animation.ViewAnimationUtils;

import java.util.ArrayList;
import java.util.List;

import yalantis.com.sidemenu.interfaces.Resourceble;
import yalantis.com.sidemenu.interfaces.ScreenShotable;
import yalantis.com.sidemenu.model.SlideMenuItem;
import yalantis.com.sidemenu.util.ViewAnimator;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.os.Build.VERSION;
import android.os.Build.VERSION_CODES;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.view.animation.AccelerateInterpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import cn.pedant.SweetAlert.SweetAlertDialog;

import com.avos.avoscloud.AVAnalytics;
import com.avos.avoscloud.feedback.FeedbackAgent;
import com.readystatesoftware.systembartint.SystemBarTintManager;
import com.science.materialmenu.DrawerArrowDrawable;
import com.science.strangertofriend.fragment.AddressListFragment;
import com.science.strangertofriend.fragment.MessageFragment;
import com.science.strangertofriend.fragment.ShakeFragment;
import com.science.strangertofriend.fragment.UserFragment;
import com.science.strangertofriend.ui.AlterActivity;
import com.science.strangertofriend.ui.SettingActivity;
import com.yalantis.contextmenu.lib.ContextMenuDialogFragment;
import com.yalantis.contextmenu.lib.MenuObject;
import com.yalantis.contextmenu.lib.MenuParams;
import com.yalantis.contextmenu.lib.interfaces.OnMenuItemClickListener;
import com.yalantis.contextmenu.lib.interfaces.OnMenuItemLongClickListener;

/**
 * @description
 * 
 * @author 幸运Science·陈土燊
 * @school University of South China
 * @email chentushen.science@gmail.com,274240671@qq.com
 * @date 2015-4-25
 */

public class MainActivity extends ActionBarActivity implements
		ViewAnimator.ViewAnimatorListener, OnMenuItemClickListener,
		OnMenuItemLongClickListener {

	private AppContext appContext;// 全局Context

	private DrawerLayout mDrawerLayout;
	// private ActionBarDrawerToggle mActionBarDrawerToggle;
	private DrawerArrowDrawable mDrawerArrowDrawable;
	private float offset;
	private boolean flipped;
	private ImageView imageView;

	private List<SlideMenuItem> mMenuList = new ArrayList<>();
	private ShakeFragment mShakeFragment;
	private UserFragment mUserFragment;
	private MessageFragment mMessageFragment;
	private AddressListFragment mAddressListFragment;
	@SuppressWarnings("rawtypes")
	private ViewAnimator mViewAnimator;
	private LinearLayout mLinearLayout;
	private TextView mTitleText;
	// 定义一个变量，来标识是否退出
	private static boolean isExit = false;
	private int i = -1;

	// 上下文菜单
	private DialogFragment mMenuDialogFragment;
	private FragmentManager mFragmentManager;
	private ImageView mTitleMore;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		// avos用户反馈系统,在用户打开App时，通知用户新的反馈回复
		FeedbackAgent agent = new FeedbackAgent(MainActivity.this);
		agent.sync();

		// 将activity加入到AppManager堆栈中
		AppManager.getAppManager().addActivity(this);

		// 跟踪统计应用的打开情况
		AVAnalytics.trackAppOpened(getIntent());
		appContext = (AppContext) getApplication();
		// 网络连接判断
		if (!appContext.isNetworkConnected())
			Toast.makeText(this, R.string.network_not_connected,
					Toast.LENGTH_LONG).show();

		// 沉浸式状态栏设置
		initSystemBar();
		initComponent();
		initListener();
		setActionBar();
		createMenuList();
		// mMenuList 为菜单每个项的内容
		// contentFragment 为主体显示继承自Fragment 并实现了ScreenShotable接口
		// 最后一个参数为ViewAnimator.ViewAnimatorListener
		// 接口,其中一个方法便是addViewToContainer
		// 在ViewAnimator 中创建view 并添加到 linearLayout 菜单中.
		mViewAnimator = new ViewAnimator<>(this, mMenuList, mMessageFragment,
				mDrawerLayout, this);
	}

	@TargetApi(Build.VERSION_CODES.KITKAT)
	private void initSystemBar() {
		if (VERSION.SDK_INT >= VERSION_CODES.KITKAT) {
			// 透明状态栏
			getWindow().addFlags(
					WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
			// 透明导航栏
			getWindow().addFlags(
					WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
		}
		// 创建状态栏的管理实例
		SystemBarTintManager tintManager = new SystemBarTintManager(this);
		// 激活状态栏设置
		tintManager.setStatusBarTintEnabled(true);
		// 激活导航栏设置
		tintManager.setNavigationBarTintEnabled(true);
		// 设置一个颜色给系统栏
		tintManager.setTintColor(Color.parseColor("#f698b2"));
	}

	private void initComponent() {

		mTitleText = (TextView) findViewById(R.id.title);
		mTitleText.setText("聊天");
		mMessageFragment = new MessageFragment();
		getSupportFragmentManager().beginTransaction()
				.replace(R.id.content_frame, mMessageFragment).commit();
		mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
		mDrawerLayout.setScrimColor(Color.TRANSPARENT);

		imageView = (ImageView) findViewById(R.id.drawer_indicator);
		final Resources resources = getResources();
		mDrawerArrowDrawable = new DrawerArrowDrawable(resources);
		mDrawerArrowDrawable.setStrokeColor(0xffffffff);
		imageView.setImageDrawable(mDrawerArrowDrawable);

		mLinearLayout = (LinearLayout) findViewById(R.id.left_drawer);
		mLinearLayout.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				mDrawerLayout.closeDrawers();
			}
		});

		// 上下文菜单
		mTitleMore = (ImageView) findViewById(R.id.title_more);
		mFragmentManager = getSupportFragmentManager();
		initMenuFragment();
	}

	// 上下文菜单
	private void initMenuFragment() {

		MenuParams menuParams = new MenuParams();
		menuParams.setActionBarSize((int) getResources().getDimension(
				R.dimen.title_height));
		menuParams.setMenuObjects(getMenuObjects());
		menuParams.setClosableOutside(false);
		mMenuDialogFragment = ContextMenuDialogFragment.newInstance(menuParams);
	}

	// 上下文菜单
	private List<MenuObject> getMenuObjects() {
		List<MenuObject> menuObjects = new ArrayList<>();

		MenuObject close = new MenuObject();
		close.setResource(R.drawable.close_drawer);

		MenuObject set = new MenuObject("应用设置");
		set.setResource(R.drawable.set);

		MenuObject user = new MenuObject("资料更改");
		user.setResource(R.drawable.user);

		MenuObject quit = new MenuObject("退出应用");
		quit.setResource(R.drawable.quit);

		menuObjects.add(close);
		menuObjects.add(set);
		menuObjects.add(user);
		menuObjects.add(quit);
		return menuObjects;
	}

	private void initListener() {

		mTitleMore.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (mFragmentManager
						.findFragmentByTag(ContextMenuDialogFragment.TAG) == null) {
					mMenuDialogFragment.show(mFragmentManager,
							ContextMenuDialogFragment.TAG);
				}
			}
		});
	}

	private void setActionBar() {

		mDrawerLayout
				.setDrawerListener(new DrawerLayout.SimpleDrawerListener() {
					@Override
					public void onDrawerClosed(View drawerView) {
						super.onDrawerClosed(drawerView);
						mLinearLayout.removeAllViews();
						mLinearLayout.invalidate();
					}

					@Override
					public void onDrawerSlide(View drawerView, float slideOffset) {
						super.onDrawerSlide(drawerView, slideOffset);
						offset = slideOffset;
						if (slideOffset > 0.6
								&& mLinearLayout.getChildCount() == 0) {
							mViewAnimator.showMenuContent();
							flipped = true;
							mDrawerArrowDrawable.setFlip(flipped);
						} else {
							flipped = false;
							mDrawerArrowDrawable.setFlip(flipped);
						}

						mDrawerArrowDrawable.setParameter(offset);
					}

					/**
					 * Called when a drawer has settled in a completely open
					 * state.
					 */
					public void onDrawerOpened(View drawerView) {
						super.onDrawerOpened(drawerView);
					}
				});

		imageView.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (mDrawerLayout.isDrawerVisible(START)) {
					mDrawerLayout.closeDrawer(START);
				} else {
					mDrawerLayout.openDrawer(START);
				}
			}
		});

	}

	private void createMenuList() {

		SlideMenuItem slideMenuItemShake = new SlideMenuItem("Shake",
				R.drawable.a);
		mMenuList.add(slideMenuItemShake);
		SlideMenuItem slideMenuItemMessage = new SlideMenuItem("Message",
				R.drawable.b);
		mMenuList.add(slideMenuItemMessage);
		SlideMenuItem slideMenuItemAddress = new SlideMenuItem("Address",
				R.drawable.c);
		mMenuList.add(slideMenuItemAddress);
		SlideMenuItem slideMenuItemUser = new SlideMenuItem("User",
				R.drawable.d);
		mMenuList.add(slideMenuItemUser);
	}

	// 摇一摇视图切换动画实现
	public ScreenShotable replaceShakeFragment(ScreenShotable screenShotable,
			int topPosition) {

		CircularRevealAnima(screenShotable, topPosition);
		mTitleText.setText("摇一摇");
		mShakeFragment = new ShakeFragment();
		getSupportFragmentManager().beginTransaction()
				.replace(R.id.content_frame, mShakeFragment).commit();

		return mShakeFragment;
	}

	// 用户信息视图切换动画实现
	public ScreenShotable replaceUserFragment(ScreenShotable screenShotable,
			int topPosition) {

		CircularRevealAnima(screenShotable, topPosition);
		mTitleText.setText("个人");
		mUserFragment = new UserFragment();
		getSupportFragmentManager().beginTransaction()
				.replace(R.id.content_frame, mUserFragment).commit();

		return mUserFragment;
	}

	// 聊天视图切换动画实现
	public ScreenShotable replaceMessageFragment(ScreenShotable screenShotable,
			int topPosition) {

		CircularRevealAnima(screenShotable, topPosition);
		mTitleText.setText("聊天");
		mMessageFragment = new MessageFragment();
		getSupportFragmentManager().beginTransaction()
				.replace(R.id.content_frame, mMessageFragment).commit();

		return mMessageFragment;
	}

	// 通讯录视图切换动画实现
	public ScreenShotable replaceAddressListFragment(
			ScreenShotable screenShotable, int topPosition) {

		CircularRevealAnima(screenShotable, topPosition);
		mTitleText.setText("通讯录");
		mAddressListFragment = new AddressListFragment();
		getSupportFragmentManager().beginTransaction()
				.replace(R.id.content_frame, mAddressListFragment).commit();

		return mAddressListFragment;
	}

	@Override
	public ScreenShotable onSwitch(Resourceble slideMenuItem,
			ScreenShotable screenShotable, int topPosition) {

		switch (slideMenuItem.getName()) {
		case "Shake":
			return replaceShakeFragment(screenShotable, topPosition);
		case "User":
			return replaceUserFragment(screenShotable, topPosition);
		case "Message":
			return replaceMessageFragment(screenShotable, topPosition);
		case "Address":
			return replaceAddressListFragment(screenShotable, topPosition);
		default:
			return screenShotable;
		}
	}

	@SuppressWarnings("deprecation")
	private void CircularRevealAnima(ScreenShotable screenShotable,
			int topPosition) {
		View view = findViewById(R.id.content_frame);
		int finalRadius = Math.max(view.getWidth(), view.getHeight());
		// 创建圆形动画
		SupportAnimator animator = ViewAnimationUtils.createCircularReveal(
				view, 0, topPosition, 0, finalRadius);
		animator.setInterpolator(new AccelerateInterpolator());
		animator.setDuration(ViewAnimator.CIRCULAR_REVEAL_ANIMATION_DURATION);
		// 由于圆形动画是一点点的扩大的,其没有全部覆盖的部分应该为上一个视图的内容,
		// 因此我们需要将前面的视图截图保存下来,可将下面代码屏蔽可明白其意义.
		findViewById(R.id.content_overlay).setBackgroundDrawable(
				new BitmapDrawable(getResources(), screenShotable.getBitmap()));
		animator.start();
	}

	@Override
	public void disableHomeButton() {
	}

	@Override
	public void enableHomeButton() {
		mDrawerLayout.closeDrawers();
	}

	// 添加到菜单的LinearLayout 中去
	@Override
	public void addViewToContainer(View view) {
		mLinearLayout.addView(view);
	}

	@Override
	public void onMenuItemClick(View clickedView, int position) {
		switch (position) {
		case 0:
			break;

		case 1:
			Intent intentSet = new Intent(this, SettingActivity.class);
			startActivity(intentSet);
			break;

		case 2:
			Intent intentAlter = new Intent(this, AlterActivity.class);
			startActivityForResult(intentAlter, 1);
			break;

		case 3:
			quitApp();
			break;

		default:
			break;
		}
	}

	@Override
	public void onMenuItemLongClick(View clickedView, int position) {
		Toast.makeText(this, "Can i help you?", Toast.LENGTH_SHORT).show();
	}

	// 退出APP
	private void quitApp() {
		new SweetAlertDialog(this, SweetAlertDialog.WARNING_TYPE)
				.setTitleText("真的要退出么?")
				.setContentText("再玩会儿啦!")
				.setCancelText("再玩一会")
				.setConfirmText("残忍退出")
				.showCancelButton(true)
				.setCancelClickListener(
						new SweetAlertDialog.OnSweetClickListener() {
							@Override
							public void onClick(SweetAlertDialog sDialog) {
								// reuse previous dialog instance, keep
								// widget user state, reset them if you need
								sDialog.setTitleText("已经成功取消")
										.setConfirmText("确定")
										.setContentText("欢迎回来")
										.showCancelButton(false)
										.setCancelClickListener(null)
										.setConfirmClickListener(null)
										.changeAlertType(
												SweetAlertDialog.SUCCESS_TYPE);

							}
						})
				.setConfirmClickListener(
						new SweetAlertDialog.OnSweetClickListener() {
							@Override
							public void onClick(SweetAlertDialog sDialog) {
								sDialog.dismiss();

								final SweetAlertDialog nAlertDialog = new SweetAlertDialog(
										MainActivity.this,
										SweetAlertDialog.PROGRESS_TYPE);
								nAlertDialog.setTitleText("正在退出..")
										.setContentText("下次再见！记得(⊙o⊙)喔!");
								nAlertDialog.show();
								nAlertDialog.setCancelable(false);
								new CountDownTimer(800 * 4, 800) {
									public void onTick(long millisUntilFinished) {
										// you can change the progress bar color
										// by ProgressHelper
										// every 800 millis
										i++;
										switch (i) {
										case 0:
											nAlertDialog
													.getProgressHelper()
													.setBarColor(
															getResources()
																	.getColor(
																			android.R.color.holo_blue_bright));
											break;

										case 1:
											nAlertDialog
													.getProgressHelper()
													.setBarColor(
															getResources()
																	.getColor(
																			android.R.color.holo_green_light));
											break;
										case 2:
											nAlertDialog
													.getProgressHelper()
													.setBarColor(
															getResources()
																	.getColor(
																			android.R.color.holo_orange_light));
											break;

										case 3:
											nAlertDialog
													.getProgressHelper()
													.setBarColor(
															getResources()
																	.getColor(
																			android.R.color.holo_red_light));
											break;
										}
									}

									public void onFinish() {
										i = -1;
										nAlertDialog.dismiss();
										MainActivity.this.finish();
										System.exit(0);
									}
								}.start();
							}
						}).show();
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
			exit();
			return false;
		}
		return super.onKeyDown(keyCode, event);
	}

	private void exit() {
		if (!isExit) {
			isExit = true;
			Toast.makeText(getApplicationContext(), "再按一次退出程序",
					Toast.LENGTH_SHORT).show();
			// 利用handler延迟发送更改状态信息
			mHandler.sendEmptyMessageDelayed(0, 2000);
		} else {
			// finish();
			// System.exit(0);
			AppManager.getAppManager().AppExit(MainActivity.this);
		}
	}

	@SuppressLint("HandlerLeak")
	Handler mHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			isExit = false;
		}
	};

}
