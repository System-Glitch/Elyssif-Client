package fr.elyssif.client.gui.controller;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Interface for controllers that need to validate inputs (such as forms).
 * @author Jérémy LAMBERT
 *
 */
public interface Validatable {

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
	 * Handle validation errors coming from the server.
	 * @param errors
	 */
	void handleValidationErrors(HashMap<String, ArrayList<String>> errors);

}
