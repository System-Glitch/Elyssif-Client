package fr.elyssif.client.gui.view;

import com.jfoenix.transitions.CachedTransition;

import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.scene.Node;
import javafx.util.Duration;

public class CenterTransition extends CachedTransition {

	CenterTransition(Node contentHolder) {
		super(contentHolder, new Timeline(
				new KeyFrame(Duration.ZERO,
						new KeyValue(contentHolder.scaleXProperty(), 0, Interpolator.EASE_BOTH),
						new KeyValue(contentHolder.scaleYProperty(), 0, Interpolator.EASE_BOTH)
						),
				new KeyFrame(Duration.millis(800),
						new KeyValue(contentHolder.scaleXProperty(), 1, Interpolator.EASE_BOTH),
						new KeyValue(contentHolder.scaleYProperty(), 1, Interpolator.EASE_BOTH)
						))
				);
		// reduce the number to increase the shifting , increase number to reduce shifting
		setCycleDuration(Duration.seconds(0.4));
		setDelay(Duration.seconds(0));
	}
}