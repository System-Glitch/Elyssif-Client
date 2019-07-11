package fr.elyssif.client.gui.repository;

import java.util.HashMap;
import java.util.logging.Logger;

import fr.elyssif.client.callback.FailCallbackData;
import fr.elyssif.client.callback.FormCallbackData;
import fr.elyssif.client.callback.RestCallback;
import fr.elyssif.client.gui.model.User;
import fr.elyssif.client.http.HttpMethod;

public class UserRepository extends Repository<User> {

	/**
	 * <p>Update the password of the currently authenticated
	 * user.</p>
	 * @param currentPassword the current password
	 * @param newPassword the new password
	 * @param confirmation the password confirmation supposed to
	 * match the <code>newPassword</code> parameter
	 * @param callback the callback executed on success, nullable.
	 * Wrapped data is of type RestCallbackData.
	 * @param formCallback the callback executed on validation error
	 * Wrapped data is of type FormCallbackData.
	 * @param failCallback the callback executed on failure, nullable.
	 * Wrapped data is of type FailCallbackData.
	 * @throws IllegalArgumentException thrown if no field is provided
	 */
	public void updatePassword(String currentPassword, String newPassword, String confirmation, RestCallback callback, RestCallback formCallback, RestCallback failCallback) {

		var params = new HashMap<String, Object>();
		params.put("old_password", currentPassword);
		params.put("password", newPassword);
		params.put("password_confirmation", confirmation);

		request("password", HttpMethod.PUT, params, data -> {

			if(data.getStatus() != 204) {
				Logger.getGlobal().warning("Update request returned status " + data.getStatus() + ", expected 204.");
			}

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

}
