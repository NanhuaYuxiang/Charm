package com.science.strangertofriend.adapter;

import java.util.List;
import java.util.Map;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.science.strangertofriend.R;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * @description 消息列表适配器
 * 
 * @author 幸运Science 陈土燊
 * @school University of South China
 * @email chentushen.science@gmail.com,274240671@qq.com
 * @2015-5-14
 * 
 */

public class MessageListAdapter extends BaseAdapter {

	private List<Map<String, Object>> mMessageList;
	private Context mContext;
	private LayoutInflater mInflater;
	private String mSendUsernameString;

	public MessageListAdapter(Context context,
			List<Map<String, Object>> mUsernameList) {
		mInflater = LayoutInflater.from(context);
		mMessageList = mUsernameList;
		mContext = context;
	}

	@SuppressLint({ "InflateParams", "SimpleDateFormat" })
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		if (convertView == null) {
			convertView = mInflater.inflate(R.layout.item_list_message, null);
			new ViewHolder(convertView);
		}
		ViewHolder viewHolder = (ViewHolder) convertView.getTag();

		// viewHolder.avater.setImageDrawable();
		viewHolder.friendUsername.setText((String) mMessageList.get(position)
				.get("friend"));// 发送请求的好友
		viewHolder.message.setText((String) mMessageList.get(position).get(
				"frienRequest"));// 信息“请求加为好友"
		viewHolder.time.setText((String) mMessageList.get(position).get(
				"requestTime"));// 获取消息的接收时间

		@SuppressWarnings("deprecation")
		DisplayImageOptions options = new DisplayImageOptions.Builder()
				.showStubImage(R.drawable.default_load)
				.showImageForEmptyUri(R.drawable.default_load)
				.showImageOnFail(R.drawable.default_load).cacheInMemory(true)
				.cacheOnDisk(true).bitmapConfig(Bitmap.Config.RGB_565).build();
		ImageLoader.getInstance().displayImage(
				(String) mMessageList.get(position).get("urlAvater"),
				viewHolder.avater, options);

		return convertView;
	}

	class ViewHolder {
		CircleImageView avater;
		TextView friendUsername;
		TextView message;
		TextView time;

		public ViewHolder(View view) {
			avater = (CircleImageView) view.findViewById(R.id.item_avater);
			friendUsername = (TextView) view.findViewById(R.id.username);
			message = (TextView) view.findViewById(R.id.message);
			time = (TextView) view.findViewById(R.id.time);
			view.setTag(this);
		}
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public int getCount() {
		return mMessageList.size();
	}

	@Override
	public Object getItem(int position) {
		return mMessageList.get(position);
	}

}
