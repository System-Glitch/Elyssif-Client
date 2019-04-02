package fr.elyssif.client.callback;

import fr.elyssif.client.gui.model.Model;
import fr.elyssif.client.gui.view.Paginator;

/**
 * Custom Runnable used as callbacks for REST paginated requests.
 * @author Jérémy LAMBERT
 *
 * @see Runnable
 * @see fr.elyssif.client.gui.view.Paginator
 */
public abstract class PaginateCallback<T extends Model<T>> extends RestCallback implements Runnable {

	private Paginator<? extends Model<?>> paginator;

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
