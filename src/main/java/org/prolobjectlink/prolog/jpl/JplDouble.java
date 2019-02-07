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

import static org.prolobjectlink.prolog.PrologTermType.DOUBLE_TYPE;

import org.prolobjectlink.prolog.PrologDouble;
import org.prolobjectlink.prolog.PrologProvider;

import jpl.Float;

public final class JplDouble extends JplFloat implements PrologDouble {

	public JplDouble(PrologProvider provider) {
		super(DOUBLE_TYPE, provider, new Float(0));
	}

	public JplDouble(PrologProvider provider, Number value) {
		super(DOUBLE_TYPE, provider, new Float(value.doubleValue()));
	}

}
