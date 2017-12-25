package com.oseasy.initiate.common.utils.sms;

import java.io.Serializable;

public class SMSReturnState implements Serializable{

	private static final long serialVersionUID = 1L;
	
	private String flag;
	
	private String message;

	

	public String getFlag() {
		return flag;
	}



	public void setFlag(String flag) {
		this.flag = flag;
	}



	public String getMessage() {
		return message;
	}



	public void setMessage(String message) {
		this.message = message;
	}



	@Override
	public String toString() {
		return "SMSReturnState [flag=" + flag + ", message=" + message + "]";
	}
	
	
	
}
