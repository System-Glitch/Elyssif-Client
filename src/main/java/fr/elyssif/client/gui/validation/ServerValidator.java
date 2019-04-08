package fr.elyssif.client.gui.validation;

import java.util.ArrayList;

import com.jfoenix.validation.base.ValidatorBase;

import javafx.beans.DefaultProperty;

/**
 * Validator for validation coming from the server
 * @author Jérémy LAMBERT
 *
 */
@DefaultProperty(value = "icon")
public class ServerValidator extends ValidatorBase {

	private ArrayList<String> messages;

	public final void setMessages(ArrayList<String> messages) {
		this.messages = messages;
	}

	protected void eval() {
		if (messages != null && messages.size() > 0) {
			setMessage(buildMessage());
			hasErrors.set(true);
		} else {
			hasErrors.set(false);
		}
	}

	public boolean getHasErrors() {
		boolean errors = hasErrors.get();
		hasErrors.set(false); //Reset after getting status so can resubmit form
		setMessages(null);
		return errors;
	}

	private String buildMessage() {
		return String.join("\n", messages);
	}
}
