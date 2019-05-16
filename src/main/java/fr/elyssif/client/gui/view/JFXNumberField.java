package fr.elyssif.client.gui.view;

import com.jfoenix.controls.JFXTextField;

import javafx.scene.control.TextFormatter;
import javafx.util.converter.NumberStringConverter;

/**
 * Text field extension, only allowing numbers.
 * @author Jérémy LAMBERT
 *
 */
public class JFXNumberField extends JFXTextField {

	private NumberStringConverter converter;
	
	/**
	 * {@inheritDoc}
	 */
	public JFXNumberField() {
		super();
		forceNumbers();
	}

	/**
	 * {@inheritDoc}
	 */
	public JFXNumberField(String text) {
		super(text);
		forceNumbers();
	}

	private void forceNumbers() {
		converter = new NumberStringConverter("###.########");
		setTextFormatter(new TextFormatter<>(converter));
	}
	
	public double getValue() {
		return (double) getTextFormatter().getValue();
	}
	
	public NumberStringConverter getConverter() {
		return converter;
	}

}
