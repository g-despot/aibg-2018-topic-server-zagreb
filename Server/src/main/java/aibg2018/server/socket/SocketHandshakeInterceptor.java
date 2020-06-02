package aibg2018.server.socket;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

import aibg2018.server.service.SocketService;

/**
 * {@link HandshakeInterceptor} which takes HTTP request parameter 'gameId' and
 * puts it to session attributes associated with the session which will be
 * started.
 */
public class SocketHandshakeInterceptor implements HandshakeInterceptor {

	/**
	 * Takes HTTP request parameter 'gameId' and puts it to attributes map given
	 * under key {@link SocketService#GAME_ID}. If parameter is not present
	 * <code>false</code> is returned meaning handshake will be refused.
	 */
	@Override
	public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler,
			Map<String, Object> attributes) throws Exception {

		HttpServletRequest req = ((ServletServerHttpRequest) request).getServletRequest();
		String gameIdString = (String) req.getParameter("gameId");

		if (gameIdString == null) {
			return false;
		}

		Integer gameId = Integer.parseInt(gameIdString);
		attributes.put(SocketService.GAME_ID, gameId);

		return true;
	}

	@Override
	public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler,
			Exception exception) {
	}

}
