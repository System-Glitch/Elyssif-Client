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
