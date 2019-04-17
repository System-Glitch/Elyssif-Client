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
