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
import java.util.ResourceBundle;
import java.util.logging.Logger;

import com.jfoenix.controls.JFXButton;

import fr.elyssif.client.Config;
import fr.elyssif.client.gui.model.Model;
import fr.elyssif.client.gui.view.Paginator;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

/**
 * Controller for the paginate controls component.
 * @author Jérémy LAMBERT
 *
 */
public final class PaginateController extends Controller {

	@FXML private JFXButton previousButton;
	@FXML private JFXButton nextButton;

	@FXML private Label pageLabel;

	private Paginator<? extends Model<?>> paginator;
	private Runnable callback;
	private int page = 1;

	public void initialize(URL location, ResourceBundle resources) {
		if(Config.getInstance().isVerbose())
			Logger.getGlobal().info("Loading paginate controller.");
		super.initialize(location, resources);

		show(false);
	}

	public final Paginator<? extends Model<?>> getPaginator() {
		return paginator;
	}

	public final void setPaginator(Paginator<? extends Model<?>> paginator) {
		this.paginator = paginator;
		pageLabel.setText(paginator.getCurrentPage() + "/" + paginator.getMaxPage());
		previousButton.setDisable(paginator.getCurrentPage() == 1);
		nextButton.setDisable(paginator.getCurrentPage() == paginator.getMaxPage());
	}

	public final void setOnPageChange(Runnable callback) {
		this.callback = callback;
	}

	public final int getPage() {
		return page;
	}

	@FXML
	private void onPreviousClicked() {
		if(page > 1) {
			page--;
		}
		callback.run();
	}

	@FXML
	private void onNextClicked() {
		page++;
		callback.run();
	}

}
