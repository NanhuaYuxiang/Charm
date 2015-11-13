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
 * ��������б��������
 * 
 * @author lilin
 * @date 2015��11��11�� .����2:57:24
 * @blog www.gaosililn.iteye.com
 * @email gaosi0812@gamil.com
 * @school usc
 *
 */
public class Task_Publish_UnComplete_Adapter extends BaseAdapter {
	public static Vector<Task> vector = null;
	private LayoutInflater layoutInflater = null;

	public Task_Publish_UnComplete_Adapter() {
	}

	public Task_Publish_UnComplete_Adapter(Activity activity) {
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
					R.layout.task_pubulisg_uncomplete_layout, null, false);

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
			viewHolder.publish_hunter = (TextView) convertView
					.findViewById(R.id.publish_hunter);
			convertView.setTag(viewHolder);
			// ������ɫ
			if (position % 2 == 0) {
				convertView
						.setBackgroundColor(color.background_floating_material_dark);
			}
		} else {// convertView �Ի���
			viewHolder = (ViewHolder) convertView.getTag();
		}

		initValues(viewHolder, position);// ����ֵ
		return convertView;
	}

	/**
	 * ��ؼ�������ֵ
	 * 
	 * @param viewHolder
	 *            �������пս���Holder
	 * @param position
	 *            ListView��id
	 */
	private void initValues(ViewHolder viewHolder, int position) {
		viewHolder.publish_address.setText(vector.get(position).getLocation());
		viewHolder.publish_gold.setText(vector.get(position).getPrice() + "");
		viewHolder.publish_time.setText(vector.get(position).getEndTime());
		viewHolder.publish_topsy_turvy.setText(vector.get(position)
				.getTaskDescription());
		viewHolder.publish_hunter.setText(vector.get(position)
				.getAcceptedName());

	}

	static class ViewHolder {
		private TextView publish_hunter, publish_type, publish_time,
				publish_address, publish_gold, publish_topsy_turvy;

	}

	public static Vector<Task> getVector() {
		return vector;
	}

	public static void setVector(Vector<Task> vector) {
		Task_Publish_UnComplete_Adapter.vector = vector;
	}

	/**
	 * �������ʵ�嵽������е���
	 * 
	 * @param task
	 *            ����ʵ��
	 */
	public static void addVector(Task task) {
		vector.add(task);
	}

	/**
	 * �������������е���
	 * 
	 * @param task
	 *            ����ʵ��
	 * @param i
	 *            ָ����λ�� ���ƶ���λ�ó���vector�Ĵ�С ����ӵ�vector��ĩβ
	 */
	public static void addVector(Task task, int i) {
		if (i < vector.size()) {
			vector.add(i, task);
		} else {
			vector.add(task);
		}
	}

	/**
	 * ����Ӧ������Ӷ��е����Ƴ�
	 * 
	 * @param task
	 *            ��Ҫ�Ƴ�������ʵ��
	 */
	public static void removeVector(Task task) {
		vector.remove(task);
	}

	/**
	 * �Ƴ���Ӧ������ʵ��
	 * 
	 * @param i
	 *            ��Ҫ�Ƴ�������ʵ���λ�ú�
	 */
	public static void removeVector(int i) {
		if (i <= vector.size()) {
			vector.remove(i);
		}
	}

	/**
	 * �Ƴ�ȫ��������
	 */
	public static void removeAll() {
		vector.removeAllElements();
	}
}
