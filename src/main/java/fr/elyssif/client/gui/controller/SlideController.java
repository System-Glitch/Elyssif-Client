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

import fr.elyssif.client.gui.view.SlideDirection;
import javafx.animation.Interpolator;
import javafx.animation.TranslateTransition;
import javafx.util.Duration;

/**
 * Super-class for controllers with a sliding animation.
 * @author Jérémy LAMBERT
 *
 */
public abstract class SlideController extends Controller {

	private static final double SLIDING_DURATION = 500;

	private SlideDirection slideDirection = SlideDirection.NONE;
	private TranslateTransition openTransition;
	private TranslateTransition closeTransition;

	public void initialize(URL location, ResourceBundle resources) {
		super.initialize(location, resources);

		openTransition = new TranslateTransition(new Duration(SLIDING_DURATION), getPane());
		openTransition.setToX(0);
		openTransition.setInterpolator(Interpolator.EASE_OUT);

		closeTransition = new TranslateTransition(new Duration(SLIDING_DURATION), getPane());
		closeTransition.setInterpolator(Interpolator.EASE_IN);
		closeTransition.setFromX(0);
		closeTransition.setOnFinished((e) -> getBackController().show(false));
	}

	/**
	 * Show the according view. Plays the transition if slide direction is not "none" and transition is true.
	 * @param transition plays transition if true, simply puts pane to front if false
	 * @param backController the controller which should be called when the back button is clicked
	 */
	protected void show(boolean transition, Controller backController) {
		super.show(transition, backController);
		if(transition && !slideDirection.equals(SlideDirection.NONE)) {
			if(slideDirection == SlideDirection.VERTICAL)
				openTransition.setFromY(-getPane().getHeight()-40);
			else if(slideDirection == SlideDirection.HORIZONTAL)
				openTransition.setFromX(-getPane().getWidth()-40);
			openTransition.play();
		}
	}

	/**
	 * Show the previous view. Plays the transition backwards.
	 */
	protected void back() {
		if(slideDirection == SlideDirection.VERTICAL)
			closeTransition.setToY(-getPane().getHeight()-40);
		else if(slideDirection == SlideDirection.HORIZONTAL)
			closeTransition.setToX(-getPane().getWidth()-40);
		closeTransition.play();
	}

	/**
	 * Get the slide transition direction.
	 * @return slideDirection
	 * @see SlideDirection
	 */
	protected final SlideDirection getSlideDirection() {
		return slideDirection;
	}

	/**
	 * Set the slide transition direction
	 * @param slideDirection
	 */
	protected final void setSlideDirection(SlideDirection slideDirection) {
		this.slideDirection = slideDirection;
		if(slideDirection == SlideDirection.VERTICAL) {
			openTransition.setToY(0);
			openTransition.setToX(0);
			openTransition.setFromX(0);

			closeTransition.setFromY(0);
			closeTransition.setToX(0);
			closeTransition.setFromX(0);
		} else if(slideDirection == SlideDirection.HORIZONTAL) {
			openTransition.setToX(0);
			openTransition.setToY(0);
			openTransition.setFromY(0);

			closeTransition.setFromX(0);
			closeTransition.setToY(0);
			closeTransition.setFromY(0);
		}
	}
}
