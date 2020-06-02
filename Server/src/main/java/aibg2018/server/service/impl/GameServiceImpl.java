package aibg2018.server.service.impl;

import java.io.File;
import java.io.IOException;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Random;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import aibg2018.server.config.TeamNames;
import aibg2018.server.service.GameService;
import hr.best.aibg2018.logic.entites.Player;
import hr.best.aibg2018.logic.game.Game;
import hr.best.aibg2018.logic.game.GameException;
import hr.best.aibg2018.logic.map.Map;

@Service
public class GameServiceImpl implements GameService {

	public static File MAPS_FOLDER;

	public static final long GAME_JOIN_TIMEOUT = 600_000;
	public static final long MOVE_TIMEOUT = 2_000;
	public static final int MAP_SIZE = 20;

	private static final Logger LOG = LoggerFactory.getLogger(GameServiceImpl.class);

	private Hashtable<Integer, Game> games = new Hashtable<>();
	@SuppressWarnings("unused")
	private Hashtable<Integer, Player> players = new Hashtable<>();

	private Queue<Game> awaiting = new LinkedList<>();

	public GameServiceImpl() {
	}

	@Override
	public Game playGame(Integer gameId, Integer playerId) {

		Game game = games.get(gameId);
		if (game.getPlayer1().getId() != playerId && game.getPlayer2().getId() != playerId) {
			throw new RuntimeException("Not a player in this game");
		}

		if (!waitForMyMove(game, playerId))
			return null;

		return game;
	}

	// ----------
	// BELOW CODE IS LOCAL VERSION ONLY
	// ----------

	@Override
	public Game doAction(Integer gameId, Integer playerId, String action) {

		Game game = games.get(gameId);

		game.doAction(action, playerId);

		if (!waitForMyMove(game, playerId))
			return null;

		return game;
	}

	@SuppressWarnings("unlikely-arg-type")
	@Override
	public Game randomGame(Integer playerId) {

		Game game;

		synchronized (awaiting) {

			if ((game = awaiting.peek()) == null) {

				LOG.info("Player " + playerId + " is initiating a random game");
				game = createGame(null, null);
				game.setPlayer1(new Player(0, 0, playerId, getTeamName(playerId)));

				games.put(game.getId(), game);
				awaiting.add(game);
				LOG.info("Player " + playerId + " initiated a random game");
			} else {

				LOG.info("Player " + playerId + " is joining a random game");
				awaiting.poll();
				game.setPlayer2(new Player(MAP_SIZE - 1, MAP_SIZE - 1, playerId, getTeamName(playerId)));
				LOG.info("Player " + playerId + " has joined a random game");
			}
		}

		if (!waitForGameStart(game)) {
			synchronized (awaiting) {

				// make sure nobody has joined in meantime
				if (game.getPlayer2() == null) {
					awaiting.remove(game);
					games.remove(game);
					return null;
				}

				LOG.info("Somebody joined in meantime");
			}
		}

		if (!waitForMyMove(game, playerId)) {
			return null;
		}

		return game;
	}

	@Override
	public Game createGame(Integer id, String mapName) {

		if (id != null && games.containsKey(id)) {
			throw new IllegalArgumentException("Game with id=" + id + " already exists");
		}

		Map map;
		if (mapName != null) {
			map = loadMap(mapName);
		} else {
			File[] maps = MAPS_FOLDER.listFiles();
			map = new Map(maps[new Random().nextInt(maps.length)].toPath());
		}

		return id != null ? new Game(map, id) : new Game(map);
	}

	private Map loadMap(String mapName) {
		try {
			return new Map(new File(MAPS_FOLDER, mapName + ".txt").toPath());
		} catch (Exception ex) {
			System.out.println("Loading map exception: " + ex.getMessage());
			try {
				throw new GameException("Map " + MAPS_FOLDER.getCanonicalPath() + mapName + ".txt does not exist!");
			} catch (IOException e) {
				throw new GameException(MAPS_FOLDER.getAbsolutePath() + " not accesible");
			}
		}
	}

	private boolean waitForGameStart(Game game) {
		long start = System.currentTimeMillis();

		while (game.getPlayer2() == null) {
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				// no-op, should not happen
			}

			if (System.currentTimeMillis() > start + MOVE_TIMEOUT) {
				LOG.info("Game start timeout");
				return false;
			}
		}

		return true;
	}

	@Override
	public Integer newGame(Integer playerId) {
		Game game = createGame(null, null);
		game.setPlayer1(new Player(0, 0, playerId, getTeamName(playerId)));
		return game.getId();
	}

	@Override
	public Game joinGame(Integer gameId, Integer playerId) {
		Game game = games.get(gameId);

		synchronized (game) {
			if (game.getPlayer1() == null) {
				game.setPlayer1(new Player(0, 0, playerId, getTeamName(playerId)));
			} else if (game.getPlayer2() == null) {
				game.setPlayer2(new Player(MAP_SIZE - 1, MAP_SIZE - 1, playerId, getTeamName(playerId)));
			} else {
				throw new IllegalStateException("Game already populated");
			}
		}

		if (!waitForGameStart(game) || !waitForMyMove(game, playerId))
			return null;

		return game;
	}

	private boolean waitForMyMove(Game game, Integer playerId) {
		long start = System.currentTimeMillis();

		while (game.getNextPlayer().getId() != playerId.intValue() && game.winner == null) {
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				// no-op, should not happen
			}

			if (System.currentTimeMillis() > start + MOVE_TIMEOUT) {
				LOG.info("Player " + playerId + " timed out waiting for his move");
				return false;
			}
		}

		return true;
	}

	@Override
	public void addGame(Game game) {
		if (game == null || game.getPlayer1() == null || game.getPlayer2() == null) {
			throw new IllegalArgumentException("Invalid game object");
		}

		games.put(game.getId(), game);
	}

	public Game getLast() {
		Game y = null;
		for (Game x : games.values()) {
			y = x;
		}
		return y;
	}

	public Game getById(int gameId) {
		return games.get(gameId);
	}

	public static String getTeamName(int playerId) {
		return TeamNames.teamNames.get(playerId);
	}
}
