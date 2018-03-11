package com.amazon.asksdk.spacegeek;

import java.util.Random;

public class SpeechProvider {
	// Speech related variables
    String cancelText = "In Ordnung, ich vergesse was bisher gesagt wurde. Wir können nun von Neuem beginnen ";
    String goodbyeText = "Bis zum nächsten Mal ";
    String badWeatherInfo = "Ich würde daher empfehlen drinnen zu bleiben. ";
    String greetingText = "Hallo, fragen Sie mich nach einem Spiel, einer Übung oder einer Beschäftigung ";
    String introductionText = "Hallo, freut mich Sie kennenzulernen. Ich kann Ihnen verschiedene Spiele, Übungen oder Beschäftigungen vorschlagen. Zuerst muss ich Ihnen aber ein paar Fragen stellen um Sie besser kennenzulernen. ";
    
    String helpText = "leer";
    String helpTextF = "Ich kann verschiedene Aktivitäten, Spiele und Übungen, drinnen oder draußen, vorschlagen. Fragen Sie mich einfach nach einem Spiel, einer Übung oder einer Beschäftigung. Alles Weitere klären wir darauf gemeinsam ";
    String helpTextI = "Ich kann verschiedene Aktivitäten, Spiele und Übungen, drinnen oder draußen, vorschlagen. Frag mich einfach nach einem Spiel, einer Übung oder einer Beschäftigung. Alles Weitere klären wir darauf gemeinsam ";
    
    String errorText = "leer";
    String errorTextF = "Es tut mir leid, etwas ist schief gegangen. Fangen Sie bitte noch einmal von vorne an ";
    String errorTextI = "Es tut mir leid, etwas ist schief gegangen. Fang bitte noch einmal von vorne an ";
    
    String askForLocation = "leer";
    String askForLocationF = "Möchten Sie dazu drinnen bleiben oder rausgehen? ";
    String askForLocationI = "Möchtest Du dazu drinnen bleiben oder rausgehen? ? ";
    String askForLocationRe = "";
    String askForLocationReF = "Es tut mir leid, ich konnte nichts verstehen. Möchten Sie dazu drinnen bleiben oder rausgehen? ";
    String askForLocationReI = "Es tut mir leid, ich konnte nichts verstehen. Möchtest Du dazu drinnen bleiben oder rausgehen? ";
    
    String askForExertion = "leer";
    String askForExertionF = "Möchten Sie etwas entspannendes oder aktives unternehmen? ";
    String askForExertionI = "Möchtest Du etwas entspannendes oder aktives unternehmen? ";
    String askForExertionRe = "leer"; 
    String askForExertionReF = "Es tut mir leid, ich konnte nichts verstehen. Möchten Sie etwas entspannendes oder aktives unternehmen? ";
    String askForExertionReI = "Es tut mir leid, ich konnte nichts verstehen. Möchtest Du etwas entspannendes oder aktives unternehmen? ";
    
    String askForBodypart = "leer";
    String askForBodypartF = "Welchen Körperteil möchten Sie traineren? ";
    String askForBodypartI = "Welchen Körperteil möchtest Du traineren? ";
    String askForBodypartRe = "leer"; 
    String askForBodypartReF = "Es tut mir leid, ich konnte nichts verstehen. Welchen Körperteil möchten Sie traineren? ";
    String askForBodypartReI = "Es tut mir leid, ich konnte nichts verstehen. Welchen Körperteil möchtest Du traineren? ";
    
    String askForPain = "leer";
    String askForPainF = "Welchen Körperteil möchten Sie von den Übungen ausschließen? ";
    String askForPainI = "Welchen Körperteil möchtest Du von den Übungen ausschließen? ";
    String askForPainRe = "leer"; 
    String askForPainReF = "Es tut mir leid, ich konnte nichts verstehen. Welchen Körperteil möchten Sie von den Übungen ausschließen? ";
    String askForPainReI = "Es tut mir leid, ich konnte nichts verstehen. Welchen Körperteil möchtest Du von den Übungen ausschließen? ";
    
    String askForName = "Wie heißen Sie mit Vornamen? ";
    String askForNameRe = "Es tut mir leid, ich konnte nichts verstehen. Wie heißen Sie mit Vornamen? ";
    
    String askForSpeechStyle = "Möchten Sie gesiezt oder geduzt werden? ";
    String askForSpeechStyleRe = "Es tut mir leid, ich konnte nichts verstehen. Möchten Sie gesiezt oder geduzt werden? ";
    
    String confirmSpeechStyle = "leer";
    String confirmSpeechStyleF = "In Ordnung, ich werde Sie ab jetzt Siezen ";
    String confirmSpeechStyleI = "In Ordnung, ich werde Dich ab jetzt Duzen ";
    
    String[] proposals = new String[] {
    		"Wie wäre es hiermit? ", 
    		"Ich schlage Folgendes vor: ", 
    		"Das hier klingt nach einer guten Beschäftigung: "
    };
    private static final String[] GREETINGS_FORMAL = new String[] {
    		"Hallo, wie kann ich Ihnen helfen? ",
    		"Was kann ich ür Sie tun? ",
    		"Kann ich Ihnen eine Aktivität für heute empfehlen? ",
    };
    private static final String[] GREETINGS_INFORMAL = new String[] {
    		"Hallo, wie kann ich Dir helfen ",
    		"Was kann ich für Dich tun ",
    		"Kann ich Dir eine Aktivität für heute empfehlen ",
    };
    
    public SpeechProvider(boolean formalSpeech) {
    	if(formalSpeech) {
    		helpText = helpTextF; 
    		errorText = errorTextF;
    		askForLocation = askForLocationF; 
    		askForLocationRe = askForLocationReF;
    		askForExertion = askForExertionF; 
    		askForExertionRe = askForExertionReF;
    		askForBodypart = askForBodypartF;
    		askForBodypartRe = askForBodypartReF;
    		askForPain = askForPainF; 
    		askForPainRe = askForPainReF; 
    		confirmSpeechStyle = confirmSpeechStyleF;
    		greetingText = GREETINGS_FORMAL[(int) Math.floor(Math.random() * GREETINGS_FORMAL.length)];
    	} else {
    		helpText = helpTextI; 
    		errorText = errorTextI;
    		askForLocation = askForLocationI;
    		askForLocationRe = askForLocationReI; 
    		askForExertion = askForExertionI; 
    		askForExertionRe = askForExertionReI;
    		askForBodypart = askForBodypartI;
    		askForBodypartRe = askForBodypartReI;
    		askForPain = askForPainI; 
    		askForPainRe = askForPainReI; 
    		confirmSpeechStyle = confirmSpeechStyleI;
    		greetingText = GREETINGS_INFORMAL[(int) Math.floor(Math.random() * GREETINGS_INFORMAL.length)];
    	}
    }
    
    public void setFormalSpeech(boolean formalSpeech) {
    	if(formalSpeech) {
    		helpText = helpTextF; 
    		errorText = errorTextF;
    		askForLocation = askForLocationF; 
    		askForLocationRe = askForLocationReF;
    		askForExertion = askForExertionF; 
    		askForExertionRe = askForExertionReF;
    		askForBodypart = askForBodypartF;
    		askForBodypartRe = askForBodypartReF;
    		confirmSpeechStyle = confirmSpeechStyleF;
    		greetingText = GREETINGS_FORMAL[(int) Math.floor(Math.random() * GREETINGS_FORMAL.length)];
    	} else {
    		helpText = helpTextI; 
    		errorText = errorTextI;
    		askForLocation = askForLocationI;
    		askForLocationRe = askForLocationReI; 
    		askForExertion = askForExertionI; 
    		askForExertionRe = askForExertionReI;
    		askForBodypart = askForBodypartI;
    		askForBodypartRe = askForBodypartReI;
    		confirmSpeechStyle = confirmSpeechStyleI;
    		greetingText = GREETINGS_INFORMAL[(int) Math.floor(Math.random() * GREETINGS_INFORMAL.length)];
    	}
    }

	public String getCancelText() {
		return cancelText;
	}

	public String getGoodbyeText() {
		return goodbyeText;
	}

	public String getBadWeatherInfo() {
		return badWeatherInfo;
	}

	public String getGreetingText() {
		return greetingText;
	}

	public String getIntroductionText() {
		return introductionText;
	}

	public String getHelpText() {
		return helpText;
	}

	public String getErrorText() {
		return errorText;
	}

	public String getAskForLocation() {
		return askForLocation;
	}

	public String getAskForLocationRe() {
		return askForLocationRe;
	}

	public String getAskForExertion() {
		return askForExertion;
	}

	public String getAskForExertionRe() {
		return askForExertionRe;
	}

	public String getAskForBodypart() {
		return askForBodypart;
	}

	public String getAskForBodypartRe() {
		return askForBodypartRe;
	}

	public String getAskForName() {
		return askForName;
	}

	public String getAskForNameRe() {
		return askForNameRe;
	}

	public String getAskForSpeechStyle() {
		return askForSpeechStyle;
	}

	public String getAskForSpeechStyleRe() {
		return askForSpeechStyleRe;
	}

	public String getConfirmSpeechStyle() {
		return confirmSpeechStyle;
	}

	public String getAskForPain() {
		return askForPain; 
	}
	
	public String getAskForPainRe() {
		return askForPainRe; 
	}

	// Return random propsal sentence
	public String getProposal() {
		return proposals[new Random().nextInt(proposals.length)];
	}

	// Return random greeting
	public static String getGreetingFormal() {
		return GREETINGS_FORMAL[new Random().nextInt(GREETINGS_FORMAL.length)];
	}

	// Return random greeting
	public static String getGreetingInformal() {
		return GREETINGS_INFORMAL[new Random().nextInt(GREETINGS_INFORMAL.length)];
	}
}
