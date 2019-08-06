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

import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
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

	public final void setOnFinished(EventHandler<ActionEvent> handler) {
		timeline.setOnFinished(handler);
	}

	public final void play() {
		timeline.play();
	}


}
