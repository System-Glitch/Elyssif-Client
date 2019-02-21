package fr.elyssif.client.gui.controller;

import java.net.URL;
import java.util.HashMap;
import java.util.ResourceBundle;
import java.util.logging.Logger;

import com.jfoenix.controls.JFXListView;

import fr.elyssif.client.Config;
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
		binding.put(index, controller);
	}

	private void registerListener() {
		sideList.getSelectionModel().selectedIndexProperty().addListener((o, oldVal, newVal) -> {
			if(binding.containsKey(newVal)) {
				if(currentController == null && binding.containsKey(oldVal)) {
					currentController = binding.get(oldVal);
				}
				Controller next = binding.get(newVal);
				currentController.showNext(next, true);
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
