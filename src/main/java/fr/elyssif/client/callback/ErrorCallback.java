package fr.elyssif.client.callback;

/**
 * Callback executed on error.
 * @author Jérémy LAMBERT
 *
 */
public abstract class ErrorCallback implements Runnable { // TODO change to functionnal interface

	private Exception exception;

	public final Exception getException() {
		return exception;
	}

	public final void setException(Exception exception) {
		this.exception = exception;
	}

}
