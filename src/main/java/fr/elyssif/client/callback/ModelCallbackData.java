package fr.elyssif.client.callback;

import fr.elyssif.client.gui.model.Model;
import fr.elyssif.client.http.RestResponse;

/**
 * Callback data for REST requests returning a record.
 * @author Jérémy LAMBERT
 *
 * @see Model
 */
public final class ModelCallbackData<T extends Model<T>> extends RestCallbackData {

	private T model;

	public ModelCallbackData(RestResponse response, T model) {
		super(response);
		setModel(model);
	}

	/**
	 * Set the record from the request.
	 * @param model the record from the response, not nullable
	 */
	public void setModel(T model) {
		this.model = model;
	}

	/**
	 * Get the record from the response.
	 * @return the record, cannot be null
	 */
	public T getModel() {
		return model;
	}

}
