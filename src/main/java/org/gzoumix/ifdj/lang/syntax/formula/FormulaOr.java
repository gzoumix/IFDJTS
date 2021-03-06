package org.gzoumix.ifdj.lang.syntax.formula;
/******************************************************************************/
/* Copyright Michael Lienhardt 2015                                           */
/*                                                                            */
/* This file is part of IFDJTS (a type system for the IFDJ language).         */
/*                                                                            */
/* IFDJTS is free software: you can redistribute it and/or modify             */
/* it under the terms of the GNU General Public License as published by       */
/* the Free Software Foundation, either version 3 of the License, or          */
/* (at your option) any later version.                                        */
/*                                                                            */
/* IFDJTS is distributed in the hope that it will be useful,                  */
/* but WITHOUT ANY WARRANTY; without even the implied warranty of             */
/* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the              */
/* GNU General Public License for more details.                               */
/*                                                                            */
/* You should have received a copy of the GNU General Public License          */
/* along with IFDJTS.  If not, see <http://www.gnu.org/licenses/>.            */
/******************************************************************************/

import org.gzoumix.ifdj.lang.syntax.IASTNode;
import org.gzoumix.ifdj.lang.syntax.visitor.IVisitor;
import org.gzoumix.ifdj.util.Reference;
import org.gzoumix.util.data.ImmutableCollection;
import org.gzoumix.util.syntax.Position;

import java.util.*;


public class FormulaOr extends ImmutableCollection<IFormula> implements IFormula {
  private IASTNode father;
  private Position pos;

  public FormulaOr(Position pos) { this.pos = pos; }
  public FormulaOr() { this(Reference.DUMMY_POSITION); }


  @Override
  public boolean add(IFormula formula) {
    boolean res = this.content.add(formula);
    if(res) { formula.setFather(this); }
    return res;
  }

  public boolean addAll(Collection<? extends IFormula> fs) { return this.content.addAll(fs); }

  @Override
  public boolean eval(Collection<FormulaPredicate> truePredicates) {
    for(IFormula f: this) {
      if(f.eval(truePredicates)) { return true; }
    }
    return false;
  }

  @Override
  public IFormula simplify() {
    Set<IFormula> l = new HashSet<>();
    for(IFormula formula: this) {
      IFormula sformula = formula.simplify();
      if(sformula.equals(Reference.FORMULA_TRUE)) { return Reference.FORMULA_TRUE; }
      else if(!sformula.equals(Reference.FORMULA_FALSE)) { l.add(sformula); }
    }

    if(l.isEmpty()) { return Reference.FORMULA_FALSE; }
    FormulaOr res = new FormulaOr();
    res.content.addAll(l);
    return res;
  }



  @Override
  public IASTNode getFather() { return this.father; }

  @Override
  public void setFather(IASTNode father) { this.father = father; }

  @Override
  public Position getPosition() { return this.pos; }


  @Override
  public void setType(String type) { }

  @Override
  public String getType() { return null; }

  @Override
  public void setDependencyConstraint(IFormula constraint) { }

  @Override
  public IFormula getDependencyConstraint() { return null; }


  @Override
  public void accept(IVisitor visitor) { visitor.visit(this); }


  @Override
  public String toString() {
    if(this.size() == 0) { return Reference.FALSE; }
    else if(this.size() == 1) { return this.iterator().next().toString(); }
    else {
      String res = "(";
      Iterator<IFormula> i = this.iterator();
      while(i.hasNext()) {
        res = res + i.next().toString();
        if(i.hasNext()) { res = res + " || "; }
      }
      return res + ")";
    }
  }
}
