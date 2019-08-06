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
