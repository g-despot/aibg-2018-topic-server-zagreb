package aibg2018.server.dao;

import hr.best.aibg2018.logic.entites.Player;

public class PlayerEntity {

	private int id;
	private int health;
	private int lives;
	private int kills;

	public PlayerEntity() {
	}

	public PlayerEntity(Player player) {
		this.id = player.getId();
		this.health = player.getHealth();
		this.lives = player.getLives();
		this.kills = player.getKills();
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getHealth() {
		return health;
	}

	public void setHealth(int health) {
		this.health = health;
	}

	public int getLives() {
		return lives;
	}

	public void setLives(int lives) {
		this.lives = lives;
	}

	public int getKills() {
		return kills;
	}

	public void setKills(int kills) {
		this.kills = kills;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + health;
		result = prime * result + id;
		result = prime * result + kills;
		result = prime * result + lives;
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
		PlayerEntity other = (PlayerEntity) obj;
		if (health != other.health)
			return false;
		if (id != other.id)
			return false;
		if (kills != other.kills)
			return false;
		if (lives != other.lives)
			return false;
		return true;
	}
}
