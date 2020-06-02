package aibg2018.server.controller;

import java.io.IOException;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import com.fasterxml.jackson.core.JsonProcessingException;

import aibg2018.server.dao.GameEntityDao;
import aibg2018.server.response.ErrorResponse;
import aibg2018.server.response.SuccessResponse;
import aibg2018.server.service.GameService;
import aibg2018.server.service.impl.GameServiceImpl;
import hr.best.aibg2018.logic.entites.Player;
import hr.best.aibg2018.logic.game.Game;

@Controller
public class AdminController extends AbstractController {

	@Autowired
	private GameService gameService;

	@Autowired
	private GameEntityDao gameEntityDao;

	@GetMapping("/admin/createGame")
	public void createGame(Integer gameId, Integer playerOne, Integer playerTwo, String mapName,
			HttpServletResponse resp) throws JsonProcessingException, IOException {
		try {
			Game game = gameService.createGame(gameId, mapName);
			game.setPlayer1(new Player(0, 0, playerOne, GameServiceImpl.getTeamName(playerOne)));
			game.setPlayer2(new Player(game.getMap().getHeight() - 1, game.getMap().getHeight() - 1, playerTwo,
					GameServiceImpl.getTeamName(playerTwo)));
			gameService.addGame(game);

			respond(resp, new SuccessResponse(game));
		} catch (IllegalArgumentException e) {
			respond(resp, new ErrorResponse("Game with id " + gameId + " already exists!"));
		}
	}

	@GetMapping("/admin/getGame")
	public void getGame(Integer id, HttpServletResponse resp) throws JsonProcessingException, IOException {
		respond(resp, new SuccessResponse(gameEntityDao.getGameEntity(id)));
	}

}
