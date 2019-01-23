package fr.elyssif.client.gui.view;

import javafx.animation.Interpolator;
import javafx.animation.Transition;
import javafx.geometry.Insets;
import javafx.scene.control.Control;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.paint.Color;
import javafx.util.Duration;

/**
 * Transition animating the background color.
 * @author Jérémy LAMBERT
 *
 */
public class BackgroundTransition extends Transition {

	private static final double DEFAULT_DURATION = 500;
	
	private Control element;
	private Color fromColor;
	private Color toColor;

	public BackgroundTransition(Control element) {
		this(element, Duration.millis(DEFAULT_DURATION));
	}

	public BackgroundTransition(Control element, Duration duration) {
		this(element, duration, Color.WHITE, Color.BLACK);
	}
	
	public BackgroundTransition(Control element, Color fromColor, Color toColor) {
		this(element, Duration.millis(DEFAULT_DURATION), fromColor, toColor);
	}

	public BackgroundTransition(Control element, Duration duration, Color fromColor, Color toColor) {
		setInterpolator(Interpolator.LINEAR);
		setCycleDuration(duration);
		this.element = element;
		this.fromColor = fromColor;
		this.toColor = toColor;
	}

	public final Color getFromColor() {
		return fromColor;
	}

	public final void setFromColor(Color fromColor) {
		this.fromColor = fromColor;
	}

	public final Color getToColor() {
		return toColor;
	}

	public final void setToColor(Color toColor) {
		this.toColor = toColor;
	}

	protected void interpolate(double frac) {
		Color color = fromColor.interpolate(toColor, frac);
		element.setBackground(new Background(new BackgroundFill(color, CornerRadii.EMPTY, Insets.EMPTY)));
	}

}
