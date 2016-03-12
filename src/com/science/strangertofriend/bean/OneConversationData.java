package com.science.strangertofriend.bean;

import android.graphics.Bitmap;

public class OneConversationData {
	
	private String convsClientName="";
	private String lastMessage = "";
	private Bitmap convsClientBitmap;
	public OneConversationData(String convsClientName,String lastMessage,Bitmap convsClientBitmap){
		this.convsClientName = convsClientName;
		this.lastMessage = lastMessage;
		this.convsClientBitmap = convsClientBitmap;
	}
	
	public OneConversationData(){
		
	}
	
	public void setConvsClientName(String convsClientName){
		this.convsClientName = convsClientName;
	}
	
	public void setLastMessage(String lastMessage){
		this.lastMessage=lastMessage;
	}
	
	public void setConvsClientBitmap(Bitmap convsClientBitmap){
		this.convsClientBitmap = convsClientBitmap;
	}
	
	public String getConvsClientName(){
		return convsClientName;
	}
	
	public String getLastMessage(){
		return lastMessage;
	}
	
	public Bitmap getconvsClientBitmap(){
		return convsClientBitmap;
	}
}
