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

import javafx.animation.FadeTransition;
import javafx.animation.Interpolator;
import javafx.fxml.FXML;
import javafx.scene.layout.Pane;
import javafx.util.Duration;

/**
 * Super-class for controllers with a fade animation
 * @author Jérémy LAMBERT
 *
 */
public abstract class FadeController extends Controller {

	private static final double FADE_DURATION = 250;

	private FadeTransition openTransition;
	private FadeTransition closeTransition;
	private FadeTransition nextTransition;

	@FXML private Pane fadePane;

	public void initialize(URL location, ResourceBundle resources) {
		super.initialize(location, resources);

		openTransition = new FadeTransition(new Duration(FADE_DURATION), fadePane);
		openTransition.setInterpolator(Interpolator.EASE_OUT);
		openTransition.setFromValue(0);
		openTransition.setToValue(1);

		closeTransition = new FadeTransition(new Duration(FADE_DURATION), fadePane);
		closeTransition.setInterpolator(Interpolator.EASE_IN);
		closeTransition.setFromValue(1);
		closeTransition.setToValue(0);
		closeTransition.setOnFinished(e -> onHide());

		nextTransition = new FadeTransition(new Duration(FADE_DURATION), fadePane);
		nextTransition.setInterpolator(Interpolator.EASE_IN);
		nextTransition.setFromValue(1);
		nextTransition.setToValue(0);
		nextTransition.setOnFinished((e) -> onNext());

		fadePane.opacityProperty().set(0);
	}

	/**
	 * Get the container which is fading.
	 * @return fadePane
	 */
	protected final Pane getFadePane() {
		return fadePane;
	}

	/**
	 * Show the according view. Plays the transition if slide direction is not "none" and transition is true.
	 * @param transition plays transition if true, simply puts pane to front if false
	 * @param backController the controller which should be called when the back button is clicked
	 */
	protected void show(boolean transition, Controller backController) {
		super.show(transition, backController);
		if(transition)
			openTransition.play();
		else
			fadePane.opacityProperty().set(1);
	}

	/**
	 * Set the next controller and show it.
	 * @param controller the controller of the next view to show
	 * @param transition
	 */
	public void showNext(Controller controller, boolean transition) {
		setNextController(controller);
		nextTransition.play();
	}

	/**
	 * Executed when the next transition is over
	 * and the next controller is shown.
	 */
	protected void onNext() {
		getNextController().show(true, this);
	}

	/**
	 * Executed when the view is hidden after the close animation transition end
	 */
	protected void onHide() {
		getBackController().show(getBackController() instanceof FadeController);
	}

	/**
	 * Show the previous view. Plays the transition backwards.
	 */
	protected void back() {
		closeTransition.play();
	}
}
