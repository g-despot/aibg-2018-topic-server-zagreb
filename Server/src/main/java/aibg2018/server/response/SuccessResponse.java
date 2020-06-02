package aibg2018.server.response;

@SuppressWarnings("serial")
public class SuccessResponse extends AbstractResponse {

	protected Object result;

	public SuccessResponse(Object result) {
		super(true);
		this.result = result;
	}

	public Object getResult() {
		return result;
	}

	public void setResult(Object result) {
		this.result = result;
	}
}
