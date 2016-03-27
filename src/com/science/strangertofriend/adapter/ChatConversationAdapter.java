package com.science.strangertofriend.adapter;

import java.util.List;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.im.v2.AVIMConversation;
import com.avos.avoscloud.im.v2.AVIMMessage;
import com.avos.avoscloud.im.v2.callback.AVIMMessagesQueryCallback;
import com.avos.avoscloud.im.v2.messages.AVIMTextMessage;
import com.science.strangertofriend.R;
import com.science.strangertofriend.bean.ChatMessage;
import com.science.strangertofriend.bean.OneConversationData;

import de.hdodenhof.circleimageview.CircleImageView;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class ChatConversationAdapter extends BaseAdapter{

	private View view;
	private Context context ;
	private List<OneConversationData> convsDataList;
	private CircleImageView convsImg;
	private TextView  convsNameTv,convsLastMessage;
	
	public ChatConversationAdapter(Context context,List<OneConversationData> convsDataList){
		this.context = context;
		this.convsDataList = convsDataList;
	}
	public void reFresh(List<OneConversationData> convsDataList){
		this.convsDataList = convsDataList;
		notifyDataSetChanged();

	}
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return convsDataList.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return convsDataList.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}
	

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		view = LayoutInflater.from(context).inflate(R.layout.conversation_adapter, null);
		convsImg = (CircleImageView) view.findViewById(R.id.convsImg);
		if((convsDataList.get(position).getconvsClientBitmap())==null){
			convsImg.setImageResource(R.drawable.app_logo);
		}else{
			convsImg.setImageBitmap(convsDataList.get(position).getconvsClientBitmap());
		}
		convsNameTv = (TextView) view.findViewById(R.id.convsNameTv);
		convsLastMessage = (TextView) view.findViewById(R.id.convsLastMessage);
		convsNameTv.setText(convsDataList.get(position).getConvsClientName());
		convsLastMessage.setText(convsDataList.get(position).getLastMessage());
		return view;
	}

}
