package com.science.strangertofriend.adapter;

import java.util.Vector;

import com.science.strangertofriend.R;
import com.science.strangertofriend.R.color;
import com.science.strangertofriend.bean.Task;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

/**
 * 完成任务列表的适配器
 * 
 * @author lilin
 * @date 2015年11月11日 .下午2:57:24
 * @blog www.gaosililn.iteye.com
 * @email gaosi0812@gamil.com
 * @school usc
 *
 */
public class Task_Accept_UnComplete_Adapter extends BaseAdapter {
	public static Vector<Task> vector = null;
	private LayoutInflater layoutInflater = null;

	public Task_Accept_UnComplete_Adapter() {
	}

	public Task_Accept_UnComplete_Adapter(Activity activity) {
		vector = new Vector<Task>();
		layoutInflater = activity.getLayoutInflater();
//		vector.add(new Task());
//		vector.add(new Task());
//		vector.add(new Task());
	}

	public int getCount() {
		// TODO Auto-generated method stub
		return vector.size();
	}

	@Override
	public Object getItem(int position) {

		return vector.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		ViewHolder viewHolder = null;
		if (convertView == null) {
			viewHolder = new ViewHolder();
			convertView = layoutInflater.inflate(
					R.layout.task_accept_uncomplete_layout, null, false);

			viewHolder.publish_type = (TextView) convertView
					.findViewById(R.id.publish_type);
			viewHolder.publish_time = (TextView) convertView
					.findViewById(R.id.publish_time);
			viewHolder.publish_address = (TextView) convertView
					.findViewById(R.id.publish_address);
			viewHolder.publish_gold = (TextView) convertView
					.findViewById(R.id.publish_gold);
			viewHolder.publish_topsy_turvy = (TextView) convertView
					.findViewById(R.id.publish_topsy_turvy);
			viewHolder.publish_publisher = (TextView) convertView
					.findViewById(R.id.publish_publisher);
			convertView.setTag(viewHolder);
			// 设置颜色
			if (position % 2 == 0) {
				convertView
						.setBackgroundColor(color.background_floating_material_dark);
			}
		} else {// convertView 以缓冲
			viewHolder = (ViewHolder) convertView.getTag();
		}

		initValues(viewHolder, position);// 设置值
		return convertView;
	}

	/**
	 * 向控件中设置值
	 * 
	 * @param viewHolder
	 *            保存所有空进的Holder
	 * @param position
	 *            ListView的id
	 */
	private void initValues(ViewHolder viewHolder, int position) {
		viewHolder.publish_publisher.setText(vector.get(position)
				.getPublisherName());
		viewHolder.publish_address.setText(vector.get(position).getLocation());
		viewHolder.publish_gold.setText(vector.get(position).getPrice() + "");
		viewHolder.publish_time.setText(vector.get(position).getEndTime());
		viewHolder.publish_topsy_turvy.setText(vector.get(position)
				.getTaskDescription());

	}

	static class ViewHolder {
		private TextView publish_publisher, publish_type, publish_time,
				publish_address, publish_gold, publish_topsy_turvy;

	}

	public static Vector<Task> getVector() {
		return vector;
	}

	public static void setVector(Vector<Task> vector) {
		Task_Accept_UnComplete_Adapter.vector = vector;
	}

	/**
	 * 添加任务实体到任务队列当中
	 * 
	 * @param task
	 *            任务实体
	 */
	public static void addVector(Task task) {
		vector.add(task);
	}

	/**
	 * 添加任务到任务队列当中
	 * 
	 * @param task
	 *            任务实体
	 * @param i
	 *            指定的位置 若制定的位置超过vector的大小 则添加到vector的末尾
	 */
	public static void addVector(Task task, int i) {
		if (i < vector.size()) {
			vector.add(i, task);
		} else {
			vector.add(task);
		}
	}

	/**
	 * 把相应分任务从队列当中移除
	 * 
	 * @param task
	 *            所要移除的任务实体
	 */
	public static void removeVector(Task task) {
		vector.remove(task);
	}

	/**
	 * 移除相应的任务实体
	 * 
	 * @param i
	 *            所要移除的任务实体的位置好
	 */
	public static void removeVector(int i) {
		if (i <= vector.size()) {
			vector.remove(i);
		}
	}
	/**
	 * 移除全部的数据
	 */
	public static void removeAll() {
		vector.removeAllElements();
	}
}
