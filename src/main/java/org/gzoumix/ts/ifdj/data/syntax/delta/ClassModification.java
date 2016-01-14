package org.gzoumix.ts.ifdj.data.syntax.delta;
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

import org.gzoumix.ts.ifdj.data.FCS;
import org.gzoumix.ts.ifdj.data.syntax.ASTNodeCommonFunctionalities;
import org.gzoumix.ts.ifdj.data.syntax.ISuperClassDeclaration;
import org.gzoumix.ts.ifdj.data.syntax.formula.IFormulaElement;
import org.gzoumix.ts.ifdj.data.syntax.visitor.IVisitor;
import org.gzoumix.util.syntax.Position;

import java.util.LinkedList;
import java.util.List;

public class ClassModification extends ASTNodeCommonFunctionalities<DeltaModule> implements IClassOperation, ISuperClassDeclaration<DeltaModule> {
  private IFormulaElement delta;
  private String name;
  private String superClass;
  private List<IAttributeOperation> operations;

  public ClassModification(Position pos, IFormulaElement delta,  String name) {
    super(pos);
    this.delta = delta;
    this.name = name;
    this.operations = new LinkedList<>();
  }

  public void setSuper(String superClass) { this.superClass = superClass; }
  public String getSuper() { return this.superClass; }
  public boolean addOperation(IAttributeOperation op) {
    boolean res = this.operations.add(op);
    if(res) { op.setFather(this); }
    return res;
  }
  public List<IAttributeOperation> getOperations() { return this.operations; }


  // implementation of IClassOperation
  @Override
  public String getName() { return this.name; }

  @Override
  public FCS getFCS() {
    FCS res = new FCS(this.getName());
    for(IAttributeOperation op: this.getOperations()) {
      if(op instanceof AttributeAddition) {
        res.addAttribute(((AttributeAddition) op).getAttribute());
      } else if(op instanceof AttributeModification) {
        res.addAttribute(((AttributeModification) op).getAttribute());
      }
    }
    return res;
  }

  @Override
  public IFormulaElement getDelta() { return this.delta; }

  @Override
  public String getBaseClass() { return this.getName(); }

  @Override
  public String getSuperClass() { return this.getSuper(); }

  @Override
  public void accept(IVisitor visitor) { visitor.visit(this); }

}
