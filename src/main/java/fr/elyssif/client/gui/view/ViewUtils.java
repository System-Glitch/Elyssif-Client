package fr.elyssif.client.gui.view;

import java.util.ResourceBundle;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXDialog;
import com.jfoenix.controls.JFXDialogLayout;

import javafx.animation.FadeTransition;
import javafx.animation.Interpolator;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.input.ContextMenuEvent;
import javafx.scene.layout.StackPane;
import javafx.util.Duration;

/**
 * Utility class for views.
 * Allows to create transitions and more.
 * @author Jérémy LAMBERT
 *
 */
public abstract class ViewUtils {

	public static FadeTransition createFadeTransition(Node node) {
		return createFadeTransition(node, Duration.millis(1000));
	}

	public static FadeTransition createFadeTransition(Node node, Duration duration) {
		FadeTransition ft = new FadeTransition(duration, node);
		ft.setInterpolator(Interpolator.EASE_BOTH);
		return ft;
	}

	public static FadeTransition createFadeInTransition(Node node) {
		return createFadeInTransition(node, Duration.millis(1000));
	}

	public static FadeTransition createFadeInTransition(Node node, Duration duration) {
		FadeTransition ft = createFadeTransition(node, duration);
		ft.setFromValue(0);
		ft.setToValue(1.0);
		return ft;
	}

	public static FadeTransition createFadeOutTransition(Node node) {
		return createFadeOutTransition(node, Duration.millis(1000));
	}

	public static FadeTransition createFadeOutTransition(Node node, Duration duration) {
		FadeTransition ft = createFadeTransition(node, duration);
		ft.setFromValue(1.0);
		ft.setToValue(0);
		return ft;
	}

	/**
	 * Fade-out a label before updating its value through the
	 * given callback.
	 * @param label
	 * @param callback
	 */
	public static void blinkUpdateLabel(Label label, Runnable callback) {
		var fot = ViewUtils.createFadeOutTransition(label, Duration.millis(250));
		var fit = ViewUtils.createFadeInTransition(label, Duration.millis(250));
		fot.setOnFinished(e -> {
			callback.run();
			fit.play();
		});
		fot.play();
	}

	/**
	 * Disable context menus for the given scene by consuming the
	 * CONTEXT_MENU_REQUESTED event.
	 * @param scene
	 */
	public static void disableContextMenu(Scene scene) {
		scene.addEventFilter(ContextMenuEvent.CONTEXT_MENU_REQUESTED, evt -> {
			evt.consume();
		});
	}

	/**
	 * Build a confirm dialog with a "warning" theme.
	 * The dialog is not yet shown.
	 * @param container
	 * @param bundle
	 * @param message a resource entry
	 * @param showDeleteIcon if true, adds a trash bin icon in the "yes" button
	 * @param onAccept the action to execute on clicking "yes"
	 * @param closeOnAccept
	 * @return confirmDialog
	 */
	public static JFXDialog buildConfirmDialog(StackPane container, ResourceBundle bundle, String message, boolean showDeleteIcon, EventHandler<ActionEvent> onAccept, boolean closeOnAccept) {
		var confirmDialog = new JFXDialog();
		confirmDialog.setDialogContainer(container);

		JFXDialogLayout content = new JFXDialogLayout();
		Label header = new Label(bundle.getString("confirm"), new ImageView("view/img/warning.png"));
		header.getStyleClass().add("text-white");
		content.setHeading(header);
		Label body = new Label(bundle.getString(message).replace("\\n", "\n"));
		body.getStyleClass().add("text-md");
		content.setBody(body);
		content.getStyleClass().add("dialog-warning");

		JFXButton cancelButton = new JFXButton(bundle.getString("cancel"));
		cancelButton.setMaxHeight(Double.MAX_VALUE);
		cancelButton.setOnAction(e -> {
			confirmDialog.close();
		});

		JFXButton acceptButton = new JFXButton(bundle.getString("yes"));
		acceptButton.getStyleClass().add("red-A700");
		acceptButton.setOnAction(e -> {
			if(closeOnAccept) {
				confirmDialog.close();
			}
			onAccept.handle(e);
		});

		if(showDeleteIcon) {
			ImageView image = new ImageView("view/img/delete.png");
			image.setFitWidth(24);
			image.setFitHeight(24);
			acceptButton.setGraphic(image);
		}


		content.setActions(cancelButton, acceptButton);

		confirmDialog.setContent(content);
		confirmDialog.setTransitionType(JFXDialog.DialogTransition.CENTER);
		confirmDialog.setOverlayClose(false);

		return confirmDialog;
	}

	/**
	 * Build an error dialog. The dialog is not yet shown.
	 * @param container
	 * @param bundle
	 * @param message
	 * @return errorDialog
	 */
	public static JFXDialog buildErrorDialog(StackPane container, ResourceBundle bundle, String message) {
		var errorDialog = new JFXDialog();
		errorDialog.setDialogContainer(container);

		JFXDialogLayout content = new JFXDialogLayout();
		Label header = new Label(bundle.getString("error-header"), new ImageView("view/img/warning.png"));
		header.getStyleClass().add("text-white");
		content.setHeading(header);
		Label body = new Label(message);
		body.getStyleClass().add("text-md");
		content.setBody(body);
		content.getStyleClass().add("dialog-error");

		JFXButton acceptButton = new JFXButton(bundle.getString("ok"));
		acceptButton.getStyleClass().add("blue-700");
		acceptButton.setOnAction(e -> {
			errorDialog.close();
		});

		content.setActions(acceptButton);

		errorDialog.setContent(content);
		errorDialog.setTransitionType(JFXDialog.DialogTransition.CENTER);
		errorDialog.setOverlayClose(false);

		return errorDialog;
	}

}
