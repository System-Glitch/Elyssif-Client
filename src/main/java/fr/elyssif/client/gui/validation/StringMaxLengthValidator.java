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
