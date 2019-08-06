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
import fr.elyssif.client.gui.view.Paginator;
import fr.elyssif.client.http.RestResponse;

/**
 * Custom Runnable used as callbacks for REST paginated requests.
 * @author Jérémy LAMBERT
 *
 * @see Paginator
 */
public final class PaginateCallbackData<T extends Model<T>> extends RestCallbackData {

	private Paginator<? extends Model<?>> paginator;

	public PaginateCallbackData(RestResponse response, Paginator<? extends Model<?>> paginator) {
		super(response);
		setPaginator(paginator);
	}

	public final Paginator<? extends Model<?>> getPaginator() {
		return paginator;
	}

	/**
	 * Get the response from the request.
	 * @param paginator
	 */
	public final void setPaginator(Paginator<? extends Model<?>> paginator) {
		this.paginator = paginator;
	}

}
