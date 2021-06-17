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

import static jpl.JPL.JFALSE;
import static jpl.JPL.JNULL;
import static jpl.JPL.JTRUE;
import static jpl.JPL.JVOID;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import io.github.prolobjectlink.prolog.AbstractProvider;
import io.github.prolobjectlink.prolog.PrologAtom;
import io.github.prolobjectlink.prolog.PrologConverter;
import io.github.prolobjectlink.prolog.PrologDouble;
import io.github.prolobjectlink.prolog.PrologFloat;
import io.github.prolobjectlink.prolog.PrologInteger;
import io.github.prolobjectlink.prolog.PrologJavaConverter;
import io.github.prolobjectlink.prolog.PrologList;
import io.github.prolobjectlink.prolog.PrologLogger;
import io.github.prolobjectlink.prolog.PrologLong;
import io.github.prolobjectlink.prolog.PrologProvider;
import io.github.prolobjectlink.prolog.PrologStructure;
import io.github.prolobjectlink.prolog.PrologTerm;
import io.github.prolobjectlink.prolog.PrologVariable;
import jpl.Term;
import jpl.Util;

/**
 * @author Jose Zalacain
 * @since 1.0
 */
public abstract class JplProvider extends AbstractProvider implements PrologProvider {

	static final PrologLogger logger = new JplLogger();

	public JplProvider(PrologConverter<Term> adapter) {
		super(adapter);
	}

	public final PrologTerm prologNil() {
		return new JplNil(this);
	}

	public final PrologTerm prologCut() {
		return new JplCut(this);
	}

	public final PrologTerm prologFail() {
		return new JplFail(this);
	}

	public final PrologTerm prologTrue() {
		return new JplTrue(this);
	}

	public final PrologTerm prologFalse() {
		return new JplFalse(this);
	}

	public final PrologTerm prologEmpty() {
		return new JplEmpty(this);
	}

	public final PrologTerm prologInclude(String file) {
		return newStructure("consult", newAtom(file));
	}

	public final PrologTerm parseTerm(String term) {
		return toTerm(Util.textToTerm(term), PrologTerm.class);
	}

	public final PrologTerm[] parseTerms(String stringTerms) {
		PrologTerm[] a = new PrologTerm[0];
		Term ptr = Util.textToTerm(stringTerms);
		List<PrologTerm> terms = new ArrayList<PrologTerm>();
		while (ptr.isCompound() && ptr.hasFunctor(",", 2)) {
			terms.add(toTerm(ptr.arg(1), PrologTerm.class));
			ptr = ptr.arg(2);
		}
		terms.add(toTerm(ptr, PrologTerm.class));
		return terms.toArray(a);
	}

	public final PrologAtom newAtom(String functor) {
		return new JplAtom(this, functor);
	}

	public final PrologFloat newFloat(Number value) {
		return new JplFloat(this, value);
	}

	public final PrologDouble newDouble(Number value) {
		return new JplDouble(this, value);
	}

	public final PrologInteger newInteger(Number value) {
		return new JplInteger(this, value);
	}

	public final PrologLong newLong(Number value) {
		return new JplLong(this, value);
	}

	public final PrologVariable newVariable(int position) {
		return new JplVariable(this);
	}

	public final PrologVariable newVariable(String name, int position) {
		return new JplVariable(this, name);
	}

	public final PrologList newList() {
		return new JplList(this);
	}

	public final PrologList newList(PrologTerm[] arguments) {
		return new JplList(this, arguments);
	}

	public final PrologList newList(PrologTerm head, PrologTerm tail) {
		return new JplList(this, head, tail);
	}

	public final PrologList newList(PrologTerm[] arguments, PrologTerm tail) {
		return new JplList(this, arguments, tail);
	}

	public final PrologStructure newStructure(String functor, PrologTerm... arguments) {
		return new JplStructure(this, functor, arguments);
	}

	public final PrologTerm newStructure(PrologTerm left, String operator, PrologTerm right) {
		return new JplStructure(this, left, operator, right);
	}

	public final PrologTerm newEntry(PrologTerm key, PrologTerm value) {
		return new JplEntry(this, key, value);
	}

	public final PrologTerm newEntry(Object key, Object value) {
		PrologJavaConverter transformer = getJavaConverter();
		PrologTerm keyTerm = transformer.toTerm(key);
		PrologTerm valueTerm = transformer.toTerm(value);
		return new JplEntry(this, keyTerm, valueTerm);
	}

	public final PrologTerm newMap(Map<PrologTerm, PrologTerm> map) {
		return new JplMap(this, map);
	}

	public final PrologTerm newMap(int initialCapacity) {
		return new JplMap(this, initialCapacity);
	}

	public final PrologTerm newMap() {
		return new JplMap(this);
	}

	public final PrologTerm newReference(Object reference) {
		return new JplReference(this, reference);
	}

	public final PrologTerm falseReference() {
		return new JplReference(this, JFALSE);
	}

	public final PrologTerm trueReference() {
		return new JplReference(this, JTRUE);
	}

	public final PrologTerm nullReference() {
		return new JplReference(this, JNULL);
	}

	public final PrologTerm voidReference() {
		return new JplReference(this, JVOID);
	}

	public final PrologLogger getLogger() {
		return logger;
	}

}
