package fr.elyssif.client.gui.model;

import java.util.Date;

import com.google.gson.JsonObject;

import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public final class User extends Model<User>{

	private SimpleStringProperty email;
	private SimpleStringProperty name;

	private SimpleObjectProperty<Date> emailVerifiedAt;

	private ObservableList<User> list;

	/**
	 * Create a new instance of User with a zero ID.
	 */
	public User() {
		this(0);
	}

	/**
	 * Create a new instance of a User.
	 * @param id - the ID of the resource
	 */
	public User(Integer id) {
		super(id);
		email = new SimpleStringProperty();
		name = new SimpleStringProperty();
		list = FXCollections.observableArrayList();
		emailVerifiedAt = new SimpleObjectProperty<Date>();
	}

	/**
	 * Create a new instance of a User and load it from
	 * the given json object.
	 * @param object
	 */
	public User(JsonObject object) {
		this();
		loadFromJsonObject(object);
	}

	public final SimpleStringProperty getEmail() {
		return email;
	}

	public final void setEmail(String email) {
		this.email.set(email);
	}

	public final SimpleStringProperty getName() {
		return name;
	}

	public final void setName(String name) {
		this.name.set(name);
	}

	public final ObservableList<User> getList() {
		return list;
	}

	public final SimpleObjectProperty<Date> getEmailVerifiedAt() {
		return emailVerifiedAt;
	}

	public final void setEmailVerifiedAt(Date emailVerifiedAt) {
		this.emailVerifiedAt.set(emailVerifiedAt);
	}

}
