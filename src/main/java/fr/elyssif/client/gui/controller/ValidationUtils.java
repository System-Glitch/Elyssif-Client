package fr.elyssif.client.gui.controller;

import com.jfoenix.controls.JFXPasswordField;
import com.jfoenix.controls.JFXTextField;

/**
 * Helper class for registering validation-related events.
 * @author Jérémy LAMBERT
 *
 */
public abstract class ValidationUtils {

	public static void setValidationListener(JFXTextField input) {
		input.focusedProperty().addListener((o, oldVal, newVal) -> {
			if (!newVal) {
				input.validate();
			}
		});
	}

	public static void setValidationListener(JFXPasswordField input) {
		input.focusedProperty().addListener((o, oldVal, newVal) -> {
			if (!newVal) {
				input.validate();
			}
		});
	}

}
