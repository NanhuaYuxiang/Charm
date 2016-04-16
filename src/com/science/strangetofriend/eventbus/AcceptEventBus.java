package com.science.strangetofriend.eventbus;

import com.avos.avoscloud.im.v2.AVIMTypedMessage;

public class AcceptEventBus {
	
	public AVIMTypedMessage message;
	
	public AcceptEventBus(AVIMTypedMessage message){
		this.message = message;
	}
	
	public AVIMTypedMessage getMessage(){
		return message;
	}
	

}
