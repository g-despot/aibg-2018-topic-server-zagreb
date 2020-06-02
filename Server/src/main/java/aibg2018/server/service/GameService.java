package aibg2018.server.service;

import hr.best.aibg2018.logic.game.Game;

public interface GameService {

	/**
	 * Performs an action in given game and returns update game once other player
	 * has made a move. If other player did not make a move within timeout returns
	 * <code>null</code>.
	 * 
	 * @param gameId
	 * @param playerId
	 * @param action
	 * @return
	 */
	public Game doAction(Integer gameId, Integer playerId, String action);

	/**
	 * Joins a game with already assigned players.
	 * 
	 * @param gameId
	 * @param playerId
	 * @return
	 */
	public Game playGame(Integer gameId, Integer playerId);

	// ----------
	// BELOW CODE IS LOCAL VERSION ONLY
	// ----------

	/**
	 * Joins a random game and returns game object once there are two players joined
	 * and it's this player's turn. If no player joins before timeout returns
	 * <code>null</code>.
	 * 
	 * @param playerId
	 * @return
	 */
	public Game randomGame(Integer playerId);

	/**
	 * Creates a new game and returns generated game id. Once game is created both
	 * players need to join it for the game to start.
	 * 
	 * @param playerId
	 * @return
	 */
	public Integer newGame(Integer playerId);

	/**
	 * Joins an existing game and waits until both players are registered. Returns
	 * game object once it is this player's turn to play. If no other player has
	 * joined before timeout, or other player has joined but was first and his move
	 * timed out returns <code>null</code>.
	 * 
	 * @param gameId
	 * @param playerId
	 * @return
	 */
	public Game joinGame(Integer gameId, Integer playerId);

	/**
	 * Creates a new game object with a random map or if map name is not
	 * <code>null</code> map with that name. If given id is not
	 * <code>null</code>assigns it the given id.
	 * 
	 * @throws IllegalArgumentException if id is not <code>null</code> and a game
	 *                                  with that id already exists in managed games
	 */
	public Game createGame(Integer id, String mapName);

	/**
	 * Adds this game to managed games.
	 * 
	 * @throws IllegalArgumentException if game is not properly initialized
	 */
	public void addGame(Game game);

	Game getLast();

	Game getById(int gameId);
}
