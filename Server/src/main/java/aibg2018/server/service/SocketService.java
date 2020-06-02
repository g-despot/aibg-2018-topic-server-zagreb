package aibg2018.server.service;

import java.io.IOException;

import org.springframework.web.socket.WebSocketSession;

import hr.best.aibg2018.logic.game.Game;

/**
 * Service for handling socket connections to users subscribed to notifications
 * related to game changes.
 */
public interface SocketService {

	/**
	 * String key under which game id should be stored in {@link WebSocketSession}'s
	 * parameters.
	 */
	public static final String GAME_ID = "SocketService_GAME_ID";

	/**
	 * Subscribes the user represented by {@link WebSocketSession} to notifications
	 * related to game whose id is present in session attributes under key
	 * {@link SocketService#GAME_ID}.
	 * 
	 * @param session
	 * @return whether the operation was successful
	 */
	boolean subscribe(WebSocketSession session);

	/**
	 * Unsubscribes the user represented by {@link WebSocketSession} from
	 * notifications related to game whose id is present in session attributes under
	 * key {@link SessionService#GAME_ID}.
	 * 
	 * @param session
	 * @return <code>true</code> if user was subscribed and unsubscribing was
	 *         successful
	 */
	boolean unsubscribe(WebSocketSession session);

	/**
	 * Notifies all users subscribed to notifications from given game's id.
	 * 
	 * @param game
	 * @throws IOException
	 */
	void notifySubscribed(Game game) throws IOException;

}
