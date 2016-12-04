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
 * ����������֤
 * @author ����
 *
 */
public class OnlineFaceVerify extends Activity implements OnClickListener {
	private Button bt_check, bt_camera;
	private ImageView img_user;// �û����պ�Ҫ��֤��ͷ��

	//private final int REQUEST_PICTURE_CHOOSE = 1;
	private final int REQUEST_CAMERA_IMAGE = 2;

	private Bitmap mImage = null;
	private byte[] mImageData = null;
	// authidΪ6-18���ַ����ȣ�����Ψһ��ʶ�û�
	private String mAuthid = null;
	private Toast mToast;
	// ���ȶԻ���
	private ProgressDialog mProDialog;
	// ���յõ�����Ƭ�ļ�
	private File mPictureFile;
	// FaceRequest���󣬼���������ʶ��ĸ��ֹ���
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
		mProDialog.setTitle("���Ժ�");

		mProDialog.setOnCancelListener(new OnCancelListener() {

			@Override
			public void onCancel(DialogInterface dialog) {
				// cancel���ȿ�ʱ,ȡ�����ڽ��еĲ���
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
					showTip("authid�Ѿ���ע�ᣬ�����������");
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
			showTip("��֤ʧ��");
			
			return;
		}
		if ("success".equals(obj.get("rst"))) {
			if (obj.getBoolean("verf")) {
				showTip("ͨ����֤����ӭ������");
				double latitude=getIntent().getIntExtra("latitude", 0);
				Intent intent=new Intent(this,MainActivity.class);
				intent.putExtra("latitude", "0");
				startActivity(intent);
				OnlineFaceVerify.this.finish();
			} else {
//				showTip("��֤��ͨ��");
				new SweetAlertDialog(this, SweetAlertDialog.ERROR_TYPE)
				.setTitleText(" ")
				.setContentText("��֤ʧ�ܣ�������")
				.setConfirmText("Yes")
				.show();
			}
		} else {
			showTip("��֤ʧ��");
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
				showTip("authid����Ϊ��");
				return;
			}
			if (null != mImageData) {
				mProDialog.setMessage("��֤��...");
				mProDialog.show();
				// �����û���ʶ����ʽΪ6-18���ַ�������ĸ�����֡��»�����ɣ����������ֿ�ͷ�����ܰ����ո񣩡�
				// ��������ʱ���ƶ˽�ʹ���û��豸���豸ID����ʶ�ն��û���
				mFaceRequest.setParameter(SpeechConstant.AUTH_ID, mAuthid);
				mFaceRequest.setParameter(SpeechConstant.WFR_SST, "verify");
				mFaceRequest.sendRequest(mImageData, mRequestListener);
			} else {
				showTip("��ѡ��ͼƬ������֤");
			}
			break;
		case R.id.bt_camera:
			// ����������պ���Ƭ����·��
			mPictureFile = new File(Environment.getExternalStorageDirectory(),
					"picture" + System.currentTimeMillis() / 1000 + ".jpg");
			// ��������,�����浽��ʱ�ļ�
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
				showTip("����ʧ�ܣ�������");
				return;
			}

			fileSrc = mPictureFile.getAbsolutePath();
			updateGallery(fileSrc);
			// ��ת��ͼƬ�ü�ҳ��
			FaceUtil.cropPicture(this, Uri.fromFile(new File(fileSrc)));

		}else if (requestCode == FaceUtil.REQUEST_CROP_IMAGE) {
			// ��ȡ��������
			Bitmap bmp = data.getParcelableExtra("data");
			// ���������ݲ�Ϊnull�����������أ���ֹ�ü�ʱδ����������
			if(null != bmp){
				FaceUtil.saveBitmapToFile(OnlineFaceVerify.this, bmp);
			}
			// ��ȡͼƬ����·��
			fileSrc = FaceUtil.getImagePath(OnlineFaceVerify.this);
			// ��ȡͼƬ�Ŀ�͸�
			Options options = new Options();
			options.inJustDecodeBounds = true;
			mImage = BitmapFactory.decodeFile(fileSrc, options);
			
			// ѹ��ͼƬ
			options.inSampleSize = Math.max(1, (int) Math.ceil(Math.max(
					(double) options.outWidth / 1024f,
					(double) options.outHeight / 1024f)));
			options.inJustDecodeBounds = false;
			mImage = BitmapFactory.decodeFile(fileSrc, options);
			
			
			// ��mImageBitmapΪ����ͼƬ��Ϣ����������ȡ
			if(null == mImage) {
				showTip("ͼƬ��Ϣ�޷�������ȡ��");
				return;
			}
			
			// �����ֻ����ͼƬ����ת����������ת�Ƕ�
			int degree = FaceUtil.readPictureDegree(fileSrc);
			if (degree != 0) {
				// ��ͼƬ��תΪ���ķ���
				mImage = FaceUtil.rotateImage(degree, mImage);
			}

			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			
			//�ɸ�������������״����ͼƬ����ѹ��
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
