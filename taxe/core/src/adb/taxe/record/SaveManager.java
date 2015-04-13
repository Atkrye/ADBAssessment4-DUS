package adb.taxe.record;

import java.io.IOException;
import java.io.Writer;

import Util.Tuple;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;
import com.badlogic.gdx.utils.JsonWriter;

import gameLogic.Game;
import gameLogic.goal.GoalManager;
import gameLogic.map.Station;
import gameLogic.obstacle.ObstacleManager;
import gameLogic.player.Player;
import gameLogic.player.PlayerManager;
import gameLogic.resource.Resource;
import gameLogic.resource.ResourceManager;
import gameLogic.resource.Train;

public class SaveManager {
	private PlayerManager playerManager;
	public SaveManager(PlayerManager playerManager, GoalManager goalManager,
			ResourceManager resourceManager, ObstacleManager obstacleManager) {
		// TODO Auto-generated constructor stub
	}

	public void save(FileHandle file)
	{
		Json writer = new Json();
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
			    	  if(res.getClass().equals(Train.class))
			    	  {
			    		  Train train = (Train)res;
			    		  json.writeObjectStart();
			    		  json.writeValue("DataType", "Train");
			    		  json.writeValue("Name", train.toString());
			    		  json.writeValue("Speed", train.getSpeed());
			    		  json.writeValue("Image", train.getImage().split("/")[1]);
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
				    		  json.writeValue("Position", "Empty");
			    		  }
			    		  else
			    		  {
				    		  json.writeValue("Position", train.getPosition().getX() + "," + train.getPosition().getY());
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
			      
			      json.writeObjectEnd();
			   }

			   public Player read (Json json, JsonValue jsonData, Class type) {
				  for(JsonValue child = jsonData.child(); child != null; child = child.next())
				  {
					  
				  }
			      Player player = new Player(new PlayerManager(), 1, jsonData.child().asString());
			      return player;
			   }
			});
		writer.setSerializer(Resource.class, new Json.Serializer<Resource>() {
			   public void write (Json json, Resource res, Class knownType) {
			      json.writeObjectStart();
			      json.writeValue("DataType", "Resource");
			      json.writeValue("Name", res.toString());
			      json.writeObjectEnd();
			   }

			   public Resource read (Json json, JsonValue jsonData, Class type) {
				  for(JsonValue child = jsonData.child(); child != null; child = child.next())
				  {
					  
				  }
			      return null;
			   }
			});
		System.out.println(writer.prettyPrint(Game.getInstance().getPlayerManager().getCurrentPlayer()));
	}

}
