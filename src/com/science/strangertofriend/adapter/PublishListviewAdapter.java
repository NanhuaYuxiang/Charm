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
 * PublishListview发布任务的适配器
 * ListView的根目录分为已完成的发布的任务 与还未完成的任务
 * ListView 的数据均以ArrayList 保存
 * 二级目录以嵌套的ArrayList保存
 * Created by lilin on 2015/11/3.11:31
 * Blog: www.gaosililin.iteye.com
 * e_mail:gaosi0812@gmail.com
 */
public class PublishListviewAdapter extends BaseExpandableListAdapter {
    private LayoutInflater inflater = null;
    //根目录队列
    private ArrayList<String> group = null;
    //二级目录队列
    public static ArrayList<ArrayList<Task>> child = null;
    //二级目录的对象的队列  已完成的队列
    private ArrayList<Task> publish = null;
    //二级目录的对象的队列  未完成的队列
    private ArrayList<Task> unpublish = null;

    public PublishListviewAdapter(LayoutInflater inflater) {
        super();
        initValue();
        this.inflater = inflater;
    }

    /**
     * 初始化数据
     */
    private void initValue() {
        //初始化数据
        group = new ArrayList<String>();
        child = new ArrayList<ArrayList<Task>>();
        unpublish = new ArrayList<Task>();
        publish = new ArrayList<Task>();
        //加载数据
        group.add("未完成的任务");
        group.add("已完成的任务");

        child.add(unpublish);
        child.add(publish);

    }

    public PublishListviewAdapter() {

    }

    /**
     * 获取根目录的个数
     *
     * @return
     */
    @Override
    public int getGroupCount() {
        return group.size();
    }

    /**
     * 获取每个人目录下的子目录的个数
     *
     * @param groupPosition
     * @return
     */
    @Override
    public int getChildrenCount(int groupPosition) {
        return child.get(groupPosition).size();
    }

    /**
     * 获取根目录对象
     *
     * @param groupPosition
     * @return
     */
    @Override
    public Object getGroup(int groupPosition) {
        return group.get(groupPosition);
    }

    /**
     * 获取二级目录的对象
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
     * 获取根目录的ID
     *
     * @param groupPosition
     * @return
     */
    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    /**
     * 获取二级目录的ID
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
     * 加载根目录的XML布局文件 返回根目录的视图
     *
     * @param groupPosition
     * @param isExpanded
     * @param convertView
     * @param parent
     * @return 根目录的视图
     */
    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        View view = inflater.inflate(R.layout.expandable_listview_group_publish_tasks, null, false);
        TextView textView = (TextView) view.findViewById(R.id.publish_task_group_textView);
        textView.setText(group.get(groupPosition));
        return view;
    }

    /**
     * 加载子目录的视图布局文件XML 返回子目录的视图
     *
     * @param groupPosition
     * @param childPosition
     * @param isLastChild
     * @param convertView
     * @param parent
     * @return 子目录的视图
     */
    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        //加载子目录的组件布局文件
        View view = inflater.inflate(R.layout.expandable_listview_child_publish_tasks, null, false);
        if (childPosition % 2 == 0) {//隔条设置不一样的颜色
            view.setBackgroundColor(Color.BLUE);
        }
        initValues(view, groupPosition, childPosition);//加载子目录的组件并设值
        //给view设置数据标志
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
     * 发布任务完成的队列添加元素
     *
     * @param Task 添加的对象
     */
    public void addPublish(Task Task) {
        publish.add(Task);
    }

    /**
     * 发布任务的队列当中移出元素
     *
     * @param Task 需要移出的对列
     */
    public void deletePublish(Task Task) {
        publish.remove(Task);
    }

    /**
     * 发布任务队列当中添加元素
     *
     * @param Task 添加的对象
     */
    public void addUnpublish(Task Task) {
        unpublish.add(Task);
    }

    /**
     * 发布任务的对列当中，移除元素
     *
     * @param Task 需要移出的对象
     */
    public void deleteUnpublish(Task Task) {
        unpublish.remove(Task);
    }

    /**
     * 将数据库查询到的数据加载到XML文件上
     * 加载的组件分别是：任务类型，时间，地点，金币，状态
     *
     * @param view          被加载的组件 View
     * @param groupPosition 根目录下标
     * @param childPosition 子目录下标
     */
    private void initValues(View view, int groupPosition, int childPosition) {
        //加载组件
        TextView publish_type = (TextView) view.findViewById(R.id.publish_type);
        TextView publish_time = (TextView) view.findViewById(R.id.publish_time);
        TextView publish_address = (TextView) view.findViewById(R.id.publish_address);
        TextView publish_gold = (TextView) view.findViewById(R.id.publish_gold);
        TextView publish_topsy_turvy = (TextView) view.findViewById(R.id.publish_topsy_turvy);
        //给相应的组件设值
        publish_type.setText(child.get(groupPosition).get(childPosition).getTheme());
        publish_time.setText(child.get(groupPosition).get(childPosition).getEndTime());
        publish_address.setText(child.get(groupPosition).get(childPosition).getLocation());
        publish_gold.setText(child.get(groupPosition).get(childPosition).getPrice() + "");
        if (groupPosition == 0) {
            publish_topsy_turvy.setText("已完成");
        } else {
            publish_topsy_turvy.setText("未完成");
        }
    }
}

