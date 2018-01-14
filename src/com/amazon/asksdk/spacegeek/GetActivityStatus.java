package com.amazon.asksdk.spacegeek;

public class GetActivityStatus {

	private boolean locationSet; 
	private boolean exertionSet; 
	private boolean bodypartSet; 
	
	public GetActivityStatus() {
		locationSet = false;
		exertionSet = false;
		bodypartSet = false; 
	}
	
	public boolean isLocationSet() {
		return locationSet; 
	}
	
	public void setLocationSet(boolean locationSet) {
		this.locationSet = locationSet;
	}
	
	public boolean isExertionSet() {
		return exertionSet; 
	}
	
	public void setExertionSet(boolean exertionSet) {
		this.exertionSet = exertionSet;
	}
	
	public boolean isBodypartSet() {
		return bodypartSet; 
	}
	
	public void setBodypartSet(boolean bodypartSet) {
		this.bodypartSet = bodypartSet;
	}
}
