package com.science.strangertofriend;

import java.util.Locale;

import com.science.strangertofriend.adapter.PublishListviewAdapter;
import com.science.strangertofriend.adapter.ReceivingListviewAdapter;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v13.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ExpandableListView;

/**
 * 个人任务详情列表 分为接收任务与发布任务 在这两情况下再分完成与未完成
 * 
 * @author lilin
 * @date 2015年11月5日 .下午9:49:54
 * @blog www.gaosililn.iteye.com 
 * @email gaosi0812@gamil.com
 * @school usc
 *
 */
public class IndividualTaskActivity extends Activity implements
		ActionBar.TabListener {
	/**
	 * 联系 
	 */
	private static final int CONTAST = 1;// 联系
	/**
	 * 删除
	 */
	private static final int DELETE = 2;// 删除
	SectionsPagerAdapter mSectionsPagerAdapter;

	ViewPager mViewPager;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_individual_task);

		final ActionBar actionBar = getActionBar();
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
		mSectionsPagerAdapter = new SectionsPagerAdapter(getFragmentManager());

		mViewPager = (ViewPager) findViewById(R.id.pager);
		mViewPager.setAdapter(mSectionsPagerAdapter);

		mViewPager
				.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
					@Override
					public void onPageSelected(int position) {
						actionBar.setSelectedNavigationItem(position);
					}
				});

		for (int i = 0; i < mSectionsPagerAdapter.getCount(); i++) {
			actionBar.addTab(actionBar.newTab()
					.setText(mSectionsPagerAdapter.getPageTitle(i))
					.setTabListener(this));
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.individual_task, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onTabSelected(ActionBar.Tab tab,
			FragmentTransaction fragmentTransaction) {
		mViewPager.setCurrentItem(tab.getPosition());
	}

	@Override
	public void onTabUnselected(ActionBar.Tab tab,
			FragmentTransaction fragmentTransaction) {
	}

	@Override
	public void onTabReselected(ActionBar.Tab tab,
			FragmentTransaction fragmentTransaction) {
	}

	public class SectionsPagerAdapter extends FragmentPagerAdapter {

		public SectionsPagerAdapter(FragmentManager fm) {
			super(fm);
		}

		@Override
		public Fragment getItem(int position) {
			return PlaceholderFragment.newInstance(position + 1);
		}

		/**
		 * 返回ACtionBar的tab数量 2
		 */
		@Override
		public int getCount() {
			return 2;
		}

		/**
		 * 返回ActionBar的tab的标题
		 */
		@Override
		public CharSequence getPageTitle(int position) {
			Locale l = Locale.getDefault();
			switch (position) {
			case 0:
				return "  ";
			case 1:
				return "发布任务";
			}
			return null;
		}
	}

	/**
	 * Framgment 自定义类 分类加载Framgment的布局文件
	 * 
	 * @author lilin
	 * @date 2015年11月5日 .下午10:23:57
	 * @blog www.gaosililn.iteye.com
	 * @email gaosi0812@gamil.com
	 * @school usc
	 *
	 */
	public static class PlaceholderFragment extends Fragment {
		private static final String ARG_SECTION_NUMBER = "section_number";

		public static PlaceholderFragment newInstance(int sectionNumber) {
			PlaceholderFragment fragment = new PlaceholderFragment();
			Bundle args = new Bundle();
			args.putInt(ARG_SECTION_NUMBER, sectionNumber);
			fragment.setArguments(args);
			return fragment;
		}

		public PlaceholderFragment() {
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			int anInt = getArguments().getInt(ARG_SECTION_NUMBER);

			View rootView = null;
			Log.e("info", "anInt:" + anInt);
			switch (anInt) {
			case 1:// 加载接收任务的fragment的XML布局文件
				rootView = inflater.inflate(R.layout.fragment_receiving_task,
						container, false);
				ExpandableListView completedListview = (ExpandableListView) rootView
						.findViewById(R.id.receiving_task_expandadle_listview);
				completedListview.setAdapter(new ReceivingListviewAdapter(
						inflater));
				// completedListview.setOnItemLongClickListener(new
				// ListItemLongClickListener());
				completedListviewSetListner(completedListview);
				break;
			case 2:// 加载发布任务的XML布局文件
				rootView = inflater.inflate(R.layout.fragment_publishing_tasks,
						container, false);
				ExpandableListView publishListview = (ExpandableListView) rootView
						.findViewById(R.id.publish_task_expandadle_listview);
				publishListview
						.setAdapter(new PublishListviewAdapter(inflater));
				// publishListview.setOnItemLongClickListener(new
				// ListItemLongClickListener());
				publishListviewSetListener(publishListview);
				break;
			}
			return rootView;
		}

		/**
		 * 发布任务ListView设置按钮监听
		 *
		 * @param publishListview
		 *            遭到监听的对象
		 */
		private void publishListviewSetListener(
				final ExpandableListView publishListview) {

			publishListview
					.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
						@Override
						public boolean onItemLongClick(AdapterView<?> parent,
								final View view, final int position,
								final long id) {
							AlertDialog.Builder builder = new AlertDialog.Builder(
									getActivity());
							builder.setIcon(R.drawable.ic_mood_black);
							builder.setTitle("请选择相应的操作");
							builder.setMessage("");
							builder.setPositiveButton("联系",
									new DialogInterface.OnClickListener() {
										@Override
										public void onClick(
												DialogInterface dialog,
												int which) {
											Intent intent = (Intent) view
													.getTag();
											int groupPosition = intent
													.getIntExtra(
															"groupPosition", -1);
											int childPosition = intent
													.getIntExtra(
															"childPosition", -1);
											Log.e("info", "groupPosition:"
													+ groupPosition
													+ ";childPosition"
													+ childPosition);

										}
									});
							builder.setNegativeButton("删除",
									new DialogInterface.OnClickListener() {
										@Override
										public void onClick(
												DialogInterface dialog,
												int which) {
											Intent intent = (Intent) view
													.getTag();
											int groupPosition = intent
													.getIntExtra(
															"groupPosition", -1);
											int childPosition = intent
													.getIntExtra(
															"childPosition", -1);
											// 删除相应的数据
											PublishListviewAdapter.child.get(
													groupPosition).remove(
													childPosition);
											// 刷新listView
											publishListview
													.refreshDrawableState();
										}
									});
							AlertDialog alertDialog = builder.create();// 创建对话框
							// 显示对话框
							alertDialog.show();
							return true;
						}
					});
		}

		/**
		 * 接收任务ListView设置长按监听
		 *
		 * @param completedListview
		 */
		private void completedListviewSetListner(
				final ExpandableListView completedListview) {
			completedListview
					.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
						@Override
						public boolean onItemLongClick(AdapterView<?> parent,
								final View view, final int position,
								final long id) {
							// 创建并设置dialog
							AlertDialog.Builder builder = new AlertDialog.Builder(
									getActivity());
							builder.setIcon(R.drawable.ic_mood_black);
							builder.setTitle("选择相应的操作");
							builder.setMessage("");
							// 积极的选项，用于设置联系发布人的操作
							builder.setPositiveButton("联系",
									new DialogInterface.OnClickListener() {
										@Override
										public void onClick(
												DialogInterface dialog,
												int which) {
											Intent intent = (Intent) view
													.getTag();
											int groupPosition = intent
													.getIntExtra(
															"groupPosition", -1);
											int childPosition = intent
													.getIntExtra(
															"childPosition", -1);
											Log.e("info", "groupPosition:"
													+ groupPosition
													+ ";childPosition"
													+ childPosition);

										}
									});
							// 消极的选项，用于删除该子目录的操作
							builder.setNegativeButton("删除",
									new DialogInterface.OnClickListener() {
										@Override
										public void onClick(
												DialogInterface dialog,
												int which) {
											Intent intent = (Intent) view
													.getTag();
											int groupPosition = intent
													.getIntExtra(
															"groupPosition", -1);
											int childPosition = intent
													.getIntExtra(
															"childPosition", -1);
											// 删除数据
											ReceivingListviewAdapter.child.get(
													groupPosition).remove(
													childPosition);
											// 涮新列表
											completedListview
													.refreshDrawableState();
										}
									});

							AlertDialog alertDialog = builder.create();// 创建对话框
							// 显示对话框
							alertDialog.show();
							return true;
						}
					});
		}
	}

}
