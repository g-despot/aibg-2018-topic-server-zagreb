package aibg2018.server.controller;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.ExceptionHandler;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import aibg2018.server.response.ErrorResponse;
import hr.best.aibg2018.logic.game.GameException;

public abstract class AbstractController {

	private static final Logger LOG = LoggerFactory.getLogger(AbstractController.class);

	@Autowired
	protected ObjectMapper mapper;

	@ExceptionHandler(GameException.class)
	public void handleGameException(GameException ex, HttpServletResponse resp)
			throws JsonProcessingException, IOException {
		LOG.info("Game exception caught", ex);
		respond(resp, new ErrorResponse(ex.getMessage()));
	}

	@ExceptionHandler(Exception.class)
	public void handleException(Exception ex, HttpServletResponse resp) throws JsonProcessingException, IOException {
		LOG.error("Exception caught", ex);
		respond(resp, new ErrorResponse("Internal server error"));
	}

	public void respond(HttpServletResponse resp, Object response) throws JsonProcessingException, IOException {
		PrintWriter writer = resp.getWriter();
		writer.print(mapper.writeValueAsString(response));
		writer.close();
	}

}
