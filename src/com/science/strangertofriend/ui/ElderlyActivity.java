package com.science.strangertofriend.ui;

import java.util.HashMap;
import java.util.LinkedHashMap;

import org.json.JSONException;
import org.json.JSONObject;

import cn.pedant.SweetAlert.SweetAlertDialog;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVGeoPoint;
import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.SaveCallback;
import com.avos.avoscloud.LogUtil.log;
import com.iflytek.cloud.ErrorCode;
import com.iflytek.cloud.InitListener;
import com.iflytek.cloud.RecognizerListener;
import com.iflytek.cloud.RecognizerResult;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechError;
import com.iflytek.cloud.SpeechRecognizer;
import com.iflytek.cloud.ui.RecognizerDialog;
import com.iflytek.cloud.ui.RecognizerDialogListener;
import com.iflytek.sunflower.FlowerCollector;
import com.science.strangertofriend.R;
import com.science.strangertofriend.TaskType;
import com.science.strangertofriend.utils.AVService;
import com.science.strangertofriend.utils.JsonParser;

import de.hdodenhof.circleimageview.CircleImageView;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.gesture.Prediction;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

/**
 * ����ר��,����������������һ������
 * 
 * @author 49149
 * 
 */
public class ElderlyActivity extends BaseActivity implements OnClickListener {
	private CircleImageView img_startSay, img_phone, img_publishTask;// ��ʼ˵����һ�����ţ���������ť
	private EditText et_content;// �����ı����е�����
	private String voiceResultString = "";
	private double latitude, longitute;
	private String addstr;// ������Ϣ����

	// ��������
	private static String TAG = ElderlyActivity.class.getSimpleName();
	// ������д����
	private SpeechRecognizer mIat;
	// ������дUI
	private RecognizerDialog mIatDialog;
	// ��HashMap�洢��д���
	private HashMap<String, String> mIatResults = new LinkedHashMap<String, String>();

	private Toast mToast;
	private SharedPreferences mSharedPreferences;
	// ��������
	private String mEngineType = SpeechConstant.TYPE_CLOUD;
	private ImageView img_back;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_elderly);
		initComponent();

		getGeowind();
	}

	private void initComponent() {
		initView();
		initListener();
		// ʹ��SpeechRecognizer���󣬿ɸ��ݻص���Ϣ�Զ�����棻
		mIat = SpeechRecognizer.createRecognizer(ElderlyActivity.this,
				mInitListener);
		// ��ʼ����дDialog�����ֻʹ����UI��д���ܣ����贴��SpeechRecognizer
		// ʹ��UI��д���ܣ������sdk�ļ�Ŀ¼�µ�notice.txt,���ò����ļ���ͼƬ��Դ
		mIatDialog = new RecognizerDialog(ElderlyActivity.this, mInitListener);

		mSharedPreferences = getSharedPreferences(VoiceSetting.VOICESETTING,
				MODE_PRIVATE);

		addstr = getSharedPreferences(WelcomeActivity.GEOWIND, MODE_PRIVATE)
				.getString("addstr", "��ͼ���ڵ�");
		mToast = Toast.makeText(this, "", Toast.LENGTH_SHORT);
	}

	private void initView() {
		img_phone = (CircleImageView) findViewById(R.id.img_phone);
		img_startSay = (CircleImageView) findViewById(R.id.img_startSay);
		et_content = (EditText) findViewById(R.id.et_content);
		img_publishTask = (CircleImageView) findViewById(R.id.img_publishTask);
		img_back=(ImageView) findViewById(R.id.back_img);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.img_startSay:
			// �ƶ����ݷ������ռ���ʼ��д�¼�
			FlowerCollector.onEvent(ElderlyActivity.this, "iat_recognize");

			et_content.setText(null);// �����ʾ����
			mIatResults.clear();
			// ���ò���
			setParam();
			// ��ʾ��д�Ի���
			mIatDialog.setListener(mRecognizerDialogListener);
			mIatDialog.show();
			showTip("��д��ʼ");
			break;
		case R.id.img_publishTask:// ��������
			final AVGeoPoint geoPoint = new AVGeoPoint(latitude, longitute);
			
			new SweetAlertDialog(this, SweetAlertDialog.WARNING_TYPE)
			.setTitleText("��ȷ��������������")
		    .setConfirmText("Yes")
		    .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
		        @Override
		        public void onClick(SweetAlertDialog sDialog) {
		            sDialog.dismissWithAnimation();
		            publishTaskByElderly(AVUser.getCurrentUser(), voiceResultString,
		            		geoPoint);
		        }
		    })
		    .show();
			
			break;
		case R.id.img_phone:// һ������
			String phoneString = getSharedPreferences(
					VoiceSetting.VOICESETTING, MODE_PRIVATE).getString(
					"phone1", "123456");
			int phoneNumber = Integer.parseInt(phoneString);
			Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:"
					+ phoneNumber));
			startActivity(intent);
			break;
		case R.id.back_img:
			finish();
			break;
		default:
			break;
		}
	}

	/**
	 * �������˵�����
	 */
	public void publishTaskByElderly(AVUser user, String content,
			AVGeoPoint geoPoint) {
		if (TextUtils.isEmpty(content)) {
			showTip("�������ݲ���Ϊ��Ŷ��");
		}else{
			AVService.addNewTask(user, user.getCurrentUser().getUsername(), "",
					"��������", content, "��", geoPoint, addstr, "0",
					TaskType.SERVICE_ELDERLY, false, false, new SaveCallback() {

						@Override
						public void done(AVException exception) {
							if (exception == null) {
								showTip("�����ɹ���");
							} else {
								showTip("����ʧ�ܣ�");
							}
						}
					});
		}
	}

	/**
	 * ����Ѷ����������
	 */
	private void setParam() {
		// ��ղ���
		mIat.setParameter(SpeechConstant.PARAMS, null);

		// ������д����
		mIat.setParameter(SpeechConstant.ENGINE_TYPE, mEngineType);
		// ���÷��ؽ����ʽ
		mIat.setParameter(SpeechConstant.RESULT_TYPE, "json");

		// ��������
		mIat.setParameter(SpeechConstant.LANGUAGE, "zh_cn");
		// ������������
		mIat.setParameter(SpeechConstant.ACCENT, "mandarin");

		// ��������ǰ�˵�:������ʱʱ�䣬���û��೤ʱ�䲻˵��������ʱ����
		mIat.setParameter(SpeechConstant.VAD_BOS, "4000");
		// ����������˵�:��˵㾲�����ʱ�䣬���û�ֹͣ˵���೤ʱ���ڼ���Ϊ�������룬 �Զ�ֹͣ¼��
		mIat.setParameter(SpeechConstant.VAD_EOS,
				mSharedPreferences.getString("time", "1000"));
		// ���ñ�����,����Ϊ"0"���ؽ���ޱ��,����Ϊ"1"���ؽ���б��
		mIat.setParameter(SpeechConstant.ASR_PTT, "1");

		// ������Ƶ����·����������Ƶ��ʽ֧��pcm��wav������·��Ϊsd����ע��WRITE_EXTERNAL_STORAGEȨ��
		// ע��AUDIO_FORMAT���������Ҫ���°汾������Ч
		mIat.setParameter(SpeechConstant.AUDIO_FORMAT, "wav");
		mIat.setParameter(SpeechConstant.ASR_AUDIO_PATH,
				Environment.getExternalStorageDirectory() + "/msc/iat.wav");
	}

	public void initListener() {
		img_phone.setOnClickListener(this);
		img_startSay.setOnClickListener(this);
		img_publishTask.setOnClickListener(this);
		img_back.setOnClickListener(this);
	}

	/**
	 * ��ʼ����������
	 */
	private InitListener mInitListener = new InitListener() {

		@Override
		public void onInit(int code) {
			Log.d(TAG, "SpeechRecognizer init() code = " + code);
			if (code != ErrorCode.SUCCESS) {
				showTip("��ʼ��ʧ�ܣ������룺" + code);
			}
		}
	};

	private void showTip(final String str) {
		mToast.setText(str);
		mToast.show();
	}

	/**
	 * ��дUI������
	 */
	private RecognizerDialogListener mRecognizerDialogListener = new RecognizerDialogListener() {
		public void onResult(RecognizerResult results, boolean isLast) {
			printResult(results);
		}

		/**
		 * ʶ��ص�����.
		 */
		public void onError(SpeechError error) {
			showTip(error.getPlainDescription(true));
		}

	};

	private void printResult(RecognizerResult results) {
		String text = JsonParser.parseIatResult(results.getResultString());

		String sn = null;
		// ��ȡjson����е�sn�ֶ�
		try {
			JSONObject resultJson = new JSONObject(results.getResultString());
			sn = resultJson.optString("sn");
		} catch (JSONException e) {
			e.printStackTrace();
		}

		mIatResults.put(sn, text);

		StringBuffer resultBuffer = new StringBuffer();
		for (String key : mIatResults.keySet()) {
			resultBuffer.append(mIatResults.get(key));
		}
		voiceResultString = resultBuffer.toString();
		et_content.setText(resultBuffer.toString());
		et_content.setSelection(et_content.length());
	}

	/**
	 * ��д��������
	 */
	private RecognizerListener mRecognizerListener = new RecognizerListener() {

		@Override
		public void onBeginOfSpeech() {
			// �˻ص���ʾ��sdk�ڲ�¼�����Ѿ�׼�����ˣ��û����Կ�ʼ��������
			showTip("��ʼ˵��");
		}

		@Override
		public void onError(SpeechError error) {
			// Tips��
			// �����룺10118(��û��˵��)��������¼����Ȩ�ޱ�������Ҫ��ʾ�û���Ӧ�õ�¼��Ȩ�ޡ�
			// ���ʹ�ñ��ع��ܣ���ǣ���Ҫ��ʾ�û�������ǵ�¼��Ȩ�ޡ�
			showTip(error.getPlainDescription(true));
		}

		@Override
		public void onEndOfSpeech() {
			// �˻ص���ʾ����⵽��������β�˵㣬�Ѿ�����ʶ����̣����ٽ�����������
			showTip("����˵��");
		}

		@Override
		public void onResult(RecognizerResult results, boolean isLast) {
			Log.d(TAG, results.getResultString());
			printResult(results);

			if (isLast) {
				// TODO ���Ľ��
			}
		}

		@Override
		public void onVolumeChanged(int volume, byte[] data) {
			showTip("��ǰ����˵����������С��" + volume);
			Log.d(TAG, "������Ƶ���ݣ�" + data.length);
		}

		@Override
		public void onEvent(int eventType, int arg1, int arg2, Bundle obj) {
			// ���´������ڻ�ȡ���ƶ˵ĻỰid����ҵ�����ʱ���Ựid�ṩ������֧����Ա�������ڲ�ѯ�Ự��־����λ����ԭ��
			// ��ʹ�ñ����������ỰidΪnull
			// if (SpeechEvent.EVENT_SESSION_ID == eventType) {
			// String sid = obj.getString(SpeechEvent.KEY_EVENT_SESSION_ID);
			// Log.d(TAG, "session id =" + sid);
			// }
		}
	};

	/**
	 * ��ȡ��γ����Ϣ
	 */
	public void getGeowind() {
		SharedPreferences preferences = getSharedPreferences(
				WelcomeActivity.GEOWIND, MODE_PRIVATE);
		latitude = Double.parseDouble(preferences.getString("latitude", "0"));
		longitute = Double.parseDouble(preferences.getString("longitude", "0"));
		// showTip(latitude+"   ,   "+longitute);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		// �˳�ʱ�ͷ�����
		mIat.cancel();
		mIat.destroy();
	}

	@Override
	protected void onResume() {
		// ����ͳ�� �ƶ�����ͳ�Ʒ���
		FlowerCollector.onResume(ElderlyActivity.this);
		FlowerCollector.onPageStart(TAG);
		super.onResume();
	}

	@Override
	protected void onPause() {
		// ����ͳ�� �ƶ�����ͳ�Ʒ���
		FlowerCollector.onPageEnd(TAG);
		FlowerCollector.onPause(ElderlyActivity.this);
		super.onPause();
	}
}
