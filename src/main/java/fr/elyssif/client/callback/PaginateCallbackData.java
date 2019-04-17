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
