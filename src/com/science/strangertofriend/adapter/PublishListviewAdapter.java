package com.science.strangertofriend.adapter;



import java.util.ArrayList;

import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

import com.science.strangertofriend.R;
import com.science.strangertofriend.bean.Task;
//import com.usc.geowind.lilin.tabsfragments.R;

/**
 * PublishListview���������������
 * ListView�ĸ�Ŀ¼��Ϊ����ɵķ��������� �뻹δ��ɵ�����
 * ListView �����ݾ���ArrayList ����
 * ����Ŀ¼��Ƕ�׵�ArrayList����
 * Created by lilin on 2015/11/3.11:31
 * Blog: www.gaosililin.iteye.com
 * e_mail:gaosi0812@gmail.com
 */
public class PublishListviewAdapter extends BaseExpandableListAdapter {
    private LayoutInflater inflater = null;
    //��Ŀ¼����
    private ArrayList<String> group = null;
    //����Ŀ¼����
    public static ArrayList<ArrayList<Task>> child = null;
    //����Ŀ¼�Ķ���Ķ���  ����ɵĶ���
    private ArrayList<Task> publish = null;
    //����Ŀ¼�Ķ���Ķ���  δ��ɵĶ���
    private ArrayList<Task> unpublish = null;

    public PublishListviewAdapter(LayoutInflater inflater) {
        super();
        initValue();
        this.inflater = inflater;
    }

    /**
     * ��ʼ������
     */
    private void initValue() {
        //��ʼ������
        group = new ArrayList<String>();
        child = new ArrayList<ArrayList<Task>>();
        unpublish = new ArrayList<Task>();
        publish = new ArrayList<Task>();
        //��������
        group.add("δ��ɵ�����");
        group.add("����ɵ�����");

        child.add(unpublish);
        child.add(publish);

    }

    public PublishListviewAdapter() {

    }

    /**
     * ��ȡ��Ŀ¼�ĸ���
     *
     * @return
     */
    @Override
    public int getGroupCount() {
        return group.size();
    }

    /**
     * ��ȡÿ����Ŀ¼�µ���Ŀ¼�ĸ���
     *
     * @param groupPosition
     * @return
     */
    @Override
    public int getChildrenCount(int groupPosition) {
        return child.get(groupPosition).size();
    }

    /**
     * ��ȡ��Ŀ¼����
     *
     * @param groupPosition
     * @return
     */
    @Override
    public Object getGroup(int groupPosition) {
        return group.get(groupPosition);
    }

    /**
     * ��ȡ����Ŀ¼�Ķ���
     *
     * @param groupPosition
     * @param childPosition
     * @return
     */
    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return child.get(groupPosition).get(childPosition);
    }

    /**
     * ��ȡ��Ŀ¼��ID
     *
     * @param groupPosition
     * @return
     */
    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    /**
     * ��ȡ����Ŀ¼��ID
     *
     * @param groupPosition
     * @param childPosition
     * @return
     */
    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    /**
     * ���ظ�Ŀ¼��XML�����ļ� ���ظ�Ŀ¼����ͼ
     *
     * @param groupPosition
     * @param isExpanded
     * @param convertView
     * @param parent
     * @return ��Ŀ¼����ͼ
     */
    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        View view = inflater.inflate(R.layout.expandable_listview_group_publish_tasks, null, false);
        TextView textView = (TextView) view.findViewById(R.id.publish_task_group_textView);
        textView.setText(group.get(groupPosition));
        return view;
    }

    /**
     * ������Ŀ¼����ͼ�����ļ�XML ������Ŀ¼����ͼ
     *
     * @param groupPosition
     * @param childPosition
     * @param isLastChild
     * @param convertView
     * @param parent
     * @return ��Ŀ¼����ͼ
     */
    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        //������Ŀ¼����������ļ�
        View view = inflater.inflate(R.layout.expandable_listview_child_publish_tasks, null, false);
        if (childPosition % 2 == 0) {//�������ò�һ������ɫ
            view.setBackgroundColor(Color.BLUE);
        }
        initValues(view, groupPosition, childPosition);//������Ŀ¼���������ֵ
        //��view�������ݱ�־
        Intent intent = new Intent();
        intent.putExtra("groupPosition", groupPosition);
        intent.putExtra("childPosition", childPosition);
        view.setTag(intent);
        return view;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }

    /**
     * ����������ɵĶ������Ԫ��
     *
     * @param Task ��ӵĶ���
     */
    public void addPublish(Task Task) {
        publish.add(Task);
    }

    /**
     * ��������Ķ��е����Ƴ�Ԫ��
     *
     * @param Task ��Ҫ�Ƴ��Ķ���
     */
    public void deletePublish(Task Task) {
        publish.remove(Task);
    }

    /**
     * ����������е������Ԫ��
     *
     * @param Task ��ӵĶ���
     */
    public void addUnpublish(Task Task) {
        unpublish.add(Task);
    }

    /**
     * ��������Ķ��е��У��Ƴ�Ԫ��
     *
     * @param Task ��Ҫ�Ƴ��Ķ���
     */
    public void deleteUnpublish(Task Task) {
        unpublish.remove(Task);
    }

    /**
     * �����ݿ��ѯ�������ݼ��ص�XML�ļ���
     * ���ص�����ֱ��ǣ��������ͣ�ʱ�䣬�ص㣬��ң�״̬
     *
     * @param view          �����ص���� View
     * @param groupPosition ��Ŀ¼�±�
     * @param childPosition ��Ŀ¼�±�
     */
    private void initValues(View view, int groupPosition, int childPosition) {
        //�������
        TextView publish_type = (TextView) view.findViewById(R.id.publish_type);
        TextView publish_time = (TextView) view.findViewById(R.id.publish_time);
        TextView publish_address = (TextView) view.findViewById(R.id.publish_address);
        TextView publish_gold = (TextView) view.findViewById(R.id.publish_gold);
        TextView publish_topsy_turvy = (TextView) view.findViewById(R.id.publish_topsy_turvy);
        //����Ӧ�������ֵ
        publish_type.setText(child.get(groupPosition).get(childPosition).getTheme());
        publish_time.setText(child.get(groupPosition).get(childPosition).getEndTime());
        publish_address.setText(child.get(groupPosition).get(childPosition).getLocation());
        publish_gold.setText(child.get(groupPosition).get(childPosition).getPrice() + "");
        if (groupPosition == 0) {
            publish_topsy_turvy.setText("�����");
        } else {
            publish_topsy_turvy.setText("δ���");
        }
    }
}

