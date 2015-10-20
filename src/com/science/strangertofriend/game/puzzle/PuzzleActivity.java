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
 * @description ƴͼ��Ϸ
 * 
 * @author ����Science ������
 * @school University of South China
 * @email chentushen.science@gmail.com,274240671@qq.com
 * @2015-5-24
 * 
 */

public class PuzzleActivity extends ActionBarActivity implements
		OnMenuItemClickListener {

	private static final String IMAGE_FILE_NAME = "puzzle_game.jpg";// ͷ���ļ�����
	private static final int REQUESTCODE_PICK = 0; // ���ѡͼ���
	private static final int REQUESTCODE_TAKE = 1; // ������ձ��
	private static final int REQUESTCODE_CUTTING = 2; // ͼƬ���б��

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
	// �����Ĳ˵�
	private DialogFragment mMenuDialogFragment;
	private FragmentManager mFragmentManager;

	// ��ϷͼƬ-��
	private int gameBitmapBoy[] = { R.drawable.boy_a, R.drawable.boy_b,
			R.drawable.boy_c, R.drawable.boy_d, R.drawable.boy_e,
			R.drawable.boy_f, R.drawable.game_a };
	// ��ϷͼƬ-Ů
	private int gameBitmapGirl[] = { R.drawable.girl_a, R.drawable.girl_b,
			R.drawable.girl_c, R.drawable.girl_d, R.drawable.girl_e,
			R.drawable.game_a, R.drawable.game_b };

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.game_puzzle);

		// ��activity���뵽AppManager��ջ��
		AppManager.getAppManager().addActivity(this);
		// ����ʽ״̬������
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

		mTitle.setText("������Ϸ");
		mGamePicture = (int) (Math.random() * 7);
		switch (AVUser.getCurrentUser().get("gender").toString()) {
		case "��":
			mGamePintuLayout.setTimeEnabled(true, BitmapFactory.decodeResource(
					getResources(), gameBitmapGirl[mGamePicture]));
			mOriginalImage.setImageResource(gameBitmapGirl[mGamePicture]);
			break;

		case "Ů":
			mGamePintuLayout.setTimeEnabled(true, BitmapFactory.decodeResource(
					getResources(), gameBitmapBoy[mGamePicture]));
			mOriginalImage.setImageResource(gameBitmapBoy[mGamePicture]);
			break;
		}

		mRevealLayout = (RevealLayout) findViewById(R.id.reveal_layout);
		mLayout = (RelativeLayout) findViewById(R.id.layout);
		mLayout.setBackgroundColor(Color.WHITE);

		// �����Ĳ˵�
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

		MenuObject current = new MenuObject("��ǰԭͼ");
		current.setResource(R.drawable.game_current);

		MenuObject potho = new MenuObject("ͼƬ��Դ");
		potho.setResource(R.drawable.game_potho);

		MenuObject about = new MenuObject("������Ϸ");
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
						.setTitleText("��Ϸʧ��!")
						.setCancelText("�˳���Ϸ")
						.setConfirmText("���¿�ʼ")
						.showCancelButton(true)
						.setCancelClickListener(
								new SweetAlertDialog.OnSweetClickListener() {
									@Override
									public void onClick(SweetAlertDialog sDialog) {
										sDialog.dismiss();

										final SweetAlertDialog nAlertDialog = new SweetAlertDialog(
												PuzzleActivity.this,
												SweetAlertDialog.PROGRESS_TYPE);
										nAlertDialog.setTitleText("�����˳���Ϸ")
												.setContentText("���Ժ�");
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
				SweetAlertDialog.PROGRESS_TYPE).setTitleText("��ϲ�������");
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
						getIntent().getStringExtra("receiveUser")); // ������֤��
				intent.putExtra("sendUsername",
						getIntent().getStringExtra("sendUsername")); // ������֤��(��ǰ�û�)
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

	// ��������ɫ
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

	// �����Ĳ˵�
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
			case "��":
				new SweetAlertDialog(this, SweetAlertDialog.CUSTOM_IMAGE_TYPE)
						.setTitleText("��ǰԭͼ").setContentText("��ʵ�Һ�������ֻ�������Ĳ�����")
						.setCustomImage(gameBitmapGirl[mGamePicture]).show();
				break;

			case "Ů":
				new SweetAlertDialog(this, SweetAlertDialog.CUSTOM_IMAGE_TYPE)
						.setTitleText("��ǰԭͼ").setContentText("��ʵ�Һ�������ֻ�������Ĳ�����")
						.setCustomImage(gameBitmapBoy[mGamePicture]).show();
				break;
			}

		} else {
			new SweetAlertDialog(this, SweetAlertDialog.CUSTOM_IMAGE_TYPE)
					.setTitleText("ԭͼ").setContentText("��ʵ�Һ�������ֻ�������Ĳ�����")
					.setCustomImage(new BitmapDrawable(getResources(), bitmap))
					.show();
		}
	}

	private void showAboutGame() {

		new SweetAlertDialog(this, SweetAlertDialog.CUSTOM_IMAGE_TYPE)
				.setTitleText("������Ϸ")
				.setContentText("ƴͼ��Ϸ�����ڹ涨��ʱ���ڣ��Ѵ��ҵ���ͼƴ��Ϊ��ȷͼƬ��")
				.setCustomImage(R.drawable.game_about_dialog).show();
	}

	private void showPictureFrom() {
		new SweetAlertDialog(PuzzleActivity.this, SweetAlertDialog.WARNING_TYPE)
				.setTitleText("��ѡ��ͼƬ��Դ")
				.setCancelText("ͼ��")
				.setConfirmText("����")
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
		// ִ������ǰ��Ӧ�����ж�SD���Ƿ����
		String SDState = Environment.getExternalStorageState();
		if (SDState.equals(Environment.MEDIA_MOUNTED)) {

			Intent takeIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
			// �������ָ������������պ����Ƭ�洢��·��
			takeIntent.putExtra(MediaStore.EXTRA_OUTPUT,
					Uri.fromFile(new File(Environment
							.getExternalStorageDirectory(), IMAGE_FILE_NAME)));
			startActivityForResult(takeIntent, REQUESTCODE_TAKE);

		} else {
			Toast.makeText(PuzzleActivity.this, "�ڴ濨������", Toast.LENGTH_LONG)
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
				e.printStackTrace();// �û����ȡ������
			}
			break;

		case REQUESTCODE_TAKE:// �����������
			File temp = new File(Environment.getExternalStorageDirectory()
					+ "/" + IMAGE_FILE_NAME);
			startPhotoZoom(Uri.fromFile(temp));
			break;

		case REQUESTCODE_CUTTING:// ȡ�òü����ͼƬ
			if (data != null) {
				setPicToView(data);
			}
			break;
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	/**
	 * �ü�ͼƬ����ʵ��
	 * 
	 * @param uri
	 */
	public void startPhotoZoom(Uri uri) {
		Intent intent = new Intent("com.android.camera.action.CROP");
		intent.setDataAndType(uri, "image/*");
		// crop=true�������ڿ�����Intent��������ʾ��VIEW�ɲü�
		intent.putExtra("crop", "true");
		// aspectX aspectY �ǿ�ߵı���
		intent.putExtra("aspectX", 1);
		intent.putExtra("aspectY", 1);
		// outputX outputY �ǲü�ͼƬ���
		intent.putExtra("outputX", 450);
		intent.putExtra("outputY", 450);
		intent.putExtra("return-data", true);
		startActivityForResult(intent, REQUESTCODE_CUTTING);
	}

	/**
	 * ����ü�֮���ͼƬ����
	 * 
	 * @param picdata
	 */
	private void setPicToView(Intent picdata) {
		Bundle extras = picdata.getExtras();
		if (extras != null) {
			// ȡ��SDCardͼƬ·������ʾ
			flag = true;
			bitmap = extras.getParcelable("data");
			mOriginalImage.setImageBitmap(bitmap);
			// ͼƬ����·��
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
