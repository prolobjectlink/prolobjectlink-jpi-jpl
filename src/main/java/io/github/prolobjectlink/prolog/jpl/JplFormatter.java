/*
 * #%L
 * prolobjectlink-jpi-jpl
 * %%
 * Copyright (C) 2019 Prolobjectlink Project
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

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Formatter;
import java.util.logging.Level;
import java.util.logging.LogRecord;

import io.github.prolobjectlink.prolog.PrologFormatter;

/**
 * 
 * @author Jose Zalacain
 * @since 1.0
 */
class JplFormatter extends Formatter implements PrologFormatter {

	private static final String ERROR = "ERROR";
	private static final String WARN = "WARN";
	private static final String INFO = "INFO";
	private static final String DEBUG = "DEBUG";
	private static final String TRACE = "TRACE";

	@Override
	public String format(LogRecord record) {
		StringBuilder buffer = new StringBuilder();

		// retrieve record data
		String levelString = "";
		Level level = record.getLevel();
		if (level.intValue() == Level.SEVERE.intValue()) {
			levelString = ERROR;
		} else if (level.intValue() == Level.WARNING.intValue()) {
			levelString = WARN;
		} else if (level.intValue() == Level.INFO.intValue()) {
			levelString = INFO;
		} else if (level.intValue() == Level.CONFIG.intValue()) {
			levelString = DEBUG;
		} else if (level.intValue() == Level.FINE.intValue()) {
			levelString = DEBUG;
		} else if (level.intValue() == Level.FINER.intValue()) {
			levelString = DEBUG;
		} else if (level.intValue() == Level.FINEST.intValue()) {
			levelString = TRACE;
		}

		long millisecs = record.getMillis();
		String name = record.getLoggerName();
		String message = record.getMessage();
		Throwable throwable = record.getThrown();
		SimpleDateFormat f = new SimpleDateFormat("MMM-dd-yyyy HH:mm");
		Date resultdate = new Date(millisecs);
		String date = f.format(resultdate);

		// create log line
		buffer.append('[');
		buffer.append(name);
		buffer.append(']');
		buffer.append(' ');
		buffer.append(levelString);
		buffer.append(' ');
		buffer.append(date);
		buffer.append(' ');
		buffer.append(message);
		if (throwable != null) {
			buffer.append('\n');
			buffer.append(throwable);
			buffer.append('\n');
			StackTraceElement[] s = throwable.getStackTrace();
			for (StackTraceElement stackTraceElement : s) {
				buffer.append(stackTraceElement);
				buffer.append('\n');
			}
		}
		buffer.append('\n');
		return "" + buffer + "";
	}

}
