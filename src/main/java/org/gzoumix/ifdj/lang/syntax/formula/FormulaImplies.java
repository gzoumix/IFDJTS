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

import org.gzoumix.ifdj.lang.syntax.ASTNodeCommonFunctionalities;
import org.gzoumix.ifdj.lang.syntax.IASTNode;
import org.gzoumix.ifdj.lang.syntax.visitor.IVisitor;
import org.gzoumix.ifdj.util.Reference;
import org.gzoumix.util.syntax.Position;

import java.util.Collection;


public class FormulaImplies extends ASTNodeCommonFunctionalities<IASTNode> implements IFormula {
  private IFormula left;
  private IFormula right;

  public FormulaImplies(Position pos, IFormula left, IFormula right) {
    super(pos);
    this.left = left;
    this.right = right;
  }

  public FormulaImplies(IFormula left, IFormula right) {
    this(Reference.DUMMY_POSITION, left, right);
  }

    public IFormula getLeft() { return this.left; }
  public IFormula getRight() { return this.right; }

  @Override
  public boolean eval(Collection<FormulaPredicate> truePredicates) {
    if(this.getLeft().eval(truePredicates)) {
      return this.getRight().eval(truePredicates);
    } else { return true; }
  }

  @Override
  public IFormula simplify() {
    IFormula sleft = this.getLeft().simplify();
    if(sleft.equals(Reference.FORMULA_FALSE)) { return Reference.FORMULA_TRUE; }

    IFormula sright = this.getRight().simplify();
    if (sright.equals(Reference.FORMULA_TRUE)) { return Reference.FORMULA_TRUE; }

    if(sleft.equals(Reference.FORMULA_TRUE)) { return sright; }

    return new FormulaImplies(sleft, sright);
  }




  @Override
  public void accept(IVisitor visitor) { visitor.visit(this); }

  @Override
  public String toString() {
    return "(" + this.getLeft().toString() + " => " + this.getRight().toString() + ")";
  }
}
