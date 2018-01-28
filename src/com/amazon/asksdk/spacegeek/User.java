package com.amazon.asksdk.spacegeek;

import java.util.ArrayList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.amazonaws.services.s3.waiters.HeadBucketFunction;

public class User {

	private String id; 
	private String name; 
	private String nameAsk; 
	private boolean formalSpeech; 
	private ArrayList<String> excludedBodyparts; 
	
	private boolean idSet; 
	private boolean nameSet; 
	private boolean speechStyleSet; 
	private boolean excludedBodypartsSet;
	private boolean setupComplete; 
	
	public User (String id, String name, boolean formalSpeech) {
		this.id = id;
		this.name = name + " ";
		this.nameAsk = name + "? ";
		this.formalSpeech = formalSpeech;
	}
	
	public void printUser() {
		Logger log = LoggerFactory.getLogger(SpaceGeekSpeechlet.class);
		log.info("user id: " + id);
		log.info("user name: " + name);
		log.info("user formalSpeech: " + formalSpeech);
		log.info("user setup complete: " + setupComplete);
	
	}
	
	/////////////////////////////////////////////////////////////////////////////////////////
	
	public void setNameSet(boolean nameSet) {
		this.nameSet = nameSet; 
	}
	
	public void setSpeechStyleSet(boolean speechStyleSet) {
		this.speechStyleSet = speechStyleSet; 
	}
	
	public void setSetupComplete(boolean setupComplete) {
		this.setupComplete = setupComplete;
	}
	
	public String getName() {
		return name; 
	}
	
	public boolean preferesFormalSpeech() {
		return formalSpeech; 
	}
	
	public boolean isSetupComplete() {
		return setupComplete;
	}
}

	