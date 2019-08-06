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
 package fr.elyssif.client;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.testfx.framework.junit5.ApplicationExtension;

import com.jfoenix.controls.JFXTextField;

import fr.elyssif.client.gui.validation.StringMaxLengthValidator;
import fr.elyssif.client.gui.validation.StringMinLengthValidator;
import fr.elyssif.client.gui.validation.TextMatchValidator;

@ExtendWith(ApplicationExtension.class)
class StringValidationTest {

	@Test
	void testMaxLength() {
		final int maxLength = 3;
		final String message = "Max length: " + maxLength + " characters";
		final var field = new JFXTextField("hello world");

		var validator = new StringMaxLengthValidator(message, maxLength);
		field.setValidators(validator);

		field.validate();
		assertTrue(validator.getHasErrors());

		field.setText("he");
		field.validate();
		assertTrue(!validator.getHasErrors());

		validator.setMaxLength(5);
		assertEquals(5, validator.getMaxLength());
	}

	@Test
	void testInvalidLength() {
		Assertions.assertThrows(IllegalArgumentException.class, () -> {
			new StringMaxLengthValidator("", -1);
		});
		Assertions.assertThrows(IllegalArgumentException.class, () -> {
			new StringMinLengthValidator("", -1);
		});
	}

	@Test
	void testMinLength() {
		final int minLength = 8;
		final String message = "Min length: " + minLength + " characters";
		final var field = new JFXTextField("he");

		var validator = new StringMinLengthValidator(message, minLength);
		field.setValidators(validator);

		field.validate();

		assertTrue(validator.getHasErrors());

		field.setText("hello world");
		field.validate();
		assertTrue(!validator.getHasErrors());

		validator.setMinLength(5);
		assertEquals(5, validator.getMinLength());
	}

	@Test
	void testMatch() {
		final var field1 = new JFXTextField("he");
		final var field2 = new JFXTextField("hello world");

		var validator = new TextMatchValidator("The value of field 1 doesn't match the value of field 2", field2);
		field1.setValidators(validator);

		field1.validate();
		assertTrue(validator.getHasErrors());

		field1.setText("hello world");
		field1.validate();
		assertTrue(!validator.getHasErrors());
	}

}
