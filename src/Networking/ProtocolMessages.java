package Networking;

/**
 * PROTOCOL GROUP A
 * 
 * This is the standard protocol for Group A. Please, keep up to date with changes made
 * to this document through MS Teams. Send a message on the designated channel for any
 * questions of proposal for this Protocol.
 * 
 * The full documentation is given in the Protocol Descriptor file, so be sure to use
 * it as reference for these commands!
 */
public class ProtocolMessages {

	//--------------General Implementation--------------//
	/** Dimensions of a board supported by this protocol. */
	public static final int[] BOARD_DIMENSIONS = {15, 15};
	
	/** Command Separator: Used to separate values of a command sent to the server. */
	public static final String SEPARATOR = "|";

	/** Array Separator: Used to separate values within an argument. */
	public static final String AS = " ";
	//--------------------------------------------------//
	
	//-----------------Joining A Server-----------------//
	/** The first message sent by the client or server to initialize or approve a connection */
	public static final String HELLO = "HI";

	/** Broadcast by the server to all clients, so they are aware of a new connection */
	public static final String WELCOME = "W";
	//--------------------------------------------------//
	
	//-----------------Starting A Game------------------//
	/** The message sent by the server to all clients to indicate that it is ready to start.
	 * 	It is also followed by the names of the clients that are ready to start (if any). */
	public static final String SERVERREADY = "SRDY";
	
	/**	The message is sent by each client when they are ready. After sending this, they 
	 * 	should expect to receive a {@link #SERVERREADY} command containing their respective name */
	public static final String CLIENTREADY = "CRDY";
	//--------------------------------------------------//

	//-----------------Playing The Game-----------------//
	/**	This may be sent by the client to indicate that they are disconnecting from the server */
	public static final String ABORT = "A";

	/** This is sent by the server as the first command after starting a game. */
	public static final String START = "S";
	
	/**	This command is sent by the server to assign tiles to a connected client. */
	public static final String TILES = "T";

	/** Used to inform every client connected who has to make a move */
	public static final String TURN = "TURN";

	/** -> This is sent by the client to indicate a move on the board. 
	 * 	-> The move will then have to be validated by the server
	 * 	-> This message is then broadcasted by the server to all 
	 * 	connected clients to let them know of a valid move made by another player.
	 * 	-> The coordinates and the points the player gained are also sent as arguments. */
	public static final String MOVE = "M";

	/** This command is sent by the client to indicate they would like to pass their turn, and if they want to swap any tiles. */
	public static final String PASS = "PASS";
	
	/** This message is broadcast by the server to let all clients know that the game is over and to announce who the winner is. */
	public static final String GAMEOVER = "GO";
	//--------------------------------------------------//

	//----------------Chat Functionality----------------//
	/** A message is sent by a client to the server using this command. */
	public static final String MSGSEND = "MSGOUT";

	/** A message is broadcasted by the server to all clients using this command. */
	public static final String MSGRECEIVED = "MSGIN";
	//--------------------------------------------------//

	//----------------------Errors----------------------//
	/** When a player causes an error to occur in the server, this command is used. */
	public static final String ERROR = "E";
	
	/** Array of known error names (the values of all error codes).
	 *  
	 * -> This is private since it should not be used outside of this class*/
	private static final String[] ERRORNAMES = {"DuplicateName", "InvalidMove", 
			"OutOfTurn", "Unrecognized"};
	
	//Please check the protocol descriptor for specifications on every error, and when it should occur
	public static final String DUPLICATE_NAME = ERRORNAMES[0];
	public static final String INVALID_MOVE = ERRORNAMES[1];//For multiple reasons: (out of index, not empty field, not valid word)
	public static final String OUT_OF_TURN = ERRORNAMES[2];
	public static final String UNRECOGNIZED = ERRORNAMES[3];
	//--------------------------------------------------//
    
	//--------------------Features---------------------//
	public static final String CHAT_FLAG = "C";
	public static final String PASS_TURN_FLAG = "P";
	public static final String TEAM_PLAY_FLAG = "T";
	public static final String TURN_TIME_FLAG = "L";
	//-------------------------------------------------//
}
