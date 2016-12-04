package com.science.strangertofriend.ui;

import com.iflytek.cloud.InitListener;
import com.science.strangertofriend.R;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
/**
 * 是否开启人脸检测界面
 * @author 49149
 *
 */
public class FaceDetect extends BaseActivity{
	private RadioGroup radioGroup;
	private RadioButton rbt_checked;//被选中bt
	private SharedPreferences.Editor editor;
	public static final String IS_OPEN_FACE_VERIFY="com.science.strangertofriend.ui.FaceDetect";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_face_detect);
		
		initView();
		initListener();
	}


	private void initListener() {
		radioGroup.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				rbt_checked=(RadioButton) findViewById(checkedId);
				if(rbt_checked.getText().equals("是")){
						editor.putBoolean("isOpenFaceVerify", true);
				}else {
					editor.putBoolean("isOpenFaceVerify", false);
				}
				editor.commit();
			}
		});
	}


	private void initView() {
		radioGroup=(RadioGroup) findViewById(R.id.radioGroup);
		rbt_checked=(RadioButton) findViewById(radioGroup.getCheckedRadioButtonId());
		editor=getSharedPreferences(IS_OPEN_FACE_VERIFY, MODE_PRIVATE).edit();
		boolean isOpen= getSharedPreferences(IS_OPEN_FACE_VERIFY, MODE_PRIVATE).getBoolean("isOpenFaceVerify", false);
		if(isOpen){
			radioGroup.check(R.id.rbt_yes);
		}else {
			radioGroup.check(R.id.rbt_no);
		}
	}

}
