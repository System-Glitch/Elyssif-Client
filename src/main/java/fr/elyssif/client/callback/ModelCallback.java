package fr.elyssif.client.callback;

import fr.elyssif.client.gui.model.Model;

/**
 * Functional interface for callbacks returning a model instance.
 * @author Jérémy LAMBERT
 *
 * @param <T> the type of the model
 */
@FunctionalInterface
public interface ModelCallback<T extends Model<T>> {

	/**
	 * Execute the callback.
	 * @param model
	 */
	void run(T model);
	
}
