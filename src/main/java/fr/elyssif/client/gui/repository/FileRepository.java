package fr.elyssif.client.gui.repository;

import java.util.Date;
import java.util.HashMap;
import java.util.logging.Logger;

import fr.elyssif.client.callback.FailCallbackData;
import fr.elyssif.client.callback.FormCallbackData;
import fr.elyssif.client.callback.JsonCallbackData;
import fr.elyssif.client.callback.ModelCallbackData;
import fr.elyssif.client.callback.RestCallback;
import fr.elyssif.client.gui.model.File;
import fr.elyssif.client.http.HttpMethod;

/**
 * Repository for the <code>File</code> model.
 * @author Jérémy LAMBERT
 *
 */
public class FileRepository extends Repository<File> {

	/**
	 * Fetch a file by its hash cipehered.
	 * @param hashCiphered the hash ciphered of the file to fetch, must be 64 characters long
	 * @param callback the callback executed on success.
	 * Wrapped data is of type ModelCallbackData.
	 * @param failCallback the callback executed on failure, nullable.
	 * Wrapped data is of type FailCallbackData.
	 * @throws IllegalArgumentException thrown if <code>hashCiphered</code> is null
	 * or isn't 64 characters long
	 */
	public void fetch(String hashCiphered, RestCallback callback, RestCallback failCallback) {
		if(hashCiphered == null || hashCiphered.length() != 64) {
			throw new IllegalArgumentException("Hash ciphered must be set and 64 characaters long.");
		}

		var params = new HashMap<String, Object>();
		params.put("ciphered_hash", hashCiphered);
		request("fetch", HttpMethod.GET, params, data -> {

			File model = instantiateReferenceModel();
			if(model != null) {
				var element = ((JsonCallbackData) data).getElement();
				if(element != null && element.isJsonObject()) {
					model.loadFromJsonObject(element.getAsJsonObject());
					callback.run(new ModelCallbackData<File>(data.getResponse(), model));
				} else {
					handleMalformedResponse(data.getResponse(), failCallback, "JSON object");
				}
			}

		}, failCallback);
	}

	/**
	 * Check a file by using its hash and hash ciphered.
	 * On success, the given model is updated with the current
	 * date for the fields <code>updatedAt</code> and <code>decipheredAt</code>
	 * @param model the model to check
	 * @param callback the callback executed on success.
	 * Wrapped data is of type RestCallbackData.
	 * @param failCallback the callback executed on failure, nullable.
	 * Wrapped data is of type FailCallbackData.
	 * @throws IllegalArgumentException thrown if <code>hashCiphered</code> is null
	 * or isn't 64 characters long
	 */
	public void check(File model, RestCallback callback, RestCallback failCallback) {
		String hash = model.getHash().get();
		String hashCiphered = model.getHashCiphered().get();

		if(hash == null || hash.length() != 64 || hashCiphered == null || hashCiphered.length() != 64) {
			throw new IllegalArgumentException("Hash and hash ciphered must be set and 64 characaters long.");
		}

		var params = new HashMap<String, Object>();
		params.put("hash", hash);
		params.put("ciphered_hash", hashCiphered);
		request("check", HttpMethod.GET, params, data -> {

			if(data.getStatus() == 204) {
				Date now = new Date();

				model.setUpdatedAt(now);
				model.setDecipheredAt(now);

				callback.run(data);
			} else if(failCallback != null) {
				var failData = new FailCallbackData(data.getResponse());
				failData.setMessage(data.getStatus() == 404 ? "file-check-fail" : "server-error");
				failCallback.run(failData);
			}

		}, failCallback);
	}

	/**
	 * <p>Update the given <code>File</code>'s <code>hashCiphered</code>
	 *  on the server based on its id.<p>
	 * @param model the model to update on the server
	 * @param callback the callback executed on success, nullable.
	 * Wrapped data is of type RestCallbackData.
	 * @param formCallback the callback executed on validation error.
	 * Wrapped data is of type FormCallbackData.
	 * @param failCallback the callback executed on failure, nullable.
	 * Wrapped data is of type FailCallbackData.
	 */
	public void cipher(File model, RestCallback callback, RestCallback formCallback, RestCallback failCallback) {

		var params = new HashMap<String, Object>();
		params.put("ciphered_hash", model.getHashCiphered().get());

		request(String.valueOf(model.getId().get()) + "/cipher", HttpMethod.PUT, params, data -> {

			if(data.getStatus() != 204) {
				Logger.getGlobal().warning("Update request returned status " + data.getStatus() + ", expected 204.");
			}

			model.setUpdatedAt(new Date());
			if(callback != null) {
				callback.run(data);
			}

		}, data -> {
			if(data.getStatus() == 422) { // Validation errors
				formCallback.run(new FormCallbackData(data.getResponse()));
			} else if(failCallback != null) {
				failCallback.run(new FailCallbackData(data.getResponse()));
			}
		});
	}

	/**
	 * Get a paginate of all the sent files.
	 * @param page the page number, must be positive
	 * @param callback the callback executed on success.
	 * Wrapped data is of type PaginateCallbackData.
	 * @param failCallback the callback executed on failure, nullable.
	 * Wrapped data is of type FailCallbackData.
	 * @throws IllegalArgumentException thrown if <code>page</code> isn't positive
	 */
	public void getSent(int page, RestCallback callback, RestCallback failCallback) {
		if(page <= 0) throw new IllegalArgumentException("Page number must be positive, " + page + " given.");

		var params = new HashMap<String, Integer>();
		params.put("page", page);
		get("sent", params, callback, failCallback);
	}

	/**
	 * Get a paginate of all the received files.
	 * @param page the page number, must be positive
	 * @param callback the callback executed on success.
	 * Wrapped data is of type PaginateCallbackData.
	 * @param failCallback the callback executed on failure, nullable.
	 * Wrapped data is of type FailCallbackData.
	 * @throws IllegalArgumentException thrown if <code>page</code> isn't positive
	 */
	public void getReceived(int page, RestCallback callback, RestCallback failCallback) {
		if(page <= 0) throw new IllegalArgumentException("Page number must be positive, " + page + " given.");

		var params = new HashMap<String, Integer>();
		params.put("page", page);
		get("received", params, callback, failCallback);
	}

}
