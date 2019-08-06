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
 package fr.elyssif.client.gui.view;

import java.io.IOException;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.jfoenix.controls.JFXDialog;

import fr.elyssif.client.gui.controller.FileDialogController;
import fr.elyssif.client.gui.model.File;
import fr.elyssif.client.gui.repository.FileRepository;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

/**
 * File details dialog.
 * @author Jérémy LAMBERT
 *
 */
public class FileDialog {

	public static final int MODE_SEND = 0;
	public static final int MODE_RECEIVE = 1;

	private FileRepository repository;
	private FileDialogController controller;
	private ResourceBundle bundle;

	private File file;

	/**
	 * Create a new instance of a lookup modal
	 * @param file the file to display
	 * @param repository the repository used for lookup
	 * @param bundle the language bundle to use for this dialog
	 */
	public FileDialog(File file, FileRepository repository, ResourceBundle bundle) {
		this.file = file;
		this.repository = repository;
		this.bundle = bundle;
	}

	/**
	 * Get the file displayed by this modal.
	 * @return the file displayed by this modal
	 */
	public final File getFile() {
		return file;
	}

	/**
	 * Set the file displayed by this modal.
	 * @param file the file displayed by this modal
	 */
	public final void setFile(File file) {
		this.file = file;
	}

	/**
	 * Show a new file dialog.
	 * If the owner window for the dialog is set, input to all windows in the dialog's owner
	 * chain is blocked while the dialog is being shown. Clicking the overlay closes the dialog.
	 * @param ownerPane the container pane on which the dialog will popup
	 * @param mode specifies the fields visible on the dialog.
	 * If MODE_SENT, the "decrypt" button is hidden.
	 * If MODE_RECEIVE, the "delete" button is hidden.
	 */
	public void showDialog(StackPane ownerPane, int mode) {

		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/FileDialogView.fxml"));
			loader.setResources(bundle);

			VBox rootLayout = (VBox) loader.load();
			final JFXDialog dialog = new JFXDialog(ownerPane, rootLayout, JFXDialog.DialogTransition.CENTER);

			controller = (FileDialogController) loader.getController();
			controller.setRepository(repository);
			controller.setParentDialog(dialog);
			controller.setFile(file, mode);

			dialog.show();

		} catch (IOException e) {
			Logger.getGlobal().log(Level.SEVERE, "Couldn't load file dialog.", e);
		}
	}

	/**
	 * Close the dialog if open.
	 */
	public void closeDialog() {
		if(controller != null) {
			controller.close();
		}
	}

}
