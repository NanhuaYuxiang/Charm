package com.science.strangertofriend.ui;

import android.annotation.TargetApi;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.readystatesoftware.systembartint.SystemBarTintManager;
import com.science.strangertofriend.R;
/**
 * 语音设置界面
 * @author 49149
 *
 */
public class VoiceSetting extends BaseActivity implements OnClickListener{
	public static final String VOICESETTING="com.science.strangertofriend.ui.VoiceSetting";
	private EditText et_time,et_phoneNumber1,et_phoneNumber2,et_phoneNumber3;
	private Button bt_save;
	private ImageView img_back;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_voice_setting);
		initView();
	}
	private void initView() {
		img_back=(ImageView) findViewById(R.id.back_img);
		img_back.setOnClickListener(this);
		et_time=(EditText) findViewById(R.id.ed_time);
		et_phoneNumber1=(EditText) findViewById(R.id.et_phoneNumber1);
		et_phoneNumber2=(EditText) findViewById(R.id.et_phoneNumber2);
		et_phoneNumber3=(EditText) findViewById(R.id.et_phoneNumber3);
		bt_save=(Button) findViewById(R.id.bt_save);
		bt_save.setOnClickListener(this);
		SharedPreferences preferences=getSharedPreferences(VOICESETTING, MODE_PRIVATE);
		
		int tempTime=Integer.parseInt(preferences.getString("time", "4"))/1000;
		et_time.setText(tempTime+"");
		et_phoneNumber1.setText(preferences.getString("phone1", "123456"));
		
	}
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.bt_save:
			String time=et_time.getText().toString();//讯飞接收的为毫秒,故此处也要讲time转化成毫秒
			time=Integer.parseInt(time)*1000+"";
			String phone1=et_phoneNumber1.getText().toString();
			String phone2=et_phoneNumber2.getText().toString();
			String phone3=et_phoneNumber3.getText().toString();
			
			SharedPreferences.Editor editor=getSharedPreferences(VOICESETTING, MODE_PRIVATE).edit();
			editor.putString("time", time);
			editor.putString("phone1", phone1);
			editor.putString("phone2", phone2);
			editor.putString("phone3", phone3);
			editor.commit();
			Toast.makeText(this, "保存完成", Toast.LENGTH_LONG).show();
			break;
		case R.id.back_img:
			finish();
			break;
		default:
			break;
		}
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

}
