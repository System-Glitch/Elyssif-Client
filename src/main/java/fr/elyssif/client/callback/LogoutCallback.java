package fr.elyssif.client.callback;

import fr.elyssif.client.Config;
import fr.elyssif.client.gui.controller.Controller;
import fr.elyssif.client.gui.controller.Lockable;
import fr.elyssif.client.gui.controller.MainController;
import fr.elyssif.client.gui.controller.SnackbarController;
import fr.elyssif.client.gui.controller.SnackbarController.SnackbarMessageType;

/**
 * Custom callback for logout requests.
 * @author Jérémy LAMBERT
 *
 */
public class LogoutCallback implements RestCallback {

	@Override
	public void run(RestCallbackData data) {
		Controller appController = MainController.getInstance().getController("app");
		int status = data.getResponse().getStatus();
		if(status == 204 || status == 401) {
			Config.getInstance().set("Token", null);
			Config.getInstance().save();
			appController.showNext(MainController.getInstance().getController("auth"), true);
		} else if(status == -1)
			SnackbarController.getInstance().message(appController.getBundle().getString("error") + data.getResponse().getRawBody(), SnackbarMessageType.ERROR, 4000);
		((Lockable) appController).setLocked(false);
	}

}
