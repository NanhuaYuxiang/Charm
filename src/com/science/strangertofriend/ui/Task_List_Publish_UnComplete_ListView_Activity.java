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
 * ������ʷ�嵥 ��������δ��ɵ�
 * 
 * @author lilin
 * @date 2015��11��9�� .����9:24:06
 * @blog www.gaosililn.iteye.com
 * @email gaosi0812@gamil.com
 * @school usc
 *
 */
public class Task_List_Publish_UnComplete_ListView_Activity extends
		BaseActivity implements OnClickListener {
	private ArrayList<ImageView> imageViews;// IamgeView �Ķ���
	// IamgeView ��Id����
	int[] imageIds = new int[] { R.id.image_root, R.id.image_publish,
			R.id.image_unpublish, R.id.image_accept, R.id.image_unaccept };
	private ListView listView = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.task_list_activity_layout);

		initListView();

		initAnimations();// ��ʼ������
		// ���������Ĳ˵�
		super.registerForContextMenu(listView);
	}

	/**
	 * ��ʼ��ListView
	 */
	private void initListView() {

		listView = (ListView) this.findViewById(R.id.task_publish_list);
		listView.setAdapter(new Task_Accept_UnComplete_Adapter(this));
	}

	/**
	 * ��ʼ������
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
	 * �����¼�
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
	 * �����˵�
	 */
	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		// TODO Auto-generated method stub
		super.onCreateContextMenu(menu, v, menuInfo);
		menu.setHeaderTitle("ѡ����Ӧ�Ĳ���");
		menu.add(Menu.NONE, Menu.FIRST + 1, MENUORDER, "��ϵ");
		menu.add(Menu.NONE, Menu.FIRST + 2, MENUORDER, "ɾ��");
		menu.add(Menu.NONE, Menu.FIRST + 3, MENUORDER, "���");
	}

	/**
	 * �˵���ѡ��
	 */
	@Override
	public boolean onContextItemSelected(MenuItem item) {
		// ʵ����info����ͨ��info.position�õ�ListView�е���һ��(id)��ѡ�У��Ӷ����������Ĳ˵�����ʾ
		AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item
				.getMenuInfo();
		int position = info.position;
		int itemId = item.getItemId();
		switch (itemId) {
		case Menu.FIRST + 1:// ��ϵ
			Toast.makeText(getApplicationContext(), "��ϵ" + position,
					Toast.LENGTH_LONG).show();
			break;
		case Menu.FIRST + 2:// ɾ��
			Toast.makeText(getApplicationContext(), "ɾ��" + position,
					Toast.LENGTH_LONG).show();
			break;
		case Menu.FIRST + 3:// ���
			Toast.makeText(getApplicationContext(), "���" + position,
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
