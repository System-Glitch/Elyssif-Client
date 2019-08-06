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
 package fr.elyssif.client.gui.controller;

import com.jfoenix.controls.base.IFXValidatableControl;

import javafx.scene.control.Control;

/**
 * Helper class for registering validation-related events.
 * @author Jérémy LAMBERT
 *
 */
public abstract class ValidationUtils {

	public static void setValidationListener(Control input) {
		input.focusedProperty().addListener((o, oldVal, newVal) -> {
			if (!newVal) {
				((IFXValidatableControl) input).validate();
			}
		});
	}

}
