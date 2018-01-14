package com.amazon.asksdk.spacegeek;

import java.awt.ItemSelectable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.document.AttributeUpdate;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.document.Table;
import com.amazonaws.services.dynamodbv2.document.spec.GetItemSpec;
import com.amazonaws.services.dynamodbv2.document.spec.UpdateItemSpec;
import com.amazonaws.services.dynamodbv2.model.CreateTableRequest;
import com.amazonaws.services.dynamodbv2.model.KeySchemaElement;
import com.amazonaws.services.inspector.model.Attribute;
import com.amazonaws.services.stepfunctions.model.ExecutionStartedEventDetails;

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
	
	public void saveUser(String userId, String userName, List<String> excludedBodyparts, boolean formalSpeech, boolean setupComplete, boolean introductionHeard) {
		Item item = new Item(); 
		item.withString("id", userId);
		item.withString("name", userName);
		item.withList("exludedBodyParts", excludedBodyparts);
		item.withBoolean("formalSpeech", formalSpeech);
		item.withBoolean("firstSetupComplete", setupComplete);
		item.withBoolean("introductionHeard", introductionHeard);
		
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
	
	public Item getUser(String userId) {
		Table table = getTable(usersTable);
		GetItemSpec spec = new GetItemSpec().withPrimaryKey("id", userId);
		
		try {
			Item userItem = table.getItem(spec);
			return userItem; 
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
	
	public boolean getIntroductionHeard(String userId) {
		return false; 
	}
	
	///////////////////////////////////////////////
	// READ ACTIVITY DATA FROM DB //////////////////////////////
	//////////////////////////////////////////////
	
	public String getGame(String location, String exertion) {
		return null; 
	}
	
	public String getExercise(String location, String bodypart) {
		return null; 
	}
	
	public String getOccupation(String location, String exertion) {
		return null; 
	}
}
