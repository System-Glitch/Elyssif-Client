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

import java.net.URL;
import java.util.HashMap;
import java.util.ResourceBundle;
import java.util.logging.Logger;

import com.jfoenix.controls.JFXListView;

import fr.elyssif.client.Config;
import fr.elyssif.client.callback.LogoutCallback;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

/**
 * Controller for the side menu view.
 * @author Jérémy LAMBERT
 *
 */
public final class SideMenuController extends Controller {

	@FXML private JFXListView<Label> sideList;

	private HashMap<Integer, Controller> binding;
	private Controller currentController;

	public void initialize(URL location, ResourceBundle resources) {
		if(Config.getInstance().isVerbose())
			Logger.getGlobal().info("Loading side menu controller.");
		super.initialize(location, resources);

		binding = new HashMap<Integer, Controller>();
		registerListener();
		selectIndex(0);
		getPane().setVisible(true);

		sideList.getItems().get(1).setDisable(true); // TODO enable contacts when ready
	}

	/**
	 * Get the currently active controller.
	 * @return currentController
	 */
	protected Controller getCurrentController() {
		return currentController;
	}

	/**
	 * Set the currently active controller.
	 * Clears the menu list selection.
	 * @param currentController
	 */
	protected void setCurrentController(Controller currentController) {
		sideList.getSelectionModel().clearSelection();
		this.currentController = currentController;
	}

	/**
	 * Select the given index in the menu list.
	 * @param index
	 */
	protected void selectIndex(int index) {
		sideList.getSelectionModel().select(index);
	}

	/**
	 * Bind a controller to the given index in the menu list.<br>
	 * When the given index is selected, the given controller is shown using
	 * <code>showNext()</code> on the currently visible controller.
	 * @param index
	 * @param controller
	 */
	protected void bind(Integer index, Controller controller) {
		if(controller == null) throw new IllegalArgumentException("Cannot bind null controller");
		binding.put(index, controller);

		if(binding.size() == 1) {
			currentController = controller;
		}
	}

	private void registerListener() {
		sideList.getSelectionModel().selectedIndexProperty().addListener((o, oldVal, newVal) -> {
			if(binding.containsKey(newVal)) {
				if(currentController == null && binding.containsKey(oldVal)) {
					currentController = binding.get(oldVal);
				}
				Controller next = binding.get(newVal);

				if(currentController != null) {
					currentController.showNext(next, true);
				} else {
					next.show(true);
				}

				currentController = next;
			}
		});
	}

	@FXML
	private void logoutClicked() {
		((Lockable) getParentController()).setLocked(true);
		MainController.getInstance().getAuthenticator().logout(new LogoutCallback());
	}
}
