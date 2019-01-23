package fr.elyssif.client.gui.validation;

import com.jfoenix.validation.base.ValidatorBase;

import javafx.beans.DefaultProperty;
import javafx.scene.control.TextInputControl;

/**
 * Text field validation for matching fields (such as password confirmations)
 * @author Jérémy LAMBERT
 *
 */
@DefaultProperty(value = "icon")
public class TextMatchValidator extends ValidatorBase {

	private TextInputControl field;
	
	public TextMatchValidator(String message, TextInputControl field) {
        super(message);
        this.field = field;
	}
	
	protected void eval() {
		TextInputControl textField = (TextInputControl) srcControl.get();
		if (textField.getText() != null && !textField.getText().equals(field.getText())) {
			hasErrors.set(true);
		} else {
			hasErrors.set(false);
		}
	}

	public final TextInputControl getField() {
		return field;
	}

	public final void setField(TextInputControl field) {
		this.field = field;
	}
}
