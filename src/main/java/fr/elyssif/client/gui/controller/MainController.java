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
import javafx.fxml.FXML;

/**
 * Controller for the Main view
 * @author Jérémy LAMBERT
 *
 */
public final class MainController extends ContainerController {

	private static MainController instance;

	private HttpClient client;
	private Authenticator authenticator;

	@FXML private AppController appController;
	@FXML private AuthController authController;

	private boolean canExit = true;

	public void initialize(URL location, ResourceBundle resources) {
		if(Config.getInstance().isVerbose())
			Logger.getGlobal().info("Loading main controller.");
		super.initialize(location, resources);
		client = HttpClientBuilder.create().build();
		authenticator = new Authenticator(client, Config.getInstance().get("Host"), Config.getInstance().get("Token"));
		instance = this;

		SnackbarController.getInstance().setSnackbar(new JFXSnackbar(getPane()));

		authController.show(false);
	}

	/**
	 * Get the single instance of MainController
	 * @return instance
	 */
	public static MainController getInstance() {
		return instance;
	}

	/**
	 * Get the authenticator.
	 * @return authenticator
	 */
	public final Authenticator getAuthenticator() {
		return authenticator;
	}

	/**
	 * Get the http client.
	 * @return client
	 */
	public final HttpClient getHttpClient() {
		return client;
	}

	/**
	 * Callback when GUI is ready and shown on screen.
	 */
	public void ready() {
		if(authenticator.getToken() != null) {
			authController.getController("loader").show(true);
			authenticator.requestUserInfo(data -> {
				int status = data.getResponse().getStatus();
				if(status == 200 && authenticator.getUser() != null) {
					authController.getController("loader").showNext(appController, true);
				} else if(status == 401) {
					Config.getInstance().set("Token", null);
					Config.getInstance().save();
					authController.getController("loader").showNext(authController.getController("welcome"), true);
				} else if(status == -1) {
					SnackbarController.getInstance().message(getBundle().getString("error") + data.getResponse().getRawBody(), SnackbarMessageType.ERROR, 4000);
					authController.getController("loader").showNext(authController.getController("welcome"), true);
				}
			});
		} else
			authController.getController("welcome").show(true);
	}

	/**
	 * If set to true, the program cannot exit.
	 * Use this when doing a process that shouldn't stop
	 * half way through.
	 * @param canExit
	 */
	protected void setCanExit(boolean canExit) {
		this.canExit = canExit;
	}

	public boolean canExit() {
		return canExit;
	}

}
