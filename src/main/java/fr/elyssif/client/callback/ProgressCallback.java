package fr.elyssif.client.callback;

/**
 * Functionnal interface for asynchronous process progress.
 * Mainly used to update the UI so the <code>process</code> method
 * should be called on the JavaFX thread using <code>Platform.runLater()</code>.
 * @author Jérémy LAMBERT
 *
 */
@FunctionalInterface
public interface ProgressCallback {

	/**
	 * Callback when the asynchronous process progressed.
	 * @param progress a progress value, between 0 and 1
	 */
	void progress(double progress);
	
}
