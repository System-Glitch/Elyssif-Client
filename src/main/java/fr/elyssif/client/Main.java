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

import java.io.PrintStream;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;
import java.util.logging.StreamHandler;

import fr.elyssif.client.gui.ElyssifClient;

/**
 * Main class. Sets up error handling and loads config
 * @author Jérémy LAMBERT
 * @see Config
 *
 */
public final class Main {

	private static void setup() {
		setupLogging();
		setupErrorHandling();
		setupShutdownHook();
		if(!loadConfig()) {
			Logger.getGlobal().info("Application exit");
			System.exit(1);
		}
	}

	private static boolean loadConfig() {
		return Config.getInstance().load();
	}

	private static StreamHandler createStreamHandler(PrintStream stream, LogFormatter formatter) {
		return new StreamHandler(stream, formatter) {

			@Override
			public synchronized void publish(final LogRecord record) {
				super.publish(record);
				flush(); //Flush needed to print the message even if the buffer is not full
			}

		};
	}

	private static void setupShutdownHook() {
		Runtime.getRuntime().addShutdownHook(new Thread(() -> {
			Logger.getGlobal().info("Application exit");
		}));
	}

	private static void setupLogging() {
		Logger logger = Logger.getGlobal();
		LogFormatter formatter = new LogFormatter();

		//Print INFO logs to System.out
		StreamHandler handlerInfo = createStreamHandler(System.out, formatter);
		handlerInfo.setFilter((LogRecord record) -> { return record.getLevel().equals(Level.INFO) || record.getLevel().equals(Level.CONFIG); }); //Only show INFO logs
		handlerInfo.setLevel(Level.ALL);

		logger.setLevel(Level.ALL);
		logger.setUseParentHandlers(false);
		logger.addHandler(handlerInfo);

		//Print other logs to System.err
		StreamHandler handlerError = createStreamHandler(System.err, formatter);
		handlerError.setFilter((LogRecord record) -> { return !record.getLevel().equals(Level.INFO) && !record.getLevel().equals(Level.CONFIG); });

		logger.addHandler(handlerError);
	}

	private static void setupErrorHandling() {
		Thread.setDefaultUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
			public void uncaughtException(Thread t, Throwable e) {
				Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
			}
		});
	}

	private static boolean checkConfigField(String field) {
		String value = Config.getInstance().get(field);
		if(value == null || value.length() == 0) {
			Logger.getGlobal().log(Level.CONFIG, field + " is required.");
			return false;
		}
		return true;
	}

	private static boolean checkConfig() {
		boolean ok = checkConfigField("Environment");
		ok = checkConfigField("Host") && ok;
		ok = checkConfigField("SocketHost") && ok;
		return ok;
	}

	public static void main(String[] args) throws Exception {
		setup();

		if(checkConfig()) {
			ElyssifClient.run(args);
		}
	}

}
