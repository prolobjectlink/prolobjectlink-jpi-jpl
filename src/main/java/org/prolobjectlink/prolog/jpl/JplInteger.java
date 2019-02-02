/*
 * #%L
 * prolobjectlink-jpi-jpl
 * %%
 * Copyright (C) 2019 Prolobjectlink Project
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */
package org.prolobjectlink.prolog.jpl;

import static org.prolobjectlink.prolog.PrologTermType.INTEGER_TYPE;

import org.prolobjectlink.prolog.PrologInteger;
import org.prolobjectlink.prolog.PrologProvider;
import org.prolobjectlink.prolog.PrologTerm;

import jpl.Integer;
import jpl.Term;

public class JplInteger extends JplNumber implements PrologInteger {

	public JplInteger(PrologProvider provider) {
		super(INTEGER_TYPE, provider, new Integer(0));
	}

	public JplInteger(PrologProvider provider, Number value) {
		super(INTEGER_TYPE, provider, new Integer(value.intValue()));
	}

	public JplInteger(int type, PrologProvider provider, Term value) {
		super(type, provider, value);
	}

	public final long getLongValue() {
		return ((Integer) value).longValue();
	}

	public final double getDoubleValue() {
		return ((Integer) value).doubleValue();
	}

	public final int getIntValue() {
		return ((Integer) value).intValue();
	}

	public final float getFloatValue() {
		return ((Integer) value).floatValue();
	}

	public final PrologTerm[] getArguments() {
		return new JplInteger[0];
	}

}