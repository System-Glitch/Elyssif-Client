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
 * Text field validation.<br>
 * Defines a rule for input minimum length.
 *
 * @author Jérémy LAMBERT
 */
@DefaultProperty(value = "icon")
public class StringMinLengthValidator extends ValidatorBase {

	private int minLength;

	/**
	 * Create a new instance of the validator.
	 * @param message the message shown when field is invalid
	 * @param minLength the min value length. If the input has a value length
	 * inferior to <code>minLength</code>, the input will be flagged as invalid
	 * @throws IllegalArgumentException thrown if <code>minLength</code> is not positive
	 */
	public StringMinLengthValidator(String message, int minLength) {
		super(message);
		if(minLength < 0) throw new IllegalArgumentException("Min length must be positive, " + minLength + " given.");
		this.minLength = minLength;
	}

	protected void eval() {
		TextInputControl textField = (TextInputControl) srcControl.get();
		if (textField.getText() != null && textField.getText().length() < minLength) {
			hasErrors.set(true);
		} else {
			hasErrors.set(false);
		}
	}

	/**
	 * Get the minimum valid length.<br>
	 * If the input has a value length inferior to <code>minLength</code>,
	 * the input will be flagged as invalid.
	 * @return the min value length.
	 */
	public final int getMinLength() {
		return minLength;
	}

	/**
	 * Set the minimum valid length.<br>
	 * If the input has a value length inferior to <code>minLength</code>,
	 * the input will be flagged as invalid.
	 * @param minLength the min value length
	 */
	public final void setMinLength(int minLength) {
		this.minLength = minLength;
	}

}
