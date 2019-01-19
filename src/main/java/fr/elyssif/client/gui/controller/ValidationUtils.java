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
