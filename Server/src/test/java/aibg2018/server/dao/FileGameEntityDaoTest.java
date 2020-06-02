package aibg2018.server.dao;

import static org.junit.Assert.assertEquals;

import aibg2018.server.service.GameService;
import aibg2018.server.service.impl.GameServiceImpl;
import hr.best.aibg2018.logic.entites.Player;
import hr.best.aibg2018.logic.game.Game;

// @RunWith(SpringRunner.class)
public class FileGameEntityDaoTest {

	GameService gameService = new GameServiceImpl();
	GameEntityDao gameEntityDao = new FileGameEntityDao("/home/ilakovac/aibg/games");

	// @Test
	public void test() {
		Game g = gameService.createGame(123, null);
		g.setPlayer1(new Player(0, 0, 12));
		g.setPlayer2(new Player(1, 1, 13));
		g.winner = 1;

		GameEntity g1 = new GameEntity(g);
		gameEntityDao.saveGameEntity(g1);

		GameEntity g2 = gameEntityDao.getGameEntity(123);

		assertEquals(g1, g2);
	}

}
