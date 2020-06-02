package aibg2018.server.controller;

import java.io.IOException;
import java.net.URISyntaxException;

import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.fasterxml.jackson.core.JsonProcessingException;

import aibg2018.server.dao.GameEntity;
import aibg2018.server.dao.GameEntityDao;
import aibg2018.server.response.ErrorResponse;
import aibg2018.server.response.GameResponse;
import aibg2018.server.service.GameService;
import aibg2018.server.service.SocketService;
import hr.best.aibg2018.logic.game.Game;
import hr.best.aibg2018.logic.game.GameFinishedException;

@Controller
@CrossOrigin
public class GameController extends AbstractController {

	private static final Logger LOG = LoggerFactory.getLogger(GameController.class);

	@Autowired
	private GameService gameService;
	@Autowired
	private SocketService socketService;
	@Autowired
	private GameEntityDao gameEntityDao;

	@GetMapping("/game")
	@ResponseBody
	public Game getGame(@RequestParam("gameId") Integer gameId) throws URISyntaxException {
		return gameService.getById(gameId);
	}

	@GetMapping("/doAction")
	public void doAction(@RequestParam("gameId") Integer gameId, @RequestParam("playerId") Integer playerId,
			@RequestParam("action") String action, HttpServletResponse resp)
			throws JsonProcessingException, IOException, InterruptedException {
		Thread.sleep(100);
		LOG.info("Player " + playerId + " is doing an action '" + action + "'");

		Game game;

		try {
			game = gameService.doAction(gameId, playerId, action);
			if (game.winner != null) {
				gameEntityDao.saveGameEntity(new GameEntity(game));
			}
			if (game != null) {
				socketService.notifySubscribed(game);
			}
		} catch (GameFinishedException e) {
			respond(resp, new ErrorResponse("Game has finished"));
			return;
		} catch (Exception ex) {
			LOG.error("Exception while doing action", ex);
			respond(resp, new ErrorResponse(ex.getMessage()));
			return;
		}

		respond(resp, new GameResponse(game, playerId));
	}

	@GetMapping("/game/join")
	public void joinGame(@RequestParam("gameId") Integer gameId, @RequestParam("playerId") Integer playerId,
			HttpServletResponse resp) throws JsonProcessingException, IOException {

		Game game;

		try {
			game = gameService.joinGame(gameId, playerId);
		} catch (Exception ex) {
			ex.printStackTrace();
			respond(resp, new ErrorResponse(ex.getMessage()));
			return;
		}

		respond(resp, new GameResponse(game, playerId));
	}

	@GetMapping("/game/play")
	public void playGame(@RequestParam("gameId") Integer gameId, @RequestParam("playerId") Integer playerId,
			HttpServletResponse resp) throws JsonProcessingException, IOException {

		Game game;

		try {
			game = gameService.playGame(gameId, playerId);
		} catch (Exception ex) {
			ex.printStackTrace();
			respond(resp, new ErrorResponse(ex.getMessage()));
			return;
		}

		respond(resp, new GameResponse(game, playerId));
	}

	@GetMapping("/train/random")
	public void randomGame(@RequestParam("playerId") Integer playerId, HttpServletResponse resp) throws IOException {

		Game game;
		try {
			game = gameService.randomGame(playerId);
		} catch (Exception ex) {
			ex.printStackTrace();
			respond(resp, new ErrorResponse(ex.getMessage()));
			return;
		}

		respond(resp, new GameResponse(game, playerId));
	}
}
