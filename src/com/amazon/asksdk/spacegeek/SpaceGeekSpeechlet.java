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
 
    // Session data
    private String requestId;
    private String sessionId;
    private String userId = ""; 
    private WeatherProvider weatherProvider; 
    private SpeechProvider speaker; 
    private Database database; 
    private Activity activity; 
    private Session session; 
    private User user; 
   
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
    		"erschˆpfend", 
    		"erschˆpfende",
    		"erschˆpfendes",
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
    		"drauﬂen",
    		"nach drauﬂen",
    		"raus",
    		"auﬂer Haus",
    		"unter freiem Himmel",
    		"im Freien",
    		"in der Sonne",
    };
    private static final String[] SPEECH_FORMAL = new String[] {
    		"Sie",
    		"Siezen",
    		"Ihr",
    		"Ihre",
    		"Ihres",
    		"Ihrs",
    		"Ihnen"
    };
    private static final String[] SPEECH_INFORMAL = new String[] {
    		"Du",
    		"Duzen",
    		"Dein",
    		"Deine",
    		"Deins",
    		"Dich"
    };
    private static final String[] BODYPARTS = new String[] {
    		"bein",
    		"beine", 
    		"h¸fte",
    		"bauch",
    		"r¸cken",
    		"brust",
    		"arme",
    		"arm",
    		"schultern",
    		"schulter",
    		"nacken",
    		"kopf",
    		"knie",
    		"keinen bestimmten",
    		"keine bestimmten",
    		"kein bestimmtes",
    		"alle"
    };
    private static final String[] ACTIVITY_TYPE_GAME = new String[] {
    		"spiel",
    		"spiele",
    		"spielen",
    		"brettspiel",
    		"brettspiele",
    		"kartenspiel",
    		"kartenspiele"
    };
    private static final String[] ACTIVITY_TYPE_EXERCISE = new String[] {
    		"¸bung",
    		"¸ben",
    		"training",
    		"traineren"
    };
    private static final String[] ACTIVITY_TYPE_OCCUPATION = new String[] {
    		"besch‰ftigung",
    		"t‰tigkeit",
    		"allgemeine besch‰ftigung",
    		"allgemeine t‰tigket",
    		"normale besch‰ftigung",
    		"normale t‰tigkeit"
    };
    private static final String[] ACTIVITY_TYPE_ACTIVITY = new String[] {
    		"besch‰ftigung",
    		"t‰tigkeit",
    		"allgemeine besch‰ftigung",
    		"allgemeine t‰tigket",
    		"normale besch‰ftigung",
    		"normale t‰tigkeit"
    };
    private static final String[] SPEECHSTYLE_FORMAL = new String[] {
    		"siezen",
    		"gesiezt",
    		"sie"
    };
    private static final String[] SPEECHSTYLE_INFORMAL = new String[] {
    		"duzen",
    		"geduzt",
    		"du"
    };
    
    // DB emulator (game)
    String[] gameInsideRelaxedDb = new String[] {
    		"drinnen etwas entspanntes spielen eins",
    		"drinnen etwas entspanntes spielen zwei"
    };
    String[] gameInsideExhaustingDb = new String[] {
    		"drinnen etwas anstrengendes spielen eins",
    		"drinnen etwas anstrengendes spielen zwei"
    };
	String[] gameOutsideRelaxedDb = new String[] {
	    	"drauﬂen etwas entspanntes spielen eins",
	    	"drauﬂen etwas entspanntes spielen zwei"
	};
	String[] gameOutsideExhaustingDb = new String[] {
    		"drauﬂen etwas anstrengendes spielen eins",
    		"drauﬂen etwas anstrengendes spielen zwei"
	};    
	
	// DB emulator activity
    String[] occupationInsideRelaxedDb = new String[] {
    		"drinnen etwas entspanntes tun eins",
    		"drinnen etwas entspanntes tun zwei"
    };
    String[] occupationInsideExhaustingDb = new String[] {
    		"drauﬂen etwas anstrengendes tun eins",
    		"drauﬂen etwas anstrengendes tun zwei"
	};
	String[] occupationOutsideRelaxedDb = new String[] {
	    	"drauﬂen etwas entspanntes tun eins",
	    	"drauﬂen etwas entspanntes tun zwei"
	};
	String[] occupationOutsideExhaustingDb = new String[] {
    		"drauﬂen etwas anstrengendes tun eins",
    		"drauﬂen etwas anstrengendes tun zwei"
	};  
    
    // DB emulator exercise
    String[] exerciseInsideExhaustingDb = new String[] {
    		"drauﬂen etwas trainieren eins",
    		"drauﬂen etwas trainieren zwei"
	};
	String[] exerciseOutsideExhaustingDb = new String[] {
    		"drauﬂen etwas trainieren eins",
    		"drauﬂen etwas trainieren zwei"
	};  
    
    
    
    
    
    ////////// LIFECYCLE METHODS /////////////////////////////////////////////////////////////////////////////////////////
    
	// Called when application is launched from keyword ("Starte Fakten")
    public SpeechletResponse onLaunch(SpeechletRequestEnvelope<LaunchRequest> requestEnvelope) {
    	log.info("ONLAUNCH");
    	init(requestEnvelope.getSession()); 
        /*session = requestEnvelope.getSession();
        userId = session.getUser().getUserId();
        goodWeather = getWeather(); 
        // TODO: Remove this line
	    goodWeather = true; 
	    
	    initDb(); 
        initUserData(); 
        initSpeech();*/
        return SpeechletResponse.newTellResponse(getPlainTextOutputSpeech(speaker.getGreetingText()));
    }

    // Ka, wann das gestartet wird 
    public void onSessionStarted(SpeechletRequestEnvelope<SessionStartedRequest> requestEnvelope) {
    	log.info("ONSESSIONSTARTED");
    	init(requestEnvelope.getSession()); 
      /*  requestId = requestEnvelope.getRequest().getRequestId();
	    sessionId = requestEnvelope.getSession().getSessionId();
	    userId = requestEnvelope.getSession().getUser().getUserId();
    	log.info("ONSESSIONSTARTED" + userId);
	    goodWeather = getWeather(); 
        // TODO: Remove this line
	    goodWeather = true; 
	    
	    initDb(); 
        initUserData(); 
        initSpeech();
	    log.info("onSessionStarted requestId={}, sessionId={}", requestId, sessionId);
    */}
    
    
    public void onSessionEnded(SpeechletRequestEnvelope<SessionEndedRequest> requestEnvelope) {
        /*log.info("onSessionEnded requestId={}, sessionId={}", requestEnvelope.getRequest().getRequestId(),
		requestEnvelope.getSession().getSessionId());
        // any cleanup logic goes here
    */}
    
    // TODO: Checken ob setup komplett ist 
    public SpeechletResponse onIntent(SpeechletRequestEnvelope<IntentRequest> requestEnvelope) {
    	log.info("ONINTENT");
    	
    	IntentRequest request = requestEnvelope.getRequest();
    	Intent intent = request.getIntent();
        String intentName = (intent != null) ? intent.getName() : null;
        session = requestEnvelope.getSession();
    	
        init(session); 
        
        
        
        
        log.info("user Setup:" + user.isSetupComplete());
        // This is the start of a user request
        if(!user.isSetupComplete()) {
        	return firstSetup(intent); 
        } else {
        	if(!activity.isTypeSet()) {
        	switch(intentName) {
            case "GetGameIntent":
            	return getGame(intent, session);
        	case "GetExerciseIntent":
            	return getExercise(intent, session);
        	case "GetOccupationIntent":
        		return getOccupation(intent, session);
            case "AMAZON.HelpIntent":
        		return getHelpResponse();
        	case "AMAZON.StopIntent":
                return SpeechletResponse.newTellResponse(getPlainTextOutputSpeech(speaker.getGoodbyeText()));
        	case "AMAZON.CancelIntent": 
                return cancelRequest();
            default: 
            	String output = "Das habe ich leider nicht verstanden. " + speaker.getHelpText();
            	return SpeechletResponse.newAskResponse(getPlainTextOutputSpeech(output), getReprompt(getPlainTextOutputSpeech(output)));
        	}
        	// Activity type already set, meaning this intent is for a follow-up question by alexa
	        } else {
	        	switch(activity.getType()) {
	        	case "game":
	        		return getGame(intent, session);
	        	case "exercise":
	        		return getExercise(intent, session);
	        	case "activity": 
	        		return getOccupation(intent, session);
	        	default: 
	        		return SpeechletResponse.newTellResponse(getPlainTextOutputSpeech(speaker.getErrorText()));
	        	}
	        }
        }
    }
    
    
    
    
    
	////////// SETUP AND USER DATA METHODS /////////////////////////////////////////////////////////////////////////////////////////
	
    // Loads all required data for the session 
    private void init(Session session) {
    	log.info("init");
    	
    	// Variables
    	userId = session.getUser().getUserId();
    	// Initiate database dynamodb service connection
    	database = new Database(); 
    	// Get weather data
    	weatherProvider = new WeatherProvider(); 
    	// Create empty activity
    	activity = new Activity();
    	
    	// Get user data
    	User user = database.getUser(userId);
		if(user != null) {
			// if user exists, load his data
			log.info("found user in database");
			this.user = user; 
			user.printUser();
		} else {
			// if user doesn't exist in table, create new empty user 
			log.error("could not find userId in table");
			user = new User(userId, "nameEmpty", false, false, new ArrayList<String>());
			user.printUser();
		}
		
		// Overwrite speech variables based on 
		speaker = new SpeechProvider(user.preferesFormalSpeech());
    }
    
    
	
	
	
    
    
    /////////////////////////////////////////// SETUP CYCLE ///////////////////////////////////////////////////////////////////////////////////////
    
    // Called when the user starts the skill for the first time
	private SpeechletResponse firstSetup(Intent intent) {
		log.info("FIRSTSETUP");
    	 
        SpeechletResponse nameOutput = null; 
        SpeechletResponse formalOutput = null;
        
        // Check if location and exertion were provided as slots
        if(!user.isNameSet()) {
    		nameOutput = setUserName(intent); 	
        }
        if(!user.isSpeechStyleSet()) {
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
		database.saveUser(user);
		
		String output = "Nun gut, " + user.getName() + ", sie kˆnnen mich nun nach ‹bungen, Spielen oder Besch‰ftigungen fragen";
		
		return SpeechletResponse.newTellResponse(getPlainTextOutputSpeech(output));
	}
	
	// TODO: namen-slot aufsetzen, dann namen in variable speichern, in db schreiben. Auﬂerdem descriptionText beim ersten FDragen nach dem Namen ausgeben
	// Called when skill is first exectuted or user wants to change their name
	private SpeechletResponse setUserName(Intent intent) {
		log.info("SETUSERNAME");
		
		Slot nameSlot = intent.getSlot("name");
    	
    	// Check if activity location was provided
        if(nameSlot != null && nameSlot.getValue() != null && !nameSlot.getValue().equalsIgnoreCase("")){
        	user.setName(nameSlot.getValue().toLowerCase()); 
	        return null; 
        } else {
        	// Kein Ort angegeben, nachfragen
        	user.setName(null);
        	return SpeechletResponse.newAskResponse(getPlainTextOutputSpeech(speaker.getAskForName()), getReprompt(getPlainTextOutputSpeech(speaker.getAskForNameRe())));
        }
	}
	
    // Called when skill is first exectuted or user wants to change the speech style (formal/informal) 
    private SpeechletResponse setSpeechStyle(Intent intent) {
    	log.info("SETSPEECHSTYLE");
	    	
    	Slot styleSlot = intent.getSlot("formalSpeechType");
    	
    	
    	// Check if speech style was provided
        if(styleSlot != null && styleSlot.getValue() != null && !styleSlot.getValue().equalsIgnoreCase("")){
        	String style = styleSlot.getValue().toLowerCase();
        
	        if(Arrays.asList(SPEECHSTYLE_FORMAL).contains(style)) {
	        	user.setFormalSpeech(true);
	        } 
	        
	        if (Arrays.asList(SPEECHSTYLE_INFORMAL).contains(style)) {
	        	user.setFormalSpeech(false);
	        }

	        return null;
	        
        } else {
        	// Kein Style angegeben, nachfragen
        	user.setSpeechStyleSet(false);
        	return SpeechletResponse.newAskResponse(getPlainTextOutputSpeech(speaker.getAskForSpeechStyle()), getReprompt(getPlainTextOutputSpeech(speaker.getAskForSpeechStyleRe())));
        }
    }
    
    
	
    /////////////////////////////////////////// ACTIVITY CYCLE ///////////////////////////////////////////////////////////////////////////////////////
    
    
    
	// Returns a game for the user
    private SpeechletResponse getGame(Intent intent, Session session) {// Check if activity location was provided
    	log.info("GETGAME");
    	// Initialize params
    	SpeechletResponse locationOutput = null; 
        SpeechletResponse exertionOutput = null;
        String proposal; 
    	activity.setType("game");
    	
        
        // Check if location and exertion were provided as slots
    	if(!activity.isLocationSet()) {
    		locationOutput = getLocation(intent, session);
    	} else {
    		activity.setLocation(null);
	    	locationOutput = getLocation(intent, session);
        }
        
    	if(!activity.isExertionSet()) {
    		exertionOutput = getExertion(intent, session);
    	} else {
        	activity.setExertion(null);
        	exertionOutput = getExertion(intent, session);
        }
        
        
        // Ask for missing params
        if(locationOutput != null) {
	        return locationOutput; 
	    }
        if(exertionOutput != null) {
	        return exertionOutput; 
        }
        
        
        // Fetch game from db
        String activityString = database.getGame(activity.getLocation(), activity.getExertion());
        
        
        // Generate output string
        if(weatherProvider.isWeatherGood()) {
        	proposal = speaker.getProposal() + activityString;
        } else {
        	proposal = "Heute " + weatherProvider.getWeatherDescription() + speaker.getBadWeatherInfo() + speaker.getProposal() + activityString;
        }
        
        
        // Generate output speech
        return SpeechletResponse.newTellResponse(getPlainTextOutputSpeech(proposal));
    }
    
    // Returns an exercise for the user 
    private SpeechletResponse getExercise(Intent intent, Session session) {
    	log.info("GETEXERCISE");
    	// Initialize params
    	SpeechletResponse locationOutput = null; 
    	SpeechletResponse bodypartOutput = null;
        String proposal; 
    	activity.setType("exercise");
        
        
        // Check if location and bodypart were provided as slots
    	if(!activity.isLocationSet()) {
    		locationOutput = getLocation(intent, session);
    	} else {
    		activity.setLocation(null);
	    	locationOutput = getLocation(intent, session);
        }
        
    	if(!activity.isBodypartSet()) {
    		bodypartOutput = getBodypart(intent, session);
    	} else {
        	activity.setBodypart(null);
        	bodypartOutput = getBodypart(intent, session);
        }
        
        
        // Ask for missing params
        if(locationOutput != null) {
	        return locationOutput; 
	    }
        if(bodypartOutput != null) {
	        return bodypartOutput; 
        }
        
        
        // Fetch exercise from db
        String activityString = database.getExercise(activity.getLocation(), activity.getBodypart());
        
        
        // Generate output string
        if(weatherProvider.isWeatherGood()) {
        	proposal = speaker.getProposal() + activityString;
        } else {
        	proposal = "Heute " + weatherProvider.getWeatherDescription() + speaker.getBadWeatherInfo() + speaker.getProposal() + activityString;
        }
        
        
        // Generate output speech
        return SpeechletResponse.newTellResponse(getPlainTextOutputSpeech(proposal));
    }
    
    // Returns an occupation for the user 
    private SpeechletResponse getOccupation(Intent intent, Session session) {
    	log.info("GETGAME");
    	// Initialize params
    	SpeechletResponse locationOutput = null; 
        SpeechletResponse exertionOutput = null;
        String proposal; 
    	activity.setType("occupation");
    	
        
        // Check if location and exertion were provided as slots
    	if(!activity.isLocationSet()) {
    		locationOutput = getLocation(intent, session);
    	} else {
    		activity.setLocation(null);
	    	locationOutput = getLocation(intent, session);
        }
        
    	if(!activity.isExertionSet()) {
    		exertionOutput = getExertion(intent, session);
    	} else {
        	activity.setExertion(null);
        	exertionOutput = getExertion(intent, session);
        }
        
        
        // Ask for missing params
        if(locationOutput != null) {
	        return locationOutput; 
	    }
        if(exertionOutput != null) {
	        return exertionOutput; 
        }
        
        
        // Fetch game from db
        String activityString = database.getOccupation(activity.getLocation(), activity.getExertion());
        
        
        // Generate output string
        if(weatherProvider.isWeatherGood()) {
        	proposal = speaker.getProposal() + activityString;
        } else {
        	proposal = "Heute " + weatherProvider.getWeatherDescription() + speaker.getBadWeatherInfo() + speaker.getProposal() + activityString;
        }
        
        
        // Generate output speech
        return SpeechletResponse.newTellResponse(getPlainTextOutputSpeech(proposal));
    }

    // Checks if location was provided, if not, asks for it
    private SpeechletResponse getLocation(Intent intent, Session session) {
    	log.info("GETLOCATION");
    	
    	Slot locationSlot = intent.getSlot("location");
    	
    	if(weatherProvider.isWeatherGood()) {
        	// Check if activity location was provided
	        if(locationSlot != null && locationSlot.getValue() != null && !locationSlot.getValue().equalsIgnoreCase("")){
	        	String location = locationSlot.getValue().toLowerCase();
	        
		        if(Arrays.asList(LOCATIONS_INSIDE).contains(location)) {
		        	activity.setLocation("inside");
		        	} 
		        
		        if (Arrays.asList(LOCATIONS_OUTSIDE).contains(location)) {
		        	activity.setLocation("outside"); 
		        }
		        
		        return null; 
		        
	        } else {
	        	// Kein Ort angegeben, nachfragen
	        	activity.setLocation(null);
	        	return SpeechletResponse.newAskResponse(getPlainTextOutputSpeech(speaker.getAskForLocation()), getReprompt(getPlainTextOutputSpeech(speaker.getAskForLocationRe())));
	        }
	        
        } else {
        	activity.setLocation("inside");
        	return null; 
        }
    }
    
    // Checks if exertion was provided, if not, asks for it
    private SpeechletResponse getExertion(Intent intent, Session session) {
    	log.info("GETEXERTION");
    	
    	Slot exertionSlot = intent.getSlot("exertion");

    	// Check if activity exertion was provided
        if(exertionSlot != null && exertionSlot.getValue() != null && !exertionSlot.getValue().equalsIgnoreCase("")){
        	String exertion = exertionSlot.getValue().toLowerCase();
        
	        if(Arrays.asList(EXERTIONS_RELAXED).contains(exertion)) {
	        	activity.setExertion("relaxed");
	        } 
	        
	        if (Arrays.asList(EXERTIONS_EXHAUSTING).contains(exertion)) {
	        	activity.setExertion("exhausting");
	        }
	        
	        return null;
	        
        } else {
        	// Kein Exertion angegeben, nachfragen
        	activity.setExertion(null);
        	return SpeechletResponse.newAskResponse(getPlainTextOutputSpeech(speaker.getAskForExertion()), getReprompt(getPlainTextOutputSpeech(speaker.getAskForExertionRe())));
        }
    }
    
    // Checks if a bodypart (for the exercise) was provided, if not, asks for it
    private SpeechletResponse getBodypart(Intent intent, Session session) {
    	log.info("GETBODYPART");
    	
    	Slot bodypartSlot = intent.getSlot("bodypart");

    	
    	// Check if bodypart bodypart was provided
        if(bodypartSlot != null && bodypartSlot.getValue() != null && !bodypartSlot.getValue().equalsIgnoreCase("")){
        	String bodypart = bodypartSlot.getValue().toLowerCase();
        
	        if(Arrays.asList(BODYPARTS).contains(bodypart)) {
	        	activity.setBodypart(bodypart);
	        } 
	        
	        return null; 
	        
        } else {
        	// Kein bodypart angegeben, nachfragen
        	activity.setBodypart(null);
        	return SpeechletResponse.newAskResponse(getPlainTextOutputSpeech(speaker.getAskForBodypart()), getReprompt(getPlainTextOutputSpeech(speaker.getAskForBodypartRe())));
        }
    }
    
    
    


    
    
    //////////BUILT_IN FUNCTIONS /////////////////////////////////////////////////////////////////////////////////////////
    
    // Returns response for the help intent.
    private SpeechletResponse getHelpResponse() {
    	log.info("GETHELP");
    	
    	
    	// Tell the user what they can do 
        return getAskResponse("Trainer", speaker.getHelpText());
    }
    
    // Cancels the user request 
    private SpeechletResponse cancelRequest() {
    	log.info("CANCELREQUEST");
    	
    	
    	// Activity data
        activity.setType(null);
        activity.setLocation(null);
        activity.setExertion(null);
        activity.setBodypart(null);

        
        // Tell the user all was cancelled 
    	return SpeechletResponse.newTellResponse(getPlainTextOutputSpeech(speaker.getCancelText()));
    }

    
    
    
    
    
    
    ////////// HELPER FUNCTIONS /////////////////////////////////////////////////////////////////////////////////////////
    
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





