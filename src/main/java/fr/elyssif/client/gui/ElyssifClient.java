package fr.elyssif.client.gui;

import java.util.Locale;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.jfoenix.controls.JFXDecorator;

import fr.elyssif.client.Config;
import fr.elyssif.client.gui.controller.MainController;
import fr.elyssif.client.gui.view.ViewUtils;
import fr.elyssif.client.http.RestRequest;
import fr.elyssif.client.http.echo.Echo;
import fr.elyssif.client.http.echo.SocketIOConnector;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

/**
 * Main class for the GUI.
 * @author Jérémy LAMBERT
 *
 */
public final class ElyssifClient extends Application {

	private static final int DEFAULT_WIDTH = 1050;
	private static final int DEFAULT_HEIGHT = 635;

	@Override
	public void start(Stage primaryStage) throws Exception {
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/MainView.fxml"));
			setupLanguage(loader);
			StackPane rootLayout = (StackPane) loader.load();
			JFXDecorator decorator = new JFXDecorator(primaryStage, rootLayout, false, true, true);
			Scene scene = new Scene(decorator);
			scene.getStylesheets().addAll(getClass().getResource("/com/jfoenix/assets/css/jfoenix-fonts.css").toExternalForm(),
					getClass().getResource("/com/jfoenix/assets/css/jfoenix-design.css").toExternalForm(),
					getClass().getResource("/view/css/application.css").toExternalForm());

			ViewUtils.disableContextMenu(scene);

			primaryStage.setScene(scene);
			primaryStage.setTitle("Elyssif");

			setupIcons(primaryStage);

			primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
				public void handle(WindowEvent event) {
					if(!MainController.getInstance().canExit()) {
						event.consume();
					} else {
						Echo echo = MainController.getInstance().getAuthenticator().getEcho();
						if(echo != null) {
							SocketIOConnector.setExiting(true);
							echo.disconnect();
						}
					}
				}
			});

			primaryStage.show();

			primaryStage.setMinHeight(DEFAULT_HEIGHT);
			primaryStage.setMinWidth(DEFAULT_WIDTH);
			primaryStage.setHeight(DEFAULT_HEIGHT);
			primaryStage.setWidth(DEFAULT_WIDTH);

			primaryStage.centerOnScreen();

			MainController.getInstance().ready();

		} catch( Exception e ) {
			Logger.getGlobal().log(Level.SEVERE, "Error while loading the graphical interface.", e);
			Platform.exit();
		}
	}

	private void setupIcons(Stage primaryStage) {
		primaryStage.getIcons().add(new Image(getClass().getResource("/view/img/logo/logo16.png").toExternalForm()));
		primaryStage.getIcons().add(new Image(getClass().getResource("/view/img/logo/logo32.png").toExternalForm()));
		primaryStage.getIcons().add(new Image(getClass().getResource("/view/img/logo/logo48.png").toExternalForm()));
		primaryStage.getIcons().add(new Image(getClass().getResource("/view/img/logo/logo64.png").toExternalForm()));
		primaryStage.getIcons().add(new Image(getClass().getResource("/view/img/logo/logo128.png").toExternalForm()));
		primaryStage.getIcons().add(new Image(getClass().getResource("/view/img/logo/logo256.png").toExternalForm()));
	}

	private void setupLanguage(FXMLLoader loader) {
		Locale locale = getLocale();
		Logger.getGlobal().info("Language: " + locale.getLanguage());

		loader.setResources(ResourceBundle.getBundle("bundles.lang", locale));
		RestRequest.setGlobalLocale(locale);
	}

	private Locale getLocale() {
		String loc = Config.getInstance().get("Locale");
		if(loc == null) return Locale.ENGLISH;

		for(Locale locale : Locale.getAvailableLocales()) {
			if(locale.getLanguage().equals(loc)) {
				if(getClass().getResource("/bundles/lang_" + locale.getLanguage() + ".properties") != null) {
					return locale;
				} else {
					Logger.getGlobal().warning("Selected language \"" + locale.getLanguage() + "\" is not supported.");
					return Locale.ENGLISH;
				}
			}				
		}
		return Locale.ENGLISH;
	}

	public static void run(String[] args) {
		launch(args);
	}

}
