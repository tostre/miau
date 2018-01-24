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
import com.amazonaws.services.dynamodbv2.document.spec.GetItemSpec;
import com.amazonaws.services.dynamodbv2.document.spec.QuerySpec;
import com.amazonaws.services.dynamodbv2.document.spec.ScanSpec;
import com.amazonaws.services.dynamodbv2.document.spec.UpdateItemSpec;
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
	
	// Creates a new table with specified key schema
	public void createTable(String tableName, List<KeySchemaElement> keySchema) {
		CreateTableRequest req = new CreateTableRequest(tableName, keySchema);
		dynamoDB.createTable(req);
	}
	
	// returns a table specified by name
	private Table getTable(String tableName) {
		return dynamoDB.getTable(tableName);
	}
	
	// Puts an item in a table, overwrites the old one if it already exists
	public void putItem(String tableName, Item item) {
		Table table = getTable(tableName);
		table.putItem(item);
	}
	
	
	///////////////////////////////////////////////
	// UNCOMPLETE METHODS //////////////////////////////
	//////////////////////////////////////////////
	
	// TODO: Wirte this methode Updates an already existing item
	public void updateItem() {
		
	}
	
	public void saveUser(String userId, String userName, List<String> excludedBodyparts, boolean formalSpeech, boolean setupComplete) {
		Item item = new Item(); 
		item.withString("id", userId);
		item.withString("name", userName);
		item.withList("exludedBodyParts", excludedBodyparts);
		item.withBoolean("formalSpeech", formalSpeech);
		item.withBoolean("firstSetupComplete", setupComplete);
		
		getTable(usersTable).putItem(item);
	}
	
	public void saveUser(User user) {
		Item item = new Item(); 
		item.withString("id", user.getId());
		item.withString("name", user.getName());
		item.withList("exludedBodyParts", user.getExcludedBodyparts());
		item.withBoolean("formalSpeech", user.preferesFormalSpeech());
		item.withBoolean("firstSetupComplete", user.isSetupComplete());
		
		getTable(usersTable).putItem(item);
	}
	
	public void updateName(String userId, String userName) {
		Map<String, String> attributeNames = new HashMap<String, String>();
		attributeNames.put("id", userId);
		
		Map<String, Object> attributeValues = new HashMap<String, Object>();
		attributeValues.put("name", userName);
		
		UpdateItemSpec spec = new UpdateItemSpec(); 
		//AttributeUpdate update = new AttributeUpdate();
		//update.
		
		//getTable(usersTable).up
	}
	
	public void updateFormalSpeech(boolean formalSpeech) {
		
	}
	
	public void updateExcludedBodyparts(List<String> excludedBodyparts) {
		// Get List of already excluded bodyparts
		// Combine listst
		// Save list
	}
	
	///////////////////////////////////////////////
	// READ USER DATA FROM DB //////////////////////////////
	//////////////////////////////////////////////
	
	public User getUser(String userId) {
		Table table = getTable(usersTable);
		GetItemSpec spec = new GetItemSpec().withPrimaryKey("id", userId);
		
		try {
			Item userItem = table.getItem(spec);
			
			return new User(userItem.getString("id"), userItem.getString("name"), userItem.getBoolean("formalSpeech"), userItem.getBoolean("firstSetupComplete"), new ArrayList<String>(userItem.getStringSet("excludedBodyParts"))); 
		} catch (Exception e){
			return null;
		}
	}
	
	
	public void createNewUser(String userId) {
		Table table = getTable(usersTable);
		//TODO: Create a new empty user (delete whatever is not needed)
		//excludedBodyParts.add("schulter");
 		//excludedBodyParts.add("knie");
 		
 		Item item = new Item(); 
 		item.withNumber("id", 3);
 		item.withString("name", "Walter");
 		//item.withList("excludedBodyParts", excludedBodyParts);
		
		
		table.putItem(item);
	}
	
	
	
	
	
	
	//TODO: die beiden Strings ausgeben, gucken wie die aufgebaut sind und wie man den namen da raus bekommt 
	public String getUserName(String userId) {
		Table table = getTable(usersTable);
		Item item = table.getItem("id", userId);
		
		String itemJson = item.toJSONPretty();
		String itemString = item.toString(); 
		
		log.info("ITEMJSON: " + itemJson);
		log.info("ITEMSTRING: " + itemString);
		
		return null; 
	}
	
	public List<String> getExcludedBodyparts(String userId){
		return null; 
	}
	
	public boolean getFormalSpeech(String userId) {
		
		return false; 
	}
	
	public boolean getFirstSetupComplete(String userId) {
		return false; 
	}
	
	
	
	///////////////////////////////////////////////
	// READ ACTIVITY DATA FROM DB //////////////////////////////
	//////////////////////////////////////////////
	

	
	public ArrayList<String> getGame (String location, String exertion) {
		ArrayList<String> game = new ArrayList<>();
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

		try{
			Map<String, AttributeValue> item = loadFromDb(scanRequest);
			// Put key and description in arrayLIst to return
			game.add(item.get("name").toString());
			game.add(item.get("description").toString());
			return game;

		} catch (Exception e){
			game.add("Beim Laden der Übung ist ein Fehler aufgetreten, das tut mir leid. Versuche es bitte später noch eimal");
			game.add("");
			return game;
		}
		/*
		try{
			// Write all items in list
			ScanResult result = client.scan(scanRequest);
			List<Map<String, AttributeValue>> items = result.getItems();
			// Get random item from list (map holds several key-value-pairs)
			Map<String, AttributeValue> item = items.get((int) Math.floor(Math.random() * items.size()));
			// Get name and description by key-name
			String name = item.get("name").toString();
			String description = item.get("description").toString();
			// Put key and description in arrayLIst to return
			game.add(name);
			game.add(description);

			return game;

		} catch (Exception e){
			game.add("Beim Laden des Spiels ist ein Fehler aufgetreten, das tut mir leid. Versuche es bitte später noch eimal");
			game.add("");
			return game;
		}*/
	}
	
	public ArrayList<String> getExercise(String location, String bodypart) {
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
			Map<String, AttributeValue> item = loadFromDb(scanRequest);
			// Put key and description in arrayLIst to return
			exercise.add(item.get("name").toString());
			exercise.add(item.get("description").toString());
			return exercise;

		} catch (Exception e){
			exercise.add("Beim Laden der Übung ist ein Fehler aufgetreten, das tut mir leid. Versuche es bitte später noch eimal");
			exercise.add("");
			return exercise;
		}
	}
	
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
			occupation.add("Beim Laden der Übung ist ein Fehler aufgetreten, das tut mir leid. Versuche es bitte später noch eimal");
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
		Map<String, AttributeValue> item = items.get((int) Math.floor(Math.random() * items.size()));

		return items.get((int) Math.floor(Math.random() * items.size()));
	}
}
