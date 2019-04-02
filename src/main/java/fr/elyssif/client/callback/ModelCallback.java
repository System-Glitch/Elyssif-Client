package fr.elyssif.client.callback;

import fr.elyssif.client.gui.model.Model;

/**
 * Custom Runnable used as callbacks for json REST requests, returning a Model instance
 * @author Jérémy LAMBERT
 *
 * @see Runnable
 * @see Model
 */
public abstract class ModelCallback<T extends Model<T>> extends RestCallback {

	private T model;

	public void setModel(T model) {
		this.model = model;
	}

	/**
	 * Get the response from the request.
	 * @return the response, can be null
	 */
	public T getModel() {
		return model;
	}

}
