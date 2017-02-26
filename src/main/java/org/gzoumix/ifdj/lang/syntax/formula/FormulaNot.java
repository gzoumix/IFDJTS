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


public class FormulaNot extends ASTNodeCommonFunctionalities<IASTNode> implements IFormula {
  private IFormula formula;

  public FormulaNot(Position pos, IFormula f) {
    super(pos);
    this.formula = f;
  }

  public FormulaNot(IFormula f) {
    this(Reference.DUMMY_POSITION, f);
  }

  public IFormula getFormula() { return this.formula; }

  @Override
  public boolean eval(Collection<FormulaPredicate> truePredicates) {
    return !this.formula.eval(truePredicates);
  }

  @Override
  public IFormula simplify() {
    IFormula sformula = this.formula.simplify();
    if(sformula.equals(Reference.FORMULA_FALSE)) { return Reference.FORMULA_TRUE; }
    if(sformula.equals(Reference.FORMULA_TRUE)) { return Reference.FORMULA_FALSE; }

    return new FormulaNot(sformula);
  }

  @Override
  public void accept(IVisitor visitor) { visitor.visit(this); }

  @Override
  public String toString() { return "!" + this.getFormula().toString(); }
}
