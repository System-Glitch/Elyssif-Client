package fr.elyssif.client.callback;

/**
 * Callback executed on error.
 * @author Jérémy LAMBERT
 *
 */
public abstract class ErrorCallback implements Runnable {

	private Exception exception;

	public final Exception getException() {
		return exception;
	}

	public final void setException(Exception exception) {
		this.exception = exception;
	}

}
