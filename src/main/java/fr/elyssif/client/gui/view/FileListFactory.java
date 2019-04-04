package fr.elyssif.client.gui.view;

import java.util.Date;

import com.jfoenix.controls.JFXListView;

import fr.elyssif.client.gui.model.File;
import fr.elyssif.client.gui.model.User;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

/**
 * List factory for files.
 * @author Jérémy LAMBERT
 *
 */
public class FileListFactory implements ListFactory<File> {

	/**
	 * Setup a table to be a file table. Sets the text, icon, formatting and coloring.
	 * @param listView the list to prepare
	 * @param list the observable list containing the values to show in the table
	 */
	@Override
	public void make(JFXListView<?> listView, ObservableList<File> list) {
		make(listView, list, null);
	}

	/**
	 * Setup a table to be a file table. Sets the text, icon, formatting and coloring.
	 * @param listView the list to prepare
	 * @param fileList the list containing the values to show in the table
	 * @param onMouseClicked the event handler executed on a list cell click
	 */
	@SuppressWarnings("unchecked")
	@Override
	public final void make(JFXListView<?> listView, ObservableList<File> fileList, EventHandler<? super MouseEvent> onMouseClicked) {
		JFXListView<File> fileListView = (JFXListView<File>) listView;
		fileListView.setItems(fileList);
		fileListView.setCellFactory(param -> {
			JFXListCellAnimated<File> cell = new JFXListCellAnimated<File>() {
				@Override
				public void updateItem(File file, boolean empty) {
					super.updateItem(file, empty);

					if (empty) {
						setText(null);
						setGraphic(null);
					} else {
						setGraphic(createFileEntry(file));
					}
				}
			};
			cell.setOnMouseClicked(onMouseClicked);

			return cell;
		});
	}

	private HBox createFileEntry(File file) {
		HBox container = new HBox();
		container.setSpacing(10);
		container.setAlignment(Pos.CENTER);

		ImageView icon = new ImageView(file.getDecipheredAt() != null ? "view/img/checked.png" : "view/img/stopwatch.png");
		Tooltip.install(icon, new Tooltip("test"));

		VBox textContainer = new VBox();
		
		Label fileNameLabel = new Label(file.getName().get());
		fileNameLabel.getStyleClass().add("text-lg");

		User sender = file.getSender().get();
		Label fromLabel = new Label("From: " + sender.getName().get() + "(" + sender.getEmail().get() + ")"); // TODO language file
		
		BorderPane datesContainer = new BorderPane();
		Label sentLabel = new Label("Sent ");
		datesContainer.setLeft(sentLabel); // TODO language file
		BorderPane.setMargin(sentLabel, new Insets(0, 15, 0, 0));
		
		Date receivedDate = file.getDecipheredAt().get();
		datesContainer.setRight(new Label(receivedDate != null ? "Received " + receivedDate.toString() : "Pending")); // TODO language file + date format
		
		textContainer.getChildren().addAll(fileNameLabel, fromLabel, datesContainer);

		container.getChildren().addAll(icon, textContainer);
		return container;
	}
}
