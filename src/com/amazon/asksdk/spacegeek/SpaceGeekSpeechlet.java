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
import org.json.JSONArray;
import org.json.JSONObject;

import com.amazon.speech.slu.Intent;
import com.amazon.speech.slu.Slot;
import com.amazon.speech.speechlet.IntentRequest;
import com.amazon.speech.speechlet.LaunchRequest;
import com.amazon.speech.speechlet.SessionEndedRequest;
import com.amazon.speech.speechlet.SessionStartedRequest;
import com.amazon.speech.speechlet.SpeechletV2;
import com.amazon.speech.speechlet.SpeechletResponse;
import com.amazon.speech.json.SpeechletRequestEnvelope;
import com.amazon.speech.ui.PlainTextOutputSpeech;
import com.amazon.speech.ui.Reprompt;
import com.amazon.speech.ui.SimpleCard;
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
    private String activityType; // exercise, game, activity
    private String activityExertion; // relaxed, exhausting
    private String activityLocation; // inside, outside, both
    private boolean activityWithFriends; // true, false
    private ArrayList<String> activityExcludeBodypart = new ArrayList<>(); // Exclude activities that put strain on these body parts
    private ArrayList<String> activityIncludeBodypart = new ArrayList<>(); // Choose activities that include these body parts
    // Actity data set 
    private boolean activityTypeSet; 
    private boolean activityExertionSet; 
    private boolean activityLocationSet; 
    private boolean activityWithFriendsSet; 
    private boolean activityExcludeBodypartSet; 
    private boolean activityIncludeBodypartSet; 
    private boolean weatherSet; 
    // weather data
    private String fetchWeatherUrl = "http://api.openweathermap.org/data/2.5/weather?id=2935517&APPID=44956c0ccd5905a239c4ee266863eb06";
    private String weatherDescription;
    private boolean goodWeather;
    // database data
    private AmazonDynamoDBClient dbclient;
 
    
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
    		"ruhig", 
    		"ruhige"
    };
    private static final String[] EXERTIONS_EXHAUSTING = new String[] {
    		"anstrengend",
    		"anstrengende", 
    		"erschöpfend", 
    		"erschöpfende", 
    		"aktiv", 
    		"aktive"
    };
    private static final String[] LOCATIONS_INSIDE = new String[] {
    		"drinnen",
    		"im haus",
    		"in der wohnung",
    		"hier",
    		"im innnern",
    		"im inneren"
    };
    private static final String[] LOCATIONS_OUTSIDE = new String[] {
    		"draußen",
    		"außer Haus",
    		"unter freiem Himmel",
    		"im Freien",
    		"in der Sonne"
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
    
    
    
    
    
    
    
    
    
    
    ////////// LIFECYCLE METHODS /////////////////////////////////////////////////////////////////////////////////////////
    
    public SpeechletResponse onLaunch(SpeechletRequestEnvelope<LaunchRequest> requestEnvelope) {
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
        log.info("onIntent requestId={}, sessionId={}", request.getRequestId(), requestEnvelope.getSession().getSessionId());

        Intent intent = request.getIntent();
        String intentName = (intent != null) ? intent.getName() : null;
        PlainTextOutputSpeech outputSpeech;
        
        switch(intentName) {
        case "GetStatusIntent": 
        	
        case "GetNewFactIntent":
        	return getNewFactResponse(intent);
        case "RepeatIntent":
        	return repeat(intent);
        case "BmiIntent": 
        	return getHelpResponse();
        case "SetFormalSpeechIntent":
        	return setFormalSpeech(intent); 
        case "SetNameIntent":
        	return setName(intent);
        case "GetExerciseIntent":
        	return getExercise(intent);
        case "GetGameIntent":
        	return getGame(intent);
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
        }
    }
    
    
    
    
    
    
    
    
    
    
	////////// SETUP AND USER DATA METHODS /////////////////////////////////////////////////////////////////////////////////////////
	    
	private SpeechletResponse firstSetup() {
		return SpeechletResponse.newTellResponse(getPlainTextOutputSpeech("Platzhalter"));
	}
	
	private void initializeComponents(SpeechletRequestEnvelope<SessionStartedRequest> requestEnvelope) {
	    	requestId = requestEnvelope.getRequest().getRequestId();
	        sessionId = requestEnvelope.getSession().getSessionId();
	        goodWeather = getWeather(); 
	        
	        if (dbclient == null) {
	        	dbclient = new AmazonDynamoDBClient();
	        	
	        }
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
    
    private SpeechletResponse getExercise(Intent intent) {
    	Map<String, Slot> slots = intent.getSlots();
        Slot bodypart = slots.get("bodypart");
        String part = bodypart.getValue();
        
        Slot location = slots.get("location");
        String loc = location.getName();
        
        
        
        if(loc.equals("drinnen")) {
        	return SpeechletResponse.newTellResponse(getPlainTextOutputSpeech("Drinnen erkannt"));
        } else if (loc.equals("draußen")) {
        	return SpeechletResponse.newTellResponse(getPlainTextOutputSpeech("Draußen erkannt"));
        }
        
        // Hier die DB-Abfrage, zu Testzwecken habe ich die Übungen in ein Array gespeichert
        String[][] exercises = {{"Kniebeuge", "Knie", "Po"},{"Kopfpendel", "nun", "Nacken"}};
        int length = exercises.length; 
        int rand = new Random(length).nextInt();
        
        String name = exercises[rand][0];
    	
        
        
        return SpeechletResponse.newTellResponse(getPlainTextOutputSpeech("Übung für folgenden Körperteil: " + part));
    }

    private SpeechletResponse getGame(Intent intent) {
    	       
    	// Check if activity location was provided
        String location = intent.getSlot("location").getValue().toLowerCase();
        
        if(Arrays.asList(LOCATIONS_INSIDE).contains(location)) {
        	activityLocationSet = true;
        	activityLocation = "inside";
        	log.info("location: " + location);
        } else if (Arrays.asList(LOCATIONS_OUTSIDE).contains(location)) {
        	activityLocationSet = true;
        	activityLocation = "outside";   
        	log.info("location: " + location);
        } else {
        	activityLocationSet = false;
        	log.info("No location found");
        }
        
        // Check if activity exertion was provided
        String exertion = intent.getSlot("exertion").getValue().toLowerCase();
        
        if(Arrays.asList(EXERTIONS_RELAXED).contains(exertion)) {
        	activityExertionSet = true;
        	activityExertion = "relaxed";
        	log.info("exertion: " + exertion);
        } else if (Arrays.asList(EXERTIONS_EXHAUSTING).contains(exertion)) {
        	activityExertionSet = true;
        	activityExertion = "exhausting"; 
        	log.info("exertion: " + exertion);
        } else {
        	activityExertionSet = false;
        	log.info("No exertion found");
        }
        
        // Gather additional info
        if(!activityLocationSet) {
        	// get additional data
        }
        if (!activityExertionSet) {
        	// get additional data
        }
        
        // Get game from DB according to set parameters
        String game = "Schach spielen";
        
        // Generate output
        int random = new Random(proposals.length).nextInt();
        String proposal = proposals[random] + game;
        
        
        return SpeechletResponse.newTellResponse(getPlainTextOutputSpeech(proposal));
       
        
       /* 
        Hier die DB-Abfrage, zu Testzwecken habe ich die Übungen in ein Array gespeichert
        String[][] exercises = {{"Kniebeuge", "Knie", "Po"},{"Kopfpendel", "nun", "Nacken"}};
        int length = exercises.length; 
        int rand = new Random(length).nextInt();
        
        String name = exercises[rand][0];*/
    	
        
        
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
