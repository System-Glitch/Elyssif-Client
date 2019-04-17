package fr.elyssif.client.gui.view;

import com.jfoenix.controls.JFXListCell;

import javafx.animation.FadeTransition;
import javafx.scene.layout.Background;
import javafx.util.Duration;

/**
 * Animated list cell. Appears with a fade in and scale effect. 
 * @author Jérémy LAMBERT
 *
 */
public class JFXListCellAnimated<T> extends JFXListCell<T> {

	private boolean wasSelected = false;

	/**
     * {@inheritDoc}
     */
	@Override
	public void updateItem(T entry, boolean empty) {
		super.updateItem(entry, empty);

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
	}
	
}
