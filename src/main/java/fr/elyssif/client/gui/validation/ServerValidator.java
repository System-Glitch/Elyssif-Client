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
