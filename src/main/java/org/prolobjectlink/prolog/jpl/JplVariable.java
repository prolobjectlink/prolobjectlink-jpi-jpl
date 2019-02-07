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

import static org.prolobjectlink.prolog.PrologTermType.VARIABLE_TYPE;

import org.prolobjectlink.prolog.ArityError;
import org.prolobjectlink.prolog.FunctorError;
import org.prolobjectlink.prolog.IndicatorError;
import org.prolobjectlink.prolog.PrologProvider;
import org.prolobjectlink.prolog.PrologTerm;
import org.prolobjectlink.prolog.PrologVariable;

import jpl.Variable;

public class JplVariable extends JplTerm implements PrologVariable {

	JplVariable(PrologProvider provider) {
		super(VARIABLE_TYPE, provider, new Variable("_"));
	}

	JplVariable(PrologProvider provider, String name) {
		super(VARIABLE_TYPE, provider, new Variable(name));
	}

	public boolean isAnonymous() {
		return ((Variable) value).name().equals("_");
	}

	public String getName() {
		return ((Variable) value).name();
	}

	public void setName(String name) {
		this.value = new Variable(name);
	}

	public PrologTerm[] getArguments() {
		return new JplVariable[0];
	}

	public int getArity() {
		throw new ArityError(this);
	}

	public String getFunctor() {
		throw new FunctorError(this);
	}

	public String getIndicator() {
		throw new IndicatorError(this);
	}

	public boolean hasIndicator(String functor, int arity) {
		return false;
	}

	public int getPosition() {
		throw new UnsupportedOperationException("getPosition()");
	}

}
