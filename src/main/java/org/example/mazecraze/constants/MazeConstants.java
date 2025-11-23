package org.example.mazecraze.constants;

public class MazeConstants {
    // Welcome and gameplay messages
    public static final String WELCOME_MESSAGE = "WELCOME TO MazeCraze!";
    public static final String MOVEMENT_PROMPT = "PRESS N, S, W, E TO NAVIGATE THROUGH THE MAZE. YOU ARE ";
    public static final String INVALID_COMMAND = "YOU: INVALID COMMAND!";
    public static final String YOU_PREFIX = "YOU:";
    public static final String WIN_MESSAGE = "YOU HAVE WON!";
    public static final String CONTINUE_PROMPT = "QUIT OR CONTINUE PLAYING...";
    public static final String SERVER_PING_MESSAGE = "Got a connection";
    // Gold and distance queries
    public static final String SEE_GOLD_COMMAND = "sg";
    public static final String GOLD_MESSAGE = "YOU CAN COLLECT ";
    public static final String GOLD_SUFFIX = " UNITS OF GOLD AT CURRENT POSITION";

    public static final String DISTANCE_COMMAND = "sd";
    public static final String DISTANCE_MESSAGE = "YOU ARE AT LEAST ";
    public static final String DISTANCE_SUFFIX = " UNITS AWAY FROM DESTINATION AT CURRENT POSITION";

    // Movement commands
    public static final String NORTH = "n";
    public static final String SOUTH = "s";
    public static final String WEST = "w";
    public static final String EAST = "e";

    // Server configuration
    public static final int SERVER_PORT = 8080;
    public static final int MAX_PLAYERS = 2;
}
