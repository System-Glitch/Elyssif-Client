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
 package fr.elyssif.client.gui.controller.auth;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Logger;

import com.jfoenix.controls.JFXSpinner;

import fr.elyssif.client.Config;
import fr.elyssif.client.gui.controller.Controller;
import fr.elyssif.client.gui.controller.FadeController;
import javafx.fxml.FXML;

/**
 * Controller for the loader view
 * @author Jérémy LAMBERT
 *
 */
public final class LoaderController extends FadeController {

	@FXML
	private JFXSpinner spinner;

	public void initialize(URL location, ResourceBundle resources) {
		if(Config.getInstance().isVerbose())
			Logger.getGlobal().info("Loading loader controller.");
		super.initialize(location, resources);

		spinner.setVisible(false);
	}

	/**
	 * {@inheritDoc}
	 */
	protected void show(boolean transition, Controller backController) {
		super.show(transition, backController);
		spinner.setVisible(true);
	}

	@Override
	public void onHide() {
		super.onHide();
		spinner.setVisible(false);
	}

}
