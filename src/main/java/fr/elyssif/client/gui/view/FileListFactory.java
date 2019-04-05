package fr.elyssif.client.gui.view;

import java.text.SimpleDateFormat;
import java.util.Date;

import com.jfoenix.controls.JFXListView;

import fr.elyssif.client.gui.model.File;
import fr.elyssif.client.gui.model.User;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.text.TextAlignment;

/**
 * List factory for files.
 * @author Jérémy LAMBERT
 *
 */
public class FileListFactory implements ListFactory<File> {

	public static final int MODE_SEND = 0;
	public static final int MODE_RECEIVE = 1;

	private int mode = MODE_SEND;

	public void setMode(int mode) {
		this.mode = mode;
	}

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
						setText(null);
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
		container.setAlignment(Pos.CENTER_LEFT);

		Date sentDate = file.getCipheredAt().get();
		Date receivedDate = file.getDecipheredAt().get();
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd"); // TODO language file

		ImageView icon = new ImageView(sentDate != null && receivedDate != null ? "view/img/checked.png" : "view/img/stopwatch.png");
		icon.setFitHeight(42);
		icon.setFitWidth(42);


		VBox textContainer = new VBox();

		Label fileNameLabel = new Label(file.getName().get());
		fileNameLabel.getStyleClass().add("text-lg");

		User user = mode == MODE_SEND ? file.getRecipient().get() : file.getSender().get();
		Label fromLabel = new Label((mode == MODE_SEND ? "To: " : "From: ") + user.getName().get() + "(" + user.getEmail().get() + ")"); // TODO language file
		fromLabel.getStyleClass().add("text-sm");

		BorderPane datesContainer = new BorderPane();

		Label sentLabel = new Label(sentDate != null ? "Sent " + format.format(sentDate) : "Pending"); // TODO language file + date format
		sentLabel.getStyleClass().add("text-sm");
		datesContainer.setLeft(sentLabel); // TODO language file
		BorderPane.setMargin(sentLabel, new Insets(0, 15, 0, 0));

		Label receivedLabel = new Label(receivedDate != null ? "Received " + format.format(receivedDate) : "Pending"); // TODO language file + date format
		receivedLabel.getStyleClass().add("text-sm");
		receivedLabel.setTextAlignment(TextAlignment.RIGHT);
		datesContainer.setRight(receivedLabel);

		textContainer.getChildren().addAll(fileNameLabel, fromLabel, datesContainer);

		container.getChildren().addAll(icon, textContainer);
		HBox.setHgrow(textContainer, Priority.ALWAYS);
		return container;
	}
}
