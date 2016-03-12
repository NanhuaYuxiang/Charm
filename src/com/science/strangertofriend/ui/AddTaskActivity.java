package com.science.strangertofriend.ui;

import java.util.ArrayList;
import java.util.List;

import android.annotation.TargetApi;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.util.Log;
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
import cn.pedant.SweetAlert.SweetAlertDialog.OnSweetClickListener;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVGeoPoint;
import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.SaveCallback;
import com.readystatesoftware.systembartint.SystemBarTintManager;
import com.science.strangertofriend.R;
import com.science.strangertofriend.TaskType;
import com.science.strangertofriend.adapter.MySpinnerAdapter.onItemClickListener;
import com.science.strangertofriend.bean.Task;
import com.science.strangertofriend.utils.AVService;
import com.science.strangertofriend.widget.RevealLayout;

public class AddTaskActivity extends BaseActivity implements OnClickListener {
	private AVUser currentUser;
	private RevealLayout mRevealLayout;
	private RelativeLayout mLayout, mAdd_task_layout;
	private ImageView mBackImg;// ���ذ�ť
	private TextView mTitle;// ����
	private EditText theme, description, endTime, publishedLocation,
			publishedPrice;// �������⣬��������������ʱ�䣬�����ص㣬����۸�
	private String publisherName, latitude, longitude;// ������������ά�ȣ�����
	// private ImageView img_task;//�������ͼƬ
	private boolean isAccepted, isaccomplished;// �Ƿ񱻽��ܣ��Ƿ����
	private Button bt_publish;// ������ť

	private RelativeLayout spinnner;// spinner�Ĳ���
	private TextView mSpinnerTitle;
	private List<String> task_types;
	private String service_type;// ��������

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.add_task);
		currentUser=AVUser.getCurrentUser();
		initView();
		initListener();
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

		// ���Ͻ��˳���ǰactivity
		mBackImg.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				AddTaskActivity.this.finish();
			}
		});

		bt_publish.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (!theme.getText().toString().isEmpty()) {

					if (!description.getText().toString().isEmpty()) {

						if (!endTime.getText().toString().isEmpty()) {

							if (!publishedPrice.getText().toString().isEmpty()) {
								
								if(!mSpinnerTitle.getText().equals("��ѡ���������")){
									
									progressDialogShow();
								}else {
									Toast.makeText(AddTaskActivity.this, "��ѡ����������", Toast.LENGTH_SHORT).show();
								}
								

							} else {
								Toast.makeText(AddTaskActivity.this,
										"����д���������", Toast.LENGTH_SHORT).show();
							}

						} else {
							Toast.makeText(AddTaskActivity.this, "����д�����ֹʱ��",
									Toast.LENGTH_SHORT).show();
						}

					} else {
						Toast.makeText(AddTaskActivity.this, "����д��������",
								Toast.LENGTH_SHORT).show();
					}

				} else {
					Toast.makeText(AddTaskActivity.this, "����д��������",
							Toast.LENGTH_SHORT).show();
				}

				
			}

		});

		spinnner.setOnClickListener(this);

	}

	public void progressDialogShow() {
		final SweetAlertDialog pDialog = new SweetAlertDialog(
				AddTaskActivity.this, SweetAlertDialog.PROGRESS_TYPE)
				.setTitleText("ƴ��������");
		pDialog.show();
		pDialog.setCancelable(false);
		new CountDownTimer(800 * 4, 800) {
			public void onTick(long millisUntilFinished) {
				colorProgress(pDialog);
			}

			public void onFinish() {
				i = -1;
				pDialog.dismiss();
				publishTask();
			}
		}.start();
	}

	public void publishTask() {
		String themeString = theme.getText().toString();
		String descriptionString = description.getText().toString();
		String endTimeString = endTime.getText().toString();
		String locationString = publishedLocation.getText().toString();
		publisherName = AVUser.getCurrentUser().getUsername();
		String price = publishedPrice.getText().toString();

		double latitude = ShowNearMenMapActivity.getLatitude();
		double longitude = ShowNearMenMapActivity.getLongitude();
		AVGeoPoint geoPoint = new AVGeoPoint(latitude, longitude);
		AVService.addNewTask(currentUser,publisherName,"", themeString, descriptionString,
				endTimeString, geoPoint, locationString, price,
				service_type,false,false, new SaveCallback() {

					@Override
					public void done(AVException exception) {
						if (exception == null) {
							mHandler.obtainMessage(1).sendToTarget();
							Log.e("AddTaskActivity", "����ɹ�");
						} else {
							mHandler.obtainMessage(2).sendToTarget();
							Log.e("AddTaskActivity", "����ʧ��");
						}
					}
				});
	}

	private Handler mHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case 1:
				final SweetAlertDialog dialog = new SweetAlertDialog(
						AddTaskActivity.this, SweetAlertDialog.SUCCESS_TYPE);
				dialog.setTitleText("Good job");
				dialog.setContentText("Saved successfully");
				dialog.show();
				dialog.setCancelable(false);
				Log.e("AddTaskActivity", "����ɹ�");

				dialog.setConfirmText("ȷ��");
				dialog.setConfirmClickListener(new OnSweetClickListener() {

					public void onClick(SweetAlertDialog sweetAlertDialog) {

						AddTaskActivity.this.finish();
						Intent intent=new Intent(AddTaskActivity.this,ShowNearMenMapActivity.class);
						startActivity(intent);
					}
				});
				break;
			case 2:
				Log.e("AddTaskActivity", "����ʧ��");
				final SweetAlertDialog dialogError = new SweetAlertDialog(
						AddTaskActivity.this, SweetAlertDialog.ERROR_TYPE);
				dialogError.setTitleText("sorry");
				dialogError.setConfirmText("Saved filed");
				dialogError.show();
				break;
			default:
				break;
			}
		};
	};

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

	private void initView() {
		mRevealLayout = (RevealLayout) findViewById(R.id.reveal_layout);
		mAdd_task_layout = (RelativeLayout) findViewById(R.id.add_task_layout);
		mLayout = (RelativeLayout) findViewById(R.id.layout);
		mLayout.setBackgroundColor(Color.WHITE);

		mBackImg = (ImageView) findViewById(R.id.back_img);
		mTitle = (TextView) findViewById(R.id.title);
		mTitle.setText("����������");

		theme = (EditText) findViewById(R.id.theme);
		description = (EditText) findViewById(R.id.description);
		endTime = (EditText) findViewById(R.id.time_task);
		publishedPrice = (EditText) findViewById(R.id.price);
		bt_publish = (Button) findViewById(R.id.distribute);
		publishedLocation = (EditText) findViewById(R.id.location);
		spinnner = (RelativeLayout) findViewById(R.id.rl_spinner);
		mSpinnerTitle = (TextView) findViewById(R.id.tv_text);
		task_types = new ArrayList<String>();
		task_types.add("��������");
		task_types.add("ά�޷���");
		task_types.add("��������");
		task_types.add("��������");
		task_types.add("��������");
		task_types.add("�������");
		task_types.add("��������");
	}

	/**
	 * spinner�ĵ������
	 */
	@Override
	public void onClick(View v) {
		mAdd_task_layout.setVisibility(View.GONE);
		bt_publish.setVisibility(View.GONE);
		com.science.strangertofriend.utils.MyPopupWindow window = new com.science.strangertofriend.utils.MyPopupWindow(
				this, spinnner.getWidth(), task_types);
		window.showAsDropDown(spinnner, 0, 0);
		window.setOnItemClickListener(new onItemClickListener() {

			@Override
			public void click(int position, View view) {
				mAdd_task_layout.setVisibility(View.VISIBLE);
				bt_publish.setVisibility(View.VISIBLE);
				mSpinnerTitle.setText(task_types.get(position));
				// Toast.makeText(AddTaskActivity.this,
				// "�����˵�"+(position+1)+"��", Toast.LENGTH_SHORT).show();
				switch (position) {
				case 0:
					service_type = TaskType.SERVICE_HOUSEWORK;
					break;
				case 1:
					service_type = TaskType.SERVICE_FIX;
					break;
				case 2:
					service_type = TaskType.SERVICE_EXPRESS;

					break;
				case 3:
					service_type = TaskType.SERVICE_EDUCATION;
					break;
				case 4:
					service_type = TaskType.SERVICE_CATERING;
					break;
				case 5:
					service_type = TaskType.SERVICE_INTERNET;
					break;
				case 6:
					service_type = TaskType.SERVICE_OTHERS;
					break;
				default:

					break;
				}
			}
		});
	}

}
