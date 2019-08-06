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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;

import com.jfoenix.controls.base.IFXValidatableControl;

import fr.elyssif.client.gui.validation.ServerValidator;

/**
 * Interface for controllers that need to validate inputs (such as forms).
 * @author Jérémy LAMBERT
 *
 */
public interface Validatable {

	/**
	 * Get the server validators used in this controller.
	 * @return serverValidators
	 */
	HashMap<String, ServerValidator> getServerValidators();

	/**
	 * Create and setup validators for all inputs in the view.
	 */
	void setupValidators();

	/**
	 * Create and setup server validators for all inputs in the view.<br>
	 * Map input names to the create ServerValidator.
	 */
	void setupServerValidators();

	/**
	 * Validate all inputs in the view.
	 * @return true if all inputs are validated, false otherwise
	 */
	boolean validateAll();

	/**
	 * Reset validation for all inputs.
	 */
	void resetValidation();
	
	/**
	 * Reset all inputs.
	 */
	void resetForm();

	/**
	 * Create a ServerValidator and register it.<br>
	 * You still have to add it to the input's validators:<br>
	 * <code>input.getValidators().add(createServerValidator("name"));</code>
	 * @param inputName
	 * @return validator
	 */
	default ServerValidator createServerValidator(String inputName) {
		var validator = new ServerValidator();
		getServerValidators().put(inputName, validator);
		return validator;
	}

	/**
	 * Handle validation errors coming from the server.
	 * @param errors
	 */
	default void handleValidationErrors(HashMap<String, ArrayList<String>> errors) {
		for(Entry<String, ArrayList<String>> entry : errors.entrySet()) {
			HashMap<String, ServerValidator> serverValidators = getServerValidators();
			if(serverValidators.containsKey(entry.getKey())) {
				ServerValidator validator = serverValidators.get(entry.getKey());
				validator.setMessages(entry.getValue());
				((IFXValidatableControl) validator.getSrcControl()).validate();
			}
		}
	}

}
