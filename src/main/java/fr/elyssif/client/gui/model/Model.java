package fr.elyssif.client.gui.model;

import com.jfoenix.controls.datamodels.treetable.RecursiveTreeObject;

import javafx.beans.property.SimpleIntegerProperty;

/**
 * Super-class for models. All members of the model may not be filled, depending on the request that created it.
 * 
 * @author Jérémy LAMBERT
 *
 */
public abstract class Model<T> extends RecursiveTreeObject<T> {

	private SimpleIntegerProperty id;
	//TODO timestamps

	/**
	 * Create a new instance of a model.
	 * @param id - the ID of the resource
	 */
	public Model(int id) {
		this.id = new SimpleIntegerProperty(id);
	}

	/**
	 * Get the ID of the resource.
	 * @return id
	 */
	public final SimpleIntegerProperty getId() {
		return id;
	}

	/**
	 * Set the ID of the resource.
	 * @param id
	 */
	public final void setId(int id) {
		this.id.set(id);
	}

}