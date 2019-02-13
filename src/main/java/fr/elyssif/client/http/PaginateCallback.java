package fr.elyssif.client.http;

import fr.elyssif.client.gui.view.Paginator;

/**
 * Custom Runnable used as callbacks for REST paginated requests.
 * @author Jérémy LAMBERT
 *
 * @see Runnable
 * @see fr.elyssif.client.gui.view.Paginator
 */
public abstract class PaginateCallback<T> {

	private Paginator<T> paginator;

	public final Paginator<T> getPaginator() {
		return paginator;
	}

	/**
	 * Get the response from the request.
	 * @param paginator
	 */
	public final void setPaginator(Paginator<T> paginator) {
		this.paginator = paginator;
	}
	
}
