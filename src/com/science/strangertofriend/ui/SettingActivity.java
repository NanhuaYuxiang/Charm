package com.science.strangertofriend.ui;

import android.annotation.TargetApi;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewTreeObserver;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TableRow;
import android.widget.TextView;
import cn.pedant.SweetAlert.SweetAlertDialog;

import com.avos.avoscloud.feedback.FeedbackAgent;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.readystatesoftware.systembartint.SystemBarTintManager;
import com.science.strangertofriend.R;
import com.science.strangertofriend.guide.GuideActivity;
import com.science.strangertofriend.widget.RevealLayout;

/**
 * @description ���ý���
 * 
 * 
 */

public class SettingActivity extends BaseActivity implements OnCheckedChangeListener {

	private RevealLayout mRevealLayout;
	private RelativeLayout mLayout;
	private ImageView mBackImg;
	private TextView mTitle;
	private TableRow mNumberSafe, mNumberBound, mMessageTip, mSetClearCache,
			mSetHelp, mSetUpdate, mSetVersion, mUserDeal, mAboutUs,mVoiceSetting;
	private Switch switch_isOpenFaceVerify;
	public static final String IS_OPEN_FACE_VERIFY="com.science.strangertofriend.ui.SettingActivity";
	private SharedPreferences.Editor editor;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.setting);

		initView();
		initListener();
	}

	private void initView() {
		switch_isOpenFaceVerify=(Switch) findViewById(R.id.switch_isOpenFaceVerify);
		mBackImg = (ImageView) findViewById(R.id.back_img);
		mTitle = (TextView) findViewById(R.id.title);
		mTitle.setText("����");

		mNumberSafe = (TableRow) findViewById(R.id.number_safe);
		mNumberBound = (TableRow) findViewById(R.id.number_bound);
		mMessageTip = (TableRow) findViewById(R.id.message_tip);
		mSetClearCache = (TableRow) findViewById(R.id.clear_cache);
		mSetHelp = (TableRow) findViewById(R.id.set_help);
		mSetUpdate = (TableRow) findViewById(R.id.set_update);
		mSetVersion = (TableRow) findViewById(R.id.set_version);
		mUserDeal = (TableRow) findViewById(R.id.user_deal);
		mAboutUs = (TableRow) findViewById(R.id.about_us);
		mVoiceSetting=(TableRow) findViewById(R.id.voice_setting);

		mRevealLayout = (RevealLayout) findViewById(R.id.reveal_layout);
		mLayout = (RelativeLayout) findViewById(R.id.layout);
		mLayout.setBackgroundColor(Color.WHITE);
		
		editor=getSharedPreferences(SettingActivity.IS_OPEN_FACE_VERIFY, MODE_PRIVATE).edit();
		
		boolean isopenFace=getSharedPreferences(SettingActivity.IS_OPEN_FACE_VERIFY, MODE_PRIVATE).getBoolean("isOpenFaceVerify", false);
		if(isopenFace){
			switch_isOpenFaceVerify.setChecked(true);
		}else {
			switch_isOpenFaceVerify.setChecked(false);
		}
	}

	private void initListener() {
		
		
		//�����Ƿ�������ʶ��
		switch_isOpenFaceVerify.setOnCheckedChangeListener(this);

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

		// ���Ͻ��˳���ǰactivity
		mBackImg.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				SettingActivity.this.finish();
			}
		});
		
		
		
		
		
		/**
		//�˺Ű�ȫ����
		mNumberSafe.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				
				//�Ƿ���������֤
				Intent intent=new Intent(SettingActivity.this,FaceDetect.class);
				startActivity(intent);
				
			
				new SweetAlertDialog(SettingActivity.this,
						SweetAlertDialog.WARNING_TYPE)
						.setTitleText("�˺Ű�ȫ")
						.setContentText("�˺Ű�ȫ���ܹ���ʨ�����ܹ���..")
						.setConfirmText("ok")
						.setConfirmClickListener(
								new SweetAlertDialog.OnSweetClickListener() {
									@Override
									public void onClick(SweetAlertDialog sDialog) {
										sDialog.dismiss();
									}
								}).show();
				
				
			}

		});
		*/
		mNumberBound.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				new SweetAlertDialog(SettingActivity.this,
						SweetAlertDialog.WARNING_TYPE)
						.setTitleText("�˺Ű�")
						.setContentText("�˺Ű󶨹��ܹ���ʨ�����ܹ���..")
						.setConfirmText("ok")
						.setConfirmClickListener(
								new SweetAlertDialog.OnSweetClickListener() {
									@Override
									public void onClick(SweetAlertDialog sDialog) {
										sDialog.dismiss();
									}
								}).show();
			}

		});
		mMessageTip.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

			}

		});
		mSetClearCache.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				new SweetAlertDialog(SettingActivity.this,
						SweetAlertDialog.WARNING_TYPE)
						.setTitleText("ȷ��������棿")
						.setContentText("���ɻָ���")
						.setCancelText("ȡ��")
						.setConfirmText("ȷ��")
						.showCancelButton(true)
						.setCancelClickListener(
								new SweetAlertDialog.OnSweetClickListener() {
									@Override
									public void onClick(SweetAlertDialog sDialog) {

										sDialog.dismiss();
										new SweetAlertDialog(
												SettingActivity.this,
												SweetAlertDialog.ERROR_TYPE)
												.setTitleText("�ɹ�ȡ��").show();
									}
								})
						.setConfirmClickListener(
								new SweetAlertDialog.OnSweetClickListener() {
									@Override
									public void onClick(SweetAlertDialog sDialog) {
										ImageLoader.getInstance()
												.clearMemoryCache();
										ImageLoader.getInstance()
												.clearDiskCache();
										sDialog.dismiss();
										new SweetAlertDialog(
												SettingActivity.this,
												SweetAlertDialog.SUCCESS_TYPE)
												.setTitleText("�ɹ����").show();
									}
								}).show();
			}

		});
		
		mVoiceSetting.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent=new Intent(SettingActivity.this,VoiceSetting.class);
				startActivity(intent);
			}
		});
		
		mSetHelp.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// avos�û�����ϵͳ
				FeedbackAgent agent = new FeedbackAgent(SettingActivity.this);
				agent.startDefaultThreadActivity();
			}

		});
		mSetUpdate.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				final SweetAlertDialog nAlertDialog = new SweetAlertDialog(
						SettingActivity.this, SweetAlertDialog.PROGRESS_TYPE);
				nAlertDialog.setTitleText("���ڼ�����").setContentText("���Ժ�");
				nAlertDialog.show();
				nAlertDialog.setCancelable(false);
				new CountDownTimer(800 * 4, 800) {
					public void onTick(long millisUntilFinished) {
						colorProgress(nAlertDialog);
					}

					public void onFinish() {
						i = -1;
						nAlertDialog.dismiss();
						new SweetAlertDialog(SettingActivity.this,
								SweetAlertDialog.WARNING_TYPE)
								.setTitleText("�������°汾")
								.setConfirmText("ok")
								.setConfirmClickListener(
										new SweetAlertDialog.OnSweetClickListener() {
											@Override
											public void onClick(
													SweetAlertDialog sDialog) {
												sDialog.dismiss();
											}
										}).show();
					}
				}.start();
			}

		});
		mSetVersion.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(SettingActivity.this,
						GuideActivity.class);
				intent.putExtra("set", 1);
				startActivity(intent);
			}

		});
		mUserDeal.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(SettingActivity.this,
						UserDealActivity.class);
				startActivity(intent);
			}

		});
		mAboutUs.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				new SweetAlertDialog(SettingActivity.this,
						SweetAlertDialog.WARNING_TYPE)
						.setTitleText("��������")
						.setContentText("��������ϻ���ѧ��γ���Ŷ�����������ɾͣ����ע΢�� @�ϻ���ѧ��γ���Ŷ�")
						.setConfirmText("ok")
						.setConfirmClickListener(
								new SweetAlertDialog.OnSweetClickListener() {
									@Override
									public void onClick(SweetAlertDialog sDialog) {
										sDialog.dismiss();
									}
								}).show();
			}

		});
	}

	@Override
	@TargetApi(19)
	public void initSystemBar() {


		super.initSystemBar();
		// ����״̬���Ĺ���ʵ��
		SystemBarTintManager tintManager = new SystemBarTintManager(this);
		// ����״̬������
		tintManager.setStatusBarTintEnabled(true);
		// �����������
		tintManager.setNavigationBarTintEnabled(true);
		// ����һ����ɫ��ϵͳ��
		tintManager.setTintColor(Color.parseColor("#f698b2"));
	}

	@Override
	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
		
		//�鿴�û��Ƿ��Ѿ���������ģ��,�����������ϴ������ޣ����������ϴ�����
		boolean isModelExist=getSharedPreferences(SettingActivity.IS_OPEN_FACE_VERIFY, MODE_PRIVATE).getBoolean("isModelExist", false);
		//�˴�objectid�����ֲ�ͬ�û��ı���model״̬��
		//��ֹ��ͬ���û���¼ͬһ̨�ֻ�
		//����������,��Ҫ�ϴ���Ƭ����model
		if(!isModelExist){
			Intent intent=new Intent(this,GenerateFaceModel.class);
			startActivity(intent);
		}
		
		if(isChecked){
			editor.putBoolean("isOpenFaceVerify", true);
		}else{
			editor.putBoolean("isOpenFaceVerify", false);
		}
		editor.commit();
	}
}
