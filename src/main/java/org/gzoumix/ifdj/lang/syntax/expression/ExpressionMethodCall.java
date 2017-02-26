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

import java.util.List;
import java.util.Vector;


public class ExpressionMethodCall extends ExpressionCommonFunctionalities {

  private IExpression base;
  private String name;
  private List<IExpression> parameters;

  public ExpressionMethodCall(Position pos, String name) {
    super(pos);
    this.name = name;
    this.parameters = new Vector<>();
  }

  public ExpressionMethodCall(Position pos, IExpression base, String name) {
    this(pos, name);
    this.base = base;
  }


  public boolean addParameter(IExpression param) { return this.parameters.add(param); }

  public IExpression getBase() { return base; }
  public String getName() { return name; }
  public List<IExpression> getParameters() { return this.parameters; }


  @Override
  public boolean containsOriginalCall() {
    boolean res = (this.base == null) ? false : this.base.containsOriginalCall();
    for(IExpression param: this.getParameters()) {
      if(res) { return true; }
      res = res || param.containsOriginalCall();
    }
    return res;
  }

  @Override
  public void accept(IVisitor visitor) { visitor.visit(this); }
}
