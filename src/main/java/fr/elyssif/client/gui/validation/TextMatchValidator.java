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
 package fr.elyssif.client.gui.validation;

import com.jfoenix.validation.base.ValidatorBase;

import javafx.beans.DefaultProperty;
import javafx.scene.control.TextInputControl;

/**
 * Text field validation for matching fields (such as password confirmations)
 * @author Jérémy LAMBERT
 *
 */
@DefaultProperty(value = "icon")
public class TextMatchValidator extends ValidatorBase {

	private TextInputControl field;

	public TextMatchValidator(String message, TextInputControl field) {
		super(message);
		this.field = field;
	}

	protected void eval() {
		TextInputControl textField = (TextInputControl) srcControl.get();
		if (textField.getText() != null && !textField.getText().equals(field.getText())) {
			hasErrors.set(true);
		} else {
			hasErrors.set(false);
		}
	}
}
