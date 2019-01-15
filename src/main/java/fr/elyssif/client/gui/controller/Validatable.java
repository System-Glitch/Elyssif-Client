package fr.elyssif.client.gui.controller;

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
	 * Validate all inputs in the view.
	 * @return true if all inputs are validated, false otherwise
	 */
	boolean validateAll();
	
	/**
	 * Reset validation for all inputs.
	 */
	void resetValidation();
	
}
