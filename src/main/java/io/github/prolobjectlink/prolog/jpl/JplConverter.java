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

import static io.github.prolobjectlink.prolog.PrologTermType.ATOM_TYPE;
import static io.github.prolobjectlink.prolog.PrologTermType.CUT_TYPE;
import static io.github.prolobjectlink.prolog.PrologTermType.DOUBLE_TYPE;
import static io.github.prolobjectlink.prolog.PrologTermType.FAIL_TYPE;
import static io.github.prolobjectlink.prolog.PrologTermType.FALSE_TYPE;
import static io.github.prolobjectlink.prolog.PrologTermType.FLOAT_TYPE;
import static io.github.prolobjectlink.prolog.PrologTermType.INTEGER_TYPE;
import static io.github.prolobjectlink.prolog.PrologTermType.LIST_TYPE;
import static io.github.prolobjectlink.prolog.PrologTermType.LONG_TYPE;
import static io.github.prolobjectlink.prolog.PrologTermType.NIL_TYPE;
import static io.github.prolobjectlink.prolog.PrologTermType.OBJECT_TYPE;
import static io.github.prolobjectlink.prolog.PrologTermType.STRUCTURE_TYPE;
import static io.github.prolobjectlink.prolog.PrologTermType.TRUE_TYPE;
import static io.github.prolobjectlink.prolog.PrologTermType.VARIABLE_TYPE;

import java.util.ArrayList;
import java.util.List;

import io.github.prolobjectlink.prolog.AbstractConverter;
import io.github.prolobjectlink.prolog.PrologAtom;
import io.github.prolobjectlink.prolog.PrologConverter;
import io.github.prolobjectlink.prolog.PrologDouble;
import io.github.prolobjectlink.prolog.PrologFloat;
import io.github.prolobjectlink.prolog.PrologInteger;
import io.github.prolobjectlink.prolog.PrologLong;
import io.github.prolobjectlink.prolog.PrologStructure;
import io.github.prolobjectlink.prolog.PrologTerm;
import io.github.prolobjectlink.prolog.PrologVariable;
import io.github.prolobjectlink.prolog.UnknownTermError;
import jpl.Atom;
import jpl.Compound;
import jpl.Float;
import jpl.Integer;
import jpl.JPL;
import jpl.JPLException;
import jpl.Query;
import jpl.Term;
import jpl.Variable;

/**
 * @author Jose Zalacain
 * @since 1.0
 */
public abstract class JplConverter extends AbstractConverter<Term> implements PrologConverter<Term> {

	public final PrologTerm toTerm(Term prologTerm) {
		if (prologTerm.isAtom()) {
			String functor = prologTerm.name();
			if (functor.equals("nil")) {
				return new JplNil(provider);
			} else if (functor.equals("!")) {
				return new JplCut(createProvider());
			} else if (functor.equals("fail")) {
				return new JplFail(provider);
			} else if (functor.equals("true")) {
				return new JplTrue(provider);
			} else if (functor.equals("false")) {
				return new JplFalse(provider);
			} else if (functor.equals("[]")) {
				return new JplEmpty(provider);
			}
			return new JplAtom(provider, functor);
		} else if (prologTerm.equals(JplList.EMPTY)) {
			return new JplEmpty(provider);
		} else if (prologTerm.isFloat()) {
			return new JplFloat(provider, ((Float) prologTerm).floatValue());
		} else if (prologTerm.isInteger()) {
			try {
				return new JplInteger(provider, ((Integer) prologTerm).intValue());
			} catch (JPLException e) {
				return new JplLong(provider, ((Integer) prologTerm).longValue());
			}
		} else if (prologTerm.isVariable()) {
			String name = ((Variable) prologTerm).name();
			PrologVariable variable = sharedVariables.get(name);
			if (variable == null) {
				variable = new JplVariable(provider, name);
				sharedVariables.put(variable.getName(), variable);
			}
			return variable;
		} else if (prologTerm.hasFunctor(".", 2)) {
			Term[] a = new Term[0];
			List<Term> l = new ArrayList<Term>();
			Term ptr = prologTerm;
			while (!ptr.isVariable() && ptr.hasFunctor(".", 2)) {
				l.add(ptr.arg(1));
				ptr = ptr.arg(2);
			}
			return new JplList(provider, l.toArray(a));
		} else if (prologTerm.isCompound()) {

			Compound compound = (Compound) prologTerm;
			int arity = compound.arity();
			String functor = compound.name();
			Term[] arguments = new Term[arity];

			// object reference
			if (functor.equals("@") && arity == 1) {
				return new JplReference(provider, compound);
			}

			// expressions
			else if (arity == 2) {
				String key = "LIST";
				String opQuery = "findall(OP,current_op(_,_,OP)," + key + ")";
				Query query = new Query(opQuery);
				if (query.hasSolution()) {

					Term term = (Term) query.oneSolution().get(key);
					Term[] termArray = term.toTermArray();
					for (Term termArray1 : termArray) {
						if (termArray1.name().equals(functor)) {
							Term left = compound.arg(1);
							Term right = compound.arg(2);
							return new JplStructure(provider, left, functor, right);
						}
					}

				}
				query.close();
			}

			for (int i = 0; i < arity; i++) {
				arguments[i] = compound.arg(i + 1);
			}
			return new JplStructure(provider, functor, arguments);

		}

		throw new UnknownTermError(prologTerm);
	}

	public final Term fromTerm(PrologTerm term) {
		switch (term.getType()) {
		case NIL_TYPE:
			return new Atom("nil");
		case CUT_TYPE:
			return new Atom("!");
		case FAIL_TYPE:
			return new Atom("fail");
		case TRUE_TYPE:
			return new Atom("true");
		case FALSE_TYPE:
			return new Atom("false");
		case ATOM_TYPE:
			return new Atom(removeQuoted(((PrologAtom) term).getStringValue()));
		case FLOAT_TYPE:
			return new Float(((PrologFloat) term).getFloatValue());
		case INTEGER_TYPE:
			return new Integer(((PrologInteger) term).getIntegerValue());
		case DOUBLE_TYPE:
			return new Float(((PrologDouble) term).getDoubleValue());
		case LONG_TYPE:
			return new Integer(((PrologLong) term).getLongValue());
		case VARIABLE_TYPE:
			String name = ((PrologVariable) term).getName();
			Term variable = sharedPrologVariables.get(name);
			if (variable == null) {
				variable = new Variable(name);
				sharedPrologVariables.put(name, variable);
			}
			return variable;
		case LIST_TYPE:
			PrologTerm[] array = term.getArguments();
			Term list = JplEmpty.EMPTY;
			for (int i = array.length - 1; i >= 0; --i) {
				list = new Compound(".", new Term[] { fromTerm(array[i]), list });
			}
			return list;
		case STRUCTURE_TYPE:
			String functor = term.getFunctor();
			Term[] arguments = fromTermArray(((PrologStructure) term).getArguments());
			return new Compound(functor, arguments);
		case OBJECT_TYPE:
			return JPL.newJRef(term.getObject());
		default:
			throw new UnknownTermError(term);
		}
	}

	public final Term[] fromTermArray(PrologTerm[] terms) {
		Term[] prologTerms = new Term[terms.length];
		for (int i = 0; i < terms.length; i++) {
			prologTerms[i] = fromTerm(terms[i]);
		}
		return prologTerms;
	}

	public final Term fromTerm(PrologTerm head, PrologTerm[] body) {
		Term clauseHead = fromTerm(head);
		if (body != null && body.length > 0) {
			Term clauseBody = fromTerm(body[body.length - 1]);
			for (int i = body.length - 2; i >= 0; --i) {
				clauseBody = new Compound(",", new Term[] { fromTerm(body[i]), clauseBody });
			}
			return new Compound(":-", new Term[] { clauseHead, clauseBody });
		}
		return clauseHead;
	}

}
