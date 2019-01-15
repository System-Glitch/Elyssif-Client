package fr.elyssif.client.gui.validation;

import com.jfoenix.validation.base.ValidatorBase;

import javafx.beans.DefaultProperty;
import javafx.scene.control.TextInputControl;

/**
 * Text field validation.
 * @author Jérémy LAMBERT
 *
 */
@DefaultProperty(value = "icon")
public class StringMinLengthValidator extends ValidatorBase {

	private int minLength;
	
	public StringMinLengthValidator(String message, int minLength) {
        super(message);
        if(minLength < 0) throw new IllegalArgumentException("Min length must be positive");
        this.minLength = minLength;
	}
	
	protected void eval() {
		TextInputControl textField = (TextInputControl) srcControl.get();
		if (textField.getText() != null && textField.getText().length() < minLength) {
			hasErrors.set(true);
		} else {
			hasErrors.set(false);
		}
	}

	public final int getMinLength() {
		return minLength;
	}

	public final void setMinLength(int minLength) {
		this.minLength = minLength;
	}

}
