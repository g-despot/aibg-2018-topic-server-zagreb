package aibg2018.server.service.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import com.fasterxml.jackson.databind.ObjectMapper;

import aibg2018.server.service.SocketService;
import hr.best.aibg2018.logic.game.Game;

@Service
public class SocketServiceImpl implements SocketService {

	@Autowired
	private ObjectMapper mapper;

	private Hashtable<Integer, List<WebSocketSession>> subscriptions = new Hashtable<>();

	@Override
	public synchronized boolean subscribe(WebSocketSession session) {

		Integer gameId = (Integer) session.getAttributes().get(SocketService.GAME_ID);

		if (gameId == null) {
			return false;
		}

		List<WebSocketSession> sessions = subscriptions.get(gameId);
		if (sessions == null) {
			sessions = new ArrayList<>();
			subscriptions.put(gameId, sessions);
		}

		return sessions.add(session);
	}

	@Override
	public synchronized boolean unsubscribe(WebSocketSession session) {

		Integer gameId = (Integer) session.getAttributes().get(SocketService.GAME_ID);

		if (gameId == null) {
			return false;
		}

		List<WebSocketSession> sessions = subscriptions.get(gameId);
		if (sessions == null) {
			return false;
		}

		return sessions.remove(session);
	}

	@Override
	public synchronized void notifySubscribed(Game game) throws IOException {
		List<WebSocketSession> sessions = subscriptions.get(game.getId());
		if (sessions == null) {
			return;
		}

		String message = mapper.writeValueAsString(game);
		for (WebSocketSession ses : sessions) {

			/*
			 * In case session was closed in meantime ignore as it should be processed
			 * eventually by this#unsubscribe(WebSocketSession)
			 */
			if (ses.isOpen()) {
				ses.sendMessage(new TextMessage(message));
			}
		}
	}

}
