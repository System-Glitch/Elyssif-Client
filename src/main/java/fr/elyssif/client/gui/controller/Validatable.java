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
