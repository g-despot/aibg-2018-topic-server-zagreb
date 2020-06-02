package aibg2018.server.response;

import hr.best.aibg2018.logic.game.Game;

/**
 * A successful response which also includes a playerIndex field (through
 * getter).
 */
@SuppressWarnings("serial")
public class GameResponse extends SuccessResponse {

	private int playerId;

	public GameResponse(Game game, int playerId) {
		super(game);
		this.playerId = playerId;
	}

	public Integer getPlayerIndex() {
		Game game = (Game) result;
		if (playerId == game.getPlayer1().getId()) {
			return 1;
		}
		if (playerId == game.getPlayer2().getId()) {
			return 2;
		}

		return null;
	}

}
