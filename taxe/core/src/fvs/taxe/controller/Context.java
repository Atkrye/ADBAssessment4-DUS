package fvs.taxe.controller;

import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import fvs.taxe.TaxeGame;
import gameLogic.Game;

/**This class stores the Context of the game, such as the game itself, the stage, etc.*/
public class Context {
    //Context appears to be a class that allows different aspects of the system access parts that they otherwise logically shouldn't have access to.
    //While this is a bit of a workaround to make implementation easier, it does weaken encapsulation somewhat, however a full system overhaul would be unfeasible to remedy this.
    
	
	/**The main Instance of the game is storedhere.*/
    private TaxeGame taxeGame;
    
    /**The stage of the game is stored here.*/
    private Stage stage;
    
    /**The skin used for UI in the game is stored here.*/
    private Skin skin;
    
    /**A RouteController for the context that can be get or set.*/
    private RouteController routeController;
    
    /**A TopBarController for the context that can be get or set.*/
    private TopBarController topBarController;
    
    /** A connectionController for the context that can be get or set.*/
	private ConnectionController connectionController;

	/**Instantiation method sets up private variables.
     * @param stage The stage to be used in the context
     * @param skin The skin to be used in the context
     * @param taxeGame The main Game instance to be used in the context
     * @param gameLogic The Game's logic instance to be used in the context
     */
    public Context(Stage stage, Skin skin, TaxeGame taxeGame) {
        this.stage = stage;
        this.skin = skin;
        this.taxeGame = taxeGame;
    }

    //Getters and setters: pretty self-explanatory
    public Stage getStage() {
        return stage;
    }

    public Skin getSkin() {
        return skin;
    }

    public TaxeGame getTaxeGame() {
        return taxeGame;
    }

    public Game getGameLogic() {
        return Game.getInstance();
    }

    public RouteController getRouteController() {
        return routeController;
    }

    public void setRouteController(RouteController routeController) {
        this.routeController = routeController;
    }

    public TopBarController getTopBarController() {
        return topBarController;
    }

    public void setTopBarController(TopBarController topBarController) {
        this.topBarController = topBarController;
    }

	public ConnectionController getConnectionController() {
		return connectionController;
	}
	
	public void setConnectionController(ConnectionController connectionController) {
		this.connectionController = connectionController;
	}
}
