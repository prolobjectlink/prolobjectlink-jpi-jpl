/*
 * #%L
 * prolobjectlink-jpi-jpl
 * %%
 * Copyright (C) 2012 - 2017 WorkLogic Project
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
package org.logicware.prolog.jpl;

import static org.logicware.prolog.PrologTermType.ATOM_TYPE;
import static org.logicware.prolog.PrologTermType.CUT_TYPE;
import static org.logicware.prolog.PrologTermType.DOUBLE_TYPE;
import static org.logicware.prolog.PrologTermType.EMPTY_TYPE;
import static org.logicware.prolog.PrologTermType.FAIL_TYPE;
import static org.logicware.prolog.PrologTermType.FALSE_TYPE;
import static org.logicware.prolog.PrologTermType.FLOAT_TYPE;
import static org.logicware.prolog.PrologTermType.INTEGER_TYPE;
import static org.logicware.prolog.PrologTermType.LIST_TYPE;
import static org.logicware.prolog.PrologTermType.LONG_TYPE;
import static org.logicware.prolog.PrologTermType.NIL_TYPE;
import static org.logicware.prolog.PrologTermType.STRUCTURE_TYPE;
import static org.logicware.prolog.PrologTermType.TRUE_TYPE;
import static org.logicware.prolog.PrologTermType.VARIABLE_TYPE;

import java.util.Map;

import org.logicware.prolog.AbstractConverter;
import org.logicware.prolog.PrologAtom;
import org.logicware.prolog.PrologConverter;
import org.logicware.prolog.PrologDouble;
import org.logicware.prolog.PrologEngine;
import org.logicware.prolog.PrologFloat;
import org.logicware.prolog.PrologInteger;
import org.logicware.prolog.PrologList;
import org.logicware.prolog.PrologLong;
import org.logicware.prolog.PrologQuery;
import org.logicware.prolog.PrologStructure;
import org.logicware.prolog.PrologTerm;
import org.logicware.prolog.PrologVariable;
import org.logicware.prolog.UnknownTermError;

import jpl.Atom;
import jpl.Compound;
import jpl.Float;
import jpl.Integer;
import jpl.Term;
import jpl.Variable;

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
			return new JplInteger(provider, ((Integer) prologTerm).intValue());
		} else if (prologTerm.isVariable()) {
			String name = ((Variable) prologTerm).name();
			PrologVariable variable = sharedVariables.get(name);
			if (variable == null) {
				variable = new JplVariable(provider, name);
				sharedVariables.put(variable.getName(), variable);
			}
			return variable;
		} else if (prologTerm.hasFunctor(".", 2)) {
			return new JplList(provider, prologTerm.toTermArray());
		} else if (prologTerm.isCompound()) {

			Compound compound = (Compound) prologTerm;
			int arity = compound.arity();
			String functor = compound.name();
			Term[] arguments = new Term[arity];

			if (arity == 2) {
				String key = "LIST";
				PrologEngine engine = provider.newEngine();
				String stringQuery = "findall(OP,current_op(_,_,OP)," + key + ")";
				PrologQuery query = engine.query(stringQuery);
				Map<String, PrologTerm>[] solution = query.allVariablesSolutions();
				for (Map<String, PrologTerm> map : solution) {
					for (PrologTerm operatorList : map.values()) {
						if (!operatorList.isVariable() && operatorList.isList()) {
							PrologList l = (PrologList) operatorList;
							for (PrologTerm operator : l) {
								if (operator.getFunctor().equals(functor)) {
									Term left = compound.arg(1);
									Term right = compound.arg(2);
									return new JplStructure(provider, left, functor, right);
								}
							}
						}
					}
				}
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
		case EMPTY_TYPE:
			return JplEmpty.EMPTY;
		case ATOM_TYPE:
			return new Atom(removeQuoted(((PrologAtom) term).getStringValue()));
		case FLOAT_TYPE:
			return new Float(((PrologFloat) term).getFloatValue());
		case INTEGER_TYPE:
			return new Integer(((PrologInteger) term).getIntValue());
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
