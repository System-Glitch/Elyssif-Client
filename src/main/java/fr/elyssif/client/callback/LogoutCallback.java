/*
 * Elyssif-Client
 * Copyright (C) 2019 Jérémy LAMBERT (System-Glitch)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.

 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.

 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
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
