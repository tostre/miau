/**
    Copyright 2014-2015 Amazon.com, Inc. or its affiliates. All Rights Reserved.

    Licensed under the Apache License, Version 2.0 (the "License"). You may not use this file except in compliance with the License. A copy of the License is located at

        http://aws.amazon.com/apache2.0/

    or in the "license" file accompanying this file. This file is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.
 */
package com.amazon.asksdk.spacegeek;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.json.JSONArray;
import org.json.JSONException;
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
import com.amazon.speech.ui.OutputSpeech;

// most basic example of how to handle Alexa Skill requests
public class SpaceGeekSpeechlet implements SpeechletV2 {
    private static final Logger log = LoggerFactory.getLogger(SpaceGeekSpeechlet.class);
    private static final String TOPIC_SLOT = "topic";
    private static final String CITY_SLOT = "city";
    private boolean activityLocationSet = false;
    private boolean activityTypeSet = false;
    private boolean activityModeSet = false;
    
    private String onLaunchMessage = "Hallo, wie kann ich helfen?";
    
    private String number = "";
    private String metric = "";
    private String metricAdj = "";
    
    private int weight = 0;
    private int height = 0;
    private String gender = ""; 
    
    // user data
    private String userName = "Maria";
    // weather data
    private String apiKey = "44956c0ccd5905a239c4ee266863eb06";
    private String cityName = "Dortmund";
    private int cityId = 2935517;
    private String fetchWeatherUrl = "http://api.openweathermap.org/data/2.5/weather?id=2935517&APPID=44956c0ccd5905a239c4ee266863eb06";
    
    // Array mit Fakten
    private static final String[] SPACE_FACTS = new String[] {
            "Ein Jahr auf dem Merkur ist 88 Tage lang.",
            "Venus rotiert gegen den Uhrzeigersinn.",
            "Die Erde ist als einziger Planet nicht nach einem Gott benannt."
    };
    
 // Array mit Fakten
    private static final String[] CARS_FACTS = new String[] {
            "Ein Auto fährt.",
            "Autos verbrauchen Benzin.",
            "Autos haben vier Räder."
    };
    
    
    @Override
    public SpeechletResponse onLaunch(SpeechletRequestEnvelope<LaunchRequest> requestEnvelope) {
        log.info("onLaunch requestId={}, sessionId={}", requestEnvelope.getRequest().getRequestId(), requestEnvelope.getSession().getSessionId());
        
        String weather = getWeather(); 
        
        // Create the Simple card content.
        SimpleCard card = getSimpleCard("SpaceGeek", onLaunchMessage);
        // Create the plain text output.
        PlainTextOutputSpeech speech = getPlainTextOutputSpeech(onLaunchMessage + " " + "Hier ist das Wetter: " + weather);

        
        
        return SpeechletResponse.newTellResponse(speech, card);
    }

    @Override
    public void onSessionStarted(SpeechletRequestEnvelope<SessionStartedRequest> requestEnvelope) {
        log.info("onSessionStarted requestId={}, sessionId={}", requestEnvelope.getRequest().getRequestId(),
                requestEnvelope.getSession().getSessionId());
        // any initialization logic goes here
    }
    
    @Override
    public void onSessionEnded(SpeechletRequestEnvelope<SessionEndedRequest> requestEnvelope) {
        log.info("onSessionEnded requestId={}, sessionId={}", requestEnvelope.getRequest().getRequestId(),
                requestEnvelope.getSession().getSessionId());
        // any cleanup logic goes here
    }
    
    @Override
    public SpeechletResponse onIntent(SpeechletRequestEnvelope<IntentRequest> requestEnvelope) {
        IntentRequest request = requestEnvelope.getRequest();
        log.info("onIntent requestId={}, sessionId={}", request.getRequestId(),
                requestEnvelope.getSession().getSessionId());

        Intent intent = request.getIntent();
        String intentName = (intent != null) ? intent.getName() : null;
        PlainTextOutputSpeech outputSpeech;
        
        switch(intentName) {
        case "GetNewFactIntent":
        	return getNewFactResponse(intent);
        case "RepeatIntent":
        	return repeat(intent);
        case "BmiIntent": 
        	return getWeightClass(intent); 
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
        	return getAskResponse("SpaceGeek", "Das wird nicht unterstützt.  VErsuche etwas anderes.");
        }
        
        	
    }
    
    
    
    private String getWeather(){
    	//JSONParser parser = new JSONParser();
        //URL url = new URL(fetchWeatherUrl);
        //URLConnection connection = url.openConnection();
        //BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
String weather = "Kein Wetter";
        
        InputStream inputStream;
		try {
			inputStream = new URL(fetchWeatherUrl).openStream();
			BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));
	        StringBuilder sb = new StringBuilder();
	        String line = "";
	        while ((line = br.readLine()) != null) {
            	sb.append(line);
	        }
	        
	        inputStream.close();
	        log.info("Nach StringBuilder");
	        
	        JSONObject json = new JSONObject(sb.toString());
	        JSONArray jsonArray = json.getJSONArray("weather");
	        JSONObject currentWeather = jsonArray.getJSONObject(0);
	        System.out.println("MAAAAAIN: " + currentWeather.getString("main"));
	        weather = currentWeather.getString("main");
	        
	        log.info(weather);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        
        return weather;
    }
    
    
     
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
    			speechText = "Du hast kein Thema angegeben, Anfänger, aber hier ist das Wetter " + getWeather();
    			break; 
            
            }
        } else {
        	speechText = "Du hast kein Thema angegeben, Anfänger, aber hier ist das Wetter" + getWeather();
        }
        
        	
        // Create the Simple card content.
        SimpleCard card = getSimpleCard("SpaceGeek", speechText);
        // Create the plain text output.
        PlainTextOutputSpeech speech = getPlainTextOutputSpeech(speechText);

        return SpeechletResponse.newTellResponse(speech, card);
    }

    private SpeechletResponse getWeightClass(final Intent intent) {
    	// Get the slots from the intent.
        Map<String, Slot> slots = intent.getSlots();
        Slot weightSlot = slots.get("weight");
        Slot heightSlot = slots.get("height");
        Slot genderSlot = slots.get("gender");
        
        weight = Integer.parseInt(weightSlot.getValue());
        height = Integer.parseInt(heightSlot.getValue());
        gender = genderSlot.getValue();
        
        int bmi = calculateBmi(weight, height);
        String weightClass = calculateWeightClass(bmi, gender);
        
        String speechText = "Sie haben " + weightClass; 
        
     // Create the plain text output.
        PlainTextOutputSpeech speech = getPlainTextOutputSpeech(speechText);
        //return SpeechletResponse.newTellResponse(speech, card);
        return SpeechletResponse.newTellResponse(speech);
    }
    
    // Calculates an BMI
    private int calculateBmi(int weight, int height) { 
    	int bmi = weight/(height^2); 
    	
    	return bmi; 
    	
    }
    
    
    private String calculateWeightClass(int bmi, String gender) {
    	if(gender == "männlich" && bmi < 20){
    			 return "Untergewicht";
    	}
    	if(gender == "männlich" && bmi > 20 && bmi <= 25){
    			 return "Normalgewicht";
    	}
    	if(gender == "männlich" && bmi > 25 && bmi <= 30){
    			 return "Übergewicht";
    	}
    	if(gender == "männlich" && bmi > 30 && bmi <= 40){
    			 return "Adipositas";
    	}
    	if(gender == "männlich" && bmi > 41){
    			 return "starke Adipositas";
    	}
    	
    	if(gender == "weiblich" && bmi < 19){
			 return "Untergewicht";
    	}
    	if(gender == "weiblich" && bmi > 19 && bmi <= 24){
			 return "Normalgewicht";
    	}
    	if(gender == "weiblich" && bmi > 25 && bmi <= 30){
			 return "Übergewicht";
		}
		if(gender == "weiblich" && bmi > 30 && bmi <= 40){
			 return "Adipositas";
		}
		if(gender == "weiblich" && bmi > 40){
			 return "starke Adipositas";
		}
    	
    	return "Konnte nicht berechnet werden"; 
    }
    
    
    
    
    
    
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
