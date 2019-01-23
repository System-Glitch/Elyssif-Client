package fr.elyssif.client.gui.controller;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Logger;

import com.jfoenix.controls.JFXListView;

import fr.elyssif.client.Config;
import fr.elyssif.client.gui.controller.SnackbarController.SnackbarMessageType;
import fr.elyssif.client.http.RequestCallback;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

/**
 * Controller for the side menu view.
 * @author Jérémy LAMBERT
 *
 */
public final class SideMenuController extends Controller {

	@FXML private JFXListView<Label> sideList;

	public void initialize(URL location, ResourceBundle resources) {
		if(Config.getInstance().isVerbose())
			Logger.getGlobal().info("Loading side menu controller.");
		super.initialize(location, resources);
	}

	@FXML
	private void logoutClicked() {
		((Lockable) getParentController()).setLocked(true);
		MainController.getInstance().getAuthenticator().logout(new RequestCallback() {

			public void run() {
				int status = getResponse().getStatus();
				if(status == 204 || status == 401) {
					Config.getInstance().set("Token", null);
					Config.getInstance().save();
					getParentController().showNext(MainController.getInstance().getController("auth"), true);
				} else if(status == -1)
					SnackbarController.getInstance().message(getBundle().getString("error") + getResponse().getRawBody(), SnackbarMessageType.ERROR, 4000);
				((Lockable) getParentController()).setLocked(false);				
			}

		});
	}
}
