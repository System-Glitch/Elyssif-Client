package fr.elyssif.client.gui.controller;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Logger;

import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.HttpClientBuilder;

import com.jfoenix.controls.JFXSnackbar;

import fr.elyssif.client.Config;
import fr.elyssif.client.gui.controller.SnackbarController.SnackbarMessageType;
import fr.elyssif.client.gui.controller.auth.AuthController;
import fr.elyssif.client.http.Authenticator;
import fr.elyssif.client.http.RequestCallback;
import javafx.fxml.FXML;

/**
 * Controller for the Main view
 * @author Jérémy LAMBERT
 *
 */
public final class MainController extends Controller {

	private static MainController instance;

	private HttpClient client;
	private Authenticator authenticator;

	@FXML private HomeController homeController;
	@FXML private AuthController authController;

	public void initialize(URL location, ResourceBundle resources) {
		if(Config.getInstance().isVerbose())
			Logger.getGlobal().info("Loading main controller.");
		super.initialize(location, resources);
		client = HttpClientBuilder.create().build();
		authenticator = new Authenticator(client, Config.getInstance().get("Host"), Config.getInstance().get("Token"));
		instance = this;

		SnackbarController.getInstance().setSnackbar(new JFXSnackbar(getPane()));
		SnackbarController.getInstance().message("This is a success!", SnackbarMessageType.SUCCESS);
		SnackbarController.getInstance().message("This is an error", SnackbarMessageType.ERROR);
		SnackbarController.getInstance().message("This is some info.", SnackbarMessageType.INFO);
		
		authController.show(false);
	}

	public static MainController getInstance() {
		return instance;
	}

	public final Authenticator getAuthenticator() {
		return authenticator;
	}

	/**
	 * Callback when GUI is ready and shown on screen.
	 */
	public void ready() {
		if(authenticator.getToken() != null) {
			authenticator.requestUserInfo(new RequestCallback() {

				public void run() {
					int status = getResponse().getStatus();
					if(status == 200 && authenticator.getUser() != null)
						homeController.show(true);
					else if(status == 401)
						authController.getController("welcome").show(true);
					else if(status == -1)
						SnackbarController.getInstance().message(getBundle().getString("error") + getResponse().getRawBody(), SnackbarMessageType.ERROR, 4000);
				}

			});
		} else
			authController.getController("welcome").show(true);
	}

}
