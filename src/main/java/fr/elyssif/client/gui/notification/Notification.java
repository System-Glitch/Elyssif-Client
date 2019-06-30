package fr.elyssif.client.gui.notification;

import java.util.logging.Level;
import java.util.logging.Logger;

import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.Window;
import javafx.util.Duration;

/**
 * System tray notification
 * @author Jérémy LAMBERT
 *
 */
public class Notification {

	private String title;
	private String message;

	private Window ownerWindow;
	private Stage stage;
	private AnchorPane rootLayout;
	private NotificationController controller;

	/**
	 * Create a new notification. Call <code>show</code> to show it.
	 * @param title
	 * @param message
	 * @param ownerWindow
	 */
	public Notification(String title, String message, Window ownerWindow) {
		this.title = title;
		this.message = message;
		this.ownerWindow = ownerWindow;
		Platform.runLater(() -> init());
	}

	private void init() {
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/Notification.fxml"));
			rootLayout = (AnchorPane) loader.load();
			controller = (NotificationController) loader.getController();
			Scene scene = new Scene(rootLayout);
			scene.getStylesheets().addAll(getClass().getResource("/view/css/notification.css").toExternalForm());
			scene.setFill(Color.TRANSPARENT);

			stage = new Stage(StageStyle.TRANSPARENT);
			stage.setScene(scene);
			stage.setResizable(false);
			stage.setAlwaysOnTop(true);
			stage.initModality(Modality.NONE);
			stage.initOwner(ownerWindow);

			controller.setTitle(title);
			controller.setMessage(message);
		} catch( Exception e ) {
			Logger.getGlobal().log(Level.SEVERE, "Error while loading the notification view.", e);
		}
	}

	public void show() {
		Platform.runLater(() -> {
			PauseTransition delay = new PauseTransition(Duration.seconds(8));
			delay.setOnFinished( event -> stage.close() );
			delay.play();

			stage.show();
			Rectangle2D screenBounds = Screen.getPrimary().getVisualBounds();
			double x = screenBounds.getMinX() + screenBounds.getWidth() - rootLayout.getWidth() - 20;
			double y = screenBounds.getMinY() + screenBounds.getHeight() - rootLayout.getHeight() - 20;
			stage.setX(x);
			stage.setY(y);
		});
	}
}
