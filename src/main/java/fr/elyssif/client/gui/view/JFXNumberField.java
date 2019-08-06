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

import com.jfoenix.controls.JFXTextField;

import javafx.scene.control.TextFormatter;
import javafx.util.converter.NumberStringConverter;

/**
 * Text field extension, only allowing numbers.
 * @author Jérémy LAMBERT
 *
 */
public class JFXNumberField extends JFXTextField {

	private NumberStringConverter converter;

	public JFXNumberField() {
		super();
		forceNumbers();
	}

	public JFXNumberField(String text) {
		super(text);
		forceNumbers();
	}

	private void forceNumbers() {
		converter = new NumberStringConverter("###.########");
		setTextFormatter(new TextFormatter<>(converter));
	}

	public double getValue() {
		Object value = getTextFormatter().getValue();
		return ((Number)value).doubleValue();
	}

	public NumberStringConverter getConverter() {
		return converter;
	}

}
