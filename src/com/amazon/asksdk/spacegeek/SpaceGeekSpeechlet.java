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
    String badWeatherInfo = "Da das Wetter heute nicht gut werden soll, empfehle ich drinnen zu bleiben. ";
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
    private String userNameTell = "Maria ";
    private String userNameAsk = "Maria? ";
    private boolean userNameSet = false; 
    private boolean formalSpeech = true; 
    private boolean speechStyleSet = false; 
    private boolean excludedBodypartsSet = false; 
    private boolean firstSetupComplete; 
    private boolean introductionHeard = false; 
    private ArrayList<String> EXCLUDED_BODYPARTS = new ArrayList<String>();
    
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
    private boolean goodWeather = true;
    
    // database and storage data
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
        session = requestEnvelope.getSession();
        goodWeather = getWeather(); 
        // TODO: Remove this line
	    goodWeather = true; 
	    
	    initDb(); 
        initUserData(); 
        initSpeech();
        return SpeechletResponse.newTellResponse(getPlainTextOutputSpeech(greetingText));
    }

    // Ka, wann das gestartet wird 
    public void onSessionStarted(SpeechletRequestEnvelope<SessionStartedRequest> requestEnvelope) {
    	log.info("ONSESSIONSTARTED");
        requestId = requestEnvelope.getRequest().getRequestId();
	    sessionId = requestEnvelope.getSession().getSessionId();
	    goodWeather = getWeather(); 
        // TODO: Remove this line
	    goodWeather = true; 
	    
	    initDb(); 
        initUserData(); 
        initSpeech();
	    log.info("onSessionStarted requestId={}, sessionId={}", requestId, sessionId);
    }
    
    public void onSessionEnded(SpeechletRequestEnvelope<SessionEndedRequest> requestEnvelope) {
        log.info("onSessionEnded requestId={}, sessionId={}", requestEnvelope.getRequest().getRequestId(),
		requestEnvelope.getSession().getSessionId());
        // any cleanup logic goes here
    }
    
    // TODO: Checken ob setup komplett ist 
    public SpeechletResponse onIntent(SpeechletRequestEnvelope<IntentRequest> requestEnvelope) {
    	IntentRequest request = requestEnvelope.getRequest();
        session = requestEnvelope.getSession();
    	log.info("ONINTENT");
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
        if(!firstSetupComplete) {
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
        }
        
        
    }
    
    
    
    
    
	////////// SETUP AND USER DATA METHODS /////////////////////////////////////////////////////////////////////////////////////////
	    
    private void dbTest() {
    	
    }
    
    // Initializes db access, handles access requests
	private void initDb() {
		// test 
		/*AmazonDynamoDB client = AmazonDynamoDBClientBuilder.standard().build();
		//AmazonDynamoDB client = new AmazonDynamoDBClient();
		DynamoDB dynamoDB = new DynamoDB(client);
		Item item = new Item();
		item.withString("hallo", "ich");
		dynamoDB.getTable("exEinsTable").putItem(item);
		*/
		
    	log.info("INITDB");
		Database database = new Database(); 
    	String tableName = "exEinsUsers";
    	String dbEnpointNorthVirginia = "dynamodb.us-east-1.amazonaws.com";
    	String loc = "inside";
    	String ext = "relaxed";
    	String userId = "ihfg3j3jj5j5jh63989ao";
    	Regions REGION = Regions.US_WEST_2;
    	
		
		// Create new table with specified key schema
		List<KeySchemaElement> keySchema = new ArrayList<>();
        keySchema.add(new KeySchemaElement("id", "Number"));
        keySchema.add(new KeySchemaElement("name", "String"));
		database.createTable(tableName, keySchema);
		
		
		
		// Writes an item into the table 
		List<String> excludedBodyParts = new ArrayList<String>();
		excludedBodyParts.add("schulter");
		excludedBodyParts.add("knie");
		
		Item item = new Item(); 
		item.withNumber("id", 3);
		item.withString("name", "Walter");
		item.withList("excludedBodyParts", excludedBodyParts);
		
		database.putItem(tableName, item);
		
		
    	// Gets a users name
		String userName = database.getUserName(userId); 
        
        // etc. 
    
        
        
    }

	// Sets all user-related data (specified in the db)
	private void initUserData() {
		log.info("INITUSERERDATA");
		// TODO: Get data from db
		firstSetupComplete = false;
		
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
		}
	}
	
	// Overwrites speech variables as to match the user preference regarding formal and informal speech
    private void initSpeech() {
    	log.info("INITSPEECH");
    	if(formalSpeech) {
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
        if(!userNameSet) {
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
		
		
		firstSetupComplete = true; 
		
		String output = "Nun gut, " + userNameTell + ", sie können mich nun nach Übungen, Spielen oder Beschäftigungen fragen";
		
		return SpeechletResponse.newTellResponse(getPlainTextOutputSpeech(output));
	}
	
	// TODO: namen-slot aufsetzen, dann namen in variable speichern, in db schreiben. Außerdem descriptionText beim ersten FDragen nach dem Namen ausgeben
	// Called when skill is first exectuted or user wants to change their name
	private SpeechletResponse setUserName(Intent intent) {
		log.info("SETUSERNAME");
		
		Slot nameSlot = intent.getSlot("name");
    	
    	// Check if activity location was provided
        if(nameSlot != null && nameSlot.getValue() != null && !nameSlot.getValue().equalsIgnoreCase("")){
        	String name = nameSlot.getValue().toLowerCase();
        
	        userNameTell = name + " ";
	        userNameAsk = name + "? ";
	        userNameSet = true; 
	        
        } else {
        	// Kein Ort angegeben, nachfragen
        	userNameSet = false;
        	log.info("name not set");
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
	        	speechStyleSet = true;
	        	formalSpeech = true; 
	        	log.info("speech style set formal");
	        	return SpeechletResponse.newTellResponse(getPlainTextOutputSpeech("gesiezt wurde erkannt"));
	        } 
	        
	        if (Arrays.asList(SPEECHSTYLE_INFORMAL).contains(style)) {
	        	speechStyleSet = true;
	        	formalSpeech = false; 
	        	log.info("speech style set informal");
	        	return SpeechletResponse.newTellResponse(getPlainTextOutputSpeech("geduzt wurde erkannt"));
	        }
	        
        } else {
        	// Kein Style angegeben, nachfragen
        	speechStyleSet = false; 
        	log.info("location not set");
        	return SpeechletResponse.newAskResponse(getPlainTextOutputSpeech(askForSpeechStyle), getReprompt(getPlainTextOutputSpeech(askForSpeechStyleRe)));
        }
    	
		return null;
    }
    
    
	
	////////// CORE FUNCTIONS /////////////////////////////////////////////////////////////////////////////////////////
    
	// Returns a game for the user
    private SpeechletResponse getGame(Intent intent, Session session) {// Check if activity location was provided
    	log.info("GETGAME, activityType = " + activityType);
    	
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
        	return fetchFromDb(badWeatherInfo, activityType, (String) session.getAttribute("activityLocation"), (String) session.getAttribute("activityExertion"), null);
        } else {
        	return fetchFromDb("", activityType, (String) session.getAttribute("activityLocation"), (String) session.getAttribute("activityExertion"), null);
        }
    }
    
    // Returns an exercise for the user 
    private SpeechletResponse getExercise(Intent intent, Session session) {
    	log.info("GETEXERCISE, activityType = " + activityType);
    	
    	// Initialize params
    	//session.setAttribute("activityType", "game");
    	activityType = "exercise";
    	activityTypeSet = true; 
        SpeechletResponse locationOutput = null; 
        SpeechletResponse bodypartOutput = null; 
        
        
        
        // Check if location and exertion were provided as slots
        if(session.getAttributes().containsKey("activityLocationSet")) {
        	if(!(Boolean) session.getAttribute("activityLocationSet")) {
        		locationOutput = getLocation(intent, session);
        	}
        	
        } else {
        	session.setAttribute("activityLocationSet", false);
        	locationOutput = getLocation(intent, session);
        }
        
        if(session.getAttributes().containsKey("activityBodypartSet")) {
        	if(!(Boolean) session.getAttribute("activityBodypartSet")) {
        		bodypartOutput = getBodypart(intent, session);
        	}
        } else {
        	session.setAttribute("activityBodypartSet", false);
        	bodypartOutput = getBodypart(intent, session);
        }
        
        
        
        // Ask for missing params
        if(locationOutput != null) {
	        return locationOutput; 
	    }
        if(bodypartOutput != null) {
	        return bodypartOutput; 
        }
        
        
        
        // Gets a game from the db according to set params
        if(!goodWeather) {
        	return fetchFromDb(badWeatherInfo, activityType, (String) session.getAttribute("activityLocation"), "Exhausting", (String) session.getAttribute("activityBodypart"));
        } else {
        	return fetchFromDb("", activityType, (String) session.getAttribute("activityLocation"), "Exhausting", (String) session.getAttribute("activityBodypart"));
        }
    }
    
    // Returns an occupation for the user 
    private SpeechletResponse getOccupation(Intent intent, Session session) {
    	log.info("GETOCCUPATION, activityType = " + activityType);
    	
    	// Initialize params
    	//session.setAttribute("activityType", "game");
    	activityType = "occupation";
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
        
        // Ask for whats missing
        if(locationOutput != null) {
	        return locationOutput; 
	    }
        if(exertionOutput != null) {
	        return exertionOutput; 
        }
        
        
        
        // Gets a game from the db according to set params
        if(!goodWeather) {
        	return fetchFromDb(badWeatherInfo, activityType, (String) session.getAttribute("activityLocation"), (String) session.getAttribute("activityExertion"), null);
        } else {
        	return fetchFromDb("", activityType, (String) session.getAttribute("activityLocation"), (String) session.getAttribute("activityExertion"), null);
        }
    }

    // Checks if location was provided, if not, asks for it
    private SpeechletResponse getLocation(Intent intent, Session session) {
    	log.info("GETLOCATION");
    	
    	Slot locationSlot = intent.getSlot("location");
    	
    	if(goodWeather) {
        	// Check if activity location was provided
	        if(locationSlot != null && locationSlot.getValue() != null && !locationSlot.getValue().equalsIgnoreCase("")){
	        	String location = locationSlot.getValue().toLowerCase();
	        
		        if(Arrays.asList(LOCATIONS_INSIDE).contains(location)) {
		        	session.setAttribute("activityLocationSet", true);
		        	session.setAttribute("activityLocation", "inside");
		        	log.info("location set inside");
		        } 
		        
		        if (Arrays.asList(LOCATIONS_OUTSIDE).contains(location)) {
		        	session.setAttribute("activityLocationSet", true);
		        	session.setAttribute("activityLocation", "outside");
		        	log.info("location set outside"); 
		        }
		        
	        } else {
	        	// Kein Ort angegeben, nachfragen
	        	session.setAttribute("activityLocationSet", false);
	        	log.info("location not set");
	        	return SpeechletResponse.newAskResponse(getPlainTextOutputSpeech(askForLocation), getReprompt(getPlainTextOutputSpeech(askForLocationRe)));
	        }
	        
        } else {
        	session.setAttribute("activityLocationSet", true);
        	session.setAttribute("activityLocation", "inside");
        	log.info("location set inside (badWeather");
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
        	log.info("getExertion: " + exertion);
        
	        if(Arrays.asList(EXERTIONS_RELAXED).contains(exertion)) {
	        	session.setAttribute("activityExertionSet", true);
	        	session.setAttribute("activityExertion", "relaxed");
	        	log.info("exertion set relaxed");
	        	//activityExertionSet = true;
	        	//activityExertion = "relaxed";
	        } 
	        
	        if (Arrays.asList(EXERTIONS_EXHAUSTING).contains(exertion)) {
	        	session.setAttribute("activityExertionSet", true);
	        	session.setAttribute("activityExertion", "exhausting");
	        	log.info("exertion set exhausting");
	        	//activityExertionSet = true;
	        	//activityExertion = "exhausting";
	       
	        }
	        
        } else {
        	// Kein Exertion angegeben, nachfragen
        	session.setAttribute("activityExertionSet", false);
        	//activityExertionSet = false;
        	log.info("exertion not set");
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
        	log.info("getBodypart: " + bodypart);
        
	        if(Arrays.asList(BODYPARTS).contains(bodypart)) {
	        	session.setAttribute("activityBodypartSet", true);
	        	session.setAttribute("activityBodypart", bodypart);
	        } 
	        
        } else {
        	// Kein bodypart angegeben, nachfragen
        	session.setAttribute("activityBodypartSet", false);
        	//activitybodypartSet = false;
        	return SpeechletResponse.newAskResponse(getPlainTextOutputSpeech(askForBodypart), getReprompt(getPlainTextOutputSpeech(askForBodypartRe)));
        }
	        
        
		return null;
    }
    
    // Fetches an entry from the db specified by the intent
    private SpeechletResponse fetchFromDb(String additionalInfo, String activityType, String activityLocation, String activityExertion, String bodypart) {
    	log.info("FETCHFROMDB");
    	
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
        case "gameInsideExhaustingDb":
        	random = new Random().nextInt(gameInsideExhaustingDb.length);
        	activity = gameInsideExhaustingDb[random];
        case "gameOutsideRelaxedDb":
        	random = new Random().nextInt(gameOutsideRelaxedDb.length);
        	activity = gameOutsideRelaxedDb[random];
        	break;
        case "gameOutsideExhaustingDb":
        	random = new Random().nextInt(gameOutsideExhaustingDb.length);
        	activity = gameOutsideExhaustingDb[random];
        	break; 
        case "occupationInsideRelaxedDb":
        	random = new Random().nextInt(occupationInsideRelaxedDb.length);
        	activity = occupationInsideRelaxedDb[random];
        	break;
        case "occupationInsideExhaustingDb":
        	random = new Random().nextInt(occupationInsideExhaustingDb.length);
        	activity = occupationInsideExhaustingDb[random];
        	break;
        case "occupationOutsideRelaxedDb":
        	random = new Random().nextInt(occupationOutsideRelaxedDb.length);
        	activity = occupationOutsideRelaxedDb[random];
        	break; 
        case "occupationOutsideExhaustingDb":
        	random = new Random().nextInt(occupationOutsideExhaustingDb.length);
        	activity = occupationOutsideExhaustingDb[random];
        	break;
        case "exerciseInsideExhaustingDb":
        	random = new Random().nextInt(exerciseInsideExhaustingDb.length);
        	activity = exerciseInsideExhaustingDb[random];
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
    	log.info("GETWEATHER");
    	
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
    
    
    
    
    //////////BUILT_IN FUNCTIONS /////////////////////////////////////////////////////////////////////////////////////////
    
    // Returns response for the help intent.
    private SpeechletResponse getHelpResponse() {
    	log.info("GETHELP");
    	
        return getAskResponse("Trainer", helpText);
    }
    
    // Cancels the user request 
    private SpeechletResponse cancelRequest() {
    	log.info("CANCELREQUEST");
    	
    	
    	// Activity data
        activityType = null;
        activityExertion = null; 
        activityLocation = null; 
        activityBodyPart = null;
        activityWithFriends = false;
        // Actity data set 
        activityTypeSet = false; 
        activityExertionSet = false; 
        activityLocationSet = false; 
        activityBodypartSet = false;
        activityWithFriendsSet = false; 
        activityExcludeBodypartSet = false; 
        activityIncludeBodypartSet = false; 

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
    
    
    
//////////TEST FUNCTIONS /////////////////////////////////////////////////////////////////////////////////////////
    
// Repeats a number and a metric
private SpeechletResponse repeat(final Intent intent) {
	String number = "";
 String metric = "";
 String metricAdj = "";
	
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







}





