package fr.elyssif.client.gui.controller;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Logger;

import com.jfoenix.controls.JFXButton;
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

	@FXML private JFXButton refreshButton;

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
	 * @param transition plays transition if true, simply puts pane to front if false
	 * @param backController the controller which should be called when the back button is clicked
	 */
	protected void show(boolean transition, Controller backController) {
		super.show(transition, backController);
		drawer.close();
		sideMenuController.selectIndex(0);
		((HomeController) containerController.getController("home")).refresh();
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

		sideMenuController.bind(0, containerController.getController("home"));
		sideMenuController.bind(2, containerController.getController("settings"));
	}

	@Override
	public void bindControls() {
		getFadePane().disableProperty().bind(disableProperty);
		drawer.disableProperty().bind(disableProperty);
		refreshButton.disableProperty().bind(containerController.getController("home").getPane().disableProperty());
		sideMenuController.getPane().disableProperty().bind(disableProperty);
	}

	@Override
	public void setLocked(boolean locked) {
		disableProperty.set(locked);
	}

	@FXML
	private void sendClicked() {
		Controller sendViewController = containerController.getController("send");
		sideMenuController.getCurrentController().showNext(sendViewController, true);
		sideMenuController.setCurrentController(sendViewController);
		drawer.close();
	}

	@FXML
	private void receiveClicked() {
		Controller receiveViewController = containerController.getController("receive");
		sideMenuController.getCurrentController().showNext(receiveViewController, true);
		sideMenuController.setCurrentController(receiveViewController);
		drawer.close();
	}

	@FXML
	private void refreshClicked() {
		((HomeController) containerController.getController("home")).refresh();
	}

}
