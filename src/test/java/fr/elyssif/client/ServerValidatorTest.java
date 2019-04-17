package fr.elyssif.client;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.assertj.core.util.Lists;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.testfx.framework.junit5.ApplicationExtension;

import com.jfoenix.controls.JFXTextField;

import fr.elyssif.client.gui.validation.ServerValidator;

@ExtendWith(ApplicationExtension.class)
class ServerValidatorTest {

	@Test
	void testNull() {
		var field = new JFXTextField();
		var validator = new ServerValidator();
		field.setValidators(validator);
		
		field.validate();
		assertTrue(!validator.getHasErrors());
	}
	
	@Test
	void testHasErrors() {
		var field = new JFXTextField();
		var validator = new ServerValidator();
		field.setValidators(validator);
		
		validator.setMessages(Lists.list("Error 1", "Error 2"));
		
		assertTrue(!field.validate());
		assertEquals("Error 1\nError 2", validator.getMessage());
		
		// Errors should be cleared after first validation
		assertTrue(!validator.getHasErrors());
	}

}
