package com.science.strangertofriend.ui;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.annotation.TargetApi;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.FindCallback;
import com.readystatesoftware.systembartint.SystemBarTintManager;
import com.science.strangertofriend.R;
import com.science.strangertofriend.widget.RevealLayout;

/**
 * @description
 * 
 * @author 幸运Science 陈土燊
 * @school University of South China
 * @email chentushen.science@gmail.com,274240671@qq.com
 * @2015-8-19
 * 
 */

public class RankingActivity extends BaseActivity {

	private RevealLayout mRevealLayout;
	private RelativeLayout mLayout;
	private ImageView mBackImg;
	private TextView mTitle;
	private ListView mListRanking;
	private RankingAdapter mRankingAdapter;
	public List<Map<String, Object>> mRankingList;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.ranking_layout);

		initView();
		initListener();
	}

	@Override
	@TargetApi(19)
	public void initSystemBar() {
		super.initSystemBar();
		// 创建状态栏的管理实例
		SystemBarTintManager tintManager = new SystemBarTintManager(this);
		// 激活状态栏设置
		tintManager.setStatusBarTintEnabled(true);
		// 激活导航栏设置
		tintManager.setNavigationBarTintEnabled(true);
		// 设置一个颜色给系统栏
		tintManager.setTintColor(Color.parseColor("#f698b2"));
	}

	private void initView() {

		mRevealLayout = (RevealLayout) findViewById(R.id.reveal_layout);
		mLayout = (RelativeLayout) findViewById(R.id.layout);
		mLayout.setBackgroundColor(Color.WHITE);

		mBackImg = (ImageView) findViewById(R.id.back_img);
		mTitle = (TextView) findViewById(R.id.title);
		mTitle.setText("签到排名");

		mListRanking = (ListView) findViewById(R.id.ranking_list);
		mRankingList = new ArrayList<Map<String, Object>>();
		getRankingList();

	}

	private List<Map<String, Object>> getRankingList() {
		AVQuery<AVObject> query = new AVQuery<AVObject>("Sign");
		query.orderByAscending("signTimes");// 按照时间降序
		query.findInBackground(new FindCallback<AVObject>() {

			@Override
			public void done(List<AVObject> list, AVException e) {
				if (list != null && list.size() != 0) {

					for (AVObject avo : list) {
						Map<String, Object> map = new HashMap<String, Object>();
						map.put("username", avo.getString("username"));
						map.put("signTimes", avo.getInt("signTimes"));
						mRankingList.add(0, map);
					}
					mRankingAdapter = new RankingAdapter(mRankingList);
					mListRanking.setAdapter(mRankingAdapter);
				} else {
					Toast.makeText(RankingActivity.this, "暂无排名",
							Toast.LENGTH_SHORT).show();
				}
			}
		});

		return mRankingList;

	}

	private void initListener() {

		mRevealLayout.setContentShown(false);
		mRevealLayout.getViewTreeObserver().addOnGlobalLayoutListener(
				new ViewTreeObserver.OnGlobalLayoutListener() {
					@SuppressWarnings("deprecation")
					@Override
					public void onGlobalLayout() {
						mRevealLayout.getViewTreeObserver()
								.removeGlobalOnLayoutListener(this);
						mRevealLayout.postDelayed(new Runnable() {
							@Override
							public void run() {
								mRevealLayout.show(2000);
							}
						}, 50);
					}
				});
		mRevealLayout.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {

			}
		});

		// 左上角退出当前activity
		mBackImg.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				RankingActivity.this.finish();
			}
		});
	}

	class RankingAdapter extends BaseAdapter {

		List<Map<String, Object>> list;

		public RankingAdapter(List<Map<String, Object>> list) {
			this.list = list;
		}

		@Override
		public int getCount() {
			return list.size();
		}

		@Override
		public Object getItem(int position) {
			return position;
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			if (convertView == null) {
				convertView = LayoutInflater.from(RankingActivity.this)
						.inflate(R.layout.item_ranking, null);
				new ViewHolder(convertView);
			}
			ViewHolder viewHolder = (ViewHolder) convertView.getTag();

			if (list.get(position).get("username") == AVUser.getCurrentUser()
					.getUsername()) {
				viewHolder.username.setTextColor(Color.RED);
				viewHolder.signTimes.setTextColor(Color.RED);
			}

			viewHolder.username.setText((String) list.get(position).get(
					"username"));//
			viewHolder.signTimes.setText(list.get(position).get("signTimes")
					+ "");//

			return convertView;
		}

	}

	class ViewHolder {
		TextView username;
		TextView signTimes;

		public ViewHolder(View view) {
			username = (TextView) view.findViewById(R.id.username);
			signTimes = (TextView) view.findViewById(R.id.sign_times);
			view.setTag(this);
		}
	}

}
