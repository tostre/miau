package com.amazon.asksdk.spacegeek;

public class SetupStatus {

	private boolean nameSet; 
	private boolean speechStyleSet; 
	private boolean excludedBodypartsSet;
	private boolean introductionHeard; 
	private boolean setupComplete; 
	
	public SetupStatus() {
		nameSet = false; 
		speechStyleSet = false; 
		excludedBodypartsSet = false; 
		introductionHeard = false; 
		setupComplete = false; 
	}

	public boolean isNameSet() {
		return nameSet;
	}

	public void setNameSet(boolean nameSet) {
		this.nameSet = nameSet;
	}

	public boolean isSpeechStyleSet() {
		return speechStyleSet;
	}

	public void setSpeechStyleSet(boolean speechStyleSet) {
		this.speechStyleSet = speechStyleSet;
	}

	public boolean isExcludedBodypartsSet() {
		return excludedBodypartsSet;
	}

	public void setExcludedBodypartsSet(boolean excludedBodypartsSet) {
		this.excludedBodypartsSet = excludedBodypartsSet;
	}

	public boolean isIntroductionHeard() {
		return introductionHeard;
	}

	public void setIntroductionHeard(boolean introductionHeard) {
		this.introductionHeard = introductionHeard;
	}

	public boolean isSetupComplete() {
		return setupComplete;
	}

	public void setSetupComplete(boolean setupComplete) {
		this.setupComplete = setupComplete;
	}
	
	
}
