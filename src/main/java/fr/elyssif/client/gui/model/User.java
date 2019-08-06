/*
 * Elyssif-Client
 * Copyright (C) 2019 Jérémy LAMBERT (System-Glitch)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.

 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.

 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
 package fr.elyssif.client.gui.model;

import java.util.Date;

import com.google.gson.JsonObject;

import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;

public final class User extends Model<User> {

	private SimpleStringProperty email;
	private SimpleStringProperty name;
	private SimpleStringProperty address;

	private SimpleObjectProperty<Date> emailVerifiedAt;

	/**
	 * Create a new instance of User with a zero ID.
	 */
	public User() {
		this(0);
	}

	/**
	 * Create a new instance of a User.
	 * @param id the ID of the resource
	 */
	public User(Integer id) {
		super(id);
		email = new SimpleStringProperty();
		name = new SimpleStringProperty();
		address = new SimpleStringProperty();
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

	public final SimpleStringProperty getAddress() {
		return address;
	}

	public final void setAddress(String address) {
		this.address.set(address);
	}

	public final SimpleObjectProperty<Date> getEmailVerifiedAt() {
		return emailVerifiedAt;
	}

	public final void setEmailVerifiedAt(Date emailVerifiedAt) {
		this.emailVerifiedAt.set(emailVerifiedAt);
	}

}
