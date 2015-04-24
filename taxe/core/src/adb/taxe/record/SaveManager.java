package adb.taxe.record;


import fvs.taxe.GameScreen;
import gameLogic.Game;
import gameLogic.goal.Goal;
import gameLogic.map.Connection;
import gameLogic.map.Map;
import gameLogic.map.Station;
import gameLogic.obstacle.Obstacle;
import gameLogic.obstacle.ObstacleManager;
import gameLogic.player.Player;
import gameLogic.player.PlayerManager;
import gameLogic.resource.KamikazeTrain;
import gameLogic.resource.PioneerTrain;
import gameLogic.resource.Resource;
import gameLogic.resource.Skip;
import gameLogic.resource.Train;

import java.util.ArrayList;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileNameExtensionFilter;

import Util.Tuple;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;

public class SaveManager {
	private static boolean isRecording = false;
	private static Json writer = setupWriter();
	@SuppressWarnings("rawtypes")
	public static Json setupWriter()
	{
		Json writer = new Json();
		//Serializer to store and read all of the information related to the game
		writer.setSerializer(Game.class, new Json.Serializer<Game>() {
			   public void write (Json json, Game game, Class knownType) {
				   json.writeObjectStart();
				   //Data about the Game, e.g. the mode, the turn, the stations, connections and obstacles etc.
				   json.writeValue("DataType", "GameData");
				   json.writeValue("Mode", game.getMode());
				   json.writeValue("Max Turns", game.TOTAL_TURNS);
				   json.writeValue("Max Points", game.MAX_POINTS);
				   json.writeValue("Nighttime", game.getPlayerManager().isNight());
				   json.writeValue("Turn", game.getPlayerManager().getTurnNumber());
				   //Stations and connections formatted for Json importer as the code already exists
				   json.writeArrayStart("stations");
				   ArrayList<Obstacle> obstacles = new ArrayList<Obstacle>();
				   for(Station st : game.getMap().getStations())
				   {
					   json.writeObjectStart();
					   json.writeValue("name", st.getName());
					   json.writeValue("junction", st.isJunction);
					   json.writeValue("x", st.getPosition().getX());
					   json.writeValue("y", st.getPosition().getY());
					   json.writeObjectEnd();
					   if(st.hasObstacle())
					   {
						   obstacles.add(st.getObstacle());
					   }
				   }
				   json.writeArrayEnd();
				   json.writeArrayStart("connections");
				   for(Connection cn : game.getMap().getConnections())
				   {
					   json.writeObjectStart();
					   json.writeValue("station1", cn.getStation1().getName());
					   json.writeValue("station2", cn.getStation2().getName());
					   json.writeObjectEnd();
				   }
				   json.writeArrayEnd();
				   json.writeArrayStart("obstacles");
				   for(Tuple<Obstacle, Float> obstacle : game.getObstacleManager().getObstacles())
				   {
					   Obstacle ob = obstacle.getFirst();
					   json.writeObjectStart();
					   json.writeValue("Obstacle Type", ob.getType());
					   json.writeValue("Time left", ob.getTimeLeft());
					   json.writeValue("Probability", obstacle.getSecond());
					   json.writeValue("x", ob.getPosition().getX());
					   json.writeValue("y", ob.getPosition().getY());
					   json.writeValue("Station", ob.getStation().getName());
					   json.writeValue("Active", ob.isActive());
					   json.writeObjectEnd();
				   }
				   json.writeArrayEnd();
				   json.writeValue("Player1", game.getPlayerManager().getAllPlayers().get(0));
				   json.writeValue("Player2", game.getPlayerManager().getAllPlayers().get(1));
				   json.writeObjectEnd();
			   }
			   
			   public Game read (Json json, JsonValue jsonData, Class type) {
				  String mode = jsonData.getString("Mode");
				  int maxTurns = jsonData.getInt("Max Turns");
				  int maxPoints = jsonData.getInt("Max Points");
				  boolean isNight = jsonData.getBoolean("Nighttime");
				  int turn = jsonData.getInt("Turn");
				  Map m = new Map(true, jsonData);
				  ObstacleManager om = new ObstacleManager(m, jsonData);
				  PlayerManager pm = new PlayerManager(jsonData, isNight, turn, m);
				  Game g = new Game(mode, maxTurns, maxPoints, pm, m, om, isRecording);
				  Game.setInstance(g, false);
			      return g;
			   }
		});
		
		//Serializer to store and read all of the information related to a player
		writer.setSerializer(Player.class, new Json.Serializer<Player>() {
			   public void write (Json json, Player player, Class knownType) {
			      json.writeObjectStart();
			      json.writeValue("DataType", "Player" + player.getPlayerNumber());
			      json.writeValue("Name", player.getName());
			      json.writeValue("Score", player.getScore());
			      json.writeValue("Skip", player.getSkip());
			      if(Game.getInstance().getPlayerManager().getCurrentPlayer().equals(player))
			      {
				      json.writeValue("Active", true);
			      }
			      else
			      {
			    	  json.writeValue("Active", false);
			      }
			      json.writeArrayStart("Resources");
			      for(Resource res : player.getResources())
			      {
			    	  if(res.getClass().equals(Skip.class))
			    	  {

			    		  json.writeObjectStart();
			    		  json.writeValue("DataType", "Skip");
			    		  json.writeObjectEnd();
			    	  }
			    	  else if(res.getClass().equals(Train.class) || res.getClass().equals(KamikazeTrain.class) || res.getClass().equals(PioneerTrain.class))
			    	  {
			    		  Train train = (Train)res;
			    		  json.writeObjectStart();
			    		  json.writeValue("DataType", "Train");
			    		  json.writeValue("ID", train.getID());
			    		  json.writeValue("Name", train.toString());
			    		  json.writeValue("Speed", train.getSpeed());
			    		  json.writeValue("Image", train.getImage().split("/")[1]);
			    		  json.writeValue("HasRoute", !train.getRoute().isEmpty());
			    		  if(res.getClass().equals(KamikazeTrain.class))
			    		  {
				    		  json.writeValue("Special", "Kamikaze");
			    		  }
			    		  if(res.getClass().equals(PioneerTrain.class))
			    		  {
				    		  json.writeValue("Special", "Pioneer");
				    		  json.writeValue("Creating", ((PioneerTrain)train).isCreating());
				    		  if(((PioneerTrain)train).isCreating())
				    		  {
				    			  json.writeValue("TargetStation", ((PioneerTrain)train).getConnection().getStation2().getName());
				    		  }
			    		  }
			    		  if(train.getFinalDestination() == null)
			    		  {
				    		  json.writeValue("Destination", "Empty");
			    		  }
			    		  else
			    		  {
			    			  json.writeValue("Destination", train.getFinalDestination().getName());
			    		  }
			    		  if(train.getPosition() == null)
			    		  {
				    		  json.writeValue("x", "Empty");
				    		  json.writeValue("y", "Empty");
			    		  }
			    		  else
			    		  {
				    		  json.writeValue("x", train.getPosition().getX());
				    		  json.writeValue("y", train.getPosition().getY());
				    		  if(train.getPosition().getX() == -1 && train.getPosition().getY() == -1)
				    		  {
				    			json.writeValue("actorX", train.getActor().getBounds().getX()); 
				    			json.writeValue("actorY", train.getActor().getBounds().getY()); 
				    			json.writeValue("actorRot", train.getActor().getBounds().getRotation());
				    		  }
			    		  }
			    		  json.writeArrayStart("Route");
			    		  if(train.getRoute() == null || train.getRoute().isEmpty())
			    		  {
			    			  json.writeObjectStart();
			    			  json.writeValue("DataType", "Empty");
			    			  json.writeObjectEnd();
			    		  }
			    		  else
			    		  {
			    			  for(Station st : train.getRoute())
			    			  {
			    				  json.writeObjectStart();
			    				  json.writeValue("DataType", "Station");
			    				  json.writeValue("Name", st.getName());
			    				  json.writeObjectEnd();
			    			  }
			    		  }
			    		  json.writeArrayEnd();
			    		  json.writeArrayStart("History");
			    		  if(train.getHistory() == null || train.getHistory().isEmpty())
			    		  {
			    			  json.writeObjectStart();
			    			  json.writeValue("DataType", "Empty");
			    			  json.writeObjectEnd();
			    		  }
			    		  else
			    		  {
			    			  for(Tuple<Station, Integer> historyObject : train.getHistory())
			    			  {
			    				  json.writeObjectStart();
			    				  json.writeValue("DataType", "Station");
			    				  json.writeValue("Name", historyObject.getFirst().getName());
			    				  json.writeValue("Turn", historyObject.getSecond());
			    				  json.writeObjectEnd();
			    			  }
			    		  }
			    		  json.writeArrayEnd();
			    		  json.writeObjectEnd();
			    	  }
			      }
			      json.writeArrayEnd();
			      json.writeArrayStart("Goals");
			      for(Goal g : player.getGoals())
			      {
			    	  json.writeObjectStart();
			    	  json.writeValue("Origin", g.getOrigin().getName());
			    	  json.writeValue("Destination", g.getDestination().getName());
			    	  if(g.getIntermediary() != null)
			    	  {
			    		  json.writeValue("Intermediate", g.getIntermediary().getName());
			    	  }
			    	  else
			    	  {
			    		  json.writeValue("Intermediate", "None");
			    	  }
			    	  if(g.getTrain() != null)
			    	  {
			    		  json.writeValue("Train", g.getTrain().getName());
			    	  }
			    	  else
			    	  {
			    		  json.writeValue("Train", "None");
			    	  }
			    	  json.writeValue("Turn", g.getTurn());
			    	  json.writeValue("TurnCount", g.getTurnsTime());
			    	  json.writeValue("ScoreValue", g.getScore());
			    	  json.writeValue("BonusValue", g.getBonus());
			    	  json.writeObjectEnd();
			      }
			      json.writeArrayEnd();
			      json.writeObjectEnd();
			   }

			   public Player read (Json json, JsonValue jsonData, Class type) {
			      return null;
			   }
			});
		
		//Writer for storing events. We have to use a container to get around the abstraction of Lists in Java
		writer.setSerializer(EventArrayContainer.class, new Json.Serializer<EventArrayContainer>() {
			   
			public void write (Json json, EventArrayContainer eventContainer, Class knownType) {
				   ArrayList<Event> events = eventContainer.events;
				   json.writeObjectStart();
				   json.writeArrayStart("Recording");
				   for(Event event : events)
				   {
					   event.toJson(json);
				   }
				   json.writeArrayEnd();
				   json.writeObjectEnd();
			   }
			   
			   public EventArrayContainer read (Json json, JsonValue jsonData, Class type) {
				  ArrayList<Event> events = new ArrayList<Event>();
				  for(JsonValue eventData = jsonData.getChild("Recording"); eventData != null; eventData = eventData.next())
				  {
					  String dataType = eventData.getString("Type");
					  if(dataType.equals("Game Data"))
					  {
						  Game g = json.readValue(Game.class, new JsonReader().parse(eventData.getString("Data")));
						  EmbeddedSaveData game = new EmbeddedSaveData(g);
						  events.add(game);
					  }
					  else if(dataType.equals("Click"))
					  {
						  events.add(new ClickEvent(eventData.getInt("x"), eventData.getInt("y")));
					  }
					  else if(dataType.equals("Key"))
					  {
						  events.add(new KeyEvent(eventData.getInt("Keycode")));
						  
					  }
					  else if(dataType.equals("Char"))
					  {
						  events.add(new CharEvent(eventData.getChar("CharValue")));
					  }
					  else if(dataType.equals("Obstacle"))
					  {
						  events.add(new ObstacleEvent(eventData.getString("ObstacleType"), eventData.getString("ObstacleStation")));
					  }
					  else if (dataType.equals("Goal"))
					  {
						  String origin = eventData.getString("Origin");
						  String destination = eventData.getString("Destination");
						  String intermediary = eventData.getString("Intermediary");
						  int turn = eventData.getInt("Turn");
						  int turnsTime = eventData.getInt("TurnsTime");
						  int score = eventData.getInt("Score");
						  int bonus = eventData.getInt("Bonus");
						  String train = eventData.getString("Train");
						  events.add(new GoalEvent(origin, destination, intermediary, turn, turnsTime, score, bonus, train));
					  }
					  else if (dataType.equals("Resource"))
					  {
						  events.add(new ResourceEvent(eventData.getString("Name")));
					  }
				  }
				  return new EventArrayContainer(events);
				  
			   }
		});
		return writer;
	}

	public static void save()
	{
		saveFromChooser();
	}
	
	public static Game load()
	{
		return loadFromChooser();
	}
	
	public static void saveFromChooser() {
		
		//Create filter so only .taxe files may be loaded
		FileNameExtensionFilter filter = new FileNameExtensionFilter("TaxE Saves", "taxe");	
		//Instantiate new JFileChooser, default directory the local root
		JFileChooser chooser = new JFileChooser(Gdx.files.getLocalStoragePath());
		chooser.setDialogTitle("Create or overwrite a save game");
		chooser.setApproveButtonText("Save");
		chooser.setApproveButtonToolTipText("Save to this file");
		//Apply filter created above
		chooser.setAcceptAllFileFilterUsed(true);
		chooser.addChoosableFileFilter(filter);
		chooser.setFileFilter(filter); 
		String loadFilePath = "";
		// Open Dialog for File Choosing and assign absolute path to loadFilePath
		int returnVal = chooser.showSaveDialog(chooser);
	    if(returnVal == JFileChooser.APPROVE_OPTION) {
	       loadFilePath = chooser.getSelectedFile().getAbsolutePath();
	    }
	    else {
	    	return;
	    }
	    if(!loadFilePath.endsWith(".taxe"))
	    {
	    	loadFilePath = loadFilePath + ".taxe";
	    }
	    
	    //Load file into libgdx's FileHandle System using absolute path.
	    //Have to create a new FileHandle loadedFromChooser.. Doesn't work without this
	    FileHandle loadedFromChooser = Gdx.files.absolute(loadFilePath);
	    //Make our chosen save file
	    save(loadedFromChooser);
	}
	
	public static void saveRecordingFromChooser(String jsonData) {
		
		GameScreen.instance.pause();
		//Create filter so only .taxe files may be loaded
		FileNameExtensionFilter filter = new FileNameExtensionFilter("TaxE Recordings", "taxeR");	
		//Instantiate new JFileChooser, default directory the local root
		JFileChooser chooser = new JFileChooser(Gdx.files.getLocalStoragePath());
		chooser.setDialogTitle("Save your recording");
		chooser.setApproveButtonText("Save");
		chooser.setApproveButtonToolTipText("Save to this file");
		//Apply filter created above
		chooser.setAcceptAllFileFilterUsed(true);
		chooser.addChoosableFileFilter(filter);
		chooser.setFileFilter(filter); 
		String loadFilePath = "";
		// Open Dialog for File Choosing and assign absolute path to loadFilePath
		int returnVal = chooser.showSaveDialog(chooser);
	    if(returnVal == JFileChooser.APPROVE_OPTION) {
	       loadFilePath = chooser.getSelectedFile().getAbsolutePath();
	    }
	    else {
	    	return;
	    }
	    if(!loadFilePath.endsWith(".taxeR"))
	    {
	    	loadFilePath = loadFilePath + ".taxeR";
	    }
	    
	    //Load file into libgdx's FileHandle System using absolute path.
	    //Have to create a new FileHandle loadedFromChooser.. Doesn't work without this
	    FileHandle loadedFromChooser = Gdx.files.absolute(loadFilePath);
	    //Make our chosen save file
	    loadedFromChooser.writeString(jsonData, false);
		GameScreen.instance.resume();
	}
	
	public static void loadRecordingFromChooser() {
		
		//Create filter so only .taxe files may be loaded
		FileNameExtensionFilter filter = new FileNameExtensionFilter("TaxE Recordings", "taxeR");	
		
		//Instantiate new JFileChooser, default directory the local root
		JFileChooser chooser = new JFileChooser(Gdx.files.getLocalStoragePath());
		chooser.setDialogTitle("Load a game");
		chooser.setApproveButtonText("Load");
		chooser.setApproveButtonToolTipText("Load this file");
		//Apply filter created above
		chooser.setAcceptAllFileFilterUsed(true);
		chooser.addChoosableFileFilter(filter);
		chooser.setFileFilter(filter); 
		String loadFilePath = "";
		// Open Dialog for File Choosing and assign absolute path to loadFilePath
		int returnVal;
		while(true)
		{
			returnVal = chooser.showOpenDialog(chooser);
		    if(returnVal == JFileChooser.APPROVE_OPTION &&
		      !chooser.getSelectedFile().exists())
		        JOptionPane.showMessageDialog(null, "You must select an existing file!");
		    else break;
		}
	    if(returnVal == JFileChooser.APPROVE_OPTION) {
	       loadFilePath = chooser.getSelectedFile().getAbsolutePath();
	    }
	    else {
	    }
	    if(!loadFilePath.endsWith(".taxeR"))
	    {
	    	loadFilePath = loadFilePath + ".taxeR";
	    }
	    
	    RecordingWindow.createNewRecordingWindow(loadFilePath);
	}
	
	public static Game loadFromChooser() {
		
		//Create filter so only .taxe files may be loaded
		FileNameExtensionFilter filter = new FileNameExtensionFilter("TaxE Saves", "taxe");	
		
		//Instantiate new JFileChooser, default directory the local root
		JFileChooser chooser = new JFileChooser(Gdx.files.getLocalStoragePath());
		chooser.setDialogTitle("Load a game");
		chooser.setApproveButtonText("Load");
		chooser.setApproveButtonToolTipText("Load this file");
		//Apply filter created above
		chooser.setAcceptAllFileFilterUsed(true);
		chooser.addChoosableFileFilter(filter);
		chooser.setFileFilter(filter); 
		String loadFilePath = "";
		// Open Dialog for File Choosing and assign absolute path to loadFilePath
		int returnVal;
		while(true)
		{
			returnVal = chooser.showOpenDialog(chooser);
		    if(returnVal == JFileChooser.APPROVE_OPTION &&
		      !chooser.getSelectedFile().exists())
		        JOptionPane.showMessageDialog(null, "You must select an existing file!");
		    else break;
		}
	    if(returnVal == JFileChooser.APPROVE_OPTION) {
	       loadFilePath = chooser.getSelectedFile().getAbsolutePath();
	    }
	    else {
	    	return null;
	    }
	    if(!loadFilePath.endsWith(".taxe"))
	    {
	    	loadFilePath = loadFilePath + ".taxe";
	    }
	    
	    //Load file into libgdx's FileHandle System using absolute path.
	    //Have to create a new FileHandle loadedFromChooser.. Doesn't work without this
	    FileHandle loadedFromChooser = Gdx.files.absolute(loadFilePath);
	    //Load our save game. If the load throws any error, give the user an error message!
	    Game retGame;
	    try
	    {
	    	retGame = load(loadedFromChooser);
	    }
	    catch(Exception e)
	    {
	    	e.printStackTrace();
	    	retGame = null;
	    }
	    if(retGame == null)
	    {
	        JOptionPane.showMessageDialog(null, "Corrupt data: Game could not be loaded!");
	        return null;
	    }
	    else
	    {
	    	return retGame;
	    }
	}

	public static void save(FileHandle file)
	{
		System.out.println("SAVE!");
		String fileData = writer.prettyPrint(Game.getInstance());
		file.writeString(fileData, false);
	}
	
	public static String getSaveText()
	{
		return writer.prettyPrint(Game.getInstance());
	}
	
	public static Game load(FileHandle file)
	{

		JsonReader jsonReader = new JsonReader();
		JsonValue jsonData = jsonReader.parse(file);
		Game g = writer.readValue(Game.class, jsonData);
		return g;
	}
	
	/**Loads a recording from a file. The save game is stored as the first event in the recording
	 * @param file The file to be loaded
	 * @return The list of events, including the game, returned
	 */
	public static ArrayList<Event> loadRec(FileHandle file)
	{
		isRecording = true;
		JsonReader jsonReader = new JsonReader();
		JsonValue jsonData = jsonReader.parse(file);
		ArrayList<Event> events = writer.readValue(EventArrayContainer.class, jsonData).events;
		isRecording = false;
		return events;
	}
	
	public static Game loadFromText(String text)
	{
		return writer.readValue(Game.class, new JsonReader().parse(text));
	}

	public static void saveRecording(String gameData, ArrayList<Event> events) {
		events.add(0, new EmbeddedJsonData(gameData));
		saveRecordingFromChooser(writer.prettyPrint(new EventArrayContainer(events)));
	}

}
