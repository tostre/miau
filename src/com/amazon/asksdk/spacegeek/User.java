package com.amazon.asksdk.spacegeek;

import java.util.ArrayList;

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
	private boolean introductionHeard; 
	private boolean setupComplete; 
	
	public User(String id, String name, boolean formalSpeech, boolean setupComplete, boolean introductionHeard, ArrayList<String> excludedBodyparts) {
		this.id = id;
		this.name = name + "";
		this.nameAsk = name + "? ";
		this.formalSpeech = formalSpeech;
		this.excludedBodyparts = excludedBodyparts;
		this.setupComplete = setupComplete;
		this.introductionHeard = introductionHeard;
		
	}
	
	public void setId(String id) {
		this.id = id; 
		
		if(id != null) {
			idSet = true; 
		} else {
			idSet = false; 
		}
	}
	
	public void setName(String name) {
		this.name = name + " "; 
		this.nameAsk = name + "? "; 
		
		if(name != null) {
			nameSet = true; 
		} else {
			nameSet = false; 
		}
	}
	
	public void setFormalSpeech(boolean formalSpeech) {
		this.formalSpeech = formalSpeech; 
		speechStyleSet = true; 
	}
	
	public void setSpeechStyleSet(boolean speechStyleSet) {
		this.speechStyleSet = speechStyleSet; 
	}
	
	public void setExcludedBodyparts(ArrayList<String> excludedBodyparts) {
		this.excludedBodyparts = excludedBodyparts;
	}
	
	public void setSetupComplete(boolean setupComplete) {
		this.setupComplete = setupComplete;
	}
	
	public void setIntroductionHeard(boolean introductionHeard) {
		this.introductionHeard = introductionHeard;
	}
	
	public String getId() {
		return id; 
	}
	
	public String getName() {
		return name; 
	}
	
	public String getNameAsk() {
		return nameAsk; 
	}
	
	public boolean isNameSet() {
		return nameSet;
	}
	
	public boolean isSpeechStyleSet() {
		return speechStyleSet;
	}
	
	public boolean preferesFormalSpeech() {
		return formalSpeech; 
	}
	
	public ArrayList<String> getExcludedBodyparts(){
		return excludedBodyparts;
	}
	
	public boolean isSetupComplete() {
		return setupComplete;
	}
	
	public boolean hasHeardIntroduction() {
		return introductionHeard;
	}
}

	