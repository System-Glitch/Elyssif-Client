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
 package fr.elyssif.client.gui.controller;

import java.awt.Desktop;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXSpinner;

import fr.elyssif.client.Config;
import fr.elyssif.client.gui.repository.FileRepository;
import fr.elyssif.client.gui.validation.ServerValidator;
import fr.elyssif.client.gui.view.ImageSlideTransition;
import fr.elyssif.client.gui.view.TadaAnimation;
import fr.elyssif.client.gui.view.ViewUtils;
import javafx.animation.FadeTransition;
import javafx.application.Platform;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.fxml.FXML;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

/**
 * Super-class for "send" and "receive" controllers which
 * share the same animation and overall logic.
 * @author Jérémy LAMBERT
 *
 */
public abstract class EncryptionController extends FadeController implements Lockable, Validatable {

	@FXML private ImageView image;
	@FXML private JFXSpinner spinner;
	@FXML private VBox formContainer;
	@FXML private JFXButton button;

	private java.io.File destinationFile;
	private SimpleBooleanProperty disableProperty;
	private FileRepository fileRepository;

	private HashMap<String, ServerValidator> serverValidators;
	private double progress = 0;

	private FadeTransition formContainerTransition;
	private FadeTransition buttonTransition;
	private FadeTransition spinnerTransition;
	private ImageSlideTransition slideTransition;

	public void initialize(URL location, ResourceBundle resources) {
		if(Config.getInstance().isVerbose())
			Logger.getGlobal().info("Loading encryption controller.");
		super.initialize(location, resources);

		disableProperty = new SimpleBooleanProperty(false);
		serverValidators = new HashMap<String, ServerValidator>();

		bindControls();
		setupValidators();

		Platform.runLater(() -> fileRepository = new FileRepository());
	}

	protected final java.io.File getDestinationFile() {
		return destinationFile;
	}

	protected final void setDestinationFile(java.io.File destinationFile) {
		this.destinationFile = destinationFile;
	}

	protected final FileRepository getFileRepository() {
		return fileRepository;
	}

	protected final double getProgress() {
		return progress;
	}

	protected final void setProgress(double progress) {
		this.progress = progress;
		Platform.runLater(() -> spinner.setProgress(progress));
	}

	protected void playAnimation() {
		spinner.setVisible(true);
		spinner.setProgress(ProgressIndicator.INDETERMINATE_PROGRESS);
		formContainerTransition = ViewUtils.createFadeOutTransition(formContainer, Duration.millis(750));
		buttonTransition = ViewUtils.createFadeOutTransition(button, Duration.millis(750));
		spinnerTransition = ViewUtils.createFadeInTransition(spinner, Duration.millis(750));
		spinnerTransition.setDelay(Duration.millis(1000));

		slideTransition = new ImageSlideTransition(image, getFadePane().getHeight(), Duration.millis(750));
		spinner.toFront();
		formContainerTransition.play();
		buttonTransition.play();
		spinnerTransition.play();
		slideTransition.play();

		slideTransition.setOnFinished(e -> {
			process(() -> {
				resetForm();
				TadaAnimation tada = new TadaAnimation(image);
				tada.play();
				tada.setOnFinished(e2 -> {
					openFile();
					revertAnimation();
				});
			}, () -> {
				// On process failure
				destinationFile.delete();
				resetForm();
				revertAnimation();
			});
		});
	}

	protected void revertAnimation() {
		formContainerTransition.setRate(-1);
		buttonTransition.setRate(-1);
		spinnerTransition.setRate(-1);
		slideTransition.revert();

		spinnerTransition.setDelay(Duration.millis(0));

		formContainerTransition.setDelay(Duration.millis(1000));
		buttonTransition.setDelay(Duration.millis(1000));
		slideTransition.setDelay(Duration.millis(1000));

		formContainerTransition.play();
		buttonTransition.play();
		spinnerTransition.play();
		slideTransition.play();
		slideTransition.setOnFinished(e3 -> {
			spinner.toBack();
			progress = 0;
			setLocked(false);
			spinner.setVisible(false);
		});
	}

	private void openFile() {
		new Thread(() -> {
			Desktop desktop = Desktop.getDesktop();
			try {
				if(Desktop.isDesktopSupported() && desktop.isSupported(Desktop.Action.OPEN)) {
					desktop.open(destinationFile.getParentFile());
				} else {
					Logger.getGlobal().warning("Desktop open action not supported.");
				}
			} catch (IOException e) {
				Logger.getGlobal().log(Level.SEVERE, "Couldn't open file explorer.", e);
			}
		}).start();
	}

	@FXML
	private void buttonClicked() {
		if(validateAll()) {
			onButtonClicked();
		}
	}

	protected abstract void process(Runnable successCallback, Runnable failureCallback);

	/**
	 * Executed when the user clicks the button
	 * and the form is validated (using <code>validateAll()</code>)
	 */
	protected abstract void onButtonClicked();

	@Override
	public void setLocked(boolean locked) {
		disableProperty.set(locked);
		MainController.getInstance().setCanExit(!locked);
		((Lockable) MainController.getInstance().getController("app")).setLocked(locked);
	}

	@Override
	public void bindControls() {
		formContainer.disableProperty().bind(disableProperty);
		button.disableProperty().bind(disableProperty);
	}

	@Override
	public HashMap<String, ServerValidator> getServerValidators() {
		return serverValidators;
	}

	protected JFXSpinner getSpinner() {
		return spinner;
	}

}
