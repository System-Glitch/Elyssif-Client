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
