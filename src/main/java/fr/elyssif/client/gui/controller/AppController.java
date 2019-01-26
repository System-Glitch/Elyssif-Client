package fr.elyssif.client.gui.controller;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Logger;

import com.jfoenix.controls.JFXDrawer;
import com.jfoenix.controls.JFXHamburger;

import fr.elyssif.client.Config;
import javafx.animation.Interpolator;
import javafx.animation.Transition;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.fxml.FXML;
import javafx.scene.layout.StackPane;

/**
 * Controller for the main app view.
 * @author Jérémy LAMBERT
 *
 */
public final class AppController extends FadeController implements Lockable {

	@FXML private StackPane titleBurgerContainer;
	@FXML private JFXHamburger titleBurger;
	@FXML private JFXDrawer drawer;

	@FXML private AppContainerController containerController;

	@FXML private SideMenuController sideMenuController;

	private SimpleBooleanProperty disableProperty;

	public void initialize(URL location, ResourceBundle resources) {
		if(Config.getInstance().isVerbose())
			Logger.getGlobal().info("Loading app controller.");
		super.initialize(location, resources);
		disableProperty = new SimpleBooleanProperty(false);
		bindControls();
		initSideMenu();
	}

	/**
	 * Show the according view. Plays the transition if slide direction is not "none" and transition is true.
	 * @param transition - plays transition if true, simply puts pane to front if false
	 * @param backController - the controller which should be called when the back button is clicked
	 */
	protected void show(boolean transition, Controller backController) {
		super.show(transition, backController);
		sideMenuController.getPane().setDisable(false);
		drawer.close();
	}

	private void initSideMenu() {
		titleBurger.getAnimation().setInterpolator(Interpolator.EASE_BOTH);
		drawer.setOnDrawerOpening(e -> {
			final Transition animation = titleBurger.getAnimation();
			animation.setRate(1);
			animation.play();
		});
		drawer.setOnDrawerClosing(e -> {
			final Transition animation = titleBurger.getAnimation();
			animation.setRate(-1);
			animation.play();
		});
		titleBurgerContainer.setOnMouseClicked(e -> {
			if (drawer.isClosed() || drawer.isClosing()) {
				drawer.open();
			} else {
				drawer.close();
			}
		});

		// TODO bind menus
		// sideMenuController.bind(0, containerController.getController("test"));
	}

	public void bindControls() {
		getFadePane().disableProperty().bind(disableProperty);
	}

	public void setLocked(boolean locked) {
		disableProperty.set(locked);
	}

}
