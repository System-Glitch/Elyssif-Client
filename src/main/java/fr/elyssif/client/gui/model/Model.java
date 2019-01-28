package fr.elyssif.client.gui.model;

import java.util.Date;

import com.jfoenix.controls.datamodels.treetable.RecursiveTreeObject;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;

/**
 * Super-class for models. All members of the model may not be filled, depending on the request that created it.
 * 
 * @author Jérémy LAMBERT
 *
 */
public abstract class Model<T> extends RecursiveTreeObject<T> {

	private SimpleIntegerProperty id;
	private SimpleObjectProperty<Date> createdAt;
	private SimpleObjectProperty<Date> updatedAt;

	/**
	 * Create a new instance of a model.
	 * @param id - the ID of the resource
	 */
	public Model(int id) {
		this(id, null, null);
	}
	
	/**
	 * Create a new instance of a model.
	 * @param id
	 * @param createdAt
	 * @param updatedAt
	 */
	public Model(int id, Date createdAt, Date updatedAt) {
		this.id = new SimpleIntegerProperty(id);
		this.createdAt = new SimpleObjectProperty<Date>(createdAt);
		this.updatedAt = new SimpleObjectProperty<Date>(updatedAt);
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

	/**
	 * Get the date this record was created.
	 * @param updatedAt
	 */
	public final SimpleObjectProperty<Date> getCreatedAt() {
		return createdAt;
	}

	/**
	 * Set the date this record was created.
	 * @param updatedAt
	 */
	public final void setCreatedAt(Date createdAt) {
		this.createdAt.set(createdAt);
	}

	/**
	 * Get the date this record was last updated.
	 * @param updatedAt
	 */
	public final SimpleObjectProperty<Date> getUpdatedAt() {
		return updatedAt;
	}

	/**
	 * Set the date this record was last updated.
	 * @param updatedAt
	 */
	public final void setUpdatedAt(Date updatedAt) {
		this.updatedAt.set(updatedAt);
	}

}