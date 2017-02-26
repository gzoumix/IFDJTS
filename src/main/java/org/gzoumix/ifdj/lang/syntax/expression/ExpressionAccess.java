package org.gzoumix.ifdj.lang.syntax.expression;
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

import org.gzoumix.ifdj.lang.syntax.visitor.IVisitor;
import org.gzoumix.util.syntax.Position;


public class ExpressionAccess extends ExpressionCommonFunctionalities {
  private IExpression base;
  private String name;

  public ExpressionAccess(Position pos, String name) {
    super(pos);
    this.name = name;
  }

  public ExpressionAccess(Position pos, IExpression base, String name) {
    super(pos);
    this.base = base;
    this.name = name;
  }

  public IExpression getBase() { return this.base; }
  public String getName() { return this.name; }

  @Override
  public boolean containsOriginalCall() {
    return (this.base == null) ? false : this.base.containsOriginalCall();
  }

  @Override
  public void accept(IVisitor visitor) { visitor.visit(this); }
}
