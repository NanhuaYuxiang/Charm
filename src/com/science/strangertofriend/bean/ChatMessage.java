package com.science.strangertofriend.bean;

import com.avos.avoscloud.im.v2.AVIMMessage;
import com.science.strangertofriend.R;

import android.graphics.Bitmap;

public class ChatMessage extends AVIMMessage{

	public static final int MESSAGE_FROM=0;
	public static final int MESSAGE_TO=1;
	public static final int MESSAGE_DATE=2;
	private int type;
	private String content;
	private Bitmap currentClientBitmap;
	private Bitmap otherClientBitmap;
	
	public ChatMessage(int type,String content){
		this.type=type;
		this.content=content;
	}
	
	public ChatMessage(){
		
	}
	
	public void setCurrentClientBitmap(Bitmap bitmap){
		this.currentClientBitmap=bitmap;
	}
	
	public void setOtherClientBitmap(Bitmap bitmap){
		this.otherClientBitmap=bitmap;
	}
	
	public int getType(){
		return type;
	}
	
	public String getContent(){
		return content;
	}
	
	public Bitmap getBitmap(int messageSendOrReceive){
		if(messageSendOrReceive==MESSAGE_TO){
			return currentClientBitmap;
		}else{
			return otherClientBitmap;
		}
	}
}
