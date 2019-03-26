package fr.elyssif.client.gui.view;

import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.scene.Node;
import javafx.util.Duration;

/**
 * Simple pulse animation.
 * @author Jérémy LAMBERT
 *
 */
public final class PulseAnimation {

	private Timeline timeline;

	public PulseAnimation(Node node) {
		this(node, Duration.millis(1000));
	}

	public PulseAnimation(Node node, Duration duration) {
		timeline = new Timeline();

		double currentScaleX = node.getScaleX();
		double currentScaleY = node.getScaleY();

		KeyFrame kf = new KeyFrame(duration.divide(2), 
				new KeyValue(node.scaleXProperty(), currentScaleX * 1.05),
				new KeyValue(node.scaleYProperty(), currentScaleY * 1.05));

		KeyFrame kf2 = new KeyFrame(duration, 
				new KeyValue(node.scaleXProperty(), currentScaleX),
				new KeyValue(node.scaleYProperty(), currentScaleY));

		timeline.getKeyFrames().addAll(kf, kf2);
	}

	public final void setDelay(Duration duration) {
		timeline.setDelay(duration);
	}

	public final void play() {
		timeline.play();
	}


}
