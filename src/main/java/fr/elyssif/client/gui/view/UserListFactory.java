package fr.elyssif.client.gui.view;

import com.jfoenix.controls.JFXListCell;
import com.jfoenix.controls.JFXListView;

import fr.elyssif.client.gui.model.User;
import javafx.animation.FadeTransition;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Background;
import javafx.util.Duration;

/**
 * List factory for users.
 * @author Jérémy LAMBERT
 *
 */
public class UserListFactory implements ListFactory<User> {

	/**
	 * Setup a table to be a user table. Sets the columns, formatting and coloring.
	 * @param listView the list to prepare
	 * @param list the observable list containing the values to show in the table
	 */
	@Override
	public void make(JFXListView<?> listView, ObservableList<User> list) {
		make(listView, list, null);
	}

	/**
	 * Setup a table to be a user table. Sets the columns, formatting and coloring.
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
			JFXListCell<User> cell = new JFXListCell<User>() {
				private ImageView imageView = new ImageView(userIcon);
				private boolean wasSelected = false;

				@Override
				public void updateItem(User user, boolean empty) {
					super.updateItem(user, empty);

					Background background = getBackground();

					if((background == null || background.getFills().get(0).getFill().toString().equals("0xffffffff")) && !wasSelected) {
						this.setOpacity(0);
						FadeTransition ft = ViewUtils.createFadeInTransition(this, Duration.millis(800));
						ft.play();

						this.setScaleX(0);
						this.setScaleY(0);
						CenterTransition animation = new CenterTransition(this);
						animation.play();

					}

					wasSelected = isSelected();

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
