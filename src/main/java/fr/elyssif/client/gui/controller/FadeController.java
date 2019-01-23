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
		closeTransition.setOnFinished((e) -> getBackController().show(getBackController() instanceof FadeController));

		nextTransition = new FadeTransition(new Duration(FADE_DURATION), fadePane);
		nextTransition.setInterpolator(Interpolator.EASE_IN);
		nextTransition.setFromValue(1);
		nextTransition.setToValue(0);
		nextTransition.setOnFinished((e) -> getNextController().show(true, this));

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
	 * @param transition - plays transition if true, simply puts pane to front if false
	 * @param backController - the controller which should be called when the back button is clicked
	 */
	protected void show(boolean transition, Controller backController) {
		super.show(transition, backController);
		if(transition)
			openTransition.play();
	}

	/**
	 * Set the next controller and show it.
	 * @param controller - the controller of the next view to show
	 * @param transition
	 */
	public void showNext(Controller controller, boolean transition) {
		setNextController(controller);
		nextTransition.play();
	}

	/**
	 * Show the previous view. Plays the transition backwards.
	 */
	protected void back() {
		closeTransition.play();
	}
}
