package aibg2018.server.dao;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;

@Component
public class FileGameEntityDao implements GameEntityDao {

	private static final Logger LOG = LoggerFactory.getLogger(FileGameEntityDao.class);

	private ObjectMapper mapper = new ObjectMapper();
	private File rootFolder;

	public FileGameEntityDao(@Value("${db.location}") String dbLoc) {
		rootFolder = new File(dbLoc);
		if (!rootFolder.exists()) {
			rootFolder.mkdirs();
		}
	}

	@Override
	public void saveGameEntity(GameEntity game) {
		File entityFile = new File(rootFolder, game.getGameId() + ".game");

		OutputStream out;
		try {
			out = Files.newOutputStream(entityFile.toPath(), StandardOpenOption.CREATE, StandardOpenOption.WRITE,
					StandardOpenOption.TRUNCATE_EXISTING);
			out.write(mapper.writeValueAsBytes(game));
		} catch (IOException e) {
			LOG.error("Unable to save game " + game.getGameId(), e);
		}
	}

	@Override
	public GameEntity getGameEntity(int id) {
		File entityFile = new File(rootFolder, id + ".game");
		try {
			return mapper.readValue(entityFile, GameEntity.class);
		} catch (IOException e) {
			LOG.error("Unable to get game entity " + id, e);
			return null;
		}
	}

}
