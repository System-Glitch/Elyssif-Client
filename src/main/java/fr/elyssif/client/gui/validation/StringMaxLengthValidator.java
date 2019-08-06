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
 * Defines a rule for input maximum length.
 *
 * @author Jérémy LAMBERT
 */
@DefaultProperty(value = "icon")
public class StringMaxLengthValidator extends ValidatorBase {

	private int maxLength;

	/**
	 * Create a new instance of the validator.
	 * @param message the message shown when field is invalid
	 * @param maxLength the max value length. If the input has a value length
	 * superior to <code>maxLength</code>, the input will be flagged as invalid
	 * @throws IllegalArgumentException thrown if <code>maxLength</code> is not positive
	 */
	public StringMaxLengthValidator(String message, int maxLength) {
		super(message);
		if(maxLength < 0) throw new IllegalArgumentException("Max length must be positive, " + maxLength + " given.");
		this.maxLength = maxLength;
	}

	protected void eval() {
		TextInputControl textField = (TextInputControl) srcControl.get();
		if (textField.getText() != null && textField.getText().length() > maxLength) {
			hasErrors.set(true);
		} else {
			hasErrors.set(false);
		}
	}

	/**
	 * Get the maximum valid length.<br>
	 * If the input has a value length superior to <code>maxLength</code>,
	 * the input will be flagged as invalid.
	 * @return the max value length.
	 */
	public final int getMaxLength() {
		return maxLength;
	}

	/**
	 * Set the maximum valid length.<br>
	 * If the input has a value length superior to <code>maxLength</code>,
	 * the input will be flagged as invalid.
	 * @param maxLength the max value length
	 */
	public final void setMaxLength(int maxLength) {
		this.maxLength = maxLength;
	}

}
