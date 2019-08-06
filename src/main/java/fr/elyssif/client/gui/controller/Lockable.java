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
 package fr.elyssif.client.gui.controller;

/**
 * Interface for controllers that need to be locked (such as forms)<br>
 * All controllers implementing this interface should have a SimpleBooleanProperty which defines
 * if the view is locked or not and bind it to the controls through <code>bindControls()</code>.
 * @author Jérémy LAMBERT
 *
 */
public interface Lockable {

	/**
	 * Bind the disable property to all controls in the view.
	 */
	void bindControls();

	/**
	 * Lock the view by disabling all controls or
	 * unlock by enabling all controls.
	 */
	void setLocked(boolean locked);

}
