package com.science.strangertofriend.bean;

import android.graphics.Bitmap;

public class OneConversationData {
	
	private String convsClientName="";
	private String lastMessage = "";
	private Bitmap convsClientBitmap;
	public OneConversationData(String convsClientName,String lastMessage){
		this.convsClientName = convsClientName;
		this.lastMessage = lastMessage;
	}
	
	public String getConvsClientName(){
		return convsClientName;
	}
	
	public String getLastMessage(){
		return lastMessage;
	}
	
	

}
