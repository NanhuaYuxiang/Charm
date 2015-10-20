package com.science.strangertofriend.ui;

import java.io.File;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import cn.pedant.SweetAlert.SweetAlertDialog;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVInstallation;
import com.avos.avoscloud.SaveCallback;
import com.avos.avoscloud.SignUpCallback;
import com.science.strangertofriend.MainActivity;
import com.science.strangertofriend.R;
import com.science.strangertofriend.utils.AVService;
import com.science.strangertofriend.utils.FileUtil;
import com.science.strangertofriend.widget.MyDialog;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * @description 注册界面
 * 
 * @author 幸运Science 陈土燊
 * @school University of South China
 * @email chentushen.science@gmail.com,274240671@qq.com
 * @2015-4-26
 * 
 */

public class RegisterActivity extends BaseActivity {

	private CircleImageView mCameraAvatar;
	private ImageView mGenderBoy, mGenderGirl;
	private EditText mUserName, mEmail, mPassword;
	private Button mRegisterButton;
	private MyDialog mMyDialog;
	private String mUsernameString, mPasswordString, mEmailString;
	private Boolean mBoyflag = false, mGirlFlag = false;
	private Boolean mGenderPicBoyFlag = true, mGenderPicGirlFlag = true;

	private static final String IMAGE_FILE_NAME = "avatar.jpg";// 头像文件名称
	private String avaterUrl; // 图片本地路径
	private static final int REQUESTCODE_TAKE = 1; // 相机拍照标记
	private static final int REQUESTCODE_CUTTING = 2; // 图片裁切标记
	private Bitmap mAvateritmap;
	private Boolean isTakeGenderFlag = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.register_layout);

		initComponent();
		addListener();
	}

	public void initComponent() {

		mCameraAvatar = (CircleImageView) findViewById(R.id.camera_avatar);
		mGenderBoy = (ImageView) findViewById(R.id.boy);
		mGenderGirl = (ImageView) findViewById(R.id.girl);
		mUserName = (EditText) findViewById(R.id.username);
		mEmail = (EditText) findViewById(R.id.email);
		mPassword = (EditText) findViewById(R.id.password);
		mRegisterButton = (Button) findViewById(R.id.register);
		mMyDialog = new MyDialog(RegisterActivity.this);

	}

	// 注册按钮监听
	public void addListener() {

		mCameraAvatar.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				cameraConfirm();
			}
		});

		mGenderBoy.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				mBoyflag = true;
				mGirlFlag = false;
				if (mGenderPicBoyFlag) {
					mGenderBoy.setImageDrawable(getResources().getDrawable(
							R.drawable.click_after_boy));
					mGenderGirl.setImageDrawable(getResources().getDrawable(
							R.drawable.girl));
					mGenderPicBoyFlag = false;
					mGenderPicGirlFlag = true;
				} else {
					mGenderBoy.setImageDrawable(getResources().getDrawable(
							R.drawable.boy));
					mGenderGirl.setImageDrawable(getResources().getDrawable(
							R.drawable.girl));
					mGenderPicBoyFlag = true;
					mGenderPicGirlFlag = true;
				}
			}
		});

		mGenderGirl.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				mBoyflag = false;
				mGirlFlag = true;
				if (mGenderPicGirlFlag) {
					mGenderGirl.setImageDrawable(getResources().getDrawable(
							R.drawable.click_after_girl));
					mGenderBoy.setImageDrawable(getResources().getDrawable(
							R.drawable.boy));
					mGenderPicGirlFlag = false;
					mGenderPicBoyFlag = true;
				} else {
					mGenderGirl.setImageDrawable(getResources().getDrawable(
							R.drawable.girl));
					mGenderBoy.setImageDrawable(getResources().getDrawable(
							R.drawable.boy));
					mGenderPicGirlFlag = true;
					mGenderPicBoyFlag = true;
				}
			}
		});

		mRegisterButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				mUsernameString = mUserName.getText().toString();
				mPasswordString = mPassword.getText().toString();
				mEmailString = mEmail.getText().toString();
				if (!mPasswordString.isEmpty()) {
					if (!mUsernameString.isEmpty()) {
						if (!mEmailString.isEmpty()) {
							if (isTakeGenderFlag) {
								if (mBoyflag || mGirlFlag) {
									if (mBoyflag) {
										progressDialog("男");
									} else {
										progressDialog("女");
									}
								} else {
									Toast.makeText(
											RegisterActivity.this,
											R.string.error_register_gender_null,
											Toast.LENGTH_LONG).show();
								}
							} else {
								Toast.makeText(
										RegisterActivity.this,
										R.string.error_register_take_gender_null,
										Toast.LENGTH_LONG).show();
							}
						} else {
							Toast.makeText(RegisterActivity.this,
									R.string.error_register_email_address_null,
									Toast.LENGTH_LONG).show();
						}
					} else {
						Toast.makeText(RegisterActivity.this,
								R.string.error_register_user_name_null,
								Toast.LENGTH_LONG).show();
					}
				} else {
					Toast.makeText(RegisterActivity.this,
							R.string.error_register_password_null,
							Toast.LENGTH_LONG).show();
				}

			}
		});
	}

	public void register(final String gender) {
		final SignUpCallback signUpCallback = new SignUpCallback() {
			public void done(AVException e) {
				if (e == null) {
					mMyDialog.successDialog("注册成功!");
					AVService.uploadImage(mUsernameString, mEmailString,
							avaterUrl, gender);

					Intent mainIntent = new Intent(RegisterActivity.this,
							MainActivity.class);
					mainIntent.putExtra("avater", mAvateritmap);
					startActivity(mainIntent);
					RegisterActivity.this.finish();
				} else {
					switch (e.getCode()) {
					case 202:
						Toast.makeText(
								RegisterActivity.this,
								getString(R.string.error_register_user_name_repeat),
								Toast.LENGTH_LONG).show();
						break;
					case 203:
						Toast.makeText(
								RegisterActivity.this,
								getString(R.string.error_register_email_repeat),
								Toast.LENGTH_LONG).show();
						break;
					default:
						Toast.makeText(RegisterActivity.this,
								getString(R.string.network_not_connected),
								Toast.LENGTH_LONG).show();
						break;
					}
				}
			}
		};

		AVInstallation.getCurrentInstallation().saveInBackground(
				new SaveCallback() {
					public void done(AVException e) {
						if (e == null) {
							// 保存成功
							String installationId = AVInstallation
									.getCurrentInstallation()
									.getInstallationId();
							// 关联 installationId 到用户表等操作……
							AVService.signUp(mUsernameString, mPasswordString,
									mEmailString, gender, installationId,
									signUpCallback);
						} else {
							// 保存失败，输出错误信息
						}
					}
				});
	}

	private void progressDialog(final String gender) {
		final SweetAlertDialog pDialog = new SweetAlertDialog(
				RegisterActivity.this, SweetAlertDialog.PROGRESS_TYPE)
				.setTitleText("拼命加载中");
		pDialog.show();
		pDialog.setCancelable(false);
		new CountDownTimer(800 * 4, 800) {
			public void onTick(long millisUntilFinished) {
				// you can change the progress bar color by ProgressHelper
				// every 800 millis
				colorProgress(pDialog);
			}

			public void onFinish() {
				i = -1;
				register(gender);
				pDialog.dismiss();
			}
		}.start();
	}

	// 确认打开相机
	public void cameraConfirm() {

		new SweetAlertDialog(RegisterActivity.this,
				SweetAlertDialog.WARNING_TYPE)
				.setTitleText("自拍作为头像?")
				.setCancelText("取消")
				.setConfirmText("确认")
				.showCancelButton(true)
				.setCancelClickListener(
						new SweetAlertDialog.OnSweetClickListener() {
							@Override
							public void onClick(SweetAlertDialog sDialog) {
								// reuse previous dialog instance, keep
								// widget user state, reset them if you need
								sDialog.setTitleText("已取消!")
										.setConfirmText("OK")
										.showCancelButton(false)
										.setCancelClickListener(null)
										.setConfirmClickListener(null)
										.changeAlertType(
												SweetAlertDialog.SUCCESS_TYPE);

								// or you can new a SweetAlertDialog to show
								/*
								 * sDialog.dismiss(); new
								 * SweetAlertDialog(SampleActivity.this,
								 * SweetAlertDialog.ERROR_TYPE)
								 * .setTitleText("Cancelled!") .setContentText
								 * ("Your imaginary file is safe :)")
								 * .setConfirmText("OK") .show();
								 */
							}
						})
				.setConfirmClickListener(
						new SweetAlertDialog.OnSweetClickListener() {
							@Override
							public void onClick(final SweetAlertDialog sDialog) {
								sDialog.dismiss();

								final SweetAlertDialog nAlertDialog = new SweetAlertDialog(
										RegisterActivity.this,
										SweetAlertDialog.PROGRESS_TYPE);
								nAlertDialog.setTitleText("蒙娜丽莎的微笑")
										.setContentText("即将开始");
								nAlertDialog.show();
								nAlertDialog.setCancelable(false);
								new CountDownTimer(800 * 4, 800) {
									public void onTick(long millisUntilFinished) {
										// you can change the progress bar color
										// by ProgressHelper
										// every 800 millis
										colorProgress(nAlertDialog);
									}

									public void onFinish() {
										i = -1;
										nAlertDialog.dismiss();
										takePhoto();
									}
								}.start();
							}
						}).show();
	}

	/**
	 * 拍照获取图片
	 */
	private void takePhoto() {
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
			Toast.makeText(RegisterActivity.this, "内存卡不存在", Toast.LENGTH_LONG)
					.show();
		}
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {

		switch (requestCode) {

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
		intent.putExtra("outputX", 200);
		intent.putExtra("outputY", 200);
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
			mAvateritmap = extras.getParcelable("data");
			Drawable drawable = new BitmapDrawable(null, mAvateritmap);
			mCameraAvatar.setImageDrawable(drawable);

			avaterUrl = FileUtil.saveFile(RegisterActivity.this,
					IMAGE_FILE_NAME, mAvateritmap);

			// 压缩图片
			// BitmapFactory.Options option = new BitmapFactory.Options();
			// 压缩图片:表示缩略图大小为原始图片大小的几分之一，1为原图
			// option.inSampleSize = 2;
			// 根据图片的SDCard路径读出Bitmap
			// genderBitmap = BitmapFactory.decodeFile(avaterUrl, option);

			Toast.makeText(RegisterActivity.this,
					"头像保存在:" + avaterUrl.replaceAll(IMAGE_FILE_NAME, ""),
					Toast.LENGTH_LONG).show();
			isTakeGenderFlag = true;

			// 新线程后台上传服务端
			// pd = ProgressDialog.show(RegisterActivity.this, null,
			// "正在上传图片，请稍候...");
			// new Thread(uploadImageRunnable).start();
		}
	}

}
