package fr.elyssif.client.gui.controller;

import fr.elyssif.client.gui.view.SlideDirection;
import javafx.animation.Interpolator;
import javafx.animation.TranslateTransition;
import javafx.fxml.FXML;
import javafx.scene.layout.Pane;
import javafx.util.Duration;

/**
 * SUper-class for controllers. Handles sliding transitions and back buttons.
 * @author Jérémy LAMBERT
 *
 */
abstract class Controller {

	private Controller backController;
	private SlideDirection slideDirection = SlideDirection.NONE;
	private TranslateTransition openTransition;
	private TranslateTransition closeTransition;
	
	@FXML private Pane pane;
	
	@FXML
	protected void initialize() {
		openTransition = new TranslateTransition(new Duration(200), pane);
		openTransition.setToX(0);
		openTransition.setInterpolator(Interpolator.EASE_OUT);
		
		closeTransition = new TranslateTransition(new Duration(200), pane);
		closeTransition.setInterpolator(Interpolator.EASE_IN);
		closeTransition.setFromX(0);
		closeTransition.setOnFinished((e) -> backController.show(false));
	}
	
	/**
	 * Show the according view. Plays the transition if slide direction is not "none" and transition is true.
	 * @param transition - plays transition if true, simply puts pane to front if false
	 */
	protected void show(boolean transition) {
		pane.toFront();
		if(transition && !slideDirection.equals(SlideDirection.NONE)) {
			if(slideDirection == SlideDirection.VERTICAL)
				openTransition.setFromY(-pane.getHeight()-40);
			else if(slideDirection == SlideDirection.HORIZONTAL)
				openTransition.setFromX(-pane.getWidth()-40);
			openTransition.play();
		}
	}
	
	/**
	 * Show the previous view. Plays the transition backwards.
	 */
	protected void back() {
		if(slideDirection == SlideDirection.VERTICAL)
			closeTransition.setToY(-pane.getHeight()-40);
		else if(slideDirection == SlideDirection.HORIZONTAL)
			closeTransition.setToX(-pane.getWidth()-40);
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
	
	/**
	 * Get the main pane for this controller.
	 * @return pane
	 */
	protected final Pane getPane() {
		return pane;
	}

	/**
	 * Get the controller which should be called when the back button is clicked.
	 * @return backController
	 */
	protected final Controller getBackController() {
		return backController;
	}

	/**
	 * Set the controller which should be called when the back button is clicked.
	 * @return backController
	 */
	protected final void setBackController(Controller backController) {
		this.backController = backController;
	}
	
}
