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

import static io.github.prolobjectlink.prolog.PrologTermType.INTEGER_TYPE;

import io.github.prolobjectlink.prolog.PrologInteger;
import io.github.prolobjectlink.prolog.PrologProvider;
import io.github.prolobjectlink.prolog.PrologTerm;
import jpl.Integer;
import jpl.Term;

/**
 * 
 * @author Jose Zalacain
 * @since 1.0
 */
class JplInteger extends JplNumber implements PrologInteger {

	JplInteger(PrologProvider provider, Number value) {
		super(INTEGER_TYPE, provider, new Integer(value.intValue()));
	}

	JplInteger(int type, PrologProvider provider, Term value) {
		super(type, provider, value);
	}

	public final long getLongValue() {
		return ((Integer) value).longValue();
	}

	public final double getDoubleValue() {
		return ((Integer) value).doubleValue();
	}

	public final int getIntegerValue() {
		return ((Integer) value).intValue();
	}

	public final float getFloatValue() {
		return ((Integer) value).floatValue();
	}

	public final PrologTerm[] getArguments() {
		return new JplInteger[0];
	}

}
