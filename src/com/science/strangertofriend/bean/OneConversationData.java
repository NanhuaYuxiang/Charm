package com.science.strangertofriend.bean;

import com.science.strangertofriend.R;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

public class OneConversationData {
	
	private String convsClientName="";
	private String lastMessage = "";
	private Bitmap convsClientBitmap;
	private Context con;
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
