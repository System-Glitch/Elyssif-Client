package fr.elyssif.client.gui.model;

import javafx.beans.property.SimpleStringProperty;

public final class User extends Model<User>{

	private SimpleStringProperty email;
	private SimpleStringProperty name;

	public User(int id) {
		super(id);
		email = new SimpleStringProperty();
		name = new SimpleStringProperty();
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

}
