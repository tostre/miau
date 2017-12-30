package com.amazon.asksdk.spacegeek;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;
import java.util.Random;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.apache.commons.lang3.text.WordUtils;
import org.json.JSONArray;
import org.json.JSONObject;

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
import com.amazonaws.AmazonClientException;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;
import com.amazon.speech.ui.OutputSpeech;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Table;
import com.amazonaws.services.dynamodbv2.model.AttributeDefinition;
import com.amazonaws.services.dynamodbv2.model.KeySchemaElement;
import com.amazonaws.services.dynamodbv2.model.KeyType;
import com.amazonaws.services.dynamodbv2.model.ProvisionedThroughput;
import com.amazonaws.services.dynamodbv2.model.ScalarAttributeType;



public class SpaceGeekSpeechlet implements SpeechletV2 {
    private static final Logger log = LoggerFactory.getLogger(SpaceGeekSpeechlet.class);
    
    // Slot data
    private static final String TOPIC_SLOT = "topic";
    private static final String CITY_SLOT = "city";
    
    
    
    
    
    private String onLaunchMessage = "Hallo, wie kann ich helfen?";
    private String number = "";
    private String metric = "";
    private String metricAdj = "";
    
    
    
    
    // Speech related variables
    String speechText;
    String helpText = "Ich kann verschiedene Aktivitäten, Spiele und Übungen, drinnen oder draußen, vorschlagen";
    String taskDescription = "Hallo, ich kann Empfehlungen für eine Tagesaktivität geben";
    String askForActivityType = "Welche Art von Tätigkeit darf ich vorschlagen? Ich kann Ihnen Spiele, Übungen oder allgemeine Aktivitäten empfehlen";
    String askForGameLocation = "Möchten Sie drinnen oder draußen spielen?"; 
    String askForGameLocationReprompt = "Ich habe nichts verstanden. Darf ich Ihnen ein Spiel für drinnen oder draußen vorschlagen?"; 
    String askForGameExertion = "Möchten Sie etwas entspannendes spielen oder darf es leicht anstrengend sein?"; 
    String askForGameExertionReprompt = "Ich habe nichts verstanden. Möchten Sie etwas entspannendes oder anstrengendes spielen?"; 
    
    String askForLocation = "Möchten Sie drinnen bleiben oder rausgehen?";
    String askForLocationRe = "Ich konnte nichts verstehen. Möchten Sie heute drinnen bleiben oder rausgehen?";
    String askForExertion = "Möchten Sie etwas entspanntes oder aktives unternehmen?";
    String askForExertionRe = "Ich konnte nichts verstehen. Möchten Sie heute lieber etwas entspanntes oder aktives unternehmen?";
    
    String badWeatherInfo = "Da das Wetter heute nicht gut werden soll, empfehle ich drinnen zu bleiben. ";
    
    String[] proposals = new String[] {
    		"Wie wäre es hiermit? ", 
    		"Ich schlage Folgendes vor: ", 
    		"Das hier klingt nach einer guten Beschäftigung: "
    };
    private static final String[] GREETINGS_FORMAL = new String[] {
    		"Hallo, wie kann ich Ihnen helfen",
    		"Was kann ich für Sie tun",
    		"Kann ich Ihnen eine Aktivität für heute empfehlen",
    };
    private static final String[] GREETINGS_INFORMAL = new String[] {
    		"Hallo, wie kann ich Dir helfen",
    		"Was kann ich für Dich tun",
    		"Kann ich Dir eine Aktivität für heute empfehlen",
    };
    
    // Session data
    String requestId;
    String sessionId;
    
    // user data
    private String userNameTell = "Maria";
    private String userNameAsk = "Maria?";
    private boolean formalSpeech = true; 
    private boolean firstMeeting = true; 
    
    // Activity data
    private String activityType = "leer"; // exercise, game, activity
    private String activityExertion = "leer"; // relaxed, exhausting
    private String activityLocation = "leer"; // inside, outside, both
    private String activityBodyPart = "leer";
    private boolean activityWithFriends = false; // true, false
    private ArrayList<String> activityExcludeBodypart = new ArrayList<>(); // Exclude activities that put strain on these body parts
    private ArrayList<String> activityIncludeBodypart = new ArrayList<>(); // Choose activities that include these body parts
    
    // Actity data set 
    private boolean activityTypeSet; 
    private boolean activityExertionSet; 
    private boolean activityLocationSet; 
    private boolean activityBodypartSet;
    private boolean activityWithFriendsSet; 
    private boolean activityExcludeBodypartSet; 
    private boolean activityIncludeBodypartSet; 
    private boolean weatherSet; 
    
    // weather data
    private String fetchWeatherUrl = "http://api.openweathermap.org/data/2.5/weather?id=2935517&APPID=44956c0ccd5905a239c4ee266863eb06";
    private String weatherDescription;
    private boolean goodWeather;
    
    // database and storage data
    private AmazonDynamoDBClient dbclient;
    static AmazonDynamoDB dynamoDB;
    private Session session; 
    
    // Array mit Fakten
    private static final String[] SPACE_FACTS = new String[] {
            "Ein Jahr auf dem Merkur ist 88 Tage lang.",
            "Venus rotiert gegen den Uhrzeigersinn.",
            "Die Erde ist als einziger Planet nicht nach einem Gott benannt."
    };
    private static final String[] CARS_FACTS = new String[] {
            "Ein Auto fährt.",
            "Autos verbrauchen Benzin.",
            "Autos haben vier Räder."
    };
    
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
    		"draußen",
    		"nach draußen",
    		"raus",
    		"außer Haus",
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
    
    // DB emulator (game)
    String[] gameInsideRelaxedDb = new String[] {
    		"drinnen etwas entspanntes spielen eins",
    		"drinnen etwas entspanntes spielen zwei"
    };
	String[] gameOutsideRelaxedDb = new String[] {
	    	"draußen etwas entspanntes spielen eins",
	    	"draußen etwas entspanntes spielen zwei"
	};
	String[] gameOutsideExhaustingDb = new String[] {
    		"draußen etwas anstrengendes spielen eins",
    		"draußen etwas anstrengendes spielen zwei"
	};    
	
	// DB emulator activity
    String[] activityInsideRelaxedDb = new String[] {
    		"drinnen etwas entspanntes tun eins",
    		"drinnen etwas entspanntes tun zwei"
    };
    String[] activityInsideExhaustingDb = new String[] {
    		"draußen etwas anstrengendes tun eins",
    		"draußen etwas anstrengendes tun zwei"
	};
	String[] activityOutsideRelaxedDb = new String[] {
	    	"draußen etwas entspanntes tun eins",
	    	"draußen etwas entspanntes tun zwei"
	};
	String[] activityOutsideExhaustingDb = new String[] {
    		"draußen etwas anstrengendes tun eins",
    		"draußen etwas anstrengendes tun zwei"
	};  
    
    // DB emulator exercise
	String[] exerciseInsideRelaxedDb = new String[] {
    		"drinnen etwas entspanntes trainieren eins",
    		"drinnen etwas entspanntes trainieren zwei"
    };
    String[] exerciseInsideExhaustingDb = new String[] {
    		"draußen etwas anstrengendes trainieren eins",
    		"draußen etwas anstrengendes trainieren zwei"
	};
	String[] exerciseOutsideRelaxedDb = new String[] {
	    	"draußen etwas entspanntes trainieren eins",
	    	"draußen etwas entspanntes trainieren zwei"
	};
	String[] exerciseOutsideExhaustingDb = new String[] {
    		"draußen etwas anstrengendes trainieren eins",
    		"draußen etwas anstrengendes trainieren zwei"
	};  
    
    
    
    
    
    ////////// LIFECYCLE METHODS /////////////////////////////////////////////////////////////////////////////////////////
    
    public SpeechletResponse onLaunch(SpeechletRequestEnvelope<LaunchRequest> requestEnvelope) {
        session = requestEnvelope.getSession();
    	
    	if(formalSpeech) {
        	speechText = GREETINGS_FORMAL[(int) Math.floor(Math.random() * GREETINGS_FORMAL.length)];
        } else {
        	speechText = GREETINGS_INFORMAL[(int) Math.floor(Math.random() * GREETINGS_INFORMAL.length)];
        }
    	
    	// Create the Simple card content.
        SimpleCard card = getSimpleCard("exEins", taskDescription);
        // Create the plain text output.
        PlainTextOutputSpeech speech = getPlainTextOutputSpeech(speechText);

        return SpeechletResponse.newTellResponse(speech, card);
    }

    public void onSessionStarted(SpeechletRequestEnvelope<SessionStartedRequest> requestEnvelope) {
        log.info("onSessionStarted requestId={}, sessionId={}", requestEnvelope.getRequest().getRequestId(), requestEnvelope.getSession().getSessionId());
        
        
        initializeComponents(requestEnvelope);
    }
    
    public void onSessionEnded(SpeechletRequestEnvelope<SessionEndedRequest> requestEnvelope) {
        log.info("onSessionEnded requestId={}, sessionId={}", requestEnvelope.getRequest().getRequestId(),
                requestEnvelope.getSession().getSessionId());
        // any cleanup logic goes here
    }
    
    public SpeechletResponse onIntent(SpeechletRequestEnvelope<IntentRequest> requestEnvelope) {
        IntentRequest request = requestEnvelope.getRequest();
        session = requestEnvelope.getSession();
        
        log.info("onIntent: START");
        log.info("onIntent requestId={}, sessionId={}", request.getRequestId()  , requestEnvelope.getSession().getSessionId());
        //getWeather();
        
        Intent intent = request.getIntent();
        String intentName = (intent != null) ? intent.getName() : null;
        PlainTextOutputSpeech outputSpeech;
        
        if(!activityTypeSet) {
        	switch(intentName) {
            case "GetGameIntent":
            	return getGame(intent, session);
        	case "GetExerciseIntent":
            	return getExercise(intent);
            case "SetLocationIntent":
            	getLocation(intent, session);
            case "SetExertionIntent":
            	getExertion(intent, session);
            // Built-in intents
            case "AMAZON.HelpIntent":
        		return getHelpResponse();
        	case "AMAZON.StopIntent":
        		outputSpeech = new PlainTextOutputSpeech();
                outputSpeech.setText("Tschüss");
                return SpeechletResponse.newTellResponse(outputSpeech);
        	case "AMAZON.CancelIntent": 
        		outputSpeech = new PlainTextOutputSpeech();
                outputSpeech.setText("Tschüss");
                return SpeechletResponse.newTellResponse(outputSpeech);
            default: 
            	return getAskResponse("Gino", "Das habe ich leider nicht verstanden. Sie können mich nach einem Spiel oder einer Übung fragen");
        	}
        } else {
        	switch(activityType) {
        	case "game":
        		return getGame(intent, session);
        	case "exercise":
        		return getExercise(intent);
        	default: 
        		return getAskResponse("Gino", "Aktivitätstyp: " + activityType + "Typ gesetzt. Das habe ich leider nicht verstanden.");
        	}
        }
        
        /*
        switch(intentName) {
        case "GetStatusIntent": 
        	
        case "GetNewFactIntent":
        	return getNewFactResponse(intent);
        case "RepeatIntent":
        	return repeat(intent);
        case "SetFormalSpeechIntent":
        	return setFormalSpeech(intent); 
        case "SetNameIntent":
        	return setName(intent);
        case "GetExerciseIntent":
        	return getExercise(intent);
        case "GetGameIntent":
        	return getGame(intent);
        case "SetLocationIntent":
        	//return getLocation(intent);
        	getLocation(intent, "");
    	case "AMAZON.HelpIntent":
    		return getHelpResponse();
    	case "AMAZON.StopIntent":
    		outputSpeech = new PlainTextOutputSpeech();
            outputSpeech.setText("Tschüss");
            return SpeechletResponse.newTellResponse(outputSpeech);
    	case "AMAZON.CancelIntent": 
    		outputSpeech = new PlainTextOutputSpeech();
            outputSpeech.setText("Tschüss");
            return SpeechletResponse.newTellResponse(outputSpeech);
        default: 
        	return getAskResponse("Gino", "Das habe ich leider nicht verstanden.");
        }*/
    }
    
    
    
    
    
    
    
    
    
    
	////////// SETUP AND USER DATA METHODS /////////////////////////////////////////////////////////////////////////////////////////
	    
	private SpeechletResponse firstSetup() {
		return SpeechletResponse.newTellResponse(getPlainTextOutputSpeech("Platzhalter"));
	}
	
	private void initializeComponents(SpeechletRequestEnvelope<SessionStartedRequest> requestEnvelope) {
	    	requestId = requestEnvelope.getRequest().getRequestId();
	        sessionId = requestEnvelope.getSession().getSessionId();
	        //goodWeather = getWeather(); 
	    }
	
	// Change or set name for the first time
	private SpeechletResponse setName(Intent intent) {
		// Get the slots from the intent.
	 Map<String, Slot> slots = intent.getSlots();
	 String username = slots.get("username").getValue();
	
	 userNameTell = username + "!";
	 userNameAsk = username + "?";
	 
	 if(firstMeeting) {
	 	return SpeechletResponse.newTellResponse(getPlainTextOutputSpeech("Nett Dich kennen zu lernen " + userNameTell));
	 } else {
	 	return SpeechletResponse.newTellResponse(getPlainTextOutputSpeech("Verstanden " + userNameTell));
	 }
	}
	
	// Let's the user choose if they want to be "geduzt" or "gesiezt"
	private SpeechletResponse setFormalSpeech(Intent intent) {
		// Get the slots from the intent.
	 Map<String, Slot> slots = intent.getSlots();
	 Slot formalSpeechStyle = slots.get("formalSpeechType");
	 
	 String style = formalSpeechStyle.getValue();
	 
	 if(style.equals("siezen")) {
	 	formalSpeech = true; 
	 	return SpeechletResponse.newTellResponse(getPlainTextOutputSpeech("Verstanden, ich werde Sie ab jetzt Siezen " + userNameTell));
	 } else {
	 	formalSpeech = false;
	 	return SpeechletResponse.newTellResponse(getPlainTextOutputSpeech("Verstanden, ich werden Sie ab jetzt Duzen " + userNameTell));
	 }
	}










	////////// CORE FUNCTIONS /////////////////////////////////////////////////////////////////////////////////////////
    

    private SpeechletResponse getGame(Intent intent, Session session) {// Check if activity location was provided
    	
    	// Initialize params
    	//session.setAttribute("activityType", "game");
    	activityType = "game";
    	activityTypeSet = true; 
        SpeechletResponse locationOutput = null; 
        SpeechletResponse exertionOutput = null;
        
        
        
        // Check if location and exertion were provided as slots
        if(session.getAttributes().containsKey("activityLocationSet")) {
        	if(!(Boolean) session.getAttribute("activityLocationSet")) {
        		locationOutput = getLocation(intent, session);
        	}
        	
        } else {
        	session.setAttribute("activityLocationSet", false);
        	locationOutput = getLocation(intent, session);
        }
        
        if(session.getAttributes().containsKey("activityExertionSet")) {
        	if(!(Boolean) session.getAttribute("activityExertionSet")) {
        		exertionOutput = getExertion(intent, session);
        	}
        } else {
        	session.setAttribute("activityExertionSet", false);
        	exertionOutput = getExertion(intent, session);
        }
        
        
        
        // Ask for missing params
        if(locationOutput != null) {
	        return locationOutput; 
	    }
        if(exertionOutput != null) {
	        return exertionOutput; 
        }
        
        
        
        // Gets a game from the db according to set params
        if(!goodWeather) {
        	return fetchFromDb(badWeatherInfo, activityType, (String) session.getAttribute("activityLocation"), (String) session.getAttribute("activityExertion"));
        } else {
        	return fetchFromDb("", activityType, (String) session.getAttribute("activityLocation"), (String) session.getAttribute("activityExertion"));
        }
    }
    
    // Returns an exercise for the user 
    private SpeechletResponse getExercise(Intent intent) {
    	return SpeechletResponse.newTellResponse(getPlainTextOutputSpeech("Platzhalter"));
    }
    
    // Returns an (non-game, non-exercise) activity for the user 
    private SpeechletResponse getActivity(Intent intent) {
    	return SpeechletResponse.newTellResponse(getPlainTextOutputSpeech("Platzhalter"));
    }
    
    
    
    
    
    // Checks if location was provided, if not, asks for it
    private SpeechletResponse getLocation(Intent intent, Session session) {
    	Slot locationSlot = intent.getSlot("location");
    	
    	if(goodWeather) {
        	// Check if activity location was provided
	        if(locationSlot != null && locationSlot.getValue() != null && !locationSlot.getValue().equalsIgnoreCase("")){
	        	String location = locationSlot.getValue().toLowerCase();
	        
		        if(Arrays.asList(LOCATIONS_INSIDE).contains(location)) {
		        	session.setAttribute("activityLocationSet", true);
		        	session.setAttribute("activityLocation", "inside");
		        	//activityLocationSet = true;
		        	//activityLocation = "inside";
		        } 
		        
		        if (Arrays.asList(LOCATIONS_OUTSIDE).contains(location)) {
		        	session.setAttribute("activityLocationSet", true);
		        	session.setAttribute("activityLocation", "outside");
		        	//activityLocationSet = true;
		        	//activityLocation = "outside";   
		        }
		        
	        } else {
	        	// Kein Ort angegeben, nachfragen
	        	//activityLocationSet = false;
	        	session.setAttribute("activityLocationSet", false);
	        	return SpeechletResponse.newAskResponse(getPlainTextOutputSpeech(askForLocation), getReprompt(getPlainTextOutputSpeech(askForLocationRe)));
	        }
	        
        } else {
        	//activityLocationSet = true; 
        	session.setAttribute("activityLocationSet", true);
        	session.setAttribute("activityLocation", "inside");
        	//activityLocation = "inside";
        	return null; 
        }
		return null;
    }
    
    // Checks if exertion was provided, if not, asks for it
    private SpeechletResponse getExertion(Intent intent, Session session) {
    	Slot exertionSlot = intent.getSlot("exertion");

    	if(goodWeather) {
        	// Check if activity exertion was provided
	        if(exertionSlot != null && exertionSlot.getValue() != null && !exertionSlot.getValue().equalsIgnoreCase("")){
	        	String exertion = exertionSlot.getValue().toLowerCase();
	        	log.info("getExertion: " + exertion);
	        
		        if(Arrays.asList(EXERTIONS_RELAXED).contains(exertion)) {
		        	session.setAttribute("activityExertionSet", true);
		        	session.setAttribute("activityExertion", "relaxed");
		        	//activityExertionSet = true;
		        	//activityExertion = "relaxed";
		        } 
		        
		        if (Arrays.asList(EXERTIONS_EXHAUSTING).contains(exertion)) {
		        	session.setAttribute("activityExertionSet", true);
		        	session.setAttribute("activityExertion", "exhausting");
		        	//activityExertionSet = true;
		        	//activityExertion = "exhausting";   
		       
		        }
		        
	        } else {
	        	// Kein Exertion angegeben, nachfragen
	        	session.setAttribute("activityExertionSet", false);
	        	//activityExertionSet = false;
	        	return SpeechletResponse.newAskResponse(getPlainTextOutputSpeech(askForExertion), getReprompt(getPlainTextOutputSpeech(askForExertionRe)));
	        }
	        
        } else {
        	session.setAttribute("activityExertionSet", true);
        	session.setAttribute("activityExertion", "relaxed");
        	//activityExertionSet = true; 
        	//activityExertion = "relaxed";
        	return null; 
        }
		return null;
    }
    
    // Returns a game for the user 
    private SpeechletResponse setActivityType(Intent intent) {
		log.info("setActivityIntent");
    	return SpeechletResponse.newTellResponse(getPlainTextOutputSpeech("Platzhalter: Welche Art Aktivität darf es sein?"));
    }
    
    // Checks if a bodypart (for the exercise) was provided, if not, asks for it
    private SpeechletResponse getBodypart(Intent intent, Session session) {
    	Slot exertionSlot = intent.getSlot("exertion");

    	if(goodWeather) {
        	// Check if activity exertion was provided
	        if(exertionSlot != null && exertionSlot.getValue() != null && !exertionSlot.getValue().equalsIgnoreCase("")){
	        	String exertion = exertionSlot.getValue().toLowerCase();
	        	log.info("getExertion: " + exertion);
	        
		        if(Arrays.asList(EXERTIONS_RELAXED).contains(exertion)) {
		        	session.setAttribute("activityExertionSet", true);
		        	session.setAttribute("activityExertion", "relaxed");
		        	//activityExertionSet = true;
		        	//activityExertion = "relaxed";
		        } 
		        
		        if (Arrays.asList(EXERTIONS_EXHAUSTING).contains(exertion)) {
		        	session.setAttribute("activityExertionSet", true);
		        	session.setAttribute("activityExertion", "exhausting");
		        	//activityExertionSet = true;
		        	//activityExertion = "exhausting";   
		       
		        }
		        
	        } else {
	        	// Kein Exertion angegeben, nachfragen
	        	session.setAttribute("activityExertionSet", false);
	        	//activityExertionSet = false;
	        	return SpeechletResponse.newAskResponse(getPlainTextOutputSpeech(askForExertion), getReprompt(getPlainTextOutputSpeech(askForExertionRe)));
	        }
	        
        } else {
        	session.setAttribute("activityExertionSet", true);
        	session.setAttribute("activityExertion", "relaxed");
        	//activityExertionSet = true; 
        	//activityExertion = "relaxed";
        	return null; 
        }
		return null;
    }
    
    
    
    
    private SpeechletResponse fetchFromDb(String additionalInfo, String activityType, String activityLocation, String activityExertion) {
    	// Get game from DB according to set parameters
        String activity = "Schach spielen";
        
        // Get the fitting db table/array from the user-set parmas
        String dbName = activityType + WordUtils.capitalize(activityLocation) + WordUtils.capitalize(activityExertion) + "Db";
        int random; 
        
        // Get entry from db
        switch (dbName) {
        case "gameInsideRelaxedDb":
        	random = new Random().nextInt(gameInsideRelaxedDb.length);
        	activity = gameInsideRelaxedDb[random];
        	break; 
        case "gameOutsideRelaxedDb":
        	random = new Random().nextInt(gameOutsideRelaxedDb.length);
        	activity = gameOutsideRelaxedDb[random];
        	break;
        case "gameOutsideExhaustingDb":
        	random = new Random().nextInt(gameOutsideExhaustingDb.length);
        	activity = gameOutsideExhaustingDb[random];
        	break; 
        case "activityInsideRelaxedDb":
        	random = new Random().nextInt(activityInsideRelaxedDb.length);
        	activity = activityInsideRelaxedDb[random];
        	break;
        case "activityInsideExhaustingDb":
        	random = new Random().nextInt(activityInsideExhaustingDb.length);
        	activity = activityInsideExhaustingDb[random];
        	break;
        case "activityOutsideRelaxedDb":
        	random = new Random().nextInt(activityOutsideRelaxedDb.length);
        	activity = activityOutsideRelaxedDb[random];
        	break; 
        case "activityOutsideExhaustingDb":
        	random = new Random().nextInt(activityOutsideExhaustingDb.length);
        	activity = activityOutsideExhaustingDb[random];
        	break;
        case "exerciseInsideRelaxedDb":
        	random = new Random().nextInt(exerciseInsideRelaxedDb.length);
        	activity = exerciseInsideRelaxedDb[random];
        	break;
        case "exerciseInsideExhaustingDb":
        	random = new Random().nextInt(exerciseInsideExhaustingDb.length);
        	activity = exerciseInsideExhaustingDb[random];
        	break; 
        case "exerciseOutsideRelaxedDb":
        	random = new Random().nextInt(exerciseOutsideRelaxedDb.length);
        	activity = exerciseOutsideRelaxedDb[random];
        	break;
        case "exerciseOutsideExhaustingDb":
        	random = new Random().nextInt(exerciseOutsideExhaustingDb.length);
        	activity = exerciseOutsideExhaustingDb[random];
        	break;
        }
        
        
        
        // Generate output string
        random = new Random().nextInt(proposals.length);
        String proposal = proposals[random] + activity;
        
        // Debug
        //String location = (String) session.getAttribute("activityLocation");
        //String exertion = (String) session.getAttribute("activityExertion");
        //return SpeechletResponse.newTellResponse(getPlainTextOutputSpeech("Ort: " + location + ". Anstrengung: " + exertion));
        
        return SpeechletResponse.newTellResponse(getPlainTextOutputSpeech(additionalInfo + proposal));
        
    }
    

    
    
    
    
    
    ////////// ADDITIONAL FUNCTIONS /////////////////////////////////////////////////////////////////////////////////////////
    
    // Fetch weather data and get weather description and if weather is good enough for going out
    private boolean getWeather(){
    	weatherDescription = "Ich konnte keine Daten zum jetzigen Wetter finden";
    	int weatherId = 0; 
    	
        // Fetch weather data from openWeatherMap
		try {
			InputStream inputStream = new URL(fetchWeatherUrl).openStream();
			BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));
	        StringBuilder sb = new StringBuilder();
	        String line = "";
	        
	        // Build String from weather data json
	        while ((line = br.readLine()) != null) {
            	sb.append(line);
	        }
	        
	        inputStream.close();
	        // Get weather id from weather data
	        JSONObject json = new JSONObject(sb.toString());
	        JSONArray jsonArray = json.getJSONArray("weather");
	        JSONObject currentWeather = jsonArray.getJSONObject(0);
	        weatherId = currentWeather.getInt("id");
		} catch (IOException e) {e.printStackTrace();}
		
        // Set weather attributes 
		if(weatherId >= 200 && weatherId < 300) {
			// Gewitter
			weatherDescription = "gewittert es";
			weatherSet = true; 
			return goodWeather = false; 
		} else if(weatherId >= 300 && weatherId < 600) {
			// leichter Regen
			weatherDescription = "regnet es";
			weatherSet = true; 
			return goodWeather = false; 
		} else if(weatherId >= 600 && weatherId < 700) {
			// Schnee
			weatherDescription = "schneit es";
			weatherSet = true; 
			return goodWeather = false; 
		} else if(weatherId >= 700 && weatherId < 800) {
			// Atmosphäre
			weatherDescription = "ist es neblig oder rauchig";
			weatherSet = true; 
			return goodWeather = false; 
		} else if(weatherId == 800) {
			// Klar
			weatherDescription = "scheint die Sonne";
			weatherSet = true; 
			return goodWeather = true; 
		} else if(weatherId >= 801 && weatherId < 900) {
			// Bewölkt
			weatherDescription = "ist es bewölkt";
			weatherSet = true; 
			return goodWeather = true; 
		} else if(weatherId >= 900 && weatherId < 910) {
			// Extrem
			weatherDescription = "ist es wegen der Wetterbedingungen gefährlich raus zu gehen";
			weatherSet = true; 
			return goodWeather = false; 
		} else if (weatherId >= 951 && weatherId < 954) {
			// Leichte Brise
			weatherDescription = "weht eine leichte Brise";
			weatherSet = true; 
			return goodWeather = true; 
		} else {
			// Sehr windig
			weatherDescription = "weht der Wind sehr stark";
			weatherSet = true; 
			return goodWeather = false; 
		}
    }
    
    ////////// TEST FUNCTIONS /////////////////////////////////////////////////////////////////////////////////////////
    
    // Repeats a number and a metric
    private SpeechletResponse repeat(final Intent intent) {
    	// Get the slots from the intent.
        Map<String, Slot> slots = intent.getSlots();
        Slot numberSlot = slots.get("number");
        Slot metricSlot = slots.get("metric");
        Slot metricAdjSlot = slots.get("metricAdj");
        
        number = numberSlot.getValue();
        metric = metricSlot.getValue();
        metricAdj = metricAdjSlot.getValue();
    	
        String speechText = "Ok, bis dann!";
        
        if(number != "" && metric != "" && metricAdj != "") {
        	if(number != null && metric != null  && metricAdj != null ) {
        		speechText = "Ok, ich habe " + number + " " + metric + " " + metricAdj + " verstanden";
        	}
        	
        }
    	
        // Create the plain text output.
        PlainTextOutputSpeech speech = getPlainTextOutputSpeech(speechText);
        //return SpeechletResponse.newTellResponse(speech, card);
        return SpeechletResponse.newTellResponse(speech);
        
    }

    // Gets random new fact from the list and returns to user.
    private SpeechletResponse getNewFactResponse(final Intent intent) {
    	// Get the slots from the intent.
        Map<String, Slot> slots = intent.getSlots();
        Slot topicSlot = slots.get(TOPIC_SLOT);
        String speechText = "Das ist der Standardtext"; 
        String repromptText = "Das habe ich nicht verstanden";
        String fact; 
        String topic; 
        int factIndex; 
        
        if(topicSlot != null && topicSlot.getValue() != null && !topicSlot.getValue().equalsIgnoreCase("")){
        	topic = topicSlot.getValue();
        	
        	switch(topic){
        	case "autos":
        		// Get a random space fact from the space facts list
            	factIndex = (int) Math.floor(Math.random() * CARS_FACTS.length);
            	fact = CARS_FACTS[factIndex];
            	speechText = "Hier ist ein Fakt über Autos: " + fact;
        		break;
        	case "weltraum":
        		factIndex = (int) Math.floor(Math.random() * SPACE_FACTS.length);
	        	fact = SPACE_FACTS[factIndex];
	        	speechText = "Hier ist ein Fakt über den Weltraum: " + fact;
        		break;
    		default: 
    			speechText = "Zur Zeit " + getWeather();
    			break; 
            
            }
        } else {
        	speechText = "Zur Zeit " + getWeather();
        }
        
        	
        // Create the Simple card content.
        SimpleCard card = getSimpleCard("SpaceGeek", speechText);
        // Create the plain text output.
        PlainTextOutputSpeech speech = getPlainTextOutputSpeech(speechText);

        return SpeechletResponse.newTellResponse(speech, card);
    }

    //////////BUILT_IN FUNCTIONS /////////////////////////////////////////////////////////////////////////////////////////
    
    // Returns response for the help intent.
    private SpeechletResponse getHelpResponse() {
        String speechText =
                "You can ask Space Geek tell me a space fact, or, you can say exit. What can I "
                        + "help you with?";
        return getAskResponse("SpaceGeek", speechText);
    }

    // Creates a card object.
    // @param title title of the card
    // @param content body of the card
    // @return SimpleCard the display card to be sent along with the voice response.
    private SimpleCard getSimpleCard(String title, String content) {
        SimpleCard card = new SimpleCard();
        card.setTitle(title);
        card.setContent(content);

        return card;
    }
    
    ////////// HELPER FUNCTIONS /////////////////////////////////////////////////////////////////////////////////////////
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
