package fr.elyssif.client.gui.view;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

/**
 * Store the pagination information.
 * @author Jérémy LAMBERT
 *
 */
public class Paginator {

	private int currentPage;
	private int maxPage;
	private int perPage;

	public Paginator(int currentPage, int maxPage, int perPage) {
		this.currentPage = currentPage;
		this.maxPage = maxPage;
		this.perPage = perPage;
	}

	public int getCurrentPage() {
		return currentPage;
	}

	public int getMaxPage() {
		return maxPage;
	}

	public int getPerPage() {
		return perPage;
	}

	/**
	 * Instantiate a new Paginator from the given object. If the needed values are not found, they will use the default value 1.
	 * @param object - the json object containing the paginator
	 * @return a new Paginator instance
	 */
	public static Paginator fromJson(JsonObject object) {
		int page = 1;
		int pageMax = 1;
		int perPage = 0;
		JsonElement element = object.get("currentPage");

		if(element != null && element.isJsonPrimitive()) page = element.getAsInt();

		element = object.get("lastPage");
		if(element != null && element.isJsonPrimitive()) pageMax = element.getAsInt();

		element = object.get("perPage");
		if(element != null && element.isJsonPrimitive()) perPage = element.getAsInt();

		return new Paginator(page, pageMax, perPage);
	}

}
