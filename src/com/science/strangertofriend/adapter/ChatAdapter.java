package com.science.strangertofriend.adapter;

import java.util.List;

import com.avos.avoscloud.im.v2.AVIMMessage;
import com.science.strangertofriend.R;
import com.science.strangertofriend.bean.ChatMessage;

import de.hdodenhof.circleimageview.CircleImageView;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class ChatAdapter extends BaseAdapter{

	private Context context;
	private List<ChatMessage> message;
	private View view;
	private ChatMessage chatMessage;
	private CircleImageView receiveimage;
	private CircleImageView sendimage;
	public ChatAdapter(Context context,List<ChatMessage>message){
		super();
		this.context = context;
		this.message = message;
	}
	
	public void reFresh(List<ChatMessage> message){
		this.message = message;
		notifyDataSetChanged();
	}
	public ChatAdapter(){
		
	}
	public void setChatMessage(ChatMessage chatMessage){
		this.chatMessage = chatMessage;
	}
	@Override
	public int getCount() {
		return message.size();
	}

	@Override
	public Object getItem(int position) {
		return message.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}
	
	public AVIMMessage getFirstMssage(){
		if(null != message&&message.size()>0){
			return message.get(0);
		}else{
			return null;
		}
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if(message.get(position).getType()==0){
			view = LayoutInflater.from(context).inflate(R.layout.message_list_left, null);
			receiveimage=(CircleImageView) view.findViewById(R.id.receiveClientImg);
			if(!(chatMessage.getBitmap(0).equals(null))){
				receiveimage.setImageBitmap(chatMessage.getBitmap(0));
			}
			TextView receiveText = (TextView) view.findViewById(R.id.receiveMessage);
			receiveText.setText(message.get(position).getContent());
			return view;
		}else if(message.get(position).getType()==1){
			view = LayoutInflater.from(context).inflate(R.layout.message_list_right, null);
			sendimage=(CircleImageView) view.findViewById(R.id.sendClientImg);
			if(!chatMessage.getBitmap(1).equals(null)){
				sendimage.setImageBitmap(chatMessage.getBitmap(1));
			}
			TextView receiveText = (TextView) view.findViewById(R.id.sendMessage);
			receiveText.setText(message.get(position).getContent());
			return view;
		}
		
		return view;
	}

}
