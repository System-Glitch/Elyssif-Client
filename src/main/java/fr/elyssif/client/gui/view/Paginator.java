package fr.elyssif.client.gui.view;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

/**
 * Store the pagination information.
 * @author Jérémy LAMBERT
 *
 */
public class Paginator<T> {

	private int currentPage;
	private int maxPage;
	private int perPage;
	private int total;
	private ObservableList<T> items;

	public Paginator() {
		this(1, 1, 0, 0, FXCollections.observableArrayList());
	}

	public Paginator(ObservableList<T> observableList) {
		this(1, 1, 0, 0, observableList);
	}

	public Paginator(int currentPage, int maxPage, int perPage, int total) {
		this(currentPage, maxPage, perPage, total, FXCollections.observableArrayList());
	}

	public Paginator(int currentPage, int maxPage, int perPage, int total, ObservableList<T> observableList) {
		this.currentPage = currentPage;
		this.maxPage = maxPage;
		this.perPage = perPage;
		this.total = total;
		this.items = observableList;
	}

	public final int getCurrentPage() {
		return currentPage;
	}

	public final int getMaxPage() {
		return maxPage;
	}

	public final int getPerPage() {
		return perPage;
	}

	public final int getTotal() {
		return total;
	}

	public final ObservableList<T> getItems() {
		return items;
	}

	/**
	 * Load page information from the given json object. If the needed values are not found, they will use the default value 1.
	 * @param object the json object containing the paginator
	 */
	public void loadFromJson(JsonObject object) {
		JsonElement element = object.get("current_page");

		if(element != null && element.isJsonPrimitive()) currentPage = element.getAsInt();

		element = object.get("last_page");
		if(element != null && element.isJsonPrimitive()) maxPage = element.getAsInt();

		element = object.get("per_page");
		if(element != null && element.isJsonPrimitive()) perPage = element.getAsInt();

		element = object.get("total");
		if(element != null && element.isJsonPrimitive()) total = element.getAsInt();
	}

}
