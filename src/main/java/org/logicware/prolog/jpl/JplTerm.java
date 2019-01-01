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

import static org.logicware.prolog.PrologTermType.CUT_TYPE;
import static org.logicware.prolog.PrologTermType.DOUBLE_TYPE;
import static org.logicware.prolog.PrologTermType.FLOAT_TYPE;
import static org.logicware.prolog.PrologTermType.INTEGER_TYPE;
import static org.logicware.prolog.PrologTermType.LONG_TYPE;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.logicware.prolog.AbstractTerm;
import org.logicware.prolog.PrologList;
import org.logicware.prolog.PrologProvider;
import org.logicware.prolog.PrologQuery;
import org.logicware.prolog.PrologTerm;
import org.logicware.prolog.UnknownTermError;

import jpl.Atom;
import jpl.Query;
import jpl.Term;

public abstract class JplTerm extends AbstractTerm implements PrologTerm {

	protected Term value;

	public static final Term JPL_TRUE = new Atom("true");
	protected static final String SIMPLE_ATOM_REGEX = ".|[a-z][A-Za-z0-9_]*";

	protected JplTerm(int type, PrologProvider provider) {
		super(type, provider);
	}

	protected JplTerm(int type, PrologProvider provider, Term value) {
		super(type, provider);
		this.value = value;
	}

	public final boolean isAtom() {
		return value.isAtom();
	}

	public final boolean isNumber() {
		return isFloat() || isDouble() || isInteger() || isLong();
	}

	public final boolean isFloat() {
		return type == FLOAT_TYPE && value.isFloat();
	}

	public final boolean isDouble() {
		return type == DOUBLE_TYPE;
	}

	public final boolean isInteger() {
		return type == INTEGER_TYPE && value.isInteger();
	}

	public final boolean isLong() {
		return type == LONG_TYPE;
	}

	public final boolean isVariable() {
		return value.isVariable();
	}

	public final boolean isList() {
		if (!isVariable()) {
			return value.equals(JplList.EMPTY) || value.hasFunctor(".", 2);
		}
		return false;
	}

	public final boolean isStructure() {
		return isCompound() && !isList();
	}

	public final boolean isNil() {
		if (!isVariable() && !isNumber()) {
			return value.hasFunctor("nil", 0);
		}
		return false;
	}

	public final boolean isEmptyList() {
		return value.equals(JplList.EMPTY);
	}

	public final boolean isEvaluable() {
		if (!isVariable() && !isList() && !isNumber() && getArity() == 2) {
			String key = "LIST";
			String stringQuery = "findall(OP,current_op(_,_,OP)," + key + ")";
			JplEngine engine = provider.newEngine().unwrap(JplEngine.class);
			PrologQuery query = new JplQuery(engine, engine.getCache(), stringQuery);
			Map<String, PrologTerm>[] solution = query.allVariablesSolutions();
			for (Map<String, PrologTerm> map : solution) {
				for (PrologTerm operatorList : map.values()) {
					if (!operatorList.isVariable() && operatorList.isList()) {
						PrologList l = (PrologList) operatorList;
						for (PrologTerm operator : l) {
							if (operator.getFunctor().equals(getFunctor())) {
								return true;
							}
						}
					}
				}
			}
		}
		return false;
	}

	public final boolean isAtomic() {
		return !isCompound() && !isList();
	}

	public final boolean isCompound() {
		return value.isCompound();
	}

	public final PrologTerm getTerm() {
		return this;
	}

	public final boolean unify(PrologTerm o) {
		if (!(o instanceof JplTerm)) {
			throw new UnknownTermError(o);
		}
		return unify(((JplTerm) o).value);
	}

	protected final boolean unify(Term o) {
		String q = "unify_with_occurs_check(" + value + "," + o + ")";
		Query query = new Query(q);
		boolean result = query.hasSolution();
		query.close();
		return result;
	}

	public final Map<String, PrologTerm> match(PrologTerm term) {
		Map<String, PrologTerm> map = new HashMap<String, PrologTerm>();
		String q = "unify_with_occurs_check(" + value + "," + term + ")";
		Query query = new Query(q);
		if (query.hasSolution()) {
			Map<String, Term> m = query.oneSolution();
			for (Entry<String, Term> e : m.entrySet()) {
				PrologTerm v = toTerm(e.getValue(), PrologTerm.class);
				String key = e.getKey();
				map.put(key, v);
			}
			query.close();
		}
		return map;
	}

	public final int compareTo(PrologTerm o) {

		if (!(o instanceof JplTerm)) {
			throw new UnknownTermError(o);
		}

		String key = "Order";
		Term term = ((JplTerm) o).value;
		String arguments = key + "," + value + "," + term;
		Query query = new Query("compare(" + arguments + ")");

		query.open();
		Term order = (Term) query.getSolution().get(key);
		query.close();

		if (order.hasFunctor("<", 0)) {
			return -1;
		} else if (order.hasFunctor(">", 0)) {
			return 1;
		}

		return 0;

	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + type;
		// Term not implement hashCode()
		result = prime * result + ((value == null) ? 0 : value.toString().hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		JplTerm other = (JplTerm) obj;
		if (type != other.type)
			return false;
		if (value == null) {
			if (other.value != null)
				return false;
		} else if (!unify(other.value)) {
			return false;
		}
		return true;
	}

	@Override
	public String toString() {
		if (type == CUT_TYPE) {
			return getFunctor();
		}
		return "" + value + "";
	}

}
