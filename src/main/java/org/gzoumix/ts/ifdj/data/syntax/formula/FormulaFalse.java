package org.gzoumix.ts.ifdj.data.syntax.formula;
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

import org.gzoumix.ts.ifdj.data.syntax.ASTNodeCommonFunctionalities;
import org.gzoumix.ts.ifdj.data.syntax.IASTNode;
import org.gzoumix.ts.ifdj.data.syntax.visitor.IVisitor;
import org.gzoumix.ts.ifdj.util.Reference;
import org.gzoumix.util.syntax.Position;

import java.util.Collection;


public class FormulaFalse extends ASTNodeCommonFunctionalities<IASTNode> implements IFormulaElement {

  public FormulaFalse(Position pos) { super(pos); }
  public FormulaFalse() { this(Reference.DUMMY_POSITION); }

  @Override
  public boolean eval(Collection<FormulaPredicate> truePredicates) {
    return false;
  }

  @Override
  public IFormula simplify() { return this; }



  @Override
  public void accept(IVisitor visitor) { visitor.visit(this); }


  @Override
  public int hashCode() { return 475203496; } // a random number

  @Override
  public boolean equals(Object o) { return (o instanceof FormulaFalse); }

  @Override
  public String toString() { return Reference.FALSE; }
}
