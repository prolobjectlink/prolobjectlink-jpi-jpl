/*-
 * #%L
 * prolobjectlink-jpi-jpl
 * %%
 * Copyright (C) 2012 - 2019 Prolobjectlink Project
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 2.1 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Lesser Public License for more details.
 * 
 * You should have received a copy of the GNU General Lesser Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/lgpl-2.1.html>.
 * #L%
 */
package io.github.prolobjectlink.prolog.jpl;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.FileHandler;
import java.util.logging.Formatter;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;

import io.github.prolobjectlink.prolog.AbstractLogger;
import io.github.prolobjectlink.prolog.PrologLogger;

/**
 * 
 * @author Jose Zalacain
 * @since 1.0
 */
final class JplLogger extends AbstractLogger implements PrologLogger {

	private static final String MESSAGE = "Logger File Handler can't be created";
	private static final Logger LOGGER = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);

	public JplLogger() {
		this(Level.INFO);
	}

	private JplLogger(Level level) {
		LOGGER.setLevel(level);
		Logger rootlogger = LOGGER.getParent();
		SimpleDateFormat f = new SimpleDateFormat("yyyy.MM.dd");
		String date = f.format(new Date());
		Formatter formatter = new JplFormatter();
		for (Handler h : rootlogger.getHandlers()) {
			h.setFormatter(formatter);
			h.setLevel(level);
		}
		FileHandler file = null;
		try {
			file = new FileHandler("%t/prolobjectlink-" + date + ".log", true);
		} catch (SecurityException e) {
			rootlogger.log(Level.SEVERE, MESSAGE, e);
		} catch (IOException e) {
			rootlogger.log(Level.SEVERE, MESSAGE, e);
		}
		assert file != null;
		file.setFormatter(formatter);
		LOGGER.addHandler(file);
	}

	public void log(Object sender, Level level, Object message, Throwable throwable) {
		LOGGER.log(level, sender + "\n" + message, throwable);
	}

}
