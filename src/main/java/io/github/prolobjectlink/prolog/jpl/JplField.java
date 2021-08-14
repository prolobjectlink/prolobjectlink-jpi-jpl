/*-
 * #%L
 * prolobjectlink-jpi-jpl
 * %%
 * Copyright (C) 2020 - 2021 Prolobjectlink Project
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

import io.github.prolobjectlink.prolog.PrologField;
import io.github.prolobjectlink.prolog.PrologProvider;
import io.github.prolobjectlink.prolog.PrologTerm;
import io.github.prolobjectlink.prolog.PrologTermType;
import io.github.prolobjectlink.prolog.PrologVariable;
import jpl.Variable;

public class JplField extends JplVariable implements PrologField {

	JplField(PrologProvider provider, String name) {
		super(PrologTermType.FIELD_TYPE, provider, name);
	}

	JplField(PrologProvider provider, PrologTerm name) {
		super(PrologTermType.FIELD_TYPE, provider);
		this.value = new Variable(((PrologVariable) name).getName());
	}

	JplField(PrologProvider provider, int position) {
		super(PrologTermType.FIELD_TYPE, provider, new Variable());
	}

	JplField(PrologProvider provider, String name, int position) {
		super(PrologTermType.FIELD_TYPE, provider, new Variable(name));
	}

	public final PrologTerm getNameTerm() {
		return provider.newVariable(getName(), getPosition());
	}

}
