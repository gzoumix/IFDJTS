package org.gzoumix.ifdj.lang.syntax.delta;
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
import org.gzoumix.ifdj.lang.syntax.formula.FormulaPredicate;
import org.gzoumix.ifdj.lang.syntax.formula.IFormulaElement;
import org.gzoumix.ifdj.lang.syntax.visitor.IVisitor;
import org.gzoumix.ifdj.lang.syntax.core.Program;
import org.gzoumix.util.syntax.Position;

import java.util.LinkedList;
import java.util.List;


public class DeltaModule extends ASTNodeCommonFunctionalities<Program> {
  private String name;
  private IFormulaElement id;
  private List<IClassOperation> operations;

  public DeltaModule(Position pos, String name) {
    super(pos);
    this.name = name;
    this.id = new FormulaPredicate<String>(this.name);
    this.operations = new LinkedList<>();
  }

  public void addOperation(IClassOperation operation) {
    this.operations.add(operation);
    operation.setFather(this);
  }

  public String getName() { return this.name; }
  public IFormulaElement getID() { return this.id; }
  public List<IClassOperation> getOperations() { return this.operations; }

  @Override
  public void accept(IVisitor visitor) { visitor.visit(this); }

}
