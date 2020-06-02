package aibg2018.server.socket;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.AbstractWebSocketHandler;

import aibg2018.server.service.SocketService;

/**
 * {@link WebSocketHandler} that subscribes/unsubscribes user's to
 * {@link SocketService} upon connection establishment/close.
 */
@Component
public class SocketHandler extends AbstractWebSocketHandler {

	@Autowired
	private SocketService service;

	@Override
	public void afterConnectionEstablished(WebSocketSession session) throws Exception {
		service.subscribe(session);
	}

	@Override
	public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
		service.unsubscribe(session);
	}
}
