package com.science.strangertofriend.fragment;

import yalantis.com.sidemenu.interfaces.ScreenShotable;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Vibrator;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationSet;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.science.strangertofriend.R;
import com.science.strangertofriend.listener.ShakeListener;
import com.science.strangertofriend.listener.ShakeListener.OnShakeListener;
import com.science.strangertofriend.ui.ShowNearMenMapActivity;

/**
 * @description ҡһҡ����
 * 
 * 
 */

public class ShakeFragment extends Fragment implements ScreenShotable {

	private View mContainerView;
	private Bitmap mBitmap;
	private View mRootView;

	// ҡһҡ
	private RelativeLayout mImgUp;//ҡ���ֻ������ϵİ��ͼƬ
	private RelativeLayout mImgDn;//ҡ���ֻ������µİ��ͼƬ
	private Vibrator mVibrator;//����
	private ShakeListener mShakeListener = null;
	private ImageView shake_line_up;
	private ImageView shake_line_down;
	private ImageView shakeBg;
	private SoundPool soundPool;// ����һ��SoundPool�������أ�
	private int music;// ��
	private int musicMatch;// ��
	private boolean flg = true;

	@Override
	public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		this.mContainerView = view.findViewById(R.id.shake_container);
	}

	@Override
	public View onCreateView(LayoutInflater inflater,
			@Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

		mRootView = inflater.inflate(R.layout.shake_fragment, container, false);

		initSound();
		initView();
		initListener();

		return mRootView;
	}

	@SuppressWarnings("static-access")
	private void initView() {

		mVibrator = (Vibrator) getActivity().getApplication().getSystemService(
				getActivity().VIBRATOR_SERVICE);
		mImgUp = (RelativeLayout) mRootView.findViewById(R.id.shake_up_ll);
		mImgDn = (RelativeLayout) mRootView.findViewById(R.id.shake_down_ll);
		shake_line_up = (ImageView) mRootView.findViewById(R.id.shake_line_up);
		shake_line_down = (ImageView) mRootView
				.findViewById(R.id.shake_line_down);
		shakeBg = (ImageView) mRootView.findViewById(R.id.shakeBg);

	}

	private void initListener() {
		mShakeListener = new ShakeListener(getActivity());
		mShakeListener.setOnShakeListener(new OnShakeListener() {
			public void onShake() {
				// Toast.makeText(getApplicationContext(),
				// "��Ǹ����ʱû���ҵ���ͬһʱ��ҡһҡ���ˡ�\n����һ�ΰɣ�", Toast.LENGTH_SHORT).show();
				// shake_line_up.setVisibility(View.VISIBLE);
				// shake_line_down.setVisibility(View.VISIBLE);
				// ��ʼ ҡһҡ���ƶ���
				startAnim();
				mShakeListener.stop();
				// shake_line_up.setVisibility(View.GONE);
				// shake_line_down.setVisibility(View.GONE);
				startVibrato(); // ��ʼ ��
				new Handler().postDelayed(new Runnable() {
					@SuppressLint("ShowToast")
					@Override
					public void run() {
						soundPool.play(musicMatch, 1, 1, 0, 0, 1);
						new CountDownTimer(1000, 1000) {
							@Override
							public void onTick(long millisUntilFinished) {
							}

							@Override
							public void onFinish() {

								Intent intent = new Intent(getActivity(),
										ShowNearMenMapActivity.class);
								startActivity(intent);
							}
						}.start();
						mVibrator.cancel();
					}
				}, 2000);

				// if (!Utils.isFastDoubleShake()) {
				// mShakeListener.start();
				// }
			}
		});
	}

	// ��ֹ����ҡһҡ
	@Override
	public void onStart() {
		super.onStart();
		mShakeListener.start();
	}

	@SuppressWarnings("deprecation")
	public void initSound() {
		soundPool = new SoundPool(10, AudioManager.STREAM_SYSTEM, 5);
		music = soundPool.load(getActivity(), R.raw.shake_sound_male, 1);
		musicMatch = soundPool.load(getActivity(), R.raw.shake_match, 1);

	}

	// ����ҡһҡ��������
	public void startAnim() {
		AnimationSet animup = new AnimationSet(true);
		TranslateAnimation mytranslateanimup0 = new TranslateAnimation(
				Animation.RELATIVE_TO_SELF, 0f, Animation.RELATIVE_TO_SELF, 0f,
				Animation.RELATIVE_TO_SELF, 0f, Animation.RELATIVE_TO_SELF,
				-0.5f);
		mytranslateanimup0.setDuration(1000);
		TranslateAnimation mytranslateanimup1 = new TranslateAnimation(
				Animation.RELATIVE_TO_SELF, 0f, Animation.RELATIVE_TO_SELF, 0f,
				Animation.RELATIVE_TO_SELF, 0f, Animation.RELATIVE_TO_SELF,
				+0.5f);
		mytranslateanimup1.setDuration(1000);
		mytranslateanimup1.setStartOffset(1000);
		animup.addAnimation(mytranslateanimup0);
		animup.addAnimation(mytranslateanimup1);
		animup.setAnimationListener(new AnimationListener() {
			@Override
			public void onAnimationStart(Animation animation) {
				shake_line_up.setVisibility(View.VISIBLE);
				if (flg) {
					soundPool.play(music, 1, 1, 0, 0, 1);
				}
			}

			@Override
			public void onAnimationRepeat(Animation animation) {
			}

			@Override
			public void onAnimationEnd(Animation animation) {
				shake_line_up.setVisibility(View.GONE);
				// soundPool.stop(music);
			}
		});
		mImgUp.startAnimation(animup);

		AnimationSet animdn = new AnimationSet(true);
		TranslateAnimation mytranslateanimdn0 = new TranslateAnimation(
				Animation.RELATIVE_TO_SELF, 0f, Animation.RELATIVE_TO_SELF, 0f,
				Animation.RELATIVE_TO_SELF, 0f, Animation.RELATIVE_TO_SELF,
				+0.5f);
		mytranslateanimdn0.setDuration(1000);
		TranslateAnimation mytranslateanimdn1 = new TranslateAnimation(
				Animation.RELATIVE_TO_SELF, 0f, Animation.RELATIVE_TO_SELF, 0f,
				Animation.RELATIVE_TO_SELF, 0f, Animation.RELATIVE_TO_SELF,
				-0.5f);
		mytranslateanimdn1.setDuration(1000);
		mytranslateanimdn1.setStartOffset(1000);
		animdn.addAnimation(mytranslateanimdn0);
		animdn.addAnimation(mytranslateanimdn1);
		animdn.setAnimationListener(new AnimationListener() {
			@Override
			public void onAnimationStart(Animation animation) {
				shake_line_down.setVisibility(View.VISIBLE);
			}

			@Override
			public void onAnimationRepeat(Animation animation) {
			}

			@Override
			public void onAnimationEnd(Animation animation) {
				shake_line_down.setVisibility(View.GONE);
			}
		});
		mImgDn.startAnimation(animdn);
	}

	// ������
	public void startVibrato() {
		mVibrator.vibrate(new long[] { 500, 200, 500, 200 }, -1);
		// ��һ�����������ǽ������飬
		// �ڶ����������ظ�������-1Ϊ���ظ�����-1���մ�pattern��ָ���±꿪ʼ�ظ�
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		if (mShakeListener != null) {
			mShakeListener.stop();
		}
	}

	@Override
	public void takeScreenShot() {
		new Handler().postDelayed(new Runnable() {
			@Override
			public void run() {
				Bitmap bitmap = Bitmap.createBitmap(mContainerView.getWidth(),
						mContainerView.getHeight(), Bitmap.Config.ARGB_8888);
				Canvas canvas = new Canvas(bitmap);
				mContainerView.draw(canvas);
				ShakeFragment.this.mBitmap = bitmap;
			}
		}, 0);
		// Thread thread = new Thread() {
		// @Override
		// public void run() {
		// Bitmap bitmap = Bitmap.createBitmap(mContainerView.getWidth(),
		// mContainerView.getHeight(), Bitmap.Config.ARGB_8888);
		// Canvas canvas = new Canvas(bitmap);
		// mContainerView.draw(canvas);
		// ShakeFragment.this.mBitmap = bitmap;
		// }
		// };
		// thread.start();
	}

	@Override
	public Bitmap getBitmap() {
		return mBitmap;
	}
}
