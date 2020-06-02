package aibg2018.server.controller;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Paths;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;

import aibg2018.server.service.SocketService;
import hr.best.aibg2018.logic.entites.MonsterType;
import hr.best.aibg2018.logic.entites.Player;
import hr.best.aibg2018.logic.game.Game;
import hr.best.aibg2018.logic.map.Map;

@Controller
@CrossOrigin("*")
public class HomeController extends AbstractController {

	@Autowired
	private SocketService service;

	@GetMapping({ "/", "/home" })
	public String home() {
		return "home";
	}

	@GetMapping("/ping")
	public String ping() throws URISyntaxException, IOException {
		Game g = new Game(new Map(Paths.get(ClassLoader.getSystemResource("./maps/mapConfig.txt").toURI())));
		g.setPlayer1(new Player(0, 0, MonsterType.FIRE, 1, "First"));
		g.setPlayer2(new Player(9, 9, MonsterType.WATER, 2, "Second"));
		g.doAction("s", 1);
		service.notifySubscribed(g);
		return "ping";
	}

}
