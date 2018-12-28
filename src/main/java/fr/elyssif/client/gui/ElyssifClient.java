package fr.elyssif.client.gui;

import java.util.logging.Level;
import java.util.logging.Logger;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

/**
 * Main class for the GUI.
 * @author Jérémy LAMBERT
 *
 */
public final class ElyssifClient extends Application {

	@Override
	public void start(Stage primaryStage) throws Exception {
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/MainView.fxml"));
			StackPane rootLayout = (StackPane) loader.load();
			Scene scene = new Scene(rootLayout);
			scene.getStylesheets().addAll(getClass().getResource("/com/jfoenix/assets/css/jfoenix-fonts.css").toExternalForm(),
										getClass().getResource("/com/jfoenix/assets/css/jfoenix-design.css").toExternalForm(),
										getClass().getResource("/view/css/application.css").toExternalForm());
			
			primaryStage.setTitle("Elyssif");
			primaryStage.setMinHeight(635);
			primaryStage.setMinWidth(1050);

			//setupIcons(primaryStage);

			primaryStage.setScene(scene);
			primaryStage.show();
		} catch( Exception e ) {
			Logger.getGlobal().log(Level.SEVERE, "Error while loading the graphical interface.", e);
			Platform.exit();
		}
	}

	private void setupIcons(Stage primaryStage) {
		primaryStage.getIcons().add(new Image(getClass().getResource("/view/logo16.png").toExternalForm()));
		primaryStage.getIcons().add(new Image(getClass().getResource("/view/logo32.png").toExternalForm()));
		primaryStage.getIcons().add(new Image(getClass().getResource("/view/logo64.png").toExternalForm()));
	}
	
	public static void run(String[] args) {
		launch(args);
	}

}
