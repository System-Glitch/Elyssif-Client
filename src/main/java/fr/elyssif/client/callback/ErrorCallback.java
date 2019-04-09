package fr.elyssif.client.callback;

/**
 * Callback executed on error.
 * @author Jérémy LAMBERT
 *
 */
@FunctionalInterface
public interface ErrorCallback {

	/**
	 * Execute the callback.
	 * @param exception the exception thrown by the failed process
	 */
	void run(Exception exception);

}
