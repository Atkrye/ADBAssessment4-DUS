package gameLogic;

//Enum to tell the system what state the game is in and what to draw
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
