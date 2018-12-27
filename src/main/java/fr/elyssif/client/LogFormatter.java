package fr.elyssif.client;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Formatter;
import java.util.logging.Level;
import java.util.logging.LogRecord;

/**
 * Custom formatter for Logger.
 * @author Jérémy LAMBERT
 *
 */
public class LogFormatter extends Formatter {

	private DateFormat df;
	
	public LogFormatter() {
		df = new SimpleDateFormat("[HH:mm:ss.SSS]");
	}
	
	public String format(LogRecord record) {
		StringBuilder builder = new StringBuilder(128);

		//Date and level
		builder.append(df.format(new Date(record.getMillis())));
		builder.append("[").append(record.getLevel()).append("] ");

		//If level is not info, add context
		if(!record.getLevel().equals(Level.INFO) && !record.getLevel().equals(Level.CONFIG))
			builder.append("("+ record.getSourceClassName() + "." + record.getSourceMethodName() +") ");

		//Message
		builder.append(formatMessage(record));
		builder.append("\n");

		//Print stacktrace if an error occurred
		if(record.getThrown() != null) {
			StringWriter errors = new StringWriter();
			record.getThrown().printStackTrace(new PrintWriter(errors));
			builder.append(errors.toString());

			//Optional error dialog
		}
		return builder.toString();
	}

}
