package com.science.strangertofriend.game.puzzle;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import android.annotation.TargetApi;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build.VERSION;
import android.os.Build.VERSION_CODES;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.ActionBarActivity;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import cn.pedant.SweetAlert.SweetAlertDialog;

import com.avos.avoscloud.AVUser;
import com.readystatesoftware.systembartint.SystemBarTintManager;
import com.science.strangertofriend.AppManager;
import com.science.strangertofriend.R;
import com.science.strangertofriend.game.puzzle.GamePintuLayout.GamePintuListener;
import com.science.strangertofriend.ui.FriendInformationAddActivity;
import com.science.strangertofriend.utils.FileUtil;
import com.science.strangertofriend.widget.RevealLayout;
import com.yalantis.contextmenu.lib.ContextMenuDialogFragment;
import com.yalantis.contextmenu.lib.MenuObject;
import com.yalantis.contextmenu.lib.MenuParams;
import com.yalantis.contextmenu.lib.interfaces.OnMenuItemClickListener;

/**
 * @description 拼图游戏
 * 
 * @author 幸运Science 陈土燊
 * @school University of South China
 * @email chentushen.science@gmail.com,274240671@qq.com
 * @2015-5-24
 * 
 */

public class PuzzleActivity extends ActionBarActivity implements
		OnMenuItemClickListener {

	private static final String IMAGE_FILE_NAME = "puzzle_game.jpg";// 头像文件名称
	private static final int REQUESTCODE_PICK = 0; // 相册选图标记
	private static final int REQUESTCODE_TAKE = 1; // 相机拍照标记
	private static final int REQUESTCODE_CUTTING = 2; // 图片裁切标记

	private GamePintuLayout mGamePintuLayout;
	private TextView mTime;
	private RevealLayout mRevealLayout;
	private RelativeLayout mLayout;

	private ImageView mBackImg;
	private TextView mTitle;
	private ImageView mTitleMore;
	private Button mStart;
	private ImageView mOriginalImage;
	private int i = -1;
	private int mGamePicture;
	private Bitmap bitmap;
	private boolean flag = false;
	// 上下文菜单
	private DialogFragment mMenuDialogFragment;
	private FragmentManager mFragmentManager;

	// 游戏图片-男
	private int gameBitmapBoy[] = { R.drawable.boy_a, R.drawable.boy_b,
			R.drawable.boy_c, R.drawable.boy_d, R.drawable.boy_e,
			R.drawable.boy_f, R.drawable.game_a };
	// 游戏图片-女
	private int gameBitmapGirl[] = { R.drawable.girl_a, R.drawable.girl_b,
			R.drawable.girl_c, R.drawable.girl_d, R.drawable.girl_e,
			R.drawable.game_a, R.drawable.game_b };

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.game_puzzle);

		// 将activity加入到AppManager堆栈中
		AppManager.getAppManager().addActivity(this);
		// 沉浸式状态栏设置
		initSystemBar();
		initView();
		initListener();

	}

	private void initView() {

		mBackImg = (ImageView) findViewById(R.id.back_img);
		mTitle = (TextView) findViewById(R.id.title);
		mTitleMore = (ImageView) findViewById(R.id.title_more);
		mOriginalImage = (ImageView) findViewById(R.id.original_image);
		mStart = (Button) findViewById(R.id.start);
		mTime = (TextView) findViewById(R.id.countdown);
		mGamePintuLayout = (GamePintuLayout) findViewById(R.id.id_gamepintu);

		mTitle.setText("解密游戏");
		mGamePicture = (int) (Math.random() * 7);
		switch (AVUser.getCurrentUser().get("gender").toString()) {
		case "男":
			mGamePintuLayout.setTimeEnabled(true, BitmapFactory.decodeResource(
					getResources(), gameBitmapGirl[mGamePicture]));
			mOriginalImage.setImageResource(gameBitmapGirl[mGamePicture]);
			break;

		case "女":
			mGamePintuLayout.setTimeEnabled(true, BitmapFactory.decodeResource(
					getResources(), gameBitmapBoy[mGamePicture]));
			mOriginalImage.setImageResource(gameBitmapBoy[mGamePicture]);
			break;
		}

		mRevealLayout = (RevealLayout) findViewById(R.id.reveal_layout);
		mLayout = (RelativeLayout) findViewById(R.id.layout);
		mLayout.setBackgroundColor(Color.WHITE);

		// 上下文菜单
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

		MenuObject current = new MenuObject("当前原图");
		current.setResource(R.drawable.game_current);

		MenuObject potho = new MenuObject("图片来源");
		potho.setResource(R.drawable.game_potho);

		MenuObject about = new MenuObject("关于游戏");
		about.setResource(R.drawable.game_about);

		menuObjects.add(close);
		menuObjects.add(current);
		menuObjects.add(potho);
		menuObjects.add(about);
		return menuObjects;
	}

	private void initListener() {

		mBackImg.setOnClickListener(new android.view.View.OnClickListener() {

			@Override
			public void onClick(View v) {
				PuzzleActivity.this.finish();
			}
		});

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

		mStart.setOnClickListener(new android.view.View.OnClickListener() {

			@Override
			public void onClick(View v) {
				mOriginalImage.setVisibility(View.GONE);
				gameStart();
			}
		});

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
	}

	private void gameStart() {

		mGamePintuLayout.setVisibility(View.VISIBLE);
		mGamePintuLayout.setOnGamePintuListener(new GamePintuListener() {
			@Override
			public void timechanged(int currentTime) {
				mTime.setText("" + currentTime);
			}

			@Override
			public void nextLevel(final int nextLevel) {
				// new AlertDialog.Builder(PuzzleActivity.this)
				// .setTitle("Game Info").setMessage("LEVEL UP !!!")
				// .setPositiveButton("NEXT LEVEL", new OnClickListener() {
				// @Override
				// public void onClick(DialogInterface dialog,
				// int which) {
				// mGamePintuLayout.nextLevel();
				// }
				// }).show();
				showProgress();
			}

			@Override
			public void gameover() {
				new SweetAlertDialog(PuzzleActivity.this,
						SweetAlertDialog.WARNING_TYPE)
						.setTitleText("游戏失败!")
						.setCancelText("退出游戏")
						.setConfirmText("重新开始")
						.showCancelButton(true)
						.setCancelClickListener(
								new SweetAlertDialog.OnSweetClickListener() {
									@Override
									public void onClick(SweetAlertDialog sDialog) {
										sDialog.dismiss();

										final SweetAlertDialog nAlertDialog = new SweetAlertDialog(
												PuzzleActivity.this,
												SweetAlertDialog.PROGRESS_TYPE);
										nAlertDialog.setTitleText("正在退出游戏")
												.setContentText("请稍后");
										nAlertDialog.show();
										nAlertDialog.setCancelable(false);
										new CountDownTimer(800 * 4, 800) {
											public void onTick(
													long millisUntilFinished) {
												colorProgress(nAlertDialog);
											}

											public void onFinish() {
												i = -1;
												nAlertDialog.dismiss();
												PuzzleActivity.this.finish();
											}
										}.start();
									}
								})
						.setConfirmClickListener(
								new SweetAlertDialog.OnSweetClickListener() {
									@Override
									public void onClick(
											final SweetAlertDialog sDialog) {
										sDialog.dismiss();
										mGamePintuLayout.restart();
									}
								}).show();
			}
		});
	}

	private void showProgress() {

		final SweetAlertDialog pDialog = new SweetAlertDialog(this,
				SweetAlertDialog.PROGRESS_TYPE).setTitleText("恭喜解密完成");
		pDialog.show();
		pDialog.setCancelable(false);
		new CountDownTimer(800 * 4, 800) {
			public void onTick(long millisUntilFinished) {
				// you can change the progress bar color by ProgressHelper
				// every 800 millis
				i++;
				colorProgress(pDialog);
			}

			public void onFinish() {
				i = -1;
				pDialog.dismiss();
				Intent intent = new Intent(PuzzleActivity.this,
						FriendInformationAddActivity.class);
				intent.putExtra("receiveUser",
						getIntent().getStringExtra("receiveUser")); // 接收验证的
				intent.putExtra("sendUsername",
						getIntent().getStringExtra("sendUsername")); // 发送验证的(当前用户)
				intent.putExtra("distance",
						getIntent().getStringExtra("distance"));
				intent.putExtra("email", getIntent().getStringExtra("email"));
				intent.putExtra("gender", getIntent().getStringExtra("gender"));
				intent.putExtra("locationTime",
						getIntent().getStringExtra("locationTime"));

				startActivity(intent);
				PuzzleActivity.this.finish();
			}
		}.start();

	}

	// 进度条颜色
	public void colorProgress(SweetAlertDialog pDialog) {
		i++;
		switch (i) {
		case 0:
			pDialog.getProgressHelper().setBarColor(
					getResources().getColor(android.R.color.holo_blue_bright));
			break;

		case 1:
			pDialog.getProgressHelper().setBarColor(
					getResources().getColor(android.R.color.holo_green_light));
			break;
		case 2:
			pDialog.getProgressHelper().setBarColor(
					getResources().getColor(android.R.color.holo_orange_light));
			break;

		case 3:
			pDialog.getProgressHelper().setBarColor(
					getResources().getColor(android.R.color.holo_red_light));
			break;
		}
	}

	@TargetApi(19)
	public void initSystemBar() {
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

	// 上下文菜单
	@Override
	public void onMenuItemClick(View clickedView, int position) {
		switch (position) {
		case 0:
			break;

		case 1:
			showCurrentImg();
			break;

		case 2:
			showPictureFrom();
			break;

		case 3:
			showAboutGame();
			break;

		default:
			break;
		}
	}

	private void showCurrentImg() {

		if (!flag) {

			switch (AVUser.getCurrentUser().get("gender").toString()) {
			case "男":
				new SweetAlertDialog(this, SweetAlertDialog.CUSTOM_IMAGE_TYPE)
						.setTitleText("当前原图").setContentText("其实我很厉害，只是厉害的不明显")
						.setCustomImage(gameBitmapGirl[mGamePicture]).show();
				break;

			case "女":
				new SweetAlertDialog(this, SweetAlertDialog.CUSTOM_IMAGE_TYPE)
						.setTitleText("当前原图").setContentText("其实我很厉害，只是厉害的不明显")
						.setCustomImage(gameBitmapBoy[mGamePicture]).show();
				break;
			}

		} else {
			new SweetAlertDialog(this, SweetAlertDialog.CUSTOM_IMAGE_TYPE)
					.setTitleText("原图").setContentText("其实我很厉害，只是厉害的不明显")
					.setCustomImage(new BitmapDrawable(getResources(), bitmap))
					.show();
		}
	}

	private void showAboutGame() {

		new SweetAlertDialog(this, SweetAlertDialog.CUSTOM_IMAGE_TYPE)
				.setTitleText("关于游戏")
				.setContentText("拼图游戏，即在规定的时间内，把打乱的切图拼凑为正确图片。")
				.setCustomImage(R.drawable.game_about_dialog).show();
	}

	private void showPictureFrom() {
		new SweetAlertDialog(PuzzleActivity.this, SweetAlertDialog.WARNING_TYPE)
				.setTitleText("请选择图片来源")
				.setCancelText("图库")
				.setConfirmText("拍照")
				.showCancelButton(true)
				.setCancelClickListener(
						new SweetAlertDialog.OnSweetClickListener() {
							@Override
							public void onClick(SweetAlertDialog sDialog) {
								sDialog.dismiss();
								showGallery();
							}
						})
				.setConfirmClickListener(
						new SweetAlertDialog.OnSweetClickListener() {
							@Override
							public void onClick(final SweetAlertDialog sDialog) {
								sDialog.dismiss();
								showCamera();
							}
						}).show();
	}

	private void showGallery() {
		Intent pickIntent = new Intent(Intent.ACTION_PICK, null);
		pickIntent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
				"image/*");
		startActivityForResult(pickIntent, REQUESTCODE_PICK);
	}

	private void showCamera() {
		// 执行拍照前，应该先判断SD卡是否存在
		String SDState = Environment.getExternalStorageState();
		if (SDState.equals(Environment.MEDIA_MOUNTED)) {

			Intent takeIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
			// 下面这句指定调用相机拍照后的照片存储的路径
			takeIntent.putExtra(MediaStore.EXTRA_OUTPUT,
					Uri.fromFile(new File(Environment
							.getExternalStorageDirectory(), IMAGE_FILE_NAME)));
			startActivityForResult(takeIntent, REQUESTCODE_TAKE);

		} else {
			Toast.makeText(PuzzleActivity.this, "内存卡不存在", Toast.LENGTH_LONG)
					.show();
		}
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {

		switch (requestCode) {

		case REQUESTCODE_PICK:
			try {
				startPhotoZoom(data.getData());
			} catch (NullPointerException e) {
				e.printStackTrace();// 用户点击取消操作
			}
			break;

		case REQUESTCODE_TAKE:// 调用相机拍照
			File temp = new File(Environment.getExternalStorageDirectory()
					+ "/" + IMAGE_FILE_NAME);
			startPhotoZoom(Uri.fromFile(temp));
			break;

		case REQUESTCODE_CUTTING:// 取得裁剪后的图片
			if (data != null) {
				setPicToView(data);
			}
			break;
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	/**
	 * 裁剪图片方法实现
	 * 
	 * @param uri
	 */
	public void startPhotoZoom(Uri uri) {
		Intent intent = new Intent("com.android.camera.action.CROP");
		intent.setDataAndType(uri, "image/*");
		// crop=true是设置在开启的Intent中设置显示的VIEW可裁剪
		intent.putExtra("crop", "true");
		// aspectX aspectY 是宽高的比例
		intent.putExtra("aspectX", 1);
		intent.putExtra("aspectY", 1);
		// outputX outputY 是裁剪图片宽高
		intent.putExtra("outputX", 450);
		intent.putExtra("outputY", 450);
		intent.putExtra("return-data", true);
		startActivityForResult(intent, REQUESTCODE_CUTTING);
	}

	/**
	 * 保存裁剪之后的图片数据
	 * 
	 * @param picdata
	 */
	private void setPicToView(Intent picdata) {
		Bundle extras = picdata.getExtras();
		if (extras != null) {
			// 取得SDCard图片路径做显示
			flag = true;
			bitmap = extras.getParcelable("data");
			mOriginalImage.setImageBitmap(bitmap);
			// 图片本地路径
			String urlpath = FileUtil.saveFile(PuzzleActivity.this,
					IMAGE_FILE_NAME, bitmap);
			mGamePintuLayout.setTimeEnabled(true, bitmap);
		}
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
			PuzzleActivity.this.finish();
			return false;
		}
		return super.onKeyDown(keyCode, event);
	}

}
