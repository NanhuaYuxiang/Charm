package com.science.strangertofriend.ui;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import cn.pedant.SweetAlert.SweetAlertDialog;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.SaveCallback;
import com.readystatesoftware.systembartint.SystemBarTintManager;
import com.science.strangertofriend.MainActivity;
import com.science.strangertofriend.R;
import com.science.strangertofriend.utils.AVService;
import com.science.strangertofriend.widget.RevealLayout;

/**
 * @description 更改资料
 * 
 * @author 幸运Science 陈土燊
 * @school University of South China
 * @email chentushen.science@gmail.com,274240671@qq.com
 * @2015-5-22
 * 
 */

public class AlterActivity extends BaseActivity {

	private RevealLayout mRevealLayout;
	private RelativeLayout mLayout;
	private ImageView mBackImg;
	private TextView mTitle;
	private EditText mBirth, mHometown, mInLove, mConstellation, mMyStatement;
	private Button mSaveButton;
	private String mUsername;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.alter_mydata);

		initView();
		initData();
		initListener();
	}

	@Override
	@TargetApi(19)
	public void initSystemBar() {
		super.initSystemBar();
		// 创建状态栏的管理实例
		SystemBarTintManager tintManager = new SystemBarTintManager(this);
		// 激活状态栏设置
		tintManager.setStatusBarTintEnabled(true);
		// 激活导航栏设置
		tintManager.setNavigationBarTintEnabled(true);
		// 设置一个颜色给系统栏
		tintManager.setTintColor(Color.parseColor("#f698b2"));
	}

	private void initView() {

		mRevealLayout = (RevealLayout) findViewById(R.id.reveal_layout);
		mLayout = (RelativeLayout) findViewById(R.id.layout);
		mLayout.setBackgroundColor(Color.WHITE);

		mBackImg = (ImageView) findViewById(R.id.back_img);
		mTitle = (TextView) findViewById(R.id.title);
		mTitle.setText("更改资料");

		mBirth = (EditText) findViewById(R.id.birth);
		mHometown = (EditText) findViewById(R.id.hometown);
		mInLove = (EditText) findViewById(R.id.inlove);
		mConstellation = (EditText) findViewById(R.id.constellation);
		mMyStatement = (EditText) findViewById(R.id.personal_statement);

		mSaveButton = (Button) findViewById(R.id.save);
	}

	private void initData() {
		mUsername = AVUser.getCurrentUser().getUsername();
		mBirth.setText(getIntent().getStringExtra("birth"));
		mHometown.setText(getIntent().getStringExtra("hometown"));
		mInLove.setText(getIntent().getStringExtra("inlove"));
		mConstellation.setText(getIntent().getStringExtra("constellation"));
		mMyStatement.setText(getIntent().getStringExtra("personalStatement"));
	}

	@SuppressLint("HandlerLeak")
	private Handler mHandler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 1:
				intentActivity();
				Toast.makeText(AlterActivity.this, "保存成功！", Toast.LENGTH_SHORT)
						.show();
				break;
			case 2:
				Toast.makeText(AlterActivity.this, "非常抱歉，提交出错！",
						Toast.LENGTH_LONG).show();
				break;
			default:
				break;
			}
		}
	};

	// 跳回之前界面
	private void intentActivity() {
		Intent intent = new Intent(AlterActivity.this, MainActivity.class);
		intent.putExtra("birth", mBirth.getText().toString());
		intent.putExtra("hometown", mHometown.getText().toString());
		intent.putExtra("inlove", mInLove.getText().toString());
		intent.putExtra("constellation", mConstellation.getText().toString());
		intent.putExtra("personalStatement", mMyStatement.getText().toString());
		setResult(-1, intent);
		AlterActivity.this.finish();
	}

	private void initListener() {

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

		// 左上角退出当前activity
		mBackImg.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				AlterActivity.this.finish();
			}
		});

		mSaveButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (!mBirth.getText().toString().isEmpty()) {

					if (!mHometown.getText().toString().isEmpty()) {

						if (!mInLove.getText().toString().isEmpty()) {

							if (!mConstellation.getText().toString().isEmpty()) {
								if (!mMyStatement.getText().toString()
										.isEmpty()) {

									progressDialogShow();
								} else {
									Toast.makeText(AlterActivity.this,
											"请填写你的签名", Toast.LENGTH_LONG)
											.show();
								}

							} else {
								Toast.makeText(AlterActivity.this, "请填写星座",
										Toast.LENGTH_SHORT).show();
							}

						} else {
							Toast.makeText(AlterActivity.this, "请填写恋爱状态",
									Toast.LENGTH_SHORT).show();
						}

					} else {
						Toast.makeText(AlterActivity.this, "请填写家乡",
								Toast.LENGTH_SHORT).show();
					}

				} else {
					Toast.makeText(AlterActivity.this, "请填写出生日期",
							Toast.LENGTH_SHORT).show();
				}
			}
		});
	}

	private void progressDialogShow() {
		final SweetAlertDialog pDialog = new SweetAlertDialog(
				AlterActivity.this, SweetAlertDialog.PROGRESS_TYPE)
				.setTitleText("拼命加载中");
		pDialog.show();
		pDialog.setCancelable(false);
		new CountDownTimer(800 * 4, 800) {
			public void onTick(long millisUntilFinished) {
				colorProgress(pDialog);
			}

			public void onFinish() {
				i = -1;
				pDialog.dismiss();
				saveAlter();
			}
		}.start();
	}

	private void saveAlter() {

		String birth = mBirth.getText().toString();
		String hometown = mHometown.getText().toString();
		String inlove = mInLove.getText().toString();
		String constellation = mConstellation.getText().toString();
		String personalStatement = mMyStatement.getText().toString();
		AVService.alterUserInformation(mUsername, AVUser.getCurrentUser()
				.getEmail(), AVUser.getCurrentUser().getString("gender"),
				birth, hometown, inlove, constellation, personalStatement,
				new SaveCallback() {
					@Override
					public void done(AVException e) {
						if (e == null) {
							mHandler.obtainMessage(1).sendToTarget();
						} else {
							mHandler.obtainMessage(2).sendToTarget();
						}
					}
				});
	}

}
