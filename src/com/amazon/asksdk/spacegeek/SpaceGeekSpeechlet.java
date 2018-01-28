package com.amazon.asksdk.spacegeek;

import java.util.ArrayList;
import java.util.Arrays;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.amazon.speech.slu.Intent;
import com.amazon.speech.slu.Slot;
import com.amazon.speech.speechlet.IntentRequest;
import com.amazon.speech.speechlet.LaunchRequest;
import com.amazon.speech.speechlet.Session;
import com.amazon.speech.speechlet.SessionEndedRequest;
import com.amazon.speech.speechlet.SessionStartedRequest;
import com.amazon.speech.speechlet.SpeechletV2;
import com.amazon.speech.speechlet.SpeechletResponse;
import com.amazon.speech.json.SpeechletRequestEnvelope;
import com.amazon.speech.ui.PlainTextOutputSpeech;
import com.amazon.speech.ui.Reprompt;
import com.amazon.speech.ui.SimpleCard;
import com.amazon.speech.ui.OutputSpeech;

public class SpaceGeekSpeechlet implements SpeechletV2 {
    private static final Logger log = LoggerFactory.getLogger(SpaceGeekSpeechlet.class);
 
    private WeatherProvider weatherProvider; 
    private SpeechProvider speaker; 
    private Database database; 
    private Session session; 
    // User data
    private User user; 
    private String userId = null; 
    private boolean knownUser = false; 
    private String userName = null;
    private boolean formalSpeech = true; 
    private boolean nameSet = false; 
    private boolean speechStyleSet = false;
    private boolean setupComplete = false; 
    // Conversation status variables
	private Boolean typeSet = false;
	private Boolean locationSet = false; 
	private Boolean exertionSet = false; 
	private Boolean bodypartSet = false; 
	// Activity data
	private String type = null; 
	private String location = null; 
	private String exertion = null; 
	private String bodypart = null; 
    // Slots mit Synonymen (weil man ohne den SkillBuilder keine Synonyme nutzen kann
    private static final String[] EXERTIONS_RELAXED = new String[] {
    		"entspannt", 
    		"entspannte",
    		"entspanntes",
    		"entspannendes",
    		"ruhig", 
    		"ruhige",
    		"ruhiges",
    		"einfaches"
    };
    private static final String[] EXERTIONS_EXHAUSTING = new String[] {
    		"anstrengend",
    		"anstrengende",
    		"anstrengendes",
    		"erschöpfend",
    		"erschöpfende",
    		"erschöpfendes",
    		"aktiv", 
    		"aktive",
    		"aktives"
    };
    private static final String[] LOCATIONS_INSIDE = new String[] {
    		"drinnen",
    		"nach drinnen",
    		"rein",
    		"im haus",
    		"in der wohnung",
    		"hier",
    		"im innnern",
    		"im inneren"
    };
    private static final String[] LOCATIONS_OUTSIDE = new String[] {
    		"drauẞen",
    		"nach draußen",
    		"raus",
    		"außer Haus",
    		"unter freiem Himmel",
    		"im Freien",
    		"in der Sonne",
    };
    private static final String[] BODYPARTS = new String[] {
    		"bein",
    		"beine", 
    		"hüfte",
    		"bauch",
    		"rücken",
    		"brust",
    		"arme",
    		"arm",
    		"schultern",
    		"schulter",
    		"nacken",
    		"kopf",
    		"knie",
    };
    private static final String[] SPEECHSTYLE_FORMAL = new String[] {
    		"siezen",
    		"gesiezt",
    		"sie",
    		"formal",
    		"höflich"
    };
    private static final String[] SPEECHSTYLE_INFORMAL = new String[] {
    		"duzen",
    		"geduzt",
    		"du",
    		"informal",
    		"umgangssprachlich"
    };
    
    
	
	
		
    ////////// LIFECYCLE METHODS /////////////////////////////////////////////////////////////////////////////////////////
    
	// Ka, wann das gestartet wird 
    public void onSessionStarted(SpeechletRequestEnvelope<SessionStartedRequest> requestEnvelope) {
    	init(requestEnvelope.getSession()); 
    }
	
	// Called when application is launched from keyword ("Starte Fakten")
    public SpeechletResponse onLaunch(SpeechletRequestEnvelope<LaunchRequest> requestEnvelope) {
    	init(requestEnvelope.getSession()); 
    	String output = "Hallo " + user.getName() + speaker.getGreetingText();
        return SpeechletResponse.newTellResponse(getPlainTextOutputSpeech(output));
    }

    // TODO: Checken ob setup komplett ist 
    public SpeechletResponse onIntent(SpeechletRequestEnvelope<IntentRequest> requestEnvelope) {
    	IntentRequest request = requestEnvelope.getRequest();
    	Intent intent = request.getIntent();
        String intentName = (intent != null) ? intent.getName() : null;
        session = requestEnvelope.getSession();
        init(session); 
        
        
        // This is the start of a user request
        if(!user.isSetupComplete()) {
        	return firstSetup(intent); 
        } else {
        	if(!typeSet) {
        	switch(intentName) {
            case "GetGameIntent":
            	return getGame(intent, session);
        	case "GetExerciseIntent":
            	return getExercise(intent, session);
        	case "GetOccupationIntent":
        		return getOccupation(intent, session);
        	case "changeNameIntent": 
        		return changeName(intent, session); 
        	case "changeSpeechSytleIntent":
        		return changeSpeechStyle(intent);
        	case "addPain":
        		return addPain(intent);
            case "AMAZON.HelpIntent":
            	return getAskResponse("Trainer", speaker.getHelpText());
        	case "AMAZON.StopIntent":
                return SpeechletResponse.newTellResponse(getPlainTextOutputSpeech(speaker.getGoodbyeText()));
        	case "AMAZON.CancelIntent": 
        		clear(); 
        		return SpeechletResponse.newTellResponse(getPlainTextOutputSpeech(speaker.getCancelText()));
            default: 
            	String output = "Das habe ich leider nicht verstanden. " + speaker.getHelpText();
            	//return SpeechletResponse.newAskResponse(getPlainTextOutputSpeech(output), getReprompt(getPlainTextOutputSpeech(output)));
            	return SpeechletResponse.newTellResponse(getPlainTextOutputSpeech(output));
        	}
        	// Activity type already set, meaning this intent is for a follow-up question by alexa
	        } else {
	        	switch(type) {
	        	case "game":
	        		return getGame(intent, session);
	        	case "exercise":
	        		return getExercise(intent, session);
	        	case "activity": 
	        		return getOccupation(intent, session);
	        	case "changeName":
	        		return changeName(intent, session);
	        	case "changeSpeech": 
	        		return changeSpeechStyle(intent);
	        	case "addPain":
	        		return addPain(intent);
	        	default: 
	        		return SpeechletResponse.newTellResponse(getPlainTextOutputSpeech(speaker.getErrorText()));
	        	}
	        }
        }
    }
    
    // Ka, wann das gestartet wird
    public void onSessionEnded(SpeechletRequestEnvelope<SessionEndedRequest> requestEnvelope) {
        clear(); 
    }
    
    
    
    
    
	/////////////////////////// SETUP AND USER DATA METHODS /////////////////////////////////////////////////////////////////////////////////////////
	
    // Loads all required data for the session 
    private void init(Session session) {
    	// Variables
    	userId = session.getUser().getUserId();
    	// Initiate database dynamodb service connection
    	database = new Database(); 
    	// Get weather data
    	weatherProvider = new WeatherProvider(); 
    	// Check if user already exists
    	knownUser = database.isUserKnown(userId);
    	
    	// Set params according to user preferences
    	if(knownUser) {
    		user = database.getUser(userId);
    		user.setSetupComplete(true);
    		setupComplete = true; 
    		log.info("this user is known: ");
    		user.printUser();
    	} else {
    		user = new User(userId, null, true);
    		user.setSetupComplete(false);
    		setupComplete = false; 
    		log.info("this user is not known: ");
    		user.printUser();
    	}
		speaker = new SpeechProvider(user.preferesFormalSpeech());
    }
    
    // Clears all user data at the end of a session
    private void clear() {
    	type = null; 
    	location = null; 
    	exertion = null;
    	bodypart = null; 
    	
    	typeSet = false;
    	locationSet = false; 
    	exertionSet = false; 
    	bodypartSet = false; 
    }
	
    // Handles changeName-Request
    private SpeechletResponse changeName(Intent intent, Session session) {
    	
    	type = "changeName";
    	typeSet = true; 
    	
    	Slot nameSlot = intent.getSlot("name");
    	
    	// Check if name was provided
        if(nameSlot != null && nameSlot.getValue() != null && !nameSlot.getValue().equalsIgnoreCase("")){
        	userName = nameSlot.getValue().toLowerCase();
        	database.updateUser(userId, userName, formalSpeech, null);
        	nameSet = true;
        	clear(); 
        	String output = "Verstanden " + userName + ", ich habe mir deinen Namen gemerkt.";
        	return SpeechletResponse.newTellResponse(getPlainTextOutputSpeech(output));
        } else {
        	// Kein Name angegeben, nachfragen
        	nameSet = false; 
        	return SpeechletResponse.newAskResponse(getPlainTextOutputSpeech(speaker.getAskForName()), getReprompt(getPlainTextOutputSpeech(speaker.getAskForNameRe())));
        }
    }
    
    // Handles changeSpeechStyle-Request
    private SpeechletResponse changeSpeechStyle(Intent intent) {
    	type = "changeSpeech";
    	typeSet = true; 
    	
    	Slot speechSlot = intent.getSlot("speechStyle");
    	
    	// Check if name was provided
        if(speechSlot != null && speechSlot.getValue() != null && !speechSlot.getValue().equalsIgnoreCase("")){
        	String style = speechSlot.getValue().toLowerCase();
        	
        	if(Arrays.asList(SPEECHSTYLE_FORMAL).contains(style)) {
	        	formalSpeech = true; 
	        	speechStyleSet = true; 
	        	database.updateUser(userId, userName, formalSpeech,null);
	        	
	        	String output = "Verstanden " + userName + ", ich werde Sie ab jetzt siezen.";
	        	return SpeechletResponse.newTellResponse(getPlainTextOutputSpeech(output));
	        } 
	        
	        if (Arrays.asList(SPEECHSTYLE_INFORMAL).contains(style)) {
	        	formalSpeech = false; 
	        	speechStyleSet = true; 
	        	database.updateUser(userId, userName, formalSpeech, null);
	        	
	        	String output = "Verstanden " + userName + ", ich werde Dich ab jetzt duzen.";
	        	return SpeechletResponse.newTellResponse(getPlainTextOutputSpeech(output));
	        }
        	
        	clear(); 
        	
        } else {
        	// Kein Name angegeben, nachfragen
        	speechStyleSet = false; 
        	return SpeechletResponse.newAskResponse(getPlainTextOutputSpeech(speaker.getAskForSpeechStyle()), getReprompt(getPlainTextOutputSpeech(speaker.getAskForSpeechStyleRe())));
        }
		return null;
    }
    
    // Lets the user add a bodypart that will be excluded from the exercises
    private SpeechletResponse addPain(Intent intent) {
    	type = "addPain";
    	typeSet = true; 
    	
    	Slot painSlot = intent.getSlot("bodypart");
    	
    	// Check if name was provided
        if(painSlot != null && painSlot.getValue() != null && !painSlot.getValue().equalsIgnoreCase("")){
        	String bodypart = painSlot.getValue().toLowerCase();
        	
        	if(Arrays.asList(BODYPARTS).contains(bodypart )) {
	        	database.updateUser(userId, userName, formalSpeech, bodypart);
	        	//145
	        	
	        	String output = "Verstanden " + userName + ", ich merke mir " + bodypart + " Ich werde darauf achten, ab jetzt keine Übungen mehr vorzuschlagen, die den Körperteil weiter belasten";
	        	return SpeechletResponse.newTellResponse(getPlainTextOutputSpeech(output));
	        } 
	        
        	
        	clear(); 
        	
        } else {
        	// Kein Name angegeben, nachfragen
        	return SpeechletResponse.newAskResponse(getPlainTextOutputSpeech(speaker.getAskForPain()), getReprompt(getPlainTextOutputSpeech(speaker.getAskForPainRe())));
        }
		return null;
    }
    
    /////////////////////////////////////////// SETUP CYCLE ///////////////////////////////////////////////////////////////////////////////////////
    
    // Called when the user starts the skill for the first time
	private SpeechletResponse firstSetup(Intent intent) {
        SpeechletResponse nameOutput = null; 
        SpeechletResponse formalOutput = null;
       
        // Check if all data was provided
        if(!nameSet) {
    		nameOutput = setUserName(intent); 	
        }
        if(!speechStyleSet) {
        	formalOutput = setSpeechStyle(intent);
        }
        
        // Ask for missing params
        if(nameOutput != null) {
	        return nameOutput; 
	    }
        if(formalOutput != null) {
	        return formalOutput; 
        }
		
		user.setSetupComplete(true); 
		database.saveUser(userId, userName, formalSpeech);
		
		String output = "Nun gut " + userName + ", sie können mich nun nach Übungen, Spielen oder Beschäftigungen fragen. Sie können außerdem jederzeit Ihren Namen ändern oder mir mittleilen, ob Sie Probleme mit bestimmten Körperteilen haben. Ich werde diese Informationen in meinen Vorschlägen berücksichtigen";
		return SpeechletResponse.newTellResponse(getPlainTextOutputSpeech(output));
	}
	
	// Called when user has set no name, generates aksOutput
	private SpeechletResponse setUserName(Intent intent) {
		
		Slot nameSlot = intent.getSlot("name");
    	
    	// Check if name was provided
        if(nameSlot != null && nameSlot.getValue() != null && !nameSlot.getValue().equalsIgnoreCase("")){
        	userName = nameSlot.getValue().toLowerCase();
        	nameSet = true; 
	        return null; 
        } else {
        	// Kein Ort angegeben, nachfragen
        	nameSet = false; 
        	// Make a first introduction to the skill
        	String introduction = speaker.getIntroductionText() + speaker.getAskForName();
        	return SpeechletResponse.newAskResponse(getPlainTextOutputSpeech(introduction), getReprompt(getPlainTextOutputSpeech(speaker.getAskForNameRe())));
        }
	}
	
	// Called when user has not specified a speech style yet, generates aksOutput
    private SpeechletResponse setSpeechStyle(Intent intent) {
    	Slot styleSlot = intent.getSlot("formalSpeechType");
    	
    	
    	// Check if speech style was provided
        if(styleSlot != null && styleSlot.getValue() != null && !styleSlot.getValue().equalsIgnoreCase("")){
        	String style = styleSlot.getValue().toLowerCase();
        
	        if(Arrays.asList(SPEECHSTYLE_FORMAL).contains(style)) {
	        	formalSpeech = true; 
	        	speechStyleSet = true; 
	        } 
	        
	        if (Arrays.asList(SPEECHSTYLE_INFORMAL).contains(style)) {
	        	formalSpeech = false; 
	        	speechStyleSet = true; 
	        }

	        return null;
	        
        } else {
        	// Kein Style angegeben, nachfragen
        	speechStyleSet = false; 
        	return SpeechletResponse.newAskResponse(getPlainTextOutputSpeech(speaker.getAskForSpeechStyle()), getReprompt(getPlainTextOutputSpeech(speaker.getAskForSpeechStyleRe())));
        }
    }
    
    
    
    
    
    /////////////////////////////////////////// ACTIVITY CYCLE ///////////////////////////////////////////////////////////////////////////////////////
    
	// Returns a game for the user
    private SpeechletResponse getGame(Intent intent, Session session) {// Check if activity location was provided
    	// Initialize params
    	SpeechletResponse locationOutput = null; 
        SpeechletResponse exertionOutput = null;
        String proposal; 
    	type = "game";
    	typeSet = true; 
    	
    	// Check if slots were provided, ask for them if not 
    	if(!locationSet) {
    		locationOutput = getLocation(intent, session); 
    	}
    	if(!exertionSet) {
    		exertionOutput = getExertion(intent, session);
    	}
        
        // Ask for missing params
        if(locationOutput != null) {
	        return locationOutput; 
	    }
        if(exertionOutput != null) {
	        return exertionOutput; 
        }
        
		// Fetch name and description of the game from db        
		ArrayList<String> game = cleanArray(database.getGame(location, exertion));
        String activityString = game.get(0) + ". " + game.get(1);

        // Generate output string
        if(weatherProvider.isWeatherGood()) {
        	proposal = speaker.getProposal() + activityString;
        } else {
        	proposal = "Heute " + weatherProvider.getWeatherDescription() + speaker.getBadWeatherInfo() + speaker.getProposal() + activityString;
        }
        
        clear(); 
        // Generate output speech
        return SpeechletResponse.newTellResponse(getPlainTextOutputSpeech(proposal));
    }
    
    // Returns an exercise for the user 
    private SpeechletResponse getExercise(Intent intent, Session session) {
    	// Initialize params
    	SpeechletResponse locationOutput = null; 
    	SpeechletResponse bodypartOutput = null;
        String proposal; 
    	type = "exercise"; 
        typeSet = true; 
        
        // Check if slots were provided, ask for them if not 
    	if(!locationSet) {
    		locationOutput = getLocation(intent, session);
    	}
    	if(!bodypartSet) {
    		bodypartOutput = getBodypart(intent, session);
    	}
        
        // Ask for missing params
        if(locationOutput != null) {
	        return locationOutput; 
	    }
        if(bodypartOutput != null) {
	        return bodypartOutput; 
        }

		// Fetch name and description of the game from db
		ArrayList<String> exercise = cleanArray(database.getExercise(userId, location, bodypart));
		String activityString = exercise.get(0) + ". " + exercise.get(1);

        // Generate output string
        if(weatherProvider.isWeatherGood()) {
        	proposal = speaker.getProposal() + activityString;
        } else {
        	proposal = "Heute " + weatherProvider.getWeatherDescription() + speaker.getBadWeatherInfo() + speaker.getProposal() + activityString;
        }
        
        clear(); 
        // Generate output speech
        return SpeechletResponse.newTellResponse(getPlainTextOutputSpeech(proposal));
    }
    
    // Returns an occupation for the user 
    private SpeechletResponse getOccupation(Intent intent, Session session) {
    	// Initialize params
    	SpeechletResponse locationOutput = null; 
        SpeechletResponse exertionOutput = null;
        String proposal; 
    	type = "occupation";
    	typeSet = true; 
    	
    	// Check if slots were provided, ask for them if not 
    	if(!locationSet) {
    		locationOutput = getLocation(intent, session);
    	}
    	if(!exertionSet) {
    		exertionOutput = getExertion(intent, session);
    	}
        
        
        // Ask for missing params
        if(locationOutput != null) {
	        return locationOutput; 
	    }
        if(exertionOutput != null) {
	        return exertionOutput; 
        }

		// Fetch name and description of the game from db
		ArrayList<String> occupation = cleanArray(database.getOccupation(location, exertion));
		String activityString = occupation.get(0) + ". " + occupation.get(1);
        
        // Generate output string
        if(weatherProvider.isWeatherGood()) {
        	proposal = speaker.getProposal() + activityString;
        } else {
        	proposal = "Heute " + weatherProvider.getWeatherDescription() + speaker.getBadWeatherInfo() + speaker.getProposal() + activityString;
        }
        
        clear(); 
        // Generate output speech
        return SpeechletResponse.newTellResponse(getPlainTextOutputSpeech(proposal));
    }

    // Checks if location was provided, if not, asks for it
    private SpeechletResponse getLocation(Intent intent, Session session) {
    	
    	Slot locationSlot = intent.getSlot("location");
    	
    	if(weatherProvider.isWeatherGood()) {
    		log.info("GetLocation, isWeatherGood " + weatherProvider.isWeatherGood());
        	// Check if activity location was provided
	        if(locationSlot != null && locationSlot.getValue() != null && !locationSlot.getValue().equalsIgnoreCase("")){
	        	String location = locationSlot.getValue().toLowerCase();
	        
		        if(Arrays.asList(LOCATIONS_INSIDE).contains(location)) {
		        	this.location = "inside";
		        	locationSet = true; 
		        	} 
		        
		        if (Arrays.asList(LOCATIONS_OUTSIDE).contains(location)) {
		        	this.location = "outside";
		        	locationSet = true; 
		        }
		        
		        return null; 
		        
	        } else {
	        	// Kein Ort angegeben, nachfragen
	        	locationSet = false; 
	        	return SpeechletResponse.newAskResponse(getPlainTextOutputSpeech(speaker.getAskForLocation()), getReprompt(getPlainTextOutputSpeech(speaker.getAskForLocationRe())));
	        }
	        
        } else {
        	this.location = "inside";
        	locationSet = true; 
        	return null; 
        }
    }
    
    // Checks if exertion was provided, if not, asks for it
    private SpeechletResponse getExertion(Intent intent, Session session) {
    	Slot exertionSlot = intent.getSlot("exertion");

    	// Check if activity exertion was provided
        if(exertionSlot != null && exertionSlot.getValue() != null && !exertionSlot.getValue().equalsIgnoreCase("")){
        	String exertion = exertionSlot.getValue().toLowerCase();
        
	        if(Arrays.asList(EXERTIONS_RELAXED).contains(exertion)) {
	        	this.exertion = "relaxed";
	        	exertionSet = true; 
	        } 
	        
	        if (Arrays.asList(EXERTIONS_EXHAUSTING).contains(exertion)) {
	        	this.exertion = "exhausting";
	        	exertionSet = true; 
	        }
	        
	        return null;
	        
        } else {
        	// Kein Exertion angegeben, nachfragen
        	exertionSet = false; 
        	return SpeechletResponse.newAskResponse(getPlainTextOutputSpeech(speaker.getAskForExertion()), getReprompt(getPlainTextOutputSpeech(speaker.getAskForExertionRe())));
        }
    }
    
    // Checks if a bodypart (for the exercise) was provided, if not, asks for it
    private SpeechletResponse getBodypart(Intent intent, Session session) {
    	Slot bodypartSlot = intent.getSlot("bodypart");

    	
    	// Check if bodypart bodypart was provided
        if(bodypartSlot != null && bodypartSlot.getValue() != null && !bodypartSlot.getValue().equalsIgnoreCase("")){
        	String bodypart = bodypartSlot.getValue().toLowerCase();
        
        	// Filter out plural forms, so I don't have to define them in the DB
	        if(Arrays.asList(BODYPARTS).contains(bodypart)) {
	        	switch (bodypart) {
	        	case "beine":
	        		this.bodypart = "bein";
	        		break;
	        	case "arme":
	        		this.bodypart = "arm";
	        		break;
	        	case "schultern":
	        		this.bodypart = "schulter"; 
	        		break;
	        	default: 
	        		this.bodypart = bodypart; 
	        		break; 
	        	}
	        	
	        	bodypartSet = true; 
	        } 
	        
	        return null; 
	        
        } else {
        	// Kein bodypart angegeben, nachfragen
        	bodypartSet = false; 
        	return SpeechletResponse.newAskResponse(getPlainTextOutputSpeech(speaker.getAskForBodypart()), getReprompt(getPlainTextOutputSpeech(speaker.getAskForBodypartRe())));
        }
    }
    
    
    
    
    
    ////////// HELPER FUNCTIONS /////////////////////////////////////////////////////////////////////////////////////////
    
    // Cleans a string from the DB 
    private ArrayList<String> cleanArray(ArrayList<String> dirtyArrayList) {
    	String cleanName = dirtyArrayList.get(0);
    	cleanName = cleanName.replace("{S:", "");
    	cleanName = cleanName.replace(",}.", "");
    	cleanName = cleanName.replace(",}", "");
    	String cleanDescription = dirtyArrayList.get(1);
    	cleanDescription = cleanDescription.replace("{S:", "");
    	cleanDescription = cleanDescription.replace(",}.", "");
    	cleanDescription = cleanDescription.replace(",}", "");
    	
    	ArrayList<String> cleanArrayList = new ArrayList<>();
    	cleanArrayList.add(cleanName);
    	cleanArrayList.add(cleanDescription);
    	
    	return cleanArrayList;
    	
    }
    
    // Creates a card object.
    private SimpleCard getSimpleCard(String title, String content) {
        SimpleCard card = new SimpleCard();
        card.setTitle(title);
        card.setContent(content);

        return card;
    }
    
    /**
     * Helper method for retrieving an OutputSpeech object when given a string of TTS.
     * @param speechText the text that should be spoken out to the user.
     * @return an instance of SpeechOutput.
     */
    private PlainTextOutputSpeech getPlainTextOutputSpeech(String speechText) {
        PlainTextOutputSpeech speech = new PlainTextOutputSpeech();
        speech.setText(speechText);

        return speech;
    }

    /**
     * Helper method that returns a reprompt object. This is used in Ask responses where you want
     * the user to be able to respond to your speech.
     * @param outputSpeech The OutputSpeech object that will be said once and repeated if necessary.
     * @return Reprompt instance.
     */
    private Reprompt getReprompt(OutputSpeech outputSpeech) {
        Reprompt reprompt = new Reprompt();
        reprompt.setOutputSpeech(outputSpeech);

        return reprompt;
    }

    /**
     * Helper method for retrieving an Ask response with a simple card and reprompt included.
     * @param cardTitle Title of the card that you want displayed.
     * @param speechText speech text that will be spoken to the user.
     * @return the resulting card and speech text.
     */
    private SpeechletResponse getAskResponse(String cardTitle, String speechText) {
        SimpleCard card = getSimpleCard(cardTitle, speechText);
        PlainTextOutputSpeech speech = getPlainTextOutputSpeech(speechText);
        Reprompt reprompt = getReprompt(speech);

        return SpeechletResponse.newAskResponse(speech, reprompt, card);
    }
    
    
    


}





