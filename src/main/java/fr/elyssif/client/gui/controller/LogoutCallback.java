package fr.elyssif.client.gui.controller;

import fr.elyssif.client.Config;
import fr.elyssif.client.gui.controller.SnackbarController.SnackbarMessageType;
import fr.elyssif.client.http.RequestCallback;

/**
 * Custom callback for logout requests.
 * @author Jérémy LAMBERT
 *
 */
public class LogoutCallback extends RequestCallback {

	@Override
	public void run() {
		Controller appController = MainController.getInstance().getController("app");
		int status = getResponse().getStatus();
		if(status == 204 || status == 401) {
			Config.getInstance().set("Token", null);
			Config.getInstance().save();
			appController.showNext(MainController.getInstance().getController("auth"), true);
		} else if(status == -1)
			SnackbarController.getInstance().message(appController.getBundle().getString("error") + getResponse().getRawBody(), SnackbarMessageType.ERROR, 4000);
		((Lockable) appController).setLocked(false);
	}

}
