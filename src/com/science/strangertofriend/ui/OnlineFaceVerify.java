package com.science.strangertofriend.ui;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.UnsupportedEncodingException;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.DialogInterface.OnCancelListener;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import cn.pedant.SweetAlert.SweetAlertDialog;

import com.avos.avoscloud.AVUser;
import com.iflytek.cloud.ErrorCode;
import com.iflytek.cloud.FaceRequest;
import com.iflytek.cloud.RequestListener;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechError;
import com.science.strangertofriend.MainActivity;
import com.science.strangertofriend.R;
import com.science.strangertofriend.utils.FaceUtil;
/**
 * 在线人脸验证
 * @author 赵鑫
 *
 */
public class OnlineFaceVerify extends Activity implements OnClickListener {
	private Button bt_check, bt_camera;
	private ImageView img_user;// 用户拍照后要验证的头像

	//private final int REQUEST_PICTURE_CHOOSE = 1;
	private final int REQUEST_CAMERA_IMAGE = 2;

	private Bitmap mImage = null;
	private byte[] mImageData = null;
	// authid为6-18个字符长度，用于唯一标识用户
	private String mAuthid = null;
	private Toast mToast;
	// 进度对话框
	private ProgressDialog mProDialog;
	// 拍照得到的照片文件
	private File mPictureFile;
	// FaceRequest对象，集成了人脸识别的各种功能
	private FaceRequest mFaceRequest;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_facecheck);
		init();
	}

	public  void init() {
		bt_check = (Button) findViewById(R.id.bt_check);
		bt_camera = (Button) findViewById(R.id.bt_camera);
		bt_camera.setOnClickListener(this);
		bt_check.setOnClickListener(this);
		img_user = (ImageView) findViewById(R.id.img_user);
		mToast = Toast.makeText(this, "", Toast.LENGTH_SHORT);

		mProDialog = new ProgressDialog(this);
		mProDialog.setCancelable(true);
		mProDialog.setTitle("请稍后");

		mProDialog.setOnCancelListener(new OnCancelListener() {

			@Override
			public void onCancel(DialogInterface dialog) {
				// cancel进度框时,取消正在进行的操作
				if (null != mFaceRequest) {
					mFaceRequest.cancel();
				}
			}
		});

		mFaceRequest = new FaceRequest(this);
	}

	public RequestListener mRequestListener = new RequestListener() {

		@Override
		public void onEvent(int eventType, Bundle params) {
		}

		@Override
		public void onBufferReceived(byte[] buffer) {
			if (null != mProDialog) {
				mProDialog.dismiss();
			}

			try {
				String result = new String(buffer, "utf-8");
				Log.d("FaceDemo", result);

				JSONObject object = new JSONObject(result);
				verify(object);
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			} catch (JSONException e) {
			}
		}

		@Override
		public void onCompleted(SpeechError error) {
			if (null != mProDialog) {
				mProDialog.dismiss();
			}

			if (error != null) {
				switch (error.getErrorCode()) {
				case ErrorCode.MSP_ERROR_ALREADY_EXIST:
					showTip("authid已经被注册，请更换后再试");
					break;

				default:
					showTip(error.getPlainDescription(true));
					break;
				}
			}
		}
	};

	public void verify(JSONObject obj) throws JSONException {
		int ret = obj.getInt("ret");
		if (ret != 0) {
			showTip("验证失败");
			
			return;
		}
		if ("success".equals(obj.get("rst"))) {
			if (obj.getBoolean("verf")) {
				showTip("通过验证，欢迎回来！");
				double latitude=getIntent().getIntExtra("latitude", 0);
				Intent intent=new Intent(this,MainActivity.class);
				intent.putExtra("latitude", "0");
				startActivity(intent);
				OnlineFaceVerify.this.finish();
			} else {
//				showTip("验证不通过");
				new SweetAlertDialog(this, SweetAlertDialog.ERROR_TYPE)
				.setTitleText(" ")
				.setContentText("验证失败，请重试")
				.setConfirmText("Yes")
				.show();
			}
		} else {
			showTip("验证失败");
		}
	}

	public void showTip(final String str) {
		mToast.setText(str);
		mToast.show();
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.bt_check:
			mAuthid=AVUser.getCurrentUser().getObjectId();
			if(TextUtils.isEmpty(mAuthid)){
				showTip("authid不能为空");
				return;
			}
			if (null != mImageData) {
				mProDialog.setMessage("验证中...");
				mProDialog.show();
				// 设置用户标识，格式为6-18个字符（由字母、数字、下划线组成，不得以数字开头，不能包含空格）。
				// 当不设置时，云端将使用用户设备的设备ID来标识终端用户。
				mFaceRequest.setParameter(SpeechConstant.AUTH_ID, mAuthid);
				mFaceRequest.setParameter(SpeechConstant.WFR_SST, "verify");
				mFaceRequest.sendRequest(mImageData, mRequestListener);
			} else {
				showTip("请选择图片后再验证");
			}
			break;
		case R.id.bt_camera:
			// 设置相机拍照后照片保存路径
			mPictureFile = new File(Environment.getExternalStorageDirectory(),
					"picture" + System.currentTimeMillis() / 1000 + ".jpg");
			// 启动拍照,并保存到临时文件
			Intent mIntent = new Intent();
			mIntent.setAction(MediaStore.ACTION_IMAGE_CAPTURE);
			mIntent.putExtra(MediaStore.EXTRA_OUTPUT,
					Uri.fromFile(mPictureFile));
			mIntent.putExtra("camerasensortype", 2);
			mIntent.putExtra(MediaStore.Images.Media.ORIENTATION, 0);
			startActivityForResult(mIntent, REQUEST_CAMERA_IMAGE);
			break;
		default:
			break;
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode != RESULT_OK) {
			return;
		}
		String fileSrc = null;

		if (requestCode == REQUEST_CAMERA_IMAGE) {
			if (null == mPictureFile) {
				showTip("拍照失败，请重试");
				return;
			}

			fileSrc = mPictureFile.getAbsolutePath();
			updateGallery(fileSrc);
			// 跳转到图片裁剪页面
			FaceUtil.cropPicture(this, Uri.fromFile(new File(fileSrc)));

		}else if (requestCode == FaceUtil.REQUEST_CROP_IMAGE) {
			// 获取返回数据
			Bitmap bmp = data.getParcelableExtra("data");
			// 若返回数据不为null，保存至本地，防止裁剪时未能正常保存
			if(null != bmp){
				FaceUtil.saveBitmapToFile(OnlineFaceVerify.this, bmp);
			}
			// 获取图片保存路径
			fileSrc = FaceUtil.getImagePath(OnlineFaceVerify.this);
			// 获取图片的宽和高
			Options options = new Options();
			options.inJustDecodeBounds = true;
			mImage = BitmapFactory.decodeFile(fileSrc, options);
			
			// 压缩图片
			options.inSampleSize = Math.max(1, (int) Math.ceil(Math.max(
					(double) options.outWidth / 1024f,
					(double) options.outHeight / 1024f)));
			options.inJustDecodeBounds = false;
			mImage = BitmapFactory.decodeFile(fileSrc, options);
			
			
			// 若mImageBitmap为空则图片信息不能正常获取
			if(null == mImage) {
				showTip("图片信息无法正常获取！");
				return;
			}
			
			// 部分手机会对图片做旋转，这里检测旋转角度
			int degree = FaceUtil.readPictureDegree(fileSrc);
			if (degree != 0) {
				// 把图片旋转为正的方向
				mImage = FaceUtil.rotateImage(degree, mImage);
			}

			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			
			//可根据流量及网络状况对图片进行压缩
			mImage.compress(Bitmap.CompressFormat.JPEG, 80, baos);
			mImageData = baos.toByteArray();
			
			img_user.setImageBitmap(mImage);
		}
	}

	public void updateGallery(String filename) {
		MediaScannerConnection.scanFile(this, new String[] { filename }, null,
				new MediaScannerConnection.OnScanCompletedListener() {

					@Override
					public void onScanCompleted(String path, Uri uri) {

					}
				});
	}

	@Override
	public void finish() {
		if (null != mProDialog) {
			mProDialog.dismiss();
		}
		super.finish();
	}
}
