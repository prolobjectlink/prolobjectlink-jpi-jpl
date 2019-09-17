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

import static org.prolobjectlink.prolog.PrologTermType.CUT_TYPE;
import static org.prolobjectlink.prolog.PrologTermType.DOUBLE_TYPE;
import static org.prolobjectlink.prolog.PrologTermType.FLOAT_TYPE;
import static org.prolobjectlink.prolog.PrologTermType.INTEGER_TYPE;
import static org.prolobjectlink.prolog.PrologTermType.LONG_TYPE;

import java.util.Map;

import org.prolobjectlink.prolog.AbstractTerm;
import org.prolobjectlink.prolog.PrologList;
import org.prolobjectlink.prolog.PrologProvider;
import org.prolobjectlink.prolog.PrologQuery;
import org.prolobjectlink.prolog.PrologTerm;
import org.prolobjectlink.prolog.UnknownTermError;

import jpl.Atom;
import jpl.Query;
import jpl.Term;

/**
 * 
 * @author Jose Zalacain
 * @since 1.0
 */
abstract class JplTerm extends AbstractTerm implements PrologTerm {

	protected Term value;

	static final Term JPL_TRUE = new Atom("true");

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
			JplEngine engine = (JplEngine) provider.newEngine();
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
		return value.isCompound() && !isAtom();
	}

	public final boolean isTrueType() {
		return value.isJTrue();
	}

	public final boolean isFalseType() {
		return value.isJFalse();
	}

	public final boolean isNullType() {
		return value.isJNull();
	}

	public final boolean isVoidType() {
		return value.isJVoid();
	}

	public final boolean isObjectType() {
		return value.isJObject();
	}

	public final boolean isReference() {
		return value.isJRef();
	}

	public final Object getReference() {
		return value.jrefToObject();
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

	private final boolean unify(Term o) {
		String q = "unify_with_occurs_check(" + value + "," + o + ")";
		Query query = new Query(q);
		boolean result = query.hasSolution();
		query.close();
		return result;
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

	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + type;
		// Term not implement hashCode()
		result = prime * result + ((value == null) ? 0 : value.toString().hashCode());
		return result;
	}

	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		JplTerm other = (JplTerm) obj;
		if (value == null) {
			if (other.value != null)
				return false;
		} else if (!unify(other.value)) {
			return false;
		}
		return true;
	}

	public String toString() {
		if (type == CUT_TYPE) {
			return getFunctor();
		}
		return "" + value + "";
	}

}
