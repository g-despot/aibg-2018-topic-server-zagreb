package aibg2018.server.dao;

import hr.best.aibg2018.logic.game.Game;

public class GameEntity {

	private int gameId;
	private int winner;
	private PlayerEntity player1;
	private PlayerEntity player2;

	public GameEntity() {
	}

	public GameEntity(Game game) {
		this.gameId = game.getId();
		this.winner = game.winner;
		player1 = new PlayerEntity(game.getPlayer1());
		player2 = new PlayerEntity(game.getPlayer2());
	}

	public int getGameId() {
		return gameId;
	}

	public void setGameId(int gameId) {
		this.gameId = gameId;
	}

	public int getWinner() {
		return winner;
	}

	public void setWinner(int winner) {
		this.winner = winner;
	}

	public PlayerEntity getPlayer1() {
		return player1;
	}

	public void setPlayer1(PlayerEntity player1) {
		this.player1 = player1;
	}

	public PlayerEntity getPlayer2() {
		return player2;
	}

	public void setPlayer2(PlayerEntity player2) {
		this.player2 = player2;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + gameId;
		result = prime * result + ((player1 == null) ? 0 : player1.hashCode());
		result = prime * result + ((player2 == null) ? 0 : player2.hashCode());
		result = prime * result + winner;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		GameEntity other = (GameEntity) obj;
		if (gameId != other.gameId)
			return false;
		if (player1 == null) {
			if (other.player1 != null)
				return false;
		} else if (!player1.equals(other.player1))
			return false;
		if (player2 == null) {
			if (other.player2 != null)
				return false;
		} else if (!player2.equals(other.player2))
			return false;
		if (winner != other.winner)
			return false;
		return true;
	}
}
