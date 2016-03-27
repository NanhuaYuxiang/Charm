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
import com.avos.avoscloud.FindCallback;
import com.avos.avoscloud.GetCallback;
import com.avos.avoscloud.SaveCallback;
import com.science.strangertofriend.R;
import com.science.strangertofriend.adapter.Task_Accept_Complete_Adapter;
import com.science.strangertofriend.adapter.Task_Accept_UnComplete_Adapter;
import com.science.strangertofriend.adapter.Task_Publish_Complete_Adapter;
import com.science.strangertofriend.bean.Task;

/**
 * 任务历史清单 接收任务还未完成的
 * 
 * @author lilin
 * @date 2015年11月9日 .下午9:24:06
 * @blog www.gaosililn.iteye.com
 * @email gaosi0812@gamil.com
 * @school usc
 *
 */
public class Task_List_Accept_UnComplete_ListView_Activity extends BaseActivity
		implements OnClickListener {
	private ArrayList<ImageView> imageViews;// IamgeView 的队列
	private ImageView img_no_task;
	// IamgeView 的Id数组
	int[] imageIds = new int[] { R.id.image_root, R.id.image_publish,
			R.id.image_unpublish, R.id.image_accept, R.id.image_unaccept };
	private ListView listView = null;
	private Task_Accept_UnComplete_Adapter adapter = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.task_list_activity_layout);

		initListView();
		isShow_Img_NoTask();
		initAnimations();// 初始化动画
		// 设置上下文菜单
		super.registerForContextMenu(listView);
	}


	/**
	 * 判断当前页面是否有任务，没有则显示no_task图片
	 * 
	 * @return
	 */
	public void isShow_Img_NoTask() {
		if (Task_Accept_UnComplete_Adapter.vector.size() > 0) {
			img_no_task.setVisibility(View.INVISIBLE);
		} else {
			img_no_task.setVisibility(View.VISIBLE);
			//img_no_task.setImageDrawable(getDrawable(R.drawable.notask_accep_unaccom));
			img_no_task.setImageResource(R.drawable.notask_accep_unaccom);
		}
	}
	/**
	 * 初始化ListView
	 */
	private void initListView() {
		img_no_task=(ImageView) findViewById(R.id.img_no_task);
		listView = (ListView) this.findViewById(R.id.task_publish_list);
		adapter = Task_Accept_UnComplete_Adapter.initAdapter(this);
		listView.setAdapter(adapter);
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
			openMenu();
			break;
		case R.id.image_publish:
			closeMenu();
			finish();
			startActivity(new Intent(
					Task_List_Accept_UnComplete_ListView_Activity.this,
					Task_List_Publish_Complete_ListView_Activity.class));
			break;
		case R.id.image_unpublish:
			closeMenu();
			finish();
			startActivity(new Intent(
					Task_List_Accept_UnComplete_ListView_Activity.this,
					Task_List_Publish_UnComplete_ListView_Activity.class));
			break;
		case R.id.image_accept:
			closeMenu();
			finish();
			startActivity(new Intent(
					Task_List_Accept_UnComplete_ListView_Activity.this,
					Task_List_Accept_Complete_ListView_Activity.class));
			break;
		case R.id.image_unaccept:
			closeMenu();
			finish();
			startActivity(new Intent(
					Task_List_Accept_UnComplete_ListView_Activity.this,
					Task_List_Accept_UnComplete_ListView_Activity.class));
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
			// Toast.makeText(getApplicationContext(), "删除" + position,
			// Toast.LENGTH_LONG).show();
			deleteTask(position);
			break;
		case Menu.FIRST + 3:// 完成
			// Toast.makeText(getApplicationContext(), "完成" + position,
			// Toast.LENGTH_LONG).show();
			accepteTask(position);
			break;
		default:
			break;
		}

		return super.onContextItemSelected(item);
	}

	/**
	 * 删除任务
	 * 
	 * @param position
	 *            任务的额下标
	 */
	private void deleteTask(int position) {
		// 本地删除并更新
		Task task = Task_Accept_UnComplete_Adapter.vector.remove(position);
		adapter.notifyDataSetChanged();

		// 云删除
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
					Log.d("失败", "查询错误: " + e.getMessage());
				}
			}
		});

	}

	AVObject post = null;
	/**
	 * 完成任务
	 * 
	 * @param position
	 *            任务的下标
	 */
	private void accepteTask(int position) {
		// 本地删除并更新
		Task task = Task_Accept_UnComplete_Adapter.vector.remove(position);
		adapter.notifyDataSetChanged();
		// 添加到完成队列并更新
		Task_Accept_Complete_Adapter.vector.add(task);
		Task_Accept_Complete_Adapter.initAdapter().notifyDataSetChanged();

		String tableName = "Task";
		AVQuery<AVObject> query = new AVQuery<AVObject>(tableName);


		query.getInBackground(task.getObjectId(), new GetCallback<AVObject>() {
			
			@Override
			public void done(AVObject arg0, AVException arg1) {
				post=arg0;
				post.put("isAccomplished",true);
				post.saveInBackground(new SaveCallback() {
					@Override
					public void done(AVException e) {
						if (e == null) {
							Log.i("LeanCloud", "Save successfully.");
						} else {
							Log.e("LeanCloud", "Save failed.");
						}
					}
				});
			}
		});
		
	}

	private void closeMenu() {
		for (int i = 0; i < imageIds.length; i++) {
			AnimatorSet animatorSet = new AnimatorSet();
			ObjectAnimator animator1 = ObjectAnimator.ofFloat(
					imageViews.get(i), "translationX", 150 * i, 0);
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
					imageViews.get(i), "translationX", 0, 150 * i);
			animatorSet.playTogether(animator1);
			animatorSet.setDuration(500);
			animatorSet.setInterpolator(new BounceInterpolator());
			animatorSet.start();
		}
	}

}
