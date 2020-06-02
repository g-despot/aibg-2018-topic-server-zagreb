package aibg2018.server.response;

@SuppressWarnings("serial")
public class ErrorResponse extends AbstractResponse {

	String message;

	public ErrorResponse(String message) {
		super(false);
		this.message = message;
	}

	public String getMessage() {
		return message;
	}
}
