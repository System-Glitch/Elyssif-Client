package fr.elyssif.client.gui.notification;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

public class NotificationController {

	@FXML private ImageView icon;
	@FXML private Label title;
	@FXML private Label message;

	@FXML
	public void onClickDismiss(ActionEvent e) {
		((Stage) ((Button) e.getSource()).getScene().getWindow()).hide();
	}

	protected void setTitle(String title) {
		this.title.setText(title);
	}

	protected void setMessage(String message) {
		this.message.setText(message);
	}
}
