package com.amazon.asksdk.spacegeek;

import java.util.ArrayList;

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
	//TODO: Getter und setter, genauso aufbauen wie in activity
	public User(String id, String name, boolean formalSpeech, boolean setupComplete, boolean introductionHeard, ArrayList<String> excludedBodyparts) {
		this.id = id;
		this.name = name + "";
		this.nameAsk = name + "? ";
		this.formalSpeech = formalSpeech;
		this.setupComplete = setupComplete;
		this.introductionHeard = introductionHeard;
		this.excludedBodyparts = excludedBodyparts;
	}
}

	