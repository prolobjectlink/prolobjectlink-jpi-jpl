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

import org.prolobjectlink.prolog.AbstractClause;
import org.prolobjectlink.prolog.PrologClause;
import org.prolobjectlink.prolog.PrologProvider;
import org.prolobjectlink.prolog.PrologTerm;

public final class JplClause extends AbstractClause implements PrologClause {

	protected JplClause(PrologProvider provider, PrologTerm head) {
		super(provider, head, false, false, false);
	}

	protected JplClause(PrologProvider provider, PrologTerm head, PrologTerm body) {
		super(provider, head, body, false, false, false);
	}

	protected JplClause(PrologProvider provider, PrologTerm head, boolean dynamic, boolean multifile,
			boolean discontiguous) {
		super(provider, head, dynamic, multifile, discontiguous);
	}

	protected JplClause(PrologProvider provider, PrologTerm head, PrologTerm body, boolean dynamic, boolean multifile,
			boolean discontiguous) {
		super(provider, head, body, dynamic, multifile, discontiguous);
	}
}
