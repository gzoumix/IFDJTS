package org.gzoumix.ts.ifdj.data.syntax.fm;
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
import org.gzoumix.ts.ifdj.data.syntax.visitor.IVisitor;
import org.gzoumix.ts.ifdj.data.syntax.core.Program;
import org.gzoumix.ts.ifdj.data.syntax.formula.IFormula;
import org.gzoumix.util.syntax.Position;


public class Configuration extends ASTNodeCommonFunctionalities<Program> {
  private IFormula formula;

  public Configuration(Position pos, IFormula formula) {
    super(pos);
    this.formula = formula;
    formula.setFather(this);
  }

  public IFormula getFormula() { return this.formula; }

  @Override
  public void accept(IVisitor visitor) { visitor.visit(this); }

}
