package fr.elyssif.client.gui.view;

import javafx.animation.FadeTransition;
import javafx.animation.Interpolator;
import javafx.scene.Node;
import javafx.util.Duration;

/**
 * Utility class for views.
 * Allows to create transitions and more.
 * @author Jérémy LAMBERT
 *
 */
public abstract class ViewUtils {

	public static FadeTransition createFadeTransition(Node node) {
		return createFadeTransition(node, Duration.millis(1000));
	}

	public static FadeTransition createFadeTransition(Node node, Duration duration) {
		FadeTransition ft = new FadeTransition(duration, node);
		ft.setInterpolator(Interpolator.EASE_BOTH);
		return ft;
	}

	public static FadeTransition createFadeInTransition(Node node) {
		return createFadeInTransition(node, Duration.millis(1000));
	}
	
	public static FadeTransition createFadeInTransition(Node node, Duration duration) {
		FadeTransition ft = createFadeTransition(node, duration);
		ft.setFromValue(0);
		ft.setToValue(1.0);
		return ft;
	}
	
	public static FadeTransition createFadeOutTransition(Node node) {
		return createFadeOutTransition(node, Duration.millis(1000));
	}
	
	public static FadeTransition createFadeOutTransition(Node node, Duration duration) {
		FadeTransition ft = createFadeTransition(node, duration);
		ft.setFromValue(1.0);
		ft.setToValue(0);
		return ft;
	}

}
