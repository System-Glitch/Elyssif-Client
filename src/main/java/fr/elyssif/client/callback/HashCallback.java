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
 * Callback for the asynchronous hash calculation.
 * @author Jérémy LAMBERT
 *
 * @see fr.elyssif.client.security.Hash
 */
@FunctionalInterface
public interface HashCallback {

	/**
	 * Execute the callback.
	 * @param digest the result of the hash. Can be converted to
	 * a hex string using <code>Hash.toHex()</code>
	 *
	 */
	void run(byte[] digest);

}
