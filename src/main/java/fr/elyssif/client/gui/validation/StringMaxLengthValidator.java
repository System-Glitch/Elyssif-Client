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
public class StringMaxLengthValidator extends ValidatorBase {

	private int maxLength;
	
	public StringMaxLengthValidator(String message, int maxLength) {
        super(message);
        if(maxLength < 0) throw new IllegalArgumentException("Max length must be positive");
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

	public final int getMaxLength() {
		return maxLength;
	}

	public final void setMaxLength(int maxLength) {
		this.maxLength = maxLength;
	}

}
