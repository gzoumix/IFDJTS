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

import org.gzoumix.ifdj.lang.syntax.ASTNodeCommonFunctionalities;
import org.gzoumix.ifdj.lang.syntax.IASTNode;
import org.gzoumix.ifdj.lang.syntax.visitor.IVisitor;
import org.gzoumix.ifdj.util.Reference;
import org.gzoumix.util.syntax.Position;

import java.util.Collection;

/******************************************************************************/

public class FormulaPredicate<I> extends ASTNodeCommonFunctionalities<IASTNode> implements IFormulaElement {
  private I id;

  public FormulaPredicate(Position pos, I id) {
    super(pos);
    this.id = id;
  }

  public FormulaPredicate(I id) {
    this(Reference.DUMMY_POSITION, id);
  }

  @Override
  public boolean eval(Collection<FormulaPredicate> truePredicates) {
    return truePredicates.contains(this);
  }

  @Override
  public IFormula simplify() { return this; }



  @Override
  public boolean equals(Object o) {
    if(o instanceof FormulaPredicate) {
      FormulaPredicate p = (FormulaPredicate) o;
      boolean res = this.id == p.id;
      res = res || ((this.id != null) && (p.id != null) && (this.id.equals(p.id)));
      return res;
    } else { return false; }
  }

  @Override
  public int hashCode() { return (this.id != null ) ? this.id.hashCode() : 0; }

  @Override
  public void accept(IVisitor visitor) { visitor.visit(this); }

  @Override
  public String toString() { return this.id.toString(); }
}
