package fr.elyssif.client.gui.view;

import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.scene.Node;
import javafx.util.Duration;

/**
 * Simple "tada" animation.
 * @author Jérémy LAMBERT
 *
 */
public final class TadaAnimation {

	private Timeline timeline;

	public TadaAnimation(Node node) {
		this(node, Duration.millis(1000));
	}

	public TadaAnimation(Node node, Duration duration) {
		timeline = new Timeline();

		double currentScaleX = node.getScaleX();
		double currentScaleY = node.getScaleY();

		Duration d = duration.divide(10);
		
		KeyFrame kf2 = new KeyFrame(d.multiply(2), 
				new KeyValue(node.scaleXProperty(), currentScaleX * 0.9),
				new KeyValue(node.scaleYProperty(), currentScaleY * 0.9),
				new KeyValue(node.rotateProperty(), -3));
		
		KeyFrame kf3 = new KeyFrame(d.multiply(3), 
				new KeyValue(node.scaleXProperty(), currentScaleX * 1.1),
				new KeyValue(node.scaleYProperty(), currentScaleY * 1.1),
				new KeyValue(node.rotateProperty(), 3));
		
		KeyFrame kf4 = new KeyFrame(d.multiply(4), 
				new KeyValue(node.scaleXProperty(), currentScaleX * 1.1),
				new KeyValue(node.scaleYProperty(), currentScaleY * 1.1),
				new KeyValue(node.rotateProperty(), -3));
		
		KeyFrame kf5 = new KeyFrame(d.multiply(5), 
				new KeyValue(node.scaleXProperty(), currentScaleX * 1.1),
				new KeyValue(node.scaleYProperty(), currentScaleY * 1.1),
				new KeyValue(node.rotateProperty(), 3));
		
		KeyFrame kf6 = new KeyFrame(d.multiply(6), 
				new KeyValue(node.scaleXProperty(), currentScaleX * 1.1),
				new KeyValue(node.scaleYProperty(), currentScaleY * 1.1),
				new KeyValue(node.rotateProperty(), -3));
		
		KeyFrame kf7 = new KeyFrame(d.multiply(7), 
				new KeyValue(node.scaleXProperty(), currentScaleX * 1.1),
				new KeyValue(node.scaleYProperty(), currentScaleY * 1.1),
				new KeyValue(node.rotateProperty(), 3));
		
		KeyFrame kf8 = new KeyFrame(d.multiply(8), 
				new KeyValue(node.scaleXProperty(), currentScaleX * 1.1),
				new KeyValue(node.scaleYProperty(), currentScaleY * 1.1),
				new KeyValue(node.rotateProperty(), -3));
		
		KeyFrame kf9 = new KeyFrame(d.multiply(9), 
				new KeyValue(node.scaleXProperty(), currentScaleX * 1.1),
				new KeyValue(node.scaleYProperty(), currentScaleY * 1.1),
				new KeyValue(node.rotateProperty(), 3));
		
		KeyFrame kf10 = new KeyFrame(duration, 
				new KeyValue(node.scaleXProperty(), currentScaleX),
				new KeyValue(node.scaleYProperty(), currentScaleY),
				new KeyValue(node.rotateProperty(), 0));

		timeline.getKeyFrames().addAll(kf2, kf3, kf4, kf5, kf6, kf7, kf8, kf9, kf10);
	}

	public final void setDelay(Duration duration) {
		timeline.setDelay(duration);
	}

	public final void play() {
		timeline.play();
	}


}
