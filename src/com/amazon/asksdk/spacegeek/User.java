package com.amazon.asksdk.spacegeek;

import java.util.ArrayList;

public class User {

	private String id; 
	private String name; 
	private String nameAsk; 
	private boolean formalSpeech; 
	private boolean setupComplete;
	private boolean introductionHeard; 
	private ArrayList<String> excludedBodyparts; 
	
	public User(String id, String name, boolean formalSpeech, boolean setupComplete, boolean introductionHeard, ArrayList<String> excludedBodyparts) {
		this.id = id;
		this.name = name + "";
		this.nameAsk = name + "? ";
		this.formalSpeech = formalSpeech;
		this.setupComplete = setupComplete;
		this.introductionHeard = introductionHeard;
		this.excludedBodyparts = excludedBodyparts;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name + "";
	}
	
	public String getNameAsk() {
		return nameAsk; 
	}
	
	public void setNameAsk(String nameAsk) {
		this.nameAsk = nameAsk + "? "; 
	}

	public boolean isFormalSpeech() {
		return formalSpeech;
	}

	public void setFormalSpeech(boolean formalSpeech) {
		this.formalSpeech = formalSpeech;
	}

	public boolean isSetupComplete() {
		return setupComplete;
	}

	public void setSetupComplete(boolean setupComplete) {
		this.setupComplete = setupComplete;
	}

	public boolean isIntroductionHeard() {
		return introductionHeard;
	}

	public void setIntroductionHeard(boolean introductionHeard) {
		this.introductionHeard = introductionHeard;
	}

	public ArrayList<String> getExcludedBodyparts() {
		return excludedBodyparts;
	}

	public void setExcludedBodyparts(ArrayList<String> excludedBodyparts) {
		this.excludedBodyparts = excludedBodyparts;
	}
}
