package aibg2018.server.response;

import java.io.Serializable;

@SuppressWarnings("serial")
public abstract class AbstractResponse implements Serializable {

	private boolean success;

	public AbstractResponse(boolean success) {
		this.success = success;
	}

	public boolean isSuccess() {
		return success;
	}

	public void setSuccess(boolean success) {
		this.success = success;
	}

}
