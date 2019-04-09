package fr.elyssif.client.callback;

/**
 * Callback for the asynchronous hash calculation.
 * @author Jérémy LAMBERT
 *
 * @see Hash
 */
@FunctionalInterface
public interface HashCallback {

	/**
	 * Execute the callback.
	 * @param digest the result of the hash. Can be converted to
	 * a hex string using <code>Hash.toHex()</code>
	 *
	 */
	void run(byte[] digest);

}
