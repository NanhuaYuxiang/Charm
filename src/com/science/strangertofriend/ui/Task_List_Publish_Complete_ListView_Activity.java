package com.science.strangertofriend.ui;

import java.util.ArrayList;
import java.util.List;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
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

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.FindCallback;
import com.science.strangertofriend.R;
import com.science.strangertofriend.adapter.Task_Accept_Complete_Adapter;
import com.science.strangertofriend.adapter.Task_Publish_Complete_Adapter;
import com.science.strangertofriend.bean.Task;

/**
 * ������ʷ�嵥 ������������ɵ�
 * 
 * @author lilin
 * @date 2015��11��9�� .����9:24:06
 * @blog www.gaosililn.iteye.com
 * @email gaosi0812@gamil.com
 * @school usc
 *
 */
public class Task_List_Publish_Complete_ListView_Activity extends BaseActivity
		implements OnClickListener {
	private ArrayList<ImageView> imageViews;// IamgeView �Ķ���
	// IamgeView ��Id����
	int[] imageIds = new int[] { R.id.image_root, R.id.image_publish,
			R.id.image_unpublish, R.id.image_accept, R.id.image_unaccept };
	private ListView listView = null;

	private Task_Publish_Complete_Adapter adapter = null;
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
		adapter = Task_Publish_Complete_Adapter.initAdapter(this);
		listView.setAdapter(adapter);
		listViewSetListener();// ���ü���
	}

	/**
	 * listView ���ü���
	 */
	private void listViewSetListener() {
		listView.setOnItemLongClickListener(new android.widget.AdapterView.OnItemLongClickListener() {

			@Override
			public boolean onItemLongClick(AdapterView<?> parent, View view,
					int position, long id) {
				AlertDialog.Builder builder = new Builder(
						getApplicationContext());
				builder.setTitle("ѡ����Ӧ�Ĳ���");
				builder.setMessage("");
				builder.setIcon(R.drawable.ic_keyboard_arrow_down_black);
				builder.setPositiveButton("ȷ��",
						new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which) {
							}
						});
				// builder.setNegativeButton("", listener)

				return false;
			}

		});

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
			Toast.makeText(getApplicationContext(), "��ϵ"+position, Toast.LENGTH_LONG)
					.show();
			break;
		case Menu.FIRST + 2:// ɾ��
//			Toast.makeText(getApplicationContext(), "ɾ��"+position, Toast.LENGTH_LONG)
//					.show();
			deleteTask(position);
			break;
		default:
			break;
		}

		return super.onContextItemSelected(item);
	}
	
	/**
	 * ɾ������
	 * 
	 * @param position
	 *            ������±�
	 */
	private void deleteTask(int position) {
		//����ɾ��������
		Task task = Task_Publish_Complete_Adapter.vector.remove(position);
		adapter.notifyDataSetChanged();
		//��ɾ��
		final AVQuery<AVObject> query = new AVQuery<AVObject>("Task");
		query.whereEqualTo("objectId", task.getObjectId());
		query.findInBackground(new FindCallback<AVObject>() {
		    public void done(List<AVObject> avObjects, AVException e) {
		        if (e == null) {
		        	try {
						query.deleteAll();
					} catch (AVException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
		        } else {
		            Log.d("ʧ��", "��ѯ����: " + e.getMessage());
		        }
		    }
		});
		

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
			openMenu();
			break;
		case R.id.image_publish:
			closeMenu();
			startActivity(new Intent(
					Task_List_Publish_Complete_ListView_Activity.this,
					Task_List_Publish_Complete_ListView_Activity.class));
			finish();
			break;
		case R.id.image_unpublish:

			closeMenu();
			startActivity(new Intent(
					Task_List_Publish_Complete_ListView_Activity.this,
					Task_List_Publish_UnComplete_ListView_Activity.class));
			
			finish();
			break;
		case R.id.image_accept:

			closeMenu();
			startActivity(new Intent(
					Task_List_Publish_Complete_ListView_Activity.this,
					Task_List_Accept_Complete_ListView_Activity.class));
			finish();
			break;
		case R.id.image_unaccept:

			closeMenu();
			startActivity(new Intent(
					Task_List_Publish_Complete_ListView_Activity.this,
					Task_List_Accept_UnComplete_ListView_Activity.class));
			finish();
			break;

		default:
			Log.e("info", "default");
			break;
		}

	}

	/**
	 * �رղ˵�
	 */
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

	/**
	 * �򿪲˵�
	 */
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
