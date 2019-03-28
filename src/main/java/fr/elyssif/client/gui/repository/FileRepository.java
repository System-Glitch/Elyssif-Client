package fr.elyssif.client.gui.repository;

import java.util.Date;
import java.util.HashMap;
import java.util.logging.Logger;

import fr.elyssif.client.gui.model.File;
import fr.elyssif.client.http.FailCallback;
import fr.elyssif.client.http.FormCallback;
import fr.elyssif.client.http.HttpMethod;
import fr.elyssif.client.http.RestCallback;

public class FileRepository extends Repository<File> {

	/**
	 * <p>Update the given <code>File</code>'s <code>hashCiphered</code>
	 *  on the server based on its id.<p>
	 * @param model the model to update on the server
	 * @param callback the callback executed on success, nullable
	 * @param formCallback the callback executed on validation error
	 * @param failCallback the callback executed on failure, nullable
	 */
	public void cipher(File model, RestCallback callback, FormCallback formCallback, FailCallback failCallback) {

		var params = new HashMap<String, Object>();
		params.put("ciphered_hash", model.getHashCiphered().get());

		request(String.valueOf(model.getId().get()) + "/cipher", HttpMethod.PUT, params, new RestCallback() {

			public void run() {
				if(getStatus() != 204) {
					Logger.getGlobal().warning("Update request returned status " + getStatus() + ", expected 204.");
				}

				model.setUpdatedAt(new Date());
				if(callback != null) {
					callback.setResponse(getResponse());
					callback.run();
				}
			}

		}, new FailCallback() {

			public void run() {
				if(getStatus() == 422) { // Validation errors
					formCallback.setResponse(getResponse());
					formCallback.run();
				} else if(failCallback != null) {
					failCallback.setResponse(getResponse());
					failCallback.run();
				}
			}

		});
	}

}
