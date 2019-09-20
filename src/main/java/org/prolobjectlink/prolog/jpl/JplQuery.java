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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

import org.prolobjectlink.prolog.AbstractEngine;
import org.prolobjectlink.prolog.AbstractIterator;
import org.prolobjectlink.prolog.AbstractQuery;
import org.prolobjectlink.prolog.PrologError;
import org.prolobjectlink.prolog.PrologLogger;
import org.prolobjectlink.prolog.PrologQuery;
import org.prolobjectlink.prolog.PrologTerm;

import jpl.PrologException;
import jpl.Query;
import jpl.Term;
import jpl.Util;
import jpl.Variable;

/**
 * 
 * @author Jose Zalacain
 * @since 1.0
 */
final class JplQuery extends AbstractQuery implements PrologQuery {

	private String stringQuery;
	private Map<String, PrologTerm>[] solutions;
	private Iterator<Map<String, PrologTerm>> iter;

	private final List<String> variables = new ArrayList<String>();

	private void enumerateVariables(List<String> vector, Term term) {
		if (!(term instanceof Variable)) {
			Term[] terms = term.args();
			for (Term t : terms) {
				enumerateVariables(vector, t);
			}
		} else if (!vector.contains(term.name())) {
			vector.add(term.name());
		}
	}

	JplQuery(AbstractEngine engine, String file, String stringQuery) {
		super(engine);

		if (stringQuery != null && stringQuery.length() > 0) {
			this.stringQuery = stringQuery;

			// saving variable order
			enumerateVariables(variables, Util.textToTerm(stringQuery));

			try {

				Query.hasSolution("consult('" + file + "')");
				Query query = new Query(stringQuery);
				Map<String, Term>[] solve = query.allSolutions();
				solutions = toTermMapArray(solve, PrologTerm.class);
				iter = new JplQueryIter(solutions);

			} catch (PrologException e) {
				getLogger().error(getClass(), PrologLogger.RUNTIME_ERROR, e);
				Term error = e.term();
				Term exception = error.arg(1);
				Term ref = exception.arg(1);
				if (ref.isJRef()) {
					Exception k = (Exception) ref.jrefToObject();
					getLogger().error(getClass(), PrologLogger.RUNTIME_ERROR, k);
				}
			}
		}

	}

	public boolean hasSolution() {
		return iter != null && iter.hasNext();
	}

	public boolean hasMoreSolutions() {
		return iter != null && iter.hasNext();
	}

	public PrologTerm[] oneSolution() {
		int index = 0;
		Map<String, PrologTerm> solution = oneVariablesSolution();
		PrologTerm[] array = new PrologTerm[solution.size()];
		for (Iterator<String> i = variables.iterator(); i.hasNext();) {
			array[index++] = solution.get(i.next());
		}
		return array;
	}

	public Map<String, PrologTerm> oneVariablesSolution() {
		return solutions[0];
	}

	public PrologTerm[] nextSolution() {
		int index = 0;
		Map<String, PrologTerm> solution = nextVariablesSolution();
		PrologTerm[] array = new PrologTerm[solution.size()];
		for (Iterator<String> i = variables.iterator(); i.hasNext();) {
			array[index++] = solution.get(i.next());
		}
		return array;
	}

	public Map<String, PrologTerm> nextVariablesSolution() {
		return iter.next();
	}

	public PrologTerm[][] nSolutions(int n) {
		if (n > 0) {
			// m:solutionSize
			int m = 0;
			int index = 0;
			ArrayList<PrologTerm[]> all = new ArrayList<PrologTerm[]>();
			while (hasNext() && index < n) {
				PrologTerm[] solution = nextSolution();
				m = solution.length > m ? solution.length : m;
				index++;
				all.add(solution);
			}

			PrologTerm[][] allSolutions = new PrologTerm[n][m];
			for (int i = 0; i < n; i++) {
				PrologTerm[] solution = all.get(i);
				for (int j = 0; j < m; j++) {
					allSolutions[i][j] = solution[j];
				}
			}
			return allSolutions;
		}
		throw new PrologError("Impossible find " + n + " solutions");
	}

	public Map<String, PrologTerm>[] nVariablesSolutions(int n) {
		return Arrays.copyOf(solutions, n);
	}

	public PrologTerm[][] allSolutions() {
		// n:solutionCount, m:solutionSize
		int n = 0;
		int m = 0;
		ArrayList<PrologTerm[]> all = new ArrayList<PrologTerm[]>();
		while (hasMoreSolutions()) {
			PrologTerm[] solution = nextSolution();
			m = solution.length > m ? solution.length : m;
			n++;
			all.add(solution);
		}

		PrologTerm[][] allSolutions = new PrologTerm[n][m];
		for (int i = 0; i < n; i++) {
			PrologTerm[] solution = all.get(i);
			for (int j = 0; j < m; j++) {
				allSolutions[i][j] = solution[j];
			}
		}
		return allSolutions;
	}

	public Map<String, PrologTerm>[] allVariablesSolutions() {
		return solutions;
	}

	public void dispose() {
		iter = null;
		variables.clear();
		int l = solutions.length;
		for (int i = 0; i < l; i++) {
			solutions[i].clear();
			solutions[i] = null;
		}
		solutions = null;
	}

	public List<Map<String, PrologTerm>> all() {
		List<Map<String, PrologTerm>> l = new ArrayList<Map<String, PrologTerm>>();
		for (Map<String, PrologTerm> map : solutions) {
			l.add(map);
		}
		return l;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + stringQuery.hashCode();
		result = prime * result + variables.hashCode();
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (!super.equals(obj)) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		JplQuery other = (JplQuery) obj;
		return variables.equals(other.variables);
	}

	@Override
	public String toString() {
		return stringQuery;
	}

	private class JplQueryIter extends AbstractIterator<Map<String, PrologTerm>>
			implements Iterator<Map<String, PrologTerm>> {

		private int nextIndex;
		private final Map<String, PrologTerm>[] maps;

		private JplQueryIter(Map<String, PrologTerm>[] maps) {
			this.maps = maps;
		}

		public boolean hasNext() {
			return nextIndex < maps.length;
		}

		public Map<String, PrologTerm> next() {
			if (!hasNext()) {
				throw new NoSuchElementException();
			}
			return maps[nextIndex++];
		}

	}

}
