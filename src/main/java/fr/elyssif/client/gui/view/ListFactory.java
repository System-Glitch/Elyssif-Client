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
 package fr.elyssif.client.gui.view;

import com.jfoenix.controls.JFXListView;

import fr.elyssif.client.gui.model.Model;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.scene.input.MouseEvent;

public interface ListFactory<T extends Model<?>> {
	
	/**
	 * Setup a list. Sets the formatting and coloring.
	 * @param listView the list to prepare
	 * @param list the list containing the values to show in the table
	 */
	void make(JFXListView<?> listView, ObservableList<T> list);
	
	/**
	 * Setup a list. Sets the formatting and coloring.
	 * @param listView the list to prepare
	 * @param list the list containing the values to show in the table
	 * @param onMouseClicked the event handler executed on a list cell click
	 */
	void make(JFXListView<?> listView, ObservableList<T> list, EventHandler<? super MouseEvent> onMouseClicked);

}
