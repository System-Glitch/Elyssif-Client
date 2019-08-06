/*
 * Elyssif-Client
 * Copyright (C) 2019 Jérémy LAMBERT (System-Glitch)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.

 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.

 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
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
