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
 * 老人专区,老人语音发布任务，一键拨号
 * 
 * @author 49149
 * 
 */
public class ElderlyActivity extends BaseActivity implements OnClickListener {
	private CircleImageView img_startSay, img_phone, img_publishTask;// 开始说话，一键拨号，发布任务按钮
	private EditText et_content;// 任务文本框中的内容
	private String voiceResultString = "";
	private double latitude, longitute;
	private String addstr;// 地理信息描述

	// 语音属性
	private static String TAG = ElderlyActivity.class.getSimpleName();
	// 语音听写对象
	private SpeechRecognizer mIat;
	// 语音听写UI
	private RecognizerDialog mIatDialog;
	// 用HashMap存储听写结果
	private HashMap<String, String> mIatResults = new LinkedHashMap<String, String>();

	private Toast mToast;
	private SharedPreferences mSharedPreferences;
	// 引擎类型
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
		// 使用SpeechRecognizer对象，可根据回调消息自定义界面；
		mIat = SpeechRecognizer.createRecognizer(ElderlyActivity.this,
				mInitListener);
		// 初始化听写Dialog，如果只使用有UI听写功能，无需创建SpeechRecognizer
		// 使用UI听写功能，请根据sdk文件目录下的notice.txt,放置布局文件和图片资源
		mIatDialog = new RecognizerDialog(ElderlyActivity.this, mInitListener);

		mSharedPreferences = getSharedPreferences(VoiceSetting.VOICESETTING,
				MODE_PRIVATE);

		addstr = getSharedPreferences(WelcomeActivity.GEOWIND, MODE_PRIVATE)
				.getString("addstr", "地图所在地");
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
			// 移动数据分析，收集开始听写事件
			FlowerCollector.onEvent(ElderlyActivity.this, "iat_recognize");

			et_content.setText(null);// 清空显示内容
			mIatResults.clear();
			// 设置参数
			setParam();
			// 显示听写对话框
			mIatDialog.setListener(mRecognizerDialogListener);
			mIatDialog.show();
			showTip("听写开始");
			break;
		case R.id.img_publishTask:// 发布任务
			final AVGeoPoint geoPoint = new AVGeoPoint(latitude, longitute);
			
			new SweetAlertDialog(this, SweetAlertDialog.WARNING_TYPE)
			.setTitleText("您确定发布此任务吗？")
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
		case R.id.img_phone:// 一键拨号
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
	 * 发布老人的任务
	 */
	public void publishTaskByElderly(AVUser user, String content,
			AVGeoPoint geoPoint) {
		if (TextUtils.isEmpty(content)) {
			showTip("任务内容不能为空哦！");
		}else{
			AVService.addNewTask(user, user.getCurrentUser().getUsername(), "",
					"老人任务", content, "无", geoPoint, addstr, "0",
					TaskType.SERVICE_ELDERLY, false, false, new SaveCallback() {

						@Override
						public void done(AVException exception) {
							if (exception == null) {
								showTip("发布成功！");
							} else {
								showTip("发布失败！");
							}
						}
					});
		}
	}

	/**
	 * 设置讯飞语音参数
	 */
	private void setParam() {
		// 清空参数
		mIat.setParameter(SpeechConstant.PARAMS, null);

		// 设置听写引擎
		mIat.setParameter(SpeechConstant.ENGINE_TYPE, mEngineType);
		// 设置返回结果格式
		mIat.setParameter(SpeechConstant.RESULT_TYPE, "json");

		// 设置语言
		mIat.setParameter(SpeechConstant.LANGUAGE, "zh_cn");
		// 设置语言区域
		mIat.setParameter(SpeechConstant.ACCENT, "mandarin");

		// 设置语音前端点:静音超时时间，即用户多长时间不说话则当做超时处理
		mIat.setParameter(SpeechConstant.VAD_BOS, "4000");
		// 设置语音后端点:后端点静音检测时间，即用户停止说话多长时间内即认为不再输入， 自动停止录音
		mIat.setParameter(SpeechConstant.VAD_EOS,
				mSharedPreferences.getString("time", "1000"));
		// 设置标点符号,设置为"0"返回结果无标点,设置为"1"返回结果有标点
		mIat.setParameter(SpeechConstant.ASR_PTT, "1");

		// 设置音频保存路径，保存音频格式支持pcm、wav，设置路径为sd卡请注意WRITE_EXTERNAL_STORAGE权限
		// 注：AUDIO_FORMAT参数语记需要更新版本才能生效
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
	 * 初始化监听器。
	 */
	private InitListener mInitListener = new InitListener() {

		@Override
		public void onInit(int code) {
			Log.d(TAG, "SpeechRecognizer init() code = " + code);
			if (code != ErrorCode.SUCCESS) {
				showTip("初始化失败，错误码：" + code);
			}
		}
	};

	private void showTip(final String str) {
		mToast.setText(str);
		mToast.show();
	}

	/**
	 * 听写UI监听器
	 */
	private RecognizerDialogListener mRecognizerDialogListener = new RecognizerDialogListener() {
		public void onResult(RecognizerResult results, boolean isLast) {
			printResult(results);
		}

		/**
		 * 识别回调错误.
		 */
		public void onError(SpeechError error) {
			showTip(error.getPlainDescription(true));
		}

	};

	private void printResult(RecognizerResult results) {
		String text = JsonParser.parseIatResult(results.getResultString());

		String sn = null;
		// 读取json结果中的sn字段
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
	 * 听写监听器。
	 */
	private RecognizerListener mRecognizerListener = new RecognizerListener() {

		@Override
		public void onBeginOfSpeech() {
			// 此回调表示：sdk内部录音机已经准备好了，用户可以开始语音输入
			showTip("开始说话");
		}

		@Override
		public void onError(SpeechError error) {
			// Tips：
			// 错误码：10118(您没有说话)，可能是录音机权限被禁，需要提示用户打开应用的录音权限。
			// 如果使用本地功能（语记）需要提示用户开启语记的录音权限。
			showTip(error.getPlainDescription(true));
		}

		@Override
		public void onEndOfSpeech() {
			// 此回调表示：检测到了语音的尾端点，已经进入识别过程，不再接受语音输入
			showTip("结束说话");
		}

		@Override
		public void onResult(RecognizerResult results, boolean isLast) {
			Log.d(TAG, results.getResultString());
			printResult(results);

			if (isLast) {
				// TODO 最后的结果
			}
		}

		@Override
		public void onVolumeChanged(int volume, byte[] data) {
			showTip("当前正在说话，音量大小：" + volume);
			Log.d(TAG, "返回音频数据：" + data.length);
		}

		@Override
		public void onEvent(int eventType, int arg1, int arg2, Bundle obj) {
			// 以下代码用于获取与云端的会话id，当业务出错时将会话id提供给技术支持人员，可用于查询会话日志，定位出错原因
			// 若使用本地能力，会话id为null
			// if (SpeechEvent.EVENT_SESSION_ID == eventType) {
			// String sid = obj.getString(SpeechEvent.KEY_EVENT_SESSION_ID);
			// Log.d(TAG, "session id =" + sid);
			// }
		}
	};

	/**
	 * 获取经纬度信息
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
		// 退出时释放连接
		mIat.cancel();
		mIat.destroy();
	}

	@Override
	protected void onResume() {
		// 开放统计 移动数据统计分析
		FlowerCollector.onResume(ElderlyActivity.this);
		FlowerCollector.onPageStart(TAG);
		super.onResume();
	}

	@Override
	protected void onPause() {
		// 开放统计 移动数据统计分析
		FlowerCollector.onPageEnd(TAG);
		FlowerCollector.onPause(ElderlyActivity.this);
		super.onPause();
	}
}
