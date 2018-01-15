package com.amazon.asksdk.spacegeek;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

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
import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazon.speech.ui.OutputSpeech;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.document.Table;
import com.amazonaws.services.dynamodbv2.model.AttributeDefinition;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.CreateTableRequest;
import com.amazonaws.services.dynamodbv2.model.KeySchemaElement;
import com.amazonaws.services.dynamodbv2.model.KeyType;
import com.amazonaws.services.dynamodbv2.model.ProvisionedThroughput;
import com.amazonaws.services.dynamodbv2.model.ScalarAttributeType;
import com.amazonaws.services.dynamodbv2.model.TableDescription;



public class SpaceGeekSpeechlet implements SpeechletV2 {
    private static final Logger log = LoggerFactory.getLogger(SpaceGeekSpeechlet.class);
    
    // Slot data
    private static final String TOPIC_SLOT = "topic";
    
    
    
    
    
   
    
    
    
    
    
    // Speech related variables
    String cancelText = "In Ordnung, ich vergesse was bisher gesagt wurde. Wir können nun von Neuem beginnen ";
    String goodbyeText = "Bis zum nächsten Mal ";
    String badWeatherInfo = "Ich würde daher empfehlen drinnen zu bleiben. ";
    String greetingText = "Hallo, fragen Sie mich nach einem Spiel, einer Übung oder einer Beschäftigung ";
    String introductionText = "Hallo, freut mich Sie kennenzulernen. Ich kann Ihnen verschiedene Spiele, Übungen oder Beschäftigungen vorschlagen. Zuerst muss ich Ihnen aber ein paar Fragen stellen, um Sie besser kennenzulernen. ";
    
    String helpText = "";
    String helpTextF = "Ich kann verschiedene Aktivitäten, Spiele und Übungen, drinnen oder draußen, vorschlagen. Fragen Sie mich einfach nach einem Spiel, einer Übung oder einer Beschäftigung. Alles Weitere klären wir darauf gemeinsam ";
    String helpTextI = "Ich kann verschiedene Aktivitäten, Spiele und Übungen, drinnen oder draußen, vorschlagen. Frag mich einfach nach einem Spiel, einer Übung oder einer Beschäftigung. Alles Weitere klären wir darauf gemeinsam ";
    
    String errorText = "";
    String errorTextF = "Es tut mir leid, etwas ist schief gegangen. Fangen Sie bitte noch einmal von vorne an ";
    String errorTextI = "Es tut mir leid, etwas ist schief gegangen. Fang bitte noch einmal von vorne an ";
    
    String askForLocation = "";
    String askForLocationF = "Möchten Sie heute drinnen bleiben oder rausgehen? ";
    String askForLocationI = "Möchtest Du heute drinnen bleiben oder rausgehen? ";
    String askForLocationRe = "";
    String askForLocationReF = "Es tut mir leid, ich konnte nichts verstehen. Möchten Sie heute drinnen bleiben oder rausgehen? ";
    String askForLocationReI = "Es tut mir leid, ich konnte nichts verstehen. Möchtest Du heute drinnen bleiben oder rausgehen? ";
    
    String askForExertion = "";
    String askForExertionF = "Möchten Sie etwas entspannendes oder aktives unternehmen? ";
    String askForExertionI = "Möchtest Du etwas entspannendes oder aktives unternehmen? ";
    String askForExertionRe = ""; 
    String askForExertionReF = "Es tut mir leid, ich konnte nichts verstehen. Möchten Sie etwas entspannendes oder aktives unternehmen? ";
    String askForExertionReI = "Es tut mir leid, ich konnte nichts verstehen. Möchtest Du etwas entspannendes oder aktives unternehmen? ";
    
    String askForBodypart = "";
    String askForBodypartF = "Welchen Körperteil möchten Sie traineren? ";
    String askForBodypartI = "Welchen Körperteil möchtest Du traineren? ";
    String askForBodypartRe = ""; 
    String askForBodypartReF = "Es tut mir leid, ich konnte nichts verstehen. Welchen Körperteil möchten Sie traineren? ";
    String askForBodypartReI = "Es tut mir leid, ich konnte nichts verstehen. Welchen Körperteil möchtest Du traineren? ";
    
    String askForName = "";
    String askForNameF = "Wie heißen Sie mit Vornamen? ";
    String askForNameI = "Wie heißt Du mit Vornamen? ";
    String askForNameRe = "";
    String askForNameReF = "Es tut mir leid, ich konnte nichts verstehen. Wie heißen Sie mit Vornamen? ";
    String askForNameReI = "Es tut mir leid, ich konnte nichts verstehen. Wie heißt Du mit Vornamen? ";
    
    String askForSpeechStyle = "Möchten Sie gesiezt oder geduzt werden? ";
    String askForSpeechStyleRe = "Es tut mir leid, ich konnte nichts verstehen. Möchten Sie gesiezt oder geduzt werden? ";
    
    String confirmSpeechStyle = "";
    String confirmSpeechStyleF = "In Ordnung, ich werde Sie ab jetzt Siezen ";
    String confirmSpeechStyleI = "In Ordnung, ich werde Dich ab jetzt Duzen ";
    
    String[] proposals = new String[] {
    		"Wie wäre es hiermit? ", 
    		"Ich schlage Folgendes vor: ", 
    		"Das hier klingt nach einer guten Beschäftigung: "
    };
    private static final String[] GREETINGS_FORMAL = new String[] {
    		"Hallo, wie kann ich Ihnen helfen ",
    		"Was kann ich für Sie tun ",
    		"Kann ich Ihnen eine Aktivität für heute empfehlen ",
    };
    private static final String[] GREETINGS_INFORMAL = new String[] {
    		"Hallo, wie kann ich Dir helfen ",
    		"Was kann ich für Dich tun ",
    		"Kann ich Dir eine Aktivität für heute empfehlen ",
    };
    
    // Session data
    String requestId;
    String sessionId;
    
    // user data
    private User user; 
    //private boolean userNameSet = false; 
    //private boolean formalSpeech = true; 
    //private boolean speechStyleSet = false; 
    //private boolean excludedBodypartsSet = false; 
    //private boolean firstSetupComplete; 
    //private boolean introductionHeard = false; 
    //private ArrayList<String> EXCLUDED_BODYPARTS = new ArrayList<String>();
    private String userId = ""; 
    private Activity activity; 
    private int random; 
    
    // Activity data
    //private String activityType = "leer"; // exercise, game, activity
    //private String activityExertion = "leer"; // relaxed, exhausting
    //private String activityLocation = "leer"; // inside, outside, both
    //private String activityBodyPart = "leer";
    
    // Actity data set 
    //private boolean activityTypeSet; 
    //private boolean activityExertionSet; 
    //private boolean activityLocationSet; 
    //private boolean activityBodypartSet;
    private WeatherProvider weatherProvider; 
    
    // weather data
    private String fetchWeatherUrl = "http://api.openweathermap.org/data/2.5/weather?id=2935517&APPID=44956c0ccd5905a239c4ee266863eb06";
    private String weatherDescription;
    private boolean goodWeather = true;
    
    // database and storage data
    private Session session; 
    private Database database; 
    private static final String usersTable = "exEinsUsers";
    private static final String gamesTable = "exEinsGames";
    private static final String exercisesTable = "exEinsExercises";
    private static final String occupationsTable = "exEinsOccupations";
    
    // Array mit Fakten
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
    		"übung",
    		"üben",
    		"training",
    		"traineren"
    };
    private static final String[] ACTIVITY_TYPE_OCCUPATION = new String[] {
    		"beschäftigung",
    		"tätigkeit",
    		"allgemeine beschäftigung",
    		"allgemeine tätigket",
    		"normale beschäftigung",
    		"normale tätigkeit"
    };
    private static final String[] ACTIVITY_TYPE_ACTIVITY = new String[] {
    		"beschäftigung",
    		"tätigkeit",
    		"allgemeine beschäftigung",
    		"allgemeine tätigket",
    		"normale beschäftigung",
    		"normale tätigkeit"
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
	    	"draußen etwas entspanntes spielen eins",
	    	"draußen etwas entspanntes spielen zwei"
	};
	String[] gameOutsideExhaustingDb = new String[] {
    		"draußen etwas anstrengendes spielen eins",
    		"draußen etwas anstrengendes spielen zwei"
	};    
	
	// DB emulator activity
    String[] occupationInsideRelaxedDb = new String[] {
    		"drinnen etwas entspanntes tun eins",
    		"drinnen etwas entspanntes tun zwei"
    };
    String[] occupationInsideExhaustingDb = new String[] {
    		"draußen etwas anstrengendes tun eins",
    		"draußen etwas anstrengendes tun zwei"
	};
	String[] occupationOutsideRelaxedDb = new String[] {
	    	"draußen etwas entspanntes tun eins",
	    	"draußen etwas entspanntes tun zwei"
	};
	String[] occupationOutsideExhaustingDb = new String[] {
    		"draußen etwas anstrengendes tun eins",
    		"draußen etwas anstrengendes tun zwei"
	};  
    
    // DB emulator exercise
    String[] exerciseInsideExhaustingDb = new String[] {
    		"draußen etwas trainieren eins",
    		"draußen etwas trainieren zwei"
	};
	String[] exerciseOutsideExhaustingDb = new String[] {
    		"draußen etwas trainieren eins",
    		"draußen etwas trainieren zwei"
	};  
    
    
    
    
    
    ////////// LIFECYCLE METHODS /////////////////////////////////////////////////////////////////////////////////////////
    
	// Called when application is launched from keyword ("Starte Fakten")
    public SpeechletResponse onLaunch(SpeechletRequestEnvelope<LaunchRequest> requestEnvelope) {
    	log.info("ONLAUNCH");
        /*session = requestEnvelope.getSession();
        userId = session.getUser().getUserId();
        goodWeather = getWeather(); 
        // TODO: Remove this line
	    goodWeather = true; 
	    
	    initDb(); 
        initUserData(); 
        initSpeech();*/
        return SpeechletResponse.newTellResponse(getPlainTextOutputSpeech(greetingText));
    }

    // Ka, wann das gestartet wird 
    public void onSessionStarted(SpeechletRequestEnvelope<SessionStartedRequest> requestEnvelope) {
    	log.info("ONSESSIONSTARTED");
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
    	/*IntentRequest request = requestEnvelope.getRequest();
        session = requestEnvelope.getSession();
        userId = session.getUser().getUserId();
    	log.info("ONINTENT");
    	log.info("ONINTENT" + userId);
        log.info("onIntent requestId={}, sessionId={}", request.getRequestId()  , requestEnvelope.getSession().getSessionId());
    	
        goodWeather = getWeather(); 
        // TODO: Remove this line
	    goodWeather = true; 
	    
	    initDb(); 
        initUserData(); 
        initSpeech();
        
        Intent intent = request.getIntent();
        String intentName = (intent != null) ? intent.getName() : null;
        
        // This is the start of a user request
        if(!user.isSetupComplete()) {
        	return firstSetup(intent); 
        } else {
        	if(!activityTypeSet) {
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
                return SpeechletResponse.newTellResponse(getPlainTextOutputSpeech(goodbyeText));
        	case "AMAZON.CancelIntent": 
                return cancelRequest();
            default: 
            	String output = "Das habe ich leider nicht verstanden. " + helpText;
            	return SpeechletResponse.newAskResponse(getPlainTextOutputSpeech(output), getReprompt(getPlainTextOutputSpeech(output)));
        	}
        // Activity type already set, meaning this intent is for a follow-up question by alexa
	        } else {
	        	switch(activityType) {
	        	case "game":
	        		return getGame(intent, session);
	        	case "exercise":
	        		return getExercise(intent, session);
	        	case "activity": 
	        		return getOccupation(intent, session);
	        	default: 
	        		return SpeechletResponse.newTellResponse(getPlainTextOutputSpeech(errorText));
	        	}
	        }
        }*/
        
        return SpeechletResponse.newTellResponse(getPlainTextOutputSpeech(errorText));
    }
    
    
    
    
    
	////////// SETUP AND USER DATA METHODS /////////////////////////////////////////////////////////////////////////////////////////
	
    private void init(Session session) {
    	// Ablauf: DB, UserDate, SpeechData, WeatherData, Activity 
    	
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
    	Item userItem = database.getUser(userId);
		if(userItem != null) {
			// if user exists, load his data
			String name = userItem.getString("name");
			boolean formalSpeech = userItem.getBoolean("formalSpeech");
			boolean firstSetupComplete = userItem.getBoolean("firstSetupComplete");
			boolean introductionHeard = userItem.getBoolean("introductionHeard");
			ArrayList<String> excludedBodyParts = new ArrayList<String>(userItem.getStringSet("excludedBodyParts"));
			
			user = new User(userId, name, formalSpeech, firstSetupComplete, introductionHeard, excludedBodyParts);
		} else {
			// if user doesn't exist in table, create new empty user 
			log.error("could not find userId in table");
			user = new User(userId, "nameEmpty", false, false, false, new ArrayList<String>());
		}
		
		
    }
    
    

	// Sets all user-related data (specified in the db)
	private void initUserData() {
		log.info("INITUSERERDATA");
		log.info(userId);
		
		/*
		if(!firstSetupComplete) {
			return; 
		} else {
			// userName
			userNameTell = "Maria"+ " ";
		    userNameAsk = "Maria" + "? ";
		    userNameSet = false; 
		    // formalSpeech
		    formalSpeech = true; 
		    speechStyleSet = false; 
		    // excluded bodyparts from exercises
		    EXCLUDED_BODYPARTS.add("knie");
		    EXCLUDED_BODYPARTS.add("schulter");
		}*/
	}
	
	// Overwrites speech variables as to match the user preference regarding formal and informal speech
    private void initSpeech() {
    	log.info("INITSPEECH");
    	if(user.preferesFormalSpeech()) {
    		helpText = helpTextF; 
    		errorText = errorTextF;
    		askForLocation = askForLocationF; 
    		askForLocationRe = askForLocationReF;
    		askForExertion = askForExertionF; 
    		askForExertionRe = askForExertionReF;
    		askForBodypart = askForBodypartF;
    		askForBodypartRe = askForBodypartReF;
    		askForName = askForNameF;
    		askForNameRe = askForNameReF; 
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
    		askForName = askForNameI;
    		askForNameRe = askForNameReI;
    		confirmSpeechStyle = confirmSpeechStyleI;
    		greetingText = GREETINGS_INFORMAL[(int) Math.floor(Math.random() * GREETINGS_INFORMAL.length)];
    	}
    }
    
    
    
    
    // Called when the user starts the skill for the first time
	private SpeechletResponse firstSetup(Intent intent) {
		log.info("FIRSTSETUP");
		//Ask for name, Siezen/Duzen, excluded Körperteile
		String introduction = ""; 
		
		// Initialize params
    	//session.setAttribute("activityType", "game");
    	 
        SpeechletResponse nameOutput = null; 
        SpeechletResponse formalOutput = null;
        SpeechletResponse bodypartsOutput = null; 
        
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
		
		String output = "Nun gut, " + user.getName() + ", sie können mich nun nach Übungen, Spielen oder Beschäftigungen fragen";
		
		return SpeechletResponse.newTellResponse(getPlainTextOutputSpeech(output));
	}
	
	// TODO: namen-slot aufsetzen, dann namen in variable speichern, in db schreiben. Außerdem descriptionText beim ersten FDragen nach dem Namen ausgeben
	// Called when skill is first exectuted or user wants to change their name
	private SpeechletResponse setUserName(Intent intent) {
		log.info("SETUSERNAME");
		
		Slot nameSlot = intent.getSlot("name");
    	
    	// Check if activity location was provided
        if(nameSlot != null && nameSlot.getValue() != null && !nameSlot.getValue().equalsIgnoreCase("")){
        	user.setName(nameSlot.getValue().toLowerCase()); 
	        
        } else {
        	// Kein Ort angegeben, nachfragen
        	user.setName(null);
        	return SpeechletResponse.newAskResponse(getPlainTextOutputSpeech(askForName), getReprompt(getPlainTextOutputSpeech(askForNameRe)));
        }
	        
		return null;
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
	        	log.info("speech style set formal");
	        	return SpeechletResponse.newTellResponse(getPlainTextOutputSpeech("gesiezt wurde erkannt"));
	        } 
	        
	        if (Arrays.asList(SPEECHSTYLE_INFORMAL).contains(style)) {
	        	user.setFormalSpeech(false);
	        	log.info("speech style set informal");
	        	return SpeechletResponse.newTellResponse(getPlainTextOutputSpeech("geduzt wurde erkannt"));
	        }
	        
        } else {
        	// Kein Style angegeben, nachfragen
        	user.setSpeechStyleSet(false);
        	log.info("location not set");
        	return SpeechletResponse.newAskResponse(getPlainTextOutputSpeech(askForSpeechStyle), getReprompt(getPlainTextOutputSpeech(askForSpeechStyleRe)));
        }
    	
		return null;
    }
    
    
	
	////////// CORE FUNCTIONS /////////////////////////////////////////////////////////////////////////////////////////
    
    
    
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
        random = new Random().nextInt(proposals.length);
        if(weatherProvider.isWeatherGood()) {
        	proposal = proposals[random] + activityString;
        } else {
        	proposal = "Heute " + weatherProvider.getWeatherDescription() + badWeatherInfo + proposals[random] + activityString;
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
        random = new Random().nextInt(proposals.length);
        if(weatherProvider.isWeatherGood()) {
        	proposal = proposals[random] + activityString;
        } else {
        	proposal = "Heute " + weatherProvider.getWeatherDescription() + badWeatherInfo + proposals[random] + activityString;
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
        random = new Random().nextInt(proposals.length);
        if(weatherProvider.isWeatherGood()) {
        	proposal = proposals[random] + activityString;
        } else {
        	proposal = "Heute " + weatherProvider.getWeatherDescription() + badWeatherInfo + proposals[random] + activityString;
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
		        
	        } else {
	        	// Kein Ort angegeben, nachfragen
	        	activity.setLocation(null);
	        	return SpeechletResponse.newAskResponse(getPlainTextOutputSpeech(askForLocation), getReprompt(getPlainTextOutputSpeech(askForLocationRe)));
	        }
	        
        } else {
        	activity.setLocation("inside");
        	return null; 
        }
		return null;
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
	        
        } else {
        	// Kein Exertion angegeben, nachfragen
        	activity.setExertion(null);
        	return SpeechletResponse.newAskResponse(getPlainTextOutputSpeech(askForExertion), getReprompt(getPlainTextOutputSpeech(askForExertionRe)));
        }
	        
        
		return null;
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
	        
        } else {
        	// Kein bodypart angegeben, nachfragen
        	activity.setBodypart(null);
        	return SpeechletResponse.newAskResponse(getPlainTextOutputSpeech(askForBodypart), getReprompt(getPlainTextOutputSpeech(askForBodypartRe)));
        }
	        
        
		return null;
    }
    
    
    


    
    
    //////////BUILT_IN FUNCTIONS /////////////////////////////////////////////////////////////////////////////////////////
    
    // Returns response for the help intent.
    private SpeechletResponse getHelpResponse() {
    	log.info("GETHELP");
    	
    	
    	// Tell the user what they can do 
        return getAskResponse("Trainer", helpText);
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
    	return SpeechletResponse.newTellResponse(getPlainTextOutputSpeech(cancelText));
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





