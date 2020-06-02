package aibg2018.server;

import java.io.File;
import java.io.IOException;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import aibg2018.server.service.impl.GameServiceImpl;

@SpringBootApplication
public class Application {

	public static void main(String[] args) throws IOException {

		if (args.length > 0) {
			GameServiceImpl.MAPS_FOLDER = new File(args[0]);
		} else {
			GameServiceImpl.MAPS_FOLDER = new File("./maps");
		}

		if (!GameServiceImpl.MAPS_FOLDER.exists()) {
			System.err.println("Folder" + GameServiceImpl.MAPS_FOLDER.getPath() + " does not exist");
			System.exit(-1);
		}

		SpringApplication.run(Application.class, args);
	}
}