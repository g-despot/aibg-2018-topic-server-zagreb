package aibg2018.server.dao;

public interface GameEntityDao {

	void saveGameEntity(GameEntity game);

	GameEntity getGameEntity(int id);

}
