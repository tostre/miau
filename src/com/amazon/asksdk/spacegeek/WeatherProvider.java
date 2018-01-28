package com.amazon.asksdk.spacegeek;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;

import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WeatherProvider {

	private static final Logger log = LoggerFactory.getLogger(SpaceGeekSpeechlet.class);
	
	private static final String fetchWeatherUrl = "http://api.openweathermap.org/data/2.5/weather?id=2935517&APPID=44956c0ccd5905a239c4ee266863eb06";
    private String weatherDescription;
    private boolean weatherGood = true;
	
	public WeatherProvider() {
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
			weatherDescription = "soll es gewittern. ";
			weatherGood = false; 
		} else if(weatherId >= 300 && weatherId < 600) {
			// leichter Regen
			weatherDescription = "soll es regnen. ";
			weatherGood = false; 
		} else if(weatherId >= 600 && weatherId < 700) {
			// Schnee
			weatherDescription = "soll es scheien. ";
			weatherGood = false; 
		} else if(weatherId >= 700 && weatherId < 800) {
			// Atmosph�re
			weatherDescription = "soll es sehr neblig werden. ";
			weatherGood = false; 
		} else if(weatherId == 800) {
			// Klar
			weatherDescription = "soll es sonnig werden. ";
			weatherGood = true; 
		} else if(weatherId >= 801 && weatherId < 900) {
			// Bew�lkt
			weatherDescription = "soll es bewölkt werden. ";
			weatherGood = true; 
		} else if(weatherId >= 900 && weatherId < 910) {
			// Extrem
			weatherDescription = "ist es wegen der Wetterbedingungen gefährlich raus zu gehen. ";
			weatherGood = false; 
		} else if (weatherId >= 951 && weatherId < 954) {
			// Leichte Brise
			weatherDescription = "soll eine leichte Brise wehen. ";
			weatherGood = true; 
		} else {
			// Sehr windig
			weatherDescription = "soll der Wind sehr stark wehen. ";
			weatherGood = false; 
		}
	}

	public void fetchWeather() {
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
			weatherDescription = "soll es gewittern. ";
			weatherGood = false; 
		} else if(weatherId >= 300 && weatherId < 600) {
			// leichter Regen
			weatherDescription = "soll es regnen. ";
			weatherGood = false; 
		} else if(weatherId >= 600 && weatherId < 700) {
			// Schnee
			weatherDescription = "soll es scheien. ";
			weatherGood = false; 
		} else if(weatherId >= 700 && weatherId < 800) {
			// Atmosph�re
			weatherDescription = "soll es sehr neblig werden. ";
			weatherGood = false; 
		} else if(weatherId == 800) {
			// Klar
			weatherDescription = "soll es sonnig werden. ";
			weatherGood = true; 
		} else if(weatherId >= 801 && weatherId < 900) {
			// Bew�lkt
			weatherDescription = "soll es bewölkt werden. ";
			weatherGood = true; 
		} else if(weatherId >= 900 && weatherId < 910) {
			// Extrem
			weatherDescription = "ist es wegen der Wetterbedingungen gefährlich raus zu gehen. ";
			weatherGood = false; 
		} else if (weatherId >= 951 && weatherId < 954) {
			// Leichte Brise
			weatherDescription = "soll eine leichte Brise wehen. ";
			weatherGood = true; 
		} else {
			// Sehr windig
			weatherDescription = "soll der Wind sehr stark wehen. ";
			weatherGood = false; 
		}
		
		log.info("WEATHER GOOD" + weatherGood);
		log.info("WEATHER ID" + weatherId);
		log.info("WEATHER DESC" + weatherDescription);
	}
	
	public boolean isWeatherGood() {
		fetchWeather();
		return weatherGood; 
	}
	
	public String getWeatherDescription() {
		//fetchWeather();
		return weatherDescription; 
	}
	
}
