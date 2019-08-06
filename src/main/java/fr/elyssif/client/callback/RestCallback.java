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

/**
 * Functional interface allowing for callback info
 * and the use of lambda expressions.
 * @author Jérémy LAMBERT
 *
 * @see RestCallbackData
 */
@FunctionalInterface
public interface RestCallback {

	/**
	 * Execute the callback.
	 * @param data the data resulting from the REST call.
	 */
	void run(RestCallbackData data);
	
}
