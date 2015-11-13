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
import com.science.strangertofriend.fragment.TaskFragment;
import com.science.strangertofriend.fragment.UserFragment;
import com.science.strangertofriend.ui.AlterActivity;
import com.science.strangertofriend.ui.SettingActivity;
import com.science.strangertofriend.ui.Task_List_Publish_Complete_ListView_Activity;
import com.science.strangertofriend.utils.GetUserTaskLists;
import com.yalantis.contextmenu.lib.ContextMenuDialogFragment;
import com.yalantis.contextmenu.lib.MenuObject;
import com.yalantis.contextmenu.lib.MenuParams;
import com.yalantis.contextmenu.lib.interfaces.OnMenuItemClickListener;
import com.yalantis.contextmenu.lib.interfaces.OnMenuItemLongClickListener;

public class MainActivity extends ActionBarActivity implements
		ViewAnimator.ViewAnimatorListener, OnMenuItemClickListener,
		OnMenuItemLongClickListener {

	private AppContext appContext;// ȫ��Context

	private DrawerLayout mDrawerLayout;
	// private ActionBarDrawerToggle mActionBarDrawerToggle;
	private DrawerArrowDrawable mDrawerArrowDrawable;// ���������ͼ�꣬���ݲ�ͬ״̬�仯
	private float offset;
	private boolean flipped;
	private ImageView imageView;// �������ѡ���б�ͼ��

	private List<SlideMenuItem> mMenuList = new ArrayList<>();
	private ShakeFragment mShakeFragment;
	private UserFragment mUserFragment;
	private MessageFragment mMessageFragment;
	private AddressListFragment mAddressListFragment;
	private TaskFragment mTaskFragment;
	@SuppressWarnings("rawtypes")
	private ViewAnimator mViewAnimator;
	private LinearLayout mLinearLayout;// ���drawerLayout
	private TextView mTitleText;
	// ����һ������������ʶ�Ƿ��˳�
	private static boolean isExit = false;
	private int i = -1;

	// �����Ĳ˵�
	private DialogFragment mMenuDialogFragment;
	private FragmentManager mFragmentManager;
	private ImageView mTitleMore;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		// avos�û�����ϵͳ,���û���Appʱ��֪ͨ�û��µķ����ظ�
		FeedbackAgent agent = new FeedbackAgent(MainActivity.this);
		agent.sync();

		// ��activity���뵽AppManager��ջ��
		AppManager.getAppManager().addActivity(this);

		// ����ͳ��Ӧ�õĴ����
		AVAnalytics.trackAppOpened(getIntent());
		appContext = (AppContext) getApplication();
		// ���������ж�
		if (!appContext.isNetworkConnected())
			Toast.makeText(this, R.string.network_not_connected,
					Toast.LENGTH_LONG).show();

		// ����ʽ״̬������
		initSystemBar();
		initComponent();
		initListener();
		setActionBar();
		createMenuList();
		// mMenuList Ϊ�˵�ÿ���������
		// contentFragment Ϊ������ʾ�̳���Fragment ��ʵ����ScreenShotable�ӿ�
		// ���һ������ΪViewAnimator.ViewAnimatorListener
		// �ӿ�,����һ����������addViewToContainer
		// ��ViewAnimator �д���view ����ӵ� linearLayout �˵���.
		mViewAnimator = new ViewAnimator<>(this, mMenuList, mShakeFragment,
				mDrawerLayout, this);

	}

	@TargetApi(Build.VERSION_CODES.KITKAT)
	private void initSystemBar() {
		if (VERSION.SDK_INT >= VERSION_CODES.KITKAT) {
			// ͸��״̬��
			getWindow().addFlags(
					WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
			// ͸��������
			getWindow().addFlags(
					WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
		}
		// ����״̬���Ĺ���ʵ��
		SystemBarTintManager tintManager = new SystemBarTintManager(this);
		// ����״̬������
		tintManager.setStatusBarTintEnabled(true);
		// �����������
		tintManager.setNavigationBarTintEnabled(true);
		// ����һ����ɫ��ϵͳ��
		tintManager.setTintColor(Color.parseColor("#f698b2"));
	}

	private void initComponent() {

		mTitleText = (TextView) findViewById(R.id.title);
		mTitleText.setText("ҡһҡ��������");
		// mMessageFragment = new MessageFragment();
		// mAddressListFragment=new AddressListFragment();
		mShakeFragment = new ShakeFragment();
		getSupportFragmentManager().beginTransaction()
				.replace(R.id.content_frame, mShakeFragment).commit();
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

		// �����Ĳ˵�
		mTitleMore = (ImageView) findViewById(R.id.title_more);
		mFragmentManager = getSupportFragmentManager();
		initMenuFragment();
	}

	// �����Ĳ˵�
	private void initMenuFragment() {

		MenuParams menuParams = new MenuParams();
		menuParams.setActionBarSize((int) getResources().getDimension(
				R.dimen.title_height));
		menuParams.setMenuObjects(getMenuObjects());
		menuParams.setClosableOutside(false);
		mMenuDialogFragment = ContextMenuDialogFragment.newInstance(menuParams);
	}

	// �����Ĳ˵�
	private List<MenuObject> getMenuObjects() {
		List<MenuObject> menuObjects = new ArrayList<>();

		MenuObject close = new MenuObject();
		close.setResource(R.drawable.close_drawer);

		MenuObject set = new MenuObject("Ӧ������");
		set.setResource(R.drawable.set);

		MenuObject user = new MenuObject("���ϸ���");
		user.setResource(R.drawable.user);

		MenuObject quit = new MenuObject("�˳�Ӧ��");
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

		// ����
		SlideMenuItem slideMenuItemTask = new SlideMenuItem("Task",
				R.drawable.a);
		mMenuList.add(slideMenuItemTask);

	}

	// ҡһҡ��ͼ�л�����ʵ��
	public ScreenShotable replaceShakeFragment(ScreenShotable screenShotable,
			int topPosition) {

		CircularRevealAnima(screenShotable, topPosition);
		mTitleText.setText("ҡһҡ");
		mShakeFragment = new ShakeFragment();
		getSupportFragmentManager().beginTransaction()
				.replace(R.id.content_frame, mShakeFragment).commit();

		return mShakeFragment;
	}

	// �û���Ϣ��ͼ�л�����ʵ��
	public ScreenShotable replaceUserFragment(ScreenShotable screenShotable,
			int topPosition) {

		CircularRevealAnima(screenShotable, topPosition);
		mTitleText.setText("����");
		mUserFragment = new UserFragment();
		getSupportFragmentManager().beginTransaction()
				.replace(R.id.content_frame, mUserFragment).commit();

		return mUserFragment;
	}

	// ������ͼ�л�����ʵ��
	public ScreenShotable replaceMessageFragment(ScreenShotable screenShotable,
			int topPosition) {

		CircularRevealAnima(screenShotable, topPosition);
		mTitleText.setText("����");
		mMessageFragment = new MessageFragment();
		getSupportFragmentManager().beginTransaction()
				.replace(R.id.content_frame, mMessageFragment).commit();

		return mMessageFragment;
	}

	// �����б��л�����ʵ��
	public ScreenShotable replaceTaskFragment(ScreenShotable screenShotable,
			int topPosition) {
		CircularRevealAnima(screenShotable, topPosition);
		mTitleText.setText("����");
		mTaskFragment = new TaskFragment();
		getSupportFragmentManager().beginTransaction()
				.replace(R.id.content_frame, mTaskFragment).commit();

		return mTaskFragment;
	}

	// ͨѶ¼��ͼ�л�����ʵ��
	public ScreenShotable replaceAddressListFragment(
			ScreenShotable screenShotable, int topPosition) {

		CircularRevealAnima(screenShotable, topPosition);
		mTitleText.setText("ͨѶ¼");
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
		case "Task":
			// ��ȡ��Դ
//			new GetUserTaskLists();
			// ��ת�����嵥
			startActivity(new Intent(MainActivity.this,
					Task_List_Publish_Complete_ListView_Activity.class));

			// finish();
			// return replaceTaskFragment(screenShotable, topPosition);
		default:
			return screenShotable;
		}
	}

	@SuppressWarnings("deprecation")
	private void CircularRevealAnima(ScreenShotable screenShotable,
			int topPosition) {
		View view = findViewById(R.id.content_frame);
		int finalRadius = Math.max(view.getWidth(), view.getHeight());
		// ����Բ�ζ���
		SupportAnimator animator = ViewAnimationUtils.createCircularReveal(
				view, 0, topPosition, 0, finalRadius);
		animator.setInterpolator(new AccelerateInterpolator());
		animator.setDuration(ViewAnimator.CIRCULAR_REVEAL_ANIMATION_DURATION);
		// ����Բ�ζ�����һ���������,��û��ȫ�����ǵĲ���Ӧ��Ϊ��һ����ͼ������,
		// ���������Ҫ��ǰ�����ͼ��ͼ��������,�ɽ�����������ο�����������.
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

	// ��ӵ��˵���LinearLayout ��ȥ
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

	// �˳�APP
	private void quitApp() {
		new SweetAlertDialog(this, SweetAlertDialog.WARNING_TYPE)
				.setTitleText("���Ҫ�˳�ô?")
				.setContentText("��������!")
				.setCancelText("����һ��")
				.setConfirmText("�����˳�")
				.showCancelButton(true)
				.setCancelClickListener(
						new SweetAlertDialog.OnSweetClickListener() {
							@Override
							public void onClick(SweetAlertDialog sDialog) {
								// reuse previous dialog instance, keep
								// widget user state, reset them if you need
								sDialog.setTitleText("�Ѿ��ɹ�ȡ��")
										.setConfirmText("ȷ��")
										.setContentText("��ӭ����")
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
								nAlertDialog.setTitleText("�����˳�..")
										.setContentText("�´��ټ����ǵ�(��o��)�!");
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
			Toast.makeText(getApplicationContext(), "�ٰ�һ���˳�����",
					Toast.LENGTH_SHORT).show();
			// ����handler�ӳٷ��͸���״̬��Ϣ
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
