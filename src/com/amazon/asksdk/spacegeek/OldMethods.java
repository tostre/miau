package com.amazon.asksdk.spacegeek;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Arrays;
import java.util.Random;

import org.apache.commons.lang3.text.WordUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import com.amazon.speech.slu.Intent;
import com.amazon.speech.slu.Slot;
import com.amazon.speech.speechlet.Session;
import com.amazon.speech.speechlet.SpeechletResponse;
import com.amazonaws.regions.Regions;

public class OldMethods {

    // Initializes db access, handles access requests
 	private void testDb() {
 		// test 
 		/*AmazonDynamoDB client = AmazonDynamoDBClientBuilder.standard().build();
 		//AmazonDynamoDB client = new AmazonDynamoDBClient();
 		DynamoDB dynamoDB = new DynamoDB(client);
 		Item item = new Item();
 		item.withString("hallo", "ich");
 		dynamoDB.getTable("exEinsTable").putItem(item);
 		*/
 		
     	//log.info("INITDB");
 		//database = new Database(); 
     	String dbEnpointNorthVirginia = "dynamodb.us-east-1.amazonaws.com";
     	String loc = "inside";
     	String ext = "relaxed";
     	String userId = "ihfg3j3jj5j5jh63989ao";
     	Regions REGION = Regions.US_WEST_2;
     	
 		
 		// Create new table with specified key schema
 		/*List<KeySchemaElement> keySchema = new ArrayList<>();
         keySchema.add(new KeySchemaElement("id", "Number"));
         keySchema.add(new KeySchemaElement("name", "String"));
 		database.createTable(tableName, keySchema);*/
 		
 		
 		
 		// Writes an item into the table 
 		/*List<String> excludedBodyParts = new ArrayList<String>();
 		excludedBodyParts.add("schulter");
 		excludedBodyParts.add("knie");
 		
 		Item item = new Item(); 
 		item.withNumber("id", 3);
 		item.withString("name", "Walter");
 		item.withList("excludedBodyParts", excludedBodyParts);
 		
 		database.putItem(tableName, item);*/
 		
 		
     	// Gets a users name
 		//String userName = database.getUserName(userId); 
         
         // etc. 
     
         
         
     }
    
 	// Returns a game for the user
    private SpeechletResponse getGameOld(Intent intent, Session session) {// Check if activity location was provided
    	/*log.info("GETGAME");
    	
    	// Initialize params
    	//session.setAttribute("activityType", "game");
    	activity.setType("game");
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
        	return fetchFromDb(badWeatherInfo, activity.getType(), (String) session.getAttribute("activityLocation"), (String) session.getAttribute("activityExertion"), null);
        } else {
        	return fetchFromDb("", activity.getType(), (String) session.getAttribute("activityLocation"), (String) session.getAttribute("activityExertion"), null);
        }*/
        
        return null; 
    }

    // Returns an exercise for the user 
    private SpeechletResponse getExerciseOld(Intent intent, Session session) {
    	/*log.info("GETEXERCISE, activityType = " + activityType);
    	
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
        }*/
        
        return null; 
    }

    // Returns an occupation for the user 
    private SpeechletResponse getOccupationOld(Intent intent, Session session) {
    	/*log.info("GETOCCUPATION, activityType = " + activityType);
    	
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
        }*/
    	
    	return null; 
    }

    // Fetches the weather from openWeatherMap
    private boolean getWeatherOld(){
    	/*log.info("GETWEATHER");
    	
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
			//weatherSet = true; 
			return goodWeather = false; 
		} else if(weatherId >= 300 && weatherId < 600) {
			// leichter Regen
			weatherDescription = "regnet es";
			//weatherSet = true; 
			return goodWeather = false; 
		} else if(weatherId >= 600 && weatherId < 700) {
			// Schnee
			weatherDescription = "schneit es";
			//weatherSet = true; 
			return goodWeather = false; 
		} else if(weatherId >= 700 && weatherId < 800) {
			// Atmosphäre
			weatherDescription = "ist es neblig oder rauchig";
			//weatherSet = true; 
			return goodWeather = false; 
		} else if(weatherId == 800) {
			// Klar
			weatherDescription = "scheint die Sonne";
			//weatherSet = true; 
			return goodWeather = true; 
		} else if(weatherId >= 801 && weatherId < 900) {
			// Bewölkt
			weatherDescription = "ist es bewölkt";
			//weatherSet = true; 
			return goodWeather = true; 
		} else if(weatherId >= 900 && weatherId < 910) {
			// Extrem
			weatherDescription = "ist es wegen der Wetterbedingungen gefährlich raus zu gehen";
			//weatherSet = true; 
			return goodWeather = false; 
		} else if (weatherId >= 951 && weatherId < 954) {
			// Leichte Brise
			weatherDescription = "weht eine leichte Brise";
			//weatherSet = true; 
			return goodWeather = true; 
		} else {
			// Sehr windig
			weatherDescription = "weht der Wind sehr stark";
			//weatherSet = true; 
			return goodWeather = false; 
		}
		*/
		return false;
    }
    
    // Fetches an entry from the db specified by the intent
    private SpeechletResponse fetchFromDbOld(String additionalInfo, String activityType, String activityLocation, String activityExertion, String bodypart) {
    	/*log.info("FETCHFROMDB");
    	
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
        */
        return null; 
    }

 // Checks if location was provided, if not, asks for it
    private SpeechletResponse getLocationOld(Intent intent, Session session) {
    	/*log.info("GETLOCATION");
    	
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
        }+7
		return null;
    }
    
    // Checks if exertion was provided, if not, asks for it
    private SpeechletResponse getExertionOld(Intent intent, Session session) {
    	/*log.info("GETEXERTION");
    	
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
        }+7
	        
        
		return null;
    }
    
    // Checks if a bodypart (for the exercise) was provided, if not, asks for it
    private SpeechletResponse getBodypartOld(Intent intent, Session session) {
    	/*log.info("GETBODYPART");
    	
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
        }*/
	        
        
		return null;
    }
	
	
}
