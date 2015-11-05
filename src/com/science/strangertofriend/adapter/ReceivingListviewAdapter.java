package com.science.strangertofriend.adapter;

import java.util.ArrayList;

import android.content.Intent;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

import com.science.strangertofriend.R;
import com.science.strangertofriend.bean.Task;

/**
 * CompletedListview ���Զ����������
 * listView �Ĳ��ָ�Ŀ¼������ɽ��յ����� ��δ��ɵ�����
 * ListView���������ݾ���ArrayList���б���
 * ����Ŀ¼��Ƕ��ArrayListView���ݴ洢
 * ���ز��ֵ�����
 * Created by lilin on 2015/11/3.11:27
 * Blog: www.gaosililin.iteye.com
 * e_mail:gaosi0812@gmail.com
 */
public class ReceivingListviewAdapter extends BaseExpandableListAdapter {
    //���ز����ļ��Ĳ���
    private LayoutInflater inflater = null;

    //��Ŀ¼����
    private ArrayList<String> group = null;
    //����Ŀ¼����
    public static ArrayList<ArrayList<Task>> child = null;
    //����Ŀ¼�Ķ���Ķ���  ����ɵĶ���
    private ArrayList<Task> completedList = null;
    //����Ŀ¼�Ķ���Ķ���  δ��ɵĶ���
    private ArrayList<Task> uncompletedList = null;

    /**
     * ���췽��
     *
     * @param inflater ���ز��Ĳ���
     */
    public ReceivingListviewAdapter(LayoutInflater inflater) {
        this.inflater = inflater;
        initValue();
    }

    /**
     * ��ʼ������
     */
    private void initValue() {
        //��ʼ������
        group = new ArrayList<String>();
        child = new ArrayList<ArrayList<Task>>();
        uncompletedList = new ArrayList<Task>();
        completedList = new ArrayList<Task>();
        //��������
        group.add("δ��ɵ�����");
        group.add("����ɵ�����");

        child.add(uncompletedList);
        child.add(completedList);
    }

    public ReceivingListviewAdapter() {
    }

    /**
     * ��ȡ��Ŀ¼������
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
     * ��ȡ��Ŀ¼�Ķ���
     *
     * @param groupPosition
     * @return ���ظ�Ŀ¼�Ķ���
     */
    @Override
    public Object getGroup(int groupPosition) {
        return group.get(groupPosition);
    }

    /**
     * ��ȡÿ����Ŀ¼�µ���Ŀ¼�Ķ���
     *
     * @param groupPosition ��Ŀ¼�Ĳ���ID
     * @param childPosition ��Ŀ¼�Ĳ���ID
     * @return ������Ŀ¼�Ķ���
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

    /***
     * ��ȡ��Ŀ¼��ID
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
     * ���ظ�Ŀ¼����ͼXML�����ļ� ��ȡ��Ŀ¼����ͼ
     *
     * @param groupPosition
     * @param isExpanded
     * @param convertView
     * @param parent
     * @return ��Ŀ¼����ͼ����
     */
    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        View view = inflater.inflate(R.layout.expandable_listview_group_completed_tasks, null, false);
        TextView textView = (TextView) view.findViewById(R.id.expandable_group_textView);
        textView.setText(group.get(groupPosition));
        return view;
    }

    /**
     * ������Ŀ¼����ͼ�����ļ� ��ȡ��Ŀ¼����Ŀ¼��ͼ
     *
     * @param groupPosition
     * @param childPosition
     * @param isLastChild
     * @param convertView
     * @param parent
     * @return ��Ŀ¼����ͼ���ֶ���
     */
    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        View view = inflater.inflate(R.layout.expandable_listview_child_completed_tasks, null, false);
        if (childPosition % 2 == 0) {//�������ò�һ������ɫ
            view.setBackgroundColor(Color.BLUE);
        }
        initValues(view, groupPosition, childPosition);//���ز��ֵ��������ֵ
        //��view�������ݱ�־
        Intent intent = new Intent();
        intent.putExtra("groupPosition", groupPosition);
        intent.putExtra("childPosition", childPosition);
        view.setTag(intent);
        return view;
    }

    /**
     * �������ļ���������
     * ���ص�����ֱ��ǣ������ˣ����ͣ�ʱ�䣬�ص㣬��ң�״̬
     *
     * @param view          �����ص����view
     * @param groupPosition ��Ŀ¼�±�
     * @param childPosition ��Ŀ¼�±�
     */
    private void initValues(final View view, int groupPosition, int childPosition) {
        TextView completed_publisher = (TextView) view.findViewById(R.id.completed_publisher);
        TextView completed_type = (TextView) view.findViewById(R.id.completed_type);
        TextView completed_time = (TextView) view.findViewById(R.id.completed_time);
        TextView completed_address = (TextView) view.findViewById(R.id.completed_address);
        TextView completed_gold = (TextView) view.findViewById(R.id.completed_gold);
        TextView completed_topsy_turvy = (TextView) view.findViewById(R.id.completed_topsy_turvy);
        completed_publisher.setText(child.get(groupPosition).get(childPosition).getPublisherName());
        completed_type.setText(child.get(groupPosition).get(childPosition).getTheme());
        completed_time.setText(child.get(groupPosition).get(childPosition).getEndTime());
        completed_address.setText(child.get(groupPosition).get(childPosition).getAcceptedName());
        completed_gold.setText(child.get(groupPosition).get(childPosition).getPrice() + "");
        if (groupPosition == 0) {//����ɵ�
            completed_topsy_turvy.setText("�����");
        } else {//δ��ɵ�
            completed_topsy_turvy.setText("δ���");
            completed_topsy_turvy.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.e("info", "���δ��ɵ�������");
                }
            });
        }
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }

    /**
     * δ��ɵĶ����������
     *
     * @param Task ��ӵĶ���
     */
    public void addUncompletedList(Task Task) {
        uncompletedList.add(Task);
    }

    /**
     * ��δ��ɵĶ��е���ɾ������
     *
     * @param Task ��Ҫɾ���Ķ���
     */
    public void deleteUncompletedList(Task Task) {
        uncompletedList.add(Task);
    }

    /**
     * ����ɵĶ�������Ӷ���
     *
     * @param Task ��ӽ����Ķ���
     */
    public void addCompletedList(Task Task) {
        uncompletedList.add(Task);
    }

    /**
     * ����ɵĶ��е���ɾ������
     *
     * @param Task ��Ҫɾ���Ķ���
     */
    public void deleteCompletedList(Task Task) {
        uncompletedList.add(Task);
    }
}

