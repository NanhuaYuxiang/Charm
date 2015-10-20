package com.science.strangertofriend.ui;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.annotation.TargetApi;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewTreeObserver;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.readystatesoftware.systembartint.SystemBarTintManager;
import com.science.strangertofriend.R;
import com.science.strangertofriend.widget.RevealLayout;

/**
 * @description
 * 
 * @author 幸运Science 陈土燊
 * @school University of South China
 * @email chentushen.science@gmail.com,274240671@qq.com
 * @2015-8-18
 * 
 */

public class ContactActivity extends BaseActivity {

	private RevealLayout mRevealLayout;
	private LinearLayout mLayout;
	private ImageView mBackImg;
	private TextView mTitle;

	private ListView listView;
	public String[] checkNumber;
	public String number = "";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.contactactivity);

		initView();
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
		mLayout = (LinearLayout) findViewById(R.id.layout);
		mLayout.setBackgroundColor(Color.WHITE);

		mBackImg = (ImageView) findViewById(R.id.back_img);
		mTitle = (TextView) findViewById(R.id.title);
		mTitle.setText("邀请人");

		listView = (ListView) findViewById(R.id.listview);
		show();
		checkNumber = new String[listView.getCount()];
		for (int i = 0; i < checkNumber.length; i++) {
			checkNumber[i] = "";
		}
		Intent intent = getIntent();
		Bundle bundle = intent.getExtras();
		number = bundle.getString("number");
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

				ContactActivity.this.finish();
			}
		});

		listView.setOnItemClickListener(new itemClickListener());

	}

	private final class itemClickListener implements OnItemClickListener {
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			CheckBox box = (CheckBox) view.findViewById(R.id.checkbox);
			TextView view2 = (TextView) view.findViewById(R.id.number);
			if (box.isChecked()) {
				checkNumber[position] = "";
			} else {
				checkNumber[position] = view2.getText().toString();
			}
			box.setChecked(!box.isChecked());
		}
	}

	public void confirmClick(View v) {
		Intent data = new Intent();
		String response = number;
		if (response.length() > 0
				&& response.charAt(response.length() - 1) != ';')
			response = response + ";";
		for (int i = 0; i < checkNumber.length; i++) {
			if (checkNumber[i] != "" && response.indexOf(checkNumber[i]) < 0)
				response = response + checkNumber[i] + ";";
		}
		data.putExtra("result", response);
		setResult(30, data);
		this.finish();
	}

	public void contentClick(View v) {
		Intent data = new Intent();
		String response = number;
		data.putExtra("result", response);
		setResult(40, data);
		this.finish();
	}

	private void show() {
		Uri uri = Uri.parse("content://com.android.contacts/contacts");
		ContentResolver resolver = this.getContentResolver();
		Cursor cursor = resolver.query(uri, new String[] { "_id" }, null, null,
				null);
		List<HashMap<String, Object>> data = new ArrayList<HashMap<String, Object>>();
		while (cursor.moveToNext()) {
			int contactid = cursor.getInt(0);
			uri = Uri.parse("content://com.android.contacts/contacts/"
					+ contactid + "/data");
			Cursor datacursor = resolver.query(uri, new String[] { "mimetype",
					"data1", "data2" }, null, null, null);
			HashMap<String, Object> item = new HashMap<String, Object>();
			while (datacursor.moveToNext()) {
				String data1 = datacursor.getString(datacursor
						.getColumnIndex("data1"));
				String type = datacursor.getString(datacursor
						.getColumnIndex("mimetype"));
				if ("vnd.android.cursor.item/name".equals(type)) {
					item.put("name", data1);
				} else if ("vnd.android.cursor.item/phone_v2".equals(type)) {
					item.put("number", data1);
				}
			}
			data.add(item);
		}
		SimpleAdapter adapter = new SimpleAdapter(this, data, R.layout.item,
				new String[] { "name", "number" }, new int[] { R.id.name,
						R.id.number });
		listView.setAdapter(adapter);
	}
}
