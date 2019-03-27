package fr.elyssif.client.gui.view;

import javafx.animation.Interpolator;
import javafx.animation.ScaleTransition;
import javafx.animation.TranslateTransition;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.util.Duration;

/**
 * Slide down with slight lower scaling transition.
 * @author Jérémy LAMBERT
 *
 */
public final class ImageSlideTransition {

	private Node node;

	private ScaleTransition scale;
	private TranslateTransition translate;

	public ImageSlideTransition(Node node, double containerHeight) {
		this(node, containerHeight, Duration.millis(1000));
	}

	public ImageSlideTransition(Node node, double containerHeight, Duration duration) {
		this.node = node;
		scale = new ScaleTransition(duration, node);
		scale.setFromX(1);
		scale.setFromY(1);
		scale.setToX(0.8);
		scale.setToY(0.8);
		scale.setInterpolator(Interpolator.EASE_BOTH);

		translate = new TranslateTransition(duration, node);
		translate.setFromY(0);
		translate.setToY(containerHeight / 2 - node.getBoundsInParent().getCenterY());
		translate.setInterpolator(Interpolator.EASE_BOTH);
	}

	public final void revert() {
		scale.setRate(-scale.getRate());
		translate.setRate(-translate.getRate());
	}

	public final Node getNode() {
		return node;
	}

	public final void setNode(Node node) {
		this.node = node;
	}

	public final void setDelay(Duration duration) {
		scale.setDelay(duration);
		translate.setDelay(duration);
	}

	public final void setOnFinished(EventHandler<ActionEvent> handler) {
		scale.setOnFinished(handler);
	}

	public final void play() {
		scale.play();
		translate.play();
	}

}
