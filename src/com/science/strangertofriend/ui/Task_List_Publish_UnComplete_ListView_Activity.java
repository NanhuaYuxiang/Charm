package com.science.strangertofriend.ui;

import java.util.ArrayList;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.BounceInterpolator;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.science.strangertofriend.R;
import com.science.strangertofriend.adapter.Task_Accept_UnComplete_Adapter;

/**
 * 任务历史清单 发布任务还未完成的
 * 
 * @author lilin
 * @date 2015年11月9日 .下午9:24:06
 * @blog www.gaosililn.iteye.com
 * @email gaosi0812@gamil.com
 * @school usc
 *
 */
public class Task_List_Publish_UnComplete_ListView_Activity extends
		BaseActivity implements OnClickListener {
	private ArrayList<ImageView> imageViews;// IamgeView 的队列
	// IamgeView 的Id数组
	int[] imageIds = new int[] { R.id.image_root, R.id.image_publish,
			R.id.image_unpublish, R.id.image_accept, R.id.image_unaccept };
	private ListView listView = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.task_list_activity_layout);

		initListView();

		initAnimations();// 初始化动画
		// 设置上下文菜单
		super.registerForContextMenu(listView);
	}

	/**
	 * 初始化ListView
	 */
	private void initListView() {

		listView = (ListView) this.findViewById(R.id.task_publish_list);
		listView.setAdapter(new Task_Accept_UnComplete_Adapter(this));
	}

	/**
	 * 初始化动画
	 */
	private void initAnimations() {
		DisplayMetrics displayMetrics = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
		int height = displayMetrics.heightPixels;
		ImageView imageView = null;
		imageViews = new ArrayList<ImageView>();
		for (int imageId : imageIds) {
			imageView = (ImageView) findViewById(imageId);
			imageViews.add(imageView);
			imageView.setOnClickListener(this);
		}

	}

	/**
	 * 监听事件
	 */
	@Override
	public void onClick(View v) {
		Log.e("info", "id:" + v.getId());
		switch (v.getId()) {
		case R.id.image_root:
			closeMenu();
			break;
		case R.id.image_publish:
			startActivity(new Intent(
					Task_List_Publish_UnComplete_ListView_Activity.this,
					Task_List_Publish_Complete_ListView_Activity.class));
			closeMenu();
			break;
		case R.id.image_unpublish:
			startActivity(new Intent(
					Task_List_Publish_UnComplete_ListView_Activity.this,
					Task_List_Publish_UnComplete_ListView_Activity.class));

			closeMenu();
			break;
		case R.id.image_accept:
			startActivity(new Intent(
					Task_List_Publish_UnComplete_ListView_Activity.this,
					Task_List_Accept_Complete_ListView_Activity.class));

			closeMenu();
			break;
		case R.id.image_unaccept:
			startActivity(new Intent(
					Task_List_Publish_UnComplete_ListView_Activity.this,
					Task_List_Accept_UnComplete_ListView_Activity.class));

			openMenu();
			break;

		default:
			break;
		}

	}

	private static final int MENUORDER = 1;

	/**
	 * 长按菜单
	 */
	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		// TODO Auto-generated method stub
		super.onCreateContextMenu(menu, v, menuInfo);
		menu.setHeaderTitle("选择相应的操作");
		menu.add(Menu.NONE, Menu.FIRST + 1, MENUORDER, "联系");
		menu.add(Menu.NONE, Menu.FIRST + 2, MENUORDER, "删除");
		menu.add(Menu.NONE, Menu.FIRST + 3, MENUORDER, "完成");
	}

	/**
	 * 菜单被选择
	 */
	@Override
	public boolean onContextItemSelected(MenuItem item) {
		// 实例化info进而通过info.position得到ListView中的那一项(id)被选中，从而生产上下文菜单并显示
		AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item
				.getMenuInfo();
		int position = info.position;
		int itemId = item.getItemId();
		switch (itemId) {
		case Menu.FIRST + 1:// 联系
			Toast.makeText(getApplicationContext(), "联系" + position,
					Toast.LENGTH_LONG).show();
			break;
		case Menu.FIRST + 2:// 删除
			Toast.makeText(getApplicationContext(), "删除" + position,
					Toast.LENGTH_LONG).show();
			break;
		case Menu.FIRST + 3:// 完成
			Toast.makeText(getApplicationContext(), "完成" + position,
					Toast.LENGTH_LONG).show();
			break;
		default:
			break;
		}

		return super.onContextItemSelected(item);
	}

	private void closeMenu() {
		for (int i = 0; i < imageIds.length; i++) {
			AnimatorSet animatorSet = new AnimatorSet();
			ObjectAnimator animator1 = ObjectAnimator.ofFloat(
					imageViews.get(i), "translationX", 100 * i, 0);
			animatorSet.playTogether(animator1);
			animatorSet.setDuration(500);
			animatorSet.setInterpolator(new BounceInterpolator());
			animatorSet.start();
		}

	}

	private void openMenu() {
		for (int i = 0; i < imageIds.length; i++) {
			AnimatorSet animatorSet = new AnimatorSet();
			ObjectAnimator animator1 = ObjectAnimator.ofFloat(
					imageViews.get(i), "translationX", 0, 100 * i);
			animatorSet.playTogether(animator1);
			animatorSet.setDuration(500);
			animatorSet.setInterpolator(new BounceInterpolator());
			animatorSet.start();
		}
	}

}
