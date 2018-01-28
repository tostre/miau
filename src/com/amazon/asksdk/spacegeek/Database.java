package com.amazon.asksdk.spacegeek;

import java.awt.ItemSelectable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.document.AttributeUpdate;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.document.ItemCollection;
import com.amazonaws.services.dynamodbv2.document.QueryOutcome;
import com.amazonaws.services.dynamodbv2.document.ScanOutcome;
import com.amazonaws.services.dynamodbv2.document.Table;
import com.amazonaws.services.dynamodbv2.document.UpdateItemOutcome;
import com.amazonaws.services.dynamodbv2.document.spec.GetItemSpec;
import com.amazonaws.services.dynamodbv2.document.spec.QuerySpec;
import com.amazonaws.services.dynamodbv2.document.spec.ScanSpec;
import com.amazonaws.services.dynamodbv2.document.spec.UpdateItemSpec;
import com.amazonaws.services.dynamodbv2.document.utils.NameMap;
import com.amazonaws.services.dynamodbv2.document.utils.ValueMap;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.CreateTableRequest;
import com.amazonaws.services.dynamodbv2.model.KeySchemaElement;
import com.amazonaws.services.dynamodbv2.model.ScanRequest;
import com.amazonaws.services.dynamodbv2.model.ScanResult;

public class Database {

	private static final Logger log = LoggerFactory.getLogger(SpaceGeekSpeechlet.class);
	private AmazonDynamoDB client; 
	private DynamoDB dynamoDB; 
	
	private static final String usersTable = "exEinsUsers";
    private static final String gamesTable = "exEinsGames";
    private static final String exercisesTable = "exEinsExercises";
    private static final String occupationsTable = "exEinsOccupations";
	
	public Database() {
		client = AmazonDynamoDBClientBuilder.standard().build();
		dynamoDB = new DynamoDB(client);
	}
	
	
	
	
	
	///////////////////////////////////////////////// READS USER DATA ////////////////////////////////////////////////////////////////////////////
	
	
	// Checks if a user already has an entry in the db
	public boolean isUserKnown(String userId) {
		log.info("isUserKnown?");
		Table table = dynamoDB.getTable(usersTable);
		GetItemSpec spec = new GetItemSpec().withPrimaryKey("id", userId);
		
		Item user = table.getItem(spec);
		
		if(user != null) {
			log.info("user is known");
			return true; 
		} else {
			log.info("user is not known");
			return false; 
		}
	}
	
	// Returns all user data from the db
	public User getUser(String userId) {
		Table table = dynamoDB.getTable(usersTable);
		GetItemSpec spec = new GetItemSpec().withPrimaryKey("id", userId);
		
		Item user = table.getItem(spec);
		
		
		String id = user.getString("id");
		String name = user.getString("name");
		boolean formalSpeech = user.getBoolean("formalSpeech");
			
		return new User(id, name, formalSpeech);
	}
	
	// Saves a user in the db when he finished the setup
	public void saveUser(String userId, String userName, boolean formalSpeech) {
		Item item = new Item(); 
		item.withString("id", userId);
		item.withString("name", userName);
		item.withBoolean("formalSpeech", formalSpeech);
		
		dynamoDB.getTable(usersTable).putItem(item);
	}
	
	// Updates a users entry in the db (depending in the parameters) 
	public void updateUser(String userId, String name, boolean formalSpeech, String bodypart) {
		Table table = dynamoDB.getTable(usersTable);
		ValueMap valueMap = new ValueMap();
		NameMap nameMap = new NameMap(); 
		UpdateItemSpec updateItemSpec = new UpdateItemSpec(); 
		
		// Update the name
		nameMap.with("#n", "name");
		valueMap.withString(":n", name);
		// Update speech style 
		nameMap.with("#f", "formalspeech");
		valueMap.withBoolean(":f", formalSpeech);
		//Update bodyparts
		List<String> pains = getPains(userId);
		if(bodypart != null) {
			pains.add(bodypart);
		}
		nameMap.with("#p", "pain");
		valueMap.withList(":p", pains);
		
		// Save parameters in updatespec
		updateItemSpec.withPrimaryKey("id", userId);
		updateItemSpec.withUpdateExpression("set #n = :n, #f = :f, #p = :p");
		updateItemSpec.withNameMap(nameMap);
		updateItemSpec.withValueMap(valueMap);
		
		try {
			UpdateItemOutcome outcome = table.updateItem(updateItemSpec);
		} catch (Exception e) {
			log.info("ERROR WHILE UPDATING: " + e.getMessage());
		}
	}
	
	// Reads the user-defined bodyparts of his profile als blacklist-items for exercises
	public List<String> getPains(String userId){
		Table table = dynamoDB.getTable(usersTable);
		GetItemSpec spec = new GetItemSpec().withPrimaryKey("id", userId);
		Item user = table.getItem(spec);
		
		return user.getList("pain"); 
	}
	
	
	
	
	
	///////////////////////////////////////////////// LOAD ACTIVITY DATA FROM DB ////////////////////////////////////////////////////////////////////////////
	

	// Loads a game from the db and returns it to the main class
	public ArrayList<String> getGame (String location, String exertion) {
		ArrayList<String> game = new ArrayList<>();
		//List<String> pains = getPains(userId);
		
		// Define filter expressions (bebause location is a protected name
		Map<String, String> expressionAttributeNames = new HashMap<String, String>();
		expressionAttributeNames.put("#loc", "location");
		expressionAttributeNames.put("#ext", "exertion");
		Map<String, AttributeValue> expressionAttributeValues = new HashMap<String, AttributeValue>();
		expressionAttributeValues.put(":loc", new AttributeValue().withS(location));
		expressionAttributeValues.put(":ext", new AttributeValue().withS(exertion));
		// Scan the table with defined filters 
		ScanRequest scanRequest = new ScanRequest();
		scanRequest.withTableName(gamesTable);
		scanRequest.withFilterExpression("#loc = :loc AND #ext = :ext");
		scanRequest.withExpressionAttributeNames(expressionAttributeNames);
		scanRequest.withExpressionAttributeValues(expressionAttributeValues);
		
        log.info("DBGAME:" + location);
        log.info("DBGAME:" + exertion);
		try{
			Map<String, AttributeValue> item = loadFromDb(scanRequest);
			// Put key and description in arrayLIst to return
			game.add(item.get("name").toString());
			game.add(item.get("description").toString());
			return game;

		} catch (Exception e){
			log.info("LOADGAME EXCEPTION: " + e.getMessage());
			game.add("Beim Laden der Übung ist ein Fehler aufgetreten, das tut mir leid. Versuche es bitte später noch eimal");
			game.add("");
			return game;
		}
	}
	
	// Loads an exercise from the db and returns it to the main class
	public ArrayList<String> getExercise(String userId, String location, String bodypart) {
		log.info("DATABASE GETEXERCISE");
		ArrayList<String> exercise = new ArrayList<>();
		// Define filter expressions (bebause location is a protected name
		Map<String, String> expressionAttributeNames = new HashMap<String, String>();
		expressionAttributeNames.put("#loc", "location");
		expressionAttributeNames.put("#bod", "bodypart");
		Map<String, AttributeValue> expressionAttributeValues = new HashMap<String, AttributeValue>();
		expressionAttributeValues.put(":loc", new AttributeValue().withS(location));
		expressionAttributeValues.put(":bod", new AttributeValue().withS(bodypart));
		// Scan the table with defined filters
		ScanRequest scanRequest = new ScanRequest();
		scanRequest.withTableName(exercisesTable);
		scanRequest.withFilterExpression("#loc = :loc AND #bod = :bod");
		scanRequest.withExpressionAttributeNames(expressionAttributeNames);
		scanRequest.withExpressionAttributeValues(expressionAttributeValues);

		try{
			// Counter so that the program doesnt get stuck while loading an unfitting exercise
			int counter = 0; 
			boolean exerciseContainsPain = true; 
			Map<String, AttributeValue> item = loadFromDb(scanRequest);
			String includedBodypart = item.get("bodypart").getS();
			List<String> blacklistedBodyparts = getPains(userId);
			// Repeat loading of an exercise, if the current one contains a blacklisted bodypart
			while(exerciseContainsPain == true || counter >= 5) {
				
				if(blacklistedBodyparts.contains(includedBodypart)) {
					log.info("Übereinstimmung");
					item = loadFromDb(scanRequest);
					includedBodypart = item.get("bodypart").getS();
				} else {
					log.info("Keine Übereinstimmung");
					exercise.add(item.get("name").toString());
					exercise.add(item.get("description").toString());
					exerciseContainsPain = false; 
				}
			}
			
			if(exercise.isEmpty()) {
				exercise.add("");
				exercise.add("Es tut mir leid, ich konnte keine Übung laden, die zu Ihren Angaben passt. Versuchen Sie es später noch einmal, ich werde mich nach neuen Übungen umschauen. ");
			}
			
			return exercise; 

		} catch (Exception e){
			log.info("DATABASE GETEXERCISE CATCH");
			log.info("EXERCISE ERROR: " + e.getMessage());
			exercise.add("Beim Laden der Übung ist ein Fehler aufgetreten, das tut mir leid. Versuche es bitte später noch eimal");
			exercise.add("");
			return exercise;
		}
	}

	// Loads an occupation from the db and returns it to the main class
	public ArrayList<String> getOccupation(String location, String exertion) {
		ArrayList<String> occupation = new ArrayList<>();
		// Define filter expressions (bebause location is a protected name
		Map<String, String> expressionAttributeNames = new HashMap<String, String>();
		expressionAttributeNames.put("#loc", "location");
		expressionAttributeNames.put("#ext", "exertion");
		Map<String, AttributeValue> expressionAttributeValues = new HashMap<String, AttributeValue>();
		expressionAttributeValues.put(":loc", new AttributeValue().withS(location));
		expressionAttributeValues.put(":ext", new AttributeValue().withS(exertion));
		// Scan the table with defined filters
		ScanRequest scanRequest = new ScanRequest();
		scanRequest.withTableName(occupationsTable);
		scanRequest.withFilterExpression("#loc = :loc AND #ext = :ext");
		scanRequest.withExpressionAttributeNames(expressionAttributeNames);
		scanRequest.withExpressionAttributeValues(expressionAttributeValues);

		try{
			Map<String, AttributeValue> item = loadFromDb(scanRequest);
			// Put key and description in arrayLIst to return
			occupation.add(item.get("name").toString());
			occupation.add(item.get("description").toString());
			return occupation;

		} catch (Exception e){
			occupation.add("Beim Laden der Beschäftigung ist ein Fehler aufgetreten, das tut mir leid. Versuche es bitte später noch eimal");
			occupation.add("");
			return occupation;
		}
	}

	// Returns random item from db specified in request
	private Map<String, AttributeValue> loadFromDb(ScanRequest scanRequest) throws Exception {
		// Write all items in list
		ScanResult result = client.scan(scanRequest);
		List<Map<String, AttributeValue>> items = result.getItems();
		// Get random item from list (map holds several key-value-pairs)
		return items.get((int) Math.floor(Math.random() * items.size()));
	}
}
