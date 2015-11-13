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
 * �������������б� ��Ϊ���������뷢������ ������������ٷ������δ���
 * 
 * @author lilin
 * @date 2015��11��5�� .����9:49:54
 * @blog www.gaosililn.iteye.com 
 * @email gaosi0812@gamil.com
 * @school usc
 *
 */
public class IndividualTaskActivity extends Activity implements
		ActionBar.TabListener {
	/**
	 * ��ϵ 
	 */
	private static final int CONTAST = 1;// ��ϵ
	/**
	 * ɾ��
	 */
	private static final int DELETE = 2;// ɾ��
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
		 * ����ACtionBar��tab���� 2
		 */
		@Override
		public int getCount() {
			return 2;
		}

		/**
		 * ����ActionBar��tab�ı���
		 */
		@Override
		public CharSequence getPageTitle(int position) {
			Locale l = Locale.getDefault();
			switch (position) {
			case 0:
				return "  ";
			case 1:
				return "��������";
			}
			return null;
		}
	}

	/**
	 * Framgment �Զ����� �������Framgment�Ĳ����ļ�
	 * 
	 * @author lilin
	 * @date 2015��11��5�� .����10:23:57
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
			case 1:// ���ؽ��������fragment��XML�����ļ�
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
			case 2:// ���ط��������XML�����ļ�
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
		 * ��������ListView���ð�ť����
		 *
		 * @param publishListview
		 *            �⵽�����Ķ���
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
							builder.setTitle("��ѡ����Ӧ�Ĳ���");
							builder.setMessage("");
							builder.setPositiveButton("��ϵ",
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
							builder.setNegativeButton("ɾ��",
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
											// ɾ����Ӧ������
											PublishListviewAdapter.child.get(
													groupPosition).remove(
													childPosition);
											// ˢ��listView
											publishListview
													.refreshDrawableState();
										}
									});
							AlertDialog alertDialog = builder.create();// �����Ի���
							// ��ʾ�Ի���
							alertDialog.show();
							return true;
						}
					});
		}

		/**
		 * ��������ListView���ó�������
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
							// ����������dialog
							AlertDialog.Builder builder = new AlertDialog.Builder(
									getActivity());
							builder.setIcon(R.drawable.ic_mood_black);
							builder.setTitle("ѡ����Ӧ�Ĳ���");
							builder.setMessage("");
							// ������ѡ�����������ϵ�����˵Ĳ���
							builder.setPositiveButton("��ϵ",
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
							// ������ѡ�����ɾ������Ŀ¼�Ĳ���
							builder.setNegativeButton("ɾ��",
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
											// ɾ������
											ReceivingListviewAdapter.child.get(
													groupPosition).remove(
													childPosition);
											// �����б�
											completedListview
													.refreshDrawableState();
										}
									});

							AlertDialog alertDialog = builder.create();// �����Ի���
							// ��ʾ�Ի���
							alertDialog.show();
							return true;
						}
					});
		}
	}

}
