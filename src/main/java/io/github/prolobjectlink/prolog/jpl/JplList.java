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

import static io.github.prolobjectlink.prolog.PrologTermType.LIST_TYPE;

import java.util.Iterator;
import java.util.NoSuchElementException;

import io.github.prolobjectlink.prolog.AbstractIterator;
import io.github.prolobjectlink.prolog.PrologList;
import io.github.prolobjectlink.prolog.PrologProvider;
import io.github.prolobjectlink.prolog.PrologTerm;
import jpl.Atom;
import jpl.Compound;
import jpl.Term;

/**
 * 
 * @author Jose Zalacain
 * @since 1.0
 */
public class JplList extends JplTerm implements PrologList {

	public static final Term EMPTY = new Atom("[]");

	protected JplList(PrologProvider provider) {
		super(LIST_TYPE, provider, EMPTY);
	}

	protected JplList(PrologProvider provider, Term[] arguments) {
		super(LIST_TYPE, provider);
		value = EMPTY;
		for (int i = arguments.length - 1; i >= 0; --i) {
			value = new Compound(".", new Term[] { arguments[i], value });
		}
	}

	protected JplList(PrologProvider provider, PrologTerm[] arguments) {
		super(LIST_TYPE, provider);
		value = EMPTY;
		for (int i = arguments.length - 1; i >= 0; --i) {
			value = new Compound(".", new Term[] { ((JplTerm) arguments[i]).value, value });
		}
	}

	protected JplList(PrologProvider provider, PrologTerm head, PrologTerm tail) {
		super(LIST_TYPE, provider);
		Term h = ((JplTerm) head).value;
		Term t = ((JplTerm) tail).value;
		value = new Compound(".", new Term[] { h, t });
	}

	protected JplList(PrologProvider provider, PrologTerm[] arguments, PrologTerm tail) {
		super(LIST_TYPE, provider);
		value = fromTerm(tail, Term.class);
		for (int i = arguments.length - 1; i >= 0; --i) {
			Term[] args = { fromTerm(arguments[i], Term.class), value };
			value = new Compound(".", args);
		}
	}

	public int size() {
		return ((Compound) value).listLength();
	}

	public void clear() {
		value = EMPTY;
	}

	public boolean isEmpty() {
		return value.equals(EMPTY);
	}

	public Iterator<PrologTerm> iterator() {
		return new SwiPrologListIter(value);
	}

	public PrologTerm getHead() {
		Compound list = (Compound) value;
		return provider.toTerm(list.arg(1), PrologTerm.class);
	}

	public PrologTerm getTail() {
		Compound list = (Compound) value;
		return provider.toTerm(list.arg(2), PrologTerm.class);
	}

	public int getArity() {
		return value.arity();
	}

	public String getFunctor() {
		return ".";
	}

	public String getIndicator() {
		return getFunctor() + "/" + getArity();
	}

	public boolean hasIndicator(String functor, int arity) {
		return getFunctor().equals(functor) && getArity() == arity;
	}

	public PrologTerm[] getArguments() {
		return toTermArray(value.toTermArray(), PrologTerm[].class);
	}

	public String toString() {
		StringBuilder string = new StringBuilder("[");
		Iterator<PrologTerm> i = iterator();
		if (i.hasNext()) {
			string.append(i.next());
		}
		while (i.hasNext()) {
			string.append(", ");
			string.append(i.next());
		}
		return string.append("]").toString();
	}

	private class SwiPrologListIter extends AbstractIterator<PrologTerm> implements Iterator<PrologTerm> {

		private Term ptr;
		private int index;
		private int length;

		private SwiPrologListIter(Term l) {
			ptr = l;
			if (l.hasFunctor(".", 2)) {
				length = l.listLength();
			}
		}

		public boolean hasNext() {
			return index < length;
		}

		public PrologTerm next() {

			if (!hasNext()) {
				throw new NoSuchElementException();
			}

			PrologTerm term = toTerm(ptr.arg(1), PrologTerm.class);
			ptr = ptr.arg(2);
			index++;
			return term;
		}

	}

}
