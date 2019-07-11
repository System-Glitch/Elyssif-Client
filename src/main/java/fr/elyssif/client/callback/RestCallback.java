package fr.elyssif.client.callback;

/**
 * Functional interface allowing for callback info
 * and the use of lambda expressions.
 * @author Jérémy LAMBERT
 *
 * @see RestCallbackData
 */
@FunctionalInterface
public interface RestCallback {

	/**
	 * Execute the callback.
	 * @param data the data resulting from the REST call.
	 */
	void run(RestCallbackData data);
	
}
