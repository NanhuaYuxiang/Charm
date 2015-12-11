package com.science.strangertofriend.adapter;

import java.util.List;

import com.science.strangertofriend.R;
import com.science.strangertofriend.bean.ChatMessage;

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
	public ChatAdapter(Context context,List<ChatMessage>message){
		super();
		this.context = context;
		this.message = message;
	}
	public ChatAdapter(){
		
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

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if(message.get(position).getType()==0){
			view = LayoutInflater.from(context).inflate(R.layout.message_list_left, null);
			ImageView receiveimage=(ImageView) view.findViewById(R.id.receiveClientImg);
//			receiveimage.setImageBitmap(message.get(0).getBitmap(0));
			TextView receiveText = (TextView) view.findViewById(R.id.receiveMessage);
			receiveText.setText(message.get(position).getContent());
			return view;
		}else if(message.get(position).getType()==1){
			view = LayoutInflater.from(context).inflate(R.layout.message_list_right, null);
			ImageView receiveimage=(ImageView) view.findViewById(R.id.sendClientImg);
//			receiveimage.setImageBitmap(message.get(position).getBitmap(1));
			TextView receiveText = (TextView) view.findViewById(R.id.sendMessage);
			receiveText.setText(message.get(position).getContent());
			return view;
		}
		
		return view;
	}

}
