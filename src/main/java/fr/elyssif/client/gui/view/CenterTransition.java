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