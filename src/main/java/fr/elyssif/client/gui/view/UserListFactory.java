package fr.elyssif.client.gui.view;

import com.jfoenix.controls.JFXListView;

import fr.elyssif.client.gui.model.User;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;

/**
 * List factory for users.
 * @author Jérémy LAMBERT
 *
 */
public class UserListFactory implements ListFactory<User> {

	/**
	 * Setup a table to be a user table. Sets the text, icon, formatting and coloring.
	 * @param listView the list to prepare
	 * @param list the observable list containing the values to show in the table
	 */
	@Override
	public void make(JFXListView<?> listView, ObservableList<User> list) {
		make(listView, list, null);
	}

	/**
	 * Setup a table to be a user table. Sets the text, icon, formatting and coloring.
	 * @param listView the list to prepare
	 * @param userList the list containing the values to show in the table
	 * @param onMouseClicked the event handler executed on a list cell click
	 */
	@SuppressWarnings("unchecked")
	@Override
	public final void make(JFXListView<?> listView, ObservableList<User> userList, EventHandler<? super MouseEvent> onMouseClicked) {
		Image userIcon = new Image("view/img/account.png");
		JFXListView<User> userListView = (JFXListView<User>) listView;
		userListView.setItems(userList);
		userListView.setCellFactory(param -> {
			JFXListCellAnimated<User> cell = new JFXListCellAnimated<User>() {
				private ImageView imageView = new ImageView(userIcon);

				@Override
				public void updateItem(User user, boolean empty) {
					super.updateItem(user, empty);

					if (empty) {
						setText(null);
						setGraphic(null);
					} else {
						setText(user.getName().get() + '\n' + user.getEmail().get());
						setGraphic(imageView);
					}
				}
			};
			cell.setOnMouseClicked(onMouseClicked);

			return cell;
		});
	}
}
