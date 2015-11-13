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
 * @description ע�����
 * 
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

	private static final String IMAGE_FILE_NAME = "avatar.jpg";// ͷ���ļ�����
	private String avaterUrl; // ͼƬ����·��
	private static final int REQUESTCODE_TAKE = 1; // ������ձ��
	private static final int REQUESTCODE_CUTTING = 2; // ͼƬ���б��
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

	// ע�ᰴť����
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
										progressDialog("��");
									} else {
										progressDialog("Ů");
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
					mMyDialog.successDialog("ע��ɹ�!");
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
							// ����ɹ�
							String installationId = AVInstallation
									.getCurrentInstallation()
									.getInstallationId();
							// ���� installationId ���û���Ȳ�������
							AVService.signUp(mUsernameString, mPasswordString,
									mEmailString, gender, installationId,
									signUpCallback);
						} else {
							// ����ʧ�ܣ����������Ϣ
						}
					}
				});
	}

	private void progressDialog(final String gender) {
		final SweetAlertDialog pDialog = new SweetAlertDialog(
				RegisterActivity.this, SweetAlertDialog.PROGRESS_TYPE)
				.setTitleText("ƴ��������");
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

	// ȷ�ϴ����
	public void cameraConfirm() {

		new SweetAlertDialog(RegisterActivity.this,
				SweetAlertDialog.WARNING_TYPE)
				.setTitleText("������Ϊͷ��?")
				.setCancelText("ȡ��")
				.setConfirmText("ȷ��")
				.showCancelButton(true)
				.setCancelClickListener(
						new SweetAlertDialog.OnSweetClickListener() {
							@Override
							public void onClick(SweetAlertDialog sDialog) {
								// reuse previous dialog instance, keep
								// widget user state, reset them if you need
								sDialog.setTitleText("��ȡ��!")
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
								nAlertDialog.setTitleText("������ɯ��΢Ц")
										.setContentText("������ʼ");
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
	 * ���ջ�ȡͼƬ
	 */
	private void takePhoto() {
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
			Toast.makeText(RegisterActivity.this, "�ڴ濨������", Toast.LENGTH_LONG)
					.show();
		}
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {

		switch (requestCode) {

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
		intent.putExtra("outputX", 200);
		intent.putExtra("outputY", 200);
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
			mAvateritmap = extras.getParcelable("data");
			Drawable drawable = new BitmapDrawable(null, mAvateritmap);
			mCameraAvatar.setImageDrawable(drawable);

			avaterUrl = FileUtil.saveFile(RegisterActivity.this,
					IMAGE_FILE_NAME, mAvateritmap);

			// ѹ��ͼƬ
			// BitmapFactory.Options option = new BitmapFactory.Options();
			// ѹ��ͼƬ:��ʾ����ͼ��СΪԭʼͼƬ��С�ļ���֮һ��1Ϊԭͼ
			// option.inSampleSize = 2;
			// ����ͼƬ��SDCard·������Bitmap
			// genderBitmap = BitmapFactory.decodeFile(avaterUrl, option);

			Toast.makeText(RegisterActivity.this,
					"ͷ�񱣴���:" + avaterUrl.replaceAll(IMAGE_FILE_NAME, ""),
					Toast.LENGTH_LONG).show();
			isTakeGenderFlag = true;

			// ���̺߳�̨�ϴ������
			// pd = ProgressDialog.show(RegisterActivity.this, null,
			// "�����ϴ�ͼƬ�����Ժ�...");
			// new Thread(uploadImageRunnable).start();
		}
	}

}
