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
package org.prolobjectlink.prolog.jpl;

import static org.prolobjectlink.prolog.PrologLogger.FILE_NOT_FOUND;
import static org.prolobjectlink.prolog.PrologLogger.IO;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

import jpl.Term;
import jpl.Util;

/**
 * 
 * @author Jose Zalacain
 * @since 1.0
 */
public final class JplParser {

	public Term parseTerm(String term) {
		return Util.textToTerm(term);
	}

	public Term[] parseTerms(Term term) {
		return parseTerms("" + term + "");
	}

	public Term[] parseTerms(String stringTerms) {
		Term[] a = new Term[0];
		Term ptr = Util.textToTerm(stringTerms);
		List<Term> terms = new ArrayList<Term>();
		while (ptr.isCompound() && ptr.hasFunctor(",", 2)) {
			terms.add(ptr.arg(1));
			ptr = ptr.arg(2);
		}
		terms.add(ptr);
		return terms.toArray(a);
	}

	public JplProgram parseProgram(String file) {
		return parseProgram(new File(file));
	}

	public JplProgram parseProgram(File in) {

		FileReader reader = null;
		BufferedReader buffer = null;
		JplProgram program = new JplProgram();

		try {
			reader = new FileReader(in);
			buffer = new BufferedReader(reader);
			String line = buffer.readLine();
			StringBuilder b = new StringBuilder();
			while (line != null) {
				if (!line.isEmpty() && line.lastIndexOf('.') == line.length() - 1) {
					b.append(line.substring(0, line.length() - 1));
					Term clauseTerm = Util.textToTerm("" + b + "");
					program.add(clauseTerm);
					b = new StringBuilder();
				} else {
					b.append(line);
				}
				line = buffer.readLine();
			}
		} catch (FileNotFoundException e) {
			JplProvider.logger.error(getClass(), FILE_NOT_FOUND, e);
		} catch (IOException e) {
			JplProvider.logger.error(getClass(), IO, e);
		} finally {
			if (reader != null) {
				try {
					reader.close();
				} catch (IOException e) {
					JplProvider.logger.error(getClass(), IO, e);
				}
			}
			if (buffer != null) {
				try {
					buffer.close();
				} catch (IOException e) {
					JplProvider.logger.error(getClass(), IO, e);
				}
			}
		}

		return program;
	}

	public JplProgram parseProgram(Reader in) {

		BufferedReader buffer = null;
		JplProgram program = new JplProgram();

		try {
			buffer = new BufferedReader(in);
			String line = buffer.readLine();
			StringBuilder b = new StringBuilder();
			while (line != null) {
				if (!line.isEmpty() && line.lastIndexOf('.') == line.length() - 1) {
					b.append(line.substring(0, line.length() - 1));
					Term clauseTerm = Util.textToTerm("" + b + "");
					program.add(clauseTerm);
					b = new StringBuilder();
				} else {
					b.append(line);
				}
				line = buffer.readLine();
			}
		} catch (IOException e) {
			JplProvider.logger.error(getClass(), IO, e);
		} finally {
			if (buffer != null) {
				try {
					buffer.close();
				} catch (IOException e) {
					JplProvider.logger.error(getClass(), IO, e);
				}
			}
		}

		return program;
	}

}
