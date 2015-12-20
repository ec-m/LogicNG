///////////////////////////////////////////////////////////////////////////
//                   __                _      _   ________               //
//                  / /   ____  ____ _(_)____/ | / / ____/               //
//                 / /   / __ \/ __ `/ / ___/  |/ / / __                 //
//                / /___/ /_/ / /_/ / / /__/ /|  / /_/ /                 //
//               /_____/\____/\__, /_/\___/_/ |_/\____/                  //
//                           /____/                                      //
//                                                                       //
//               The Next Generation Logic Library                       //
//                                                                       //
///////////////////////////////////////////////////////////////////////////
//                                                                       //
//  Copyright 2015 Christoph Zengler                                     //
//                                                                       //
//  Licensed under the Apache License, Version 2.0 (the "License");      //
//  you may not use this file except in compliance with the License.     //
//  You may obtain a copy of the License at                              //
//                                                                       //
//  http://www.apache.org/licenses/LICENSE-2.0                           //
//                                                                       //
//  Unless required by applicable law or agreed to in writing, software  //
//  distributed under the License is distributed on an "AS IS" BASIS,    //
//  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or      //
//  implied.  See the License for the specific language governing        //
//  permissions and limitations under the License.                       //
//                                                                       //
///////////////////////////////////////////////////////////////////////////

package org.logicng.formulas;

import org.logicng.datastructures.Assignment;
import org.logicng.datastructures.Substitution;

import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.SortedMap;
import java.util.SortedSet;

import static org.logicng.formulas.cache.TransformationCacheEntry.NNF;

/**
 * Boolean negation.
 * @author Christoph Zengler
 * @version 1.0
 * @since 1.0
 */
public final class Not extends Formula {

  private final Formula operand;
  private volatile int hashCode;

  /**
   * Constructor.
   * @param operand the operand of the negation
   * @param f       the factory which created this instance
   */
  Not(final Formula operand, final FormulaFactory f) {
    super(FType.NOT, f);
    this.operand = operand;
    this.hashCode = 0;
  }

  /**
   * Returns the operand of this negation.
   * @return the operand of this negation
   */
  public Formula operand() {
    return this.operand;
  }

  @Override
  public int numberOfAtoms() {
    return this.operand.numberOfAtoms();
  }

  @Override
  public int numberOfNodes() {
    return 1 + this.operand.numberOfNodes();
  }

  @Override
  public int numberOfOperands() {
    return 1;
  }

  @Override
  protected void varProfileRec(SortedMap<Literal, Integer> map) {
    this.operand.varProfileRec(map);
  }

  @Override
  protected void litProfileRec(SortedMap<Literal, Integer> map) {
    this.operand.litProfileRec(map);
  }

  @Override
  public LinkedHashSet<Formula> subformulas() {
    final LinkedHashSet<Formula> set = new LinkedHashSet<>();
    set.addAll(this.operand.subformulas());
    set.add(this);
    return set;
  }

  @Override
  public SortedSet<Literal> variables() {
    if (this.variables == null)
      this.variables = this.operand.variables();
    return this.variables;
  }

  @Override
  public SortedSet<Literal> literals() {
    return this.operand.literals();
  }

  @Override
  public boolean contains(Literal literal) {
    return this.operand.contains(literal);
  }

  @Override
  public boolean evaluate(final Assignment assignment) {
    return !this.operand.evaluate(assignment);
  }

  @Override
  public Formula restrict(final Assignment assignment) {
    return f.not(this.operand.restrict(assignment));
  }

  @Override
  public boolean containsSubformula(final Formula formula) {
    return this == formula || this.equals(formula) || this.operand.containsSubformula(formula);
  }

  @Override
  public Formula substitute(final Substitution substitution) {
    return f.not(this.operand.substitute(substitution));
  }

  @Override
  public Formula negate() {
    return this.operand;
  }

  @Override
  public Formula nnf() {
    Formula nnf = this.transformationCache.get(NNF);
    if (nnf == null) {
      switch (this.operand.type) {
        case AND:
        case OR:
          final LinkedHashSet<Formula> nops = new LinkedHashSet<>();
          for (final Formula op : this.operand)
            nops.add(op.negate().nnf());
          nnf = f.naryOperator(this.operand.type == FType.AND ? FType.OR : FType.AND, nops);
          break;
        case IMPL:
          nnf = f.and(((BinaryOperator) this.operand).left, ((BinaryOperator) this.operand).right.negate()).nnf();
          break;
        case EQUIV:
          nnf = f.and(f.or(((BinaryOperator) this.operand).left.negate().nnf(), ((BinaryOperator) this.operand).right.negate().nnf()),
                  f.or(((BinaryOperator) this.operand).left.nnf(), ((BinaryOperator) this.operand).right.nnf()));
          break;
        default:
          nnf = this;
      }
      this.transformationCache.put(NNF, nnf);
    }
    return nnf;
  }

  @Override
  public void generateDotString(StringBuilder sb, Map<Formula, Integer> ids) {
    if (!ids.containsKey(this.operand))
      this.operand.generateDotString(sb, ids);
    final int id = ids.size();
    ids.put(this, id);
    sb.append("  id").append(id).append(" [label=\"¬\"];\n");
    sb.append("  id").append(id).append(" -> id").append(ids.get(this.operand)).append(";\n");
  }

  @Override
  public int hashCode() {
    final int result = this.hashCode;
    if (result == 0)
      this.hashCode = this.operand.hashCode() << 1;
    return this.hashCode;
  }

  @Override
  public boolean equals(final Object other) {
    if (other == this)
      return true;
    if (other instanceof Formula && this.f == ((Formula) other).f)
      return false; // the same formula factory would have produced a == object
    if (other instanceof Not) {
      Not otherNot = (Not) other;
      return this.operand.equals(otherNot.operand);
    }
    return false;
  }

  @Override
  public Iterator<Formula> iterator() {
    final Formula instance = this.operand;
    return new Iterator<Formula>() {
      private boolean iterated;

      @Override
      public boolean hasNext() {
        return !iterated;
      }

      @Override
      public Formula next() {
        if (!iterated) {
          iterated = true;
          return instance;
        }
        throw new NoSuchElementException();
      }

      @Override
      public void remove() {
        throw new UnsupportedOperationException();
      }
    };
  }
}