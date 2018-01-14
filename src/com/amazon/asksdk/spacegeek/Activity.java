package com.amazon.asksdk.spacegeek;

public class Activity {

	private String type;
	private String location; 
	private String exertion; 
	private String bodypart;
	
	private boolean typeSet; 
	private boolean locationSet;
	private boolean exertionSet;
	private boolean bodypartSet;
	
	public Activity() {
		
	}
	
	
	
	
	// Set status
	public void setType(String type) {
		this.type = type;
		
		if(type != null) {
			typeSet = true; 
		} else {
			typeSet = false; 
		}
	}

	public void setLocation(String location) {
		this.location = location;
		
		if(location != null) {
			locationSet = true; 
		} else {
			locationSet = false; 
		}
	}

	public void setExertion(String exertion) {
		this.exertion = exertion;
		
		if(exertion != null) {
			exertionSet = true; 
		} else {
			exertionSet = false; 
		}
	}

	public void setBodypart(String bodypart) {
		this.bodypart = bodypart;
		
		if(bodypart != null) {
			bodypartSet = true; 
		} else {
			bodypartSet = false; 
		}
	}
	
	// Get status values
	public String getType() {
		return type;
	}
	
	public String getLocation() {
		return location;
	}
	
	public String getExertion() {
		return exertion;
	}

	public String getBodypart() {
		return bodypart;
	}
	
	// Get status methods
	public boolean isTypeSet() {
		return typeSet;
	}

	public boolean isLocationSet() {
		return locationSet;
	}

	public boolean isExertionSet() {
		return exertionSet;
	}

	public boolean isBodypartSet() {
		return bodypartSet;
	}

	


	
	

}
