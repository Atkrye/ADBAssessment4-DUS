package gameLogic;

/**This enum tracks the 8 states that the game can be in.*/
public enum GameState {
    NORMAL,
    PLACING_TRAIN,
    PLACING_RESOURCE,
    ROUTING,
    ANIMATING,
    WAITING, 
    CREATING_CONNECTION, 
    REMOVING_CONNECTION
    
}
