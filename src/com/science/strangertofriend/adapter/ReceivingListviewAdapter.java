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
 * CompletedListview 的自定义的适配器
 * listView 的布局根目录分已完成接收的任务 与未完成的任务
 * ListView的所有数据均以ArrayList队列保存
 * 二级目录以嵌套ArrayListView数据存储
 * 加载布局的数据
 * Created by lilin on 2015/11/3.11:27
 * Blog: www.gaosililin.iteye.com
 * e_mail:gaosi0812@gmail.com
 */
public class ReceivingListviewAdapter extends BaseExpandableListAdapter {
    //加载布局文件的参数
    private LayoutInflater inflater = null;

    //根目录队列
    private ArrayList<String> group = null;
    //二级目录队列
    public static ArrayList<ArrayList<Task>> child = null;
    //二级目录的对象的队列  已完成的队列
    private ArrayList<Task> completedList = null;
    //二级目录的对象的队列  未完成的队列
    private ArrayList<Task> uncompletedList = null;

    /**
     * 构造方法
     *
     * @param inflater 加载布的参数
     */
    public ReceivingListviewAdapter(LayoutInflater inflater) {
        this.inflater = inflater;
        initValue();
    }

    /**
     * 初始化数据
     */
    private void initValue() {
        //初始化数据
        group = new ArrayList<String>();
        child = new ArrayList<ArrayList<Task>>();
        uncompletedList = new ArrayList<Task>();
        completedList = new ArrayList<Task>();
        //加载数据
        group.add("未完成的任务");
        group.add("已完成的任务");

        child.add(uncompletedList);
        child.add(completedList);
    }

    public ReceivingListviewAdapter() {
    }

    /**
     * 获取根目录的数量
     *
     * @return
     */
    @Override
    public int getGroupCount() {
        return group.size();
    }

    /**
     * 获取每个根目录下的子目录的个数
     *
     * @param groupPosition
     * @return
     */
    @Override
    public int getChildrenCount(int groupPosition) {
        return child.get(groupPosition).size();
    }

    /**
     * 获取根目录的对象
     *
     * @param groupPosition
     * @return 返回根目录的对象
     */
    @Override
    public Object getGroup(int groupPosition) {
        return group.get(groupPosition);
    }

    /**
     * 获取每个根目录下的子目录的对象
     *
     * @param groupPosition 根目录的参数ID
     * @param childPosition 子目录的参数ID
     * @return 返回子目录的对象
     */
    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return child.get(groupPosition).get(childPosition);
    }

    /**
     * 获取根目录的ID
     *
     * @param groupPosition
     * @return
     */
    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    /***
     * 获取子目录的ID
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
     * 加载根目录的视图XML布局文件 获取根目录的视图
     *
     * @param groupPosition
     * @param isExpanded
     * @param convertView
     * @param parent
     * @return 根目录的视图对象
     */
    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        View view = inflater.inflate(R.layout.expandable_listview_group_completed_tasks, null, false);
        TextView textView = (TextView) view.findViewById(R.id.expandable_group_textView);
        textView.setText(group.get(groupPosition));
        return view;
    }

    /**
     * 加载子目录的视图布局文件 获取子目录的子目录视图
     *
     * @param groupPosition
     * @param childPosition
     * @param isLastChild
     * @param convertView
     * @param parent
     * @return 子目录的视图布局对象
     */
    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        View view = inflater.inflate(R.layout.expandable_listview_child_completed_tasks, null, false);
        if (childPosition % 2 == 0) {//隔条设置不一样的颜色
            view.setBackgroundColor(Color.BLUE);
        }
        initValues(view, groupPosition, childPosition);//加载布局的组件并设值
        //给view设置数据标志
        Intent intent = new Intent();
        intent.putExtra("groupPosition", groupPosition);
        intent.putExtra("childPosition", childPosition);
        view.setTag(intent);
        return view;
    }

    /**
     * 给布局文件加载数据
     * 加载的组件分别是：发布人，类型，时间，地点，金币，状态
     *
     * @param view          被加载的组件view
     * @param groupPosition 根目录下标
     * @param childPosition 子目录下标
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
        if (groupPosition == 0) {//已完成的
            completed_topsy_turvy.setText("已完成");
        } else {//未完成的
            completed_topsy_turvy.setText("未完成");
            completed_topsy_turvy.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.e("info", "点击未完成的任务了");
                }
            });
        }
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }

    /**
     * 未完成的队列添加数据
     *
     * @param Task 添加的对象
     */
    public void addUncompletedList(Task Task) {
        uncompletedList.add(Task);
    }

    /**
     * 在未完成的对列当中删除对象
     *
     * @param Task 需要删除的对象
     */
    public void deleteUncompletedList(Task Task) {
        uncompletedList.add(Task);
    }

    /**
     * 在完成的对象当中添加对象
     *
     * @param Task 添加进来的对象
     */
    public void addCompletedList(Task Task) {
        uncompletedList.add(Task);
    }

    /**
     * 在完成的队列当中删除数据
     *
     * @param Task 需要删除的对象
     */
    public void deleteCompletedList(Task Task) {
        uncompletedList.add(Task);
    }
}

