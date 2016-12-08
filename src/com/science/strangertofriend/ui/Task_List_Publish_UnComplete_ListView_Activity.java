package com.science.strangertofriend.ui;

import java.util.ArrayList;
import java.util.List;

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

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.FindCallback;
import com.avos.avoscloud.GetCallback;
import com.avos.avoscloud.SaveCallback;
import com.science.strangertofriend.R;
import com.science.strangertofriend.adapter.Task_Accept_UnComplete_Adapter;
import com.science.strangertofriend.adapter.Task_Publish_Complete_Adapter;
import com.science.strangertofriend.adapter.Task_Publish_UnComplete_Adapter;
import com.science.strangertofriend.bean.Task;

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
	private ImageView img_no_task;
	// IamgeView ��Id����
	int[] imageIds = new int[] { R.id.image_root, R.id.image_publish,
			R.id.image_unpublish, R.id.image_accept, R.id.image_unaccept };
	private ListView listView = null;
	private Task_Publish_UnComplete_Adapter adapter = null;
	private int price = 0;// ���������
	private AVUser acceptor;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.task_list_activity_layout);

		initListView();
		isShow_Img_NoTask();
		initAnimations();// ��ʼ������
		// ���������Ĳ˵�
		super.registerForContextMenu(listView);
	}

	/**
	 * �жϵ�ǰҳ���Ƿ�������û������ʾno_taskͼƬ
	 * 
	 * @return
	 */
	public void isShow_Img_NoTask() {
		if (Task_Publish_UnComplete_Adapter.vector.size() > 0) {
			img_no_task.setVisibility(View.INVISIBLE);
		} else {
			img_no_task.setVisibility(View.VISIBLE);
			// img_no_task.setImageDrawable(getResources().getDrawable(R.drawable.notask_pub_unaccom));
			img_no_task.setImageResource(R.drawable.notask_pub_unaccom);
		}
	}

	/**
	 * ��ʼ��ListView
	 */
	private void initListView() {
		img_no_task = (ImageView) findViewById(R.id.img_no_task);
		listView = (ListView) this.findViewById(R.id.task_publish_list);
		adapter = Task_Publish_UnComplete_Adapter.initAdapter(this);
		listView.setAdapter(adapter);
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
		int id = v.getId();
		if (id == R.id.image_root) {
			openMenu();

		} else if (id == R.id.image_publish) {
			closeMenu();
			finish();
			startActivity(new Intent(
					Task_List_Publish_UnComplete_ListView_Activity.this,
					Task_List_Publish_Complete_ListView_Activity.class));
		} else if (id == R.id.image_unpublish) {
			closeMenu();
			finish();
			startActivity(new Intent(
					Task_List_Publish_UnComplete_ListView_Activity.this,
					Task_List_Publish_UnComplete_ListView_Activity.class));
		} else if (id == R.id.image_accept) {
			closeMenu();
			finish();
			startActivity(new Intent(
					Task_List_Publish_UnComplete_ListView_Activity.this,
					Task_List_Accept_Complete_ListView_Activity.class));
		} else if (id == R.id.image_unaccept) {
			closeMenu();
			finish();
			startActivity(new Intent(
					Task_List_Publish_UnComplete_ListView_Activity.this,
					Task_List_Accept_UnComplete_ListView_Activity.class));
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
			// Toast.makeText(getApplicationContext(), "ɾ��" + position,
			// Toast.LENGTH_LONG).show();
			deleteTask(position);
			break;
		case Menu.FIRST + 3:// ���
			Toast.makeText(getApplicationContext(), "���" + position,
					Toast.LENGTH_LONG).show();
			accepteTask(position);
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
	 *            ����Ķ��±�
	 */
	private void deleteTask(int position) {
		// ����ɾ��������
		Task task = Task_Accept_UnComplete_Adapter.vector.remove(position);
		adapter.notifyDataSetChanged();

		// ��ɾ��
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

	AVObject post = null;

	/**
	 * �������
	 * 
	 * @param position
	 *            ������±�
	 */
	private void accepteTask(int position) {
		// ����ɾ��������
		Task task = Task_Publish_UnComplete_Adapter.vector.remove(position);
		adapter.notifyDataSetChanged();
		// ��ӵ���ɶ��в�����
		Task_Publish_Complete_Adapter.vector.add(task);
		Task_Publish_Complete_Adapter.initAdapter().notifyDataSetChanged();

		// Log.i("taskid", task.getObjectId() + "");
		final AVQuery<AVObject> query = new AVQuery<AVObject>("Task");
		query.include("acceptedUser");
		query.getInBackground(task.getObjectId(), new GetCallback<AVObject>() {

			@Override
			public void done(AVObject arg0, AVException arg1) {
				post = arg0;
				post.put("isAccomplished", true);
				price = Integer.parseInt(post.getString("price"));
				acceptor = post.getAVUser("acceptedUser");
				post.saveInBackground(new SaveCallback() {
					@Override
					public void done(AVException e) {
						if (e == null) {
							updateUserTotalGolds();
							Log.i("LeanCloud", "Save successfully.");
						} else {
							Log.e("LeanCloud", "Save failed.");
						}
					}
				});
			}
		});

	}

	/**
	 * ���¸��������
	 */
	public void updateUserTotalGolds() {
		if (price != 0) {
			String currentUsername = AVUser.getCurrentUser().getUsername();
			String acceptorUsername = acceptor.getUsername();

			// �����񷢲��˵����������
			AVQuery<AVObject> query = new AVQuery<AVObject>("userAccount");
			query.whereEqualTo("username", currentUsername);
			query.findInBackground(new FindCallback<AVObject>() {

				@Override
				public void done(List<AVObject> arg0, AVException arg1) {
					AVObject userAccount = arg0.get(arg0.size() - 1);
					int golds = userAccount.getInt("totalGolds");
					userAccount.put("totalGolds", golds - price);
					userAccount.saveInBackground();
				}
			});
			// ����������˵���Ŀ����
			AVQuery<AVObject> query2 = new AVQuery<AVObject>("userAccount");
			query2.whereEqualTo("username", acceptorUsername);
			query2.findInBackground(new FindCallback<AVObject>() {

				@Override
				public void done(List<AVObject> arg0, AVException arg1) {
					AVObject userAccount = arg0.get(arg0.size() - 1);
					int golds = userAccount.getInt("totalGolds");
					userAccount.put("totalGolds", golds + price);
					userAccount.saveInBackground();
				}
			});
		}
	}

	private void closeMenu() {
		for (int i = 0; i < imageIds.length; i++) {
			AnimatorSet animatorSet = new AnimatorSet();
			ObjectAnimator animator1 = ObjectAnimator.ofFloat(
					imageViews.get(i), "translationX", getScreenWidth()/5 * i, 0);
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
					imageViews.get(i), "translationX", 0, getScreenWidth()/5 * i);
			animatorSet.playTogether(animator1);
			animatorSet.setDuration(500);
			animatorSet.setInterpolator(new BounceInterpolator());
			animatorSet.start();
		}
	}

}
