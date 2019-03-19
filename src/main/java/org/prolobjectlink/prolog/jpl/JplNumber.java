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

import org.prolobjectlink.prolog.ArityError;
import org.prolobjectlink.prolog.FunctorError;
import org.prolobjectlink.prolog.IndicatorError;
import org.prolobjectlink.prolog.PrologDouble;
import org.prolobjectlink.prolog.PrologFloat;
import org.prolobjectlink.prolog.PrologInteger;
import org.prolobjectlink.prolog.PrologLong;
import org.prolobjectlink.prolog.PrologNumber;
import org.prolobjectlink.prolog.PrologProvider;

import jpl.Term;

/**
 * 
 * @author Jose Zalacain
 * @since 1.0
 */
public abstract class JplNumber extends JplTerm implements PrologNumber {

	protected JplNumber(int type, PrologProvider provider, Term value) {
		super(type, provider, value);
	}

	public final PrologInteger getPrologInteger() {
		return new JplInteger(provider, getIntValue());
	}

	public final PrologFloat getPrologFloat() {
		return new JplFloat(provider, getFloatValue());
	}

	public final PrologDouble getPrologDouble() {
		return new JplDouble(provider, getDoubleValue());
	}

	public final PrologLong getPrologLong() {
		return new JplLong(provider, getLongValue());
	}

	public final int getArity() {
		throw new ArityError(this);
	}

	public final String getFunctor() {
		throw new FunctorError(this);
	}

	public final String getIndicator() {
		throw new IndicatorError(this);
	}

	public final boolean hasIndicator(String functor, int arity) {
		return false;
	}

}
